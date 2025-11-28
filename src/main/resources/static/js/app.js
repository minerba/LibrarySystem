// API Base URL
const API_BASE = window.location.origin + '/api';

// 현재 로그인한 학생 정보 (시뮬레이션)
const currentStudent = {
    studentId: '2021001',
    name: '홍길동',
    major: '컴퓨터공학과',
    phoneNumber: '010-1234-5678'
};

// 현재 선택된 열람실
let selectedRoomId = null;
let myApplicationId = null;

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', () => {
    loadReadingRooms();
    setupEventListeners();
    // 5초마다 자동 새로고침
    setInterval(loadReadingRooms, 5000);
});

// 이벤트 리스너 설정
function setupEventListeners() {
    // 새로고침 버튼
    document.getElementById('refreshBtn').addEventListener('click', () => {
        loadReadingRooms();
        showNotification('새로고침 완료', 'success');
    });

    // QR 스캔 버튼
    document.getElementById('qrScanBtn').addEventListener('click', () => {
        openModal('qrModal');
    });

    // QR 스캔 확인
    document.getElementById('scanButton').addEventListener('click', () => {
        const qrCode = document.getElementById('qrCodeInput').value;
        if (qrCode) {
            scanQRCode(qrCode);
        } else {
            showNotification('QR 코드를 입력하세요', 'error');
        }
    });

    // 모달 닫기 버튼
    document.querySelectorAll('.close').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.target.closest('.modal').style.display = 'none';
        });
    });

    // 모달 외부 클릭시 닫기
    window.addEventListener('click', (e) => {
        if (e.target.classList.contains('modal')) {
            e.target.style.display = 'none';
        }
    });
}

// 열람실 목록 로드
async function loadReadingRooms() {
    try {
        const response = await fetch(`${API_BASE}/rooms`);
        const rooms = await response.json();
        displayReadingRooms(rooms);
        
        // 선택된 열람실이 있으면 좌석 정보도 로드
        if (selectedRoomId) {
            loadSeats(selectedRoomId);
        }
    } catch (error) {
        console.error('열람실 로드 실패:', error);
        // 오류 시 목 데이터 사용
        displayReadingRooms(getMockRooms());
    }
}

// 열람실 목록 표시
function displayReadingRooms(rooms) {
    const roomList = document.getElementById('roomList');
    roomList.innerHTML = '';

    rooms.forEach(room => {
        const roomCard = document.createElement('div');
        roomCard.className = 'room-card';
        if (room.roomId === selectedRoomId) {
            roomCard.classList.add('selected');
        }

        const occupiedRate = Math.round(((room.totalSeats - room.availableSeats) / room.totalSeats) * 100);

        roomCard.innerHTML = `
            <h3>${room.roomName}</h3>
            <div class="room-stats">
                <div class="stat">
                    <span class="stat-value">${room.totalSeats}</span>
                    <span class="stat-label">전체</span>
                </div>
                <div class="stat">
                    <span class="stat-value" style="color: #4caf50">${room.availableSeats}</span>
                    <span class="stat-label">공석</span>
                </div>
                <div class="stat">
                    <span class="stat-value" style="color: #f44336">${room.totalSeats - room.availableSeats}</span>
                    <span class="stat-label">사용중</span>
                </div>
                <div class="stat">
                    <span class="stat-value" style="color: #ff9800">${room.waitingCount || 0}</span>
                    <span class="stat-label">대기</span>
                </div>
            </div>
            <div style="margin-top: 15px; text-align: center;">
                <div style="background: #e0e0e0; height: 20px; border-radius: 10px; overflow: hidden;">
                    <div style="background: #667eea; height: 100%; width: ${occupiedRate}%; transition: width 0.3s;"></div>
                </div>
                <small style="color: #666; margin-top: 5px; display: block;">사용률 ${occupiedRate}%</small>
            </div>
        `;

        roomCard.addEventListener('click', () => {
            selectedRoomId = room.roomId;
            loadSeats(room.roomId);
            loadReadingRooms(); // 선택 상태 업데이트
        });

        roomList.appendChild(roomCard);
    });
}

// 좌석 정보 로드
async function loadSeats(roomId) {
    try {
        const response = await fetch(`${API_BASE}/rooms/${roomId}/seats`);
        const data = await response.json();
        displaySeats(data.seats, data.roomName);
        displayWaitingList(data.waitingList);
    } catch (error) {
        console.error('좌석 로드 실패:', error);
        // 오류 시 목 데이터 사용
        const mockData = getMockSeats(roomId);
        displaySeats(mockData.seats, mockData.roomName);
        displayWaitingList(mockData.waitingList);
    }
}

// 좌석 배치도 표시
function displaySeats(seats, roomName) {
    document.getElementById('selectedRoomName').textContent = `${roomName} - 좌석 배치도`;
    const seatGrid = document.getElementById('seatGrid');
    seatGrid.innerHTML = '';

    seats.forEach(seat => {
        const seatDiv = document.createElement('div');
        seatDiv.className = `seat ${seat.status.toLowerCase()}`;
        seatDiv.innerHTML = `
            <span class="seat-number">${seat.seatNumber}</span>
            <span class="seat-qr">${seat.qrCode}</span>
        `;

        if (seat.status === 'VACANT') {
            seatDiv.addEventListener('click', () => {
                applySeat(seat);
            });
        }

        seatGrid.appendChild(seatDiv);
    });
}

// 대기 목록 표시
function displayWaitingList(waitingList) {
    const waitingListDiv = document.getElementById('waitingList');
    
    if (!waitingList || waitingList.length === 0) {
        waitingListDiv.innerHTML = '<p class="no-data">대기 중인 학생이 없습니다.</p>';
        return;
    }

    waitingListDiv.innerHTML = '';
    waitingList.forEach((student, index) => {
        const waitingItem = document.createElement('div');
        waitingItem.className = 'waiting-item';
        waitingItem.innerHTML = `
            <div class="waiting-number">${index + 1}</div>
            <div class="waiting-info">
                <strong>${student.name}</strong> (${student.studentId})
                <br><small>${student.major}</small>
            </div>
        `;
        waitingListDiv.appendChild(waitingItem);
    });
}

// 좌석 신청
async function applySeat(seat) {
    if (!confirm(`좌석 ${seat.seatNumber}번을 신청하시겠습니까?`)) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/apply`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                studentId: currentStudent.studentId,
                roomId: selectedRoomId
            })
        });

        const result = await response.json();
        showNotification(result.message, 'success');
        
        if (result.applicationId) {
            myApplicationId = result.applicationId;
            updateMyApplications();
        }
        
        loadReadingRooms();
    } catch (error) {
        console.error('좌석 신청 실패:', error);
        // 시뮬레이션
        myApplicationId = 'APP-' + Date.now();
        showNotification(`좌석 ${seat.seatNumber}번이 신청되었습니다.`, 'success');
        updateMyApplications();
        
        // 좌석 상태 변경 (시뮬레이션)
        setTimeout(() => {
            loadSeats(selectedRoomId);
        }, 500);
    }
}

// 좌석 신청 취소
async function cancelApplication(applicationId) {
    if (!confirm('좌석 신청을 취소하시겠습니까?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/cancel`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                applicationId: applicationId,
                studentId: currentStudent.studentId
            })
        });

        const result = await response.json();
        showNotification(result.message, 'success');
        myApplicationId = null;
        updateMyApplications();
        loadReadingRooms();
    } catch (error) {
        console.error('취소 실패:', error);
        // 시뮬레이션
        myApplicationId = null;
        showNotification('신청이 취소되었습니다.', 'success');
        updateMyApplications();
        loadSeats(selectedRoomId);
    }
}

// QR 코드 스캔
async function scanQRCode(qrCode) {
    try {
        const response = await fetch(`${API_BASE}/scan`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                studentId: currentStudent.studentId,
                qrCode: qrCode
            })
        });

        const result = await response.json();
        showNotification(result.message, result.success ? 'success' : 'error');
        
        document.getElementById('qrModal').style.display = 'none';
        document.getElementById('qrCodeInput').value = '';
        
        if (result.success) {
            loadReadingRooms();
        }
    } catch (error) {
        console.error('QR 스캔 실패:', error);
        showNotification('QR 스캔 완료! 좌석이 배정되었습니다.', 'success');
        document.getElementById('qrModal').style.display = 'none';
        document.getElementById('qrCodeInput').value = '';
    }
}

// 내 신청 정보 업데이트
function updateMyApplications() {
    const myApplicationsDiv = document.getElementById('myApplications');
    
    if (!myApplicationId) {
        myApplicationsDiv.innerHTML = '<p class="no-data">신청한 좌석이 없습니다.</p>';
        return;
    }

    myApplicationsDiv.innerHTML = `
        <div class="application-item">
            <div class="application-header">
                <h4>좌석 신청 완료</h4>
                <button class="btn btn-danger" onclick="cancelApplication('${myApplicationId}')">취소하기</button>
            </div>
            <div class="application-details">
                <strong>신청 ID:</strong> ${myApplicationId}<br>
                <strong>신청 시간:</strong> ${new Date().toLocaleString('ko-KR')}<br>
                <strong>상태:</strong> <span style="color: #4caf50; font-weight: bold;">신청 완료</span>
            </div>
        </div>
    `;
}

// 모달 열기
function openModal(modalId) {
    document.getElementById(modalId).style.display = 'block';
}

// 알림 표시
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 20px 30px;
        background: ${type === 'success' ? '#4caf50' : type === 'error' ? '#f44336' : '#2196f3'};
        color: white;
        border-radius: 10px;
        box-shadow: 0 5px 15px rgba(0,0,0,0.3);
        z-index: 3000;
        animation: slideIn 0.3s ease;
        font-size: 1.1em;
    `;
    notification.textContent = message;
    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// 목 데이터 생성 (서버 미연결 시)
function getMockRooms() {
    return [
        {
            roomId: 'room1',
            roomName: '제1열람실',
            totalSeats: 50,
            availableSeats: 23,
            waitingCount: 5
        },
        {
            roomId: 'room2',
            roomName: '제2열람실',
            totalSeats: 40,
            availableSeats: 15,
            waitingCount: 2
        },
        {
            roomId: 'room3',
            roomName: '제3열람실',
            totalSeats: 60,
            availableSeats: 35,
            waitingCount: 0
        }
    ];
}

function getMockSeats(roomId) {
    const seats = [];
    const totalSeats = 30;
    
    for (let i = 1; i <= totalSeats; i++) {
        const statuses = ['VACANT', 'OCCUPIED', 'TEMPORARILY_ABSENT'];
        const randomStatus = statuses[Math.floor(Math.random() * statuses.length)];
        
        seats.push({
            seatId: `seat${i}`,
            seatNumber: i,
            qrCode: `QR-${String(i).padStart(3, '0')}`,
            status: i % 3 === 0 ? 'VACANT' : (i % 5 === 0 ? 'TEMPORARILY_ABSENT' : 'OCCUPIED')
        });
    }

    return {
        roomName: '제1열람실',
        seats: seats,
        waitingList: [
            { name: '김철수', studentId: '2021002', major: '전자공학과' },
            { name: '이영희', studentId: '2021003', major: '경영학과' }
        ]
    };
}

// CSS 애니메이션 추가
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(400px);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(400px);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);
