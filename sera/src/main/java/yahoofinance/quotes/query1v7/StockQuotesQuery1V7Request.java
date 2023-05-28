package yahoofinance.quotes.query1v7;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.util.TimeZone;
import yahoofinance.Stock;
import yahoofinance.Utils;
import yahoofinance.exchanges.ExchangeTimeZone;
import yahoofinance.quotes.stock.StockDividend;
import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.quotes.stock.StockStats;

public class StockQuotesQuery1V7Request extends QuotesRequest<Stock> {
  private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
  
  public StockQuotesQuery1V7Request(String symbols) {
    super(symbols);
  }
  
  protected Stock parseJson(JsonNode node) {
    String symbol = node.get("symbol").asText();
    Stock stock = new Stock(symbol);
    if (node.has("longName")) {
      stock.setName(node.get("longName").asText());
    } else {
      stock.setName(getStringValue(node, "shortName"));
    } 
    stock.setCurrency(getStringValue(node, "currency"));
    stock.setStockExchange(getStringValue(node, "fullExchangeName"));
    stock.setQuote(getQuote(node));
    stock.setStats(getStats(node));
    stock.setDividend(getDividend(node));
    return stock;
  }
  
  private String getStringValue(JsonNode node, String field) {
    if (node.has(field))
      return node.get(field).asText(); 
    return null;
  }
  
  private StockQuote getQuote(JsonNode node) {
    String symbol = node.get("symbol").asText();
    StockQuote quote = new StockQuote(symbol);
    quote.setPrice(Utils.getBigDecimal(getStringValue(node, "regularMarketPrice")));
    quote.setAsk(Utils.getBigDecimal(getStringValue(node, "ask")));
    quote.setAskSize(Utils.getLong(getStringValue(node, "askSize")));
    quote.setBid(Utils.getBigDecimal(getStringValue(node, "bid")));
    quote.setBidSize(Utils.getLong(getStringValue(node, "bidSize")));
    quote.setOpen(Utils.getBigDecimal(getStringValue(node, "regularMarketOpen")));
    quote.setPreviousClose(Utils.getBigDecimal(getStringValue(node, "regularMarketPreviousClose")));
    quote.setDayHigh(Utils.getBigDecimal(getStringValue(node, "regularMarketDayHigh")));
    quote.setDayLow(Utils.getBigDecimal(getStringValue(node, "regularMarketDayLow")));
    if (node.has("exchangeTimezoneName")) {
      quote.setTimeZone(TimeZone.getTimeZone(node.get("exchangeTimezoneName").asText()));
    } else {
      quote.setTimeZone(ExchangeTimeZone.getStockTimeZone(symbol));
    } 
    if (node.has("regularMarketTime"))
      quote.setLastTradeTime(Utils.unixToCalendar(node.get("regularMarketTime").asLong())); 
    quote.setYearHigh(Utils.getBigDecimal(getStringValue(node, "fiftyTwoWeekHigh")));
    quote.setYearLow(Utils.getBigDecimal(getStringValue(node, "fiftyTwoWeekLow")));
    quote.setPriceAvg50(Utils.getBigDecimal(getStringValue(node, "fiftyDayAverage")));
    quote.setPriceAvg200(Utils.getBigDecimal(getStringValue(node, "twoHundredDayAverage")));
    quote.setVolume(Utils.getLong(getStringValue(node, "regularMarketVolume")));
    quote.setAvgVolume(Utils.getLong(getStringValue(node, "averageDailyVolume3Month")));
    return quote;
  }
  
  private StockStats getStats(JsonNode node) {
    String symbol = getStringValue(node, "symbol");
    StockStats stats = new StockStats(symbol);
    stats.setMarketCap(Utils.getBigDecimal(getStringValue(node, "marketCap")));
    stats.setSharesOutstanding(Utils.getLong(getStringValue(node, "sharesOutstanding")));
    stats.setEps(Utils.getBigDecimal(getStringValue(node, "epsTrailingTwelveMonths")));
    stats.setPe(Utils.getBigDecimal(getStringValue(node, "trailingPE")));
    stats.setEpsEstimateCurrentYear(Utils.getBigDecimal(getStringValue(node, "epsForward")));
    stats.setPriceBook(Utils.getBigDecimal(getStringValue(node, "priceToBook")));
    stats.setBookValuePerShare(Utils.getBigDecimal(getStringValue(node, "bookValue")));
    if (node.has("earningsTimestamp"))
      stats.setEarningsAnnouncement(Utils.unixToCalendar(node.get("earningsTimestamp").asLong())); 
    return stats;
  }
  
  private StockDividend getDividend(JsonNode node) {
    String symbol = getStringValue(node, "symbol");
    StockDividend dividend = new StockDividend(symbol);
    if (node.has("dividendDate")) {
      long dividendTimestamp = node.get("dividendDate").asLong();
      dividend.setPayDate(Utils.unixToCalendar(dividendTimestamp));
    } 
    if (node.has("trailingAnnualDividendRate"))
      dividend.setAnnualYield(Utils.getBigDecimal(getStringValue(node, "trailingAnnualDividendRate"))); 
    if (node.has("trailingAnnualDividendYield")) {
      BigDecimal yield = Utils.getBigDecimal(getStringValue(node, "trailingAnnualDividendYield"));
      if (yield != null)
        dividend.setAnnualYieldPercent(yield.multiply(ONE_HUNDRED)); 
    } 
    return dividend;
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinance\quotes\query1v7\StockQuotesQuery1V7Request.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */