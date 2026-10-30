package annexe.src.java.rh.pointage;

import bean.ClassEtat;
import bean.ClassMAPTable;

import java.sql.Connection;
import java.sql.Date;

public class PointageMagasinier extends ClassEtat {
    private String id;
    private String idPoint;
    private String idPersonnel;
    private String idTypePointage;
    Date daty;
    String heure;
    String remarque;
    int etat;

    public String getIdPoint() {
        return idPoint;
    }

    public void setIdPoint(String idPoint) {
        this.idPoint = idPoint;
    }

    public String getIdPersonnel() {
        return idPersonnel;
    }

    public void setIdPersonnel(String idPersonnel) {
        this.idPersonnel = idPersonnel;
    }

    public String getIdTypePointage() {
        return idTypePointage;
    }

    public void setIdTypePointage(String idTypePointage) {
        this.idTypePointage = idTypePointage;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public PointageMagasinier(){
        this.setNomTable("POINTAGE_MAGASINIER");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void construirePK(Connection c) throws Exception {
        this.preparePk("POM", "GETSEQ_POINTAGE_MAGASINIER");
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
