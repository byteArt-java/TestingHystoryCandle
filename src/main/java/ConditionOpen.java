import java.util.Stack;

public class ConditionOpen {
    private final ConditionClose conditionClose = new ConditionClose();

    //входим либо,если предыд свеча закрылась и она сходила в одну из сторон не более чем на minRP пунк.
    private boolean conditionOne(Stack<Candle> stack,Candle candle,boolean isOpenDeal){
        //==== условие в рост и дальше в снижение
        if ((stack.peek().getOpen() - stack.peek().getLow()) < StaticData.minRP &&
                (stack.peek().getClose() - stack.peek().getOpen()) > StaticData.minMove &&
                (candle.getClose() - candle.getOpen()) < StaticData.largeMove && !isOpenDeal){
            StaticData.isBuy = true;
            StaticData.oneMethod = 1;
            return true;
        }else if ((stack.peek().getHigh() - stack.peek().getOpen()) < StaticData.minRP &&
                (stack.peek().getOpen() - stack.peek().getClose()) > StaticData.minMove &&
                (candle.getOpen() - candle.getClose()) < StaticData.largeMove && !isOpenDeal){
            StaticData.isBuy = false;
            StaticData.oneMethod = 1;
            return true;
        }

        return false;
    }
    //входим, если на предыд свече было сильное движение, и она закрылась норм. Без сильных теней и не голой с какой либо из сторон не менее minNakedSize пунктов
    private boolean conditionTwo(Stack<Candle> stack,Candle candle, boolean isOpenDeal){
        //==== условие в рост и дальше в снижение
        if ((candle.getClose() - candle.getOpen()) > StaticData.largeMove &&
                (candle.getHigh() - candle.getClose()) > StaticData.minNakedSize &&
                (candle.getOpen() - candle.getLow()) > StaticData.minNakedSize &&
                ((candle.getClose() - candle.getOpen()) * 1.3f) > (candle.getHigh() - candle.getClose()) + (candle.getOpen() - candle.getLow())){
            StaticData.isBuy = true;
            StaticData.threeMethod = 1;
            return true;
        }else if ((candle.getOpen() - candle.getClose()) > StaticData.largeMove &&
                (candle.getHigh() - candle.getOpen()) > StaticData.minNakedSize &&
                (candle.getClose() - candle.getLow()) > StaticData.minNakedSize &&
                ((candle.getOpen() - candle.getClose()) * 1.3f) > (candle.getHigh() - candle.getOpen()) + (candle.getClose() - candle.getLow())){
            StaticData.isBuy = false;
            StaticData.threeMethod = 1;
            return true;
        }
        return false;
    }
    //входим либо,если предыд свеча закрылась и она сходила в одну из сторон не более чем на minRP пунк. и не голой с какой либо из сторон не менее minNakedSize пунктов
    private boolean conditionThree(Stack<Candle> stack,Candle candle,boolean isOpenDeal){
        //==== условие в рост и дальше в снижение
        if ((stack.peek().getOpen() - stack.peek().getLow()) < StaticData.minRP &&
                (stack.peek().getClose() - stack.peek().getOpen()) > StaticData.minMove &&
                (candle.getHigh() - candle.getClose()) > StaticData.minNakedSize &&
                (candle.getOpen() - candle.getLow()) > StaticData.minNakedSize &&
                (candle.getClose() - candle.getOpen()) < StaticData.largeMove && !isOpenDeal){
            StaticData.isBuy = true;
            StaticData.oneMethod = 1;
            return true;
        }else if ((stack.peek().getHigh() - stack.peek().getOpen()) < StaticData.minRP &&
                (stack.peek().getOpen() - stack.peek().getClose()) > StaticData.minMove &&
                (candle.getHigh() - candle.getOpen()) > StaticData.minNakedSize &&
                (candle.getClose() - candle.getLow()) > StaticData.minNakedSize &&
                (candle.getOpen() - candle.getClose()) < StaticData.largeMove && !isOpenDeal){
            StaticData.isBuy = false;
            StaticData.oneMethod = 1;
            return true;
        }
        return false;
    }

    interface ArrayConditions{
        boolean conditions(Stack<Candle> stack,Candle candle,boolean isOpenDeal);
    }

    private final ArrayConditions[] arrayConditions = new ArrayConditions[]{
            new ArrayConditions() {
                @Override public boolean conditions(Stack<Candle> stack, Candle candle,boolean isOpenDeal) {
                    return conditionOne(stack, candle,isOpenDeal);
                }
            },
            new ArrayConditions() {
                @Override public boolean conditions(Stack<Candle> stack, Candle candle,boolean isOpenDeal) {
                    return conditionTwo(stack, candle,isOpenDeal);
                }
            },
            new ArrayConditions() {
                @Override public boolean conditions(Stack<Candle> stack, Candle candle,boolean isOpenDeal) {
                    return conditionThree(stack, candle,isOpenDeal);
                }
            }
    };
    public boolean getCondition(int index,Stack<Candle> stack,Candle candle,boolean isOpenDeal){
        return arrayConditions[index].conditions(stack, candle,isOpenDeal);
    }
}
