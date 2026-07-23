package com.example.neuma.models;

public class Level {
    private String id;
    private String letter;
    private int order;
    private String title;
    private String description;
    private String status; // "LOCKED", "UNLOCKED", "COMPLETED"

    public String getId() { return id; }
    public String getLetter() { return letter; }
    public int getOrder() { return order; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
}
