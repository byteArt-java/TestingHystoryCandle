import java.util.ArrayList;
import java.util.List;

public class StaticData {
    public static float limitStop = 330;//Стоп Лосс//default 270

    public static boolean isBuy = true;//Покупка или продажа по откртымы позициям

    public static float rangeYields = 0;//предел прибыли, после которого идет вывод на экран и запись в файл

    public static int oneMethod = 0;//маркеры дают понять, по каким методам выходить в ConditionClose при первой попытке,устанавливаются в ConditionOpen
    public static int twoMethod = 0;//маркеры дают понять, по каким методам выходить в ConditionClose при первой попытке,устанавливаются в ConditionOpen
    public static int threeMethod = 0;//маркеры дают понять, по каким методам выходить в ConditionClose при первой попытке,устанавливаются в ConditionOpen

    public static float minRP = 220;//(minReversePrice)условие обратного движения по свече//default 110
    public static float minMove = 310;//сколько минимально должн пройти цена от открытия до закрытия//default 150
    public static float largeMove = 1730;//сколько должно пройти, чтобы сработало 2 условие//default 900
    public static float minNakedSize = 10;//минимальный диапазон для неголого закрытия//default 10

    public static List<Candle> candleList = new ArrayList<>();//лист для хранения проведенных сделок
    public static int countCandleOpenPosition = 0;//нужна для того, чтобы изменить условие выхода после 1 пройденной свечи
    public static boolean isOpenDeal = false;//открыта ли сделка

    public static List<Float> maxDescendingMoneyList = new ArrayList<>();//максимальная просадка
    public static float maxDescendingMoney = 0;//максимальная просадка
}
