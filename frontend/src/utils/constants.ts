/**
 * 애플리케이션 상수
 * 
 * 하드코딩된 값을 상수로 정의하여 중복을 제거하고 유지보수를 용이하게 합니다.
 */

/**
 * API 기본 URL
 */
export const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

/**
 * 로컬 스토리지 키
 */
export const STORAGE_KEYS = {
  /** 인증 토큰 */
  TOKEN: 'token',
  /** 사용자 정보 */
  USER: 'user',
} as const;

/**
 * 질문 타입 목록
 */
export const QUESTION_TYPES = {
  CONTEXT: 'context',
  SENTIMENT: 'sentiment',
  EXPECTATION: 'expectation',
  DISTANCE: 'distance',
  COMFORT: 'comfort',
} as const;

/**
 * 매칭 상태
 */
export const MATCHING_STATUS = {
  WAITING: 'WAITING',
  ESTABLISHED: 'ESTABLISHED',
  COMPLETED: 'COMPLETED',
} as const;

/**
 * 페이지네이션 기본값
 */
export const PAGINATION = {
  /** 페이지당 항목 수 */
  PAGE_SIZE: 10,
  /** 최대 페이지 사이즈 */
  MAX_PAGE_SIZE: 100,
} as const;

