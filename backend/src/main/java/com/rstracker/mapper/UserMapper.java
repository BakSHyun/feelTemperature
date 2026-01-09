package com.rstracker.mapper;

import com.rstracker.dto.CreateUserDto;
import com.rstracker.dto.UserDto;
import com.rstracker.entity.User;
import org.springframework.stereotype.Component;

/**
 * User 엔티티와 DTO 간 변환을 담당하는 Mapper
 * 
 * Entity와 DTO 간 변환 로직을 중앙화하여 유지보수성 향상
 */
@Component
public class UserMapper {

    /**
     * User 엔티티를 UserDto로 변환
     * 
     * @param user 변환할 User 엔티티
     * @return UserDto
     */
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUserid(user.getUserid());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setName(user.getName());
        dto.setBirthDate(user.getBirthDate());
        dto.setGender(user.getGender());
        dto.setStatus(user.getStatus());
        dto.setVerificationStatus(user.getVerificationStatus());
        dto.setPhoneVerifiedAt(user.getPhoneVerifiedAt());
        dto.setEmailVerifiedAt(user.getEmailVerifiedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        return dto;
    }

    /**
     * CreateUserDto를 User 엔티티로 변환
     * 
     * @param createDto 생성할 회원 정보 DTO
     * @return User 엔티티 (저장 전)
     */
    public User toEntity(CreateUserDto createDto) {
        if (createDto == null) {
            return null;
        }

        User user = new User();
        user.setUserid(createDto.getUserid());
        user.setEmail(createDto.getEmail());
        user.setPhoneNumber(createDto.getPhoneNumber());
        user.setName(createDto.getName());
        user.setBirthDate(createDto.getBirthDate());
        user.setGender(createDto.getGender());
        // status와 verificationStatus는 기본값 사용 (ACTIVE, UNVERIFIED)

        return user;
    }
}

