import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private final static Random random = new Random();
    private final static String pathFiles = "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\SI-Hour-220310-220420.txt";
    private final static Map<String,Float> mapWarrantyProvision = new HashMap<>();

    private static String[] allPathFiles = {
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\RI-Hour-190101-220430.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\RI-30M-190101-220430.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-190101-220430.txt",
            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220318-220518.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220422-220515.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\SI.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\SI-Hour-110101-220506.txt"
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-30M-190101-220430.txt"
    };

    public static void main(String[] args) throws IOException, ParseException {
//        System.out.println(-500 - (150));
//        System.out.println(-500 + (150));
//        System.out.println(500 + (-150));


        //===========================
        long startMS = System.currentTimeMillis();
        addWarrantyToMap();
        for (int i = 0; i < 100000; i++) {
            StaticData.limitStop = random.nextInt(50) * 10 + 30;
            StaticData.minRP = random.nextInt(50)*10 + 10;//(minReversePrice)������� ��������� �������� �� �����
            StaticData.minMove = random.nextInt(150)*10 + 10;//������� ���������� ����� ������ ���� �� �������� �� ��������
            StaticData.largeMove = random.nextInt(400)*10 + 10;//������� ������ ������, ����� ��������� 2 �������
            StaticData.minNakedSize = random.nextInt(20)*5 + 5;//����������� �������� ��� �������� ��������
            testInternallyDays(1100000.00f,1,true,"M",1,false,allPathFiles);//���� ������ ���, ������ �� 4 �������
        }
        System.out.println("Ms = " + (System.currentTimeMillis() - startMS) + ".Minute = " + ((System.currentTimeMillis() - startMS) / 1000) / 60);
        //=============================

        //================================
//        for (int i = 0; i < 1; i++) {
//            long startMS = System.currentTimeMillis();
//            addWarrantyToMap();
//        StaticData.limitStop = 10;
//        StaticData.minRP = 280;//(minReversePrice)������� ��������� �������� �� �����
//        StaticData.minMove = 1160;//������� ���������� ����� ������ ���� �� �������� �� ��������
//        StaticData.largeMove = 100;//������� ������ ������, ����� ��������� 2 �������
//        StaticData.minNakedSize = 10;//����������� �������� ��� �������� ��������
//            testInternallyDays(1100000.00f,1,true,"M",1,true,allPathFiles);
//            System.out.println("Ms = " + (System.currentTimeMillis() - startMS));
//        }
        //=================================
    }

    private static void testInternallyDays(float capital,int spanInterval,boolean isShowCompoundInterest,String interval,int countInterval,boolean isShowIntervalYields,String... pathFiles) throws IOException, ParseException {
        BufferedWriter writerFile = new BufferedWriter(new FileWriter("F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\totalData.txt",true));
        for (String pathFile : pathFiles) {//������ ��������� ��� ������� ������������ ����� � ������� �������
            if (!Files.exists(Paths.get(pathFile))){
                System.out.println("����� �� ����������");
                return;
            }
            //==�������������� ������ ��� ���������� ���������� ������ � ������� �������
            float warrantyProvision = 0.0f;
            String codeForWarrantyProvision = Paths.get(pathFile).getFileName().toString().substring(0,2).toUpperCase();//��� ��� ��������� �������� ������������ ����������� �� Map
            if (isShowCompoundInterest){
                StaticData.capitalCompoundInterest = capital;
                warrantyProvision = mapWarrantyProvision.get(codeForWarrantyProvision);//��������� �������� ������������ �����
            }
            //==========================================================================

            BufferedReader in = new BufferedReader(new FileReader(pathFile));
            String line = "";

            ConditionOpen conditionOpen = new ConditionOpen();//�������� ������ ����������� ������� ��� �������� ������

            ConditionClose conditionClose = new ConditionClose();//�������� ������ ����������� ������� ��� �������� ������
            StaticData.countContractsCompoundInterest = capital / mapWarrantyProvision.get(codeForWarrantyProvision);//���.���������� ��� �������� �������� � � ������ spanMonth
            final float countContractsOrdinary = capital / mapWarrantyProvision.get(codeForWarrantyProvision);//���.���������� ��� yields

            while ((line = in.readLine()) != null){
                mainMethod(line,isShowIntervalYields,interval,countInterval,countContractsOrdinary,isShowCompoundInterest,spanInterval,warrantyProvision,conditionClose,conditionOpen);

            }//while end
            //==���� � ����� ������ �������� �������, �� ����� �� ������� � ��������� ��������� ������ ��������� � ������
            if (StaticData.isOpenDeal){
                float result = 0.0f;
                if (StaticData.isBuy){
                    StaticData.close = StaticData.candleStack.peek().getClose();
                    result = (StaticData.close - StaticData.open);
                }else {
                    StaticData.close = StaticData.candleStack.peek().getClose();
                    result = (StaticData.open - StaticData.close);
                }
                StaticData.countCandleOpenPosition = 0;
                StaticData.yields = StaticData.yields + result;
                StaticData.isOpenDeal = false;
                print("����� �� ������ = ",result);
                //===��� ������� ������� ��� ������ �� �������� �������� � ������ �������������  � spanMonth
                StaticData.yieldsCompoundInterest = (result * StaticData.countContractsCompoundInterest) + StaticData.yieldsCompoundInterest;
                StaticData.capitalCompoundInterest = (StaticData.capitalCompoundInterest + StaticData.yieldsCompoundInterest) - capital;
                //=============================================================================

                //===��� �� ����������� ������ �������� ������������ ��������
                countMaxDescendingMoney(result * countContractsOrdinary);
                //===========================================================
                //===������ ��������� ������ � ����
                posAndNegList(result);
                //========================================================================
            }
            //===========================================================================================================

            //===��� �� ������� ������������ ��������
            float f1 = 0.0f;
            for (Float f2 : StaticData.maxDescendingMoneyList) {
                if (f2 < f1){
                    f1 = f2;
                }
            }
            //===========================================================
            StringBuilder total = new StringBuilder();
            if (pathFile.contains("RI")){
                StaticData.yields *= 1.45f;
            }

            //===��������� �� ���������� ����������, ������� �� ��������� ������� �������
            StaticData.yields = StaticData.yields * countContractsOrdinary;
            //==========================================================================================================

            //===������� ������� ������� � ������ �� ������ �� positiveRes List � negativeRes List
            double optionalPos = (StaticData.positiveRes.stream().mapToDouble(Deal::getYields).sum() / StaticData.positiveRes.size());
            double optionalNeg = (StaticData.negativeRes.stream().mapToDouble(Deal::getYields).sum() / StaticData.negativeRes.size());
            //====================================================================================

            //===������� ����� ���������������� ��������� ������
            float averageFailSequence = 0;
            for (int i = 0; i < StaticData.countFailSequenceList.size(); i++) {
                if (StaticData.countFailSequenceList.get(i) > 0){
                    averageFailSequence = averageFailSequence + StaticData.countFailSequenceList.get(i);
                }
            }
            averageFailSequence = averageFailSequence / StaticData.countFailSequenceList.size();
            //==================================================
//            System.out.println(Arrays.toString(new List[]{StaticData.countFailSequenceList}));

            if (pathFile.contains("ED")){
                 total.append(String.format("Yields = %.4f. ���� = %.4f. minRP = %.4f. minMove = %.4f. largeMove = %.4f. minNakedSize = %.4f. ���� = %s. ����.�������� = %.3f. ����� ������ = %d. ����� ������ = %d. ������� ������� �� ������ = %.2f. ������� ������ �� ������ = %.2f.������������ ���. ��������� ������ = %d.������� ���������� ��������� ������ = %.3f.\n",
                         StaticData.yields,StaticData.limitStop,StaticData.minRP,StaticData.minMove,StaticData.largeMove,StaticData.minNakedSize,pathFile.substring(60),f1,StaticData.positiveRes.size(),StaticData.negativeRes.size(),optionalPos,optionalNeg,StaticData.countFailSequence,averageFailSequence));
            }else if (pathFile.contains("BR") || pathFile.contains("GD")){
                total.append(String.format("Yields = %.2f. ���� = %.2f. minRP = %.2f. minMove = %.2f. largeMove = %.2f. minNakedSize = %.2f. ���� = %s. ����.�������� = %.3f. ����� ������ = %d. ����� ������ = %d. ������� ������� �� ������ = %.2f. ������� ������ �� ������ = %.2f.������������ ���. ��������� ������ = %d.������� ���������� ��������� ������ = %.3f.\n",
                        StaticData.yields,StaticData.limitStop,StaticData.minRP,StaticData.minMove,StaticData.largeMove,StaticData.minNakedSize,pathFile.substring(60),f1,StaticData.positiveRes.size(),StaticData.negativeRes.size(),optionalPos,optionalNeg,StaticData.countFailSequence,averageFailSequence));
            }else {
                total.append(String.format("Yields = %.0f. ���� = %.0f. minRP = %.0f. minMove = %.0f. largeMove = %.0f. minNakedSize = %.0f. ���� = %s. ����.�������� = %.3f. ����� ������ = %d. ����� ������ = %d. ������� ������� �� ������ = %.2f. ������� ������ �� ������ = %.2f.������������ ���. ��������� ������ = %d.������� ���������� ��������� ������ = %.3f.\n",
                        StaticData.yields,StaticData.limitStop,StaticData.minRP,StaticData.minMove,StaticData.largeMove,StaticData.minNakedSize,pathFile.substring(60),f1,StaticData.positiveRes.size(),StaticData.negativeRes.size(),optionalPos,optionalNeg,StaticData.countFailSequence,averageFailSequence));
            }

            //==���������� ��������� ������� �� �������������, ����� ������� ������ �������, ���� ������� � ������ �������������
            if (isShowCompoundInterest){
                total.append(String.format(" Yields � ������ ���. ���������� � ������������� = %.3f.\n",StaticData.capitalCompoundInterest - capital));
            }
            //==================================================================================================================

//            int total2 = (int) (StaticData.yieldsLimitFailSequence * countContractsOrdinary);
            if (StaticData.yields > StaticData.rangeYields){
                System.out.print(total.toString());
                writerFile.write(total.toString());
                writerFile.flush();
//                System.out.println(total2 + " StaticData.yieldsLimitFailSequence");
            }
            StaticData.candleStack.clear();
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
            StaticData.tempYields = 0;
            StaticData.countFailSequence = 0;
            StaticData.tempFailSequence = 0;
            StaticData.isFirstFailSequence = false;
            StaticData.countFailSequenceList.clear();
            StaticData.isFirstCandle = true;
            StaticData.capitalCompoundInterest = 0;
            StaticData.countContractsCompoundInterest = 0;
            StaticData.yieldsCompoundInterest = 0;
            StaticData.totalIntervalCompoundInterest = 0;

//            System.out.println("������ ���������� ���������������: " + Arrays.toString(new List[]{StaticData.commonListDeals}));
        }//����� iter �����
    }//test
    private static void print(String msg,float result){
//        if (true && result > 4000.0f || true && result < -4000.0f){
//            System.out.println(msg + " = " + result);
//        }
        if (false){
            System.out.println(msg + " = " + result);
        }
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
        mapWarrantyProvision.put("SI",10483.00f);//�������� ����� ������� �����������
        mapWarrantyProvision.put("RI",68950.00f);//�������� ����� ������� �����������
//        QueryWarrantyProvision.getWarranty(mapWarrantyProvision,"RI","SF","ED","SI");
    }

    //===���������� ������������� ������� � ����������� �� ��������� ���������
    //� ������� ("M",1) - ��� 1 �����
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
                System.out.println("������� ������ �������� ��� ����������� ����������");
        }
        if (!StaticData.isFirstCandleYields && candle.getDateClose().after(StaticData.dateForIntervalYields)){
            Date prevPeriod = new Date(StaticData.dateForIntervalYields.getTime() - intervalMS);
            Date toPeriod = new Date(candle.getDateClose().getTime());
            if (StaticData.yields < 0 && StaticData.tempYields < 0){
                float clearYields = StaticData.yields - (StaticData.tempYields);
                System.out.printf("Yields for period from (%s) to (%s) = %.3f\n",prevPeriod.toString(),toPeriod.toString(),clearYields * countContractsOrdinary);
                StaticData.tempYields = StaticData.yields;
            }else {
                float clearYields = StaticData.yields - StaticData.tempYields;
                System.out.printf("Yields for period from (%s) to (%s) = %.3f\n",prevPeriod.toString(),toPeriod.toString(),clearYields * countContractsOrdinary);
                StaticData.tempYields = StaticData.yields;
            }
        }
        StaticData.dateForIntervalYields.setTime(StaticData.dateForIntervalYields.getTime() + intervalMS);
        while (candle.getDateClose().after(StaticData.dateForIntervalYields)){
            StaticData.dateForIntervalYields.setTime(StaticData.dateForIntervalYields.getTime() + intervalMS);
        }
    }

    //������ ����������� ������
    private static void analyzeCompletedDeals(List<Candle> listDeals){

    }

    private static void analyzeMaxFailDeals(float result,Candle candle){
        if (result < 0){
            StaticData.tempFailSequence++;
        }
        if (StaticData.tempFailSequence == 1 && !StaticData.isFirstFailSequence){
            StaticData.isFirstFailSequence = true;
//            System.out.println("from " + candle.getDateClose() + " = " + StaticData.tempFailSequence);
        }
        if (result > 0 && StaticData.tempFailSequence != 0 && StaticData.tempFailSequence >= StaticData.countFailSequence){
            StaticData.countFailSequenceList.add(StaticData.tempFailSequence);
            StaticData.countFailSequence = StaticData.tempFailSequence;
            StaticData.isFirstFailSequence = false;
//            System.out.println("to " + candle.getDateClose() + " = " + StaticData.tempFailSequence);
            StaticData.tempFailSequence = 0;
        }else if (result > 0 && StaticData.tempFailSequence < StaticData.countFailSequence){
            StaticData.countFailSequenceList.add(StaticData.tempFailSequence);
            StaticData.isFirstFailSequence = false;
//            System.out.println("to " + candle.getDateClose() + " = " + StaticData.tempFailSequence);
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
            StaticData.positiveRes.add(new Deal(StaticData.openDeal,StaticData.candleStack.peek().getDateClose(),result,StaticData.open,StaticData.close,StaticData.isBuy));
        }else if (result < 0){
            if (StaticData.currentFailSequence >= 2){

            }else {
                StaticData.yieldsLimitFailSequence = StaticData.yieldsLimitFailSequence + result;
            }
            StaticData.currentFailSequence++;
            StaticData.negativeRes.add(new Deal(StaticData.openDeal,StaticData.candleStack.peek().getDateClose(),result,StaticData.open,StaticData.close,StaticData.isBuy));
        }
        StaticData.commonListDeals.add(result);
    }

    private static void method1(boolean isShowIntervalYields,Candle candle,String interval,int countInterval,float countContractsOrdinary){
        if (StaticData.isFirstCandleYields && isShowIntervalYields){
            StaticData.dateForIntervalYields.setTime(candle.getDateClose().getTime() - 3600000L);//��������� �� ���, ����� �� ������ ��� ������������ ����������
            showIntervalYields(candle,interval,countInterval,countContractsOrdinary);
            StaticData.isFirstCandleYields = false;
        }else if (candle.getDateClose().after(StaticData.dateForIntervalYields) && isShowIntervalYields){
            showIntervalYields(candle,interval,countInterval,countContractsOrdinary);
        }
    }

    private static float method3(){
        float a = 0;
        if (StaticData.isBuy){
            a = (StaticData.candleStack.peek().getClose() - StaticData.open);
        }else {
            a = (StaticData.open - StaticData.candleStack.peek().getClose());
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

    private static float method5(){
        float a = 0;
        if (StaticData.isBuy){
            a = (StaticData.candleStack.peek().getLow() - StaticData.open);
        }else {
            a = (StaticData.open - StaticData.candleStack.peek().getHigh());
            StaticData.countCandleOpenPosition = 0;
        }
        return a;
    }

    private static float method6(float result){
        StaticData.oneMethod = 0;
        StaticData.twoMethod = 0;
        StaticData.threeMethod = 0;
        StaticData.isOpenDeal = false;
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

    private static float method8(float result){
        StaticData.oneMethod = 0;
        StaticData.twoMethod = 0;
        StaticData.threeMethod = 0;
        StaticData.countCandleOpenPosition = 0;
        StaticData.isOpenDeal = false;
        return StaticData.yields + result;
    }

    private static void mainMethod(String line,boolean isShowIntervalYields,String interval,int countInterval,
                                   float countContractsOrdinary,boolean isShowCompoundInterest,int spanInterval,
                                   float warrantyProvision,
                                   ConditionClose conditionClose,ConditionOpen conditionOpen) throws ParseException {
        String[] array = line.split("\\s");
        //==�������������� � ���� � �����
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = sdf.parse(String.format("%s.%s.%s %s:%s:%s", array[2].substring(0,4), array[2].substring(4,6),
                array[2].substring(6,8),array[3].substring(0,2),array[3].substring(2,4),array[3].substring(4,6)));

        Candle candle = new Candle(array[1], date, Float.parseFloat(array[4]), Float.parseFloat(array[5]), Float.parseFloat(array[6]), Float.parseFloat(array[7]), Integer.parseInt(array[8]));

        //=���������� ����������� ������ ��� ����������� ���������� �� ������������ ���������� ������� � ����� ����������
        //method1
        method1(isShowIntervalYields,candle,interval,countInterval,countContractsOrdinary);
        //===============================================================================================================
        //����������� ���� ��� �������� ��������
        if (StaticData.isFirstCandle && isShowCompoundInterest){
//                    totalMonth = 2592000000L * spanInterval;//���������� �����
//                    totalMonth = 86400000L * spanInterval;//���������� ����
            StaticData.totalIntervalCompoundInterest = 604800000L * spanInterval;//���������� ������
            long tempDate = candle.getDateClose().getTime();
            StaticData.isFirstCandle = false;
            StaticData.dateFirstCandle.setTime(StaticData.totalIntervalCompoundInterest + tempDate);
        }

        //====��� ������� ���� ���� ������� ����� ����, � ��������� ������� �� ���� ����, ���� �� ���� �������
        if (StaticData.candleStack.size() > 0){
            if (Integer.parseInt(StaticData.candleStack.peek().getDateClose().toString().substring(8,10)) != Integer.parseInt(array[2].substring(6,8))){
                if (StaticData.isOpenDeal){
                    float result = 0.0f;
                    //method3
                    result = method3();

                    //===������ ������������ ��������� ���������������� ������
                    analyzeMaxFailDeals(result,candle);
                    //========================================================

                    StaticData.countCandleOpenPosition = 0;
                    StaticData.yields = StaticData.yields + result;
                    StaticData.isOpenDeal = false;
                    print("����� �� ������ = ",result);
                    //===��� ������� ������� ��� ������ �� �������� �������� � ������ �������������  � spanMonth
                    //method7
                    method7(isShowCompoundInterest,candle,warrantyProvision,result);
                    //=============================================================================

                    //===��� �� ����������� ������ �������� ������������ ��������
                    countMaxDescendingMoney(result * countContractsOrdinary);
                    //===========================================================
                    //===������ ��������� ������ � ����
                    posAndNegList(result);
                    //========================================================================
                }
                StaticData.candleStack.clear();
            }
        }

        //����� ��������� �������, �� ����������� ������� ����������� �� ������ ������
        if (StaticData.isOpenDeal){
            //==���� ���������� ���������� ���� ��������, ���� �� ������ ���� �������� �� ���� ����� ��������, � ��� ������� �������� ��������� ������� ����� � �������
            if (StaticData.countCandleOpenPosition  == 0){
                StaticData.openDeal = date;
            }
            //=========================================================================================================================================================
            StaticData.countCandleOpenPosition++;
        }

        //������� ������ �� ������.����� 3 � ConditionClose � ����� 1 � ConditionClose.
        if (!StaticData.candleStack.isEmpty()){
            if (StaticData.countCandleOpenPosition <= 1 && StaticData.isOpenDeal &&
                    conditionClose.getCondition(2,StaticData.candleStack,candle) && StaticData.threeMethod == 1
                    || StaticData.countCandleOpenPosition <= 1 && conditionClose.
                    getCondition(0,StaticData.candleStack,candle) && StaticData.oneMethod == 1){

                float result = method4(candle);
                //===������ ������������ ��������� ���������������� ������
                analyzeMaxFailDeals(result,candle);
                //========================================================

                //method8
                StaticData.yields = method8(result);

                print("����� �� ������ = ",result);
                //===��� ������� ������� ��� ������ �� �������� �������� � ������ �������������
                //method7
                method7(isShowCompoundInterest,candle,warrantyProvision,result);
                //=============================================================================

                //===��� �� ����������� ������ �������� ������������ ��������
                countMaxDescendingMoney(result * countContractsOrdinary);
                //===========================================================

                //===������ ��������� ������ � ����
                posAndNegList(result);
                //========================================================================
            }
            ////������� ������ �� ������.����� 2 � ConditionClose.
            if (StaticData.countCandleOpenPosition > 1 && StaticData.isOpenDeal &&
                    conditionClose.getCondition(1,StaticData.candleStack,candle)){

                float result = method5();
                //===������ ������������ ��������� ���������������� ������
                analyzeMaxFailDeals(result,candle);
                //========================================================

                //method6
                StaticData.yields = method6(result);

                print("����� �� ������ = ",result);
                //===��� ������� ������� ��� ������ �� �������� �������� � ������ �������������
                //method7
                method7(isShowCompoundInterest,candle,warrantyProvision,result);
                //=============================================================================

                //===��� �� ����������� ������ �������� ������������ ��������
                countMaxDescendingMoney(result * countContractsOrdinary);
                //===========================================================

                //===������ ��������� ������ � ����
                posAndNegList(result);
                //========================================================================
            }
        }

        StaticData.candleStack.push(candle);

        //������� ����� � ������.����� 1 � ConditionOpen
        if (!StaticData.isOpenDeal && conditionOpen.getCondition(0,StaticData.candleStack,candle,StaticData.isOpenDeal)){
            StaticData.open = candle.getClose();
            StaticData.isOpenDeal = true;//������ �������
        }
        //������� ����� � ������.����� 2 � ConditionOpen
        if (!StaticData.isOpenDeal && conditionOpen.getCondition(1,StaticData.candleStack,candle,StaticData.isOpenDeal)){
            StaticData.open = candle.getClose();
            StaticData.isOpenDeal = true;//������ �������
        }
    }
}
