/**
 * 기록 관리 API 서비스
 * 
 * 기록 관련 API 호출을 담당합니다.
 * @see backend/src/main/java/com/rstracker/controller/RecordController.java
 */

import api from './api';
import type { Record } from '../types/record.types';

/**
 * 기록 목록 조회 파라미터
 */
export interface RecordListParams {
  /** 최소 온도 */
  minTemp?: number;
  /** 최대 온도 */
  maxTemp?: number;
  /** 활성 여부 */
  isActive?: boolean;
  /** 시작 날짜 */
  startDate?: string;
  /** 종료 날짜 */
  endDate?: string;
  /** 페이지 번호 (0부터 시작) */
  page?: number;
  /** 페이지 크기 */
  size?: number;
  /** 정렬 필드 */
  sort?: string;
}

/**
 * 페이징 정보
 */
export interface PageInfo {
  /** 현재 페이지 번호 (0부터 시작) */
  number: number;
  /** 페이지 크기 */
  size: number;
  /** 전체 페이지 수 */
  totalPages: number;
  /** 전체 항목 수 */
  totalElements: number;
  /** 첫 페이지 여부 */
  first: boolean;
  /** 마지막 페이지 여부 */
  last: boolean;
}

/**
 * 페이지네이션된 응답
 */
export interface PageResponse<T> {
  /** 데이터 목록 */
  content: T[];
  /** 페이징 정보 */
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  /** 전체 페이지 수 */
  totalPages: number;
  /** 전체 항목 수 */
  totalElements: number;
  /** 첫 페이지 여부 */
  first: boolean;
  /** 마지막 페이지 여부 */
  last: boolean;
  /** 현재 페이지 번호 */
  number: number;
  /** 현재 페이지 크기 */
  size: number;
  /** 정렬 정보 */
  sort: {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  };
}

/**
 * 기록 서비스
 */
export const recordService = {
  /**
   * 기록 목록 조회 (페이징, 필터링, 정렬)
   * GET /api/records?minTemp=0&maxTemp=100&isActive=true&page=0&size=20&sort=createdAt,desc
   */
  getList: async (params?: RecordListParams): Promise<PageResponse<Record>> => {
    const queryParams = new URLSearchParams();
    if (params?.minTemp !== undefined) queryParams.append('minTemp', params.minTemp.toString());
    if (params?.maxTemp !== undefined) queryParams.append('maxTemp', params.maxTemp.toString());
    if (params?.isActive !== undefined) queryParams.append('isActive', params.isActive.toString());
    if (params?.startDate) queryParams.append('startDate', params.startDate);
    if (params?.endDate) queryParams.append('endDate', params.endDate);
    if (params?.page !== undefined) queryParams.append('page', params.page.toString());
    if (params?.size !== undefined) queryParams.append('size', params.size.toString());
    if (params?.sort) queryParams.append('sort', params.sort);

    const url = `/records${queryParams.toString() ? '?' + queryParams.toString() : ''}`;
    const response = await api.get<PageResponse<Record>>(url);
    return response.data;
  },

  /**
   * 기록 생성
   * POST /api/records/create/{matchingId}
   */
  create: async (matchingId: number): Promise<Record> => {
    const response = await api.post<Record>(`/records/create/${matchingId}`);
    return response.data;
  },

  /**
   * 기록 조회 (Record ID)
   * GET /api/records/{recordId}
   */
  getByRecordId: async (recordId: string): Promise<Record> => {
    const response = await api.get<Record>(`/records/${recordId}`);
    return response.data;
  },

  /**
   * 기록 조회 (Matching ID)
   * GET /api/records/matching/{matchingId}
   */
  getByMatchingId: async (matchingId: number): Promise<Record> => {
    const response = await api.get<Record>(`/records/matching/${matchingId}`);
    return response.data;
  },

  /**
   * 기록 비활성화
   * PUT /api/records/{recordId}/deactivate
   */
  deactivate: async (recordId: string): Promise<void> => {
    await api.put(`/records/${recordId}/deactivate`);
  },
};

