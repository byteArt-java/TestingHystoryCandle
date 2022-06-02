package RunCounting;

import Conditions.ConditionsClose;
import Conditions.ConditionsOpen;
import DateProviders.StaticData;
import Instruments.ATR;
import Instruments.MovingAverage;
import Instruments.TypeCandleParameter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private final static Random random = new Random();

    public static void main(String[] args) throws IOException, ParseException {
        preparation(true,false,10000,900000,1,false,"M",
                1,false,false,DateProviders.StaticData.allPathFiles);
    }

    //isRandomConditions - включить рандомное условие по входу и рандомное количество условий по входу
    //isRandom - включить рандомизацию Статических параметров к примеру стоп-лосс.
    //countRandom - количество цикла рандомизации
    //capital - начальный капитал, участвует в Сложном проценте
    //spanInterval - период для просчета сложного процента
    //isShowCompoundInterest - показывать и учитывать доходность по сложному проценту отдельно
    //interval - вывод доходности за определенный интервал времени по стандар доходн, принимает Месяц, дни, недели
    //countInterval - определенный интервал времени * умножение интервала времени.
    //isShowIntervalYields - показать ли доходность за определенный интервал времени
    //isLimitLoss - включить ли стратегию ограничения убытков
    //pathFiles - Массив Url адресов с историческими данными.
    private static void preparation(boolean isRandomConditions,boolean isRandom,int countRandom,float capital,
                                    int spanInterval, boolean isShowCompoundInterest, String interval,
                                    int countInterval,
                                    boolean isShowIntervalYields, boolean isLimitLoss, String... pathFiles) throws IOException, ParseException {
        addDataToMap();
        if (isRandom){
            for (String pathFile : pathFiles) {//запуск программы для каждого загруженного файла с историч данными
                //====включить рандомное условие по входу и рандомное количество условий по входу
                if (isRandomConditions){
                    int countConditions = random.nextInt(ConditionsOpen.length);
                    for (int i = 0; i < countConditions; i++) {
                        int indexConditions = random.nextInt(ConditionsOpen.length);
                        ConditionsOpen.markerRandomOrDefaultOpen.set(indexConditions,true);
                    }
                }else {
                    ConditionsOpen.markerRandomOrDefaultOpen.set(0,true);
                }
                //================================================================================
                for (int i = 0; i < countRandom; i++) {
                    String nameInstrument = pathFile.substring(60,62).toUpperCase();
                    switch (nameInstrument){
                        case "RI":randomRI();break;
                        case "SI":randomSI();break;
                        case "GD":randomGD();break;
                        case "ED":randomED();break;
                        case "BR":randomBR();break;
                        case "SF":randomSF();break;
                    }
                    processing(capital,spanInterval,isShowCompoundInterest,interval,countInterval,isShowIntervalYields,
                            isLimitLoss,pathFile);

                }
                ConditionsOpen.defaultSetListBooleanFromConditionOpen();
            }//конец iter файла
        }else {
            if (isRandomConditions){
                for (String pathFile : pathFiles) {
                    int countConditions = random.nextInt(ConditionsOpen.length);
                    for (int i = 0; i < countConditions; i++) {
                        int indexConditions = random.nextInt(ConditionsOpen.length);
                        ConditionsOpen.markerRandomOrDefaultOpen.set(indexConditions,true);
                    }
                    processing(capital,spanInterval,isShowCompoundInterest,interval,countInterval,isShowIntervalYields,
                            isLimitLoss,pathFile);
                    ConditionsOpen.defaultSetListBooleanFromConditionOpen();
                }
            }else {
                //====запуск програмы без каких либо параметров рамндомизации, все параметры по умолчанию в DateProviders.StaticData
                for (String pathFile : pathFiles) {//запуск программы для каждого загруженного файла с историч данными
                    ConditionsOpen.markerRandomOrDefaultOpen.set(0,true);//установка условия входа по умолчанию
                    StaticData.limitStop = 199;
                    StaticData.minRP = 270;
                    StaticData.minMove = 90;
                    StaticData.largeMove = 1060;
//                DateProviders.StaticData.maxLossTotal = -0.0055f;
//            DateProviders.StaticData.minNakedSize = random.nextFloat();//минимальный диапазон для неголого закрытия
//            DateProviders.StaticData.conditionExitLargeCandle = random.nextFloat();//параметр для выхода для больш свечи
                    processing(capital,spanInterval,isShowCompoundInterest,interval,countInterval,isShowIntervalYields,
                            isLimitLoss,pathFile);
                    ConditionsOpen.defaultSetListBooleanFromConditionOpen();
                }//конец iter файла
                //=====================================================================================================
            }
        }
    }

    private static void processing(float capital, int spanInterval, boolean isShowCompoundInterest,
                                   String interval, int countInterval, boolean isShowIntervalYields,
                                   boolean isLimitLoss, String file) throws IOException, ParseException {
        BufferedWriter writerFile = new BufferedWriter(new FileWriter("F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\totalData.txt",true));
        if (!Files.exists(Paths.get(file))){
            System.out.println("Файла не существует");
            return;
        }
        //==подготавливаем данные для склыдвания результата сделок в сложный процент
        float warrantyProvision = 0.0f;
        String codeForMaps = Paths.get(file).getFileName().toString().substring(0,2).toUpperCase();//код для получения значения гарантийного обеспечения из Map
        if (isShowCompoundInterest){
            StaticData.capitalCompoundInterest = capital;
            warrantyProvision = DateProviders.StaticData.mapWarrantyProvision.get(codeForMaps);//получение значения Вариационной маржи
        }
        //==========================================================================

        BufferedReader in = new BufferedReader(new FileReader(file));
        String line = "";

        StaticData.countContractsCompoundInterest = capital / DateProviders.StaticData.mapWarrantyProvision.get(codeForMaps);//кол.контрактов для сложного процента и с учетом spanMonth
        final float countContractsOrdinary = capital / DateProviders.StaticData.mapWarrantyProvision.get(codeForMaps);//кол.контрактов для yields

        while ((line = in.readLine()) != null){
            mainMethod(line,isShowIntervalYields,interval,countInterval,countContractsOrdinary,isShowCompoundInterest,spanInterval,warrantyProvision,isLimitLoss,countContractsOrdinary);

        }//while end
        //==если в конце сделка осталась открыта, то нужно ее закрыть и результат последней сделки прибавить к общему
        if (StaticData.isOpenDeal){
            float result = 0.0f;
            if (StaticData.isBuy){
                StaticData.close = StaticData.candleList.get(StaticData.candleList.size() - 1).getClose();
                result = (StaticData.close - StaticData.open);
            }else {
                StaticData.close = StaticData.candleList.get(StaticData.candleList.size() - 1).getClose();
                result = (StaticData.open - StaticData.close);
            }
            StaticData.countCandleOpenPosition = 0;
            StaticData.yields = StaticData.yields + result;
            StaticData.isOpenDeal = false;
            ConditionsClose.defaultSetListBooleanFromConditionClose();
            print("Доход со сделки = ",result);
            //===тут считаем прибыль или убыток по сложному проценту с учетом капитализации  и spanMonth
            StaticData.yieldsCompoundInterest = (result * StaticData.countContractsCompoundInterest) + StaticData.yieldsCompoundInterest;
            StaticData.capitalCompoundInterest = (StaticData.capitalCompoundInterest + StaticData.yieldsCompoundInterest) - capital;
            //=============================================================================

            //===тут мы выстраиваем логику подсчета максимальной просадки
            countMaxDescendingMoneyEnd(result * countContractsOrdinary,isLimitLoss,countContractsOrdinary);
            //===========================================================
            //===кладем результат сделки в Лист
            posAndNegList(result);
            //========================================================================
        }
        //===========================================================================================================

        //===тут мы считаем максимальную просадку
        float f1 = 0.0f;
        for (Float f2 : StaticData.maxDescendingMoneyList) {
            if (f2 < f1){
                f1 = f2;
            }
        }
        //===========================================================
        StringBuilder total = new StringBuilder();
        if (file.contains("ED")){
            StaticData.yields *= 10000;
        }else if (file.contains("BR")){
            StaticData.yields *= 100;
        }else if (file.contains("GD")){
            StaticData.yields *= 10;
        }else if (file.contains("SF")){
            StaticData.yields *= 100;
        }

        //===умножение на количество контрактов, который не учитывает сложный процент
        StaticData.yields = (StaticData.yields * countContractsOrdinary) * DateProviders.StaticData.mapPriceStep.get(codeForMaps);
        //==========================================================================================================

        //===подсчет средней прибыли и убытка на сделку из positiveRes List и negativeRes List
        double optionalPos = (StaticData.positiveRes.stream().mapToDouble(Deal::getYields).sum() /
                StaticData.positiveRes.size());
        double optionalNeg = (StaticData.negativeRes.stream().mapToDouble(Deal::getYields).sum() /
                StaticData.negativeRes.size());
        //====================================================================================

        //===среднее число последовательных неудачных сделок
        float averageFailSequence = 0;
        for (int i = 0; i < StaticData.countFailSequenceList.size(); i++) {
            if (StaticData.countFailSequenceList.get(i) > 0){
                averageFailSequence = averageFailSequence + StaticData.countFailSequenceList.get(i);
            }
        }
        averageFailSequence = averageFailSequence / StaticData.countFailSequenceList.size();
        //==================================================
//            System.out.println(Arrays.toString(new List[]{DateProviders.StaticData.countFailSequenceList}));

        if (file.contains("ED")){
            total.append(String.format("Yields = %.4f. Стоп = %.4f. minRP = %.4f. minMove = %.4f. largeMove = %.4f. maxLossTotal = %.4f. minNakedSize = %.4f. Путь = %s. Макс.просадка = %.3f. Полож сделок = %d. Отриц сделок = %d. Средняя прибыль на сделку = %.2f. Средняя убыток на сделку = %.2f.Максимальное кол. неудачных сделок = %d.Среднее количество неудачных сделок = %.3f.\n",
                    StaticData.yields,StaticData.limitStop,StaticData.minRP,StaticData.minMove,StaticData.largeMove,StaticData.maxLossTotal,StaticData.minNakedSize,file.substring(60),f1,StaticData.positiveRes.size(),StaticData.negativeRes.size(),optionalPos,optionalNeg,StaticData.countFailSequence,averageFailSequence));
        }else if (file.contains("BR") || file.contains("GD")){
            total.append(String.format("Yields = %.2f. Стоп = %.2f. minRP = %.2f. minMove = %.2f. largeMove = %.2f. maxLossTotal = %.4f. minNakedSize = %.2f. Путь = %s. Макс.просадка = %.3f. Полож сделок = %d. Отриц сделок = %d. Средняя прибыль на сделку = %.2f. Средняя убыток на сделку = %.2f.Максимальное кол. неудачных сделок = %d.Среднее количество неудачных сделок = %.3f.\n",
                    StaticData.yields,StaticData.limitStop,StaticData.minRP,StaticData.minMove,StaticData.largeMove,StaticData.maxLossTotal,StaticData.minNakedSize,file.substring(60),f1,StaticData.positiveRes.size(),StaticData.negativeRes.size(),optionalPos,optionalNeg,StaticData.countFailSequence,averageFailSequence));
        }else {
            total.append(String.format("Yields = %.0f. Стоп = %.0f. minRP = %.0f. minMove = %.0f. largeMove = %.0f. maxLossTotal = %.4f. minNakedSize = %.0f. Путь = %s. Макс.просадка = %.3f. Полож сделок = %d. Отриц сделок = %d. Средняя прибыль на сделку = %.2f. Средняя убыток на сделку = %.2f.Максимальное кол. неудачных сделок = %d.Среднее количество неудачных сделок = %.3f.\n",
                    StaticData.yields,StaticData.limitStop,StaticData.minRP,StaticData.minMove,StaticData.largeMove,StaticData.maxLossTotal,StaticData.minNakedSize,file.substring(60),f1,StaticData.positiveRes.size(),StaticData.negativeRes.size(),optionalPos,optionalNeg,StaticData.countFailSequence,averageFailSequence));
        }

        //==вычитываем начальный капитал из заработанного, чтобы получит чистую прибыль, если прибыль с учетом капитализации
        if (isShowCompoundInterest){
            total.append(String.format(" Yields с учетом кол. контрактов и капитализации = %.3f.\n",StaticData.capitalCompoundInterest - capital));
        }
        //==================================================================================================================

//            int total2 = (int) (DateProviders.StaticData.yieldsLimitFailSequence * countContractsOrdinary);
        if (StaticData.yields > DateProviders.StaticData.rangeYields){
            System.out.print(total.toString());
            writerFile.write(total.toString());
            writerFile.flush();
//                System.out.println(DateProviders.StaticData.conditionExitLargeCandle);
//                System.out.println(total2 + " DateProviders.StaticData.yieldsLimitFailSequence");
        }
        StaticData.candleList.clear();
        StaticData.commonListDeals.clear();
        StaticData.open = 0;
        StaticData.close = 0;
        StaticData.openDeal = null;
        StaticData.currentFailSequence = 0;
        StaticData.yieldsLimitFailSequence = 0;
        StaticData.isBuy = true;
        StaticData.yields = 0;
        StaticData.positiveRes.clear();
        StaticData.negativeRes.clear();
        StaticData.countCandleOpenPosition = 0;
        StaticData.maxDescendingMoney = 0;
        StaticData.maxDescendingMoneyList.clear();
        StaticData.isFirstCandleYields = true;
        StaticData.intervalYields = 0;
        StaticData.countFailSequence = 0;
        StaticData.tempFailSequence = 0;
        StaticData.isFirstFailSequence = false;
        StaticData.countFailSequenceList.clear();
        StaticData.isFirstCandle = true;
        StaticData.capitalCompoundInterest = 0;
        StaticData.countContractsCompoundInterest = 0;
        StaticData.yieldsCompoundInterest = 0;
        StaticData.totalIntervalCompoundInterest = 0;

//            System.out.println("Сделки дохдоность последовательно: " + Arrays.toString(new List[]{DateProviders.StaticData.commonListDeals}));

    }//test
    private static void print(String msg,float result){
//        if (true && result > 4000.0f || true && result < -4000.0f){
//            System.out.println(msg + " = " + result);
//        }
        if (false){
            System.out.println(msg + " = " + result);
        }
    }

    private static void countMaxDescendingMoney(float result,boolean isLimitLoss,float ordinaryCountContracts){
        if (isLimitLoss && (StaticData.tempYields + result) > StaticData.maxLossTotal){
            StaticData.maxDescendingMoneyList.add((StaticData.tempYields + result) * ordinaryCountContracts);
            return;
        }
        if (result < 0){
            StaticData.maxDescendingMoney = -(Math.abs(StaticData.maxDescendingMoney) + Math.abs(result));
        }else if (result >= Math.abs(StaticData.maxDescendingMoney) && result > 0 && StaticData.maxDescendingMoney != 0) {
            StaticData.maxDescendingMoneyList.add(StaticData.maxDescendingMoney);
            StaticData.maxDescendingMoney = 0;
        }else if (result > 0 && StaticData.maxDescendingMoney != 0){
            StaticData.maxDescendingMoney = -(Math.abs(StaticData.maxDescendingMoney) - result);
        }
    }

    //метод для подсчета максимальной просадки, когда свечи закончились а сделка осталась открыта
    private static void countMaxDescendingMoneyEnd(float result,boolean isLimitLoss,float ordinaryCountContracts){
        if (result < 0){
            StaticData.maxDescendingMoney = -(Math.abs(StaticData.maxDescendingMoney) + Math.abs(result));
            StaticData.maxDescendingMoneyList.add(StaticData.maxDescendingMoney);
        }else if (result >= Math.abs(StaticData.maxDescendingMoney) && result > 0 && StaticData.maxDescendingMoney != 0) {
            StaticData.maxDescendingMoneyList.add(StaticData.maxDescendingMoney);
            StaticData.maxDescendingMoney = 0;
        }
    }

    private static void addDataToMap(){
        DateProviders.StaticData.mapWarrantyProvision.put("SI",10483.00f);//заглушка чтобы быстрее тестировать
        DateProviders.StaticData.mapWarrantyProvision.put("RI",68950.00f);//заглушка чтобы быстрее тестировать
        DateProviders.StaticData.mapWarrantyProvision.put("BR",23263.00f);//заглушка чтобы быстрее тестировать
        DateProviders.StaticData.mapWarrantyProvision.put("SR",5106.00f);//заглушка чтобы быстрее тестировать
        DateProviders.StaticData.mapWarrantyProvision.put("GD",6839.00f);//заглушка чтобы быстрее тестировать
        DateProviders.StaticData.mapWarrantyProvision.put("ED",2748.00f);//заглушка чтобы быстрее тестировать
        DateProviders.StaticData.mapWarrantyProvision.put("SF",2985.79f);//заглушка чтобы быстрее тестировать

        DateProviders.StaticData.mapPriceStep.put("SI",1.00f);//заглушка чтобы быстрее тестировать
        DateProviders.StaticData.mapPriceStep.put("RI",1.3088f);//заглушка чтобы быстрее тестировать
        DateProviders.StaticData.mapPriceStep.put("BR",6.5444f);//заглушка чтобы быстрее тестировать
        DateProviders.StaticData.mapPriceStep.put("SR",1.00f);//заглушка чтобы быстрее тестировать
        DateProviders.StaticData.mapPriceStep.put("GD",6.5444f);//заглушка чтобы быстрее тестировать
        DateProviders.StaticData.mapPriceStep.put("ED",6.5444f);//заглушка чтобы быстрее тестировать
        DateProviders.StaticData.mapPriceStep.put("SF",0.65444f);//заглушка чтобы быстрее тестировать

//        DateProviders.QueryDataMOEX.getWarranty(DateProviders.StaticData.mapWarrantyProvision,DateProviders.StaticData.mapPriceStep,"RI","SF","ED","SI","GD","BR");
    }

    //===показывает промежуточную прибыль в зависимости от заданного интервала
    //к примеру ("M",1) - это 1 месяц
    private static void showIntervalYields(Candle candle,String interval,int count,float countContractsOrdinary){
        interval = interval.toUpperCase();
        String nameInterval = "";
        long intervalMS = 0;
        switch (interval){
            case "M": intervalMS = 2592000000L * count ; nameInterval = "Month";break;
            case "D": intervalMS = 86400000L * count;nameInterval = "Day";break;
            case "W": intervalMS = 604800000L * count;nameInterval = "Week";break;
            case "Y": intervalMS = 31104000000L * count;nameInterval = "Year";break;
            default:
                System.out.println("НЕВЕРНО УКАЗАН ИНТЕРВАЛ ДЛЯ ОТОБРАЖЕНИЯ ДОХОДНОСТИ");
        }
        if (!StaticData.isFirstCandleYields && candle.getDateClose().after(StaticData.dateForIntervalYields)){
            Date prevPeriod = new Date(StaticData.dateForIntervalYields.getTime() - intervalMS);
            Date toPeriod = new Date(candle.getDateClose().getTime());
            if (StaticData.yields < 0 && StaticData.intervalYields < 0){
                float clearYields = StaticData.yields - (StaticData.intervalYields);
//                System.out.printf("Yields for period from (%s) to (%s) = %.3f\n",prevPeriod.toString(),toPeriod.toString(),clearYields * countContractsOrdinary);
                StaticData.intervalYields = StaticData.yields;
            }else {
                float clearYields = StaticData.yields - StaticData.intervalYields;
//                System.out.printf("Yields for period from (%s) to (%s) = %.3f\n",prevPeriod.toString(),toPeriod.toString(),clearYields * countContractsOrdinary);
                StaticData.intervalYields = StaticData.yields;
            }
        }
        StaticData.dateForIntervalYields.setTime(StaticData.dateForIntervalYields.getTime() + intervalMS);
        StaticData.tempYields = 0;
        while (candle.getDateClose().after(StaticData.dateForIntervalYields)){
            StaticData.dateForIntervalYields.setTime(StaticData.dateForIntervalYields.getTime() + intervalMS);
        }
    }

    //анализ проведенных сделок
    private static void analyzeCompletedDeals(List<Candle> listDeals){

    }

    private static void analyzeMaxFailDeals(float result,Candle candle){
        if (result < 0){
            StaticData.tempFailSequence++;
        }
        if (StaticData.tempFailSequence == 1 && !StaticData.isFirstFailSequence){
            StaticData.isFirstFailSequence = true;
//            System.out.println("from " + candle.getDateClose() + " = " + DateProviders.StaticData.tempFailSequence);
        }
        if (result > 0 && StaticData.tempFailSequence != 0 && StaticData.tempFailSequence >= StaticData.countFailSequence){
            StaticData.countFailSequenceList.add(StaticData.tempFailSequence);
            StaticData.countFailSequence = StaticData.tempFailSequence;
            StaticData.isFirstFailSequence = false;
//            System.out.println("to " + candle.getDateClose() + " = " + DateProviders.StaticData.tempFailSequence);
            StaticData.tempFailSequence = 0;
        }else if (result > 0 && StaticData.tempFailSequence < StaticData.countFailSequence){
            StaticData.countFailSequenceList.add(StaticData.tempFailSequence);
            StaticData.isFirstFailSequence = false;
//            System.out.println("to " + candle.getDateClose() + " = " + DateProviders.StaticData.tempFailSequence);
            StaticData.tempFailSequence = 0;
        }
    }

    private static void posAndNegList(float result){
        if (result > 0){
            if (StaticData.currentFailSequence >= 2){
                StaticData.currentFailSequence = 0;
            }else {
                StaticData.yieldsLimitFailSequence = StaticData.yieldsLimitFailSequence + result;
                StaticData.currentFailSequence = 0;
            }
            StaticData.positiveRes.add(new Deal(StaticData.openDeal,StaticData.candleList.get(StaticData.candleList.size() - 1).getDateClose(),result,StaticData.open,StaticData.close,StaticData.isBuy));
        }else if (result < 0){
            if (StaticData.currentFailSequence >= 2){

            }else {
                StaticData.yieldsLimitFailSequence = StaticData.yieldsLimitFailSequence + result;
            }
            StaticData.currentFailSequence++;
            StaticData.negativeRes.add(new Deal(StaticData.openDeal,StaticData.candleList.get(StaticData.candleList.size() - 1).getDateClose(),result,StaticData.open,StaticData.close,StaticData.isBuy));
        }
        StaticData.commonListDeals.add(result);
    }

    private static void method1(boolean isShowIntervalYields,Candle candle,String interval,int countInterval,float countContractsOrdinary){
        if (StaticData.isFirstCandleYields && isShowIntervalYields){
            StaticData.dateForIntervalYields.setTime(candle.getDateClose().getTime() - 3600000L);//уменьшаем на час, чтобы до начала дня отображалась доходность
            showIntervalYields(candle,interval,countInterval,countContractsOrdinary);
            StaticData.isFirstCandleYields = false;
        }else if (candle.getDateClose().after(StaticData.dateForIntervalYields) && isShowIntervalYields){
            showIntervalYields(candle,interval,countInterval,countContractsOrdinary);
        }
    }

    private static float countingResult(){
        float a = 0;
        if (StaticData.isBuy){
            a = (StaticData.candleList.get(StaticData.candleList.size() - 1).getClose() - StaticData.open);
        }else {
            a = (StaticData.open - StaticData.candleList.get(StaticData.candleList.size() - 1).getClose());
        }
        return a;
    }

    private static float method4(Candle candle){
        float a = 0;
        if (StaticData.isBuy){
            a = ((candle.getOpen() - StaticData.limitStop) - StaticData.open);
        }else {
            a = (StaticData.open - (candle.getOpen() + StaticData.limitStop));
        }
        return a;
    }

    private static float method0(Candle candle){
        float a = 0;
        if (StaticData.isBuy){
            a = ((StaticData.candleList.get(StaticData.candleList.size() - 1).getOpen() + StaticData.candleList.get(StaticData.candleList.size() - 1).getClose())
                    / 2)  - StaticData.open;
        }else {
            a = StaticData.open - ((StaticData.candleList.get(StaticData.candleList.size() - 1).getOpen() + StaticData.candleList.get(StaticData.candleList.size() - 1).getClose())
                    / 2);
        }
        return a;
    }

    private static float method5(){
        float a = 0;
        if (StaticData.isBuy){
            a = (StaticData.candleList.get(StaticData.candleList.size() - 1).getLow() - StaticData.open);
        }else {
            a = (StaticData.open - StaticData.candleList.get(StaticData.candleList.size() - 1).getHigh());
            StaticData.countCandleOpenPosition = 0;
        }
        return a;
    }

    private static float method6(float result,boolean isLimitLoss){
        ConditionsClose.defaultSetListBooleanFromConditionClose();
        StaticData.isOpenDeal = false;
        StaticData.countCandleOpenPosition = 0;
        //==тут мы складываем додходность в перменную для того, чтобы ограничить убытки, если мы установили огранич убытка к примеру в -546 пунктов
        if (isLimitLoss && StaticData.maxLossTotal > StaticData.tempYields){
            return StaticData.yields;
        }
        StaticData.tempYields = StaticData.tempYields + result;
        return StaticData.yields + result;
    }

    private static void method7(boolean isShowCompoundInterest,Candle candle,float warrantyProvision,float result){
        if (isShowCompoundInterest && candle.getDateClose().after(StaticData.dateFirstCandle)){
            StaticData.capitalCompoundInterest = StaticData.capitalCompoundInterest + StaticData.yieldsCompoundInterest;
            StaticData.countContractsCompoundInterest = (float) Math.floor(StaticData.capitalCompoundInterest / warrantyProvision);
            StaticData.yieldsCompoundInterest = 0;
            StaticData.dateFirstCandle.setTime(StaticData.dateFirstCandle.getTime() + StaticData.totalIntervalCompoundInterest);
            float returnNet = StaticData.countContractsCompoundInterest * result;
            StaticData.yieldsCompoundInterest += returnNet;
        }else if (isShowCompoundInterest){
            StaticData.yieldsCompoundInterest = (result * StaticData.countContractsCompoundInterest) + StaticData.yieldsCompoundInterest;
        }
    }

    private static float amountYields(float result, boolean isLimitLoss){
        ConditionsClose.defaultSetListBooleanFromConditionClose();
        StaticData.countCandleOpenPosition = 0;
        StaticData.isOpenDeal = false;
        //==тут мы складываем додходность в перменную для того, чтобы ограничить убытки, если мы установили огранич убытка к примеру в -546 пунктов
        StaticData.tempYields = StaticData.tempYields + result;
        if (isLimitLoss && StaticData.maxLossTotal > StaticData.tempYields){
            return StaticData.yields;
        }
        return StaticData.yields + result;
    }

    private static float endDay(float result){
        ConditionsClose.defaultSetListBooleanFromConditionClose();
        StaticData.countCandleOpenPosition = 0;
        StaticData.isOpenDeal = false;
        //==тут мы складываем додходность в перменную для того, чтобы ограничить убытки, если мы установили огранич убытка к примеру в -546 пунктов
        StaticData.tempYields = StaticData.tempYields + result;
        return StaticData.yields + result;
    }

    private static void mainMethod(String line,boolean isShowIntervalYields,String interval,int countInterval,
                                   float countContractsOrdinary,boolean isShowCompoundInterest,int spanInterval,
                                   float warrantyProvision,
                                   boolean isLimitLoss,
                                   float ordinaryCountContracts) throws ParseException {
        String[] array = line.split("\\s");
        //==преобразование в Дату и Время
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = sdf.parse(String.format("%s.%s.%s %s:%s:%s", array[2].substring(0,4), array[2].substring(4,6),
                array[2].substring(6,8),array[3].substring(0,2),array[3].substring(2,4),array[3].substring(4,6)));

        Candle candle = new Candle(array[1], date, Float.parseFloat(array[4]), Float.parseFloat(array[5]), Float.parseFloat(array[6]), Float.parseFloat(array[7]), Integer.parseInt(array[8]));

        //==подготовка работы инструментов из пакета Instruments
        MovingAverage.findMovingAverage(TypeCandleParameter.CLOSE,StaticData.rangeCandleForMA);
        ATR.findATR(candle);
        //======================================================

        //=подготовка статических данных для отображения доходности за определенный промежуток времени и отобр доходности
        //method1
        method1(isShowIntervalYields,candle,interval,countInterval,countContractsOrdinary);
        //===============================================================================================================
        //передвигаем дату для сложного процента
        if (StaticData.isFirstCandle && isShowCompoundInterest){
//                    totalMonth = 2592000000L * spanInterval;//промежуток месяц
//                    totalMonth = 86400000L * spanInterval;//промежуток день
            StaticData.totalIntervalCompoundInterest = 604800000L * spanInterval;//промежуток неделя
            long tempDate = candle.getDateClose().getTime();
            StaticData.isFirstCandle = false;
            StaticData.dateFirstCandle.setTime(StaticData.totalIntervalCompoundInterest + tempDate);
        }

        //====тут очищаем стэк если начался новый день, и закрываем позицию за пред день, если не были закрыты
        if (StaticData.candleList.size() > 0){
            if (Integer.parseInt(StaticData.candleList.get(StaticData.candleList.size() - 1).getDateClose().toString().substring(8,10)) != Integer.parseInt(array[2].substring(6,8))){
                if (StaticData.isOpenDeal){
                    float result = 0.0f;
                    //method3
                    result = countingResult();

                    //===логика максимальных неудачных последовательных сделок
                    analyzeMaxFailDeals(result,candle);
                    //========================================================

                    StaticData.yields = endDay(result);

                    print("Доход со сделки = ",result);
                    //===тут считаем прибыль или убыток по сложному проценту с учетом капитализации  и spanMonth
                    //method7
                    method7(isShowCompoundInterest,candle,warrantyProvision,result);
                    //=============================================================================

                    //===тут мы выстраиваем логику подсчета максимальной просадки
                    countMaxDescendingMoney(result * countContractsOrdinary,isLimitLoss,ordinaryCountContracts);
                    //===========================================================
                    //===кладем результат сделки в Лист с отриц результатом или полож результатом, а тажк е кладем в любом случае в лист общий
                    posAndNegList(result);
                    //========================================================================
                }
            }
        }

        //когда открываем позицию, то увеличиваем счетчик накопленных по сделке свечей
        if (StaticData.isOpenDeal){
            //==сюда завидываем правильную дату открытия, ведь мы фиксир цену открытия по пред свече закрытия, и это создает проблему реального времени входа в позицию
            if (StaticData.countCandleOpenPosition  == 0){
                StaticData.openDeal = date;
            }
            //=========================================================================================================================================================
            StaticData.countCandleOpenPosition++;
        }

        //все возможные выходы из сделки
        if (!StaticData.candleList.isEmpty()){
            //условие выхода по стопу
            if (StaticData.countCandleOpenPosition <= 1 && ConditionsClose.
                    getCondition(0,candle)){

                float result = method4(candle);
                //===логика максимальных неудачных последовательных сделок
                analyzeMaxFailDeals(result,candle);
                //========================================================

                //amountYields
                StaticData.yields = amountYields(result,isLimitLoss);

                print("Доход со сделки = ",result);
                //===тут считаем прибыль или убыток по сложному проценту с учетом капитализации
                //method7
                method7(isShowCompoundInterest,candle,warrantyProvision,result);
                //=============================================================================

                //===тут мы выстраиваем логику подсчета максимальной просадки
                countMaxDescendingMoney(result * countContractsOrdinary,isLimitLoss,ordinaryCountContracts);
                //===========================================================

                //===кладем результат сделки в Лист
                posAndNegList(result);
                //========================================================================
            }

            //условие выхода за середину, большой свечи
            else if (StaticData.countCandleOpenPosition <= 1 &&
                    ConditionsClose.getCondition(2,candle)){
                float result = method0(candle);
                //===логика максимальных неудачных последовательных сделок
                analyzeMaxFailDeals(result,candle);
                //========================================================

                //amountYields
                StaticData.yields = amountYields(result,isLimitLoss);

                print("Доход со сделки = ",result);
                //===тут считаем прибыль или убыток по сложному проценту с учетом капитализации
                //method7
                method7(isShowCompoundInterest,candle,warrantyProvision,result);
                //=============================================================================

                //===тут мы выстраиваем логику подсчета максимальной просадки
                countMaxDescendingMoney(result * countContractsOrdinary,isLimitLoss,ordinaryCountContracts);
                //===========================================================

                //===кладем результат сделки в Лист
                posAndNegList(result);
                //========================================================================
            }

            ////условие выхода, когда цена выходит за край свечи
            else if (StaticData.countCandleOpenPosition > 1 &&
                    ConditionsClose.getCondition(1,candle)){

                float result = method5();
                //===логика максимальных неудачных последовательных сделок
                analyzeMaxFailDeals(result,candle);
                //========================================================

                //method6
                StaticData.yields = method6(result,isLimitLoss);

                print("Доход со сделки = ",result);
                //===тут считаем прибыль или убыток по сложному проценту с учетом капитализации
                //method7
                method7(isShowCompoundInterest,candle,warrantyProvision,result);
                //=============================================================================

                //===тут мы выстраиваем логику подсчета максимальной просадки
                countMaxDescendingMoney(result * countContractsOrdinary,isLimitLoss,ordinaryCountContracts);
                //===========================================================

                //===кладем результат сделки в Лист
                posAndNegList(result);
                //========================================================================
            }
        }

        //метод добавляет свечу в лист общий, находит макс необходимое число для расчета инструментов и удаляет из листа с начала свечи, если размер листа больше макс числа
        addAndCheckSizeList(candle);

        //Проверка условия входа в сделку в Conditions.ConditionsOpen.List<Boolean> markerRandomOrDefaultOpen
        if (!StaticData.isOpenDeal){
            for (int i = 0; i < ConditionsOpen.length; i++) {
                if (ConditionsOpen.markerRandomOrDefaultOpen.get(i)){
                    if (ConditionsOpen.getCondition(i,candle)){
                        StaticData.open = candle.getClose();
                        StaticData.isOpenDeal = true;//сделка открыта
                        break;
                    }
                }
            }
        }
    }

    private static void randomRI(){
        StaticData.limitStop = random.nextInt(200) + 30;
        StaticData.minRP = random.nextInt(50)*10 + 10;//(minReversePrice)условие обратного движения по свече
        StaticData.minMove = random.nextInt(150)*10 + 10;//сколько минимально должн пройти цена от открытия до закрытия
        StaticData.largeMove = random.nextInt(400)*10 + 10;//сколько должно пройти, чтобы сработало 2 условие
        StaticData.maxLossTotal = random.nextInt(990) - 1000;
        StaticData.minNakedSize = random.nextInt(20)*5 + 5;//минимальный диапазон для неголого закрытия
        StaticData.conditionExitLargeCandle = random.nextInt(100)*20;//параметр для выхода для больш свечи
        StaticData.bodyMove = 0;//сколько тело прошло от открыти до закрытия
        StaticData.mainShadow = 0;//основная тень одной из сторон пин бара
        StaticData.reverseShadow = 0;//обратная тень одной из сторон пин бара
        StaticData.coefficientBS = 0;//коэффициент тела > относительно тени

        StaticData.rangeExtremum = 0;//диапазон с конца листа, за этот диапазон смотрится и находится экстремум цены макс. и минимальной
        StaticData.rangeCandleForTrend = 2;//диапазон с конца листа, за этот диапазон смотрится и находится точка экстремума, после которой определяется тренд
        StaticData.rangeCandleForATR = 5;//диапазон с конца листа, за этот диапазон смотрится и находится ATR-среднее истинное значение-в каком диапазоне ходит цена внутри этогоо диапазона
        StaticData.rangeCandleForMA = 18;//диапазон с конца листа, за этот диапазон смотрится и находится сред

    }

    private static void randomSI(){
        StaticData.limitStop = random.nextInt(300) + 30;
        StaticData.minRP = random.nextInt(100)*10 + 10;//(minReversePrice)условие обратного движения по свече
        StaticData.minMove = random.nextInt(150)*10 + 10;//сколько минимально должн пройти цена от открытия до закрытия
        StaticData.largeMove = random.nextInt(400)*10 + 10;//сколько должно пройти, чтобы сработало 2 условие
//        DateProviders.StaticData.maxLossTotal = random.nextInt(990) - 1000;
//        DateProviders.StaticData.minNakedSize = random.nextInt(20)*5 + 5;//минимальный диапазон для неголого закрытия
//        DateProviders.StaticData.conditionExitLargeCandle = random.nextInt(100)*20;//параметр для выхода для больш свечи
//        DateProviders.StaticData.bodyMove = 0;//сколько тело прошло от открыти до закрытия
//        DateProviders.StaticData.mainShadow = 0;//основная тень одной из сторон пин бара
//        DateProviders.StaticData.reverseShadow = 0;//обратная тень одной из сторон пин бара
//        DateProviders.StaticData.coefficientBS = 0;//коэффициент тела > относительно тени

//        DateProviders.StaticData.rangeList = 0;//диапазон с конца листа, за этот диапазон смотрится и находится экстремум цены макс. и минимальной
//        DateProviders.StaticData.rangeCandleForTrend = 2;//диапазон с конца листа, за этот диапазон смотрится и находится точка экстремума, после которой определяется тренд
//        DateProviders.StaticData.rangeCandleForATR = 5;//диапазон с конца листа, за этот диапазон смотрится и находится ATR-среднее истинное значение-в каком диапазоне ходит цена внутри этогоо диапазона
//        DateProviders.StaticData.rangeCandleForMA = 18;//диапазон с конца листа, за этот диапазон смотрится и находится сред
    }

    private static void randomGD(){//1900.1 к примеру
        StaticData.limitStop = random.nextFloat() * 10;
        StaticData.minRP = random.nextFloat() * 10;//условие обратного движения по свече
        StaticData.minMove = random.nextFloat() * 10;//сколько минимально должн пройти цена от открытия до закрытия
        StaticData.largeMove = random.nextFloat() * 10;//сколько должно пройти, чтобы сработало 2 условие
        StaticData.maxLossTotal = random.nextFloat() * 10;
        StaticData.minNakedSize = random.nextInt(20)*5 + 5;//минимальный диапазон для неголого закрытия
        StaticData.conditionExitLargeCandle = random.nextInt(100)*20;//параметр для выхода для больш свечи
        StaticData.bodyMove = 0;//сколько тело прошло от открыти до закрытия
        StaticData.mainShadow = 0;//основная тень одной из сторон пин бара
        StaticData.reverseShadow = 0;//обратная тень одной из сторон пин бара
        StaticData.coefficientBS = 0;//коэффициент тела > относительно тени

        StaticData.rangeExtremum = 0;//диапазон с конца листа, за этот диапазон смотрится и находится экстремум цены макс. и минимальной
        StaticData.rangeCandleForTrend = 2;//диапазон с конца листа, за этот диапазон смотрится и находится точка экстремума, после которой определяется тренд
        StaticData.rangeCandleForATR = 5;//диапазон с конца листа, за этот диапазон смотрится и находится ATR-среднее истинное значение-в каком диапазоне ходит цена внутри этогоо диапазона
        StaticData.rangeCandleForMA = 18;//диапазон с конца листа, за этот диапазон смотрится и находится сред
    }

    private static void randomBR(){//82.14 к примеру
        StaticData.limitStop = (random.nextInt(50) / 100f) + 0.01f;
        StaticData.minRP = (random.nextInt(50) / 100f) + 0.01f;//условие обратного движения по свече
        StaticData.minMove = (random.nextInt(50) / 100f) + 0.01f;//сколько минимально должн пройти цена от открытия до закрытия
        StaticData.largeMove = (random.nextInt(50) / 100f) + 0.01f;//сколько должно пройти, чтобы сработало 2 условие
        StaticData.maxLossTotal = -(random.nextInt(50) / 100f) + 0.01f;
        StaticData.minNakedSize = random.nextInt(20)*5 + 5;//минимальный диапазон для неголого закрытия
        StaticData.conditionExitLargeCandle = random.nextInt(100)*20;//параметр для выхода для больш свечи
        StaticData.bodyMove = 0;//сколько тело прошло от открыти до закрытия
        StaticData.mainShadow = 0;//основная тень одной из сторон пин бара
        StaticData.reverseShadow = 0;//обратная тень одной из сторон пин бара
        StaticData.coefficientBS = 0;//коэффициент тела > относительно тени

        StaticData.rangeExtremum = 0;//диапазон с конца листа, за этот диапазон смотрится и находится экстремум цены макс. и минимальной
        StaticData.rangeCandleForTrend = 2;//диапазон с конца листа, за этот диапазон смотрится и находится точка экстремума, после которой определяется тренд
        StaticData.rangeCandleForATR = 5;//диапазон с конца листа, за этот диапазон смотрится и находится ATR-среднее истинное значение-в каком диапазоне ходит цена внутри этогоо диапазона
        StaticData.rangeCandleForMA = 18;//диапазон с конца листа, за этот диапазон смотрится и находится сред
    }

    private static void randomED(){//1.1099 к примеру
        StaticData.limitStop = (random.nextInt(50) / 10000f) + 0.0004f;
        StaticData.minRP = (random.nextInt(50) / 10000f) + 0.0001f;//условие обратного движения по свече
        StaticData.minMove = (random.nextInt(150) / 10000f) + 0.0005f;//сколько минимально должн пройти цена от открытия до закрытия
        StaticData.largeMove = (random.nextInt(500) / 10000f) + 0.0100f;//сколько должно пройти, чтобы сработало 2 условие
        StaticData.maxLossTotal = -(random.nextInt(500) / 10000f) + 0.0005f;
        StaticData.minNakedSize = random.nextInt(20)*5 + 5;//минимальный диапазон для неголого закрытия
        StaticData.conditionExitLargeCandle = random.nextInt(100)*20;//параметр для выхода для больш свечи
        StaticData.bodyMove = 0;//сколько тело прошло от открыти до закрытия
        StaticData.mainShadow = 0;//основная тень одной из сторон пин бара
        StaticData.reverseShadow = 0;//обратная тень одной из сторон пин бара
        StaticData.coefficientBS = 0;//коэффициент тела > относительно тени

        StaticData.rangeExtremum = 0;//диапазон с конца листа, за этот диапазон смотрится и находится экстремум цены макс. и минимальной
        StaticData.rangeCandleForTrend = 2;//диапазон с конца листа, за этот диапазон смотрится и находится точка экстремума, после которой определяется тренд
        StaticData.rangeCandleForATR = 5;//диапазон с конца листа, за этот диапазон смотрится и находится ATR-среднее истинное значение-в каком диапазоне ходит цена внутри этогоо диапазона
        StaticData.rangeCandleForMA = 18;//диапазон с конца листа, за этот диапазон смотрится и находится сред
    }

    private static void randomSF(){//420.10 к примеру
        StaticData.limitStop = (random.nextInt(50) / 100f) + 0.0001f;
        StaticData.minRP = (random.nextInt(80) / 100f) + 0.0001f;//условие обратного движения по свече
        StaticData.minMove = (random.nextInt(100) / 100f) + 0.0001f;//сколько минимально должн пройти цена от открытия до закрытия
        StaticData.largeMove = (random.nextInt(500) / 100f) + 0.0001f;//сколько должно пройти, чтобы сработало 2 условие
        StaticData.maxLossTotal = -(random.nextInt(70) / 100f) + 0.0001f;
        StaticData.minNakedSize = random.nextInt(20)*5 + 5;//минимальный диапазон для неголого закрытия
        StaticData.conditionExitLargeCandle = random.nextInt(100)*20;//параметр для выхода для больш свечи
        StaticData.bodyMove = 0;//сколько тело прошло от открыти до закрытия
        StaticData.mainShadow = 0;//основная тень одной из сторон пин бара
        StaticData.reverseShadow = 0;//обратная тень одной из сторон пин бара
        StaticData.coefficientBS = 0;//коэффициент тела > относительно тени

        StaticData.rangeExtremum = 0;//диапазон с конца листа, за этот диапазон смотрится и находится экстремум цены макс. и минимальной
        StaticData.rangeCandleForTrend = 2;//диапазон с конца листа, за этот диапазон смотрится и находится точка экстремума, после которой определяется тренд
        StaticData.rangeCandleForATR = 5;//диапазон с конца листа, за этот диапазон смотрится и находится ATR-среднее истинное значение-в каком диапазоне ходит цена внутри этогоо диапазона
        StaticData.rangeCandleForMA = 18;//диапазон с конца листа, за этот диапазон смотрится и находится сред
    }

    private static void addAndCheckSizeList(Candle candle){
        StaticData.candleList.add(candle);
        int maxNumber = Math.max(Math.max(StaticData.rangeExtremum,StaticData.rangeCandleForTrend),
                Math.max(StaticData.rangeCandleForATR,StaticData.rangeCandleForMA));
        if (StaticData.candleList.size() > maxNumber){
            StaticData.candleList.remove(0);
        }
    }

}
