package depense;

import bean.ClassFille;
import java.sql.Connection;

public class AchatDiversFille extends ClassFille {
    private String id;
    private String idMere;
    private String idProduit;
    private String designation;
    private double quantite;
    private double pu;

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

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public double getPu() {
        return pu;
    }

    public void setPu(double pu) {
        this.pu = pu;
    }


    @Override
    public String getNomClasseMere() {
        return "depense.AchatDivers";
    }

    @Override
    public String getLiaisonMere() {
        return "idMere";
    }

    public AchatDiversFille() throws Exception {
        this.setNomTable("ACHATDIVERSFILLE");
        this.setNomClasseMere("depense.AchatDivers");
        this.setLiaisonMere("idMere");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("ADF","getseqachatDiversFille");
        this.setId(makePK(c));
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

