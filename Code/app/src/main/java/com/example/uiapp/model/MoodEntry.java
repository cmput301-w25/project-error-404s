package com.example.uiapp.model;

import java.io.Serializable;

public class MoodEntry implements Serializable {
    private String dateTime;
    private String mood;
    private String note;
    private String people;
    private String location;
    private int moodIcon;
    private String imageUri;
    private Boolean isHome;

    // Main constructor
    public MoodEntry(String dateTime, String mood, String note, String people,
                     String location, int moodIcon, String imageUri, Boolean isHome) {
        this.dateTime = dateTime;
        this.mood = mood;
        this.note = note;
        this.people = people;
        this.location = location;
        this.moodIcon = moodIcon;
        this.imageUri = imageUri;
        this.isHome = isHome;
    }

    // Overloaded constructor without isHome
    public MoodEntry(String dateTime, String mood, String note, String people,
                     String location, int moodIcon, String imageUri) {
        this(dateTime, mood, note, people, location, moodIcon, imageUri, null);
    }

    // Add the missing getter/setter for imageUri
    public String getImageUrl() {
        return imageUri;
    }

    public void setImageUrl(String imageUri) {
        this.imageUri = imageUri;
    }


    public Boolean getIsHome() { return isHome; }
    public void setIsHome(Boolean isHome) { this.isHome = isHome; }
    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getPeople() { return people; }
    public void setPeople(String people) { this.people = people; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getMoodIcon() { return moodIcon; }
    public void setMoodIcon(int moodIcon) { this.moodIcon = moodIcon; }
}