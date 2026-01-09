package com.rstracker.service;

import com.rstracker.dto.RecordDto;
import com.rstracker.entity.Answer;
import com.rstracker.entity.Matching;
import com.rstracker.entity.MatchingStatus;
import com.rstracker.entity.Question;
import com.rstracker.entity.QuestionChoice;
import com.rstracker.entity.Record;
import com.rstracker.exception.BusinessException;
import com.rstracker.exception.ResourceNotFoundException;
import com.rstracker.mapper.RecordMapper;
import com.rstracker.repository.MatchingRepository;
import com.rstracker.repository.QuestionChoiceRepository;
import com.rstracker.repository.QuestionRepository;
import com.rstracker.repository.RecordRepository;
import com.rstracker.service.temperature.TemperatureCalculationStrategy;
import com.rstracker.util.RecordIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final MatchingRepository matchingRepository;
    private final AnswerQueryService answerQueryService; // 인터페이스를 통한 의존 (마이크로서비스 전환 준비)
    private final QuestionRepository questionRepository;
    private final QuestionChoiceRepository questionChoiceRepository;
    private final RecordMapper recordMapper;
    private final TemperatureCalculationStrategy temperatureCalculationStrategy;

    @Transactional
    public RecordDto createRecord(Long matchingId) {
        log.debug("Creating record for matching: {}", matchingId);
        
        Matching matching = matchingRepository.findById(matchingId)
                .orElseThrow(() -> new ResourceNotFoundException("매칭을 찾을 수 없습니다: " + matchingId));

        if (recordRepository.findByMatchingId(matchingId).isPresent()) {
            throw new BusinessException("이미 기록이 생성되었습니다");
        }

        // 모든 응답 조회 (AnswerQueryService를 통한 조회 - 마이크로서비스 전환 준비)
        List<Answer> answers = answerQueryService.getAnswersByMatching(matchingId);
        
        if (answers.isEmpty()) {
            throw new BusinessException("응답 데이터가 없습니다");
        }

        // 선택지와 질문 매핑 생성 (Fetch Join으로 N+1 문제 해결)
        Map<Long, QuestionChoice> choiceMap = questionChoiceRepository.findAllWithQuestions().stream()
                .collect(Collectors.toMap(QuestionChoice::getId, choice -> choice));

        Map<Long, Integer> questionOrderMap = questionRepository.findAllWithChoices().stream()
                .collect(Collectors.toMap(Question::getId, Question::getOrder));

        // 온도 계산 (Strategy 패턴 사용)
        TemperatureCalculationStrategy.TemperatureResult result = temperatureCalculationStrategy.calculate(
                answers, choiceMap, questionOrderMap);

        // 응답 요약 생성
        Map<String, Object> summary = createSummary(answers, questionOrderMap);

        // 기록 생성
        Record record = new Record();
        record.setRecordId(RecordIdGenerator.generate());
        record.setMatching(matching);
        record.setTemperature(result.getAverageTemperature());
        record.setTemperatureDiff(result.getTemperatureDiff());
        record.setIsActive(true);
        record.setSummary(summary);

        record = recordRepository.save(record);

        // 매칭 상태 변경
        matching.setStatus(MatchingStatus.COMPLETED.getValue());
        matching.setCompletedAt(java.time.LocalDateTime.now());
        matchingRepository.save(matching);

        log.info("Record created for matching: {}, recordId: {}", matchingId, record.getRecordId());
        return recordMapper.toDto(record);
    }

    @Transactional(readOnly = true)
    public RecordDto getRecord(String recordId) {
        Record record = recordRepository.findByRecordId(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("기록을 찾을 수 없습니다: " + recordId));
        return recordMapper.toDto(record);
    }

    @Transactional(readOnly = true)
    public RecordDto getRecordByMatchingId(Long matchingId) {
        Record record = recordRepository.findByMatchingId(matchingId)
                .orElseThrow(() -> new ResourceNotFoundException("기록을 찾을 수 없습니다: " + matchingId));
        return recordMapper.toDto(record);
    }

    @Transactional
    public void deactivateRecord(String recordId) {
        Record record = recordRepository.findByRecordId(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("기록을 찾을 수 없습니다: " + recordId));
        record.setIsActive(false);
        recordRepository.save(record);
        log.info("Record deactivated: {}", recordId);
    }

    /**
     * 기록 목록 조회 (페이징, 필터링, 정렬)
     * 
     * @param minTemp 최소 온도 (선택)
     * @param maxTemp 최대 온도 (선택)
     * @param isActive 활성 여부 (선택)
     * @param startDate 시작 날짜 (선택)
     * @param endDate 종료 날짜 (선택)
     * @param pageable 페이징 정보
     * @return 기록 목록 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<RecordDto> getRecords(
            Double minTemp,
            Double maxTemp,
            Boolean isActive,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        log.debug("Fetching records with filters: minTemp={}, maxTemp={}, isActive={}, startDate={}, endDate={}, pageable={}",
                minTemp, maxTemp, isActive, startDate, endDate, pageable);
        
        // 필터 조건이 모두 없는 경우 전체 조회
        if (minTemp == null && maxTemp == null && isActive == null && startDate == null && endDate == null) {
            Page<Record> records = recordRepository.findAllWithMatching(pageable);
            return records.map(recordMapper::toDto);
        }
        
        // 필터 조건이 있는 경우 필터링 조회
        Page<Record> records = recordRepository.findByFiltersWithMatching(
                minTemp, maxTemp, isActive, startDate, endDate, pageable);
        return records.map(recordMapper::toDto);
    }

    private Map<String, Object> createSummary(List<Answer> answers, Map<Long, Integer> questionOrderMap) {
        Map<String, Object> summary = new HashMap<>();

        for (Answer answer : answers) {
            Integer order = questionOrderMap.get(answer.getQuestion().getId());
            if (order != null) {
                String key = "Q" + order;
                Map<String, Object> answerData = new HashMap<>();
                answerData.put("question_text", answer.getQuestion().getQuestionText());
                answerData.put("choice_text", answer.getChoice().getChoiceText());
                answerData.put("question_type", answer.getQuestion().getQuestionType());
                summary.put(key, answerData);
            }
        }

        return summary;
    }

}

