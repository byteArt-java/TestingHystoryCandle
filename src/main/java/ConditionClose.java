import java.util.Stack;

public class ConditionClose {

    private boolean conditionOne(Stack<Candle> stack,Candle candle){//условие выхода по стопу
        //==== условие в рост и дальше в снижение
        if (candle.getLow() < (stack.peek().getClose() - StaticData.limitStop) && StaticData.isBuy && StaticData.isOpenDeal){
            return true;
        } else if (candle.getHigh() > (stack.peek().getClose() + StaticData.limitStop) && !StaticData.isBuy && StaticData.isOpenDeal){
            return true;
        }
        return false;
    }
    private boolean conditionTwo(Stack<Candle> stack,Candle candle){//условие выхода, когда цена выходит за край свечи
        if (candle.getLow() < stack.peek().getLow() && StaticData.isBuy && StaticData.isOpenDeal){
            return true;
        }else if (candle.getHigh() > stack.peek().getHigh() && !StaticData.isBuy && StaticData.isOpenDeal){
            return true;
        }
        return false;
    }
    private boolean conditionThree(Stack<Candle> stack,Candle candle){//условие выхода за середину, большой свечи
        if (candle.getLow() < ((stack.peek().getClose() + stack.peek().getOpen()) / 2) && StaticData.isBuy && StaticData.isOpenDeal){
            return true;
        }else if (candle.getHigh() > ((stack.peek().getOpen() + stack.peek().getClose()) / 2) && !StaticData.isBuy && StaticData.isOpenDeal){
            return true;
        }
        return false;
    }

    interface ArrayConditions{
        boolean conditions(Stack<Candle> stack,Candle candle);
    }

    private final ArrayConditions[] arrayConditions = new ArrayConditions[]{
            new ArrayConditions() {
                @Override public boolean conditions(Stack<Candle> stack, Candle candle) {
                    return conditionOne(stack, candle);
                }
            },
            new ArrayConditions() {
                @Override public boolean conditions(Stack<Candle> stack, Candle candle) {
                    return conditionTwo(stack, candle);
                }
            },
            new ArrayConditions() {
                @Override public boolean conditions(Stack<Candle> stack, Candle candle) {
                    return conditionThree(stack, candle);
                }
            }
    };
    public boolean getCondition(int index,Stack<Candle> stack,Candle candle){
        return arrayConditions[index].conditions(stack, candle);
    }

}
