package com.example.journalaccountservice.core.service.interfaces;



import com.example.journalaccountservice.core.entity.Account;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface IAccountsService {
    public Account delete(String id);
    public List<Account> findAll();
    public Account findByEmail(String email);
    public Account findByID(String id);
    public Account create(Account account);
    public Account findBySessionID(String ID);
    public Account findByUserRepresentation(UserRepresentation userRepresentation);


    }