# Relationship Status Tracker Backend

Spring Boot 기반 백엔드 API 서버입니다.

## 기술 스택

- Spring Boot 3.2.0
- Java 17
- PostgreSQL
- JPA/Hibernate
- Lombok
- Maven

## 실행 방법

### 옵션 0: Docker Compose 사용 ⭐ (추천 - 격리된 환경)

Docker를 사용하여 백엔드와 PostgreSQL을 함께 실행합니다. 로컬 환경과 충돌 없이 개발할 수 있습니다.

```bash
# 프로젝트 루트 디렉토리에서 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f backend

# 중지
docker-compose down
```

자세한 내용은 [Docker 설정 가이드](./docs/DOCKER_SETUP.md) 참고

**접속 정보:**
- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- PostgreSQL: localhost:5433 (데이터베이스: relationship_tracker, 사용자: rstracker)

---

### 옵션 1: 로컬 환경에서 실행

### 1. PostgreSQL 데이터베이스 설정

데이터베이스를 생성합니다:

```sql
CREATE DATABASE relationship_tracker;
```

### 2. 데이터베이스 설정

#### 옵션 1: Supabase (PostgreSQL) - 추천 ⭐ (무료 티어)

1. **Supabase 가입 및 프로젝트 생성**: https://supabase.com
2. **연결 정보 확인**: Settings → Database → Connection String (JDBC)
3. **환경 변수 설정**:
```bash
export SUPABASE_HOST=db.xxxxx.supabase.co
export SUPABASE_DB_PASSWORD=your-password
```

4. **프로덕션 프로파일로 실행**:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod-supabase
```

자세한 설정은 [Supabase 설정 가이드](./docs/SUPABASE_SETUP.md) 참고

#### 옵션 2: 로컬 PostgreSQL

1. **데이터베이스 생성** (이미 생성되어 있으면 생략):
```sql
CREATE DATABASE relationship_tracker;
```

2. **애플리케이션 실행**:
```bash
mvn spring-boot:run -DskipTests
```

**Flyway 마이그레이션**:
- 애플리케이션 시작 시 Flyway가 자동으로 스키마를 생성합니다
- `src/main/resources/db/migration/` 디렉토리의 마이그레이션 파일이 자동 실행됩니다
- `DataInitializer`가 질문 데이터를 자동으로 생성합니다

자세한 내용은 [Supabase 설정 가이드](./docs/SUPABASE_SETUP.md) 참고

### 3. 애플리케이션 실행

```bash
mvn spring-boot:run
```

또는 IDE에서 `RstrackerApplication` 클래스를 실행합니다.

### 4. API 문서 확인

애플리케이션 실행 후:
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/api-docs

## API 엔드포인트

기본 URL: `http://localhost:8080/api`

### 매칭 (Matching)

- `POST /api/matching/create` - 매칭 생성
- `POST /api/matching/join/{code}` - 매칭 참여
- `GET /api/matching/{code}` - 매칭 정보 조회
- `GET /api/matching/status/{code}` - 매칭 상태 조회

### 질문 (Questions)

- `GET /api/questions` - 활성 질문 목록 조회
- `GET /api/questions/{id}` - 질문 상세 조회

### 응답 (Answers)

- `POST /api/answers/submit/{participantCode}` - 응답 제출

### 기록 (Records)

- `POST /api/records/create/{matchingId}` - 기록 생성
- `GET /api/records/{recordId}` - 기록 조회
- `GET /api/records/matching/{matchingId}` - 매칭 ID로 기록 조회
- `PUT /api/records/{recordId}/deactivate` - 기록 비활성화

## 초기 데이터

애플리케이션 시작 시 `DataInitializer`가 질문 세트 v1을 자동으로 생성합니다:

1. Q1: 만남의 장소 (context)
2. Q2: 만남의 계기 (context)
3. Q3: 현재 분위기 인식 (sentiment) - 온도 계산 가중치 3.0
4. Q4: 기대 수준 (expectation) - 온도 계산 가중치 2.0
5. Q5: 신체적 거리 인식 (distance) - 온도 계산 가중치 3.0
6. Q6: 현재 상태 확인 (comfort) - 온도 계산 가중치 2.0

## 데이터베이스 스키마

주요 테이블:
- `matchings`: 매칭 정보
- `participants`: 참여자 정보
- `questions`: 질문 정보
- `question_choices`: 질문 선택지
- `answers`: 응답 정보
- `records`: 기록 정보 (최종 결과)
- `admin_users`: 관리자 계정

## 문서

### 핵심 문서
- [**개발 규칙**](./CODING_STANDARDS.md): ⭐ 개발 시 반드시 준수해야 할 규칙
- [**기능 명세서**](./docs/FEATURE_SPECIFICATION.md): 모든 기능의 상세 명세 및 의존성
- [**마이크로서비스 전환 가이드**](./docs/MICROSERVICES_MIGRATION_GUIDE.md): ⭐ 장기 계획 및 전환 전략

### 참고 문서
- [아키텍처 문서](./ARCHITECTURE.md): 시스템 아키텍처 및 설계 원칙
- [운영 가이드](./PRODUCTION_GUIDE.md): 프로덕션 배포 및 운영 가이드
- [개발 로드맵](./docs/ROADMAP.md): 단기/중기/장기 개발 계획
- [마이크로서비스 체크리스트](./docs/MICROSERVICES_CHECKLIST.md): 마이크로서비스 전환 체크리스트

## 주요 기능

### 성능 최적화
- N+1 문제 해결 (Fetch Join)
- 배치 저장 처리
- 캐싱 (질문 데이터)
- 읽기 전용 트랜잭션

### 코드 품질
- Strategy 패턴 (온도 계산)
- Service 인터페이스 분리
- Configuration Properties (설정 외부화)
- 단위 테스트

### 보안
- API Rate Limiting
- 입력 검증 (@Valid)
- 전역 예외 처리

### 모니터링
- Spring Boot Actuator
- Health Check
- Metrics (Prometheus 지원)

### API 문서
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api/api-docs`

## 테스트 자동화

테스트 자동화 가이드는 [테스트 자동화 가이드](./docs/TEST_AUTOMATION_GUIDE.md)를 참고하세요.

- CI/CD 파이프라인 설정 (GitHub Actions)
- 코드 커버리지 (JaCoCo)
- 테스트 실행 자동화

