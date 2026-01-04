package com.rstracker.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerCreateDto {
    @NotNull(message = "질문 ID는 필수입니다")
    private Long questionId;
    
    @NotNull(message = "선택지 ID는 필수입니다")
    private Long choiceId;
}

