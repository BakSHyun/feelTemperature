/**
 * 대시보드 페이지
 * 
 * 전체 통계 및 요약 정보를 표시하는 메인 페이지입니다.
 */

export function DashboardPage() {
  return (
    <div className="mx-auto max-w-7xl">
      {/* 페이지 헤더 */}
      <div className="mb-12">
        <h1 className="text-4xl font-semibold tracking-tight text-pretty text-white sm:text-5xl">
          대시보드
        </h1>
        <p className="mt-4 text-lg text-gray-300">
          Feeling Temperature (FETE) 관리 시스템
        </p>
      </div>

      {/* 통계 카드 영역 */}
      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
        {/* 질문 수 카드 */}
        <div className="overflow-hidden rounded-xl bg-white/5 ring-1 ring-white/10">
          <div className="p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                  <svg
                    className="h-6 w-6 text-primary"
                    fill="none"
                    viewBox="0 0 24 24"
                    strokeWidth="1.5"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M9.879 7.519c1.171-1.025 3.071-1.025 4.242 0 1.172 1.025 1.172 2.687 0 3.712-.203.179-.43.326-.67.442-.745.361-1.45.999-1.45 1.827v.75M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9 5.25h.008v.008H12v-.008z"
                    />
                  </svg>
                </div>
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-400">질문 수</dt>
                  <dd className="mt-1 text-2xl font-semibold text-white">
                    {/* TODO: API 연동 후 실제 데이터 표시 */}
                    -
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        {/* 기록 수 카드 */}
        <div className="overflow-hidden rounded-xl bg-white/5 ring-1 ring-white/10">
          <div className="p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                  <svg
                    className="h-6 w-6 text-primary"
                    fill="none"
                    viewBox="0 0 24 24"
                    strokeWidth="1.5"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z"
                    />
                  </svg>
                </div>
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-400">기록 수</dt>
                  <dd className="mt-1 text-2xl font-semibold text-white">
                    {/* TODO: API 연동 후 실제 데이터 표시 */}
                    -
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        {/* 활성 매칭 카드 */}
        <div className="overflow-hidden rounded-xl bg-white/5 ring-1 ring-white/10">
          <div className="p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                  <svg
                    className="h-6 w-6 text-primary"
                    fill="none"
                    viewBox="0 0 24 24"
                    strokeWidth="1.5"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M18 18.72a9.094 9.094 0 003.741-.479 3 3 0 00-4.682-2.72m.94 3.198l.001.031c0 .225-.012.447-.037.666A11.944 11.944 0 0112 21c-2.17 0-4.207-.576-5.963-1.584A6.062 6.062 0 016 18.719m12 0a5.971 5.971 0 00-.941-3.197m0 0A5.995 5.995 0 0012 12.75a5.995 5.995 0 00-5.059 2.772m0 0a3 3 0 00-4.681 2.72 8.986 8.986 0 003.74.477m.94-3.197a5.971 5.971 0 00-.94 3.197M15 6.75a3 3 0 11-6 0 3 3 0 016 0zm6 3a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0zm-13.5 0a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0z"
                    />
                  </svg>
                </div>
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-400">활성 매칭</dt>
                  <dd className="mt-1 text-2xl font-semibold text-white">
                    {/* TODO: API 연동 후 실제 데이터 표시 */}
                    -
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        {/* 평균 온도 카드 */}
        <div className="overflow-hidden rounded-xl bg-white/5 ring-1 ring-white/10">
          <div className="p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                  <svg
                    className="h-6 w-6 text-primary"
                    fill="none"
                    viewBox="0 0 24 24"
                    strokeWidth="1.5"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M12 3v2.25m6.364.386l-1.591 1.591M21 12h-2.25m-.386 6.364l-1.591-1.591M12 18.75V21m-4.773-4.227l-1.591 1.591M5.25 12H3m4.227-4.773L5.636 5.636M15.75 12a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0z"
                    />
                  </svg>
                </div>
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-400">평균 온도</dt>
                  <dd className="mt-1 text-2xl font-semibold text-white">
                    {/* TODO: API 연동 후 실제 데이터 표시 */}
                    -
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* 최근 활동 영역 (향후 구현) */}
      <div className="mt-12">
        <h2 className="text-2xl font-semibold text-white">최근 활동</h2>
        <p className="mt-2 text-gray-400">최근 생성된 기록 및 매칭을 확인할 수 있습니다.</p>
        {/* TODO: 최근 활동 목록 컴포넌트 구현 */}
      </div>
    </div>
  );
}

