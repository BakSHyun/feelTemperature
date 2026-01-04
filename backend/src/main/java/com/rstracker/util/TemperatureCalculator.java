package com.rstracker.util;

import com.rstracker.entity.Answer;
import com.rstracker.entity.QuestionChoice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.rstracker.constants.AppConstants;

public class TemperatureCalculator {
    
    // 질문별 가중치 (상수 클래스에서 질문 순서 참조)
    private static final Map<Integer, Double> QUESTION_WEIGHTS = Map.of(
        AppConstants.QUESTION_ORDER_SENTIMENT, 3.0,      // Q3 분위기
        AppConstants.QUESTION_ORDER_EXPECTATION, 2.0,    // Q4 기대
        AppConstants.QUESTION_ORDER_DISTANCE, 3.0,       // Q5 거리
        AppConstants.QUESTION_ORDER_COMFORT, 2.0         // Q6 편안함
    );
    
    public static TemperatureResult calculate(List<Answer> answers, Map<Long, QuestionChoice> choiceMap, Map<Long, Integer> questionOrderMap) {
        // 참여자별 온도 계산
        Map<Long, ParticipantTemperature> participantTemps = answers.stream()
            .collect(Collectors.groupingBy(
                answer -> answer.getParticipant().getId(),
                Collectors.toList()
            ))
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> calculateParticipantTemperature(entry.getValue(), choiceMap, questionOrderMap)
            ));
        
        List<Double> temperatures = participantTemps.values().stream()
            .map(ParticipantTemperature::getTemperature)
            .filter(temp -> temp != null)
            .toList();
        
        if (temperatures.size() == 2) {
            double avgTemp = (temperatures.get(0) + temperatures.get(1)) / 2.0;
            double tempDiff = Math.abs(temperatures.get(0) - temperatures.get(1));
            return new TemperatureResult(avgTemp, tempDiff);
        } else if (temperatures.size() == 1) {
            return new TemperatureResult(temperatures.get(0), 0.0);
        } else {
            return new TemperatureResult(0.0, 0.0);
        }
    }
    
    private static ParticipantTemperature calculateParticipantTemperature(
            List<Answer> answers,
            Map<Long, QuestionChoice> choiceMap,
            Map<Long, Integer> questionOrderMap) {
        
        double total = 0.0;
        double weightSum = 0.0;
        
        for (Answer answer : answers) {
            QuestionChoice choice = choiceMap.get(answer.getChoice().getId());
            Integer questionOrder = questionOrderMap.get(answer.getQuestion().getId());
            
            if (choice != null && questionOrder != null) {
                Double weight = QUESTION_WEIGHTS.get(questionOrder);
                if (weight != null && weight > 0) {
                    double contribution = choice.getTemperatureWeight() * weight;
                    total += contribution;
                    weightSum += weight;
                }
            }
        }
        
        double temperature = weightSum > 0 ? total / weightSum : 0.0;
        return new ParticipantTemperature(temperature);
    }
    
    private static class ParticipantTemperature {
        private final Double temperature;
        
        public ParticipantTemperature(Double temperature) {
            this.temperature = temperature;
        }
        
        public Double getTemperature() {
            return temperature;
        }
    }
    
    public static class TemperatureResult {
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

