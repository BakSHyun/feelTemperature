/**
 * Axios 인스턴스 설정
 * 
 * API 요청/응답 인터셉터 및 기본 설정을 관리합니다.
 */

import axios, { type AxiosError, type InternalAxiosRequestConfig, type AxiosResponse } from 'axios';
import type { ApiError } from '../types/api.types';

/**
 * API 기본 URL
 * 환경 변수 또는 기본값 사용
 */
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

/**
 * Axios 인스턴스 생성
 */
export const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * 요청 인터셉터
 * 인증 토큰 추가, 요청 로깅 등
 */
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 로컬 스토리지에서 토큰 가져오기
    const token = localStorage.getItem('token');
    
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

/**
 * 응답 인터셉터
 * 에러 처리, 토큰 만료 처리 등
 */
api.interceptors.response.use(
  (response: AxiosResponse) => {
    // 성공 응답은 그대로 반환
    return response;
  },
  (error: AxiosError<ApiError>) => {
    // 401 Unauthorized: 인증 실패
    if (error.response?.status === 401) {
      // 토큰 제거 및 로그인 페이지로 리다이렉트
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    
    // 403 Forbidden: 권한 없음
    if (error.response?.status === 403) {
      // 권한 오류 처리
      console.error('권한이 없습니다.');
    }
    
    // 404 Not Found: 리소스 없음
    if (error.response?.status === 404) {
      // 리소스 없음 처리
      console.error('리소스를 찾을 수 없습니다.');
    }
    
    // 500 Internal Server Error: 서버 오류
    if (error.response?.status === 500) {
      // 서버 오류 처리
      console.error('서버 오류가 발생했습니다.');
    }
    
    return Promise.reject(error);
  }
);

export default api;

