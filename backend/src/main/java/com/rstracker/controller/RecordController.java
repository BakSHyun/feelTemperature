package com.rstracker.controller;

import com.rstracker.dto.RecordDto;
import com.rstracker.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @PostMapping("/create/{matchingId}")
    public ResponseEntity<RecordDto> createRecord(@PathVariable Long matchingId) {
        RecordDto record = recordService.createRecord(matchingId);
        return ResponseEntity.ok(record);
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<RecordDto> getRecord(@PathVariable String recordId) {
        RecordDto record = recordService.getRecord(recordId);
        return ResponseEntity.ok(record);
    }

    @GetMapping("/matching/{matchingId}")
    public ResponseEntity<RecordDto> getRecordByMatchingId(@PathVariable Long matchingId) {
        RecordDto record = recordService.getRecordByMatchingId(matchingId);
        return ResponseEntity.ok(record);
    }

    @PutMapping("/{recordId}/deactivate")
    public ResponseEntity<Void> deactivateRecord(@PathVariable String recordId) {
        recordService.deactivateRecord(recordId);
        return ResponseEntity.ok().build();
    }
}

