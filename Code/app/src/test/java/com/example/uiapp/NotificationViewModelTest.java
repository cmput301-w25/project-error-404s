package com.example.uiapp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

// testing follow requests
public class NotificationViewModelTest {

    // A fake list to simulate follow requests
    List<String> pendingRequests;

    @BeforeEach
    public void setup() {
        // Start with an empty list before each test
        pendingRequests = new ArrayList<>();
    }

    @Test
    public void testEmptyList() {
        //Test that the list starts empty
        assertEquals(0, pendingRequests.size());
    }

    @Test
    public void testAddOneRequest() {
        // Add one user to the pending requests
        pendingRequests.add("abcd");

        // Check that the size is now 1
        assertEquals(1, pendingRequests.size());
        // Check the value at position 0
        assertEquals("abcd", pendingRequests.get(0));
    }

    @Test
    public void testAddMultipleRequests() {
        // Add multiple users
        pendingRequests.add("x");
        pendingRequests.add(" ");
        pendingRequests.add("lelouch");

        // Check the list size
        assertEquals(3, pendingRequests.size());

        // Check values in the list
        assertEquals("x", pendingRequests.get(0));
        assertEquals(" ", pendingRequests.get(1));
        assertEquals("lelouch", pendingRequests.get(2));
    }
}
