package com.rstracker.service.temperature;

import com.rstracker.entity.Answer;
import com.rstracker.entity.QuestionChoice;

import java.util.Map;

/**
 * 온도 계산 전략 인터페이스
 * 다양한 온도 계산 알고리즘을 지원하기 위한 Strategy 패턴
 */
public interface TemperatureCalculationStrategy {
    
    /**
     * 응답 데이터를 기반으로 온도 계산
     * 
     * @param answers 응답 목록
     * @param choiceMap 선택지 ID -> QuestionChoice 매핑
     * @param questionOrderMap 질문 ID -> 질문 순서 매핑
     * @return 온도 계산 결과
     */
    TemperatureResult calculate(
            java.util.List<Answer> answers,
            Map<Long, QuestionChoice> choiceMap,
            Map<Long, Integer> questionOrderMap
    );
    
    /**
     * 온도 계산 결과
     */
    class TemperatureResult {
        private final Double averageTemperature;
        private final Double temperatureDiff;
        
        public TemperatureResult(Double averageTemperature, Double temperatureDiff) {
            this.averageTemperature = averageTemperature;
            this.temperatureDiff = temperatureDiff;
        }
        
        public Double getAverageTemperature() {
            return averageTemperature;
        }
        
        public Double getTemperatureDiff() {
            return temperatureDiff;
        }
    }
}

