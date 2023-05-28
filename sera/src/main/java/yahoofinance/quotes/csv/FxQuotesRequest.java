package yahoofinance.quotes.csv;

import java.util.ArrayList;
import java.util.List;
import yahoofinance.Utils;
import yahoofinance.quotes.fx.FxQuote;

public class FxQuotesRequest extends QuotesRequest<FxQuote> {
  public static final List<QuotesProperty> DEFAULT_PROPERTIES = new ArrayList<>();
  
  static {
    DEFAULT_PROPERTIES.add(QuotesProperty.Symbol);
    DEFAULT_PROPERTIES.add(QuotesProperty.LastTradePriceOnly);
  }
  
  public FxQuotesRequest(String query) {
    super(query, DEFAULT_PROPERTIES);
  }
  
  protected FxQuote parseCSVLine(String line) {
    String[] split = Utils.stripOverhead(line).split(",");
    if (split.length >= 2)
      return new FxQuote(split[0], Utils.getBigDecimal(split[1])); 
    return null;
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinance\quotes\csv\FxQuotesRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */