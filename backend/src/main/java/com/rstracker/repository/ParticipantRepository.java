package com.rstracker.repository;

import com.rstracker.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByMatchingId(Long matchingId);
    Optional<Participant> findByParticipantCode(String participantCode);
    long countByMatchingId(Long matchingId);
    
    /**
     * 회원 ID로 참여 목록 조회 (매칭, 기록 정보 포함)
     * Fetch Join으로 N+1 문제 해결 및 성능 최적화
     */
    @Query("SELECT DISTINCT p FROM Participant p " +
           "LEFT JOIN FETCH p.matching m " +
           "LEFT JOIN FETCH m.record r " +
           "LEFT JOIN FETCH m.participants " +
           "WHERE p.user.id = :userId " +
           "ORDER BY p.joinedAt DESC")
    List<Participant> findByUserIdWithMatchingAndRecord(@Param("userId") Long userId);
    
    /**
     * 회원 ID로 참여 목록 조회 (기본 조회)
     */
    List<Participant> findByUserId(Long userId);
    
    /**
     * 회원 ID로 참여 횟수 조회
     */
    long countByUserId(Long userId);
}

