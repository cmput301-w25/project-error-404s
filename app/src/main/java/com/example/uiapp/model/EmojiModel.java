package com.example.uiapp.model;

import android.graphics.Color;

public class EmojiModel {
    private int emojiPath;
    private String name;
    private int color;


    public EmojiModel(int emojiPath, String name, int color) {
        this.emojiPath = emojiPath;
        this.name = name;
        this.color = color;
    }

    public int getEmojiPath() {
        return emojiPath;
    }

    public void setEmojiPath(int emojiPath) {
        this.emojiPath = emojiPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
