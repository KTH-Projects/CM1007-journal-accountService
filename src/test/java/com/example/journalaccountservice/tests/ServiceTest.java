package com.example.journalaccountservice.tests;

import com.example.journalaccountservice.core.entity.Account;
import com.example.journalaccountservice.core.service.AccountsService;
import com.example.journalaccountservice.persistence.entity.AccountDB;
import com.example.journalaccountservice.persistence.repositories.AccountRepository;
import com.example.journalaccountservice.util.enums.Role;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountsService accountsService;

    @Test
    public void testFindAll() {
        // Arrange
        List<AccountDB> mockAccounts = new ArrayList<>();
        mockAccounts.add(new AccountDB("id1", "email1", "password1", "name1", Role.doctor, "staffID1", "patientID1"));
        mockAccounts.add(new AccountDB("id2", "email2", "password2", "name2", Role.patient, "staffID2", "patientID2"));
        when(accountRepository.findAll()).thenReturn(mockAccounts);

        // Act
        List<Account> accounts = accountsService.findAll();

        // Assert
        assertNotNull(accounts, "The returned accounts list should not be null");
        assertEquals(2, accounts.size(), "The accounts list should contain 2 accounts");
        assertEquals("email1", accounts.get(0).getEmail(), "The email of the first account should match");
    }


}

