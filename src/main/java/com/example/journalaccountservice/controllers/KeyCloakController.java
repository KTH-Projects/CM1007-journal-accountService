package com.example.journalaccountservice.controllers;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/keycloak")
public class KeyCloakController {

    private Keycloak keycloak;

    @Value("${server-url}")
    private String serverUrl;

    @Value("${realm}")
    private String realm;

    @Value("${client-id}")
    private String clientId;

    @Value("${grant-type}")
    private String grantType;

    @Value("${name}")
    private String username;

    @Value("${password}")
    private String password;

    private Keycloak getKeycloakInstance() {
        System.out.println(toString());
        if (keycloak == null) {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .grantType(grantType)
                    .username(username)
                    .password(password)
                    .build();
        }
        return keycloak;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('admin')")
    public List<UserRepresentation> getAccount(){
        List<UserRepresentation> userRepresentations = getKeycloakInstance().realm(realm).users().list();
        return userRepresentations;
    }

    @Override
    public String toString() {
        return "KeyCloakController{" +
                "keycloak=" + keycloak +
                ", serverUrl='" + serverUrl + '\'' +
                ", realm='" + realm + '\'' +
                ", clientId='" + clientId + '\'' +
                ", grantType='" + grantType + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
