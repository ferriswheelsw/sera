package yahoofinance.quotes.stock;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.TimeZone;
import yahoofinance.Utils;

public class StockQuote {
  private final String symbol;
  
  private TimeZone timeZone;
  
  private BigDecimal ask;
  
  private Long askSize;
  
  private BigDecimal bid;
  
  private Long bidSize;
  
  private BigDecimal price;
  
  private Long lastTradeSize;
  
  private String lastTradeDateStr;
  
  private String lastTradeTimeStr;
  
  private Calendar lastTradeTime;
  
  private BigDecimal open;
  
  private BigDecimal previousClose;
  
  private BigDecimal dayLow;
  
  private BigDecimal dayHigh;
  
  private BigDecimal yearLow;
  
  private BigDecimal yearHigh;
  
  private BigDecimal priceAvg50;
  
  private BigDecimal priceAvg200;
  
  private Long volume;
  
  private Long avgVolume;
  
  public StockQuote(String symbol) {
    this.symbol = symbol;
  }
  
  public BigDecimal getChange() {
    if (this.price == null || this.previousClose == null)
      return null; 
    return this.price.subtract(this.previousClose);
  }
  
  public BigDecimal getChangeInPercent() {
    return Utils.getPercent(getChange(), this.previousClose);
  }
  
  public BigDecimal getChangeFromYearLow() {
    if (this.price == null || this.yearLow == null)
      return null; 
    return this.price.subtract(this.yearLow);
  }
  
  public BigDecimal getChangeFromYearLowInPercent() {
    return Utils.getPercent(getChangeFromYearLow(), this.yearLow);
  }
  
  public BigDecimal getChangeFromYearHigh() {
    if (this.price == null || this.yearHigh == null)
      return null; 
    return this.price.subtract(this.yearHigh);
  }
  
  public BigDecimal getChangeFromYearHighInPercent() {
    return Utils.getPercent(getChangeFromYearHigh(), this.yearHigh);
  }
  
  public BigDecimal getChangeFromAvg50() {
    if (this.price == null || this.priceAvg50 == null)
      return null; 
    return this.price.subtract(this.priceAvg50);
  }
  
  public BigDecimal getChangeFromAvg50InPercent() {
    return Utils.getPercent(getChangeFromAvg50(), this.priceAvg50);
  }
  
  public BigDecimal getChangeFromAvg200() {
    if (this.price == null || this.priceAvg200 == null)
      return null; 
    return this.price.subtract(this.priceAvg200);
  }
  
  public BigDecimal getChangeFromAvg200InPercent() {
    return Utils.getPercent(getChangeFromAvg200(), this.priceAvg200);
  }
  
  public String getSymbol() {
    return this.symbol;
  }
  
  public BigDecimal getAsk() {
    return this.ask;
  }
  
  public void setAsk(BigDecimal ask) {
    this.ask = ask;
  }
  
  public Long getAskSize() {
    return this.askSize;
  }
  
  public void setAskSize(Long askSize) {
    this.askSize = askSize;
  }
  
  public BigDecimal getBid() {
    return this.bid;
  }
  
  public void setBid(BigDecimal bid) {
    this.bid = bid;
  }
  
  public Long getBidSize() {
    return this.bidSize;
  }
  
  public void setBidSize(Long bidSize) {
    this.bidSize = bidSize;
  }
  
  public BigDecimal getPrice() {
    return this.price;
  }
  
  public void setPrice(BigDecimal price) {
    this.price = price;
  }
  
  public Long getLastTradeSize() {
    return this.lastTradeSize;
  }
  
  public void setLastTradeSize(Long lastTradeSize) {
    this.lastTradeSize = lastTradeSize;
  }
  
  public String getLastTradeDateStr() {
    return this.lastTradeDateStr;
  }
  
  public void setLastTradeDateStr(String lastTradeDateStr) {
    this.lastTradeDateStr = lastTradeDateStr;
  }
  
  public String getLastTradeTimeStr() {
    return this.lastTradeTimeStr;
  }
  
  public void setLastTradeTimeStr(String lastTradeTimeStr) {
    this.lastTradeTimeStr = lastTradeTimeStr;
  }
  
  public Calendar getLastTradeTime() {
    return this.lastTradeTime;
  }
  
  public void setLastTradeTime(Calendar lastTradeTime) {
    this.lastTradeTime = lastTradeTime;
  }
  
  public Calendar getLastTradeTime(TimeZone timeZone) {
    return Utils.parseDateTime(this.lastTradeDateStr, this.lastTradeTimeStr, timeZone);
  }
  
  public TimeZone getTimeZone() {
    return this.timeZone;
  }
  
  public void setTimeZone(TimeZone timeZone) {
    this.timeZone = timeZone;
  }
  
  public BigDecimal getOpen() {
    return this.open;
  }
  
  public void setOpen(BigDecimal open) {
    this.open = open;
  }
  
  public BigDecimal getPreviousClose() {
    return this.previousClose;
  }
  
  public void setPreviousClose(BigDecimal previousClose) {
    this.previousClose = previousClose;
  }
  
  public BigDecimal getDayLow() {
    return this.dayLow;
  }
  
  public void setDayLow(BigDecimal dayLow) {
    this.dayLow = dayLow;
  }
  
  public BigDecimal getDayHigh() {
    return this.dayHigh;
  }
  
  public void setDayHigh(BigDecimal dayHigh) {
    this.dayHigh = dayHigh;
  }
  
  public BigDecimal getYearLow() {
    return this.yearLow;
  }
  
  public void setYearLow(BigDecimal yearLow) {
    this.yearLow = yearLow;
  }
  
  public BigDecimal getYearHigh() {
    return this.yearHigh;
  }
  
  public void setYearHigh(BigDecimal yearHigh) {
    this.yearHigh = yearHigh;
  }
  
  public BigDecimal getPriceAvg50() {
    return this.priceAvg50;
  }
  
  public void setPriceAvg50(BigDecimal priceAvg50) {
    this.priceAvg50 = priceAvg50;
  }
  
  public BigDecimal getPriceAvg200() {
    return this.priceAvg200;
  }
  
  public void setPriceAvg200(BigDecimal priceAvg200) {
    this.priceAvg200 = priceAvg200;
  }
  
  public Long getVolume() {
    return this.volume;
  }
  
  public void setVolume(Long volume) {
    this.volume = volume;
  }
  
  public Long getAvgVolume() {
    return this.avgVolume;
  }
  
  public void setAvgVolume(Long avgVolume) {
    this.avgVolume = avgVolume;
  }
  
  public String toString() {
    return "Ask: " + this.ask + ", Bid: " + this.bid + ", Price: " + this.price + ", Prev close: " + this.previousClose;
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinance\quotes\stock\StockQuote.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */