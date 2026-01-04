# 개발 규칙 (Coding Standards)

## 목적
일관된 코드 품질을 유지하고, 유지보수성, 확장성, 성능을 보장하기 위한 개발 규칙입니다.

---

## 1. 일반 원칙

### 1.1 코드 품질 기준
- **명확성**: 코드만 봐도 의도가 명확해야 함
- **간결성**: 불필요한 복잡성 제거
- **일관성**: 프로젝트 전반에 걸친 일관된 스타일
- **테스트 가능성**: 단위 테스트 작성 가능한 구조

### 1.2 금지 사항
- ❌ 하드코딩된 값 (상수 클래스 또는 설정 파일 사용)
- ❌ 중복 코드 (재사용 가능한 메서드/클래스로 분리)
- ❌ 긴 메서드 (20줄 이상 시 분리 고려)
- ❌ 깊은 중첩 (3단계 이상 시 리팩토링)
- ❌ 예외 무시 (catch 블록에서 로그 남기거나 처리)
- ❌ System.out.println 사용 (로그 프레임워크 사용)

---

## 2. 패키지 구조 및 네이밍

### 2.1 패키지 구조
```
com.rstracker
├── config/          # 설정 클래스
├── constants/       # 상수
├── controller/      # REST API 컨트롤러
├── dto/            # 데이터 전송 객체
├── entity/         # JPA 엔티티
├── exception/      # 예외 처리
├── mapper/         # Entity ↔ DTO 변환
├── repository/     # 데이터 접근 레이어
├── service/        # 비즈니스 로직
└── util/           # 유틸리티 클래스
```

### 2.2 네이밍 규칙

#### 클래스
- **Service**: `{Domain}Service` (예: `MatchingService`)
- **Controller**: `{Domain}Controller` (예: `MatchingController`)
- **Repository**: `{Entity}Repository` (예: `MatchingRepository`)
- **DTO**: `{Domain}{Purpose}Dto` (예: `MatchingDto`, `AnswerCreateDto`)
- **Entity**: 단수형 명사 (예: `Matching`, `Question`)
- **Exception**: `{Purpose}Exception` (예: `BusinessException`)
- **Mapper**: `{Domain}Mapper` (예: `MatchingMapper`)
- **Util**: `{Purpose}Generator` 또는 `{Purpose}Util` (예: `MatchingCodeGenerator`)

#### 메서드
- **조회**: `get{Entity}`, `find{Entity}`, `list{Entity}` (예: `getMatching`, `findByCode`)
- **생성**: `create{Entity}` (예: `createMatching`)
- **수정**: `update{Entity}`, `{action}{Entity}` (예: `updateQuestion`, `deactivateRecord`)
- **삭제**: `delete{Entity}`, `remove{Entity}`
- **Boolean 반환**: `is{Property}`, `has{Property}`, `exists{Entity}` (예: `isActive`)

#### 변수
- **camelCase** 사용
- **명확한 의미**: 축약어 지양 (예: `participantCount` O, `pcnt` X)
- **컬렉션**: 복수형 사용 (예: `answers`, `questions`)
- **Boolean**: `is`, `has`, `should` 접두사 (예: `isActive`, `hasPermission`)

---

## 3. 레이어별 규칙

### 3.1 Controller 레이어

#### 책임
- HTTP 요청/응답 처리
- 입력 검증 (`@Valid` 사용)
- DTO 변환

#### 규칙
```java
@RestController
@RequestMapping("/api/{domain}")
@RequiredArgsConstructor
public class MatchingController {
    
    private final MatchingService matchingService;
    
    @PostMapping("/create")
    public ResponseEntity<MatchingDto> createMatching() {
        // 1. Service 호출만 (비즈니스 로직 없음)
        MatchingDto result = matchingService.createMatching();
        
        // 2. 성공 응답 반환
        return ResponseEntity.ok(result);
    }
}
```

#### 주의사항
- ❌ 비즈니스 로직 작성 금지
- ❌ Repository 직접 접근 금지
- ✅ 모든 입력값 `@Valid` 검증
- ✅ 전역 예외 핸들러 활용 (try-catch 불필요)

---

### 3.2 Service 레이어

#### 책임
- 비즈니스 로직 구현
- 트랜잭션 관리
- 여러 Repository 조율

#### 규칙
```java
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService implements MatchingServiceInterface {
    
    private final MatchingRepository matchingRepository;
    private final MatchingMapper matchingMapper;
    
    @Transactional
    public MatchingDto createMatching() {
        log.debug("Creating new matching");
        
        // 1. 비즈니스 로직
        // 2. Repository 호출
        // 3. 로깅
        // 4. DTO 변환 (Mapper 사용)
        
        log.info("Matching created: {}", code);
        return matchingMapper.toDto(matching);
    }
    
    @Transactional(readOnly = true)
    public MatchingDto getMatching(String code) {
        // 읽기 전용 트랜잭션 명시
    }
}
```

#### 주의사항
- ✅ 모든 Service는 인터페이스 구현 (테스트 가능성)
- ✅ 트랜잭션 범위 명시 (`@Transactional`)
- ✅ 읽기 전용은 `@Transactional(readOnly = true)`
- ✅ 로깅 필수 (`@Slf4j`, `log.debug/info/warn/error`)
- ✅ 예외는 커스텀 예외 사용 (`BusinessException`, `ResourceNotFoundException`)

---

### 3.3 Repository 레이어

#### 책임
- 데이터 접근
- 쿼리 실행

#### 규칙
```java
@Repository
public interface MatchingRepository extends JpaRepository<Matching, Long> {
    // 1. Spring Data JPA 메서드 네이밍
    Optional<Matching> findByCode(String code);
    
    // 2. 복잡한 쿼리는 @Query 사용
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.choices WHERE q.isActive = true")
    List<Question> findActiveWithChoices();
}
```

#### 주의사항
- ✅ N+1 문제 방지 (Fetch Join 사용)
- ✅ Optional 반환 (null 안전성)
- ✅ 쿼리 성능 고려 (인덱스 활용)

---

### 3.4 Entity 레이어

#### 규칙
```java
@Entity
@Table(name = "matchings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Matching {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 10)
    private String code;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "matching", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();
}
```

#### 주의사항
- ✅ Lombok 사용 (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`)
- ✅ JPA 어노테이션 명확히 설정
- ✅ 연관 관계 설정 시 양방향/단방향 고려
- ✅ CascadeType 신중히 선택

---

## 4. 성능 최적화 규칙

### 4.1 데이터베이스 접근

#### 필수
- ✅ N+1 문제 해결 (Fetch Join 사용)
- ✅ 배치 처리 (`saveAll()`, `batch_size` 설정)
- ✅ 읽기 전용 트랜잭션 (`@Transactional(readOnly = true)`)
- ✅ 인덱스 활용 (자주 조회되는 컬럼)

#### 예시
```java
// ❌ 나쁜 예: N+1 문제
List<Question> questions = questionRepository.findAll();
questions.forEach(q -> q.getChoices().size()); // 각 질문마다 쿼리 실행

// ✅ 좋은 예: Fetch Join
@Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.choices")
List<Question> findAllWithChoices();
```

### 4.2 캐싱

#### 규칙
- ✅ 자주 조회되고 변경이 적은 데이터 캐싱
- ✅ 캐시 키 명확히 설정
- ✅ 캐시 무효화 전략 수립

#### 예시
```java
@Cacheable(value = "questions", key = "'active'")
public List<QuestionDto> getActiveQuestions() {
    // 캐싱 대상
}

@CacheEvict(value = "questions", allEntries = true)
public void updateQuestion(Long id, QuestionUpdateDto dto) {
    // 캐시 무효화
}
```

### 4.3 트랜잭션 최적화

#### 규칙
- ✅ 트랜잭션 범위 최소화
- ✅ 읽기 전용 명시
- ✅ 데드락 방지 (락 순서 일관성)

---

## 5. 예외 처리 규칙

### 5.1 예외 클래스

#### 커스텀 예외 사용
```java
// 비즈니스 예외
throw new BusinessException("이미 종료된 매칭입니다");

// 리소스 없음 예외
throw new ResourceNotFoundException("매칭을 찾을 수 없습니다: " + code);
```

#### 예외 계층
```
RuntimeException
├── BusinessException          # 비즈니스 규칙 위반
└── ResourceNotFoundException  # 리소스 없음
```

### 5.2 예외 처리

#### Controller
```java
// ❌ 나쁜 예: try-catch 사용
try {
    return matchingService.createMatching();
} catch (Exception e) {
    return ResponseEntity.badRequest().build();
}

// ✅ 좋은 예: 전역 예외 핸들러 사용
public ResponseEntity<MatchingDto> createMatching() {
    return ResponseEntity.ok(matchingService.createMatching());
}
```

#### Service
```java
// ✅ 좋은 예: 명확한 예외 메시지
Matching matching = matchingRepository.findByCode(code)
    .orElseThrow(() -> new ResourceNotFoundException("매칭을 찾을 수 없습니다: " + code));
```

---

## 6. 주석 규칙

### 6.1 JavaDoc 주석

#### 클래스
```java
/**
 * 매칭 서비스
 * 매칭 생성, 참여, 조회 기능을 제공합니다.
 */
@Service
public class MatchingService {
}
```

#### 메서드
```java
/**
 * 매칭을 생성합니다.
 * 
 * @return 생성된 매칭 정보
 * @throws BusinessException 매칭 코드 생성 실패 시
 */
public MatchingDto createMatching() {
}
```

#### 복잡한 로직
```java
// 참여자별 온도 계산
// Q3 (분위기): 가중치 3.0
// Q4 (기대): 가중치 2.0
// Q5 (거리): 가중치 3.0
// Q6 (편안함): 가중치 2.0
Map<Long, ParticipantTemperature> participantTemps = ...;
```

### 6.2 인라인 주석

#### 규칙
- ✅ 복잡한 로직 설명
- ✅ 비즈니스 규칙 설명
- ✅ TODO/FIXME 사용 시 상세 설명
- ❌ 코드를 반복 설명 (코드 자체가 명확하면 불필요)

#### 예시
```java
// ✅ 좋은 예: 비즈니스 규칙 설명
// 참여자 수가 2명이 되면 상태 변경 (ESTABLISHED)
if (participantCount == AppConstants.MAX_PARTICIPANTS_PER_MATCHING) {
    matching.setStatus(MatchingStatus.ESTABLISHED.getValue());
}

// ❌ 나쁜 예: 코드 반복
// matching의 status를 ESTABLISHED로 설정
matching.setStatus(MatchingStatus.ESTABLISHED.getValue());
```

---

## 7. 테스트 규칙

### 7.1 단위 테스트

#### 필수
- ✅ 모든 Service 레이어 테스트
- ✅ 복잡한 비즈니스 로직 테스트
- ✅ 예외 케이스 테스트

#### 예시
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("MatchingService 테스트")
class MatchingServiceTest {
    
    @Mock
    private MatchingRepository matchingRepository;
    
    @InjectMocks
    private MatchingService matchingService;
    
    @Test
    @DisplayName("매칭 생성 성공")
    void createMatching_Success() {
        // given
        when(matchingRepository.existsByCode(anyString())).thenReturn(false);
        
        // when
        MatchingDto result = matchingService.createMatching();
        
        // then
        assertThat(result).isNotNull();
        verify(matchingRepository, times(1)).save(any());
    }
}
```

### 7.2 테스트 네이밍

#### 규칙
- 메서드명: `{메서드명}_{시나리오}_{예상결과}` (예: `createMatching_Success`)
- `@DisplayName`: 한글로 명확한 설명

---

## 8. TODO 및 FIXME 규칙

### 8.1 TODO
```java
// TODO: [작성자] [날짜] [설명]
// TODO: 2024-01-01 Redis 캐싱 도입
```

#### 규칙
- ✅ 구체적인 설명
- ✅ 우선순위 표시 (필요시)
- ✅ 마일스톤/이슈 트래커 연동 (필요시)

### 8.2 FIXME
```java
// FIXME: [문제점] [해결 방안] [우선순위]
// FIXME: 성능 이슈 - N+1 문제 발생, Fetch Join 적용 필요 (높음)
```

#### 규칙
- ✅ 문제점 명확히 설명
- ✅ 해결 방안 제시
- ✅ 우선순위 표시

---

## 9. 설정 및 상수 규칙

### 9.1 상수 클래스
```java
public class AppConstants {
    public static final int MAX_PARTICIPANTS_PER_MATCHING = 2;
    
    private AppConstants() {
        // 인스턴스화 방지
    }
}
```

#### 규칙
- ✅ 매직 넘버/문자열 상수 클래스로 분리
- ✅ private 생성자로 인스턴스화 방지
- ✅ 명확한 네이밍

### 9.2 Configuration Properties
```java
@ConfigurationProperties(prefix = "app.temperature-calculation")
public class TemperatureCalculationProperties {
    private Map<Integer, Double> questionWeights = new HashMap<>();
}
```

#### 규칙
- ✅ 환경별 설정 가능한 값은 Properties 사용
- ✅ 기본값 제공 (하위 호환성)

---

## 10. 로깅 규칙

### 10.1 로그 레벨

#### 규칙
- **DEBUG**: 개발/디버깅 정보
- **INFO**: 중요한 비즈니스 이벤트 (생성, 수정, 삭제)
- **WARN**: 예상치 못한 상황 (예외 가능성)
- **ERROR**: 에러 발생

#### 예시
```java
@Slf4j
@Service
public class MatchingService {
    
    public MatchingDto createMatching() {
        log.debug("Creating new matching"); // 디버깅 정보
        
        // ... 비즈니스 로직 ...
        
        log.info("Matching created: {}", code); // 중요한 이벤트
        
        return result;
    }
    
    public MatchingDto getMatching(String code) {
        Matching matching = matchingRepository.findByCode(code)
            .orElseThrow(() -> {
                log.warn("Matching not found: {}", code); // 예상치 못한 상황
                return new ResourceNotFoundException("매칭을 찾을 수 없습니다");
            });
        return result;
    }
}
```

### 10.2 로그 포맷

#### 규칙
- ✅ 파라미터 사용 (`{}` 플레이스홀더)
- ✅ 민감 정보 제외 (비밀번호, 토큰 등)
- ✅ 구조화된 정보 (키-값 쌍)

---

## 11. 확장성 고려 규칙

### 11.1 디자인 패턴

#### 필수 패턴
- ✅ **Strategy 패턴**: 알고리즘 교체 가능
- ✅ **Repository 패턴**: 데이터 접근 추상화
- ✅ **DTO 패턴**: Entity와 API 분리
- ✅ **Mapper 패턴**: 변환 로직 분리

### 11.2 인터페이스 사용

#### 규칙
- ✅ Service 레이어는 인터페이스 구현
- ✅ Strategy 패턴 사용 시 인터페이스 정의

### 11.3 마이크로서비스 전환 준비

#### 원칙 (중요!)
마이크로서비스 아키텍처로 전환을 고려하여 다음 원칙을 준수합니다:

**서비스 간 의존성:**
```java
// ❌ 나쁜 예: 다른 서비스의 Repository 직접 접근
@Service
public class RecordService {
    private final AnswerRepository answerRepository; // 직접 접근
}

// ✅ 좋은 예: 서비스 인터페이스를 통한 의존
@Service
public class RecordService {
    private final AnswerQueryService answerQueryService; // 인터페이스
}

// ✅ 더 좋은 예: 이벤트 기반 통신 (향후)
@Service
public class RecordService {
    private final EventPublisher eventPublisher;
    
    public void createRecord(Long matchingId) {
        // 이벤트 발행하여 데이터 요청
    }
}
```

**도메인 경계:**
- ✅ 도메인별 서비스 인터페이스 명확히 정의
- ✅ 다른 도메인의 Repository 직접 접근 금지
- ✅ 서비스 간 통신은 인터페이스를 통해서만
- ✅ 이벤트 기반 통신 구조 고려 (향후 전환)

**API 버전 관리:**
```java
// ✅ API 버전 명시 (마이크로서비스 전환 시 필수)
@RestController
@RequestMapping("/api/v1/matching")  // 버전 포함
public class MatchingController {
}
```

**공유 코드:**
- ✅ 공유 코드는 별도 모듈로 분리 고려
- ✅ 도메인별 DTO 분리 (필요시)
- ✅ 예외 클래스는 공유 라이브러리 고려

**참고 문서:**
- [마이크로서비스 전환 가이드](./docs/MICROSERVICES_MIGRATION_GUIDE.md)

---

## 12. 보안 규칙

### 12.1 입력 검증

#### 필수
- ✅ 모든 입력값 `@Valid` 검증
- ✅ DTO 레벨 검증 (`@NotNull`, `@NotEmpty` 등)
- ✅ SQL Injection 방지 (JPA 사용)

### 12.2 Rate Limiting

#### 규칙
- ✅ API Rate Limiting 적용
- ✅ 엔드포인트별 제한 설정

---

## 13. 코드 리뷰 체크리스트

### 필수 확인 사항
- [ ] 하드코딩 없음
- [ ] 중복 코드 없음
- [ ] 예외 처리 적절
- [ ] 로깅 추가
- [ ] 단위 테스트 작성
- [ ] N+1 문제 없음
- [ ] 트랜잭션 범위 적절
- [ ] 읽기 전용 트랜잭션 명시
- [ ] 주석 적절 (필요시)
- [ ] 네이밍 규칙 준수

---

## 14. 문서화 규칙

### 14.1 코드 문서화

#### 필수
- ✅ 복잡한 비즈니스 로직 JavaDoc
- ✅ Public API JavaDoc
- ✅ README 업데이트 (주요 변경 시)

### 14.2 기능 문서화

#### 필수
- ✅ 기능 명세서 업데이트 (FEATURE_SPECIFICATION.md)
- ✅ API 변경 시 문서 업데이트

---

## 15. 커밋 메시지 규칙

### 형식
```
[타입] 간단한 설명

상세 설명 (필요시)

- 변경 사항 1
- 변경 사항 2
```

### 타입
- `feat`: 새로운 기능
- `fix`: 버그 수정
- `refactor`: 리팩토링
- `perf`: 성능 개선
- `docs`: 문서 수정
- `test`: 테스트 추가/수정
- `chore`: 빌드/설정 변경

### 예시
```
[feat] 매칭 생성 API 추가

- MatchingService.createMatching() 구현
- MatchingCodeGenerator 유틸리티 추가
- 단위 테스트 추가
```

---

## 16. 코드 품질 도구

### 필수 도구
- ✅ Lombok (보일러플레이트 제거)
- ✅ SLF4J (로깅)
- ✅ Spring Boot Validation (입력 검증)

### 권장 도구
- SonarQube (코드 품질 분석)
- SpotBugs (버그 탐지)
- Checkstyle (코드 스타일)

---

## 17. 비상 규칙

### 절대 금지
- ❌ 프로덕션 코드에 System.out.println
- ❌ 예외 무시 (빈 catch 블록)
- ❌ 비밀번호/토큰 하드코딩
- ❌ SQL 문자열 직접 작성 (JPA 사용)
- ❌ 트랜잭션 없이 데이터 변경

### 즉시 수정 필요
- 🚨 N+1 문제 발견
- 🚨 메모리 누수 의심
- 🚨 보안 취약점
- 🚨 성능 저하 (응답 시간 1초 이상)

---

## 18. 예외 케이스

### 특수 상황 처리 규칙
- ✅ 모든 예외는 커스텀 예외 사용
- ✅ 사용자에게 명확한 에러 메시지
- ✅ 로그에 상세 정보 기록

---

**마지막 업데이트**: 2024년
**규칙 유지보수 담당자**: [담당자 정보]
**위반 시**: 코드 리뷰 반려

