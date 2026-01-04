package com.rstracker.repository;

import com.rstracker.entity.QuestionChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionChoiceRepository extends JpaRepository<QuestionChoice, Long> {
    List<QuestionChoice> findByQuestionIdOrderByOrderAsc(Long questionId);
    void deleteByQuestionId(Long questionId);
    
    @Query("SELECT qc FROM QuestionChoice qc JOIN FETCH qc.question")
    List<QuestionChoice> findAllWithQuestions();
}

