package RunCounting;

import java.util.Date;

public class Deal {
    private Date dateOpen;
    private Date dateClose;
    private float yields;
    private float open;
    private float close;
    private boolean isBuy;

    public Deal(Date dateOpen, Date dateClose, float yields, float open, float price, boolean isBuy) {
        this.dateOpen = dateOpen;
        this.dateClose = dateClose;
        this.yields = yields;
        this.open = open;
        this.close = price;
        this.isBuy = isBuy;
    }

    public Date getDateOpen() {
        return dateOpen;
    }

    public void setDateOpen(Date dateOpen) {
        this.dateOpen = dateOpen;
    }

    public Date getDateClose() {
        return dateClose;
    }

    public void setDateClose(Date dateClose) {
        this.dateClose = dateClose;
    }

    public float getYields() {
        return yields;
    }

    public void setYields(float yields) {
        this.yields = yields;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public boolean getBuy() {
        return isBuy;
    }

    public void setBuy(boolean buy) {
        isBuy = buy;
    }

    @Override public String toString() {
        return "RunCounting.Deal{" + "dateOpen='" + dateOpen + '\'' + ", dateClose='" + dateClose + '\'' + ", yields=" + yields + ", open=" + open + ", price=" + close + ", isBuy=" + isBuy + '}';
    }
}
