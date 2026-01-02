package com.itstep.homecook.data.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.itstep.homecook.services.config.JsonConfigService;

import java.sql.Statement;

public class DataAccesor {
    private static final Logger logger = Logger.getLogger("DBLogger");
    private static final JsonConfigService config = new JsonConfigService(logger);
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.severe("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }
    public static Connection getConnection() throws SQLException {            
        String connectionString = config.get("connectionStrings.mainDb");
        return DriverManager.getConnection(connectionString);
    }       

    public static boolean init() {
        try (Connection conn = getConnection()) {
        String sql_init = "CREATE TABLE IF NOT EXISTS Ingredients (     "+
        "id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,           "+
        "name VARCHAR(100) NOT NULL,                                    "+
        "description VARCHAR(1024),                                     "+
        "category VARCHAR(150));";
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql_init);
        } catch (SQLException ex) {
            logger.log(Level.WARNING ,"DataAccessor::install" + ex.getMessage() + " | " + sql_init);
            return false;
        }

        sql_init = "CREATE TABLE IF NOT EXISTS Recipes (                           "+
        "id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,           "+
        "dish_name VARCHAR(100) NOT NULL,                               "+
        "cook_time INT NOT NULL,                                        "+
        "dish_shorttext VARCHAR(255) NOT NULL,                          "+
        "recipe_fulltext VARCHAR(8192) NOT NULL,                        "+
        "category VARCHAR(255));                                        ";
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql_init);
        } catch (SQLException ex) {
            logger.log(Level.WARNING ,"DataAccessor::install" + ex.getMessage() + " | " + sql_init);
        }

        sql_init = "CREATE TABLE IF NOT EXISTS Recipe_Positions (                  "+
        "id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,           "+
        "ingredient_id INT UNSIGNED,                                             "+
        "recipe_id INT UNSIGNED,                                                 "+
        "amount INT NOT NULL,                                           "+
        "unit VARCHAR(10),                                              "+
        "notes VARCHAR(255),                                            "+
        "FOREIGN KEY (ingredient_id) REFERENCES Ingredients(id),        "+
        "FOREIGN KEY (recipe_id) REFERENCES Recipes(id));                ";
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql_init);
        } catch (SQLException ex) {
            logger.log(Level.WARNING ,"DataAccessor::install" + ex.getMessage() + " | " + sql_init);
        }
    }
    catch (SQLException ex) {
        return false;
    }
    return true;
    }
}
