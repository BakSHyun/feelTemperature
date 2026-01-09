# 로컬 PostgreSQL 비밀번호 설정 가이드

## 문제 상황
PostgreSQL 인증 오류: `Couldn't get password from PGPASS file` 또는 비밀번호 요구 오류가 발생할 때

## 해결 방법

### 방법 1: 비밀번호 설정 없이 접속 (개발 환경 권장)

로컬 개발 환경에서는 `trust` 인증을 사용할 수 있습니다:

1. PostgreSQL 설정 파일 찾기:
```bash
psql -U postgres -c "SHOW config_file;"
```

2. `pg_hba.conf` 파일 수정:
   - `host all all 127.0.0.1/32 trust` 또는 `host all all localhost trust` 추가
   - PostgreSQL 재시작 필요

3. `application.yml`에서 비밀번호 제거:
```yaml
password: ${DB_PASSWORD:}
```

### 방법 2: 비밀번호 설정 후 사용

1. psql로 PostgreSQL에 접속:
```bash
psql -U postgres
```

2. 사용자 비밀번호 설정:
```sql
ALTER USER backsunghyun WITH PASSWORD 'your_password';
```

또는 새 사용자 생성:
```sql
CREATE USER backsunghyun WITH PASSWORD 'your_password';
ALTER USER backsunghyun CREATEDB;
```

3. `.env` 파일에 비밀번호 설정:
```env
DB_PASSWORD=your_password
```

### 방법 3: .pgpass 파일 생성

1. `~/.pgpass` 파일 생성:
```bash
touch ~/.pgpass
chmod 600 ~/.pgpass
```

2. 파일에 다음 형식으로 추가:
```
localhost:5432:relationship_tracker:backsunghyun:your_password
```

형식: `hostname:port:database:username:password`

3. 권한 확인:
```bash
chmod 600 ~/.pgpass
```

## 권장 설정 (개발 환경)

로컬 개발 환경에서는 비밀번호 없이 접속하는 것이 편리합니다:

1. **PostgreSQL 설정 변경**:
   - `pg_hba.conf`에서 `trust` 인증 추가
   - 또는 현재 사용자를 `trust`로 설정

2. **application.yml 설정**:
```yaml
spring:
  datasource:
    username: ${DB_USERNAME:backsunghyun}
    password: ${DB_PASSWORD:}  # 빈 문자열 허용
```

3. **.env 파일 설정**:
```env
DB_USERNAME=backsunghyun
DB_PASSWORD=
```

## 문제 해결 체크리스트

- [ ] PostgreSQL 서버가 실행 중인지 확인: `brew services list` (Homebrew) 또는 `pg_isready`
- [ ] 사용자 이름이 올바른지 확인: `whoami`
- [ ] 데이터베이스가 존재하는지 확인: `psql -U backsunghyun -l`
- [ ] `.env` 파일에 올바른 값이 설정되어 있는지 확인
- [ ] Spring Boot 애플리케이션이 `.env` 파일을 올바르게 읽고 있는지 확인

