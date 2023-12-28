package com.example.journalaccountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JournalAccountServiceApplication {

    public static void main(String[] args) {
        System.out.println("Env: " +
                "\nMYSQL_USER="+System.getenv("MYSQL_USER") +
                "\nMYSQL_PASSWORD=" + System.getenv("MYSQL_PASSWORD") +
                "\nMYSQL_URL="+ System.getenv("MYSQL_URL") +
                "\nMYSQL_ROOT_PASSWORD=" + System.getenv("MYSQL_ROOT_PASSWORD") +
                "\nJOURNAL_SERVICE_URL=" + System.getenv("JOURNAL_SERVICE_URL") +
                "\nKEYCLOAK_ACCESS_TOKEN_URL=" + System.getenv("KEYCLOAK_ACCESS_TOKEN_URL")
        );
        SpringApplication.run(JournalAccountServiceApplication.class, args);
    }

}
