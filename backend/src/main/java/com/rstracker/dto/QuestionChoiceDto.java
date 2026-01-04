package com.rstracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionChoiceDto {
    private Long id;
    private String choiceText;
    private String choiceValue;
    private Integer order;
    private Double temperatureWeight;
}

