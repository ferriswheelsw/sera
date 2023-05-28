package yahoofinance.quotes.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yahoofinance.Utils;
import yahoofinance.YahooFinance;
import yahoofinance.util.RedirectableRequest;

public abstract class QuotesRequest<T> {
  private static final Logger log = LoggerFactory.getLogger(QuotesRequest.class);
  
  protected final String query;
  
  protected List<QuotesProperty> properties;
  
  public QuotesRequest(String query, List<QuotesProperty> properties) {
    this.query = query;
    this.properties = properties;
  }
  
  public String getQuery() {
    return this.query;
  }
  
  public List<QuotesProperty> getProperties() {
    return this.properties;
  }
  
  public void setProperties(List<QuotesProperty> properties) {
    this.properties = properties;
  }
  
  protected abstract T parseCSVLine(String paramString);
  
  private String getFieldsString() {
    StringBuilder result = new StringBuilder();
    for (QuotesProperty property : this.properties)
      result.append(property.getTag()); 
    return result.toString();
  }
  
  public T getSingleResult() throws IOException {
    List<T> results = getResult();
    if (results.size() > 0)
      return results.get(0); 
    return null;
  }
  
  public List<T> getResult() throws IOException {
    List<T> result = new ArrayList<>();
    Map<String, String> params = new LinkedHashMap<>();
    params.put("s", this.query);
    params.put("f", getFieldsString());
    params.put("e", ".csv");
    String url = YahooFinance.QUOTES_BASE_URL + "?" + Utils.getURLParameters(params);
    log.info("Sending request: " + url);
    URL request = new URL(url);
    RedirectableRequest redirectableRequest = new RedirectableRequest(request, 5);
    redirectableRequest.setConnectTimeout(YahooFinance.CONNECTION_TIMEOUT);
    redirectableRequest.setReadTimeout(YahooFinance.CONNECTION_TIMEOUT);
    URLConnection connection = redirectableRequest.openConnection();
    InputStreamReader is = new InputStreamReader(connection.getInputStream());
    try {
      BufferedReader br = new BufferedReader(is);
      try {
        for (String line = br.readLine(); line != null; line = br.readLine()) {
          if (line.equals("Missing Symbols List.")) {
            log.error("The requested symbol was not recognized by Yahoo Finance");
          } else {
            log.info("Parsing CSV line: " + Utils.unescape(line));
            T data = parseCSVLine(line);
            result.add(data);
          } 
        } 
        br.close();
      } catch (Throwable throwable) {
        try {
          br.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
      is.close();
    } catch (Throwable throwable) {
      try {
        is.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return result;
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinance\quotes\csv\QuotesRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */