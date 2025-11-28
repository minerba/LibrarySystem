package com.library.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 열람실을 나타내는 클래스
 */
@Getter
@Setter
public class ReadingRoom {
    private String roomId;
    private String roomName;
    private int totalSeats;
    private int availableSeats;
    private List<Seat> seatList;
    private WaitingList waitingList;

    public ReadingRoom(String roomName, int totalSeats) {
        this.roomId = UUID.randomUUID().toString();
        this.roomName = roomName;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.seatList = new ArrayList<>();
        this.waitingList = new WaitingList(roomId);
        
        // 좌석 초기화
        for (int i = 1; i <= totalSeats; i++) {
            seatList.add(new Seat(i));
        }
    }

    /**
     * 좌석 상태 업데이트
     */
    public void updateSeatStatus() {
        availableSeats = 0;
        for (Seat seat : seatList) {
            if (seat.getStatus() == SeatStatus.VACANT) {
                availableSeats++;
            }
        }
    }

    /**
     * QR 코드 업데이트
     */
    public void updateQRCode() {
        for (Seat seat : seatList) {
            seat.updateQRCode();
        }
    }

    /**
     * 열람실 상태 조회
     */
    public String getReadingRoomStatus() {
        return String.format("%s - 전체: %d, 사용가능: %d, 대기: %d", 
                           roomName, totalSeats, availableSeats, waitingList.getSize());
    }

    /**
     * 빈 좌석 찾기
     */
    public Seat findVacantSeat() {
        for (Seat seat : seatList) {
            if (seat.getStatus() == SeatStatus.VACANT) {
                return seat;
            }
        }
        return null;
    }

    /**
     * 좌석 번호로 좌석 찾기
     */
    public Seat getSeatByNumber(int seatNumber) {
        if (seatNumber > 0 && seatNumber <= seatList.size()) {
            return seatList.get(seatNumber - 1);
        }
        return null;
    }
    
    public List<Seat> getSeatList() {
        return new ArrayList<>(seatList);
    }
}
