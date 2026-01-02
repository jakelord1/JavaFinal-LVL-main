package com.itstep.homecook.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.itstep.homecook.data.dao.DataAccesor;
import com.itstep.homecook.data.dao.IngredientsDAO;
import com.itstep.homecook.data.dao.Recipe_PositionsDAO;
import com.itstep.homecook.data.dao.RecipesDAO;
import com.itstep.homecook.services.config.ConfigService;
import com.itstep.homecook.services.config.JsonConfigService;
import java.sql.Connection;
import java.sql.SQLException;

public class ServicesConfig extends AbstractModule {
    @Override
    protected void configure() {
        bind(ConfigService.class).to(JsonConfigService.class).asEagerSingleton();
        bind(IngredientsDAO.class).in(Singleton.class);
        bind(RecipesDAO.class).in(Singleton.class);
        bind(Recipe_PositionsDAO.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    Connection provideConnection() {
        try {
            return DataAccesor.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to provide database connection", e);
        }
    }
}
