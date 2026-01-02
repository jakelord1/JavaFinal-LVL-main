package com.itstep.homecook.ioc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import jakarta.servlet.ServletContextEvent;

public class IocContextListener extends GuiceServletContextListener { 
    private Injector injector;
    
    @Override
    protected Injector getInjector() {
        System.out.println("IocContextListener::getInjector");
        try {
            return Guice.createInjector(
                    new ServicesConfig(),
                    new ServletsConfig()
            );
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (injector != null) {
            injector = null;
        }
        super.contextDestroyed(servletContextEvent);
    }

}
