package com.itstep.homecook.data.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;

import com.itstep.homecook.data.dto.Recipe_Positions;
import com.itstep.homecook.data.dto.Recipes;
import com.itstep.homecook.data.dto.Ingredients;


public class RecipesDAO {
    private final Logger logger;
    private final Connection connection;

        @com.google.inject.Inject
    public RecipesDAO(Connection connection, Logger logger) {
        this.connection = connection;
        this.logger = logger;
    }

    public List<Recipes> getAll(int page) {
    List<Recipes> ret = new ArrayList<>();

    String sql = """
        SELECT 
            rec.id AS recipe_id,
            rec.dish_name AS dish_name,
            rec.dish_shorttext AS dish_shorttext,
            rec.cook_time AS cook_time,
            rec.category AS categories,
            rec.recipe_fulltext AS recipe_fulltext,
            rec.image AS image,
            rec_pos.id AS position_id,
            rec_pos.ingredient_id AS position_ingredient_id,
            rec_pos.amount AS position_amount,
            ing.id AS ingredient_id,
            ing.name AS ingredient_name
        FROM Recipes rec
        LEFT JOIN Recipe_Positions rec_pos ON rec_pos.recipe_id = rec.id
        LEFT JOIN Ingredients ing ON ing.id = rec_pos.ingredient_id
        LIMIT ?,500
    """;

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, page * 100);
        ResultSet rs = stmt.executeQuery();

        Map<Integer, Recipes> recipesMap = new HashMap<>();

        while (rs.next()) {
            int recipeId = rs.getInt("recipe_id");
            Recipes recipe = recipesMap.get(recipeId);

            if (recipe == null) {
                recipe = new Recipes();
                recipe.setId(recipeId);
                recipe.setDish_name(rs.getString("dish_name"));
                recipe.setDish_shorttext(rs.getString("dish_shorttext"));
                recipe.setCook_time(rs.getInt("cook_time"));
                recipe.setCategories(rs.getString("categories"));
                recipe.setRecipe_fulltext(rs.getString("recipe_fulltext"));
                recipe.setImage(rs.getString("image"));
                recipe.setRecipe_positions(new ArrayList<>());
                recipesMap.put(recipeId, recipe);
            }

            int posId = rs.getInt("position_id");
            if (posId > 0) { 
                Recipe_Positions pos = new Recipe_Positions();
                pos.setId(posId);
                pos.setIngredient_id(rs.getInt("position_ingredient_id"));
                pos.setAmount(rs.getInt("position_amount"));

                Ingredients ing = new Ingredients();
                ing.setId(rs.getInt("ingredient_id"));
                ing.setName(rs.getString("ingredient_name"));
                pos.setIngredient(ing);

                pos.setRecipe(null);
                recipe.getRecipe_positions().add(pos);
            }
        }
        ret.addAll(recipesMap.values());
    } catch (SQLException ex) {
        logger.warning("RecipesDAO::getAll " + ex.getMessage());
    }
    return ret;
}

    public Recipes getId(int id) {
        String sql = "SELECT * FROM Recipes rec INNER JOIN Recipe_Positions rec_pos ON rec_pos.recipe_id = rec.id INNER JOIN Ingredients ing ON ing.id = rec_pos.ingredient_id WHERE rec.id = ?;";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setInt(1, id);
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    Recipes that = Recipes.fromResultSet(rs);
                    for (Recipe_Positions r : that.getRecipe_positions()) {
                        r.setRecipe(null);
                    }
                    String posids = "SELECT id FROM Recipe_Positions WHERE recipe_id = ?;";
                    try (PreparedStatement preps = connection.prepareStatement(posids)) {
                        preps.setInt(1, id);
                        try (ResultSet rss = preps.executeQuery()) {
                            int index = 0;
                            while (rss.next() && index < that.getRecipe_positions().size()) {
                                that.getRecipe_positions().get(index).setId(rss.getInt("id"));
                                index++;
                            }
                        }
                    }
                    return that;
                }
            }
        } catch (SQLException ex) {
            logger.warning("RecipesDAO::getId " + ex.getMessage());
        }
        return null;
    }


    public List<Recipes> findMatchingRecipes(List<Recipe_Positions> recipePositions) {
        List<Recipes> result = new ArrayList<>();
        if (recipePositions == null || recipePositions.isEmpty()) {
            return result;
        }
        List<Integer> ingredientIds = recipePositions.stream()
                .map(Recipe_Positions::getIngredient_id)
                .distinct()
                .collect(Collectors.toList());
        String placeholders = ingredientIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));
                
        String findIdsSql = "SELECT rec.id " +
            "FROM Recipes rec " +
            "JOIN Recipe_Positions rp ON rp.recipe_id = rec.id " +
            "GROUP BY rec.id " +
            "HAVING SUM(CASE WHEN rp.ingredient_id NOT IN (" + placeholders + ") THEN 1 ELSE 0 END) = 0 " +
            "   AND SUM(CASE WHEN rp.ingredient_id IN (" + placeholders + ") THEN 1 ELSE 0 END) > 0";

    try {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement prep = connection.prepareStatement(findIdsSql)) {
            int index = 1;
            for (Integer id : ingredientIds) prep.setInt(index++, id); 
            for (Integer id : ingredientIds) prep.setInt(index++, id); 

            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("id"));
            }
        }

        if (!ids.isEmpty()) {
            String idsPlaceholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
            
            String fetchSql = "SELECT * FROM Recipes rec " +
                    "INNER JOIN Recipe_Positions rec_pos ON rec_pos.recipe_id = rec.id " +
                    "INNER JOIN Ingredients ing ON ing.id = rec_pos.ingredient_id " +
                    "WHERE rec.id IN (" + idsPlaceholders + ")";

            Map<Integer, Recipes> recipeMap = new LinkedHashMap<>();

            try (PreparedStatement fetchPrep = connection.prepareStatement(fetchSql)) {
                int index = 1;
                for (Integer id : ids) fetchPrep.setInt(index++, id);

                try (ResultSet rs = fetchPrep.executeQuery()) {
                    while (rs.next()) {
                        int rId = rs.getInt("id");

                        if (!recipeMap.containsKey(rId)) {
                            Recipes r = new Recipes(
                                null,
                                rs.getInt("id"),
                                rs.getString("dish_name"),
                                rs.getInt("cook_time"),
                                rs.getString("dish_shorttext"),
                                rs.getString("recipe_fulltext"),
                                rs.getString("category"),
                                rs.getString("image")
                            );
                            r.setRecipe_positions(new ArrayList<>());
                            recipeMap.put(rId, r);
                        }

                        Recipe_Positions pos = Recipe_Positions.fromResultSet(rs);
                        pos.setRecipe(null);
                        recipeMap.get(rId).getRecipe_positions().add(pos);
                    }
                }
            }
            result.addAll(recipeMap.values());
        }
        } catch (SQLException ex) {
            logger.warning("RecipesDAO::findMatchingRecipes " + ex.getMessage());
        }

        return result;
    }


    public List<Recipes> getName(String name) {
        String sql = "SELECT * FROM Recipes WHERE dish_name = ?;";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, name);
            try (ResultSet rs = prep.executeQuery()) {
                List<Recipes> recipes = new ArrayList<>();
                while (rs.next()) {
                    Recipes recipe = new Recipes(
                        null,
                        rs.getInt("id"),
                        rs.getString("dish_name"),
                        rs.getInt("cook_time"),
                        rs.getString("dish_shorttext"),
                        rs.getString("recipe_fulltext"),
                        rs.getString("category"),
                        rs.getString("image")
                    );
                    recipes.add(recipe);
                }
                return recipes;
            }
        } catch (SQLException ex) {
            logger.warning("RecipesDAO::getName " + ex.getMessage());
        }
        return new ArrayList<>();
    }

    public void add(Recipes recipes) {
        String sql = "INSERT INTO Recipes(dish_name, dish_shorttext, recipe_fulltext, category, cook_time, image) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, recipes.getDish_name());
            prep.setString(2, recipes.getDish_shorttext());
            prep.setString(3, recipes.getRecipe_fulltext());
            prep.setString(4, recipes.getCategories());
            prep.setInt(5, recipes.getCook_time());
            prep.setString(6, recipes.getImage());
            prep.executeUpdate();
        } catch (SQLException ex) {
            logger.warning("RecipesDAO::add " + ex.getMessage());
        }
    }

    public boolean update(Recipes recipes) {
        String sql = "UPDATE Recipes SET dish_name = ?, dish_shorttext = ?,  recipe_fulltext = ?, category = ?, cook_time = ?, image = ? WHERE id = ?";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, recipes.getDish_name());
            prep.setString(2, recipes.getDish_shorttext());
            prep.setString(3, recipes.getRecipe_fulltext());
            prep.setString(4, recipes.getCategories());
            prep.setInt(5, recipes.getCook_time());
            prep.setString(6, recipes.getImage());
            prep.setInt(7, recipes.getId());
            prep.executeUpdate();
            return true;
        } catch (SQLException ex) {
            logger.warning("RecipesDAO::update " + ex.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Recipes WHERE id = ?;";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setInt(1, id);
            prep.executeUpdate();
            return true;
        } catch (SQLException ex) {
            logger.warning("RecipesDAO::delete " + ex.getMessage());
        }
        return false;
    }
}