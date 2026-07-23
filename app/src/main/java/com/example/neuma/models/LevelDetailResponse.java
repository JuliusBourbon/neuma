package com.example.neuma.models;

import java.util.List;

public class LevelDetailResponse {
    private Level level;
    private UserProgress userProgress;
    private List<LeaderboardEntry> leaderboard;
    private Integer userRank;

    public Level getLevel() { return level; }
    public UserProgress getUserProgress() { return userProgress; }
    public List<LeaderboardEntry> getLeaderboard() { return leaderboard; }
    public Integer getUserRank() { return userRank; }

    public static class UserProgress {
        private String status;
        private int bestScore;

        public String getStatus() { return status; }
        public int getBestScore() { return bestScore; }
    }
}
