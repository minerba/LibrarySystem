package com.library.controller;

import com.library.model.*;
import com.library.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Spring Boot REST API 컨트롤러
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LibraryController {
    
    @Autowired
    private LibraryService libraryService;
    
    /**
     * 테스트 데이터 초기화
     */
    private void initializeTestData() {
        // 관리자 생성
        LibraryAdmin admin = new LibraryAdmin("admin001", "관리자");

        // 열람실 등록
        ReadingRoom room1 = admin.registerReadingRoom("제1열람실", 50);
        ReadingRoom room2 = admin.registerReadingRoom("제2열람실", 40);
        ReadingRoom room3 = admin.registerReadingRoom("제3열람실", 60);

        system.addReadingRoom(room1);
        system.addReadingRoom(room2);
        system.addReadingRoom(room3);

        // CCTV 등록
        CCTV cctv1 = admin.registerCCTV("CCTV-001");
        CCTV cctv2 = admin.registerCCTV("CCTV-002");
        CCTV cctv3 = admin.registerCCTV("CCTV-003");

        system.getCctvManager().addCCTV(cctv1);
        system.getCctvManager().addCCTV(cctv2);
        system.getCctvManager().addCCTV(cctv3);

        // 일부 좌석 랜덤으로 배정
        Random random = new Random();
        for (ReadingRoom room : system.getReadingRoomList()) {
            int occupiedSeats = room.getTotalSeats() / 2 + random.nextInt(room.getTotalSeats() / 4);
            for (int i = 0; i < occupiedSeats && i < room.getSeatList().size(); i++) {
                Seat seat = room.getSeatList().get(i);
                StudentInfo info = new StudentInfo(
                    "STU" + (1000 + i),
                    "학생" + i,
                    "컴퓨터공학과",
                    "010-0000-" + String.format("%04d", i)
                );
                Student student = new Student(info);
                seat.assignSeat(student);
            }
            room.updateSeatStatus();
        }

        System.out.println("✅ 테스트 데이터 초기화 완료");
        System.out.println(system.getSystemStatus());
    }
    
    /**
     * 열람실 목록 조회
     */
    @GetMapping("/rooms")
    public List<Map<String, Object>> getReadingRooms() {
        return system.getReadingRoomList().stream()
            .map(room -> {
                Map<String, Object> roomData = new HashMap<>();
                roomData.put("roomId", room.getRoomId());
                roomData.put("roomName", room.getRoomName());
                roomData.put("totalSeats", room.getTotalSeats());
                roomData.put("availableSeats", room.getAvailableSeats());
                roomData.put("waitingCount", room.getWaitingList().getSize());
                return roomData;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 특정 열람실의 좌석 정보 조회
     */
    @GetMapping("/rooms/{roomId}/seats")
    public Map<String, Object> getSeats(@PathVariable String roomId) {
        ReadingRoom room = libraryService.findReadingRoomById(roomId);
        
        if (room == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "열람실을 찾을 수 없습니다.");
            return error;
        }
        
        List<Map<String, Object>> seats = room.getSeatList().stream()
            .map(seat -> {
                Map<String, Object> seatData = new HashMap<>();
                seatData.put("seatId", seat.getSeatId());
                seatData.put("seatNumber", seat.getSeatNumber());
                seatData.put("qrCode", seat.getQrCode());
                seatData.put("status", seat.getStatus().toString());
                if (seat.getCurrentStudent() != null) {
                    seatData.put("studentName", seat.getCurrentStudent().getStudentInfo().getName());
                }
                return seatData;
            })
            .collect(Collectors.toList());
        
        List<Map<String, Object>> waitingList = room.getWaitingList().getWaitingStudents().stream()
            .map(student -> {
                Map<String, Object> studentData = new HashMap<>();
                studentData.put("studentId", student.getStudentInfo().getStudentId());
                studentData.put("name", student.getStudentInfo().getName());
                studentData.put("major", student.getStudentInfo().getMajor());
                return studentData;
            })
            .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("roomName", room.getRoomName());
        response.put("seats", seats);
        response.put("waitingList", waitingList);
        
        return response;
    }
    
    /**
     * 좌석 신청
     */
    @PostMapping("/apply")
    public Map<String, Object> applySeat(@RequestBody Map<String, String> request) {
        String studentId = request.get("studentId");
        String roomId = request.get("roomId");
        
        String result = libraryService.applySeat(studentId, roomId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", result);
        response.put("applicationId", UUID.randomUUID().toString());
        
        return response;
    }
    
    /**
     * 좌석 신청 취소
     */
    @PostMapping("/cancel")
    @PostMapping("/cancel")
    public Map<String, Object> cancelApplication(@RequestBody Map<String, String> request) {
        String applicationId = request.get("applicationId");
        String studentId = request.get("studentId");
        
        String result = libraryService.cancelApplication(applicationId, studentId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", result);
        
        return response;
    }
    
    /**
     * QR 코드 스캔
     */
    @PostMapping("/scan")
    @PostMapping("/scan")
    public Map<String, Object> scanQRCode(@RequestBody Map<String, String> request) {
        String studentId = request.get("studentId");
        String qrCode = request.get("qrCode");
        
        String result = libraryService.scanQRCode(studentId, qrCode);
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.contains("완료") || result.contains("배정"));
        response.put("message", result);
        
        return response;
    }
    
    /**
     * 시스템 상태 조회
     */
    @GetMapping("/system/status")
    @GetMapping("/system/status")
    public Map<String, String> getSystemStatus() {
        String status = libraryService.getSystemStatus();
        Map<String, String> response = new HashMap<>();
        response.put("status", status);
        return response;
    }
}