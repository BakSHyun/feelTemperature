package com.rstracker.service;

import com.rstracker.entity.Answer;
import com.rstracker.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 응답 조회 서비스 구현체
 * 
 * 현재는 AnswerService와 같은 패키지에 있지만,
 * 마이크로서비스 전환 시 Answer Service로 분리됩니다.
 * 
 * 구현 전략:
 * - 현재: AnswerRepository 직접 접근 (모놀리식)
 * - 전환 후: Answer Service의 REST API 호출 또는 이벤트 기반 통신
 */
@Service
@RequiredArgsConstructor
public class AnswerQueryServiceImpl implements AnswerQueryService {
    
    private final AnswerRepository answerRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<Answer> getAnswersByMatching(Long matchingId) {
        // 현재: Repository 직접 접근
        // 향후: Answer Service API 호출로 변경 예정
        return answerRepository.findByMatchingId(matchingId);
    }
}

