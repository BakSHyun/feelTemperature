package com.rstracker.repository;

import com.rstracker.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    Optional<Record> findByRecordId(String recordId);
    Optional<Record> findByMatchingId(Long matchingId);
    boolean existsByRecordId(String recordId);
}

