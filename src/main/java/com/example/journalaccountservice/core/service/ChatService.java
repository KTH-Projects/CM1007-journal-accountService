package com.example.journalaccountservice.core.service;

import com.example.journalaccountservice.core.entity.Account;
import com.example.journalaccountservice.core.entity.Message;
import com.example.journalaccountservice.core.service.interfaces.IAccountsService;
import com.example.journalaccountservice.core.service.interfaces.IChatService;
import com.example.journalaccountservice.view.dto.MessageDTO;
import com.example.journalaccountservice.view.entity.AccountView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.tcp.SslProvider;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
@Service
public class ChatService implements IChatService{

    private final IAccountsService accountsService;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public ChatService(IAccountsService accountsService, ObjectMapper objectMapper) {
        this.accountsService = accountsService;
        this.objectMapper = objectMapper;
        this.webClient = WebClient.create("http://localhost:8082"); // Replace with your actual API base URL
    }

    @Override
    public Message postChat(Account toAccount, Account fromAccount, String msg) {
        try {
            // Create the request payload
            MessageDTO messageDTO = new MessageDTO(
                    toAccount.getId(),
                    fromAccount.getId(),
                    msg
            );

            // Send the request and get the response
            MessageDTO responseMessage = webClient.post()
                    .uri("/message/send")
                    .body(BodyInserters.fromValue(messageDTO))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<MessageDTO>() {})
                    .block();
            Message convertedMessage = findMessageFromDTO(responseMessage);
            if(convertedMessage == null)
                throw new RuntimeException("Could not find accounts to message: "+ messageDTO);
            return convertedMessage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Message findMessageFromDTO(MessageDTO messageDTO){
         Account fromAcc = accountsService.findByID(messageDTO.getFromId());
         Account toAcc = accountsService.findByID(messageDTO.getToId());
         if(fromAcc == null || toAcc == null)
             return null;
         return new Message(
                 UUID.randomUUID().toString(),
                 AccountView.convert(toAcc),
                 AccountView.convert(fromAcc),
                 messageDTO.getMessage()
         );
    }
    /*
    @Override
    public List<Message> getChatsFromAccount(Account fromAccount) {
        try{
            String json = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/send")
                            .queryParam("",fromAccount )
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println("Successfull post! JSON: " + json);
            Gson gson = new Gson();
            Type messageType = new TypeToken<List<Message>>() {}.getType();
            List<Message> messageDTOs = gson.fromJson(json, messageType);
            //Account fromAcc = accountsService.findByID(msgDTO.getFromId());
            //Account toAcc = accountsService.findByID(msgDTO.getToId());
            return null;
        }catch (Exception e){
            System.out.println("Could not send message: "+ e.getMessage());
            return null;
        }
    }

    @Override
    public List<Message> getChatsFromAccountANDtoAccount(Account toAccount, Account fromAccount) {
        return null;
    }
    private static HttpClient createHttpClientWithSSL() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        return HttpClient.newBuilder()
                .sslContext(javax.net.ssl.SSLContext.getDefault())
                .build();
    }

     */
}
