package com.example.mood_pulse;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mood_pulse.ui.TestClass;

import java.util.UUID;


/**
 * This activity allows users to add a mood event by entering details such as
 * a description, image, people they were with, and location.
 * The user can expand/collapse different sections using up/down arrows.
 * -----------------------------------------------------------------------------
 * Handled in this class:
 * - Writing a mood description
 * - Uploading an image
 * - Selecting anyone the user was with
 * - Add a location
 * - Can navigate back to the previous screen
 *
 */
public class AddMood extends AppCompatActivity {

    /** EditText used for writing mood description */
    private EditText writeHereET;

    /** EditText used for uploading an image */
    private EditText uploadImageET;

    /** EditText used for entering the location */
    private EditText locationET;

    /** ImageView used for up/down arrows */
    private ImageView downArrow1;

    /** ImageView used for up/down arrows */
    private ImageView upArrow1;

    /** ImageView used for up/down arrows */
    private ImageView downArrow2;

    /** ImageView used for up/down arrows */
    private ImageView upArrow2;

    /** ImageView used for place holder */
    private ImageView placeHolderIV;

    /** ImageView used for up/down arrows */
    private ImageView upArrow3;

    /** ImageView used for up/down arrows */
    private ImageView downArrow3;

    /** ImageView used for up/down arrows */
    private ImageView upArrow4;

    /** ImageView used for up/down arrows */

    private ImageView downArrow4;

    /** TextView used for uploading a photo */
    private TextView uploadPhotoTV;

    /** Button used for selecting people */
    private Button aloneBtn;

    /** Button used for selecting people */
    private Button with1personBTN;

    /** Button used for selecting people */
    private Button with2personBTN;

    /** Button used for selecting people */
    private Button crowdBTN;
    /** Button used for submitting mood */
    private Button addMoodBTN;
    private String userEmotion = "";
    private String socialSituation = "";


    /**
     * This method is called when the activity is created.
     * It instantiates the UI components, and sets up the click listeners.
     * As well, it handles the expandable/collapsible behavior of different sections.
     *
     * @param savedInstanceState The saved instance state from previous session.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_mood);

        if (getIntent().getBooleanExtra("FROM_DASHBOARD", false)) {
            finish(); // Automatically close AddMood when back is pressed
        }

        // Find the left arrow button
        ImageView leftArrow = findViewById(R.id.leftArrow);

        // Set onClickListener to go back to the previous screen
        leftArrow.setOnClickListener(v -> finish());

        writeHereET = findViewById(R.id.writeHereET);

        // Setup time
        TestClass test = new TestClass(this);
        test.liveTime();


        // Get emotion
        test.emotionClickListners();
        userEmotion = test.getEmotion();


        // Collect user input for trigger
        test.textWatcherNote(writeHereET, this);
        String userNote = test.getInput();

        // Collect social situation
        test.socialSituationListeners(this);
        socialSituation = test.getSocialSituation();


        addMoodBTN = findViewById(R.id.addButton);

        addMoodBTN.setOnClickListener(v -> {
            MoodEvent moodEvent = new MoodEvent(
                    UUID.randomUUID().hashCode(),
                    userEmotion, // Emotion
                    writeHereET.getText().toString().trim(), // Trigger (optional)
                    socialSituation, // Social situation
                    new Date(),
                    writeHereET.getText().toString().trim() // Use writeHereET for note
            );

            // Pass the mood event back to MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("MoodEvent", moodEvent);
            setResult(RESULT_OK, resultIntent);
            finish();
        });



        // test
        // Initialize UI elements
        writeHereET = findViewById(R.id.writeHereET);
        uploadImageET = findViewById(R.id.uploadImageET); // FIXED: Initialized the missing view
        uploadPhotoTV = findViewById(R.id.uploadPhotoTV);
        placeHolderIV = findViewById(R.id.placeHolderIV);


        downArrow1 = findViewById(R.id.downArrow1);
        downArrow2 = findViewById(R.id.downArrow2);
        upArrow1 = findViewById(R.id.upArrow1);
        upArrow2 = findViewById(R.id.upArrow2);

        // Set initial visibility
        writeHereET.setVisibility(View.GONE);
        uploadImageET.setVisibility(View.GONE);
        upArrow1.setVisibility(View.GONE);
        upArrow2.setVisibility(View.GONE);

        //people
        upArrow3 = findViewById(R.id.upArrow3);
        downArrow3 = findViewById(R.id.downArrow3);
        aloneBtn = findViewById(R.id.aloneBtn);
        with2personBTN = findViewById(R.id.with2personBTN);
        with1personBTN = findViewById(R.id.with1personBTN);
        crowdBTN = findViewById(R.id.crowdBTN);

        //location
        upArrow4 = findViewById(R.id.upArrow4);
        downArrow4 = findViewById(R.id.downArrow4);
        locationET = findViewById(R.id.locationET);

        // Toggle visibility for writeHereET
        downArrow1.setOnClickListener(v -> {
            writeHereET.setVisibility(View.VISIBLE);
            upArrow1.setVisibility(View.VISIBLE);
            downArrow1.setVisibility(View.GONE);
        });

        upArrow1.setOnClickListener(v -> {
            writeHereET.setVisibility(View.GONE);
            upArrow1.setVisibility(View.GONE);
            downArrow1.setVisibility(View.VISIBLE);
        });

        // Toggle visibility for uploadImageET
        downArrow2.setOnClickListener(v -> {
            uploadImageET.setVisibility(View.VISIBLE);
            upArrow2.setVisibility(View.VISIBLE);
            downArrow2.setVisibility(View.GONE);
            uploadPhotoTV.setVisibility(View.VISIBLE);
            placeHolderIV.setVisibility(View.VISIBLE);
        });

        upArrow2.setOnClickListener(v -> { // FIXED: Used upArrow2 instead of upArrow1
            uploadImageET.setVisibility(View.GONE);
            upArrow2.setVisibility(View.GONE);
            uploadPhotoTV.setVisibility(View.GONE);
            placeHolderIV.setVisibility(View.GONE);
            downArrow2.setVisibility(View.VISIBLE);
        });
//people
        downArrow3.setOnClickListener(v -> {
            upArrow3.setVisibility(View.VISIBLE);
            aloneBtn.setVisibility(View.VISIBLE);
            with1personBTN.setVisibility(View.VISIBLE);
            with2personBTN.setVisibility(View.VISIBLE);
            crowdBTN.setVisibility(View.VISIBLE);
            downArrow3.setVisibility(View.GONE);
        });

        upArrow3.setOnClickListener(v -> {
            upArrow3.setVisibility(View.GONE);
            aloneBtn.setVisibility(View.GONE);
            with1personBTN.setVisibility(View.GONE);
            with2personBTN.setVisibility(View.GONE);
            crowdBTN.setVisibility(View.GONE);
            downArrow3.setVisibility(View.VISIBLE);
        });

        //location
        downArrow4.setOnClickListener(v -> {
            locationET.setVisibility(View.VISIBLE);
            upArrow4.setVisibility(View.VISIBLE);
            downArrow4.setVisibility(View.GONE);
        });

        upArrow4.setOnClickListener(v -> {
            locationET.setVisibility(View.GONE);
            upArrow4.setVisibility(View.GONE);
            downArrow4.setVisibility(View.VISIBLE);
        });

        // Click Listener for changing button colors and drawable tint
        View.OnClickListener buttonClickListener = v -> {
            Button clickedButton = (Button) v;

            resetButtonStyles();

            // Set selected button background and text color
            clickedButton.setBackgroundResource(R.drawable.button_bg);
            clickedButton.getBackground().setColorFilter(Color.parseColor("#9891F8"), PorterDuff.Mode.SRC_ATOP);
            clickedButton.setTextColor(Color.WHITE);

            // Apply white tint to drawable
            for (Button button : new Button[]{aloneBtn, with1personBTN, with2personBTN, crowdBTN}) {
                if (button == clickedButton) {
                    button.getCompoundDrawables()[0].setTint(Color.WHITE);
                } else {
                    button.getCompoundDrawables()[0].setTint(Color.BLACK);
                }
            }
        };

        // Set click listeners for all buttons
        aloneBtn.setOnClickListener(buttonClickListener);
        with1personBTN.setOnClickListener(buttonClickListener);
        with2personBTN.setOnClickListener(buttonClickListener);
        crowdBTN.setOnClickListener(buttonClickListener);
    }


    /**
     * Resets the style of all buttons to the default state.
     */
    private void resetButtonStyles() {
        for (Button button : new Button[]{aloneBtn, with1personBTN, with2personBTN, crowdBTN}) {
            button.setBackgroundResource(R.drawable.button_bg);
            button.setTextColor(Color.BLACK);
            button.getBackground().clearColorFilter();
            button.getCompoundDrawables()[0].setTint(Color.BLACK); // Reset drawable tint to black
        }
    }
}
