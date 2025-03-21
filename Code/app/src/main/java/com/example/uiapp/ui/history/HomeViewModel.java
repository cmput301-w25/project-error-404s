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

    public void clearFilters() {
        moodFilter.setValue("");
        dateFilter.setValue("");
        filtersApplied.setValue(false);
    }

    public List<MoodEntry> applyFilters(List<MoodEntry> originalList) {
        if (!filtersApplied.getValue()) {
            return originalList;
        }

        List<MoodEntry> filteredList = new ArrayList<>();
        String mood = moodFilter.getValue();
        String date = dateFilter.getValue();

        for (MoodEntry entry : originalList) {
            boolean moodMatches = mood.isEmpty() || entry.getMood().equals(mood);
            boolean dateMatches = isDateInRange(entry.getDateTime(), date);

            if (moodMatches && dateMatches) {
                filteredList.add(entry);
            }
        }

        return filteredList;
    }

    private boolean isDateInRange(String dateTimeStr, String dateFilter) {
        if (dateFilter.isEmpty()) {
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

