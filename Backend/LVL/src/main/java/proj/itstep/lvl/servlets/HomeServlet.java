/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proj.itstep.lvl.servlets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import proj.itstep.lvl.data.DataAccessor;
import proj.itstep.lvl.services.kdf.KdfService;
import proj.itstep.lvl.services.timestamp.TimestampService;

@Singleton
public class HomeServlet extends HttpServlet {

    private final KdfService kdfService;
    private final TimestampService timestampService;
    private final DataAccessor dataAccessor;

    @Inject
    public HomeServlet(KdfService kdfService, TimestampService timestampService, DataAccessor dataAccessor) {
        this.kdfService = kdfService;
        this.timestampService = timestampService;
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("HomeServlet", "Hello from HomeServlet "
                + kdfService.dk("123", "")
                + "\n"
                + dataAccessor.getDbIdentity()
                + (dataAccessor.install() ? "\nInstall OK" : "\nInstall error" )
                + (dataAccessor.seed() ? "\nSeed OK" : "\nSeed error" )
        );
        req.setAttribute("UnixTimestampSeconds", String.valueOf(timestampService.nowSeconds()));
        LocalDateTime dbNow = dataAccessor.getDbTime();
        req.setAttribute("DbTime", dbNow != null ? dbNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "n/a");
        req.getRequestDispatcher("index.jsp").forward(req, resp);
    }
}
