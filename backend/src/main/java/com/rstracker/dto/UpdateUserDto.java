package com.rstracker.dto;

import com.rstracker.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 회원 수정 DTO
 * 
 * 선택적 필드만 포함 (null이 아닌 필드만 업데이트)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {

    /** 이메일 (선택사항) */
    private String email;

    /** 이름 */
    private String name;

    /** 생년월일 */
    private LocalDate birthDate;

    /** 성별 */
    private User.Gender gender;

    /** 회원 상태 */
    private User.UserStatus status;
}

