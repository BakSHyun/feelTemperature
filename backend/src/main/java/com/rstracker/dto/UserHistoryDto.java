package com.rstracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 회원 히스토리 정보 DTO
 * 
 * 회원의 매칭 내역, 답변 내역 등을 포함합니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserHistoryDto {
    
    /** 회원 정보 */
    private UserDto user;
    
    /** 참여한 매칭 목록 */
    private List<MatchingHistoryDto> matchings;
    
    /** 총 참여 횟수 */
    private long totalParticipations;
    
    /**
     * 매칭 히스토리 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchingHistoryDto {
        /** 매칭 ID */
        private Long matchingId;
        
        /** 매칭 코드 */
        private String matchingCode;
        
        /** 매칭 상태 */
        private String status;
        
        /** 참여일시 */
        private LocalDateTime joinedAt;
        
        /** 매칭 완료일시 */
        private LocalDateTime completedAt;
        
        /** 기록 정보 */
        private RecordInfoDto record;
        
        /** 매칭 상대 정보 */
        private List<ParticipantInfoDto> otherParticipants;
        
        /** 답변 정보 */
        private List<AnswerInfoDto> answers;
    }
    
    /**
     * 기록 정보 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordInfoDto {
        /** 기록 ID */
        private String recordId;
        
        /** 온도 */
        private Double temperature;
        
        /** 온도 차이 */
        private Double temperatureDiff;
        
        /** 생성일시 */
        private LocalDateTime createdAt;
    }
    
    /**
     * 참여자 정보 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipantInfoDto {
        /** 참여자 코드 */
        private String participantCode;
        
        /** 참여일시 */
        private LocalDateTime joinedAt;
    }
    
    /**
     * 답변 정보 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerInfoDto {
        /** 질문 ID */
        private Long questionId;
        
        /** 질문 텍스트 */
        private String questionText;
        
        /** 질문 순서 */
        private Integer questionOrder;
        
        /** 선택지 텍스트 */
        private String choiceText;
        
        /** 선택지 값 */
        private String choiceValue;
        
        /** 답변일시 */
        private LocalDateTime answeredAt;
    }
}

