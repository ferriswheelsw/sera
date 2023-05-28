package sera.sera;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@WebServlet("/income")
public class IncomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        request.
        System.out.println("get income");
        String market = request.getParameter("selectM");
        if (market == null){
            market = "NasdaqGS";
        }
        System.out.println("market");
        request.setAttribute("currentMarket", market);

        // stock code, 12 months
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        List<Stock> userstocks =  new ArrayList<>();
        for (int i=0;i<user.getStocks().size();i++){
            System.out.println(market);
            if (user.getStocks().get(i).getMarket().equals(market)){
                System.out.println(i);
                System.out.println(user.getStocks().get(i).getStockCode());
                System.out.println(user.getStocks().get(i).getMarket());
                userstocks.add(user.getStocks().get(i));
            }
        }
        System.out.println("size");
        System.out.println(userstocks.size());

        // PAGINATION

        int page = 1;
        int recordsPerPage = 5;
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(
                    request.getParameter("page"));
            System.out.print("Page ");
            System.out.println(page);
        }
        List<Stock> stocksForPage =  new ArrayList<>();

        for (int i=(page - 1) * recordsPerPage;i<page*recordsPerPage;i++){
            if (i>=userstocks.size()){
                System.out.println("break");
                break;
            }
            System.out.println(i);
            stocksForPage.add(userstocks.get(i));
        }


//        List<Stock> list = Stock.viewAllEmployees(
//                (page - 1) * recordsPerPage,
//                recordsPerPage);
        int noOfRecords = userstocks.size();
        int noOfPages = (int)Math.ceil(noOfRecords * 1.0
                / recordsPerPage);
        System.out.print("noofPages");
        System.out.println(noOfPages);


        request.setAttribute("noOfPages", noOfPages);
        request.setAttribute("currentPage", page);

        request.setAttribute("stockList", stocksForPage);

        // 12 months

        double[][] incomeTable = new double[stocksForPage.size()][13];
        ArrayList<String> dividends = new ArrayList<>();
        for (int i=0; i< stocksForPage.size();i++){
            Stock s = stocksForPage.get(i);
            for (Dividend d:s.getDividends()){
                System.out.println("hi");
                incomeTable[i][d.getMonth()+1] = d.getDivPrice()*s.getHoldings()*(double)session.getAttribute("exRate");
            }
            if (s.getLastDiv() != null) {
                int upcomingMonth = s.getLastDiv().getMonth() + s.getGap();
                if(s.getLastDiv().getYear()==Year.now().getValue()){
                    // a month = max 11 min 0
                    while (upcomingMonth<=11){
                        incomeTable[i][upcomingMonth+1] = s.getLastDiv().getDivPrice()*s.getHoldings()*(double)session.getAttribute("exRate");
                        upcomingMonth += s.getGap();
                    }
                }else {
                    upcomingMonth -= 12;
                    while (upcomingMonth<=11){
                        if (upcomingMonth >=0){
                            incomeTable[i][upcomingMonth+1] = s.getLastDiv().getDivPrice()*s.getHoldings()*(double)session.getAttribute("exRate");
                        }
                        upcomingMonth += s.getGap();
                    }
                }

            }

        }


        request.setAttribute("incomeTable", incomeTable);

        // TESTING - PRINT TABLE
        for (double[] x : incomeTable)
        {
            for (double y : x)
            {
                System.out.print(y + " ");
            }
            System.out.println();
        }



        //System.out.println(((ArrayList<Stock>)request.getAttribute("stockList")).get(0).getStockCode());
        request.getRequestDispatcher("/income.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}