package Instruments;

import DateProviders.StaticData;

import java.util.List;

public class ATR {
    //���� ����� ���� �������� � ������ ������, ����� ����������� Candle
    //��� ����������� �������� � ��� ����������� � ���� ����� ������ ����������� �������������, ������ ��� � ����������� ATR
    public static float findATR(RunCounting.Candle candle){
        if (StaticData.candleList.size() < StaticData.rangeCandleForATR){
//            System.out.println("� List<Candle> candleList ������������ ������ ��� ������� ATR");
            return 0;
        }
        float low = 0;//����� ���� ������� ���������
        float high = 0;//����� ���� ������� ���������

        for (int i = StaticData.candleList.size() - 1; i > (StaticData.candleList.size() - StaticData.rangeCandleForATR); i--) {
            low = low + StaticData.candleList.get(i).getLow();
            high = high + StaticData.candleList.get(i).getHigh();
        }
        return Math.abs((high - low) / StaticData.rangeCandleForATR);
    }
}
