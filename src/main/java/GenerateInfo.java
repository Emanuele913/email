

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
@Component
public class GenerateInfo {

    public static String getCourtesyText(){
        String text = "Questo è un testo di Cortesia autogenerato da allegare al documento\n\n\n";
        return text;
    }
    @Bean
    public static StringBuilder getInfo(){
        /// In Questo esempio viene effettuata la tokenizzazione di una stringa e il contenuto
        /// viene allegato alla struttura HTML della tabella per una costruzione dinamicamente
        /// in un contesto reale si può tranquillamente includere un inserimento tramite i metodi
        /// GET della classe da cui estrarre le informazioni di
        /// In questa porzione viene posto come esempio la tokenizzazione di stringhe statiche ma il
        /// comportamento è medesimo per un GET() derivato da una classe

        String nomi = "mario,giuseppe,salvatore,giacomo";
        String time1 = "13:30,14:00,15:00,16:00";
        String time2 = "13:30,14:00,15:00,16:00";

        List<String> interviewTimingToFrom1 = Arrays.asList(nomi.split(","));
        List<String> interviewTimingToFrom2 = Arrays.asList(time1.split(","));
        List<String> listOfinterviewerName = Arrays.asList(time2.split(","));

        /// Realizzazione corpo HTML includendo le stringhe tokenizzate
        StringBuilder buf = new StringBuilder();

        buf.append(getCourtesyText());

        buf.append("<html>" +
                "<body>" +
                "<table>" +
                "<tr>" +
                "<th>Interviewe</th>" +
                "<th>Timing1</th>" +
                "<th>Timing2</th>" +
                "</tr>");
        for (int i = 0; i < listOfinterviewerName.size(); i++) {
            buf.append("<tr><td>")
                    .append(listOfinterviewerName.get(i))
                    .append("</td><td>")
                    .append(interviewTimingToFrom1.get(i))
                    .append("</td><td>")
                    .append(interviewTimingToFrom2.get(i))
                    .append("</td></tr>");
        }
        buf.append("</table>" +
                "</body>" +
                "</html>");
        return buf;
    }
}
