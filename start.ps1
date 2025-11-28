# 도서관 좌석 관리 시스템 실행 스크립트
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  도서관 좌석 관리 시스템" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# 현재 디렉토리로 이동
Set-Location "C:\Users\user\OneDrive\바탕 화면\library_system"

# Flask 설치 확인
Write-Host "Flask 설치 확인 중..." -ForegroundColor Yellow
try {
    pip show flask | Out-Null
    Write-Host "✅ Flask가 설치되어 있습니다." -ForegroundColor Green
} catch {
    Write-Host "Flask를 설치합니다..." -ForegroundColor Yellow
    pip install flask
}

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  서버를 시작합니다..." -ForegroundColor Green
Write-Host "  브라우저에서 http://localhost:7000" -ForegroundColor Yellow
Write-Host "  종료하려면 Ctrl+C를 누르세요" -ForegroundColor Red
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# 서버 실행
python app.py
