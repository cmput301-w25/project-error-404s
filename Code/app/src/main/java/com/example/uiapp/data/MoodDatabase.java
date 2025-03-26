package com.example.uiapp.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.uiapp.model.MoodEntry;

@Database(entities = {MoodEntry.class}, version = 1, exportSchema = false)
public abstract class MoodDatabase extends RoomDatabase {

    private static volatile MoodDatabase INSTANCE;

    public abstract MoodDao moodDao();

    public static MoodDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (MoodDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    MoodDatabase.class, "mood_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
