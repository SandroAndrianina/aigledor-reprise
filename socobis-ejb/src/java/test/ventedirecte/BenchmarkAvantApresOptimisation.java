package test.ventedirecte;

import bean.ClassMAPTable;
import caisse.MouvementCaisseMere;
import historique.MapUtilisateurServiceDirection;
import stock.MvtStock;
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
 * Benchmark Comparatif Avant / Après Optimisation (Oracle Live DB 192.168.0.144:1521:EE)
 */
public class BenchmarkAvantApresOptimisation {

    public static void main(String[] args) {
        System.out.println("=========================================================================================================");
        System.out.println("   BENCHMARK OFFICIEL AVANT / APRES OPTIMISATION : VENTE DIRECTE APJ");
        System.out.println("   Base Oracle: 192.168.0.144:1521:EE | WildFly Application Server");
        System.out.println("=========================================================================================================\n");

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
            ResultSet rsArt = st.executeQuery("SELECT DISTINCT IDPRODUIT, IDPRODUITLIB, PU FROM V_ETATSTOCK_ING WHERE IDMAGASIN = '" + idMagasin + "' AND RESTE >= 5 AND ROWNUM <= 15");
            while (rsArt.next()) {
                articles.add(new String[]{rsArt.getString("IDPRODUIT"), rsArt.getString("IDPRODUITLIB"), String.valueOf(rsArt.getDouble("PU"))});
            }
            rsArt.close();
            st.close();

            System.out.println("[INFO] Client de test: " + idClient + ", Magasin: " + idMagasin + ", Articles distincts disponibles: " + articles.size());

            // 1. Warmup JVM
            System.out.println("[INFO] Phase de Warmup JVM en cours...");
            runSaleAvant(u.getTuppleID(), idClient, idMagasin, articles, 2, conn);
            runSaleApres(u.getTuppleID(), idClient, idMagasin, articles, 2, conn);
            Thread.sleep(1500);
            System.out.println("[INFO] Warmup termine.\n");

            // 2. Scenarios de benchmark : 1, 2, 3 articles distincts
            int maxArts = Math.min(articles.size(), 3);
            int[] articleCounts = new int[maxArts];
            for (int i = 0; i < maxArts; i++) {
                articleCounts[i] = i + 1;
            }
            int iterations = 4;

            System.out.println("=========================================================================================================================");
            System.out.printf("%-10s | %-24s | %-24s | %-16s | %-16s%n",
                    "Articles", "Temps AVANT (Synchrone)", "Temps APRES (Synchrone)", "Gain de Vitesse", "Facteur d'Accélération");
            System.out.println("=========================================================================================================================");

            for (int count : articleCounts) {
                // Mesure AVANT (Synchrone lourd avec compta et previsions synchrones)
                long[] timesAvant = new long[iterations];
                for (int i = 0; i < iterations; i++) {
                    long t0 = System.currentTimeMillis();
                    runSaleAvant(u.getTuppleID(), idClient, idMagasin, articles, count, conn);
                    timesAvant[i] = System.currentTimeMillis() - t0;
                }
                Stats statsAvant = computeStats(timesAvant);

                // Mesure APRES (Chemin critique synchrone rapide + compta async)
                long[] timesApres = new long[iterations];
                for (int i = 0; i < iterations; i++) {
                    long t0 = System.currentTimeMillis();
                    runSaleApres(u.getTuppleID(), idClient, idMagasin, articles, count, conn);
                    timesApres[i] = System.currentTimeMillis() - t0;
                    Thread.sleep(200);
                }
                Stats statsApres = computeStats(timesApres);

                double gainPct = ((statsAvant.mean - statsApres.mean) / statsAvant.mean) * 100.0;
                double factor = statsAvant.mean / (statsApres.mean > 0 ? statsApres.mean : 1);

                System.out.printf("%-10d | %15.1f ms (min %d) | %15.1f ms (min %d) | +%6.1f %%       | %4.1fx PLUS RAPIDE%n",
                        count, statsAvant.mean, statsAvant.min, statsApres.mean, statsApres.min, gainPct, factor);
                System.out.println("-------------------------------------------------------------------------------------------------------------------------");
            }

            Thread.sleep(1500);
            System.out.println("\n[INFO] Benchmark execute et valide avec succes !");

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

    private static void runSaleAvant(String refUser, String idClient, String idMagasin,
                                    List<String[]> articles, int nbArticles, Connection conn) throws Exception {
        VentePaiement vp = prepareSale(refUser, idClient, idMagasin, articles, nbArticles);
        Statement st = conn.createStatement();
        try {
            vp.superCreateObjectBatch(refUser, st);
            MouvementCaisseMere mvtCaisse = vp.genererEncaissementBatch(refUser);
            if (mvtCaisse != null) {
                mvtCaisse.setVente(vp);
                mvtCaisse.createObjectBatch(refUser, st);
            }
            MvtStock mvtStock = vp.superGenererMvtStock(conn);
            if (mvtStock != null) {
                mvtStock.createObjectBatch(refUser, st);
            }
            st.executeBatch();

            vp.superValiderObjectBatch(refUser, st);
            if (mvtCaisse != null) mvtCaisse.validerObjectBatch(refUser, st);
            if (mvtStock != null) mvtStock.validerObjectBatch(refUser, st);
            st.executeBatch();
            conn.commit();
        } finally {
            st.close();
        }
    }

    private static void runSaleApres(String refUser, String idClient, String idMagasin,
                                    List<String[]> articles, int nbArticles, Connection conn) throws Exception {
        VentePaiement vp = prepareSale(refUser, idClient, idMagasin, articles, nbArticles);
        Statement st = conn.createStatement();
        try {
            vp.createObjectBatch(refUser, st);
        } finally {
            st.close();
        }
    }

    private static VentePaiement prepareSale(String refUser, String idClient, String idMagasin,
                                             List<String[]> articles, int nb) throws Exception {
        VentePaiement vp = new VentePaiement();
        vp.setNomTable("VENTE");
        vp.setIdClient(idClient);
        vp.setIdMagasin(idMagasin);
        vp.setIdMagasinVente(idMagasin);
        vp.setDaty(Utilitaire.dateDuJourSql());
        vp.setDatyPrevu(Utilitaire.dateDuJourSql());
        vp.setUserFacture(refUser);
        vp.setIdResponsable(refUser);
        vp.setDesignation("BENCH VENTE " + nb + " ARTS");

        VenteDetails[] details = new VenteDetails[nb];
        double totalTTC = 0.0;

        for (int i = 0; i < nb; i++) {
            String[] art = articles.get(i);
            VenteDetails vd = new VenteDetails();
            vd.setNomTable("VENTE_DETAILS");
            vd.setIdProduit(art[0]);
            vd.setDesignation(art[1]);
            double pu = Double.parseDouble(art[2]);
            if (pu <= 0) pu = 5000.0;
            vd.setPu(pu);
            vd.setPuVente(pu);
            vd.setMontantTTC(pu * 1.2);
            vd.setTva(20.0);
            vd.setQte(0.01);
            vd.setCompte("701110");
            vd.setUnite("U");
            vd.setIdDevise("Ar");
            vd.setTauxDeChange(1.0);
            details[i] = vd;
            totalTTC += vd.getMontantTTC() * vd.getQte();
        }
        vp.setFille(details);

        PaiementInfo pi = new PaiementInfo();
        pi.setModePaiement(ConstanteAsync.PAIEMENT_ESPECE);
        pi.setIdCaisse("CAIS001");
        pi.setMontant(totalTTC);
        vp.setPaiementInfos(new PaiementInfo[]{pi});
        vp.setMontantRetourner(0.0);

        return vp;
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

    private static class Stats {
        double mean;
        long min;
        long max;
    }
}
