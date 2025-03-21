package com.example.uiapp.model;
public class MoodEntry {
    private String dateTime;
    private String mood;
    private static String note;
    private static String people;
    private static String location;
    private int moodIcon;
    private int imageUrl; // Nullable, only for entries with images
    private Boolean isHome; // Nullable field
    private String moodID;

    // Constructor with isHome as an optional parameter
    public MoodEntry(String dateTime, String mood, String note, String people, String location, int moodIcon, int imageUrl, Boolean isHome) {
        this.dateTime = dateTime;
        this.mood = mood;
        this.note = note;
        this.people = people;
        this.location = location;
        this.moodIcon = moodIcon;
        this.imageUrl = imageUrl;
        this.isHome = isHome; // Nullable field
    }

    // Overloaded constructor without isHome (default to null)
    public MoodEntry(String dateTime, String mood, String note, String people, String location, int moodIcon, int imageUrl) {
        this(dateTime, mood, note, people, location, moodIcon, imageUrl, null);
    }

    // Getter and Setter for isHome
    public Boolean getIsHome() {
        return isHome;
    }

    public void setIsHome(Boolean isHome) {
        this.isHome = isHome;
    }

    // Other getters and setters
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public static String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public static String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public static String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getMoodIcon() {
        return moodIcon;
    }

    public void setMoodIcon(int moodIcon) {
        this.moodIcon = moodIcon;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setFirestoreId(String ID) { this.moodID = ID;}

    public String getFirestoreId() {return moodID;}
}

