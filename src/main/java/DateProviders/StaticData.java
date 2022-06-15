package DateProviders;

import RunCounting.Candle;
import RunCounting.Deal;
import RunCounting.Result;

import java.util.*;

public class StaticData {
    public static float rangeYields = -5000000;//������ �������, ����� �������� ���� ����� �� ����� � ������ � ����

    public static float coefficientRiskManagement = 1.0f;//������� ������� ������������ � ���������� ����������, ���� ���������� ���� 100, �� ��������� �� ���� ���� ���� 30 �����������
    public static List<Candle> candleList = new ArrayList<>();//���� ��� �������� ������ �� ����������� ���������� �������
    public static float yieldsCommons = 0.0f;//��������� ��� ����������� ���������� ��������
    public static List<Result> commonListDeals = new ArrayList<>();//���� ��� ���� ������ ��������������
    public static float open = 0.0f;//���� ��������
    public static float close = 0.0f;//���� ��������
    public static Date openDeal = null;//��������� ���� ��� ���������� ���� �������� � ������ � ������ Static.positiveRes

    public static int currentFailSequence = 0;//����� �� �������� �������������� ������� ������, ���� ������ �� ����� 2 ��������� ������
    public static float yieldsLimitFailSequence = 0;//���������� ���.��������� ���������� � �������� �� ������� ���� ������ 2 ��������� ������

    public static boolean isBuy = true;//������� ��� ������� �� �������� ��������

    //��������� ��� ���������� ������� �����, ����� ���� ����������
    public static float limitStop = 94;//���� ����//default 94
    public static float minRP = 100;//(minReversePrice)������� ��������� �������� �� �����//default 100
    public static float minMove = 20;//������� ���������� ����� ������ ���� �� �������� �� ��������//default 20
    public static float largeMove = 980;//������� ������ ������, ����� ��������� 2 �������//default 980
    public static float minNakedSize = 10;//����������� �������� ��� �������� ��������//default 10
    public static float maxLossTotal = -546;
    public static float slipPage = -11;//������ ��������������� ��� �������� ������ ��� ���������������� �����
    public static float conditionExitLargeCandle = 600;//�������� ��� ���������� ������� �������� ��� ������� �� ���� �����, � ����� �� ���� ������ ��� �������, � ��� �� ������ ���� ����� ����� �� ��� ������� ��� ���
    public static float bodyMove = 0;//������� ���� ������ �� ������� �� ��������
    public static float mainShadow = 0;//�������� ���� ����� �� ������ ��� ����
    public static float reverseShadow = 0;//�������� ���� ����� �� ������ ��� ����
    public static float coefficientBS = 0;//����������� ���� > ������������ ����

    //��������� ��� ������� ������������, ����� ���� ����������
    public static boolean isBuyTrend = true;//��������� �����, ������� �������������� � ������ findTrend.false-��������� � ��������
    public static int rangeExtremum = 18;//int rangeExtremum-�������� ������� ����������� �� �����
    public static int rangeCandleForTrend = 2;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ����� ����������, ����� ������� ������������ �����
    public static int rangeCandleForATR = 5;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ATR-������� �������� ��������-� ����� ��������� ����� ���� ������ ������ ���������
    public static int rangeCandleForMA = 5;//�������� � ����� �����, �� ���� �������� ��������� � ��������� ������� ���������� � ����� ���������


    public static List<Deal> positiveRes = new ArrayList<>();//list ��� �������� ����������� ������������� ������
    public static List<Deal> negativeRes = new ArrayList<>();//list ��� �������� ����������� ������������� ������
    public static int countCandleOpenPosition = 0;//����� ��� ����, ����� �������� ������� ������ ����� 1 ���������� �����
    public static boolean isOpenDeal = false;//������� �� ������

    public static List<Float> maxDescendingMoneyList = new ArrayList<>();//������������ ��������
    public static float maxDescendingMoney = 0;//������������ ��������

    public static int countWorkingDays = 1; //===������� ��� �������� ��������� �������� ����
    public static float tempYieldsMaxLoss = 0.0f;//��������� ��� ����������� ���������� ��������, � ������ ���������� ������� maxLossTotal
    public static Calendar dateForMaxLossTotal = Calendar.getInstance();//��� ���� ����� �������� tempYields � ������� ������ �����, ���� ����� �������� 1 �����
    public static boolean isFirstEnterCalendar = true;//������ ������� �������, ��� �� ������ ��� �������� �������� Calendar

    public static Calendar calendarForIntervalYields = Calendar.getInstance();//����� ��������� ������ ����� ��� ����������� ���������� �� ����� ��������
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


    public final static Map<String,Float> mapWarrantyProvision = new HashMap<>();//����� ������� ������ ����������� ����������� �� ���������� ��������
    public final static Map<String,Float> mapPriceStep = new HashMap<>();//����� ������� ������ ��������� ���� ����

    public static String[] allPathFiles = {
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-190101-220522.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-211118-220522.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-211118-220606.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220602-220606.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220318-220522.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220519-220519.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220513-220519.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220329-220529.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220607-220607.txt",
            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220329-220607.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220530-220603.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220528-220605.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220525-220601.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220531-220601.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220513-220518.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220519-220524.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220519-220530.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220318-220518.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220422-220515.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220326-220525.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\ED.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\SI-Hour-110101-220506.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-30M-190101-220430.txt",

//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si_220301_220430.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si_220101_220228.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si_211101_211231.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si_210901_211031.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si_210701_210831.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si_210430_210630.txt",
//
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\BR-30M-190101-220430.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\BR-Hour-190101-220430.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\GD-Hour-190101-220430.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\RI-30M-190101-220430.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\RI-Hour-190101-220430.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\RI-Hour-220326-220525.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\SR-30M-190101-220430.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\SR-Hour-190101-220430.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\ED-Hour-190101-220526.txt"
    };
}
