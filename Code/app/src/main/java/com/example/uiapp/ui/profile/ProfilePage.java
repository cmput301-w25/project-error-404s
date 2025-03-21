package com.example.uiapp.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;


import com.example.uiapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Displays the user's profile information, including username and stats.
 */
public class ProfilePage extends AppCompatActivity {

    private TextView usernameTextView, postsNumberTextView, followersNumberTextView, followedNumberTextView;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;


    // TODO: Once XML file for the profile page is set, match the following ids
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();

        // Link UI components
        usernameTextView = findViewById(R.id.textView); // Username
        postsNumberTextView = findViewById(R.id.tv_posts_number);
        followersNumberTextView = findViewById(R.id.tv_followers_number);
        followedNumberTextView = findViewById(R.id.tv_followed_number);

        // Fetch user data
        loadUserProfile();

        BottomNavigationView bottomNav = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        NavigationUI.setupWithNavController(bottomNav, navController);

    }

    /**
     * Loads the user's profile data from Firestore.
     */
    private void loadUserProfile() {
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference userRef = db.collection("Users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Retrieve fields from Firestore
                String username = documentSnapshot.getString("username");
                Long posts = documentSnapshot.getLong("posts");
                Long followers = documentSnapshot.getLong("followers");
                Long followed = documentSnapshot.getLong("followed");

                // Set values in UI
                usernameTextView.setText(username != null ? username : "Unknown");
                postsNumberTextView.setText(posts != null ? String.valueOf(posts) : "0");
                followersNumberTextView.setText(followers != null ? String.valueOf(followers) : "0");
                followedNumberTextView.setText(followed != null ? String.valueOf(followed) : "0");

            } else {
                Log.e("Profile", "User document not found in Firestore");
                Toast.makeText(this, "Profile data not available", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Error fetching profile data", e);
            Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
        });
    }
}
