package com.lvl.homecookai.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recent_scan")
public class RecentScan {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String imageUri;
    private String summary;
    private long createdAt;

    public RecentScan() {}

    @androidx.room.Ignore
    public RecentScan(String imageUri, String summary, long createdAt) {
        this.imageUri = imageUri;
        this.summary = summary;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
