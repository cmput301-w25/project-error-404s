package com.example.uiapp.ui.profile;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.uiapp.model.MoodEntry;
import com.example.uiapp.utils.HelperClass;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<List<MoodEntry>> moodEntries = new MutableLiveData<>(new ArrayList<>());

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference moodEventsRef = db.collection("MoodEvents");

    public LiveData<List<MoodEntry>> getMoodEntries() {
        return moodEntries;
    }


    /**
     * This function fetches data regarding the existing mood event
     * The mood event is connected and limited to the specific to the users of choice
     * ---------------------------------------------------------------------------------
     * @param from
     * @param userName - username is required as a paramter to fetch relavant mood events
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fetchMoodEvents(String from, String userName) {
        moodEventsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                QuerySnapshot snapshot = task.getResult();

                List<MoodEntry> allMoods = new ArrayList<>();
                List<MoodEntry> otherUserMoods = new ArrayList<>();
                for (QueryDocumentSnapshot document : snapshot) {
                    MoodEntry mood = document.toObject(MoodEntry.class);
                    if (mood.getUsername().contains(userName)) {
                        allMoods.add(mood);
                        if (mood.getStatus().equals("Public")){
                            otherUserMoods.add(mood);
                        }
                    }
                }

                if (from.equals("otherUserProfile")) {
                    // Sort moods by dateTime in descending order
                    otherUserMoods.sort((m1, m2) -> {
                        LocalDateTime dateTime1 = parseDateTime(m1.getDateTime());
                        LocalDateTime dateTime2 = parseDateTime(m2.getDateTime());
                        return dateTime2.compareTo(dateTime1); // Descending order
                    });

                    // Get the most recent 3 entries
                    List<MoodEntry> recentMoods = otherUserMoods.stream()
                            .limit(3)
                            .collect(Collectors.toList());

                    moodEntries.postValue(recentMoods);
                } else {
                    moodEntries.postValue(allMoods);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDateTime parseDateTime(String dateTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy | HH:mm", Locale.ENGLISH);
        return LocalDateTime.parse(dateTimeStr, formatter);
    }

}
