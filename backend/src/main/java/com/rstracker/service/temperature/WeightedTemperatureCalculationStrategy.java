package com.rstracker.service.temperature;

import com.rstracker.entity.Answer;
import com.rstracker.entity.QuestionChoice;
import com.rstracker.config.TemperatureCalculationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 가중치 기반 온도 계산 전략
 * 질문별 가중치를 적용하여 온도를 계산합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WeightedTemperatureCalculationStrategy implements TemperatureCalculationStrategy {
    
    private final TemperatureCalculationProperties properties;
    
    @Override
    public TemperatureResult calculate(
            List<Answer> answers,
            Map<Long, QuestionChoice> choiceMap,
            Map<Long, Integer> questionOrderMap) {
        
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
            log.warn("온도 계산할 참여자 데이터가 없습니다. answers size: {}", answers.size());
            return new TemperatureResult(0.0, 0.0);
        }
    }
    
    private ParticipantTemperature calculateParticipantTemperature(
            List<Answer> answers,
            Map<Long, QuestionChoice> choiceMap,
            Map<Long, Integer> questionOrderMap) {
        
        double total = 0.0;
        double weightSum = 0.0;
        
        Map<Integer, Double> questionWeights = properties.getQuestionWeights();
        
        for (Answer answer : answers) {
            QuestionChoice choice = choiceMap.get(answer.getChoice().getId());
            Integer questionOrder = questionOrderMap.get(answer.getQuestion().getId());
            
            if (choice != null && questionOrder != null) {
                Double weight = questionWeights.get(questionOrder);
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
}

