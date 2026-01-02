package com.itstep.homecook.data.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.itstep.homecook.data.dto.Ingredients;
import java.sql.Statement;


public class IngredientsDAO {
    private final Logger logger;
    private final Connection connection;

        @com.google.inject.Inject
    public IngredientsDAO(Connection connection, Logger logger) {
        this.connection = connection;
        this.logger = logger;
    }

    public List<Ingredients> getAll() {
        String sql = "SELECT * FROM Ingredients;";
        List<Ingredients> ret = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ret.add(Ingredients.fromResultSet(rs));
            }
        } catch (SQLException ex) {
            logger.warning("IngredientsDAO::getAll " + ex.getMessage());
        }
        return ret;
    }

    public Ingredients getId(int id) {
        String sql = "SELECT * FROM Ingredients WHERE id = ?;";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setInt(1, id);
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    return Ingredients.fromResultSet(rs);
                }
            }
        } catch (SQLException ex) {
            logger.warning("IngredientsDAO::getAll " + ex.getMessage());
        }
        return null;
    }

    public void add(Ingredients ingredient) {
        String sql = "INSERT INTO Ingredients(name, description, category) VALUES(?,?,?)";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, ingredient.getName());
            prep.setString(2, ingredient.getDescription());
            prep.setString(3, ingredient.getCategory());
            prep.executeUpdate();
        } catch (SQLException ex) {
            logger.warning("IngredientsDAO::add " + ex.getMessage());
        }
    }

    public boolean update(Ingredients ingredient) {
        String sql = "UPDATE Ingredients SET name = ?, description = ?, category = ? WHERE id = ?";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, ingredient.getName());
            prep.setString(2, ingredient.getDescription());
            prep.setString(3, ingredient.getCategory());
            prep.setInt(4, ingredient.getId());
            prep.executeUpdate();
            return true;
        } catch (SQLException ex) {
            logger.warning("IngredientsDAO::update " + ex.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Ingredients WHERE id = ?;";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setInt(1, id);
            prep.executeUpdate();
            return true;
        } catch (SQLException ex) {
            logger.warning("IngredientsDAO::delete " + ex.getMessage());
        }
        return false;
    }
}


