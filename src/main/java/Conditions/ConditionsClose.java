package Conditions;

import DateProviders.StaticData;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ConditionsClose {
    private static final int length = 3;//скольк количество условий, столько и длина
    public static List<Boolean> markerExit = new ArrayList<>(length);//маркер который срабатывает когда идет вход по сделке, чтобы понять по каким условиям выходить

    static {
        for (int i = 0; i < length; i++) {
            markerExit.add(false);
        }
    }

    private static boolean conditionZero(RunCounting.Candle candle){//условие выхода по стопу
        //==== условие в рост и дальше в снижение
        if (markerExit.get(0)){
            if (candle.getLow() <= (StaticData.candleList.get(StaticData.candleList.size() - 1).getClose() - DateProviders.StaticData.limitStop) && DateProviders.StaticData.isBuy && DateProviders.StaticData.isOpenDeal){
                return true;
            } else if (candle.getHigh() >= (DateProviders.StaticData.candleList.get(StaticData.candleList.size() - 1).getClose() + DateProviders.StaticData.limitStop) && !DateProviders.StaticData.isBuy && DateProviders.StaticData.isOpenDeal){
                return true;
            }
        }
        return false;
    }
    private static boolean conditionOne(RunCounting.Candle candle){//условие выхода, когда цена выходит за край свечи
        if (markerExit.get(1)){
            if (candle.getLow() <= DateProviders.StaticData.candleList.get(StaticData.candleList.size() - 1).getLow() && DateProviders.StaticData.isBuy && DateProviders.StaticData.isOpenDeal){
                return true;
            }else if (candle.getHigh() >= DateProviders.StaticData.candleList.get(StaticData.candleList.size() - 1).getHigh() && !DateProviders.StaticData.isBuy && DateProviders.StaticData.isOpenDeal){
                return true;
            }
        }
        return false;
    }
    //условие выхода за середину, большой свечи, параметр DateProviders.StaticData.conditionExitLargeCandle, может быть рандомным
    private static boolean conditionTwo(RunCounting.Candle candle){
        if (markerExit.get(2)){
            //        stack.peek().getClose() - DateProviders.StaticData.conditionExitLargeCandle
            if (candle.getLow() <= ((DateProviders.StaticData.candleList.get(StaticData.candleList.size() - 1).getClose() + DateProviders.StaticData.candleList.get(StaticData.candleList.size() - 1).getOpen()) / 2) && DateProviders.StaticData.isBuy && DateProviders.StaticData.isOpenDeal){
                return true;
//            stack.peek().getOpen() - DateProviders.StaticData.conditionExitLargeCandle
            }else if (candle.getHigh() >= ((DateProviders.StaticData.candleList.get(StaticData.candleList.size() - 1).getClose() + DateProviders.StaticData.candleList.get(StaticData.candleList.size() - 1).getOpen()) / 2) && !DateProviders.StaticData.isBuy && DateProviders.StaticData.isOpenDeal){
                return true;
            }
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
            }
    };

    public static boolean getCondition(int index, RunCounting.Candle candle){
        return arrayConditions[index].conditions(candle);
    }

    public static void defaultSetListBooleanFromConditionClose(){
        markerExit = new ArrayList<>(length);
        initializeListBoolean();
    }
    private static void initializeListBoolean(){
        for (int i = 0; i < length; i++) {
            markerExit.add(false);
        }
    }

}
