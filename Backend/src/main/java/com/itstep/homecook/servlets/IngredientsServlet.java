package com.itstep.homecook.servlets;

import com.google.gson.Gson;
import com.google.inject.Singleton;
import com.itstep.homecook.data.dao.IngredientsDAO;
import com.itstep.homecook.data.dto.Ingredients;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Singleton
public class IngredientsServlet extends HttpServlet {

    private IngredientsDAO ingredientsDAO;

    @Inject
    public IngredientsServlet(IngredientsDAO ingredientsDAO) {
        this.ingredientsDAO = ingredientsDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Ingredients> ingredients = ingredientsDAO.getAll();
        String json = new Gson().toJson(ingredients);
        resp.setContentType("application/json");
        resp.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Ingredients ingredient = new Gson().fromJson(req.getReader(), Ingredients.class);
        ingredientsDAO.add(ingredient);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Ingredients ingredient = new Gson().fromJson(req.getReader(), Ingredients.class);
        int id = Integer.parseInt(req.getParameter("id"));
        if (ingredient.getId() == id) {
            boolean success = ingredientsDAO.update(ingredient);
            resp.setStatus(success ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND);
        }
        else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        boolean success = ingredientsDAO.delete(id);
        resp.setStatus(success ? HttpServletResponse.SC_NO_CONTENT : HttpServletResponse.SC_NOT_FOUND);
    }
}