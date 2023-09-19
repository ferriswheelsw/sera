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
        for (Stock s : user.getStocks()){
            yahoofinance.Stock stock = YahooFinance.get(s.getStockCode());
            // fetch exchange rate from the set fetched from currency API
            double er = j.get(stock.getCurrency()).getAsDouble();
            // fetch current price, price change from yesterday, yesterday's closing price, dividend yield (in % and the actual value), name of currency
            double price = stock.getQuote().getPrice().doubleValue();
            double change = stock.getQuote().getChangeInPercent().doubleValue();
            double prevclosing = stock.getQuote().getPreviousClose().doubleValue();
            double percentdiv = 0;
            double totaldiv = 0;
            if (stock.getDividend().getAnnualYield()!= null){
                percentdiv = stock.getDividend().getAnnualYieldPercent().doubleValue();
                totaldiv = stock.getDividend().getAnnualYield().doubleValue()*(double)s.getHoldings()/er;
            }
            String stockCur = stock.getCurrency();

            // store everything in the session
            s.setMarketPrice(price);
            s.setPriceChange(change);
            s.setPnl((price-prevclosing)*s.getHoldings());
            s.setPercentdiv(percentdiv);
            s.setTotaldiv(totaldiv);
            s.setStockCur(stockCur);

            // fetch all dividends in past 1Y
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR,-1);
            List<HistoricalDividend> dividendList = stock.getDividendHistory(cal);
            // set dividend frequency (per year) to be stored in session
            s.setDivfreq(dividendList.size());

            // if list is empty we don't need to store / estimate any dividends
            if(dividendList.size()>0){
                // account for the case when there is only one dividend (nothing to compare it to)
                if (dividendList.size()==1){
                    // default 1-year gap - stored as attribute of stock (in session)
                    s.setGap(12);
                }
                // otherwise: calculate gap between most recent and 2nd most recent dividend date
                else{
                    Calendar start = dividendList.get(dividendList.size()-2).getDate();
                    Calendar end = dividendList.get(dividendList.size()-1).getDate();
                    // to get total months between 2 dates and compare day of the month - must convert to LocalDate
                    LocalDate lstart = LocalDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault()).toLocalDate();
                    LocalDate lend = LocalDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault()).toLocalDate();
                    int sgap = (int) Period.between(lstart,lend).toTotalMonths();
                    // compare day of the month - if over half a month apart will round up the gap
                    // eg if 2nd last div = Jan 1st and last div = Feb 28th --> will round up to 2 months (and not 1 month)
                    if (Period.between(lstart,lend).getDays()>=15){
                        sgap++;
                    }
                    // stored as attribute of stock (in session)
                    s.setGap(sgap);
                }

                // get information on the most recent dividend
                double divprice = dividendList.get(dividendList.size()-1).getAdjDividend().doubleValue()/er;
                Calendar date = dividendList.get(dividendList.size()-1).getDate();
                Calendar payDate = stock.getDividend().getPayDate();

                // store info on the last confirmed dividend paydate as an attribute of stock (in session)
                s.setLastDiv(new Dividend(date.get(Calendar.MONTH), date.get(Calendar.YEAR), date.get(Calendar.DATE), s.getStockCode(), divprice));

                // if the API has a valid expected pay date stored
                if (payDate!=null){
                    // the month from the API is one month ahead of how I store my other dates (Java Calendars start from month 0 (January))
                    payDate.add(Calendar.MONTH,-1);
                    // validate that the pay date is after today
                    if ((Calendar.getInstance().compareTo(payDate)==-1)){
                        // store this as both the next upcoming dividend pay date and the latest confirmed dividend paydate (in session)
                        s.setPayDiv(new Dividend(payDate.get(Calendar.MONTH), payDate.get(Calendar.YEAR), payDate.get(Calendar.DATE), s.getStockCode(), divprice));
                        s.setLastDiv(new Dividend(payDate.get(Calendar.MONTH), payDate.get(Calendar.YEAR), payDate.get(Calendar.DATE), s.getStockCode(), divprice));
                    }
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

                // checking if the dividend is in this year (2023)
                if (date.get(Calendar.YEAR) == (Year.now().getValue())) {
                    dividends.add(new Dividend(date.get(Calendar.MONTH), date.get(Calendar.YEAR), date.get(Calendar.DATE), s.getStockCode(), divprice));
                }
            }
            s.setDividends(dividends);
        }
    }

    // to be called when loading home page
    public static ArrayList<Stock> topFive(User user){
        // get a complete list of all the stocks owned by user (stored in the session)
        ArrayList<Stock> unsorted = user.getStocks();
        // initialise an empty ArrayList to store only the top five
        ArrayList<Stock> sorted = new ArrayList<>();
        // track position of unsorted section of the unsorted ArrayList
        int count = 0;
        while(sorted.size()<5 && sorted.size()<unsorted.size()){
            // track index of the largest value so far
            int maxindex = count;
            // track the actual largest value so far
            double max = unsorted.get(count).getPriceChange();
            // for loop to loop through all values in the unsorted section
            for (int j=count;j<unsorted.size();j++){
                // compare each value with the current max
                if (unsorted.get(j).getPriceChange()>max){
                    // update max and maxindex variables
                    maxindex = j;
                    max = unsorted.get(j).getPriceChange();
                }
            }
            // add the max to the sorted list (so the first one added is the largest value in the unsorted Arraylist)
            sorted.add(new Stock(unsorted.get(maxindex)));
            // if the max value is not at the beginning of the unsorted list, right after the sorted section of the unsorted Arraylist
            // then that value should be moved to the front, at the end of the sorted section and in front of the unsorted section
            if (count!=maxindex){
                Stock temp = new Stock(unsorted.get(maxindex));
                unsorted.remove(maxindex);
                unsorted.add(count, temp);
            }
            // now there is one less value to sort through and we move the start of the unsorted section to the next value
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
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", 587);
        properties.put("mail.smtp.ssl.trust", host);

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
            //Establish connection to database
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
                //linear search for a particular stock in the user's list of stocks
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

                        // Email content
                        String message = "Stock code: " + s.getStockCode() + "\nHoldings: " + s.getHoldings() + "\nDividend price: " + s.getPayDiv().getDivPrice() + "\nPayment date: "+s.getPayDiv().getDay()+"/"+(s.getPayDiv().getMonth()+1)+"/"+s.getPayDiv().getYear();
                        // Get the date of 7 days before the payment date
                        Calendar c = Calendar.getInstance();
                        c.set(s.getPayDiv().getYear(), s.getPayDiv().getMonth(), s.getPayDiv().getDay());
                        c.add(Calendar.DATE,-7);
                        // Schedule the email to send on that day
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
                        }, c.getTime());


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

