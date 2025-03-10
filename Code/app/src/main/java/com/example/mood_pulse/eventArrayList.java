package com.example.mood_pulse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class eventArrayList {
    private List<MoodEvent> events = new ArrayList<>();

    public void addEvent(MoodEvent event) {
        if (event == null) throw new NullPointerException();
        if (events.contains(event)) {
            throw new IllegalArgumentException("Duplicate event");
        }
        events.add(event);
    }
    public void updateEvents(List<MoodEvent> newEvents) {
        this.events.clear();
        this.events.addAll(newEvents);
    }

    public void clearEvents() {
        events.clear();
    }

    public void removeEvent(MoodEvent event) {
        events.remove(event);
    }

    public List<MoodEvent> getEvents() {
        List<MoodEvent> sorted = new ArrayList<>(events);
        Collections.sort(sorted);
        return sorted;
    }
}