package yahoofinance.histquotes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yahoofinance.Utils;
import yahoofinance.YahooFinance;
import yahoofinance.util.RedirectableRequest;

public class HistQuotesRequest {
  private static final Logger log = LoggerFactory.getLogger(HistQuotesRequest.class);
  
  private final String symbol;
  
  private final Calendar from;
  
  private final Calendar to;
  
  private final Interval interval;
  
  public static final Calendar DEFAULT_FROM = Calendar.getInstance();
  
  static {
    DEFAULT_FROM.add(1, -1);
  }
  
  public static final Calendar DEFAULT_TO = Calendar.getInstance();
  
  public static final Interval DEFAULT_INTERVAL = Interval.MONTHLY;
  
  public HistQuotesRequest(String symbol) {
    this(symbol, DEFAULT_INTERVAL);
  }
  
  public HistQuotesRequest(String symbol, Interval interval) {
    this(symbol, DEFAULT_FROM, DEFAULT_TO, interval);
  }
  
  public HistQuotesRequest(String symbol, Calendar from, Calendar to) {
    this(symbol, from, to, DEFAULT_INTERVAL);
  }
  
  public HistQuotesRequest(String symbol, Calendar from, Calendar to, Interval interval) {
    this.symbol = symbol;
    this.from = cleanHistCalendar(from);
    this.to = cleanHistCalendar(to);
    this.interval = interval;
  }
  
  public HistQuotesRequest(String symbol, Date from, Date to) {
    this(symbol, from, to, DEFAULT_INTERVAL);
  }
  
  public HistQuotesRequest(String symbol, Date from, Date to, Interval interval) {
    this(symbol, interval);
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
  
  public List<HistoricalQuote> getResult() throws IOException {
    List<HistoricalQuote> result = new ArrayList<>();
    if (this.from.after(this.to)) {
      log.warn("Unable to retrieve historical quotes. From-date should not be after to-date. From: " + this.from
          
          .getTime() + ", to: " + this.to.getTime());
      return result;
    } 
    Map<String, String> params = new LinkedHashMap<>();
    params.put("s", this.symbol);
    params.put("a", String.valueOf(this.from.get(2)));
    params.put("b", String.valueOf(this.from.get(5)));
    params.put("c", String.valueOf(this.from.get(1)));
    params.put("d", String.valueOf(this.to.get(2)));
    params.put("e", String.valueOf(this.to.get(5)));
    params.put("f", String.valueOf(this.to.get(1)));
    params.put("g", this.interval.getTag());
    params.put("ignore", ".csv");
    String url = YahooFinance.HISTQUOTES_BASE_URL + "?" + Utils.getURLParameters(params);
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
        br.readLine();
        for (String line = br.readLine(); line != null; line = br.readLine()) {
          log.info("Parsing CSV line: " + Utils.unescape(line));
          HistoricalQuote quote = parseCSVLine(line);
          result.add(quote);
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
  
  private HistoricalQuote parseCSVLine(String line) {
    String[] data = line.split(",");
    return new HistoricalQuote(this.symbol, 
        Utils.parseHistDate(data[0]), 
        Utils.getBigDecimal(data[1]), 
        Utils.getBigDecimal(data[3]), 
        Utils.getBigDecimal(data[2]), 
        Utils.getBigDecimal(data[4]), 
        Utils.getBigDecimal(data[6]), 
        Utils.getLong(data[5]));
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinance\histquotes\HistQuotesRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */