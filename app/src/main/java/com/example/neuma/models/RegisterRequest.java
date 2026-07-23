package com.example.neuma.models;

public class RegisterRequest {
    private String name;
    private String password;

    public RegisterRequest(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() { return name; }
    public String getPassword() { return password; }
}
