package com.rstracker.controller;

import com.rstracker.dto.RecordDto;
import com.rstracker.service.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 기록 관리 API 컨트롤러
 * 
 * 기록 생성, 조회, 목록 조회, 비활성화 기능을 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    /**
     * 기록 생성
     * POST /api/records/create/{matchingId}
     */
    @PostMapping("/create/{matchingId}")
    public ResponseEntity<RecordDto> createRecord(@PathVariable Long matchingId) {
        log.info("Received request to create record for matchingId: {}", matchingId);
        RecordDto record = recordService.createRecord(matchingId);
        return ResponseEntity.ok(record);
    }

    /**
     * 기록 목록 조회 (페이징, 필터링, 정렬)
     * GET /api/records?minTemp=0&maxTemp=100&isActive=true&startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59&page=0&size=20&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<Page<RecordDto>> getRecords(
            @RequestParam(required = false) Double minTemp,
            @RequestParam(required = false) Double maxTemp,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        log.info("Received request to get records with filters: minTemp={}, maxTemp={}, isActive={}, startDate={}, endDate={}",
                minTemp, maxTemp, isActive, startDate, endDate);
        Page<RecordDto> records = recordService.getRecords(minTemp, maxTemp, isActive, startDate, endDate, pageable);
        return ResponseEntity.ok(records);
    }

    /**
     * 기록 상세 조회 (recordId)
     * GET /api/records/{recordId}
     */
    @GetMapping("/{recordId}")
    public ResponseEntity<RecordDto> getRecord(@PathVariable String recordId) {
        log.info("Received request to get record: {}", recordId);
        RecordDto record = recordService.getRecord(recordId);
        return ResponseEntity.ok(record);
    }

    /**
     * 기록 상세 조회 (matchingId)
     * GET /api/records/matching/{matchingId}
     */
    @GetMapping("/matching/{matchingId}")
    public ResponseEntity<RecordDto> getRecordByMatchingId(@PathVariable Long matchingId) {
        log.info("Received request to get record by matchingId: {}", matchingId);
        RecordDto record = recordService.getRecordByMatchingId(matchingId);
        return ResponseEntity.ok(record);
    }

    /**
     * 기록 비활성화
     * PUT /api/records/{recordId}/deactivate
     */
    @PutMapping("/{recordId}/deactivate")
    public ResponseEntity<Void> deactivateRecord(@PathVariable String recordId) {
        log.info("Received request to deactivate record: {}", recordId);
        recordService.deactivateRecord(recordId);
        return ResponseEntity.ok().build();
    }
}

