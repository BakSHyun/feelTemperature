package com.rstracker.mapper;

import com.rstracker.dto.RecordDto;
import com.rstracker.entity.Record;
import org.springframework.stereotype.Component;

@Component
public class RecordMapper {

    public RecordDto toDto(Record record) {
        if (record == null) {
            return null;
        }
        
        RecordDto dto = new RecordDto();
        dto.setId(record.getId());
        dto.setRecordId(record.getRecordId());
        dto.setMatchingId(record.getMatching().getId());
        dto.setTemperature(record.getTemperature());
        dto.setTemperatureDiff(record.getTemperatureDiff());
        dto.setIsActive(record.getIsActive());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setSummary(record.getSummary());
        return dto;
    }
}

