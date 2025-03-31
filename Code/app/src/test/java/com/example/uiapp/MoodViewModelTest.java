package com.example.uiapp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.uiapp.model.MoodEntry;
import com.example.uiapp.model.UserModel;
import com.example.uiapp.utils.HelperClass;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.*;

// filter and sorting
public class MoodViewModelTest {

    List<MoodEntry> mockMoods;

    @BeforeEach
    public void setup() {
        // Simulate logged-in user with followers
        UserModel user = new UserModel("me", "1234");
        user.setFollowers(Arrays.asList("joestar", "jojo"));
        HelperClass.users = user;

        mockMoods = new ArrayList<>();
        mockMoods.add(createMood("me", "Mar 30, 2025 | 10:00", "Private"));
        mockMoods.add(createMood("joestar", "Mar 30, 2025 | 11:00", "Public"));
        mockMoods.add(createMood("jojo", "Mar 30, 2025 | 09:00", "Public"));
        mockMoods.add(createMood("kebabs", "Mar 30, 2025 | 13:00", "Public")); // not a follower
    }

    private MoodEntry createMood(String username, String dateTime, String status) {
        MoodEntry moods = new MoodEntry();
        moods.setUsername(username);
        moods.setDateTime(dateTime);
        moods.setStatus(status);
        return moods;
    }

    @Test
    public void testMyMoodFiltering() {
        List<MoodEntry> myMoods = new ArrayList<>();

        for (MoodEntry m : mockMoods) {
            if (m.getUsername().equals(HelperClass.users.getUsername())) {
                myMoods.add(m);
            }
        }

        assertEquals(1, myMoods.size());
        assertEquals("me", myMoods.get(0).getUsername());
    }

    @Test
    public void testFollowerPublicMoodFiltering() {
        List<MoodEntry> followerMoods = new ArrayList<>();

        for (MoodEntry m : mockMoods) {
            if (!m.getUsername().equals(HelperClass.users.getUsername())
                    && HelperClass.users.getFollowers().contains(m.getUsername())
                    && m.getStatus().equals("Public")) {
                followerMoods.add(m);
            }
        }

        assertEquals(2, followerMoods.size());
    }

    @Test
    public void testSortingByDateTimeDescending() {
        List<MoodEntry> testList = new ArrayList<>();
        testList.add(createMood("a", "Mar 30, 2025 | 09:00", "Public"));
        testList.add(createMood("b", "Mar 30, 2025 | 11:00", "Public"));
        testList.add(createMood("c", "Mar 30, 2025 | 10:00", "Public"));

        // Sort by datetime (newest first)
        testList.sort((m1, m2) -> {
            try {
                String dt1 = m1.getDateTime().replace(" | ", " ");
                String dt2 = m2.getDateTime().replace(" | ", " ");
                SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                Date d1 = format.parse(dt1);
                Date d2 = format.parse(dt2);
                return d2.compareTo(d1); // reverse order
            } catch (Exception e) {
                return 0;
            }
        });

        // Check that the first is the most recent
        assertEquals("Mar 30, 2025 | 11:00", testList.get(0).getDateTime());
    }
}
