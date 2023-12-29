package com.example.journalaccountservice.controllers;

import com.example.journalaccountservice.core.service.AccountsService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.journalaccountservice.core.entity.Account;
import com.example.journalaccountservice.core.service.KeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/keycloak")
public class KeyCloakController {

    private final KeycloakService keycloakService;
    private final AccountsService accountsService;

    @Autowired
    public KeyCloakController(KeycloakService keycloakService, AccountsService accountsService) {
        this.keycloakService = keycloakService;
        this.accountsService = accountsService;
    }

    @PostMapping
    public Account createUser(@RequestBody Account account){
        return keycloakService.createUser(account);
    }

    // TODO: frontend need to save token
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Account account){
        return keycloakService.authenticateUser(account.getName(), account.getPassword());
    }

    @PreAuthorize("hasRole('doctor')")
    @GetMapping("/doctorId")
    public ResponseEntity<String> getAccount(@RequestHeader("Authorization") String token){
        UserRepresentation userRepresentation = keycloakService.getUserByToken(token);
        Account account = accountsService.findByUserRepresentation(userRepresentation);
        return ResponseEntity.ok(account.getStaffID());
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping
    public UserRepresentation getUser(Principal principal){
        return keycloakService.getUserById(principal.getName());
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{userId}")
    public Boolean deleteUser(@PathVariable String userId){
        return keycloakService.deleteUserById(userId);
    }

}
