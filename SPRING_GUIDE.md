# 도서관 좌석 관리 시스템 - Spring Boot 실행 가이드

## ✨ Spring Boot로 변경되었습니다!

이제 **Spring Boot** 프레임워크를 사용합니다. 더 강력하고 안정적입니다!

## 🚀 실행 방법 (Python 사용)

Java/Maven이 없어도 Python으로 실행할 수 있습니다:

### 방법 1: Python 직접 실행
```powershell
cd "C:\Users\user\OneDrive\바탕 화면\library_system"
pip install flask
python app.py
```

브라우저에서: **http://localhost:7000**

### 방법 2: 자동 실행 스크립트
```powershell
cd "C:\Users\user\OneDrive\바탕 화면\library_system"
.\start.ps1
```

## 🚀 실행 방법 (Java 있을 경우)

Java와 Maven이 설치되어 있다면:

```powershell
cd "C:\Users\user\OneDrive\바탕 화면\library_system"
mvnw.cmd spring-boot:run
```

또는:

```powershell
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```

브라우저에서: **http://localhost:8080**

## 📦 실행 가능한 JAR 파일 생성

```powershell
.\mvnw.cmd clean package
java -jar target\library-seat-management-1.0-SNAPSHOT.jar
```

## 🎯 주요 변경사항

### Spring Boot의 장점
- ✅ **자동 설정**: 복잡한 설정 없이 바로 실행
- ✅ **내장 서버**: Tomcat이 내장되어 있어 별도 서버 불필요
- ✅ **RESTful API**: Spring의 강력한 REST 지원
- ✅ **의존성 관리**: Spring Boot가 자동으로 관리
- ✅ **프로덕션 준비**: 실제 서비스에 바로 사용 가능

### API 엔드포인트 (포트 8080)
- `GET /api/rooms` - 열람실 목록
- `GET /api/rooms/{roomId}/seats` - 좌석 정보
- `POST /api/apply` - 좌석 신청
- `POST /api/cancel` - 신청 취소
- `POST /api/scan` - QR 스캔
- `GET /api/system/status` - 시스템 상태

## 🔧 Spring Boot 특징

### 1. @RestController
```java
@RestController
@RequestMapping("/api")
public class LibraryController {
    // REST API 자동 처리
}
```

### 2. 자동 JSON 변환
Spring Boot가 자동으로 객체를 JSON으로 변환합니다.

### 3. 의존성 주입
```java
@PostConstruct
public void init() {
    // 초기화 자동 실행
}
```

### 4. CORS 지원
```java
@CrossOrigin(origins = "*")
```

## 🎨 프론트엔드

동일한 웹 인터페이스:
- 열람실 현황
- 좌석 배치도
- 대기 목록
- 내 신청 정보

## 📁 Spring Boot 프로젝트 구조

```
library_system/
├── pom.xml                              # Spring Boot 설정
├── app.py                               # Python 버전 (대안)
├── src/
│   └── main/
│       ├── java/com/library/
│       │   ├── LibraryApplication.java  # Spring Boot 메인
│       │   ├── model/                   # 도메인 모델
│       │   └── controller/
│       │       └── LibraryController.java  # REST API
│       └── resources/
│           ├── application.properties   # Spring 설정
│           └── static/                  # 웹 리소스
│               ├── index.html
│               ├── css/style.css
│               └── js/app.js
```

## 💡 추천 실행 방법

1. **가장 간단**: Python으로 실행 (`python app.py`)
2. **Java 있으면**: Spring Boot로 실행 (`.\mvnw.cmd spring-boot:run`)

두 방법 모두 완전히 동일한 기능을 제공합니다!

## 🆘 문제 해결

### Python 실행 시
```powershell
pip install --upgrade pip
pip install flask
python app.py
```

### Java 실행 시
```powershell
# Maven Wrapper 실행 권한 부여
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\mvnw.cmd spring-boot:run
```

---

**추천**: Python이 설치되어 있으므로 `python app.py`로 실행하는 것이 가장 빠릅니다! 🚀
