package com.example.uiapp.model;

import java.io.Serializable;

public class MoodEntry implements Serializable {
    private String moodId;
    private String username;
    private String dateTime;
    private String mood;
    private String note;
    private String people;
    private String location;
    private String locationLat;
    private String locationLng;
    private String status;
    private int moodIcon;
    private String imageUrl;

    public MoodEntry() {
    }

    /**
     * This activity sets up the general parameter that a mood entry requires.
     * ------------------------------------------------------------------------
     * @param moodId
     * @param username
     * @param dateTime
     * @param mood
     * @param note
     * @param people
     * @param location
     * @param locationLat
     * @param locationLng
     * @param status
     * @param moodIcon
     * @param imageUrl
     */
    public MoodEntry(String moodId, String username, String dateTime, String mood, String note, String people, String location, String locationLat, String locationLng, String status, int moodIcon, String imageUrl) {
        this.moodId = moodId;
        this.username = username;
        this.dateTime = dateTime;
        this.mood = mood;
        this.note = note;
        this.people = people;
        this.location = location;
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.status = status;
        this.moodIcon = moodIcon;
        this.imageUrl = imageUrl;
    }

    public String getMoodId() {
        return moodId;
    }

    public void setMoodId(String moodId) {
        this.moodId = moodId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(String locationLat) {
        this.locationLat = locationLat;
    }

    public String getLocationLng() {
        return locationLng;
    }

    public void setLocationLng(String locationLng) {
        this.locationLng = locationLng;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMoodIcon() {
        return moodIcon;
    }

    public void setMoodIcon(int moodIcon) {
        this.moodIcon = moodIcon;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}