# Supabase 데이터베이스 설정 가이드

## 목차

1. [빠른 시작](#빠른-시작)
2. [Supabase 프로젝트 생성](#supabase-프로젝트-생성)
3. [데이터베이스 스키마 생성](#데이터베이스-스키마-생성)
4. [애플리케이션 연결 설정](#애플리케이션-연결-설정)
5. [문제 해결](#문제-해결)
6. [테이블 구조](#테이블-구조)

---

## 빠른 시작

### 1단계: Supabase 프로젝트 생성
1. https://supabase.com 접속 및 가입
2. New Project 생성
3. Database Password 설정 및 Region 선택

### 2단계: 스키마 생성 (Flyway 자동 실행)
애플리케이션 실행 시 Flyway가 자동으로 스키마를 생성합니다:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod-supabase
```

또는 SQL Editor에서 수동 실행:
1. Supabase 대시보드 → **SQL Editor**
2. `backend/src/main/resources/db/migration/V1__Create_initial_schema.sql` 파일 내용 복사/붙여넣기
3. **Run** 버튼 클릭

### 3단계: 애플리케이션 연결 설정
1. Supabase 대시보드 → **Settings** → **Database**
2. **Connection String** → **Method**: **"Session pooler"** 선택
3. 연결 문자열 복사
4. `.env` 파일 및 `application-prod-supabase.yml` 설정

### 4단계: 애플리케이션 실행
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=prod-supabase
```

---

## Supabase 프로젝트 생성

### 1. Supabase 가입 및 프로젝트 생성

1. **Supabase 가입**: https://supabase.com
2. **New Project** 클릭
3. **프로젝트 정보 입력**:
   - **Name**: `rstracker` (또는 원하는 이름)
   - **Database Password**: 강력한 비밀번호 설정 (기억해두세요!)
   - **Region**: 가장 가까운 지역 선택 (예: `ap-northeast-1`)
4. **Create new project** 클릭 (약 2분 소요)

### 2. 연결 정보 확인

프로젝트 생성 후:
1. **Settings** → **Database** 메뉴
2. **Connection String** 탭에서 연결 정보 확인

**중요**: 
- Direct connection은 IPv6만 지원 (IPv4 네트워크에서는 연결 불가)
- **Session Pooler**를 사용해야 IPv4 네트워크에서도 연결 가능

---

## 데이터베이스 스키마 생성

### 방법 1: Flyway 자동 마이그레이션 (권장) ⭐⭐⭐

애플리케이션 실행 시 Flyway가 자동으로 스키마를 생성합니다:

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=prod-supabase
```

**동작**:
- Flyway가 `src/main/resources/db/migration/` 디렉토리의 마이그레이션 파일을 자동 실행
- `flyway_schema_history` 테이블 자동 생성 (마이그레이션 히스토리 관리)
- 이미 실행된 마이그레이션은 재실행하지 않음

**장점**:
- 자동화된 스키마 관리
- 버전 관리 및 히스토리 추적
- 코드와 스키마가 함께 버전 관리됨

### 방법 2: SQL Editor 사용 (수동 - 선택적)

네트워크 문제로 Flyway가 실행되지 않는 경우:

1. Supabase 대시보드 접속
2. 왼쪽 메뉴 → **SQL Editor** 클릭
3. `backend/src/main/resources/db/migration/V1__Create_initial_schema.sql` 파일 열기
4. 전체 내용 복사 (Ctrl+A, Ctrl+C)
5. SQL Editor에 붙여넣기 (Ctrl+V)
6. **Run** 버튼 클릭 (또는 Cmd+Enter)

**주의**: 이 방법은 Flyway 히스토리가 생성되지 않으므로, 가능하면 방법 1을 권장합니다.

---

## 애플리케이션 연결 설정

### 1. Session Pooler 연결 문자열 가져오기

1. Supabase 대시보드 → **Settings** → **Database**
2. **Connection String** 탭 클릭
3. 다음 설정 확인:
   - **Type**: JDBC
   - **Source**: Primary Database
   - **Method**: **"Session pooler"** 선택 ⭐ (중요!)
4. 연결 문자열 복사

**연결 문자열 예시**:
```
jdbc:postgresql://aws-1-ap-northeast-1.pooler.supabase.com:5432/postgres?user=postgres.xxxxx&password=[YOUR-PASSWORD]
```

### 2. 환경 변수 설정

`backend/.env` 파일에 추가:

```env
# Supabase Database Connection
SUPABASE_DATABASE=postgres
SUPABASE_DB_PASSWORD=your-password

# Supabase Session Pooler (IPv4 지원)
SUPABASE_POOLER_HOST=aws-1-ap-northeast-1.pooler.supabase.com
SUPABASE_POOLER_PORT=5432
SUPABASE_POOLER_USER=postgres.xxxxx  # project-ref 포함된 username
```

**참고**: 
- `SUPABASE_POOLER_USER`는 `postgres.프로젝트ID` 형식입니다
- 프로젝트 ID는 Supabase 대시보드 URL에서 확인 가능

### 3. application-prod-supabase.yml 설정

`backend/src/main/resources/application-prod-supabase.yml`:

```yaml
spring:
  datasource:
    # Session Pooler (IPv4 지원)
    url: jdbc:postgresql://${SUPABASE_POOLER_HOST:aws-1-ap-northeast-1.pooler.supabase.com}:${SUPABASE_POOLER_PORT:5432}/${SUPABASE_DATABASE:postgres}?user=${SUPABASE_POOLER_USER:postgres.xxxxx}&password=${SUPABASE_DB_PASSWORD:your-password}&sslmode=require
    username: ${SUPABASE_POOLER_USER:postgres.xxxxx}
    password: ${SUPABASE_DB_PASSWORD:your-password}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10  # Supabase 무료 티어: 최대 60 연결
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
  
  jpa:
    hibernate:
      ddl-auto: update  # 프로덕션에서는 validate 또는 none 권장
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: false
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true

logging:
  level:
    com.rstracker: INFO
    org.springframework: WARN
    org.hibernate: WARN
    org.hibernate.SQL: WARN
```

### 4. 애플리케이션 실행

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=prod-supabase
```

**성공 확인**:
- 애플리케이션이 정상적으로 시작됨
- Health check: `curl http://localhost:8080/api/actuator/health` → `{"status":"UP"}`
- 초기 데이터(질문 세트)가 자동으로 생성됨

---

## 문제 해결

### 1. IPv4 호환성 문제

**증상**:
```
java.net.NoRouteToHostException: No route to host
```

**원인**: Supabase Direct connection은 IPv6만 지원하는데, 현재 네트워크가 IPv4-only입니다.

**해결**: **Session Pooler**를 사용하세요 (위의 "애플리케이션 연결 설정" 참고)

### 2. SSL 인증서 오류

**질문**: SSL 인증서를 다운로드해야 하나요?

**답변**: **아니요, 필요 없습니다.**

- 현재 설정(`sslmode=require`)으로 충분합니다
- SSL 인증서는 IPv4 문제를 해결하지 않습니다

### 3. PostgreSQL 예약어 오류

**증상**:
```
ERROR: syntax error at or near "order"
```

**원인**: `order`는 PostgreSQL 예약어입니다.

**해결**: Entity 클래스에서 `@Column(name = "\"order\"")`로 설정되어 있습니다.

**확인 파일**:
- `Question.java`
- `QuestionChoice.java`

### 4. 연결 실패

**확인 사항**:
1. Session Pooler 사용 여부 확인 (`pooler.supabase.com` 도메인)
2. 사용자명에 project-ref 포함 여부 (`postgres.xxxxx`)
3. 비밀번호 정확성 확인
4. `.env` 파일의 환경 변수 로드 확인

---

## 테이블 구조

### 테이블 목록

1. **matchings** - 매칭 정보
2. **participants** - 참여자 정보
3. **questions** - 질문
4. **question_choices** - 질문 선택지
5. **answers** - 응답
6. **records** - 기록 (최종 결과)
7. **admin_users** - 관리자 계정

### 주요 테이블 구조

#### questions
- `id` (BIGSERIAL, PK)
- `question_text` (TEXT)
- `question_type` (VARCHAR(50))
- `"order"` (INTEGER) - 예약어이므로 따옴표 사용
- `is_active` (BOOLEAN)
- `version` (INTEGER)
- `created_at`, `updated_at` (TIMESTAMP)

#### question_choices
- `id` (BIGSERIAL, PK)
- `question_id` (BIGINT, FK → questions.id)
- `choice_text` (TEXT)
- `choice_value` (VARCHAR(100))
- `"order"` (INTEGER) - 예약어이므로 따옴표 사용
- `temperature_weight` (DOUBLE PRECISION)
- `created_at` (TIMESTAMP)

#### matchings
- `id` (BIGSERIAL, PK)
- `code` (VARCHAR(10), UNIQUE)
- `status` (VARCHAR(20))
- `qr_code_path` (VARCHAR(255))
- `created_at`, `completed_at` (TIMESTAMP)

#### participants
- `id` (BIGSERIAL, PK)
- `matching_id` (BIGINT, FK → matchings.id)
- `participant_code` (VARCHAR(36), UNIQUE)
- `joined_at` (TIMESTAMP)

#### answers
- `id` (BIGSERIAL, PK)
- `participant_id` (BIGINT, FK → participants.id)
- `question_id` (BIGINT, FK → questions.id)
- `choice_id` (BIGINT, FK → question_choices.id)
- `answered_at` (TIMESTAMP)

#### records
- `id` (BIGSERIAL, PK)
- `record_id` (VARCHAR(36), UNIQUE)
- `matching_id` (BIGINT, FK → matchings.id, UNIQUE)
- `temperature` (DOUBLE PRECISION)
- `temperature_diff` (DOUBLE PRECISION)
- `summary` (JSONB)
- `is_active` (BOOLEAN)
- `created_at` (TIMESTAMP)

#### admin_users
- `id` (BIGSERIAL, PK)
- `username` (VARCHAR(100), UNIQUE)
- `password_hash` (VARCHAR(255))
- `is_active` (BOOLEAN)
- `created_at` (TIMESTAMP)

### 인덱스

모든 주요 컬럼에 인덱스가 생성되어 있습니다:
- 외래키 컬럼
- 검색에 자주 사용되는 컬럼 (code, participant_code 등)
- 정렬에 사용되는 컬럼 (order)

---

## Session Pooler 제약사항

Session Pooler를 사용할 때 주의사항:

1. **Prepared Statements**
   - 세션 레벨에서만 사용 가능
   - Spring Boot/JPA는 일반적으로 문제없음

2. **LISTEN/NOTIFY**
   - 사용 불가 (대부분의 애플리케이션에 영향 없음)

3. **일부 관리 명령**
   - 제한될 수 있음 (일반 애플리케이션에는 문제없음)

**결론**: 대부분의 Spring Boot 애플리케이션에는 Session Pooler가 문제없이 작동합니다.

---

## 초기 데이터

애플리케이션이 시작되면 `DataInitializer`가 자동으로 질문 세트 v1을 생성합니다:

- Q1: 만남의 장소 (context)
- Q2: 만남의 계기 (context)
- Q3: 현재 분위기 인식 (sentiment, 가중치 3.0)
- Q4: 기대 수준 (expectation, 가중치 2.0)
- Q5: 신체적 거리 인식 (distance, 가중치 3.0)
- Q6: 현재 상태 확인 (comfort, 가중치 2.0)

**참고**: 이미 데이터가 있으면 스킵됩니다.

---

## 유용한 명령어

### 연결 테스트

```bash
# Health check
curl http://localhost:8080/api/actuator/health

# Swagger UI
open http://localhost:8080/api/swagger-ui.html
```

### 데이터베이스 확인 (Supabase 대시보드)

1. **Table Editor**: 테이블 데이터 확인/수정
2. **SQL Editor**: SQL 쿼리 실행
3. **Database**: 연결 정보 및 설정

---

## 참고 자료

- [Supabase 공식 문서](https://supabase.com/docs)
- [PostgreSQL 예약어 목록](https://www.postgresql.org/docs/current/sql-keywords-appendix.html)
- [Session Pooler 가이드](https://supabase.com/docs/guides/database/connecting-to-postgres#connection-pooler)

---

**최종 업데이트**: 2026-01-04
