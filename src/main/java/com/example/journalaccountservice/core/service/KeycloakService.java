package com.example.journalaccountservice.core.service;

import com.example.journalaccountservice.core.entity.Account;
import com.example.journalaccountservice.core.service.interfaces.IKeycloakService;
import com.example.journalaccountservice.security.KeycloakSecurityUtil;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        UserRepresentation user = getUserRepresentation(account);
        UsersResource usersResource = getUserResource();

        Response response = usersResource.create(user);
        if (Objects.equals(201, response.getStatus())) {
            System.out.println("create user, response ok");
            extracted(account, response, usersResource);

            return account;
        }

        return null;
    }

    private void extracted(Account account, Response response, UsersResource usersResource) {
        try{
        String userId = CreatedResponseUtil.getCreatedId(response);

        Keycloak keycloak = keycloakUtil.getKeycloakInstanceAdmin();
        RealmResource realmResource = keycloak.realm(keycloakUtil.getRealm());
        RolesResource rolesResource = realmResource.roles();

        List<RoleRepresentation> assignedRoles = new ArrayList<>();
        RoleRepresentation userRole = rolesResource.get("user").toRepresentation();
        if (userRole != null) {
            assignedRoles.add(userRole);
        } else {
            System.out.println("User role not found");
        }

        String roleCode = account.getRole().getCode();
        RoleRepresentation additionalRole = rolesResource.get(roleCode).toRepresentation();

            switch (roleCode){
                case "doctor" :
                    assignedRoles.add(additionalRole);
                    additionalRole = rolesResource.get("staff").toRepresentation();
                    assignedRoles.add(additionalRole);
                    break;
                case "staff" :
                    assignedRoles.add(additionalRole);
                    break;
                case "patient" :
                    assignedRoles.add(additionalRole);
                    break;
                case "admin" :
                    assignedRoles.add(additionalRole);
                    break;
            }

        if (!assignedRoles.isEmpty()) {
            usersResource.get(userId).roles().realmLevel().add(assignedRoles);
            System.out.println("Roles assigned: " + assignedRoles);
        } else {
            System.out.println("No roles to assign");
        }
        System.out.println(assignedRoles.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static UserRepresentation getUserRepresentation(Account account) {
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
        return user;
    }

    private UsersResource getUserResource(){
        Keycloak keycloak = keycloakUtil.getKeycloakInstanceAdmin();
        RealmResource realm = keycloak.realm(keycloakUtil.getRealm());
        return realm.users();
    }

    public ResponseEntity<?> authenticateUser(String username, String password) {
        // Delegate to KeycloakSecurityUtil to authenticate with Keycloak
        return keycloakUtil.authenticateUser(username, password);
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

    @Override
    public UserRepresentation getUserByToken(String token) {
        // Remove "Bearer " prefix if it exists
        token = token.replace("Bearer ", "").trim();

        // Assume KeycloakSecurityUtil has a method to decode the access token and get the subject or username
        String subject = keycloakUtil.getSubjectFromToken(token);

        if (subject == null || subject.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        // Use the subject to get user details from Keycloak
        Keycloak keycloakInstance = keycloakUtil.getKeycloakInstanceAdmin();
        UsersResource usersResource = keycloakInstance.realm(keycloakUtil.getRealm()).users();
        UserRepresentation userRepresentation = usersResource.get(subject).toRepresentation();

        return userRepresentation;
    }
}
