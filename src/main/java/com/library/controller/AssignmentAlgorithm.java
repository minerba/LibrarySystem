package com.library.controller;

import com.library.model.Student;
import com.library.model.WaitingList;
import java.util.List;

/**
 * 좌석 배정 알고리즘 인터페이스
 */
public interface AssignmentAlgorithm {
    /**
     * 대기 목록에서 학생 선택
     */
    Student selectStudent(WaitingList waitingList);

    /**
     * 학생의 우선순위 계산
     */
    int calculatePriority(Student student);
}
