/**
 * 매칭 관련 타입 정의
 * 
 * 백엔드 API와 동일한 구조를 유지합니다.
 * @see backend/src/main/java/com/rstracker/dto/MatchingDto.java
 */

/**
 * 매칭 상태 타입
 */
export type MatchingStatus = 'WAITING' | 'ESTABLISHED' | 'COMPLETED';

/**
 * 참여자 타입
 */
export interface Participant {
  /** 참여자 고유 ID */
  id: number;
  /** 참여자 코드 */
  participantCode: string;
  /** 매칭 ID */
  matchingId: number;
  /** 참여자 이름 (선택) */
  name?: string;
}

/**
 * 매칭 타입
 */
export interface Matching {
  /** 매칭 고유 ID */
  id: number;
  /** 매칭 코드 */
  code: string;
  /** 매칭 상태 */
  status: MatchingStatus;
  /** 생성일시 */
  createdAt: string;
  /** 완료일시 */
  completedAt?: string;
  /** 참여자 목록 */
  participants?: Participant[];
}

/**
 * 매칭 상태 DTO
 */
export interface MatchingStatusDto {
  /** 매칭 코드 */
  code: string;
  /** 매칭 상태 */
  status: MatchingStatus;
  /** 참여자 수 */
  participantCount: number;
  /** 최대 참여자 수 */
  maxParticipants: number;
}

