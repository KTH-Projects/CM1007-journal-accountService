package com.example.journalaccountservice.util.enums;

public enum Role {
    admin("admin"), doctor("doctor"), patient("patient"), other("other");

    private String code;

    private Role(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}