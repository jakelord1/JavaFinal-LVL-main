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

@Database(entities = {Recipe.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecipeDao recipeDao();
    
    private static volatile AppDatabase INSTANCE;
    
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "recipe_database")
                            .allowMainThreadQueries()
                            .build();
                    initializeData(INSTANCE);
                }
            }
        }
        return INSTANCE;
    }

    private static void initializeData(AppDatabase database) {
        RecipeDao dao = database.recipeDao();

        List<Recipe> seeds = java.util.Arrays.asList(
            new Recipe(
                "Pasta Carbonara",
                "- Pasta\n- Eggs\n- Bacon\n- Parmesan Cheese",
                "20 min",
                "1. Boil pasta in salted water\n2. Fry bacon until crispy\n3. Mix eggs with grated cheese\n4. Combine hot pasta with bacon\n5. Add egg mixture and toss quickly\n6. Serve immediately with black pepper",
                R.drawable.ic_launcher_foreground
            ),
            new Recipe(
                "Pizza Margarita",
                "- Pizza Dough\n- Tomato Sauce\n- Mozzarella\n- Fresh Basil\n- Olive Oil",
                "30 min",
                "1. Preheat oven to 220C\n2. Stretch pizza dough\n3. Spread tomato sauce evenly\n4. Add mozzarella cheese\n5. Bake for 12-15 minutes\n6. Add fresh basil and olive oil before serving",
                R.drawable.ic_launcher_foreground
            ),
            new Recipe(
                "Caesar Salad",
                "- Romaine Lettuce\n- Croutons\n- Parmesan Cheese\n- Caesar Dressing\n- Black Pepper\n- Lemon",
                "15 min",
                "1. Wash and chop romaine lettuce\n2. Toss with caesar dressing\n3. Add croutons\n4. Sprinkle parmesan cheese\n5. Add black pepper\n6. Squeeze fresh lemon juice\n7. Serve immediately",
                R.drawable.ic_launcher_foreground
            ),
            new Recipe(
                "Cucumber Tomato Salad",
                "- Cucumber\n- Tomato\n- Red Onion\n- Parsley\n- Olive Oil\n- Lemon\n- Salt\n- Black Pepper",
                "10 min",
                "1. Slice cucumber, tomato, and red onion\n2. Chop parsley finely\n3. Toss with olive oil and lemon juice\n4. Season with salt and pepper\n5. Serve chilled",
                R.drawable.ic_launcher_foreground
            ),
            new Recipe(
                "Tomato Parsley Bruschetta",
                "- Tomato\n- Parsley\n- Garlic\n- Olive Oil\n- Baguette\n- Salt\n- Black Pepper",
                "15 min",
                "1. Dice tomato and parsley\n2. Mix with garlic, olive oil, salt, and pepper\n3. Toast baguette slices\n4. Spoon tomato mixture over toast\n5. Serve immediately",
                R.drawable.ic_launcher_foreground
            ),
            new Recipe(
                "Creamy Dill Potatoes",
                "- Potatoes\n- Dill\n- Butter\n- Garlic\n- Salt\n- Black Pepper",
                "25 min",
                "1. Boil potatoes until tender\n2. Melt butter and add garlic\n3. Toss potatoes with dill and butter\n4. Season with salt and pepper\n5. Serve warm",
                R.drawable.ic_launcher_foreground
            ),
            new Recipe(
                "Cucumber Dill Yogurt Dip",
                "- Cucumber\n- Dill\n- Yogurt\n- Garlic\n- Lemon\n- Salt",
                "10 min",
                "1. Grate cucumber and squeeze excess water\n2. Mix yogurt, dill, garlic, and lemon\n3. Stir in cucumber\n4. Season with salt\n5. Chill before serving",
                R.drawable.ic_launcher_foreground
            ),
            new Recipe(
                "Red Onion Tomato Relish",
                "- Red Onion\n- Tomato\n- Vinegar\n- Sugar\n- Salt\n- Black Pepper",
                "20 min",
                "1. Dice red onion and tomato\n2. Simmer with vinegar and sugar\n3. Cook until slightly thickened\n4. Season with salt and pepper\n5. Cool before serving",
                R.drawable.ic_launcher_foreground
            )
        );

        List<Recipe> existing = dao.getAllRecipes();
        Set<String> existingNames = new HashSet<>();
        for (Recipe recipe : existing) {
            if (recipe.name != null) {
                existingNames.add(recipe.name);
            }
        }

        List<Recipe> toInsert = new ArrayList<>();
        for (Recipe seed : seeds) {
            if (!existingNames.contains(seed.name)) {
                toInsert.add(seed);
            }
        }

        if (!toInsert.isEmpty()) {
            dao.insertRecipes(toInsert);
        }
    }
}
