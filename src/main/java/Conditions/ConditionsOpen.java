package Conditions;

import DateProviders.StaticData;
import RunCounting.Candle;

import java.util.ArrayList;
import java.util.List;

//Добавить след.условия на вход
//---сделать подсчет прибыли в программе 30% от капитала, чтобы можно было нарастить количество контрактов ,если сделка идет с прибылью и протестировать это все
//0.добавить механизм анализа свечей(в том числе свечи предшествующие хорошей свече) после которых идет хорошее движение, и добавить в Лист возможно както с предшествующими свечами, с подсчетом как часто похожие свечи встречаются
//0.1 добавить вариант условия 0, где учитывается minRP, а также уцчитывать Другую сторону свечи, к примеру если тень той стороны была знач больше minRP или наоборот
//0.2 добвить возможность увеличения колич контрактов, если сделка показывает прибыль и прошла (movedDealPos) ка константа, кот можно рандомизировать
//1.условие которое принимает сигнал на вход в месте где есть уровни, где нет уровня игнорировать
//2.условие которое принимает в расчет ATR
//3.условие которое копирует conditionTwo в кот.игнорировались голые с одной из сторон свечи,а в этом условии не игнорировать голыу свечи, а проверять если есть голая сторона, то пересекла ли эта сторона предыддущую свечку в сторону голого закрытия
//4.условие при котором сигнал на вход подается только в сторону направления тренда
//5.проверка доходности дня, недели, месяца. на открытии рынка открываем сделку на закрытиии закрываем
//6.программа находит свечи, после которых идет хорошее движение на кот. можно заработать и программа закидывает
//в массив параметры таких свечей, для выявления формы свечи после кот. чаще всего идет хорошее движение
//7.вход по пин бару, либо с учетом тренда или средне скользящей либо без ввсего этого
//8.ограничение убытков кот уже есть в программе, это ограничение учитывает уход в минус на протяжении всего периода
//, а если сделать ограничение от уже накопленной суммы, к примеру если прошла неделя и заработано 1000р, и в этом
// месяце мы поставили ограничение убытка к примеру 400р, и проходит 2 дня и наша 100р доходности упала более чем на
//400р к 600р доходности, то даем системе понять, что торговля прекращается, пока стратегия не покажет переход доходности
// свыше 600р обратно в сторону 1000р.


public class ConditionsOpen {
    public static int length = 9;//скольк количество условий, столько и длина
    public static List<Boolean> markerRandomOrDefaultOpen = new ArrayList<>(length);//маркер который срабатывает когда идет вход по сделке, чтобы понять по каким условиям выходить

    static {
        for (int i = 0; i < length; i++) {
            markerRandomOrDefaultOpen.add(false);
        }
    }

    //входим либо,если предыд свеча закрылась и она сходила в одну из сторон не более чем на minRP пунк.
    private static boolean conditionZero(RunCounting.Candle candle){
        if (markerRandomOrDefaultOpen.get(0)){
            //==== условие в рост и дальше в снижение
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
    //входим, если на предыд свече было сильное движение, и она закрылась норм. Без сильных теней и не голой с какой либо из сторон не менее minNakedSize пунктов
    private static boolean conditionOne(RunCounting.Candle candle){
        if (markerRandomOrDefaultOpen.get(1)){
            //==== условие в рост и дальше в снижение
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
    //входим либо,если предыд свеча закрылась и она сходила в одну из сторон не более чем на minRP пунк. и не голой с какой либо из сторон не менее minNakedSize пунктов
    private static boolean conditionTwo(RunCounting.Candle candle){
        if (markerRandomOrDefaultOpen.get(2)){
            //==== условие в рост и дальше в снижение
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

    //===условие входа по пин бару и с учетом последний свечей. К примеру шло нисходящее движение и разворотный наверх пин бар
    //который пробил минимум, самой минимальной свечи.
    private static boolean conditionThree(RunCounting.Candle candle){
        if (DateProviders.StaticData.candleList.size() < StaticData.rangeExtremum){
            System.out.println("В List<RunCounting.Candle> candleList недостаточно свечей для расчета условия в conditionThree ConditionsOpen");
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

    //===условие входа по пин бару, не менять зависимость conditionSeven
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

    //===условие входа по доджи, но с явным выделением направления тела
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


    //===условие входа по дожи, но с явным выделением направления тела и с учетом последний свечей.
    // К примеру шло нисходящее движение и разворотный наверх дожи который пробил минимум, самой минимальной свечи.
    private static boolean conditionSix(RunCounting.Candle candle){//int rangeList-диапазон расчета экстремумов тз Листа
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

    //===входим либо,если предыд свеча закрылась и она сходила в одну из сторон не более чем на minRP пунк.,
    //но перед этим свеча закрылась либо по дожи, либо по пин бару
    private static boolean conditionSeven(RunCounting.Candle candle){//int rangeList-диапазон расчета экстремумов тз Листа
        if (StaticData.candleList.size() < 2){
            return false;
        }
        if (conditionEight(StaticData.candleList.get(StaticData.candleList.size() - 1)) ||
                conditionFourth(StaticData.candleList.get(StaticData.candleList.size() - 1))){
            //==== условие в рост и дальше в снижение
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

    //===условие входа по доджи, но без явныго выделения направления тела.Зависимость от conditionSeven.не менять.
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

    //находим минимальный экстремум из списка и сравниваем с текущей свечой
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

    //находим максимальный экстремум из списка и сравниваем с текущей свечой
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
