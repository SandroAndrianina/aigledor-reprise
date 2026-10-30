package faturefournisseur;

import java.sql.Date;

public class ProformaAchatFilleLib extends ProformaAchatFille {

    private String idProduitLib;
    private String idUniteLib;
    private Date daty;
    private String idDemandePrix;
    private String idFournisseur;
    private String idFournisseurLib;
    private String montant;

    public ProformaAchatFilleLib() throws Exception {
        this.setNomTable("PROFORMA_ACHAT_FILLELIB");
    }

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
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

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public String getIdDemandePrix() {
        return idDemandePrix;
    }

    public void setIdDemandePrix(String idDemandePrix) {
        this.idDemandePrix = idDemandePrix;
    }

    public String getIdFournisseur() {
        return idFournisseur;
    }

    public void setIdFournisseur(String idFournisseur) {
        this.idFournisseur = idFournisseur;
    }

    public String getIdFournisseurLib() {
        return idFournisseurLib;
    }

    public void setIdFournisseurLib(String idFournisseurLib) {
        this.idFournisseurLib = idFournisseurLib;
    }
}
