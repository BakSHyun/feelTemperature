package com.rstracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchingStatusDto {
    private String code;
    private String status;
    private Integer participantCount;
    private Integer maxParticipants = 2;
}

