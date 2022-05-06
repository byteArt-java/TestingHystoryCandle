import java.util.ArrayList;
import java.util.List;

public class StaticData {
    public static float limitStop = 330;//���� ����//default 270

    public static boolean isBuy = true;//������� ��� ������� �� �������� ��������

    public static float rangeYields = 0;//������ �������, ����� �������� ���� ����� �� ����� � ������ � ����

    public static int oneMethod = 0;//������� ���� ������, �� ����� ������� �������� � ConditionClose ��� ������ �������,��������������� � ConditionOpen
    public static int twoMethod = 0;//������� ���� ������, �� ����� ������� �������� � ConditionClose ��� ������ �������,��������������� � ConditionOpen
    public static int threeMethod = 0;//������� ���� ������, �� ����� ������� �������� � ConditionClose ��� ������ �������,��������������� � ConditionOpen

    public static float minRP = 220;//(minReversePrice)������� ��������� �������� �� �����//default 110
    public static float minMove = 310;//������� ���������� ����� ������ ���� �� �������� �� ��������//default 150
    public static float largeMove = 1730;//������� ������ ������, ����� ��������� 2 �������//default 900
    public static float minNakedSize = 10;//����������� �������� ��� �������� ��������//default 10

    public static List<Candle> candleList = new ArrayList<>();//���� ��� �������� ����������� ������
    public static int countCandleOpenPosition = 0;//����� ��� ����, ����� �������� ������� ������ ����� 1 ���������� �����
    public static boolean isOpenDeal = false;//������� �� ������

    public static List<Float> maxDescendingMoneyList = new ArrayList<>();//������������ ��������
    public static float maxDescendingMoney = 0;//������������ ��������
}
