package pointage;

import bean.ClassMAPTable;
import java.sql.Connection;
import java.sql.Date;

public class PointageKPIAbsence extends ClassMAPTable {
    private Date daty;
    private int nb_retards;
    private int retard_moyen_minutes;

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public int getNb_retards() {
        return nb_retards;
    }

    public void setNb_retards(int nb_retards) {
        this.nb_retards = nb_retards;
    }

    public int getRetard_moyen_minutes() {
        return retard_moyen_minutes;
    }

    public void setRetard_moyen_minutes(int retard_moyen_minutes) {
        this.retard_moyen_minutes = retard_moyen_minutes;
    }



    public PointageKPIAbsence() throws Exception {
        this.setNomTable("V_POINTAGE_KPI_RETARD");
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

