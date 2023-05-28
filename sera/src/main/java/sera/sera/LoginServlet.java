package sera.sera;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes2.HistoricalDividend;

import static sera.sera.UserDB.login;

import com.google.gson.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

@WebServlet("/home")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("hi");
        HttpSession session = request.getSession();
        User got = (User) session.getAttribute("user");
        String name = got.getFirstName();
        request.setAttribute("name", name);
        request.setAttribute("stockdivfreq", got.getStocks().get(0).getDivfreq());
        request.getRequestDispatcher("/home.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String pw = request.getParameter("pw");

        //email("sonja.hinting@gmail.com", "hi", "hi");


        try {
            User user = login(email, pw);

            if (user!=null){
                System.out.println("OMG");
                user.updatestocks();
                HttpSession session = request.getSession();

                //testing
                System.out.println(user.getStocks().get(0).getStockCode());

                // API - currency


                String url_str = "https://v6.exchangerate-api.com/v6/0e01e6c4ed84843e593f49ff/latest/"+user.getDefaultCurrency();

// Making Request
                URL url = new URL(url_str);
                HttpURLConnection request1 = (HttpURLConnection) url.openConnection();
                request1.connect();

// Convert to JSON
                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request1.getContent()));
                JsonObject jsonobj = root.getAsJsonObject();

// Accessing object
//        String req_result = jsonobj.get("conversion_rates").getAsString();
//        for (int i = 0; i < req_result.size(); i++) {
//            JsonElement a = req_result.get(i);
//            String hi = a.getAsString();
//            System.out.println(hi);
//        }
                JsonObject j = (JsonObject) jsonobj.get("conversion_rates");
                Set<String> keyset = j.keySet();
                Iterator<String> keys = keyset.iterator();
                while(keys.hasNext()) {
                    String key = keys.next();
                    Double value = j.get(key).getAsDouble();
                    System.out.println(key + " : " + value);
                    if (key.equals(user.getDefaultCurrency())){
                        session.setAttribute("exRate", value);
                    }
                }





                // API - stock - currently only have symbol market holding for a stock
                // i need market price, dividends, avgdiv, totaldiv, divfreq, price change, pnl

                for (Stock s : user.getStocks()){
                    yahoofinance.Stock stock = YahooFinance.get(s.getStockCode());
                    double er = j.get(stock.getCurrency()).getAsDouble();
                    double price = stock.getQuote().getPrice().doubleValue();
                    System.out.println(price);
                    System.out.println("price");
                    double change = stock.getQuote().getChangeInPercent().doubleValue();
                    System.out.println(change);
                    System.out.println("change");
                    double prevclosing = stock.getQuote().getPreviousClose().doubleValue();
                    System.out.println(prevclosing);
                    System.out.println("prevclosing");
                    double percentdiv = 0;
                    double totaldiv = 0;
                    if (stock.getDividend().getAnnualYield()!= null){
                        percentdiv = stock.getDividend().getAnnualYieldPercent().doubleValue();
                        System.out.println(percentdiv);
                        System.out.println("percentdiv");
                        totaldiv = stock.getDividend().getAnnualYield().doubleValue()*(double)s.getHoldings()/er;
                        System.out.println(totaldiv);
                        System.out.println("totaldiv");
                    }
                    String stockCur = stock.getCurrency();
                    System.out.print("Currency: ");
                    System.out.println(stockCur);

                    s.setMarketPrice(price);
                    s.setPriceChange(change);
                    s.setPnl((price-prevclosing)*s.getHoldings());
                    s.setPercentdiv(percentdiv);
                    s.setTotaldiv(totaldiv);
                    s.setStockCur(stockCur);

                    // dividends in past 1Y
                    Calendar cal = Calendar.getInstance();
//                    cal.set(Calendar.MONTH, 0);
                    cal.add(Calendar.YEAR,-1);

                    List<HistoricalDividend> dividendList = stock.getDividendHistory(cal);
                    System.out.println(dividendList.size());
                    s.setDivfreq(dividendList.size());
                    if(dividendList.size()>0){
                        double divprice = dividendList.get(dividendList.size()-1).getAdjDividend().doubleValue()/er;
                        Calendar date = dividendList.get(dividendList.size()-1).getDate();
                        s.setLastDiv(new Dividend(date.get(Calendar.MONTH), date.get(Calendar.YEAR), date.get(Calendar.DATE), s.getStockCode(), divprice));
                        if (dividendList.size()==1){
                            s.setGap(12);
                        }else{
                            Calendar start = dividendList.get(dividendList.size()-2).getDate();
                            Calendar end = dividendList.get(dividendList.size()-1).getDate();
                            LocalDate lstart = LocalDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault()).toLocalDate();
                            LocalDate lend = LocalDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault()).toLocalDate();
                            int sgap = Period.between(lstart,lend).getMonths();
                            System.out.println("sgap");
                            System.out.println(sgap);
                            s.setGap(sgap);
                        }
                    }

                    ArrayList<Dividend> dividends = new ArrayList<>();
                    for (HistoricalDividend d : dividendList){
                        double divprice = d.getAdjDividend().doubleValue()/er;
                        Calendar date = d.getDate();

                        //testing
                        System.out.println("dividend");
                        System.out.println(date.get(Calendar.YEAR)+" "+(date.get(Calendar.MONTH)+1)+" "+date.get(Calendar.DATE));

                        // checking if the dividend was after Jan 1st of this year
                        if (date.get(Calendar.YEAR) == (Year.now().getValue())) {
                            System.out.println(date.get(Calendar.YEAR));
                            System.out.println(Year.now().getValue());
                            dividends.add(new Dividend(date.get(Calendar.MONTH), date.get(Calendar.YEAR), date.get(Calendar.DATE), s.getStockCode(), divprice));
                        }
                    }
                    s.setDividends(dividends);

                }



                session.setAttribute("user",user);
                User got = (User) session.getAttribute("user");
                String name = got.getFirstName();
                System.out.println(name);
                System.out.println(session.getAttribute("exRate"));
                request.setAttribute("name", name);
                request.setAttribute("stockdivfreq", got.getStocks().get(0).getDivfreq());
                request.getRequestDispatcher("/home.jsp").forward(request, response);

            }
            else{
                System.out.println("fail");
            }


        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
//
//        try {

//            Class.forName("com.mysql.jdbc.Driver");
//            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/learndb", "root", "Appletree1!");
//            Statement stmt = con.createStatement();
//            //Query to get the number of rows in a table
//            String query = "select password from learndb.users where id = " + uid;
//
//            PrintWriter out = response.getWriter();
//            out.println("<html><body>");
//
//            //Executing the query
//            ResultSet rs1 = stmt.executeQuery(query);
//            rs1.next();
//
//
//            String realpw = rs1.getString(1);
//            out.println("<h1>" + pw + "</h1>");
//            out.println("<h1>" + realpw + "</h1>");
//
//            if (realpw.equals(pw)){
//                out.println("<h1>" + "success" + "</h1>");
//            }else{
//                out.println("<h1>" + "wrong username or password" + "</h1>");
//            }
//
//            out.println("</body></html>");
//            con.close();
//
//
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//
//        PrintWriter out = response.getWriter();
//        out.println("<html><body><b>Successfully Run"
//                + "</b></body></html>");




//        request.setAttribute("yes", "yayayaya");
//        request.getRequestDispatcher("editsuccess.jsp").forward(request, response);

    }

//    private String message;
//
//    public void init() {
//        message = "Login";
//    }
//
//    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("text/html");
//
//        // Hello
//        PrintWriter out = response.getWriter();
//        out.println("<html><body>");
//        out.println("<h1>" + message + "</h1>");
//        out.println("</body></html>");
//    }
//
//    public void destroy() {
//    }

}