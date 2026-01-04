package com.rstracker.config;

import com.rstracker.entity.Question;
import com.rstracker.entity.QuestionChoice;
import com.rstracker.repository.QuestionRepository;
import com.rstracker.repository.QuestionChoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final QuestionRepository questionRepository;
    private final QuestionChoiceRepository questionChoiceRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // 이미 데이터가 있으면 스킵
        if (questionRepository.count() > 0) {
            return;
        }

        // Q1: 만남의 장소
        Question q1 = createQuestion(1, "지금 이 만남은 어디에서 시작되었나요?", "context", 1);
        createChoice(q1, "술집 / 바", "bar", 1, 0.0);
        createChoice(q1, "식당", "restaurant", 2, 0.0);
        createChoice(q1, "카페", "cafe", 3, 0.0);
        createChoice(q1, "길 / 동네", "street", 4, 0.0);
        createChoice(q1, "기타", "other", 5, 0.0);

        // Q2: 만남의 계기
        Question q2 = createQuestion(2, "두 분은 어떻게 만나게 되었나요?", "context", 2);
        createChoice(q2, "지인 소개", "introduction", 1, 0.0);
        createChoice(q2, "우연히", "coincidence", 2, 0.0);
        createChoice(q2, "SNS / 앱", "sns_app", 3, 0.0);
        createChoice(q2, "직장 / 학교", "work_school", 4, 0.0);
        createChoice(q2, "기타", "other", 5, 0.0);

        // Q3: 현재 분위기 인식 (가중치 3.0)
        Question q3 = createQuestion(3, "지금 이 순간, 이 만남의 분위기는 어떤가요?", "sentiment", 3);
        createChoice(q3, "조금 어색해요", "awkward", 1, 0.2);
        createChoice(q3, "편안해요", "comfortable", 2, 0.5);
        createChoice(q3, "설레요", "excited", 3, 0.7);
        createChoice(q3, "많이 가까워진 느낌이에요", "close", 4, 0.9);

        // Q4: 기대 수준 (가중치 2.0)
        Question q4 = createQuestion(4, "오늘 이 만남에 대해 기대하는 정도는 어느 쪽에 가까운가요?", "expectation", 4);
        createChoice(q4, "대화 정도면 충분해요", "conversation", 1, 0.2);
        createChoice(q4, "좋은 시간 보내고 싶어요", "good_time", 2, 0.4);
        createChoice(q4, "더 가까워질 수도 있을 것 같아요", "closer", 3, 0.6);
        createChoice(q4, "흐름에 맡기고 싶어요", "go_with_flow", 4, 0.5);

        // Q5: 신체적 거리 인식 (가중치 3.0)
        Question q5 = createQuestion(5, "지금 이 순간 기준으로, 편안하게 느껴지는 신체적 거리는 어디까지인가요?", "distance", 5);
        createChoice(q5, "대화만", "conversation_only", 1, 0.1);
        createChoice(q5, "가벼운 스킨십 (손 잡기 등)", "light_skin", 2, 0.4);
        createChoice(q5, "포옹 정도", "hug", 3, 0.6);
        createChoice(q5, "더 가까워도 괜찮아요", "closer_ok", 4, 0.9);

        // Q6: 현재 상태 확인 (가중치 2.0)
        Question q6 = createQuestion(6, "지금 이 상태가 본인에게 편안하게 느껴지나요?", "comfort", 6);
        createChoice(q6, "네, 괜찮아요", "ok", 1, 0.7);
        createChoice(q6, "조금 고민돼요", "concerned", 2, 0.3);
        createChoice(q6, "아직 잘 모르겠어요", "unsure", 3, 0.5);
    }

    private Question createQuestion(int questionNumber, String text, String type, int questionOrder) {
        Question question = new Question();
        question.setQuestionText(text);
        question.setQuestionType(type);
        question.setOrder(questionOrder);
        question.setIsActive(true);
        question.setVersion(1);
        Question savedQuestion = questionRepository.save(question);
        
        // 질문 번호를 기억하기 위해 Q1, Q2 등의 형태로 저장할 수도 있지만,
        // 현재는 order로 관리하므로 그대로 사용
        return savedQuestion;
    }

    private void createChoice(Question question, String text, String value, int order, double weight) {
        QuestionChoice choice = new QuestionChoice();
        choice.setQuestion(question);
        choice.setChoiceText(text);
        choice.setChoiceValue(value);
        choice.setOrder(order);
        choice.setTemperatureWeight(weight);
        questionChoiceRepository.save(choice);
    }
}

