package com.example.uiapp.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.uiapp.data.MoodDao;
import com.example.uiapp.data.MoodDatabase;
import com.example.uiapp.model.MoodEntry;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MoodViewModel extends AndroidViewModel {
    private final MoodDao moodDao;
    private final LiveData<List<MoodEntry>> moodList;
    private final FirebaseFirestore db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String userId;

    public MoodViewModel(Application application) {
        super(application);
        MoodDatabase database = MoodDatabase.getInstance(application);
        moodDao = database.moodDao();
        moodList = moodDao.getAllMoods(); // Directly observe the database
        db = FirebaseFirestore.getInstance();

        // Retrieve the userId from SharedPreferences
        String user = application.getSharedPreferences("MoodPulsePrefs", MODE_PRIVATE)
                .getString("USERNAME", null);

        //Log.d("add userID debug", String.format("id : " + db.collection("users").document(user)));
        Log.d("add userID debug", String.format("id : " + db.collection("users").document(user).getId()));

        String userId = db.collection("users").document(user).getId();

        if (userId != null) {
            Log.d("MoodViewModel", "Logged in as: " + userId);
        } else {
            Log.e("MoodViewModel", "User is not logged in or userId not found!");
        }
    }

    public LiveData<List<MoodEntry>> getMoodEntries() {
        return moodList;
    }

    public void addMoodEntry(MoodEntry entry, Runnable onComplete) {
        // Save locally first
        executor.execute(() -> {
            moodDao.insert(entry);  // Save locally in the SQLite database
            // Now save to Firestore
            saveMoodToFirestore(entry);
        });
    }

    public void deleteMoodEntry(MoodEntry entry) {
        executor.execute(() -> moodDao.delete(entry));
    }

    private void saveMoodToFirestore(MoodEntry entry) {
        if (userId == null) {
            Log.e("MoodViewModel", "Error: User ID is null, cannot save mood.");
            return;
        }

        // Get Firestore reference to the user's "moods" subcollection
        db.collection("users").document(userId)
                .collection("moods").add(entry) // Use add() to generate a unique document ID
                .addOnSuccessListener(documentReference -> {
                    // Optionally, if you want to set the Firestore ID in the entry object:
                    entry.setFirestoreId(documentReference.getId());
                    Log.d("MoodViewModel", "Mood successfully added to Firestore!");
                })
                .addOnFailureListener(e -> {
                    Log.e("MoodViewModel", "Error adding mood to Firestore", e);
                });
    }
}
