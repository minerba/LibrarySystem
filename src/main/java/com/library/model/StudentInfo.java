package com.library.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.Duration;

/**
 * 학생 정보를 담는 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentInfo {
    private String studentId;
    private String name;
    private String major;
    private String phoneNumber;
    private int noShowCount = 0;
    private Duration totalUsageTime = Duration.ZERO;

    public StudentInfo(String studentId, String name, String major, String phoneNumber) {
        this.studentId = studentId;
        this.name = name;
        this.major = major;
        this.phoneNumber = phoneNumber;
        this.noShowCount = 0;
        this.totalUsageTime = Duration.ZERO;
    }

    public void updateNoShowCount() {
        this.noShowCount++;
    }

    public void addUsageTime(Duration duration) {
        this.totalUsageTime = this.totalUsageTime.plus(duration);
    }
}
