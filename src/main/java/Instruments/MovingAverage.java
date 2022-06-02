package Instruments;

import DateProviders.StaticData;

import java.util.List;

public class MovingAverage {
    //���� ����� ���� �������� � ������ ������, ����� ����������� RunCounting.Candle
    //��� ������� ������� �������� �� ������������ �������� ������, � ������� ��������, ����� ���������� � ��������
    public static float findMovingAverage(TypeCandleParameter typeCandleParameter, float rangeCandleForMA){
        if (StaticData.candleList.size() < rangeCandleForMA){
            System.out.println("� List<RunCounting.Candle> candleList ������������ ������ ��� ������� ������� ����������");
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
