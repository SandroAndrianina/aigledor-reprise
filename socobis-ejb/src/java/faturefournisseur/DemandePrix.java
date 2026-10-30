package faturefournisseur;

import bean.ClassMere;
import utilitaire.UtilDB;

import java.sql.Connection;
import java.sql.Date;

public class DemandePrix extends ClassMere {

    private String id;
    private String designation;
    private Date daty;
    private String acheteur;

    public DemandePrix() throws Exception {
        setNomTable("DEMANDE_PRIX");
        this.setLiaisonFille("idMere");
        this.setNomClasseFille("faturefournisseur.DemandePrixFille");
    }

    public void construirePK(Connection c) throws Exception {
        this.preparePk("DMDP", "GET_SEQ_DEMANDEPRIX");
        this.setId(makePK(c));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public String getAcheteur() {
        return acheteur;
    }

    public void setAcheteur(String acheteur) {
        this.acheteur = acheteur;
    }

    @Override
    public String getTuppleID() {
        return id;
    }

    @Override
    public String getAttributIDName() {
        return "id";
    }


    public ProformaAchatFille[] getProformaAchatDetails(Connection c) throws Exception {
        int indice = 0;
        try {
            if (c == null) {
                c = new UtilDB().GetConn();
                indice = 1;
            }
            DemandePrixFille[] demandePrixFille = (DemandePrixFille[]) this.getFille("DEMANDE_PRIX_FILLE", c, "");
            ProformaAchatFille[] proformaAchatFille = new ProformaAchatFille[demandePrixFille.length];

            for (int i = 0; i < demandePrixFille.length; i++) {
                proformaAchatFille[i] = new ProformaAchatFille();
                proformaAchatFille[i].setIdProduit(demandePrixFille[i].getIdProduit());
                proformaAchatFille[i].setDesignation(demandePrixFille[i].getDesignation());
                proformaAchatFille[i].setQteDisponible(demandePrixFille[i].getQuantite());
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
}
