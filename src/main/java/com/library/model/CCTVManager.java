package com.library.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * CCTV 관리자 클래스
 */
@Getter
public class CCTVManager {
    private String ipAddress;
    private List<CCTV> cctvList;

    public CCTVManager(String ipAddress) {
        this.ipAddress = ipAddress;
        this.cctvList = new ArrayList<>();
    }

    /**
     * CCTV 추가
     */
    public void addCCTV(CCTV cctv) {
        cctvList.add(cctv);
        System.out.println("CCTV " + cctv.getCctvId() + " 추가됨");
    }

    /**
     * 빈 좌석 감지
     */
    public boolean detectVacancy() {
        for (CCTV cctv : cctvList) {
            if (cctv.isPower()) {
                VideoInfo video = cctv.getVideoInfo();
                video.analyzeVacancy();
            }
        }
        return true;
    }

    /**
     * 백업 CCTV로 전환
     */
    public void switchToBackup(String failedCctvId) {
        System.out.println("CCTV " + failedCctvId + "의 백업으로 전환 중...");
        // 백업 CCTV 전환 로직
    }

    /**
     * CCTV 상태 조회
     */
    public String getCCTVStatus() {
        StringBuilder status = new StringBuilder("=== CCTV 상태 ===\n");
        for (CCTV cctv : cctvList) {
            status.append(String.format("CCTV %s: %s (전원: %s)\n", 
                cctv.getCctvId(), cctv.getStatus(), cctv.isPower() ? "ON" : "OFF"));
        }
        return status.toString();
    }

    /**
     * CCTV 전원 제어
     */
    public void setCCTVPower(String cctvId, boolean power) {
        for (CCTV cctv : cctvList) {
            if (cctv.getCctvId().equals(cctvId)) {
                cctv.setPower(power);
                break;
            }
        }
    }

    /**
     * CCTV 재부팅
     */
    public void rebootCCTV(String cctvId) {
        for (CCTV cctv : cctvList) {
            if (cctv.getCctvId().equals(cctvId)) {
                cctv.reboot();
                break;
            }
        }
    }
    
    public List<CCTV> getCctvList() {
        return new ArrayList<>(cctvList);
    }
}
