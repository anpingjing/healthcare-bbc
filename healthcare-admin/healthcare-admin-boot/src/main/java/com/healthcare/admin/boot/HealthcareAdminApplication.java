package com.healthcare.admin.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.healthcare.admin")
@MapperScan("com.healthcare.admin.**.mapper")
public class HealthcareAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthcareAdminApplication.class, args);
    }
}
