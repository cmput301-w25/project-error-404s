package com.example.uiapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;
import com.example.uiapp.model.MoodEntry;

import java.util.List;

@Dao
public interface MoodDao {

    // Insert a mood entry (replace if conflict)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MoodEntry moodEntry);

    // Get all mood entries ordered by date
    @Query("SELECT * FROM mood_table ORDER BY roomID DESC")
    LiveData<List<MoodEntry>> getAllMoods();

    // Delete a specific mood entry
    @Delete
    void delete(MoodEntry moodEntry);

    // Delete all mood entries
    @Query("DELETE FROM mood_table")
    void deleteAll();
}
