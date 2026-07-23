package com.example.neuma.models;

import java.util.List;

public class FinishAttemptResponse {
    private String attemptId;
    private int totalScore;
    private List<Achievement> newAchievements;

    public String getAttemptId() { return attemptId; }
    public int getTotalScore() { return totalScore; }
    public List<Achievement> getNewAchievements() { return newAchievements; }

    public static class Achievement {
        private String id;
        private String title;
        private String avatarRewardSeed;
        private String avatarRewardStyle;

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getAvatarRewardSeed() { return avatarRewardSeed; }
        public String getAvatarRewardStyle() { return avatarRewardStyle; }
    }
}
