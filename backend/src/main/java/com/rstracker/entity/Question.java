package com.rstracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(nullable = false, length = 50)
    private String questionType; // context, sentiment, expectation, distance, comfort

    @Column(name = "question_category", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private QuestionCategory questionCategory = QuestionCategory.INITIAL_MATCHING;

    @Column(name = "\"order\"", nullable = false)
    private Integer order;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Integer version = 1;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionChoice> choices = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<>();

    /**
     * 질문 카테고리 Enum
     * 질문이 사용되는 조건/상황을 구분
     */
    public enum QuestionCategory {
        INITIAL_MATCHING,      // 처음 매칭되었을 때 사용되는 질문
        TEMPERATURE_REFINE     // 온도 맞춰보기 기능에서 사용되는 질문
    }
}

