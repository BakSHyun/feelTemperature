package com.rstracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchingDto {
    private Long id;
    private String code;
    private String qrCodePath;
    private String status;
    private LocalDateTime createdAt;
}

