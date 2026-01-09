# Docker 환경 설정 가이드

## 개요

Docker를 사용하여 백엔드 애플리케이션과 PostgreSQL 데이터베이스를 함께 실행할 수 있습니다. 로컬 환경에서 다른 서비스와 충돌 없이 격리된 환경에서 개발할 수 있습니다.

## 필수 요구사항

- **Docker** 20.10 이상
- **Docker Compose** 2.0 이상

### Docker 설치 확인

```bash
docker --version
docker-compose --version
```

## 빠른 시작

### 1. 환경 변수 설정 (선택사항)

프로젝트 루트에 `.env` 파일을 생성하여 환경 변수를 설정할 수 있습니다:

```bash
# .env 파일 예시
DB_PASSWORD=rstracker_dev_password
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin123
SECRET_KEY=dev-secret-key-change-in-production
LOG_LEVEL=INFO
JPA_SHOW_SQL=false
```

`.env` 파일이 없어도 기본값으로 실행됩니다.

### 2. Docker Compose로 실행

프로젝트 루트 디렉토리에서 실행:

```bash
# 백그라운드로 실행
docker-compose up -d

# 또는 로그를 보면서 실행
docker-compose up
```

### 3. 실행 확인

```bash
# 서비스 상태 확인
docker-compose ps

# 백엔드 로그 확인
docker-compose logs -f backend

# Health check
curl http://localhost:8080/api/actuator/health
```

### 4. 접속 정보

- **백엔드 API**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **PostgreSQL**: localhost:5433 (로컬 PostgreSQL과 충돌 방지를 위해 5433 포트 사용)
  - 데이터베이스: `relationship_tracker`
  - 사용자: `rstracker`
  - 비밀번호: `.env` 파일의 `DB_PASSWORD` 또는 기본값 `rstracker_dev_password`

## 주요 명령어

### 서비스 시작/중지

```bash
# 시작
docker-compose up -d

# 중지
docker-compose down

# 중지 및 볼륨 삭제 (데이터베이스 데이터 삭제)
docker-compose down -v
```

### 로그 확인

```bash
# 모든 서비스 로그
docker-compose logs -f

# 특정 서비스 로그
docker-compose logs -f backend
docker-compose logs -f postgres
```

### 재빌드

코드 변경 후 이미지를 다시 빌드하려면:

```bash
# 이미지 재빌드 후 시작
docker-compose up -d --build

# 캐시 없이 완전 재빌드
docker-compose build --no-cache
docker-compose up -d
```

### 데이터베이스 접속

```bash
# PostgreSQL 컨테이너에 접속
docker-compose exec postgres psql -U rstracker -d relationship_tracker

# 또는 로컬에서 접속 (포트 5433)
psql -h localhost -p 5433 -U rstracker -d relationship_tracker
```

## 개발 모드

### 핫 리로드 (Hot Reload)

개발 중 코드 변경을 자동으로 반영하려면 `docker-compose.yml`의 백엔드 서비스에 다음 볼륨 마운트를 추가:

```yaml
backend:
  volumes:
    - ./backend/src:/app/src
```

그리고 Dockerfile을 개발 모드로 수정하거나, 개발 전용 docker-compose 파일을 사용하는 것을 권장합니다.

### 개발용 docker-compose.dev.yml

```yaml
# docker-compose.dev.yml 예시
version: '3.8'

services:
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    volumes:
      - ./backend/src:/app/src  # 소스 코드 마운트
    environment:
      SPRING_DEVTOOLS_RESTART_ENABLED: "true"
```

```bash
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
```

## 문제 해결

### 포트 충돌

포트 8080이나 5433이 이미 사용 중인 경우:

```bash
# 사용 중인 포트 확인
lsof -i :8080
lsof -i :5433

# docker-compose.yml에서 포트 변경
# backend:
#   ports:
#     - "8081:8080"  # 로컬 8081 포트 사용
```

### 데이터베이스 연결 오류

```bash
# PostgreSQL 컨테이너 상태 확인
docker-compose ps postgres

# PostgreSQL 로그 확인
docker-compose logs postgres

# 컨테이너 재시작
docker-compose restart postgres
```

### 이미지 빌드 오류

```bash
# 캐시 삭제 후 재빌드
docker-compose build --no-cache

# Docker 시스템 정리
docker system prune -a
```

### 볼륨 데이터 확인

```bash
# 볼륨 목록
docker volume ls

# 볼륨 상세 정보
docker volume inspect ft_postgres_data
```

## 프로덕션 배포

프로덕션 환경에서는 다음 사항을 고려하세요:

1. **환경 변수 보안**: `.env` 파일 대신 Docker secrets 또는 환경 변수 주입 사용
2. **네트워크 설정**: 내부 네트워크 사용, 포트 노출 최소화
3. **리소스 제한**: CPU, 메모리 제한 설정
4. **로깅**: 로그 드라이버 설정
5. **백업**: 데이터베이스 볼륨 정기 백업

```yaml
# 프로덕션 예시
services:
  backend:
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 1G
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

## 참고

- [Docker 공식 문서](https://docs.docker.com/)
- [Docker Compose 문서](https://docs.docker.com/compose/)

