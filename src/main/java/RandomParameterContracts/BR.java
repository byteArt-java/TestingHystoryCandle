package RandomParameterContracts;

import DateProviders.StaticData;

import java.util.Random;

public class BR {
    private static Random random = new Random();

    public static void randomBR(){//82.14 � �������
        StaticData.limitStop = (random.nextInt(50) / 100f) + 0.01f;
        StaticData.minRP = (random.nextInt(50) / 100f) + 0.01f;//������� ��������� �������� �� �����
        StaticData.minMove = (random.nextInt(50) / 100f) + 0.01f;//������� ���������� ����� ������ ���� �� �������� �� ��������
        StaticData.largeMove = (random.nextInt(50) / 100f) + 0.01f;//������� ������ ������, ����� ��������� 2 �������
        StaticData.maxLossTotal = -(random.nextInt(50) / 100f) + 0.01f;
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
