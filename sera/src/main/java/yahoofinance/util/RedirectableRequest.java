package yahoofinance.util;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class RedirectableRequest {
  private URL request;
  
  private int protocolRedirectLimit;
  
  private int connectTimeout = 10000;
  
  private int readTimeout = 10000;
  
  static {
    CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
  }
  
  public RedirectableRequest(URL request) {
    this(request, 2);
  }
  
  public RedirectableRequest(URL request, int protocolRedirectLimit) {
    this.request = request;
    this.protocolRedirectLimit = protocolRedirectLimit;
  }
  
  public URLConnection openConnection() throws IOException {
    return openConnection(new HashMap<>());
  }
  
  public URLConnection openConnection(Map<String, String> requestProperties) throws IOException {
    int redirectCount = 0;
    boolean hasResponse = false;
    HttpURLConnection connection = null;
    URL currentRequest = this.request;
    while (!hasResponse && redirectCount <= this.protocolRedirectLimit) {
      String location;
      connection = (HttpURLConnection)currentRequest.openConnection();
      connection.setConnectTimeout(this.connectTimeout);
      connection.setReadTimeout(this.readTimeout);
      for (String requestProperty : requestProperties.keySet())
        connection.addRequestProperty(requestProperty, requestProperties.get(requestProperty)); 
      connection.setInstanceFollowRedirects(true);
      switch (connection.getResponseCode()) {
        case 301:
        case 302:
          redirectCount++;
          location = connection.getHeaderField("Location");
          currentRequest = new URL(this.request, location);
          continue;
      } 
      hasResponse = true;
    } 
    if (redirectCount > this.protocolRedirectLimit)
      throw new IOException("Protocol redirect count exceeded for url: " + this.request.toExternalForm()); 
    if (connection == null)
      throw new IOException("Unexpected error while opening connection"); 
    return connection;
  }
  
  public URL getRequest() {
    return this.request;
  }
  
  public void setRequest(URL request) {
    this.request = request;
  }
  
  public int getProtocolRedirectLimit() {
    return this.protocolRedirectLimit;
  }
  
  public void setProtocolRedirectLimit(int protocolRedirectLimit) {
    if (protocolRedirectLimit >= 0)
      this.protocolRedirectLimit = protocolRedirectLimit; 
  }
  
  public int getConnectTimeout() {
    return this.connectTimeout;
  }
  
  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }
  
  public int getReadTimeout() {
    return this.readTimeout;
  }
  
  public void setReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinanc\\util\RedirectableRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */