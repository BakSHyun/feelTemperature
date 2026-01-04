package com.rstracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "question_choices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String choiceText;

    @Column(nullable = false, length = 100)
    private String choiceValue;

    @Column(nullable = false)
    private Integer order;

    @Column(nullable = false)
    private Double temperatureWeight = 0.0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "choice", cascade = CascadeType.ALL)
    private List<Answer> answers;
}

