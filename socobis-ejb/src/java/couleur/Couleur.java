package couleur;

import bean.ClassMAPTable;
import java.sql.Connection;

public class Couleur extends ClassMAPTable {
    private String referencecouleur;
    private String nomcouleur;
    private String codecouleur;

    public String getReferencecouleur() {
        return referencecouleur;
    }

    public void setReferencecouleur(String referencecouleur) {
        this.referencecouleur = referencecouleur;
    }

    public String getNomcouleur() {
        return nomcouleur;
    }

    public void setNomcouleur(String nomcouleur) {
        this.nomcouleur = nomcouleur;
    }

    public String getCodecouleur() {
        return codecouleur;
    }

    public void setCodecouleur(String codecouleur) {
        this.codecouleur = codecouleur;
    }

    public Couleur() throws Exception {
        this.setNomTable("V_REFERENCE_COULEUR");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
//        this.preparePk("","");
//        this.setId(makePK(c));
    }

    @Override
    public String getTuppleID() {
        return referencecouleur;
    }

    @Override
    public String getAttributIDName() {
        return "referencecouleur";
    }

    @Override
    public String[] getMotCles() {
        String[] motCles={"referencecouleur","nomcouleur"};
        return motCles;
    }

}

