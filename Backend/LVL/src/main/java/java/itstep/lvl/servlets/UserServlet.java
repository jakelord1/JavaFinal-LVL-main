/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package java.itstep.LVL.servlets;
import com.google.gson.Gson;
import com.google.inject.Singleton;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.itstep.LVL.data.dto.User;
import java.util.Base64;

@Singleton
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
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

        User user = new User()
                .setId(UUID.randomUUID())
                .setName("Pron V")
                .setEmail("pronchatov.vo");
        resp.setHeader("Content-Type", "application/json");
        resp.getWriter().print(gson.toJson(user));

    }
}
