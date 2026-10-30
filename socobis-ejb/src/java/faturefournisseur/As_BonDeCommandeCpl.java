/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faturefournisseur;


public class As_BonDeCommandeCpl extends As_BonDeCommande{
    double montantTVA;
    double montantHT;
    double montantTTC;
    double montantTTCAriary;
    String idDeviselib;
    String etatLib , idCategorieLib , idMagasinLib;
    String conformiteLib;
    private String idServiceLib, idTraite , Traite;
    private int nb;

    public String getConformiteLib() {
        return conformiteLib;
    }

    public void setConformiteLib(String conformiteLib) {
        this.conformiteLib = conformiteLib;
    }

    public int getNb() {
        return nb;
    }

    public void setNb(int nb) {
        this.nb = nb;
    }

    public String getIdTraite() {
        return idTraite;
    }

    public void setIdTraite(String idTraite) {
        this.idTraite = idTraite;
    }

    public String getTraite() {
        return Traite;
    }

    public void setTraite(String traite) {
        Traite = traite;
    }

    public String getIdServiceLib() {
        return idServiceLib;
    }

    public void setIdServiceLib(String idServiceLib) {
        this.idServiceLib = idServiceLib;
    }

    public As_BonDeCommandeCpl(){
        this.setNomTable("As_BonDeCommande_MERECPL");
    }

    public String getIdMagasinLib() {
        return idMagasinLib;
    }

    public void setIdMagasinLib(String idMagasinLib) {
        this.idMagasinLib = idMagasinLib;
    }

    public String getIdCategorieLib() {
        return idCategorieLib;
    }

    public void setIdCategorieLib(String idCategorieLib) {
        this.idCategorieLib = idCategorieLib;
    }

    public double getMontantTVA() {
        return montantTVA;
    }

    public void setMontantTVA(double montantTVA) {
        this.montantTVA = montantTVA;
    }

    public double getMontantHT() {
        return montantHT;
    }

    public void setMontantHT(double montantHT) {
        this.montantHT = montantHT;
    }

    public double getMontantTTC() {
        return montantTTC;
    }

    public void setMontantTTC(double montantTTC) {
        this.montantTTC = montantTTC;
    }

    public double getMontantTTCAriary() {
        return montantTTCAriary;
    }

    public void setMontantTTCAriary(double montantTTCAriary) {
        this.montantTTCAriary = montantTTCAriary;
    }

    public String getIdDeviselib() {
        return idDeviselib;
    }

    public void setIdDeviselib(String idDeviselib) {
        this.idDeviselib = idDeviselib;
    }

    public String getEtatLib() {
        return etatLib;
    }

    public void setEtatLib(String etatLib) {
        this.etatLib = etatLib;
    }
}
