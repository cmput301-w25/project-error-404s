package com.example.mood_pulse;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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
    private EditText note;
    private Button createMoodEventButton;
    Date currentDate;

    protected  void onCreate(Bundle savedInstanceState) {

        // TODO: Change the ID of the view and triggers
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_mood);

        // get the Id's of elements
        dateTime = findViewById(R.id.dateTime);
        emotionalStateSpinner = findViewById(R.id.emotionalState);
        trigger = findViewById(R.id.emotional_Trigger);
        socialSituation = findViewById(R.id.social_situation);
        createMoodEventButton = findViewById(R.id.Create_Mood_event);
        note = findViewById(R.id.note);
        TextView noteError = findViewById(R.id.noteError); //noteError helps display error message when you input more than 20chars or 3words in the note Editext

        //Real-time validation for edittext Note
        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //
            }

            @Override
            public void afterTextChanged(Editable s) {
                String noteText = s.toString().trim();
                int wordCount = noteText.isEmpty() ? 0 : noteText.split("\\s+").length;
                if (noteText.length() > 20 || wordCount > 3) {
                    noteError.setVisibility(View.VISIBLE);
                    noteError.setText("No more than 20  characters or 3 words");
                } else {
                    noteError.setVisibility(View.GONE);
                }
            }
        });
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
        String emotionalTrigger = trigger.getText().toString().trim();
        String socialSituationCapture = socialSituation.getText().toString();
        String noteText = note.getText().toString().trim();     //is trim() necessary here?
        // validation for selecting emotional state
        if (selectedEmotion.equals("Select Emotion") || selectedEmotion.isEmpty()){
            Toast.makeText(this, "Select an Emotional State",Toast.LENGTH_LONG).show();
        }
        // validation for selecting note(Not real-time validation)
        if (!noteText.isEmpty()){
            //if has input, no longer than 20 char or 3 words.
            String[] words = noteText.split("\\s+");
            if (noteText.length() > 20 || words.length > 3 ){
                Toast.makeText(this, "No more than 20 characters or 3 words.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        MoodEvent moodEvent = new MoodEvent(selectedEmotion,emotionalTrigger,socialSituationCapture,currentDate, noteText);
        Toast.makeText(this, "Created Mood Event",Toast.LENGTH_LONG).show();

    }

}