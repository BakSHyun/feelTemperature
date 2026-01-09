package com.rstracker.repository;

import com.rstracker.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    /**
     * 참여자 ID로 답변 목록 조회 (질문, 선택지 정보 포함)
     * Fetch Join으로 N+1 문제 해결 및 성능 최적화
     */
    @Query("SELECT DISTINCT a FROM Answer a " +
           "LEFT JOIN FETCH a.question q " +
           "LEFT JOIN FETCH a.choice c " +
           "WHERE a.participant.id = :participantId " +
           "ORDER BY q.order ASC, a.answeredAt ASC")
    List<Answer> findByParticipantIdWithQuestionAndChoice(@Param("participantId") Long participantId);
    
    /**
     * 참여자 ID 목록으로 답변 목록 일괄 조회 (질문, 선택지 정보 포함)
     * Fetch Join으로 N+1 문제 해결 및 성능 최적화
     */
    @Query("SELECT DISTINCT a FROM Answer a " +
           "LEFT JOIN FETCH a.question q " +
           "LEFT JOIN FETCH a.choice c " +
           "WHERE a.participant.id IN :participantIds " +
           "ORDER BY a.participant.id ASC, q.order ASC, a.answeredAt ASC")
    List<Answer> findByParticipantIdsWithQuestionAndChoice(@Param("participantIds") List<Long> participantIds);
    
    /**
     * 참여자 ID로 답변 목록 조회 (기본 조회)
     */
    List<Answer> findByParticipantId(Long participantId);
    
    /**
     * 매칭 ID로 답변 목록 조회 (Fetch Join 최적화)
     */
    @Query("SELECT DISTINCT a FROM Answer a " +
           "LEFT JOIN FETCH a.question q " +
           "LEFT JOIN FETCH a.choice c " +
           "LEFT JOIN FETCH a.participant p " +
           "WHERE p.matching.id = :matchingId " +
           "ORDER BY q.order ASC, a.answeredAt ASC")
    List<Answer> findByMatchingIdWithDetails(@Param("matchingId") Long matchingId);
    
    @Query("SELECT a FROM Answer a WHERE a.participant.matching.id = :matchingId")
    List<Answer> findByMatchingId(@Param("matchingId") Long matchingId);
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Answer a WHERE a.participant.id = :participantId AND a.question.id = :questionId")
    boolean existsByParticipantIdAndQuestionId(@Param("participantId") Long participantId, @Param("questionId") Long questionId);
}

