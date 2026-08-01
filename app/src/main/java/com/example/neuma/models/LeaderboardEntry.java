package com.example.neuma.models;

public class LeaderboardEntry {
    private String userId;
    private String name;
    private String avatarSeed;
    private String avatarStyle;
    private int score;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getAvatarSeed() { return avatarSeed; }
    public void setAvatarSeed(String avatarSeed) { this.avatarSeed = avatarSeed; }
    
    public String getAvatarStyle() { return avatarStyle; }
    public void setAvatarStyle(String avatarStyle) { this.avatarStyle = avatarStyle; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
