package com.example.journalaccountservice.core.entity;


import com.example.journalaccountservice.persistence.entity.AccountDB;
import com.example.journalaccountservice.util.enums.Role;
public class Account {
    private final String id;
    private String email;
    private String password;
    private String name;
    private final Role role;

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public Account(String id, String email, String password, String name, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public Account(String email, String name) {
        this(null,email,"password", name, Role.patient);
    }

    public Account(String email, String password, String name) {
        this(null,email,password, name, Role.patient);
    }

    public Account() {
        this("0","email","password", "name", Role.patient);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public static Account convertFromDB(AccountDB account){
        return new Account(account.getId(),account.getEmail(),account.getPassword(), account.getName(), account.getRole());
    }
}

