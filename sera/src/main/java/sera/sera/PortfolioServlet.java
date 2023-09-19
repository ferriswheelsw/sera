package sera.sera;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/portfolio")
public class PortfolioServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");


        String market = request.getParameter("action");
        if(market==null&&(user.getMarkets().get(0)!=null)){
            market = user.getMarkets().get(0);
        }
        System.out.println("market");
        request.setAttribute("currentMarket", market);

        // stock code, current market price, %1D, 1DPnL. %div, 1Ydiv, divfreq, quantitty

        List<Stock> userstocks =  new ArrayList<>();
        double totalIncome = 0.0;
        for (int i=0;i<user.getStocks().size();i++){
            System.out.println(market);
            if (user.getStocks().get(i).getMarket().equals(market)){
                System.out.println(i);
                System.out.println(user.getStocks().get(i).getStockCode());
                System.out.println(user.getStocks().get(i).getMarket());
                userstocks.add(user.getStocks().get(i));
                totalIncome += user.getStocks().get(i).getTotaldiv();
            }
        }
        System.out.println("size");
        System.out.println(userstocks.size());

        request.setAttribute("totalIncome", String.format("%.2f", totalIncome));
        request.setAttribute("stockList", userstocks);

        System.out.println(((ArrayList<Stock>)request.getAttribute("stockList")).get(0).getStockCode());
        System.out.println(request.getAttribute("currentMarket"));
        request.getRequestDispatcher("/portfolio.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}