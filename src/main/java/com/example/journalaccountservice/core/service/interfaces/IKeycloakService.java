package com.example.journalaccountservice.core.service.interfaces;

import com.example.journalaccountservice.core.entity.Account;
import org.keycloak.representations.idm.UserRepresentation;

public interface IKeycloakService {

    Account createUser(Account account);
    UserRepresentation getUserById(String userId);
    //UserRepresentation updateUserById(String userId);
    Boolean deleteUserById(String userId);
}
