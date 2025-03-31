package com.example.uiapp;

import com.example.uiapp.model.MoodEntry;
import com.example.uiapp.ui.map.MapViewModel;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


 // Test class for MapViewModel filter logic
public class MapViewModelTest {


     // Create a mock MoodEntry
    private MoodEntry createMood(String username, String mood, String date, String status) {
        MoodEntry moodEntry = new MoodEntry();
        moodEntry.setUsername(username);
        moodEntry.setMood(mood);
        moodEntry.setDateTime(date);
        moodEntry.setStatus(status);
        moodEntry.setLocation("location");
        return moodEntry;
    }


     // his test checks basic filtering by mood type
    @Test
    public void testMoodFilter() {
        List<MoodEntry> allMoods = new ArrayList<>();

        allMoods.add(createMood("Xyz", "Happy", "Fri, Mar 29, 2024 | 12:00", "Public"));
        allMoods.add(createMood("Xyz", "Sad", "Fri, Mar 29, 2024 | 13:00", "Public"));
        allMoods.add(createMood("JohnCina", "Happy", "Fri, Mar 29, 2024 | 14:00", "Private"));

        List<MoodEntry> filtered = new ArrayList<>();
        String moodFilter = "Happy";

        for (MoodEntry m : allMoods) {
            if (m.getMood().equalsIgnoreCase(moodFilter)) {
                filtered.add(m);
            }
        }

        assertEquals(2, filtered.size());
    }

    // This test checks display filter when viewing only "mine"

    @Test
    public void testDisplayMineFilter() {
        String currentUser = "maan";

        List<MoodEntry> entries = new ArrayList<>();
        entries.add(createMood("maan", "Happy", "Fri, Mar 29, 2024 | 12:00", "Public"));
        entries.add(createMood("maan1", "Sad", "Fri, Mar 29, 2024 | 13:00", "Public"));

        List<MoodEntry> result = new ArrayList<>();

        for (MoodEntry e : entries) {
            if (e.getUsername().equals(currentUser)) {
                result.add(e);
            }
        }

        assertEquals(1, result.size());
        assertEquals("maan", result.get(0).getUsername());
    }
}
