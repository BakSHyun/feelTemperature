package com.rstracker.dto;

import com.rstracker.entity.Question;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 질문 수정 DTO
 * 
 * 모든 필드가 선택 사항입니다.
 * 제공된 필드만 업데이트됩니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuestionDto {

    /**
     * 질문 텍스트
     */
    private String questionText;

    /**
     * 질문 타입 (context, sentiment, expectation, distance, comfort)
     */
    private String questionType;

    /**
     * 질문 카테고리 (INITIAL_MATCHING, TEMPERATURE_REFINE)
     */
    private Question.QuestionCategory questionCategory;

    /**
     * 질문 순서
     */
    private Integer order;

    /**
     * 활성화 여부
     */
    private Boolean isActive;

    /**
     * 선택지 목록
     * null이 아니면 전체 선택지를 교체합니다.
     */
    @Valid
    private List<UpdateQuestionChoiceDto> choices;

    /**
     * 질문 선택지 수정 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateQuestionChoiceDto {
        /**
         * 선택지 ID (수정 시 필수, 생성 시 null)
         */
        private Long id;

        /**
         * 선택지 텍스트
         */
        private String choiceText;

        /**
         * 선택지 값
         */
        private String choiceValue;

        /**
         * 선택지 순서
         */
        private Integer order;

        /**
         * 온도 가중치
         */
        private Double temperatureWeight;
    }
}

