/**
 * 기록 조회 페이지
 * 
 * 기록 목록 조회, 필터링, 검색, 상세 조회 기능을 제공합니다.
 */

import { useState, useEffect, useCallback } from 'react';
import { recordService, type RecordListParams, type PageResponse } from '../services/recordService';
import type { Record } from '../types/record.types';
import { Button } from '../components/common/Button';

/**
 * 기록 조회 페이지 컴포넌트
 */
export function RecordsPage() {
  const [records, setRecords] = useState<Record[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [selectedRecord, setSelectedRecord] = useState<Record | null>(null);
  
  // 필터 상태
  const [filters, setFilters] = useState<RecordListParams>({
    page: 0,
    size: 20,
    sort: 'createdAt,desc',
  });
  
  // 페이징 정보
  const [pageInfo, setPageInfo] = useState<{
    totalPages: number;
    totalElements: number;
    number: number;
    size: number;
    first: boolean;
    last: boolean;
  }>({
    totalPages: 0,
    totalElements: 0,
    number: 0,
    size: 20,
    first: true,
    last: true,
  });

  /**
   * 기록 목록을 API에서 가져옵니다.
   */
  const fetchRecords = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response: PageResponse<Record> = await recordService.getList(filters);
      setRecords(response.content);
      setPageInfo({
        totalPages: response.totalPages,
        totalElements: response.totalElements,
        number: response.number,
        size: response.size,
        first: response.first,
        last: response.last,
      });
    } catch (err) {
      const message = err instanceof Error ? err.message : '기록 목록을 불러오는데 실패했습니다.';
      setError(message);
      console.error('Failed to fetch records:', err);
    } finally {
      setLoading(false);
    }
  }, [filters]);

  /**
   * 기록 목록 조회
   */
  useEffect(() => {
    fetchRecords();
  }, [fetchRecords]);

  /**
   * 필터 변경 핸들러
   */
  function handleFilterChange(key: keyof RecordListParams, value: unknown) {
    setFilters((prev) => ({
      ...prev,
      [key]: value,
      page: 0, // 필터 변경 시 첫 페이지로 이동
    }));
  }

  /**
   * 페이지 변경 핸들러
   */
  function handlePageChange(page: number) {
    setFilters((prev) => ({
      ...prev,
      page,
    }));
  }

  /**
   * 기록 상세 정보를 조회합니다.
   */
  async function handleViewDetail(recordId: string) {
    try {
      const record = await recordService.getByRecordId(recordId);
      setSelectedRecord(record);
    } catch (err) {
      const message = err instanceof Error ? err.message : '기록 상세 정보를 불러오는데 실패했습니다.';
      setError(message);
      console.error('Failed to fetch record detail:', err);
    }
  }

  /**
   * 기록 비활성화
   */
  async function handleDeactivate(recordId: string) {
    if (!confirm('정말 이 기록을 비활성화하시겠습니까?')) {
      return;
    }
    try {
      await recordService.deactivate(recordId);
      await fetchRecords(); // 목록 새로고침
      if (selectedRecord?.recordId === recordId) {
        setSelectedRecord(null);
      }
    } catch (err) {
      const message = err instanceof Error ? err.message : '기록 비활성화에 실패했습니다.';
      setError(message);
      console.error('Failed to deactivate record:', err);
    }
  }

  return (
    <div>
      {/* 페이지 헤더 */}
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-4xl font-semibold tracking-tight text-pretty text-white sm:text-5xl">
            기록 조회
          </h1>
          <p className="mt-4 text-lg text-gray-300">
            생성된 기록을 조회하고 관리할 수 있습니다.
          </p>
        </div>
      </div>

      {/* 필터 영역 */}
      <div className="mb-6 rounded-xl bg-white/5 p-4 ring-1 ring-white/10">
        <div className="grid grid-cols-1 gap-4 md:grid-cols-3 lg:grid-cols-5">
          <div>
            <label className="mb-2 block text-sm text-gray-300">최소 온도</label>
            <input
              type="number"
              step="0.1"
              value={filters.minTemp ?? ''}
              onChange={(e) =>
                handleFilterChange('minTemp', e.target.value ? parseFloat(e.target.value) : undefined)
              }
              className="w-full rounded-md bg-white/5 px-4 py-2 text-white ring-1 ring-inset ring-white/10 focus:ring-2 focus:ring-primary"
              placeholder="0"
            />
          </div>
          <div>
            <label className="mb-2 block text-sm text-gray-300">최대 온도</label>
            <input
              type="number"
              step="0.1"
              value={filters.maxTemp ?? ''}
              onChange={(e) =>
                handleFilterChange('maxTemp', e.target.value ? parseFloat(e.target.value) : undefined)
              }
              className="w-full rounded-md bg-white/5 px-4 py-2 text-white ring-1 ring-inset ring-white/10 focus:ring-2 focus:ring-primary"
              placeholder="100"
            />
          </div>
          <div>
            <label className="mb-2 block text-sm text-gray-300">활성 여부</label>
            <select
              value={filters.isActive === undefined ? '' : filters.isActive.toString()}
              onChange={(e) =>
                handleFilterChange('isActive', e.target.value === '' ? undefined : e.target.value === 'true')
              }
              className="w-full rounded-md bg-white/5 px-4 py-2 text-white ring-1 ring-inset ring-white/10 focus:ring-2 focus:ring-primary"
            >
              <option value="">전체</option>
              <option value="true">활성</option>
              <option value="false">비활성</option>
            </select>
          </div>
          <div>
            <label className="mb-2 block text-sm text-gray-300">시작 날짜</label>
            <input
              type="datetime-local"
              value={filters.startDate ?? ''}
              onChange={(e) => handleFilterChange('startDate', e.target.value || undefined)}
              className="w-full rounded-md bg-white/5 px-4 py-2 text-white ring-1 ring-inset ring-white/10 focus:ring-2 focus:ring-primary"
            />
          </div>
          <div>
            <label className="mb-2 block text-sm text-gray-300">종료 날짜</label>
            <input
              type="datetime-local"
              value={filters.endDate ?? ''}
              onChange={(e) => handleFilterChange('endDate', e.target.value || undefined)}
              className="w-full rounded-md bg-white/5 px-4 py-2 text-white ring-1 ring-inset ring-white/10 focus:ring-2 focus:ring-primary"
            />
          </div>
        </div>
        <div className="mt-4 flex justify-end">
          <Button
            variant="secondary"
            size="sm"
            onClick={() => {
              setFilters({
                page: 0,
                size: 20,
                sort: 'createdAt,desc',
              });
            }}
          >
            필터 초기화
          </Button>
        </div>
      </div>

      {/* 에러 메시지 */}
      {error && (
        <div className="mb-6 rounded-lg bg-red-500/10 p-4 text-red-400 ring-1 ring-red-500/20">
          {error}
        </div>
      )}

      {/* 기록 목록 */}
      {loading ? (
        <div className="flex items-center justify-center py-12">
          <div className="text-gray-400">로딩 중...</div>
        </div>
      ) : records.length === 0 ? (
        <div className="overflow-hidden rounded-xl bg-white/5 ring-1 ring-white/10">
          <div className="p-6">
            <p className="text-center text-gray-400">등록된 기록이 없습니다.</p>
          </div>
        </div>
      ) : (
        <>
          <div className="space-y-4">
            {records.map((record) => (
              <div
                key={record.id}
                className="overflow-hidden rounded-xl bg-white/5 ring-1 ring-white/10"
              >
                <div className="p-6">
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-3">
                        <span className="text-lg font-semibold text-white">
                          기록 ID: {record.recordId}
                        </span>
                        <span
                          className={`inline-flex rounded-full px-2 py-1 text-xs font-semibold ${
                            record.isActive
                              ? 'bg-green-500/20 text-green-400'
                              : 'bg-gray-500/20 text-gray-400'
                          }`}
                        >
                          {record.isActive ? '활성' : '비활성'}
                        </span>
                      </div>
                      <div className="mt-3 grid grid-cols-2 gap-4 text-sm text-gray-300 md:grid-cols-4">
                        <div>
                          <span className="text-gray-400">매칭 ID:</span>{' '}
                          <span className="text-white">{record.matchingId}</span>
                        </div>
                        <div>
                          <span className="text-gray-400">온도:</span>{' '}
                          <span className="text-white">
                            {record.temperature?.toFixed(1) ?? 'N/A'}°C
                          </span>
                        </div>
                        <div>
                          <span className="text-gray-400">온도 차이:</span>{' '}
                          <span className="text-white">
                            {record.temperatureDiff?.toFixed(1) ?? 'N/A'}°C
                          </span>
                        </div>
                        <div>
                          <span className="text-gray-400">생성일:</span>{' '}
                          <span className="text-white">
                            {new Date(record.createdAt).toLocaleString('ko-KR')}
                          </span>
                        </div>
                      </div>
                    </div>
                    <div className="ml-4 flex gap-2">
                      <Button variant="secondary" size="sm" onClick={() => handleViewDetail(record.recordId)}>
                        상세
                      </Button>
                      {record.isActive && (
                        <Button
                          variant="danger"
                          size="sm"
                          onClick={() => handleDeactivate(record.recordId)}
                        >
                          비활성화
                        </Button>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* 페이징 */}
          {pageInfo.totalPages > 1 && (
            <div className="mt-6 flex items-center justify-center gap-2">
              <Button
                variant="secondary"
                size="sm"
                onClick={() => handlePageChange(pageInfo.number - 1)}
                disabled={pageInfo.first}
              >
                이전
              </Button>
              <span className="px-4 text-gray-300">
                {pageInfo.number + 1} / {pageInfo.totalPages} (총 {pageInfo.totalElements}개)
              </span>
              <Button
                variant="secondary"
                size="sm"
                onClick={() => handlePageChange(pageInfo.number + 1)}
                disabled={pageInfo.last}
              >
                다음
              </Button>
            </div>
          )}
        </>
      )}

      {/* 기록 상세 모달 */}
      {selectedRecord && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="w-full max-w-4xl overflow-hidden rounded-xl bg-gray-800 ring-1 ring-white/10">
            <div className="p-6">
              <div className="mb-4 flex items-center justify-between">
                <h2 className="text-2xl font-semibold text-white">기록 상세</h2>
                <button
                  onClick={() => setSelectedRecord(null)}
                  className="text-gray-400 hover:text-white"
                >
                  ✕
                </button>
              </div>
              <div className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="text-sm text-gray-400">기록 ID</label>
                    <p className="mt-1 text-white">{selectedRecord.recordId}</p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-400">매칭 ID</label>
                    <p className="mt-1 text-white">{selectedRecord.matchingId}</p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-400">온도</label>
                    <p className="mt-1 text-white">
                      {selectedRecord.temperature?.toFixed(1) ?? 'N/A'}°C
                    </p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-400">온도 차이</label>
                    <p className="mt-1 text-white">
                      {selectedRecord.temperatureDiff?.toFixed(1) ?? 'N/A'}°C
                    </p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-400">상태</label>
                    <p className="mt-1 text-white">{selectedRecord.isActive ? '활성' : '비활성'}</p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-400">생성일</label>
                    <p className="mt-1 text-white">
                      {new Date(selectedRecord.createdAt).toLocaleString('ko-KR')}
                    </p>
                  </div>
                </div>
                {selectedRecord.summary && (
                  <div>
                    <label className="text-sm text-gray-400">요약 정보</label>
                    <div className="mt-2 rounded-lg bg-white/5 p-4">
                      <pre className="text-sm text-gray-300">
                        {JSON.stringify(selectedRecord.summary, null, 2)}
                      </pre>
                    </div>
                  </div>
                )}
              </div>
              <div className="mt-6 flex justify-end">
                <Button variant="secondary" onClick={() => setSelectedRecord(null)}>
                  닫기
                </Button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
