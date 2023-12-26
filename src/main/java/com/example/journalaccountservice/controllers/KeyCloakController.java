package com.example.journalaccountservice.controllers;

import com.example.journalaccountservice.core.entity.Account;
import com.example.journalaccountservice.core.service.KeycloakService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/keycloak")
public class KeyCloakController {

    private final KeycloakService keycloakService;

    @Autowired
    public KeyCloakController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @PostMapping
    public Account createUser(@RequestBody Account account){
        return keycloakService.createUser(account);
    }

    @GetMapping
    public UserRepresentation getUser(Principal principal){
        System.out.println("hello");
        return keycloakService.getUserById(principal.getName());
    }

    @DeleteMapping("/{userId}")
    public Boolean deleteUser(@PathVariable String userId){
        return keycloakService.deleteUserById(userId);
    }



}
