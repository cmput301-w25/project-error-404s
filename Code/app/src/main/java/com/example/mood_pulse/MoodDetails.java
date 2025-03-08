package com.example.mood_pulse;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MoodDetails extends AppCompatActivity {
    private TextView moodDetailText;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TODO: Change the ID
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_detail);

        moodDetailText = findViewById(R.id.moodDetailText);

        // Get passed MoodEvent object
        MoodEvent moodEvent = (MoodEvent) getIntent().getSerializableExtra("MoodEvent");

        if (moodEvent != null) {
            String details =
                    "Date: " + dateFormat.format(moodEvent.getDate()) + "\n" +
                            "Emotional State: " + moodEvent.getEmotionalState() + "\n" +
                            "Trigger: " + moodEvent.getTrigger() + "\n" +
                            "Social Situation: " + moodEvent.getSocialSituation();
            moodDetailText.setText(details);
        }
    }
}
