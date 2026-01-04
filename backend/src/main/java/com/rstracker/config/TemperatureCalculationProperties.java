package com.rstracker.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 온도 계산 설정 Properties
 * application.yml에서 질문별 가중치를 설정할 수 있습니다.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.temperature-calculation")
public class TemperatureCalculationProperties {
    
    /**
     * 질문 순서별 가중치
     * Key: 질문 순서 (1, 2, 3, ...)
     * Value: 가중치 (Double)
     */
    private Map<Integer, Double> questionWeights = new HashMap<>();
    
    public TemperatureCalculationProperties() {
        // 기본값 설정 (질문 세트 v1 기준)
        questionWeights.put(3, 3.0);  // Q3 분위기
        questionWeights.put(4, 2.0);  // Q4 기대
        questionWeights.put(5, 3.0);  // Q5 거리
        questionWeights.put(6, 2.0);  // Q6 편안함
    }
}

