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

// for mail
import java.io.UnsupportedEncodingException;
import java.util.Properties;


import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.mail.*;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import javax.sql.rowset.serial.SerialBlob;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        System.out.println("hihi");
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

    public static ArrayList<Stock> topFive(User user){
        ArrayList<Stock> unsorted = user.getStocks();
        ArrayList<Stock> sorted = new ArrayList<>();
        // selectionsort
        int count = 0;
        while(sorted.size()<5 && sorted.size()<unsorted.size()){
            System.out.println(sorted.size());
            if (sorted.size()>=1){
                for(Stock s : sorted){
                    System.out.printf(s.getStockCode() + " ");
                }
            }
            System.out.println("abcde");
            int maxindex = count;
            double max = unsorted.get(count).getPriceChange();
            for (int j=count;j<unsorted.size();j++){
                if (unsorted.get(j).getPriceChange()>max){
                    maxindex = j;
                    max = unsorted.get(j).getPriceChange();
                }
            }
            sorted.add(new Stock(unsorted.get(maxindex)));
            if (count!=maxindex){
                Stock temp = new Stock(unsorted.get(maxindex));
                unsorted.remove(maxindex);
                unsorted.add(count, temp);
            }
            count++;
        }
       return sorted;

    }
    public static void email(User user, String msg) throws AddressException {
        System.out.println("hi");
        // https://stackoverflow.com/questions/59069456/sending-an-email-using-gmail-through-java

        // Sender's email ID needs to be mentioned
        String from = "sonja.hinting@gmail.com";

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("sonja.hinting@gmail.com", "boieespsbebbrsso");
            }
        });

        // Used to debug SMTP issues
        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));

            // Set Subject: header field
            message.setSubject("SERA: Your dividend payment awaits you!");

            // Now set the actual message
            message.setText(msg);

            System.out.println("sending...");
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");

            //update database
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/seraschema", "root", "Appletree1!");

            // Update user_stock
            PreparedStatement st = con.prepareStatement("update seraschema.user_stock set emailStatus = ? where uid = ?");
            st.setString(1, "sent");
            st.setInt(2, user.getUserID());
            st.executeUpdate();

            // Close all the connections
            st.close();

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 7);

            //schedule email
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        PreparedStatement st = con.prepareStatement("update seraschema.user_stock set emailStatus = ? where uid = ?");
                        st.setString(1, "null");
                        st.setInt(2, user.getUserID());
                        st.executeUpdate();

                        // Close all the connections
                        st.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, c.getTime());
            con.close();


        } catch (MessagingException mex) {
            mex.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
        public static void scheduleEmail(User user) throws ClassNotFoundException, SQLException {
            // connection to database user_stock table
            // if reminder column is null and paydiv is not null then email
            // set reminder column to true
            // but when do i turn reminder column false
            // turn it off when email sent

            //Establish connection
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/seraschema", "root", "Appletree1!");
            Statement stmt = con.createStatement();

            //Query to get the number of rows in a table
            String query = "select * from seraschema.user_stock where uid = '" + user.getUserID() + "'";

            //Executing the query
            ResultSet rs1 = stmt.executeQuery(query);
            while (rs1.next()) {
                System.out.println(rs1.getString("symbol"));
                Stock s = null;
                //linear search
                for (Stock stock : user.getStocks()) {
                    if (stock.getStockCode().equals(rs1.getString("symbol"))) {
                        s = stock;
                    }
                }

                System.out.println(s.getStockCode());
                if (s.getPayDiv() != null) {
                    System.out.println(rs1.getString("emailStatus"));
                    if (Objects.equals(rs1.getString("emailStatus"), "sent")) {
                        System.out.println("sent");
                    }else if (Objects.equals(rs1.getString("emailStatus"), "scheduled")){
                        System.out.println("scheduled");
                    }else{
                        System.out.println("hi");
                        String message = "Stock code: " + s.getStockCode() + "\nHoldings: " + s.getHoldings() + "\nDividend price: " + s.getPayDiv().getDivPrice() + "\nPayment date: "+s.getPayDiv().getDay()+"/"+(s.getPayDiv().getMonth()+1)+"/"+s.getPayDiv().getYear();
                        Calendar c = Calendar.getInstance();
                        c.set(s.getPayDiv().getYear(), s.getPayDiv().getMonth(), s.getPayDiv().getDay());

                        // cal just for testing purpsoses  - c is the right one
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.MINUTE, 1);

                        Date currentTimePlusOneMinute = cal.getTime();
                        //schedule email
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    email(user, message);
                                } catch (AddressException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }, currentTimePlusOneMinute);
                        System.out.println("Scheduled email");

                        // Update user_stock
                        PreparedStatement st = con.prepareStatement("update seraschema.user_stock set emailStatus = ? where uid = ?");
                        st.setString(1, "scheduled");
                        st.setInt(2, user.getUserID());
                        st.executeUpdate();

                        // Close all the connections
                        st.close();

                    }

                }
            }
            con.close();
        }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User got = (User) session.getAttribute("user");
        request.setAttribute("topfive",topFive(got));
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
                scheduleEmail(user);
                // gettotalincome
                double totalincome = 0;
                double totalPerMonth = 0;
                for (Stock s: user.getStocks()){
                    totalincome += s.getTotaldiv();
                    if (s.getLastDiv()!=null &&(s.getLastDiv().getMonth()==(Calendar.getInstance().get(Calendar.MONTH)))){
                        totalPerMonth+=s.getLastDiv().getDivPrice();
                    }
                    if (s.getPayDiv()!=null && (s.getPayDiv().getMonth()==(Calendar.getInstance().get(Calendar.MONTH)))){
                        totalPerMonth+=s.getPayDiv().getDivPrice();
                    }

                }

                session.setAttribute("totalincome", totalincome);
                session.setAttribute("totalPerMonth", totalPerMonth);

                

                session.setAttribute("user",user);
                User got = (User) session.getAttribute("user");
                request.setAttribute("topfive",topFive(got));
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

