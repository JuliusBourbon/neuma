package com.example.neuma.models;

import java.util.List;

public class LevelDetailResponse {
    private Level level;
    private UserProgress userProgress;
    private List<LeaderboardEntry> leaderboard;
    private UserRank userRank;

    public Level getLevel() { return level; }
    public UserProgress getUserProgress() { return userProgress; }
    public List<LeaderboardEntry> getLeaderboard() { return leaderboard; }
    public UserRank getUserRank() { return userRank; }

    public static class UserProgress {
        private String status;
        private int bestScore;

        public String getStatus() { return status; }
        public int getBestScore() { return bestScore; }
    }

    public static class UserRank {
        private int rank;
        private int score;

        public int getRank() { return rank; }
        public int getScore() { return score; }
    }
}
