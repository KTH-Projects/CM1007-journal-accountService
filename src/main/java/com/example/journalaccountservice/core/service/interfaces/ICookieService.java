package com.example.journalaccountservice.core.service.interfaces;

import com.example.journalaccountservice.core.entity.Account;

public interface ICookieService {
    public String createCookie(Account account);
    public String findCookieByAccount(Account account);
    public Account findAccountByCookie(String userCookieID);
    public boolean isValidCookie(String userCookieID);
    public boolean isDoctor(String userCookieID);
    public boolean isDoctorOrStaff(String userCookieID);
    public boolean isOther(String userCookieID);
    public boolean isPatient(String userCookieID);

}
