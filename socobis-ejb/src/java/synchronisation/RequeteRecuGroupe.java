package synchronisation;

import synchronisation.utilitaire.Utilitaire;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class RequeteRecuGroupe extends HistoriqueSync {

    private int etat;
    private String datyG;
    private String idrequeteAenvoyer;

    public RequeteRecuGroupe() {
    }

    public static PreparedStatement preparer(Connection c)throws Exception
    {
        String req = " INSERT INTO requete_recu_groupe (ID,ACTION,TABLECONCERNE,REQUETE,DATYG,HEURE,SERVEUR,ETAT,IDREQUETEAENVOYER) values ('RRG'||getseqrequete_recu_groupe,?,?,?,?,?,?,?,?)";
        return c.prepareStatement(req);
    }

    public RequeteRecuGroupe(String id, String action, String tableconcerne, String requete, String datyg, String heure, String serveur, int etat) {
        this.setId(id);
        this.setAction(action);
        this.setTableconcerne(tableconcerne);
        this.setRequete(requete);
        this.setDatyG(datyg);
        this.setHeure(heure);
        this.setServeur(serveur);
        this.setEtat(etat);
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public String getDatyG() {
        return datyG;
    }

    public void setDatyG(String datyG) {
        this.datyG = datyG;
    }

    public String getIdrequeteAenvoyer() {
        return idrequeteAenvoyer;
    }

    public void setIdrequeteAenvoyer(String idrequeteAenvoyer) {
        this.idrequeteAenvoyer = idrequeteAenvoyer;
    }

    static ArrayList<RequeteRecuGroupe> getAllRRecG(ResultSet rs) throws Exception {
        ArrayList<RequeteRecuGroupe> result = new ArrayList<RequeteRecuGroupe>();
        int count = 0;
        while (rs.next()) {
            RequeteRecuGroupe temp = new RequeteRecuGroupe(rs.getString("id"), Utilitaire.clobToString(rs.getClob("action")), Utilitaire.clobToString(rs.getClob("tableconcerne")), Utilitaire.clobToString(rs.getClob("requete")), Utilitaire.clobToString(rs.getClob("datyg")), Utilitaire.clobToString(rs.getClob("heure")), Utilitaire.clobToString(rs.getClob("serveur")), rs.getInt("etat"));
            count++;
            result.add(temp);
        }
        LogSynchro.println("\n");
        return result;
    }

    public static ArrayList<RequeteRecuGroupe> getRequetteRecu(Connection connection) throws Exception {
        Statement stmtServ = null;
        try {
            String req = " select * from requete_recu_groupe where etat = 1 order by id asc";
            stmtServ = connection.createStatement();
            ResultSet rs = stmtServ.executeQuery(req);
            return RequeteRecuGroupe.getAllRRecG(rs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            if (stmtServ != null) {
                stmtServ.close();
            }
        }
    }

    public  void traitementREqueteRecu(Connection connection) throws Exception {
        ArrayList<RequeteRecuGroupe> tRequeteRecuGroupe = getRequetteRecu(connection);
        PreparedStatement statement = null;
        PreparedStatement statementUpdate = null;
        try{
            int nbVita = 0;
            String req = " INSERT INTO HISTORIQUE_SYNC (ID,ACTION,TABLECONCERNE,REQUETE,DATY,HEURE,SERVEUR,ETAT) values ('HS'||GETSEQHISTORIQUESYNC,?,?,?,?,?,?,?)";
            statement = connection.prepareStatement(req);

            String scriptUpdate = " UPDATE requete_recu_groupe set etat = 11 where id = ?";
            statementUpdate = connection.prepareStatement(scriptUpdate);

            for (RequeteRecuGroupe request : tRequeteRecuGroupe) {
                String[] tAction = Utilitaire.split(request.getAction(), ";;");
                String[] tTableconcerne = Utilitaire.split(request.getTableconcerne(), ";;");
                String[] tRequete = Utilitaire.split(request.getRequete(), ";;");
                String[] tDatyg = Utilitaire.split(request.getDatyG(), ";;");
                String[] tHeure = Utilitaire.split(request.getHeure(), ";;");
                String[] tServeur = Utilitaire.split(request.getServeur(), ";;");


                for (int i = 0; i < tRequete.length; i++) {
                    statement.setString(1, tAction[i]);
                    statement.setString(2, tTableconcerne[i]);
                    statement.setString(3, tRequete[i].replace("'", "''"));
                    statement.setDate(4, Utilitaire.stringDate(Utilitaire.formatterDaty(tDatyg[i])));
                    statement.setString(5, tHeure[i]);
                    statement.setString(6, tServeur[i]);
                    statement.setInt(7, 1);
                    int valiny = statement.executeUpdate();
                    nbVita ++;
                }
                if (nbVita > 0) {
                    statementUpdate.setString(1, request.getId());
                    statementUpdate.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
            //throw new Exception(e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
                statementUpdate.close();
            }
        }
    }
}
