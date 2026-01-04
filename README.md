# 관계 상태 기록 도구 (Relationship Status Tracker)

## 프로젝트 개요

두 사람이 같은 순간에 응답한 결과를 서버에 변조 불가능한 기록으로 남기는 앱과 관리자 콘솔(CMS)입니다.

## 프로젝트 구조

```
FT/
├── backend/          # FastAPI 백엔드
├── frontend/         # React CMS (관리자 콘솔)
├── app/              # Flutter 앱 (추후 개발)
├── database/         # DB 마이그레이션 및 스키마
└── docs/             # 문서
```

## 기술 스택

- **백엔드**: Spring Boot, PostgreSQL, JPA
- **CMS**: React, TypeScript, Tailwind CSS (추후 개발)
- **앱**: Flutter (추후 개발)
- **PDF 생성**: iText7

## 시작하기

### 백엔드 실행

```bash
cd backend
mvn spring-boot:run
```

또는 IDE에서 `RstrackerApplication` 클래스를 실행합니다.

### 환경 변수 설정

`application.yml` 파일을 수정하거나 환경 변수로 다음 값들을 설정하세요:
- `DB_USERNAME`: PostgreSQL 사용자명
- `DB_PASSWORD`: PostgreSQL 비밀번호
- `ADMIN_USERNAME`: 관리자 계정 (기본값: admin)
- `ADMIN_PASSWORD`: 관리자 비밀번호 (기본값: admin123)

### CMS 실행

```bash
cd frontend
npm install
npm run dev
```

## 개발 단계

1. ✅ 프로젝트 구조 설계
2. ⏳ DB 스키마 설계 및 구현
3. ⏳ 백엔드 API 개발
4. ⏳ CMS 개발
5. ⏳ 앱 개발 (추후)

## 주요 기능

### 앱 기능
- 매칭 생성/참여 (코드/QR)
- 질문 응답 (6개 질문)
- 결과 계산 (온도)
- 기록 생성 및 조회
- PDF 다운로드

### CMS 기능
- 질문 관리 (CRUD)
- 기록 조회 및 관리
- 통계 대시보드
- PDF 재생성
- 설정 관리

