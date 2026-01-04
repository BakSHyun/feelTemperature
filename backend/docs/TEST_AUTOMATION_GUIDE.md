# 테스트 자동화 가이드

## 목차
1. [CI/CD 파이프라인 설정](#cicd-파이프라인-설정)
2. [테스트 실행 자동화](#테스트-실행-자동화)
3. [코드 커버리지](#코드-커버리지)
4. [테스트 전략](#테스트-전략)

---

## CI/CD 파이프라인 설정

### GitHub Actions

#### 1. 기본 워크플로우 생성

`.github/workflows/ci.yml` 파일 생성:

```yaml
name: CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
          POSTGRES_DB: relationship_tracker_test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      
      - name: Run tests
        env:
          DB_USERNAME: test
          DB_PASSWORD: test
          DB_URL: jdbc:postgresql://localhost:5432/relationship_tracker_test
        run: mvn clean test
      
      - name: Generate test report
        if: always()
        uses: dorny/test-reporter@v1
        with:
          name: Maven Tests
          path: target/surefire-reports/*.xml
          reporter: java-junit
          fail-on-error: true
```

#### 2. 코드 커버리지 포함 워크플로우

```yaml
name: CI with Coverage

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
          POSTGRES_DB: relationship_tracker_test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      
      - name: Run tests with coverage
        env:
          DB_USERNAME: test
          DB_PASSWORD: test
          DB_URL: jdbc:postgresql://localhost:5432/relationship_tracker_test
        run: mvn clean test jacoco:report
      
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          file: ./target/site/jacoco/jacoco.xml
          flags: unittests
          name: codecov-umbrella
```

---

## 테스트 실행 자동화

### Maven 테스트 실행

#### 기본 테스트 실행
```bash
mvn test
```

#### 특정 테스트 클래스 실행
```bash
mvn test -Dtest=MatchingServiceTest
```

#### 특정 패키지 테스트 실행
```bash
mvn test -Dtest=com.rstracker.service.*Test
```

#### 통합 테스트 실행
```bash
mvn verify
```

### 테스트 프로필 설정

`pom.xml`에 테스트 프로필 추가:

```xml
<profiles>
    <profile>
        <id>test</id>
        <properties>
            <spring.profiles.active>test</spring.profiles.active>
        </properties>
    </profile>
</profiles>
```

`src/test/resources/application-test.yml` 생성:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/relationship_tracker_test
    username: ${DB_USERNAME:test}
    password: ${DB_PASSWORD:test}
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false

logging:
  level:
    com.rstracker: INFO
    org.hibernate.SQL: WARN
```

---

## 코드 커버리지

### JaCoCo 설정

#### 1. pom.xml에 JaCoCo 플러그인 추가

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>jacoco-check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>PACKAGE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.70</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

#### 2. 커버리지 리포트 생성

```bash
mvn clean test jacoco:report
```

리포트 위치: `target/site/jacoco/index.html`

#### 3. 커버리지 체크 실행

```bash
mvn jacoco:check
```

### 커버리지 목표 설정

`pom.xml`에서 커버리지 목표 설정:

```xml
<execution>
    <id>jacoco-check</id>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>PACKAGE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.70</minimum>  <!-- 70% 커버리지 목표 -->
                    </limit>
                    <limit>
                        <counter>BRANCH</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.60</minimum>  <!-- 60% 브랜치 커버리지 -->
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

---

## 테스트 전략

### 단위 테스트 (Unit Test)

#### 목표
- 각 메서드/클래스의 독립적인 동작 검증
- Mock 객체 사용
- 빠른 실행

#### 예시
```java
@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {
    @Mock
    private MatchingRepository matchingRepository;
    
    @InjectMocks
    private MatchingService matchingService;
    
    @Test
    void createMatching_Success() {
        // given
        when(matchingRepository.existsByCode(anyString())).thenReturn(false);
        
        // when
        MatchingDto result = matchingService.createMatching();
        
        // then
        assertThat(result).isNotNull();
        verify(matchingRepository).save(any());
    }
}
```

### 통합 테스트 (Integration Test)

#### 목표
- 여러 컴포넌트 간 상호작용 검증
- 실제 데이터베이스 사용
- @SpringBootTest 사용

#### 예시
```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MatchingIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private MatchingRepository matchingRepository;
    
    @Test
    void createMatching_IntegrationTest() throws Exception {
        // when
        mockMvc.perform(post("/matching/create"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists());
        
        // then
        assertThat(matchingRepository.count()).isEqualTo(1);
    }
}
```

### 테스트 구조

```
src/test/java/com/rstracker/
├── controller/          # Controller 통합 테스트
├── service/            # Service 단위 테스트
├── repository/         # Repository 통합 테스트
└── integration/        # 전체 통합 테스트
```

---

## 테스트 실행 명령어

### 로컬 개발

```bash
# 모든 테스트 실행
mvn test

# 테스트 스킵하고 빌드
mvn package -DskipTests

# 특정 테스트만 실행
mvn test -Dtest=MatchingServiceTest

# 커버리지 리포트 생성
mvn clean test jacoco:report

# 커버리지 체크
mvn jacoco:check
```

### CI/CD에서

```bash
# 테스트 실행
mvn clean test

# 테스트 + 커버리지
mvn clean test jacoco:report

# 테스트 + 커버리지 체크
mvn clean test jacoco:report jacoco:check
```

---

## 베스트 프랙티스

### 1. 테스트 격리
- 각 테스트는 독립적으로 실행 가능해야 함
- `@Transactional` 사용 (롤백 자동)
- `@DirtiesContext` 사용 (컨텍스트 재생성)

### 2. 테스트 데이터
- `@Sql` 어노테이션으로 테스트 데이터 로드
- 또는 `@BeforeEach`에서 데이터 준비

### 3. 테스트 네이밍
- `{메서드명}_{시나리오}_{예상결과}` 형식
- `@DisplayName`으로 한글 설명

### 4. Assertion
- AssertJ 사용 (가독성 좋음)
- 명확한 메시지 제공

### 5. Mock 사용
- 외부 의존성만 Mock
- 내부 로직은 실제 구현 사용

---

## 테스트 커버리지 목표

### 권장 커버리지
- **전체**: 70% 이상
- **Service 레이어**: 80% 이상
- **Controller 레이어**: 60% 이상
- **Repository 레이어**: 70% 이상

### 커버리지 측정 항목
- **Line Coverage**: 코드 라인 커버리지
- **Branch Coverage**: 분기(if/else) 커버리지
- **Method Coverage**: 메서드 커버리지

---

## CI/CD 통합 예시

### GitHub Actions 전체 워크플로우

`.github/workflows/ci.yml`:

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
          POSTGRES_DB: relationship_tracker_test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      
      - name: Run tests
        env:
          DB_USERNAME: test
          DB_PASSWORD: test
          DB_URL: jdbc:postgresql://localhost:5432/relationship_tracker_test
        run: mvn clean test jacoco:report
      
      - name: Check coverage
        run: mvn jacoco:check
      
      - name: Upload coverage reports
        uses: codecov/codecov-action@v3
        with:
          file: ./target/site/jacoco/jacoco.xml
      
      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: test-results
          path: target/surefire-reports
      
      - name: Upload coverage report
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: coverage-report
          path: target/site/jacoco
```

---

## 참고 자료

- [JUnit 5 공식 문서](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito 공식 문서](https://site.mockito.org/)
- [JaCoCo 공식 문서](https://www.jacoco.org/jacoco/trunk/doc/)
- [Spring Boot Testing 공식 문서](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [GitHub Actions 문서](https://docs.github.com/en/actions)

---

**마지막 업데이트**: 2024년

