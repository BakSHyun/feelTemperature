/**
 * 레이아웃 컴포넌트
 * 
 * 애플리케이션의 기본 레이아웃 구조를 제공합니다.
 * Header와 메인 콘텐츠 영역을 포함합니다.
 */

import { Header } from './Header';

interface LayoutProps {
  /** 자식 컴포넌트 */
  children: React.ReactNode;
}

/**
 * 레이아웃 컴포넌트
 */
export function Layout({ children }: LayoutProps) {
  return (
    <div className="min-h-screen bg-gray-900">
      <Header />
      <main className="mx-auto max-w-7xl px-6 py-12 lg:px-8">
        {children}
      </main>
    </div>
  );
}

