package com.example.journalaccountservice.core.service.interfaces;

import com.example.journalaccountservice.dto.SignUpDTO;

public interface IJournalService {
    boolean postPatient(SignUpDTO signUpDTO) ;
    boolean postM_Staff(SignUpDTO signUpDTO);
    boolean create(SignUpDTO signUpDTO);
}
