package com.library.controller;

import com.library.model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 도서관 관리 시스템 컨트롤러
 */
public class LibraryManagementSystem {
    private List<ReadingRoom> readingRoomList;
    private CCTVManager cctvManager;
    private AssignmentAlgorithm assignmentAlgorithm;
    private Map<String, String> applicationMap; // applicationId -> studentId
    
    public LibraryManagementSystem() {
        this.readingRoomList = new ArrayList<>();
        this.cctvManager = new CCTVManager("192.168.1.100");
        this.assignmentAlgorithm = new FIFOAssignmentAlgorithm();
        this.applicationMap = new HashMap<>();
    }

    /**
     * 열람실 추가
     */
    public void addReadingRoom(ReadingRoom room) {
        readingRoomList.add(room);
        System.out.println("열람실 " + room.getRoomName() + " 추가됨");
    }

    /**
     * 열람실 목록 조회
     */
    public List<ReadingRoom> getReadingRoomList() {
        return new ArrayList<>(readingRoomList);
    }

    /**
     * 열람실 ID로 찾기
     */
    public ReadingRoom findReadingRoomById(String roomId) {
        for (ReadingRoom room : readingRoomList) {
            if (room.getRoomId().equals(roomId)) {
                return room;
            }
        }
        return null;
    }

    /**
     * 좌석 신청 처리
     */
    public String applyForSeat(Student student, String roomId) {
        ReadingRoom room = findReadingRoomById(roomId);
        if (room == null) {
            return "열람실을 찾을 수 없습니다.";
        }

        Seat vacantSeat = room.findVacantSeat();
        if (vacantSeat != null) {
            // 빈 좌석이 있으면 바로 배정
            vacantSeat.assignSeat(student);
            room.updateSeatStatus();
            String applicationId = java.util.UUID.randomUUID().toString();
            applicationMap.put(applicationId, student.getStudentInfo().getStudentId());
            return "좌석 " + vacantSeat.getSeatNumber() + "번이 배정되었습니다. (신청ID: " + applicationId + ")";
        } else {
            // 빈 좌석이 없으면 대기 목록에 추가
            room.getWaitingList().addStudent(student);
            return "빈 좌석이 없습니다. 대기 목록에 추가되었습니다. 현재 대기 순서: " + 
                   room.getWaitingList().getSize();
        }
    }

    /**
     * 좌석 신청 취소
     */
    public String cancelApplication(String applicationId, String studentId) {
        if (!applicationMap.containsKey(applicationId)) {
            return "신청 정보를 찾을 수 없습니다.";
        }

        // 모든 열람실에서 해당 학생의 좌석 찾기
        for (ReadingRoom room : readingRoomList) {
            for (Seat seat : room.getSeatList()) {
                if (seat.getCurrentStudent() != null && 
                    seat.getCurrentStudent().getStudentInfo().getStudentId().equals(studentId)) {
                    seat.setSeatStatus(SeatStatus.VACANT);
                    room.updateSeatStatus();
                    applicationMap.remove(applicationId);
                    
                    // 대기자가 있으면 자동 배정
                    processWaitingList(room);
                    
                    return "좌석 신청이 취소되었습니다.";
                }
            }
            
            // 대기 목록에서도 제거
            if (room.getWaitingList().removeStudent(studentId)) {
                return "대기 목록에서 제거되었습니다.";
            }
        }

        return "좌석 정보를 찾을 수 없습니다.";
    }

    /**
     * QR 코드 스캔으로 좌석 등록
     */
    public String scanQRCode(Student student, String qrCode) {
        for (ReadingRoom room : readingRoomList) {
            for (Seat seat : room.getSeatList()) {
                if (seat.getQrCode().equals(qrCode)) {
                    if (seat.getStatus() == SeatStatus.VACANT) {
                        seat.assignSeat(student);
                        room.updateSeatStatus();
                        return "QR 스캔 완료. 좌석 " + seat.getSeatNumber() + "번이 배정되었습니다.";
                    } else {
                        return "이미 사용중인 좌석입니다.";
                    }
                }
            }
        }
        return "유효하지 않은 QR 코드입니다.";
    }

    /**
     * 대기 목록 처리
     */
    private void processWaitingList(ReadingRoom room) {
        Seat vacantSeat = room.findVacantSeat();
        if (vacantSeat != null) {
            Student nextStudent = assignmentAlgorithm.selectStudent(room.getWaitingList());
            if (nextStudent != null) {
                vacantSeat.assignSeat(nextStudent);
                room.updateSeatStatus();
                System.out.println("대기자 " + nextStudent.getStudentInfo().getName() + 
                                 "에게 좌석이 자동 배정되었습니다.");
            }
        }
    }

    /**
     * CCTV 상태 조회
     */
    public String getCCTVStatus() {
        return cctvManager.getCCTVStatus();
    }

    /**
     * CCTV 복구 프로세스
     */
    public void recoveryProcess(String cctvId) {
        cctvManager.rebootCCTV(cctvId);
    }

    /**
     * 배정 알고리즘 변경
     */
    public void changeAssignmentAlgorithm(AssignmentAlgorithm newAlgorithm) {
        this.assignmentAlgorithm = newAlgorithm;
        System.out.println("배정 알고리즘이 변경되었습니다.");
    }

    /**
     * 전체 시스템 상태 조회
     */
    public String getSystemStatus() {
        StringBuilder status = new StringBuilder("=== 도서관 좌석 관리 시스템 상태 ===\n\n");
        
        for (ReadingRoom room : readingRoomList) {
            status.append(room.getReadingRoomStatus()).append("\n");
        }
        
        status.append("\n").append(cctvManager.getCCTVStatus());
        
        return status.toString();
    }

    // Getters
    public CCTVManager getCctvManager() {
        return cctvManager;
    }

    public AssignmentAlgorithm getAssignmentAlgorithm() {
        return assignmentAlgorithm;
    }
}
