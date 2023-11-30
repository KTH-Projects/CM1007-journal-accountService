package com.example.journalaccountservice.core.service.interfaces;

import com.example.journalaccountservice.core.entity.Account;

public interface ICookieService {
    public String createCookie(Account account);
    public String findCookieByAccount(Account account);
    public Account findAccountByCookie(String userSessionID);
    public boolean isValidCookie(String userCookieID);
    public boolean isDoctor(String userSessionID);
    public boolean isDoctorOrStaff(String userSessionID);

}
