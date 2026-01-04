package com.rstracker.controller;

import com.rstracker.dto.QuestionDto;
import com.rstracker.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<List<QuestionDto>> getActiveQuestions() {
        List<QuestionDto> questions = questionService.getActiveQuestions();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> getQuestion(@PathVariable Long id) {
        QuestionDto question = questionService.getQuestion(id);
        return ResponseEntity.ok(question);
    }
}

