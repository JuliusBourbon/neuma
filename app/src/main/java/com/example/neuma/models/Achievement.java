package com.example.neuma.models;

public class Achievement {
    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("code")
    private String code;
    @com.google.gson.annotations.SerializedName("title")
    private String title;
    @com.google.gson.annotations.SerializedName("description")
    private String description;
    @com.google.gson.annotations.SerializedName("isUnlocked")
    private boolean isUnlocked;
    @com.google.gson.annotations.SerializedName("progress")
    private int progress;
    @com.google.gson.annotations.SerializedName("target")
    private int target;
    @com.google.gson.annotations.SerializedName("rewardAvatarId")
    private String rewardAvatarId;
    @com.google.gson.annotations.SerializedName("rewardAvatarSeed")
    private String rewardAvatarSeed;
    @com.google.gson.annotations.SerializedName("rewardAvatarStyle")
    private String rewardAvatarStyle;

    public Achievement() {}

    public Achievement(String id, String code, String title, String description, boolean isUnlocked) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.description = description;
        this.isUnlocked = isUnlocked;
    }

    public String getId() { return id; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isUnlocked() { return isUnlocked; }
    public int getProgress() { return progress; }
    public int getTarget() { return target; }
    public String getRewardAvatarId() { return rewardAvatarId; }
    public String getRewardAvatarSeed() { return rewardAvatarSeed; }
    public String getRewardAvatarStyle() { return rewardAvatarStyle; }
}
