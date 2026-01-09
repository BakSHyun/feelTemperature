package com.rstracker.dto;

import com.rstracker.entity.Question;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 질문 생성 DTO
 * 
 * 질문과 선택지를 함께 생성합니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionDto {

    /**
     * 질문 텍스트
     */
    @NotBlank(message = "질문 텍스트는 필수입니다")
    private String questionText;

    /**
     * 질문 타입 (context, sentiment, expectation, distance, comfort)
     */
    @NotBlank(message = "질문 타입은 필수입니다")
    private String questionType;

    /**
     * 질문 카테고리 (INITIAL_MATCHING, TEMPERATURE_REFINE)
     */
    @NotNull(message = "질문 카테고리는 필수입니다")
    private Question.QuestionCategory questionCategory;

    /**
     * 질문 순서
     */
    @NotNull(message = "질문 순서는 필수입니다")
    private Integer order;

    /**
     * 선택지 목록
     */
    @NotEmpty(message = "선택지는 최소 1개 이상 필요합니다")
    @Valid
    private List<CreateQuestionChoiceDto> choices;

    /**
     * 질문 선택지 생성 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateQuestionChoiceDto {
        /**
         * 선택지 텍스트
         */
        @NotBlank(message = "선택지 텍스트는 필수입니다")
        private String choiceText;

        /**
         * 선택지 값
         */
        @NotBlank(message = "선택지 값은 필수입니다")
        private String choiceValue;

        /**
         * 선택지 순서
         */
        @NotNull(message = "선택지 순서는 필수입니다")
        private Integer order;

        /**
         * 온도 가중치
         */
        @NotNull(message = "온도 가중치는 필수입니다")
        private Double temperatureWeight;
    }
}

