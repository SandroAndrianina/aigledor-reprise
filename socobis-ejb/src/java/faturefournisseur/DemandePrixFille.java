package faturefournisseur;

import bean.ClassFille;

import java.sql.Connection;

public class DemandePrixFille extends ClassFille {

    private String id;
    private String idMere;
    private String idProduit;
    private String designation;
    private double quantite;

    public DemandePrixFille() throws Exception {
        this.setNomTable("DEMANDE_PRIX_FILLE");
        this.setNomClasseMere("faturefournisseur.DemandePrix");
        this.setLiaisonMere("idMere");
    }

    public void construirePK(Connection c) throws Exception {
        this.preparePk("DMDPF", "GET_SEQ_DEMANDE_PRIX_FILLE");
        this.setId(makePK(c));
    }

    @Override
    public String getNomClasseMere() {
        return "faturefournisseur.DemandePrix";
    }

    @Override
    public String getLiaisonMere() {
        return "idMere";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdMere() {
        return idMere;
    }

    public void setIdMere(String idMere) {
        this.idMere = idMere;
    }

    public String getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(String idProduit) {
        this.idProduit = idProduit;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) throws Exception {
        if(this.getMode().equals("modif")){
            if (quantite <= 0){
                throw new Exception("La quantit\u00E9 ne doit pas \u00EAtre inf\u00E9rieure ou \u00E9gale \u00E0 O");
            }
        }
        this.quantite = quantite;
    }

    @Override
    public String getTuppleID() {
        return id;
    }

    @Override
    public String getAttributIDName() {
        return "id";
    }
}
