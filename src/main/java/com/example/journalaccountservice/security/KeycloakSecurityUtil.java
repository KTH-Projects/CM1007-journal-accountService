package com.example.journalaccountservice.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.TokenVerifier;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Objects;


@Component
public class KeycloakSecurityUtil {


    @Value("${server-url}")
    private String serverUrl;

    @Value("${realm}")
    private String realm;

    @Value("${grant-type}")
    private String grantType;

    @Value("${adminClient-id}")
    private String adminClientId;

    @Value("${adminClient-secret}")
    private String adminClientSecret;

    @Value("${appClient-id}")
    private String appClientId;

    @Value("${appClient-secret}")
    private String appClientSecret;

    private Keycloak adminKeycloakInstance;
    private Keycloak appKeycloakInstance;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;



    public KeycloakSecurityUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.webClient = WebClient.create();;
        System.out.println(this.toString());
    }
    @Bean
    public Keycloak getKeycloakInstanceAdmin() {
        if (adminKeycloakInstance == null) {
            adminKeycloakInstance = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(adminClientId)
                    .clientSecret(adminClientSecret)
                    .grantType(grantType)
                    .build();
        }
        System.out.println(this);
        return adminKeycloakInstance;
    }

    @Bean
    public Keycloak getKeycloakInstanceApp() {
        if (appKeycloakInstance == null) {
            appKeycloakInstance = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(appClientId)
                    .clientSecret(appClientSecret)
                    .grantType(grantType)
                    .build();
        }
        System.out.println(this);
        return appKeycloakInstance;
    }

    public ResponseEntity<?> authenticateUser(String username, String password) {
        try {
            ResponseEntity<String> response = webClient.post()
                    .uri(serverUrl + "/realms/" + realm + "/protocol/openid-connect/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters
                            .fromFormData("grant_type", "password") // Note the change here to 'password' grant type
                            .with("client_id", appClientId)
                            .with("client_secret", appClientSecret)
                            .with("username", username)
                            .with("password", password))
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            // Handle the response appropriately.
            if (response.getStatusCode().is2xxSuccessful()) {
                String accessToken = extractAccessToken(Objects.requireNonNull(response.getBody()));
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
                return new ResponseEntity<>(headers, HttpStatus.OK);
            } else {
                // Handle non-successful response here.
                return ResponseEntity.status(response.getStatusCode()).build();
            }
        } catch (WebClientResponseException e) {
            // Handle WebClientResponseException here.
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            // Handle other exceptions here.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private String extractAccessToken(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            return rootNode.path("access_token").asText();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse access token from response", e);
        }
    }

    public String getSubjectFromToken(String accessToken) {
        try {
            AccessToken token = TokenVerifier.create(accessToken, AccessToken.class).getToken();
            return token.getSubject();
        } catch (VerificationException e) {
            // Log exception details and return null or handle accordingly
            return null;
        }
    }

    @Override
    public String toString() {
        return "KeycloakSecurityUtil{" +
                "serverUrl='" + serverUrl + '\'' +
                ", realm='" + realm + '\'' +
                ", grantType='" + grantType + '\'' +
                ", adminClientId='" + adminClientId + '\'' +
                ", adminClientSecret='" + adminClientSecret + '\'' +
                ", appClientId='" + appClientId + '\'' +
                ", appClientSecret='" + appClientSecret + '\'' +
                '}';
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getRealm() {
        return realm;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getAdminClientId() {
        return adminClientId;
    }

    public String getAdminClientSecret() {
        return adminClientSecret;
    }

    public String getAppClientId() {
        return appClientId;
    }

    public String getAppClientSecret() {
        return appClientSecret;
    }
}
