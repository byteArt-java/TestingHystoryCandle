import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class StaticData {
    public static Stack<Candle> candleStack = new Stack<>();//стэк для хранения свечей внутри дня
    public static float yields = 0.0f;//перемення для складывания результата торговли
    public static float tempYields = 0.0f;//перемення для складывания результата торговли, с учетом ограичения убытков
    public static List<Float> commonListDeals = new ArrayList<>();//лист для всех сделок последовтельно
    public static float open = 0.0f;//цена открытия
    public static float close = 0.0f;//цена закрытия
    public static Date openDeal = null;//временная дата для сохранения даты открытия и добавл в массив Static.positiveRes

    public static int currentFailSequence = 0;//Лимит по которому ограничивается откытие сделок, если прошло до этого 2 неудачные сделки
    public static float yieldsLimitFailSequence = 0;//переменная кот.учитывает доходность с условием не входить если прошли 2 неудачные сделки

    public static boolean isBuy = true;//Покупка или продажа по откртымы позициям

    public static float limitStop = 94;//Стоп Лосс//default 94
    public static float minRP = 100;//(minReversePrice)условие обратного движения по свече//default 100
    public static float minMove = 20;//сколько минимально должн пройти цена от открытия до закрытия//default 20
    public static float largeMove = 980;//сколько должно пройти, чтобы сработало 2 условие//default 980
    public static float minNakedSize = 10;//минимальный диапазон для неголого закрытия//default 10
    public static float maxLossTotal = -546;
    public static float conditionExitLargeCandle = 600;//параметр кот определяет сколько добавить или убавить от цены свечи, в завис от того кулено или продано, и так мы поймем след свеча вышла за эту границу или нет
    public static float bodyMove = 0;//сколько тело прошло от открыти до закрытия
    public static float mainShadow = 0;//основная тень одной из сторон пин бара
    public static float reverseShadow = 0;//обратная тень одной из сторон пин бара
    public static float coefficientBS = 0;//коэффициент тела > относительно тени
    public static float rangeList = 0;//диапазон с конца листа, за этот диапазон смотрится и находится экстремум цены макс. и минимальной
    public static float rangeCandleForTrend = 2;//диапазон с конца листа, за этот диапазон смотрится и находится точка экстремума, после которой определяется тренд
    public static float rangeCandleForATR = 5;//диапазон с конца листа, за этот диапазон смотрится и находится ATR-среднее истинное значение-в каком диапазоне ходит цена внутри этогоо диапазона
    public static float rangeCandleForMA = 18;//диапазон с конца листа, за этот диапазон смотрится и находится средняя скользящая в каком диапазоне


    public static List<Deal> positiveRes = new ArrayList<>();//list для хранения проведенных положительных сделок
    public static List<Deal> negativeRes = new ArrayList<>();//list для хранения проведенных отрицательных сделок
    public static int countCandleOpenPosition = 0;//нужна для того, чтобы изменить условие выхода после 1 пройденной свечи
    public static boolean isOpenDeal = false;//открыта ли сделка

    public static List<Float> maxDescendingMoneyList = new ArrayList<>();//максимальная просадка
    public static float maxDescendingMoney = 0;//максимальная просадка

    public static Date dateForIntervalYields = new Date();//дата когда считываем первую свечу для отображения доходности за опред интервал
    public static boolean isFirstCandleYields = true;
    public static float intervalYields = 0;//переменная доходности, которя учитывает разницу доходности, так как основаня переменная yields постоянно возрастает

    public static int countFailSequence = 0;//переменная которая считает, максимальное количество неудачнх сделок подряд
    public static int tempFailSequence = 0;//переменная вспомогательная
    public static boolean isFirstFailSequence = false;//перменная для того чтобы правильно отображать начало нуданых сделок
    public static List<Integer> countFailSequenceList = new ArrayList<>();//лист чтобы получить среднюю последов неудачных сделок

    public static boolean isFirstCandle = true;//первая свеча от которой мы отсчитываем период, для реализации сложного процента
    public static Date dateFirstCandle = new Date();//дата когда считываем первую свечу для сложного процента
    public static float capitalCompoundInterest = 0;//капитал для реализации сложного процента
    public static float countContractsCompoundInterest = 0;//кол контрактов для реализации сложного процента
    public static float yieldsCompoundInterest = 0;//доходность для реализации сложного процента
    public static long totalIntervalCompoundInterest = 0;//интервал, по которому нужно пересчитать количество контрактов

    public static boolean isBuyTrend = true;//индикатор треда, который рассчитывается в методе findTrend.false-нисходщий и наоборот

}
