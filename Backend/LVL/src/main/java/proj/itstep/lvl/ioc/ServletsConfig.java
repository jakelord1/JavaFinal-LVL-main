package java.itstep.LVL.ioc;

/**
 *
 * @author pronc
 */
import com.google.inject.servlet.ServletModule;
import java.itstep.LVL.servlets.HomeServlet;
import java.itstep.LVL.servlets.UserServlet;


public class ServletsConfig extends ServletModule {

    @Override
    protected void configureServlets() {
        serve("/").with(HomeServlet.class);
        serve("/user").with(UserServlet.class);
    }
}
