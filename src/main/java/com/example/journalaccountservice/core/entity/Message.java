package com.example.journalaccountservice.core.entity;

import com.example.journalaccountservice.view.entity.AccountView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class Message {
    private String id;
    private AccountView toAccount;
    private AccountView fromAccount;
    private String msg;
    public Message(String id, AccountView toAccount, AccountView fromAccount, String msg) {
        this.id = id;
        this.toAccount = toAccount;
        this.fromAccount = fromAccount;
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AccountView getToAccount() {
        return toAccount;
    }

    public void setToAccount(AccountView toAccount) {
        this.toAccount = toAccount;
    }

    public AccountView getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(AccountView fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
