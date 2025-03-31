package com.example.uiapp.ui.home;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Collections;
import java.util.Comparator;

public class MoodViewModel extends ViewModel {
    private final MutableLiveData<List<MoodEntry>> moodEntries = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<MoodEntry>> followingMoodEntries = new MutableLiveData<>(new ArrayList<>());

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference moodEventsRef = db.collection("MoodEvents");

    public LiveData<List<MoodEntry>> getMoodEntries() {
        return moodEntries;
    }

    public LiveData<List<MoodEntry>> getFollowingMoodEntries() {
        return followingMoodEntries;
    }

    /**
     * This function calls to the EventRef to fetch mood events from the database
     */
    public void fetchMoodEvents() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        moodEventsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                QuerySnapshot snapshot = task.getResult();

                List<MoodEntry> allMoods = new ArrayList<>();
                for (QueryDocumentSnapshot document : snapshot) {
                    MoodEntry mood = document.toObject(MoodEntry.class);
                    if (mood.getDateTime().contains(currentDateTime)) {
                        allMoods.add(mood);
                    }
                }

                List<MoodEntry> myMoodList = new ArrayList<>();
                List<MoodEntry> followingMoodList = new ArrayList<>();

                if (HelperClass.users != null) {
                    String loggedInUsername = HelperClass.users.getUsername();
                    List<String> followersList = HelperClass.users.getFollowers();

                    for (MoodEntry mood : allMoods) {
                        if (mood.getUsername().equals(loggedInUsername)) {
                            myMoodList.add(mood);
                        } else if (followersList.contains(mood.getUsername()) && mood.getStatus().equals("Public")) {
                            followingMoodList.add(mood);
                        }
                    }
                }

                // Sorting by DateTime (Reverse Chronological Order)
                Comparator<MoodEntry> dateComparator = (m1, m2) -> {
                    try {
                        // Fixing format by removing "|"
                        String dateTime1 = m1.getDateTime().replace(" | ", " ");
                        String dateTime2 = m2.getDateTime().replace(" | ", " ");

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                        Date date1 = dateFormat.parse(dateTime1);
                        Date date2 = dateFormat.parse(dateTime2);

                        return date2.compareTo(date1); // Reverse order (most recent first)
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                };

                Collections.sort(myMoodList, dateComparator);
                Collections.sort(followingMoodList, dateComparator);

                // Update LiveData
                moodEntries.postValue(myMoodList);
                followingMoodEntries.postValue(followingMoodList);
            }
        });
    }

}
