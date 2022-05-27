import java.util.Stack;

//Добавить след.условия на вход
//1.условие которое принимает сигнал на вход в месте где есть уровни, где нет уровня игнорировать
//2.условие которое принимает в расчет ATR
//3.условие которое копирует conditionTwo в кот.игнорировались голые с одной из сторон свечи,а в этом условии не игнорировать голыу свечи, а проверять если есть голая сторона, то пересекла ли эта сторона предыддущую свечку в сторону голого закрытия
//4.условие при котором сигнал на вход подается только в сторону направления тренда
//5.проверка доходности дня, недели, месяца. на открытии рынка открываем сделку на закрытиии закрываем
//6.программа находит свечи, после которых идет хорошее движение на кот. можно заработать и программа закидывает
//в массив параметры таких свечей, для выявления формы свечи после кот. чаще всего идет хорошее движение
//7.вход по пин бару, либо с учетом тренда или средне скользящей либо без ввсего этого
//8.ограничение убытков кот уже есть в программе, это ограничение учитывает уход в минус на протяжении всего периода
//, а если сделать ограничение от уже накопленной суммы, к примеру если прошла неделя и заработано 1000р, и в этом
// месяце мы поставили ограничение убытка к примеру 400р, и проходит 2 дня и наша 100р доходности упала более чем на
//400р к 600р доходности, то даем системе понять, что торговля прекращается, пока стратегия не покажет переход доходности
// свыше 600р обратно в сторону 1000р.


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
