package com.rstracker.controller;

import com.rstracker.dto.CreateQuestionDto;
import com.rstracker.dto.QuestionDto;
import com.rstracker.dto.UpdateQuestionDto;
import com.rstracker.entity.Question;
import com.rstracker.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 질문 관리 API 컨트롤러
 * 
 * 질문 CRUD 기능을 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 활성 질문 목록 조회
     * GET /api/questions?category=INITIAL_MATCHING
     */
    @GetMapping
    public ResponseEntity<List<QuestionDto>> getActiveQuestions(
            @RequestParam(required = false) String category) {
        log.info("Received request to get active questions with category: {}", category);
        if (category != null && !category.isEmpty()) {
            try {
                Question.QuestionCategory questionCategory = Question.QuestionCategory.valueOf(category.toUpperCase());
                List<QuestionDto> questions = questionService.getActiveQuestionsByCategory(questionCategory);
                return ResponseEntity.ok(questions);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid category: {}, returning all active questions", category);
                // 잘못된 카테고리인 경우 전체 조회
                List<QuestionDto> questions = questionService.getActiveQuestions();
                return ResponseEntity.ok(questions);
            }
        }
        List<QuestionDto> questions = questionService.getActiveQuestions();
        return ResponseEntity.ok(questions);
    }

    /**
     * 질문 상세 조회
     * GET /api/questions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> getQuestion(@PathVariable Long id) {
        log.info("Received request to get question: {}", id);
        QuestionDto question = questionService.getQuestion(id);
        return ResponseEntity.ok(question);
    }

    /**
     * 질문 생성
     * POST /api/questions
     */
    @PostMapping
    public ResponseEntity<QuestionDto> createQuestion(@Valid @RequestBody CreateQuestionDto createDto) {
        log.info("Received request to create question with order: {}", createDto.getOrder());
        QuestionDto question = questionService.createQuestion(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(question);
    }

    /**
     * 질문 수정
     * PUT /api/questions/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuestionDto> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody UpdateQuestionDto updateDto) {
        log.info("Received request to update question: {}", id);
        QuestionDto question = questionService.updateQuestion(id, updateDto);
        return ResponseEntity.ok(question);
    }

    /**
     * 질문 삭제 (소프트 삭제)
     * DELETE /api/questions/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        log.info("Received request to delete question: {}", id);
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}

