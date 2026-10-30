package rh.pointage;

import bean.ClassMAPTable;

import java.sql.Date;

public class PointageRetardPoint extends ClassMAPTable {
    Date daty;
    String point_de_vente;
    int nb_retards;
    double retard_moyen_minutes;

    public PointageRetardPoint() {
        this.setNomTable("V_POINTAGE_RETARD_POINT");
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public String getPoint_de_vente() {
        return point_de_vente;
    }

    public void setPoint_de_vente(String point_de_vente) {
        this.point_de_vente = point_de_vente;
    }

    public int getNb_retards() {
        return nb_retards;
    }

    public void setNb_retards(int nb_retards) {
        this.nb_retards = nb_retards;
    }

    public double getRetard_moyen_minutes() {
        return retard_moyen_minutes;
    }

    public void setRetard_moyen_minutes(double retard_moyen_minutes) {
        this.retard_moyen_minutes = retard_moyen_minutes;
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
