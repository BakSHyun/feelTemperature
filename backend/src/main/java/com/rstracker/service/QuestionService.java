package com.rstracker.service;

import com.rstracker.dto.CreateQuestionDto;
import com.rstracker.dto.QuestionDto;
import com.rstracker.dto.QuestionChoiceDto;
import com.rstracker.dto.UpdateQuestionDto;
import com.rstracker.entity.Question;
import com.rstracker.entity.QuestionChoice;
import com.rstracker.exception.BusinessException;
import com.rstracker.exception.ResourceNotFoundException;
import com.rstracker.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 질문 관리 서비스
 * 
 * 질문과 선택지의 CRUD 기능을 제공합니다.
 * 캐시 무효화를 통해 일관성을 유지합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    @Cacheable(value = "questions", key = "'active'")
    public List<QuestionDto> getActiveQuestions() {
        log.debug("Fetching active questions");
        // Fetch Join으로 N+1 문제 해결
        List<Question> questions = questionRepository.findActiveWithChoices();
        return questions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리별 활성 질문 조회
     * 
     * @param category 질문 카테고리 (INITIAL_MATCHING, TEMPERATURE_REFINE)
     * @return 질문 목록
     */
    @Cacheable(value = "questions", key = "'active-category-' + #category")
    public List<QuestionDto> getActiveQuestionsByCategory(Question.QuestionCategory category) {
        log.debug("Fetching active questions by category: {}", category);
        // Fetch Join으로 N+1 문제 해결
        List<Question> questions = questionRepository.findActiveByCategoryWithChoices(category);
        return questions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public QuestionDto getQuestion(Long id) {
        Question question = questionRepository.findByIdWithChoices(id)
                .orElseThrow(() -> new ResourceNotFoundException("질문을 찾을 수 없습니다: " + id));
        return toDto(question);
    }

    /**
     * 질문 생성
     * 
     * @param createDto 질문 생성 정보
     * @return 생성된 질문 정보
     * @throws BusinessException 순서 중복 또는 유효하지 않은 데이터인 경우
     */
    @Transactional
    @CacheEvict(value = "questions", allEntries = true) // 캐시 무효화
    public QuestionDto createQuestion(CreateQuestionDto createDto) {
        log.debug("Creating question with order: {}", createDto.getOrder());

        // 순서 중복 체크 (활성 질문만)
        if (questionRepository.existsByOrderAndIsActiveTrue(createDto.getOrder())) {
            throw new BusinessException("이미 해당 순서에 활성 질문이 존재합니다: " + createDto.getOrder());
        }

        // 질문 엔티티 생성
        Question question = new Question();
        question.setQuestionText(createDto.getQuestionText());
        question.setQuestionType(createDto.getQuestionType());
        question.setQuestionCategory(createDto.getQuestionCategory());
        question.setOrder(createDto.getOrder());
        question.setIsActive(true);
        question.setVersion(1);

        // 선택지 엔티티 생성
        List<QuestionChoice> choices = createDto.getChoices().stream()
                .map(choiceDto -> {
                    QuestionChoice choice = new QuestionChoice();
                    choice.setQuestion(question);
                    choice.setChoiceText(choiceDto.getChoiceText());
                    choice.setChoiceValue(choiceDto.getChoiceValue());
                    choice.setOrder(choiceDto.getOrder());
                    choice.setTemperatureWeight(choiceDto.getTemperatureWeight());
                    return choice;
                })
                .collect(Collectors.toList());

        question.setChoices(choices);

        // 저장
        Question savedQuestion = questionRepository.save(question);
        log.info("Question created successfully: id={}, order={}", savedQuestion.getId(), savedQuestion.getOrder());

        // Fetch Join으로 다시 조회하여 DTO 변환
        return toDto(questionRepository.findByIdWithChoices(savedQuestion.getId())
                .orElseThrow(() -> new ResourceNotFoundException("생성된 질문을 찾을 수 없습니다: " + savedQuestion.getId())));
    }

    /**
     * 질문 수정
     * 
     * @param id 질문 ID
     * @param updateDto 수정 정보
     * @return 수정된 질문 정보
     * @throws ResourceNotFoundException 질문을 찾을 수 없는 경우
     * @throws BusinessException 순서 중복인 경우
     */
    @Transactional
    @CacheEvict(value = "questions", allEntries = true) // 캐시 무효화
    public QuestionDto updateQuestion(Long id, UpdateQuestionDto updateDto) {
        log.debug("Updating question id: {}", id);

        Question question = questionRepository.findByIdWithChoices(id)
                .orElseThrow(() -> new ResourceNotFoundException("질문을 찾을 수 없습니다: " + id));

        // 순서 변경 시 중복 체크
        if (updateDto.getOrder() != null && !updateDto.getOrder().equals(question.getOrder())) {
            if (questionRepository.existsByOrderAndIsActiveTrue(updateDto.getOrder())) {
                throw new BusinessException("이미 해당 순서에 활성 질문이 존재합니다: " + updateDto.getOrder());
            }
            question.setOrder(updateDto.getOrder());
        }

        // 필드 업데이트
        if (updateDto.getQuestionText() != null) {
            question.setQuestionText(updateDto.getQuestionText());
        }
        if (updateDto.getQuestionType() != null) {
            question.setQuestionType(updateDto.getQuestionType());
        }
        if (updateDto.getQuestionCategory() != null) {
            question.setQuestionCategory(updateDto.getQuestionCategory());
        }
        if (updateDto.getIsActive() != null) {
            question.setIsActive(updateDto.getIsActive());
        }

        // 선택지 업데이트 (제공된 경우)
        if (updateDto.getChoices() != null) {
            // 기존 선택지 삭제 (orphanRemoval로 자동 삭제)
            question.getChoices().clear();

            // 새 선택지 추가
            List<QuestionChoice> newChoices = updateDto.getChoices().stream()
                    .map(choiceDto -> {
                        QuestionChoice choice = new QuestionChoice();
                        choice.setQuestion(question);
                        choice.setChoiceText(choiceDto.getChoiceText());
                        choice.setChoiceValue(choiceDto.getChoiceValue());
                        choice.setOrder(choiceDto.getOrder());
                        choice.setTemperatureWeight(choiceDto.getTemperatureWeight());
                        return choice;
                    })
                    .collect(Collectors.toList());

            question.getChoices().addAll(newChoices);
        }

        // 버전 증가
        question.setVersion(question.getVersion() + 1);

        // 저장
        Question savedQuestion = questionRepository.save(question);
        log.info("Question updated successfully: id={}", savedQuestion.getId());

        // Fetch Join으로 다시 조회하여 DTO 변환
        return toDto(questionRepository.findByIdWithChoices(savedQuestion.getId())
                .orElseThrow(() -> new ResourceNotFoundException("수정된 질문을 찾을 수 없습니다: " + savedQuestion.getId())));
    }

    /**
     * 질문 삭제 (소프트 삭제: isActive = false)
     * 
     * @param id 질문 ID
     * @throws ResourceNotFoundException 질문을 찾을 수 없는 경우
     */
    @Transactional
    @CacheEvict(value = "questions", allEntries = true) // 캐시 무효화
    public void deleteQuestion(Long id) {
        log.debug("Deleting question id: {}", id);

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("질문을 찾을 수 없습니다: " + id));

        // 소프트 삭제 (isActive = false)
        question.setIsActive(false);
        questionRepository.save(question);

        log.info("Question deleted (soft delete) successfully: id={}", id);
    }

    private QuestionDto toDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setQuestionText(question.getQuestionText());
        dto.setQuestionType(question.getQuestionType());
        dto.setQuestionCategory(question.getQuestionCategory() != null 
            ? question.getQuestionCategory().name() 
            : Question.QuestionCategory.INITIAL_MATCHING.name());
        dto.setOrder(question.getOrder());
        dto.setIsActive(question.getIsActive());
        dto.setVersion(question.getVersion());
        dto.setCreatedAt(question.getCreatedAt());
        dto.setUpdatedAt(question.getUpdatedAt());

        // Fetch Join으로 이미 로드된 choices 사용 (N+1 방지)
        List<QuestionChoiceDto> choices = question.getChoices().stream()
                .map(this::toChoiceDto)
                .collect(Collectors.toList());
        dto.setChoices(choices);

        return dto;
    }

    private QuestionChoiceDto toChoiceDto(QuestionChoice choice) {
        QuestionChoiceDto dto = new QuestionChoiceDto();
        dto.setId(choice.getId());
        dto.setChoiceText(choice.getChoiceText());
        dto.setChoiceValue(choice.getChoiceValue());
        dto.setOrder(choice.getOrder());
        dto.setTemperatureWeight(choice.getTemperatureWeight());
        return dto;
    }
}

