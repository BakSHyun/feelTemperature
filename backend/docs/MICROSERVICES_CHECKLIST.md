# 마이크로서비스 전환 체크리스트

코드를 작성/수정할 때 마이크로서비스 전환을 고려한 체크리스트입니다.

## 개발 시 확인 사항

### 서비스 간 의존성
- [ ] 다른 도메인의 Repository 직접 접근하지 않음
- [ ] 서비스 간 통신은 인터페이스를 통해서만
- [ ] Service 인터페이스 명확히 정의

### 도메인 경계
- [ ] 도메인별 책임 명확
- [ ] 도메인 간 결합도 최소화
- [ ] 공유 코드 최소화

### API 설계
- [ ] API 버전 관리 고려 (`/api/v1/...`)
- [ ] RESTful 원칙 준수
- [ ] 서비스별 독립적인 API

### 데이터 접근
- [ ] 서비스별 데이터베이스 분리 가능성 고려
- [ ] JOIN 쿼리 최소화 (서비스 분리 시 어려움)
- [ ] 데이터 일관성 전략 고려

### 이벤트 기반 통신
- [ ] 이벤트 발행/구독 구조 고려
- [ ] 비동기 처리 가능성 검토

---

## 코드 작성 시 원칙

### ✅ 좋은 예
```java
// 서비스 인터페이스를 통한 의존
@Service
public class RecordService {
    private final AnswerQueryService answerQueryService; // 인터페이스
}

// API 버전 포함
@RestController
@RequestMapping("/api/v1/matching")
```

### ❌ 나쁜 예
```java
// 다른 도메인의 Repository 직접 접근
@Service
public class RecordService {
    private final AnswerRepository answerRepository; // 직접 접근 금지
}

// 버전 없는 API
@RestController
@RequestMapping("/matching") // 버전 명시 필요
```

---

## 참고 문서
- [마이크로서비스 전환 가이드](./MICROSERVICES_MIGRATION_GUIDE.md)
- [개발 규칙](../CODING_STANDARDS.md) - 확장성 고려 규칙

