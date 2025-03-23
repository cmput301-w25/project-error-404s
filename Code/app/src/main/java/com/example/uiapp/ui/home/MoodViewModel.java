package com.example.uiapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.uiapp.model.MoodEntry;

import java.util.ArrayList;
import java.util.List;

public class MoodViewModel extends ViewModel {
    private final MutableLiveData<List<MoodEntry>> moodList = new MutableLiveData<>(new ArrayList<>());

    public void addMoodEntry(MoodEntry entry) {
        List<MoodEntry> currentList = moodList.getValue();
        if (currentList != null) {
            currentList.add(0, entry);
            moodList.postValue(currentList);
        }
    }

    public LiveData<List<MoodEntry>> getMoodEntries() {
        return moodList;
    }
}