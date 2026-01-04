# 아키텍처 문서

## 개요

이 문서는 관계 상태 기록 도구 백엔드의 아키텍처와 설계 원칙을 설명합니다.

## 설계 원칙

### 1. 계층 분리 (Layered Architecture)

```
Controller Layer (API 엔드포인트)
    ↓
Service Layer (비즈니스 로직)
    ↓
Repository Layer (데이터 접근)
    ↓
Entity Layer (도메인 모델)
```

- **Controller**: HTTP 요청/응답 처리, 검증
- **Service**: 비즈니스 로직, 트랜잭션 관리
- **Repository**: 데이터 접근, 쿼리 실행
- **Entity**: 도메인 모델, 데이터 매핑

### 2. 인터페이스 분리

서비스 레이어에 인터페이스를 도입하여:
- 테스트 가능성 향상 (Mock 객체 주입)
- 구현체 교체 용이
- 계약(Contract) 명확화

예시: `MatchingServiceInterface`

### 3. Strategy 패턴

온도 계산 알고리즘을 Strategy 패턴으로 구현:
- `TemperatureCalculationStrategy` 인터페이스
- `WeightedTemperatureCalculationStrategy` 구현체
- 새로운 계산 방식 추가 용이

### 4. Configuration Properties

하드코딩된 값들을 설정 파일로 분리:
- `TemperatureCalculationProperties`: 온도 계산 가중치
- `application.yml`에서 관리
- 런타임 변경 가능 (환경별 설정)

## 패키지 구조

```
com.rstracker
├── config/              # 설정 클래스
│   ├── CacheConfig
│   ├── RateLimitConfig
│   ├── TemperatureCalculationProperties
│   └── WebConfig
├── constants/           # 상수
│   └── AppConstants
├── controller/          # REST API 컨트롤러
├── dto/                 # 데이터 전송 객체
├── entity/              # JPA 엔티티
├── exception/           # 예외 처리
│   ├── BusinessException
│   ├── ResourceNotFoundException
│   └── GlobalExceptionHandler
├── mapper/              # Entity ↔ DTO 변환
├── repository/          # 데이터 접근 레이어
├── service/             # 비즈니스 로직
│   ├── temperature/     # 온도 계산 전략
│   └── *ServiceInterface (인터페이스)
└── util/                # 유틸리티 클래스
```

## 주요 디자인 패턴

### 1. Repository 패턴
- 데이터 접근 로직 추상화
- JPA Repository 활용
- 쿼리 메서드 명명 규칙 준수

### 2. DTO 패턴
- Entity와 API 응답 분리
- 불필요한 데이터 노출 방지
- API 버전 관리 용이

### 3. Mapper 패턴
- Entity ↔ DTO 변환 로직 분리
- 재사용성 향상
- 변환 로직 테스트 용이

### 4. Strategy 패턴
- 알고리즘 교체 가능
- 확장성 향상
- 단위 테스트 용이

## 데이터 흐름

### 매칭 생성 플로우

```
1. Client → Controller
   POST /api/matching/create

2. Controller → Service
   MatchingService.createMatching()

3. Service → Repository
   MatchingRepository.save()

4. Repository → Database
   INSERT INTO matchings

5. Database → Repository → Service → Controller → Client
   MatchingDto 반환
```

### 기록 생성 플로우

```
1. Client → Controller
   POST /api/records/create/{matchingId}

2. Controller → Service
   RecordService.createRecord()

3. Service → Repository
   - AnswerRepository.findByMatchingId()
   - QuestionRepository.findAllWithChoices()
   - QuestionChoiceRepository.findAllWithQuestions()

4. Service → Strategy
   TemperatureCalculationStrategy.calculate()

5. Service → Repository
   RecordRepository.save()

6. Database → Repository → Service → Controller → Client
   RecordDto 반환
```

## 성능 최적화 전략

### 1. 쿼리 최적화

#### Fetch Join
- N+1 문제 해결
- `@Query`에서 `LEFT JOIN FETCH` 사용
- 예: `findAllWithChoices()`, `findActiveWithChoices()`

#### 배치 처리
- `saveAll()` 사용
- JPA 배치 사이즈 설정: `batch_size: 20`

#### 인덱스
- 자주 조회되는 컬럼에 인덱스
- 예: `matchings.code`, `records.record_id`

### 2. 캐싱

#### 현재 구현
- 인메모리 캐싱 (ConcurrentMapCacheManager)
- 질문 데이터 캐싱

#### 향후 개선
- Redis 캐싱
- 분산 환경 지원

### 3. 연결 풀

- HikariCP 사용
- 프로덕션: maximum-pool-size 조정
- 모니터링: Actuator 메트릭

## 확장성 고려

### 1. 수평 확장
- Stateless 애플리케이션
- 로드 밸런서를 통한 다중 인스턴스

### 2. 데이터베이스 확장
- 읽기 복제 (Read Replica)
- 파티셔닝 (대용량 데이터)

### 3. 비동기 처리
- 큐 시스템 도입 가능
- 기록 생성 비동기화

## 보안

### 1. 입력 검증
- `@Valid`, `@NotNull` 사용
- DTO 레벨 검증

### 2. Rate Limiting
- Bucket4j 사용
- API 엔드포인트별 제한

### 3. 예외 처리
- 전역 예외 핸들러
- 민감 정보 노출 방지

## 테스트 전략

### 1. 단위 테스트
- Service 레이어: Mockito 사용
- Strategy: 순수 Java 테스트
- Repository: @DataJpaTest (추가 권장)

### 2. 통합 테스트
- @SpringBootTest 사용
- Testcontainers (추가 권장)

### 3. 테스트 커버리지
- 목표: 70% 이상
- 핵심 비즈니스 로직: 90% 이상

## 모니터링

### 1. Actuator
- Health Check
- Metrics
- Prometheus 연동

### 2. 로깅
- 구조화된 로깅
- 로그 레벨 관리
- 에러 로그 집중 모니터링

## 유지보수성

### 1. 코드 가독성
- 명확한 네이밍
- 주석 (필요시)
- 일관된 코딩 스타일

### 2. 문서화
- README.md
- API 문서 (Swagger 권장)
- 아키텍처 문서 (이 문서)

### 3. 리팩토링
- 정기적인 코드 리뷰
- 기술 부채 관리
- 최신 패턴 적용

---

**마지막 업데이트**: 2024년

