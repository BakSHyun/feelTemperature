package com.rstracker.repository;

import com.rstracker.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByIsActiveTrueOrderByOrderAsc();
    Optional<Question> findByOrderAndIsActiveTrue(Integer order);
    boolean existsByOrderAndIsActiveTrue(Integer order);
    
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.choices ORDER BY q.order")
    List<Question> findAllWithChoices();
    
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.choices WHERE q.isActive = true ORDER BY q.order")
    List<Question> findActiveWithChoices();
    
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.choices WHERE q.id = :id")
    Optional<Question> findByIdWithChoices(Long id);
}

