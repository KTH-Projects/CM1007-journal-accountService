package com.example.journalaccountservice.core.service;

import com.example.journalaccountservice.core.entity.Account;
import com.example.journalaccountservice.core.service.interfaces.IKeycloakService;
import com.example.journalaccountservice.security.KeycloakSecurityUtil;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class KeycloakService implements IKeycloakService {

    private final KeycloakSecurityUtil keycloakUtil;


    @Autowired
    public KeycloakService(KeycloakSecurityUtil keycloak) {
        this.keycloakUtil = keycloak;
    }

    @Override
    public Account createUser(Account account) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(account.getName());
        user.setEmail(account.getEmail());
        user.setFirstName(account.getName());

        user.setEmailVerified(true);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(account.getPassword());
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        List<CredentialRepresentation> list = new ArrayList<>();
        list.add(credentialRepresentation);
        user.setCredentials(list);

        UsersResource usersResource = getUserResource();

        Response response = usersResource.create(user);
        if (Objects.equals(201, response.getStatus())) {
            // Handle successful user creation
            return account;
        }

        //response.readEntity()

        return null;
    }

    private UsersResource getUserResource(){
        Keycloak keycloak = keycloakUtil.getKeycloakInstance();
        RealmResource realm = keycloak.realm(keycloakUtil.getRealm());
        return realm.users();
    }

    @Override
    public UserRepresentation getUserById(String userId) {
        return getUserResource().get(userId).toRepresentation();
    }

    @Override
    public Boolean deleteUserById(String userId) {
        Response r = getUserResource().delete(userId);
        return r.getStatus() == 204;
    }
}
