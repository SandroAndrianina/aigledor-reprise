package vente;

import bean.ClassMAPTable;

import java.sql.Date;

public class VenteCaEvolutionMagasin extends ClassMAPTable {
    private String idmagasin;
    private String idmagasinlib;
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

    public VenteCaEvolutionMagasin() {
        setNomTable("VENTE_CA_EVOLUTION_MAGASIN");
    }

    public String getIdmagasin() {
        return idmagasin;
    }

    public void setIdmagasin(String idmagasin) {
        this.idmagasin = idmagasin;
    }

    public String getIdmagasinlib() {
        return idmagasinlib;
    }

    public void setIdmagasinlib(String idmagasinlib) {
        this.idmagasinlib = idmagasinlib;
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
}
