@echo off
echo ====================================
echo   도서관 좌석 관리 시스템 실행
echo ====================================
echo.

cd /d "%~dp0"

echo Python이 설치되어 있으면 Python으로 실행합니다...
echo.

python --version >nul 2>&1
if %errorlevel% == 0 (
    echo [Python 실행]
    pip install flask >nul 2>&1
    python app.py
) else (
    echo Python이 설치되어 있지 않습니다.
    echo.
    echo 실행 방법:
    echo 1. Python 설치 후 실행: python app.py
    echo 2. Java 설치 후 실행: mvnw.cmd spring-boot:run
    pause
)
