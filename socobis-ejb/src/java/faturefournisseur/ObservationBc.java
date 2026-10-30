package faturefournisseur;

import bean.ClassMAPTable;

import java.sql.Connection;

public class ObservationBc extends ClassMAPTable {

    private String id;
    private String idBc;
    private String observation;

    public ObservationBc() throws Exception {
        setNomTable("OBSERVATIONBC");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        preparePk("OBS", "GET_SEQ_OBSERVATIONBC");
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdBc() {
        return idBc;
    }

    public void setIdBc(String idBc) throws Exception {
        if (getMode().equals("modif") && (idBc == null || idBc.isEmpty())) {
            throw new Exception("L'Id du bon de commande doit \u00EAtre pr\u00E9sent.");
        }
        this.idBc = idBc;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
