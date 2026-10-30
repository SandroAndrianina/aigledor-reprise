package vente;

import bean.ClassMAPTable;
import chatbot.AiTabDesc;
import chatbot.ClassIA;

import java.sql.Date;

@AiTabDesc("La structure de ma view pour avoir les CA est: ")
public class CaJournalier extends ClassMAPTable  implements ClassIA {
    String annee,mois,moislib;
    Date daty;
    double montant;

    @Override
    public String getNomTableIA() {
        return "CA_JOURNALIER";
    }
    @Override
    public String getUrlListe() {
        return "/aigledor/pages/module.jsp?but=vente/vente-liste.jsp&currentMenu=MNDN000000007";
    }
    @Override
    public String getUrlAnalyse() {
        return "/aigledor/pages/module.jsp?but=vente/vente-analyse.jsp&currentMenu=MNDN0000000111";
    }
    @Override
    public String getUrlSaisie() {
        return "/aigledor/pages/module.jsp?but=vente/vente-saisie.jsp&currentMenu=MNDN000000006";
    }
    @Override
    public ClassIA getClassListe() {
        return this;
    }
    @Override
    public ClassIA getClassAnalyse() {
        return this;
    }
    @Override
    public ClassIA getClassSaisie() {
        return this;
    }

    public String getMoislib() {
        return moislib;
    }

    public void setMoislib(String moislib) {
        this.moislib = moislib;
    }

    public CaJournalier() {
        this.setNomTable("CA_JOURNALIER");
    }

    @Override
    public String getAttributIDName() {
        return "annee";
    }

    @Override
    public String getTuppleID() {
        return annee;
    }

    public String getAnnee() {
        return annee;
    }

    public void setAnnee(String annee) {
        this.annee = annee;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }
}
