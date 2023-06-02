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
import java.util.Enumeration;
import java.util.List;

@WebServlet("/income")
public class IncomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        request.
        System.out.println("get income");

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        System.out.println("HELP");
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            for (int i = 0; i < paramValues.length; i++) {
                String paramValue = paramValues[i];
                System.out.println(paramName + " = " + paramValue);
            }
        }
        String market = request.getParameter("action");
        if(market==null&&(user.getMarkets().get(0)!=null)){
            market = user.getMarkets().get(0);
        }
        System.out.println("market");
        request.setAttribute("currentMarket", market);

        // stock code, 12 months

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
        //https://www.javatpoint.com/pagination-in-servlet
        //https://www.geeksforgeeks.org/servlet-pagination-with-example/


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


        int noOfRecords = userstocks.size();
        int noOfPages = (int)Math.ceil(noOfRecords * 1.0
                / recordsPerPage);
        System.out.print("noofPages");
        System.out.println(noOfPages);


        request.setAttribute("noOfPages", noOfPages);
        request.setAttribute("currentPage", page);

        request.setAttribute("stockList", stocksForPage);

        // total
        double[] total = new double[stocksForPage.size()];


        // 12 months

        Dividend[][] divTable = new Dividend[stocksForPage.size()][12];
        ArrayList<String> dividendList = new ArrayList<>();
        for (int i=0; i< stocksForPage.size();i++){
            Stock s = stocksForPage.get(i);
            for (Dividend d:s.getDividends()){
                d.setDivType("past");
                divTable[i][d.getMonth()] = d;
                total[i] += d.getDivPrice()*s.getHoldings();
            }

            // if have expected dividend
            if(s.getPayDiv() != null){
                s.getPayDiv().setDivType("upcoming");
                divTable[i][s.getPayDiv().getMonth()] = s.getPayDiv();
                total[i] += s.getPayDiv().getDivPrice()*s.getHoldings();
                s.setLastDiv(new Dividend(s.getPayDiv()));
            }

            if (s.getLastDiv() != null) {
                int upcomingMonth = s.getLastDiv().getMonth() + s.getGap();
                if(s.getLastDiv().getYear()==Year.now().getValue()){
                    // a month = max 11 min 0
                    while (upcomingMonth<=11){
                        s.getLastDiv().setDivType("estimated");
                        divTable[i][upcomingMonth] = s.getLastDiv();
                        total[i] += s.getLastDiv().getDivPrice()*s.getHoldings();
                        upcomingMonth += s.getGap();
                    }
                }else {
                    upcomingMonth -= 12;
                    while (upcomingMonth<=11){
                        if (upcomingMonth >=0){
                            s.getLastDiv().setDivType("estimated");
                            divTable[i][upcomingMonth] = s.getLastDiv();
                            total[i] += s.getLastDiv().getDivPrice()*s.getHoldings();
                        }
                        upcomingMonth += s.getGap();
                    }
                }

            }

        }

        request.setAttribute("divTable", divTable);
        request.setAttribute("total", total);


        // TESTING - PRINT TABLE
        for (Dividend[] x : divTable)
        {
            for (Dividend y : x)
            {
                if(y==null){
                    System.out.print("- ");
                }else{
                    System.out.print(y.getDivPrice() + y.getDivType()+" ");
                }
            }
            System.out.println();
        }



        request.getRequestDispatcher("/income.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}