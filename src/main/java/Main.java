import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private final static Random random = new Random();
    private final static String pathFiles = "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\SI-Hour-220310-220420.txt";
    private final static Map<String,Float> mapWarrantyProvision = new HashMap<>();

    private static String[] allPathFiles = {
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\RI-Hour-190101-220430.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\RI-30M-190101-220430.txt",
            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-190101-220430.txt",
//            "F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\Si-30M-190101-220430.txt"
    };

    public static void main(String[] args) throws IOException, ParseException {
        long startMS = System.currentTimeMillis();
        addWarrantyToMap();
        for (int i = 0; i < 100; i++) {
            StaticData.limitStop = random.nextInt(8) * 10 + 260;
            StaticData.minRP = random.nextInt(35)*10;//(minReversePrice)условие обратного движения по свече
            StaticData.minMove = random.nextInt(90)*10;//сколько минимально должн пройти цена от открытия до закрытия
            StaticData.largeMove = random.nextInt(200)*10;//сколько должно пройти, чтобы сработало 2 условие
            StaticData.minNakedSize = random.nextInt(20)*5;//минимальный диапазон для неголого закрытия
            testInternallyDays(1100000.00f,true,pathFiles);//тест внутри дня, вплоть до 4 часовых
        }
        System.out.println("Ms = " + (System.currentTimeMillis() - startMS));

//        addWarrantyToMap();
//        testInternallyDays(pathFiles);//тест внутри дня, вплоть до 4 часовых

    }

    private static void countMaxDescendingMoney(float result){
        if (result < 0){
            StaticData.maxDescendingMoney = -(Math.abs(StaticData.maxDescendingMoney) + Math.abs(result));
        }else if (result >= Math.abs(StaticData.maxDescendingMoney) && result > 0 && StaticData.maxDescendingMoney != 0) {
            StaticData.maxDescendingMoneyList.add(StaticData.maxDescendingMoney);
            StaticData.maxDescendingMoney = 0;
        }else if (result > 0 && StaticData.maxDescendingMoney != 0){
            StaticData.maxDescendingMoney = -(Math.abs(StaticData.maxDescendingMoney) - result);
        }
    }

    private static void addWarrantyToMap(){
        QueryWarrantyProvision.getWarranty(mapWarrantyProvision,"RI","SF","ED","SI");
    }

    private static void testInternallyDays(float capital,boolean compoundInterest,String... pathFiles) throws IOException, ParseException {
        BufferedWriter writerFile = new BufferedWriter(new FileWriter("F:\\Программирование\\TestingHystoryCandle\\src\\main\\resources\\totalData.txt",true));
        for (String pathFile : pathFiles) {//запуск программы для каждого загруженного файла с историч данными
            if (!Files.exists(Paths.get(pathFile))){
                System.out.println("Файла не существует");
                return;
            }
            //==подготавливаем данные для склыдвания результата сделок в сложный процент
            float warrantyProvision = 0.0f;
            String codeForWarrantyProvision = Paths.get(pathFile).getFileName().toString().substring(0,2);//код для получения значения гарантийного обеспечения из Map
            float capitalCompoundInterest = 0.0f;
            if (compoundInterest){
                capitalCompoundInterest = capital;
                warrantyProvision = mapWarrantyProvision.get(codeForWarrantyProvision);//получение значения Вариационной маржи
            }
            //==========================================================================

            BufferedReader in = new BufferedReader(new FileReader(pathFile));
            String line = "";

            Stack<Candle> candleStack = new Stack<>();//стэк для хранения свечей внутри дня
            ConditionOpen conditionOpen = new ConditionOpen();//создание класса проверочных условий для открытия сделки

            ConditionClose conditionClose = new ConditionClose();//создание класса проверочных условий для закрытия сделки
            float yields = 0.0f;//перемення для складывания результата торговли

            float open = 0.0f;//цена открытия
            float close = 0.0f;//цена закрытия

            while ((line = in.readLine()) != null){
                String[] array = line.split("\\s");
                //=================временная дата для сохранения даты открытия и добавл в массив Static.positiveRes
                Date openDeal = null;
                //=================================================================================================

                //==преобразование в Дату и Время
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                Date date = sdf.parse(String.format("%s.%s.%s %s:%s:%s", array[2].substring(0,4), array[2].substring(4,6),
                        array[2].substring(6,8),array[3].substring(0,2),array[3].substring(2,4),array[3].substring(4,6)));

                Candle candle = new Candle(array[1], date, Float.parseFloat(array[4]), Float.parseFloat(array[5]), Float.parseFloat(array[6]), Float.parseFloat(array[7]), Integer.parseInt(array[8]));

                //====тут очищаем стэк если начался новый день, и закрываем позицию за пред день, если не были закрыты
                if (candleStack.size() > 0){
                    if (Integer.parseInt(candleStack.peek().getDateClose().toString().substring(8,10)) != Integer.parseInt(array[2].substring(6,8))){
                        if (StaticData.isOpenDeal){
                            float result = 0.0f;
                            if (StaticData.isBuy){
                                close = candleStack.peek().getClose();
                                result = (close - open);
                            }else {
                                close = candleStack.peek().getClose();
                                result = (open - close);
                            }
                            StaticData.countCandleOpenPosition = 0;
                            yields = yields + result;
                            StaticData.isOpenDeal = false;
                            print("Доход со сделки = ",result);
                            //===тут считаем прибыль или убыток по сложному проценту с учетом капитализации
                            if (compoundInterest){
                                float countContracts = (float) Math.floor(capitalCompoundInterest / warrantyProvision);
                                float returnNet = countContracts * result;
                                capitalCompoundInterest = returnNet > 0 ? capitalCompoundInterest + returnNet : capitalCompoundInterest - Math.abs(returnNet);
                            }
                            //=============================================================================

                            //===тут мы выстраиваем логику подсчета максимальной просадки
                            countMaxDescendingMoney(result);
                            //===========================================================
                            //===кладем результат сделки в Лист
                            if (result > 0){
                                StaticData.positiveRes.add(new Deal(openDeal,candleStack.peek().getDateClose(),result,open,close,StaticData.isBuy));
                            }else {
                                StaticData.negativeRes.add(new Deal(openDeal,candleStack.peek().getDateClose(),result,open,close,StaticData.isBuy));
                            }
                            //========================================================================
                        }
                        candleStack.clear();
                    }
                }

                //когда открываем позицию, то увеличиваем счетчик накопленных по сделке свечей
                if (StaticData.isOpenDeal){
                    //==сюда завидываем правильную дату открытия, ведь мы фиксир цену открытия по пред свече закрытия, и это создает проблему реального времени входа в позицию
                    if (StaticData.countCandleOpenPosition  == 0){
                        openDeal = date;
                    }
                    //=========================================================================================================================================================
                    StaticData.countCandleOpenPosition++;
                }

                //Условие выхода из сделки.Метод 3 в ConditionClose и Метод 1 в ConditionClose.
                if (!candleStack.isEmpty()){
                    if (StaticData.countCandleOpenPosition <= 1 && StaticData.isOpenDeal &&
                            conditionClose.getCondition(2,candleStack,candle) && StaticData.threeMethod == 1
                            || StaticData.countCandleOpenPosition <= 1 && conditionClose.
                            getCondition(0,candleStack,candle) && StaticData.oneMethod == 1){
                        float result = 0.0f;
                        if (StaticData.isBuy){
                            close = candle.getOpen() - StaticData.limitStop;
                            result = (close - open);
                        }else {
                            close = candle.getOpen() + StaticData.limitStop;
                            result = (open - close);
                        }
                        StaticData.oneMethod = 0;
                        StaticData.twoMethod = 0;
                        StaticData.threeMethod = 0;
                        StaticData.countCandleOpenPosition = 0;
                        yields = yields + result;
                        StaticData.isOpenDeal = false;
                        print("Доход со сделки = ",result);
                        //===тут считаем прибыль или убыток по сложному проценту с учетом капитализации
                        if (compoundInterest){
                            float countContracts = (float) Math.floor(capitalCompoundInterest / warrantyProvision);
                            float returnNet = countContracts * result;
                            capitalCompoundInterest = returnNet > 0 ? capitalCompoundInterest + returnNet : capitalCompoundInterest - Math.abs(returnNet);
                        }
                        //=============================================================================
                        //===тут мы выстраиваем логику подсчета максимальной просадки
                        countMaxDescendingMoney(result);
                        //===========================================================
                        //===кладем результат сделки в Лист
                        if (result > 0){
                            StaticData.positiveRes.add(new Deal(openDeal,candleStack.peek().getDateClose(),result,open,close,StaticData.isBuy));
                        }else {
                            StaticData.negativeRes.add(new Deal(openDeal,candleStack.peek().getDateClose(),result,open,close,StaticData.isBuy));
                        }
                        //========================================================================
                    }
                    ////Условие выхода из сделки.Метод 2 в ConditionClose.
                    if (StaticData.countCandleOpenPosition > 1 && StaticData.isOpenDeal &&
                            conditionClose.getCondition(1,candleStack,candle)){
                        float result = 0.0f;
                        if (StaticData.isBuy){
                            close = candleStack.peek().getLow();
                            result = (close - open);
                        }else {
                            close = candleStack.peek().getHigh();
                            result = (open - close);
                            StaticData.countCandleOpenPosition = 0;
                        }
                        StaticData.oneMethod = 0;
                        StaticData.twoMethod = 0;
                        StaticData.threeMethod = 0;
                        yields = yields + result;
                        StaticData.isOpenDeal = false;
                        print("Доход со сделки = ",result);
                        //===тут считаем прибыль или убыток по сложному проценту с учетом капитализации
                        if (compoundInterest){
                            float countContracts = (float) Math.floor(capitalCompoundInterest / warrantyProvision);
                            float returnNet = countContracts * result;
                            capitalCompoundInterest = returnNet > 0 ? capitalCompoundInterest + returnNet : capitalCompoundInterest - Math.abs(returnNet);
                        }
                        //=============================================================================
                        //===тут мы выстраиваем логику подсчета максимальной просадки
                        countMaxDescendingMoney(result);
                        //===========================================================
                        //===кладем результат сделки в Лист
                        if (result > 0){
                            StaticData.positiveRes.add(new Deal(openDeal,candleStack.peek().getDateClose(),result,open,close,StaticData.isBuy));
                        }else {
                            StaticData.negativeRes.add(new Deal(openDeal,candleStack.peek().getDateClose(),result,open,close,StaticData.isBuy));
                        }
                        //========================================================================
                    }
                }

                candleStack.push(candle);

                //Условия входа в сделку.Метод 1 в ConditionOpen
                if (!StaticData.isOpenDeal && conditionOpen.getCondition(0,candleStack,candle,StaticData.isOpenDeal)){
                    open = candle.getClose();
                    StaticData.isOpenDeal = true;//сделка открыта
                }
                //Условия входа в сделку.Метод 2 в ConditionOpen
                if (!StaticData.isOpenDeal && conditionOpen.getCondition(1,candleStack,candle,StaticData.isOpenDeal)){
                    open = candle.getClose();
                    StaticData.isOpenDeal = true;//сделка открыта
                }

            }//while end
            //===тут мы считаем максимальную просадку
            float f1 = 0.0f;
            for (Float f2 : StaticData.maxDescendingMoneyList) {
                if (f2 < f1){
                    f1 = f2;
                }
            }
            StaticData.maxDescendingMoney = 0;
            StaticData.maxDescendingMoneyList.clear();
            //===========================================================
            StringBuilder total = new StringBuilder();
            if (pathFile.contains("RTS")){
                yields *= 1.45f;
            }

            //===умножение на количество контрактов, который позволяет капитал в зависимости от гарантийного обеспечения
            float countContracts = capital / mapWarrantyProvision.get(codeForWarrantyProvision);
            yields = yields * countContracts;
            //==========================================================================================================

            //===подсчет средней прибыли и убытка на сделку из positiveRes List и negativeRes List
            double optionalPos = (StaticData.positiveRes.stream().mapToDouble(Deal::getYields).sum() / StaticData.positiveRes.size());
            double optionalNeg = (StaticData.negativeRes.stream().mapToDouble(Deal::getYields).sum() / StaticData.negativeRes.size());
            //====================================================================================

            if (pathFile.contains("ED")){
                 total.append(String.format("Yields = %.4f. Стоп = %.4f. minRP = %.4f. minMove = %.4f. largeMove = %.4f. minNakedSize = %.4f. Путь = %s. Макс.просадка = %.3f. Полож сделок = %d. Отриц сделок = %d. Средняя прибыль на сделку = %.2f. Средняя убыток на сделку = %.2f.\n",
                         yields,StaticData.limitStop,StaticData.minRP,StaticData.minMove,StaticData.largeMove,StaticData.minNakedSize,pathFile.substring(60),f1,StaticData.positiveRes.size(),StaticData.negativeRes.size(),optionalPos,optionalNeg));
            }else if (pathFile.contains("BR") || pathFile.contains("GD")){
                total.append(String.format("Yields = %.2f. Стоп = %.2f. minRP = %.2f. minMove = %.2f. largeMove = %.2f. minNakedSize = %.2f. Путь = %s. Макс.просадка = %.3f. Полож сделок = %d. Отриц сделок = %d. Средняя прибыль на сделку = %.2f. Средняя убыток на сделку = %.2f.\n",
                        yields,StaticData.limitStop,StaticData.minRP,StaticData.minMove,StaticData.largeMove,StaticData.minNakedSize,pathFile.substring(60),f1,StaticData.positiveRes.size(),StaticData.negativeRes.size(),optionalPos,optionalNeg));
            }else {
                total.append(String.format("Yields = %.0f. Стоп = %.0f. minRP = %.0f. minMove = %.0f. largeMove = %.0f. minNakedSize = %.0f. Путь = %s. Макс.просадка = %.3f. Полож сделок = %d. Отриц сделок = %d. Средняя прибыль на сделку = %.2f. Средняя убыток на сделку = %.2f.\n",
                        yields,StaticData.limitStop,StaticData.minRP,StaticData.minMove,StaticData.largeMove,StaticData.minNakedSize,pathFile.substring(60),f1,StaticData.positiveRes.size(),StaticData.negativeRes.size(),optionalPos,optionalNeg));
            }

            //==вычитываем начальный капитал из заработанного, чтобы получит чистую прибыль, если прибыль с учетом капитализации
            if (compoundInterest){
                capitalCompoundInterest = capitalCompoundInterest - capital;
                total.append(String.format(" Yields с учетом кол. контрактов и капитализации = %.3f.\n",capitalCompoundInterest));
            }
            //==================================================================================================================

            if (yields > StaticData.rangeYields){
                System.out.print(total.toString());
                writerFile.write(total.toString());
                writerFile.flush();
            }
            yields = 0;
            StaticData.positiveRes.clear();
            StaticData.negativeRes.clear();
        }//конец iter файла
        StaticData.isOpenDeal = false;
    }//test
    private static void print(String msg,float result){
        if (true && result > 4000.0f || true && result < -4000.0f){
            System.out.println(msg + " = " + result);
        }
//        if (false){
//            System.out.println(msg + " = " + result);
//        }
    }

    //анализ проведенных сделок
    private static void analyzeCompletedDeals(List<Candle> listDeals){

    }

}
