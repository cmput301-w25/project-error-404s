package com.example.uiapp.ui.history;

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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ViewModel for managing mood history data and filtering operations.
 */
public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<MoodEntry>> moodEntries = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<MoodEntry>> filteredMoodEntries = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<String> moodFilter = new MutableLiveData<>("");
    private final MutableLiveData<String> dateFilter = new MutableLiveData<>("");
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
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
    public void fetchMoodEntries(String following) {
        moodEventsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                QuerySnapshot snapshot = task.getResult();
                String loggedInUsername = HelperClass.users.getUsername();
                List<MoodEntry> allMoods = new ArrayList<>();
                for (QueryDocumentSnapshot document : snapshot) {
                    MoodEntry mood = document.toObject(MoodEntry.class);
                    if (following.isEmpty()) {
                        if (mood.getUsername().equals(loggedInUsername)) {
                            allMoods.add(mood);
                        }
                    } else if (following.contains("following")) {
                        if (HelperClass.users.getFollowing().contains(mood.getUsername())) {
                            allMoods.add(mood);
                        }
                    }
                }
                Collections.sort(allMoods, (a, b) -> b.getDateTime().compareTo(a.getDateTime()));

                moodEntries.setValue(allMoods);
                filterMoodEntries(); // Apply filters

            }
        });
    }

    /**
     * Applies filters based on mood, date, and search query.
     */
    private void filterMoodEntries() {
        List<MoodEntry> originalList = moodEntries.getValue();
        if (originalList == null) return;

        List<MoodEntry> filteredList = new ArrayList<>();

        String mood = moodFilter.getValue() != null ? moodFilter.getValue() : "";
        String date = dateFilter.getValue() != null ? dateFilter.getValue() : "";
        String query = searchQuery.getValue() != null ? searchQuery.getValue().toLowerCase() : "";

        for (MoodEntry entry : originalList) {
            if (entry == null) continue;

            boolean moodMatches = mood.isEmpty() || entry.getMood().equalsIgnoreCase(mood);
            boolean dateMatches = isDateInRange(entry.getDateTime(), date);
            boolean searchMatches = query.isEmpty() || entryContainsSearchText(entry, query);

            if (moodMatches && dateMatches && searchMatches) {
                filteredList.add(entry);
            }
        }

        filteredMoodEntries.setValue(filteredList);
    }

    public void clearMoodEntries() {
        filteredMoodEntries.setValue(new ArrayList<>()); // Clear LiveData
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

    private boolean entryContainsSearchText(MoodEntry entry, String searchText) {
        String mood = entry.getMood() != null ? entry.getMood().toLowerCase() : "";
        String note = entry.getNote() != null ? entry.getNote().toLowerCase() : "";
        String location = entry.getLocation() != null ? entry.getLocation().toLowerCase() : "";
        String people = entry.getPeople() != null ? entry.getPeople().toLowerCase() : "";

        return mood.contains(searchText) || note.contains(searchText) || location.contains(searchText) || people.contains(searchText);
    }

    public void setFilters(String mood, String date) {
        moodFilter.setValue(mood);
        dateFilter.setValue(date);
        filtersApplied.setValue(true);
        filterMoodEntries();
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
        filterMoodEntries();
    }

    public void deleteMoodEntry(MoodEntry moodEntry) {
        if (moodEntry == null || moodEntry.getMoodId() == null || moodEntry.getMoodId().isEmpty()) {
            return;
        }

        moodEventsRef.document(moodEntry.getMoodId()).delete()
                .addOnSuccessListener(aVoid -> {
                    List<MoodEntry> updatedList = new ArrayList<>(moodEntries.getValue());
                    updatedList.remove(moodEntry);
                    moodEntries.setValue(updatedList);
                    filterMoodEntries();
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

}