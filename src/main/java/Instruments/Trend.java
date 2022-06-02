package Instruments;

import java.util.List;

public class Trend {
    //====этот метод нужно вставить в логику программы в самое начало после опред свечи. Метод кладет тренд в StatisData.isBuyTrend.
    //а также берется количество = StaticData.rangeCandleForTrend, как последнии свечи, к примеру если текущий минимум свечи пробивает
    //минимум последних двух свечей, то тренд меняется на нисходящий
    //если в Листе еще недостаточно закинутых свечей, к примеру программа прочитала только 1 свечу из файла, то возврат false
    public boolean findTrend(List<RunCounting.Candle> candleList, RunCounting.Candle candle, int rangeCandleForTrend){//диапазон последних свечей, после кот определяется направление тренда
        if (candleList.size() < rangeCandleForTrend){
            return false;
        }
        boolean isBuyTrend = true;
        boolean isSellTrend = true;
        for (int i = candleList.size() - 1; i > (candleList.size() - rangeCandleForTrend); i--) {
            isBuyTrend &= candleList.get(i).getHigh() < candle.getHigh();
            isSellTrend &= candleList.get(i).getLow() > candle.getLow();
        }
        if (isBuyTrend && isSellTrend){
            return false;
        }
        if (isBuyTrend){
            DateProviders.StaticData.isBuyTrend = true;
            return true;
        }
        if (isSellTrend){
            DateProviders.StaticData.isBuyTrend = false;
            return true;
        }
        return false;
    }
}
