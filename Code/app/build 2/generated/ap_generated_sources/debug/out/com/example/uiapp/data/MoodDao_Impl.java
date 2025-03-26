package com.example.uiapp.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.uiapp.model.MoodEntry;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class MoodDao_Impl implements MoodDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MoodEntry> __insertionAdapterOfMoodEntry;

  private final EntityDeletionOrUpdateAdapter<MoodEntry> __deletionAdapterOfMoodEntry;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public MoodDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMoodEntry = new EntityInsertionAdapter<MoodEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `mood_table` (`roomID`,`dateTime`,`mood`,`note`,`people`,`location`,`username`,`latitude`,`longitude`,`moodIcon`,`imageUrl`,`isHome`,`moodID`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final MoodEntry entity) {
        statement.bindLong(1, entity.getRoomID());
        if (entity.getDateTime() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getDateTime());
        }
        if (entity.getMood() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getMood());
        }
        if (entity.getNote() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getNote());
        }
        if (entity.getPeople() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getPeople());
        }
        if (entity.getLocation() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getLocation());
        }
        if (entity.getUsername() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getUsername());
        }
        statement.bindDouble(8, entity.getLatitude());
        statement.bindDouble(9, entity.getLongitude());
        statement.bindLong(10, entity.getMoodIcon());
        if (entity.getImageUrl() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getImageUrl());
        }
        final Integer _tmp = entity.getIsHome() == null ? null : (entity.getIsHome() ? 1 : 0);
        if (_tmp == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, _tmp);
        }
        if (entity.getMoodID() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getMoodID());
        }
      }
    };
    this.__deletionAdapterOfMoodEntry = new EntityDeletionOrUpdateAdapter<MoodEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `mood_table` WHERE `roomID` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final MoodEntry entity) {
        statement.bindLong(1, entity.getRoomID());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM mood_table";
        return _query;
      }
    };
  }

  @Override
  public void insert(final MoodEntry moodEntry) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfMoodEntry.insert(moodEntry);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final MoodEntry moodEntry) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfMoodEntry.handle(moodEntry);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAll() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public LiveData<List<MoodEntry>> getAllMoods() {
    final String _sql = "SELECT * FROM mood_table ORDER BY roomID DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"mood_table"}, false, new Callable<List<MoodEntry>>() {
      @Override
      @Nullable
      public List<MoodEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfRoomID = CursorUtil.getColumnIndexOrThrow(_cursor, "roomID");
          final int _cursorIndexOfDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTime");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfPeople = CursorUtil.getColumnIndexOrThrow(_cursor, "people");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfMoodIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "moodIcon");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfIsHome = CursorUtil.getColumnIndexOrThrow(_cursor, "isHome");
          final int _cursorIndexOfMoodID = CursorUtil.getColumnIndexOrThrow(_cursor, "moodID");
          final List<MoodEntry> _result = new ArrayList<MoodEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MoodEntry _item;
            _item = new MoodEntry();
            final int _tmpRoomID;
            _tmpRoomID = _cursor.getInt(_cursorIndexOfRoomID);
            _item.setRoomID(_tmpRoomID);
            final String _tmpDateTime;
            if (_cursor.isNull(_cursorIndexOfDateTime)) {
              _tmpDateTime = null;
            } else {
              _tmpDateTime = _cursor.getString(_cursorIndexOfDateTime);
            }
            _item.setDateTime(_tmpDateTime);
            final String _tmpMood;
            if (_cursor.isNull(_cursorIndexOfMood)) {
              _tmpMood = null;
            } else {
              _tmpMood = _cursor.getString(_cursorIndexOfMood);
            }
            _item.setMood(_tmpMood);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _item.setNote(_tmpNote);
            final String _tmpPeople;
            if (_cursor.isNull(_cursorIndexOfPeople)) {
              _tmpPeople = null;
            } else {
              _tmpPeople = _cursor.getString(_cursorIndexOfPeople);
            }
            _item.setPeople(_tmpPeople);
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            _item.setLocation(_tmpLocation);
            final String _tmpUsername;
            if (_cursor.isNull(_cursorIndexOfUsername)) {
              _tmpUsername = null;
            } else {
              _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            }
            _item.setUsername(_tmpUsername);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            _item.setLatitude(_tmpLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            _item.setLongitude(_tmpLongitude);
            final int _tmpMoodIcon;
            _tmpMoodIcon = _cursor.getInt(_cursorIndexOfMoodIcon);
            _item.setMoodIcon(_tmpMoodIcon);
            final String _tmpImageUrl;
            if (_cursor.isNull(_cursorIndexOfImageUrl)) {
              _tmpImageUrl = null;
            } else {
              _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            }
            _item.setImageUrl(_tmpImageUrl);
            final Boolean _tmpIsHome;
            final Integer _tmp;
            if (_cursor.isNull(_cursorIndexOfIsHome)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(_cursorIndexOfIsHome);
            }
            _tmpIsHome = _tmp == null ? null : _tmp != 0;
            _item.setIsHome(_tmpIsHome);
            final String _tmpMoodID;
            if (_cursor.isNull(_cursorIndexOfMoodID)) {
              _tmpMoodID = null;
            } else {
              _tmpMoodID = _cursor.getString(_cursorIndexOfMoodID);
            }
            _item.setMoodID(_tmpMoodID);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
