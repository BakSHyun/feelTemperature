package com.rstracker.service;

import com.rstracker.dto.CreateUserDto;
import com.rstracker.dto.UpdateUserDto;
import com.rstracker.dto.UserDto;
import com.rstracker.dto.UserHistoryDto;
import com.rstracker.entity.User;
import com.rstracker.exception.BusinessException;
import com.rstracker.exception.ResourceNotFoundException;
import com.rstracker.mapper.UserMapper;
import com.rstracker.repository.UserRepository;
import com.rstracker.repository.ParticipantRepository;
import com.rstracker.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 회원 관리 서비스
 * 
 * 회원 생성, 조회, 수정, 삭제 기능을 제공합니다.
 * 본인인증 서비스 연동을 고려한 구조입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final AnswerRepository answerRepository;
    private final UserMapper userMapper;

    /**
     * 회원 생성
     * 
     * 아이디, 전화번호 중복 체크 후 회원을 생성합니다.
     * 
     * @param createDto 회원 생성 정보
     * @return 생성된 회원 정보
     * @throws BusinessException 아이디 또는 전화번호가 이미 존재하는 경우
     */
    @Transactional
    public UserDto createUser(CreateUserDto createDto) {
        log.debug("Creating user with userid: {}", createDto.getUserid());

        // 아이디 중복 체크
        if (userRepository.existsByUserid(createDto.getUserid())) {
            throw new BusinessException("이미 사용 중인 아이디입니다: " + createDto.getUserid());
        }

        // 전화번호 중복 체크
        if (userRepository.existsByPhoneNumber(createDto.getPhoneNumber())) {
            throw new BusinessException("이미 사용 중인 전화번호입니다: " + createDto.getPhoneNumber());
        }

        // 이메일이 제공된 경우 중복 체크
        if (createDto.getEmail() != null && !createDto.getEmail().isBlank()) {
            if (userRepository.existsByEmail(createDto.getEmail())) {
                throw new BusinessException("이미 사용 중인 이메일입니다: " + createDto.getEmail());
            }
        }

        // 엔티티 생성 및 저장
        User user = userMapper.toEntity(createDto);
        @SuppressWarnings("null") // Spring Data JPA의 save()는 항상 non-null을 반환
        User savedUser = userRepository.save(user);

        log.info("User created successfully: userid={}, id={}", savedUser.getUserid(), savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    /**
     * 회원 조회 (ID)
     * 
     * @param id 회원 ID
     * @return 회원 정보
     * @throws ResourceNotFoundException 회원을 찾을 수 없는 경우
     */
    public UserDto getUserById(Long id) {
        log.debug("Fetching user by id: {}", id);
        @SuppressWarnings("null")
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다: " + id));
        return userMapper.toDto(user);
    }

    /**
     * 회원 조회 (아이디)
     * 
     * @param userid 회원 아이디
     * @return 회원 정보
     * @throws ResourceNotFoundException 회원을 찾을 수 없는 경우
     */
    public UserDto getUserByUserid(String userid) {
        log.debug("Fetching user by userid: {}", userid);
        User user = userRepository.findByUserid(userid)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다: " + userid));
        return userMapper.toDto(user);
    }

    /**
     * 회원 목록 조회 (페이징)
     * 
     * @param pageable 페이징 정보
     * @return 회원 목록 (페이징)
     */
    public Page<UserDto> getUsers(Pageable pageable) {
        log.debug("Fetching users with pageable: {}", pageable);
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    /**
     * 회원 목록 조회 (전체)
     * 
     * @return 회원 목록
     */
    public List<UserDto> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 회원 수정
     * 
     * @param id 회원 ID
     * @param updateDto 수정할 정보
     * @return 수정된 회원 정보
     * @throws ResourceNotFoundException 회원을 찾을 수 없는 경우
     * @throws BusinessException 이메일이 이미 사용 중인 경우
     */
    @Transactional
    public UserDto updateUser(Long id, UpdateUserDto updateDto) {
        log.debug("Updating user id: {}", id);

        @SuppressWarnings("null")
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다: " + id));

        // 이메일 변경 시 중복 체크
        if (updateDto.getEmail() != null && !updateDto.getEmail().isBlank()) {
            if (!updateDto.getEmail().equals(user.getEmail()) && 
                userRepository.existsByEmail(updateDto.getEmail())) {
                throw new BusinessException("이미 사용 중인 이메일입니다: " + updateDto.getEmail());
            }
            user.setEmail(updateDto.getEmail());
        }

        // 선택적 필드 업데이트
        if (updateDto.getName() != null) {
            user.setName(updateDto.getName());
        }
        if (updateDto.getBirthDate() != null) {
            user.setBirthDate(updateDto.getBirthDate());
        }
        if (updateDto.getGender() != null) {
            user.setGender(updateDto.getGender());
        }
        if (updateDto.getStatus() != null) {
            user.setStatus(updateDto.getStatus());
        }

        @SuppressWarnings("null") // Spring Data JPA의 save()는 항상 non-null을 반환
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: id={}, userid={}", updatedUser.getId(), updatedUser.getUserid());
        return userMapper.toDto(updatedUser);
    }

    /**
     * 회원 삭제 (소프트 삭제)
     * 
     * 실제로는 상태를 DELETED로 변경합니다.
     * 
     * @param id 회원 ID
     * @throws ResourceNotFoundException 회원을 찾을 수 없는 경우
     */
    @Transactional
    public void deleteUser(Long id) {
        log.debug("Deleting user id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다: " + id));

        user.setStatus(User.UserStatus.DELETED);
        @SuppressWarnings("null") // Spring Data JPA의 save()는 항상 non-null을 반환
        User deletedUser = userRepository.save(user);
        log.debug("User soft deleted: id={}", deletedUser.getId());

        log.info("User deleted (soft delete) successfully: id={}, userid={}", user.getId(), user.getUserid());
    }

    /**
     * 아이디 중복 확인
     * 
     * @param userid 확인할 아이디
     * @return 사용 가능 여부 (true: 사용 가능, false: 사용 불가)
     */
    public boolean isUseridAvailable(String userid) {
        return !userRepository.existsByUserid(userid);
    }

    /**
     * 전화번호 중복 확인
     * 
     * @param phoneNumber 확인할 전화번호
     * @return 사용 가능 여부 (true: 사용 가능, false: 사용 불가)
     */
    public boolean isPhoneNumberAvailable(String phoneNumber) {
        return !userRepository.existsByPhoneNumber(phoneNumber);
    }

    /**
     * 이메일 중복 확인
     * 
     * @param email 확인할 이메일
     * @return 사용 가능 여부 (true: 사용 가능, false: 사용 불가)
     */
    public boolean isEmailAvailable(String email) {
        if (email == null || email.isBlank()) {
            return true; // 이메일은 선택사항이므로 null/빈 문자열은 사용 가능
        }
        return !userRepository.existsByEmail(email);
    }

    /**
     * 회원 히스토리 조회
     * 
     * 회원의 매칭 내역, 답변 내역, 기록 정보를 조회합니다.
     * 
     * @param userId 회원 ID
     * @return 회원 히스토리 정보
     * @throws ResourceNotFoundException 회원을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public UserHistoryDto getUserHistory(Long userId) {
        log.debug("Fetching user history for userId: {}", userId);

        @SuppressWarnings("null")
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다: " + userId));

        // 회원이 참여한 매칭 목록 조회 (Fetch Join으로 N+1 문제 해결)
        // 매칭, 기록, 다른 참여자 정보를 한 번에 조회
        List<com.rstracker.entity.Participant> participants = participantRepository.findByUserIdWithMatchingAndRecord(userId);

        // 모든 참여자 ID 수집
        List<Long> participantIds = participants.stream()
                .map(com.rstracker.entity.Participant::getId)
                .collect(Collectors.toList());

        // 모든 답변을 한 번에 조회 (Fetch Join으로 N+1 문제 해결)
        // 각 참여자별로 별도 쿼리 실행 방지 - 단일 쿼리로 모든 답변 조회
        Map<Long, List<com.rstracker.entity.Answer>> answersByParticipant = participantIds.isEmpty()
                ? java.util.Collections.emptyMap()
                : answerRepository.findByParticipantIdsWithQuestionAndChoice(participantIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                answer -> answer.getParticipant().getId()
                        ));

        // 매칭 히스토리 구성
        List<UserHistoryDto.MatchingHistoryDto> matchingHistories = participants.stream()
                .map(participant -> {
                    // Fetch Join으로 이미 로드된 매칭 정보 사용 (추가 쿼리 없음)
                    com.rstracker.entity.Matching matching = participant.getMatching();

                    // 다른 참여자 정보 (Fetch Join으로 이미 로드됨)
                    List<UserHistoryDto.ParticipantInfoDto> otherParticipants = matching.getParticipants().stream()
                            .filter(p -> !p.getId().equals(participant.getId()))
                            .map(p -> new UserHistoryDto.ParticipantInfoDto(
                                    p.getParticipantCode(),
                                    p.getJoinedAt()
                            ))
                            .collect(Collectors.toList());

                    // 기록 정보 (Fetch Join으로 이미 로드됨)
                    UserHistoryDto.RecordInfoDto recordInfo = null;
                    if (matching.getRecord() != null) {
                        com.rstracker.entity.Record record = matching.getRecord();
                        recordInfo = new UserHistoryDto.RecordInfoDto(
                                record.getRecordId(),
                                record.getTemperature(),
                                record.getTemperatureDiff(),
                                record.getCreatedAt()
                        );
                    }

                    // 답변 정보 (이미 조회된 데이터 사용)
                    List<UserHistoryDto.AnswerInfoDto> answers = answersByParticipant
                            .getOrDefault(participant.getId(), java.util.Collections.emptyList())
                            .stream()
                            .map(answer -> {
                                // Fetch Join으로 이미 로드된 질문과 선택지 정보 사용
                                com.rstracker.entity.Question question = answer.getQuestion();
                                com.rstracker.entity.QuestionChoice choice = answer.getChoice();
                                return new UserHistoryDto.AnswerInfoDto(
                                        question.getId(),
                                        question.getQuestionText(),
                                        question.getOrder(),
                                        choice.getChoiceText(),
                                        choice.getChoiceValue(),
                                        answer.getAnsweredAt()
                                );
                            })
                            .sorted((a, b) -> a.getQuestionOrder().compareTo(b.getQuestionOrder()))
                            .collect(Collectors.toList());

                    return new UserHistoryDto.MatchingHistoryDto(
                            matching.getId(),
                            matching.getCode(),
                            matching.getStatus(),
                            participant.getJoinedAt(),
                            matching.getCompletedAt(),
                            recordInfo,
                            otherParticipants,
                            answers
                    );
                })
                .sorted((a, b) -> b.getJoinedAt().compareTo(a.getJoinedAt())) // 최신순 정렬
                .collect(Collectors.toList());

        UserHistoryDto historyDto = new UserHistoryDto();
        historyDto.setUser(userMapper.toDto(user));
        historyDto.setMatchings(matchingHistories);
        historyDto.setTotalParticipations(participants.size());

        return historyDto;
    }
}

