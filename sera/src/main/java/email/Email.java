//package email;
//
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import jakarta.servlet.ServletContext;
//import yahoofinance.YahooFinance;
//import yahoofinance.histquotes2.HistoricalDividend;
//
//import javax.activation.DataHandler;
//import javax.activation.FileDataSource;
//import javax.mail.*;
//import javax.mail.internet.*;
//import javax.activation.DataSource;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.sql.*;
//import java.time.*;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Properties;
//import java.util.Set;
//
//public class Email implements Runnable {
//    public static void runn() throws ClassNotFoundException, SQLException, IOException{
//        System.out.println("hibye");
//        //Establish connection
//        Class.forName("com.mysql.jdbc.Driver");
//        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/seraschema", "root", "Appletree1!");
//        Statement u_s = con.createStatement();
//
//        //Query to get the number of rows in a table
//        String query = "select * from seraschema.user_stock";
//        System.out.println("hi");
//        ResultSet rss = u_s.executeQuery(query);
//        while (rss.next()){
//            System.out.println(rss.getString("symbol"));
//        }
//        //Executing the query
//        ResultSet rs1 = u_s.executeQuery(query);
//        while (rs1.next()) {
//            System.out.println(rs1.getString("symbol"));
//            String stockCode = rs1.getString("symbol");
//            System.out.println(stockCode);
//            String emailStatus = rs1.getString("emailStatus");
//            System.out.println(emailStatus);
//            // user
//            int uid = rs1.getInt("uid");
//            System.out.println(uid);
//            int holding = rs1.getInt("holding");
//            // look up default currency in user table
//            Statement u = con.createStatement();
//            ResultSet rsUser = u.executeQuery("select * from seraschema.user where userid = '" + uid + "'");
//            String cur = "";
//            String email = "";
//            while (rsUser.next()) {
//                cur = rsUser.getString("default_currency");
//                email = rsUser.getString("email");
//            }
//
//
//            // fetch exchange rate
//            String url_str = "https://v6.exchangerate-api.com/v6/0e01e6c4ed84843e593f49ff/latest/" + cur;
//            // Making Request
//            URL url = new URL(url_str);
//            HttpURLConnection request1 = (HttpURLConnection) url.openConnection();
//            request1.connect();
//            // Convert to JSON
//            JsonParser jp = new JsonParser();
//            JsonElement root = jp.parse(new InputStreamReader((InputStream) request1.getContent()));
//            JsonObject jsonobj = root.getAsJsonObject();
//            // Accessing object
//            JsonObject j = (JsonObject) jsonobj.get("conversion_rates");
//            Set<String> keyset = j.keySet();
//
//            // fetch pay div
//            yahoofinance.Stock stock = YahooFinance.get(stockCode);
//            double er = j.get(stock.getCurrency()).getAsDouble();
//            String name = stock.getName();
//
//            Calendar cal = Calendar.getInstance();
//            cal.add(Calendar.YEAR, -1);
//            List<HistoricalDividend> dividendList = stock.getDividendHistory(cal);
//
//            Calendar payDate = stock.getDividend().getPayDate();
//
//            if (payDate != null && (Calendar.getInstance().compareTo(payDate) <0)) {
//                System.out.println("hihihibye");
//                double divprice = dividendList.get(dividendList.size() - 1).getAdjDividend().doubleValue() / er;
//                LocalDate divDay = LocalDateTime.ofInstant(payDate.toInstant(), ZoneId.systemDefault()).toLocalDate();
//                LocalDate today = LocalDateTime.ofInstant(Calendar.getInstance().toInstant(), ZoneId.systemDefault()).toLocalDate();
//                System.out.println("emailStatus");
//                System.out.println(Period.between(today, divDay).getDays());
//                System.out.println((Period.between(today, divDay).getDays() <= 7));
//                System.out.println((emailStatus.equals("sent")));
//                if ((Period.between(today, divDay).getDays() <= 7) && (!emailStatus.equals("sent"))) {
//                    System.out.println("hihihibye3");
//                    System.out.println("emailStatus");
//                    System.out.println(emailStatus);
//                    //sendemail
//
//                    System.out.println("hiiiiiii");
//                    // https://stackoverflow.com/questions/59069456/sending-an-email-using-gmail-through-java
//
//                    // Sender's email ID needs to be mentioned
//                    String from = "sonja.hinting@gmail.com";
//
//                    // Assuming you are sending email from through gmails smtp
//                    String host = "smtp.gmail.com";
//
//                    // Get system properties
//                    Properties properties = System.getProperties();
//
//                    // Setup mail server
//                    properties.put("mail.smtp.auth", true);
//                    properties.put("mail.smtp.starttls.enable", "true");
//                    properties.put("mail.smtp.host", host);
//                    properties.put("mail.smtp.port", 587);
//                    properties.put("mail.smtp.ssl.trust", host);
//
//                    // Get the Session object.// and pass username and password
//                    Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
//                        protected PasswordAuthentication getPasswordAuthentication() {
//                            return new PasswordAuthentication("sonja.hinting@gmail.com", "boieespsbebbrsso");
//                        }
//                    });
//
//                    // Used to debug SMTP issues
//                    session.setDebug(true);
//
//                    try {
//                        // Create a default MimeMessage object.
//                        MimeMessage message = new MimeMessage(session);
//
//                        // Set From: header field of the header.
//                        message.setFrom(new InternetAddress(from));
//
//                        // Set To: header field of the header.
//                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
//
//                        // Set Subject: header field
//                        message.setSubject("SERA: Your dividend payment awaits you!");
//
//
//                        MimeMultipart multipart = new MimeMultipart("related");
//                        // first part (the html)
//                        BodyPart messageBodyPart = new MimeBodyPart();
//                        String htmlText = "<H1>Congratulations! " + name + "'s next payment date is coming soon!</H1><p>Stock code: " + stockCode + "<br>Holdings: " + holding + "<br>Dividend price per holding / " + cur + ": " + String.format("%.2f", divprice) + "<br>Total payment / " + cur + ": " + String.format("%.2f", divprice * holding) + "<br>Payment date: " + payDate.get(Calendar.DATE) + "/" + (payDate.get(Calendar.MONTH) + 1) + "/" + payDate.get(Calendar.YEAR) + "<br><br>Best wishes,<br>SERA TEAM<br><br></p><img src=\"cid:image\" width=\"362\" height=\"114\">";
//                        messageBodyPart.setContent(htmlText, "text/html");
//                        // add it
//                        multipart.addBodyPart(messageBodyPart);
//
//
//                        // second part (the image)
//                        messageBodyPart = new MimeBodyPart();
//
//                        DataSource fds = new FileDataSource(
//                                "C:\\Users\\sonja\\OneDrive\\Documents\\CSIA\\sera\\sera\\src\\main\\webapp\\img\\seralogo.png");
//
//                        messageBodyPart.setDataHandler(new DataHandler(fds));
//                        messageBodyPart.setFileName(fds.getName());
//                        messageBodyPart.setHeader("Content-ID", "<image>");
//
//                        // add image to the multipart
//                        multipart.addBodyPart(messageBodyPart);
//
//                        // put everything together
//                        message.setContent(multipart);
//                        System.out.println("sending...");
//                        // Send message
//                        Transport.send(message);
//                        System.out.println("Sent message successfully....");
//
//                        PreparedStatement st = con.prepareStatement("update seraschema.user_stock set emailStatus = ? where uid = ? and symbol = ?");
//                        st.setString(1, "sent");
//                        st.setInt(2, uid);
//                        st.setString(3, stockCode);
//                        st.executeUpdate();
//                        st.close();
//
//                    } catch (AddressException e) {
//                        throw new RuntimeException(e);
//                    } catch (MessagingException e) {
//                        throw new RuntimeException(e);
//                    }
//
//
//                } else if (emailStatus == "sent" && (Calendar.getInstance().compareTo(payDate) > 0)) {
//                    System.out.println("hihihihi");
//                    PreparedStatement st = con.prepareStatement("update seraschema.user_stock set emailStatus = ? where uid = ? and symbol = ?");
//                    st.setString(1, "null");
//                    st.setInt(2, uid);
//                    st.setString(3, stockCode);
//                    st.executeUpdate();
//                    st.close();
//                } else {
//                    System.out.println("what");
//                }
//            } else {
//                System.out.println("hello?");
//            }
//        }
//        System.out.println("a");
//        rs1.close();
//    }
//    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
//        System.out.println("hi");
//        //Establish connection
//        Class.forName("com.mysql.jdbc.Driver");
//        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/seraschema", "root", "Appletree1!");
//        Statement u_s = con.createStatement();
//
//        //Query to get the number of rows in a table
//        String query = "select * from seraschema.user_stock";
//        System.out.println("hi");
//        //Executing the query
//        ResultSet rs1 = u_s.executeQuery(query);
//        while (rs1.next()) {
//            String stockCode = rs1.getString("symbol");
//            System.out.println(stockCode);
//            String emailStatus = rs1.getString("emailStatus");
//            System.out.println(emailStatus);
//            // user
//            int uid = rs1.getInt("uid");
//            System.out.println(uid);
//            int holding = rs1.getInt("holding");
//            // look up default currency in user table
//            Statement u = con.createStatement();
//            ResultSet rsUser = u.executeQuery("select * from seraschema.user where userid = '" + uid + "'");
//            String cur = "";
//            String email = "";
//            while (rsUser.next()) {
//                cur = rsUser.getString("default_currency");
//                email = rsUser.getString("email");
//            }
//
//
//            // fetch exchange rate
//            String url_str = "https://v6.exchangerate-api.com/v6/0e01e6c4ed84843e593f49ff/latest/" + cur;
//            // Making Request
//            URL url = new URL(url_str);
//            HttpURLConnection request1 = (HttpURLConnection) url.openConnection();
//            request1.connect();
//            // Convert to JSON
//            JsonParser jp = new JsonParser();
//            JsonElement root = jp.parse(new InputStreamReader((InputStream) request1.getContent()));
//            JsonObject jsonobj = root.getAsJsonObject();
//            // Accessing object
//            JsonObject j = (JsonObject) jsonobj.get("conversion_rates");
//            Set<String> keyset = j.keySet();
//
//            // fetch pay div
//            yahoofinance.Stock stock = YahooFinance.get(stockCode);
//            double er = j.get(stock.getCurrency()).getAsDouble();
//            String name = stock.getName();
//
//            Calendar cal = Calendar.getInstance();
//            cal.add(Calendar.YEAR, -1);
//            List<HistoricalDividend> dividendList = stock.getDividendHistory(cal);
//
//            Calendar payDate = stock.getDividend().getPayDate();
//
//            if (payDate != null && (Calendar.getInstance().compareTo(payDate) <0)) {
//                System.out.println("hihihi");
//                double divprice = dividendList.get(dividendList.size() - 1).getAdjDividend().doubleValue() / er;
//                LocalDate divDay = LocalDateTime.ofInstant(payDate.toInstant(), ZoneId.systemDefault()).toLocalDate();
//                LocalDate today = LocalDateTime.ofInstant(Calendar.getInstance().toInstant(), ZoneId.systemDefault()).toLocalDate();
//                if (Period.between(divDay, today).getDays() <= 7 && (!emailStatus.equals("sent"))) {
//                    System.out.println("emailStatus");
//                    System.out.println(emailStatus);
//                    //sendemail
//
//                    System.out.println("hiiiiiii");
//                    // https://stackoverflow.com/questions/59069456/sending-an-email-using-gmail-through-java
//
//                    // Sender's email ID needs to be mentioned
//                    String from = "sonja.hinting@gmail.com";
//
//                    // Assuming you are sending email from through gmails smtp
//                    String host = "smtp.gmail.com";
//
//                    // Get system properties
//                    Properties properties = System.getProperties();
//
//                    // Setup mail server
//                    properties.put("mail.smtp.auth", true);
//                    properties.put("mail.smtp.starttls.enable", "true");
//                    properties.put("mail.smtp.host", host);
//                    properties.put("mail.smtp.port", 587);
//                    properties.put("mail.smtp.ssl.trust", host);
//
//                    // Get the Session object.// and pass username and password
//                    Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
//                        protected PasswordAuthentication getPasswordAuthentication() {
//                            return new PasswordAuthentication("sonja.hinting@gmail.com", "boieespsbebbrsso");
//                        }
//                    });
//
//                    // Used to debug SMTP issues
//                    session.setDebug(true);
//
//                    try {
//                        // Create a default MimeMessage object.
//                        MimeMessage message = new MimeMessage(session);
//
//                        // Set From: header field of the header.
//                        message.setFrom(new InternetAddress(from));
//
//                        // Set To: header field of the header.
//                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
//
//                        // Set Subject: header field
//                        message.setSubject("SERA: Your dividend payment awaits you!");
//
//
//                        MimeMultipart multipart = new MimeMultipart("related");
//                        // first part (the html)
//                        BodyPart messageBodyPart = new MimeBodyPart();
//                        String htmlText = "<H1>Congratulations! "+name+ "'s next payment date is coming soon!</H1><p>Stock code: " + stockCode + "<br>Holdings: " + holding + "<br>Dividend price per holding / " + cur + ": " + String.format("%.2f", divprice) + "<br>Total payment / " + cur + ": " + String.format("%.2f", divprice * holding) + "<br>Payment date: " + payDate.get(Calendar.DATE) + "/" + (payDate.get(Calendar.MONTH) + 1) + "/" + payDate.get(Calendar.YEAR)+"<br><br>Best wishes,<br>SERA TEAM<br><br></p><img src=\"cid:image\" width=\"362\" height=\"114\">";
//                        messageBodyPart.setContent(htmlText, "text/html");
//                        // add it
//                        multipart.addBodyPart(messageBodyPart);
//
//
//                        // second part (the image)
//                        messageBodyPart = new MimeBodyPart();
//
//                        DataSource fds = new FileDataSource(
//                                "C:\\Users\\sonja\\OneDrive\\Documents\\CSIA\\sera\\sera\\src\\main\\webapp\\img\\seralogo.png");
//
//                        messageBodyPart.setDataHandler(new DataHandler(fds));
//                        messageBodyPart.setFileName(fds.getName());
//                        messageBodyPart.setHeader("Content-ID", "<image>");
//
//                        // add image to the multipart
//                        multipart.addBodyPart(messageBodyPart);
//
//                        // put everything together
//                        message.setContent(multipart);
//                        System.out.println("sending...");
//                        // Send message
//                        Transport.send(message);
//                        System.out.println("Sent message successfully....");
//
//                        PreparedStatement st = con.prepareStatement("update seraschema.user_stock set emailStatus = ? where uid = ? and symbol = ?");
//                        st.setString(1, "sent");
//                        st.setInt(2, uid);
//                        st.setString(3, stockCode);
//                        st.executeUpdate();
//                        st.close();
//
//                    } catch (AddressException e) {
//                        throw new RuntimeException(e);
//                    } catch (MessagingException e) {
//                        throw new RuntimeException(e);
//                    }
//
//
//                }
//                System.out.println("AYY");
//            } else if (emailStatus == "sent" && (Calendar.getInstance().compareTo(payDate) > 0)) {
//                System.out.println("hihihihi");
//                PreparedStatement st = con.prepareStatement("update seraschema.user_stock set emailStatus = ? where uid = ? and symbol = ?");
//                st.setString(1, "null");
//                st.setInt(2, uid);
//                st.setString(3, stockCode);
//                st.executeUpdate();
//                st.close();
//            }
//            System.out.println("hihihihihihi");
//        }
//        System.out.println("a");
//        rs1.close();
//    }
//
//    @Override
//    public void run() {
//        String[] str = new String[1];
//        try {
//            System.out.println("main");
////            main(str);
//            runn();
//            System.out.println("main");
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//}