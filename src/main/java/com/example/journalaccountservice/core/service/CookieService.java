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

        CookieDB returnCookie = cookieRepository.save(new CookieDB(accountDB));

        accountDB.setCookie(returnCookie);
        accountRepository.save(accountDB);

        return returnCookie.getId();
    }

    @Override
    public String findCookieByAccount(Account account){
        AccountDB accountDB = accountRepository.findByEmail(account.getEmail());
        CookieDB cookieDB = cookieRepository.findByAccount(accountDB);
        return cookieDB.getId();
    }

    @Override
    public Account findAccountByCookie(String userCookieID) {
        AccountDB accountDB = accountRepository.findByCookie_Id(userCookieID);
        if(accountDB == null) return null;
        return Account.convertFromDB(accountDB);
    }

    @Override
    public boolean isValidCookie(String userCookieID){
        String cookieID = userCookieID;
        if(cookieID == null) return false;
        AccountDB accountDB = accountRepository.findByCookie_Id(cookieID);
        CookieDB cookieDB = cookieRepository.findByAccount(accountDB);
        return cookieDB.isValid();
    }

    @Override
    public boolean isDoctor(String userCookieID) {
        if(!isValidCookie(userCookieID)) return false;
        String cookieID = userCookieID;
        if(cookieID == null) return false;
        AccountDB accountDB = accountRepository.findByCookie_Id(cookieID);
        return accountDB.getRole().equals(Role.doctor);
    }

    public boolean isOther(String userCookieID) {
        if(!isValidCookie(userCookieID)) return false;
        String cookieID = userCookieID;
        if(cookieID == null) return false;
        AccountDB accountDB = accountRepository.findByCookie_Id(cookieID);
        return accountDB.getRole().equals(Role.other);
    }
    @Override
    public boolean isDoctorOrStaff(String userCookieID) {
        if(!isValidCookie(userCookieID)) return false;
        String cookieID = userCookieID;
        if(cookieID == null) return false;
        AccountDB accountDB = accountRepository.findByCookie_Id(cookieID);
        return accountDB.getRole().equals(Role.doctor) || accountDB.getRole().equals(Role.other);
    }

    @Override
    public boolean isPatient(String userCookieID) {
        if(!isValidCookie(userCookieID)) return false;
        String cookieID = userCookieID;
        if(cookieID == null) return false;
        AccountDB accountDB = accountRepository.findByCookie_Id(cookieID);
        return accountDB.getRole().equals(Role.patient);
    }


}

