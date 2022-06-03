package RandomParameterContracts;

import DateProviders.StaticData;

import java.util.Random;

public class RI{
    private static Random random = new Random();

    public static void randomRI(){
        StaticData.limitStop = random.nextInt(200) + 30;
        StaticData.minRP = random.nextInt(50)*10 + 10;//(minReversePrice)������� ��������� �������� �� �����
        StaticData.minMove = random.nextInt(150)*10 + 10;//������� ���������� ����� ������ ���� �� �������� �� ��������
        StaticData.largeMove = random.nextInt(400)*10 + 10;//������� ������ ������, ����� ��������� 2 �������
        StaticData.maxLossTotal = random.nextInt(990) - 1000;
        StaticData.minNakedSize = random.nextInt(20)*5 + 5;//����������� �������� ��� �������� ��������
        StaticData.conditionExitLargeCandle = random.nextInt(100)*20;//�������� ��� ������ ��� ����� �����
        StaticData.bodyMove = 0;//������� ���� ������ �� ������� �� ��������
        StaticData.mainShadow = 0;//�������� ���� ����� �� ������ ��� ����
        StaticData.reverseShadow = 0;//�������� ���� ����� �� ������ ��� ����
        StaticData.coefficientBS = 0;//����������� ���� > ������������ ����
        StaticData.slipPage = -11;//������ ��������������� ��� �������� ������ ��� ���������������� �����

        StaticData.rangeExtremum = 0;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ��������� ���� ����. � �����������
        StaticData.rangeCandleForTrend = 2;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ����� ����������, ����� ������� ������������ �����
        StaticData.rangeCandleForATR = 5;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ATR-������� �������� ��������-� ����� ��������� ����� ���� ������ ������ ���������
        StaticData.rangeCandleForMA = 18;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ����

    }
}
