package com.example.journalaccountservice.core.service.interfaces;



import com.example.journalaccountservice.core.entity.Account;

import java.util.List;

public interface IAccountsService {
    public List<Account> findAll();
    public Account findByEmail(String email);
    public Account findByID(String id);
    public Account create(Account account);
    public Account findBySessionID(String ID);

}