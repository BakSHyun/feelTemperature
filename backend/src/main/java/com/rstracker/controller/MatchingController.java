package com.rstracker.controller;

import com.rstracker.dto.MatchingDto;
import com.rstracker.dto.MatchingStatusDto;
import com.rstracker.dto.ParticipantDto;
import com.rstracker.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 매칭 관리 API 컨트롤러
 * 
 * API 버전: v1 (마이크로서비스 전환 시 버전 관리)
 * 현재는 /api/matching이지만, 전환 시 /api/v1/matching으로 변경
 */
@RestController
@RequestMapping("/matching")  // TODO: 마이크로서비스 전환 시 "/v1/matching"으로 변경
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    @PostMapping("/create")
    public ResponseEntity<MatchingDto> createMatching() {
        MatchingDto matching = matchingService.createMatching();
        return ResponseEntity.ok(matching);
    }

    @PostMapping("/join/{code}")
    public ResponseEntity<ParticipantDto> joinMatching(@PathVariable String code) {
        ParticipantDto participant = matchingService.joinMatching(code);
        return ResponseEntity.ok(participant);
    }

    @GetMapping("/{code}")
    public ResponseEntity<MatchingDto> getMatching(@PathVariable String code) {
        MatchingDto matching = matchingService.getMatching(code);
        return ResponseEntity.ok(matching);
    }

    @GetMapping("/status/{code}")
    public ResponseEntity<MatchingStatusDto> getMatchingStatus(@PathVariable String code) {
        MatchingStatusDto status = matchingService.getMatchingStatus(code);
        return ResponseEntity.ok(status);
    }
}

