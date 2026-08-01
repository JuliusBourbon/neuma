package com.example.neuma.models;

public class AvatarItem {
    private String style;
    private String seed;
    private String name;

    public AvatarItem(String style, String seed, String name) {
        this.style = style;
        this.seed = seed;
        this.name = name;
    }

    public String getStyle() {
        return style;
    }

    public String getSeed() {
        return seed;
    }

    public String getName() {
        return name;
    }
}
