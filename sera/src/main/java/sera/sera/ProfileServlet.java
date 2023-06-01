package sera.sera;


import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("profile");
        if (request.getParameter("selectCur")==null){
            System.out.println("null");
            response.sendRedirect(request.getContextPath()+"/profile.jsp");
        }else{
            System.out.println(request.getParameter("selectCur"));
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute("user");
            try {
                user.setDefaultCurrency(request.getParameter("selectCur"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }


            // reset everything api everything again

            RequestDispatcher rd = request.getRequestDispatcher("home");
            request.setAttribute("update", "currency");
            rd.forward(request, response);
        }




        // or
//        request.getRequestDispatcher("/portfolio/.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
