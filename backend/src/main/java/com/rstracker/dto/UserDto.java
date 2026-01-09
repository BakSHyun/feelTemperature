package com.rstracker.dto;

import com.rstracker.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 회원 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String userid;
    private String email;
    private String phoneNumber;
    private String name;
    private LocalDate birthDate;
    private User.Gender gender;
    private User.UserStatus status;
    private User.VerificationStatus verificationStatus;
    private LocalDateTime phoneVerifiedAt;
    private LocalDateTime emailVerifiedAt;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

