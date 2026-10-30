package synchronisation;

import synchronisation.utils.CustomTime;
import utilitaire.UtilDB;
import utilitaire.Utilitaire;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class SynchroBase {

    private static long time = 0;
    private static int LIMIT_REQUEST_PER_SCHEDULE = 1000;
    private static String[] SPECIFIC_TABLES = new String[]{};

    public static String ipsource,
            username,
            password;

    private static List<Server_Broadcast> _serv;
    public static Connection GetConn() throws Exception {
        return SynchroBase.GetConn(ipsource, username, password);
    }

    public static void execute() throws Exception {
        Connection con = null;
        ArrayList<Server_Broadcast> servers = null;
        PreparedStatement stGroupe = null;
        PreparedStatement statmentDist = null;
        PreparedStatement statmentLoc = null;
        try {
            LogSynchro.println("[INFO]: Début de la synchronisation");
            //connection = SynchroBase.GetConn();

            con = (new UtilDB()).GetConn();
            con.setAutoCommit(false);

            // Execution local
            LogSynchro.println("[INFO]: Reception requetes");
            // maka anizay requete_recu_groupe etat = 1 de manao insert ao amin'ny historique_sync
            //eto no olana
            new RequeteRecuGroupe().traitementREqueteRecu(con);
            ArrayList<HistoriqueSync> requestsToExecute = getRequetteAExecuter(con);
            LogSynchro.println("[INFO]: Reception des requetes recus");
            executeRequeteRecu(con, requestsToExecute);
            con.commit();
            LogSynchro.println("INFO: Execution des requetes recus ");
            //Alaina ny liste serveur cible
            servers = SynchroBase.getServers(con);
            _serv = servers;
            LogSynchro.println("servers tonga" + servers.size());
            // boucle liste serveurs
            stGroupe = RequeteAenvoyerGroupe.getPrepared(con);
            statmentLoc = RequeteExecute.preparer(con);
            for (Server_Broadcast server : servers) {
                //manoratra anaty fichier connex = true
                if (server != null) {
                    Connection connection = null;
                    try {
                        // connection a une serveur
                        connection = server.GetConn();
                        //connection.setAutoCommit(false);
                        LogSynchro.println("INFO: Connection established [" + server.getNom() + "]");
                        LogSynchro.println("INFO: Enregistrement des operations! Veuillez ne plus interrompre le programme");
                        if (connection != null) {
                            NamedServerConnection connectionRow = new NamedServerConnection(server);

                            connectionRow.setConnection(connection);
                            //result.add(nc);
                            ArrayList<RequeteAEnvoyer> requests = null;

                            // step one:
                            LogSynchro.println("INFO[" + connectionRow.getNom() + "]: recuperation des requetes a envoyer ... ");
                            //requests = getHistoriqueSync(connectionRow);
                            requests = getRequetteAEnvoyer(con, server);
                            //RequeteAenvoyerGroupe requeteAenvoyerGroupe = new RequeteAenvoyerGroupe(requests);
                            //envoyeRequeteGroupe(connectionRow, requeteAenvoyerGroupe.grouperRequeteAenvoyer(con), con);
                            AdminReqGroupe listeGroupe = new AdminReqGroupe(requests);
                            statmentDist = RequeteRecuGroupe.preparer(connectionRow.getConnection());
                            for (RequeteAenvoyerGroupe requeteAenvoyerGroupe : listeGroupe.getListeGroupe()) {
                                requeteAenvoyerGroupe.inserer(stGroupe);
                                envoyeRequeteGroupe(connectionRow, requeteAenvoyerGroupe, statmentDist, statmentLoc);
                            }
                            LogSynchro.println("INFO[" + connectionRow.getNom() + "]: Enregistrement OK!");

                        }
                    } //catch exception connxeion is not establish
                    //manoratra anaty fichier connex = false
                    //continue;
                    catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    } finally {
                        if (statmentDist != null) {
                            statmentDist.close();
                        }
                        if (connection != null) {
                            connection.close();
                        }
                    }
                }
            }


//            Traitement Requete recus



            con.commit();
            LogSynchro.println("INFO: Fin du programme de synchronisation! SUCCES! En attente de la prochaine synchronisation");

        }
        catch (Exception e) {
            e.printStackTrace();
            LogSynchro.println(e.getMessage());
            con.rollback();
            throw e;
        } finally {
            if (stGroupe != null) {
                stGroupe.close();
            }
            if (statmentLoc != null) {
                statmentLoc.close();
            }
            if (con != null) {
                con.close();
            }
            LogSynchro.println("INFO: Synchronisation terminee");
            CustomTime _time = new CustomTime().fromMillisecond(time);
            String tt = String.format("%02dd:%02dh:%02dm:%02ds", _time.getDays(), _time.getHours(), _time.getMinutes(), _time.getSeconds());
            LogSynchro.println("INFO: Synchronisation suivante dans [" + tt + "]");
//            LogSynchro.println("----------------------------------- " + new Date().toString());
        }
    }

    private static void setClobSafe(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null) value = "";
        ps.setClob(index, new java.io.StringReader(value), value.length());
    }

    public static void envoyeRequeteGroupe(NamedServerConnection connectionRow, RequeteAenvoyerGroupe request, PreparedStatement statement, PreparedStatement statementLocal) throws Exception {
        //LogSynchro.println("niditra *********************** envoyeRequeteGroupe");

        try {
            LogSynchro.println("request === " + request);
            if (request != null) {

                //statement = connectionRow.getConnection().prepareStatement(req);
//            statement.setString(1, "'RRG'||getseqrequete_recu_groupe");

                /*statement.setString(1, request.getAction());
                statement.setString(2, request.getTableconcerne());
                statement.setString(3, request.getRequete());
                statement.setString(4, request.getDatyg());
                statement.setString(5, request.getHeure());
                statement.setString(6, request.getServeur());
                statement.setInt(7, 1);
                statement.setString(8, request.getIdrequeteAenvoyer());*/

                setClobSafe(statement, 1, request.getAction());
                setClobSafe(statement, 2, request.getTableconcerne());
                setClobSafe(statement, 3, request.getRequete());
                setClobSafe(statement, 4, request.getDatyg());
                setClobSafe(statement, 5, request.getHeure());
                setClobSafe(statement, 6, request.getServeur());
                statement.setInt(7, 1);
                setClobSafe(statement, 8, request.getIdrequeteAenvoyer());

                int valiny = statement.executeUpdate();

                String[] tIdRequete = Utilitaire.split(request.getIdrequeteAenvoyer(), ";;");
                String[] tDaty = Utilitaire.split(request.getDatyg(), ";;");
                String[] tHeure = Utilitaire.split(request.getHeure(), ";;");
                String[] tServeur = Utilitaire.split(request.getServeur(), ";;");
                //LogSynchro.println("ETO *********************** Valiny "+valiny);
                if (valiny > 0) {
                    for (int i = 0; i < tIdRequete.length; i++) {
                        //,,
                        statementLocal.setString(1, tIdRequete[i]);
                        statementLocal.setString(2, Utilitaire.formatterDaty(tDaty[i]));
                        statementLocal.setString(3, tHeure[i]);
                        statementLocal.setString(4, connectionRow.getId());
                        statementLocal.executeUpdate();

                    }
                }
            }
            //LogSynchro.println("nivoka *********************** envoyeRequeteGroupe");

        } catch (Exception e) {
            statement.getConnection().rollback();
            if (statementLocal != null) {
                statementLocal.getConnection().rollback();
            }
            e.printStackTrace();
            LogSynchro.println("INFO[" + connectionRow.getNom() + "]: " + e.getMessage());
            //throw e;
        }
    }

    public static ArrayList<RequeteAEnvoyer> getRequetteAEnvoyer(Connection connection, Server_Broadcast server) throws Exception {
        LogSynchro.println("niditra ******************** getRequetteAEnvoyer");
        Statement stmtServ = null;
        try {
            /*
            RequeteAEnvoyer hsc = new RequeteAEnvoyer();
            hsc.setNomTable("requete_a_envoyer_serveur");
            RequeteAEnvoyer[] listRequete = (RequeteAEnvoyer[]) CGenUtil.rechercher(hsc, null, null, connection, "  and SERVEUR is null or SERVEUR != '" + server.getId() + "' ORDER BY id asc");
             */
            //String getServ = "SELECT * FROM(SELECT * FROM requete_a_envoyer_serveur where id not in (select id from REQUETE_EXECUTE_SERVEUR where SERVEUR = '" + server.getId() + "') ORDER BY daty asc,heure asc,id asc) where rownum <= 1000";
            String getServ = "SELECT * FROM requete_a_envoyer_serveur where id not in (select id from REQUETE_EXECUTE_SERVEUR where SERVEUR = '" + server.getId() + "') ORDER BY daty asc,heure asc,id asc ";

            stmtServ = connection.createStatement();
            LogSynchro.println(getServ);
            ResultSet rs = stmtServ.executeQuery(getServ);
            return RequeteAEnvoyer.getAll(rs);

            //return listRequete;
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
            throw new Exception(e.getMessage());
        } finally {
            if (stmtServ != null) {
                stmtServ.close();
            }
        }
    }

    public static ArrayList<Server_Broadcast> getServers(Connection connection) throws Exception {
        Statement stmtServ = connection.createStatement();
        ArrayList<Server_Broadcast> ret = null;
        try {
            LogSynchro.println("INFO: Recuperation de la liste des serveurs a synchroniser");
            String getServ = "SELECT * FROM Server_Broadcast";
            LogSynchro.println(getServ);
            ResultSet rs = stmtServ.executeQuery(getServ);
            ret = Server_Broadcast.getAll(rs);
        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
            }
        } finally {
            if (stmtServ != null) {
                stmtServ.close();
            }
        }
        return ret;
    }

    public static ArrayList<HistoriqueSync> getRequetteAExecuter(Connection connection) throws Exception {
        Statement stmtServ = null;
        Statement statement2 = null;
        try {
            /*HistoriqueSync hsc = new HistoriqueSync();
            hsc.setNomTable("HISTORIQUE_SYNC");
            HistoriqueSync[] listRequete = (HistoriqueSync[]) CGenUtil.rechercher(hsc, null, null, connection, " and ETAT = 1 ORDER BY id asc");
            return listRequete;*/

//          manala ireo req tsy ho-executé akory
            String req2 = "UPDATE historique_sync set etat = 8 where etat = 1 and tableconcerne in (select tableconcerne from tablenonsync)";
            statement2 = connection.createStatement();
            statement2.executeUpdate(req2);

            String req = " select * from HISTORIQUE_SYNC where etat = 1 order by daty asc, heure asc, id asc";
            stmtServ = connection.createStatement();
            ResultSet rs = stmtServ.executeQuery(req);
            return HistoriqueSync.getAll(rs);
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
            throw new Exception(e.getMessage());
        } finally {
            if (stmtServ != null) {
                stmtServ.close();
            }
            if(statement2!=null)
            {
                statement2.close();
            }
        }
    }

    public static void executeRequeteRecu(Connection connection, ArrayList<HistoriqueSync> request) throws Exception {
        Statement statement = null;
        try {
            if (request != null && request.size() > 0) {
                LogSynchro.println("INFO[]: X: " + request.size() + " requetes a execute");
                LogSynchro.println("INFO[]: Execution des requetes depuis la source...");
                //SynchroBase.insertMainRequest(connectionRow,connection,requests);
                statement = connection.createStatement();
                int count = 1;

                for (HistoriqueSync requests : request) {
                    //execution
                    String query = requests.getRequete().replace("''", "'");
                    LogSynchro.println("INFO[]: X: " + query);
                    //statement = connection.prepareStatement(query);
                    int valiny = 0;
                    try {
                        valiny = statement.executeUpdate(query);
                        //valiny = statement.executeUpdate();
                    } catch (SQLIntegrityConstraintViolationException exc) {
                        requests.setEtat(11);
                        String req = " UPDATE HISTORIQUE_SYNC set etat = 11 where id = '" + requests.getId() + "'";
                        statement.executeUpdate(req);
                        exc.printStackTrace();
                        continue;
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }

                    if (valiny > 0 || (valiny == 0 && (requests.getAction().compareToIgnoreCase("update") == 0 || requests.getAction().compareToIgnoreCase("delete") == 0))) {
                        // maj etat requette
                        requests.setEtat(11);
                        //LogSynchro.println("UPDATE []: X: " + query);

                        String req = " UPDATE HISTORIQUE_SYNC set etat = 11 where id = '" + requests.getId() + "'";
                        //statement.executeUpdate(req);
                        // statement = connection.prepareStatement(req);
                        statement.executeUpdate(req);
                        //requests.updateToTable(connection);
                        //connection.commit();
                    }
                    count++;
                    LogSynchro.println("INFO[]: X: " + count + "/" + request.size() + " requetes execute");
                }
            } else {
                LogSynchro.println("INFO[]: Aucune requete a execute pour ce serveur.");
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            connection.rollback();
            //throw exc;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static Connection GetConn(String ip, String user, String mdp) throws Exception {
        LogSynchro.println("INFO: Tentative de connexion");
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.update(mdp.getBytes());
        String encryptedString = new String(messageDigest.digest());
        //LogSynchro.println("INFO: ip remote server: "+ip);
        LogSynchro.println("INFO: [" + ip + "/User = " + user + "/Password = ***********]");
        Connection conn = null;
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Baghdad");
        TimeZone.setDefault(timeZone);
        try {
            Context jndiContext = new InitialContext();
            DataSource ds = (DataSource) jndiContext.lookup("java:jboss/datasources/CnapsDS");
            conn = ds.getConnection();
            //conn.setAutoCommit(false);
            if (conn != null) {
                LogSynchro.println("INFO: Connected\n");
            }
            return conn;
        } catch (Exception ne) {
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                conn = DriverManager.getConnection("jdbc:oracle:thin:@" + ip, user, mdp);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                throw ex;
            } catch (SQLException ex) {
                LogSynchro.println("ERROR: UtilDB Erreur Connexion : ".concat(String.valueOf(String.valueOf(ex.getMessage()))));
                throw ex;
            }
        }
        return conn;
    }
}
