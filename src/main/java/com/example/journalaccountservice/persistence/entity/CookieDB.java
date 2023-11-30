package com.example.journalaccountservice.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "session")
public class CookieDB {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private String id;

    @OneToOne(mappedBy = "cookie")
    private AccountDB account;
    @Column(nullable = false)
    private LocalDateTime end;

    public CookieDB(AccountDB account) {
        this.id = null;
        this.account = account;
        this.end = LocalDateTime.now().plusMinutes(30);
    }

    public CookieDB() {
        this(new AccountDB());
    }



    public AccountDB getAccount() {
        return account;
    }

    public void setAccount(AccountDB account) {
        this.account = account;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isValid(){
        return this.end.isAfter(LocalDateTime.now());
    }
}

