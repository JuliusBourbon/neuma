package com.example.neuma.models;

public class StartAttemptRequest {
    private String levelId;

    public StartAttemptRequest(String levelId) {
        this.levelId = levelId;
    }

    public String getLevelId() { return levelId; }
}
