package sera.sera;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Testing {
    public static void main(String[] args) throws IOException {
        String cookie = null;

        try {
            // Open the URL connection
            System.out.println("hi");
            URL url = new URL("https://finance.yahoo.com/quote/SPY");
            URLConnection con = url.openConnection();
            System.out.println("hi");
            for (Map.Entry<String, List<String>> entry : con.getHeaderFields().entrySet()) {
                System.out.println(entry.getKey());
                if (entry.getKey() == null || !entry.getKey().equals("Set-Cookie"))
                    continue;
                for (String s : entry.getValue()) {
                    // store your cookie
                    cookie = s;
                    System.out.println( "Cookie = " + cookie);
                }
            }

            String crumb = null;
            InputStream inStream = con.getInputStream();
            InputStreamReader irdr = new InputStreamReader(inStream);
            BufferedReader rsv = new BufferedReader(irdr);

            Pattern crumbPattern = Pattern.compile(".*\"CrumbStore\":\\{\"crumb\":\"([^\"]+)\"\\}.*");

            String line = null;
            while (crumb == null && (line = rsv.readLine()) != null) {
                Matcher matcher = crumbPattern.matcher(line);
                if (matcher.matches()) {
                    crumb = matcher.group(1);
                    System.out.println( "Crumb = " + crumb);
                }
            }
            rsv.close();
        }
        catch (java.net.SocketTimeoutException e) {
            // The URL connection timed out.  Try again.
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("hi");
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        URL url = new URL("https://finance.yahoo.com/quote/SPY");

        URLConnection connection = url.openConnection();
        connection.getContent();

        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
        System.out.println("hi");
        for (HttpCookie c : cookies) {
            System.out.println("hi");
            System.out.println(c.getDomain());
            System.out.println(c);
        }

    }
}
