package com.rstracker.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSubmissionDto {
    @NotEmpty(message = "응답 목록이 비어있습니다")
    @Valid
    private List<AnswerCreateDto> answers;
}

