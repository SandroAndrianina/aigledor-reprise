package synchronisation;

import bean.ClassMAPTable;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;

public class RequeteAEnvoyer extends ClassMAPTable {

    private String id;
    private String action;
    private String heure;
    private Date daty;
    private String tableconcerne;
    private String requete;
    private String serveur;

    public RequeteAEnvoyer() {
        this.setNomTable("requete_a_envoyer");
    }

    public static ArrayList<RequeteAEnvoyer> getAll(ResultSet rs) throws Exception {
        ArrayList<RequeteAEnvoyer> result = new ArrayList<RequeteAEnvoyer>();
        while (rs.next()) {
            RequeteAEnvoyer temp = new RequeteAEnvoyer(rs.getString("ID"), rs.getString("ACTION"), rs.getString("TABLECONCERNE"), rs.getString("REQUETE"), rs.getDate("DATY"), rs.getString("HEURE"), rs.getString("SERVEUR"));
            result.add(temp);
        }

        return result;
    }

    public RequeteAEnvoyer(String action, String heure, Date daty, String tableconcerne, String requete, String serveur) {
        this.action = action;
        this.heure = heure;
        this.daty = daty;
        this.tableconcerne = tableconcerne;
        this.requete = requete;
        this.serveur = serveur;
    }

    public RequeteAEnvoyer(String id, String action, String tableconcerne, String requete, Date daty, String heure, String serveur) {
        this.id = id;
        this.action = action;
        this.heure = heure;
        this.daty = daty;
        this.tableconcerne = tableconcerne;
        this.requete = requete;
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

    public void construirePK(Connection connection) throws Exception {
        preparePk("RAE", "getSeqRequeteAEnvoyer");
        setId(makePK(connection));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public String getTableconcerne() {
        return tableconcerne;
    }

    public void setTableconcerne(String tableconcerne) {
        this.tableconcerne = tableconcerne;
    }

    public String getRequete() {
        return requete;
    }

    public void setRequete(String requete) {
        this.requete = requete;
    }

    public String getServeur() {
        return serveur;
    }

    public void setServeur(String serveur) {
        this.serveur = serveur;
    }

}
