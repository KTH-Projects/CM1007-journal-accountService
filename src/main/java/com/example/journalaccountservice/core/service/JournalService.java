package com.example.journalaccountservice.core.service;

import com.example.journalaccountservice.core.service.interfaces.IJournalService;
import com.example.journalaccountservice.dto.SignUpDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
@Service
public class JournalService implements IJournalService {
    private final WebClient webClient;
    public JournalService(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.baseUrl("https://localhost:8080").build();

    }
    @Override
    public boolean postPatient(SignUpDTO signUpDTO) {
        try{
            String json = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/patient")
                            .queryParam("patient", signUpDTO)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println("Successfull post! JSON: " + json);
            return true;
        }catch (Exception e){
            System.out.println("Could not create patient: "+ e.getMessage());
            return false;
        }
    }

    @Override
    public boolean postM_Staff(SignUpDTO signUpDTO) {
        try{
            String json = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/staff")
                            .queryParam("staff", signUpDTO)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println("Successfull post! JSON: " + json);
            return true;
        }catch (Exception e){
            System.out.println("Could not create staff: "+ e.getMessage());
            return false;
        }
    }

    @Override
    public boolean create(SignUpDTO signUpDTO) {
        return switch (signUpDTO.getRole()) {
            case "doctor" -> postM_Staff(signUpDTO);
            case "staff" -> postM_Staff(signUpDTO);
            case "patient" -> postPatient(signUpDTO);
            default -> false;
        };
    }
}
