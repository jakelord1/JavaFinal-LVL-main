package com.itstep.homecook.data.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.itstep.homecook.data.dto.Ingredients;
import com.itstep.homecook.data.dto.Recipe_Positions;
import java.sql.Statement;


public class Recipe_PositionsDAO {
    private final Logger logger;
    private final Connection connection;

        @com.google.inject.Inject
    public Recipe_PositionsDAO(Connection connection, Logger logger) {
        this.connection = connection;
        this.logger = logger;
    }

    public List<Recipe_Positions> getAll() {
        String sql = "SELECT * FROM Recipes;";
        List<Recipe_Positions> ret = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ret.add(Recipe_Positions.fromResultSet(rs));
            }
        } catch (SQLException ex) {
            logger.warning("RecipesDAO::getAll " + ex.getMessage());
        }
        return ret;
    }

    public Recipe_Positions getId(int id) {
        String sql = "SELECT rec_pos.*, ing.id AS ingredient_id, ing.name, ing.description, ing.category\r\n" +
                        "FROM Recipe_Positions rec_pos\r\n" +
                        "INNER JOIN Ingredients ing ON rec_pos.ingredient_id = ing.id\r\n" +
                        "WHERE rec_pos.id = ?;\r\n" +
                        "";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setInt(1, id);
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    Ingredients ingredient = new Ingredients(
                        rs.getInt("ingredient_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("category")
                    );
                    Recipe_Positions recipe_position = new Recipe_Positions(
                        rs.getInt("id"),
                        ingredient,
                        null,
                        rs.getInt("amount"),
                        rs.getString("unit"),
                        rs.getString("notes"),
                        rs.getInt("ingredient_id")
                    );
                    return recipe_position;
                }
            }
        } catch (SQLException ex) {
            logger.warning("Recipe_PositionsDAO::getId " + ex.getMessage());
        }
        return null;
    }

    public void add(Recipe_Positions recipe_Positions) {
        String sql = "INSERT INTO Recipe_Positions(amount, unit, notes, ingredient_id, recipe_id) VALUES(?,?,?,?,?)";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setInt(1, recipe_Positions.getAmount());
            prep.setString(2, recipe_Positions.getUnit());
            prep.setString(3, recipe_Positions.getNotes());
            prep.setInt(4, recipe_Positions.getIngredient().getId());
            prep.setInt(5, recipe_Positions.getRecipe().getId());
            prep.executeUpdate();
        } catch (SQLException ex) {
            logger.warning("Recipe_PositionsDAO::add " + ex.getMessage());
        }
    }

    public boolean update(Recipe_Positions recipe_Positions) {
        String sql = "UPDATE Recipe_Positions SET amount = ?, unit = ?, notes = ?, ingredient_id = ? WHERE id = ?";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setInt(1, recipe_Positions.getAmount());
            prep.setString(2, recipe_Positions.getUnit());
            prep.setString(3, recipe_Positions.getNotes());
            prep.setInt(4, recipe_Positions.getIngredient().getId());
            prep.setInt(5, recipe_Positions.getId());
            logger.info("Updating Recipe_Position with ID: " + prep.toString());
            prep.executeUpdate();
            return true;
        } catch (SQLException ex) {
            logger.warning("Recipe_PositionsDAO::update " + ex.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Recipe_Positions WHERE id = ?;";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setInt(1, id);
            prep.executeUpdate();
            return true;
        } catch (SQLException ex) {
            logger.warning("Recipe_PositionsDAO::delete " + ex.getMessage());
        }
        return false;
    }
}
