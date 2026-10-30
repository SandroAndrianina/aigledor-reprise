package modele;

public class ModeleLib extends Modele{


    private String idCategorieLib;
    private String idSousCategorieLib;
    private String universLib;
    private String gammeLib;
    private String statutLib;
    private String matiereLib;
    private String serieLib;
    private String collectionLib;
    private String typologieLib;
    private String provenanceLib;
    private String idCouleur;
    private String nomCouleur;
    private String codeCouleur;
    private String semelleLib;

    public String getIdCategorieLib() {
        return idCategorieLib;
    }

    public void setIdCategorieLib(String idCategorieLib) {
        this.idCategorieLib = idCategorieLib;
    }

    public String getIdSousCategorieLib() {
        return idSousCategorieLib;
    }

    public void setIdSousCategorieLib(String idSousCategorieLib) {
        this.idSousCategorieLib = idSousCategorieLib;
    }

    public String getUniversLib() {
        return universLib;
    }

    public void setUniversLib(String universLib) {
        this.universLib = universLib;
    }

    public String getGammeLib() {
        return gammeLib;
    }

    public void setGammeLib(String gammeLib) {
        this.gammeLib = gammeLib;
    }

    public String getStatutLib() {
        return statutLib;
    }

    public void setStatutLib(String statutLib) {
        this.statutLib = statutLib;
    }

    public String getMatiereLib() {
        return matiereLib;
    }

    public void setMatiereLib(String matiereLib) {
        this.matiereLib = matiereLib;
    }

    public String getSerieLib() {
        return serieLib;
    }

    public void setSerieLib(String serieLib) {
        this.serieLib = serieLib;
    }

    public String getCollectionLib() {
        return collectionLib;
    }

    public void setCollectionLib(String collectionLib) {
        this.collectionLib = collectionLib;
    }

    public String getTypologieLib() {
        return typologieLib;
    }

    public void setTypologieLib(String typologieLib) {
        this.typologieLib = typologieLib;
    }

    public String getProvenanceLib() {
        return provenanceLib;
    }

    public void setProvenanceLib(String provenanceLib) {
        this.provenanceLib = provenanceLib;
    }

    public String getIdCouleur() {
        return idCouleur;
    }

    public void setIdCouleur(String idCouleur) {
        this.idCouleur = idCouleur;
    }

    public String getNomCouleur() {
        return nomCouleur;
    }

    public void setNomCouleur(String nomCouleur) {
        this.nomCouleur = nomCouleur;
    }

    public String getCodeCouleur() {
        return codeCouleur;
    }

    public void setCodeCouleur(String codeCouleur) {
        this.codeCouleur = codeCouleur;
    }

    public String getSemelleLib() {
        return semelleLib;
    }

    public void setSemelleLib(String semelleLib) {
        this.semelleLib = semelleLib;
    }

    public ModeleLib() throws Exception {
        this.setNomTable("V_MODELE_LIB");
    }
    @Override
    public String[] getMotCles() {
        String[] motCles={"id","libelle","nomcouleur"};
        return motCles;
    }
}
