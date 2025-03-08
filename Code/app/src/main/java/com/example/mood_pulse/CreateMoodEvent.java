package com.example.mood_pulse;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class CreateMoodEvent extends AppCompatActivity {

    private TextView dateTime, dateTv, timeTv, writeHereET;
    private Spinner emotionalStateSpinner;
    private EditText trigger;
    private EditText socialSituation;
    private TextView note;
    private Button createMoodEventButton;
    Date currentDate;
    private FirebaseFirestore db;
    private CollectionReference eventRef;
    private String selectedEmotion = "";
    private ImageView happyIcon, sadIcon, angerIcon, fearIcon, disgustIcon, confusedIcon, shameIcon, surprisedIcon, tiredIcon,anxiousIcon, proudIcon, boredIcon;

    protected  void onCreate(Bundle savedInstanceState) {

        // TODO: Change the ID of the view and triggers
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_mood);

        //database setup
        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("CreateMoodEvent");


        // get the Id's of elements
        dateTime = findViewById(R.id.dateTv);
        //emotionalStateSpinner = findViewById(R.id.emotional_Trigger);
        //trigger = findViewById(R.id.);
        //socialSituation = findViewById(R.id.social_situation);
        createMoodEventButton = findViewById(R.id.addButton);
        note = findViewById(R.id.noteTV);
        // temporarily add an id to test
        TextView noteError = findViewById(R.id.uploadImageET); //noteError helps display error message when you input more than 20chars or 3words in the note Editext

        happyIcon = findViewById(R.id.happyIcon);
        sadIcon = findViewById(R.id.sadIcon);
        angerIcon = findViewById(R.id.angerIcon);
        fearIcon = findViewById(R.id.fearIcon);
        disgustIcon = findViewById(R.id.disgustIcon);
        confusedIcon = findViewById(R.id.confusedIcon);
        shameIcon = findViewById(R.id.ShapeIcon);
        surprisedIcon = findViewById(R.id.surprisedIcon);
        tiredIcon = findViewById(R.id.tiredIcon);
        anxiousIcon = findViewById(R.id.anxiousIcon);
        proudIcon = findViewById(R.id.proudIcon);
        boredIcon = findViewById(R.id.boredIcon);
        // if users click on icon, then assign the variable
        happyIcon.setOnClickListener(v -> { selectedEmotion = "Happy"; });
        sadIcon.setOnClickListener(v -> { selectedEmotion = "Sad"; });
        angerIcon.setOnClickListener(v -> { selectedEmotion = "Angry"; });
        fearIcon.setOnClickListener(v -> { selectedEmotion = "Fear"; });
        disgustIcon.setOnClickListener(v -> { selectedEmotion = "Disgust"; });
        confusedIcon.setOnClickListener(v -> { selectedEmotion = "Confused"; });
        shameIcon.setOnClickListener(v -> { selectedEmotion = "Shame"; });
        surprisedIcon.setOnClickListener(v -> { selectedEmotion = "Surprised"; });
        tiredIcon.setOnClickListener(v -> { selectedEmotion = "Tired"; });
        anxiousIcon.setOnClickListener(v -> { selectedEmotion = "Anxious"; });
        proudIcon.setOnClickListener(v -> { selectedEmotion = "Proud"; });
        boredIcon.setOnClickListener(v -> { selectedEmotion = "Bored"; });


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
                }//
            }
        });
        // get the current date and time and set it
        //currentDate = new Date();
        //SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm d-MM-yyyy", Locale.getDefault());
        //String formatDate = dateFormat.format(currentDate);
        //dateTime.setText(formatDate);

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        // do format
        SimpleDateFormat formatDate = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        String formattedDate = formatDate.format((currentDate));

        SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formatedTime = time.format(currentDate);

        SimpleDateFormat wordsFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String wordsDate = wordsFormat.format(currentDate).toString();

        String leftDate = wordsDate + ", " + formattedDate;

        // show string  dateTv
        dateTv = findViewById(R.id.dateTv);
        dateTv.setText(leftDate);
        // shows the time on the right
        timeTv = findViewById(R.id.timeTv);
        timeTv.setText(formatedTime);





        //handle submit button  maybe make it so you cant submit if you have not selected an emotion
        createMoodEventButton.setOnClickListener(v -> checkCreate());
    }


    private void checkCreate() {

       // int uid = UUID.randomUUID().toString();
       // String selectedEmotion = emotionalStateSpinner.getSelectedItem().toString();
        String emotionalTrigger = trigger.getText().toString().trim();
        String socialSituationCapture = socialSituation.getText().toString();
        // note is trigger
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
        // add a unique id
        MoodEvent moodEvent = new MoodEvent(1, selectedEmotion,emotionalTrigger, socialSituationCapture,currentDate, noteText);

        Toast.makeText(this, "Created Mood Event",Toast.LENGTH_LONG).show();

    }

}