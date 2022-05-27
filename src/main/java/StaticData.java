import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class StaticData {
    public static Stack<Candle> candleStack = new Stack<>();//���� ��� �������� ������ ������ ���
    public static float yields = 0.0f;//��������� ��� ����������� ���������� ��������
    public static float tempYields = 0.0f;//��������� ��� ����������� ���������� ��������, � ������ ���������� �������
    public static List<Float> commonListDeals = new ArrayList<>();//���� ��� ���� ������ ��������������
    public static float open = 0.0f;//���� ��������
    public static float close = 0.0f;//���� ��������
    public static Date openDeal = null;//��������� ���� ��� ���������� ���� �������� � ������ � ������ Static.positiveRes

    public static int currentFailSequence = 0;//����� �� �������� �������������� ������� ������, ���� ������ �� ����� 2 ��������� ������
    public static float yieldsLimitFailSequence = 0;//���������� ���.��������� ���������� � �������� �� ������� ���� ������ 2 ��������� ������

    public static boolean isBuy = true;//������� ��� ������� �� �������� ��������

    public static float rangeYields = 5000000;//������ �������, ����� �������� ���� ����� �� ����� � ������ � ����

    public static int oneMethod = 0;//������� ���� ������, �� ����� ������� �������� � ConditionClose ��� ������ �������,��������������� � ConditionOpen
    public static int twoMethod = 0;//������� ���� ������, �� ����� ������� �������� � ConditionClose ��� ������ �������,��������������� � ConditionOpen
    public static int threeMethod = 0;//������� ���� ������, �� ����� ������� �������� � ConditionClose ��� ������ �������,��������������� � ConditionOpen

    public static float limitStop = 270;//���� ����//default 270
    public static float minRP = 110;//(minReversePrice)������� ��������� �������� �� �����//default 110
    public static float minMove = 150;//������� ���������� ����� ������ ���� �� �������� �� ��������//default 150
    public static float largeMove = 900;//������� ������ ������, ����� ��������� 2 �������//default 900
    public static float minNakedSize = 10;//����������� �������� ��� �������� ��������//default 10
    public static float maxLossTotal = -546;
    public static float conditionExitLargeCandle = 600;//�������� ��� ���������� ������� �������� ��� ������� �� ���� �����, � ����� �� ���� ������ ��� �������, � ��� �� ������ ���� ����� ����� �� ��� ������� ��� ���

    public static List<Deal> positiveRes = new ArrayList<>();//list ��� �������� ����������� ������������� ������
    public static List<Deal> negativeRes = new ArrayList<>();//list ��� �������� ����������� ������������� ������
    public static int countCandleOpenPosition = 0;//����� ��� ����, ����� �������� ������� ������ ����� 1 ���������� �����
    public static boolean isOpenDeal = false;//������� �� ������

    public static List<Float> maxDescendingMoneyList = new ArrayList<>();//������������ ��������
    public static float maxDescendingMoney = 0;//������������ ��������

    public static Date dateForIntervalYields = new Date();//���� ����� ��������� ������ ����� ��� ����������� ���������� �� ����� ��������
    public static boolean isFirstCandleYields = true;
    public static float intervalYields = 0;//���������� ����������, ������ ��������� ������� ����������, ��� ��� �������� ���������� yields ��������� ����������

    public static int countFailSequence = 0;//���������� ������� �������, ������������ ���������� �������� ������ ������
    public static int tempFailSequence = 0;//���������� ���������������
    public static boolean isFirstFailSequence = false;//��������� ��� ���� ����� ��������� ���������� ������ ������� ������
    public static List<Integer> countFailSequenceList = new ArrayList<>();//���� ����� �������� ������� �������� ��������� ������

    public static boolean isFirstCandle = true;//������ ����� �� ������� �� ����������� ������, ��� ���������� �������� ��������
    public static Date dateFirstCandle = new Date();//���� ����� ��������� ������ ����� ��� �������� ��������
    public static float capitalCompoundInterest = 0;//������� ��� ���������� �������� ��������
    public static float countContractsCompoundInterest = 0;//��� ���������� ��� ���������� �������� ��������
    public static float yieldsCompoundInterest = 0;//���������� ��� ���������� �������� ��������
    public static long totalIntervalCompoundInterest = 0;//��������, �� �������� ����� ����������� ���������� ����������

}
