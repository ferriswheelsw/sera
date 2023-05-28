package yahoofinance.histquotes2;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HistoricalDividend {
  private String symbol;
  
  private Calendar date;
  
  private BigDecimal adjDividend;
  
  public HistoricalDividend() {}
  
  public HistoricalDividend(String symbol, Calendar date, BigDecimal adjDividend) {
    this.symbol = symbol;
    this.date = date;
    this.adjDividend = adjDividend;
  }
  
  public String getSymbol() {
    return this.symbol;
  }
  
  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }
  
  public Calendar getDate() {
    return this.date;
  }
  
  public void setDate(Calendar date) {
    this.date = date;
  }
  
  public BigDecimal getAdjDividend() {
    return this.adjDividend;
  }
  
  public void setAdjDividend(BigDecimal adjDividend) {
    this.adjDividend = adjDividend;
  }
  
  public String toString() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String dateStr = dateFormat.format(this.date.getTime());
    return "DIVIDEND: " + this.symbol + "@" + dateStr + ": " + this.adjDividend;
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinance\histquotes2\HistoricalDividend.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */