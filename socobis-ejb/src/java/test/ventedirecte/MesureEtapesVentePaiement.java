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
 * Mesure precise du temps d'execution de chacune des etapes du cycle de vie de VentePaiement.
 */
public class MesureEtapesVentePaiement {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   CHRONOMETRAGE FIN DES ETAPES DU CYCLE DE VIE (VentePaiement)");
        System.out.println("   Base Oracle: 192.168.0.144:1521:EE");
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
            ResultSet rsArt = st.executeQuery("SELECT IDPRODUIT, IDPRODUITLIB, PU FROM V_ETATSTOCK_ING WHERE IDMAGASIN = '" + idMagasin + "' AND RESTE >= 50 AND ROWNUM <= 20");
            while (rsArt.next()) {
                articles.add(new String[]{rsArt.getString("IDPRODUIT"), rsArt.getString("IDPRODUITLIB"), String.valueOf(rsArt.getDouble("PU"))});
            }
            rsArt.close();
            st.close();

            // 1 Warmup
            runMeasuredSale(u.getTuppleID(), idClient, idMagasin, articles, 3, conn, false);

            // 5 iterations de mesure
            int iterations = 5;
            int nbArticles = 3;

            StepTiming totals = new StepTiming();

            System.out.println("Mesure sur " + iterations + " iterations avec " + nbArticles + " articles :\n");

            for (int it = 1; it <= iterations; it++) {
                StepTiming current = runMeasuredSale(u.getTuppleID(), idClient, idMagasin, articles, nbArticles, conn, true);
                totals.add(current);
            }

            StepTiming avg = totals.divide(iterations);

            System.out.println("\n=========================================================================================================");
            System.out.printf("%-65s | %12s | %8s%n", "ETAPE DU CYCLE DE VIE DANS VentePaiement", "DUREE MOYENNE", "% TOTAL");
            System.out.println("=========================================================================================================");
            printRow("1. Insertion batch VENTE + VENTE_DETAILS (super.createObjectBatch)", avg.t_e1, avg.t_total);
            printRow("2. Generation + Insertion batch MOUVEMENTCAISSE (mvtCaisse.createObjectBatch)", avg.t_e2, avg.t_total);
            printRow("3. Generation + Insertion batch MVTSTOCK (mvtStock.createObjectBatch)", avg.t_e3, avg.t_total);
            printRow("4. Premier Flush JDBC (st.executeBatch)", avg.t_e4, avg.t_total);
            printRow("5. Validation batch VENTE (Revient, COMPTA_ECRITURE, Previsions)", avg.t_e5, avg.t_total);
            printRow("6. Validation batch CAISSE (mvtCaisse.validerObjectBatch)", avg.t_e6, avg.t_total);
            printRow("7. Validation batch STOCK (mvtStock.validerObjectBatch)", avg.t_e7, avg.t_total);
            printRow("8. Flush final JDBC (st.executeBatch)", avg.t_e8, avg.t_total);
            printRow("9. Commit transactionnel (conn.commit)", avg.t_e9, avg.t_total);
            System.out.println("---------------------------------------------------------------------------------------------------------");
            System.out.printf("%-65s | %10.1f ms | %7.1f %%%n", "TEMPS TOTAL DE LA TRANSACTION", avg.t_total, 100.0);
            System.out.println("=========================================================================================================\n");

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

    private static void printRow(String label, double durationMs, double totalMs) {
        double pct = (durationMs / totalMs) * 100.0;
        System.out.printf("%-65s | %10.1f ms | %7.1f %%%n", label, durationMs, pct);
    }

    private static StepTiming runMeasuredSale(String refUser, String idClient, String idMagasin,
                                             List<String[]> articles, int nbArticles, Connection conn, boolean logIter) throws Exception {
        VentePaiement vp = new VentePaiement();
        vp.setNomTable("VENTE");
        vp.setIdClient(idClient);
        vp.setIdMagasin(idMagasin);
        vp.setIdMagasinVente(idMagasin);
        vp.setDaty(Utilitaire.dateDuJourSql());
        vp.setDatyPrevu(Utilitaire.dateDuJourSql());
        vp.setUserFacture(refUser);
        vp.setIdResponsable(refUser);
        vp.setDesignation("MESURE ETAPES BATCH " + nbArticles + " ARTS");

        VenteDetails[] details = new VenteDetails[nbArticles];
        double totalTTC = 0.0;

        for (int i = 0; i < nbArticles; i++) {
            String[] art = articles.get(i % articles.size());
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
            vd.setQte(0.1);
            vd.setCompte("701110");
            vd.setUnite("U");
            vd.setIdDevise("Ar");
            vd.setTauxDeChange(1.0);
            details[i] = vd;
            totalTTC += vd.getMontantTTC() * vd.getQte();
        }
        vp.setFille(details);

        // Multi-paiement : Especes + MVola
        PaiementInfo p1 = new PaiementInfo();
        p1.setModePaiement(ConstanteAsync.PAIEMENT_ESPECE);
        p1.setIdCaisse("CAIS001");
        p1.setMontant(totalTTC * 0.6);

        PaiementInfo p2 = new PaiementInfo();
        p2.setModePaiement(ConstanteAsync.PAIEMENT_MOBILEMONEY);
        p2.setIdCaisse(ConstanteAsync.caisseCategMvola);
        p2.setMontant(totalTTC * 0.4);

        vp.setPaiementInfos(new PaiementInfo[]{p1, p2});
        vp.setMontantRetourner(0.0);

        StepTiming st = new StepTiming();
        Statement stmt = conn.createStatement();

        long startAll = System.currentTimeMillis();

        // Etape 1 : Insertion batch VENTE + VENTE_DETAILS
        long t0 = System.currentTimeMillis();
        vp.superCreateObjectBatch(refUser, stmt);
        long t1 = System.currentTimeMillis();
        st.t_e1 = t1 - t0;

        // Etape 2 : Generation et insertion batch MOUVEMENTCAISSE
        MouvementCaisseMere mvtCaisse = vp.genererEncaissementBatch(refUser);
        if (mvtCaisse != null) {
            mvtCaisse.setVente(vp);
            mvtCaisse.createObjectBatch(refUser, stmt);
        }
        long t2 = System.currentTimeMillis();
        st.t_e2 = t2 - t1;

        // Etape 3 : Generation et insertion batch MVTSTOCK
        MvtStock mvtStock = vp.superGenererMvtStock(conn);
        if (mvtStock != null) {
            mvtStock.createObjectBatch(refUser, stmt);
        }
        long t3 = System.currentTimeMillis();
        st.t_e3 = t3 - t2;

        // Etape 4 : Flush initial executeBatch
        long t4_start = System.currentTimeMillis();
        stmt.executeBatch();
        long t4 = System.currentTimeMillis();
        st.t_e4 = t4 - t4_start;

        // Etape 5 : Validation batch VENTE
        long t5_start = System.currentTimeMillis();
        vp.superValiderObjectBatch(refUser, stmt);
        long t5 = System.currentTimeMillis();
        st.t_e5 = t5 - t5_start;

        // Etape 6 : Validation batch CAISSE
        long t6_start = System.currentTimeMillis();
        if (mvtCaisse != null) {
            mvtCaisse.validerObjectBatch(refUser, stmt);
        }
        long t6 = System.currentTimeMillis();
        st.t_e6 = t6 - t6_start;

        // Etape 7 : Validation batch STOCK
        long t7_start = System.currentTimeMillis();
        if (mvtStock != null) {
            mvtStock.validerObjectBatch(refUser, stmt);
        }
        long t7 = System.currentTimeMillis();
        st.t_e7 = t7 - t7_start;

        // Etape 8 : Flush final executeBatch
        long t8_start = System.currentTimeMillis();
        stmt.executeBatch();
        long t8 = System.currentTimeMillis();
        st.t_e8 = t8 - t8_start;

        // Etape 9 : Commit
        long t9_start = System.currentTimeMillis();
        conn.commit();
        long t9 = System.currentTimeMillis();
        st.t_e9 = t9 - t9_start;

        st.t_total = t9 - startAll;

        stmt.close();

        if (logIter) {
            System.out.printf("  > Iteration terminee en %d ms (Vente: %d ms, ValVente: %d ms, ValStock: %d ms, ValCaisse: %d ms)%n",
                    (long) st.t_total, (long) st.t_e1, (long) st.t_e5, (long) st.t_e7, (long) st.t_e6);
        }

        return st;
    }

    private static class StepTiming {
        double t_e1;
        double t_e2;
        double t_e3;
        double t_e4;
        double t_e5;
        double t_e6;
        double t_e7;
        double t_e8;
        double t_e9;
        double t_total;

        void add(StepTiming other) {
            this.t_e1 += other.t_e1;
            this.t_e2 += other.t_e2;
            this.t_e3 += other.t_e3;
            this.t_e4 += other.t_e4;
            this.t_e5 += other.t_e5;
            this.t_e6 += other.t_e6;
            this.t_e7 += other.t_e7;
            this.t_e8 += other.t_e8;
            this.t_e9 += other.t_e9;
            this.t_total += other.t_total;
        }

        StepTiming divide(int n) {
            StepTiming res = new StepTiming();
            res.t_e1 = this.t_e1 / n;
            res.t_e2 = this.t_e2 / n;
            res.t_e3 = this.t_e3 / n;
            res.t_e4 = this.t_e4 / n;
            res.t_e5 = this.t_e5 / n;
            res.t_e6 = this.t_e6 / n;
            res.t_e7 = this.t_e7 / n;
            res.t_e8 = this.t_e8 / n;
            res.t_e9 = this.t_e9 / n;
            res.t_total = this.t_total / n;
            return res;
        }
    }
}
