package com.example.mood_pulse;

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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public CollectionReference eventRef;

    public Context context;
    private eventArrayAdapter adapter;
    private eventArrayList eventList;

    private static final int ADD_MOOD_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        eventRef = FirebaseFirestore.getInstance().collection("MoodEvents");
        context = this;

        updateEventsFromFirebase(); // Fetch and update events from Firestore

        // Initialize the event list and adapter
        ListView moodListView = findViewById(R.id.moodListView);
        eventList = new eventArrayList();
        adapter = new eventArrayAdapter(this, eventList.getEvents());
        moodListView.setAdapter(adapter);

        moodListView.setOnItemLongClickListener((parent, view, position, id) -> {
            MoodEvent event = eventList.getEvents().get(position);
            deleteEvent(event);
            return true;
        });

        // Inside MainActivity.java's onCreate():

        BottomNavigationView bottomNav = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

// Set up navigation with NavController
        NavigationUI.setupWithNavController(bottomNav, navController);

// Override default behavior for "navigation_dashboard" to launch AddMood activity
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_dashboard) {
                Intent intent = new Intent(MainActivity.this, AddMood.class);
                startActivityForResult(intent, ADD_MOOD_REQUEST);
                return true; // Consume the click event
            } else {
                // Let the default NavController handle other items
                NavigationUI.onNavDestinationSelected(item, navController);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_MOOD_REQUEST && resultCode == RESULT_OK) {
            MoodEvent moodEvent = (MoodEvent) data.getSerializableExtra("MoodEvent");

            // Write the event to Firestore
            eventRef.add(moodEvent)
                    .addOnSuccessListener(documentReference -> {
                        // Get the Firebase-generated document ID
                        String documentId = documentReference.getId();
                        Log.d("Firestore", "Event added with ID: " + documentId);

                        // Set the Firestore document ID in the MoodEvent object
                        moodEvent.setFirestoreId(documentId);

                        // Optionally, update the event in your local list and notify the adapter
                        eventList.addEvent(moodEvent);
                        adapter.notifyDataSetChanged();

                        Toast.makeText(this, "Mood event added!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error adding event", e);
                        Toast.makeText(this, "Failed to add event to Firebase", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateEventsFromFirebase() {
        if (eventRef == null) {
            Log.e("Firestore", "eventRef is null. Firestore not initialized properly.");
            return;
        }

        eventRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Log.e("Firestore", "Error fetching data: " + error.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(this, "Failed to load events: " + error.getMessage(), Toast.LENGTH_SHORT).show()
                );
                return;
            }

            if (snapshot != null && !snapshot.isEmpty()) {
                List<MoodEvent> updatedEvents = new ArrayList<>();
                for (QueryDocumentSnapshot document : snapshot) {
                    try {
                        MoodEvent event = document.toObject(MoodEvent.class);
                        event.setFirestoreId(document.getId());
                        updatedEvents.add(event);
                    } catch (Exception e) {
                        Log.e("Firestore", "Error parsing MoodEvent: " + e.getMessage());
                    }
                }

                runOnUiThread(() -> {
                    eventList.updateEvents(updatedEvents);
                    adapter.updateData(updatedEvents);
                });
            }
        });
    }


    // TODO: uncomment and adjust the code when database is implemented and functioning
    public void updateEvent(MoodEvent moodEvent, String emotionalState, String trigger, String socialSituation, Date date, String note) {
        String documentId = moodEvent.getFirestoreId(); // Get Firestore document ID
        if (documentId == null) {
            Log.e("Firestore", "No Firestore ID stored for this event.");
            Toast.makeText(context, "Cannot update event: missing ID", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference docRef = eventRef.document(documentId);
        docRef.update("emotionalState", emotionalState, "trigger", trigger, "socialSituation", socialSituation, "date", date, "note", note)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Event updated successfully");
                    moodEvent.setEmotionalState(emotionalState);
                    moodEvent.setTrigger(trigger);
                    moodEvent.setSocialSituation(socialSituation);
                    moodEvent.setDate(date);
                    moodEvent.setNote(note);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(context, "Event updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error updating event", e);
                    Toast.makeText(context, "Failed to update event. Please try again.", Toast.LENGTH_SHORT).show();
                });
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

    public void addMoodEvent(MoodEvent moodEvent) {
        eventRef.add(moodEvent)
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();
                    Log.d("Firestore", "Event added with ID: " + documentId);
                    // Save Firestore document ID in the MoodEvent object
                    moodEvent.setFirestoreId(documentId);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error adding event", e);
                    Toast.makeText(this, "Failed to add event to Firebase", Toast.LENGTH_SHORT).show();
                });

    }

    public void deleteEvent(MoodEvent moodEvent) {
        String documentId = moodEvent.getFirestoreId(); // Get the Firestore ID
        if (documentId == null) {
            Log.e("Firestore", "No Firestore ID stored for this event.");
            Toast.makeText(context, "Cannot delete event: missing ID", Toast.LENGTH_SHORT).show();
            return;
        }
        DocumentReference docRef = eventRef.document(documentId); // Reference the Firestore document by ID
        docRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Event deleted successfully");
                    eventList.getEvents().remove(moodEvent); // Remove from local list
                    adapter.notifyDataSetChanged(); // Notify the adapter to update the ListView
                    Toast.makeText(context, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error deleting event", e);
                    Toast.makeText(context, "Failed to delete event. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }


}