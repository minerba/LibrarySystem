package com.library;

import com.library.api.ApiServer;
import com.library.controller.LibraryManagementSystem;
import com.library.model.*;

/**
 * 메인 애플리케이션
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("  도서관 좌석 관리 시스템 시작");
        System.out.println("====================================\n");

        // API 서버 시작
        ApiServer apiServer = new ApiServer();
        
        // 포트 7000에서 서버 시작
        apiServer.start(7000);

        System.out.println("\n====================================");
        System.out.println("  서버가 성공적으로 시작되었습니다!");
        System.out.println("  브라우저에서 http://localhost:7000 접속");
        System.out.println("====================================\n");

        // 콘솔에서 시스템 상태 주기적으로 출력
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(30000); // 30초마다
                    System.out.println("\n" + apiServer.getSystem().getSystemStatus());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
