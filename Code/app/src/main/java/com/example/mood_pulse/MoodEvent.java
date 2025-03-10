package com.example.mood_pulse;

import java.io.Serializable;
import java.util.Date;

/**
 * This class represents a mood event recorded by the user.
 * Each mood event includes details such as:
 * - Emotional state
 * - Trigger (reason for the mood)
 * - Social situation (who the user was with)
 * - Date and time when the mood was recorded
 * - An optional note to describe the experience
 * -----------------------------------------------------------------------------
 * Handled in this class:
 * - Stores mood event details
 * - Provides getters and setters to access and modify event data
 * - Implements Serializable to allow data transfer between activities
 */
public class MoodEvent implements Serializable {

    private String firestoreId; // Firebase auto-generated document ID
    private int moodId; // ID of the mood event
    private String emotionalState;
    private String trigger;
    private String socialSituation;
    private Date date;
    private String note;

    // Constructor with moodId
    /**
     * Constructs a MoodEvent with the specified details.
     * -----------------------------------------------------------------------------
     * Parameters:
     * - moodId: Unique identifier for the mood event
     * - emotionalState: The emotional state recorded by the user
     * - trigger: The cause or reason for the mood
     * - socialSituation: The social setting when the mood was recorded
     * - date: The date and time of the mood event
     * - noteText: Additional notes about the mood event
     */
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
    /**
     * Retrieves the Firestore document ID for this mood event.
     * -----------------------------------------------------------------------------
     * @return The Firestore document ID as a String.
     */
    public String getFirestoreId() {
        return firestoreId;
    }
    
    /**
     * Sets the Firestore document ID for this mood event.
     * -----------------------------------------------------------------------------
     * @param firestoreId The Firestore document ID to be set.
     */
    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    /**
     * Retrieves the emotional state of the user for this mood event.
     * -----------------------------------------------------------------------------
     * @return The recorded emotional state as a String.
     */
    public String getEmotionalState() {
        return emotionalState;
    }

    /**
     * Sets the emotional state of the user for this mood event.
     * -----------------------------------------------------------------------------
     * @param emotionalState The emotional state to be recorded.
     */
    public void setEmotionalState(String emotionalState) {
        this.emotionalState = emotionalState;
    }

    /**
     * Retrieves the trigger (cause) of the mood event.
     * -----------------------------------------------------------------------------
     * @return The trigger as a String.
     */
    public String getTrigger() {
        return trigger;
    }

    /**
     * Sets the trigger (cause) of the mood event.
     * -----------------------------------------------------------------------------
     * @param trigger The trigger to be recorded.
     */
    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    /**
     * Retrieves the social situation when the mood event was recorded.
     * -----------------------------------------------------------------------------
     * @return The social situation as a String.
     */
    public String getSocialSituation() {
        return socialSituation;
    }

    /**
     * Sets the social situation when the mood event was recorded.
     * -----------------------------------------------------------------------------
     * @param socialSituation The social situation to be recorded.
     */
    public void setSocialSituation(String socialSituation) {
        this.socialSituation = socialSituation;
    }

    /**
     * Retrieves the date and time of the mood event.
     * -----------------------------------------------------------------------------
     * @return The recorded date and time as a Date object.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date and time of the mood event.
     * -----------------------------------------------------------------------------
     * @param date The date and time to be recorded.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Retrieves any additional notes associated with the mood event.
     * -----------------------------------------------------------------------------
     * @return The note as a String.
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets additional notes for the mood event.
     * -----------------------------------------------------------------------------
     * @param note The note to be recorded.
     */
    public void setNote(String note) {
        this.note = note;
    }
}
