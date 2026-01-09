package com.rstracker.controller;

import com.rstracker.dto.CreateUserDto;
import com.rstracker.dto.UpdateUserDto;
import com.rstracker.dto.UserDto;
import com.rstracker.dto.UserHistoryDto;
import com.rstracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 회원 관리 API 컨트롤러
 * 
 * 회원 생성, 조회, 수정, 삭제 기능을 제공합니다.
 * 본인인증 서비스 연동을 고려한 구조입니다.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원 생성
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserDto createDto) {
        UserDto user = userService.createUser(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * 회원 조회 (ID)
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 회원 조회 (아이디)
     * GET /api/users/userid/{userid}
     */
    @GetMapping("/userid/{userid}")
    public ResponseEntity<UserDto> getUserByUserid(@PathVariable String userid) {
        UserDto user = userService.getUserByUserid(userid);
        return ResponseEntity.ok(user);
    }

    /**
     * 회원 목록 조회 (페이징)
     * GET /api/users?page=0&size=10&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<UserDto> users = userService.getUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * 회원 목록 조회 (전체)
     * GET /api/users/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * 회원 수정
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDto updateDto) {
        UserDto user = userService.updateUser(id, updateDto);
        return ResponseEntity.ok(user);
    }

    /**
     * 회원 삭제 (소프트 삭제)
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 아이디 중복 확인
     * GET /api/users/check/userid/{userid}
     */
    @GetMapping("/check/userid/{userid}")
    public ResponseEntity<Boolean> checkUserid(@PathVariable String userid) {
        boolean available = userService.isUseridAvailable(userid);
        return ResponseEntity.ok(available);
    }

    /**
     * 전화번호 중복 확인
     * GET /api/users/check/phone/{phoneNumber}
     */
    @GetMapping("/check/phone/{phoneNumber}")
    public ResponseEntity<Boolean> checkPhoneNumber(@PathVariable String phoneNumber) {
        boolean available = userService.isPhoneNumberAvailable(phoneNumber);
        return ResponseEntity.ok(available);
    }

    /**
     * 이메일 중복 확인
     * GET /api/users/check/email/{email}
     */
    @GetMapping("/check/email/{email}")
    public ResponseEntity<Boolean> checkEmail(@PathVariable String email) {
        boolean available = userService.isEmailAvailable(email);
        return ResponseEntity.ok(available);
    }

    /**
     * 회원 히스토리 조회
     * GET /api/users/{id}/history
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<UserHistoryDto> getUserHistory(@PathVariable Long id) {
        UserHistoryDto history = userService.getUserHistory(id);
        return ResponseEntity.ok(history);
    }
}

