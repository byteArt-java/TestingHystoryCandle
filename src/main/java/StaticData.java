import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StaticData {
    public static float limitStop = 270;//���� ����//default 270

    public static boolean isBuy = true;//������� ��� ������� �� �������� ��������

    public static float rangeYields = -500000;//������ �������, ����� �������� ���� ����� �� ����� � ������ � ����

    public static int oneMethod = 0;//������� ���� ������, �� ����� ������� �������� � ConditionClose ��� ������ �������,��������������� � ConditionOpen
    public static int twoMethod = 0;//������� ���� ������, �� ����� ������� �������� � ConditionClose ��� ������ �������,��������������� � ConditionOpen
    public static int threeMethod = 0;//������� ���� ������, �� ����� ������� �������� � ConditionClose ��� ������ �������,��������������� � ConditionOpen

    public static float minRP = 110;//(minReversePrice)������� ��������� �������� �� �����//default 110
    public static float minMove = 150;//������� ���������� ����� ������ ���� �� �������� �� ��������//default 150
    public static float largeMove = 900;//������� ������ ������, ����� ��������� 2 �������//default 900
    public static float minNakedSize = 10;//����������� �������� ��� �������� ��������//default 10

    public static List<Deal> positiveRes = new ArrayList<>();//list ��� �������� ����������� ������������� ������
    public static List<Deal> negativeRes = new ArrayList<>();//list ��� �������� ����������� ������������� ������
    public static int countCandleOpenPosition = 0;//����� ��� ����, ����� �������� ������� ������ ����� 1 ���������� �����
    public static boolean isOpenDeal = false;//������� �� ������

    public static List<Float> maxDescendingMoneyList = new ArrayList<>();//������������ ��������
    public static float maxDescendingMoney = 0;//������������ ��������

    public static Date dateForIntervalYields = new Date();//���� ����� ��������� ������ ����� ��� ����������� ���������� �� ����� ��������
    public static boolean isFirstCandleYields = true;
    public static float tempYields = 0;//���������� ����������, ������ ��������� ������� ����������, ��� ��� �������� ���������� yields ��������� ����������

    public static int countFailSequence = 0;//���������� ������� �������, ������������ ���������� �������� ������ ������
    public static int tempFailSequence = 0;//���������� ���������������
    public static boolean isFirstFailSequence = false;//��������� ��� ���� ����� ��������� ���������� ������ ������� ������
    public static List<Integer> countFailSequenceList = new ArrayList<>();//���� ����� �������� ������� �������� ��������� ������
}
