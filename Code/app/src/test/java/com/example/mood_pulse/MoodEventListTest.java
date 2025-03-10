package com.example.mood_pulse;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.List;

public class MoodEventListTest {

    private eventArrayList createTestList() {
        eventArrayList list = new eventArrayList();
        list.addEvent(createTestEvent());
        return list;
    }

    private MoodEvent createTestEvent() {
        return new MoodEvent(
                1,
                "Happy",
                "Good weather",
                "Alone",
                new Date(),
                "Nice day"
        );
    }

    // Test 1: Add new mood event
    @Test
    void addEvent_IncreasesListSize() {
        eventArrayList list = new eventArrayList();
        list.addEvent(createTestEvent());
        assertEquals(1, list.getEvents().size());
    }

    // Test 2: Prevent duplicate additions
    @Test
    void addDuplicateEvent_ThrowsException() {
        eventArrayList list = createTestList();
        MoodEvent duplicate = createTestEvent();
        assertThrows(IllegalArgumentException.class, () ->
                list.addEvent(duplicate)
        );
    }

    // Test 3: Remove existing event
    @Test
    void removeEvent_DecreasesListSize() {
        eventArrayList list = createTestList(); // Contains 1 event
        MoodEvent eventToRemove = list.getEvents().get(0);

        list.removeEvent(eventToRemove); // Use the new method

        assertEquals(0, list.getEvents().size(),
                "List should be empty after removal");
    }

    // Test 4: Update note content
    @Test
    void updateNote_ModifiesExistingEvent() {
        eventArrayList list = createTestList();
        MoodEvent original = list.getEvents().get(0);
        original.setNote("Updated note");
        assertEquals("Updated note", list.getEvents().get(0).getNote());
    }

    // Test 5: Chronological sorting
    @Test
    void getEvents_ReturnsSortedByDate() {
        eventArrayList list = new eventArrayList();

        MoodEvent oldEvent = new MoodEvent(
                2, "Sad", "Rain", "Alone",
                new Date(System.currentTimeMillis() - 10000),
                "Old note"
        );

        MoodEvent newEvent = new MoodEvent(
                3, "Neutral", "Cloudy", "Friends",
                new Date(),
                "New note"
        );

        list.addEvent(oldEvent);
        list.addEvent(newEvent);

        List<MoodEvent> sorted = list.getEvents();
        assertEquals(newEvent, sorted.get(0));
        assertEquals(oldEvent, sorted.get(1));
    }

    // Test 6: Clear all events
    @Test
    void clearEvents_EmptiesList() {
        eventArrayList list = createTestList();
        list.clearEvents();
        assertEquals(0, list.getEvents().size());
    }

    // Test 7: Retrieve empty list
    @Test
    void getEvents_EmptyList_ReturnsEmpty() {
        eventArrayList list = new eventArrayList();
        assertTrue(list.getEvents().isEmpty());
    }

    // Test 8: Verify event properties
    @Test
    void getEvent_ContainsCorrectData() {
        MoodEvent testEvent = createTestEvent();
        eventArrayList list = new eventArrayList();
        list.addEvent(testEvent);

        MoodEvent retrieved = list.getEvents().get(0);
        assertAll(
                () -> assertEquals("Happy", retrieved.getEmotionalState()),
                () -> assertEquals("Alone", retrieved.getSocialSituation()),
                () -> assertEquals("Nice day", retrieved.getNote())
        );
    }

    // Test 9: Prevent null additions
    @Test
    void addNullEvent_ThrowsException() {
        eventArrayList list = new eventArrayList();
        assertThrows(NullPointerException.class, () ->
                list.addEvent(null)
        );
    }

    // Test 10: Update non-existing event
    @Test
    void updateNonExistentEvent_NoChange() {
        eventArrayList list = createTestList();
        MoodEvent newEvent = new MoodEvent(
                2, "Sad", "Rain", "Alone",
                new Date(),
                "New entry"
        );

        assertFalse(list.getEvents().contains(newEvent));
    }
}