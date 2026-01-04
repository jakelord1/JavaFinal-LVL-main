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

import com.itstep.homecook.data.dto.Recipe_Positions;
import com.itstep.homecook.data.dto.Recipes;


public class RecipesDAO {
    private final Logger logger;
    private final Connection connection;

        @com.google.inject.Inject
    public RecipesDAO(Connection connection, Logger logger) {
        this.connection = connection;
        this.logger = logger;
    }

    public List<Recipes> getAll(int page) {
        String sql = "SELECT * FROM Recipes rec INNER JOIN Recipe_Positions rec_pos ON rec_pos.recipe_id = rec.id INNER JOIN Ingredients ing ON ing.id = rec_pos.ingredient_id LIMIT ?,100;";
        List<Recipes> ret = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, page * 100);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Recipes that = Recipes.fromResultSet(rs);
                for (Recipe_Positions r : that.getRecipe_positions()) {
                    r.setRecipe(null);
                }
                ret.add(that);
            }
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
            for (Integer id : ingredientIds) prep.setInt(index++, id); // Для NOT IN
            for (Integer id : ingredientIds) prep.setInt(index++, id); // Для IN

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


    public int getName(String name) {
        String sql = "SELECT id FROM Recipes WHERE dish_name = ?;";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, name);
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next())
                    return rs.getInt("id");
            }
        } catch (SQLException ex) {
            logger.warning("RecipesDAO::getAll " + ex.getMessage());
        }
        return 0;
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