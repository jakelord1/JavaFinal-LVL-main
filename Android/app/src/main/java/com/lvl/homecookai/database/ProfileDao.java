package com.lvl.homecookai.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ProfileDao {

    @Query("SELECT * FROM profile WHERE id = 1 LIMIT 1")
    ProfileEntity getProfile();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(ProfileEntity profile);
}
