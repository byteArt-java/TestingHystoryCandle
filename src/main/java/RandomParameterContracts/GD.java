package RandomParameterContracts;

import DateProviders.StaticData;

import java.util.Random;

public class GD {
    private static Random random = new Random();

    public static void randomGD(){//1900.1 � �������
        StaticData.limitStop = random.nextFloat() * 10;
        StaticData.minRP = random.nextFloat() * 10;//������� ��������� �������� �� �����
        StaticData.minMove = random.nextFloat() * 10;//������� ���������� ����� ������ ���� �� �������� �� ��������
        StaticData.largeMove = random.nextFloat() * 10;//������� ������ ������, ����� ��������� 2 �������
        StaticData.maxLossTotal = random.nextFloat() * 10;
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
