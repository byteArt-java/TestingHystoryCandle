import java.util.ArrayList;
import java.util.List;

public class StaticData {
    public static float limitStop = 510;//Стоп Лосс//default 270
    public static boolean isBuy = true;//Покупка или продажа по откртымы позициям

    public static int oneMethod = 0;//маркеры дают понять, по каким методам выходить в ConditionClose при первой попытке,устанавливаются в ConditionOpen
    public static int twoMethod = 0;//маркеры дают понять, по каким методам выходить в ConditionClose при первой попытке,устанавливаются в ConditionOpen
    public static int threeMethod = 0;//маркеры дают понять, по каким методам выходить в ConditionClose при первой попытке,устанавливаются в ConditionOpen

    public static float minRP = 40;//(minReversePrice)условие обратного движения по свече//default 110
    public static float minMove = 180;//сколько минимально должн пройти цена от открытия до закрытия//default 150
    public static float largeMove = 570;//сколько должно пройти, чтобы сработало 2 условие//default 900
    public static float minNakedSize = 30;//минимальный диапазон для неголого закрытия//default 10
    public static List<Candle> candleList = new ArrayList<>();//лист для хранения проведенных сделок
    public static int countCandleOpenPosition = 0;//нужна для того, чтобы изменить условие выхода после 1 пройденной свечи
    public static boolean isOpenDeal = false;//открыта ли сделка
}
