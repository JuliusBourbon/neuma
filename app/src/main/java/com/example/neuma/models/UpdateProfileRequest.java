package com.example.neuma.models;

public class UpdateProfileRequest {
    private String name;
    private String avatarStyle;
    private String avatarSeed;

    public UpdateProfileRequest(String name, String avatarStyle, String avatarSeed) {
        this.name = name;
        this.avatarStyle = avatarStyle;
        this.avatarSeed = avatarSeed;
    }

    public String getName() { return name; }
    public String getAvatarStyle() { return avatarStyle; }
    public String getAvatarSeed() { return avatarSeed; }
}
