package com.rstracker.repository;

import com.rstracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 회원 Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 아이디로 회원 조회
     */
    Optional<User> findByUserid(String userid);

    /**
     * 이메일로 회원 조회
     */
    Optional<User> findByEmail(String email);

    /**
     * 휴대폰 번호로 회원 조회
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * 아이디 존재 여부 확인
     */
    boolean existsByUserid(String userid);

    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 휴대폰 번호 존재 여부 확인
     */
    boolean existsByPhoneNumber(String phoneNumber);
}

