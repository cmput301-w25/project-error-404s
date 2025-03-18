package com.example.mood_pulse;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class MoodEvent implements Comparable<MoodEvent>, Serializable {

    private String firestoreId; // Firebase auto-generated document ID
    private int moodId; // ID of the mood event
    private String emotionalState;
    private String trigger;
    private String socialSituation;
    private Date date;
    private String note;

    // Constructor with moodId
    public MoodEvent(int moodId, String emotionalState, String trigger, String socialSituation, Date date, String noteText) {
        this.moodId = moodId;
        this.firestoreId = this.firestoreId = String.valueOf(moodId);
        this.emotionalState = emotionalState;
        this.trigger = trigger;
        this.socialSituation = socialSituation;
        this.date = date;
        this.note = noteText;
    }

    // Getters and Setters
    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    public String getEmotionalState() {
        return emotionalState;
    }

    public void setEmotionalState(String emotionalState) {
        this.emotionalState = emotionalState;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getSocialSituation() {
        return socialSituation;
    }

    public void setSocialSituation(String socialSituation) {
        this.socialSituation = socialSituation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoodEvent moodEvent = (MoodEvent) o;
        return moodId == moodEvent.moodId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(moodId);
    }

    @Override
    public int compareTo(MoodEvent other) {
        return this.date.compareTo(other.getDate());
    }
}
