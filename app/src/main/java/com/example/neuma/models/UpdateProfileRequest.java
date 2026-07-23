package com.example.neuma.models;

public class UpdateProfileRequest {
    private String name;

    public UpdateProfileRequest(String name) {
        this.name = name;
    }

    public String getName() { return name; }
}
