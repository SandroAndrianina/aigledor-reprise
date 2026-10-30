package depense;

public class AchatDiversFilleLib extends AchatDiversFille{
    private String idProduitLib;
    private double montant;
    public AchatDiversFilleLib() throws Exception {
        this.setNomTable("achatDiversFilleLIB");
    }

    public String getIdProduitLib() {
        return idProduitLib;
    }

    public void setIdProduitLib(String produitLib) {
        this.idProduitLib = produitLib;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }
}
