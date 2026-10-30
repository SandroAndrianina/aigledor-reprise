package compteur;

public class CompteurElectriciteMereLib extends CompteurElectriciteMere{
    private  String lignelib;
    private  String etatlib;
    private  double consommation;


    public String getEtatlib() {
        return etatlib;
    }

    public void setEtatlib(String etatlib) {
        this.etatlib = etatlib;
    }

    public double getConsommation() {
        return consommation;
    }

    public void setConsommation(double consommation) {
        this.consommation = consommation;
    }

    public String getLignelib() {
            return lignelib;
        }

    public void setLignelib(String lignelib) {
        this.lignelib = lignelib;
    }

    public  CompteurElectriciteMereLib() throws Exception {
        this.setNomTable("COMPTEURELECTRICITEMERELIB");
    }

}
