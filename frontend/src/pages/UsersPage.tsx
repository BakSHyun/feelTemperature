/**
 * 회원 관리 페이지
 * 
 * 회원 목록 조회, 생성, 수정, 삭제 기능을 제공합니다.
 * 페이징, 검색, 필터링 기능을 포함합니다.
 */

import { useState, useEffect, useMemo } from 'react';
import { userService } from '../services/userService';
import type { User, CreateUserDto, UpdateUserDto } from '../types/user.types';
import { Button } from '../components/common/Button';
import { Input } from '../components/common/Input';
import { UserFormModal } from '../components/users/UserFormModal';
import { UserDetailModal } from '../components/users/UserDetailModal';

/**
 * 회원 관리 페이지 컴포넌트
 */
export function UsersPage() {
  // 상태 관리
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);

  // 페이징 상태
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize] = useState(20);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  // 검색/필터 상태
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('');

  /**
   * 회원 목록 조회
   */
  useEffect(() => {
    fetchUsers();
  }, [currentPage, statusFilter]);

  /**
   * 회원 목록을 API에서 가져옵니다.
   */
  async function fetchUsers() {
    setLoading(true);
    setError(null);
    try {
      const response = await userService.getUsers(currentPage, pageSize, 'createdAt,desc');
      setUsers(response.content || []);
      setTotalElements(response.totalElements || 0);
      setTotalPages(response.totalPages || 0);
    } catch (err) {
      const message = err instanceof Error ? err.message : '회원 목록을 불러오는데 실패했습니다.';
      setError(message);
      console.error('Failed to fetch users:', err);
      setUsers([]);
    } finally {
      setLoading(false);
    }
  }

  /**
   * 검색/필터링된 회원 목록
   */
  const filteredUsers = useMemo(() => {
    let filtered = users;

    // 검색어 필터링
    if (searchTerm.trim()) {
      const term = searchTerm.toLowerCase();
      filtered = filtered.filter(
        (user) =>
          user.userid.toLowerCase().includes(term) ||
          user.name.toLowerCase().includes(term) ||
          user.phoneNumber.includes(term) ||
          (user.email && user.email.toLowerCase().includes(term))
      );
    }

    // 상태 필터링
    if (statusFilter) {
      filtered = filtered.filter((user) => user.status === statusFilter);
    }

    return filtered;
  }, [users, searchTerm, statusFilter]);

  /**
   * 회원 생성 처리
   */
  async function handleCreate(data: CreateUserDto | UpdateUserDto) {
    try {
      await userService.create(data as CreateUserDto);
      setShowCreateForm(false);
      await fetchUsers();
    } catch (err) {
      const message = err instanceof Error ? err.message : '회원 생성에 실패했습니다.';
      setError(message);
      throw err; // 모달에서 에러 처리하도록
    }
  }

  /**
   * 회원 수정 처리
   */
  async function handleUpdate(data: CreateUserDto | UpdateUserDto) {
    if (!editingUser) return;

    try {
      await userService.update(editingUser.id, data as UpdateUserDto);
      setEditingUser(null);
      await fetchUsers();
    } catch (err) {
      const message = err instanceof Error ? err.message : '회원 수정에 실패했습니다.';
      setError(message);
      throw err; // 모달에서 에러 처리하도록
    }
  }

  /**
   * 회원 삭제 처리
   */
  async function handleDelete(id: number) {
    if (!confirm('정말 삭제하시겠습니까?')) {
      return;
    }

    try {
      await userService.delete(id);
      await fetchUsers();
    } catch (err) {
      const message = err instanceof Error ? err.message : '회원 삭제에 실패했습니다.';
      setError(message);
      console.error('Failed to delete user:', err);
    }
  }

  return (
    <div>
      {/* 페이지 헤더 */}
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-4xl font-semibold tracking-tight text-pretty text-white sm:text-5xl">
            회원 관리
          </h1>
          <p className="mt-4 text-lg text-gray-300">
            회원 정보를 조회, 생성, 수정, 삭제할 수 있습니다.
          </p>
        </div>
        <Button onClick={() => setShowCreateForm(true)}>회원 추가</Button>
      </div>

      {/* 검색 및 필터 */}
      <div className="mb-6 flex gap-4">
        <div className="flex-1">
          <Input
            placeholder="아이디, 이름, 전화번호, 이메일로 검색..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="rounded-md bg-white/5 px-4 py-2 text-white ring-1 ring-inset ring-white/10 focus:ring-2 focus:ring-primary"
        >
          <option value="">전체 상태</option>
          <option value="ACTIVE">활성</option>
          <option value="INACTIVE">비활성</option>
          <option value="SUSPENDED">정지</option>
          <option value="DELETED">삭제</option>
        </select>
      </div>

      {/* 에러 메시지 */}
      {error && (
        <div className="mb-6 rounded-lg bg-red-500/10 p-4 text-red-400 ring-1 ring-red-500/20">
          {error}
        </div>
      )}

      {/* 회원 목록 */}
      {loading ? (
        <div className="flex items-center justify-center py-12">
          <div className="text-gray-400">로딩 중...</div>
        </div>
      ) : filteredUsers.length === 0 ? (
        <div className="overflow-hidden rounded-xl bg-white/5 ring-1 ring-white/10">
          <div className="p-6">
            <p className="text-center text-gray-400">
              {searchTerm || statusFilter ? '검색 결과가 없습니다.' : '등록된 회원이 없습니다.'}
            </p>
          </div>
        </div>
      ) : (
        <>
          <div className="overflow-hidden rounded-xl bg-white/5 ring-1 ring-white/10">
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-white/10">
                <thead className="bg-white/5">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-300">
                      아이디
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-300">
                      이름
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-300">
                      전화번호
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-300">
                      이메일
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-300">
                      상태
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-300">
                      인증 상태
                    </th>
                    <th className="px-6 py-3 text-right text-xs font-medium uppercase tracking-wider text-gray-300">
                      작업
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-white/10 bg-white/5">
                  {filteredUsers.map((user) => (
                    <tr key={user.id} className="hover:bg-white/5">
                      <td className="whitespace-nowrap px-6 py-4 text-sm text-white">
                        {user.userid}
                      </td>
                      <td className="whitespace-nowrap px-6 py-4 text-sm text-white">
                        {user.name}
                      </td>
                      <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-300">
                        {user.phoneNumber}
                      </td>
                      <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-300">
                        {user.email || '-'}
                      </td>
                      <td className="whitespace-nowrap px-6 py-4 text-sm">
                        <span
                          className={`inline-flex rounded-full px-2 py-1 text-xs font-semibold ${
                            user.status === 'ACTIVE'
                              ? 'bg-green-500/20 text-green-400'
                              : user.status === 'SUSPENDED'
                                ? 'bg-red-500/20 text-red-400'
                                : 'bg-gray-500/20 text-gray-400'
                          }`}
                        >
                          {user.status}
                        </span>
                      </td>
                      <td className="whitespace-nowrap px-6 py-4 text-sm">
                        <span
                          className={`inline-flex rounded-full px-2 py-1 text-xs font-semibold ${
                            user.verificationStatus === 'FULLY_VERIFIED'
                              ? 'bg-blue-500/20 text-blue-400'
                              : user.verificationStatus === 'PHONE_VERIFIED'
                                ? 'bg-yellow-500/20 text-yellow-400'
                                : 'bg-gray-500/20 text-gray-400'
                          }`}
                        >
                          {user.verificationStatus}
                        </span>
                      </td>
                      <td className="whitespace-nowrap px-6 py-4 text-right text-sm font-medium">
                        <button
                          onClick={() => setSelectedUser(user)}
                          className="mr-2 text-primary hover:text-primary-light"
                        >
                          상세
                        </button>
                        <button
                          onClick={() => setEditingUser(user)}
                          className="mr-2 text-primary hover:text-primary-light"
                        >
                          수정
                        </button>
                        <button
                          onClick={() => handleDelete(user.id)}
                          className="text-red-400 hover:text-red-300"
                        >
                          삭제
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* 페이징 */}
          {totalPages > 1 && (
            <div className="mt-6 flex items-center justify-between">
              <div className="text-sm text-gray-400">
                총 {totalElements}명 중 {currentPage * pageSize + 1}-
                {Math.min((currentPage + 1) * pageSize, totalElements)}명 표시
              </div>
              <div className="flex gap-2">
                <Button
                  variant="secondary"
                  size="sm"
                  onClick={() => setCurrentPage(0)}
                  disabled={currentPage === 0}
                >
                  처음
                </Button>
                <Button
                  variant="secondary"
                  size="sm"
                  onClick={() => setCurrentPage((p) => Math.max(0, p - 1))}
                  disabled={currentPage === 0}
                >
                  이전
                </Button>
                <span className="flex items-center px-4 text-sm text-gray-300">
                  {currentPage + 1} / {totalPages}
                </span>
                <Button
                  variant="secondary"
                  size="sm"
                  onClick={() => setCurrentPage((p) => Math.min(totalPages - 1, p + 1))}
                  disabled={currentPage >= totalPages - 1}
                >
                  다음
                </Button>
                <Button
                  variant="secondary"
                  size="sm"
                  onClick={() => setCurrentPage(totalPages - 1)}
                  disabled={currentPage >= totalPages - 1}
                >
                  마지막
                </Button>
              </div>
            </div>
          )}
        </>
      )}

      {/* 회원 생성 모달 */}
      {showCreateForm && (
        <UserFormModal
          onClose={() => setShowCreateForm(false)}
          onSave={handleCreate}
        />
      )}

      {/* 회원 수정 모달 */}
      {editingUser && (
        <UserFormModal
          user={editingUser}
          onClose={() => setEditingUser(null)}
          onSave={handleUpdate}
        />
      )}

      {/* 회원 상세 모달 */}
      {selectedUser && (
        <UserDetailModal user={selectedUser} onClose={() => setSelectedUser(null)} />
      )}
    </div>
  );
}
