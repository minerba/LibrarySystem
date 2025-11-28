package com.library.model;

import lombok.Data;
import lombok.AllArgsConstructor;

/**
 * 학생을 나타내는 클래스
 */
@Data
@AllArgsConstructor
public class Student {
    private StudentInfo studentInfo;

    /**
     * 열람실 신청
     */
    public boolean applyForReadingRoom(String roomId) {
        System.out.println(studentInfo.getName() + "이(가) " + roomId + " 열람실을 신청합니다.");
        return true;
    }

    /**
     * 신청 취소
     */
    public boolean cancelApplication(String applicationId) {
        System.out.println(studentInfo.getName() + "이(가) " + applicationId + " 신청을 취소합니다.");
        return true;
    }

    /**
     * QR 코드 스캔
     */
    public boolean scanQRCode(String seatId) {
        System.out.println(studentInfo.getName() + "이(가) " + seatId + " 좌석 QR을 스캔합니다.");
        return true;
    }
}
