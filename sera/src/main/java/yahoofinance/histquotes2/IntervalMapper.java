package yahoofinance.histquotes2;

import yahoofinance.histquotes.Interval;

public class IntervalMapper {
  public static QueryInterval get(Interval interval) {
    switch (interval) {
      case DAILY:
        return QueryInterval.DAILY;
      case WEEKLY:
        return QueryInterval.WEEKLY;
      case MONTHLY:
        return QueryInterval.MONTHLY;
    } 
    return QueryInterval.MONTHLY;
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinance\histquotes2\IntervalMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */