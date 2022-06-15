package DateProviders;

import RunCounting.Candle;
import RunCounting.Deal;
import RunCounting.Result;

import java.util.*;

public class StaticData {
    public static float rangeYields = -5000000;//предел прибыли, после которого идет вывод на экран и запись в файл

    public static float coefficientRiskManagement = 1.0f;//коэффиц который манипулирует с количесвом контрактов, если контрактов было 100, то умножение на этот коэф даст 30 констрактов
    public static List<Candle> candleList = new ArrayList<>();//лист для хранения свечей за пределенный промежуток времени
    public static float yieldsCommons = 0.0f;//перемення для складывания результата торговли
    public static List<Result> commonListDeals = new ArrayList<>();//лист для всех сделок последовтельно
    public static float open = 0.0f;//цена открытия
    public static float close = 0.0f;//цена закрытия
    public static Date openDeal = null;//временная дата для сохранения даты открытия и добавл в массив Static.positiveRes

    public static int currentFailSequence = 0;//Лимит по которому ограничивается откытие сделок, если прошло до этого 2 неудачные сделки
    public static float yieldsLimitFailSequence = 0;//переменная кот.учитывает доходность с условием не входить если прошли 2 неудачные сделки

    public static boolean isBuy = true;//Покупка или продажа по откртымы позициям

    //константы для параметров расчета свечи, могут быть рандомными
    public static float limitStop = 94;//Стоп Лосс//default 94
    public static float minRP = 100;//(minReversePrice)условие обратного движения по свече//default 100
    public static float minMove = 20;//сколько минимально должн пройти цена от открытия до закрытия//default 20
    public static float largeMove = 980;//сколько должно пройти, чтобы сработало 2 условие//default 980
    public static float minNakedSize = 10;//минимальный диапазон для неголого закрытия//default 10
    public static float maxLossTotal = -546;
    public static float slipPage = -11;//эффект проскальзывания при открытии сделки или сколькальзывании стопа
    public static float conditionExitLargeCandle = 600;//параметр кот определяет сколько добавить или убавить от цены свечи, в завис от того кулено или продано, и так мы поймем след свеча вышла за эту границу или нет
    public static float bodyMove = 0;//сколько тело прошло от открыти до закрытия
    public static float mainShadow = 0;//основная тень одной из сторон пин бара
    public static float reverseShadow = 0;//обратная тень одной из сторон пин бара
    public static float coefficientBS = 0;//коэффициент тела > относительно тени

    //константы для расчета инструментов, могут быть рандомными
    public static boolean isBuyTrend = true;//индикатор треда, который рассчитывается в методе findTrend.false-нисходщий и наоборот
    public static int rangeExtremum = 18;//int rangeExtremum-диапазон расчета экстремумов из Листа
    public static int rangeCandleForTrend = 2;//диапазон с конца листа, за этот диапазон смотрится и находится точка экстремума, после которой определяется тренд
    public static int rangeCandleForATR = 5;//диапазон с конца листа, за этот диапазон смотрится и находится ATR-среднее истинное значение-в каком диапазоне ходит цена внутри этогоо диапазона
    public static int rangeCandleForMA = 5;//диапазон с конца листа, за этот диапазон смотрится и находится средняя скользящая в каком диапазоне


    public static List<Deal> positiveRes = new ArrayList<>();//list для хранения проведенных положительных сделок
    public static List<Deal> negativeRes = new ArrayList<>();//list для хранения проведенных отрицательных сделок
    public static int countCandleOpenPosition = 0;//нужна для того, чтобы изменить условие выхода после 1 пройденной свечи
    public static boolean isOpenDeal = false;//открыта ли сделка

    public static List<Float> maxDescendingMoneyList = new ArrayList<>();//максимальная просадка
    public static float maxDescendingMoney = 0;//максимальная просадка

    public static int countWorkingDays = 1; //===счетчик для подсчета прошедщих торговых дней
    public static float tempYieldsMaxLoss = 0.0f;//перемення для складывания результата торговли, с учетом ограичения убытков maxLossTotal
    public static Calendar dateForMaxLossTotal = Calendar.getInstance();//для того чтобы обнулять tempYields к примеру каждый месяц, если стоит интервал 1 месяц
    public static boolean isFirstEnterCalendar = true;//маркер который говорит, что мы первый раз пытаемся изменить Calendar

    public static Calendar calendarForIntervalYields = Calendar.getInstance();//когда считываем первую свечу для отображения доходности за опред интервал
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


    public final static Map<String,Float> mapWarrantyProvision = new HashMap<>();//карта которая отдает гарантийное обеспечение на конкретный контракт
    public final static Map<String,Float> mapPriceStep = new HashMap<>();//карта которая отдает стоимость шага цены

    public static String[] allPathFiles = {
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-190101-220522.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-211118-220522.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-211118-220606.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220602-220606.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220318-220522.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220519-220519.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220513-220519.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220329-220529.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220607-220607.txt",
            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220329-220607.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220530-220603.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220528-220605.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220525-220601.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220531-220601.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220513-220518.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220519-220524.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220519-220530.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220318-220518.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220422-220515.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220326-220525.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\ED.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\SI-Hour-110101-220506.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-30M-190101-220430.txt",

//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si_220301_220430.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si_220101_220228.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si_211101_211231.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si_210901_211031.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si_210701_210831.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si_210430_210630.txt",
//
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\BR-30M-190101-220430.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\BR-Hour-190101-220430.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\GD-Hour-190101-220430.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\RI-30M-190101-220430.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\RI-Hour-190101-220430.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\RI-Hour-220326-220525.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\SR-30M-190101-220430.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\SR-Hour-190101-220430.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\ED-Hour-190101-220526.txt"
    };
}
