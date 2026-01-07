package com.itstep.homecook.data.dto;

public class Ingredients {
    private int id;
    private String name;
    private String description;
    private String category;

    public Ingredients(int id, String name, String desc, String category) {
        this.id = id;
        this.name = name;
        this.description = desc;
        this.category = category;
    }
    public Ingredients() {
    }
    public static Ingredients fromResultSet(java.sql.ResultSet rs) throws java.sql.SQLException {
        Ingredients ingredient = new Ingredients(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getString("category")
        );
        return ingredient;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
