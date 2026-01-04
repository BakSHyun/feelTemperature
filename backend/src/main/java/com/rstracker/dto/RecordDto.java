package com.rstracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordDto {
    private Long id;
    private String recordId;
    private Long matchingId;
    private Double temperature;
    private Double temperatureDiff;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Map<String, Object> summary;
}

