package yahoofinance.quotes.stock;

import java.math.BigDecimal;
import java.util.Calendar;
import yahoofinance.Utils;

public class StockStats {
  private final String symbol;
  
  private BigDecimal marketCap;
  
  private Long sharesFloat;
  
  private Long sharesOutstanding;
  
  private Long sharesOwned;
  
  private BigDecimal eps;
  
  private BigDecimal pe;
  
  private BigDecimal peg;
  
  private BigDecimal epsEstimateCurrentYear;
  
  private BigDecimal epsEstimateNextQuarter;
  
  private BigDecimal epsEstimateNextYear;
  
  private BigDecimal priceBook;
  
  private BigDecimal priceSales;
  
  private BigDecimal bookValuePerShare;
  
  private BigDecimal revenue;
  
  private BigDecimal EBITDA;
  
  private BigDecimal oneYearTargetPrice;
  
  private BigDecimal shortRatio;
  
  private Calendar earningsAnnouncement;
  
  public StockStats(String symbol) {
    this.symbol = symbol;
  }
  
  public BigDecimal getROE() {
    return Utils.getPercent(this.EBITDA, this.marketCap);
  }
  
  public String getSymbol() {
    return this.symbol;
  }
  
  public BigDecimal getMarketCap() {
    return this.marketCap;
  }
  
  public void setMarketCap(BigDecimal marketCap) {
    this.marketCap = marketCap;
  }
  
  public Long getSharesFloat() {
    return this.sharesFloat;
  }
  
  public void setSharesFloat(Long sharesFloat) {
    this.sharesFloat = sharesFloat;
  }
  
  public Long getSharesOutstanding() {
    return this.sharesOutstanding;
  }
  
  public void setSharesOutstanding(Long sharesOutstanding) {
    this.sharesOutstanding = sharesOutstanding;
  }
  
  public Long getSharesOwned() {
    return this.sharesOwned;
  }
  
  public void setSharesOwned(Long sharesOwned) {
    this.sharesOwned = sharesOwned;
  }
  
  public BigDecimal getEps() {
    return this.eps;
  }
  
  public void setEps(BigDecimal eps) {
    this.eps = eps;
  }
  
  public BigDecimal getPe() {
    return this.pe;
  }
  
  public void setPe(BigDecimal pe) {
    this.pe = pe;
  }
  
  public BigDecimal getPeg() {
    return this.peg;
  }
  
  public void setPeg(BigDecimal peg) {
    this.peg = peg;
  }
  
  public BigDecimal getEpsEstimateCurrentYear() {
    return this.epsEstimateCurrentYear;
  }
  
  public void setEpsEstimateCurrentYear(BigDecimal epsEstimateCurrentYear) {
    this.epsEstimateCurrentYear = epsEstimateCurrentYear;
  }
  
  public BigDecimal getEpsEstimateNextQuarter() {
    return this.epsEstimateNextQuarter;
  }
  
  public void setEpsEstimateNextQuarter(BigDecimal epsEstimateNextQuarter) {
    this.epsEstimateNextQuarter = epsEstimateNextQuarter;
  }
  
  public BigDecimal getEpsEstimateNextYear() {
    return this.epsEstimateNextYear;
  }
  
  public void setEpsEstimateNextYear(BigDecimal epsEstimateNextYear) {
    this.epsEstimateNextYear = epsEstimateNextYear;
  }
  
  public BigDecimal getPriceBook() {
    return this.priceBook;
  }
  
  public void setPriceBook(BigDecimal priceBook) {
    this.priceBook = priceBook;
  }
  
  public BigDecimal getPriceSales() {
    return this.priceSales;
  }
  
  public void setPriceSales(BigDecimal priceSales) {
    this.priceSales = priceSales;
  }
  
  public BigDecimal getBookValuePerShare() {
    return this.bookValuePerShare;
  }
  
  public void setBookValuePerShare(BigDecimal bookValuePerShare) {
    this.bookValuePerShare = bookValuePerShare;
  }
  
  public BigDecimal getRevenue() {
    return this.revenue;
  }
  
  public void setRevenue(BigDecimal revenue) {
    this.revenue = revenue;
  }
  
  public BigDecimal getEBITDA() {
    return this.EBITDA;
  }
  
  public void setEBITDA(BigDecimal EBITDA) {
    this.EBITDA = EBITDA;
  }
  
  public BigDecimal getOneYearTargetPrice() {
    return this.oneYearTargetPrice;
  }
  
  public void setOneYearTargetPrice(BigDecimal oneYearTargetPrice) {
    this.oneYearTargetPrice = oneYearTargetPrice;
  }
  
  public BigDecimal getShortRatio() {
    return this.shortRatio;
  }
  
  public void setShortRatio(BigDecimal shortRatio) {
    this.shortRatio = shortRatio;
  }
  
  public Calendar getEarningsAnnouncement() {
    return this.earningsAnnouncement;
  }
  
  public void setEarningsAnnouncement(Calendar earningsAnnouncement) {
    this.earningsAnnouncement = earningsAnnouncement;
  }
  
  public String toString() {
    String earningsStr = "/";
    if (this.earningsAnnouncement != null)
      earningsStr = this.earningsAnnouncement.getTime().toString(); 
    return "EPS: " + this.eps + ", PE: " + this.pe + ", Earnings announcement: " + earningsStr;
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinance\quotes\stock\StockStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */