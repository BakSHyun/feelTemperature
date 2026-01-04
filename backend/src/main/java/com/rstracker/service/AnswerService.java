package com.rstracker.service;

import com.rstracker.dto.AnswerCreateDto;
import com.rstracker.entity.Answer;
import com.rstracker.entity.Participant;
import com.rstracker.entity.Question;
import com.rstracker.entity.QuestionChoice;
import com.rstracker.exception.ResourceNotFoundException;
import com.rstracker.repository.AnswerRepository;
import com.rstracker.repository.ParticipantRepository;
import com.rstracker.repository.QuestionRepository;
import com.rstracker.repository.QuestionChoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final ParticipantRepository participantRepository;
    private final QuestionRepository questionRepository;
    private final QuestionChoiceRepository questionChoiceRepository;

    @Transactional
    public void submitAnswers(String participantCode, List<AnswerCreateDto> answerDtos) {
        log.debug("Submitting answers for participant: {}", participantCode);
        
        Participant participant = participantRepository.findByParticipantCode(participantCode)
                .orElseThrow(() -> new ResourceNotFoundException("참여자를 찾을 수 없습니다: " + participantCode));

        // 기존 응답 삭제 (중간 저장 불가, 완료 시만 저장)
        List<Answer> existingAnswers = answerRepository.findByParticipantId(participant.getId());
        answerRepository.deleteAll(existingAnswers);

        // 배치 저장을 위한 리스트 생성
        List<Answer> answersToSave = new java.util.ArrayList<>();
        
        // 질문과 선택지 ID를 미리 수집하여 한 번에 조회 (N+1 방지)
        List<Long> questionIds = answerDtos.stream()
                .map(AnswerCreateDto::getQuestionId)
                .distinct()
                .toList();
        List<Long> choiceIds = answerDtos.stream()
                .map(AnswerCreateDto::getChoiceId)
                .distinct()
                .toList();
        
        Map<Long, Question> questionMap = questionRepository.findAllById(questionIds).stream()
                .collect(Collectors.toMap(Question::getId, q -> q));
        Map<Long, QuestionChoice> choiceMap = questionChoiceRepository.findAllById(choiceIds).stream()
                .collect(Collectors.toMap(QuestionChoice::getId, c -> c));
        
        // 새 응답 생성
        for (AnswerCreateDto answerDto : answerDtos) {
            Question question = questionMap.get(answerDto.getQuestionId());
            if (question == null) {
                throw new ResourceNotFoundException("질문을 찾을 수 없습니다: " + answerDto.getQuestionId());
            }
            
            QuestionChoice choice = choiceMap.get(answerDto.getChoiceId());
            if (choice == null) {
                throw new ResourceNotFoundException("선택지를 찾을 수 없습니다: " + answerDto.getChoiceId());
            }
            
            Answer answer = new Answer();
            answer.setParticipant(participant);
            answer.setQuestion(question);
            answer.setChoice(choice);
            answersToSave.add(answer);
        }
        
        // 배치 저장
        answerRepository.saveAll(answersToSave);
        
        log.info("Answers submitted for participant: {}, count: {}", participantCode, answerDtos.size());
    }

    @Transactional(readOnly = true)
    public List<Answer> getAnswersByParticipant(String participantCode) {
        Participant participant = participantRepository.findByParticipantCode(participantCode)
                .orElseThrow(() -> new ResourceNotFoundException("참여자를 찾을 수 없습니다: " + participantCode));
        return answerRepository.findByParticipantId(participant.getId());
    }

    /**
     * 매칭 ID로 응답 조회
     * 
     * @deprecated 마이크로서비스 전환 준비: AnswerQueryService 사용 권장
     * @see AnswerQueryService
     */
    @Deprecated
    @Transactional(readOnly = true)
    public List<Answer> getAnswersByMatching(Long matchingId) {
        return answerRepository.findByMatchingId(matchingId);
    }
}

