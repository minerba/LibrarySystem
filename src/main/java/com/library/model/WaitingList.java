package com.library.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 대기 목록을 관리하는 클래스
 */
@Getter
public class WaitingList {
    private String listId;
    private String readingRoomId;
    private List<Student> waitingStudents;

    public WaitingList(String readingRoomId) {
        this.listId = UUID.randomUUID().toString();
        this.readingRoomId = readingRoomId;
        this.waitingStudents = new ArrayList<>();
    }

    /**
     * 학생을 대기 목록에 추가
     */
    public void addStudent(Student student) {
        waitingStudents.add(student);
        System.out.println(student.getStudentInfo().getName() + 
                         "이(가) 대기 목록에 추가되었습니다. 대기 순서: " + waitingStudents.size());
    }

    /**
     * 학생을 대기 목록에서 제거
     */
    public boolean removeStudent(String studentId) {
        return waitingStudents.removeIf(s -> 
            s.getStudentInfo().getStudentId().equals(studentId));
    }

    /**
     * 다음 학생 가져오기
     */
    public Student getNextStudent() {
        if (!waitingStudents.isEmpty()) {
            return waitingStudents.remove(0);
        }
        return null;
    }

    /**
     * 학생의 대기 순서 조회
     */
    public int getPosition(String studentId) {
        for (int i = 0; i < waitingStudents.size(); i++) {
            if (waitingStudents.get(i).getStudentInfo().getStudentId().equals(studentId)) {
                return i + 1;
            }
        }
        return -1;
    }

    public int getSize() {
        return waitingStudents.size();
    }
    
    public List<Student> getWaitingStudents() {
        return new ArrayList<>(waitingStudents);
    }
}
