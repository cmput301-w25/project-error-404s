package com.example.mood_pulse;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AddMood extends AppCompatActivity {

    private EditText writeHereET, uploadImageET,locationET;
    private ImageView downArrow1, upArrow1, downArrow2, upArrow2, placeHolderIV, upArrow3, downArrow3,upArrow4, downArrow4;
    private TextView uploadPhotoTV;
    private Button aloneBtn, with1personBTN, with2personBTN, crowdBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_mood); // Ensure this is the correct XML file name

        if (getIntent().getBooleanExtra("FROM_DASHBOARD", false)) {
            finish(); // Automatically close AddMood when back is pressed
        }

        // Find the left arrow button
        ImageView leftArrow = findViewById(R.id.leftArrow);

        // Set onClickListener to go back to the previous screen
        leftArrow.setOnClickListener(v -> finish());


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

            // Reset all buttons to default state
            resetButtonStyles();

            // Set selected button background and text color
            clickedButton.setBackgroundResource(R.drawable.button_bg);
            clickedButton.getBackground().setColorFilter(Color.parseColor("#9891F8"), PorterDuff.Mode.SRC_ATOP);
            clickedButton.setTextColor(Color.WHITE);

            // Apply white tint to drawable
            for (Button button : new Button[]{aloneBtn, with1personBTN, with2personBTN, crowdBTN}) {
                if (button == clickedButton) {
                    button.getCompoundDrawables()[0].setTint(Color.WHITE); // Tint drawable to white
                } else {
                    button.getCompoundDrawables()[0].setTint(Color.BLACK); // Reset others to black
                }
            }
        };

        // Set click listeners for all buttons
        aloneBtn.setOnClickListener(buttonClickListener);
        with1personBTN.setOnClickListener(buttonClickListener);
        with2personBTN.setOnClickListener(buttonClickListener);
        crowdBTN.setOnClickListener(buttonClickListener);
    }

    // Reset all button styles to default
    private void resetButtonStyles() {
        for (Button button : new Button[]{aloneBtn, with1personBTN, with2personBTN, crowdBTN}) {
            button.setBackgroundResource(R.drawable.button_bg);
            button.setTextColor(Color.BLACK);
            button.getBackground().clearColorFilter();
            button.getCompoundDrawables()[0].setTint(Color.BLACK); // Reset drawable tint to black
        }
    }
}
