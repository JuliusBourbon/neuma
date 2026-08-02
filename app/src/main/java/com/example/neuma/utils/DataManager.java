package com.example.neuma.utils;

import com.example.neuma.models.Achievement;
import com.example.neuma.models.Level;
import com.example.neuma.models.User;

import java.util.List;

public class DataManager {
    private static DataManager instance;

    private User currentUser;
    private List<Level> levels;
    private List<Achievement> achievements;

    private DataManager() {
        // Private constructor to prevent instantiation
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public List<Level> getLevels() {
        return levels;
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public void clear() {
        currentUser = null;
        levels = null;
        achievements = null;
    }
}
