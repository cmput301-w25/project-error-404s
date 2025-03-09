package com.example.mood_pulse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private static final int ADD_MOOD_REQUEST = 1;
    private eventArrayAdapter adapter;
    private eventArrayList eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize the event list and adapter
        eventList = new eventArrayList();
        adapter = new eventArrayAdapter(this, eventList.getEvents());

        // Initialize the ListView and set the adapter
        ListView moodListView = findViewById(R.id.moodListView);
        moodListView.setAdapter(adapter);

        // Set up the button to open AddMood
        Button addMoodButton = findViewById(R.id.addMoodButton);
        addMoodButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddMood.class);
            startActivityForResult(intent, ADD_MOOD_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_MOOD_REQUEST && resultCode == RESULT_OK) {
            MoodEvent moodEvent = (MoodEvent) data.getSerializableExtra("MoodEvent");

            // Add the mood event to the list and update the adapter
            eventList.addEvent(moodEvent);
            adapter.notifyDataSetChanged();

            Toast.makeText(this, "Mood event added!", Toast.LENGTH_SHORT).show();
        }
    }
}