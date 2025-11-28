package com.library.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * CCTV를 나타내는 클래스
 */
@Getter
@Setter
public class CCTV {
    private String cctvId;
    private String status;
    private boolean power;
    private List<VideoInfo> videoHistory;

    public CCTV(String cctvId) {
        this.cctvId = cctvId;
        this.status = "정상";
        this.power = true;
        this.videoHistory = new ArrayList<>();
    }

    /**
     * 영상 정보 가져오기
     */
    public VideoInfo getVideoInfo() {
        VideoInfo video = new VideoInfo(this.cctvId);
        videoHistory.add(video);
        return video;
    }

    /**
     * CCTV 전원 제어
     */
    public void setPower(boolean power) {
        this.power = power;
        this.status = power ? "정상" : "전원꺼짐";
    }

    /**
     * CCTV 재부팅
     */
    public void reboot() {
        System.out.println("CCTV " + cctvId + " 재부팅 중...");
        this.power = false;
        try {
            Thread.sleep(1000); // 재부팅 시뮬레이션
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.power = true;
        this.status = "정상";
        System.out.println("CCTV " + cctvId + " 재부팅 완료");
    }
    
    public List<VideoInfo> getVideoHistory() {
        return new ArrayList<>(videoHistory);
    }
}
