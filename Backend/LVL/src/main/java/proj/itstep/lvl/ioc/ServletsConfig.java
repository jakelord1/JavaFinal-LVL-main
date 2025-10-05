package proj.itstep.lvl.ioc;

/**
 *
 * @author pronc
 */
import com.google.inject.servlet.ServletModule;
import proj.itstep.lvl.servlets.HomeServlet;
import proj.itstep.lvl.servlets.UserServlet;
import proj.itstep.lvl.filter.CorsFilter;


public class ServletsConfig extends ServletModule {

    @Override
    protected void configureServlets() {
        filter("/*").through(CorsFilter.class);
        serve("/").with(HomeServlet.class);
        serve("/user").with(UserServlet.class);
    }
}
