package sera.sera;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.sql.rowset.serial.SerialBlob;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.ArrayList;




public class UserDB {
    private static ArrayList<User> users = new ArrayList<>();
    private static final byte[] salt = { 82, 122, 43, 30, 47, 97, 4, 124, 31, 63, 108, 69, 83, 86, 125, 88 };

    // Insert a new user
    public static void insert(int id, String fN, String lN, String email, String pw, String cur) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        // hash password
        SecureRandom random = new SecureRandom();
        KeySpec spec = new PBEKeySpec(pw.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        Blob pwblob = new SerialBlob(hash);

        // Establish connection
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/seraschema", "root", "Appletree1!");

        // Insert new user
        PreparedStatement create = con.prepareStatement("insert into seraschema.user values(?,?,?,?,?,?)");
        create.setInt(1, id);
        create.setString(2, fN);
        create.setString(3,lN);
        create.setString(4, email);
        create.setBlob(5, pwblob);
        create.setString(6, cur);
        create.executeUpdate();

        // Close all the connections
        create.close();
        con.close();

        users.add(new User(id, fN, lN, email, pwblob, cur));
    }

    // Update an existing user's information
    public static void update(int id, String fN, String lN, String email, String pw, String cur) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        // hash password
        SecureRandom random = new SecureRandom();
        KeySpec spec = new PBEKeySpec(pw.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        Blob pwblob = new SerialBlob(hash);

        // Establish connection
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/seraschema", "root", "Appletree1!");

        // Update existing user
        PreparedStatement st = con.prepareStatement("update seraschema.user set firstName = ?, lastName = ?, email = ?, password = ?, default_currency = ? where userid = ?");
        st.setString(1, fN);
        st.setString(2, lN);
        st.setString(3, email);
        st.setBlob(4, pwblob);
        st.setString(5, cur);
        st.setInt(6, id);
        st.executeUpdate();

        // Close all the connections
        st.close();
        con.close();

    }

    // Delete a user
    public static void delete(int id) throws ClassNotFoundException, SQLException {
        // Establish connection
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/seraschema", "root", "Appletree1!");

        // delete user
        PreparedStatement st = con.prepareStatement("delete from seraschema.user where userid = ?");
        st.setInt(1, id);
        st.executeUpdate();

        // Close all the connections
        st.close();
        con.close();

    }

    // login and validation
    public static User login(String email, String pw) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        // Check if email exists, if not return wrong email error message

        // Establish connection
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/seraschema", "root", "Appletree1!");

        // Get password from database
        String query = "select * from seraschema.user where email = '"+email+ "'";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next() == false){
            System.out.println("no such email exists");
            return null;
        }
        byte[] x = rs.getBytes(5);
        String dbpw = new String(x, StandardCharsets.UTF_8);

        User user = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getBlob(5), rs.getString(6));

        // Close all connections
        stmt.close();
        con.close();

        // Hash password input
        SecureRandom random = new SecureRandom();
        KeySpec spec = new PBEKeySpec(pw.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        String inputpw = new String(hash, StandardCharsets.UTF_8);
//        System.out.println(inputpw);

        // Check if password column of row of email address = hashed password input
        if (dbpw.equals(inputpw)){
            System.out.println("success");
            return user;
        }else{
            System.out.println("wrong password");
            return null;
        }
    }

    public static void main(String[] args) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
//        delete(1);
//        insert(2,"S", "W", "sonja.hinting@gmail.com", "abc", "HKD");
//        update(1,"I", "R", "ishkitty5@gmail.com", "abc", "HKD");
//        login("ishkitty5@gmail.com", "abc");
    }
}
