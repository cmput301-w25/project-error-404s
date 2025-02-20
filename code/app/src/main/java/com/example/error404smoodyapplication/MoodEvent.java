package com.example.error404smoodyapplication;

import java.io.Serializable;
import java.util.Date;

public class MoodEvent implements Serializable {
    private String emotionalState;
    private String trigger;
    private String socialSituation;
    private Date date;
    private String note;

    // Constructor
    public MoodEvent(String emotionalState, String trigger, String socialSituation, Date date, String noteText) {
        this.emotionalState = emotionalState;
        this.trigger = trigger;
        this.socialSituation = socialSituation;
        this.date = date;
        this.note = noteText;
    }

    // public Getters are required for the Serializable interface
    public String getEmotionalState() { return emotionalState; }
    public String getTrigger() { return trigger; }
    public String getSocialSituation() { return socialSituation; }
    public Date getDate() { return date; }
    public String getNote(){ return note; }
}
