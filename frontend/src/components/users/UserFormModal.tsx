/**
 * 회원 생성/수정 폼 모달 컴포넌트
 * 
 * 회원 생성 및 수정을 위한 폼 모달입니다.
 */

import { useState, useEffect } from 'react';
import type { User, CreateUserDto, UpdateUserDto, Gender, UserStatus } from '../../types/user.types';
import { Button } from '../common/Button';
import { Input } from '../common/Input';

/**
 * 회원 폼 모달 Props
 */
interface UserFormModalProps {
  /** 수정 모드일 경우 회원 정보 */
  user?: User | null;
  /** 모달 닫기 콜백 */
  onClose: () => void;
  /** 저장 완료 콜백 */
  onSave: (data: CreateUserDto | UpdateUserDto) => Promise<void>;
}

/**
 * 회원 생성/수정 폼 모달 컴포넌트
 */
export function UserFormModal({ user, onClose, onSave }: UserFormModalProps) {
  const isEdit = !!user;
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 폼 상태
  const [userid, setUserid] = useState('');
  const [email, setEmail] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [name, setName] = useState('');
  const [birthDate, setBirthDate] = useState('');
  const [gender, setGender] = useState<Gender>('MALE');
  const [status, setStatus] = useState<UserStatus>('ACTIVE');

  // 수정 모드일 경우 초기값 설정
  useEffect(() => {
    if (user) {
      setEmail(user.email || '');
      setPhoneNumber(user.phoneNumber);
      setName(user.name);
      setBirthDate(user.birthDate.split('T')[0]); // 날짜 부분만
      setGender(user.gender);
      setStatus(user.status);
    }
  }, [user]);

  /**
   * 폼 유효성 검사
   */
  function validate(): boolean {
    if (!isEdit && !userid.trim()) {
      setError('아이디를 입력해주세요.');
      return false;
    }
    if (!phoneNumber.trim()) {
      setError('전화번호를 입력해주세요.');
      return false;
    }
    if (!name.trim()) {
      setError('이름을 입력해주세요.');
      return false;
    }
    if (!birthDate) {
      setError('생년월일을 입력해주세요.');
      return false;
    }
    if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      setError('유효한 이메일 형식이 아닙니다.');
      return false;
    }
    return true;
  }

  /**
   * 폼 제출 처리
   */
  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);

    if (!validate()) {
      return;
    }

    setLoading(true);
    try {
      if (isEdit) {
        const updateData: UpdateUserDto = {
          email: email.trim() || undefined,
          name: name.trim(),
          birthDate: birthDate,
          gender,
          status,
        };
        await onSave(updateData);
      } else {
        const createData: CreateUserDto = {
          userid: userid.trim(),
          email: email.trim() || undefined,
          phoneNumber: phoneNumber.trim(),
          name: name.trim(),
          birthDate: birthDate,
          gender,
        };
        await onSave(createData);
      }
    } catch (err) {
      const message = err instanceof Error ? err.message : '저장에 실패했습니다.';
      setError(message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-2xl overflow-hidden rounded-xl bg-gray-800 ring-1 ring-white/10">
        <div className="p-6">
          <div className="mb-6 flex items-center justify-between">
            <h2 className="text-2xl font-semibold text-white">
              {isEdit ? '회원 수정' : '회원 추가'}
            </h2>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-white"
              disabled={loading}
            >
              ✕
            </button>
          </div>

          {error && (
            <div className="mb-4 rounded-lg bg-red-500/10 p-3 text-sm text-red-400 ring-1 ring-red-500/20">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            {!isEdit && (
              <Input
                label="아이디"
                required
                value={userid}
                onChange={(e) => setUserid(e.target.value)}
                disabled={loading}
                placeholder="사용할 아이디를 입력하세요"
              />
            )}

            <Input
              label="이름"
              required
              value={name}
              onChange={(e) => setName(e.target.value)}
              disabled={loading}
              placeholder="이름을 입력하세요"
            />

            <Input
              label="전화번호"
              type="tel"
              required
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
              disabled={loading || isEdit}
              placeholder="010-1234-5678"
            />

            <Input
              label="이메일"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              disabled={loading}
              placeholder="example@email.com"
            />

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-300">
                생년월일 <span className="text-red-400">*</span>
              </label>
              <Input
                type="date"
                required
                value={birthDate}
                onChange={(e) => setBirthDate(e.target.value)}
                disabled={loading}
              />
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-300">
                성별 <span className="text-red-400">*</span>
              </label>
              <select
                value={gender}
                onChange={(e) => setGender(e.target.value as Gender)}
                disabled={loading}
                className="block w-full rounded-md bg-white/5 px-3 py-2 text-white ring-1 ring-inset ring-white/10 focus:ring-2 focus:ring-primary"
              >
                <option value="MALE">남성</option>
                <option value="FEMALE">여성</option>
                <option value="OTHER">기타</option>
              </select>
            </div>

            {isEdit && (
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-300">회원 상태</label>
                <select
                  value={status}
                  onChange={(e) => setStatus(e.target.value as UserStatus)}
                  disabled={loading}
                  className="block w-full rounded-md bg-white/5 px-3 py-2 text-white ring-1 ring-inset ring-white/10 focus:ring-2 focus:ring-primary"
                >
                  <option value="ACTIVE">활성</option>
                  <option value="INACTIVE">비활성</option>
                  <option value="SUSPENDED">정지</option>
                  <option value="DELETED">삭제</option>
                </select>
              </div>
            )}

            <div className="flex justify-end gap-3 pt-4">
              <Button type="button" variant="secondary" onClick={onClose} disabled={loading}>
                취소
              </Button>
              <Button type="submit" loading={loading}>
                {isEdit ? '수정' : '생성'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

