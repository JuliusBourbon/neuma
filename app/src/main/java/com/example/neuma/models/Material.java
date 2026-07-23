package com.example.neuma.models;

public class Material {
    private String id;
    private String levelId;
    private String type; // GENERAL_INTRO, BISINDO_INTRO, WORD_EXAMPLE
    private String title;
    private String textContent;
    private String mediaUrl;
    private int order;

    public String getId() { return id; }
    public String getLevelId() { return levelId; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getTextContent() { return textContent; }
    public String getMediaUrl() { return mediaUrl; }
    public int getOrder() { return order; }
}
