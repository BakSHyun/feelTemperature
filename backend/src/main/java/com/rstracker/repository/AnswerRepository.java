package com.rstracker.repository;

import com.rstracker.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByParticipantId(Long participantId);
    
    @Query("SELECT a FROM Answer a WHERE a.participant.matching.id = :matchingId")
    List<Answer> findByMatchingId(@Param("matchingId") Long matchingId);
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Answer a WHERE a.participant.id = :participantId AND a.question.id = :questionId")
    boolean existsByParticipantIdAndQuestionId(@Param("participantId") Long participantId, @Param("questionId") Long questionId);
}

