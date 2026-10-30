package compteur;

public class CompteurElectriciteLib extends  CompteurElectricite{

    private String idmachinelib;

    private String IdCategorieLib;

    public String getIdCategorieLib() {
        return IdCategorieLib;
    }

    public void setIdCategorieLib(String idCategorieLib) {
        IdCategorieLib = idCategorieLib;
    }

    public String getIdmachinelib() {
        return idmachinelib;
    }

    public void setIdmachinelib(String idmachinelib) {
        this.idmachinelib = idmachinelib;
    }

    public CompteurElectriciteLib() throws Exception {
        this.setNomTable("COMPTEURELECTRICITELIB");
    }
}
