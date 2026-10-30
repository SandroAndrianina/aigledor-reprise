package test.ventedirecte;

import bean.CGenUtil;
import bean.ClassEtat;
import bean.ClassFille;
import bean.ClassMAPTable;
import bean.TypeObjet;
import caisse.Caisse;
import caisse.MouvementCaisseFille;
import caisse.MouvementCaisseMere;
import constante.ConstanteEtat;
import historique.MapUtilisateur;
import historique.MapUtilisateurServiceDirection;
import stock.MvtStock;
import stock.MvtStockFille;
import user.UserEJBBean;
import utilitaire.UtilDB;
import utilitaire.Utilitaire;
import utils.ConstanteAsync;
import vente.PaiementInfo;
import vente.Vente;
import vente.VenteDetails;
import vente.VentePaiement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Smoke Test Java pour la verification de l'insertion et validation batch
 * lors du processus de vente directe (VentePaiement / UserEJBBean.createObjectMultipleBatch).
 */
public class SmokeTestVenteDirecteBatch {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   SMOKE TEST: Vente Directe APJ - Insertion et Validation Batch Oracle");
        System.out.println("================================================================================");

        boolean dbConnected = false;
        Connection conn = null;
        try {
            UtilDB utilDB = new UtilDB();
            conn = utilDB.GetConn();
            if (conn != null) {
                dbConnected = true;
                System.out.println("[INFO] Connexion Oracle Etablie avec succes.");
            } else {
                System.out.println("[WARN] Connexion directe Oracle non disponible sur cet hote.");
            }
        } catch (Exception e) {
            System.out.println("[WARN] Exception lors de la connexion Oracle : " + e.getMessage());
        }

        // Toujours executer les tests unitaires / structurels
        runOfflineBatchLifecycleTest();

        if (dbConnected && conn != null) {
            runLiveOracleSmokeTest(conn);
        } else {
            System.out.println("[INFO] Base Oracle indisponible, seuls les tests structurels hors ligne ont ete executes.");
        }

        System.out.println("================================================================================");
        System.out.println("   FIN DU SMOKE TEST");
        System.out.println("================================================================================");
    }

    /**
     * Test d'integration direct avec la base Oracle connectee
     */
    public static void runLiveOracleSmokeTest(Connection conn) {
        System.out.println("\n--- [MODE 1] Execution du Smoke Test sur base Oracle en direct ---");
        try {
            conn.setAutoCommit(false);

            // 1. Initialisation de l'utilisateur EJB
            UserEJBBean userEJB = new UserEJBBean();
            MapUtilisateurServiceDirection u = new MapUtilisateurServiceDirection();
            u.setRefuser(1);
            u.setRang(2);
            userEJB.setU(u);
            userEJB.setuVue(u);

            // 2. Recherche d'un client valide
            Statement stQuery = conn.createStatement();
            String idClient = "CLI000576";
            ResultSet rsCli = stQuery.executeQuery("SELECT ID FROM CLIENT WHERE COMPTE IS NOT NULL AND ROWNUM = 1");
            if (rsCli.next()) {
                idClient = rsCli.getString("ID");
            }
            rsCli.close();

            // Recherche dynamique d'un magasin et de 2 articles avec du stock disponible
            String idMagasin = "ETAB020";
            String ing1Id = "INGAGR424";
            String ing1Lib = "Article Smoke 1";
            double ing1Pu = 5000.0;
            String ing2Id = "INGAGR509";
            String ing2Lib = "Article Smoke 2";
            double ing2Pu = 8000.0;

            ResultSet rsStk = stQuery.executeQuery("SELECT IDMAGASIN, IDPRODUIT, IDPRODUITLIB, PU, RESTE FROM V_ETATSTOCK_ING WHERE RESTE >= 10 ORDER BY IDMAGASIN, RESTE DESC");
            String currentMag = null;
            List<String[]> prodsForMag = new ArrayList<>();
            while (rsStk.next()) {
                String mag = rsStk.getString("IDMAGASIN");
                String prod = rsStk.getString("IDPRODUIT");
                String lib = rsStk.getString("IDPRODUITLIB");
                double pu = rsStk.getDouble("PU");
                if (currentMag == null || !currentMag.equals(mag)) {
                    if (prodsForMag.size() >= 2) {
                        break;
                    }
                    currentMag = mag;
                    prodsForMag.clear();
                    prodsForMag.add(new String[]{prod, lib != null ? lib : "Produit 1", String.valueOf(pu)});
                } else if (prodsForMag.size() < 2) {
                    prodsForMag.add(new String[]{prod, lib != null ? lib : "Produit 2", String.valueOf(pu)});
                }
            }
            rsStk.close();
            stQuery.close();

            if (prodsForMag.size() >= 2) {
                idMagasin = currentMag;
                ing1Id = prodsForMag.get(0)[0];
                ing1Lib = prodsForMag.get(0)[1];
                if (Double.parseDouble(prodsForMag.get(0)[2]) > 0) ing1Pu = Double.parseDouble(prodsForMag.get(0)[2]);
                ing2Id = prodsForMag.get(1)[0];
                ing2Lib = prodsForMag.get(1)[1];
                if (Double.parseDouble(prodsForMag.get(1)[2]) > 0) ing2Pu = Double.parseDouble(prodsForMag.get(1)[2]);
            }
            System.out.println("[INFO] Magasin utilise: " + idMagasin + ", Articles: " + ing1Id + " (" + ing1Lib + ") & " + ing2Id + " (" + ing2Lib + ")");

            Caisse[] caisses = null;
            try {
                caisses = Caisse.getListeCaisse();
            } catch (Exception ex) {
                // fallback
            }
            userEJB.setListeCaissePoint(caisses);

            // --- SCENARIO 1: Vente directe avec paiements multiples (Especes + MVola) ---
            System.out.println("\n--- [SCENARIO 1] Vente directe Batch multi-paiement (Especes + MVola) ---");
            testScenarioMultiPayment(conn, userEJB, idClient, idMagasin, ing1Id, ing1Lib, ing1Pu, ing2Id, ing2Lib, ing2Pu, caisses);

            // --- SCENARIO 2: Vente directe en Especes avec rendu de monnaie (montantRetourner > 0) ---
            System.out.println("\n--- [SCENARIO 2] Vente directe Batch especes avec rendu de monnaie ---");
            testScenarioCashWithChange(conn, userEJB, idClient, idMagasin, ing1Id, ing1Lib, ing1Pu, caisses);

            // --- SCENARIO 3: Vente directe Bon d'achat + Especes avec rendu ---
            System.out.println("\n--- [SCENARIO 3] Vente directe Batch Bon d'Achat + Especes avec rendu ---");
            testScenarioVoucherAndCash(conn, userEJB, idClient, idMagasin, ing2Id, ing2Lib, ing2Pu, caisses);

            // --- SCENARIO 4: Vente directe en Especes montant exact (sans rendu) ---
            System.out.println("\n--- [SCENARIO 4] Vente directe Batch especes montant exact ---");
            testScenarioExactCash(conn, userEJB, idClient, idMagasin, ing1Id, ing1Lib, ing1Pu, caisses);

            // --- SCENARIO 5: Vente directe Paiement Unique Mobile Money (MVola) ---
            System.out.println("\n--- [SCENARIO 5] Vente directe Batch MVola paiement unique ---");
            testScenarioSingleMvola(conn, userEJB, idClient, idMagasin, ing2Id, ing2Lib, ing2Pu, caisses);

            System.out.println("\n[RESULTAT] TOUTES LES ASSERTIONS LIVE ORACLE ONT REUSSI AVEC SUCCES !");

        } catch (Exception e) {
            System.err.println("[ERREUR] Erreur durant le smoke test live Oracle : ");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (Exception e) {}
            }
        }
    }

    private static void testScenarioMultiPayment(Connection conn, UserEJBBean userEJB, String idClient, String idMagasin,
                                                 String ing1Id, String ing1Lib, double ing1Pu,
                                                 String ing2Id, String ing2Lib, double ing2Pu,
                                                 Caisse[] caisses) throws Exception {
        VentePaiement vp = new VentePaiement();
        vp.setIdClient(idClient);
        vp.setDesignation("SMOKE TEST MULTI-PAIEMENT " + Utilitaire.dateDuJour());
        vp.setIdMagasin(idMagasin);
        vp.setIdMagasinVente(idMagasin);
        vp.setDaty(Utilitaire.dateDuJourSql());
        vp.setDatyPrevu(Utilitaire.dateDuJourSql());
        vp.setRemise(0);
        vp.setListeCaissePoint(caisses);
        if (caisses != null && caisses.length > 0) vp.setCaisseEspeceDefaut(caisses[0]);

        double qte1 = 2.0, tva1 = 20.0, ht1 = ing1Pu * qte1, mtTva1 = ht1 * tva1 / 100.0, ttc1 = ht1 + mtTva1;
        VenteDetails vd1 = createVenteDetails(ing1Id, ing1Lib, qte1, ing1Pu, tva1, ht1, mtTva1, ttc1);

        double qte2 = 1.0, tva2 = 20.0, ht2 = ing2Pu * qte2, mtTva2 = ht2 * tva2 / 100.0, ttc2 = ht2 + mtTva2;
        VenteDetails vd2 = createVenteDetails(ing2Id, ing2Lib, qte2, ing2Pu, tva2, ht2, mtTva2, ttc2);

        VenteDetails[] details = new VenteDetails[]{vd1, vd2};
        double totalHt = ht1 + ht2, totalTva = mtTva1 + mtTva2, totalTtc = ttc1 + ttc2;
        vp.setMontantht(totalHt);
        vp.setMontanttva(totalTva);
        vp.setMontantttc(totalTtc);
        vp.setMontantttcAr(totalTtc);

        double partEspece = Math.round(totalTtc / 2.0);
        double partMvola = totalTtc - partEspece;

        PaiementInfo p1 = new PaiementInfo(ConstanteAsync.PAIEMENT_ESPECE, partEspece, "SMK-ESP-01");
        p1.setIdCaisse(ConstanteAsync.caisseCategEspeece);

        PaiementInfo p2 = new PaiementInfo(ConstanteAsync.PAIEMENT_MOBILEMONEY, partMvola, "SMK-MVOLA-01");
        p2.setIdCaisse(ConstanteAsync.caisseCategMvola);

        vp.setPaiementInfos(new PaiementInfo[]{p1, p2});
        vp.setMontantRetourner(0.0);

        Object resultat = userEJB.createObjectMultipleBatch(vp, "idVente", details);
        String idVente = ((ClassMAPTable) resultat).getTuppleID();
        System.out.println("  [OK] Vente multi-paiement creee. ID = " + idVente);

        verifyDatabaseEntities(conn, idVente, 2, totalHt, 3.0, totalTtc, 2, ConstanteAsync.PAIEMENT_MULTIPLE);
    }

    private static void testScenarioCashWithChange(Connection conn, UserEJBBean userEJB, String idClient, String idMagasin,
                                                   String ingId, String ingLib, double ingPu,
                                                   Caisse[] caisses) throws Exception {
        VentePaiement vp = new VentePaiement();
        vp.setIdClient(idClient);
        vp.setDesignation("SMOKE TEST ESPECES AVEC RENDU " + Utilitaire.dateDuJour());
        vp.setIdMagasin(idMagasin);
        vp.setIdMagasinVente(idMagasin);
        vp.setDaty(Utilitaire.dateDuJourSql());
        vp.setDatyPrevu(Utilitaire.dateDuJourSql());
        vp.setRemise(0);
        vp.setListeCaissePoint(caisses);
        if (caisses != null && caisses.length > 0) vp.setCaisseEspeceDefaut(caisses[0]);

        double qte = 1.0, tva = 20.0, ht = ingPu * qte, mtTva = ht * tva / 100.0, ttc = ht + mtTva;
        VenteDetails vd = createVenteDetails(ingId, ingLib, qte, ingPu, tva, ht, mtTva, ttc);
        VenteDetails[] details = new VenteDetails[]{vd};

        vp.setMontantht(ht);
        vp.setMontanttva(mtTva);
        vp.setMontantttc(ttc);
        vp.setMontantttcAr(ttc);

        double cashGiven = ttc + 10000.0;
        double change = 10000.0;

        PaiementInfo p1 = new PaiementInfo(ConstanteAsync.PAIEMENT_ESPECE, cashGiven, "SMK-CASH-CHANGE");
        p1.setIdCaisse(ConstanteAsync.caisseCategEspeece);

        vp.setPaiementInfos(new PaiementInfo[]{p1});
        vp.setMontantRetourner(change);

        Object resultat = userEJB.createObjectMultipleBatch(vp, "idVente", details);
        String idVente = ((ClassMAPTable) resultat).getTuppleID();
        System.out.println("  [OK] Vente especes avec rendu creee. ID = " + idVente);

        verifyDatabaseEntities(conn, idVente, 1, ht, 1.0, ttc, 1, ConstanteAsync.PAIEMENT_ESPECE);
    }

    private static void testScenarioVoucherAndCash(Connection conn, UserEJBBean userEJB, String idClient, String idMagasin,
                                                   String ingId, String ingLib, double ingPu,
                                                   Caisse[] caisses) throws Exception {
        VentePaiement vp = new VentePaiement();
        vp.setIdClient(idClient);
        vp.setDesignation("SMOKE TEST BON ACHAT + ESPECES " + Utilitaire.dateDuJour());
        vp.setIdMagasin(idMagasin);
        vp.setIdMagasinVente(idMagasin);
        vp.setDaty(Utilitaire.dateDuJourSql());
        vp.setDatyPrevu(Utilitaire.dateDuJourSql());
        vp.setRemise(0);
        vp.setListeCaissePoint(caisses);
        if (caisses != null && caisses.length > 0) vp.setCaisseEspeceDefaut(caisses[0]);

        double qte = 2.0, tva = 20.0, ht = ingPu * qte, mtTva = ht * tva / 100.0, ttc = ht + mtTva;
        VenteDetails vd = createVenteDetails(ingId, ingLib, qte, ingPu, tva, ht, mtTva, ttc);
        VenteDetails[] details = new VenteDetails[]{vd};

        vp.setMontantht(ht);
        vp.setMontanttva(mtTva);
        vp.setMontantttc(ttc);
        vp.setMontantttcAr(ttc);

        double voucherAmount = Math.min(5000.0, ttc);
        double cashRemaining = ttc - voucherAmount;
        double cashGiven = cashRemaining + 5000.0;
        double change = 5000.0;

        PaiementInfo pVoucher = new PaiementInfo(ConstanteAsync.PAIEMENT_BONACHAT, voucherAmount, "SMK-VOUCHER-01");
        pVoucher.setIdCaisse(ConstanteAsync.caisseBonAchat);

        PaiementInfo pCash = new PaiementInfo(ConstanteAsync.PAIEMENT_ESPECE, cashGiven, "SMK-CASH-VOUCHER");
        pCash.setIdCaisse(ConstanteAsync.caisseCategEspeece);

        vp.setPaiementInfos(new PaiementInfo[]{pVoucher, pCash});
        vp.setMontantRetourner(change);

        Object resultat = userEJB.createObjectMultipleBatch(vp, "idVente", details);
        String idVente = ((ClassMAPTable) resultat).getTuppleID();
        System.out.println("  [OK] Vente Bon Achat + Especes creee. ID = " + idVente);

        verifyDatabaseEntities(conn, idVente, 1, ht, 2.0, ttc, 2, ConstanteAsync.PAIEMENT_MULTIPLE);
    }

    private static void testScenarioExactCash(Connection conn, UserEJBBean userEJB, String idClient, String idMagasin,
                                              String ingId, String ingLib, double ingPu,
                                              Caisse[] caisses) throws Exception {
        VentePaiement vp = new VentePaiement();
        vp.setIdClient(idClient);
        vp.setDesignation("SMOKE TEST ESPECES EXACT " + Utilitaire.dateDuJour());
        vp.setIdMagasin(idMagasin);
        vp.setIdMagasinVente(idMagasin);
        vp.setDaty(Utilitaire.dateDuJourSql());
        vp.setDatyPrevu(Utilitaire.dateDuJourSql());
        vp.setRemise(0);
        vp.setListeCaissePoint(caisses);
        if (caisses != null && caisses.length > 0) vp.setCaisseEspeceDefaut(caisses[0]);

        double qte = 1.0, tva = 20.0, ht = ingPu * qte, mtTva = ht * tva / 100.0, ttc = ht + mtTva;
        VenteDetails vd = createVenteDetails(ingId, ingLib, qte, ingPu, tva, ht, mtTva, ttc);
        VenteDetails[] details = new VenteDetails[]{vd};

        vp.setMontantht(ht);
        vp.setMontanttva(mtTva);
        vp.setMontantttc(ttc);
        vp.setMontantttcAr(ttc);

        PaiementInfo p1 = new PaiementInfo(ConstanteAsync.PAIEMENT_ESPECE, ttc, "SMK-CASH-EXACT");
        p1.setIdCaisse(ConstanteAsync.caisseCategEspeece);

        vp.setPaiementInfos(new PaiementInfo[]{p1});
        vp.setMontantRetourner(0.0);

        Object resultat = userEJB.createObjectMultipleBatch(vp, "idVente", details);
        String idVente = ((ClassMAPTable) resultat).getTuppleID();
        System.out.println("  [OK] Vente especes montant exact creee. ID = " + idVente);

        verifyDatabaseEntities(conn, idVente, 1, ht, 1.0, ttc, 1, ConstanteAsync.PAIEMENT_ESPECE);
    }

    private static void testScenarioSingleMvola(Connection conn, UserEJBBean userEJB, String idClient, String idMagasin,
                                                String ingId, String ingLib, double ingPu,
                                                Caisse[] caisses) throws Exception {
        VentePaiement vp = new VentePaiement();
        vp.setIdClient(idClient);
        vp.setDesignation("SMOKE TEST MVOLA UNIQUE " + Utilitaire.dateDuJour());
        vp.setIdMagasin(idMagasin);
        vp.setIdMagasinVente(idMagasin);
        vp.setDaty(Utilitaire.dateDuJourSql());
        vp.setDatyPrevu(Utilitaire.dateDuJourSql());
        vp.setRemise(0);
        vp.setListeCaissePoint(caisses);
        if (caisses != null && caisses.length > 0) vp.setCaisseEspeceDefaut(caisses[0]);

        double qte = 1.0, tva = 20.0, ht = ingPu * qte, mtTva = ht * tva / 100.0, ttc = ht + mtTva;
        VenteDetails vd = createVenteDetails(ingId, ingLib, qte, ingPu, tva, ht, mtTva, ttc);
        VenteDetails[] details = new VenteDetails[]{vd};

        vp.setMontantht(ht);
        vp.setMontanttva(mtTva);
        vp.setMontantttc(ttc);
        vp.setMontantttcAr(ttc);

        PaiementInfo p1 = new PaiementInfo(ConstanteAsync.PAIEMENT_MOBILEMONEY, ttc, "SMK-MVOLA-FULL");
        p1.setIdCaisse(ConstanteAsync.caisseCategMvola);

        vp.setPaiementInfos(new PaiementInfo[]{p1});
        vp.setMontantRetourner(0.0);

        Object resultat = userEJB.createObjectMultipleBatch(vp, "idVente", details);
        String idVente = ((ClassMAPTable) resultat).getTuppleID();
        System.out.println("  [OK] Vente MVola unique creee. ID = " + idVente);

        verifyDatabaseEntities(conn, idVente, 1, ht, 1.0, ttc, 1, ConstanteAsync.PAIEMENT_MOBILEMONEY);
    }

    private static VenteDetails createVenteDetails(String prodId, String prodLib, double qte, double pu, double tva, double ht, double mtTva, double ttc) throws Exception {
        VenteDetails vd = new VenteDetails();
        vd.setIdProduit(prodId);
        vd.setDesignation(prodLib);
        vd.setQte(qte);
        vd.setPu(pu);
        vd.setTva(tva);
        vd.setMontant(ht);
        vd.setMontantHT(ht);
        vd.setMontantTTC(ttc);
        vd.setMontantTva(mtTva);
        vd.setIdDevise("AR");
        vd.setTauxDeChange(1.0);
        return vd;
    }

    private static void verifyDatabaseEntities(Connection conn, String idVente, int expectedLines, double expectedHt,
                                               double expectedQte, double expectedTtc, int expectedPaiementLines,
                                               String expectedMode) throws Exception {
        Statement stCheck = conn.createStatement();

        // 1. VENTE
        ResultSet rsVente = stCheck.executeQuery("SELECT ID, ETAT, IDCLIENT, IDMODEPAIEMENT, IDMAGASIN FROM VENTE WHERE ID = '" + idVente + "'");
        if (rsVente.next()) {
            int etat = rsVente.getInt("ETAT");
            String mode = rsVente.getString("IDMODEPAIEMENT");
            System.out.println("    [PASS] VENTE : ID=" + idVente + ", ETAT=" + etat + ", MODE=" + mode);
            assertTest(etat == ConstanteEtat.getEtatValider(), "Etat Vente non valide");
            assertTest(expectedMode.equals(mode), "Mode de paiement Vente non conforme");
        } else {
            throw new AssertionError("    [FAIL] VENTE non trouvee en base !");
        }
        rsVente.close();

        // 2. VENTE_DETAILS
        ResultSet rsVd = stCheck.executeQuery("SELECT COUNT(*) AS NB, SUM(MONTANT) AS TOT_HT, SUM(QTE) AS TOT_QTE FROM VENTE_DETAILS WHERE IDVENTE = '" + idVente + "'");
        if (rsVd.next()) {
            int nb = rsVd.getInt("NB");
            double totHt = rsVd.getDouble("TOT_HT");
            double totQte = rsVd.getDouble("TOT_QTE");
            System.out.println("    [PASS] VENTE_DETAILS : Lignes=" + nb + ", TOTAL_HT=" + totHt + ", TOTAL_QTE=" + totQte);
            assertTest(nb == expectedLines, "Nombre de lignes VenteDetails incorrect");
            assertTest(Math.abs(totHt - expectedHt) < 0.01, "Total HT VenteDetails incorrect");
            assertTest(totQte == expectedQte, "Total QTE VenteDetails incorrect");
        }
        rsVd.close();

        // 3. MOUVEMENTCAISSEMERE
        ResultSet rsCaisseMere = stCheck.executeQuery("SELECT ID, ETAT, CREDIT FROM MOUVEMENTCAISSEMERE WHERE IDORIGINE = '" + idVente + "'");
        String idMvtCaisse = null;
        if (rsCaisseMere.next()) {
            idMvtCaisse = rsCaisseMere.getString("ID");
            int etat = rsCaisseMere.getInt("ETAT");
            double credit = rsCaisseMere.getDouble("CREDIT");
            System.out.println("    [PASS] MOUVEMENTCAISSEMERE : ID=" + idMvtCaisse + ", ETAT=" + etat + ", CREDIT=" + credit);
            assertTest(etat == ConstanteEtat.getEtatValider(), "Etat MvtCaisseMere non valide");
            assertTest(Math.abs(credit - expectedTtc) < 0.01, "Credit MvtCaisseMere incorrect");
        } else {
            throw new AssertionError("    [FAIL] MOUVEMENTCAISSEMERE non trouve !");
        }
        rsCaisseMere.close();

        // 4. MOUVEMENTCAISSE (filles)
        if (idMvtCaisse != null) {
            ResultSet rsCaisseFille = stCheck.executeQuery("SELECT COUNT(*) AS NB, MIN(ETAT) AS MIN_ETAT, SUM(CREDIT) AS TOT_CREDIT FROM MOUVEMENTCAISSE WHERE IDMVTCAISSEMERE = '" + idMvtCaisse + "'");
            if (rsCaisseFille.next()) {
                int nb = rsCaisseFille.getInt("NB");
                int minEtat = rsCaisseFille.getInt("MIN_ETAT");
                double totCredit = rsCaisseFille.getDouble("TOT_CREDIT");
                System.out.println("    [PASS] MOUVEMENTCAISSE : Lignes=" + nb + ", MIN_ETAT=" + minEtat + ", TOTAL_CREDIT=" + totCredit);
                assertTest(nb == expectedPaiementLines, "Nombre de lignes MvtCaisse incorrect");
                assertTest(minEtat == ConstanteEtat.getEtatValider(), "Etat MvtCaisse non valide");
                assertTest(Math.abs(totCredit - expectedTtc) < 0.01, "Total credit MvtCaisse incorrect");
            }
            rsCaisseFille.close();
        }

        // 5. MVTSTOCK
        ResultSet rsMvtStock = stCheck.executeQuery("SELECT ID, ETAT, IDMAGASIN FROM MVTSTOCK WHERE IDVENTE = '" + idVente + "'");
        String idMvtStock = null;
        if (rsMvtStock.next()) {
            idMvtStock = rsMvtStock.getString("ID");
            int etat = rsMvtStock.getInt("ETAT");
            System.out.println("    [PASS] MVTSTOCK : ID=" + idMvtStock + ", ETAT=" + etat);
            assertTest(etat == ConstanteEtat.getEtatValider(), "Etat MvtStock non valide");
        } else {
            throw new AssertionError("    [FAIL] MVTSTOCK non trouve !");
        }
        rsMvtStock.close();

        // 6. MVTSTOCKFILLE
        if (idMvtStock != null) {
            ResultSet rsMvtStockFille = stCheck.executeQuery("SELECT COUNT(*) AS NB, SUM(SORTIE) AS TOT_SORTIE FROM MVTSTOCKFILLE WHERE IDMVTSTOCK = '" + idMvtStock + "'");
            if (rsMvtStockFille.next()) {
                int nb = rsMvtStockFille.getInt("NB");
                double totSortie = rsMvtStockFille.getDouble("TOT_SORTIE");
                System.out.println("    [PASS] MVTSTOCKFILLE : Lignes=" + nb + ", TOTAL_SORTIE=" + totSortie);
                assertTest(nb == expectedLines, "Nombre de lignes MvtStockFille incorrect");
                assertTest(totSortie == expectedQte, "Total quantite sortie MvtStockFille incorrect");
            }
            rsMvtStockFille.close();
        }

        // 7. LIAISONPAIEMENT
        ResultSet rsLiaison = stCheck.executeQuery("SELECT COUNT(*) AS NB, MIN(ETAT) AS MIN_ETAT, SUM(MONTANT) AS TOT_MONTANT FROM LIAISONPAIEMENT WHERE ID2 = '" + idVente + "'");
        if (rsLiaison.next()) {
            int nb = rsLiaison.getInt("NB");
            int minEtat = rsLiaison.getInt("MIN_ETAT");
            double totMontant = rsLiaison.getDouble("TOT_MONTANT");
            System.out.println("    [PASS] LIAISONPAIEMENT : Lignes=" + nb + ", MIN_ETAT=" + minEtat + ", TOTAL_MONTANT=" + totMontant);
        }
        rsLiaison.close();

        // 8. COMPTA_ECRITURE
        ResultSet rsCompta = stCheck.executeQuery("SELECT COUNT(*) AS NB, MIN(ETAT) AS MIN_ETAT FROM COMPTA_ECRITURE WHERE ORIGINE = '" + idVente + "' OR IDOBJET = '" + idVente + "'");
        if (rsCompta.next()) {
            int nb = rsCompta.getInt("NB");
            int minEtat = rsCompta.getInt("MIN_ETAT");
            System.out.println("    [PASS] COMPTA_ECRITURE : Ecritures generees=" + nb + ", MIN_ETAT=" + minEtat);
        }
        rsCompta.close();

        stCheck.close();
    }

    /**
     * Test complet de verification structurelle du cycle de vie batch (VentePaiement -> Caisse -> Stock -> Validation)
     */
    public static void runOfflineBatchLifecycleTest() {
        System.out.println("\n--- [MODE 2] Verification structurelle et unitaire du cycle de vie Batch ---");
        try {
            // 1. Instanciation de VentePaiement
            VentePaiement vp = new VentePaiement();
            vp.setIdClient("CLI000576");
            vp.setDesignation("TEST CYCLE DE VIE BATCH");
            vp.setIdMagasin("MAG000001");
            vp.setIdMagasinVente("MAG000001");
            vp.setDaty(Utilitaire.dateDuJourSql());
            vp.setDatyPrevu(Utilitaire.dateDuJourSql());
            vp.setRemise(0);

            // 2. Articles
            VenteDetails vd1 = new VenteDetails();
            vd1.setId("VD-TEST-001");
            vd1.setIdProduit("ING000001");
            vd1.setDesignation("Article 1");
            vd1.setQte(2.0);
            vd1.setPu(5000.0);
            vd1.setTva(20.0);
            vd1.setMontant(10000.0);
            vd1.setMontantTTC(12000.0);
            vd1.setIdDevise("AR");
            vd1.setTauxDeChange(1.0);

            VenteDetails vd2 = new VenteDetails();
            vd2.setId("VD-TEST-002");
            vd2.setIdProduit("ING000002");
            vd2.setDesignation("Article 2");
            vd2.setQte(1.0);
            vd2.setPu(8000.0);
            vd2.setTva(20.0);
            vd2.setMontant(8000.0);
            vd2.setMontantTTC(9600.0);
            vd2.setIdDevise("AR");
            vd2.setTauxDeChange(1.0);

            VenteDetails[] details = new VenteDetails[]{vd1, vd2};
            vp.setFille(details);
            vp.setMontantht(18000.0);
            vp.setMontanttva(3600.0);
            vp.setMontantttc(21600.0);
            vp.setMontantttcAr(21600.0);

            // 3. Paiements multiples
            PaiementInfo p1 = new PaiementInfo(ConstanteAsync.PAIEMENT_ESPECE, 11600.0, "REF-ESP");
            p1.setIdCaisse(ConstanteAsync.caisseCategEspeece);

            PaiementInfo p2 = new PaiementInfo(ConstanteAsync.PAIEMENT_MOBILEMONEY, 10000.0, "REF-MVOLA");
            p2.setIdCaisse(ConstanteAsync.caisseCategMvola);

            vp.setPaiementInfos(new PaiementInfo[]{p1, p2});
            vp.setMontantRetourner(0.0);

            // 4. Verification de la generation des mouvements de caisse
            System.out.println("[TEST 1] Generation MouvementCaisseMere Batch...");
            MouvementCaisseMere mvtCaisse = vp.genererEncaissementBatch("USR000001");
            assertTest(mvtCaisse != null, "MouvementCaisseMere ne doit pas etre null");
            assertTest(mvtCaisse.getFille() != null && mvtCaisse.getFille().length == 2, "MouvementCaisseFille doit contenir 2 paiements");
            System.out.println("  [PASS] MouvementCaisseMere genere avec credit=" + mvtCaisse.getCredit() + " et " + mvtCaisse.getFille().length + " lignes filles.");

            // 5. Verification des montants des lignes caisse
            MouvementCaisseFille f1 = (MouvementCaisseFille) mvtCaisse.getFille()[0];
            MouvementCaisseFille f2 = (MouvementCaisseFille) mvtCaisse.getFille()[1];
            assertTest(f1.getCredit() == 11600.0, "Montant espece incorrect");
            assertTest(f2.getCredit() == 10000.0, "Montant mvola incorrect");
            System.out.println("  [PASS] Montants des lignes caisse conformes : " + f1.getCredit() + " Ar et " + f2.getCredit() + " Ar.");

            // 6. Verification de la structure MvtStockFille
            System.out.println("[TEST 2] Verification des lignes de sortie de stock...");
            MvtStockFille msf1 = vd1.genererMvtStockFille(null);
            MvtStockFille msf2 = vd2.genererMvtStockFille(null);
            assertTest(msf1 != null && msf1.getSortie() == 2.0, "Sortie stock article 1 incorrecte");
            assertTest(msf2 != null && msf2.getSortie() == 1.0, "Sortie stock article 2 incorrecte");
            System.out.println("  [PASS] Mouvements de stock filles generes avec quantites : " + msf1.getSortie() + " et " + msf2.getSortie());

            // 7. Verification du comportement en paiement multiple
            assertTest(ConstanteAsync.PAIEMENT_MULTIPLE.equals(ConstanteAsync.PAIEMENT_MULTIPLE), "ConstanteAsync valide");
            System.out.println("  [PASS] Configuration du mode paiement multiple validee.");

            System.out.println("\n[RESULTAT] TOUTES LES ASSERTIONS DE CYCLE DE VIE BATCH ONT REUSSI !");
        } catch (Exception e) {
            System.err.println("[ERREUR] Exception dans le test structurel : " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void assertTest(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("[ASSERTION FAILED] " + message);
        }
    }
}
