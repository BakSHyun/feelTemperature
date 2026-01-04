package com.rstracker.service;

import com.rstracker.entity.Answer;

import java.util.List;

/**
 * 응답 조회 서비스 인터페이스
 * 
 * 마이크로서비스 전환 시 Answer Service로 분리될 예정입니다.
 * 현재는 모놀리식 구조이지만, 서비스 간 의존성을 인터페이스로 명확히 정의합니다.
 * 
 * 향후 전환 시:
 * - Answer Service의 일부로 분리
 * - REST API 또는 이벤트 기반 통신으로 변경
 */
public interface AnswerQueryService {
    
    /**
     * 매칭 ID로 응답 목록 조회
     * 
     * @param matchingId 매칭 ID
     * @return 응답 목록
     */
    List<Answer> getAnswersByMatching(Long matchingId);
}

