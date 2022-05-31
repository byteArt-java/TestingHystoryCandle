import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ConditionClose {
    private static int length = 3;//������ ���������� �������, ������� � �����
    public static List<Boolean> markerExit = new ArrayList<>(length);//������ ������� ����������� ����� ���� ���� �� ������, ����� ������ �� ����� �������� ��������

    static {
        for (int i = 0; i < length; i++) {
            markerExit.add(false);
        }
    }

    private static boolean conditionZero(Candle candle){//������� ������ �� �����
        //==== ������� � ���� � ������ � ��������
        if (markerExit.get(0)){
            if (candle.getLow() <= (StaticData.candleStack.peek().getClose() - StaticData.limitStop) && StaticData.isBuy && StaticData.isOpenDeal){
                return true;
            } else if (candle.getHigh() >= (StaticData.candleStack.peek().getClose() + StaticData.limitStop) && !StaticData.isBuy && StaticData.isOpenDeal){
                return true;
            }
        }
        return false;
    }
    private static boolean conditionOne(Candle candle){//������� ������, ����� ���� ������� �� ���� �����
        if (markerExit.get(1)){
            if (candle.getLow() <= StaticData.candleStack.peek().getLow() && StaticData.isBuy && StaticData.isOpenDeal){
                return true;
            }else if (candle.getHigh() >= StaticData.candleStack.peek().getHigh() && !StaticData.isBuy && StaticData.isOpenDeal){
                return true;
            }
        }
        return false;
    }
    //������� ������ �� ��������, ������� �����, �������� StaticData.conditionExitLargeCandle, ����� ���� ���������
    private static boolean conditionTwo(Candle candle){
        if (markerExit.get(2)){
            //        stack.peek().getClose() - StaticData.conditionExitLargeCandle
            if (candle.getLow() <= ((StaticData.candleStack.peek().getClose() + StaticData.candleStack.peek().getOpen()) / 2) && StaticData.isBuy && StaticData.isOpenDeal){
                return true;
//            stack.peek().getOpen() - StaticData.conditionExitLargeCandle
            }else if (candle.getHigh() >= ((StaticData.candleStack.peek().getClose() + StaticData.candleStack.peek().getOpen()) / 2) && !StaticData.isBuy && StaticData.isOpenDeal){
                return true;
            }
        }
        return false;
    }

    interface ArrayConditions{
        boolean conditions(Candle candle);
    }

    private static final ArrayConditions[] arrayConditions = new ArrayConditions[]{
            new ArrayConditions() {
                @Override public boolean conditions(Candle candle) {
                    return conditionZero(candle);
                }
            },
            new ArrayConditions() {
                @Override public boolean conditions(Candle candle) {
                    return conditionOne(candle);
                }
            },
            new ArrayConditions() {
                @Override public boolean conditions(Candle candle) {
                    return conditionTwo(candle);
                }
            }
    };

    public static ArrayConditions[] getArrayConditions() {
        return arrayConditions;
    }

    public static boolean getCondition(int index,Candle candle){
        return arrayConditions[index].conditions(candle);
    }

    public static void defaultSetListBooleanFromConditionClose(){
        markerExit = new ArrayList<>(length);
        initializeListBoolean();
    }
    private static void initializeListBoolean(){
        for (int i = 0; i < length; i++) {
            markerExit.add(false);
        }
    }

}
