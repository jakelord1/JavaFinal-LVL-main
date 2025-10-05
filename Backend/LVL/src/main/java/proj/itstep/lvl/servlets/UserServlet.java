/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proj.itstep.lvl.servlets;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import proj.itstep.lvl.data.DataAccessor;
import java.util.UUID;
import proj.itstep.lvl.data.dto.User;
import proj.itstep.lvl.data.dto.UserAccess;
import proj.itstep.lvl.data.dto.AccessToken;
import proj.itstep.lvl.data.jwt.JwtToken;
import java.util.Base64;

@Singleton
public class UserServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final DataAccessor dataAccessor;

    @Inject
    public UserServlet(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authHeader = req.getHeader("Authorization");  // Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ== 
        if (authHeader == null || "".equals(authHeader)) { // Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
            resp.setStatus(401);
            resp.getWriter().print(
                    gson.toJson("Missing 'Authorization' header"));
            return;
        }

        String authScheme = "Basic ";
        if (!authHeader.startsWith(authScheme)) {
            resp.setStatus(401);
            resp.getWriter().print(
                    gson.toJson("Invalid 'Authorization' scheme. Must be " + authScheme));
            return;
        }

        String credentials = authHeader.substring(authScheme.length()); // QWxhZGRpbjpvcGVuIHNlc2FtZQ==
        String userPass;
        try {
            userPass = new String(Base64.getDecoder().decode(credentials));
        } catch (IllegalArgumentException ex) {
            resp.setStatus(401);
            resp.getWriter().print(
                    gson.toJson("Invalid credentials. Base64 decode error " + ex.getMessage()));
            return;
        }
        String[] parts = userPass.split(":", 2);
        if (parts.length != 2) {
            resp.setStatus(401);
            resp.getWriter().print(
                    gson.toJson("Invalid user-pass. Missing symbol ':' "));
            return;
        }

        UserAccess ua = dataAccessor.getUserAccessByCredentials(parts[0], parts[1]);
        if (ua == null) {
            resp.setStatus(401);
            resp.getWriter().print(
                    gson.toJson("Credentials rejected. Access denied"));
            return;
        }
        AccessToken at = dataAccessor.getTokenByUserAccess(ua);
        JwtToken jwt = JwtToken.fromAccessToken(at);
        resp.setHeader("Content-Type", "application/json");
        resp.getWriter().print(
                gson.toJson(jwt)
        );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().print(
                gson.toJson("POST works")
        );
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-Type", "application/json");
        resp.getWriter().print(
                gson.toJson("PUT works")
        );
    }

    
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-Type", "application/json");
        resp.getWriter().print(gson.toJson("PATCH works"));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-Type", "application/json");
        resp.getWriter().print(gson.toJson("DELETE works"));
    }
}
