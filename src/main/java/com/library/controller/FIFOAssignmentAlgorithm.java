package com.library.controller;

import com.library.model.Student;
import com.library.model.WaitingList;

/**
 * FIFO (First In First Out) 배정 알고리즘
 */
public class FIFOAssignmentAlgorithm implements AssignmentAlgorithm {
    
    @Override
    public Student selectStudent(WaitingList waitingList) {
        return waitingList.getNextStudent();
    }

    @Override
    public int calculatePriority(Student student) {
        // FIFO에서는 순서대로이므로 별도 우선순위 없음
        return 0;
    }
}
