package RunCounting;

import Conditions.ConditionsClose;
import Conditions.ConditionsOpen;
import DateProviders.StaticData;
import Instruments.ATR;
import Instruments.MovingAverage;
import Instruments.TypeCandleParameter;
import RandomParameterContracts.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private final static Random random = new Random();

    public static void main(String[] args) throws IOException, ParseException {

        testing(false,
                false,100000,900000,
                1, false,
                "W", 1,false,
                false,DateProviders.StaticData.allPathFiles);
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
    private static void testing(boolean isRandomConditions, boolean isRandomParameters, int countRandom, float capital,
                                int spanInterval, boolean isShowCompoundInterest, String interval,
                                int countInterval,
                                boolean isShowIntervalYields, boolean isLimitLoss, String... pathFiles) throws IOException, ParseException {
        addDataToMap();
        if (isRandomParameters){
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
                        case "RI": RI.randomRI();break;
                        case "SI": SI.randomSI();break;
                        case "GD": GD.randomGD();break;
                        case "ED": ED.randomED();break;
                        case "BR": BR.randomBR();break;
                        case "SF": SF.randomSF();break;
                    }
                    processing(capital,spanInterval,isShowCompoundInterest,interval,countInterval,isShowIntervalYields,
                            isLimitLoss,pathFile);

                }
                ConditionsOpen.defaultSetListBooleanFromConditionOpen();
            }//конец iter файла
        }else {
            if (isRandomConditions){
                for (String pathFile : pathFiles) {
//                    int countConditions = random.nextInt(ConditionsOpen.length);
//                    for (int i = 0; i < countConditions; i++) {
//                        int indexConditions = random.nextInt(ConditionsOpen.length);
//                        ConditionsOpen.markerRandomOrDefaultOpen.set(indexConditions,true);
//                    }
                    ConditionsOpen.markerRandomOrDefaultOpen.set(0,true);
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
        final float countContractsOrdinary = (capital / DateProviders.StaticData.mapWarrantyProvision.get(codeForMaps)) * StaticData.coefficientRiskManagement;//кол.контрактов для yields

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
            StaticData.yieldsCommons = StaticData.yieldsCommons + result;
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

            if (isShowIntervalYields){
                float clearYields = StaticData.yieldsCommons - (StaticData.intervalYields);
                checkDateForIntervalYieldsAndCandle(StaticData.candleList.get(StaticData.candleList.size() - 1), interval,countInterval);
                Date prevPeriod = StaticData.dateForIntervalYields;
                Date toPeriod = StaticData.candleList.get(StaticData.candleList.size() - 1).getDateClose();
                System.out.printf("Yields for period from (%s) to (%s) = %.3f\n",prevPeriod.toString(),toPeriod.toString(),clearYields * countContractsOrdinary);
            }
        }
        //===========================================================================================================

        //===тут мы считаем максимальную просадку
        float f1 = 0.0f;
        for (Float f2 : StaticData.maxDescendingMoneyList) {
            if (f2 < f1){
                f1 = f2;
            }
        }

        //========находим шаг цены в зависимости от инструмента и приводим к целому цислу после запятой слева
        if (file.contains("ED")){
            StaticData.yieldsCommons *= 10000;
        }else if (file.contains("BR")){
            StaticData.yieldsCommons *= 100;
        }else if (file.contains("GD")){
            StaticData.yieldsCommons *= 10;
        }else if (file.contains("SF")){
            StaticData.yieldsCommons *= 100;
        }

        //===умножение на количество контрактов, который не учитывает сложный процент
        StaticData.yieldsCommons = (StaticData.yieldsCommons * countContractsOrdinary) * DateProviders.StaticData.mapPriceStep.get(codeForMaps);
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

        StringBuilder total = new StringBuilder();
        total.append(String.format("Yields = %.4f. Стоп = %.4f. minRP = %.4f. minMove = %.4f. largeMove = %.4f. maxLossTotal = %.4f. minNakedSize = %.4f. slipPage = %.4f. conditionExitLargeCandle = %.4f.  bodyMove = %.4f. mainShadow = %.4f. reverseShadow  = %.4f. coefficientBS = %.4f. Путь = %s. Макс.просадка = %.3f. Полож сделок = %d. Отриц сделок = %d. Средняя прибыль на сделку = %.2f. Средняя убыток на сделку = %.2f.Максимальное кол. неудачных сделок = %d.Среднее количество неудачных сделок = %.3f.\n",
                StaticData.yieldsCommons,StaticData.limitStop,StaticData.minRP,StaticData.minMove,StaticData.largeMove,StaticData.maxLossTotal,StaticData.minNakedSize,StaticData.slipPage,StaticData.conditionExitLargeCandle,StaticData.bodyMove,StaticData.mainShadow,StaticData.reverseShadow,StaticData.coefficientBS,file.substring(60),f1,StaticData.positiveRes.size(),StaticData.negativeRes.size(),optionalPos,optionalNeg,StaticData.countFailSequence,averageFailSequence));

        //==вычитываем начальный капитал из заработанного, чтобы получит чистую прибыль, если прибыль с учетом капитализации
        if (isShowCompoundInterest){
            total.append(String.format(" Yields с учетом кол. контрактов и капитализации = %.3f.\n",StaticData.capitalCompoundInterest - capital));
        }
        //==================================================================================================================

//            int total2 = (int) (DateProviders.StaticData.yieldsLimitFailSequence * countContractsOrdinary);
        if (StaticData.yieldsCommons > DateProviders.StaticData.rangeYields){
            System.out.print(total.toString());
            writerFile.write(total.toString());
            writerFile.flush();
//                System.out.println(DateProviders.StaticData.conditionExitLargeCandle);
//                System.out.println(total2 + " DateProviders.StaticData.yieldsLimitFailSequence");
        }
        clearStaticField();
//            System.out.println("Сделки дохдоность последовательно: " + Arrays.toString(new List[]{DateProviders.StaticData.commonListDeals}));

    }//test
    private static void print(String msg,float result){
//        if (true && StaticData.resultBetweenOpenClose > 4000.0f || true && StaticData.resultBetweenOpenClose < -4000.0f){
//            System.out.println(msg + " = " + StaticData.resultBetweenOpenClose);
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
            if (StaticData.yieldsCommons < 0 && StaticData.intervalYields < 0){
                float clearYields = StaticData.yieldsCommons - (StaticData.intervalYields);
                System.out.printf("Yields for period from (%s) to (%s) = %.3f\n",prevPeriod.toString(),toPeriod.toString(),clearYields * countContractsOrdinary);
                StaticData.intervalYields = StaticData.yieldsCommons;
            }else {
                float clearYields = StaticData.yieldsCommons - StaticData.intervalYields;
                System.out.printf("Yields for period from (%s) to (%s) = %.3f\n",prevPeriod.toString(),toPeriod.toString(),clearYields * countContractsOrdinary);
                StaticData.intervalYields = StaticData.yieldsCommons;
            }
        }
        StaticData.dateForIntervalYields.setTime(StaticData.dateForIntervalYields.getTime() + intervalMS);
        StaticData.tempYields = 0;
        while (candle.getDateClose().after(StaticData.dateForIntervalYields)){
            StaticData.dateForIntervalYields.setTime(StaticData.dateForIntervalYields.getTime() + intervalMS);
        }
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

    private static float countingResult(TypeCountingResult typeCountingResult,Candle candle){
        float a = 0;
        if (StaticData.isBuy){
            switch (typeCountingResult){
                case CLEARDAYS: {
                    a = (StaticData.candleList.get(StaticData.candleList.size() - 1).getClose() - StaticData.open);
                    break;
                }
                case EXITSTOP: {
                    a = ((candle.getOpen() - StaticData.limitStop) - StaticData.open);
                    break;
                }
                case LARGECANDLE: {
                    a = ((StaticData.candleList.get(StaticData.candleList.size() - 1).getOpen() + StaticData.candleList.get(StaticData.candleList.size() - 1).getClose())
                            / 2)  - StaticData.open;
                    break;
                }
                case EDGECANDLE: {
                    a = (StaticData.candleList.get(StaticData.candleList.size() - 1).getLow() - StaticData.open);
                    break;
                }
            }
        }else {
            switch (typeCountingResult){
                case CLEARDAYS: {
                    a = (StaticData.open - StaticData.candleList.get(StaticData.candleList.size() - 1).getClose());
                    break;
                }
                case EXITSTOP: {
                    a = (StaticData.open - (candle.getOpen() + StaticData.limitStop));
                    break;
                }
                case LARGECANDLE: {
                    a = StaticData.open - ((StaticData.candleList.get(StaticData.candleList.size() - 1).getOpen() + StaticData.candleList.get(StaticData.candleList.size() - 1).getClose())
                            / 2);
                    break;
                }
                case EDGECANDLE: {
                    a = (StaticData.open - StaticData.candleList.get(StaticData.candleList.size() - 1).getHigh());
                    StaticData.countCandleOpenPosition = 0;
                    break;
                }
            }
        }
        return (a + (StaticData.slipPage));
    }

    private static float method6(float result,boolean isLimitLoss){
        ConditionsClose.defaultSetListBooleanFromConditionClose();
        StaticData.isOpenDeal = false;
        StaticData.countCandleOpenPosition = 0;
        //==тут мы складываем додходность в перменную для того, чтобы ограничить убытки, если мы установили огранич убытка к примеру в -546 пунктов
        if (isLimitLoss && StaticData.maxLossTotal > StaticData.tempYields){
            return StaticData.yieldsCommons;
        }
        StaticData.tempYields = StaticData.tempYields + result;
        return StaticData.yieldsCommons + result;
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

    private static float amountYieldsWithMaxLossTotal(float result, boolean isLimitLoss){
        ConditionsClose.defaultSetListBooleanFromConditionClose();
        StaticData.countCandleOpenPosition = 0;
        StaticData.isOpenDeal = false;
        //==тут мы складываем додходность в перменную для того, чтобы ограничить убытки, если мы установили огранич убытка к примеру в -546 пунктов
        StaticData.tempYields = StaticData.tempYields + result;
        if (isLimitLoss && StaticData.maxLossTotal > StaticData.tempYields){
            return StaticData.yieldsCommons;
        }
        return StaticData.yieldsCommons + result;
    }

    private static float endDay(float result){
        ConditionsClose.defaultSetListBooleanFromConditionClose();
        StaticData.countCandleOpenPosition = 0;
        StaticData.isOpenDeal = false;
        //==тут мы складываем додходность в перменную для того, чтобы ограничить убытки, если мы установили огранич убытка к примеру в -546 пунктов
        StaticData.tempYields = StaticData.tempYields + result;
        return StaticData.yieldsCommons + result;
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
                    result = countingResult(TypeCountingResult.CLEARDAYS,candle);

                    //===логика максимальных неудачных последовательных сделок
                    analyzeMaxFailDeals(result,candle);
                    //========================================================

                    StaticData.yieldsCommons = endDay(result);

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

                //===посчет результат по этой сделке
                float result = countingResult(TypeCountingResult.EXITSTOP,candle);

                //===логика максимальных неудачных последовательных сделок
                analyzeMaxFailDeals(result,candle);
                //========================================================

                //подсчет доходности с ограничением потерь maxLossTotal
                StaticData.yieldsCommons = amountYieldsWithMaxLossTotal(result,isLimitLoss);

                print("Доход со сделки = ",result);

                //===тут считаем прибыль или убыток по сложному проценту с учетом капитализации
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

                //===посчет результат по этой сделке
                float result = countingResult(TypeCountingResult.LARGECANDLE,candle);

                //===логика максимальных неудачных последовательных сделок
                analyzeMaxFailDeals(result,candle);
                //========================================================

                //amountYields
                StaticData.yieldsCommons = amountYieldsWithMaxLossTotal(result,isLimitLoss);

                print("Доход со сделки = ",result);

                //===тут считаем прибыль или убыток по сложному проценту с учетом капитализации
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

                //===посчет результат по этой сделке
                float result = countingResult(TypeCountingResult.EDGECANDLE,candle);

                //===логика максимальных неудачных последовательных сделок
                analyzeMaxFailDeals(result,candle);
                //========================================================

                //method6
                StaticData.yieldsCommons = method6(result,isLimitLoss);

                print("Доход со сделки = ",result);

                //===тут считаем прибыль или убыток по сложному проценту с учетом капитализации
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

    private static void addAndCheckSizeList(Candle candle){
        StaticData.candleList.add(candle);
        int maxNumber = Math.max(Math.max(StaticData.rangeExtremum,StaticData.rangeCandleForTrend),
                Math.max(StaticData.rangeCandleForATR,StaticData.rangeCandleForMA));
        if (StaticData.candleList.size() > maxNumber){
            StaticData.candleList.remove(0);
        }
    }

    private static void checkDateForIntervalYieldsAndCandle(Candle candle,String interval,int count){
        if (StaticData.dateForIntervalYields.after(candle.getDateClose())){
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
            intervalMS = intervalMS - 3600000L;
            StaticData.dateForIntervalYields.setTime(StaticData.dateForIntervalYields.getTime() - intervalMS);
        }
    }

    private static void clearStaticField(){
        StaticData.candleList.clear();
        StaticData.commonListDeals.clear();
        StaticData.open = 0;
        StaticData.close = 0;
        StaticData.openDeal = null;
        StaticData.currentFailSequence = 0;
        StaticData.yieldsLimitFailSequence = 0;
        StaticData.isBuy = true;
        StaticData.yieldsCommons = 0;
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
    }
}
