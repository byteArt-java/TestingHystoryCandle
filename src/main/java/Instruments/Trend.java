package Instruments;

import java.util.List;

public class Trend {
    //====���� ����� ����� �������� � ������ ��������� � ����� ������ ����� ����� �����. ����� ������ ����� � StatisData.isBuyTrend.
    //� ����� ������� ���������� = StaticData.rangeCandleForTrend, ��� ��������� �����, � ������� ���� ������� ������� ����� ���������
    //������� ��������� ���� ������, �� ����� �������� �� ����������
    //���� � ����� ��� ������������ ��������� ������, � ������� ��������� ��������� ������ 1 ����� �� �����, �� ������� false
    public boolean findTrend(List<RunCounting.Candle> candleList, RunCounting.Candle candle, int rangeCandleForTrend){//�������� ��������� ������, ����� ��� ������������ ����������� ������
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
