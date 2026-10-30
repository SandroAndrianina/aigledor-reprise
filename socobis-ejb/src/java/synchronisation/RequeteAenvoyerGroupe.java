package synchronisation;

import java.sql.*;
import java.util.ArrayList;

public class RequeteAenvoyerGroupe extends RequeteAEnvoyer {

    private int etat;
    private String datyg;
    private String idrequeteAenvoyer;
    private ArrayList<RequeteAEnvoyer> tRequeteAEnvoyer;

    public RequeteAenvoyerGroupe(ArrayList<RequeteAEnvoyer> tRequeteAEnvoyer) {
        this.settRequeteAEnvoyer(tRequeteAEnvoyer);
        String idrequeteAenvoyere = "", actione = "", tableconcernee = "", requetee = "", datye = "", heuree = "", serveure = "";
        for (RequeteAEnvoyer requests : this.gettRequeteAEnvoyer()) {
            idrequeteAenvoyere += requests.getId() + ";;";
            actione += requests.getAction() + ";;";
            tableconcernee += requests.getTableconcerne() + ";;";
            requetee += requests.getRequete() + ";;";
            datye += requests.getDaty() + ";;";
            heuree += requests.getHeure() + ";;";
            serveure += requests.getServeur() + ";;";
        }
        this.setAction(actione);
        this.setRequete(requetee);
        this.setDatyg(datye);
        this.setTableconcerne(tableconcernee);
        this.setHeure(heuree);
        this.setServeur(serveure);
        this.setEtat(1);
        this.setIdrequeteAenvoyer(idrequeteAenvoyere);
    }

    public RequeteAenvoyerGroupe(String idrequeteAenvoyer, String action, String tableconcerne, String requete, String datyg, String heure, String serveur) {
        this.setIdrequeteAenvoyer(idrequeteAenvoyer);
        this.setAction(action);
        this.setHeure(heure);
        this.setDatyg(datyg);
        this.setTableconcerne(tableconcerne);
        this.setRequete(requete);
        this.setServeur(serveur);
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public String getDatyg() {
        return datyg;
    }

    public void setDatyg(String datyg) {
        this.datyg = datyg;
    }

    public ArrayList<RequeteAEnvoyer> gettRequeteAEnvoyer() {
        return tRequeteAEnvoyer;
    }

    public void settRequeteAEnvoyer(ArrayList<RequeteAEnvoyer> tRequeteAEnvoyer) {
        this.tRequeteAEnvoyer = tRequeteAEnvoyer;
    }

    public String getIdrequeteAenvoyer() {
        return idrequeteAenvoyer;
    }

    public void setIdrequeteAenvoyer(String idrequeteAenvoyer) {
        this.idrequeteAenvoyer = idrequeteAenvoyer;
    }
    public static PreparedStatement getPrepared(Connection con) throws Exception {
        String query = "INSERT INTO requete_a_envoyer_groupe (ID,ACTION,TABLECONCERNE,ETAT,DATYG,HEURE,SERVEUR,IDREQUETEAENVOYER,REQUETE) VALUES ('RAEG00'||getseqrequete_a_envoyer_groupe,?,?,?,?,?,?,?,?)";
//        return con.prepareCall(query);
        return con.prepareStatement(query);
    }

    public RequeteAenvoyerGroupe inserer(PreparedStatement insertHistoSync) throws Exception {
        LogSynchro.println("=========================grouperRequeteAenvoyer Start tttt");

        try {
            setClobSafe(insertHistoSync, 1, getAction());
            setClobSafe(insertHistoSync, 2, getTableconcerne());
            insertHistoSync.setInt(3, this.getEtat());
            setClobSafe(insertHistoSync, 4, getDatyg());
            setClobSafe(insertHistoSync, 5, getHeure());
            setClobSafe(insertHistoSync, 6, getServeur());
            setClobSafe(insertHistoSync, 7, getIdrequeteAenvoyer());
            setClobSafe(insertHistoSync, 8, getRequete());

            insertHistoSync.executeUpdate();

            LogSynchro.println("=========================grouperRequeteAenvoyer End");
            return this;

        } catch (Exception e) {
            e.printStackTrace();
            insertHistoSync.getConnection().rollback();

        }
        return this;
    }

    private void setClobSafe(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null) value = "";
        ps.setClob(index, new java.io.StringReader(value), value.length());
    }

    public RequeteAenvoyerGroupe inserer2(Connection con) throws Exception {
        LogSynchro.println("=========================grouperRequeteAenvoyer Start tttt");

        String id = null;
        try {
            LogSynchro.println("DEBUG lengths -> action=" + safeLen(getAction())
                    + " tableconcerne=" + safeLen(getTableconcerne())
                    + " datyg=" + safeLen(getDatyg())
                    + " heure=" + safeLen(getHeure())
                    + " serveur=" + safeLen(getServeur())
                    + " idrequeteAenvoyer=" + safeLen(getIdrequeteAenvoyer())
                    + " requete=" + safeLen(getRequete()));

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT 'RAEG00'||getseqrequete_a_envoyer_groupe AS ID FROM dual")) {
                if (rs.next()) {
                    id = rs.getString("ID");
                }
            }

            String insertSql = "INSERT INTO requete_a_envoyer_groupe " +
                    "(ID,ACTION,TABLECONCERNE,ETAT,DATYG,HEURE,SERVEUR,IDREQUETEAENVOYER,REQUETE) " +
                    "VALUES (?,EMPTY_CLOB(),EMPTY_CLOB(),?,EMPTY_CLOB(),EMPTY_CLOB(),EMPTY_CLOB(),EMPTY_CLOB(),EMPTY_CLOB())";
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setString(1, id);
                ps.setInt(2, this.getEtat());
                ps.executeUpdate();
            }

            String selectSql = "SELECT action, tableconcerne, datyg, heure, serveur, idrequeteaenvoyer, requete " +
                    "FROM requete_a_envoyer_groupe WHERE id = ? FOR UPDATE";
            try (PreparedStatement selPs = con.prepareStatement(selectSql)) {
                selPs.setString(1, id);
                try (ResultSet rs = selPs.executeQuery()) {
                    if (rs.next()) {
                        writeClob((oracle.sql.CLOB) rs.getClob("action"), getAction());
                        writeClob((oracle.sql.CLOB) rs.getClob("tableconcerne"), getTableconcerne());
                        writeClob((oracle.sql.CLOB) rs.getClob("datyg"), getDatyg());
                        writeClob((oracle.sql.CLOB) rs.getClob("heure"), getHeure());
                        writeClob((oracle.sql.CLOB) rs.getClob("serveur"), getServeur());
                        writeClob((oracle.sql.CLOB) rs.getClob("idrequeteaenvoyer"), getIdrequeteAenvoyer());
                        writeClob((oracle.sql.CLOB) rs.getClob("requete"), getRequete());
                    }
                }
            }

            LogSynchro.println("=========================grouperRequeteAenvoyer End");

        } catch (Exception e) {
            e.printStackTrace();
            con.rollback();
            throw e;
        }
        return this;
    }

    private void writeClob(oracle.sql.CLOB clob, String value) throws Exception {
        try (java.io.Writer w = clob.getCharacterOutputStream()) {
            w.write(value == null ? "" : value);
        }
    }

    private int safeLen(String s) { return s == null ? -1 : s.length(); }

    public RequeteAenvoyerGroupe grouperRequeteAenvoyer(Connection con) throws Exception {
        LogSynchro.println("=========================grouperRequeteAenvoyer Start");

        PreparedStatement insertHistoSync = null;
        try {
            RequeteAenvoyerGroupe requeteGroupe = null;

            if (this.gettRequeteAEnvoyer().size() > 0) {
                String idrequeteAenvoyer = "", action = "", tableconcerne = "", requete = "", daty = "", heure = "", serveur = "";
                for (RequeteAEnvoyer requests : this.gettRequeteAEnvoyer()) {
                    idrequeteAenvoyer += requests.getId() + ";;";
                    action += requests.getAction() + ";;";
                    tableconcerne += requests.getTableconcerne() + ";;";
                    requete += requests.getRequete() + ";;";
                    daty += requests.getDaty() + ";;";
                    heure += requests.getHeure() + ";;";
                    serveur += requests.getServeur() + ";;";
                }

                requeteGroupe = new RequeteAenvoyerGroupe(idrequeteAenvoyer, action, tableconcerne, requete, daty, heure, serveur);
                String query = "INSERT INTO requete_a_envoyer_groupe (ID,ACTION,TABLECONCERNE,REQUETE,DATYG,HEURE,SERVEUR,IDREQUETEAENVOYER,ETAT) VALUES ('RAEG00'||getseqrequete_a_envoyer_groupe,?,?,?,?,?,?,?,?)";
                insertHistoSync = con.prepareCall(query);
//            insertHistoSync.setString(1, "");
                insertHistoSync.setString(1, requeteGroupe.getAction());
                insertHistoSync.setString(2, requeteGroupe.getTableconcerne());
                insertHistoSync.setString(3, requeteGroupe.getRequete());
                insertHistoSync.setString(4, requeteGroupe.getDatyg());
                insertHistoSync.setString(5, requeteGroupe.getHeure());
                insertHistoSync.setString(6, requeteGroupe.getServeur());
                insertHistoSync.setString(7, requeteGroupe.getIdrequeteAenvoyer());
                insertHistoSync.setInt(8, 1);
                insertHistoSync.executeUpdate();
            }
            LogSynchro.println("=========================grouperRequeteAenvoyer End");
            return requeteGroupe;

        } catch (Exception e) {
            if (con != null) {
                con.rollback();
            }
        } finally {
            if (insertHistoSync != null) {
                insertHistoSync.close();
            }
        }
        return null;
    }

}
