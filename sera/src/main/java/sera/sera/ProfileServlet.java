package sera.sera;


import com.google.gson.JsonObject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.SQLException;

import static sera.sera.LoginServlet.fetchCurrencyAPI;
import static sera.sera.LoginServlet.fetchStockAPI;

@WebServlet("/profile")
@MultipartConfig
public class ProfileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // for either select currency OR coming here from other pages
        System.out.println("profile");
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (request.getParameter("selectCur") == null) {
            // coming here from other pages
            response.sendRedirect(request.getContextPath() + "/profile.jsp");
        } else {
            // select currency
            System.out.println(request.getParameter("selectCur"));
            try {
                user.setDefaultCurrency(request.getParameter("selectCur"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            try {
                user.updatestocks();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            JsonObject j = fetchCurrencyAPI(user, session);
            fetchStockAPI(user, j);
//            response.sendRedirect(request.getContextPath() + "/profile.jsp");
            // more on sendirect vs request dispatcher
            // https://www.geeksforgeeks.org/servlet-collaboration-java-using-requestdispatcher-httpservletresponse/?ref=rp
            request.setAttribute("message", "Currency change success!");
            request.getRequestDispatcher("/profile.jsp").forward(request, response);


        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // doPost only used for update CSV
        // https://www.baeldung.com/upload-file-servlet
        System.out.println("profile!");
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        // csv file is fetched from http request
        if (request.getPart("file") == null) {
            System.out.println("no file");
        } else {
            System.out.println("file");
            // Fetch <input type="file" name="file">
            Part filePart = request.getPart("file");
            InputStream fileContent = filePart.getInputStream();
            // call upload csv method to update database
            try {
                user.uploadCSV(fileContent);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // update user stocks in session with new stocks stored in database
            try {
                user.updatestocks();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // fetch data from APIs again for updated stock list
            JsonObject j = fetchCurrencyAPI(user, session);
            fetchStockAPI(user, j);
            // send success message back
            request.setAttribute("message", "Portfolio update success!");
            request.getRequestDispatcher("/profile.jsp").forward(request, response);
        }
    }
}