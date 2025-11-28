package com.library.service;

import com.library.controller.LibraryManagementSystem;
import com.library.model.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 도서관 좌석 관리 서비스
 */
@Service
public class LibraryService {
    
    private final LibraryManagementSystem system;
    private final Map<String, Student> studentCache;
    
    public LibraryService() {
        this.system = new LibraryManagementSystem();
        this.studentCache = new HashMap<>();
    }
    
    @PostConstruct
    public void initializeTestData() {
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
    public List<ReadingRoom> getReadingRoomList() {
        return system.getReadingRoomList();
    }
    
    /**
     * 열람실 ID로 찾기
     */
    public ReadingRoom findReadingRoomById(String roomId) {
        return system.findReadingRoomById(roomId);
    }
    
    /**
     * 좌석 신청
     */
    public String applySeat(String studentId, String roomId) {
        Student student = getOrCreateStudent(studentId);
        return system.applyForSeat(student, roomId);
    }
    
    /**
     * 좌석 신청 취소
     */
    public String cancelApplication(String applicationId, String studentId) {
        return system.cancelApplication(applicationId, studentId);
    }
    
    /**
     * QR 코드 스캔
     */
    public String scanQRCode(String studentId, String qrCode) {
        Student student = getOrCreateStudent(studentId);
        return system.scanQRCode(student, qrCode);
    }
    
    /**
     * 시스템 상태 조회
     */
    public String getSystemStatus() {
        return system.getSystemStatus();
    }
    
    /**
     * 학생 객체 가져오기 또는 생성
     */
    private Student getOrCreateStudent(String studentId) {
        return studentCache.computeIfAbsent(studentId, id -> {
            StudentInfo info = new StudentInfo(id, "학생" + id, "컴퓨터공학과", "010-0000-0000");
            return new Student(info);
        });
    }
}
