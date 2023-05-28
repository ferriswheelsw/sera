package yahoofinance.quotes.query1v7;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import yahoofinance.Utils;
import yahoofinance.quotes.fx.FxQuote;

public class FxQuotesQuery1V7Request extends QuotesRequest<FxQuote> {
  public FxQuotesQuery1V7Request(String symbols) {
    super(symbols);
  }
  
  protected FxQuote parseJson(JsonNode node) {
    String symbol = node.get("symbol").asText();
    BigDecimal price = Utils.getBigDecimal(node.get("regularMarketPrice").asText());
    return new FxQuote(symbol, price);
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinance\quotes\query1v7\FxQuotesQuery1V7Request.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */