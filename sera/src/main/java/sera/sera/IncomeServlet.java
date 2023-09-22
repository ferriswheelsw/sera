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

        // keep track of which page is requested by browser
        int page = 1;
        // variable to store max no. of stocks displayed per page
        int recordsPerPage = 10;
        // if user requested any specific page - set variable page to that page
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }
        // create a list to store the 1-10 stocks for that page
        List<Stock> stocksForPage =  new ArrayList<>();

        // use a for loop to add the required stocks for this page from the user's full list of stocks
        for (int i=(page - 1) * recordsPerPage;i<page*recordsPerPage;i++){
            // if the page doesn't have 10 stocks (reached end of the full list of stocks) the loop ends early
            if (i>=userstocks.size()){
                break;
            }
            // otherwise add the stock to the list for this page
            stocksForPage.add(userstocks.get(i));
        }

        // store the total number of stocks user has (for this market)
        int noOfRecords = userstocks.size();
        // store the number of pages available (always round up)
        int noOfPages = (int)Math.ceil(noOfRecords * 1.0 / recordsPerPage);

        // set as attributes in the request - which will be used in jsp file to display the correct stocks and buttons
        request.setAttribute("noOfPages", noOfPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("stockList", stocksForPage);




        // display the income table (2D array) and sort into past, upcoming, estimated dividends
        Dividend[][] divTable = new Dividend[stocksForPage.size()][12];
        ArrayList<String> dividendList = new ArrayList<>();
        // total - store total income earned per stock
        double[] total = new double[stocksForPage.size()];

        for (int i=0; i< stocksForPage.size();i++){
            Stock s = stocksForPage.get(i);
            // past dividends (stored in dividends list in session)
            for (Dividend d:s.getDividends()){
                d.setDivType("past");
                // fill in 2D array with the dividend object (can retrieve price through getter)
                divTable[i][d.getMonth()] = d;
                // update sum to calculate total income this year per stock
                total[i] += d.getDivPrice()*s.getHoldings();
            }



            // expected dividend this year - update arrays in similar fashion as above
            if(s.getPayDiv() != null && (s.getPayDiv().getYear()==Year.now().getValue())){
                s.getPayDiv().setDivType("upcoming");
                divTable[i][s.getPayDiv().getMonth()] = s.getPayDiv();
                total[i] += s.getPayDiv().getDivPrice()*s.getHoldings();
                // set to last div for prediction purposes - to predict the next unconfirmed paydate
                s.setLastDiv(new Dividend(s.getPayDiv()));
            }

            // estimate dividends
            if (s.getLastDiv() != null) {
                // variable to store next estimated month (make sure don't go past december of current year)
                int upcomingMonth = s.getLastDiv().getMonth() + s.getGap();
                // if the previous dividend was released this year
                if(s.getLastDiv().getYear()==Year.now().getValue()){
                    // loop until goes past december of current year
                    while (upcomingMonth<=11){
                        s.getLastDiv().setDivType("estimated");
                        divTable[i][upcomingMonth] = s.getLastDiv();
                        total[i] += s.getLastDiv().getDivPrice()*s.getHoldings();
                        upcomingMonth += s.getGap();
                    }
                }
                // if the previous dividend was released last year
                else if (Year.now().getValue()-s.getLastDiv().getYear()==1) {
                    // since the calculation starts from a date last year - must start the loop 12 months behind
                    upcomingMonth -= 12;
                    while (upcomingMonth<=11){
                        // make sure the estimations are for this year
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