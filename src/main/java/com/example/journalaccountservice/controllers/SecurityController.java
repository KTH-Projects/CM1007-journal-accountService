package com.example.journalaccountservice.controllers;

import com.example.journalaccountservice.core.entity.Account;
import com.example.journalaccountservice.core.service.interfaces.ICookieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:8080"})
@RestController
@RequestMapping("/security")
public class SecurityController {

    private final ICookieService cookieService;

    public SecurityController(ICookieService cookieService) {
        this.cookieService = cookieService;
    }

    @PreAuthorize("hasRole('patient')")
    @GetMapping("/patient")
    public ResponseEntity<String> isPatient(@CookieValue("userCookieID") String userCookieID ){
        if(!cookieService.isPatient(userCookieID))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        Account a = cookieService.findAccountByCookie(userCookieID);
        return ResponseEntity.ok(a.getPatientID());
    }

    @PreAuthorize("hasRole('doctor')")
    @GetMapping("/doctor")
    public ResponseEntity<String> isDoctor(@CookieValue("userCookieID") String userCookieID ){
        if(!cookieService.isDoctor(userCookieID))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        Account a = cookieService.findAccountByCookie(userCookieID);
        return ResponseEntity.ok(a.getStaffID());
    }

    @PreAuthorize("hasRole('staff')")
    @GetMapping("/staff")
    public ResponseEntity<String> isDoctorOrStaff(@CookieValue("userCookieID") String userCookieID ){
        if(!cookieService.isDoctorOrStaff(userCookieID))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        Account a = cookieService.findAccountByCookie(userCookieID);
        return ResponseEntity.ok(a.getStaffID());
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/other")
    public ResponseEntity<String> isOther(@CookieValue("userCookieID") String userCookieID ){
        if(!cookieService.isOther(userCookieID))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        Account a = cookieService.findAccountByCookie(userCookieID);
        return ResponseEntity.ok(a.getStaffID());
    }

}
