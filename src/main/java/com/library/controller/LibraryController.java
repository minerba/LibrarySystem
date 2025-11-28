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
     * 열람실 목록 조회
     */
    @GetMapping("/rooms")
    public List<Map<String, Object>> getReadingRooms() {
        return libraryService.getReadingRooms().stream()
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
    public Map<String, String> getSystemStatus() {
        String status = libraryService.getSystemStatus();
        Map<String, String> response = new HashMap<>();
        response.put("status", status);
        return response;
    }
}