package com.example.journalaccountservice.view.entity;

import com.example.journalaccountservice.core.entity.Account;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountView {

    private final String email;
    private final String name;

    @JsonCreator
    public AccountView(
            @JsonProperty("email") String email,
            @JsonProperty("name") String name)
    {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "AccountView{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public static AccountView convert(Account account){
        return new AccountView(account.getEmail(), account.getName());
    }

}
