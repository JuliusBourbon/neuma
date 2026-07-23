package com.example.neuma.models;

import java.util.List;

public class Question {
    private String id;
    private String levelId;
    private String type; // MULTIPLE_CHOICE, SIGN_PRACTICE
    private String text;
    private String mediaUrl;
    private int order;
    // NOTE: correctAnswer is hidden from client intentionally based on backend modification
    private List<Option> options;

    public String getId() { return id; }
    public String getLevelId() { return levelId; }
    public String getType() { return type; }
    public String getText() { return text; }
    public String getMediaUrl() { return mediaUrl; }
    public int getOrder() { return order; }
    public List<Option> getOptions() { return options; }
}
