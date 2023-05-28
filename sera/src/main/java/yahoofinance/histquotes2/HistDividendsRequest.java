package yahoofinance.histquotes2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yahoofinance.Utils;
import yahoofinance.YahooFinance;
import yahoofinance.util.RedirectableRequest;

public class HistDividendsRequest {
  private static final Logger log = LoggerFactory.getLogger(HistDividendsRequest.class);
  
  private final String symbol;
  
  private final Calendar from;
  
  private final Calendar to;
  
  public static final Calendar DEFAULT_FROM = Calendar.getInstance();
  
  static {
    DEFAULT_FROM.add(1, -1);
  }
  
  public static final Calendar DEFAULT_TO = Calendar.getInstance();
  
  public static final QueryInterval DEFAULT_INTERVAL = QueryInterval.DAILY;
  
  public HistDividendsRequest(String symbol) {
    this(symbol, DEFAULT_FROM, DEFAULT_TO);
  }
  
  public HistDividendsRequest(String symbol, Calendar from, Calendar to) {
    this.symbol = symbol;
    this.from = cleanHistCalendar(from);
    this.to = cleanHistCalendar(to);
  }
  
  public HistDividendsRequest(String symbol, Date from, Date to) {
    this(symbol);
    this.from.setTime(from);
    this.to.setTime(to);
    cleanHistCalendar(this.from);
    cleanHistCalendar(this.to);
  }
  
  private Calendar cleanHistCalendar(Calendar cal) {
    cal.set(14, 0);
    cal.set(13, 0);
    cal.set(12, 0);
    cal.set(10, 0);
    return cal;
  }
  
  public List<HistoricalDividend> getResult() throws IOException {
    List<HistoricalDividend> result = new ArrayList<>();
    if (this.from.after(this.to)) {
      log.warn("Unable to retrieve historical dividends. From-date should not be after to-date. From: " + this.from
          
          .getTime() + ", to: " + this.to.getTime());
      return result;
    } 
    Map<String, String> params = new LinkedHashMap<>();
    params.put("period1", String.valueOf(this.from.getTimeInMillis() / 1000L));
    params.put("period2", String.valueOf(this.to.getTimeInMillis() / 1000L));
    params.put("interval", DEFAULT_INTERVAL.getTag());
    params.put("events", "div");
//    params.put("crumb", CrumbManager.getCrumb());
    params.put("includeAdjustedClose", "true");
    String url = YahooFinance.HISTQUOTES2_BASE_URL + URLEncoder.encode(this.symbol, "UTF-8") + "?" + Utils.getURLParameters(params);
    log.info("Sending request: " + url);
    URL request = new URL(url);
    RedirectableRequest redirectableRequest = new RedirectableRequest(request, 5);
    redirectableRequest.setConnectTimeout(YahooFinance.CONNECTION_TIMEOUT);
    redirectableRequest.setReadTimeout(YahooFinance.CONNECTION_TIMEOUT);
    Map<String, String> requestProperties = new HashMap<>();
//    requestProperties.put("Cookie", CrumbManager.getCookie());
    URLConnection connection = redirectableRequest.openConnection(requestProperties);
    System.out.println("hi");
    InputStreamReader is = new InputStreamReader(connection.getInputStream());
    try {
      BufferedReader br = new BufferedReader(is);
      System.out.println("hii");
      try {
        br.readLine();
        for (String line = br.readLine(); line != null; line = br.readLine()) {
          log.info("Parsing CSV line: " + Utils.unescape(line));
          HistoricalDividend dividend = parseCSVLine(line);
          result.add(dividend);
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
  
  private HistoricalDividend parseCSVLine(String line) {
    String[] data = line.split(",");
    return new HistoricalDividend(this.symbol, 
        Utils.parseHistDate(data[0]), 
        Utils.getBigDecimal(data[1]));
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinance\histquotes2\HistDividendsRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */