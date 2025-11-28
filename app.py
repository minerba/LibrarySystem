"""
도서관 좌석 관리 시스템 - Python 버전
"""
from flask import Flask, jsonify, request, send_from_directory
import random
import uuid
from datetime import datetime
from typing import Dict, List, Optional
from dataclasses import dataclass, asdict, field

app = Flask(__name__, static_folder='src/main/resources/static', static_url_path='')

# 데이터 모델
@dataclass
class StudentInfo:
    student_id: str
    name: str
    major: str
    phone_number: str
    no_show_count: int = 0
    total_usage_time: int = 0

@dataclass
class Student:
    student_info: StudentInfo

@dataclass
class Seat:
    seat_id: str
    seat_number: int
    qr_code: str
    status: str  # VACANT, OCCUPIED, TEMPORARILY_ABSENT
    current_student: Optional[Student] = None

@dataclass
class WaitingList:
    list_id: str
    reading_room_id: str
    waiting_students: List[Student] = field(default_factory=list)

@dataclass
class ReadingRoom:
    room_id: str
    room_name: str
    total_seats: int
    available_seats: int
    seat_list: List[Seat] = field(default_factory=list)
    waiting_list: Optional[WaitingList] = None

# 전역 데이터 저장소
reading_rooms: Dict[str, ReadingRoom] = {}
students: Dict[str, Student] = {}
applications: Dict[str, Dict] = {}
user_accounts: Dict[str, Dict] = {}  # 학번: {password, studentInfo}

def initialize_data():
    """초기 데이터 생성"""
    global reading_rooms
    
    # 열람실 3개 생성
    rooms_config = [
        ("제1열람실", 50),
        ("제2열람실", 40),
        ("제3열람실", 60)
    ]
    
    for room_name, total_seats in rooms_config:
        room_id = str(uuid.uuid4())
        seats = []
        
        # 좌석 생성
        for i in range(1, total_seats + 1):
            seat = Seat(
                seat_id=str(uuid.uuid4()),
                seat_number=i,
                qr_code=f"QR-{str(i).zfill(3)}",
                status="VACANT"
            )
            
            # 랜덤으로 일부 좌석 점유 (50% 정도)
            if random.random() < 0.5:
                student_info = StudentInfo(
                    student_id=f"STU{1000 + i}",
                    name=f"학생{i}",
                    major="컴퓨터공학과",
                    phone_number=f"010-{random.randint(1000,9999)}-{random.randint(1000,9999)}"
                )
                student = Student(student_info=student_info)
                seat.current_student = student
                seat.status = "OCCUPIED"
            
            seats.append(seat)
        
        # 사용 가능한 좌석 수 계산
        available = sum(1 for s in seats if s.status == "VACANT")
        
        room = ReadingRoom(
            room_id=room_id,
            room_name=room_name,
            total_seats=total_seats,
            available_seats=available,
            seat_list=seats,
            waiting_list=WaitingList(
                list_id=str(uuid.uuid4()),
                reading_room_id=room_id,
                waiting_students=[]
            )
        )
        
        reading_rooms[room_id] = room
    
    print("✅ 테스트 데이터 초기화 완료!")
    print(f"📚 열람실 {len(reading_rooms)}개 생성됨")

def seat_to_dict(seat: Seat) -> dict:
    """Seat 객체를 딕셔너리로 변환"""
    result = {
        "seatId": seat.seat_id,
        "seatNumber": seat.seat_number,
        "qrCode": seat.qr_code,
        "status": seat.status
    }
    if seat.current_student:
        result["studentName"] = seat.current_student.student_info.name
    return result

def student_to_dict(student: Student) -> dict:
    """Student 객체를 딕셔너리로 변환"""
    return {
        "studentId": student.student_info.student_id,
        "name": student.student_info.name,
        "major": student.student_info.major
    }

# API 엔드포인트
@app.route('/')
def index():
    """메인 페이지"""
    return send_from_directory(app.static_folder, 'index.html')

@app.route('/api/signup', methods=['POST'])
def signup():
    """회원가입"""
    data = request.json
    name = data.get('name')
    major = data.get('major')
    phone_number = data.get('phoneNumber')
    password = data.get('password')
    
    if not all([name, major, phone_number, password]):
        return jsonify({"success": False, "message": "모든 항목을 입력해주세요."}), 400
    
    # 학번 자동 생성 (년도 + 순번)
    import time
    student_id = f"STU{int(time.time()) % 1000000}"
    
    # 학생 정보 생성
    student_info = StudentInfo(
        student_id=student_id,
        name=name,
        major=major,
        phone_number=phone_number
    )
    
    # 계정 저장
    user_accounts[student_id] = {
        "password": password,
        "studentInfo": student_info
    }
    
    students[student_id] = Student(student_info=student_info)
    
    return jsonify({
        "success": True,
        "message": "회원가입이 완료되었습니다.",
        "studentId": student_id
    })

@app.route('/api/login', methods=['POST'])
def login():
    """로그인"""
    data = request.json
    student_id = data.get('studentId')
    password = data.get('password')
    
    if not student_id or not password:
        return jsonify({"success": False, "message": "학번과 비밀번호를 입력해주세요."}), 400
    
    if student_id not in user_accounts:
        return jsonify({"success": False, "message": "존재하지 않는 학번입니다."}), 404
    
    account = user_accounts[student_id]
    if account["password"] != password:
        return jsonify({"success": False, "message": "비밀번호가 일치하지 않습니다."}), 401
    
    student_info = account["studentInfo"]
    
    return jsonify({
        "success": True,
        "message": "로그인 성공",
        "student": {
            "studentId": student_info.student_id,
            "name": student_info.name,
            "major": student_info.major,
            "phoneNumber": student_info.phone_number
        }
    })

@app.route('/api/rooms', methods=['GET'])
def get_rooms():
    """열람실 목록 조회"""
    rooms_data = []
    for room in reading_rooms.values():
        rooms_data.append({
            "roomId": room.room_id,
            "roomName": room.room_name,
            "totalSeats": room.total_seats,
            "availableSeats": room.available_seats,
            "waitingCount": len(room.waiting_list.waiting_students) if room.waiting_list else 0
        })
    return jsonify(rooms_data)

@app.route('/api/rooms/<room_id>/seats', methods=['GET'])
def get_seats(room_id):
    """특정 열람실의 좌석 정보 조회"""
    if room_id not in reading_rooms:
        return jsonify({"error": "열람실을 찾을 수 없습니다."}), 404
    
    room = reading_rooms[room_id]
    
    response = {
        "roomName": room.room_name,
        "seats": [seat_to_dict(seat) for seat in room.seat_list],
        "waitingList": [student_to_dict(s) for s in room.waiting_list.waiting_students] if room.waiting_list else []
    }
    
    return jsonify(response)

@app.route('/api/apply', methods=['POST'])
def apply_seat():
    """좌석 신청"""
    data = request.json
    student_id = data.get('studentId')
    room_id = data.get('roomId')
    
    if room_id not in reading_rooms:
        return jsonify({"success": False, "message": "열람실을 찾을 수 없습니다."}), 404
    
    # 이미 좌석을 배정받았는지 확인 (모든 열람실 검색)
    for app_id, app_info in applications.items():
        if app_info['studentId'] == student_id:
            return jsonify({
                "success": False,
                "message": "이미 좌석을 배정받았습니다. 기존 좌석을 취소한 후 다시 신청해주세요."
            }), 400
    
    room = reading_rooms[room_id]
    
    # 학생 정보 가져오기
    if student_id not in students:
        return jsonify({"success": False, "message": "로그인이 필요합니다."}), 401
    
    student = students[student_id]
    
    # 빈 좌석 찾기
    vacant_seat = None
    for seat in room.seat_list:
        if seat.status == "VACANT":
            vacant_seat = seat
            break
    
    if vacant_seat:
        # 좌석 배정
        vacant_seat.current_student = student
        vacant_seat.status = "OCCUPIED"
        room.available_seats -= 1
        
        application_id = str(uuid.uuid4())
        applications[application_id] = {
            "studentId": student_id,
            "roomId": room_id,
            "seatId": vacant_seat.seat_id,
            "seatNumber": vacant_seat.seat_number,
            "timestamp": datetime.now().isoformat()
        }
        
        return jsonify({
            "success": True,
            "message": f"좌석 {vacant_seat.seat_number}번이 배정되었습니다.",
            "applicationId": application_id
        })
    else:
        # 대기 목록에 추가
        room.waiting_list.waiting_students.append(student)
        return jsonify({
            "success": True,
            "message": f"빈 좌석이 없습니다. 대기 목록에 추가되었습니다. 현재 대기 순서: {len(room.waiting_list.waiting_students)}"
        })

@app.route('/api/cancel', methods=['POST'])
def cancel_application():
    """좌석 신청 취소"""
    data = request.json
    application_id = data.get('applicationId')
    student_id = data.get('studentId')
    
    if application_id not in applications:
        return jsonify({"success": False, "message": "신청 정보를 찾을 수 없습니다."}), 404
    
    app_info = applications[application_id]
    room_id = app_info['roomId']
    seat_id = app_info['seatId']
    
    if room_id in reading_rooms:
        room = reading_rooms[room_id]
        for seat in room.seat_list:
            if seat.seat_id == seat_id:
                seat.current_student = None
                seat.status = "VACANT"
                room.available_seats += 1
                del applications[application_id]
                
                # 대기자가 있으면 자동 배정
                if room.waiting_list.waiting_students:
                    next_student = room.waiting_list.waiting_students.pop(0)
                    seat.current_student = next_student
                    seat.status = "OCCUPIED"
                    room.available_seats -= 1
                
                return jsonify({
                    "success": True,
                    "message": "좌석 신청이 취소되었습니다."
                })
    
    return jsonify({"success": False, "message": "좌석 정보를 찾을 수 없습니다."}), 404

@app.route('/api/scan', methods=['POST'])
def scan_qr():
    """QR 코드 스캔"""
    data = request.json
    student_id = data.get('studentId')
    qr_code = data.get('qrCode')
    
    # 학생 정보 생성 또는 가져오기
    if student_id not in students:
        student_info = StudentInfo(
            student_id=student_id,
            name="홍길동",
            major="컴퓨터공학과",
            phone_number="010-1234-5678"
        )
        students[student_id] = Student(student_info=student_info)
    
    student = students[student_id]
    
    # 모든 열람실에서 QR 코드로 좌석 찾기
    for room in reading_rooms.values():
        for seat in room.seat_list:
            if seat.qr_code == qr_code:
                if seat.status == "VACANT":
                    seat.current_student = student
                    seat.status = "OCCUPIED"
                    room.available_seats -= 1
                    return jsonify({
                        "success": True,
                        "message": f"QR 스캔 완료. 좌석 {seat.seat_number}번이 배정되었습니다."
                    })
                else:
                    return jsonify({
                        "success": False,
                        "message": "이미 사용중인 좌석입니다."
                    })
    
    return jsonify({
        "success": False,
        "message": "유효하지 않은 QR 코드입니다."
    })

@app.route('/api/system/status', methods=['GET'])
def get_system_status():
    """시스템 상태 조회"""
    status_text = "=== 도서관 좌석 관리 시스템 상태 ===\n\n"
    for room in reading_rooms.values():
        status_text += f"{room.room_name} - 전체: {room.total_seats}, "
        status_text += f"사용가능: {room.available_seats}, "
        status_text += f"대기: {len(room.waiting_list.waiting_students) if room.waiting_list else 0}\n"
    
    return jsonify({"status": status_text})

if __name__ == '__main__':
    print("\n" + "="*50)
    print("  🏫 도서관 좌석 관리 시스템 시작 (Python 버전)")
    print("="*50 + "\n")
    
    initialize_data()
    
    print("\n" + "="*50)
    print("  ✅ 서버가 성공적으로 시작되었습니다!")
    print("  🌐 브라우저에서 http://localhost:7000 접속")
    print("="*50 + "\n")
    
    app.run(host='0.0.0.0', port=7000, debug=True)
