# 개발 환경 설정 가이드 (macOS)

## 필수 요구사항

이 프로젝트는 다음이 필요합니다:
- **Java 17** 이상
- **Maven** 3.6 이상
- **PostgreSQL** (데이터베이스)

## 1. Java 설치

### 방법 1: Homebrew 사용 (권장)

```bash
# Homebrew가 없다면 먼저 설치
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Java 17 설치
brew install openjdk@17

# PATH 설정 (zsh 사용 시)
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

### 방법 2: SDKMAN 사용 (권장)

```bash
# SDKMAN 설치
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Java 17 설치
sdk install java 17.0.2-open
sdk use java 17.0.2-open
```

### 방법 3: Oracle JDK 직접 다운로드

https://www.oracle.com/java/technologies/downloads/#java17 에서 다운로드

## 2. Maven 설치

### 방법 1: Homebrew 사용 (가장 쉬움)

```bash
brew install maven
```

### 방법 2: SDKMAN 사용

```bash
sdk install maven
```

### 방법 3: 직접 설치

```bash
# Maven 다운로드
cd ~/Downloads
curl -O https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz

# 압축 해제
tar -xzf apache-maven-3.9.6-bin.tar.gz
sudo mv apache-maven-3.9.6 /opt/maven

# PATH 설정
echo 'export PATH="/opt/maven/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

## 3. PostgreSQL 설치

```bash
# Homebrew로 설치
brew install postgresql@15

# 서비스 시작
brew services start postgresql@15

# 데이터베이스 생성
createdb relationship_tracker
```

## 4. 설치 확인

설치가 완료되면 다음 명령어로 확인:

```bash
# Java 버전 확인 (Java 17 이상이어야 함)
java -version

# Maven 버전 확인
mvn -version

# PostgreSQL 확인
psql --version
```

## 5. 프로젝트 빌드 및 실행

```bash
cd backend

# 의존성 다운로드 및 컴파일
mvn clean install

# 애플리케이션 실행
mvn spring-boot:run
```

## 문제 해결

### Java 버전 문제

```bash
# 여러 Java 버전이 설치된 경우
/usr/libexec/java_home -V  # 설치된 Java 버전 확인

# 특정 버전 사용
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

### Maven을 찾을 수 없는 경우

```bash
# PATH 확인
echo $PATH

# Maven 위치 확인
which mvn
ls -la $(which mvn)

# ~/.zshrc에 PATH 추가 후
source ~/.zshrc
```

### PostgreSQL 연결 오류

```bash
# PostgreSQL 서비스 상태 확인
brew services list

# 서비스 재시작
brew services restart postgresql@15

# 데이터베이스 재생성
dropdb relationship_tracker
createdb relationship_tracker
```

## IDE 설정 (IntelliJ IDEA / VS Code)

### IntelliJ IDEA

1. **File → Project Structure → Project**
   - SDK: Java 17 선택
   - Language level: 17

2. **File → Settings → Build → Build Tools → Maven**
   - Maven home directory: 자동 감지 또는 `/opt/homebrew` 확인

### VS Code

1. **Java Extension Pack** 설치
2. **Maven for Java** 확장 설치
3. `.vscode/settings.json`에 Java 경로 설정:
```json
{
  "java.home": "/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
}
```

## 빠른 설치 (한 번에)

Homebrew가 설치되어 있다면:

```bash
# Java, Maven, PostgreSQL 한 번에 설치
brew install openjdk@17 maven postgresql@15

# PATH 설정
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# PostgreSQL 서비스 시작
brew services start postgresql@15

# 데이터베이스 생성
createdb relationship_tracker
```

## 참고

- **Java 17**: Spring Boot 3.2.0이 Java 17 이상 필요
- **Maven**: 프로젝트 빌드 및 의존성 관리
- **PostgreSQL**: 데이터베이스 (로컬 개발용)

