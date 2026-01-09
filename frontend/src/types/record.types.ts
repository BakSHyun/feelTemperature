/**
 * 기록 관련 타입 정의
 * 
 * 백엔드 API와 동일한 구조를 유지합니다.
 * @see backend/src/main/java/com/rstracker/dto/RecordDto.java
 */

/**
 * 기록 타입
 */
export interface Record {
  /** 기록 고유 ID */
  id: number;
  /** 기록 ID (UUID 형식) */
  recordId: string;
  /** 매칭 ID */
  matchingId: number;
  /** 온도 값 */
  temperature: number;
  /** 온도 차이 */
  temperatureDiff: number;
  /** 활성화 여부 */
  isActive: boolean;
  /** 생성일시 */
  createdAt: string;
  /** 요약 정보 (JSON) */
  summary?: { [key: string]: unknown };
}

/**
 * 기록 생성 DTO (매칭 ID만 필요)
 */
export interface CreateRecordDto {
  matchingId: number;
}

