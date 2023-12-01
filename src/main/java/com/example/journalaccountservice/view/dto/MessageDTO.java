package com.example.journalaccountservice.view.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class MessageDTO {
    private String toId;
    private String fromId;
    private String message;
    @JsonCreator
    public MessageDTO(
            @JsonProperty("toId") String toId,
            @JsonProperty("fromId") String fromId,
            @JsonProperty("msg") String msg
    ) {
        this.toId = toId;
        this.fromId = fromId;
        this.message = msg;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "toId='" + toId + '\'' +
                ", fromId='" + fromId + '\'' +
                ", msg='" + message + '\'' +
                '}';
    }
}
