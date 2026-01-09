/**
 * 질문 관리 페이지
 * 
 * 질문 목록 조회, 상세 조회, 생성, 수정, 삭제 기능을 제공합니다.
 * 카테고리별 필터링 기능을 포함합니다.
 */

import { useState, useEffect, useCallback } from 'react';
import { questionService } from '../services/questionService';
import type { Question, QuestionCategory, CreateQuestionDto, UpdateQuestionDto } from '../types/question.types';
import { Button } from '../components/common/Button';

/**
 * 질문 관리 페이지 컴포넌트
 */
export function QuestionsPage() {
  const [questions, setQuestions] = useState<Question[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [selectedQuestion, setSelectedQuestion] = useState<Question | null>(null);
  const [selectedCategory, setSelectedCategory] = useState<QuestionCategory | ''>('');
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [editingQuestion, setEditingQuestion] = useState<Question | null>(null);

  /**
   * 질문 목록을 API에서 가져옵니다.
   * 에러 발생 시 사용자에게 알림을 표시합니다.
   */
  const fetchQuestions = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const category = selectedCategory || undefined;
      const data = await questionService.getAll(category);
      // order 기준으로 정렬
      const sortedData = [...data].sort((a, b) => a.order - b.order);
      setQuestions(sortedData);
    } catch (err) {
      const message = err instanceof Error ? err.message : '질문 목록을 불러오는데 실패했습니다.';
      setError(message);
      console.error('Failed to fetch questions:', err);
    } finally {
      setLoading(false);
    }
  }, [selectedCategory]);

  /**
   * 질문 목록 조회
   */
  useEffect(() => {
    fetchQuestions();
  }, [fetchQuestions]);

  /**
   * 질문 상세 정보를 조회합니다.
   */
  async function handleViewDetail(id: number) {
    try {
      const question = await questionService.getById(id);
      setSelectedQuestion(question);
    } catch (err) {
      const message = err instanceof Error ? err.message : '질문 상세 정보를 불러오는데 실패했습니다.';
      setError(message);
      console.error('Failed to fetch question detail:', err);
    }
  }

  /**
   * 질문 수정 모달 열기
   */
  async function handleEditClick(id: number) {
    try {
      const question = await questionService.getById(id);
      setEditingQuestion(question);
      setShowEditModal(true);
    } catch (err) {
      const message = err instanceof Error ? err.message : '질문 정보를 불러오는데 실패했습니다.';
      setError(message);
      console.error('Failed to fetch question for editing:', err);
    }
  }

  /**
   * 질문 삭제
   */
  async function handleDelete(id: number) {
    if (!confirm('정말 이 질문을 삭제하시겠습니까? (소프트 삭제: 비활성화됩니다)')) {
      return;
    }
    try {
      await questionService.delete(id);
      await fetchQuestions(); // 목록 새로고침
      if (selectedQuestion?.id === id) {
        setSelectedQuestion(null);
      }
    } catch (err) {
      const message = err instanceof Error ? err.message : '질문 삭제에 실패했습니다.';
      setError(message);
      console.error('Failed to delete question:', err);
    }
  }

  return (
    <div>
      {/* 페이지 헤더 */}
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-4xl font-semibold tracking-tight text-pretty text-white sm:text-5xl">
            질문 관리
          </h1>
          <p className="mt-4 text-lg text-gray-300">
            질문과 선택지를 관리할 수 있습니다.
          </p>
        </div>
        <Button onClick={() => setShowCreateModal(true)}>질문 생성</Button>
      </div>

      {/* 카테고리 필터 */}
      <div className="mb-6">
        <div className="flex gap-2">
          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value as QuestionCategory | '')}
            className="rounded-md bg-white/5 px-4 py-2 text-white ring-1 ring-inset ring-white/10 focus:ring-2 focus:ring-primary"
          >
            <option value="">전체 카테고리</option>
            <option value="INITIAL_MATCHING">초기 매칭 질문</option>
            <option value="TEMPERATURE_REFINE">온도 맞춰보기 질문</option>
          </select>
        </div>
      </div>

      {/* 에러 메시지 */}
      {error && (
        <div className="mb-6 rounded-lg bg-red-500/10 p-4 text-red-400 ring-1 ring-red-500/20">
          {error}
        </div>
      )}

      {/* 질문 목록 */}
      {loading ? (
        <div className="flex items-center justify-center py-12">
          <div className="text-gray-400">로딩 중...</div>
        </div>
      ) : questions.length === 0 ? (
        <div className="overflow-hidden rounded-xl bg-white/5 ring-1 ring-white/10">
          <div className="p-6">
            <p className="text-center text-gray-400">등록된 질문이 없습니다.</p>
          </div>
        </div>
      ) : (
        <div className="space-y-4">
          {questions.map((question) => (
            <div
              key={question.id}
              className="overflow-hidden rounded-xl bg-white/5 ring-1 ring-white/10"
            >
              <div className="p-6">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-3">
                      <span className="inline-flex items-center rounded-full bg-primary/20 px-3 py-1 text-sm font-semibold text-primary">
                        Q{question.order}
                      </span>
                      <span
                        className={`inline-flex rounded-full px-2 py-1 text-xs font-semibold ${
                          question.isActive
                            ? 'bg-green-500/20 text-green-400'
                            : 'bg-gray-500/20 text-gray-400'
                        }`}
                      >
                        {question.isActive ? '활성' : '비활성'}
                      </span>
                      <span className="text-sm text-gray-400">
                        {question.questionType} |{' '}
                        {question.questionCategory === 'INITIAL_MATCHING' ? '초기 매칭' : '온도 맞춰보기'}
                      </span>
                    </div>
                    <h3 className="mt-3 text-lg font-semibold text-white">{question.questionText}</h3>
                    {question.choices && question.choices.length > 0 && (
                      <div className="mt-4">
                        <p className="mb-2 text-sm text-gray-400">선택지:</p>
                        <div className="space-y-2">
                          {[...question.choices]
                            .sort((a, b) => a.order - b.order)
                            .map((choice) => (
                              <div
                                key={`choice-${question.id}-${choice.id}`}
                                className="rounded-lg bg-white/5 p-3 text-sm text-gray-300"
                              >
                                <div className="flex items-center justify-between">
                                  <span>
                                    {choice.order}. {choice.choiceText}
                                  </span>
                                  <div className="flex items-center gap-4 text-xs text-gray-400">
                                    <span>값: {choice.choiceValue}</span>
                                    {choice.temperatureWeight > 0 && (
                                      <span>가중치: {choice.temperatureWeight}</span>
                                    )}
                                  </div>
                                </div>
                              </div>
                            ))}
                        </div>
                      </div>
                    )}
                  </div>
                  <div className="ml-4 flex gap-2">
                    <Button
                      variant="secondary"
                      size="sm"
                      onClick={() => handleViewDetail(question.id)}
                    >
                      상세
                    </Button>
                    <Button variant="secondary" size="sm" onClick={() => handleEditClick(question.id)}>
                      수정
                    </Button>
                    <Button variant="danger" size="sm" onClick={() => handleDelete(question.id)}>
                      삭제
                    </Button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* 질문 상세 모달 */}
      {selectedQuestion && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="w-full max-w-2xl overflow-hidden rounded-xl bg-gray-800 ring-1 ring-white/10">
            <div className="p-6">
              <div className="mb-4 flex items-center justify-between">
                <h2 className="text-2xl font-semibold text-white">질문 상세</h2>
                <button
                  onClick={() => setSelectedQuestion(null)}
                  className="text-gray-400 hover:text-white"
                >
                  ✕
                </button>
              </div>
              <div className="space-y-4">
                <div>
                  <label className="text-sm text-gray-400">질문 텍스트</label>
                  <p className="mt-1 text-white">{selectedQuestion.questionText}</p>
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="text-sm text-gray-400">순서</label>
                    <p className="mt-1 text-white">{selectedQuestion.order}</p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-400">타입</label>
                    <p className="mt-1 text-white">{selectedQuestion.questionType}</p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-400">카테고리</label>
                    <p className="mt-1 text-white">
                      {selectedQuestion.questionCategory === 'INITIAL_MATCHING' ? '초기 매칭' : '온도 맞춰보기'}
                    </p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-400">상태</label>
                    <p className="mt-1 text-white">{selectedQuestion.isActive ? '활성' : '비활성'}</p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-400">버전</label>
                    <p className="mt-1 text-white">{selectedQuestion.version}</p>
                  </div>
                </div>
                {selectedQuestion.choices && selectedQuestion.choices.length > 0 && (
                  <div>
                    <label className="text-sm text-gray-400">선택지</label>
                    <div className="mt-2 space-y-2">
                      {[...selectedQuestion.choices]
                        .sort((a, b) => a.order - b.order)
                        .map((choice) => (
                          <div
                            key={`modal-choice-${choice.id}`}
                            className="rounded-lg bg-white/5 p-4 text-sm"
                          >
                            <div className="flex items-center justify-between">
                              <div>
                                <p className="font-semibold text-white">
                                  {choice.order}. {choice.choiceText}
                                </p>
                                <div className="mt-1 flex gap-4 text-gray-400">
                                  <span>값: {choice.choiceValue}</span>
                                  {choice.temperatureWeight > 0 && (
                                    <span>가중치: {choice.temperatureWeight}</span>
                                  )}
                                </div>
                              </div>
                            </div>
                          </div>
                        ))}
                    </div>
                  </div>
                )}
                <div className="grid grid-cols-2 gap-4 text-sm text-gray-400">
                  <div>
                    <span>생성일: </span>
                    <span>{new Date(selectedQuestion.createdAt).toLocaleString('ko-KR')}</span>
                  </div>
                  <div>
                    <span>수정일: </span>
                    <span>{new Date(selectedQuestion.updatedAt).toLocaleString('ko-KR')}</span>
                  </div>
                </div>
              </div>
              <div className="mt-6 flex justify-end gap-2">
                <Button variant="secondary" onClick={() => handleEditClick(selectedQuestion.id)}>
                  수정
                </Button>
                <Button variant="secondary" onClick={() => setSelectedQuestion(null)}>
                  닫기
                </Button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 질문 생성 모달 */}
      {showCreateModal && (
        <QuestionFormModal
          onClose={() => setShowCreateModal(false)}
          onSave={async (data) => {
            await questionService.create(data as CreateQuestionDto);
            setShowCreateModal(false);
            await fetchQuestions();
          }}
        />
      )}

      {/* 질문 수정 모달 */}
      {showEditModal && editingQuestion && (
        <QuestionFormModal
          question={editingQuestion}
          onClose={() => {
            setShowEditModal(false);
            setEditingQuestion(null);
          }}
          onSave={async (data: UpdateQuestionDto) => {
            await questionService.update(editingQuestion.id, data);
            setShowEditModal(false);
            setEditingQuestion(null);
            await fetchQuestions();
          }}
        />
      )}
    </div>
  );
}

/**
 * 질문 생성/수정 폼 모달 컴포넌트
 */
interface QuestionFormModalProps {
  question?: Question | null;
  onClose: () => void;
  onSave: (data: CreateQuestionDto | UpdateQuestionDto) => Promise<void>; // 타입 단언으로 처리
}

function QuestionFormModal({ question, onClose, onSave }: QuestionFormModalProps) {
  const isEdit = !!question;
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 폼 상태
  const [questionText, setQuestionText] = useState('');
  const [questionType, setQuestionType] = useState('');
  const [questionCategory, setQuestionCategory] = useState<QuestionCategory>('INITIAL_MATCHING');
  const [order, setOrder] = useState(1);
  const [isActive, setIsActive] = useState(true);
  const [choices, setChoices] = useState<Array<{
    choiceText: string;
    choiceValue: string;
    order: number;
    temperatureWeight: number;
  }>>([{ choiceText: '', choiceValue: '', order: 1, temperatureWeight: 0 }]);

  // 수정 모드일 경우 초기값 설정
  useEffect(() => {
    if (question) {
      setQuestionText(question.questionText);
      setQuestionType(question.questionType);
      setQuestionCategory(question.questionCategory);
      setOrder(question.order);
      setIsActive(question.isActive);
      setChoices(
        question.choices
          .sort((a, b) => a.order - b.order)
          .map((c) => ({
            choiceText: c.choiceText,
            choiceValue: c.choiceValue,
            order: c.order,
            temperatureWeight: c.temperatureWeight,
          }))
      );
    }
  }, [question]);

  /**
   * 선택지 추가
   */
  function addChoice() {
    setChoices([...choices, { choiceText: '', choiceValue: '', order: choices.length + 1, temperatureWeight: 0 }]);
  }

  /**
   * 선택지 삭제
   */
  function removeChoice(index: number) {
    setChoices(choices.filter((_, i) => i !== index).map((c, i) => ({ ...c, order: i + 1 })));
  }

  /**
   * 선택지 업데이트
   */
  function updateChoice(index: number, field: string, value: unknown) {
    const newChoices = [...choices];
    newChoices[index] = { ...newChoices[index], [field]: value };
    if (field === 'order') {
      // order 변경 시 자동 정렬
      newChoices.sort((a, b) => a.order - b.order);
      newChoices.forEach((c, i) => {
        c.order = i + 1;
      });
    }
    setChoices(newChoices);
  }

  /**
   * 폼 제출
   */
  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      // 유효성 검사
      if (!questionText.trim()) {
        throw new Error('질문 텍스트를 입력해주세요.');
      }
      if (!questionType.trim()) {
        throw new Error('질문 타입을 입력해주세요.');
      }
      if (choices.length === 0 || choices.some((c) => !c.choiceText.trim() || !c.choiceValue.trim())) {
        throw new Error('최소 1개 이상의 선택지를 입력해주세요.');
      }

      if (isEdit) {
        // 수정
        const updateData: UpdateQuestionDto = {
          questionText,
          questionType,
          questionCategory,
          order,
          isActive,
          choices: choices.map((c) => ({
            choiceText: c.choiceText,
            choiceValue: c.choiceValue,
            order: c.order,
            temperatureWeight: c.temperatureWeight,
          })),
        };
        await onSave(updateData);
      } else {
        // 생성
        const createData: CreateQuestionDto = {
          questionText,
          questionType,
          questionCategory,
          order,
          choices: choices.map((c) => ({
            choiceText: c.choiceText,
            choiceValue: c.choiceValue,
            order: c.order,
            temperatureWeight: c.temperatureWeight,
          })),
        };
        await onSave(createData);
      }
    } catch (err) {
      const message = err instanceof Error ? err.message : '저장에 실패했습니다.';
      setError(message);
      console.error('Failed to save question:', err);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-4xl max-h-[90vh] overflow-y-auto rounded-xl bg-gray-800 ring-1 ring-white/10">
        <div className="p-6">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-2xl font-semibold text-white">{isEdit ? '질문 수정' : '질문 생성'}</h2>
            <button onClick={onClose} className="text-gray-400 hover:text-white">
              ✕
            </button>
          </div>

          {error && (
            <div className="mb-4 rounded-lg bg-red-500/10 p-4 text-red-400 ring-1 ring-red-500/20">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* 질문 텍스트 */}
            <div>
              <label className="mb-2 block text-sm text-gray-300">질문 텍스트 *</label>
              <textarea
                value={questionText}
                onChange={(e) => setQuestionText(e.target.value)}
                required
                className="w-full rounded-md bg-white/5 px-4 py-2 text-white ring-1 ring-inset ring-white/10 focus:ring-2 focus:ring-primary"
                rows={3}
              />
            </div>

            {/* 질문 타입 */}
            <div>
              <label className="mb-2 block text-sm text-gray-300">질문 타입 *</label>
              <input
                type="text"
                value={questionType}
                onChange={(e) => setQuestionType(e.target.value)}
                required
                placeholder="context, sentiment, expectation, distance, comfort"
                className="w-full rounded-md bg-white/5 px-4 py-2 text-white ring-1 ring-inset ring-white/10 focus:ring-2 focus:ring-primary"
              />
            </div>

            {/* 질문 카테고리 */}
            <div>
              <label className="mb-2 block text-sm text-gray-300">질문 카테고리 *</label>
              <select
                value={questionCategory}
                onChange={(e) => setQuestionCategory(e.target.value as QuestionCategory)}
                required
                className="w-full rounded-md bg-white/5 px-4 py-2 text-white ring-1 ring-inset ring-white/10 focus:ring-2 focus:ring-primary"
              >
                <option value="INITIAL_MATCHING">초기 매칭 질문</option>
                <option value="TEMPERATURE_REFINE">온도 맞춰보기 질문</option>
              </select>
            </div>

            {/* 질문 순서 */}
            <div>
              <label className="mb-2 block text-sm text-gray-300">질문 순서 *</label>
              <input
                type="number"
                value={order}
                onChange={(e) => setOrder(parseInt(e.target.value, 10))}
                required
                min={1}
                className="w-full rounded-md bg-white/5 px-4 py-2 text-white ring-1 ring-inset ring-white/10 focus:ring-2 focus:ring-primary"
              />
            </div>

            {/* 활성화 여부 (수정 모드만) */}
            {isEdit && (
              <div>
                <label className="mb-2 flex items-center gap-2 text-sm text-gray-300">
                  <input
                    type="checkbox"
                    checked={isActive}
                    onChange={(e) => setIsActive(e.target.checked)}
                    className="rounded"
                  />
                  활성화
                </label>
              </div>
            )}

            {/* 선택지 */}
            <div>
              <div className="mb-2 flex items-center justify-between">
                <label className="text-sm text-gray-300">선택지 *</label>
                <Button type="button" variant="secondary" size="sm" onClick={addChoice}>
                  선택지 추가
                </Button>
              </div>
              <div className="space-y-3">
                {choices.map((choice, index) => (
                  <div key={index} className="rounded-lg bg-white/5 p-4">
                    <div className="grid grid-cols-1 gap-3 md:grid-cols-4">
                      <div>
                        <label className="mb-1 block text-xs text-gray-400">선택지 텍스트</label>
                        <input
                          type="text"
                          value={choice.choiceText}
                          onChange={(e) => updateChoice(index, 'choiceText', e.target.value)}
                          required
                          className="w-full rounded-md bg-white/5 px-3 py-2 text-sm text-white ring-1 ring-inset ring-white/10"
                        />
                      </div>
                      <div>
                        <label className="mb-1 block text-xs text-gray-400">선택지 값</label>
                        <input
                          type="text"
                          value={choice.choiceValue}
                          onChange={(e) => updateChoice(index, 'choiceValue', e.target.value)}
                          required
                          className="w-full rounded-md bg-white/5 px-3 py-2 text-sm text-white ring-1 ring-inset ring-white/10"
                        />
                      </div>
                      <div>
                        <label className="mb-1 block text-xs text-gray-400">가중치</label>
                        <input
                          type="number"
                          step="0.1"
                          value={choice.temperatureWeight}
                          onChange={(e) =>
                            updateChoice(index, 'temperatureWeight', parseFloat(e.target.value) || 0)
                          }
                          className="w-full rounded-md bg-white/5 px-3 py-2 text-sm text-white ring-1 ring-inset ring-white/10"
                        />
                      </div>
                      <div className="flex items-end">
                        <Button
                          type="button"
                          variant="danger"
                          size="sm"
                          onClick={() => removeChoice(index)}
                          disabled={choices.length === 1}
                        >
                          삭제
                        </Button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* 버튼 */}
            <div className="mt-6 flex justify-end gap-2">
              <Button type="button" variant="secondary" onClick={onClose} disabled={loading}>
                취소
              </Button>
              <Button type="submit" disabled={loading}>
                {loading ? '저장 중...' : isEdit ? '수정' : '생성'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
