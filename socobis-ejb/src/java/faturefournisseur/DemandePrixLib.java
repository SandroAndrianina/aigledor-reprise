package faturefournisseur;

import bean.CGenUtil;
import utilitaire.UtilDB;

import java.sql.Connection;

public class DemandePrixLib extends DemandePrix {

    private String etatlib;

    public DemandePrixLib() throws Exception {
        this.setNomTable("DEMANDE_PRIX_LIB");
    }

    public ProformaAchatFilleLib[] getProformaAchatDetails(Connection c) throws Exception {
        int indice = 0;
        try {
            if (c == null) {
                c = new UtilDB().GetConn();
                indice = 1;
            }
            DemandePrixFilleLib[] demandePrixFille = (DemandePrixFilleLib[]) CGenUtil.rechercher(new DemandePrixFilleLib(), null, null, c, " and idMere = '" + getId() + "'");
            ProformaAchatFilleLib[] proformaAchatFille = new ProformaAchatFilleLib[demandePrixFille.length];

            for (int i = 0; i < demandePrixFille.length; i++) {
                proformaAchatFille[i] = new ProformaAchatFilleLib();
                proformaAchatFille[i].setIdProduit(demandePrixFille[i].getIdProduit());
                proformaAchatFille[i].setDesignation(demandePrixFille[i].getDesignation());
                proformaAchatFille[i].setQteDisponible(demandePrixFille[i].getQuantite());
                proformaAchatFille[i].setIdUniteLib(demandePrixFille[i].getIdUniteLib());
            }
            return proformaAchatFille;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            if (indice == 1) {
                c.close();
            }
        }
    }

    public String getEtatlib() {
        return etatlib;
    }

    public void setEtatlib(String etatlib) {
        this.etatlib = etatlib;
    }
}
