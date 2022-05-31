import java.util.Stack;

//�������� ����.������� �� ����
//0.�������� ����������� �������� �������� ������� �����, ��� ���� ����� ��������� ����� ������� � ������ ������� ���� ���������� �������
//1.������� ������� ��������� ������ �� ���� � ����� ��� ���� ������, ��� ��� ������ ������������
//2.������� ������� ��������� � ������ ATR
//3.������� ������� �������� conditionTwo � ���.�������������� ����� � ����� �� ������ �����,� � ���� ������� �� ������������ ����� �����, � ��������� ���� ���� ����� �������, �� ��������� �� ��� ������� ����������� ������ � ������� ������ ��������
//4.������� ��� ������� ������ �� ���� �������� ������ � ������� ����������� ������
//5.�������� ���������� ���, ������, ������. �� �������� ����� ��������� ������ �� ��������� ���������
//6.��������� ������� �����, ����� ������� ���� ������� �������� �� ���. ����� ���������� � ��������� ����������
//� ������ ��������� ����� ������, ��� ��������� ����� ����� ����� ���. ���� ����� ���� ������� ��������
//7.���� �� ��� ����, ���� � ������ ������ ��� ������ ���������� ���� ��� ������ �����
//8.����������� ������� ��� ��� ���� � ���������, ��� ����������� ��������� ���� � ����� �� ���������� ����� �������
//, � ���� ������� ����������� �� ��� ����������� �����, � ������� ���� ������ ������ � ���������� 1000�, � � ����
// ������ �� ��������� ����������� ������ � ������� 400�, � �������� 2 ��� � ���� 100� ���������� ����� ����� ��� ��
//400� � 600� ����������, �� ���� ������� ������, ��� �������� ������������, ���� ��������� �� ������� ������� ����������
// ����� 600� ������� � ������� 1000�.


public class ConditionOpen {

    //������ ����,���� ������ ����� ��������� � ��� ������� � ���� �� ������ �� ����� ��� �� minRP ����.
    private static boolean conditionZero(Candle candle){
        //==== ������� � ���� � ������ � ��������
        if ((StaticData.candleStack.peek().getOpen() - StaticData.candleStack.peek().getLow()) < StaticData.minRP &&
                (StaticData.candleStack.peek().getClose() - StaticData.candleStack.peek().getOpen()) > StaticData.minMove &&
                (candle.getClose() - candle.getOpen()) < StaticData.largeMove && !StaticData.isOpenDeal){
            StaticData.isBuy = true;
            ConditionClose.markerExit.set(0,true);
            ConditionClose.markerExit.set(1,true);
            return true;
        }else if ((StaticData.candleStack.peek().getHigh() - StaticData.candleStack.peek().getOpen()) < StaticData.minRP &&
                (StaticData.candleStack.peek().getOpen() - StaticData.candleStack.peek().getClose()) > StaticData.minMove &&
                (candle.getOpen() - candle.getClose()) < StaticData.largeMove && !StaticData.isOpenDeal){
            StaticData.isBuy = false;
            ConditionClose.markerExit.set(0,true);
            ConditionClose.markerExit.set(1,true);
            return true;
        }

        return false;
    }
    //������, ���� �� ������ ����� ���� ������� ��������, � ��� ��������� ����. ��� ������� ����� � �� ����� � ����� ���� �� ������ �� ����� minNakedSize �������
    private static boolean conditionOne(Candle candle){
        //==== ������� � ���� � ������ � ��������
        if ((candle.getClose() - candle.getOpen()) > StaticData.largeMove &&
                (candle.getHigh() - candle.getClose()) > StaticData.minNakedSize &&
                (candle.getOpen() - candle.getLow()) > StaticData.minNakedSize &&
                ((candle.getClose() - candle.getOpen()) * 1.3f) > (candle.getHigh() - candle.getClose()) + (candle.getOpen() - candle.getLow())
        && !StaticData.isOpenDeal){
            StaticData.isBuy = true;
            ConditionClose.markerExit.set(2,true);
            return true;
        }else if ((candle.getOpen() - candle.getClose()) > StaticData.largeMove &&
                (candle.getHigh() - candle.getOpen()) > StaticData.minNakedSize &&
                (candle.getClose() - candle.getLow()) > StaticData.minNakedSize &&
                ((candle.getOpen() - candle.getClose()) * 1.3f) > (candle.getHigh() - candle.getOpen()) + (candle.getClose() - candle.getLow())
        &&!StaticData.isOpenDeal){
            StaticData.isBuy = false;
            ConditionClose.markerExit.set(2,true);
            return true;
        }
        return false;
    }
    //������ ����,���� ������ ����� ��������� � ��� ������� � ���� �� ������ �� ����� ��� �� minRP ����. � �� ����� � ����� ���� �� ������ �� ����� minNakedSize �������
    private static boolean conditionTwo(Candle candle){
        //==== ������� � ���� � ������ � ��������
        if ((StaticData.candleStack.peek().getOpen() - StaticData.candleStack.peek().getLow()) < StaticData.minRP &&
                (StaticData.candleStack.peek().getClose() - StaticData.candleStack.peek().getOpen()) > StaticData.minMove &&
                (candle.getHigh() - candle.getClose()) > StaticData.minNakedSize &&
                (candle.getOpen() - candle.getLow()) > StaticData.minNakedSize &&
                (candle.getClose() - candle.getOpen()) < StaticData.largeMove && !StaticData.isOpenDeal){
            StaticData.isBuy = true;
            ConditionClose.markerExit.set(1,true);
            return true;
        }else if ((StaticData.candleStack.peek().getHigh() - StaticData.candleStack.peek().getOpen()) < StaticData.minRP &&
                (StaticData.candleStack.peek().getOpen() - StaticData.candleStack.peek().getClose()) > StaticData.minMove &&
                (candle.getHigh() - candle.getOpen()) > StaticData.minNakedSize &&
                (candle.getClose() - candle.getLow()) > StaticData.minNakedSize &&
                (candle.getOpen() - candle.getClose()) < StaticData.largeMove && !StaticData.isOpenDeal){
            StaticData.isBuy = false;
            ConditionClose.markerExit.set(1,true);
            return true;
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

    public static boolean getCondition(int index,Candle candle){
        return arrayConditions[index].conditions(candle);
    }
}
