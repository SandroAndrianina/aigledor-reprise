package servlet;

import synchronisation.Synchronisation;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/SynchronisationServlet")
public class SynchronisationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Synchronisation.execute();
            response.getWriter().write("{\"success\": true, \"message\": \"La synchronisation s'est d&eacute;roul&eacute;e avec succ&egrave;s.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage();
            if (msg == null || msg.trim().isEmpty()) {
                msg = "Une erreur est survenue lors de la synchronisation.";
            }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String escapedMsg = msg.replace("\"", "\\\"");
            response.getWriter().write("{\"success\": false, \"message\": \"" + escapedMsg + "\"}");
        }
    }
}
