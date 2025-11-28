package com.library.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * 좌석을 나타내는 클래스
 */
@Getter
@Setter
public class Seat {
    private String seatId;
    private int seatNumber;
    private String qrCode;
    private SeatStatus status;
    private Student currentStudent;

    public Seat(int seatNumber) {
        this.seatId = UUID.randomUUID().toString();
        this.seatNumber = seatNumber;
        this.qrCode = generateQRCode();
        this.status = SeatStatus.VACANT;
        this.currentStudent = null;
    }

    /**
     * QR 코드 생성
     */
    private String generateQRCode() {
        return "QR-" + seatId.substring(0, 8);
    }

    /**
     * 좌석 배정
     */
    public boolean assignSeat(Student student) {
        if (this.status == SeatStatus.VACANT) {
            this.currentStudent = student;
            this.status = SeatStatus.OCCUPIED;
            System.out.println("좌석 " + seatNumber + "이(가) " + 
                             student.getStudentInfo().getName() + "에게 배정되었습니다.");
            return true;
        }
        return false;
    }

    /**
     * 좌석 상태 변경
     */
    public void setSeatStatus(SeatStatus status) {
        this.status = status;
        if (status == SeatStatus.VACANT) {
            this.currentStudent = null;
        }
    }

    /**
     * QR 코드 갱신
     */
    public void updateQRCode() {
        this.qrCode = generateQRCode();
    }
}
