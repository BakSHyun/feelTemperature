# 기능 명세서 (Feature Specification)

## 목차
1. [매칭 관리](#매칭-관리)
2. [질문 관리](#질문-관리)
3. [응답 관리](#응답-관리)
4. [기록 관리](#기록-관리)
5. [온도 계산](#온도-계산)

---

## 매칭 관리

### 개요
두 명의 사용자가 질문 응답을 할 수 있도록 매칭을 생성하고 관리합니다.

### 기능 목록

#### 1. 매칭 생성
- **API**: `POST /api/matching/create`
- **Controller**: `MatchingController.createMatching()`
- **Service**: `MatchingService.createMatching()`
- **Repository**: `MatchingRepository.save()`

**처리 흐름:**
```
1. MatchingController.createMatching()
   ↓
2. MatchingService.createMatching()
   - MatchingCodeGenerator.generate()로 고유 코드 생성
   - 중복 체크 (최대 10회 시도)
   - Matching 엔티티 생성 (status: WAITING)
   ↓
3. MatchingRepository.save()
   ↓
4. MatchingMapper.toDto()로 DTO 변환
   ↓
5. MatchingDto 반환
```

**의존성:**
- `MatchingCodeGenerator`: 코드 생성 유틸리티
- `MatchingMapper`: Entity → DTO 변환

**영향 범위:**
- 매칭 생성 실패 시: BusinessException 발생
- 코드 중복 시: 재시도 (최대 10회)

**수정 시 주의사항:**
- 코드 생성 로직 변경 시 중복 체크 로직 확인 필요
- 상태 변경 시 MatchingStatus Enum 확인

---

#### 2. 매칭 참여
- **API**: `POST /api/matching/join/{code}`
- **Controller**: `MatchingController.joinMatching()`
- **Service**: `MatchingService.joinMatching()`
- **Repository**: 
  - `MatchingRepository.findByCode()`
  - `ParticipantRepository.save()`
  - `ParticipantRepository.countByMatchingId()`

**처리 흐름:**
```
1. MatchingController.joinMatching(code)
   ↓
2. MatchingService.joinMatching(code)
   - MatchingRepository.findByCode()로 매칭 조회
   - 상태 검증 (WAITING만 허용)
   - 참여자 수 확인 (최대 2명)
   - Participant 엔티티 생성
   - ParticipantCodeGenerator.generate()로 참여자 코드 생성
   ↓
3. ParticipantRepository.save()
   ↓
4. 참여자 수가 2명이 되면 상태 변경 (ESTABLISHED)
   ↓
5. MatchingMapper.toDto()로 DTO 변환
   ↓
6. ParticipantDto 반환
```

**의존성:**
- `ParticipantCodeGenerator`: 참여자 코드 생성 유틸리티
- `MatchingStatus`: 매칭 상태 Enum
- `AppConstants.MAX_PARTICIPANTS_PER_MATCHING`: 최대 참여자 수 (2명)

**영향 범위:**
- 매칭이 ESTABLISHED 상태가 되면 질문 응답 가능
- 참여자 수 제한 변경 시 AppConstants 수정 필요

**수정 시 주의사항:**
- 최대 참여자 수 변경 시 AppConstants와 비즈니스 로직 동시 수정
- 상태 변경 로직은 트랜잭션 내에서 처리

---

#### 3. 매칭 조회
- **API**: `GET /api/matching/{code}`
- **Controller**: `MatchingController.getMatching()`
- **Service**: `MatchingService.getMatching()`
- **Repository**: `MatchingRepository.findByCode()`

**처리 흐름:**
```
1. MatchingController.getMatching(code)
   ↓
2. MatchingService.getMatching(code)
   - @Transactional(readOnly = true)
   - MatchingRepository.findByCode()로 매칭 조회
   ↓
3. MatchingMapper.toDto()로 DTO 변환
   ↓
4. MatchingDto 반환
```

**의존성:**
- `MatchingMapper`: Entity → DTO 변환

**영향 범위:**
- 없음 (조회 전용)

**수정 시 주의사항:**
- 읽기 전용 트랜잭션 유지

---

#### 4. 매칭 상태 조회
- **API**: `GET /api/matching/status/{code}`
- **Controller**: `MatchingController.getMatchingStatus()`
- **Service**: `MatchingService.getMatchingStatus()`
- **Repository**: 
  - `MatchingRepository.findByCode()`
  - `ParticipantRepository.countByMatchingId()`

**처리 흐름:**
```
1. MatchingController.getMatchingStatus(code)
   ↓
2. MatchingService.getMatchingStatus(code)
   - @Transactional(readOnly = true)
   - MatchingRepository.findByCode()로 매칭 조회
   - ParticipantRepository.countByMatchingId()로 참여자 수 조회
   ↓
3. MatchingStatusDto 생성 및 반환
```

**의존성:**
- `AppConstants.MAX_PARTICIPANTS_PER_MATCHING`: 최대 참여자 수

**영향 범위:**
- 참여자 수 제한 변경 시 영향

**수정 시 주의사항:**
- 읽기 전용 트랜잭션 유지

---

## 질문 관리

### 개요
질문과 선택지를 관리하고 조회합니다.

### 기능 목록

#### 1. 활성 질문 목록 조회
- **API**: `GET /api/questions`
- **Controller**: `QuestionController.getActiveQuestions()`
- **Service**: `QuestionService.getActiveQuestions()`
- **Repository**: `QuestionRepository.findActiveWithChoices()`

**처리 흐름:**
```
1. QuestionController.getActiveQuestions()
   ↓
2. QuestionService.getActiveQuestions()
   - @Cacheable("questions") 캐싱 적용
   - QuestionRepository.findActiveWithChoices() 
     (Fetch Join으로 N+1 문제 해결)
   - Question.toDto()로 DTO 변환 (choices 포함)
   ↓
3. List<QuestionDto> 반환
```

**의존성:**
- `CacheConfig`: 캐싱 설정
- `QuestionRepository.findActiveWithChoices()`: Fetch Join 쿼리

**영향 범위:**
- 질문 데이터 변경 시 캐시 무효화 필요 (현재는 자동 만료)
- 질문 추가/수정/삭제 시 캐시 영향

**수정 시 주의사항:**
- Fetch Join 쿼리 수정 시 N+1 문제 재발 가능
- 캐시 키 변경 시 캐시 무효화 정책 확인

---

#### 2. 질문 상세 조회
- **API**: `GET /api/questions/{id}`
- **Controller**: `QuestionController.getQuestion()`
- **Service**: `QuestionService.getQuestion()`
- **Repository**: `QuestionRepository.findByIdWithChoices()`

**처리 흐름:**
```
1. QuestionController.getQuestion(id)
   ↓
2. QuestionService.getQuestion(id)
   - QuestionRepository.findByIdWithChoices()
     (Fetch Join으로 N+1 문제 해결)
   - Question.toDto()로 DTO 변환
   ↓
3. QuestionDto 반환
```

**의존성:**
- `QuestionRepository.findByIdWithChoices()`: Fetch Join 쿼리

**영향 범위:**
- 없음 (조회 전용)

**수정 시 주의사항:**
- Fetch Join 쿼리 수정 시 N+1 문제 재발 가능

---

#### 3. 질문 초기 데이터 생성
- **Component**: `DataInitializer`
- **메서드**: `run()`
- **Repository**: 
  - `QuestionRepository.save()`
  - `QuestionChoiceRepository.save()`

**처리 흐름:**
```
1. 애플리케이션 시작 시 CommandLineRunner 실행
   ↓
2. DataInitializer.run()
   - QuestionRepository.count() > 0이면 스킵
   - 질문 세트 v1 생성 (6개 질문 + 선택지)
   ↓
3. QuestionRepository.save()
4. QuestionChoiceRepository.save()
```

**의존성:**
- 질문 세트 v1 데이터 (하드코딩)

**영향 범위:**
- 애플리케이션 최초 실행 시에만 실행
- 질문 데이터 변경 시 DataInitializer 수정 필요

**수정 시 주의사항:**
- 질문 순서 변경 시 온도 계산 가중치 확인 필요
- 질문 타입 변경 시 비즈니스 로직 확인 필요

---

## 응답 관리

### 개요
사용자의 질문 응답을 저장합니다.

### 기능 목록

#### 1. 응답 제출
- **API**: `POST /api/answers/submit/{participantCode}`
- **Controller**: `AnswerController.submitAnswers()`
- **Service**: `AnswerService.submitAnswers()`
- **Repository**: 
  - `ParticipantRepository.findByParticipantCode()`
  - `AnswerRepository.deleteAll()`
  - `QuestionRepository.findAllById()`
  - `QuestionChoiceRepository.findAllById()`
  - `AnswerRepository.saveAll()`

**처리 흐름:**
```
1. AnswerController.submitAnswers(participantCode, submission)
   - @Valid로 입력 검증
   ↓
2. AnswerService.submitAnswers(participantCode, answerDtos)
   - @Transactional
   - ParticipantRepository.findByParticipantCode()로 참여자 조회
   - AnswerRepository.deleteAll()로 기존 응답 삭제
   - 질문/선택지 ID 수집
   - QuestionRepository.findAllById()로 질문 일괄 조회
   - QuestionChoiceRepository.findAllById()로 선택지 일괄 조회
   - Answer 엔티티 리스트 생성
   ↓
3. AnswerRepository.saveAll()로 배치 저장
```

**의존성:**
- `AnswerCreateDto`: 입력 DTO (검증 포함)
- `AnswerSubmissionDto`: 요청 DTO (검증 포함)
- 배치 저장 (JPA batch_size 설정)

**영향 범위:**
- 응답 저장 실패 시 트랜잭션 롤백
- 기존 응답 삭제 후 새 응답 저장 (중간 저장 불가)

**수정 시 주의사항:**
- 배치 저장 로직 변경 시 JPA 배치 설정 확인
- 질문/선택지 조회 로직 변경 시 N+1 문제 재발 가능
- 트랜잭션 범위 유지 (전체 처리 성공/실패)

---

#### 2. 참여자별 응답 조회
- **Service**: `AnswerService.getAnswersByParticipant()`
- **Repository**: 
  - `ParticipantRepository.findByParticipantCode()`
  - `AnswerRepository.findByParticipantId()`

**처리 흐름:**
```
1. AnswerService.getAnswersByParticipant(participantCode)
   - @Transactional(readOnly = true)
   - ParticipantRepository.findByParticipantCode()로 참여자 조회
   - AnswerRepository.findByParticipantId()로 응답 조회
   ↓
2. List<Answer> 반환
```

**의존성:**
- 없음 (내부 사용)

**영향 범위:**
- 없음 (조회 전용)

**수정 시 주의사항:**
- 읽기 전용 트랜잭션 유지

---

#### 3. 매칭별 응답 조회
- **Service**: `AnswerService.getAnswersByMatching()`
- **Repository**: `AnswerRepository.findByMatchingId()`

**처리 흐름:**
```
1. AnswerService.getAnswersByMatching(matchingId)
   - AnswerRepository.findByMatchingId()로 응답 조회
   ↓
2. List<Answer> 반환
```

**의존성:**
- RecordService.createRecord()에서 사용

**영향 범위:**
- 기록 생성 시 사용

**수정 시 주의사항:**
- 쿼리 성능 확인 (매칭당 최대 12개 응답 예상)

---

## 기록 관리

### 개요
질문 응답을 바탕으로 최종 기록을 생성하고 관리합니다.

### 기능 목록

#### 1. 기록 생성
- **API**: `POST /api/records/create/{matchingId}`
- **Controller**: `RecordController.createRecord()`
- **Service**: `RecordService.createRecord()`
- **Repository**: 
  - `MatchingRepository.findById()`
  - `RecordRepository.findByMatchingId()`
  - `AnswerRepository.findByMatchingId()`
  - `QuestionRepository.findAllWithChoices()`
  - `QuestionChoiceRepository.findAllWithQuestions()`
  - `RecordRepository.save()`

**처리 흐름:**
```
1. RecordController.createRecord(matchingId)
   ↓
2. RecordService.createRecord(matchingId)
   - @Transactional
   - MatchingRepository.findById()로 매칭 조회
   - RecordRepository.findByMatchingId()로 중복 체크
   - AnswerRepository.findByMatchingId()로 응답 조회
   - QuestionRepository.findAllWithChoices()로 질문 조회 (Fetch Join)
   - QuestionChoiceRepository.findAllWithQuestions()로 선택지 조회 (Fetch Join)
   ↓
3. TemperatureCalculationStrategy.calculate()로 온도 계산
   ↓
4. createSummary()로 응답 요약 생성
   ↓
5. Record 엔티티 생성
   - RecordIdGenerator.generate()로 기록 ID 생성
   ↓
6. RecordRepository.save()
   ↓
7. Matching 상태 변경 (COMPLETED)
   ↓
8. RecordMapper.toDto()로 DTO 변환
   ↓
9. RecordDto 반환
```

**의존성:**
- `TemperatureCalculationStrategy`: 온도 계산 전략 (Strategy 패턴)
- `TemperatureCalculationProperties`: 온도 계산 가중치 설정
- `RecordIdGenerator`: 기록 ID 생성 유틸리티
- `RecordMapper`: Entity → DTO 변환
- `MatchingStatus`: 매칭 상태 Enum

**영향 범위:**
- 기록 생성 시 매칭 상태 변경 (COMPLETED)
- 온도 계산 로직 변경 시 TemperatureCalculationStrategy 수정
- 가중치 변경 시 TemperatureCalculationProperties 수정

**수정 시 주의사항:**
- 온도 계산 로직 변경 시 Strategy 구현체 확인
- 가중치 변경 시 application.yml 수정
- Fetch Join 쿼리 수정 시 N+1 문제 재발 가능
- 트랜잭션 범위 유지 (기록 생성 + 상태 변경)

---

#### 2. 기록 조회 (ID)
- **API**: `GET /api/records/{recordId}`
- **Controller**: `RecordController.getRecord()`
- **Service**: `RecordService.getRecord()`
- **Repository**: `RecordRepository.findByRecordId()`

**처리 흐름:**
```
1. RecordController.getRecord(recordId)
   ↓
2. RecordService.getRecord(recordId)
   - @Transactional(readOnly = true)
   - RecordRepository.findByRecordId()로 기록 조회
   ↓
3. RecordMapper.toDto()로 DTO 변환
   ↓
4. RecordDto 반환
```

**의존성:**
- `RecordMapper`: Entity → DTO 변환

**영향 범위:**
- 없음 (조회 전용)

**수정 시 주의사항:**
- 읽기 전용 트랜잭션 유지

---

#### 3. 기록 조회 (매칭 ID)
- **API**: `GET /api/records/matching/{matchingId}`
- **Controller**: `RecordController.getRecordByMatchingId()`
- **Service**: `RecordService.getRecordByMatchingId()`
- **Repository**: `RecordRepository.findByMatchingId()`

**처리 흐름:**
```
1. RecordController.getRecordByMatchingId(matchingId)
   ↓
2. RecordService.getRecordByMatchingId(matchingId)
   - @Transactional(readOnly = true)
   - RecordRepository.findByMatchingId()로 기록 조회
   ↓
3. RecordMapper.toDto()로 DTO 변환
   ↓
4. RecordDto 반환
```

**의존성:**
- `RecordMapper`: Entity → DTO 변환

**영향 범위:**
- 없음 (조회 전용)

**수정 시 주의사항:**
- 읽기 전용 트랜잭션 유지

---

#### 4. 기록 비활성화
- **API**: `PUT /api/records/{recordId}/deactivate`
- **Controller**: `RecordController.deactivateRecord()`
- **Service**: `RecordService.deactivateRecord()`
- **Repository**: 
  - `RecordRepository.findByRecordId()`
  - `RecordRepository.save()`

**처리 흐름:**
```
1. RecordController.deactivateRecord(recordId)
   ↓
2. RecordService.deactivateRecord(recordId)
   - @Transactional
   - RecordRepository.findByRecordId()로 기록 조회
   - isActive = false로 설정
   ↓
3. RecordRepository.save()
```

**의존성:**
- 없음

**영향 범위:**
- 기록 조회 시 isActive = true인 것만 조회 (필터링 필요)

**수정 시 주의사항:**
- 삭제가 아닌 비활성화 (소프트 삭제)
- 조회 로직에 isActive 필터링 필요

---

## 온도 계산

### 개요
질문 응답을 바탕으로 관계의 "온도"를 계산합니다.

### 기능 목록

#### 1. 가중치 기반 온도 계산
- **Strategy**: `WeightedTemperatureCalculationStrategy`
- **Interface**: `TemperatureCalculationStrategy`
- **Properties**: `TemperatureCalculationProperties`

**처리 흐름:**
```
1. TemperatureCalculationStrategy.calculate()
   ↓
2. WeightedTemperatureCalculationStrategy.calculate()
   - 참여자별 그룹화
   - 질문별 가중치 적용 (TemperatureCalculationProperties)
   - 선택지 temperatureWeight와 가중치 곱셈
   - 참여자별 온도 계산
   ↓
3. 두 참여자의 온도 평균 및 차이 계산
   ↓
4. TemperatureResult 반환
```

**의존성:**
- `TemperatureCalculationProperties`: 가중치 설정
- 질문 순서 (Q3, Q4, Q5, Q6만 사용)
- 선택지 temperatureWeight

**영향 범위:**
- 기록 생성 시 사용
- 가중치 변경 시 TemperatureCalculationProperties 수정
- 질문 순서 변경 시 가중치 매핑 확인 필요

**수정 시 주의사항:**
- 가중치 변경 시 application.yml 수정
- 새로운 계산 방식 추가 시 Strategy 구현체 추가
- 질문 순서와 가중치 매핑 일관성 유지

---

## 데이터 흐름 다이어그램

### 매칭 생성 → 참여 → 응답 → 기록 생성

```
[클라이언트]
    ↓
[매칭 생성 API] → MatchingService.createMatching()
    ↓
[Matching 엔티티 생성] (status: WAITING)
    ↓
[매칭 참여 API] → MatchingService.joinMatching()
    ↓
[Participant 엔티티 생성] (참여자 1)
    ↓
[매칭 참여 API] → MatchingService.joinMatching()
    ↓
[Participant 엔티티 생성] (참여자 2)
    ↓
[Matching 상태 변경] (status: ESTABLISHED)
    ↓
[응답 제출 API] → AnswerService.submitAnswers()
    ↓
[Answer 엔티티 저장] (참여자 1)
    ↓
[응답 제출 API] → AnswerService.submitAnswers()
    ↓
[Answer 엔티티 저장] (참여자 2)
    ↓
[기록 생성 API] → RecordService.createRecord()
    ↓
[온도 계산] → TemperatureCalculationStrategy.calculate()
    ↓
[Record 엔티티 생성]
    ↓
[Matching 상태 변경] (status: COMPLETED)
```

---

## 주요 의존성 관계

### 서비스 간 의존성
- `RecordService` → `TemperatureCalculationStrategy`
- `RecordService` → `AnswerService` (getAnswersByMatching)
- `MatchingService` → `MatchingMapper`
- `RecordService` → `RecordMapper`

### 유틸리티 의존성
- `MatchingService` → `MatchingCodeGenerator`
- `MatchingService` → `ParticipantCodeGenerator`
- `RecordService` → `RecordIdGenerator`

### 설정 의존성
- `WeightedTemperatureCalculationStrategy` → `TemperatureCalculationProperties`
- 모든 Service → Repository (데이터 접근)

---

## 수정 시 영향도 매트릭스

| 수정 대상 | 영향받는 기능 | 영향 범위 |
|---------|------------|----------|
| MatchingStatus Enum | 모든 매칭 관련 기능 | 높음 |
| TemperatureCalculationProperties | 기록 생성, 온도 계산 | 높음 |
| Question Entity | 질문 조회, 기록 생성 | 높음 |
| MatchingService | 매칭 생성/참여/조회 | 중간 |
| RecordService | 기록 생성/조회 | 중간 |
| AnswerService | 응답 제출 | 중간 |
| Repository 쿼리 | 해당 기능의 성능 | 중간 |
| Mapper 클래스 | DTO 변환 | 낮음 |
| 유틸리티 클래스 | 코드 생성 | 낮음 |

---

**마지막 업데이트**: 2024년
**문서 유지보수 담당자**: [담당자 정보]

