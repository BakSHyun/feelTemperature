/**
 * 회원 상세 정보 모달 컴포넌트
 * 
 * 회원의 매칭 내역, 답변 내역, 기록 정보를 표시합니다.
 */

import { useState, useEffect } from 'react';
import type { User, UserHistory, MatchingHistory } from '../../types/user.types';
import { userService } from '../../services/userService';
import { Button } from '../common/Button';

/**
 * 회원 상세 모달 Props
 */
interface UserDetailModalProps {
  /** 회원 정보 */
  user: User;
  /** 모달 닫기 콜백 */
  onClose: () => void;
}

/**
 * 회원 상세 정보 모달 컴포넌트
 */
export function UserDetailModal({ user, onClose }: UserDetailModalProps) {
  const [history, setHistory] = useState<UserHistory | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [selectedMatching, setSelectedMatching] = useState<MatchingHistory | null>(null);

  /**
   * 회원 히스토리 조회
   */
  useEffect(() => {
    fetchHistory();
  }, [user.id]);

  async function fetchHistory() {
    setLoading(true);
    setError(null);
    try {
      const data = await userService.getHistory(user.id);
      setHistory(data);
    } catch (err) {
      const message = err instanceof Error ? err.message : '회원 히스토리를 불러오는데 실패했습니다.';
      setError(message);
      console.error('Failed to fetch user history:', err);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="h-[90vh] w-full max-w-6xl overflow-hidden rounded-xl bg-gray-800 ring-1 ring-white/10">
        <div className="flex h-full flex-col">
          {/* 헤더 */}
          <div className="flex items-center justify-between border-b border-white/10 p-6">
            <div>
              <h2 className="text-2xl font-semibold text-white">회원 상세 정보</h2>
              <div className="mt-2 text-sm text-gray-400">
                아이디: {user.userid} | 이름: {user.name} | 전화번호: {user.phoneNumber}
              </div>
            </div>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-white"
              disabled={loading}
            >
              ✕
            </button>
          </div>

          {/* 내용 */}
          <div className="flex-1 overflow-y-auto p-6">
            {loading ? (
              <div className="flex items-center justify-center py-12">
                <div className="text-gray-400">로딩 중...</div>
              </div>
            ) : error ? (
              <div className="rounded-lg bg-red-500/10 p-4 text-red-400 ring-1 ring-red-500/20">
                {error}
              </div>
            ) : history ? (
              <div className="space-y-6">
                {/* 기본 정보 */}
                <div className="rounded-xl bg-white/5 p-6 ring-1 ring-white/10">
                  <h3 className="mb-4 text-lg font-semibold text-white">기본 정보</h3>
                  <div className="grid grid-cols-2 gap-4 text-sm">
                    <div>
                      <span className="text-gray-400">이메일: </span>
                      <span className="text-white">{history.user.email || '-'}</span>
                    </div>
                    <div>
                      <span className="text-gray-400">생년월일: </span>
                      <span className="text-white">{history.user.birthDate.split('T')[0]}</span>
                    </div>
                    <div>
                      <span className="text-gray-400">성별: </span>
                      <span className="text-white">{history.user.gender}</span>
                    </div>
                    <div>
                      <span className="text-gray-400">상태: </span>
                      <span className="text-white">{history.user.status}</span>
                    </div>
                    <div>
                      <span className="text-gray-400">인증 상태: </span>
                      <span className="text-white">{history.user.verificationStatus}</span>
                    </div>
                    <div>
                      <span className="text-gray-400">총 참여 횟수: </span>
                      <span className="text-white">{history.totalParticipations}회</span>
                    </div>
                  </div>
                </div>

                {/* 매칭 내역 */}
                <div className="rounded-xl bg-white/5 p-6 ring-1 ring-white/10">
                  <h3 className="mb-4 text-lg font-semibold text-white">
                    매칭 내역 ({history.matchings.length})
                  </h3>
                  {history.matchings.length === 0 ? (
                    <p className="text-center text-gray-400">참여한 매칭이 없습니다.</p>
                  ) : (
                    <div className="space-y-4">
                      {history.matchings.map((matching) => (
                        <div
                          key={matching.matchingId}
                          className="rounded-lg bg-white/5 p-4 ring-1 ring-white/10"
                        >
                          <div className="flex items-start justify-between">
                            <div className="flex-1">
                              <div className="mb-2 flex items-center gap-3">
                                <span className="font-semibold text-white">
                                  매칭 코드: {matching.matchingCode}
                                </span>
                                <span
                                  className={`inline-flex rounded-full px-2 py-1 text-xs font-semibold ${
                                    matching.status === 'COMPLETED'
                                      ? 'bg-green-500/20 text-green-400'
                                      : matching.status === 'ESTABLISHED'
                                        ? 'bg-blue-500/20 text-blue-400'
                                        : 'bg-gray-500/20 text-gray-400'
                                  }`}
                                >
                                  {matching.status}
                                </span>
                                {matching.record && matching.record.temperature !== undefined && (
                                  <span className="text-sm text-gray-300">
                                    온도: {matching.record.temperature.toFixed(1)}°C
                                    {matching.record.temperatureDiff !== undefined && (
                                      <span className="ml-2">
                                        (차이: {matching.record.temperatureDiff.toFixed(1)}°C)
                                      </span>
                                    )}
                                  </span>
                                )}
                              </div>
                              <div className="text-sm text-gray-400">
                                참여일: {new Date(matching.joinedAt).toLocaleString('ko-KR')}
                                {matching.completedAt && (
                                  <span className="ml-4">
                                    완료일: {new Date(matching.completedAt).toLocaleString('ko-KR')}
                                  </span>
                                )}
                              </div>
                              {matching.otherParticipants.length > 0 && (
                                <div className="mt-2 text-sm text-gray-400">
                                  매칭 대상: {matching.otherParticipants.length}명
                                </div>
                              )}
                            </div>
                            <Button
                              variant="secondary"
                              size="sm"
                              onClick={() =>
                                setSelectedMatching(
                                  selectedMatching?.matchingId === matching.matchingId
                                    ? null
                                    : matching
                                )
                              }
                            >
                              {selectedMatching?.matchingId === matching.matchingId
                                ? '접기'
                                : '상세'}
                            </Button>
                          </div>

                          {/* 매칭 상세 정보 */}
                          {selectedMatching?.matchingId === matching.matchingId && (
                            <div className="mt-4 space-y-4 border-t border-white/10 pt-4">
                              {/* 매칭 대상 정보 */}
                              {matching.otherParticipants.length > 0 && (
                                <div>
                                  <h4 className="mb-2 text-sm font-semibold text-gray-300">
                                    매칭 대상
                                  </h4>
                                  <div className="space-y-2">
                                    {matching.otherParticipants.map((participant, idx) => (
                                      <div
                                        key={idx}
                                        className="rounded bg-white/5 p-2 text-sm text-gray-300"
                                      >
                                        코드: {participant.participantCode} | 참여일:{' '}
                                        {new Date(participant.joinedAt).toLocaleString('ko-KR')}
                                      </div>
                                    ))}
                                  </div>
                                </div>
                              )}

                              {/* 답변 내역 */}
                              {matching.answers.length > 0 && (
                                <div>
                                  <h4 className="mb-2 text-sm font-semibold text-gray-300">
                                    답변 내역
                                  </h4>
                                  <div className="space-y-2">
                                    {matching.answers.map((answer) => (
                                      <div
                                        key={answer.questionId}
                                        className="rounded bg-white/5 p-3 text-sm"
                                      >
                                        <div className="font-semibold text-white">
                                          Q{answer.questionOrder}. {answer.questionText}
                                        </div>
                                        <div className="mt-1 text-gray-300">
                                          답변: {answer.choiceText} (값: {answer.choiceValue})
                                        </div>
                                        <div className="mt-1 text-xs text-gray-400">
                                          {new Date(answer.answeredAt).toLocaleString('ko-KR')}
                                        </div>
                                      </div>
                                    ))}
                                  </div>
                                </div>
                              )}

                              {/* 기록 정보 */}
                              {matching.record && (
                                <div>
                                  <h4 className="mb-2 text-sm font-semibold text-gray-300">
                                    기록 정보
                                  </h4>
                                  <div className="rounded bg-white/5 p-3 text-sm text-gray-300">
                                    <div>기록 ID: {matching.record.recordId}</div>
                                    {matching.record.temperature !== undefined && matching.record.temperature !== null && (
                                      <div>온도: {matching.record.temperature.toFixed(1)}°C</div>
                                    )}
                                    {matching.record.temperatureDiff !== undefined && matching.record.temperatureDiff !== null && (
                                      <div>
                                        온도 차이: {matching.record.temperatureDiff.toFixed(1)}°C
                                      </div>
                                    )}
                                    <div>
                                      생성일:{' '}
                                      {new Date(matching.record.createdAt).toLocaleString('ko-KR')}
                                    </div>
                                  </div>
                                </div>
                              )}
                            </div>
                          )}
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            ) : null}
          </div>

          {/* 푸터 */}
          <div className="border-t border-white/10 p-6">
            <div className="flex justify-end">
              <Button variant="secondary" onClick={onClose}>
                닫기
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

