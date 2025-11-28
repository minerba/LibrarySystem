package com.library.api;

import com.library.controller.LibraryManagementSystem;
import com.library.model.*;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.*;
import java.util.stream.Collectors;

/**
 * REST API 서버
 */
public class ApiServer {
    private final LibraryManagementSystem system;
    private final Gson gson;
    private final Map<String, Student> studentCache;

    public ApiServer() {
        this.system = new LibraryManagementSystem();
        this.gson = new Gson();
        this.studentCache = new HashMap<>();
        initializeTestData();
    }

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

        // 일부 좌석 랜덤으로 배정 (시뮬레이션)
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

        System.out.println("테스트 데이터 초기화 완료");
        System.out.println(system.getSystemStatus());
    }

    /**
     * API 서버 시작
     */
    public void start(int port) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/static");
            config.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            });
        }).start(port);

        // 기본 라우트
        app.get("/", ctx -> ctx.redirect("/index.html"));

        // API 라우트
        app.get("/api/rooms", this::getReadingRooms);
        app.get("/api/rooms/{roomId}/seats", this::getSeats);
        app.post("/api/apply", this::applySeat);
        app.post("/api/cancel", this::cancelApplication);
        app.post("/api/scan", this::scanQRCode);
        app.get("/api/system/status", this::getSystemStatus);

        System.out.println("서버가 포트 " + port + "에서 실행중입니다.");
        System.out.println("브라우저에서 http://localhost:" + port + " 을 열어주세요.");
    }

    /**
     * 열람실 목록 조회
     */
    private void getReadingRooms(Context ctx) {
        List<Map<String, Object>> rooms = system.getReadingRoomList().stream()
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

        ctx.json(rooms);
    }

    /**
     * 특정 열람실의 좌석 정보 조회
     */
    private void getSeats(Context ctx) {
        String roomId = ctx.pathParam("roomId");
        ReadingRoom room = system.findReadingRoomById(roomId);

        if (room == null) {
            ctx.status(404).json(Map.of("error", "열람실을 찾을 수 없습니다."));
            return;
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

        ctx.json(response);
    }

    /**
     * 좌석 신청
     */
    private void applySeat(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String studentId = body.get("studentId");
        String roomId = body.get("roomId");

        Student student = getOrCreateStudent(studentId);
        String result = system.applyForSeat(student, roomId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", result);
        response.put("applicationId", UUID.randomUUID().toString());

        ctx.json(response);
    }

    /**
     * 좌석 신청 취소
     */
    private void cancelApplication(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String applicationId = body.get("applicationId");
        String studentId = body.get("studentId");

        String result = system.cancelApplication(applicationId, studentId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", result);

        ctx.json(response);
    }

    /**
     * QR 코드 스캔
     */
    private void scanQRCode(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String studentId = body.get("studentId");
        String qrCode = body.get("qrCode");

        Student student = getOrCreateStudent(studentId);
        String result = system.scanQRCode(student, qrCode);

        Map<String, Object> response = new HashMap<>();
        response.put("success", result.contains("완료") || result.contains("배정"));
        response.put("message", result);

        ctx.json(response);
    }

    /**
     * 시스템 상태 조회
     */
    private void getSystemStatus(Context ctx) {
        String status = system.getSystemStatus();
        ctx.json(Map.of("status", status));
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

    public LibraryManagementSystem getSystem() {
        return system;
    }
}
