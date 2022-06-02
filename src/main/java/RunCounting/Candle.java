package RunCounting;

import java.util.Date;

public class Candle {
    private String timeCandle;
    private Date dateClose;
    private float open;
    private float high;
    private float low;
    private float close;
    private int volume;

    public Candle(String timeCandle, Date dateClose, float open, float high, float low, float close, int volume) {
        this.timeCandle = timeCandle;
        this.dateClose = dateClose;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public String getTimeCandle() {
        return timeCandle;
    }

    public void setTimeCandle(String timeCandle) {
        this.timeCandle = timeCandle;
    }

    public Date getDateClose() {
        return dateClose;
    }

    public void setDateClose(Date dateClose) {
        this.dateClose = dateClose;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    @Override public String toString() {
        return "timeCandle = " + timeCandle + ", dateClose = " + dateClose +
                ", open = " + open + ", high = " + high + ", low = " + low + ", close = " +
                close + ", volume = " + volume;
    }
}
