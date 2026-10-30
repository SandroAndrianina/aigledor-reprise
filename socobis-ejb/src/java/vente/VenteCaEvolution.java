package vente;

import bean.CGenUtil;
import bean.ClassMAPTable;
import bean.ResultatEtSomme;
import utilitaire.Utilitaire;

import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class VenteCaEvolution extends ClassMAPTable {
    private Date daty;
    private double ca_jour;
    private double ca_veille;
    private double evolution_pct;
    private double difference_montant;
    private double ventes_jour;
    private double ventes_veille;
    private double evolution_ventes_pct;
    private double difference_ventes;
    private String moisanneelib;


    @Override
    public String getTuppleID() {
        return this.daty.toString();
    }

    @Override
    public String getAttributIDName() {
        return "daty";
    }

    public VenteCaEvolution() {
        setNomTable("VENTE_CA_EVOLUTION");
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public double getCa_jour() {
        return ca_jour;
    }

    public void setCa_jour(double ca_jour) {
        this.ca_jour = ca_jour;
    }

    public double getCa_veille() {
        return ca_veille;
    }

    public void setCa_veille(double ca_veille) {
        this.ca_veille = ca_veille;
    }

    public double getEvolution_pct() {
        return evolution_pct;
    }

    public void setEvolution_pct(double evolution_pct) {
        this.evolution_pct = evolution_pct;
    }

    public double getDifference_montant() {
        return difference_montant;
    }

    public void setDifference_montant(double difference_montant) {
        this.difference_montant = difference_montant;
    }

    public double getVentes_jour() {
        return ventes_jour;
    }

    public void setVentes_jour(double ventes_jour) {
        this.ventes_jour = ventes_jour;
    }

    public double getVentes_veille() {
        return ventes_veille;
    }

    public void setVentes_veille(double ventes_veille) {
        this.ventes_veille = ventes_veille;
    }

    public double getEvolution_ventes_pct() {
        return evolution_ventes_pct;
    }

    public void setEvolution_ventes_pct(double evolution_ventes_pct) {
        this.evolution_ventes_pct = evolution_ventes_pct;
    }

    public double getDifference_ventes() {
        return difference_ventes;
    }

    public void setDifference_ventes(double difference_ventes) {
        this.difference_ventes = difference_ventes;
    }

    public String getMoisanneelib() {
        return moisanneelib;
    }

    public void setMoisanneelib(String moisannee) {
        this.moisanneelib = moisannee;
    }

    public ResultatEtSomme rechercherPage(String[] colInt, String[]valInt, int numPage, String apresWhere, String[]nomColSomme, Connection c, int npp) throws Exception {
        String dateDuJour = "03-03-2026";
        String dateHier = "02-03-2026";
        String req =
                "WITH vente_today AS ( " +
                        "    SELECT " +
                        "        TRUNC(DATY)           AS daty, " +
                        "        SUM(MONTANTTTC)       AS ca_today, " +
                        "        SUM(MONTANTTOTAL)     AS ca_today_total, " +
                        "        SUM(nb)               AS ventes_jour, " +
                        "        MAX(mois)             AS mois, " +
                        "        MAX(annee)            AS annee, " +
                        "        MAX(moisannee)        AS moisannee " +
                        "    FROM VENTE_CA " +
                        "    WHERE TRUNC(DATY) = TO_DATE('" + dateDuJour + "', 'DD/MM/YYYY') " +
                        "    GROUP BY TRUNC(DATY) " +
                        "), " +
                        "vente_hier AS ( " +
                        "    SELECT " +
                        "        TRUNC(DATY)           AS daty, " +
                        "        SUM(MONTANTTTC)       AS ca_hier, " +
                        "        SUM(MONTANTTOTAL)     AS ca_hier_total, " +
                        "        SUM(nb)               AS ventes_veille " +
                        "    FROM VENTE_CA " +
                        "    WHERE TRUNC(DATY) = TO_DATE('" + dateHier + "', 'DD/MM/YYYY') " +
                        "    GROUP BY TRUNC(DATY) " +
                        ") " +
                        "SELECT " +
                        "    t.daty, " +
                        "    NVL(t.ca_today_total, 0)                                           AS ca_jour, " +
                        "    NVL(h.ca_hier_total,  0)                                           AS ca_veille, " +
                        "    CASE " +
                        "        WHEN NVL(h.ca_hier_total, 0) = 0 THEN 100 " +
                        "        ELSE ROUND( " +
                        "            ((NVL(t.ca_today_total, 0) - NVL(h.ca_hier_total, 0)) " +
                        "                / NVL(h.ca_hier_total, 1)) * 100, 2) " +
                        "    END                                                                AS evolution_pct, " +
                        "    NVL(t.ca_today_total, 0) - NVL(h.ca_hier_total, 0)               AS difference_montant, " +
                        "    t.mois, " +
                        "    t.annee, " +
                        "    t.moisannee, " +
                        "    NVL(t.ventes_jour,    0)                                           AS ventes_jour, " +
                        "    NVL(h.ventes_veille,  0)                                           AS ventes_veille, " +
                        "    CASE " +
                        "        WHEN NVL(h.ventes_veille, 0) = 0 THEN 100 " +
                        "        ELSE ROUND( " +
                        "            ((NVL(t.ventes_jour, 0) - NVL(h.ventes_veille, 0)) " +
                        "                / NVL(h.ventes_veille, 1)) * 100, 2) " +
                        "    END                                                                AS evolution_ventes_pct, " +
                        "    NVL(t.ventes_jour, 0) - NVL(h.ventes_veille, 0)                  AS difference_ventes " +
                        "FROM vente_today t " +
                        "FULL OUTER JOIN vente_hier h ON 1=1";
        System.err.println(req);
        ResultatEtSomme rs= CGenUtil.rechercherPage(this,req,numPage,nomColSomme,apresWhere,c,npp);
        return rs;
    }
}
