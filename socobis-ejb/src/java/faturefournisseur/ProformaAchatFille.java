package faturefournisseur;

import bean.ClassFille;

import java.sql.Connection;

public class ProformaAchatFille extends ClassFille {

    private String id;
    private String idMere;
    private String idProduit;
    private String designation;
    private double pu;
    private double qteDisponible;
    private String remarque;
    private int delaisLivraison;

    public ProformaAchatFille() throws Exception {
        setNomTable("PROFORMA_ACHAT_FILLE");
        setNomClasseMere("faturefournisseur.ProformaAchat");
        setLiaisonMere("idMere");
    }

    public void construirePK(Connection c) throws Exception {
        this.preparePk("PROFAF", "GET_SEQ_PROFORMA_ACHAT_FILLE");
        this.setId(makePK(c));
    }

    @Override
    public void setNomClasseMere(String nomClasseMer) throws Exception {
        super.setNomClasseMere("faturefournisseur.ProformaAchat");
    }

    @Override
    public void setLiaisonMere(String liaisonMere) {
        super.setLiaisonMere("idMere");
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

    public double getPu() {
        return pu;
    }

    public void setPu(double pu) throws Exception {
        if (this.getMode().equalsIgnoreCase("modif") && pu <= 0)
            throw new Exception("PU non Valide");
        this.pu = pu;
    }

    public double getQteDisponible() {
        return qteDisponible;
    }

    public void setQteDisponible(double qteDisponible) throws Exception {
        if (this.getMode().equalsIgnoreCase("modif") && qteDisponible <= 0)
            throw new Exception("Quantit\u00E9 Disponible non Valide");
        this.qteDisponible = qteDisponible;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public int getDelaisLivraison() {
        return delaisLivraison;
    }

    public void setDelaisLivraison(int delaisLivraison) {
        this.delaisLivraison = delaisLivraison;
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
