package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 메인 애플리케이션
 */
@SpringBootApplication
public class LibraryApplication {
    
    public static void main(String[] args) {
        System.out.println("\n====================================");
        System.out.println("  도서관 좌석 관리 시스템 (Spring Boot)");
        System.out.println("====================================\n");
        
        SpringApplication.run(LibraryApplication.class, args);
    }
}
