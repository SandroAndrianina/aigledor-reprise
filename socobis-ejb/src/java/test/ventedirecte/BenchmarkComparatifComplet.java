package test.ventedirecte;

import bean.ClassMAPTable;
import historique.MapUtilisateurServiceDirection;
import user.UserEJBBean;
import utilitaire.UtilDB;
import utilitaire.Utilitaire;
import utils.ConstanteAsync;
import vente.PaiementInfo;
import vente.VenteDetails;
import vente.VentePaiement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Benchmark et Analyse Approfondie : createObject (Non-Batch) vs createObjectBatch (Batch JDBC)
 */
public class BenchmarkComparatifComplet {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   BENCHMARK & ANALYSE DE PERFORMANCE : VENTE DIRECTE APJ (ORACLE)");
        System.out.println("================================================================================\n");

        Connection conn = null;
        try {
            UtilDB utilDB = new UtilDB();
            conn = utilDB.GetConn();
            if (conn == null) {
                System.err.println("[ERREUR] Impossible de se connecter a Oracle.");
                return;
            }
            conn.setAutoCommit(false);

            UserEJBBean userEJB = new UserEJBBean();
            MapUtilisateurServiceDirection u = new MapUtilisateurServiceDirection();
            u.setRefuser(1);
            u.setRang(2);
            userEJB.setU(u);
            userEJB.setuVue(u);

            Statement st = conn.createStatement();
            String idClient = "CLI000576";
            ResultSet rsCli = st.executeQuery("SELECT ID FROM CLIENT WHERE COMPTE IS NOT NULL AND ROWNUM = 1");
            if (rsCli.next()) idClient = rsCli.getString("ID");
            rsCli.close();

            String idMagasin = "ETAB020";
            List<String[]> articles = new ArrayList<>();
            ResultSet rsArt = st.executeQuery("SELECT IDPRODUIT, IDPRODUITLIB, PU FROM V_ETATSTOCK_ING WHERE IDMAGASIN = '" + idMagasin + "' AND RESTE >= 50 AND ROWNUM <= 25");
            while (rsArt.next()) {
                articles.add(new String[]{rsArt.getString("IDPRODUIT"), rsArt.getString("IDPRODUITLIB"), String.valueOf(rsArt.getDouble("PU"))});
            }
            rsArt.close();
            st.close();

            System.out.println("[INFO] Parametres: Client=" + idClient + ", Magasin=" + idMagasin + ", Articles dispo=" + articles.size());

            // -------------------------------------------------------------
            // PARTIE 1 : BENCHMARK COMPARATIF SUR LES DML PURS (INSERTS/UPDATES)
            // -------------------------------------------------------------
            System.out.println("\n--------------------------------------------------------------------------------");
            System.out.println(" 1. BENCHMARK DML PUR : Execution individuelle (Non-Batch) vs JDBC Batch (addBatch)");
            System.out.println("    (Mesure du cout reseau par aller-retour JDBC pour N requetes DML)");
            System.out.println("--------------------------------------------------------------------------------");

            int[] testSizes = {1, 3, 5, 10};
            int iterations = 5;

            System.out.printf("%-10s | %-20s | %-16s | %-16s | %-12s%n",
                    "Articles", "DML Non-Batch (ms)", "DML Batch (ms)", "Gain Temps DML", "Facteur");
            System.out.println("--------------------------------------------------------------------------------");

            for (int nb : testSizes) {
                if (nb > articles.size()) continue;

                // Generer les commandes SQL reelles
                VentePaiement vpTest = new VentePaiement();
                vpTest.setNomTable("VENTE");
                vpTest.construirePK(conn);
                vpTest.setIdClient(idClient);
                vpTest.setIdMagasin(idMagasin);
                vpTest.setDaty(Utilitaire.dateDuJourSql());
                vpTest.setDesignation("BENCH TEST DML");
                String sqlVente = vpTest.getComandeInsert(conn);

                List<String> sqlQueries = new ArrayList<>();
                sqlQueries.add(sqlVente);
                sqlQueries.add("INSERT INTO HISTORIQUE (IDHISTORIQUE, DATEHISTORIQUE, HEURE, OBJET, ACTION, IDUTILISATEUR, REFOBJET) VALUES ('EX" + System.currentTimeMillis() + "', '02/09/2026', '12:00:00', 'Vente', 'Insert', '1', '" + vpTest.getId() + "')");

                for (int a = 0; a < nb; a++) {
                    String[] art = articles.get(a);
                    VenteDetails vd = new VenteDetails();
                    vd.setNomTable("VENTE_DETAILS");
                    vd.construirePK(conn);
                    vd.setIdVente(vpTest.getId());
                    vd.setIdProduit(art[0]);
                    vd.setDesignation(art[1]);
                    vd.setPu(Double.parseDouble(art[2]));
                    vd.setQte(1.0);
                    vd.setMontant(Double.parseDouble(art[2]));
                    sqlQueries.add(vd.getComandeInsert(conn));
                    sqlQueries.add("INSERT INTO HISTORIQUE (IDHISTORIQUE, DATEHISTORIQUE, HEURE, OBJET, ACTION, IDUTILISATEUR, REFOBJET) VALUES ('EX" + System.currentTimeMillis() + a + "', '02/09/2026', '12:00:00', 'VenteDetails', 'Insert', '1', '" + vd.getId() + "')");
                }

                // 1.1 Non-Batch : executeUpdate requete par requete
                long totalNonBatch = 0;
                for (int it = 0; it < iterations; it++) {
                    Statement stTest = conn.createStatement();
                    long t0 = System.currentTimeMillis();
                    for (String q : sqlQueries) {
                        stTest.executeUpdate(q);
                    }
                    totalNonBatch += (System.currentTimeMillis() - t0);
                    conn.rollback();
                    stTest.close();
                }
                double avgNonBatch = (double) totalNonBatch / iterations;

                // 1.2 Batch : addBatch + executeBatch unique
                long totalBatch = 0;
                for (int it = 0; it < iterations; it++) {
                    Statement stTest = conn.createStatement();
                    long t0 = System.currentTimeMillis();
                    for (String q : sqlQueries) {
                        stTest.addBatch(q);
                    }
                    stTest.executeBatch();
                    totalBatch += (System.currentTimeMillis() - t0);
                    conn.rollback();
                    stTest.close();
                }
                double avgBatch = (double) totalBatch / iterations;
                double speedup = ((avgNonBatch - avgBatch) / avgNonBatch) * 100.0;
                double factor = avgNonBatch / (avgBatch > 0 ? avgBatch : 1);

                System.out.printf("%-10d | %10.1f ms (%2d A/R) | %10.1f ms (1 A/R) | +%5.1f%%        | %4.1fx plus rapide%n",
                        nb, avgNonBatch, sqlQueries.size(), avgBatch, speedup, factor);
            }

            System.out.println("--------------------------------------------------------------------------------");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.close();
                } catch (Exception ignored) {}
            }
        }
    }
}
