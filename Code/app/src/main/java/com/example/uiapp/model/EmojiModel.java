package com.example.uiapp.model;

import android.graphics.Color;

public class EmojiModel {
    private int emojiPath;
    private int emojiGrayPath;
    private String name;
    private int color;
    private boolean selected;

    public EmojiModel(int emojiPath, int emojiGrayPath, String name, int color) {
        this.emojiPath = emojiPath;
        this.name = name;
        this.color = color;
        this.selected = false;
    }

    public int getEmojiPath() {
        return emojiPath;
    }

    public int getEmojiGrayPath() { return emojiGrayPath;}

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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
