package RandomParameterContracts;

import DateProviders.StaticData;

import java.util.Random;

public class SI{
    private static Random random = new Random();

    public static void randomSI(){
        StaticData.limitStop = random.nextInt(500) + 30;
        StaticData.minRP = random.nextInt(200)*10 + 10;//(minReversePrice)������� ��������� �������� �� �����
        StaticData.minMove = random.nextInt(250)*10 + 10;//������� ���������� ����� ������ ���� �� �������� �� ��������
        StaticData.largeMove = random.nextInt(800)*10 + 10;//������� ������ ������, ����� ��������� 2 �������
        DateProviders.StaticData.maxLossTotal = random.nextInt(1990) - 1000;
        DateProviders.StaticData.minNakedSize = random.nextInt(100)*5 + 5;//����������� �������� ��� �������� ��������
        DateProviders.StaticData.conditionExitLargeCandle = random.nextInt(1000)*20;//�������� ��� ������ ��� ����� �����
        DateProviders.StaticData.bodyMove = random.nextInt(3000) * 10 + 50;//������� ���� ������ �� ������� �� ��������
        DateProviders.StaticData.mainShadow = random.nextInt(2000) * 10 + 50;//�������� ���� ����� �� ������ ��� ����
        DateProviders.StaticData.reverseShadow = random.nextInt(2000) * 10 + 20;//�������� ���� ����� �� ������ ��� ����
        DateProviders.StaticData.coefficientBS = random.nextInt(1000);//����������� ���� > ������������ ����
        StaticData.slipPage = -11;//������ ��������������� ��� �������� ������ ��� ���������������� �����

//        DateProviders.StaticData.rangeList = 0;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ��������� ���� ����. � �����������
//        DateProviders.StaticData.rangeCandleForTrend = 2;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ����� ����������, ����� ������� ������������ �����
//        DateProviders.StaticData.rangeCandleForATR = 5;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ATR-������� �������� ��������-� ����� ��������� ����� ���� ������ ������ ���������
//        DateProviders.StaticData.rangeCandleForMA = 18;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ����
    }
}
