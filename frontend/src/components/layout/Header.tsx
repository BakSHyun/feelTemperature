/**
 * 헤더 컴포넌트
 * 
 * 애플리케이션 상단 네비게이션을 제공합니다.
 */

import { Link, useLocation } from 'react-router-dom';

/**
 * 네비게이션 메뉴 아이템 타입
 */
interface NavItem {
  /** 메뉴 이름 */
  name: string;
  /** 링크 경로 */
  href: string;
}

/**
 * 네비게이션 메뉴 목록
 */
const navigation: NavItem[] = [
  { name: '대시보드', href: '/' },
  { name: '질문 관리', href: '/questions' },
  { name: '기록 조회', href: '/records' },
  { name: '회원 관리', href: '/users' },
];

/**
 * 헤더 컴포넌트
 */
export function Header() {
  const location = useLocation();

  return (
    <header className="border-b border-white/10 bg-gray-900">
      <nav className="mx-auto max-w-7xl px-6 lg:px-8" aria-label="Top">
        <div className="flex w-full items-center justify-between border-b border-white/10 py-4 lg:border-none">
          {/* 로고 */}
          <div className="flex items-center">
            <Link to="/" className="text-2xl font-bold text-white">
              FETE
            </Link>
            <span className="ml-2 text-sm text-gray-400">CMS</span>
          </div>

          {/* 네비게이션 메뉴 */}
          <div className="ml-10 space-x-4">
            {navigation.map((item) => {
              const isActive = location.pathname === item.href;
              return (
                <Link
                  key={item.name}
                  to={item.href}
                  className={`text-sm font-semibold leading-6 transition-colors ${
                    isActive
                      ? 'text-primary'
                      : 'text-gray-300 hover:text-white'
                  }`}
                >
                  {item.name}
                </Link>
              );
            })}
          </div>
        </div>
      </nav>
    </header>
  );
}

