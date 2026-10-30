package test.ventedirecte;

import utilitaire.UtilDB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class VerifTablesPopulees {
    public static void main(String[] args) {
        try {
            Connection conn = new UtilDB().GetConn();
            Statement st = conn.createStatement();

            // Trouver les 3 dernieres ventes creees
            ResultSet rsV = st.executeQuery("SELECT ID, DATY, IDCLIENT, IDMAGASIN, ETAT FROM (SELECT * FROM VENTE ORDER BY ID DESC) WHERE ROWNUM <= 3");
            while (rsV.next()) {
                String idV = rsV.getString("ID");
                System.out.println("================================================================================");
                System.out.println("VENTE : " + idV + " | Date=" + rsV.getString("DATY") + " | Client=" + rsV.getString("IDCLIENT") + " | Magasin=" + rsV.getString("IDMAGASIN") + " | Etat=" + rsV.getInt("ETAT"));

                // 1. Details Vente
                Statement st1 = conn.createStatement();
                ResultSet rsD = st1.executeQuery("SELECT IDPRODUIT, QTE, PU, MONTANT, TVA FROM VENTE_DETAILS WHERE IDVENTE = '" + idV + "'");
                System.out.println("  -> VENTE_DETAILS :");
                while (rsD.next()) {
                    System.out.println("     * Prod=" + rsD.getString("IDPRODUIT") + ", Qte=" + rsD.getDouble("QTE") + ", PU=" + rsD.getDouble("PU") + ", HT=" + rsD.getDouble("MONTANT") + ", TVA=" + rsD.getDouble("TVA") + "%");
                }
                rsD.close();
                st1.close();

                // 2. Mouvement Caisse
                Statement st2 = conn.createStatement();
                ResultSet rsC = st2.executeQuery("SELECT ID, IDORIGINE, CREDIT, ETAT FROM MOUVEMENTCAISSEMERE WHERE IDORIGINE = '" + idV + "'");
                while (rsC.next()) {
                    String idCaisse = rsC.getString("ID");
                    System.out.println("  -> MOUVEMENTCAISSEMERE : " + idCaisse + " | Credit=" + rsC.getDouble("CREDIT") + " | Etat=" + rsC.getInt("ETAT"));

                    Statement st2b = conn.createStatement();
                    ResultSet rsCf = st2b.executeQuery("SELECT IDCAISSE, IDMODEPAIEMENT, CREDIT, ETAT FROM MOUVEMENTCAISSE WHERE IDMVTCAISSEMERE = '" + idCaisse + "'");
                    while (rsCf.next()) {
                        System.out.println("     * Ligne Caisse=" + rsCf.getString("IDCAISSE") + ", Mode=" + rsCf.getString("IDMODEPAIEMENT") + ", Montant=" + rsCf.getDouble("CREDIT") + ", Etat=" + rsCf.getInt("ETAT"));
                    }
                    rsCf.close();
                    st2b.close();
                }
                rsC.close();
                st2.close();

                // 3. Stock
                Statement st3 = conn.createStatement();
                ResultSet rsS = st3.executeQuery("SELECT ID, IDMAGASIN, ETAT FROM MVTSTOCK WHERE IDVENTE = '" + idV + "'");
                while (rsS.next()) {
                    String idStk = rsS.getString("ID");
                    System.out.println("  -> MVTSTOCK : " + idStk + " | Mag=" + rsS.getString("IDMAGASIN") + " | Etat=" + rsS.getInt("ETAT"));

                    Statement st3b = conn.createStatement();
                    ResultSet rsSf = st3b.executeQuery("SELECT IDPRODUIT, SORTIE, ENTREE FROM MVTSTOCKFILLE WHERE IDMVTSTOCK = '" + idStk + "'");
                    while (rsSf.next()) {
                        System.out.println("     * Article=" + rsSf.getString("IDPRODUIT") + ", Sortie=" + rsSf.getDouble("SORTIE") + ", Entree=" + rsSf.getDouble("ENTREE"));
                    }
                    rsSf.close();
                    st3b.close();
                }
                rsS.close();
                st3.close();

                // 4. Ecritures Comptables
                Statement st4 = conn.createStatement();
                ResultSet rsE = st4.executeQuery("SELECT ID, DATECOMPTABLE, DESIGNATION, ETAT FROM COMPTA_ECRITURE WHERE ORIGINE = '" + idV + "' OR IDOBJET = '" + idV + "'");
                System.out.println("  -> COMPTA_ECRITURE :");
                while (rsE.next()) {
                    String idEcr = rsE.getString("ID");
                    System.out.println("     * Ecriture " + idEcr + " | Date=" + rsE.getString("DATECOMPTABLE") + " | Desig=" + rsE.getString("DESIGNATION") + " | Etat=" + rsE.getInt("ETAT"));

                    Statement st4b = conn.createStatement();
                    ResultSet rsSe = st4b.executeQuery("SELECT COMPTE, DEBIT, CREDIT, REMARQUE FROM COMPTA_SOUS_ECRITURE WHERE IDMERE = '" + idEcr + "'");
                    while (rsSe.next()) {
                        System.out.println("       - Compte=" + rsSe.getString("COMPTE") + " | Debit=" + rsSe.getDouble("DEBIT") + " | Credit=" + rsSe.getDouble("CREDIT") + " | " + rsSe.getString("REMARQUE"));
                    }
                    rsSe.close();
                    st4b.close();
                }
                rsE.close();
                st4.close();

                // 5. Previsions
                Statement st5 = conn.createStatement();
                ResultSet rsP = st5.executeQuery("SELECT ID, COMPTE, CREDIT, DEBIT, DESIGNATION FROM PREVISION WHERE IDFACTURE = '" + idV + "' OR IDFACTUREMERE = '" + idV + "'");
                System.out.println("  -> PREVISION :");
                while (rsP.next()) {
                    System.out.println("     * Id=" + rsP.getString("ID") + " | Compte=" + rsP.getString("COMPTE") + " | Credit=" + rsP.getDouble("CREDIT") + " | " + rsP.getString("DESIGNATION"));
                }
                rsP.close();
                st5.close();
            }

            rsV.close();
            st.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
