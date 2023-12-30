package com.example.journalaccountservice.tests;

import com.example.journalaccountservice.core.entity.Account;
import com.example.journalaccountservice.core.service.AccountsService;
import com.example.journalaccountservice.persistence.entity.AccountDB;
import com.example.journalaccountservice.persistence.repositories.AccountRepository;
import com.example.journalaccountservice.util.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountsService accountsService;

    // Sample data for testing
    private AccountDB sampleAccountDB;
    private Account sampleAccount;
    private UserRepresentation sampleUserRepresentation;

    @BeforeEach
    void setUp() {
        // Initialize
        sampleAccountDB = new AccountDB("id1", "email@example.com", "password", "Name", Role.doctor, "staffID1", "patientID1");
        sampleAccount = Account.convertFromDB(sampleAccountDB);
        sampleUserRepresentation = new UserRepresentation();
        sampleUserRepresentation.setEmail("email@example.com");
    }

    @Test
    public void testFindAll() {
        // Arrange
        when(accountRepository.findAll()).thenReturn(List.of(sampleAccountDB));

        // Act
        List<Account> accounts = accountsService.findAll();

        // Assert
        assertNotNull(accounts, "The returned accounts list should not be null");
        assertEquals(1, accounts.size(), "The accounts list should contain 1 account");
    }

    @Test
    public void testFindByID() {
        when(accountRepository.findById("id1")).thenReturn(Optional.of(sampleAccountDB));

        Account account = accountsService.findByID("id1");

        assertNotNull(account, "The returned account should not be null");
        assertEquals("email@example.com", account.getEmail(), "The email should match");
    }

    @Test
    public void testDelete() {
        when(accountRepository.findById("id1")).thenReturn(Optional.of(sampleAccountDB));

        Account deletedAccount = accountsService.delete("id1");

        verify(accountRepository).delete(any(AccountDB.class)); // Verify delete was called
        assertNotNull(deletedAccount, "Deleted account should not be null");
    }

    @Test
    public void testFindByEmail() {
        when(accountRepository.findByEmail("email@example.com")).thenReturn(sampleAccountDB);

        Account account = accountsService.findByEmail("email@example.com");

        assertNotNull(account, "The returned account should not be null");
        assertEquals("email@example.com", account.getEmail(), "The email should match");
    }

    @Test
    public void testCreate() {
        when(accountRepository.findByEmail("email@example.com")).thenReturn(null);
        when(accountRepository.save(any(AccountDB.class))).thenReturn(sampleAccountDB);

        Account createdAccount = accountsService.create(sampleAccount);

        assertNotNull(createdAccount, "The created account should not be null");
        assertEquals("email@example.com", createdAccount.getEmail(), "The email should match");
    }

    @Test
    public void testFindBySessionID() {
        when(accountRepository.findByCookie_Id("sessionId")).thenReturn(sampleAccountDB);

        Account account = accountsService.findBySessionID("sessionId");

        assertNotNull(account, "The returned account should not be null");
        assertEquals("email@example.com", account.getEmail(), "The email should match");
    }

    @Test
    public void testFindByUserRepresentation() {
        when(accountRepository.findByEmail(sampleUserRepresentation.getEmail())).thenReturn(sampleAccountDB);

        Account account = accountsService.findByUserRepresentation(sampleUserRepresentation);

        assertNotNull(account, "The returned account should not be null");
        assertEquals("email@example.com", account.getEmail(), "The email should match");
    }

    @Test
    void testAccessAsDoctor() {
        when(accountRepository.findById("id1")).thenReturn(Optional.of(sampleAccountDB));
        Account result = accountsService.findByID("id1");
        assertNotNull(result);
        assertEquals(Role.doctor, result.getRole(), "Role should be DOCTOR for this account");
    }

}
