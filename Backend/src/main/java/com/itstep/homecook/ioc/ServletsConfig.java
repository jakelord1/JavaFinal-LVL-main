package com.itstep.homecook.ioc;

import com.google.inject.servlet.ServletModule;
import com.itstep.homecook.servlets.Recipe_PositionsServlet;
import com.itstep.homecook.servlets.RecipesServlet;
import com.itstep.homecook.servlets.IngredientsServlet;

public class ServletsConfig extends ServletModule {

    @Override
    protected void configureServlets() {
        serve("/ingredients").with(IngredientsServlet.class);
        serve("/recipes").with(RecipesServlet.class);
        serve("/recipe-positions").with(Recipe_PositionsServlet.class);

    }
}