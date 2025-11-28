package com.library.model;

/**
 * 좌석 상태를 나타내는 열거형
 */
public enum SeatStatus {
    OCCUPIED("사용중"),
    VACANT("공석"),
    TEMPORARILY_ABSENT("일시부재");

    private final String description;

    SeatStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
