package com.rstracker.controller;

import com.rstracker.dto.AnswerSubmissionDto;
import com.rstracker.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/submit/{participantCode}")
    public ResponseEntity<Void> submitAnswers(
            @PathVariable String participantCode,
            @Valid @RequestBody AnswerSubmissionDto submission) {
        answerService.submitAnswers(participantCode, submission.getAnswers());
        return ResponseEntity.ok().build();
    }
}

