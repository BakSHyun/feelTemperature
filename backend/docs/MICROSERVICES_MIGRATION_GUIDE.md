# 마이크로서비스 전환 가이드

## 목적
현재 모놀리식 구조에서 마이크로서비스 아키텍처로 전환할 때의 고려사항과 전략을 문서화합니다.

---

## 현재 구조 분석

### 도메인 식별

#### 1. 매칭 도메인 (Matching Domain)
- **책임**: 매칭 생성, 참여 관리
- **Entity**: `Matching`, `Participant`
- **Service**: `MatchingService`
- **Repository**: `MatchingRepository`, `ParticipantRepository`

#### 2. 질문 도메인 (Question Domain)
- **책임**: 질문 및 선택지 관리
- **Entity**: `Question`, `QuestionChoice`
- **Service**: `QuestionService`
- **Repository**: `QuestionRepository`, `QuestionChoiceRepository`

#### 3. 응답 도메인 (Answer Domain)
- **책임**: 사용자 응답 관리
- **Entity**: `Answer`
- **Service**: `AnswerService`
- **Repository**: `AnswerRepository`

#### 4. 기록 도메인 (Record Domain)
- **책임**: 최종 기록 생성 및 관리
- **Entity**: `Record`
- **Service**: `RecordService`
- **Repository**: `RecordRepository`

#### 5. 온도 계산 도메인 (Temperature Calculation Domain)
- **책임**: 온도 계산 알고리즘
- **Strategy**: `TemperatureCalculationStrategy`
- **Properties**: `TemperatureCalculationProperties`

---

## 마이크로서비스 전환 전략

### Phase 1: 준비 단계 (현재 구조 개선)

#### 1.1 도메인 경계 명확화

**현재 상태:**
- `RecordService`가 여러 Repository에 직접 의존
- 도메인 간 결합도 높음

**개선 방향:**
- 도메인별 서비스 인터페이스 명확히 정의
- 서비스 간 통신은 인터페이스를 통해서만
- 직접 Repository 접근 금지

#### 1.2 서비스 간 직접 의존성 제거

**원칙:**
```
❌ Service A → Service B (직접 의존)
✅ Service A → Service B Interface (인터페이스 의존)
✅ Service A → Event (이벤트 기반 통신 준비)
```

**예시:**
```java
// ❌ 나쁜 예: 직접 의존
@Service
public class RecordService {
    private final AnswerService answerService; // 직접 의존
}

// ✅ 좋은 예: 인터페이스 의존
@Service
public class RecordService {
    private final AnswerQueryService answerQueryService; // 인터페이스
}
```

#### 1.3 공유 코드 분리

**현재 공유 코드:**
- DTO (도메인별로 분리 가능)
- 유틸리티 (공유 라이브러리로 분리)
- 예외 클래스 (공유 라이브러리)

**전략:**
- 도메인별 DTO 분리 (필요시)
- 공유 라이브러리 모듈 생성
- 예외 클래스 공유 모듈

---

### Phase 2: 서비스 분리 준비

#### 2.1 이벤트 기반 통신 준비

**이벤트 정의:**
```java
// 향후 구현
public interface DomainEvent {
    String getEventType();
    LocalDateTime getOccurredAt();
}

// 예시 이벤트
public class MatchingEstablishedEvent implements DomainEvent {
    private String matchingCode;
    private Long matchingId;
    // ...
}

public class AnswersSubmittedEvent implements DomainEvent {
    private String participantCode;
    private Long matchingId;
    // ...
}
```

**서비스 간 통신:**
- 현재: 동기 호출 (Service → Service)
- 전환 후: 이벤트 발행/구독

#### 2.2 API 버전 관리

**규칙:**
```java
@RestController
@RequestMapping("/api/v1/matching")  // 버전 명시
public class MatchingController {
    // ...
}
```

**장점:**
- 서비스 독립적 배포
- 하위 호환성 유지
- 점진적 마이그레이션

---

### Phase 3: 마이크로서비스 아키텍처

#### 3.1 서비스 분리 전략

```
현재 (모놀리식)
└── rstracker-backend
    ├── Matching Service
    ├── Question Service
    ├── Answer Service
    ├── Record Service
    └── Temperature Calculation Service

전환 후 (마이크로서비스)
├── matching-service
│   ├── Matching Domain
│   └── Participant Domain
├── question-service
│   ├── Question Domain
│   └── QuestionChoice Domain
├── answer-service
│   └── Answer Domain
├── record-service
│   └── Record Domain
└── temperature-calculation-service
    └── Temperature Calculation Strategy
```

#### 3.2 데이터베이스 분리 (Database per Service)

**원칙:**
- 각 서비스는 자체 데이터베이스 보유
- 서비스 간 직접 DB 접근 금지
- API를 통한 데이터 접근만 허용

**예시:**
```
matching-service
└── matching-db (matchings, participants)

question-service
└── question-db (questions, question_choices)

answer-service
└── answer-db (answers)

record-service
└── record-db (records)
```

#### 3.3 서비스 간 통신

**동기 통신 (API Gateway):**
- 실시간 조회 필요 시
- HTTP/REST 사용
- Circuit Breaker 적용

**비동기 통신 (Event Bus):**
- 이벤트 기반 통신
- Message Queue (RabbitMQ, Kafka)
- Event Sourcing 고려

---

## 현재 코드에서 준비할 사항

### 1. 서비스 인터페이스 명확화

**현재:**
```java
@Service
public class RecordService {
    private final AnswerRepository answerRepository; // 직접 접근
}
```

**개선:**
```java
// AnswerQueryService 인터페이스 정의 (향후 분리 준비)
public interface AnswerQueryService {
    List<Answer> getAnswersByMatching(Long matchingId);
}

@Service
public class RecordService {
    private final AnswerQueryService answerQueryService; // 인터페이스 의존
}
```

### 2. 도메인 이벤트 정의 (향후 구현)

**이벤트 타입:**
- `MatchingCreatedEvent`
- `MatchingEstablishedEvent`
- `AnswersSubmittedEvent`
- `RecordCreatedEvent`

### 3. API 버전 관리

**규칙:**
- 모든 API에 버전 명시 (`/api/v1/...`)
- 버전 변경 시 새 버전 추가 (기존 유지)

### 4. 서비스별 독립적인 설정

**현재:**
- `application.yml`에 모든 설정

**개선:**
- 도메인별 설정 분리 고려
- 환경 변수 활용

---

## 마이크로서비스 전환 체크리스트

### 현재 단계 (모놀리식)

- [x] 도메인 식별 완료
- [x] 서비스 인터페이스 정의 (일부)
- [ ] 서비스 간 직접 의존성 제거
- [ ] 이벤트 기반 통신 준비
- [ ] API 버전 관리
- [ ] 공유 코드 분리

### 전환 전 준비

- [ ] 서비스 인터페이스 명확히 정의
- [ ] 서비스 간 통신을 인터페이스로 변경
- [ ] 이벤트 발행/구독 구조 설계
- [ ] API Gateway 설계
- [ ] 서비스별 데이터베이스 설계
- [ ] 서비스 발견 (Service Discovery) 설계
- [ ] 모니터링 및 로깅 전략

### 전환 후

- [ ] 각 서비스 독립 배포
- [ ] 서비스별 데이터베이스 분리
- [ ] API Gateway 구현
- [ ] 이벤트 버스 구현
- [ ] 분산 추적 시스템 (Zipkin, Jaeger)
- [ ] 서비스 메시 (Service Mesh) 고려

---

## 도메인별 분리 전략

### 1. Matching Service

**책임:**
- 매칭 생성/참여/조회
- 매칭 상태 관리

**데이터:**
- `matchings` 테이블
- `participants` 테이블

**API:**
- `POST /api/v1/matching/create`
- `POST /api/v1/matching/join/{code}`
- `GET /api/v1/matching/{code}`

**이벤트 발행:**
- `MatchingCreatedEvent`
- `MatchingEstablishedEvent`
- `MatchingCompletedEvent`

---

### 2. Question Service

**책임:**
- 질문 및 선택지 관리
- 질문 조회

**데이터:**
- `questions` 테이블
- `question_choices` 테이블

**API:**
- `GET /api/v1/questions`
- `GET /api/v1/questions/{id}`
- `POST /api/v1/questions` (CMS용)

**특징:**
- 거의 독립적인 서비스
- 다른 서비스와 약한 결합

---

### 3. Answer Service

**책임:**
- 응답 저장/조회

**데이터:**
- `answers` 테이블

**API:**
- `POST /api/v1/answers/submit/{participantCode}`
- `GET /api/v1/answers/matching/{matchingId}` (내부 API)

**이벤트 발행:**
- `AnswersSubmittedEvent`

**이벤트 구독:**
- `MatchingEstablishedEvent` (매칭이 성립되면 응답 가능)

---

### 4. Record Service

**책임:**
- 기록 생성/조회

**데이터:**
- `records` 테이블

**API:**
- `POST /api/v1/records/create/{matchingId}`
- `GET /api/v1/records/{recordId}`
- `GET /api/v1/records/matching/{matchingId}`

**의존성:**
- Answer Service (응답 조회)
- Question Service (질문 조회)
- Temperature Calculation Service (온도 계산)

**이벤트 구독:**
- `AnswersSubmittedEvent` (두 참여자 모두 응답 완료 시)

**이벤트 발행:**
- `RecordCreatedEvent`

---

### 5. Temperature Calculation Service

**책임:**
- 온도 계산 알고리즘

**특징:**
- Stateless 서비스
- 계산만 수행

**API:**
- `POST /api/v1/temperature/calculate`

**입력:**
- 응답 데이터
- 질문/선택지 데이터

**출력:**
- 온도 결과

---

## 데이터 일관성 전략

### Saga 패턴

**기록 생성 예시:**
```
1. Record Service: 기록 생성 시작
2. Answer Service: 응답 조회 (동기)
3. Question Service: 질문 조회 (동기)
4. Temperature Service: 온도 계산 (동기)
5. Record Service: 기록 저장
6. Matching Service: 상태 변경 (이벤트)
```

**보상 트랜잭션:**
- 중간 단계 실패 시 롤백
- 이벤트로 상태 복구

---

## 성능 고려사항

### 1. 캐싱 전략

**서비스별 캐싱:**
- Question Service: 질문 데이터 캐싱 (Redis)
- Matching Service: 활성 매칭 캐싱
- Record Service: 기록 조회 캐싱

**분산 캐싱:**
- Redis Cluster 사용
- 캐시 무효화 전략

### 2. 비동기 처리

**기록 생성:**
- 현재: 동기 처리
- 전환 후: 이벤트 기반 비동기 처리

### 3. 데이터 조인

**현재:**
- JOIN 쿼리 사용 가능

**전환 후:**
- 서비스별 데이터 조인 불가
- 애플리케이션 레벨 조인 또는 Materialized View

---

## 모니터링 및 관찰성

### 1. 분산 추적

**도구:**
- Zipkin
- Jaeger
- Spring Cloud Sleuth

**추적 정보:**
- 요청 ID (Trace ID)
- 서비스 간 호출 추적
- 성능 측정

### 2. 로깅

**중앙 집중식 로깅:**
- ELK Stack (Elasticsearch, Logstash, Kibana)
- CloudWatch
- Grafana Loki

**로그 형식:**
- 구조화된 로그 (JSON)
- Trace ID 포함
- 서비스명 포함

### 3. 메트릭

**메트릭:**
- Prometheus + Grafana
- 서비스별 메트릭 수집
- 대시보드 구성

---

## 보안 고려사항

### 1. 서비스 간 인증

**옵션:**
- OAuth 2.0 / JWT
- mTLS (Mutual TLS)
- API Key (내부 서비스 간)

### 2. 네트워크 보안

**서비스 메시:**
- Istio
- Linkerd
- 서비스 간 통신 암호화

---

## 전환 로드맵

### 단계 1: 모놀리식 개선 (1-2개월)
1. 서비스 인터페이스 명확화
2. 서비스 간 직접 의존성 제거
3. 이벤트 기반 통신 구조 설계
4. API 버전 관리 도입

### 단계 2: 준비 (2-3개월)
1. 이벤트 발행/구독 구현
2. API Gateway 프로토타입
3. 서비스별 데이터베이스 설계
4. 모니터링 시스템 구축

### 단계 3: 점진적 분리 (3-6개월)
1. Question Service 분리 (독립적)
2. Matching Service 분리
3. Answer Service 분리
4. Record Service 분리
5. Temperature Calculation Service 분리

### 단계 4: 최적화 (6개월+)
1. 서비스 메시 도입
2. 이벤트 소싱 고려
3. CQRS 패턴 적용 (필요시)
4. 성능 최적화

---

## 참고 자료

### 도메인 주도 설계 (DDD)
- Bounded Context 식별
- Aggregate 설계
- Domain Event 정의

### 마이크로서비스 패턴
- API Gateway Pattern
- Service Discovery Pattern
- Circuit Breaker Pattern
- Saga Pattern
- Event Sourcing
- CQRS

### 도구
- Spring Cloud (Service Discovery, Config, Gateway)
- RabbitMQ / Kafka (Message Queue)
- Redis (캐싱, 분산 락)
- Kubernetes (컨테이너 오케스트레이션)
- Istio / Linkerd (Service Mesh)

---

**마지막 업데이트**: 2024년
**유지보수 담당자**: [담당자 정보]

