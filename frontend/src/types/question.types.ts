/**
 * 질문 관련 타입 정의
 * 
 * 백엔드 API와 동일한 구조를 유지합니다.
 * @see backend/src/main/java/com/rstracker/dto/QuestionDto.java
 */

/**
 * 질문 카테고리 타입
 */
export type QuestionCategory = 'INITIAL_MATCHING' | 'TEMPERATURE_REFINE';

/**
 * 질문 선택지 타입
 */
export interface QuestionChoice {
  /** 선택지 고유 ID */
  id: number;
  /** 질문 ID */
  questionId: number;
  /** 선택지 텍스트 */
  choiceText: string;
  /** 선택지 값 */
  choiceValue: string;
  /** 선택지 순서 */
  order: number;
  /** 온도 가중치 */
  temperatureWeight: number;
}

/**
 * 질문 타입
 */
export interface Question {
  /** 질문 고유 ID */
  id: number;
  /** 질문 텍스트 */
  questionText: string;
  /** 질문 타입 (context, sentiment, expectation, distance, comfort) */
  questionType: string;
  /** 질문 카테고리 (INITIAL_MATCHING, TEMPERATURE_REFINE) */
  questionCategory: QuestionCategory;
  /** 질문 순서 (1부터 시작) */
  order: number;
  /** 활성화 여부 */
  isActive: boolean;
  /** 버전 */
  version: number;
  /** 생성일시 */
  createdAt: string;
  /** 수정일시 */
  updatedAt: string;
  /** 선택지 목록 */
  choices: QuestionChoice[];
}

/**
 * 질문 생성 DTO
 */
export interface CreateQuestionDto {
  /** 질문 텍스트 */
  questionText: string;
  /** 질문 타입 */
  questionType: string;
  /** 질문 카테고리 */
  questionCategory: QuestionCategory;
  /** 질문 순서 */
  order: number;
  /** 선택지 목록 */
  choices: Omit<QuestionChoice, 'id' | 'questionId'>[];
}

/**
 * 질문 수정 DTO
 */
export interface UpdateQuestionDto {
  /** 질문 텍스트 */
  questionText?: string;
  /** 질문 타입 */
  questionType?: string;
  /** 질문 카테고리 */
  questionCategory?: QuestionCategory;
  /** 질문 순서 */
  order?: number;
  /** 활성화 여부 */
  isActive?: boolean;
  /** 선택지 목록 */
  choices?: Omit<QuestionChoice, 'id' | 'questionId'>[];
}

