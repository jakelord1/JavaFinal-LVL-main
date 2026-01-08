package com.lvl.homecookai.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecentScanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RecentScan scan);

    @Query("SELECT * FROM recent_scan ORDER BY createdAt DESC LIMIT :limit")
    List<RecentScan> getRecent(int limit);

    @Query("SELECT COUNT(*) FROM recent_scan")
    int getScanCount();

    @Query("SELECT createdAt FROM recent_scan ORDER BY createdAt DESC")
    List<Long> getAllScanTimestamps();

    @Query("DELETE FROM recent_scan WHERE id NOT IN (SELECT id FROM recent_scan ORDER BY createdAt DESC LIMIT :limit)")
    void trimToLimit(int limit);

    @Query("DELETE FROM recent_scan WHERE id = :scanId")
    void deleteById(int scanId);
}
