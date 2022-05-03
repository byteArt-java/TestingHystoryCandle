import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private static Random random = new Random();
    private static String pathFiles = "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\test2.txt";

    private static String[] allPathFiles = {
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Hour-RTS-190101-220430.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\30M-RTS-190101-220430.txt",
            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\Hour-Si-190101-220430.txt",
//            "F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\30M-Si-190101-220430.txt"
    };

    public static void main(String[] args) throws IOException, ParseException {
//        long startMS = System.currentTimeMillis();
//        for (int i = 0; i < 1000; i++) {
//            StaticData.limitStop = random.nextInt(60) * 10;
//            StaticData.minRP = random.nextInt(35)*10;//(minReversePrice)������� ��������� �������� �� �����
//            StaticData.minMove = random.nextInt(90)*10;//������� ���������� ����� ������ ���� �� �������� �� ��������
//            StaticData.largeMove = random.nextInt(200)*10;//������� ������ ������, ����� ��������� 2 �������
//            StaticData.minNakedSize = random.nextInt(20)*5;//����������� �������� ��� �������� ��������
//            testInternallyDays(allPathFiles);//���� ������ ���, ������ �� 4 �������
//        }
//        System.out.println("Ms = " + (System.currentTimeMillis() - startMS));
        testInternallyDays(pathFiles);//���� ������ ���, ������ �� 4 �������
    }

    private static void testInternallyDays(String... pathFiles) throws IOException, ParseException {
        BufferedWriter writerFile = new BufferedWriter(new FileWriter("F:\\����������������\\TestingHystoryCandle\\src\\main\\resources\\totalData.txt",true));
        for (String pathFile : pathFiles) {//������ ��������� ��� ������� ������������ ����� � ������� �������
            if (!Files.exists(Paths.get(pathFile))){
                System.out.println("����� �� ����������");
                return;
            }
            BufferedReader in = new BufferedReader(new FileReader(pathFile));
            String line = "";

            Stack<Candle> candleStack = new Stack<>();//���� ��� �������� ������ ������ ���
            ConditionOpen conditionOpen = new ConditionOpen();//�������� ������ ����������� ������� ��� �������� ������

            ConditionClose conditionClose = new ConditionClose();//�������� ������ ����������� ������� ��� �������� ������
            float yields = 0.0f;//��������� ��� ����������� ���������� ��������
            float open = 0.0f;//���� ��������
            float close = 0.0f;//���� ��������

            while ((line = in.readLine()) != null){
                String[] array = line.split("\\s");
                //==�������������� � ���� � �����
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                Date date = sdf.parse(String.format("%s.%s.%s %s:%s:%s", array[2].substring(0,4), array[2].substring(4,6),
                        array[2].substring(6,8),array[3].substring(0,2),array[3].substring(2,4),array[3].substring(4,6)));

                Candle candle = new Candle(array[1], date, Float.parseFloat(array[4]), Float.parseFloat(array[5]), Float.parseFloat(array[6]), Float.parseFloat(array[7]), Integer.parseInt(array[8]));

                //====��� ������� ���� ���� ������� ����� ����, � ��������� ������� �� ���� ����, ���� �� ���� �������
                if (candleStack.size() > 0){
                    if (Integer.parseInt(candleStack.peek().getDateClose().toString().substring(8,10)) != Integer.parseInt(array[2].substring(6,8))){
                        if (StaticData.isOpenDeal){
                            float result = 0.0f;
                            if (StaticData.isBuy){
                                close = candleStack.peek().getClose();
                                result = (close - open);
                                StaticData.candleList.add(candle);
                            }else {
                                close = candleStack.peek().getClose();
                                result = (open - close);
                                StaticData.candleList.add(candle);
                            }
                            StaticData.countCandleOpenPosition = 0;
                            yields = yields + result;
                            StaticData.isOpenDeal = false;
                            print("����� �� ������ = " + result);
                        }
                        candleStack.clear();
                    }
                }

                //����� ��������� �������, �� ����������� ������� ����������� �� ������ ������
                if (StaticData.isOpenDeal){
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
                            StaticData.candleList.add(candle);
                        }else {
                            close = candle.getOpen() + StaticData.limitStop;
                            result = (open - close);
                            StaticData.candleList.add(candle);
                        }
                        StaticData.oneMethod = 0;
                        StaticData.twoMethod = 0;
                        StaticData.threeMethod = 0;
                        StaticData.countCandleOpenPosition = 0;
                        yields = yields + result;
                        StaticData.isOpenDeal = false;
                        print("����� �� ������ = " + result);
                    }
                    ////������� ������ �� ������.����� 2 � ConditionClose.
                    if (StaticData.countCandleOpenPosition > 1 && StaticData.isOpenDeal &&
                            conditionClose.getCondition(1,candleStack,candle)){
                        float result = 0.0f;
                        if (StaticData.isBuy){
                            close = candleStack.peek().getLow();
                            result = (close - open);
                            StaticData.candleList.add(candle);
                        }else {
                            close = candleStack.peek().getHigh();
                            result = (open - close);
                            StaticData.countCandleOpenPosition = 0;
                            StaticData.candleList.add(candle);
                        }
                        StaticData.oneMethod = 0;
                        StaticData.twoMethod = 0;
                        StaticData.threeMethod = 0;
                        yields = yields + result;
                        StaticData.isOpenDeal = false;
                        print("����� �� ������ = " + result);
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
            if (pathFile.contains("RTS")){
                yields *= 1.45f;
                yields *= 4.5f;
            }
            String total = String.format("Yields = %.2f.���� = %.2f.minRP = %.2f.minMove = %.2f.largeMove = %.2f.minNakedSize = %.2f.���� = %s.\n",
                    yields,StaticData.limitStop,StaticData.minRP,StaticData.minMove,StaticData.largeMove,StaticData.minNakedSize,pathFile.substring(60));
            if (yields > 30000){
                System.out.print(total);
                writerFile.write(total);
                writerFile.flush();
            }
        }//����� iter �����
    }//test
    private static void print(String msg){
        if (false){
            System.out.println(msg);
        }
    }

    //������ ����������� ������
    private static void analyzeCompletedDeals(List<Candle> listDeals){

    }
}
