package vente;

public class VenteCa extends VenteLib{
    private String moisanneelib;
    private String moislib;
    private String anneelib;
    private int nb;
    private String heurevente;

    public VenteCa(){
        setNomTable("VENTE_CA");
    }

    public String getMoisannelib() {
        return moisanneelib;
    }

    public void setMoisanneelib(String moisannee) {
        this.moisanneelib = moisannee;
    }

    public String getMoislib() {
        return moislib;
    }

    public void setMoislib(String mois) {
        this.moislib = mois;
    }

    public String getAnneelib() {
        return anneelib;
    }

    public void setAnneelib(String annee) {
        this.anneelib = annee;
    }

    public int getNb() {
        return nb;
    }

    public void setNb(int nb) {
        this.nb = nb;
    }

    public String getHeurevente() {
        return heurevente;
    }

    public void setHeurevente(String heurevente) {
        this.heurevente = heurevente;
    }
}
