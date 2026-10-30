package synchronisation;

import bean.ClassMAPTable;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

public class RequeteExecute extends ClassMAPTable {

    private String id;
    private String idrequete;
    private Date daty;
    private String serveur;
    private String heure;

    public static PreparedStatement preparer(Connection c)throws Exception
    {
        String req2 = " INSERT INTO REQUETE_EXECUTE (ID, IDREQUETE, DATY, HEURE, SERVEUR) values ";
        req2 += "('REXC00'||getSeqRequeteExecute,?,?,?,?)";
        return c.prepareStatement(req2);
    }

    public RequeteExecute() {
        this.setNomTable("requete_execute");
    }

    public RequeteExecute(String idrequete, String serveur, Date daty, String heure) {
        this.idrequete = idrequete;
        this.daty = daty;
        this.heure = heure;
    }

    public String getServeur() {
        return serveur;
    }

    public void setServeur(String serveur) {
        this.serveur = serveur;
    }

    @Override
    public String getTuppleID() {
        return id;
    }

    @Override
    public String getAttributIDName() {
        return "id";
    }

    public void construirePK(java.sql.Connection connection) throws Exception {
        preparePk("REX", "getSeqRequeteExecute");
        setId(makePK(connection));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdrequete() {
        return idrequete;
    }

    public void setIdrequete(String idrequete) {
        this.idrequete = idrequete;
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


}
