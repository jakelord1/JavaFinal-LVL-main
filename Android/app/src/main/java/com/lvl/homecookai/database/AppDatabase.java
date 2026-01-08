package com.lvl.homecookai.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.lvl.homecookai.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Database(entities = {Recipe.class, Ingredient.class, Recipe_Position.class, RecentScan.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecipeDao recipeDao();
    public abstract IngredientDao ingredientDao();
    public abstract RecentScanDao recentScanDao();
    

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "recipe_database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();

                    
                }
            }
        }
        return INSTANCE;
    }

    
}
