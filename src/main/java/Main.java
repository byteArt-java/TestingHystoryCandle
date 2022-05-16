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
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-Hour-220422-220515.txt",
            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\SI.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\SI-Hour-110101-220506.txt"
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Si-30M-190101-220430.txt"
    };

    public static void main(String[] args) throws IOException, ParseException {
//        System.out.println(-500 - (150));
//        System.out.println(-500 + (150));
//        System.out.println(500 + (-150));


        //===========================
//        long startMS = System.currentTimeMillis();
//        addWarrantyToMap();
//        for (int i = 0; i < 20000; i++) {
//            StaticData.limitStop = random.nextInt(50) * 10 + 10;
//            StaticData.minRP = random.nextInt(50)*10 + 10;//(minReversePrice)������� ��������� �������� �� �����
//            StaticData.minMove = random.nextInt(150)*10 + 10;//������� ���������� ����� ������ ���� �� �������� �� ��������
//            StaticData.largeMove = random.nextInt(400)*10 + 10;//������� ������ ������, ����� ��������� 2 �������
//            StaticData.minNakedSize = random.nextInt(20)*5 + 5;//����������� �������� ��� �������� ��������
//            testInternallyDays(1100000.00f,1,false,"M",1,false,allPathFiles);//���� ������ ���, ������ �� 4 �������
//        }
//        System.out.println("Ms = " + (System.currentTimeMillis() - startMS) + ".Minute = " + ((System.currentTimeMillis() - startMS) / 1000) / 60);
        //=============================

        //================================
        long startMS = System.currentTimeMillis();
        addWarrantyToMap();
//        StaticData.limitStop = 50;
//        StaticData.minRP = 130;//(minReversePrice)������� ��������� �������� �� �����
//        StaticData.minMove = 10;//������� ���������� ����� ������ ���� �� �������� �� ��������
//        StaticData.largeMove = 260;//������� ������ ������, ����� ��������� 2 �������
//        StaticData.minNakedSize = 15;//����������� �������� ��� �������� ��������
        testInternallyDays(1100000.00f,1,false,"M",1,false,allPathFiles);
        System.out.println("Ms = " + (System.currentTimeMillis() - startMS));
        //=================================
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
//        QueryWarrantyProvision.getWarranty(mapWarrantyProvision,"RI","SF","ED","SI");
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
            float capitalCompoundInterest = 0.0f;
            if (isShowCompoundInterest){
                capitalCompoundInterest = capital;
                warrantyProvision = mapWarrantyProvision.get(codeForWarrantyProvision);//��������� �������� ������������ �����
            }
            //==========================================================================

            BufferedReader in = new BufferedReader(new FileReader(pathFile));
            String line = "";

            Stack<Candle> candleStack = new Stack<>();//���� ��� �������� ������ ������ ���
            ConditionOpen conditionOpen = new ConditionOpen();//�������� ������ ����������� ������� ��� �������� ������

            ConditionClose conditionClose = new ConditionClose();//�������� ������ ����������� ������� ��� �������� ������
            float yields = 0.0f;//��������� ��� ����������� ���������� ��������
            float yieldsCompoundInterest = 0.0f;//��������� ��� ����������� ���������� �������� �������� �������� � � ������ spanMonth
            float countContractsCompoundInterest = capital / mapWarrantyProvision.get(codeForWarrantyProvision);//���.���������� ��� �������� �������� � � ������ spanMonth
            final float countContractsOrdinary = capital / mapWarrantyProvision.get(codeForWarrantyProvision);//���.���������� ��� yields
            float open = 0.0f;//���� ��������
            float close = 0.0f;//���� ��������

            Date dateFirstCandle = null;//���� ����� ��������� ������ ����� ��� �������� ��������
            boolean isFirstCandle = true;
            long totalIntervalCompoundInterest = 0;

            //=================��������� ���� ��� ���������� ���� �������� � ������ � ������ Static.positiveRes
            Date openDeal = null;
            //=================================================================================================

            while ((line = in.readLine()) != null){
                String[] array = line.split("\\s");

                //==�������������� � ���� � �����
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                Date date = sdf.parse(String.format("%s.%s.%s %s:%s:%s", array[2].substring(0,4), array[2].substring(4,6),
                        array[2].substring(6,8),array[3].substring(0,2),array[3].substring(2,4),array[3].substring(4,6)));

                Candle candle = new Candle(array[1], date, Float.parseFloat(array[4]), Float.parseFloat(array[5]), Float.parseFloat(array[6]), Float.parseFloat(array[7]), Integer.parseInt(array[8]));

                //=���������� ����������� ������ ��� ����������� ���������� �� ������������ ���������� ������� � ����� ����������
                if (StaticData.isFirstCandleYields && isShowIntervalYields){
                    StaticData.dateForIntervalYields.setTime(candle.getDateClose().getTime() - 3600000L);//��������� �� ���, ����� �� ������ ��� ������������ ����������
                    showIntervalYields(candle,interval,countInterval,yields,countContractsOrdinary);
                    StaticData.isFirstCandleYields = false;
                }else if (candle.getDateClose().after(StaticData.dateForIntervalYields) && isShowIntervalYields){
                    showIntervalYields(candle,interval,countInterval,yields,countContractsOrdinary);
                }
                //===============================================================================================================

                if (isFirstCandle && isShowCompoundInterest){
//                    totalMonth = 2592000000L * spanInterval;//���������� �����
//                    totalMonth = 86400000L * spanInterval;//���������� ����
                    totalIntervalCompoundInterest = 604800000L * spanInterval;//���������� ������
                    long tempDate = candle.getDateClose().getTime();
                    dateFirstCandle = new Date();
                    dateFirstCandle.setTime(tempDate + totalIntervalCompoundInterest);
                    isFirstCandle = false;
                }

                //====��� ������� ���� ���� ������� ����� ����, � ��������� ������� �� ���� ����, ���� �� ���� �������
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
                            //===������ ������������ ��������� ���������������� ������
                            analyzeMaxFailDeals(candleStack,result,candle);
                            //========================================================

                            StaticData.countCandleOpenPosition = 0;
                            yields = yields + result;
                            StaticData.isOpenDeal = false;
                            print("����� �� ������ = ",result);
                            //===��� ������� ������� ��� ������ �� �������� �������� � ������ �������������  � spanMonth
                            if (isShowCompoundInterest && candle.getDateClose().after(dateFirstCandle)){
                                capitalCompoundInterest = capitalCompoundInterest + yieldsCompoundInterest;
                                countContractsCompoundInterest = (float) Math.floor(capitalCompoundInterest / warrantyProvision);
                                yieldsCompoundInterest = 0;
                                dateFirstCandle.setTime(dateFirstCandle.getTime() + totalIntervalCompoundInterest);
                                float returnNet = countContractsCompoundInterest * result;
                                yieldsCompoundInterest += returnNet;
                            }else if (isShowCompoundInterest){
                                yieldsCompoundInterest = (result * countContractsCompoundInterest) + yieldsCompoundInterest;
                            }
                            //=============================================================================

                            //===��� �� ����������� ������ �������� ������������ ��������
                            countMaxDescendingMoney(result * countContractsOrdinary);
                            //===========================================================
                            //===������ ��������� ������ � ����
                            if (result > 0){
                                StaticData.positiveRes.add(new Deal(openDeal,candleStack.peek().getDateClose(),result,open,close,StaticData.isBuy));
                            }else if (result < 0){
                                StaticData.negativeRes.add(new Deal(openDeal,candleStack.peek().getDateClose(),result,open,close,StaticData.isBuy));
                            }
                            //========================================================================
                        }
                        candleStack.clear();
                    }
                }

                //����� ��������� �������, �� ����������� ������� ����������� �� ������ ������
                if (StaticData.isOpenDeal){
                    //==���� ���������� ���������� ���� ��������, ���� �� ������ ���� �������� �� ���� ����� ��������, � ��� ������� �������� ��������� ������� ����� � �������
                    if (StaticData.countCandleOpenPosition  == 0){
                        openDeal = date;
                    }
                    //=========================================================================================================================================================
                    StaticData.countCandleOpenPosition++;
                }

                //������� ������ �� ������.����� 3 � ConditionClose � ����� 1 � ConditionClose.
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
                        //===������ ������������ ��������� ���������������� ������
                        analyzeMaxFailDeals(candleStack,result,candle);
                        //========================================================

                        StaticData.oneMethod = 0;
                        StaticData.twoMethod = 0;
                        StaticData.threeMethod = 0;
                        StaticData.countCandleOpenPosition = 0;
                        yields = yields + result;
                        StaticData.isOpenDeal = false;
                        print("����� �� ������ = ",result);
                        //===��� ������� ������� ��� ������ �� �������� �������� � ������ �������������
                        if (isShowCompoundInterest && candle.getDateClose().after(dateFirstCandle)){
                            capitalCompoundInterest = capitalCompoundInterest + yieldsCompoundInterest;
                            countContractsCompoundInterest = (float) Math.floor(capitalCompoundInterest / warrantyProvision);
                            yieldsCompoundInterest = 0;
                            dateFirstCandle.setTime(dateFirstCandle.getTime() + totalIntervalCompoundInterest);
                            float returnNet = countContractsCompoundInterest * result;
                            yieldsCompoundInterest += returnNet;
                        }else if (isShowCompoundInterest){
                            yieldsCompoundInterest = (result * countContractsCompoundInterest) + yieldsCompoundInterest;
                        }
                        //=============================================================================
                        //===��� �� ����������� ������ �������� ������������ ��������
                        countMaxDescendingMoney(result * countContractsOrdinary);
                        //===========================================================
                        //===������ ��������� ������ � ����
                        if (result > 0){
                            StaticData.positiveRes.add(new Deal(openDeal,candleStack.peek().getDateClose(),result,open,close,StaticData.isBuy));
                        }else {
                            StaticData.negativeRes.add(new Deal(openDeal,candleStack.peek().getDateClose(),result,open,close,StaticData.isBuy));
                        }
                        //========================================================================
                    }
                    ////������� ������ �� ������.����� 2 � ConditionClose.
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
                        //===������ ������������ ��������� ���������������� ������
                        analyzeMaxFailDeals(candleStack,result,candle);
                        //========================================================

                        StaticData.oneMethod = 0;
                        StaticData.twoMethod = 0;
                        StaticData.threeMethod = 0;
                        yields = yields + result;
                        StaticData.isOpenDeal = false;
                        print("����� �� ������ = ",result);
                        //===��� ������� ������� ��� ������ �� �������� �������� � ������ �������������
                        if (isShowCompoundInterest && candle.getDateClose().after(dateFirstCandle)){
                            capitalCompoundInterest = capitalCompoundInterest + yieldsCompoundInterest;
                            countContractsCompoundInterest = (float) Math.floor(capitalCompoundInterest / warrantyProvision);
                            yieldsCompoundInterest = 0;
                            dateFirstCandle.setTime(dateFirstCandle.getTime() + totalIntervalCompoundInterest);
                            float returnNet = countContractsCompoundInterest * result;
                            yieldsCompoundInterest += returnNet;
                        }else if (isShowCompoundInterest){
                            yieldsCompoundInterest = (result * countContractsCompoundInterest) + yieldsCompoundInterest;
                        }
                        //=============================================================================
                        //===��� �� ����������� ������ �������� ������������ ��������
                        countMaxDescendingMoney(result * countContractsOrdinary);
                        //===========================================================
                        //===������ ��������� ������ � ����
                        if (result > 0){
                            StaticData.positiveRes.add(new Deal(openDeal,candleStack.peek().getDateClose(),result,open,close,StaticData.isBuy));
                        }else {
                            StaticData.negativeRes.add(new Deal(openDeal,candleStack.peek().getDateClose(),result,open,close,StaticData.isBuy));
                        }
                        //========================================================================
                    }
                }

                candleStack.push(candle);

                //������� ����� � ������.����� 1 � ConditionOpen
                if (!StaticData.isOpenDeal && conditionOpen.getCondition(0,candleStack,candle,StaticData.isOpenDeal)){
                    open = candle.getClose();
                    StaticData.isOpenDeal = true;//������ �������
                }
                //������� ����� � ������.����� 2 � ConditionOpen
                if (!StaticData.isOpenDeal && conditionOpen.getCondition(1,candleStack,candle,StaticData.isOpenDeal)){
                    open = candle.getClose();
                    StaticData.isOpenDeal = true;//������ �������
                }

            }//while end
            //==���� � ����� ������ �������� �������, �� ����� �� ������� � ��������� ��������� ������ ��������� � ������
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
                print("����� �� ������ = ",result);
                //===��� ������� ������� ��� ������ �� �������� �������� � ������ �������������  � spanMonth
                yieldsCompoundInterest = (result * countContractsCompoundInterest) + yieldsCompoundInterest;
                capitalCompoundInterest = (capitalCompoundInterest + yieldsCompoundInterest) - capital;
                //=============================================================================

                //===��� �� ����������� ������ �������� ������������ ��������
                countMaxDescendingMoney(result * countContractsOrdinary);
                //===========================================================
                //===������ ��������� ������ � ����
                if (result > 0){
                    StaticData.positiveRes.add(new Deal(openDeal,candleStack.peek().getDateClose(),result,open,close,StaticData.isBuy));
                }else if (result < 0){
                    StaticData.negativeRes.add(new Deal(openDeal,candleStack.peek().getDateClose(),result,open,close,StaticData.isBuy));
                }
                //========================================================================
            }
            candleStack.clear();
            //===========================================================================================================

            //===��� �� ������� ������������ ��������
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
            if (pathFile.contains("RI")){
                yields *= 1.45f;
            }

            //===��������� �� ���������� ����������, ������� �� ��������� ������� �������
            yields = yields * countContractsOrdinary;
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
                         yields,StaticData.limitStop,StaticData.minRP,StaticData.minMove,StaticData.largeMove,StaticData.minNakedSize,pathFile.substring(60),f1,StaticData.positiveRes.size(),StaticData.negativeRes.size(),optionalPos,optionalNeg,StaticData.countFailSequence,averageFailSequence));
            }else if (pathFile.contains("BR") || pathFile.contains("GD")){
                total.append(String.format("Yields = %.2f. ���� = %.2f. minRP = %.2f. minMove = %.2f. largeMove = %.2f. minNakedSize = %.2f. ���� = %s. ����.�������� = %.3f. ����� ������ = %d. ����� ������ = %d. ������� ������� �� ������ = %.2f. ������� ������ �� ������ = %.2f.������������ ���. ��������� ������ = %d.������� ���������� ��������� ������ = %.3f.\n",
                        yields,StaticData.limitStop,StaticData.minRP,StaticData.minMove,StaticData.largeMove,StaticData.minNakedSize,pathFile.substring(60),f1,StaticData.positiveRes.size(),StaticData.negativeRes.size(),optionalPos,optionalNeg,StaticData.countFailSequence,averageFailSequence));
            }else {
                total.append(String.format("Yields = %.0f. ���� = %.0f. minRP = %.0f. minMove = %.0f. largeMove = %.0f. minNakedSize = %.0f. ���� = %s. ����.�������� = %.3f. ����� ������ = %d. ����� ������ = %d. ������� ������� �� ������ = %.2f. ������� ������ �� ������ = %.2f.������������ ���. ��������� ������ = %d.������� ���������� ��������� ������ = %.3f.\n",
                        yields,StaticData.limitStop,StaticData.minRP,StaticData.minMove,StaticData.largeMove,StaticData.minNakedSize,pathFile.substring(60),f1,StaticData.positiveRes.size(),StaticData.negativeRes.size(),optionalPos,optionalNeg,StaticData.countFailSequence,averageFailSequence));
            }

            //==���������� ��������� ������� �� �������������, ����� ������� ������ �������, ���� ������� � ������ �������������
            if (isShowCompoundInterest){
                total.append(String.format(" Yields � ������ ���. ���������� � ������������� = %.3f.\n",(capitalCompoundInterest + yieldsCompoundInterest) - capital));
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
            StaticData.countFailSequence = 0;
            StaticData.tempFailSequence = 0;
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

    //===���������� ������������� ������� � ����������� �� ��������� ���������
    //� ������� ("M",1) - ��� 1 �����
    private static void showIntervalYields(Candle candle,String interval,int count,float yields,float countContractsOrdinary){
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
            if (yields < 0 && StaticData.tempYields < 0){
                float clearYields = yields - (StaticData.tempYields);
                System.out.printf("Yields for period from (%s) to (%s) = %.3f\n",prevPeriod.toString(),toPeriod.toString(),clearYields * countContractsOrdinary);
                StaticData.tempYields = yields;
            }else {
                float clearYields = yields - StaticData.tempYields;
                System.out.printf("Yields for period from (%s) to (%s) = %.3f\n",prevPeriod.toString(),toPeriod.toString(),clearYields * countContractsOrdinary);
                StaticData.tempYields = yields;
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

    private static void analyzeMaxFailDeals(Stack<Candle> stack,float result,Candle candle){
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

}
