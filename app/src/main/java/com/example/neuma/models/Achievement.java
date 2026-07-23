package com.example.neuma.models;

public class Achievement {
    private String id;
    private String code;
    private String title;
    private String description;
    private boolean isUnlocked;
    private String rewardAvatarSeed;
    private String rewardAvatarStyle;

    public String getId() { return id; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isUnlocked() { return isUnlocked; }
    public String getRewardAvatarSeed() { return rewardAvatarSeed; }
    public String getRewardAvatarStyle() { return rewardAvatarStyle; }
}
