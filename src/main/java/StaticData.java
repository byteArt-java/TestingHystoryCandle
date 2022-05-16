import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StaticData {
    public static float limitStop = 270;//Стоп Лосс//default 270

    public static boolean isBuy = true;//Покупка или продажа по откртымы позициям

    public static float rangeYields = -500000;//предел прибыли, после которого идет вывод на экран и запись в файл

    public static int oneMethod = 0;//маркеры дают понять, по каким методам выходить в ConditionClose при первой попытке,устанавливаются в ConditionOpen
    public static int twoMethod = 0;//маркеры дают понять, по каким методам выходить в ConditionClose при первой попытке,устанавливаются в ConditionOpen
    public static int threeMethod = 0;//маркеры дают понять, по каким методам выходить в ConditionClose при первой попытке,устанавливаются в ConditionOpen

    public static float minRP = 110;//(minReversePrice)условие обратного движения по свече//default 110
    public static float minMove = 150;//сколько минимально должн пройти цена от открытия до закрытия//default 150
    public static float largeMove = 900;//сколько должно пройти, чтобы сработало 2 условие//default 900
    public static float minNakedSize = 10;//минимальный диапазон для неголого закрытия//default 10

    public static List<Deal> positiveRes = new ArrayList<>();//list для хранения проведенных положительных сделок
    public static List<Deal> negativeRes = new ArrayList<>();//list для хранения проведенных отрицательных сделок
    public static int countCandleOpenPosition = 0;//нужна для того, чтобы изменить условие выхода после 1 пройденной свечи
    public static boolean isOpenDeal = false;//открыта ли сделка

    public static List<Float> maxDescendingMoneyList = new ArrayList<>();//максимальная просадка
    public static float maxDescendingMoney = 0;//максимальная просадка

    public static Date dateForIntervalYields = new Date();//дата когда считываем первую свечу для отображения доходности за опред интервал
    public static boolean isFirstCandleYields = true;
    public static float tempYields = 0;//переменная доходности, которя учитывает разницу доходности, так как основаня переменная yields постоянно возрастает

    public static int countFailSequence = 0;//переменная которая считает, максимальное количество неудачнх сделок подряд
    public static int tempFailSequence = 0;//переменная вспомогательная
    public static boolean isFirstFailSequence = false;//перменная для того чтобы правильно отображать начало нуданых сделок
    public static List<Integer> countFailSequenceList = new ArrayList<>();//лист чтобы получить среднюю последов неудачных сделок
}
