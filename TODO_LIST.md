# TODO 리스트

## 프론트엔드

### 질문 관리
- [ ] 질문 생성 기능 (QuestionsPage.tsx)
- [ ] 질문 수정 기능 (QuestionsPage.tsx)
- [ ] 질문 삭제 기능 (QuestionsPage.tsx)

### 기록 관리
- [ ] 기록 목록 컴포넌트 구현 (RecordsPage.tsx)

### 대시보드
- [ ] API 연동 후 실제 데이터 표시 (DashboardPage.tsx)
  - 통계 카드 데이터
  - 차트 데이터
  - 최근 활동 목록

## 백엔드

### API 개선
- [ ] 질문 CRUD API 구현
  - POST /api/questions (생성)
  - PUT /api/questions/{id} (수정)
  - DELETE /api/questions/{id} (삭제)

### 마이크로서비스 전환
- [ ] MatchingController 경로 변경: `/matching` → `/v1/matching`

## 완료된 항목
- ✅ 질문 카테고리 기능 구현
- ✅ 회원 관리 기능 구현
- ✅ 회원 히스토리 조회 기능 구현

