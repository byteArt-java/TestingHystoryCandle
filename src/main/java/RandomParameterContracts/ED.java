package RandomParameterContracts;

import DateProviders.StaticData;

import java.util.Random;

public class ED {
    private static Random random = new Random();

    public static void randomED(){//1.1099 � �������
        StaticData.limitStop = (random.nextInt(50) / 10000f) + 0.0004f;
        StaticData.minRP = (random.nextInt(50) / 10000f) + 0.0001f;//������� ��������� �������� �� �����
        StaticData.minMove = (random.nextInt(150) / 10000f) + 0.0005f;//������� ���������� ����� ������ ���� �� �������� �� ��������
        StaticData.largeMove = (random.nextInt(500) / 10000f) + 0.0100f;//������� ������ ������, ����� ��������� 2 �������
        StaticData.maxLossTotal = -(random.nextInt(500) / 10000f) + 0.0005f;
        StaticData.minNakedSize = random.nextInt(20)*5 + 5;//����������� �������� ��� �������� ��������
        StaticData.conditionExitLargeCandle = random.nextInt(100)*20;//�������� ��� ������ ��� ����� �����
//        StaticData.bodyMove = 0;//������� ���� ������ �� ������� �� ��������
//        StaticData.mainShadow = 0;//�������� ���� ����� �� ������ ��� ����
//        StaticData.reverseShadow = 0;//�������� ���� ����� �� ������ ��� ����
//        StaticData.coefficientBS = 0;//����������� ���� > ������������ ����
//        StaticData.slipPage = -11;//������ ��������������� ��� �������� ������ ��� ���������������� �����
//
//        StaticData.rangeExtremum = 0;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ��������� ���� ����. � �����������
//        StaticData.rangeCandleForTrend = 2;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ����� ����������, ����� ������� ������������ �����
//        StaticData.rangeCandleForATR = 5;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ATR-������� �������� ��������-� ����� ��������� ����� ���� ������ ������ ���������
//        StaticData.rangeCandleForMA = 18;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ����
    }
}
