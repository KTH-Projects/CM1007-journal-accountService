package com.example.journalaccountservice.core.service;

import com.example.journalaccountservice.core.service.interfaces.IJournalService;
import com.example.journalaccountservice.security.KeycloakSecurityUtil;
import com.example.journalaccountservice.view.dto.SignUpDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class JournalService implements IJournalService {
    private final WebClient webClient;
    private final KeycloakSecurityUtil keycloakUtil;

    @Autowired
    public JournalService(KeycloakSecurityUtil keycloakUtil){
        this.keycloakUtil = keycloakUtil;
        //this.webClient = WebClient.create(System.getenv("JOURNAL_SERVICE_URL"));
        this.webClient = WebClient.builder().baseUrl(System.getenv("JOURNAL_SERVICE_URL")).build();
    }

    @Override
    public String postPatient(SignUpDTO signUpDTO) {
        try{
            String json = webClient.post()
                    .uri("/patient")
                    .body(Mono.just(signUpDTO), SignUpDTO.class)  // Send SignUpDTO in the request body
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println("Successfully post! JSON: " + json);
            return json;
        }catch (Exception e){
            System.out.println("Could not create patient: "+ e.getMessage());
            return null;
        }
    }

    @Override
    public String postM_Staff(SignUpDTO signUpDTO) {
        try{
            String json = webClient.post()
                    .uri("/staff")
                    .body(Mono.just(signUpDTO), SignUpDTO.class)  // Send SignUpDTO in the request body
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println("Successfully post! JSON: " + json);
            return json;
        }catch (Exception e){
            System.out.println("Could not create staff: "+ e.getMessage());
            return null;
        }
    }

    @Override
    public String create(SignUpDTO signUpDTO) {
        return switch (signUpDTO.getRole()) {
            case "doctor" -> postM_Staff(signUpDTO);
            case "staff" -> postM_Staff(signUpDTO);
            case "patient" -> postPatient(signUpDTO);
            default -> null;
        };
    }
}
