/**
 * 회원 관리 API 서비스
 * 
 * 회원 관련 API 호출을 담당합니다.
 * @see backend/src/main/java/com/rstracker/controller/UserController.java
 */

import api from './api';
import type { User, CreateUserDto, UpdateUserDto } from '../types/user.types';

/**
 * 회원 서비스
 */
export const userService = {
  /**
   * 회원 생성
   * POST /api/users
   */
  create: async (data: CreateUserDto): Promise<User> => {
    const response = await api.post<User>('/users', data);
    return response.data;
  },

  /**
   * 회원 조회 (ID)
   * GET /api/users/{id}
   */
  getById: async (id: number): Promise<User> => {
    const response = await api.get<User>(`/users/${id}`);
    return response.data;
  },

  /**
   * 회원 조회 (아이디)
   * GET /api/users/userid/{userid}
   */
  getByUserid: async (userid: string): Promise<User> => {
    const response = await api.get<User>(`/users/userid/${userid}`);
    return response.data;
  },

  /**
   * 회원 목록 조회 (페이징)
   * GET /api/users?page=0&size=20&sort=createdAt,desc
   */
  getUsers: async (page: number = 0, size: number = 20, sort: string = 'createdAt,desc'): Promise<{
    content: User[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
  }> => {
    const response = await api.get<{
      content: User[];
      totalElements: number;
      totalPages: number;
      number: number;
      size: number;
    }>(`/users?page=${page}&size=${size}&sort=${sort}`);
    return response.data;
  },

  /**
   * 회원 목록 조회 (전체)
   * GET /api/users/all
   */
  getAll: async (): Promise<User[]> => {
    const response = await api.get<User[]>('/users/all');
    return response.data;
  },

  /**
   * 회원 수정
   * PUT /api/users/{id}
   */
  update: async (id: number, data: UpdateUserDto): Promise<User> => {
    const response = await api.put<User>(`/users/${id}`, data);
    return response.data;
  },

  /**
   * 회원 삭제 (소프트 삭제)
   * DELETE /api/users/{id}
   */
  delete: async (id: number): Promise<void> => {
    await api.delete(`/users/${id}`);
  },

  /**
   * 아이디 중복 확인
   * GET /api/users/check/userid/{userid}
   */
  checkUserid: async (userid: string): Promise<boolean> => {
    const response = await api.get<boolean>(`/users/check/userid/${userid}`);
    return response.data;
  },

  /**
   * 전화번호 중복 확인
   * GET /api/users/check/phone/{phoneNumber}
   */
  checkPhoneNumber: async (phoneNumber: string): Promise<boolean> => {
    const response = await api.get<boolean>(`/users/check/phone/${phoneNumber}`);
    return response.data;
  },

  /**
   * 이메일 중복 확인
   * GET /api/users/check/email/{email}
   */
  checkEmail: async (email: string): Promise<boolean> => {
    const response = await api.get<boolean>(`/users/check/email/${email}`);
    return response.data;
  },

  /**
   * 회원 히스토리 조회
   * GET /api/users/{id}/history
   */
  getHistory: async (id: number) => {
    const response = await api.get(`/users/${id}/history`);
    return response.data;
  },
};
