package yahoofinance.query2v8;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes2.QueryInterval;
import yahoofinance.util.RedirectableRequest;

public class HistQuotesQuery2V8Request {
  private static final Logger log = LoggerFactory.getLogger(HistQuotesQuery2V8Request.class);
  
  private static final ObjectMapper objectMapper = new ObjectMapper();
  
  private final String symbol;
  
  private final Calendar from;
  
  private final Calendar to;
  
  private final QueryInterval interval;
  
  public static final Calendar DEFAULT_FROM = Calendar.getInstance();
  
  static {
    DEFAULT_FROM.add(1, -1);
  }
  
  public static final Calendar DEFAULT_TO = Calendar.getInstance();
  
  public static final QueryInterval DEFAULT_INTERVAL = QueryInterval.MONTHLY;
  
  public HistQuotesQuery2V8Request(String symbol) {
    this(symbol, DEFAULT_INTERVAL);
  }
  
  public HistQuotesQuery2V8Request(String symbol, QueryInterval interval) {
    this(symbol, DEFAULT_FROM, DEFAULT_TO, interval);
  }
  
  public HistQuotesQuery2V8Request(String symbol, Calendar from, Calendar to) {
    this(symbol, from, to, DEFAULT_INTERVAL);
  }
  
  public HistQuotesQuery2V8Request(String symbol, Calendar from, Calendar to, QueryInterval interval) {
    this.symbol = symbol;
    this.from = cleanHistCalendar(from);
    this.to = cleanHistCalendar(to);
    this.interval = interval;
  }
  
  public HistQuotesQuery2V8Request(String symbol, Date from, Date to) {
    this(symbol, from, to, DEFAULT_INTERVAL);
  }
  
  public HistQuotesQuery2V8Request(String symbol, Date from, Date to, QueryInterval interval) {
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
    String json = getJson();
    JsonNode resultNode = objectMapper.readTree(json).get("chart").get("result").get(0);
    JsonNode timestamps = resultNode.get("timestamp");
    JsonNode indicators = resultNode.get("indicators");
    JsonNode quotes = indicators.get("quote").get(0);
    JsonNode closes = quotes.get("close");
    JsonNode volumes = quotes.get("volume");
    JsonNode opens = quotes.get("open");
    JsonNode highs = quotes.get("high");
    JsonNode lows = quotes.get("low");
    JsonNode adjCloses = indicators.get("adjclose").get(0).get("adjclose");
    List<HistoricalQuote> result = new ArrayList<>();
    for (int i = 0; i < timestamps.size(); i++) {
      long timestamp = timestamps.get(i).asLong();
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(timestamp * 1000L);
      BigDecimal adjClose = adjCloses.get(i).decimalValue();
      long volume = volumes.get(i).asLong();
      BigDecimal open = opens.get(i).decimalValue();
      BigDecimal high = highs.get(i).decimalValue();
      BigDecimal low = lows.get(i).decimalValue();
      BigDecimal close = closes.get(i).decimalValue();
      HistoricalQuote quote = new HistoricalQuote(this.symbol, calendar, open, low, high, close, adjClose, Long.valueOf(volume));
      result.add(quote);
    } 
    return result;
  }
  
  public String getJson() throws IOException {
    if (this.from.after(this.to)) {
      log.warn("Unable to retrieve historical quotes. From-date should not be after to-date. From: " + this.from
          
          .getTime() + ", to: " + this.to.getTime());
      return "";
    } 
    Map<String, String> params = new LinkedHashMap<>();
    params.put("period1", String.valueOf(this.from.getTimeInMillis() / 1000L));
    params.put("period2", String.valueOf(this.to.getTimeInMillis() / 1000L));
    params.put("interval", this.interval.getTag());
    params.put("events", "div|split");
    String url = YahooFinance.HISTQUOTES_QUERY2V8_BASE_URL + URLEncoder.encode(this.symbol, "UTF-8") + "?" + Utils.getURLParameters(params);
    log.info("Sending request: " + url);
    URL request = new URL(url);
    RedirectableRequest redirectableRequest = new RedirectableRequest(request, 5);
    redirectableRequest.setConnectTimeout(YahooFinance.CONNECTION_TIMEOUT);
    redirectableRequest.setReadTimeout(YahooFinance.CONNECTION_TIMEOUT);
    URLConnection connection = redirectableRequest.openConnection();
    StringBuilder builder = new StringBuilder();
    InputStreamReader is = new InputStreamReader(connection.getInputStream());
    try {
      BufferedReader br = new BufferedReader(is);
      try {
        for (String line = br.readLine(); line != null; line = br.readLine()) {
          if (builder.length() > 0)
            builder.append("\n"); 
          builder.append(line);
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
    return builder.toString();
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinance\query2v8\HistQuotesQuery2V8Request.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */