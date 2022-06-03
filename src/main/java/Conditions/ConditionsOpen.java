package Conditions;

import DateProviders.StaticData;
import RunCounting.Candle;

import java.util.ArrayList;
import java.util.List;

//�������� ����.������� �� ����
//---������� ������� ������� � ��������� 30% �� ��������, ����� ����� ���� ��������� ���������� ���������� ,���� ������ ���� � �������� � �������������� ��� ���
//0.�������� �������� ������� ������(� ��� ����� ����� �������������� ������� �����) ����� ������� ���� ������� ��������, � �������� � ���� �������� ����� � ��������������� �������, � ��������� ��� ����� ������� ����� �����������
//0.1 �������� ������� ������� 0, ��� ����������� minRP, � ����� ���������� ������ ������� �����, � ������� ���� ���� ��� ������� ���� ���� ������ minRP ��� ��������
//0.2 ������� ����������� ���������� ����� ����������, ���� ������ ���������� ������� � ������ (movedDealPos) �� ���������, ��� ����� ���������������
//1.������� ������� ��������� ������ �� ���� � ����� ��� ���� ������, ��� ��� ������ ������������
//2.������� ������� ��������� � ������ ATR
//3.������� ������� �������� conditionTwo � ���.�������������� ����� � ����� �� ������ �����,� � ���� ������� �� ������������ ����� �����, � ��������� ���� ���� ����� �������, �� ��������� �� ��� ������� ����������� ������ � ������� ������ ��������
//4.������� ��� ������� ������ �� ���� �������� ������ � ������� ����������� ������
//5.�������� ���������� ���, ������, ������. �� �������� ����� ��������� ������ �� ��������� ���������
//6.��������� ������� �����, ����� ������� ���� ������� �������� �� ���. ����� ���������� � ��������� ����������
//� ������ ��������� ����� ������, ��� ��������� ����� ����� ����� ���. ���� ����� ���� ������� ��������
//7.���� �� ��� ����, ���� � ������ ������ ��� ������ ���������� ���� ��� ������ �����
//8.����������� ������� ��� ��� ���� � ���������, ��� ����������� ��������� ���� � ����� �� ���������� ����� �������
//, � ���� ������� ����������� �� ��� ����������� �����, � ������� ���� ������ ������ � ���������� 1000�, � � ����
// ������ �� ��������� ����������� ������ � ������� 400�, � �������� 2 ��� � ���� 100� ���������� ����� ����� ��� ��
//400� � 600� ����������, �� ���� ������� ������, ��� �������� ������������, ���� ��������� �� ������� ������� ����������
// ����� 600� ������� � ������� 1000�.


public class ConditionsOpen {
    public static int length = 9;//������ ���������� �������, ������� � �����
    public static List<Boolean> markerRandomOrDefaultOpen = new ArrayList<>(length);//������ ������� ����������� ����� ���� ���� �� ������, ����� ������ �� ����� �������� ��������

    static {
        for (int i = 0; i < length; i++) {
            markerRandomOrDefaultOpen.add(false);
        }
    }

    //������ ����,���� ������ ����� ��������� � ��� ������� � ���� �� ������ �� ����� ��� �� minRP ����.
    private static boolean conditionZero(RunCounting.Candle candle){
        if (markerRandomOrDefaultOpen.get(0)){
            //==== ������� � ���� � ������ � ��������
            if ((DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getOpen() - DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getLow()) < DateProviders.StaticData.minRP &&
                    (DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getClose() - DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getOpen()) > DateProviders.StaticData.minMove &&
                    (candle.getClose() - candle.getOpen()) < DateProviders.StaticData.largeMove && !DateProviders.StaticData.isOpenDeal){
                DateProviders.StaticData.isBuy = true;
                ConditionsClose.markerExit.set(0,true);
                ConditionsClose.markerExit.set(1,true);
                return true;
            }else if ((DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getHigh() - DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getOpen()) < DateProviders.StaticData.minRP &&
                    (DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getOpen() - DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getClose()) > DateProviders.StaticData.minMove &&
                    (candle.getOpen() - candle.getClose()) < DateProviders.StaticData.largeMove && !DateProviders.StaticData.isOpenDeal){
                DateProviders.StaticData.isBuy = false;
                ConditionsClose.markerExit.set(0,true);
                ConditionsClose.markerExit.set(1,true);
                return true;
            }
        }
        return false;
    }
    //������, ���� �� ������ ����� ���� ������� ��������, � ��� ��������� ����. ��� ������� ����� � �� ����� � ����� ���� �� ������ �� ����� minNakedSize �������
    private static boolean conditionOne(RunCounting.Candle candle){
        if (markerRandomOrDefaultOpen.get(1)){
            //==== ������� � ���� � ������ � ��������
            if ((candle.getClose() - candle.getOpen()) > DateProviders.StaticData.largeMove &&
                    (candle.getHigh() - candle.getClose()) > DateProviders.StaticData.minNakedSize &&
                    (candle.getOpen() - candle.getLow()) > DateProviders.StaticData.minNakedSize &&
                    ((candle.getClose() - candle.getOpen()) * 1.3f) > (candle.getHigh() - candle.getClose()) + (candle.getOpen() - candle.getLow())
                    && !DateProviders.StaticData.isOpenDeal){
                DateProviders.StaticData.isBuy = true;
                ConditionsClose.markerExit.set(2,true);
                ConditionsClose.markerExit.set(1,true);
                return true;
            }else if ((candle.getOpen() - candle.getClose()) > DateProviders.StaticData.largeMove &&
                    (candle.getHigh() - candle.getOpen()) > DateProviders.StaticData.minNakedSize &&
                    (candle.getClose() - candle.getLow()) > DateProviders.StaticData.minNakedSize &&
                    ((candle.getOpen() - candle.getClose()) * 1.3f) > (candle.getHigh() - candle.getOpen()) + (candle.getClose() - candle.getLow())
                    &&!DateProviders.StaticData.isOpenDeal){
                DateProviders.StaticData.isBuy = false;
                ConditionsClose.markerExit.set(2,true);
                ConditionsClose.markerExit.set(1,true);
                return true;
            }
        }
        return false;
    }
    //������ ����,���� ������ ����� ��������� � ��� ������� � ���� �� ������ �� ����� ��� �� minRP ����. � �� ����� � ����� ���� �� ������ �� ����� minNakedSize �������
    private static boolean conditionTwo(RunCounting.Candle candle){
        if (markerRandomOrDefaultOpen.get(2)){
            //==== ������� � ���� � ������ � ��������
            if ((StaticData.candleList.get(StaticData.candleList.size()  - 1).getOpen() - DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getLow()) < DateProviders.StaticData.minRP &&
                    (DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getClose() - DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getOpen()) > DateProviders.StaticData.minMove &&
                    (candle.getHigh() - candle.getClose()) > DateProviders.StaticData.minNakedSize &&
                    (candle.getOpen() - candle.getLow()) > DateProviders.StaticData.minNakedSize &&
                    (candle.getClose() - candle.getOpen()) < DateProviders.StaticData.largeMove && !DateProviders.StaticData.isOpenDeal){
                DateProviders.StaticData.isBuy = true;
                ConditionsClose.markerExit.set(1,true);
                return true;
            }else if ((DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getHigh() - DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getOpen()) < DateProviders.StaticData.minRP &&
                    (DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getOpen() - DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getClose()) > DateProviders.StaticData.minMove &&
                    (candle.getHigh() - candle.getOpen()) > DateProviders.StaticData.minNakedSize &&
                    (candle.getClose() - candle.getLow()) > DateProviders.StaticData.minNakedSize &&
                    (candle.getOpen() - candle.getClose()) < DateProviders.StaticData.largeMove && !DateProviders.StaticData.isOpenDeal){
                DateProviders.StaticData.isBuy = false;
                ConditionsClose.markerExit.set(1,true);
                return true;
            }
        }
        return false;
    }

    //===������� ����� �� ��� ���� � � ������ ��������� ������. � ������� ��� ���������� �������� � ����������� ������ ��� ���
    //������� ������ �������, ����� ����������� �����.
    private static boolean conditionThree(RunCounting.Candle candle){
        if (DateProviders.StaticData.candleList.size() < StaticData.rangeExtremum){
            System.out.println("� List<RunCounting.Candle> candleList ������������ ������ ��� ������� ������� � conditionThree ConditionsOpen");
            return false;
        }
        if ((candle.getClose() - candle.getOpen()) >= DateProviders.StaticData.bodyMove &&
                (candle.getHigh() - candle.getClose()) <= DateProviders.StaticData.reverseShadow &&
                (candle.getOpen() - candle.getLow()) >= DateProviders.StaticData.mainShadow &&
                isMin(StaticData.candleList,StaticData.rangeExtremum,candle) ||
                (candle.getOpen() - candle.getClose()) >= DateProviders.StaticData.bodyMove &&
                        (candle.getHigh() - candle.getOpen()) <= DateProviders.StaticData.reverseShadow &&
                        (candle.getClose() - candle.getLow()) >= DateProviders.StaticData.mainShadow &&
                        isMin(StaticData.candleList,StaticData.rangeExtremum,candle)){
            return true;
        }else if((candle.getOpen() - candle.getClose()) >= DateProviders.StaticData.bodyMove &&
                (candle.getClose() - candle.getLow()) <= DateProviders.StaticData.reverseShadow &&
                (candle.getHigh() - candle.getOpen()) >= DateProviders.StaticData.mainShadow  &&
                isMax(StaticData.candleList,StaticData.rangeExtremum,candle) ||
                (candle.getClose() - candle.getOpen()) > DateProviders.StaticData.bodyMove &&
                        (candle.getOpen() - candle.getLow()) < DateProviders.StaticData.reverseShadow &&
                        (candle.getHigh() - candle.getClose()) > DateProviders.StaticData.mainShadow &&
                        isMax(StaticData.candleList,StaticData.rangeExtremum,candle)) {
            return true;
        }
        return false;
    }

    //===������� ����� �� ��� ����, �� ������ ����������� conditionSeven
    private static boolean conditionFourth(RunCounting.Candle candle){
        if ((candle.getClose() - candle.getOpen()) >= DateProviders.StaticData.bodyMove &&
                (candle.getHigh() - candle.getClose()) <= DateProviders.StaticData.reverseShadow &&
                (candle.getOpen() - candle.getLow()) >= DateProviders.StaticData.mainShadow ||
                (candle.getOpen() - candle.getClose()) >= DateProviders.StaticData.bodyMove &&
                        (candle.getHigh() - candle.getOpen()) <= DateProviders.StaticData.reverseShadow &&
                        (candle.getClose() - candle.getLow()) >= DateProviders.StaticData.mainShadow){
            return true;
        }else if((candle.getOpen() - candle.getClose()) >= DateProviders.StaticData.bodyMove &&
                (candle.getClose() - candle.getLow()) <= DateProviders.StaticData.reverseShadow &&
                (candle.getHigh() - candle.getOpen()) >= DateProviders.StaticData.mainShadow ||
                (candle.getClose() - candle.getOpen()) > DateProviders.StaticData.bodyMove &&
                        (candle.getOpen() - candle.getLow()) < DateProviders.StaticData.reverseShadow &&
                        (candle.getHigh() - candle.getClose()) > DateProviders.StaticData.mainShadow) {
            return true;
        }
        return false;
    }

    //===������� ����� �� �����, �� � ����� ���������� ����������� ����
    private static boolean conditionFive(RunCounting.Candle candle){
        if ((candle.getClose() - candle.getOpen()) > DateProviders.StaticData.bodyMove &&
                ((candle.getHigh() - candle.getClose()) * DateProviders.StaticData.coefficientBS) >= candle.getClose() - candle.getOpen()
                && ((candle.getOpen() - candle.getLow()) * DateProviders.StaticData.coefficientBS) >= candle.getClose() - candle.getOpen()){
            return true;
        }else if ((candle.getOpen() - candle.getClose()) > DateProviders.StaticData.bodyMove &&
                ((candle.getHigh() - candle.getClose()) * DateProviders.StaticData.coefficientBS) >= candle.getClose() - candle.getOpen()
                && ((candle.getOpen() - candle.getLow()) * DateProviders.StaticData.coefficientBS) >= candle.getClose() - candle.getOpen()){
            return true;
        }
        return false;
    }


    //===������� ����� �� ����, �� � ����� ���������� ����������� ���� � � ������ ��������� ������.
    // � ������� ��� ���������� �������� � ����������� ������ ���� ������� ������ �������, ����� ����������� �����.
    private static boolean conditionSix(RunCounting.Candle candle){//int rangeList-�������� ������� ����������� �� �����
        if (StaticData.candleList.size() < StaticData.rangeExtremum){
            return false;
        }
        if ((candle.getClose() - candle.getOpen()) > DateProviders.StaticData.bodyMove &&
                ((candle.getHigh() - candle.getClose()) * DateProviders.StaticData.coefficientBS) >= candle.getClose() - candle.getOpen()
                && ((candle.getOpen() - candle.getLow()) * DateProviders.StaticData.coefficientBS) >= candle.getClose() - candle.getOpen() &&
                isMin(StaticData.candleList,StaticData.rangeExtremum,candle)){
            return true;
        }else if ((candle.getOpen() - candle.getClose()) > DateProviders.StaticData.bodyMove &&
                ((candle.getHigh() - candle.getClose()) * DateProviders.StaticData.coefficientBS) >= candle.getClose() - candle.getOpen()
                && ((candle.getOpen() - candle.getLow()) * DateProviders.StaticData.coefficientBS) >= candle.getClose() - candle.getOpen() &&
                isMax(StaticData.candleList,StaticData.rangeExtremum,candle)){
            return true;
        }
        return false;
    }

    //===������ ����,���� ������ ����� ��������� � ��� ������� � ���� �� ������ �� ����� ��� �� minRP ����.,
    //�� ����� ���� ����� ��������� ���� �� ����, ���� �� ��� ����
    private static boolean conditionSeven(RunCounting.Candle candle){//int rangeList-�������� ������� ����������� �� �����
        if (StaticData.candleList.size() < 2){
            return false;
        }
        if (conditionEight(StaticData.candleList.get(StaticData.candleList.size() - 1)) ||
                conditionFourth(StaticData.candleList.get(StaticData.candleList.size() - 1))){
            //==== ������� � ���� � ������ � ��������
            if ((DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getOpen() - DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getLow()) < DateProviders.StaticData.minRP &&
                    (DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getClose() - DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getOpen()) > DateProviders.StaticData.minMove &&
                    (candle.getClose() - candle.getOpen()) < DateProviders.StaticData.largeMove && !DateProviders.StaticData.isOpenDeal){
                DateProviders.StaticData.isBuy = true;
                ConditionsClose.markerExit.set(0,true);
                ConditionsClose.markerExit.set(1,true);
                return true;
            }else if ((DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getHigh() - DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getOpen()) < DateProviders.StaticData.minRP &&
                    (DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getOpen() - DateProviders.StaticData.candleList.get(StaticData.candleList.size()  - 1).getClose()) > DateProviders.StaticData.minMove &&
                    (candle.getOpen() - candle.getClose()) < DateProviders.StaticData.largeMove && !DateProviders.StaticData.isOpenDeal){
                DateProviders.StaticData.isBuy = false;
                ConditionsClose.markerExit.set(0,true);
                ConditionsClose.markerExit.set(1,true);
                return true;
            }
        }
        return false;
    }

    //===������� ����� �� �����, �� ��� ������ ��������� ����������� ����.����������� �� conditionSeven.�� ������.
    private static boolean conditionEight(RunCounting.Candle candle){
        if (((candle.getHigh() - candle.getClose()) * 3) > (candle.getClose() - candle.getOpen()) &&
                ((candle.getOpen() - candle.getLow()) * 3) > (candle.getClose() - candle.getOpen())){
            return true;
        }else if (((candle.getHigh() - candle.getOpen()) * 3) > (candle.getOpen() - candle.getClose()) &&
                ((candle.getClose() - candle.getLow()) * 3) > (candle.getOpen() - candle.getClose())){
            return true;
        }
        return false;
    }

    private static final ArrayConditions[] arrayConditions = new ArrayConditions[]{
            new ArrayConditions() {
                @Override public boolean conditions(RunCounting.Candle candle) {
                    return conditionZero(candle);
                }
            },
            new ArrayConditions() {
                @Override public boolean conditions(RunCounting.Candle candle) {
                    return conditionOne(candle);
                }
            },
            new ArrayConditions() {
                @Override public boolean conditions(RunCounting.Candle candle) {
                    return conditionTwo(candle);
                }
            },
            new ArrayConditions() {
                @Override public boolean conditions(RunCounting.Candle candle) {
                    return conditionThree(candle);
                }
            },
            new ArrayConditions() {
                @Override public boolean conditions(RunCounting.Candle candle) {
                    return conditionFourth(candle);
                }
            },
            new ArrayConditions() {
                @Override public boolean conditions(RunCounting.Candle candle) {
                    return conditionFive(candle);
                }
            },
            new ArrayConditions() {
                @Override public boolean conditions(RunCounting.Candle candle) {
                    return conditionSix(candle);
                }
            },
            new ArrayConditions() {
                @Override public boolean conditions(Candle candle) {
                    return conditionSeven(candle);
                }
            },
            new ArrayConditions() {
                @Override public boolean conditions(Candle candle) {
                    return conditionEight(candle);
                }
            }
    };

    public static boolean getCondition(int index, RunCounting.Candle candle){
        return arrayConditions[index].conditions(candle);
    }

    public static void defaultSetListBooleanFromConditionOpen(){
        markerRandomOrDefaultOpen = new ArrayList<>(length);
        initializeListBoolean();
    }
    private static void initializeListBoolean(){
        for (int i = 0; i < length; i++) {
            markerRandomOrDefaultOpen.add(false);
        }
    }

    //������� ����������� ��������� �� ������ � ���������� � ������� ������
    private static boolean isMin(List<RunCounting.Candle> list,int rangeList,RunCounting.Candle candle){
        float min = Integer.MAX_VALUE;
        for (int i = list.size(); i > (list.size() - rangeList); i--) {
            if (list.get(i).getLow() < min){
                min = list.get(i).getLow();
            }
        }
        if (candle.getLow() < min){
            return true;
        }
        return false;
    }

    //������� ������������ ��������� �� ������ � ���������� � ������� ������
    private static boolean isMax(List<RunCounting.Candle> list,int rangeList,RunCounting.Candle candle){
        float max = Integer.MIN_VALUE;
        for (int i = list.size(); i > (list.size() - rangeList); i--) {
            if (list.get(i).getHigh() > max){
                max = list.get(i).getHigh();
            }
        }
        if (candle.getHigh() > max){
            return true;
        }
        return false;
    }
}
