package com.rstracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDto {
    private Long id;
    private String participantCode;
    private LocalDateTime joinedAt;
}

