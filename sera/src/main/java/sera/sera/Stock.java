package sera.sera;

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;

public class Stock {
    private String stockCode;
    private String market;
    private double marketPrice;
    private ArrayList<Dividend> dividends;
    private double percentdiv;
    private double totaldiv;
    private int divfreq;
    private double priceChange;


    private double pnl;
    private int holdings;

    // NEW ATTRIBUTES
    private Dividend lastDiv;
    private int gap;

    private String stockCur;

    private Dividend payDiv;

    public Dividend getPayDiv() {
        return payDiv;
    }

    public void setPayDiv(Dividend payDiv) {
        this.payDiv = payDiv;
    }

    public String getStockCur() {
        return stockCur;
    }

    public void setStockCur(String stockCur) {
        this.stockCur = stockCur;
    }

    public Dividend getLastDiv() {
        return lastDiv;
    }

    public void setLastDiv(Dividend lastDiv) {
        this.lastDiv = lastDiv;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }



    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public ArrayList<Dividend> getDividends() {
        return dividends;
    }

    public void setDividends(ArrayList<Dividend> dividends) {
        this.dividends = dividends;
    }

    public double getPercentdiv() {
        return percentdiv;
    }

    public void setPercentdiv(double percentdiv) {
        this.percentdiv = percentdiv;
    }

    public double getTotaldiv() {
        return totaldiv;
    }

    public void setTotaldiv(double totaldiv) {
        this.totaldiv = totaldiv;
    }

    public int getDivfreq() {
        return divfreq;
    }

    public void setDivfreq(int divfreq) {
        this.divfreq = divfreq;
    }

    public double getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(double priceChange) {
        this.priceChange = priceChange;
    }

    public double getPnl() {
        return pnl;
    }

    public void setPnl(double pnl) {
        this.pnl = pnl;
    }

    public int getHoldings() {
        return holdings;
    }

    public void setHoldings(int holdings) {
        this.holdings = holdings;
    }

    public Stock(String stockCode, String market, int holdings) {
        this.stockCode = stockCode;
        this.market = market;
        this.holdings = holdings;
    }

    public Stock(Stock stock){
        this.stockCode = stock.getStockCode();
        this.market = stock.getMarket();
        this.holdings = stock.getHoldings();
        this.marketPrice = stock.getMarketPrice();
        this.dividends = stock.getDividends();
        this.percentdiv = stock.getPercentdiv();
        this.totaldiv = stock.getTotaldiv();
        this.divfreq = stock.getDivfreq();
        this.priceChange = stock.getPriceChange();
        this.pnl = stock.getPnl();
        this.gap = stock.getGap();
        this.stockCur = stock.getStockCur();
        if (stock.getLastDiv()!= null){
            this.lastDiv = stock.getLastDiv();
        }
        if(stock.getPayDiv()!=null){
            this.payDiv = stock.getPayDiv();
        }

    }

}
