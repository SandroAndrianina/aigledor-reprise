package annexe.src.java.rh.pointage;

public class PointageMagasinierLib extends PointageMagasinier{
    private String idPointLib;
    private String idPersonnelLib;
    private String matriculePersonnel;
    private String idTypePointageLib;
    private String etatLib;

    public PointageMagasinierLib(){
        this.setNomTable("POINTAGE_MAGASINIER_LIB");
    }

    public String getIdPointLib() {
        return idPointLib;
    }

    public void setIdPointLib(String idPointLib) {
        this.idPointLib = idPointLib;
    }

    public String getIdPersonnelLib() {
        return idPersonnelLib;
    }

    public void setIdPersonnelLib(String idPersonnelLib) {
        this.idPersonnelLib = idPersonnelLib;
    }

    public String getMatriculePersonnel() {
        return matriculePersonnel;
    }

    public void setMatriculePersonnel(String matriculePersonnel) {
        this.matriculePersonnel = matriculePersonnel;
    }

    public String getIdTypePointageLib() {
        return idTypePointageLib;
    }

    public void setIdTypePointageLib(String idTypePointageLib) {
        this.idTypePointageLib = idTypePointageLib;
    }

    public String getEtatLib() {
        return etatLib;
    }

    public void setEtatLib(String etatLib) {
        this.etatLib = etatLib;
    }
}
