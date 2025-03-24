package com.example.uiapp.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.uiapp.model.MoodEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ViewModel for managing mood history data and filtering operations.
 * Handles mood and date filters, and maintains the UI state.
 */
public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> moodFilter = new MutableLiveData<>("");
    private final MutableLiveData<String> dateFilter = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> filtersApplied = new MutableLiveData<>(false);

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    /**
     * Updates mood and date filters and marks filters as applied.
     */
    public void setFilters(String mood, String date) {
        moodFilter.setValue(mood);
        dateFilter.setValue(date);
        filtersApplied.setValue(true);
    }

    public LiveData<String> getMoodFilter() {
        return moodFilter;
    }

    public LiveData<String> getDateFilter() {
        return dateFilter;
    }

    public LiveData<Boolean> getFiltersApplied() {
        return filtersApplied;
    }

    /**
     * Resets all filters to their default empty state.
     */
    public void clearFilters() {
        moodFilter.setValue("");
        dateFilter.setValue("");
        filtersApplied.setValue(false);
    }

    /**
     * Applies mood and date filters to the provided list of mood entries.
     * Includes null safety checks and handles empty filter cases.
     * Returns filtered list or original list if no filters are applied.
     */
    public List<MoodEntry> applyFilters(List<MoodEntry> originalList) {
        // Handle null input list
        if (originalList == null) {
            return new ArrayList<>();
        }
        
        // If no filters are applied, return the original list
        if (filtersApplied == null || !filtersApplied.getValue()) {
            return originalList;
        }

        List<MoodEntry> filteredList = new ArrayList<>();
        String mood = moodFilter.getValue() != null ? moodFilter.getValue() : "";
        String date = dateFilter.getValue() != null ? dateFilter.getValue() : "";

        for (MoodEntry entry : originalList) {
            // Skip null entries
            if (entry == null) {
                continue;
            }
            
            // Get mood safely
            String entryMood = entry.getMood() != null ? entry.getMood() : "";
            
            boolean moodMatches = mood.isEmpty() || entryMood.equals(mood);
            boolean dateMatches = isDateInRange(entry.getDateTime(), date);

            if (moodMatches && dateMatches) {
                filteredList.add(entry);
            }
        }

        return filteredList;
    }

    /**
     * Checks if a given date falls within the selected date filter range.
     * Handles both "Yesterday" and standard date formats.
     * Returns true if date is in range or if any input is invalid.
     */
    private boolean isDateInRange(String dateTimeStr, String dateFilter) {
        // Handle null or empty inputs
        if (dateTimeStr == null || dateTimeStr.isEmpty() || dateFilter == null || dateFilter.isEmpty()) {
            return true;
        }

        try {
            // Parse date string to Date object
            SimpleDateFormat sdf;
            if (dateTimeStr.contains("Yesterday")) {
                // Yesterday format
                sdf = new SimpleDateFormat("'Yesterday,' MMM dd, yyyy | HH:mm", Locale.US);
            } else {
                // Normal date format
                sdf = new SimpleDateFormat("EEE, MMM dd, yyyy | HH:mm", Locale.US);
            }

            Date entryDate = sdf.parse(dateTimeStr);
            if (entryDate == null) return true;

            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);

            if (dateFilter.equals("24h")) {
                // Past 24 hours
                calendar.add(Calendar.HOUR, -24);
                return entryDate.after(calendar.getTime());
            } else if (dateFilter.equals("7d")) {
                // Last 7 days
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                return entryDate.after(calendar.getTime());
            }

            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
    }
}

