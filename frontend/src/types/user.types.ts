/**
 * 회원 관련 타입 정의
 * 
 * 백엔드 API와 동일한 구조를 유지합니다.
 * @see backend/src/main/java/com/rstracker/dto/UserDto.java
 */

/**
 * 성별 타입
 */
export type Gender = 'MALE' | 'FEMALE' | 'OTHER';

/**
 * 회원 상태 타입
 */
export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED' | 'DELETED';

/**
 * 인증 상태 타입
 */
export type VerificationStatus = 'UNVERIFIED' | 'PHONE_VERIFIED' | 'EMAIL_VERIFIED' | 'FULLY_VERIFIED';

/**
 * 회원 타입
 */
export interface User {
  /** 회원 고유 ID */
  id: number;
  /** 아이디 */
  userid: string;
  /** 이메일 (선택사항) */
  email?: string;
  /** 휴대폰 번호 (필수) */
  phoneNumber: string;
  /** 이름 (필수) */
  name: string;
  /** 생년월일 (필수) */
  birthDate: string;
  /** 성별 (필수) */
  gender: Gender;
  /** 회원 상태 */
  status: UserStatus;
  /** 인증 상태 */
  verificationStatus: VerificationStatus;
  /** 휴대폰 인증 일시 */
  phoneVerifiedAt?: string;
  /** 이메일 인증 일시 */
  emailVerifiedAt?: string;
  /** 마지막 로그인 일시 */
  lastLoginAt?: string;
  /** 생성일시 */
  createdAt: string;
  /** 수정일시 */
  updatedAt: string;
}

/**
 * 회원 생성 DTO
 */
export interface CreateUserDto {
  /** 아이디 (필수) */
  userid: string;
  /** 이메일 (선택사항) */
  email?: string;
  /** 휴대폰 번호 (필수) */
  phoneNumber: string;
  /** 이름 (필수) */
  name: string;
  /** 생년월일 (필수) */
  birthDate: string;
  /** 성별 (필수) */
  gender: Gender;
}

/**
 * 회원 수정 DTO
 */
export interface UpdateUserDto {
  /** 이메일 (선택사항) */
  email?: string;
  /** 이름 */
  name?: string;
  /** 생년월일 */
  birthDate?: string;
  /** 성별 */
  gender?: Gender;
  /** 회원 상태 */
  status?: UserStatus;
}

/**
 * 회원 히스토리 타입
 */
export interface UserHistory {
  /** 회원 정보 */
  user: User;
  /** 참여한 매칭 목록 */
  matchings: MatchingHistory[];
  /** 총 참여 횟수 */
  totalParticipations: number;
}

/**
 * 매칭 히스토리 타입
 */
export interface MatchingHistory {
  /** 매칭 ID */
  matchingId: number;
  /** 매칭 코드 */
  matchingCode: string;
  /** 매칭 상태 */
  status: string;
  /** 참여일시 */
  joinedAt: string;
  /** 매칭 완료일시 */
  completedAt?: string;
  /** 기록 정보 */
  record?: RecordInfo;
  /** 매칭 상대 정보 */
  otherParticipants: ParticipantInfo[];
  /** 답변 정보 */
  answers: AnswerInfo[];
}

/**
 * 기록 정보 타입
 */
export interface RecordInfo {
  /** 기록 ID */
  recordId: string;
  /** 온도 */
  temperature?: number;
  /** 온도 차이 */
  temperatureDiff?: number;
  /** 생성일시 */
  createdAt: string;
}

/**
 * 참여자 정보 타입
 */
export interface ParticipantInfo {
  /** 참여자 코드 */
  participantCode: string;
  /** 참여일시 */
  joinedAt: string;
}

/**
 * 답변 정보 타입
 */
export interface AnswerInfo {
  /** 질문 ID */
  questionId: number;
  /** 질문 텍스트 */
  questionText: string;
  /** 질문 순서 */
  questionOrder: number;
  /** 선택지 텍스트 */
  choiceText: string;
  /** 선택지 값 */
  choiceValue: string;
  /** 답변일시 */
  answeredAt: string;
}
