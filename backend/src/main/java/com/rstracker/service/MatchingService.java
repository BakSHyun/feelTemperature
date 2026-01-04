package com.rstracker.service;

import com.rstracker.constants.AppConstants;
import com.rstracker.dto.MatchingDto;
import com.rstracker.dto.MatchingStatusDto;
import com.rstracker.dto.ParticipantDto;
import com.rstracker.entity.Matching;
import com.rstracker.entity.MatchingStatus;
import com.rstracker.entity.Participant;
import com.rstracker.exception.BusinessException;
import com.rstracker.exception.ResourceNotFoundException;
import com.rstracker.mapper.MatchingMapper;
import com.rstracker.repository.MatchingRepository;
import com.rstracker.repository.ParticipantRepository;
import com.rstracker.util.MatchingCodeGenerator;
import com.rstracker.util.ParticipantCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService implements MatchingServiceInterface {

    private final MatchingRepository matchingRepository;
    private final ParticipantRepository participantRepository;
    private final MatchingMapper matchingMapper;

    @Transactional
    public MatchingDto createMatching() {
        log.debug("Creating new matching");
        
        // 코드 생성 (중복 체크)
        String code;
        int attempts = 0;
        do {
            code = MatchingCodeGenerator.generate();
            if (!matchingRepository.existsByCode(code)) {
                break;
            }
            attempts++;
        } while (attempts < AppConstants.MAX_MATCHING_CODE_GENERATION_ATTEMPTS);

        if (attempts >= AppConstants.MAX_MATCHING_CODE_GENERATION_ATTEMPTS) {
            log.error("Failed to generate unique matching code after {} attempts", attempts);
            throw new BusinessException("매칭 코드 생성에 실패했습니다. 다시 시도해주세요.");
        }

        Matching matching = new Matching();
        matching.setCode(code);
        matching.setStatus(MatchingStatus.WAITING.getValue());
        matching = matchingRepository.save(matching);
        
        log.info("Matching created: {}", code);
        return matchingMapper.toDto(matching);
    }

    @Transactional
    public ParticipantDto joinMatching(String code) {
        log.debug("Attempting to join matching: {}", code);
        
        Matching matching = matchingRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("매칭을 찾을 수 없습니다: " + code));

        if (!MatchingStatus.WAITING.getValue().equals(matching.getStatus())) {
            throw new BusinessException("이미 종료된 매칭입니다");
        }

        long participantCount = participantRepository.countByMatchingId(matching.getId());
        if (participantCount >= AppConstants.MAX_PARTICIPANTS_PER_MATCHING) {
            throw new BusinessException("이미 " + AppConstants.MAX_PARTICIPANTS_PER_MATCHING + "명이 참여했습니다");
        }

        Participant participant = new Participant();
        participant.setMatching(matching);
        participant.setParticipantCode(ParticipantCodeGenerator.generate());
        participant = participantRepository.save(participant);

        // 참여자 수가 2명이 되면 상태 변경
        participantCount++;
        if (participantCount == AppConstants.MAX_PARTICIPANTS_PER_MATCHING) {
            matching.setStatus(MatchingStatus.ESTABLISHED.getValue());
            matchingRepository.save(matching);
            log.info("Matching {} status changed to ESTABLISHED", code);
        }

        log.info("Participant joined matching: {}", code);
        return matchingMapper.toDto(participant);
    }

    @Transactional(readOnly = true)
    public MatchingDto getMatching(String code) {
        Matching matching = matchingRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("매칭을 찾을 수 없습니다: " + code));
        return matchingMapper.toDto(matching);
    }

    @Transactional(readOnly = true)
    public MatchingStatusDto getMatchingStatus(String code) {
        Matching matching = matchingRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("매칭을 찾을 수 없습니다: " + code));

        long participantCount = participantRepository.countByMatchingId(matching.getId());

        MatchingStatusDto dto = new MatchingStatusDto();
        dto.setCode(matching.getCode());
        dto.setStatus(matching.getStatus());
        dto.setParticipantCount((int) participantCount);
        dto.setMaxParticipants(AppConstants.MAX_PARTICIPANTS_PER_MATCHING);
        return dto;
    }
}

