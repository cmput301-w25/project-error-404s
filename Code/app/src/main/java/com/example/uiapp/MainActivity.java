package com.example.uiapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.example.uiapp.model.UserModel;
import com.example.uiapp.ui.profile.LoginActivity;
import com.example.uiapp.utils.SharedPrefHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

    public static int moodDistance = 5;


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
        setContentView(R.layout.activity_bottom_nav);

        if (SharedPrefHelper.getUser(this) == null) { // Redirect only if the user is NOT logged in
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }else{
            HelperClass.users = SharedPrefHelper.getUser(this);
        }

        if (HelperClass.users != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users")
                    .document(HelperClass.users.getUsername())
                    .addSnapshotListener((documentSnapshot, error) -> {
                        if (error != null) {
                            Log.e("FirestoreError", "Error listening for user updates", error);
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            UserModel user = documentSnapshot.toObject(UserModel.class);
                            if (user != null) {
                                HelperClass.users = user;
                                SharedPrefHelper.saveUser(this, HelperClass.users);
                                Log.d("UserUpdate", "User data updated in HelperClass and SharedPreferences");
                            }
                        }
                    });
        }


        BottomNavigationView bottomNav = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_bottom_nav);
        // Set up navigation with NavController
        NavigationUI.setupWithNavController(bottomNav, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (navDestination.getId() == R.id.modeDetailsFragment){
                    bottomNav.setVisibility(View.GONE);
                }else {
                    bottomNav.setVisibility(View.VISIBLE);

                }
            }
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.homeModeFragment) {
                navController.popBackStack(R.id.homeModeFragment, true);
                navController.navigate(R.id.homeModeFragment);
                return true;
            } else if (itemId == R.id.navigation_home) {
                navController.popBackStack(R.id.navigation_home, true);
                navController.navigate(R.id.navigation_home);
                return true;
            } else if (itemId == R.id.navigation_fragment_add) {
                navController.popBackStack(R.id.navigation_fragment_add, true);
                navController.navigate(R.id.navigation_fragment_add);
                return true;
            } else if (itemId == R.id.navigation_mood_followings) {
                navController.popBackStack(R.id.navigation_mood_followings, true);
                navController.navigate(R.id.navigation_mood_followings);
                return true;
            } else if (itemId == R.id.mapFragment) {
                navController.popBackStack(R.id.mapFragment, true);
                navController.navigate(R.id.mapFragment);
                return true;
            }

            return false;
        });
    }

    public static Bitmap createInitialsBitmap(String name, int size) {
        // Extract initials
        String initials = getInitials(name);

        // Create bitmap and canvas
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Background paint
        Paint paintBg = new Paint();
        paintBg.setColor(Color.parseColor("#9891F8"));
        paintBg.setStyle(Paint.Style.FILL);
        canvas.drawCircle(size / 2, size / 2, size / 2, paintBg);

        // Text paint
        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(size / 3);
        paintText.setTypeface(Typeface.DEFAULT_BOLD);
        paintText.setAntiAlias(true);

        // Measure text size
        Rect textBounds = new Rect();
        paintText.getTextBounds(initials, 0, initials.length(), textBounds);

        // Center the text
        float x = (size - textBounds.width()) / 2f;
        float y = (size + textBounds.height()) / 2f;
        canvas.drawText(initials, x, y, paintText);

        return bitmap;
    }

    public static String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String[] parts = name.trim().split(" ");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        } else {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        }
    }
}