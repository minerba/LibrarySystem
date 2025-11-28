# 빠른 시작 가이드

## 1단계: Maven 설치 확인

PowerShell에서 다음 명령어를 실행하세요:

```powershell
mvn --version
```

Maven이 설치되어 있지 않다면:
- https://maven.apache.org/download.cgi 에서 다운로드
- 또는 Chocolatey 사용: `choco install maven`

## 2단계: 프로젝트 디렉토리로 이동

```powershell
cd "C:\Users\user\OneDrive\바탕 화면\library_system"
```

## 3단계: 의존성 다운로드 및 빌드

```powershell
mvn clean install
```

처음 실행 시 필요한 라이브러리를 자동으로 다운로드합니다.

## 4단계: 서버 실행

```powershell
mvn exec:java -Dexec.mainClass="com.library.Main"
```

또는:

```powershell
mvn exec:java
```

## 5단계: 웹 브라우저에서 접속

서버가 시작되면 브라우저를 열고:

```
http://localhost:7000
```

## 서버 종료

PowerShell에서 `Ctrl + C` 를 눌러 서버를 종료합니다.

## 문제 해결

### Maven 빌드 오류
```powershell
mvn clean
mvn install
```

### 포트 7000이 이미 사용 중
pom.xml 또는 Main.java에서 포트 번호를 변경하세요.

### Java 버전 오류
```powershell
java -version
```
Java 11 이상이 필요합니다.

## 데모 계정

- **학생 ID**: 2021001
- **학생 이름**: 홍길동

기본적으로 3개의 열람실이 자동으로 등록됩니다:
- 제1열람실 (50석)
- 제2열람실 (40석)
- 제3열람실 (60석)
