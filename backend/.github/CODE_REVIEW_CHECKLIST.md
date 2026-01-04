# 코드 리뷰 체크리스트

코드 리뷰 시 다음 항목들을 확인하세요.

## 필수 확인 사항

### 일반
- [ ] 하드코딩 없음 (상수 클래스 또는 설정 파일 사용)
- [ ] 중복 코드 없음
- [ ] 네이밍 규칙 준수 (CODING_STANDARDS.md 참고)
- [ ] 불필요한 import 제거

### 레이어별
- [ ] Controller: 비즈니스 로직 없음, `@Valid` 사용
- [ ] Service: 인터페이스 구현, 트랜잭션 명시, 로깅 추가
- [ ] Repository: N+1 문제 없음, Fetch Join 사용 (필요시)
- [ ] Entity: JPA 어노테이션 적절, Lombok 사용

### 성능
- [ ] N+1 문제 없음
- [ ] 배치 처리 적용 (필요시)
- [ ] 읽기 전용 트랜잭션 명시 (`@Transactional(readOnly = true)`)
- [ ] 캐싱 적용 (자주 조회되는 데이터)

### 예외 처리
- [ ] 커스텀 예외 사용 (BusinessException, ResourceNotFoundException)
- [ ] 예외 메시지 명확
- [ ] 전역 예외 핸들러 활용 (Controller에서 try-catch 불필요)

### 테스트
- [ ] 단위 테스트 작성 (Service 레이어)
- [ ] 테스트 커버리지 적절

### 문서화
- [ ] 복잡한 로직 주석 추가
- [ ] 기능 명세서 업데이트 (FEATURE_SPECIFICATION.md)
- [ ] JavaDoc 추가 (Public API)

### 로깅
- [ ] 적절한 로그 레벨 사용 (DEBUG/INFO/WARN/ERROR)
- [ ] 파라미터 로깅 (민감 정보 제외)

### 보안
- [ ] 입력 검증 (`@Valid`)
- [ ] SQL Injection 방지 (JPA 사용)

## 선택 확인 사항

### 확장성
- [ ] Strategy 패턴 적용 (알고리즘 교체 가능)
- [ ] 인터페이스 사용 (Service)

### 코드 품질
- [ ] 메서드 길이 적절 (20줄 이하 권장)
- [ ] 중첩 깊이 적절 (3단계 이하 권장)
- [ ] 매직 넘버 없음

---

**참고**: [개발 규칙 문서](../CODING_STANDARDS.md)

