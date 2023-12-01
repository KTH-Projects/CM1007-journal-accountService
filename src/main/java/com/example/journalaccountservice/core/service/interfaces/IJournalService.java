package com.example.journalaccountservice.core.service.interfaces;

import com.example.journalaccountservice.view.dto.SignUpDTO;

public interface IJournalService {
    String postPatient(SignUpDTO signUpDTO) ;
    String postM_Staff(SignUpDTO signUpDTO);
    String create(SignUpDTO signUpDTO);
}
