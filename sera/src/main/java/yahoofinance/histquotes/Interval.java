package yahoofinance.histquotes;

public enum Interval {
  DAILY("d"),
  WEEKLY("w"),
  MONTHLY("m");
  
  private final String tag;
  
  Interval(String tag) {
    this.tag = tag;
  }
  
  public String getTag() {
    return this.tag;
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinance\histquotes\Interval.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */