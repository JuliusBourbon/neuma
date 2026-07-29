package com.example.neuma.models;

public class Misi {
    private String title;
    private int currentProgress;
    private int maxProgress;
    private boolean isCompleted;

    public Misi(String title, int currentProgress, int maxProgress, boolean isCompleted) {
        this.title = title;
        this.currentProgress = currentProgress;
        this.maxProgress = maxProgress;
        this.isCompleted = isCompleted;
    }

    public String getTitle() { return title; }
    public int getCurrentProgress() { return currentProgress; }
    public int getMaxProgress() { return maxProgress; }
    public boolean isCompleted() { return isCompleted; }
}
