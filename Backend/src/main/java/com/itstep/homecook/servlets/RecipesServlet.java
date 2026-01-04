package com.itstep.homecook.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.inject.Singleton;
import com.itstep.homecook.data.dao.Recipe_PositionsDAO;
import com.itstep.homecook.data.dto.Ingredients;
import com.itstep.homecook.data.dto.Recipe_Positions;
import com.itstep.homecook.data.dao.RecipesDAO;
import com.itstep.homecook.data.dto.Recipes;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;


@Singleton
public class RecipesServlet extends HttpServlet {

    private RecipesDAO recipesDAO;
    private Recipe_PositionsDAO recipePositionsDAO;

    @Inject
    public RecipesServlet(RecipesDAO recipesDAO, Recipe_PositionsDAO recipePositionsDAO) {
        this.recipesDAO = recipesDAO;
        this.recipePositionsDAO = recipePositionsDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String typeParam = req.getParameter("type");
        if (typeParam.equals("id")) {
            String idParam = req.getParameter("id");
            int id = Integer.parseInt(idParam);
            Recipes recipe = recipesDAO.getId(id);
            String json = new Gson().toJson(recipe);
            resp.setContentType("application/json");
            resp.getWriter().write(json);
        }
        else if (typeParam.equals("all")) {
            int page = Integer.parseInt(req.getParameter("page"));
            List<Recipes> recipes = recipesDAO.getAll(page);
            String json = new Gson().toJson(recipes);
            resp.setContentType("application/json");
            resp.getWriter().write(json);
        }
        else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException { 
        String action = req.getParameter("action");
        Gson gson = new Gson();

        if ("search".equals(action)) {
            Type listType = new TypeToken<List<Recipe_Positions>>(){}.getType();
            List<Recipe_Positions> searchPositions = gson.fromJson(req.getReader(), listType);
            List<Recipes> foundRecipes = recipesDAO.findMatchingRecipes(searchPositions);
            resp.getWriter().write(gson.toJson(foundRecipes));
        } else {
            Recipes recipes = new Gson().fromJson(req.getReader(), Recipes.class);
            recipesDAO.add(recipes);
            int new_id = recipesDAO.getName(recipes.getDish_name());
            for (Recipe_Positions pos : recipes.getRecipe_positions()) {
                pos.setIngredient(new Ingredients(pos.getIngredient_id(), null, null, null));
                pos.setRecipe(recipes);
                pos.getRecipe().setId(new_id);
                recipePositionsDAO.add(pos);
            }
            resp.setStatus(HttpServletResponse.SC_CREATED);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Recipes recipes = new Gson().fromJson(req.getReader(), Recipes.class);
        boolean success = recipesDAO.update(recipes);
        for (Recipe_Positions pos : recipes.getRecipe_positions()) {
            pos.setIngredient(new Ingredients(pos.getIngredient_id(), null, null, null));
            recipePositionsDAO.update(pos);
        }
        resp.setStatus(success ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        boolean success = recipesDAO.delete(id);
        resp.setStatus(success ? HttpServletResponse.SC_NO_CONTENT : HttpServletResponse.SC_NOT_FOUND);
    }
}