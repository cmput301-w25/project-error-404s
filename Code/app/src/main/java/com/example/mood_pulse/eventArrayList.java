package com.example.mood_pulse;

import java.util.ArrayList;
import java.util.List;

public class eventArrayList {
    private static List<MoodEvent> events;

    public eventArrayList() {
        this.events = new ArrayList<>();
    }

    public void addEvent(MoodEvent event) {
        events.add(event);
    }

    public static void remove(MoodEvent event) {
        events.remove(event);
    }

    public List<MoodEvent> getEvents() {
        return new ArrayList<>(events);
    }

    public void clearEvents() {
        events.clear();
    }
}
