package com.example.journalaccountservice.core.service;

import com.example.journalaccountservice.core.service.interfaces.IJournalService;
import com.example.journalaccountservice.security.KeycloakSecurityUtil;
import com.example.journalaccountservice.view.dto.SignUpDTO;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Service
public class JournalService implements IJournalService {
    private final WebClient webClient;
    private final KeycloakSecurityUtil keycloakUtil;

    @Autowired
    public JournalService(KeycloakSecurityUtil keycloakUtil){
        this.keycloakUtil = keycloakUtil;
        this.webClient = createWebClientWithTimeout(60); // 60 seconds timeout
    }

    // Helper method to create a WebClient with custom timeout
    private WebClient createWebClientWithTimeout(long timeoutSeconds) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(timeoutSeconds)) // Setting the response timeout
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) Duration.ofSeconds(timeoutSeconds).toMillis()); // Setting the connection timeout

        return WebClient.builder()
                .baseUrl(System.getenv("JOURNAL_SERVICE_URL"))
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
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
