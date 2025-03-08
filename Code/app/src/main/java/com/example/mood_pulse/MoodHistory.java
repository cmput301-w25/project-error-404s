package com.example.mood_pulse;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class MoodHistory extends AppCompatActivity {

    private ListView moodListView;
    private ArrayList<MoodEvent> moodEvents;
    private ArrayAdapter<String> moodAdapter;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TODO: Match the views ID
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mood_history);

        moodListView = findViewById(R.id.moodHistory);
        moodEvents = new ArrayList<>();

        // Dummy Data for Testing (Delete this (UI team))
        //moodEvents.add(new MoodEvent("Happy", "Sunshine", "With Friends", new Date()));
        //moodEvents.add(new MoodEvent("Sad", "Rain", "Alone", new Date()));

        // Sort mood events by date (latest first)
        Collections.sort(moodEvents, (a, b) -> b.getDate().compareTo(a.getDate()));

        // Convert to string list for display
        ArrayList<String> moodSummaries = new ArrayList<>();
        for (MoodEvent event : moodEvents) {
            moodSummaries.add(dateFormat.format(event.getDate()) + " - " + event.getEmotionalState());
        }

        moodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, moodSummaries);
        moodListView.setAdapter(moodAdapter);

        // Handle item click to navigate to details view
        moodListView.setOnItemClickListener((adapterView, view, position, id) -> {
            Intent intent = new Intent(MoodHistory.this, MoodDetails.class);
            intent.putExtra("MoodEvent", moodEvents.get(position)); // Pass selected event
            startActivity(intent);
        });
    }
}