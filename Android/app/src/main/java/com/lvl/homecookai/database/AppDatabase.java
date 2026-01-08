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

@Database(entities = {Recipe.class, Ingredient.class, Recipe_Position.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecipeDao recipeDao();
    public abstract IngredientDao ingredientDao();
    // Можно добавить IngredientDao и RecipePositionDao при необходимости

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

                    // initializeData(INSTANCE);
                }
            }
        }
        return INSTANCE;
    }

    /*
    private static void initializeData(AppDatabase database) {
        RecipeDao dao = database.recipeDao();

        List<Recipe> seeds = Arrays.asList(
            new Recipe(
                "Pasta Carbonara",          // dish_name
                20,                         // cook_time (в минутах)
                "Классическая паста с беконом и сыром", // dish_shorttext
                "1. Boil pasta in salted water\n2. Fry bacon until crispy\n3. Mix eggs with grated cheese\n4. Combine hot pasta with bacon\n5. Add egg mixture and toss quickly\n6. Serve immediately with black pepper", // recipe_fulltext
                "Italian",                  // category
                "carbonara.png"             // image (URL или имя файла)
            ),
            new Recipe(
                "Pizza Margarita",
                30,
                "Простая пицца с томатами и базиликом",
                "1. Preheat oven to 220C\n2. Stretch pizza dough\n3. Spread tomato sauce evenly\n4. Add mozzarella cheese\n5. Bake for 12-15 minutes\n6. Add fresh basil and olive oil before serving",
                "Italian",
                "margarita.png"
            ),
            new Recipe(
                "Caesar Salad",
                15,
                "Салат с соусом Цезарь и сухариками",
                "1. Wash and chop romaine lettuce\n2. Toss with caesar dressing\n3. Add croutons\n4. Sprinkle parmesan cheese\n5. Add black pepper\n6. Squeeze fresh lemon juice\n7. Serve immediately",
                "Salad",
                "caesar.png"
            )
            // ... остальные рецепты
        );

        List<Recipe> existing = dao.getAllRecipes();
        Set<String> existingNames = new HashSet<>();
        for (Recipe recipe : existing) {
            if (recipe.getDish_name() != null) {
                existingNames.add(recipe.getDish_name());
            }
        }

        List<Recipe> toInsert = new ArrayList<>();
        for (Recipe seed : seeds) {
            if (!existingNames.contains(seed.getDish_name())) {
                toInsert.add(seed);
            }
        }

        if (!toInsert.isEmpty()) {
            dao.insertRecipes(toInsert);
        }
    }
    */
}
