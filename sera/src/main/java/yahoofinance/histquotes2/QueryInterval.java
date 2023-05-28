package yahoofinance.histquotes2;

public enum QueryInterval {
  DAILY("1d"),
  WEEKLY("5d"),
  MONTHLY("1mo");
  
  private final String tag;
  
  QueryInterval(String tag) {
    this.tag = tag;
  }
  
  public String getTag() {
    return this.tag;
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinance\histquotes2\QueryInterval.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */