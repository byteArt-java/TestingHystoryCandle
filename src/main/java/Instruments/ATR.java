package Instruments;

import DateProviders.StaticData;

import java.util.List;

public class ATR {
    //этот метод надо положить в логику работы, после определения Candle
    //тут проверяется движение в оба направления и если сумма одного направления положительная, значит его и высчитываем ATR
    public static float findATR(RunCounting.Candle candle){
        if (StaticData.candleList.size() < StaticData.rangeCandleForATR){
//            System.out.println("В List<Candle> candleList недостаточно свечей для расчета ATR");
            return 0;
        }
        float low = 0;//старт цены отсчета диапазона
        float high = 0;//конец цены отсчета диапазона

        for (int i = StaticData.candleList.size() - 1; i > (StaticData.candleList.size() - StaticData.rangeCandleForATR); i--) {
            low = low + StaticData.candleList.get(i).getLow();
            high = high + StaticData.candleList.get(i).getHigh();
        }
        return Math.abs((high - low) / StaticData.rangeCandleForATR);
    }
}
