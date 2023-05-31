package sera.sera;

public class Dividend {
    private int month;
    private int year;
    private int day;
    private String stockID;
    private double divPrice;

    // NEW ATTRIBUTE

    private String divType;

    public String getDivType() {
        return divType;
    }

    public void setDivType(String divType) {
        this.divType = divType;
    }
    public Dividend(Dividend d){
        this.month = d.getMonth();
        this.year = d.getYear();
        this.day = d.getDay();
        this.stockID = d.getStockID();
        this.divPrice = d.getDivPrice();
        if (d.getDivType()!=null){
            this.divType = d.getDivType();
        }
    }
    public Dividend(int month, int year, int day, String stockID, double divPrice) {
        this.month = month;
        this.year = year;
        this.day = day;
        this.stockID = stockID;
        this.divPrice = divPrice;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getStockID() {
        return stockID;
    }

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    public double getDivPrice() {
        return divPrice;
    }

    public void setDivPrice(double divPrice) {
        this.divPrice = divPrice;
    }
}
