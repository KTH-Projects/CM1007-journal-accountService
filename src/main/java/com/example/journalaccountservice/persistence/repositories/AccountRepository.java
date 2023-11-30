package com.example.journalaccountservice.persistence.repositories;

import com.example.journalaccountservice.persistence.entity.AccountDB;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<AccountDB, String> {

    @Override
    List<AccountDB> findAll();

    AccountDB findByEmail(String email);

    AccountDB findByCookie_Id(String session);

    //List<AccountDB> findAllByPatientIsNotNull();

}
