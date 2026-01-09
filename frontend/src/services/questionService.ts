/**
 * 질문 관리 API 서비스
 * 
 * 질문 관련 API 호출을 담당합니다.
 * @see backend/src/main/java/com/rstracker/controller/QuestionController.java
 */

import api from './api';
import type { Question, CreateQuestionDto, UpdateQuestionDto, QuestionCategory } from '../types/question.types';

/**
 * 질문 서비스
 */
export const questionService = {
  /**
   * 활성 질문 목록 조회
   * GET /api/questions?category=INITIAL_MATCHING
   * 
   * @param category 질문 카테고리 (선택사항)
   */
  getAll: async (category?: QuestionCategory): Promise<Question[]> => {
    const params = category ? { category } : {};
    const response = await api.get<Question[]>('/questions', { params });
    return response.data;
  },

  /**
   * 질문 상세 조회
   * GET /api/questions/{id}
   */
  getById: async (id: number): Promise<Question> => {
    const response = await api.get<Question>(`/questions/${id}`);
    return response.data;
  },

  /**
   * 질문 생성
   * POST /api/questions
   * 
   * TODO: CMS에서 질문 생성 기능 구현 시 추가
   */
  create: async (data: CreateQuestionDto): Promise<Question> => {
    const response = await api.post<Question>('/questions', data);
    return response.data;
  },

  /**
   * 질문 수정
   * PUT /api/questions/{id}
   * 
   * TODO: CMS에서 질문 수정 기능 구현 시 추가
   */
  update: async (id: number, data: UpdateQuestionDto): Promise<Question> => {
    const response = await api.put<Question>(`/questions/${id}`, data);
    return response.data;
  },

  /**
   * 질문 삭제
   * DELETE /api/questions/{id}
   * 
   * TODO: CMS에서 질문 삭제 기능 구현 시 추가
   */
  delete: async (id: number): Promise<void> => {
    await api.delete(`/questions/${id}`);
  },
};

