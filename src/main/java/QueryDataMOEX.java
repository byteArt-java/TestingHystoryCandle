import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QueryDataMOEX {
    static Map<String,String> link = new HashMap<>();



//Pattern pattern = Pattern.compile("(Стоимость шага цены)(\\D+)(\\d+)(\\,)(\\d+)");
// ---этот паттерн срабатывает и приходит к началу коэф
    static void getWarranty(Map<String,Float> mapWarrantyProvision,Map<String,Float> mapPriceStep,String... codeContractShort){
        addLink();
        for (String s : codeContractShort) {

            Document doc = null;
            try {
                doc = Jsoup.connect(link.get(s.toUpperCase())).timeout(50 * 1000).userAgent("Mozilla").get();
            } catch (IOException ioException) {
                ioException.printStackTrace();
                System.out.println("getCoefficient МЕТОД, Проблемы с соединением");
            }
            Element div = doc.getElementById("contractTables");
            String htmlTables = div.html();
            //==находим гарантийное обеспечение
            StringBuilder sbForWarrantyProvision = new StringBuilder();
            String numberRate = findWarrantyProvision(htmlTables);
            for (int j = 0; j < numberRate.length(); j++) {
                if (Character.isDigit(numberRate.charAt(j))){
                    sbForWarrantyProvision.append(numberRate.charAt(j));
                }else if (numberRate.charAt(j) == ',' && sbForWarrantyProvision.length() >= 1){
                    sbForWarrantyProvision.append(".");
                }
            }
            mapWarrantyProvision.put(s,Float.parseFloat(sbForWarrantyProvision.toString()));
            //=================================

            //==находим стоимость шага цены
            StringBuilder sbForPriceStep = new StringBuilder();
            String numberRate2 = findPriceStep(htmlTables);
            for (int j = 0; j < numberRate2.length(); j++) {
                if (Character.isDigit(numberRate2.charAt(j))){
                    sbForPriceStep.append(numberRate2.charAt(j));
                }else if (numberRate2.charAt(j) == ',' && sbForPriceStep.length() >= 1){
                    sbForPriceStep.append(".");
                }
            }
            float f = Float.parseFloat(sbForPriceStep.toString());
            if (s.contains("RI")){
                f = f /10;
            }
            mapPriceStep.put(s,f);
        }//iter
    }

    private static String findPriceStep(String htmlTables){
        //=====находим строку Стоимость шага цены, меняем ее на sK$,чтобы было проще разбирать.И находим ставку по инстр
        String newLineForRateStart = htmlTables.replace("Стоимость шага цены","hL$");
        String newLineForRateEnd = newLineForRateStart.replace("Нижний лимит","nL$");
        int[] indices = findIndices(newLineForRateEnd,'h','L','$','n');
        int start = indices[0];
        int end = indices[1];
        return newLineForRateEnd.substring(start,end);//строка между sK$ и eK$;
    }

    private static String findWarrantyProvision(String htmlTables){
        //=====находим строку Стоимость шага цены, меняем ее на sK$,чтобы было проще разбирать.И находим ставку по инстр
        String newLineForRateStart = htmlTables.replace("Гарантийное обеспечение на первом<br> уровне лимита " +
                "концентрации","sK$");
        String newLineForRateEnd = newLineForRateStart.replace("Данные по ГО на","eK$");
        int[] indices = findIndices(newLineForRateEnd,'s','K','$','e');
        int start = indices[0];
        int end = indices[1];
        return newLineForRateEnd.substring(start,end);//строка между sK$ и eK$;
    }

    private static int[] findIndices(String newLineForRateEnd,char a,char b,char c,char other){
        int[] indices = new int[2];
        for (int i = 0; i < newLineForRateEnd.length() - 3; i++) {
            if (newLineForRateEnd.charAt(i) == a && newLineForRateEnd.charAt(i + 1) == b &&
                    newLineForRateEnd.charAt(i + 2) == c){
                indices[0] = i;
            }else if (newLineForRateEnd.charAt(i) == other && newLineForRateEnd.charAt(i + 1) == b &&
                    newLineForRateEnd.charAt(i + 2) == c){
                indices[1] = i;
            }
        }
        return indices;
    }

    private static void addLink(){
        Date currentDate = new Date();
        String date = currentDate.toString();
        String shortYear = date.substring(26,28);

        link.put("RI","https://www.moex.com/ru/contract.aspx?code=RTS-6." + shortYear);

        link.put("SF","https://www.moex.com/ru/contract.aspx?code=SPYF-6." + shortYear);

        link.put("ED","https://www.moex.com/ru/contract.aspx?code=ED-6." + shortYear);

        link.put("SI","https://www.moex.com/ru/contract.aspx?code=SI-6." + shortYear);

        link.put("BR","https://www.moex.com/ru/contract.aspx?code=BR-6." + shortYear);

        link.put("GD","https://www.moex.com/ru/contract.aspx?code=GOLD-6." + shortYear);
    }

}
