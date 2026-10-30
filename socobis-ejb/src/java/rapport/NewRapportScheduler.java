package rapport;

import utilitaire.Utilitaire;
import utils.ConstanteSocobis;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.sql.Date;
import java.util.Calendar;

import static utilitaire.Utilitaire.string_date;

@Singleton
@Startup
public class NewRapportScheduler {

    @Schedule(hour = "9", minute = "0", second = "0", persistent = false)
    public void envoyerCRQuotidien() {
        try {
            if (ConstanteSocobis.EST_PROD) {
                System.out.println("Début d'envoi automatique de CR : ");

                CRJournalier crJournalier = new CRJournalier();
                Date dateJ = Utilitaire.dateDuJourSql();
                crJournalier.sendPageEmail(Utilitaire.ajoutJourDate(dateJ,-1));
                System.out.println("Envoi terminé ");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
