package com.example.neuma.models;

public class AnswerResponse {
    private boolean isCorrect;
    private int attemptNumber;
    private int pointsEarned;
    private int streakBonus;
    private int totalThisAnswer;

    public boolean isCorrect() { return isCorrect; }
    public int getAttemptNumber() { return attemptNumber; }
    public int getPointsEarned() { return pointsEarned; }
    public int getStreakBonus() { return streakBonus; }
    public int getTotalThisAnswer() { return totalThisAnswer; }
}
