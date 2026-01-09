package com.rstracker.service;

import com.rstracker.dto.MatchingDto;
import com.rstracker.dto.ParticipantDto;
import com.rstracker.entity.Matching;
import com.rstracker.entity.MatchingStatus;
import com.rstracker.entity.Participant;
import com.rstracker.mapper.MatchingMapper;
import com.rstracker.repository.MatchingRepository;
import com.rstracker.repository.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * MatchingService 단위 테스트
 * Mockito를 사용하여 의존성을 격리하고 테스트합니다.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MatchingService 테스트")
class MatchingServiceTest {

    @Mock
    private MatchingRepository matchingRepository;
    
    @Mock
    private ParticipantRepository participantRepository;
    
    @Mock
    private MatchingMapper matchingMapper;
    
    @InjectMocks
    private MatchingService matchingService;
    
    private Matching testMatching;
    
    @BeforeEach
    void setUp() {
        testMatching = new Matching();
        testMatching.setId(1L);
        testMatching.setCode("ABC123");
        testMatching.setStatus(MatchingStatus.WAITING.getValue());
    }
    
    @Test
    @DisplayName("매칭 생성 성공")
    @SuppressWarnings("null")
    void createMatching_Success() {
        // given
        when(matchingRepository.existsByCode(anyString())).thenReturn(false);
        when(matchingRepository.save(any(Matching.class))).thenReturn(testMatching);
        
        MatchingDto expectedDto = new MatchingDto();
        expectedDto.setId(1L);
        expectedDto.setCode("ABC123");
        when(matchingMapper.toDto(any(Matching.class))).thenReturn(expectedDto);
        
        // when
        MatchingDto result = matchingService.createMatching();
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("ABC123");
        verify(matchingRepository, times(1)).save(any(Matching.class));
        verify(matchingMapper, times(1)).toDto(any(Matching.class));
    }
    
    @Test
    @DisplayName("매칭 참여 성공")
    @SuppressWarnings("null")
    void joinMatching_Success() {
        // given
        String code = "ABC123";
        when(matchingRepository.findByCode(code)).thenReturn(Optional.of(testMatching));
        when(participantRepository.countByMatchingId(anyLong())).thenReturn(1L);
        when(participantRepository.save(any(Participant.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        ParticipantDto expectedDto = new ParticipantDto();
        expectedDto.setParticipantCode("participant-code");
        when(matchingMapper.toDto(any(Participant.class))).thenReturn(expectedDto);
        
        // when
        ParticipantDto result = matchingService.joinMatching(code);
        
        // then
        assertThat(result).isNotNull();
        verify(participantRepository, times(1)).save(any());
    }
}

