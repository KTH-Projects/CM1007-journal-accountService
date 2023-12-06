package com.example.journalaccountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JournalAccountServiceApplication {

    public static void main(String[] args) {
        System.out.println("Env: MYSQL_ROOT_PASSWORD=" + System.getenv("MYSQL_ROOT_PASSWORD") +" MYSQL_USER="+System.getenv("MYSQL_USER") + " MYSQL_URL"+ System.getenv("MYSQL_URL") );
        SpringApplication.run(JournalAccountServiceApplication.class, args);
    }

}
