package com.example.journalaccountservice.view.dto;

import com.example.journalaccountservice.core.entity.Account;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SignUpRequest {
    private Account account;
    private SignUpDTO signUpDTO;
    public SignUpDTO getSignupDTO(){
        return signUpDTO;
    }
    public Account getAccount() {
        return account;
    }
}
