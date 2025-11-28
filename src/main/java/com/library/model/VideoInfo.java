package com.library.model;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * CCTV 영상 정보를 담는 클래스
 */
@Getter
public class VideoInfo {
    private String videoId;
    private LocalDateTime timestamp;
    private String cctvId;

    public VideoInfo(String cctvId) {
        this.videoId = java.util.UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.cctvId = cctvId;
    }

    /**
     * 영상 분석을 통한 빈 좌석 감지
     * 실제로는 AI/영상처리 로직이 들어가야 하지만, 여기서는 시뮬레이션
     */
    public boolean analyzeVacancy() {
        System.out.println("CCTV " + cctvId + " 영상 분석 중...");
        // 실제 구현에서는 영상 분석 로직 추가
        return true;
    }
}
