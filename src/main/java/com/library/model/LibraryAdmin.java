package com.library.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 도서관 관리자 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LibraryAdmin {
    private String adminId;
    private String name;

    /**
     * 열람실 등록
     */
    public ReadingRoom registerReadingRoom(String roomName, int totalSeats) {
        System.out.println("관리자 " + name + "이(가) 열람실 " + roomName + "을(를) 등록합니다.");
        return new ReadingRoom(roomName, totalSeats);
    }

    /**
     * CCTV 등록
     */
    public CCTV registerCCTV(String cctvId) {
        System.out.println("관리자 " + name + "이(가) CCTV " + cctvId + "을(를) 등록합니다.");
        return new CCTV(cctvId);
    }

    /**
     * 통계 조회
     */
    public void viewStatistics(String period) {
        System.out.println("=== " + period + " 통계 ===");
        System.out.println("통계 데이터를 조회합니다.");
    }

    /**
     * 배정 알고리즘 변경
     */
    public void changeAssignmentAlgorithm() {
        System.out.println("좌석 배정 알고리즘을 변경합니다.");
    }

    /**
     * CCTV 상태 모니터링
     */
    public void monitoringCCTVStatus(CCTVManager cctvManager) {
        System.out.println(cctvManager.getCCTVStatus());
    }

    /**
     * CCTV 복구
     */
    public void recoveryCCTV(CCTVManager cctvManager, String cctvId) {
        System.out.println("CCTV " + cctvId + " 복구를 시작합니다.");
        cctvManager.rebootCCTV(cctvId);
    }
}
