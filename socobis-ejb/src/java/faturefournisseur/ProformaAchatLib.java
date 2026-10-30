package faturefournisseur;

public class ProformaAchatLib extends ProformaAchat {

    private String idFournisseurLib;
    private String etatlib;

    public ProformaAchatLib() throws Exception {
        this.setNomTable("PROFORMA_ACHAT_LIB");
    }

    public String getIdFournisseurLib() {
        return idFournisseurLib;
    }

    public void setIdFournisseurLib(String idFournisseurLib) {
        this.idFournisseurLib = idFournisseurLib;
    }

    public String getEtatlib() {
        return etatlib;
    }

    public void setEtatlib(String etatlib) {
        this.etatlib = etatlib;
    }
}
