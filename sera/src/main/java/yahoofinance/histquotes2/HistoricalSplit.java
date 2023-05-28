package yahoofinance.histquotes2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HistoricalSplit {
  private String symbol;
  
  private Calendar date;
  
  private BigDecimal numerator;
  
  private BigDecimal denominator;
  
  public HistoricalSplit() {}
  
  public HistoricalSplit(String symbol, Calendar date, BigDecimal numerator, BigDecimal denominator) {
    this.symbol = symbol;
    this.date = date;
    this.numerator = numerator;
    this.denominator = denominator;
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
  
  public BigDecimal getNumerator() {
    return this.numerator;
  }
  
  public void setNumerator(BigDecimal numerator) {
    this.numerator = numerator;
  }
  
  public BigDecimal getDenominator() {
    return this.denominator;
  }
  
  public void setDenominator(BigDecimal denominator) {
    this.denominator = denominator;
  }
  
  public BigDecimal getSplitFactor() {
    return this.numerator.divide(this.denominator, 10, RoundingMode.HALF_UP);
  }
  
  public String toString() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String dateStr = dateFormat.format(this.date.getTime());
    return "SPLIT: " + this.symbol + "@" + dateStr + ": " + this.numerator + " / " + this.denominator;
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinance\histquotes2\HistoricalSplit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */