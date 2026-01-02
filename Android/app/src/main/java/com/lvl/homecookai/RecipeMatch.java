package com.lvl.homecookai;

import com.lvl.homecookai.database.Recipe;

public class RecipeMatch {
    public final Recipe recipe;
    public final int matchPercent;

    public RecipeMatch(Recipe recipe, int matchPercent) {
        this.recipe = recipe;
        this.matchPercent = matchPercent;
    }
}
