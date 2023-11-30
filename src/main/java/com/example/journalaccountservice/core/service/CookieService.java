package com.example.journalaccountservice.core.service;

import com.example.journalaccountservice.core.entity.Account;
import com.example.journalaccountservice.core.service.interfaces.ICookieService;
import com.example.journalaccountservice.persistence.entity.AccountDB;
import com.example.journalaccountservice.persistence.entity.CookieDB;
import com.example.journalaccountservice.persistence.repositories.AccountRepository;
import com.example.journalaccountservice.persistence.repositories.CookieRepository;
import com.example.journalaccountservice.util.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CookieService implements ICookieService {


    private final CookieRepository cookieRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public CookieService(CookieRepository cookieRepository, AccountRepository accountRepository) {
        this.cookieRepository = cookieRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public String createCookie(Account account){
        AccountDB accountDB = accountRepository.findByEmail(account.getEmail());

        CookieDB returnSession = cookieRepository.save(new CookieDB(accountDB));

        accountDB.setCookie(returnSession);
        accountRepository.save(accountDB);

        return returnSession.getId();
    }

    @Override
    public String findCookieByAccount(Account account){
        AccountDB accountDB = accountRepository.findByEmail(account.getEmail());
        CookieDB sessionDB = cookieRepository.findByAccount(accountDB);
        return sessionDB.getId();
    }

    @Override
    public Account findAccountByCookie(String userSessionID) {
        AccountDB accountDB = accountRepository.findByCookie_Id(userSessionID);
        if(accountDB == null) return null;
        return Account.convertFromDB(accountDB);
    }

    @Override
    public boolean isValidCookie(String userSessionID){
        String cookieID = userSessionID;
        if(cookieID == null) return false;
        AccountDB accountDB = accountRepository.findByCookie_Id(cookieID);
        CookieDB sessionDB = cookieRepository.findByAccount(accountDB);
        return sessionDB.isValid();
    }

    @Override
    public boolean isDoctor(String userSessionID) {
        if(!isValidCookie(userSessionID)) return false;
        String cookieID = userSessionID;
        if(cookieID == null) return false;
        AccountDB accountDB = accountRepository.findByCookie_Id(cookieID);
        return accountDB.getRole().equals(Role.doctor);
    }

    public boolean isOther(String userSessionID) {
        if(!isValidCookie(userSessionID)) return false;
        String cookieID = userSessionID;
        if(cookieID == null) return false;
        AccountDB accountDB = accountRepository.findByCookie_Id(cookieID);
        return accountDB.getRole().equals(Role.other);
    }
    @Override
    public boolean isDoctorOrStaff(String userSessionID) {
        if(!isValidCookie(userSessionID)) return false;
        String cookieID = userSessionID;
        if(cookieID == null) return false;
        AccountDB accountDB = accountRepository.findByCookie_Id(cookieID);
        return accountDB.getRole().equals(Role.doctor) || accountDB.getRole().equals(Role.other);
    }


}

