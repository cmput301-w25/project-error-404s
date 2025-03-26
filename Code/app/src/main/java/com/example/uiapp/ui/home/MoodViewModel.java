package com.example.uiapp.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.uiapp.data.MoodDao;
import com.example.uiapp.data.MoodDatabase;
import com.example.uiapp.model.MoodEntry;
import com.google.firebase.auth.FirebaseAuth;
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
    private String moodEventsId;

    public MoodViewModel(Application application) {
        super(application);
        MoodDatabase database = MoodDatabase.getInstance(application);
        moodDao = database.moodDao();
        moodList = moodDao.getAllMoods(); // Directly observe the database
        db = FirebaseFirestore.getInstance();



        String userId = application.getSharedPreferences("MoodPulsePrefs", MODE_PRIVATE)
                .getString("USERNAME", null);

        moodEventsId = db.collection("users").document(userId).getId();
        //moodEventsRef = db.collection("users").document(moodEventsId).collection("moods");
        Log.d("mood event ref", String.format("mood data",userId));


        if (userId != null) {
            // Use the userId here
            Log.d("AddModelFragment", "Logged in as: " + userId);
            // You can now query Firestore using this userId
        } else {
            Log.e("AddModelFragment", "User is not logged in or userId not found!");
            // Handle the case where userId is null
        }


    }

    public LiveData<List<MoodEntry>> getMoodEntries() {
        return moodList;
    }

    public void addMoodEntry(MoodEntry entry, Runnable onComplete) {
        executor.execute(() -> {
            moodDao.insert(entry);  // Save locally
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

//        // Generate Firestore ID if missing
//        if (entry.getFirestoreId() == null || entry.getFirestoreId().isEmpty()) {
//            String generatedId = db.collection("users").document(userId)
//                    .collection("moods").document().getId();
//            entry.setFirestoreId(generatedId);
//        }

        // Add mood entry to Firestore
        db.collection("users").document(userId)
                .collection("moods").document(userId)
                .set(entry)
                .addOnSuccessListener(aVoid ->
                        Log.d("MoodViewModel", "Mood successfully added to Firestore!"))
                .addOnFailureListener(e ->
                        Log.e("MoodViewModel", "Error adding mood to Firestore", e));
        Log.d("add userID debug", String.format("id" + db.collection("users").document(userId)));
    }

}
