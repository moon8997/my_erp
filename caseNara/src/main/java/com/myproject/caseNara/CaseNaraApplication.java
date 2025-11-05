package com.myproject.caseNara;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CaseNaraApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaseNaraApplication.class, args);
    }

}
