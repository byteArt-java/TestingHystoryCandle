package Instruments;

import DateProviders.StaticData;

import java.util.List;

public class MovingAverage {
    //этот метод надо положить в логику работы, после определения RunCounting.Candle
    //тут берется среднее значение за определенный диапазон свечей, к примеру закрытия, можно установить и открытия
    public static float findMovingAverage(TypeCandleParameter typeCandleParameter, float rangeCandleForMA){
        if (StaticData.candleList.size() < rangeCandleForMA){
            System.out.println("В List<RunCounting.Candle> candleList недостаточно свечей для расчета средней скользящей");
            return 0;
        }
        float amountTotalPrice = 0;
        switch (typeCandleParameter){
            case CLOSE:{
                for (int i = StaticData.candleList.size() - 1; i > (StaticData.candleList.size() - rangeCandleForMA); i--) {
                    amountTotalPrice = amountTotalPrice + StaticData.candleList.get(i).getClose();
                }
                break;
            }
            case HIGH:{
                for (int i = StaticData.candleList.size() - 1; i > (StaticData.candleList.size() - rangeCandleForMA); i--) {
                    amountTotalPrice = amountTotalPrice + StaticData.candleList.get(i).getHigh();
                }
                break;
            }
            case OPEN:{
                for (int i = StaticData.candleList.size() - 1; i > (StaticData.candleList.size() - rangeCandleForMA); i--) {
                    amountTotalPrice = amountTotalPrice + StaticData.candleList.get(i).getOpen();
                }
                break;
            }
            case LOW:{
                for (int i = StaticData.candleList.size() - 1; i > (StaticData.candleList.size() - rangeCandleForMA); i--) {
                    amountTotalPrice = amountTotalPrice + StaticData.candleList.get(i).getLow();
                }
                break;
            }
        }
        return (amountTotalPrice / rangeCandleForMA);
    }
}
