package com.example.error404smoodyapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CreateMoodEvent extends AppCompatActivity {

    private TextView dateTime;
    private Spinner emotionalStateSpinner;
    private EditText trigger;
    private EditText socialSituation;
    private Button createMoodEventButton;
    Date currentDate;

    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_mood);

        // get the Id's of elements
        dateTime = findViewById(R.id.dateTime);
        emotionalStateSpinner = findViewById(R.id.emotionalState);
        trigger = findViewById(R.id.emotional_Trigger);
        socialSituation = findViewById(R.id.social_situation);
        createMoodEventButton = findViewById(R.id.Create_Mood_event);

        // get the current date and time and set it
        currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm d-MM-yyyy", Locale.getDefault());
        String formatDate = dateFormat.format(currentDate);
        dateTime.setText(formatDate);

        //add the emotions in a spinner
        ArrayList<String> emotionList = new ArrayList<>();
        emotionList.add("Select Emotion");
        emotionList.add("Happy");
        emotionList.add("Sad");
        emotionList.add("Angry");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, emotionList);
        emotionalStateSpinner.setAdapter(adapter);

        //handle submit button
        createMoodEventButton.setOnClickListener(v -> checkCreate());
    }


    private void checkCreate() {

        String selectedEmotion = emotionalStateSpinner.getSelectedItem().toString();
        String emotionalTrigger = trigger.getText().toString();
        String socialSituationCapture = socialSituation.getText().toString();
        // validation for selecting emotional state
        if (selectedEmotion.equals("Select Emotion") || selectedEmotion.isEmpty()){
            Toast.makeText(this, "Select an Emotional State",Toast.LENGTH_LONG).show();
        }

        MoodEvent moodEvent = new MoodEvent(selectedEmotion,emotionalTrigger,socialSituationCapture,currentDate);
        Toast.makeText(this, "Created Mood Event",Toast.LENGTH_LONG).show();

    }

}
