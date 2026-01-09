/**
 * 매칭 관리 API 서비스
 * 
 * 매칭 관련 API 호출을 담당합니다.
 * @see backend/src/main/java/com/rstracker/controller/MatchingController.java
 */

import api from './api';
import type { Matching, MatchingStatusDto } from '../types/matching.types';

/**
 * 매칭 서비스
 */
export const matchingService = {
  /**
   * 매칭 생성
   * POST /api/matching/create
   */
  create: async (): Promise<Matching> => {
    const response = await api.post<Matching>('/matching/create');
    return response.data;
  },

  /**
   * 매칭 참여
   * POST /api/matching/join/{code}
   */
  join: async (code: string): Promise<{ participantCode: string }> => {
    const response = await api.post<{ participantCode: string }>(`/matching/join/${code}`);
    return response.data;
  },

  /**
   * 매칭 조회
   * GET /api/matching/{code}
   */
  getByCode: async (code: string): Promise<Matching> => {
    const response = await api.get<Matching>(`/matching/${code}`);
    return response.data;
  },

  /**
   * 매칭 상태 조회
   * GET /api/matching/status/{code}
   */
  getStatus: async (code: string): Promise<MatchingStatusDto> => {
    const response = await api.get<MatchingStatusDto>(`/matching/status/${code}`);
    return response.data;
  },
};

