package RandomParameterContracts;

import DateProviders.StaticData;

import java.util.Random;

public class SF {
    private static Random random = new Random();

    public static void randomSF(){//420.10 к примеру
        StaticData.limitStop = (random.nextInt(50) / 100f) + 0.0001f;
        StaticData.minRP = (random.nextInt(80) / 100f) + 0.0001f;//условие обратного движения по свече
        StaticData.minMove = (random.nextInt(100) / 100f) + 0.0001f;//сколько минимально должн пройти цена от открытия до закрытия
        StaticData.largeMove = (random.nextInt(500) / 100f) + 0.0001f;//сколько должно пройти, чтобы сработало 2 условие
        StaticData.maxLossTotal = -(random.nextInt(70) / 100f) + 0.0001f;
        StaticData.minNakedSize = random.nextInt(20)*5 + 5;//минимальный диапазон для неголого закрытия
        StaticData.conditionExitLargeCandle = random.nextInt(100)*20;//параметр для выхода для больш свечи
//        StaticData.bodyMove = 0;//сколько тело прошло от открыти до закрытия
//        StaticData.mainShadow = 0;//основная тень одной из сторон пин бара
//        StaticData.reverseShadow = 0;//обратная тень одной из сторон пин бара
//        StaticData.coefficientBS = 0;//коэффициент тела > относительно тени
//        StaticData.slipPage = -11;//эффект проскальзывания при открытии сделки или сколькальзывании стопа
//
//        StaticData.rangeExtremum = 0;//диапазон с конца листа, за этот диапазон смотрится и находится экстремум цены макс. и минимальной
//        StaticData.rangeCandleForTrend = 2;//диапазон с конца листа, за этот диапазон смотрится и находится точка экстремума, после которой определяется тренд
//        StaticData.rangeCandleForATR = 5;//диапазон с конца листа, за этот диапазон смотрится и находится ATR-среднее истинное значение-в каком диапазоне ходит цена внутри этогоо диапазона
//        StaticData.rangeCandleForMA = 18;//диапазон с конца листа, за этот диапазон смотрится и находится сред
    }
}
