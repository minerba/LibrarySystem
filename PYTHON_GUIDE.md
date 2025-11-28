# 도서관 좌석 관리 시스템 - Python 실행 가이드

## 빠른 실행 방법 (Python 버전)

Java와 Maven이 없어도 Python만 있으면 실행할 수 있습니다!

### 1단계: Flask 설치

PowerShell에서 다음 명령어를 실행하세요:

```powershell
cd "C:\Users\user\OneDrive\바탕 화면\library_system"
pip install flask
```

### 2단계: 서버 실행

```powershell
python app.py
```

### 3단계: 브라우저에서 접속

서버가 시작되면 브라우저를 열고:

```
http://localhost:7000
```

## 서버 종료

PowerShell에서 `Ctrl + C`를 눌러 서버를 종료합니다.

## 자동 실행 스크립트

더 편하게 실행하려면:

```powershell
.\start.ps1
```

## 문제 해결

### Flask가 설치되지 않는 경우
```powershell
pip install --upgrade pip
pip install flask
```

### Python이 설치되어 있지 않은 경우
1. https://www.python.org/downloads/ 에서 Python 다운로드
2. 설치 시 "Add Python to PATH" 체크
3. 설치 후 PowerShell 재시작

### 포트 7000이 이미 사용 중
app.py 파일의 마지막 줄에서 포트 번호를 변경하세요:
```python
app.run(host='0.0.0.0', port=8000, debug=True)  # 7000 -> 8000
```

## 기능

- ✅ 3개의 열람실 (제1, 제2, 제3열람실)
- ✅ 실시간 좌석 현황 조회
- ✅ 좌석 신청/취소
- ✅ QR 코드 스캔
- ✅ 대기 목록 관리
- ✅ 반응형 웹 인터페이스

즐거운 이용 되세요! 🎉
