package com.rstracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 회원 엔티티
 * 
 * 본인인증 서비스 연동을 고려한 구조
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_userid", columnList = "userid"),
    @Index(name = "idx_users_email", columnList = "email"),
    @Index(name = "idx_users_phone_number", columnList = "phoneNumber"),
    @Index(name = "idx_users_status", columnList = "status"),
    @Index(name = "idx_users_verification_status", columnList = "verificationStatus")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String userid;

    @Column(length = 255)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "verification_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;

    @Column(name = "phone_verified_at")
    private LocalDateTime phoneVerifiedAt;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 성별 Enum
     */
    public enum Gender {
        MALE, FEMALE, OTHER
    }

    /**
     * 회원 상태 Enum
     */
    public enum UserStatus {
        ACTIVE,      // 활성
        INACTIVE,    // 비활성
        SUSPENDED,   // 정지
        DELETED      // 삭제
    }

    /**
     * 인증 상태 Enum
     */
    public enum VerificationStatus {
        UNVERIFIED,        // 미인증
        PHONE_VERIFIED,    // 휴대폰 인증 완료
        EMAIL_VERIFIED,    // 이메일 인증 완료
        FULLY_VERIFIED     // 전체 인증 완료
    }
}

