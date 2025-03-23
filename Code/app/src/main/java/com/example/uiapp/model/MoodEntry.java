package com.example.uiapp.model;

import java.io.Serializable;

public class MoodEntry implements Serializable {
    private String dateTime;
    private String mood;

    private static String note;
    private static String people;
    private static String location;
    private String username;
    private double latitude;
    private double longitude;
    private int moodIcon;
    private String imageUrl;
    private Boolean isHome;
    private String moodID;

    // Main constructor
    public MoodEntry(String dateTime, String mood, String note, String people,
                     String location, int moodIcon, String imageUri, Boolean isHome) {
        this.dateTime = dateTime;
        this.mood = mood;
        this.note = note;
        this.people = people;
        this.location = location;
        this.moodIcon = moodIcon;
        this.imageUrl = imageUri;

//        this.isHome = isHome; // Nullable field
//    }

    // Overloaded constructor without isHome (default to null)
//    public MoodEntry(String dateTime, String mood, String note, String people, String location, int moodIcon, int imageUrl) {
//        this(dateTime, mood, note, people, location, moodIcon, imageUrl, null);
//    }

//    public MoodEntry(){

//    }

    // Getter and Setter for isHome
//    public Boolean getIsHome() {
//        return isHome;
//    }

//    public void setIsHome(Boolean isHome) {

        this.isHome = isHome;
    }

    // Overloaded constructor without isHome
    public MoodEntry(String dateTime, String mood, String note, String people,
                     String location, int moodIcon, String imageUri) {
        this(dateTime, mood, note, people, location, moodIcon, imageUri, null);
    }


    // Add the missing getter/setter for imageUri
    public String getImageUrl() {
        return imageUrl;

// Save the following functions in case  
//    public static String getNote() {
//        return note;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

//    public static String getPeople() {
//        return people;
//    }


    public Boolean getIsHome() { return isHome; }
    public void setIsHome(Boolean isHome) { this.isHome = isHome; }
    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    public static String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public static String getPeople() { return people; }
    public void setPeople(String people) { this.people = people; }
    public static String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getMoodIcon() { return moodIcon; }
    public void setMoodIcon(int moodIcon) { this.moodIcon = moodIcon; }


//    public static String getLocation() {
//        return location;
//    }

    public boolean hasLocation() {
        return latitude != 0 && longitude != 0;
    }


// saving the functions below in case it is needed  
//    public int getMoodIcon() {
//        return moodIcon;
//    }

//    public void setMoodIcon(int moodIcon) {
//        this.moodIcon = moodIcon;
//    }

//    public int getImageUrl() {
//        return imageUrl;
//    }

//    public void setImageUrl(int imageUrl) {
//        this.imageUrl = imageUrl;
//    }

    public void setFirestoreId(String ID) { this.moodID = ID;}

    public String getFirestoreId() {return moodID;}
//}

}