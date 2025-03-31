package com.example.uiapp.ui.map;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.uiapp.model.MoodEntry;
import com.example.uiapp.utils.HelperClass;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ViewModel for managing mood history data and filtering operations.
 */
public class MapViewModel extends ViewModel {

    private final MutableLiveData<List<MoodEntry>> moodEntries = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<MoodEntry>> filteredMoodEntries = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<String> moodFilter = new MutableLiveData<>("");
    private final MutableLiveData<String> dateFilter = new MutableLiveData<>("");
    private final MutableLiveData<String> displayMineOrFollowingFilter = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> filtersApplied = new MutableLiveData<>(false);

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference moodEventsRef = db.collection("MoodEvents");

    private String currentUsername = "";

    public LiveData<List<MoodEntry>> getFilteredMoodEntries() {
        return filteredMoodEntries;
    }

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
        filterMoodEntries(); // Reapply filter when username changes
    }

    /**
     * Fetches mood entries from the database.
     */
    public void fetchMoodEntries() {
        moodEventsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                QuerySnapshot snapshot = task.getResult();
                List<MoodEntry> allMoods = new ArrayList<>();
                for (QueryDocumentSnapshot document : snapshot) {
                    MoodEntry mood = document.toObject(MoodEntry.class);
                    if (!mood.getLocation().isEmpty()) {
                        allMoods.add(mood);
                    }
                }
                moodEntries.setValue(allMoods);
                if (allMoods.isEmpty()){
                    filteredMoodEntries.setValue(allMoods);
                }else{
                    filterMoodEntries(); // Apply filters
                }
            }else {
                Log.e("Firestore", "Error fetching data"+ task.getException(), task.getException());
            }
        });
    }

    /**
     * Applies filters based on mood, date, and displayMineOrFollowingFilter.
     */
    private void filterMoodEntries() {
        List<MoodEntry> originalList = moodEntries.getValue();
        if (originalList == null) return;

        List<MoodEntry> filteredList = new ArrayList<>();

        String mood = moodFilter.getValue() != null ? moodFilter.getValue() : "";
        String date = dateFilter.getValue() != null ? dateFilter.getValue() : "";
        String displayFilter = displayMineOrFollowingFilter.getValue() != null ? displayMineOrFollowingFilter.getValue() : "";

        List<String> followingList = HelperClass.users.getFollowing(); // Get the following list

        for (MoodEntry entry : originalList) {
            if (entry == null) continue;

            boolean moodMatches = mood.isEmpty() || entry.getMood().equalsIgnoreCase(mood);
            boolean dateMatches = isDateInRange(entry.getDateTime(), date);
            boolean displayMatches = true;

            if (displayFilter.equals("mine")) {
                displayMatches = entry.getUsername().equals(HelperClass.users.getUsername());
            } else if (displayFilter.equals("following")) {
                displayMatches = !entry.getUsername().equals(HelperClass.users.getUsername()) && followingList.contains(entry.getUsername())
                        && entry.getStatus().equalsIgnoreCase("public");
            } else if (displayFilter.isEmpty()) {  // Handling empty displayFilter case
                displayMatches = entry.getUsername().equals(HelperClass.users.getUsername())
                        || (followingList.contains(entry.getUsername())
                        && entry.getStatus().equalsIgnoreCase("public"));
            }

            if (moodMatches && dateMatches && displayMatches) {
                filteredList.add(entry);
            }
        }

        filteredMoodEntries.setValue(filteredList);
    }

    private boolean isDateInRange(String dateTimeStr, String dateFilter) {
        if (dateTimeStr == null || dateTimeStr.isEmpty() || dateFilter == null || dateFilter.isEmpty()) {
            return true;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy | HH:mm", Locale.US);
            Date entryDate = sdf.parse(dateTimeStr);
            if (entryDate == null) return true;

            Calendar calendar = Calendar.getInstance();

            if (dateFilter.equals("24h")) {
                calendar.add(Calendar.HOUR, -24);
                return entryDate.after(calendar.getTime());
            } else if (dateFilter.equals("7d")) {
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                return entryDate.after(calendar.getTime());
            }

            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
    }

    // May not needed, delete later
    public void setFilters(String mood, String date, String displayMineOrFollowing) {
        moodFilter.setValue(mood);
        dateFilter.setValue(date);
        displayMineOrFollowingFilter.setValue(displayMineOrFollowing);
        filtersApplied.setValue(true);
        filterMoodEntries();
    }

    public void clearFilter(){
        moodFilter.setValue("");
        dateFilter.setValue("");
        displayMineOrFollowingFilter.setValue("");
        filtersApplied.setValue(false);
        filterMoodEntries();
    }
}
