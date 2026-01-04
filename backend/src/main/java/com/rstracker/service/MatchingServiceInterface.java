package com.rstracker.service;

import com.rstracker.dto.MatchingDto;
import com.rstracker.dto.MatchingStatusDto;
import com.rstracker.dto.ParticipantDto;

/**
 * 매칭 서비스 인터페이스
 * Service 레이어의 계약을 명확히 하고, 테스트 가능성을 높이기 위한 인터페이스
 */
public interface MatchingServiceInterface {
    
    MatchingDto createMatching();
    
    ParticipantDto joinMatching(String code);
    
    MatchingDto getMatching(String code);
    
    MatchingStatusDto getMatchingStatus(String code);
}

