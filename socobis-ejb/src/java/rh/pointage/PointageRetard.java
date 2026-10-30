package pointage;

import bean.ClassMAPTable;
import java.sql.Connection;
import java.sql.Date;

public class PointageRetard extends ClassMAPTable {
    private Date daty;
    private String personnel;
    private String point_de_vente;
    private String heure_arrivee;
    private int retard_minutes;

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public String getPersonnel() {
        return personnel;
    }

    public void setPersonnel(String personnel) {
        this.personnel = personnel;
    }

    public String getPoint_de_vente() {
        return point_de_vente;
    }

    public void setPoint_de_vente(String point_de_vente) {
        this.point_de_vente = point_de_vente;
    }

    public String getHeure_arrivee() {
        return heure_arrivee;
    }

    public void setHeure_arrivee(String heure_arrivee) {
        this.heure_arrivee = heure_arrivee;
    }

    public int getRetard_minutes() {
        return retard_minutes;
    }

    public void setRetard_minutes(int retard_minutes) {
        this.retard_minutes = retard_minutes;
    }



    public PointageRetard() throws Exception {
        this.setNomTable("V_POINTAGE_RETARDS");
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

