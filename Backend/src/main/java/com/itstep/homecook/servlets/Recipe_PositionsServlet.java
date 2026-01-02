package com.itstep.homecook.servlets;

import com.google.gson.Gson;
import com.itstep.homecook.data.dao.IngredientsDAO;
import com.itstep.homecook.data.dao.Recipe_PositionsDAO;
import com.itstep.homecook.data.dao.RecipesDAO;
import com.itstep.homecook.data.dto.Ingredients;
import com.itstep.homecook.data.dto.Recipe_Positions;
import com.google.inject.Singleton;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Singleton
public class Recipe_PositionsServlet extends HttpServlet {

    private Recipe_PositionsDAO recipesDAO;

    @Inject
    public Recipe_PositionsServlet(Recipe_PositionsDAO recipesDAO) {
        this.recipesDAO = recipesDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Recipe_Positions> recipes = recipesDAO.getAll();
        String json = new Gson().toJson(recipes);
        resp.setContentType("application/json");
        resp.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Recipe_Positions recipe_Positions = new Gson().fromJson(req.getReader(), Recipe_Positions.class);
        recipesDAO.add(recipe_Positions);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Recipe_Positions recipe_Positions = new Gson().fromJson(req.getReader(), Recipe_Positions.class);
        boolean success = recipesDAO.update(recipe_Positions);
        resp.setStatus(success ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        boolean success = recipesDAO.delete(id);
        resp.setStatus(success ? HttpServletResponse.SC_NO_CONTENT : HttpServletResponse.SC_NOT_FOUND);
    }
}