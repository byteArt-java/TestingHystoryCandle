package RunCounting;

import Conditions.ConditionsClose;
import Conditions.ConditionsOpen;
import DateProviders.StaticData;
import Instruments.ATR;
import Instruments.MovingAverage;
import Instruments.TypeCandleParameter;
import RandomParameterContracts.SI;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private final static Random random = new Random();

    public static void main(String[] args) throws Exception {

        testing(false,
                false,100000,900000,
                1, false,//показ доходности сложного процента за определ интервал
                "D", 1,true,//показ доходности через опред интервал
                true,"D",1,//ограничение потерь через опред интервал
                DateProviders.StaticData.allPathFiles);
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
                                boolean isShowIntervalYields,
                                boolean isLimitLoss,String intervalLimitLoss,int countIntervalLimitLoss,
                                String... pathFiles) throws Exception {
        addDataToMap();
        for (String pathFile : pathFiles) {
//            for (int i = 0; i < 200000; i++) {
                //            String nameInstrument = pathFile.substring(60,62).toUpperCase();
//            switch (nameInstrument){
//                case "RI": RI.randomRI();break;
//                case "SI": SI.randomSI();break;
//                case "GD": GD.randomGD();break;
//                case "ED": ED.randomED();break;
//                case "BR": BR.randomBR();break;
//                case "SF": SF.randomSF();break;
//            }
//                ConditionsOpen.markerRandomOrDefaultOpen.set(3,true);//установка условия входа по умолчанию
                ConditionsOpen.markerRandomOrDefaultOpen.set(0,true);//установка условия входа по умолчанию
                StaticData.limitStop = 199;//199
                StaticData.minRP = 270;//270
                StaticData.minMove = 90;//90
                StaticData.largeMove = 1060;//1060
                DateProviders.StaticData.maxLossTotal = -69;
//
//            DateProviders.StaticData.bodyMove = 170;//сколько тело прошло от открыти до закрытия
//            DateProviders.StaticData.mainShadow = 120;//основная тень одной из сторон пин бара
//            DateProviders.StaticData.reverseShadow = 850;//обратная тень одной из сторон пин бара
//            DateProviders.StaticData.coefficientBS = 2;//коэффициент тела > относительно тени
//            StaticData.rangeExtremum = 9;//диапазон с конца листа, за этот диапазон смотрится и находится экстремум цены макс. и минимальной

//            SI.randomSI();
//            DateProviders.StaticData.minNakedSize = random.nextFloat();//минимальный диапазон для неголого закрытия
//            DateProviders.StaticData.conditionExitLargeCandle = random.nextFloat();//параметр для выхода для больш свечи
                processing(capital,spanInterval,isShowCompoundInterest,interval,countInterval,isShowIntervalYields,
                        isLimitLoss,intervalLimitLoss,countIntervalLimitLoss,pathFile);
                ConditionsOpen.defaultSetListBooleanFromConditionOpen();
            }
//        }
    }

    private static void processing(float capital, int spanInterval, boolean isShowCompoundInterest,
                                   String interval, int countInterval, boolean isShowIntervalYields,
                                   boolean isLimitLoss, String intervalLimitLoss,int countIntervalLimitLoss,String file) throws Exception {
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
            mainMethod(line,isShowIntervalYields,interval,countInterval,countContractsOrdinary,
                    isShowCompoundInterest,spanInterval,warrantyProvision,isLimitLoss,intervalLimitLoss,
                    countIntervalLimitLoss,countContractsOrdinary);

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
            StaticData.yieldsCommons = checkMaxTotalLoss(result,isLimitLoss);
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
        if (isShowIntervalYields){
            showIntervalYields(true,StaticData.candleList.get(StaticData.candleList.size() - 1),interval,countInterval,countContractsOrdinary);
        }

//        System.out.println(ConditionsOpen.conditionTen(countContractsOrdinary) + " доходность по условию ConditionsOpen.conditionTen()");

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
                StaticData.positiveRes.size() * countContractsOrdinary);
        double optionalNeg = (StaticData.negativeRes.stream().mapToDouble(Deal::getYields).sum() /
                StaticData.negativeRes.size() * countContractsOrdinary);
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
        total.append(String.format("Yields = %.4f. Стоп = %.4f. minRP = %.4f. minMove = %.4f. largeMove = %.4f. maxLossTotal = %.4f. minNakedSize = %.4f. \nslipPage = %.4f. conditionExitLargeCandle = %.4f.  bodyMove = %.4f. mainShadow = %.4f. \nreverseShadow  = %.4f. coefficientBS = %.4f. Путь = %s. Макс.просадка = %.3f. Полож сделок = %d. \nОтриц сделок = %d. Средняя прибыль на сделку = %.2f. Средняя убыток на сделку = %.2f.\nМаксимальное кол. неудачных сделок = %d.Среднее количество неудачных сделок = %.3f.\n",
                StaticData.yieldsCommons,StaticData.limitStop,StaticData.minRP,StaticData.minMove,StaticData.largeMove,StaticData.maxLossTotal,StaticData.minNakedSize,StaticData.slipPage,StaticData.conditionExitLargeCandle,StaticData.bodyMove,StaticData.mainShadow,StaticData.reverseShadow,StaticData.coefficientBS,file.substring(60),f1,StaticData.positiveRes.size(),StaticData.negativeRes.size(),optionalPos,optionalNeg,StaticData.countFailSequence,averageFailSequence)).append("===========================");

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
    private static void showIntervalYields(boolean isEnd,Candle candle,String interval,int count,float countContractsOrdinary){
        if ((!StaticData.isFirstCandleYields &&
                candle.getDateClose().after(StaticData.calendarForIntervalYields.getTime()) && !isEnd) ||
                (candle.getDateClose().before(StaticData.calendarForIntervalYields.getTime()) && isEnd)){
            Date prevPeriod = null;
            switch (interval){
                case "M": {
                    StaticData.calendarForIntervalYields.add(Calendar.MONTH,-count);
                    prevPeriod = new Date(StaticData.calendarForIntervalYields.getTime().getTime() + (3600000L * 6));
                    StaticData.calendarForIntervalYields.add(Calendar.MONTH,count);
                    break;
                }
                case "D": {
                    StaticData.calendarForIntervalYields.add(Calendar.DATE,-count);
                    prevPeriod = new Date(StaticData.calendarForIntervalYields.getTime().getTime() + (3600000L * 6));
                    StaticData.calendarForIntervalYields.add(Calendar.DATE,count);
                    break;
                }
                case "W": {
                    StaticData.calendarForIntervalYields.add(Calendar.DATE,(-count * 7));
                    prevPeriod = new Date(StaticData.calendarForIntervalYields.getTime().getTime() + (3600000L * 6));
                    StaticData.calendarForIntervalYields.add(Calendar.DATE,(count * 7));
                    break;
                }
                case "Y": {
                    StaticData.calendarForIntervalYields.add(Calendar.YEAR,-count);
                    prevPeriod = new Date(StaticData.calendarForIntervalYields.getTime().getTime() + (3600000L * 6));
                    StaticData.calendarForIntervalYields.add(Calendar.YEAR,count);
                    break;
                }
                default:
                    System.out.println("НЕВЕРНО УКАЗАН ИНТЕРВАЛ ДЛЯ ОТОБРАЖЕНИЯ ДОХОДНОСТИ");
            }
            Date toPeriod = null;
            if (isEnd){
                toPeriod = new Date(candle.getDateClose().getTime());
            }else {
                toPeriod = new Date(candle.getDateClose().getTime() - (3600000L * 4));
            }
            float clearYields = 0;
            if (StaticData.yieldsCommons < 0 && StaticData.intervalYields < 0){
                clearYields = StaticData.yieldsCommons + (StaticData.intervalYields);
//                System.out.println(clearYields * countContractsOrdinary);
                System.out.printf("Yields for period from (%s) to (%s) = %.3f\n",prevPeriod.toString(),toPeriod.toString(),clearYields * countContractsOrdinary);
                StaticData.intervalYields = StaticData.yieldsCommons;
            }else {
                clearYields = StaticData.yieldsCommons - StaticData.intervalYields;
//                System.out.println(clearYields * countContractsOrdinary);
                System.out.printf("Yields for period from (%s) to (%s) = %.3f\n",prevPeriod.toString(),toPeriod.toString(),clearYields * countContractsOrdinary);
                StaticData.intervalYields = StaticData.yieldsCommons;
            }
        }
//        long tempTime = StaticData.calendarForIntervalYields.getTime().getTime() + intervalMS;
//        StaticData.calendarForIntervalYields.setTime(new Date(tempTime));
//        while (candle.getDateClose().after(StaticData.calendarForIntervalYields.getTime())){
//            StaticData.calendarForIntervalYields.setTime(new Date(StaticData.calendarForIntervalYields.getTime().
//                    getTime() + intervalMS));
//        }
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
        StaticData.commonListDeals.add(new Result(result,StaticData.isBuy));
    }

    private static void checkIntervalYields(boolean isShowIntervalYields, Candle candle, String interval,
                                            int countInterval, float countContractsOrdinary,
                                            int countIntervalLimitLoss) throws ParseException {
        if (StaticData.isFirstCandleYields && isShowIntervalYields){
            StaticData.calendarForIntervalYields = findNextInterval(candle,interval,countInterval);
            showIntervalYields(false,candle,interval,countInterval,countContractsOrdinary);
            StaticData.isFirstCandleYields = false;
        }else if (candle.getDateClose().after(StaticData.calendarForIntervalYields.getTime()) &&
                isShowIntervalYields){
            showIntervalYields(false,candle,interval,countInterval,countContractsOrdinary);
            StaticData.calendarForIntervalYields = findNextInterval(candle,interval,countInterval);
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
                case RESERVECANDLE:{
                    a = StaticData.open - candle.getClose();
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
                case RESERVECANDLE:{
                    a = candle.getClose() - StaticData.open;
                }
            }
        }
        if (typeCountingResult.equals(TypeCountingResult.CLEARDAYS)){
            return a;
        }else {
            return (a + (StaticData.slipPage));
        }
    }

    private static float checkMaxTotalLoss(float result, boolean isLimitLoss){
        ConditionsClose.defaultSetListBooleanFromConditionClose();
        StaticData.isOpenDeal = false;
        StaticData.countCandleOpenPosition = 0;
        //==тут мы складываем додходность в перменную для того, чтобы ограничить убытки, если мы установили огранич убытка к примеру в -546 пунктов
        if (isLimitLoss && StaticData.maxLossTotal > StaticData.tempYieldsMaxLoss){
            StaticData.tempYieldsMaxLoss = StaticData.tempYieldsMaxLoss + (result);
            return StaticData.yieldsCommons;
        }
        StaticData.tempYieldsMaxLoss = StaticData.tempYieldsMaxLoss + (result);
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


    private static float endDay(int countIntervalLimitLoss,String intervalLimitLoss,float result,boolean isLimit){
        ConditionsClose.defaultSetListBooleanFromConditionClose();
        StaticData.countCandleOpenPosition = 0;
        StaticData.isOpenDeal = false;
        if (StaticData.countWorkingDays > countIntervalLimitLoss && intervalLimitLoss.equals("D")){
            StaticData.tempYieldsMaxLoss = 0;
            return StaticData.yieldsCommons + result;
        }
        //==тут мы складываем додходность в перменную для того, чтобы ограничить убытки, если мы установили огранич убытка к примеру в -546 пунктов
        StaticData.yieldsCommons = checkMaxTotalLoss(result, isLimit);
        return StaticData.yieldsCommons;
    }

    private static void mainMethod(String line,boolean isShowIntervalYields,String interval,int countInterval,
                                   float countContractsOrdinary,boolean isShowCompoundInterest,int spanInterval,
                                   float warrantyProvision,
                                   boolean isLimitLoss,String intervalLimitLoss,int countIntervalLimitLoss,
                                   float ordinaryCountContracts) throws Exception {
        String[] array = line.split("\\s");
        //==преобразование в Дату и Время
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = sdf.parse(String.format("%s.%s.%s %s:%s:%s", array[2].substring(0,4), array[2].substring(4,6),
                array[2].substring(6,8),array[3].substring(0,2),array[3].substring(2,4),array[3].substring(4,6)));

        Candle candle = new Candle(array[1], date, Float.parseFloat(array[4]), Float.parseFloat(array[5]), Float.parseFloat(array[6]), Float.parseFloat(array[7]), Integer.parseInt(array[8]));

        if (!StaticData.candleList.isEmpty() && checkNextDays(array)) StaticData.countWorkingDays++;

        //==находим след интервал для ограничения убытка, если к примеру прошла неделя, то начало каждого понед обнуляем доходность чтобы она не уходила ниже maxLossTotal
        changeCalendarForMaxLossTotal(candle,isLimitLoss,intervalLimitLoss,countIntervalLimitLoss);

        //==подготовка работы инструментов из пакета Instruments
        MovingAverage.findMovingAverage(TypeCandleParameter.CLOSE,StaticData.rangeCandleForMA);
        ATR.findATR(candle);
        //======================================================

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
            if (checkNextDays(array)){
                float result = 0.0f;
                //method3
                result = countingResult(TypeCountingResult.CLEARDAYS,candle);

                //===логика максимальных неудачных последовательных сделок
                analyzeMaxFailDeals(result,candle);
                //========================================================

                StaticData.yieldsCommons = endDay(countIntervalLimitLoss,intervalLimitLoss,result,isLimitLoss);

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

                //===обнуляем countWorkingDays, если он больше заданного интервала
                if (StaticData.countWorkingDays > countIntervalLimitLoss && intervalLimitLoss.equals("D")){
                    StaticData.countWorkingDays = 1;
                }
            }
        }

        //=подготовка статических данных для отображения доходности за определенный промежуток времени и отобр доходности
        checkIntervalYields(isShowIntervalYields,candle,interval,countInterval,countContractsOrdinary,countIntervalLimitLoss);
        //===============================================================================================================

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

//            //проверка если открыта уже сделка и идет встречная сделка то переворачиваемся
//            if (reverseDeal(candle,isLimitLoss,isShowCompoundInterest,countContractsOrdinary,warrantyProvision,ordinaryCountContracts)){
//                System.out.println("Перевернутая сделка " + candle.toString());
//            }

            //условие выхода по стопу
            if (StaticData.countCandleOpenPosition <= 1 && ConditionsClose.
                    getCondition(0,candle)){

                //===посчет результат по этой сделке
                float result = countingResult(TypeCountingResult.EXITSTOP,candle);

                //===логика максимальных неудачных последовательных сделок
                analyzeMaxFailDeals(result,candle);
                //========================================================

                //подсчет доходности с ограничением потерь maxLossTotal
                StaticData.yieldsCommons = checkMaxTotalLoss(result,isLimitLoss);

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
                StaticData.yieldsCommons = checkMaxTotalLoss(result,isLimitLoss);

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

                //тут проверяется превышает ли убытки maxLossTotal, за опред интервал, если да то сделка как бы не проводится, торговля не идет, пока доходность не превышает в плюс maxLossTotal
                StaticData.yieldsCommons = checkMaxTotalLoss(result,isLimitLoss);

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
            if (ConditionsOpen.getCondition(0,candle)){
//                        System.out.println("Вход");
                StaticData.open = candle.getClose();
                StaticData.isOpenDeal = true;//сделка открыта
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
        StaticData.tempYieldsMaxLoss = 0;
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
        StaticData.countWorkingDays = 1;
    }

    private static void changeCalendarForMaxLossTotal(Candle candle,boolean isLimitLoss,String intervalLimitLoss,
                                                      int countIntervalLimitLoss) throws Exception {
        if (!intervalLimitLoss.matches("([DWM])")){
            System.out.println("Неверный формат данных");
            throw new Exception();
        }
        //вход первый и последний раз для нахождения след интервала
        if (isLimitLoss && StaticData.isFirstEnterCalendar){
            Date date = new Date();
            date.setTime(candle.getDateClose().getTime());
            StaticData.dateForMaxLossTotal = findNextInterval(candle,intervalLimitLoss,countIntervalLimitLoss);
            StaticData.isFirstEnterCalendar = false;
        }
        if (candle.getDateClose().after(StaticData.dateForMaxLossTotal.getTime())
                && StaticData.countWorkingDays > countIntervalLimitLoss){
            StaticData.dateForMaxLossTotal = findNextInterval(candle,intervalLimitLoss,countIntervalLimitLoss);
        }
    }

    private static Calendar findNextInterval(Candle candle,String intervalLimitLoss, int countIntervalLimitLoss) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(candle.getDateClose());
        calendar.set(Calendar.HOUR_OF_DAY,1);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        switch (intervalLimitLoss){
            case "M": {
                while (true){
                    int day = Integer.parseInt(calendar.getTime().toString().split("\\s")[2]);
                    if (day == 1) {
                        break;
                    }else {
                        calendar.add(Calendar.DATE,1);
                    }
                }
                calendar.add(Calendar.MONTH,countIntervalLimitLoss);
                break;
            }
            case "W": {
                if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
                    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
                        calendar.add(Calendar.DATE,1);
                    }
                }else {
                    calendar.add(Calendar.DATE,countIntervalLimitLoss * 7);
                }
                break;
            }
            case "D": {
                while (calendar.getTime().before(candle.getDateClose())){
                    calendar.add(Calendar.DATE,countIntervalLimitLoss);
                }
                break;
            }
            default:
                System.out.println("Неверный формат intervalLimitLoss");break;
        }
        return calendar;
    }

    private static boolean reverseDeal(Candle candle,boolean isLimitLoss,boolean isShowCompoundInterest,
                                       float countContractsOrdinary,float warrantyProvision,float ordinaryCountContracts){
        boolean isBuy = StaticData.isBuy;
        if (StaticData.isOpenDeal && ConditionsOpen.getCondition(0,candle)){
            if (!Objects.equals(StaticData.isBuy,isBuy)){
                //===посчет результат по этой сделке
                float result = countingResult(TypeCountingResult.RESERVECANDLE,candle);

                //===логика максимальных неудачных последовательных сделок
                analyzeMaxFailDeals(result,candle);
                //========================================================

                //тут проверяется превышает ли убытки maxLossTotal, за опред интервал, если да то сделка как бы не проводится, торговля не идет, пока доходность не превышает в плюс maxLossTotal
                StaticData.yieldsCommons = checkMaxTotalLoss(result,isLimitLoss);

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
                StaticData.open = candle.getClose();
                StaticData.isOpenDeal = true;//сделка открыта
                return true;
            }
        }
        return false;
    }

    private static boolean checkNextDays(String[] array){
        int oldDay = Integer.parseInt(StaticData.candleList.get(StaticData.candleList.size() - 1).getDateClose().
                toString().substring(8,10));
        int currentDay = Integer.parseInt(array[2].substring(6,8));
        return oldDay != currentDay;
    }
}
