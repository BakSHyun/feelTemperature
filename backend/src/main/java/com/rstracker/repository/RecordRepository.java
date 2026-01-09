package com.rstracker.repository;

import com.rstracker.entity.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 기록 Repository
 * 
 * 기록 조회, 필터링, 페이징 기능을 제공합니다.
 */
@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    
    /**
     * recordId로 기록 조회
     */
    Optional<Record> findByRecordId(String recordId);
    
    /**
     * matchingId로 기록 조회
     */
    Optional<Record> findByMatchingId(Long matchingId);
    
    /**
     * recordId 존재 여부 확인
     */
    boolean existsByRecordId(String recordId);
    
    /**
     * 활성 기록만 조회 (페이징)
     */
    Page<Record> findByIsActiveTrue(Pageable pageable);
    
    /**
     * 활성 여부로 필터링하여 조회 (페이징)
     */
    Page<Record> findByIsActive(Boolean isActive, Pageable pageable);
    
    /**
     * 온도 범위로 필터링하여 조회 (페이징, Fetch Join)
     * 매칭 정보도 함께 조회하여 N+1 문제 해결
     */
    @Query("SELECT DISTINCT r FROM Record r " +
           "LEFT JOIN FETCH r.matching m " +
           "WHERE (:minTemp IS NULL OR r.temperature >= :minTemp) " +
           "AND (:maxTemp IS NULL OR r.temperature <= :maxTemp) " +
           "AND (:isActive IS NULL OR r.isActive = :isActive) " +
           "AND (:startDate IS NULL OR r.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR r.createdAt <= :endDate)")
    Page<Record> findByFiltersWithMatching(
            @Param("minTemp") Double minTemp,
            @Param("maxTemp") Double maxTemp,
            @Param("isActive") Boolean isActive,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    /**
     * 전체 기록 조회 (Fetch Join으로 N+1 문제 해결)
     */
    @Query("SELECT DISTINCT r FROM Record r LEFT JOIN FETCH r.matching m")
    Page<Record> findAllWithMatching(Pageable pageable);
}

