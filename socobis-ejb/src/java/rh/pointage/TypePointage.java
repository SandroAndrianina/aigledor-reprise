package rh.pointage;

import bean.TypeObjet;

import java.sql.Connection;

public class TypePointage extends TypeObjet {
    public TypePointage() throws Exception {
        this.setNomTable("TYPEPOINTAGE");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("TP","GETSEQ_TYPEPOINTAGE");
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
