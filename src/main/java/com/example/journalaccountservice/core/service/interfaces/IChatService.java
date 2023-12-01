package com.example.journalaccountservice.core.service.interfaces;

import com.example.journalaccountservice.core.entity.Account;
import com.example.journalaccountservice.core.entity.Message;

import java.util.List;

public interface IChatService {
    Message postChat(Account toAccount, Account fromAccount, String msg);
   // List<Message> getChatsFromAccount(Account fromAccount);
   // List<Message> getChatsFromAccountANDtoAccount(Account toAccount, Account fromAccount);
}
