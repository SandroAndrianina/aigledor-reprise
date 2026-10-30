package produits;

public class IngredientsLibAchat extends IngredientsLib {

    private String compte_achat;

    public IngredientsLibAchat() {
        setNomTable("ST_INGREDIENTSAUTOACHAT_CPL");
    }

    @Override
    public String getCompte_achat() {
        return compte_achat;
    }

    @Override
    public void setCompte_achat(String compte_achat) {
        this.compte_achat = compte_achat;
    }
}
