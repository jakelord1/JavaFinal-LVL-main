package proj.itstep.lvl.ioc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class IocContextListener extends GuiceServletContextListener { 
    @Override
    protected Injector getInjector() {
        System.out.println("IocContextListener::getInjector");
        return Guice.createInjector(
                new ServicesConfig(),
                new ServletsConfig()
        );
    }
}
