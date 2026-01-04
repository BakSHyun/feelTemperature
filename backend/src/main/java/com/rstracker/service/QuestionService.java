package com.rstracker.service;

import com.rstracker.dto.QuestionDto;
import com.rstracker.dto.QuestionChoiceDto;
import com.rstracker.entity.Question;
import com.rstracker.entity.QuestionChoice;
import com.rstracker.exception.ResourceNotFoundException;
import com.rstracker.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public QuestionDto getQuestion(Long id) {
        Question question = questionRepository.findByIdWithChoices(id)
                .orElseThrow(() -> new ResourceNotFoundException("질문을 찾을 수 없습니다: " + id));
        return toDto(question);
    }

    private QuestionDto toDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setQuestionText(question.getQuestionText());
        dto.setQuestionType(question.getQuestionType());
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

