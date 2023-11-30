package com.example.journalaccountservice.persistence.repositories;

import com.example.journalaccountservice.persistence.entity.AccountDB;
import com.example.journalaccountservice.persistence.entity.CookieDB;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CookieRepository extends JpaRepository<CookieDB, String> {
    CookieDB findByAccount(AccountDB account);
}

