package com.example.mood_pulse;

import java.util.ArrayList;
import java.util.List;

/**
 * This class serves as a data structure to store and manage mood events.
 * -----------------------------------------------------------------------------
 * Handled in this class:
 * - Storing mood events in an internal list
 * - Adding new mood events to the list
 * - Updating the list with new data
 * - Retrieving and clearing mood events
 */
public class eventArrayList {
    private List<MoodEvent> events;

    /**
     * Initializes an empty list to store mood events.
     * -----------------------------------------------------------------------------
     * Handled in this method:
     * - Creates a new ArrayList to hold mood events
     */
    public eventArrayList() {
        this.events = new ArrayList<>();
    }

    /**
     * Adds a mood event to the list.
     * -----------------------------------------------------------------------------
     * Handled in this method:
     * - Appends a new mood event to the internal list
     *
     * @param event The mood event to be added.
     */
    public void addEvent(MoodEvent event) {
        events.add(event);
    }

    /**
     * Updates the list with a new set of mood events.
     * -----------------------------------------------------------------------------
     * Handled in this method:
     * - Clears the existing list of mood events
     * - Adds the updated mood events to the list
     *
     * @param newEvents The updated list of mood events.
     */
    public void updateEvents(List<MoodEvent> newEvents) {
        this.events.clear();
        this.events.addAll(newEvents);
    }

    /**
     * Retrieves the list of stored mood events.
     * -----------------------------------------------------------------------------
     * Handled in this method:
     * - Returns the internal list of mood events
     *
     * @return The list of mood events.
     */
    public List<MoodEvent> getEvents() {
        return events;
    }

    /**
     * Clears all mood events from the list.
     * -----------------------------------------------------------------------------
     * Handled in this method:
     * - Removes all mood events from the internal list
     */
    public void clearEvents() {
        events.clear();
    }
}
