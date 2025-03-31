package com.example.uiapp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.uiapp.model.MoodEntry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;


public class AddMoodTest {

    private MoodEntry mood;

    @BeforeEach
    public void setup() {

        mood = new MoodEntry(
                "User1",                  // moodId
                "Yser1",                        // username
                "Mar 21, 2025 | 11:00",            // dateTime
                "Happy",                           // mood
                "hello",                  // note
                "Friends",                         // people
                "usa",                       // location
                "51.03",                         // locationLat
                "113.01",                       // locationLng
                "Public",                          // status
                R.drawable.happy,                  // moodIcon
                "https://imageurl.ca"    // imageUrl
        );
    }

    @Test
    public void testMoodFields() {
        // Test each field
        assertEquals("User1", mood.getMoodId());
        assertEquals("Yser1", mood.getUsername());
        assertEquals("Mar 21, 2025 | 11:00", mood.getDateTime());
        assertEquals("Happy", mood.getMood());
        assertEquals("hello", mood.getNote());
        assertEquals("Friends", mood.getPeople());
        assertEquals("usa", mood.getLocation());
        assertEquals("51.03", mood.getLocationLat());
        assertEquals("113.01", mood.getLocationLng());
        assertEquals("Public", mood.getStatus());
        assertEquals(R.drawable.happy, mood.getMoodIcon());
        assertEquals("https://imageurl.ca", mood.getImageUrl());
    }
}
