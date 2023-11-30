package com.example.journalaccountservice.core.service;


import com.example.journalaccountservice.core.entity.Account;
import com.example.journalaccountservice.core.service.interfaces.IAccountsService;
import com.example.journalaccountservice.persistence.entity.AccountDB;
import com.example.journalaccountservice.persistence.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountsService implements IAccountsService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;

    }

    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        for(AccountDB a :  accountRepository.findAll()){
            accounts.add(Account.convertFromDB(a));
        }

        return accounts;
    }

    @Override
    public Account findByID(String ID){
        Optional<AccountDB> accountDB = accountRepository.findById(ID);
        if(accountDB.isEmpty()) return null;
        return Account.convertFromDB(accountDB.get());
    }

    public Account findByEmail(String email){
        AccountDB accountDB = accountRepository.findByEmail(email);
        return Account.convertFromDB(accountDB);
    }

    public Account create(Account account){
        if(accountRepository.findByEmail(account.getEmail()) != null) return null;

        AccountDB createdAccount = new AccountDB(account.getId(),account.getEmail(), account.getPassword(), account.getName(),account.getRole());
        createdAccount = accountRepository.save(createdAccount);
        return Account.convertFromDB(createdAccount);
    }

    public Account findBySessionID(String ID){
        AccountDB account = accountRepository.findByCookie_Id(ID);
        if(account==null) return null;

        return Account.convertFromDB(account);
    }

}
