package com.example.neuma.models;

public class LeaderboardEntry {
    private String userId;
    private String name;
    private String avatarSeed;
    private String avatarStyle;
    private int score;

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getAvatarSeed() { return avatarSeed; }
    public String getAvatarStyle() { return avatarStyle; }
    public int getScore() { return score; }
}
