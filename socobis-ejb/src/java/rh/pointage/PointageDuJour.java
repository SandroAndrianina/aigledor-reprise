package rh.pointage;

import bean.ClassMAPTable;

import java.sql.Date;

public class PointageDuJour extends ClassMAPTable {
    Date daty;
    String heure, employe, point_de_vente, action;

    public PointageDuJour() {
        this.setNomTable("V_POINTAGE_JOUR");
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

    public String getEmploye() {
        return employe;
    }

    public void setEmploye(String employe) {
        this.employe = employe;
    }

    public String getPoint_de_vente() {
        return point_de_vente;
    }

    public void setPoint_de_vente(String point_de_vente) {
        this.point_de_vente = point_de_vente;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String getTuppleID() {
        return daty.toString();
    }

    @Override
    public String getAttributIDName() {
        return "daty";
    }
}
