package com.lvl.homecookai.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ProfileEntity.class}, version = 1, exportSchema = false)
public abstract class ProfileDatabase extends RoomDatabase {

    public abstract ProfileDao profileDao();

    private static volatile ProfileDatabase INSTANCE;

    public static ProfileDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ProfileDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ProfileDatabase.class, "profile_database")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
