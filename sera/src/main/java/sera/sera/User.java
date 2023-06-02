package sera.sera;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static sera.sera.UserDB.insert;
import static sera.sera.UserDB.update;

public class User {
    private int UserID;
    private String firstName;
    private String lastName;
    private String email;
    private Blob password;
    private String defaultCurrency;
    private ArrayList<Stock> stocks;
    // we dont need this integer list for holdings
    private ArrayList<Integer> holdings;
    private ArrayList<String> markets;

    public User(int userID, String firstName, String lastName, String email, Blob password, String default_currency) {
        this.UserID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.defaultCurrency = default_currency;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Blob getPassword() {
        return password;
    }

    public void setPassword(Blob password) {
        this.password = password;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) throws SQLException, ClassNotFoundException {
        this.defaultCurrency = defaultCurrency;
        update(this.UserID, defaultCurrency);
    }

    public ArrayList<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(ArrayList<Stock> stocks) {
        this.stocks = stocks;
    }

    public ArrayList<Integer> getHoldings() {
        return holdings;
    }

    public void setHoldings(ArrayList<Integer> holdings) {
        this.holdings = holdings;
    }

    public ArrayList<String> getMarkets() {
        return markets;
    }

    public void setMarkets(ArrayList<String> markets) {
        this.markets = markets;
    }

    public void updatestocks() throws ClassNotFoundException, SQLException {
        this.stocks = new ArrayList<Stock>();
        List<String> symbols = new ArrayList<>();
        List<Integer> holdings = new ArrayList<>();
        List<String> markets = new ArrayList<>();

        List<String> symbolPair = new ArrayList<>();
        List<String> marketPair = new ArrayList<>();

        //Establish connection
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/seraschema", "root", "Appletree1!");
        Statement stmt = con.createStatement();

        //Query to get the number of rows in a table
        String query = "select * from seraschema.user_stock where uid = '" + this.UserID + "'";

        //Executing the query
        ResultSet rs1 = stmt.executeQuery(query);


        Statement stmt2 = con.createStatement();
        String query2 = "select * from seraschema.stock";

        ResultSet rs2 = stmt2.executeQuery(query2);
        while (rs2.next()) {
            symbolPair.add(rs2.getString("stockCode"));
            marketPair.add(rs2.getString("market"));
//            if(symbols.contains(rs2.getString("stockCode"))){
//                markets.add(rs2.getString("market"));
//            }
        }

        while(rs1.next()){
            symbols.add(rs1.getString("symbol"));
            holdings.add(rs1.getInt("holding"));
            for (int i=0;i<marketPair.size();i++){
                if (rs1.getString("symbol").equals(symbolPair.get(i))){
                    markets.add(marketPair.get(i));
                }
            }
        }


        for(int i=0;i<symbols.size();i++){
            this.stocks.add(new Stock(symbols.get(i), markets.get(i),holdings.get(i)));
        }

        Set<String> uniquemarket = new HashSet<String>(markets);
        this.markets = new ArrayList<String>(uniquemarket);

        for (Stock s : this.stocks){
            System.out.println(s.getStockCode());
        }

        // Close all the connections
        con.close();

    }

    // parse CSV
    public static List<List<String>> parseCsv(InputStream csvInput, char csvSeparator) {

        // Prepare.
        BufferedReader csvReader = null;
        List<List<String>> csvList = new ArrayList<List<String>>();
        String csvRecord = null;

        // Process records.
        try {
            csvReader = new BufferedReader(new InputStreamReader(csvInput, "UTF-8"));
            while ((csvRecord = csvReader.readLine()) != null) {
                csvList.add(parseCsvRecord(csvRecord, csvSeparator));
            }
        } catch (IOException e) {
            throw new RuntimeException("Reading CSV failed.", e);
        } finally {
            if (csvReader != null)
                try {
                    csvReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return csvList;
    }

    /**
     * CSV record parser. Convert a CSV record to a List of Strings representing the fields of the
     * CSV record. The CSV record is expected to be separated by the specified CSV field separator.
     * @param record The CSV record.
     * @param csvSeparator The CSV field separator to be used.
     * @return A List of Strings representing the fields of each CSV record.
     */
    private static List<String> parseCsvRecord(String record, char csvSeparator) {

        // Prepare.
        boolean quoted = false;
        StringBuilder fieldBuilder = new StringBuilder();
        List<String> fields = new ArrayList<String>();

        // Process fields.
        for (int i = 0; i < record.length(); i++) {
            char c = record.charAt(i);
            fieldBuilder.append(c);

            if (c == '"') {
                quoted = !quoted; // Detect nested quotes.
            }

            if ((!quoted && c == csvSeparator) // The separator ..
                    || i + 1 == record.length()) // .. or, the end of record.
            {
                String field = fieldBuilder.toString() // Obtain the field, ..
                        .replaceAll(csvSeparator + "$", "") // .. trim ending separator, ..
                        .replaceAll("^\"|\"$", "") // .. trim surrounding quotes, ..
                        .replace("\"\"", "\""); // .. and un-escape quotes.
                fields.add(field.trim()); // Add field to List.
                fieldBuilder = new StringBuilder(); // Reset.
            }
        }

        return fields;
    }

    public void uploadCSV(InputStream csvIS) throws ClassNotFoundException, SQLException, IOException{
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/seraschema", "root", "Appletree1!");

        PreparedStatement del = con.prepareStatement("delete from user_stock where uid = ?");
        del.setInt(1, this.getUserID());
        del.executeUpdate();

        List<List<String>> input = parseCsv(csvIS, ',');
        //CSVReader reader = new CSVReader(new FileReader(fileName));

        String insertQuery = "Insert into user_stock (uid, symbol, holding) values (?,?,?)";
        PreparedStatement pstmt = con.prepareStatement(insertQuery);
        for(int count=4;count<input.size();count++){
            List<String> i = input.get(count);
//        }
//        for (List<String> i : input){
            // i.get(1) = stockCode, i.get(2) = name, i.get(3) = stock market i.get(4) = holding
            System.out.println("testtt");
            System.out.println(this.getUserID());
            System.out.println(i.get(0));
            System.out.println(i.get(3));
            pstmt.setInt(1,this.getUserID());
            pstmt.setString(2, i.get(0));
            pstmt.setString(3,i.get(3));
            pstmt.addBatch();

            // update stock db if specific stock not already in the db
            String query = "select * from seraschema.stock where stockCode = '"+i.get(0)+"'";
            System.out.println(query);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next() != false){
                System.out.println(i.get(1) + "exists");
            }else{
                System.out.println(i.get(1) + "not in");
                sera.sera.StockDB.insertStock(i.get(0), i.get(2), i.get(1));
                System.out.println(i.get(1) + "in");
            }

        }
        System.out.println(insertQuery);
        pstmt.executeBatch();
        System.out.println("successfully uploaded");

    }

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        File initialFile = new File("C:/Users/sonja/Downloads/stock portfolio template - Sheet1.csv");
        InputStream targetStream = new FileInputStream(initialFile);
        User testing = new User(1, "S", "W", "a", null, "a");
        testing.uploadCSV(targetStream);
    }

}
