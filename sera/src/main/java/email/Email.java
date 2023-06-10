package email;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes2.HistoricalDividend;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class Email implements Runnable {
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        //Establish connection
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/seraschema", "root", "Appletree1!");
        Statement u_s = con.createStatement();

        //Query to get the number of rows in a table
        String query = "select * from seraschema.user_stock";

        //Executing the query
        ResultSet rs1 = u_s.executeQuery(query);
        while (rs1.next()) {
            String stockCode = rs1.getString("symbol");
            System.out.println(stockCode);
            String emailStatus = rs1.getString("emailStatus");
            System.out.println(emailStatus);
            // user
            int uid = rs1.getInt("uid");
            System.out.println(uid);
            int holding = rs1.getInt("holding");
            // look up default currency in user table
            Statement u = con.createStatement();
            ResultSet rsUser = u.executeQuery("select * from seraschema.user where userid = '" + uid + "'");
            String cur = "";
            String email = "";
            while (rsUser.next()) {
                cur = rsUser.getString("default_currency");
                email = rsUser.getString("email");
            }


            // fetch exchange rate
            String url_str = "https://v6.exchangerate-api.com/v6/0e01e6c4ed84843e593f49ff/latest/" + cur;
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

            // fetch pay div
            yahoofinance.Stock stock = YahooFinance.get(stockCode);
            double er = j.get(stock.getCurrency()).getAsDouble();


            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -1);
            List<HistoricalDividend> dividendList = stock.getDividendHistory(cal);

            Calendar payDate = stock.getDividend().getPayDate();

            if (payDate != null && (Calendar.getInstance().compareTo(payDate) == -1)) {
                System.out.println("hihihi");
                double divprice = dividendList.get(dividendList.size() - 1).getAdjDividend().doubleValue() / er;
                LocalDate divDay = LocalDateTime.ofInstant(payDate.toInstant(), ZoneId.systemDefault()).toLocalDate();
                LocalDate today = LocalDateTime.ofInstant(Calendar.getInstance().toInstant(), ZoneId.systemDefault()).toLocalDate();
                if (Period.between(divDay, today).getDays() <= 7 && (!emailStatus.equals("sent"))) {
                    System.out.println("emailStatus");
                    System.out.println(emailStatus);
                    //sendemail

                    System.out.println("hiiiiiii");
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
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

                        // Set Subject: header field
                        message.setSubject("SERA: Your dividend payment awaits you!");

                        // Now set the actual message
                        message.setText("Stock code: " + stockCode + "\nHoldings: " + holding + "\nDividend price per holding / " + cur + ": " + String.format("%.2f", divprice) + "\nTotal payment / " + cur + ": " + String.format("%.2f", divprice * holding) + "\nPayment date: " + payDate.get(Calendar.DATE) + "/" + (payDate.get(Calendar.MONTH) + 1) + "/" + payDate.get(Calendar.YEAR));

                        System.out.println("sending...");
                        // Send message
                        Transport.send(message);
                        System.out.println("Sent message successfully....");

                        PreparedStatement st = con.prepareStatement("update seraschema.user_stock set emailStatus = ? where uid = ? and symbol = ?");
                        st.setString(1, "sent");
                        st.setInt(2, uid);
                        st.setString(3, stockCode);
                        st.executeUpdate();
                        st.close();

                    } catch (AddressException e) {
                        throw new RuntimeException(e);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }


                }
                System.out.println("AYY");
            } else if (emailStatus == "sent" && (Calendar.getInstance().compareTo(payDate) == 0)) {
                System.out.println("hihihihi");
                PreparedStatement st = con.prepareStatement("update seraschema.user_stock set emailStatus = ? where uid = ? and symbol = ?");
                st.setString(1, "null");
                st.setInt(2, uid);
                st.setString(3, stockCode);
                st.executeUpdate();
                st.close();
            }
            System.out.println("hihihihihihi");
        }
        System.out.println("a");
        rs1.close();
    }

    @Override
    public void run() {
        //Establish connection
        System.out.println("abcdefghijklmnop");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = null;
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/seraschema", "root", "Appletree1!");
            Statement u_s = con.createStatement();
            //Query to get the number of rows in a table
            String query = "select * from seraschema.user_stock";

            //Executing the query
            ResultSet rs1 = u_s.executeQuery(query);
            while (rs1.next()) {
                String stockCode = rs1.getString("symbol");
                System.out.println(stockCode);
                String emailStatus = rs1.getString("emailStatus");
                System.out.println(emailStatus);
                // user
                int uid = rs1.getInt("uid");
                System.out.println(uid);
                int holding = rs1.getInt("holding");
                // look up default currency in user table
                Statement u = con.createStatement();
                ResultSet rsUser = u.executeQuery("select * from seraschema.user where userid = '" + uid + "'");
                String cur = "";
                String email = "";
                while (rsUser.next()) {
                    cur = rsUser.getString("default_currency");
                    email = rsUser.getString("email");
                }

                // fetch exchange rate
                String url_str = "https://v6.exchangerate-api.com/v6/0e01e6c4ed84843e593f49ff/latest/" + cur;
                // Making Request
                URL url = null;
                url = new URL(url_str);
                HttpURLConnection request1 = (HttpURLConnection) url.openConnection();
                request1.connect();
                // Convert to JSON
                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request1.getContent()));
                JsonObject jsonobj = root.getAsJsonObject();
                // Accessing object
                JsonObject j = (JsonObject) jsonobj.get("conversion_rates");
                Set<String> keyset = j.keySet();
                // fetch pay div
                yahoofinance.Stock stock = null;
                stock = YahooFinance.get(stockCode);
                double er = j.get(stock.getCurrency()).getAsDouble();
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -1);
                List<HistoricalDividend> dividendList = null;
                dividendList = stock.getDividendHistory(cal);

                Calendar payDate = stock.getDividend().getPayDate();

                if (payDate != null && (Calendar.getInstance().compareTo(payDate) == -1)) {
                    System.out.println("hihihi");
                    double divprice = dividendList.get(dividendList.size() - 1).getAdjDividend().doubleValue() / er;
                    LocalDate divDay = LocalDateTime.ofInstant(payDate.toInstant(), ZoneId.systemDefault()).toLocalDate();
                    LocalDate today = LocalDateTime.ofInstant(Calendar.getInstance().toInstant(), ZoneId.systemDefault()).toLocalDate();
                    if (Period.between(divDay, today).getDays() <= 7 && (!emailStatus.equals("sent"))) {
                        System.out.println("emailStatus");
                        System.out.println(emailStatus);
                        //sendemail

                        System.out.println("hiiiiiii");
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
                            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

                            // Set Subject: header field
                            message.setSubject("SERA: Your dividend payment awaits you!");

                            // Now set the actual message
                            message.setText("Stock code: " + stockCode + "\nHoldings: " + holding + "\nDividend price per holding / " + cur + ": " + String.format("%.2f", divprice) + "\nTotal payment / " + cur + ": " + String.format("%.2f", divprice * holding) + "\nPayment date: " + payDate.get(Calendar.DATE) + "/" + (payDate.get(Calendar.MONTH) + 1) + "/" + payDate.get(Calendar.YEAR));

                            System.out.println("sending...");
                            // Send message
                            Transport.send(message);
                            System.out.println("Sent message successfully....");

                            PreparedStatement st = con.prepareStatement("update seraschema.user_stock set emailStatus = ? where uid = ? and symbol = ?");
                            st.setString(1, "sent");
                            st.setInt(2, uid);
                            st.setString(3, stockCode);
                            st.executeUpdate();
                            st.close();

                        } catch (AddressException e) {
                            throw new RuntimeException(e);
                        } catch (MessagingException e) {
                            throw new RuntimeException(e);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }


                    }
                    System.out.println("AYY");
                } else if (emailStatus == "sent" && (Calendar.getInstance().compareTo(payDate) == 0)) {
                    System.out.println("hihihihi");
                    PreparedStatement st = null;
                    try {
                        st = con.prepareStatement("update seraschema.user_stock set emailStatus = ? where uid = ? and symbol = ?");
                        st.setString(1, "null");
                        st.setInt(2, uid);
                        st.setString(3, stockCode);
                        st.executeUpdate();
                        st.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                }
                System.out.println("hihihihihihi");


            }
            rs1.close();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}