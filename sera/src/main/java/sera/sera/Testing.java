package sera.sera;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes2.HistoricalDividend;

import javax.mail.*;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.Date;

import static sera.sera.UserDB.login;

public class Testing implements Runnable{
//    public static void main(String[] args) throws IOException {
//        String cookie = null;
//
//        try {
//            // Open the URL connection
//            System.out.println("hi");
//            URL url = new URL("https://finance.yahoo.com/quote/SPY");
//            URLConnection con = url.openConnection();
//            System.out.println("hi");
//            for (Map.Entry<String, List<String>> entry : con.getHeaderFields().entrySet()) {
//                System.out.println(entry.getKey());
//                if (entry.getKey() == null || !entry.getKey().equals("Set-Cookie"))
//                    continue;
//                for (String s : entry.getValue()) {
//                    // store your cookie
//                    cookie = s;
//                    System.out.println( "Cookie = " + cookie);
//                }
//            }
//
//            String crumb = null;
//            InputStream inStream = con.getInputStream();
//            InputStreamReader irdr = new InputStreamReader(inStream);
//            BufferedReader rsv = new BufferedReader(irdr);
//
//            Pattern crumbPattern = Pattern.compile(".*\"CrumbStore\":\\{\"crumb\":\"([^\"]+)\"\\}.*");
//
//            String line = null;
//            while (crumb == null && (line = rsv.readLine()) != null) {
//                Matcher matcher = crumbPattern.matcher(line);
//                if (matcher.matches()) {
//                    crumb = matcher.group(1);
//                    System.out.println( "Crumb = " + crumb);
//                }
//            }
//            rsv.close();
//        }
//        catch (java.net.SocketTimeoutException e) {
//            // The URL connection timed out.  Try again.
//        } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        System.out.println("hi");
//        CookieManager cookieManager = new CookieManager();
//        CookieHandler.setDefault(cookieManager);
//
//        URL url = new URL("https://finance.yahoo.com/quote/SPY");
//
//        URLConnection connection = url.openConnection();
//        connection.getContent();
//
//        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
//        System.out.println("hi");
//        for (HttpCookie c : cookies) {
//            System.out.println("hi");
//            System.out.println(c.getDomain());
//            System.out.println(c);
//        }
//
//    }
public static Runnable email(User user, String msg) throws AddressException {
    System.out.println("hi");
    // https://stackoverflow.com/questions/59069456/sending-an-email-using-gmail-through-java

    // Sender's email ID needs to be mentioned
    String from = "sonja.hinting@gmail.com";

    // Assuming you are sending email from through gmails smtp
    String host = "smtp.gmail.com";

    // Get system properties
    Properties properties = System.getProperties();

    // Setup mail server
//    properties.put("mail.smtp.host", host);
//    properties.put("mail.smtp.port", "465");
//    properties.put("mail.smtp.ssl.enable", "true");
//    properties.put("mail.smtp.auth", "true");
//
//    properties.put("mail.smtp.socketFactory.port", "465");
//    properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");

    properties.put("mail.smtp.auth", true);
    properties.put("mail.smtp.starttls.enable", "true");
    properties.put("mail.smtp.host", host);
    properties.put("mail.smtp.port", 587);
    properties.put("mail.smtp.ssl.trust", host);

//    final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
//    properties.setProperty("mail.smtp.host", "smtp.gmail.com");
////    properties.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
////    properties.setProperty("mail.smtp.socketFactory.fallback", "false");
//    properties.setProperty("mail.smtp.port", "465");
//    properties.setProperty("mail.smtp.socketFactory.port", "465");
//    properties.put("mail.smtp.auth", "true");
//    properties.put("mail.debug", "true");
//    properties.put("mail.store.protocol", "pop3");
//    properties.put("mail.transport.protocol", "smtp");

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
                    st.setString(1, null);
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
    return null;
}
    public static void scheduleEmail(User user) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
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
        int count = 0;
        while (rs1.next()) {
//        for (int i = 0; i < 1; i++) {
            System.out.println(rs1.getString("symbol"));
            Stock real = null;
            for (Stock s : user.getStocks()) {
                if (s.getStockCode().equals(rs1.getString("symbol"))) {
                    real = s;
                }
            }

            System.out.println(real.getStockCode());

                if (real.getPayDiv() != null) {
                    System.out.println("hihi");
                    System.out.println(rs1.getString("emailStatus"));
                    if (Objects.equals(rs1.getString("emailStatus"), "sent")) {
                        System.out.println("sent");
                    }
                    if (!Objects.equals(rs1.getString("emailStatus"), "sent")){
                        System.out.println("hi");
                        String message = "Stock code: " + real.getStockCode() + "\nHoldings: " + real.getHoldings() + "\nDividend price: " + real.getPayDiv().getDivPrice() + "\nPayment date: "+real.getPayDiv().getDay()+"/"+(real.getPayDiv().getMonth()+1)+"/"+real.getPayDiv().getYear();
                        Calendar c = Calendar.getInstance();
                        c.set(real.getPayDiv().getYear(), real.getPayDiv().getMonth(), real.getPayDiv().getDay());

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


                    count++;
                }
            }

        con.close();
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


            int exdivToPay = 0;

            if(dividendList.size()>0){

                if (dividendList.size()==1){
                    s.setGap(12);
                }else{
                    Calendar start = dividendList.get(dividendList.size()-2).getDate();
                    Calendar end = dividendList.get(dividendList.size()-1).getDate();
//                    printCal(start);
//                    printCal(end);
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
//                    printCal(payDate);
                    s.setPayDiv(new Dividend(payDate.get(Calendar.MONTH), payDate.get(Calendar.YEAR), payDate.get(Calendar.DATE), s.getStockCode(), divprice));
                }else if (payDate!=null){
                    System.out.println("beep");
//                    printCal(payDate);
                    s.setLastDiv(new Dividend(payDate.get(Calendar.MONTH), payDate.get(Calendar.YEAR), payDate.get(Calendar.DATE), s.getStockCode(), divprice));
                }

            }



            ArrayList<Dividend> dividends = new ArrayList<>();
            for (HistoricalDividend d : dividendList){
                System.out.println("hihihsihsd "+s.getStockCode());
//                printCal(d.getDate());
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
//                    printCal(date);
                    date.add(Calendar.MONTH, exdivToPay);
                    System.out.println("lcheck");
//                    printCal(date);
                    dividends.add(new Dividend(date.get(Calendar.MONTH), date.get(Calendar.YEAR), date.get(Calendar.DATE), s.getStockCode(), divprice));
                }
            }
            s.setDividends(dividends);
        }
    }
public static void main(String[] args) throws AddressException, NoSuchAlgorithmException, InvalidKeySpecException, SQLException, ClassNotFoundException, IOException {
//    SecureRandom random = new SecureRandom();
//    byte[] salt = { 82, 122, 43, 30, 47, 97, 4, 124, 31, 63, 108, 69, 83, 86, 125, 88 };
//    KeySpec spec = new PBEKeySpec("abc".toCharArray(), salt, 65536, 128);
//    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//    byte[] hash = factory.generateSecret(spec).getEncoded();
//    Blob pwblob = new SerialBlob(hash);
//    User test = new User(4,"s", "w", "fw1a2b3@gmail.com", pwblob, "HKD");
//    Stock teststock = new Stock("AAPL", "NasdaqGS", 50);
//    teststock.setPayDiv(new Dividend(5,2023,7, "AAPL", 10));
//    ArrayList<Stock> ustocks = new ArrayList<>();
//    ustocks.add(teststock);
//    test.setStocks(ustocks);

    User test = login("sonja.hinting@gmail.com", "abc");
    test.updatestocks();
    String url_str = "https://v6.exchangerate-api.com/v6/0e01e6c4ed84843e593f49ff/latest/"+test.getDefaultCurrency();

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
    fetchStockAPI(test, j);
    scheduleEmail(test);
    email(test, "hi");
}

    @Override
    public void run() {
        try {
            main(null);
        } catch (AddressException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
