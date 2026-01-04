# 운영 가이드 (Production Guide)

## 📋 목차
1. [배포 전 체크리스트](#배포-전-체크리스트)
2. [환경 변수 설정](#환경-변수-설정)
3. [데이터베이스 설정](#데이터베이스-설정)
4. [성능 최적화](#성능-최적화)
5. [모니터링 및 로깅](#모니터링-및-로깅)
6. [보안 고려사항](#보안-고려사항)
7. [장애 대응](#장애-대응)
8. [확장성 고려사항](#확장성-고려사항)

---

## 배포 전 체크리스트

### 필수 항목
- [ ] 환경 변수 설정 완료 (DB, Secret Key 등)
- [ ] 데이터베이스 백업 계획 수립
- [ ] 로깅 레벨 설정 (프로덕션: INFO 이상)
- [ ] SSL/TLS 인증서 설정
- [ ] 방화벽 및 보안 그룹 설정
- [ ] Health Check 엔드포인트 테스트
- [ ] Rate Limiting 설정 검증
- [ ] 데이터베이스 연결 풀 모니터링

### 권장 항목
- [ ] 로드 밸런서 설정
- [ ] CDN 설정 (정적 리소스)
- [ ] 캐시 전략 수립
- [ ] 알림 시스템 연동 (에러, 성능 이슈)

---

## 환경 변수 설정

### 필수 환경 변수

```bash
# 데이터베이스
DB_USERNAME=your_db_username
DB_PASSWORD=your_secure_password

# 보안
SECRET_KEY=your-very-long-random-secret-key-change-in-production
ADMIN_USERNAME=admin
ADMIN_PASSWORD=secure-admin-password

# 환경
ENVIRONMENT=production
```

### application.yml 설정 예시

```yaml
spring:
  datasource:
    url: jdbc:postgresql://your-db-host:5432/relationship_tracker
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20  # 프로덕션에서는 증가 권장
      minimum-idle: 10
      
  jpa:
    hibernate:
      ddl-auto: validate  # 프로덕션에서는 validate 또는 none 사용
    show-sql: false
    
logging:
  level:
    com.rstracker: INFO
    org.springframework: WARN
    org.hibernate: WARN
```

---

## 데이터베이스 설정

### 1. 연결 풀 크기 조정

트래픽에 따라 조정:
- **낮은 트래픽**: maximum-pool-size: 10
- **중간 트래픽**: maximum-pool-size: 20-30
- **높은 트래픽**: maximum-pool-size: 50+

### 2. 인덱스 최적화

필요한 인덱스 확인:
```sql
-- 매칭 코드 조회 (자주 사용)
CREATE INDEX IF NOT EXISTS idx_matchings_code ON matchings(code);

-- 참여자 코드 조회
CREATE INDEX IF NOT EXISTS idx_participants_code ON participants(participant_code);

-- 기록 ID 조회
CREATE INDEX IF NOT EXISTS idx_records_record_id ON records(record_id);

-- 매칭 ID로 기록 조회
CREATE INDEX IF NOT EXISTS idx_records_matching_id ON records(matching_id);
```

### 3. 정기 백업

- 일일 자동 백업 설정
- 백업 파일 검증
- 복구 테스트 정기 수행

---

## 성능 최적화

### 1. 캐싱 전략

#### 현재 구현
- 질문 데이터: 인메모리 캐싱 (ConcurrentMapCacheManager)

#### 프로덕션 권장
- **Redis 캐싱 도입**
  - 분산 환경 지원
  - 캐시 만료 정책 설정
  - 캐시 무효화 전략

```yaml
# Redis 설정 예시
spring:
  cache:
    type: redis
  redis:
    host: your-redis-host
    port: 6379
    password: ${REDIS_PASSWORD}
```

### 2. 데이터베이스 최적화

- **커넥션 풀 모니터링**: HikariCP 메트릭 확인
- **쿼리 성능 모니터링**: 느린 쿼리 로깅
- **인덱스 사용률 모니터링**: 미사용 인덱스 제거

### 3. 애플리케이션 서버

- JVM 힙 크기 조정: `-Xmx2g -Xms2g` (트래픽에 따라)
- GC 로깅 활성화
- 스레드 풀 크기 조정

---

## 모니터링 및 로깅

### 1. Actuator 엔드포인트

접근 가능한 엔드포인트:
- `/actuator/health`: 헬스 체크
- `/actuator/metrics`: 메트릭 정보
- `/actuator/prometheus`: Prometheus 메트릭

**주의**: 프로덕션에서는 보안 설정 필요

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info  # 필요한 것만 노출
  endpoint:
    health:
      show-details: never  # 민감 정보 숨김
```

### 2. 로깅 전략

#### 로그 레벨
- **프로덕션**: INFO 이상
- **디버깅 필요 시**: DEBUG (임시)

#### 로그 파일 관리
- 로그 로테이션 설정
- 오래된 로그 자동 삭제
- 에러 로그 별도 파일 관리

#### 구조화된 로깅 (권장)
- JSON 형식 로깅 고려
- 로그 수집 시스템 연동 (ELK, CloudWatch 등)

### 3. 알림 설정

다음 상황에 알림 필요:
- Health Check 실패
- 에러 발생 (특정 임계값 초과)
- 응답 시간 증가
- 데이터베이스 연결 실패
- 메모리 사용량 증가

---

## 보안 고려사항

### 1. API 보안

#### Rate Limiting
- 기본: 분당 100회
- 매칭 생성: 분당 10회
- 필요시 IP/사용자별 제한 강화

#### 입력 검증
- 모든 입력값 검증 (`@Valid` 사용)
- SQL Injection 방지 (JPA 사용으로 기본 방지)
- XSS 방지 (프론트엔드와 협업)

### 2. 데이터베이스 보안

- 최소 권한 원칙 적용
- SSL/TLS 연결 사용
- 정기적인 보안 업데이트
- 백업 파일 암호화

### 3. 인증/인증

- 관리자 계정: 강력한 비밀번호
- 비밀번호 정기 변경
- 세션 타임아웃 설정

### 4. HTTPS

- 모든 통신 HTTPS 사용
- SSL/TLS 인증서 정기 갱신
- HSTS 헤더 설정

---

## 장애 대응

### 1. 일반적인 장애 시나리오

#### 데이터베이스 연결 실패
- **증상**: Health Check 실패, 에러 로그
- **대응**: 
  1. 데이터베이스 상태 확인
  2. 연결 풀 상태 확인
  3. 네트워크 연결 확인
  4. 필요시 애플리케이션 재시작

#### 메모리 부족
- **증상**: OutOfMemoryError, 응답 지연
- **대응**:
  1. 힙 덤프 수집
  2. 메모리 사용 패턴 분석
  3. GC 설정 조정
  4. 캐시 크기 조정

#### 높은 CPU 사용률
- **증상**: 응답 지연, 타임아웃
- **대응**:
  1. 스레드 덤프 수집
  2. 느린 쿼리 확인
  3. 캐시 히트율 확인
  4. 인덱스 최적화

### 2. 롤백 절차

1. 이전 버전으로 롤백
2. 데이터베이스 마이그레이션 롤백 (필요시)
3. 캐시 무효화
4. 모니터링 강화

### 3. 데이터 복구

- 백업에서 복구
- 트랜잭션 로그 확인
- 데이터 일관성 검증

---

## 확장성 고려사항

### 1. 수평 확장 (Horizontal Scaling)

#### 현재 구조
- 상태 비저장(Stateless) 애플리케이션
- 로드 밸런서를 통한 다중 인스턴스 배포 가능

#### 고려사항
- **세션 관리**: 세션 없음 (JWT 사용 시 분산 가능)
- **캐시**: Redis로 전환 시 분산 캐시 가능
- **파일 저장**: 공유 스토리지 또는 S3 사용

### 2. 데이터베이스 확장

#### 읽기 복제 (Read Replica)
- 읽기 전용 쿼리를 복제본으로 라우팅
- 부하 분산

#### 파티셔닝
- 대용량 데이터 처리 시 고려
- 매칭 데이터 날짜별 파티셔닝

### 3. 비동기 처리

#### 현재
- 동기 처리

#### 개선 가능
- 기록 생성 비동기화 (큐 사용)
- PDF 생성 비동기화
- 이메일/알림 비동기화

---

## 성능 벤치마크

### 권장 테스트

1. **부하 테스트**
   - 동시 사용자: 100, 500, 1000
   - 매칭 생성/참여 시나리오
   - 응답 시간 측정

2. **스트레스 테스트**
   - 최대 처리량 확인
   - 리소스 한계점 확인

3. **지속성 테스트**
   - 장시간 운영 시 메모리 누수 확인
   - 데이터베이스 연결 풀 안정성

---

## 체크리스트 요약

### 일일 모니터링
- [ ] Health Check 상태
- [ ] 에러 로그 확인
- [ ] 응답 시간 확인
- [ ] 데이터베이스 연결 상태

### 주간 점검
- [ ] 성능 메트릭 분석
- [ ] 디스크 사용량 확인
- [ ] 백업 상태 확인
- [ ] 보안 로그 확인

### 월간 점검
- [ ] 데이터베이스 최적화
- [ ] 캐시 히트율 분석
- [ ] 용량 계획 검토
- [ ] 보안 업데이트 적용

---

## 연락처 및 문서

- 프로젝트 문서: `/docs`
- API 문서: Swagger (추가 권장)
- 운영 매뉴얼: 이 문서

---

**마지막 업데이트**: 2024년
**유지보수 담당자**: [담당자 정보]

