package RandomParameterContracts;

import DateProviders.StaticData;

import java.util.Random;

public class SI{
    private static Random random = new Random();

    public static void randomSI(){
        StaticData.limitStop = random.nextInt(90) * 10 + 30;
        StaticData.minRP = random.nextInt(100) * 10 + 10;//(minReversePrice)������� ��������� �������� �� �����
        StaticData.minMove = random.nextInt(700) * 10 + 10;//������� ���������� ����� ������ ���� �� �������� �� ��������
        StaticData.largeMove = random.nextInt(700)*10 + 10;//������� ������ ������, ����� ��������� 2 �������
        DateProviders.StaticData.maxLossTotal = random.nextInt(1500) - 1000;
//        DateProviders.StaticData.minNakedSize = random.nextInt(10)*5 + 5;//����������� �������� ��� �������� ��������
//        DateProviders.StaticData.conditionExitLargeCandle = random.nextInt(100)*20;//�������� ��� ������ ��� ����� �����
//        DateProviders.StaticData.bodyMove = random.nextInt(300) * 10 + 50;//������� ���� ������ �� ������� �� ��������
//        DateProviders.StaticData.mainShadow = random.nextInt(200) * 10 + 50;//�������� ���� ����� �� ������ ��� ����
//        DateProviders.StaticData.reverseShadow = random.nextInt(200) * 10 + 20;//�������� ���� ����� �� ������ ��� ����
//        DateProviders.StaticData.coefficientBS = random.nextInt(100);//����������� ���� > ������������ ����
        StaticData.slipPage = -11;//������ ��������������� ��� �������� ������ ��� ���������������� �����

//        DateProviders.StaticData.rangeList = 0;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ��������� ���� ����. � �����������
//        DateProviders.StaticData.rangeCandleForTrend = 2;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ����� ����������, ����� ������� ������������ �����
//        DateProviders.StaticData.rangeCandleForATR = 5;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ATR-������� �������� ��������-� ����� ��������� ����� ���� ������ ������ ���������
//        DateProviders.StaticData.rangeCandleForMA = 18;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ����
    }
}
