package com.rstracker.constants;

public class AppConstants {
    
    // 매칭 관련 상수
    public static final int MAX_PARTICIPANTS_PER_MATCHING = 2;
    public static final int MAX_MATCHING_CODE_GENERATION_ATTEMPTS = 10;
    public static final int MATCHING_CODE_LENGTH = 6;
    
    // 온도 계산 가중치 (질문 순서)
    public static final int QUESTION_ORDER_SENTIMENT = 3;      // Q3 분위기
    public static final int QUESTION_ORDER_EXPECTATION = 4;    // Q4 기대
    public static final int QUESTION_ORDER_DISTANCE = 5;       // Q5 거리
    public static final int QUESTION_ORDER_COMFORT = 6;        // Q6 편안함
    
    private AppConstants() {
        // 인스턴스화 방지
    }
}

