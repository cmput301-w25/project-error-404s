package com.example.mood_pulse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public CollectionReference eventRef;

    public Context context;
    private eventArrayAdapter adapter;
    private eventArrayList eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav_host_fragment_activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    // TODO: uncomment and adjust the code when database is implemented and functioning
    public void updateEvent(MoodEvent moodEvent, Integer newID, String emotionalState, String trigger, String socialSituation, Date date, String note) {
        if (emotionalState == null || emotionalState.trim().isEmpty()) {
            // Prevent saving if emotional state is removed
            showErrorMessage("Emotional state cannot be empty.");
            return;
        }


        // TODO: change getTitle to correpsonding fucntion
        if (!moodEvent.getMoodID().equals(newID)) {
            DocumentReference oldDocRef = eventRef.document(moodEvent.getMoodID().toString());
            MoodEvent updatedEvent = new MoodEvent(newID, emotionalState, trigger, socialSituation, date, note);
            DocumentReference newDocRef = eventRef.document(newID.toString());
            newDocRef.set(updatedEvent);
        } else {
            DocumentReference docRef = eventRef.document(moodEvent.getMoodID().toString());
            docRef.update("emotionalState", emotionalState, "trigger", trigger, "socialSituation", socialSituation);
        }

        eventList = new eventArrayList();
        adapter = new eventArrayAdapter(this, eventList.getEvents());

        moodEvent.setEmotionalState(emotionalState);
        moodEvent.setTrigger(trigger);
        moodEvent.setSocialSituation(socialSituation);
        moodEvent.setDate(date);
        moodEvent.setNote(note);
        adapter.notifyDataSetChanged();

    }

    // Call this method when the user attempts to exit without saving
    public void confirmExitWithoutSaving(Runnable onConfirm) {
        new AlertDialog.Builder(context)
                .setTitle("Unsaved Changes")
                .setMessage("You have unsaved changes. Are you sure you want to exit?")
                .setPositiveButton("Exit", (dialog, which) -> onConfirm.run())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Display error message when emotional state is removed
    private void showErrorMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void deleteEvent(MoodEvent moodEvent) {
        // Show confirmation prompt before deleting
        new AlertDialog.Builder(context)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Get a reference to the document in Firestore
                    DocumentReference docRef = eventRef.document(moodEvent.getMoodID());

                    // Delete from Firestore
                    docRef.delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "Event deleted successfully");

                                // Remove from local list after successful deletion
                                eventArrayList.remove(moodEvent);
                                adapter.notifyDataSetChanged();

                                // Navigate back to the mood history list
                                Toast.makeText(context, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, MoodHistory.class);
                                context.startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", "Error deleting event", e);
                                Toast.makeText(context, "Failed to delete event. Please try again.", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

}