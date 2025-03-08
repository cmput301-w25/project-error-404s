package com.example.mood_pulse;

import java.io.Serializable;
import java.util.Date;

public class MoodEvent implements Serializable {

    private Integer moodID;
    private String emotionalState;
    private String trigger;
    private String socialSituation;
    private Date date;
    private String note;

    // Constructor
    public MoodEvent(Integer moodID, String emotionalState, String trigger, String socialSituation, Date date, String noteText) {
        this.moodID = moodID;
        this.emotionalState = emotionalState;
        this.trigger = trigger;
        this.socialSituation = socialSituation;
        this.date = date;
        this.note = noteText;
    }

    // public Getters are required for the Serializable interface
    public String getMoodID() { return moodID.toString();}
    public String getEmotionalState() { return emotionalState; }
    public String getTrigger() { return trigger; }
    public String getSocialSituation() { return socialSituation; }
    public Date getDate() { return date; }
    public String getNote(){ return note; }
    public String setEmotionalState(String emotionalState) {
        return this.emotionalState = emotionalState;
    }
    public String setTrigger(String trigger) {
        return this.trigger = trigger;
    }
    public String setSocialSituation (String socialSituation) {
        return this.socialSituation = socialSituation;
    }
    public Date setDate(Date date){
        return this.date = date;
    }
    public String setNote(String note) {
        return this.note = note;
    }
}