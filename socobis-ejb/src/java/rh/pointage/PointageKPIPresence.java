package rh.pointage;

import bean.ClassMAPTable;

import java.sql.Date;

public class PointageKPIPresence extends ClassMAPTable {
    private Date daty;
    int presents, absents, magasiniers_attendus;
    double taux_presence;

    public PointageKPIPresence() {
        this.setNomTable("V_POINTAGE_KPI_PRESENCE");
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public int getPresents() {
        return presents;
    }

    public void setPresents(int presents) {
        this.presents = presents;
    }

    public int getAbsents() {
        return absents;
    }

    public void setAbsents(int absents) {
        this.absents = absents;
    }

    public int getMagasiniers_attendus() {
        return magasiniers_attendus;
    }

    public void setMagasiniers_attendus(int magasiniers_attendus) {
        this.magasiniers_attendus = magasiniers_attendus;
    }

    public double getTaux_presence() {
        return taux_presence;
    }

    public void setTaux_presence(double taux_presence) {
        this.taux_presence = taux_presence;
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
