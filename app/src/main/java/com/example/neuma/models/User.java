package com.example.neuma.models;

public class User {
    private String id;
    private String name;
    private String avatarStyle;
    private String avatarSeed;
    private int points;
    private int levelsCompleted;
    private int achievementsCount;
    private int streak;
    private java.util.List<ActivityItem> recentActivities;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAvatarStyle() { return avatarStyle; }
    public String getAvatarSeed() { return avatarSeed; }
    public int getPoints() { return points; }
    public int getLevelsCompleted() { return levelsCompleted; }
    public int getAchievementsCount() { return achievementsCount; }
    public int getStreak() { return streak; }
    public java.util.List<ActivityItem> getRecentActivities() { return recentActivities; }

    public static class ActivityItem {
        private String title;
        private String timestamp;
        private String type;

        public String getTitle() { return title; }
        public String getTimestamp() { return timestamp; }
        public String getType() { return type; }
    }
}
