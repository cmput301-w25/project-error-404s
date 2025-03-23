package com.example.uiapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.uiapp.ui.profile.SignupActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This activity serves as the main screen of the app, displaying a list of mood events.
 * -----------------------------------------------------------------------------
 * Handled in this class:
 * - Fetching and displaying mood events from Firestore
 * - Adding, updating, and deleting mood events
 * - Navigating between different app sections using Bottom Navigation
 * - Handling results from the AddMood activity
 */
public class MainActivity extends AppCompatActivity {

    public CollectionReference eventRef;    // Firestore reference for mood events

    public Context context;    // Context for UI interactions

    private static final int ADD_MOOD_REQUEST = 1;

    /**
     * Initializes the main activity, sets up UI elements, and fetches mood events.
     * -----------------------------------------------------------------------------
     * Handled in this method:
     * - Sets up the UI layout and navigation bar
     * - Fetches mood events from Firestore
     * - Handles user interactions for adding new moods
     *
     * @param savedInstanceState The saved instance state from the previous session, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Toast.makeText(this, "Toastig", Toast.LENGTH_SHORT).show();


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
//            Uri imageUri = data.getData();
//
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                binding.expandablePhoto.imgSelected.setImageBitmap(bitmap);  // Set image to ImageView inside CardView
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

        String username = getSharedPreferences("MoodPulsePrefs", MODE_PRIVATE)
               .getString("USERNAME", null);
//        Toast.makeText(this, "after username", Toast.LENGTH_SHORT).show();


//        if (true) {
//            Toast.makeText(this, "MainToast", Toast.LENGTH_SHORT).show();
//            // Not logged in, redirect to signup/login screen
//            Intent intent = new Intent(this, SignupActivity.class);
//            startActivity(intent);
//            finish(); // Prevent user from going back here
//            return;
//        }


        setContentView(R.layout.activity_signup);
        Toast.makeText(this, "Welcome " + username, Toast.LENGTH_SHORT).show();

        eventRef = FirebaseFirestore.getInstance().collection("MoodEvents");
        context = this;


        // Fetch and update events from Firestore


//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id, new SignupFragment())
//                    .commit();
//        }
//        SignupFragment signupFragment = new SignupFragment();
//        signupFragment.show(getSupportFragmentManager(), "SignupFragment");


        // Initialize the event list and adapter


        // Inside MainActivity.java's onCreate():

        //
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

// Set up navigation with NavController


// Override default behavior for "navigation_dashboard" to launch AddMood activity
    }}