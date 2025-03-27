package com.example.uiapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "mood_table")
public class MoodEntry implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int roomID;
    private String dateTime;
    private String mood;
    private String note;
    private String people;
    private String location;
    private String username;
    private double latitude;
    private double longitude;
    private int moodIcon;
    private String imageUrl;
    private Boolean isHome;
    private String moodID; // Firestore document ID

    // Constructor for creating MoodEntry instances
    public MoodEntry(String dateTime, String mood, String note, String people,
                     String location, int moodIcon, String imageUrl, Boolean isHome) {
        this.dateTime = dateTime;
        this.mood = mood;
        this.note = note;
        this.people = people;
        this.location = location;
        this.moodIcon = moodIcon;
        this.imageUrl = imageUrl;
        this.isHome = isHome;
        this.moodID = null;  // Initially null, Firestore ID will be set later
    }

    // Default constructor for Room database
    public MoodEntry() {}

    // Getters and Setters
    public int getRoomID() { return roomID; }
    public void setRoomID(int roomID) { this.roomID = roomID; }

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

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getIsHome() { return isHome; }
    public void setIsHome(Boolean isHome) { this.isHome = isHome; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getMoodID() { return moodID; }
    public void setMoodID(String moodID) { this.moodID = moodID; }

    // Checks if the mood entry has location data
    public boolean hasLocation() {
        return latitude != 0 && longitude != 0;
    }

    // Method to set Firestore ID when syncing with Firestore
    public void setFirestoreId(String ID) { this.moodID = ID; }
    public String getFirestoreId() { return moodID; }
}
