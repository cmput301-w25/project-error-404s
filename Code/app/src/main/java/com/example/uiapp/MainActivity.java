package com.example.uiapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
<<<<<<< HEAD
import com.example.uiapp.model.UserModel;
import com.example.uiapp.ui.profile.LoginActivity;
import com.example.uiapp.utils.SharedPrefHelper;
=======

import com.example.uiapp.ui.profile.SignupActivity;
>>>>>>> parent of 1424aef (Aakarsh_Reorganize)
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private FirebaseFirestore db;

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
        db = FirebaseFirestore.getInstance();
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

        String userID = getSharedPreferences("MoodPulsePrefs", MODE_PRIVATE)
               .getString("USERNAME", null);

        //String userID = db.collection("users").document(user).getId();




        //Toast.makeText(MainActivity.this,String.format(" This document id  is %s" , db.collection("users").document(userID)),Toast.LENGTH_LONG).show();

        //Log.d("add userID debug", String.format("id : " + db.collection("users").document(userID)));
        //Log.d("add userID debug", String.format("id : " + db.collection("users").document(userID).getId()));
        if (userID == null) { // Redirect only if the user is NOT logged in
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Check Firestore for profile existence
        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        // Profile does not exist; clear session and redirect.
                        getSharedPreferences("MoodPulsePrefs", MODE_PRIVATE)
                                .edit()
                                .remove("USERNAME")
                                .apply();
                        Toast.makeText(MainActivity.this, "Profile not found. Logging out.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Profile exists: Continue setting up the UI.
                        setContentView(R.layout.activity_bottom_nav);
                        // Initialize Firestore reference and UI components
                        initializeUI(userID);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error verifying profile.", Toast.LENGTH_SHORT).show();
                });
    }
    
    /**
     * Initializes the UI components and sets up the navigation.
     * 
     * @param userID The current user's ID
     */
    private void initializeUI(String userID) {
        Toast.makeText(this, "Welcome " + userID, Toast.LENGTH_SHORT).show();

        eventRef = FirebaseFirestore.getInstance().collection("users").document(userID).collection("moods");
        context = this;

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_bottom_nav);
        
        // Set up navigation with NavController
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}