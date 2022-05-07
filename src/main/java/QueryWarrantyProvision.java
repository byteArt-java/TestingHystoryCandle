import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QueryWarrantyProvision {
    static Map<String,String> link = new HashMap<>();



//Pattern pattern = Pattern.compile("(Стоимость шага цены)(\\D+)(\\d+)(\\,)(\\d+)");
// ---этот паттерн срабатывает и приходит к началу коэф
    static Map<String,Float> getWarranty(Map<String,Float> map,String... codeContractShort){
        for (String s : codeContractShort) {
            addLink();

            Document doc = null;
            try {
                doc = Jsoup.connect(link.get(s.toUpperCase())).userAgent("Mozilla").get();
            } catch (IOException ioException) {
                ioException.printStackTrace();
                System.out.println("getCoefficient МЕТОД, Проблемы с соединением");
            }
            Element div = doc.getElementById("contractTables");
            String htmlTables = div.html();

//        System.out.println(htmlTables);
            //=====находим строку Стоимость шага цены, меняем ее на sK$,чтобы было проще разбирать.И находим ставку по инстр
            StringBuilder sb = new StringBuilder();
            String newLineForRateStart = htmlTables.replace("Гарантийное обеспечение на первом<br> уровне лимита концентрации","sK$");
            String newLineForRateEnd = newLineForRateStart.replace("Данные по ГО на","eK$");

            int start = 0;
            int end = 0;
            String numberRate = "";
            for (int i = 0; i < newLineForRateEnd.length() - 3; i++) {
                if (newLineForRateEnd.charAt(i) == 's' && newLineForRateEnd.charAt(i + 1) == 'K' &&
                        newLineForRateEnd.charAt(i + 2) == '$'){
                    start = i;
                }else if (newLineForRateEnd.charAt(i) == 'e' && newLineForRateEnd.charAt(i + 1) == 'K' &&
                        newLineForRateEnd.charAt(i + 2) == '$'){
                    end = i;
                }
            }
            numberRate = newLineForRateEnd.substring(start,end);//строка между sK$ и eK$
//        System.out.println(numberRate + " numberRate");

            for (int j = 0; j < numberRate.length(); j++) {
                if (Character.isDigit(numberRate.charAt(j))){
                    sb.append(numberRate.charAt(j));
                }else if (numberRate.charAt(j) == ',' && sb.length() >= 1){
                    sb.append(".");
                }
            }
            map.put(s,Float.parseFloat(sb.toString()));
        }//iter
//        ==============================================================================================================
        return map;
    }

    private static void addLink(){
        Date currentDate = new Date();
        String date = currentDate.toString();
        String shortYear = date.substring(26,28);

        link.put("RI","https://www.moex.com/ru/contract.aspx?code=RTS-6." + shortYear);

        link.put("SF","https://www.moex.com/ru/contract.aspx?code=SPYF-6." + shortYear);

        link.put("ED","https://www.moex.com/ru/contract.aspx?code=ED-6." + shortYear);

        link.put("SI","https://www.moex.com/ru/contract.aspx?code=SI-6." + shortYear);
    }

}
