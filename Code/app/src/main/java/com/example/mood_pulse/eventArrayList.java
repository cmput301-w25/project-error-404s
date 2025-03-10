package com.example.mood_pulse;

import java.util.ArrayList;
import java.util.List;

public class eventArrayList {
    private List<MoodEvent> events;

    public eventArrayList() {
        this.events = new ArrayList<>();
    }

    public void addEvent(MoodEvent event) {
        events.add(event);
    }

    public void updateEvents(List<MoodEvent> newEvents) {
        this.events.clear();
        this.events.addAll(newEvents);
    }

    public List<MoodEvent> getEvents() {
        return events;
    }

    public void clearEvents() {
        events.clear();
    }
}