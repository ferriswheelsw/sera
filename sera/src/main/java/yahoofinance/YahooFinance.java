package yahoofinance;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import yahoofinance.histquotes.HistQuotesRequest;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.csv.FxQuotesRequest;
import yahoofinance.quotes.csv.StockQuotesData;
import yahoofinance.quotes.csv.StockQuotesRequest;
import yahoofinance.quotes.fx.FxQuote;
import yahoofinance.quotes.query1v7.FxQuotesQuery1V7Request;
import yahoofinance.quotes.query1v7.StockQuotesQuery1V7Request;

public class YahooFinance {
  public static final String QUOTES_BASE_URL = System.getProperty("yahoofinance.baseurl.quotes", "http://download.finance.yahoo.com/d/quotes.csv");
  
  public static final String QUOTES_QUERY1V7_BASE_URL = System.getProperty("yahoofinance.baseurl.quotesquery1v7", "https://query1.finance.yahoo.com/v6/finance/quote");
  
  public static final String QUOTES_QUERY1V7_ENABLED = System.getProperty("yahoofinance.quotesquery1v7.enabled", "true");
  
  public static final String HISTQUOTES_BASE_URL = System.getProperty("yahoofinance.baseurl.histquotes", "https://ichart.yahoo.com/table.csv");
  
  public static final String HISTQUOTES2_ENABLED = System.getProperty("yahoofinance.histquotes2.enabled", "true");
  
  public static final String HISTQUOTES2_BASE_URL = System.getProperty("yahoofinance.baseurl.histquotes2", "https://query1.finance.yahoo.com/v7/finance/download/");
  
  public static final String HISTQUOTES_QUERY2V8_BASE_URL = System.getProperty("yahoofinance.baseurl.histquotesquery2v8", "https://query2.finance.yahoo.com/v8/finance/chart/");
  
  public static final String HISTQUOTES2_SCRAPE_URL = System.getProperty("yahoofinance.scrapeurl.histquotes2", "https://finance.yahoo.com/quote/%5EGSPC/options");
  
  public static final String HISTQUOTES2_CRUMB_URL = System.getProperty("yahoofinance.crumburl.histquotes2", "https://query1.finance.yahoo.com/v1/test/getcrumb");
  
  public static final String HISTQUOTES2_CRUMB = System.getProperty("yahoofinance.crumb", "");
  
  public static final String HISTQUOTES2_COOKIE = System.getProperty("yahoofinance.cookie", "");
  
  public static final String HISTQUOTES2_COOKIE_NAMESPACE = System.getProperty("yahoofinance.cookie.namespace", "yahoo");
  
  public static final String HISTQUOTES2_COOKIE_AGREE = System.getProperty("yahoofinance.cookie.agree", "agree");
  
  public static final String HISTQUOTES2_COOKIE_OATH_URL = System.getProperty("yahoofinance.cookie.oathurl", "https://guce.oath.com/consent");
  
  public static final String HISTQUOTES2_COOKIE_OATH_HOST = System.getProperty("yahoofinance.cookie.oathhost", "guce.oath.com");
  
  public static final String HISTQUOTES2_COOKIE_OATH_ORIGIN = System.getProperty("yahoofinance.cookie.oathorigin", "https://guce.oath.com");
  
  public static final String HISTQUOTES2_COOKIE_OATH_DONEURL = System.getProperty("yahoofinance.cookie.oathDoneUrl", "https://guce.yahoo.com/copyConsent?sessionId=");
  
  public static final String QUOTES_CSV_DELIMITER = ",";
  
  public static final String TIMEZONE = "America/New_York";
  
  public static final int CONNECTION_TIMEOUT = Integer.parseInt(System.getProperty("yahoofinance.connection.timeout", "10000"));
  
  public static Stock get(String symbol) throws IOException {
    return get(symbol, false);
  }
  
  public static Stock get(String symbol, boolean includeHistorical) throws IOException {
    Map<String, Stock> result = getQuotes(symbol, includeHistorical);
    return result.get(symbol.toUpperCase());
  }
  
  public static Stock get(String symbol, Interval interval) throws IOException {
    return get(symbol, HistQuotesRequest.DEFAULT_FROM, HistQuotesRequest.DEFAULT_TO, interval);
  }
  
  public static Stock get(String symbol, Calendar from) throws IOException {
    return get(symbol, from, HistQuotesRequest.DEFAULT_TO, HistQuotesRequest.DEFAULT_INTERVAL);
  }
  
  public static Stock get(String symbol, Calendar from, Interval interval) throws IOException {
    return get(symbol, from, HistQuotesRequest.DEFAULT_TO, interval);
  }
  
  public static Stock get(String symbol, Calendar from, Calendar to) throws IOException {
    return get(symbol, from, to, HistQuotesRequest.DEFAULT_INTERVAL);
  }
  
  public static Stock get(String symbol, Calendar from, Calendar to, Interval interval) throws IOException {
    Map<String, Stock> result = getQuotes(symbol, from, to, interval);
    return result.get(symbol.toUpperCase());
  }
  
  public static Map<String, Stock> get(String[] symbols) throws IOException {
    return get(symbols, false);
  }
  
  public static Map<String, Stock> get(String[] symbols, boolean includeHistorical) throws IOException {
    return getQuotes(Utils.join(symbols, ","), includeHistorical);
  }
  
  public static Map<String, Stock> get(String[] symbols, Interval interval) throws IOException {
    return getQuotes(Utils.join(symbols, ","), HistQuotesRequest.DEFAULT_FROM, HistQuotesRequest.DEFAULT_TO, interval);
  }
  
  public static Map<String, Stock> get(String[] symbols, Calendar from) throws IOException {
    return getQuotes(Utils.join(symbols, ","), from, HistQuotesRequest.DEFAULT_TO, HistQuotesRequest.DEFAULT_INTERVAL);
  }
  
  public static Map<String, Stock> get(String[] symbols, Calendar from, Interval interval) throws IOException {
    return getQuotes(Utils.join(symbols, ","), from, HistQuotesRequest.DEFAULT_TO, interval);
  }
  
  public static Map<String, Stock> get(String[] symbols, Calendar from, Calendar to) throws IOException {
    return getQuotes(Utils.join(symbols, ","), from, to, HistQuotesRequest.DEFAULT_INTERVAL);
  }
  
  public static Map<String, Stock> get(String[] symbols, Calendar from, Calendar to, Interval interval) throws IOException {
    return getQuotes(Utils.join(symbols, ","), from, to, interval);
  }
  
  public static FxQuote getFx(String symbol) throws IOException {
    if (QUOTES_QUERY1V7_ENABLED.equalsIgnoreCase("true")) {
      FxQuotesQuery1V7Request fxQuotesQuery1V7Request = new FxQuotesQuery1V7Request(symbol);
      return (FxQuote)fxQuotesQuery1V7Request.getSingleResult();
    } 
    FxQuotesRequest request = new FxQuotesRequest(symbol);
    return (FxQuote)request.getSingleResult();
  }
  
  public static Map<String, FxQuote> getFx(String[] symbols) throws IOException {
    List<FxQuote> quotes;
    if (QUOTES_QUERY1V7_ENABLED.equalsIgnoreCase("true")) {
      FxQuotesQuery1V7Request request = new FxQuotesQuery1V7Request(Utils.join(symbols, ","));
      quotes = request.getResult();
    } else {
      FxQuotesRequest request = new FxQuotesRequest(Utils.join(symbols, ","));
      quotes = request.getResult();
    } 
    Map<String, FxQuote> result = new HashMap<>();
    for (FxQuote quote : quotes)
      result.put(quote.getSymbol(), quote); 
    return result;
  }
  
  private static Map<String, Stock> getQuotes(String query, boolean includeHistorical) throws IOException {
    Map<String, Stock> result = new HashMap<>();
    if (QUOTES_QUERY1V7_ENABLED.equalsIgnoreCase("true")) {
      StockQuotesQuery1V7Request request = new StockQuotesQuery1V7Request(query);
      List<Stock> stocks = request.getResult();
      for (Stock stock : stocks)
        result.put(stock.getSymbol(), stock); 
    } else {
      StockQuotesRequest request = new StockQuotesRequest(query);
      List<StockQuotesData> quotes = request.getResult();
      for (StockQuotesData data : quotes) {
        Stock s = data.getStock();
        result.put(s.getSymbol(), s);
      } 
    } 
    if (includeHistorical)
      for (Stock s : result.values())
        s.getHistory();  
    return result;
  }
  
  private static Map<String, Stock> getQuotes(String query, Calendar from, Calendar to, Interval interval) throws IOException {
    Map<String, Stock> stocks = getQuotes(query, false);
    stocks = fetchHistoricalQuotes(stocks, from, to, interval);
    return stocks;
  }
  
  private static Map<String, Stock> fetchHistoricalQuotes(Map<String, Stock> stocks, Calendar from, Calendar to, Interval interval) throws IOException {
    for (Stock s : stocks.values())
      s.getHistory(from, to, interval); 
    return stocks;
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinance\YahooFinance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */