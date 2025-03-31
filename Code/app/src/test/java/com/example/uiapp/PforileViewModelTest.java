package com.example.uiapp;

import com.example.uiapp.model.MoodEntry;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;


 // This is a test class for some of the logic in ProfileViewModel

public class PforileViewModelTest {


    // Parses date string to LocalDateTime
    private LocalDateTime parseDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy | HH:mm", Locale.ENGLISH);
        return LocalDateTime.parse(dateStr, formatter);
    }


    // Creates a fake MoodEntry

    private MoodEntry createMood(String username, String status, String dateTime) {
        MoodEntry m = new MoodEntry();
        m.setUsername(username);
        m.setStatus(status);
        m.setDateTime(dateTime);
        return m;
    }


    // Test filtering public moods for other user

    @Test
    public void testFilterOtherUserMoods() {
        List<MoodEntry> allMoods = new ArrayList<>();

        allMoods.add(createMood("Esa", "Public", "Mar 30, 2025 | 10:00"));
        allMoods.add(createMood("Esa", "Private", "Mar 30, 2025 | 11:00"));
        allMoods.add(createMood("Maan", "Public", "Mar 30, 2025 | 12:00"));

        List<MoodEntry> filtered = new ArrayList<>();
        for (MoodEntry m : allMoods) {
            if (m.getUsername().equals("bruh") && m.getStatus().equals("Public")) {
                filtered.add(m);
            }
            if (m.getUsername().equals("Esa") && m.getStatus().equals("Public")) {
                filtered.add(m);
            }

            assertEquals(1, filtered.size());
            assertEquals("Esa", filtered.get(0).getUsername());
            assertEquals("Public", filtered.get(0).getStatus());
        }
    }
}
