# GitHub 업로드 가이드

## 1️⃣ GitHub에서 새 저장소 만들기

1. https://github.com 접속 후 로그인
2. 우측 상단의 `+` 클릭 → `New repository` 선택
3. 저장소 정보 입력:
   - **Repository name**: `library-seat-management`
   - **Description**: `도서관 좌석 관리 시스템 - Spring Boot & Flask`
   - **Public** 또는 **Private** 선택
   - ⚠️ **Initialize this repository with: README** 체크 해제 (이미 README.md가 있음)
4. `Create repository` 클릭

## 2️⃣ 로컬 저장소와 GitHub 연결

복사한 저장소 URL을 사용하여 다음 명령어를 실행하세요:

```powershell
cd "C:\Users\user\OneDrive\바탕 화면\library_system"

# GitHub 저장소 연결 (URL은 본인의 저장소 URL로 변경)
& "C:\Program Files\Git\cmd\git.exe" remote add origin https://github.com/YOUR_USERNAME/library-seat-management.git

# 메인 브랜치 이름 설정
& "C:\Program Files\Git\cmd\git.exe" branch -M main

# GitHub에 푸시
& "C:\Program Files\Git\cmd\git.exe" push -u origin main
```

## 3️⃣ 인증 정보 입력

푸시 시 GitHub 인증이 필요합니다:

### Personal Access Token 사용 (권장)
1. GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. `Generate new token` 클릭
3. 권한 선택: `repo` 전체 체크
4. 토큰 생성 후 복사
5. Git 푸시 시 비밀번호로 토큰 입력

## 4️⃣ 완료 확인

푸시가 성공하면 GitHub 저장소에서 다음을 확인할 수 있습니다:

✅ README.md가 메인 페이지에 표시됨
✅ Architecture 다이어그램이 렌더링됨
✅ 전체 소스 코드가 업로드됨
✅ .gitignore로 불필요한 파일 제외됨

## 📋 업로드된 파일 목록

```
✅ README.md                      - 프로젝트 문서
✅ .gitignore                    - Git 제외 파일
✅ docs/architecture-diagram.svg - 아키텍처 다이어그램
✅ pom.xml                       - Maven 설정
✅ app.py                        - Flask 서버
✅ requirements.txt              - Python 의존성
✅ src/                          - Java 소스 코드
   ├── main/java/com/library/
   │   ├── LibraryApplication.java
   │   ├── controller/
   │   ├── service/
   │   └── model/
   └── main/resources/
       ├── application.properties
       └── static/
```

## 🔄 이후 변경사항 업데이트

프로젝트를 수정한 후 GitHub에 업데이트하려면:

```powershell
cd "C:\Users\user\OneDrive\바탕 화면\library_system"

# 변경사항 확인
& "C:\Program Files\Git\cmd\git.exe" status

# 변경된 파일 추가
& "C:\Program Files\Git\cmd\git.exe" add .

# 커밋 메시지와 함께 저장
& "C:\Program Files\Git\cmd\git.exe" commit -m "커밋 메시지"

# GitHub에 푸시
& "C:\Program Files\Git\cmd\git.exe" push
```

## 🎯 GitHub에서 확인할 사항

저장소 페이지에서 다음을 확인하세요:

1. **README.md**: 프로젝트 설명이 잘 표시되는지
2. **아키텍처 다이어그램**: SVG 이미지가 렌더링되는지
3. **코드 구조**: 파일 트리가 올바른지
4. **Issues 탭**: 활성화되어 있는지
5. **About 섹션**: 프로젝트 설명 추가

## 💡 추가 설정 (선택사항)

### GitHub Pages로 웹사이트 호스팅
1. Settings → Pages
2. Source: `Deploy from a branch`
3. Branch: `main` → `/docs` 선택
4. Save

### Topics 추가
저장소 메인 페이지에서 `Add topics` 클릭:
- `spring-boot`
- `flask`
- `library-management`
- `seat-reservation`
- `java`
- `python`

### About 섹션 업데이트
⚙️ 아이콘 클릭 → 설명 추가:
```
도서관 좌석 관리 및 CCTV 모니터링 시스템 (Spring Boot + Flask)
```

---

**이제 GitHub에 프로젝트가 준비되었습니다! 🎉**
