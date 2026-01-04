package com.rstracker.mapper;

import com.rstracker.dto.MatchingDto;
import com.rstracker.dto.ParticipantDto;
import com.rstracker.entity.Matching;
import com.rstracker.entity.Participant;
import org.springframework.stereotype.Component;

@Component
public class MatchingMapper {

    public MatchingDto toDto(Matching matching) {
        if (matching == null) {
            return null;
        }
        
        MatchingDto dto = new MatchingDto();
        dto.setId(matching.getId());
        dto.setCode(matching.getCode());
        dto.setQrCodePath(matching.getQrCodePath());
        dto.setStatus(matching.getStatus());
        dto.setCreatedAt(matching.getCreatedAt());
        return dto;
    }

    public ParticipantDto toDto(Participant participant) {
        if (participant == null) {
            return null;
        }
        
        ParticipantDto dto = new ParticipantDto();
        dto.setId(participant.getId());
        dto.setParticipantCode(participant.getParticipantCode());
        dto.setJoinedAt(participant.getJoinedAt());
        return dto;
    }
}

