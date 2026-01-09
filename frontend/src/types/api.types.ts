/**
 * API 응답 기본 타입
 */

/**
 * API 에러 응답
 */
export interface ApiError {
  message: string;
  status: number;
  timestamp?: string;
  path?: string;
}

/**
 * API 응답 래퍼 (필요시 사용)
 */
export interface ApiResponse<T> {
  data: T;
  message?: string;
  status?: number;
}

