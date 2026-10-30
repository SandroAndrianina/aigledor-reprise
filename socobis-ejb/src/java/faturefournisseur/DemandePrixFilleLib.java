package faturefournisseur;

public class DemandePrixFilleLib extends DemandePrixFille {

    private String idProduitLib;
    private String idUniteLib;

    public DemandePrixFilleLib() throws Exception {
        this.setNomTable("DEMANDE_PRIX_FILLELIB");
    }

    public String getIdProduitLib() {
        return idProduitLib;
    }

    public void setIdProduitLib(String idProduitLib) {
        this.idProduitLib = idProduitLib;
    }

    public String getIdUniteLib() {
        return idUniteLib;
    }

    public void setIdUniteLib(String idUniteLib) {
        this.idUniteLib = idUniteLib;
    }
}
