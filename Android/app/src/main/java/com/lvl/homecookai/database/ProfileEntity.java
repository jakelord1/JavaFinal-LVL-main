package com.lvl.homecookai.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "profile")
public class ProfileEntity {

    @PrimaryKey
    private int id = 1;

    private String name;
    private String headline;
    private String email;
    private String location;
    private int savedCount;
    private int scansCount;
    private int streakDays;

    public ProfileEntity() {
    }

    public ProfileEntity(String name, String headline, String email, String location,
                         int savedCount, int scansCount, int streakDays) {
        this.id = 1;
        this.name = name;
        this.headline = headline;
        this.email = email;
        this.location = location;
        this.savedCount = savedCount;
        this.scansCount = scansCount;
        this.streakDays = streakDays;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSavedCount() {
        return savedCount;
    }

    public void setSavedCount(int savedCount) {
        this.savedCount = savedCount;
    }

    public int getScansCount() {
        return scansCount;
    }

    public void setScansCount(int scansCount) {
        this.scansCount = scansCount;
    }

    public int getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(int streakDays) {
        this.streakDays = streakDays;
    }
}
