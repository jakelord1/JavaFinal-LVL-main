/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proj.itstep.lvl.filter;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 *
 * @author pronc
 */
@Singleton
public class CorsFilter implements Filter {

    private final Logger logger;
    private FilterConfig filterConfig;

    @Inject
    public CorsFilter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        chain.doFilter(request, response);
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        // logger.log(Level.INFO, "CORS filter works");
        if ("OPTIONS".equals(req.getMethod())) {
            resp.setHeader("Access-Control-Allow-Headers", req.getHeader("Access-Control-Request-Headers"));
            resp.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,DELETE");
        }
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }
}

