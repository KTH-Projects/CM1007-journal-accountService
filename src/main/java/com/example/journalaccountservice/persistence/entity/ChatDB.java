package com.example.journalaccountservice.persistence.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;


@Entity()
@Table(name = "chat")
public class ChatDB {

    public ChatDB(AccountDB toAccount, AccountDB fromAccount, String msg){
        this.id = UUID.randomUUID().toString();
        this.toAccount = toAccount;
        this.fromAccount = fromAccount;
        this.message = msg;
    }
    public ChatDB(){
        this(new AccountDB(),new AccountDB(),"string");
    }

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private String id;


    @ManyToOne
    @JoinColumn(name ="to_account_id")
    private AccountDB toAccount;
    @ManyToOne
    @JoinColumn(name ="from_account_id")
    private AccountDB fromAccount;
    @Column(nullable = false)
    private String message;

    public AccountDB getToAccount() {
        return toAccount;
    }

    public void setToAccount(AccountDB toAccount) {
        this.toAccount = toAccount;
    }

    public AccountDB getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(AccountDB fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setID(String ID) {
        this.id = ID;
    }

    @Override
    public String toString() {
        return "ChatDB{" +
                "ID='" + id + '\'' +
                ", toAccount=" + toAccount +
                ", fromAccount=" + fromAccount +
                ", message='" + message + '\'' +
                '}';
    }
}


