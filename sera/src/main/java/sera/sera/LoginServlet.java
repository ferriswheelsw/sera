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
    public static JsonObject fetchCurrencyAPI(User user, HttpSession session) throws IOException {
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
        return j;
    }
    public static void fetchStockAPI(User user, JsonObject j) throws IOException {
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

            // TESTING
            System.out.println(" ");
            System.out.println("TESTING - price ver 1" + s.getStockCode());
            for (HistoricalDividend d: dividendList){
                printCal(d.getDate());
                System.out.println(d.getAdjDividend().doubleValue()/er);
            }
            System.out.println(" ");



            int exdivToPay = 0;

            if(dividendList.size()>0){

                if (dividendList.size()==1){
                    s.setGap(12);
                }else{
                    Calendar start = dividendList.get(dividendList.size()-2).getDate();
                    Calendar end = dividendList.get(dividendList.size()-1).getDate();
                    printCal(start);
                    printCal(end);
                    LocalDate lstart = LocalDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault()).toLocalDate();
                    LocalDate lend = LocalDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault()).toLocalDate();
                    int sgap = (int) Period.between(lstart,lend).toTotalMonths();

                    if (Period.between(lstart,lend).getDays()>=15){
                        sgap++;
                    }

                    System.out.println("sgap");
                    System.out.println(sgap);

                    s.setGap(sgap);
                }
                double divprice = dividendList.get(dividendList.size()-1).getAdjDividend().doubleValue()/er;
                System.out.println("price2");
                System.out.println(divprice);
                Calendar date = dividendList.get(dividendList.size()-1).getDate();
                System.out.println(date.get(Calendar.MONTH));

                if (stock.getDividend().getPayDate()!=null){
                    System.out.println("printingtesting");
                    System.out.println(stock.getDividend().getPayDate().get(Calendar.MONTH));
                    System.out.println(date.get(Calendar.MONTH));
                    System.out.println(s.getGap());
                    if (stock.getDividend().getPayDate().get(Calendar.MONTH) - (date.get(Calendar.MONTH)+s.getGap())>0){
                        exdivToPay = stock.getDividend().getPayDate().get(Calendar.MONTH) - (date.get(Calendar.MONTH)+s.getGap());
                    }else if (stock.getDividend().getPayDate().get(Calendar.MONTH)-(date.get(Calendar.MONTH))<s.getGap()){
                        exdivToPay = stock.getDividend().getPayDate().get(Calendar.MONTH)-(date.get(Calendar.MONTH));
                    }
                    System.out.println("exdivtopay");
                    System.out.println(exdivToPay);
                }

                System.out.println("STOCKK" + s.getStockCode());
                System.out.println(date.get(Calendar.MONTH));
//                        date.add(Calendar.MONTH, exdivToPay);
                System.out.println(date.get(Calendar.MONTH));
                s.setLastDiv(new Dividend(date.get(Calendar.MONTH)+exdivToPay, date.get(Calendar.YEAR), date.get(Calendar.DATE), s.getStockCode(), divprice));

                //expected div
                Calendar payDate = stock.getDividend().getPayDate();
                System.out.println("PAYPAY");

                if (payDate != null && (Calendar.getInstance().compareTo(payDate)==-1)){
                    System.out.println("AYY");
                    printCal(payDate);
                    s.setPayDiv(new Dividend(payDate.get(Calendar.MONTH), payDate.get(Calendar.YEAR), payDate.get(Calendar.DATE), s.getStockCode(), divprice));
                }else if (payDate!=null){
                    System.out.println("beep");
                    printCal(payDate);
                    s.setLastDiv(new Dividend(payDate.get(Calendar.MONTH), payDate.get(Calendar.YEAR), payDate.get(Calendar.DATE), s.getStockCode(), divprice));
                }

            }



            ArrayList<Dividend> dividends = new ArrayList<>();
            for (HistoricalDividend d : dividendList){
                System.out.println("hihihsihsd "+s.getStockCode());
                printCal(d.getDate());
                double divprice = d.getAdjDividend().doubleValue()/er;
                System.out.println("price ver 3");
                System.out.println(divprice);
                Calendar date = d.getDate();

                //testing
                System.out.println("dividend");
                System.out.println(date.get(Calendar.YEAR)+" "+(date.get(Calendar.MONTH))+" "+date.get(Calendar.DATE));

                // checking if the dividend was after Jan 1st of this year
                if (date.get(Calendar.YEAR) == (Year.now().getValue())) {
                    System.out.println(date.get(Calendar.YEAR));
                    System.out.println(Year.now().getValue());
                    printCal(date);
                    date.add(Calendar.MONTH, exdivToPay);
                    System.out.println("lcheck");
                    printCal(date);
                    dividends.add(new Dividend(date.get(Calendar.MONTH), date.get(Calendar.YEAR), date.get(Calendar.DATE), s.getStockCode(), divprice));
                }
            }
            s.setDividends(dividends);

        }

    }
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
                user.updatestocks();
                HttpSession session = request.getSession();
                JsonObject j = fetchCurrencyAPI(user, session);
                fetchStockAPI(user, j);

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
                response.sendRedirect("login.jsp");
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


    }

    public static void printCal(Calendar cal) {
        System.out.println(Integer.toString(cal.get(1)) + " " + Integer.toString(cal.get(2)) + " " + Integer.toString(cal.get(5)));
    }
}

