package test.ventedirecte;

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
 * Benchmark rigoureux comparant createObject (standard individuel) vs createObjectBatch (lots JDBC).
 */
public class BenchmarkVenteDirecte {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   BENCHMARK COMPARATIF: createObject vs createObjectBatch (Vente Directe)");
        System.out.println("   Environnement: Oracle Database 192.168.0.144:1521:EE");
        System.out.println("================================================================================\n");

        Connection conn = null;
        try {
            UtilDB utilDB = new UtilDB();
            conn = utilDB.GetConn();
            if (conn == null) {
                System.err.println("[ERREUR] Impossible de se connecter a la base Oracle.");
                return;
            }
            conn.setAutoCommit(false);

            // 1. Initialiser l'utilisateur EJB
            UserEJBBean userEJB = new UserEJBBean();
            MapUtilisateurServiceDirection u = new MapUtilisateurServiceDirection();
            u.setRefuser(1);
            u.setRang(2);
            userEJB.setU(u);
            userEJB.setuVue(u);

            // 2. Charger des donnees de reference
            Statement st = conn.createStatement();
            String idClient = "CLI000576";
            ResultSet rsCli = st.executeQuery("SELECT ID FROM CLIENT WHERE COMPTE IS NOT NULL AND ROWNUM = 1");
            if (rsCli.next()) {
                idClient = rsCli.getString("ID");
            }
            rsCli.close();

            String idMagasin = "ETAB020";
            List<ArticleRef> articles = new ArrayList<>();
            ResultSet rsArt = st.executeQuery("SELECT IDPRODUIT, IDPRODUITLIB, PU, RESTE FROM V_ETATSTOCK_ING WHERE IDMAGASIN = '" + idMagasin + "' AND RESTE >= 50 ORDER BY RESTE DESC");
            while (rsArt.next() && articles.size() < 25) {
                ArticleRef a = new ArticleRef();
                a.id = rsArt.getString("IDPRODUIT");
                a.libelle = rsArt.getString("IDPRODUITLIB");
                a.pu = rsArt.getDouble("PU");
                if (a.pu <= 0) a.pu = 5000.0;
                a.puTva = a.pu * 1.2;
                a.unite = "U";
                a.compte = "701110";
                articles.add(a);
            }
            rsArt.close();
            st.close();

            if (articles.isEmpty()) {
                System.err.println("[ERREUR] Aucun article trouve dans V_ETATSTOCK_ING pour " + idMagasin);
                return;
            }
            System.out.println("[INFO] Client: " + idClient + ", Magasin: " + idMagasin + ", Articles disponibles: " + articles.size());

            // 3. Warmup JVM (1 iteration)
            System.out.println("[INFO] Phase de Warmup JVM en cours...");
            runSale(userEJB, idClient, idMagasin, articles, 2, true, conn);
            runSale(userEJB, idClient, idMagasin, articles, 2, false, conn);
            System.out.println("[INFO] Warmup termine.\n");

            // 4. Scenarios de benchmark : 1, 3, 5, 10 articles
            int[] articleCounts = {1, 3, 5, 10};
            int iterations = 3;

            System.out.println("=========================================================================================================");
            System.out.printf("%-10s | %-12s | %-16s | %-22s | %-20s%n",
                    "Articles", "Mode", "Moyenne (ms)", "Min / Max (ms)", "Performance");
            System.out.println("=========================================================================================================");

            for (int count : articleCounts) {
                if (count > articles.size()) continue;

                // Test Non-Batch (createObject classique individuel)
                long[] timesNonBatch = new long[iterations];
                for (int i = 0; i < iterations; i++) {
                    long t0 = System.currentTimeMillis();
                    runSale(userEJB, idClient, idMagasin, articles, count, false, conn);
                    timesNonBatch[i] = System.currentTimeMillis() - t0;
                }
                Stats statsNonBatch = computeStats(timesNonBatch);

                // Test Batch (createObjectBatch avec JDBC batches)
                long[] timesBatch = new long[iterations];
                for (int i = 0; i < iterations; i++) {
                    long t0 = System.currentTimeMillis();
                    runSale(userEJB, idClient, idMagasin, articles, count, true, conn);
                    timesBatch[i] = System.currentTimeMillis() - t0;
                }
                Stats statsBatch = computeStats(timesBatch);

                double speedup = ((statsNonBatch.mean - statsBatch.mean) / statsNonBatch.mean) * 100.0;
                double factor = statsNonBatch.mean / statsBatch.mean;

                System.out.printf("%-10d | %-12s | %13.1f ms | %7d / %-7d ms | Reference%n",
                        count, "Standard", statsNonBatch.mean, statsNonBatch.min, statsNonBatch.max);
                System.out.printf("%-10d | %-12s | %13.1f ms | %7d / %-7d ms | +%.1f%% (%.2fx plus rapide)%n",
                        count, "BATCH", statsBatch.mean, statsBatch.min, statsBatch.max, speedup, factor);
                System.out.println("---------------------------------------------------------------------------------------------------------");
            }

            System.out.println("\n[INFO] Benchmark execute avec succes.");

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

    private static void runSale(UserEJBBean userEJB, String idClient, String idMagasin,
                                List<ArticleRef> articles, int nbArticles, boolean isBatch, Connection conn) throws Exception {
        VentePaiement vp = new VentePaiement();
        vp.setNomTable("VENTE");
        vp.setIdClient(idClient);
        vp.setIdMagasin(idMagasin);
        vp.setIdMagasinVente(idMagasin);
        vp.setDaty(Utilitaire.dateDuJourSql());
        vp.setDatyPrevu(Utilitaire.dateDuJourSql());
        vp.setDesignation("BENCHMARK VENTE " + (isBatch ? "BATCH" : "STD") + " " + nbArticles + " ARTS");

        VenteDetails[] details = new VenteDetails[nbArticles];
        double totalTTC = 0.0;

        for (int i = 0; i < nbArticles; i++) {
            ArticleRef art = articles.get(i % articles.size());
            VenteDetails vd = new VenteDetails();
            vd.setNomTable("VENTE_DETAILS");
            vd.setIdProduit(art.id);
            vd.setDesignation(art.libelle);
            vd.setPu(art.pu);
            vd.setPuVente(art.pu);
            vd.setMontantTTC(art.puTva > 0 ? art.puTva : art.pu * 1.2);
            vd.setTva(20.0);
            vd.setQte(0.1); // Petite quantite pour ne pas epuiser le stock de test
            vd.setCompte(art.compte != null ? art.compte : "701110");
            vd.setUnite(art.unite != null ? art.unite : "U");
            vd.setIdDevise("Ar");
            vd.setTauxDeChange(1.0);
            details[i] = vd;
            totalTTC += vd.getMontantTTC() * vd.getQte();
        }

        vp.setFille(details);

        // Paiement Especes
        PaiementInfo pi = new PaiementInfo();
        pi.setModePaiement(ConstanteAsync.PAIEMENT_ESPECE);
        pi.setIdCaisse("CAIS001");
        pi.setMontant(totalTTC);
        vp.setPaiementInfos(new PaiementInfo[]{pi});
        vp.setMontantRetourner(0.0);

        if (isBatch) {
            // Execution Batch
            userEJB.createObjectMultipleBatch(vp, "idVente", details);
        } else {
            // Execution Standard non-batch
            userEJB.createObjectMultiple(vp, "idVente", details);
        }
    }

    private static Stats computeStats(long[] times) {
        Stats s = new Stats();
        s.min = Long.MAX_VALUE;
        s.max = Long.MIN_VALUE;
        long sum = 0;
        for (long t : times) {
            if (t < s.min) s.min = t;
            if (t > s.max) s.max = t;
            sum += t;
        }
        s.mean = (double) sum / times.length;
        return s;
    }

    private static class ArticleRef {
        String id;
        String libelle;
        double pu;
        double puTva;
        String unite;
        String compte;
    }

    private static class Stats {
        double mean;
        long min;
        long max;
    }
}
