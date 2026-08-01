package com.example.neuma.models;

import java.util.List;

public class Question {
    private String id;
    private String levelId;
    private String type; // MULTIPLE_CHOICE, TRUE_FALSE_VISUAL, SIGN_PRACTICE
    @com.google.gson.annotations.SerializedName("prompt")
    private String text;
    private String mediaUrl;
    private int order;
    private String correctAnswer; // Only populated for SIGN_PRACTICE
    private List<Option> options;

    public String getId() { return id; }
    public String getLevelId() { return levelId; }
    public String getType() { return type; }
    public String getText() { return text; }
    public String getMediaUrl() { return mediaUrl; }
    public int getOrder() { return order; }
    public String getCorrectAnswer() { return correctAnswer; }
    public List<Option> getOptions() { return options; }
}
