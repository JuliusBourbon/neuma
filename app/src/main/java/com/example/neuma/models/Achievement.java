package com.example.neuma.models;

public class Achievement {
    private String id;
    private String code;
    private String title;
    private String description;
    private boolean isUnlocked;
    private int progress;
    private int target;
    private String rewardAvatarId;
    private String rewardAvatarSeed;
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
