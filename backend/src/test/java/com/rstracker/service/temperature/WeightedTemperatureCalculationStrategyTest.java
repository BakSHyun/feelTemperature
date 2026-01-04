package com.rstracker.service.temperature;

import com.rstracker.config.TemperatureCalculationProperties;
import com.rstracker.entity.Answer;
import com.rstracker.entity.Participant;
import com.rstracker.entity.Question;
import com.rstracker.entity.QuestionChoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * WeightedTemperatureCalculationStrategy 단위 테스트
 */
@DisplayName("WeightedTemperatureCalculationStrategy 테스트")
class WeightedTemperatureCalculationStrategyTest {
    
    private WeightedTemperatureCalculationStrategy strategy;
    private TemperatureCalculationProperties properties;
    
    @BeforeEach
    void setUp() {
        properties = new TemperatureCalculationProperties();
        strategy = new WeightedTemperatureCalculationStrategy(properties);
    }
    
    @Test
    @DisplayName("두 참여자의 온도 계산 성공")
    void calculate_TwoParticipants_Success() {
        // given
        Map<Integer, Double> weights = new HashMap<>();
        weights.put(3, 3.0);  // Q3
        weights.put(4, 2.0);  // Q4
        properties.setQuestionWeights(weights);
        
        Participant participant1 = createParticipant(1L);
        Participant participant2 = createParticipant(2L);
        
        Question question3 = createQuestion(1L, 3);
        Question question4 = createQuestion(2L, 4);
        
        QuestionChoice choice1 = createChoice(1L, 0.5);  // temperatureWeight = 0.5
        QuestionChoice choice2 = createChoice(2L, 0.7);  // temperatureWeight = 0.7
        
        Answer answer1 = createAnswer(participant1, question3, choice1);
        Answer answer2 = createAnswer(participant1, question4, choice2);
        Answer answer3 = createAnswer(participant2, question3, choice1);
        Answer answer4 = createAnswer(participant2, question4, choice2);
        
        List<Answer> answers = List.of(answer1, answer2, answer3, answer4);
        
        Map<Long, QuestionChoice> choiceMap = Map.of(
            1L, choice1,
            2L, choice2
        );
        
        Map<Long, Integer> questionOrderMap = Map.of(
            1L, 3,
            2L, 4
        );
        
        // when
        TemperatureCalculationStrategy.TemperatureResult result = strategy.calculate(
            answers, choiceMap, questionOrderMap
        );
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.getAverageTemperature()).isGreaterThan(0);
        assertThat(result.getTemperatureDiff()).isEqualTo(0.0);  // 같은 선택지이므로 차이 0
    }
    
    private Participant createParticipant(Long id) {
        Participant participant = new Participant();
        participant.setId(id);
        return participant;
    }
    
    private Question createQuestion(Long id, Integer order) {
        Question question = new Question();
        question.setId(id);
        question.setOrder(order);
        return question;
    }
    
    private QuestionChoice createChoice(Long id, Double temperatureWeight) {
        QuestionChoice choice = new QuestionChoice();
        choice.setId(id);
        choice.setTemperatureWeight(temperatureWeight);
        return choice;
    }
    
    private Answer createAnswer(Participant participant, Question question, QuestionChoice choice) {
        Answer answer = new Answer();
        answer.setParticipant(participant);
        answer.setQuestion(question);
        answer.setChoice(choice);
        return answer;
    }
}

