package RandomParameterContracts;

import DateProviders.StaticData;

import java.util.Random;

public class SI{
    private static Random random = new Random();

    public static void randomSI(){
        StaticData.slipPage = -11;//эффект проскальзывания при открытии сделки или сколькальзывании стопа
        StaticData.limitStop = random.nextInt(30) * 10 + 30;

        StaticData.minRP = random.nextInt(100) * 10 + 10;//(minReversePrice)условие обратного движения по свече
        StaticData.minMove = random.nextInt(700) * 10 + 10;//сколько минимально должн пройти цена от открытия до закрытия
        StaticData.largeMove = random.nextInt(700)*10 + 10;//сколько должно пройти, чтобы сработало 2 условие
        DateProviders.StaticData.maxLossTotal = random.nextInt(1500) - 1000;
        DateProviders.StaticData.minNakedSize = random.nextInt(10)*5 + 5;//минимальный диапазон для неголого закрытия
        DateProviders.StaticData.conditionExitLargeCandle = random.nextInt(100)*20;//параметр для выхода для больш свечи

        DateProviders.StaticData.bodyMove = random.nextInt(300) * 10 + 50;//сколько тело прошло от открыти до закрытия
        DateProviders.StaticData.mainShadow = random.nextInt(200) * 10 + 50;//основная тень одной из сторон пин бара
        DateProviders.StaticData.reverseShadow = random.nextInt(200) * 10 + 20;//обратная тень одной из сторон пин бара
        DateProviders.StaticData.coefficientBS = random.nextInt(10);//коэффициент тела > относительно тени
        StaticData.rangeExtremum = random.nextInt(18);//диапазон с конца листа, за этот диапазон смотрится и находится экстремум цены макс. и минимальной

        DateProviders.StaticData.rangeCandleForTrend = random.nextInt(100);//2 диапазон с конца листа, за этот диапазон смотрится и находится точка экстремума, после которой определяется тренд
        DateProviders.StaticData.rangeCandleForATR = random.nextInt(100);//диапазон с конца листа, за этот диапазон смотрится и находится ATR-среднее истинное значение-в каком диапазоне ходит цена внутри этогоо диапазона
        DateProviders.StaticData.rangeCandleForMA = random.nextInt(100);//диапазон с конца листа, за этот диапазон смотрится и находится сред
    }
}
