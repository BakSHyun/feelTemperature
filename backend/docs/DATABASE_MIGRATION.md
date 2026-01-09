# 데이터베이스 마이그레이션 가이드

## 개요

Laravel의 `php artisan migrate`와 유사하게, Spring Boot에서는 **Flyway** 또는 **Liquibase**를 사용하여 데이터베이스 마이그레이션을 관리할 수 있습니다.

## 현재 상태

현재 프로젝트는 **JPA의 `ddl-auto: update`**를 사용하고 있습니다.

**장점**:
- 간단하고 빠른 설정
- Entity 변경 시 자동으로 테이블 생성/수정

**단점**:
- 프로덕션 환경에 부적합 (데이터 손실 위험)
- 버전 관리가 불가능
- 롤백 불가능
- 마이그레이션 히스토리 관리 불가

**권장**: Flyway 또는 Liquibase로 전환 권장

---

## Flyway (추천) ⭐

Laravel의 migrate와 가장 유사한 도구입니다.

### 특징

- ✅ 간단하고 직관적
- ✅ SQL 스크립트 기반 (SQL 파일만 작성하면 됨)
- ✅ 버전 관리 (V1__Create_table.sql 형식)
- ✅ 자동 실행 (애플리케이션 시작 시)
- ✅ 마이그레이션 히스토리 테이블 자동 생성

### 설치

`pom.xml`에 의존성 추가:

```xml
<dependencies>
    <!-- Flyway -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-database-postgresql</artifactId>
    </dependency>
</dependencies>
```

### 디렉토리 구조

```
src/main/resources/
  └── db/
      └── migration/
          ├── V1__Create_initial_schema.sql
          ├── V2__Add_indexes.sql
          ├── V3__Add_initial_data.sql
          └── V4__Add_new_column.sql
```

**파일 명명 규칙**:
- `V{version}__{description}.sql`
- 예: `V1__Create_tables.sql`, `V2__Add_indexes.sql`

### 설정

`application.yml`:

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true  # 기존 DB가 있어도 시작 가능
    validate-on-migrate: true
```

### 사용 예시

#### V1__Create_initial_schema.sql

```sql
-- V1__Create_initial_schema.sql
CREATE TABLE matchings (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    question_text TEXT NOT NULL,
    "order" INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

#### V2__Add_indexes.sql

```sql
-- V2__Add_indexes.sql
CREATE INDEX idx_matchings_code ON matchings(code);
CREATE INDEX idx_questions_order ON questions("order");
```

### 실행

```bash
# 애플리케이션 시작 시 자동 실행
mvn spring-boot:run

# 또는 Flyway Maven 플러그인으로 직접 실행
mvn flyway:migrate
```

### Flyway 명령어 (Maven 플러그인)

```xml
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <configuration>
        <url>jdbc:postgresql://localhost:5432/relationship_tracker</url>
        <user>postgres</user>
        <password>password</password>
    </configuration>
</plugin>
```

```bash
# 마이그레이션 실행
mvn flyway:migrate

# 상태 확인
mvn flyway:info

# 롤백 (Enterprise 버전만 가능)
mvn flyway:undo
```

---

## Liquibase

Flyway보다 더 유연하지만 설정이 복잡합니다.

### 특징

- ✅ XML, YAML, JSON, SQL 모두 지원
- ✅ 데이터 변환 기능
- ✅ 롤백 스크립트 지원 (무료 버전)
- ✅ 변경 세트 기반 관리

### 설치

```xml
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
```

### 설정

```yaml
spring:
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.yaml
```

---

## 현재 프로젝트에 Flyway 적용하기

### 1단계: 의존성 추가

`pom.xml`:

```xml
<dependencies>
    <!-- Flyway -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-database-postgresql</artifactId>
    </dependency>
</dependencies>
```

### 2단계: 디렉토리 생성

```bash
mkdir -p src/main/resources/db/migration
```

### 3단계: 기존 schema.sql을 마이그레이션 파일로 변환

`database/schema.sql`을 `src/main/resources/db/migration/V1__Create_initial_schema.sql`로 복사

### 4단계: application.yml 설정 변경

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # update → validate로 변경
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

### 5단계: 테스트

```bash
mvn spring-boot:run
```

---

## Flyway vs Liquibase 비교

| 특징 | Flyway | Liquibase |
|------|--------|-----------|
| 학습 곡선 | 낮음 | 중간 |
| SQL 지원 | ✅ | ✅ |
| XML/YAML | ❌ | ✅ |
| 롤백 (무료) | ❌ | ✅ |
| 간단함 | ⭐⭐⭐ | ⭐⭐ |
| 유연성 | ⭐⭐ | ⭐⭐⭐ |

**추천**: 대부분의 프로젝트에는 **Flyway**가 적합합니다.

---

## Laravel vs Spring Boot 비교

| 기능 | Laravel | Spring Boot (Flyway) |
|------|---------|---------------------|
| 마이그레이션 실행 | `php artisan migrate` | `mvn flyway:migrate` (또는 자동) |
| 마이그레이션 생성 | `php artisan make:migration` | 수동으로 파일 생성 |
| 롤백 | `php artisan migrate:rollback` | Flyway Enterprise만 (무료: 불가) |
| 마이그레이션 상태 | `php artisan migrate:status` | `mvn flyway:info` |
| 마이그레이션 파일 | `database/migrations/` | `src/main/resources/db/migration/` |
| 파일 명명 | `YYYY_MM_DD_HHMMSS_description.php` | `V{version}__{description}.sql` |

---

## 권장 사항

### 개발 단계
- **Flyway 사용** (버전 관리 및 히스토리 추적)
- `ddl-auto: validate` 설정

### 프로덕션
- **Flyway 필수** (데이터 손실 방지)
- `ddl-auto: none` 또는 `validate` 설정
- 마이그레이션 테스트 후 배포

---

## 현재 프로젝트 상태

**현재**: `ddl-auto: update` 사용 중

**권장**: Flyway로 전환

**전환 시점**:
- 프로덕션 배포 전
- 팀 개발 시작 전
- 데이터베이스 스키마가 복잡해지기 전

---

## 참고 자료

- [Flyway 공식 문서](https://flywaydb.org/documentation/)
- [Spring Boot Flyway 가이드](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)
- [Liquibase 공식 문서](https://docs.liquibase.com/)

---

**최종 업데이트**: 2026-01-04

