package maintenance.configuration;
import compteur.CompteurElectricite;

public class CompteurElectiriciteLib extends CompteurElectricite{

    String idLigneLib, idMachineLib, idCategorieLib;


    public String getIdLigneLib() {
        return idLigneLib;
    }

    public void setIdLigneLib(String idLigneLib) {
        this.idLigneLib = idLigneLib;
    }

    public String getIdMachineLib() {
        return idMachineLib;
    }

    public void setIdMachineLib(String idMachineLib) {
        this.idMachineLib = idMachineLib;
    }

    public String getIdCategorieLib() {
        return idCategorieLib;
    }

    public void setIdCategorieLib(String idCategorieLib) {
        this.idCategorieLib = idCategorieLib;
    }

    public CompteurElectiriciteLib() throws Exception {
        this.setNomTable("CompteurElectriciteLib");
    }
}
