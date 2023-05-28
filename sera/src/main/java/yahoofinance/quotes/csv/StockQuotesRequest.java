package yahoofinance.quotes.csv;

import java.util.ArrayList;
import java.util.List;

public class StockQuotesRequest extends QuotesRequest<StockQuotesData> {
  public static final List<QuotesProperty> DEFAULT_PROPERTIES = new ArrayList<>();
  
  static {
    DEFAULT_PROPERTIES.add(QuotesProperty.Name);
    DEFAULT_PROPERTIES.add(QuotesProperty.Symbol);
    DEFAULT_PROPERTIES.add(QuotesProperty.Currency);
    DEFAULT_PROPERTIES.add(QuotesProperty.StockExchange);
    DEFAULT_PROPERTIES.add(QuotesProperty.Ask);
    DEFAULT_PROPERTIES.add(QuotesProperty.AskRealtime);
    DEFAULT_PROPERTIES.add(QuotesProperty.Symbol);
    DEFAULT_PROPERTIES.add(QuotesProperty.AskSize);
    DEFAULT_PROPERTIES.add(QuotesProperty.Symbol);
    DEFAULT_PROPERTIES.add(QuotesProperty.Bid);
    DEFAULT_PROPERTIES.add(QuotesProperty.BidRealtime);
    DEFAULT_PROPERTIES.add(QuotesProperty.Symbol);
    DEFAULT_PROPERTIES.add(QuotesProperty.BidSize);
    DEFAULT_PROPERTIES.add(QuotesProperty.Symbol);
    DEFAULT_PROPERTIES.add(QuotesProperty.LastTradePriceOnly);
    DEFAULT_PROPERTIES.add(QuotesProperty.Symbol);
    DEFAULT_PROPERTIES.add(QuotesProperty.LastTradeSize);
    DEFAULT_PROPERTIES.add(QuotesProperty.Symbol);
    DEFAULT_PROPERTIES.add(QuotesProperty.LastTradeDate);
    DEFAULT_PROPERTIES.add(QuotesProperty.LastTradeTime);
    DEFAULT_PROPERTIES.add(QuotesProperty.Open);
    DEFAULT_PROPERTIES.add(QuotesProperty.PreviousClose);
    DEFAULT_PROPERTIES.add(QuotesProperty.DaysLow);
    DEFAULT_PROPERTIES.add(QuotesProperty.DaysHigh);
    DEFAULT_PROPERTIES.add(QuotesProperty.Volume);
    DEFAULT_PROPERTIES.add(QuotesProperty.AverageDailyVolume);
    DEFAULT_PROPERTIES.add(QuotesProperty.YearHigh);
    DEFAULT_PROPERTIES.add(QuotesProperty.YearLow);
    DEFAULT_PROPERTIES.add(QuotesProperty.FiftydayMovingAverage);
    DEFAULT_PROPERTIES.add(QuotesProperty.TwoHundreddayMovingAverage);
    DEFAULT_PROPERTIES.add(QuotesProperty.Symbol);
    DEFAULT_PROPERTIES.add(QuotesProperty.SharesOutstanding);
    DEFAULT_PROPERTIES.add(QuotesProperty.Symbol);
    DEFAULT_PROPERTIES.add(QuotesProperty.Symbol);
    DEFAULT_PROPERTIES.add(QuotesProperty.SharesOwned);
    DEFAULT_PROPERTIES.add(QuotesProperty.Symbol);
    DEFAULT_PROPERTIES.add(QuotesProperty.MarketCapitalization);
    DEFAULT_PROPERTIES.add(QuotesProperty.Symbol);
    DEFAULT_PROPERTIES.add(QuotesProperty.SharesFloat);
    DEFAULT_PROPERTIES.add(QuotesProperty.Symbol);
    DEFAULT_PROPERTIES.add(QuotesProperty.DividendPayDate);
    DEFAULT_PROPERTIES.add(QuotesProperty.ExDividendDate);
    DEFAULT_PROPERTIES.add(QuotesProperty.TrailingAnnualDividendYield);
    DEFAULT_PROPERTIES.add(QuotesProperty.TrailingAnnualDividendYieldInPercent);
    DEFAULT_PROPERTIES.add(QuotesProperty.DilutedEPS);
    DEFAULT_PROPERTIES.add(QuotesProperty.EPSEstimateCurrentYear);
    DEFAULT_PROPERTIES.add(QuotesProperty.EPSEstimateNextQuarter);
    DEFAULT_PROPERTIES.add(QuotesProperty.EPSEstimateNextYear);
    DEFAULT_PROPERTIES.add(QuotesProperty.PERatio);
    DEFAULT_PROPERTIES.add(QuotesProperty.PEGRatio);
    DEFAULT_PROPERTIES.add(QuotesProperty.PriceBook);
    DEFAULT_PROPERTIES.add(QuotesProperty.PriceSales);
    DEFAULT_PROPERTIES.add(QuotesProperty.BookValuePerShare);
    DEFAULT_PROPERTIES.add(QuotesProperty.Revenue);
    DEFAULT_PROPERTIES.add(QuotesProperty.EBITDA);
    DEFAULT_PROPERTIES.add(QuotesProperty.OneyrTargetPrice);
    DEFAULT_PROPERTIES.add(QuotesProperty.ShortRatio);
  }
  
  public StockQuotesRequest(String query) {
    super(query, DEFAULT_PROPERTIES);
  }
  
  protected StockQuotesData parseCSVLine(String line) {
    List<String> parsedLine = new ArrayList<>();
    int pos1 = 0;
    int skip = 2;
    int pos2;
    if (line.startsWith("\"")) {
      pos1 = 1;
      pos2 = line.indexOf('"', 1);
    } else {
      pos2 = line.indexOf(",\"");
      skip = 1;
    } 
    String name = line.substring(pos1, pos2);
    pos1 = pos2 + skip;
    pos2 = line.indexOf('"', pos1 + 1);
    skip = 2;
    String fullSymbol = line.substring(pos1, pos2 + 1);
    String symbol = fullSymbol.substring(1, fullSymbol.length() - 1);
    pos1 = pos2 + skip;
    if (line.charAt(pos1) == '"') {
      pos1++;
      pos2 = line.indexOf('"', pos1);
      skip = 2;
    } else {
      pos2 = line.indexOf(',', pos1);
      skip = 1;
    } 
    String currency = line.substring(pos1, pos2);
    pos1 = pos2 + skip;
    if (line.charAt(pos1) == '"') {
      pos1++;
      pos2 = line.indexOf('"', pos1);
      skip = 2;
    } else {
      pos2 = line.indexOf(',', pos1);
      skip = 1;
    } 
    String exchange = line.substring(pos1, pos2);
    parsedLine.add(name);
    parsedLine.add(symbol);
    parsedLine.add(currency);
    parsedLine.add(exchange);
    pos1 = pos2 + skip;
    for (; pos1 < line.length(); pos1++) {
      if (line.startsWith(fullSymbol, pos1)) {
        parsedLine.add(symbol);
        pos1 = pos1 + fullSymbol.length() + 1;
        pos2 = line.indexOf(fullSymbol, pos1) - 1;
        parsedLine.add(line.substring(pos1, pos2));
        parsedLine.add(symbol);
        pos1 = pos2 + fullSymbol.length() + 1;
      } else if (line.charAt(pos1) == '"') {
        pos1++;
        pos2 = line.indexOf('"', pos1);
        parsedLine.add(line.substring(pos1, pos2));
        pos1 = pos2 + 1;
      } else if (line.charAt(pos1) != ',') {
        pos2 = line.indexOf(',', pos1);
        if (pos2 <= pos1)
          pos2 = line.length(); 
        parsedLine.add(line.substring(pos1, pos2));
        pos1 = pos2;
      } 
    } 
    return new StockQuotesData(parsedLine.<String>toArray(new String[this.properties.size()]));
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinance\quotes\csv\StockQuotesRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */