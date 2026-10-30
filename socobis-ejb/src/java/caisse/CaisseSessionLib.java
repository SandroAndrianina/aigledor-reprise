package caisse;

public class CaisseSessionLib extends CaisseSession{

    private String idPointLib;
    private String idUserOuvertureLib;
    private String idUserFermetureLib,idCaisseLib;

    public CaisseSessionLib() throws Exception
    {
        this.setNomTable("CAISSESESSIONLIB");
    }

    public String getIdPointLib() {
        return idPointLib;
    }

    public void setIdPointLib(String idPointLib) {
        this.idPointLib = idPointLib;
    }

    public String getIdUserOuvertureLib() {
        return idUserOuvertureLib;
    }

    public void setIdUserOuvertureLib(String idUserOuvertureLib) {
        this.idUserOuvertureLib = idUserOuvertureLib;
    }

    public String getIdUserFermetureLib() {
        return idUserFermetureLib;
    }

    public void setIdUserFermetureLib(String idUserFermetureLib) {
        this.idUserFermetureLib = idUserFermetureLib;
    }

    public String getIdCaisseLib() {
        return idCaisseLib;
    }

    public void setIdCaisseLib(String idCaisseLib) {
        this.idCaisseLib = idCaisseLib;
    }
}
