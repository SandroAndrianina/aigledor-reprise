/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caisse;

import bean.ClassMAPTable;
import java.sql.Date;
import java.time.LocalDate;

import utilitaire.Utilitaire;
import bean.ResultatEtSomme;
import bean.CGenUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import utilitaire.UtilDB;
import utilitaire.UtilitaireAsync;

/**
 *
 * @author 26134
 */
public class EtatCaisse extends ClassMAPTable{
    String id,idCaisse,idCaisselib,idPoint,idPointlib,idTypeCaisse,idTypeCaisselib;
    Date dateDernierReport,daty;
    double montantDernierReport,credit,debit,reste,solde,resteAr,taux;

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public double getTaux() {
        return taux;
    }

    public void setTaux(double taux) {
        this.taux = taux;
    }

    public double getResteAr() {
        return resteAr;
    }

    public void setResteAr(double resteAr) {
        this.resteAr = resteAr;
    }

    public EtatCaisse() {
        this.setNomTable("v_Etatcaisse");
    }

    public double getReste() {
        return reste;
    }

    public void setReste(double reste) {
        this.reste = reste;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCaisse() {
        return idCaisse;
    }

    public void setIdCaisse(String idCaisse) {
        this.idCaisse = idCaisse;
    }

    public String getIdCaisselib() {
        return idCaisselib;
    }

    public void setIdCaisselib(String idCaisselib) {
        this.idCaisselib = idCaisselib;
    }

    public String getIdPoint() {
        return idPoint;
    }

    public void setIdPoint(String idPoint) {
        this.idPoint = idPoint;
    }

    public String getIdPointlib() {
        return idPointlib;
    }

    public void setIdPointlib(String idPointlib) {
        this.idPointlib = idPointlib;
    }

    public String getIdTypeCaisse() {
        return idTypeCaisse;
    }

    public void setIdTypeCaisse(String idTypeCaisse) {
        this.idTypeCaisse = idTypeCaisse;
    }

    public String getIdTypeCaisselib() {
        return idTypeCaisselib;
    }

    public void setIdTypeCaisselib(String idTypeCaisselib) {
        this.idTypeCaisselib = idTypeCaisselib;
    }

    public Date getDateDernierReport() {
        return dateDernierReport;
    }

    public void setDateDernierReport(Date dateDernierReport) {
        this.dateDernierReport = dateDernierReport;
    }

    public double getMontantDernierReport() {
        return montantDernierReport;
    }

    public void setMontantDernierReport(double montantDernierReport) {
        this.montantDernierReport = montantDernierReport;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public String getFieldDateName() {
        return "dateDernierReport";
    }
    @Override
    public String getTuppleID() {
        return this.id;
    }

    @Override
    public String getAttributIDName() {
        return "id";
    }

    public ResultatEtSomme rechercherPage(String[] colInt, String[]valInt, int numPage, String apresWhere, String[]nomColSomme, Connection c, int npp) throws Exception {
        String daty = Utilitaire.dateDuJour();
        if(valInt != null && valInt.length > 1) {
            daty = valInt[1].toString();
        }
        /*String req = "SELECT " +
                "ve.idproduit AS id, " +
                "ve.idproduitlib, " +
                "ve.categorieingredient, " +
                "ve.idtypeproduitlib, " +
                "ve.idmagasin, " +
                "ve.idmagasinlib, " +
                "TO_DATE('01-01-2001', 'DD-MM-YYYY') AS dateDernierMouvement, " +
                "SUM(ve.quantite) AS quantite, " +
                "SUM(ve.entree) AS entree, " +
                "SUM(ve.sortie) AS sortie, " +
                "SUM(ve.reste) AS reste, " +
                "ve.unite, " +
                "ve.idunitelib, " +
                "ve.puvente, " +
                "ve.IDPOINT, " +
                "ve.IDTYPEMAGASIN, " +
                "ve.SEUILMIN, " +
                "ve.SEUILMAX, " +
                "SUM(ve.MONTANTENTREE) AS MONTANTENTREE, " +
                "SUM(ve.MONTANTSORTIE) AS MONTANTSORTIE, " +
                "ve.PU, " +
                "SUM(ve.MONTANTRESTE) AS MONTANTRESTE " +
                "FROM V_ETATSTOCK_ENTREE ve " +
                "WHERE ve.PU > 0 AND ve.daty <= '" + daty + "' " +
                "GROUP BY ve.idproduit, ve.idproduitlib, ve.categorieingredient, " +
                "ve.idtypeproduitlib, ve.idmagasin, ve.idmagasinlib, ve.unite, " +
                "ve.idunitelib, ve.puvente, ve.IDPOINT, ve.IDTYPEMAGASIN, " +
                "ve.SEUILMIN, ve.SEUILMAX, ve.PU " +
                "ORDER BY ve.idproduit, ve.PU";*/
        String req=getReqEtatCaisse(daty, c);
        //System.out.println(generateQueryCore(Utilitaire.dateDuJourSql(),Utilitaire.dateDuJourSql()));
        ResultatEtSomme rs = CGenUtil.rechercherPage(this, req, numPage, nomColSomme, apresWhere, c, npp);
        return rs;
    }
    public String getReqEtatCaisse(String daty, Connection c) throws SQLException {
        String req = "";
        if (UtilitaireAsync.isOracle(c)) {
            req="WITH max_inventaire AS (\n" +
                    "    -- Étape 1 : On extrait la date maximale valide et la quantité par COUPLE produit/magasin\n" +
                    "    SELECT rep.IDCAISSE,\n" +
                    "           MAX(rep.DATY)                                             AS max_daty,\n" +
                    "           MAX(rep.MONTANT) KEEP (DENSE_RANK LAST ORDER BY rep.DATY) AS montant_max_daty\n" +
                    "    FROM REPORTCAISSEAVECHEURE rep\n" +
                    "    WHERE rep.ETAT >= 11 and rep.DATY<=TO_DATE('"+daty+"','DD-MM-YYYY')+1\n" +
                    "    GROUP BY rep.IDCAISSE),\n" +
                    "    tauxChangeMax AS (\n" +
                    "         SELECT\n" +
                    "             max(tau.TAUX) KEEP (DENSE_RANK LAST ORDER BY tau.DATY) as taux,\n" +
                    "             tau.IDDEVISE as tauxIdDevise,\n" +
                    "             max(tau.DATY) as tauxDaty\n" +
                    "         FROM TAUXDECHANGE tau where tau.DATY<=TO_DATE('"+daty+"','DD-MM-YYYY')+1 group by IDDEVISE\n" +
                    "     ),\n" +
                    "     somme_precalc AS (\n" +
                    "         SELECT c.ID as idcaisse, c.VAL as idCaisselib,\n" +
                    "                NVL(SUM(CASE\n" +
                    "                            WHEN mff.DATY > NVL(inv.max_daty, TO_DATE('01/01/2025', 'DD-MM-YYYY')) THEN mff.CREDIT\n" +
                    "                            ELSE 0 END), 0)       AS credit,\n" +
                    "\n" +
                    "                NVL(SUM(CASE\n" +
                    "                            WHEN mff.DATY > NVL(inv.max_daty, TO_DATE('01/01/2025', 'DD-MM-YYYY')) THEN mff.DEBIT\n" +
                    "                            ELSE 0 END), 0)       AS debit,\n" +
                    "                max(inv.max_daty)                 AS dateDernierReport,\n" +
                    "                nvl(max(inv.montant_max_daty), 0) as montantDernierReport,\n" +
                    "                c.IDPOINT,p.VAL as idPointlib,c.IDTYPECAISSE,typ.VAL as idTypeCaisselib,c.IDDEVISE,\n" +
                    "                max(tau.taux) as taux\n" +
                    "         FROM Caisse c\n" +
                    "         left join    MOUVEMENTCAISSEAVECHEURE mff on mff.IDCAISSE=c.ID and mff.ETAT>=11 and mff.DATY<=TO_DATE('"+daty+"','DD-MM-YYYY')+1\n" +
                    "         JOIN max_inventaire inv ON inv.IDCAISSE=c.ID\n" +
                    "         left JOIN tauxChangeMax TAU ON TAU.tauxIdDevise=C.IDDEVISE\n" +
                    "         left join point p on p.ID=c.IDPOINT\n" +
                    "         left join TYPECAISSE typ on typ.ID=c.IDTYPECAISSE\n" +
                    "         GROUP BY c.id,c.IDPOINT,p.VAL,c.IDTYPECAISSE,typ.VAL,c.VAL,c.IDDEVISE)\n" +
                    "-- Sélection finale\n" +
                    "SELECT idcaisse,idCaisselib||' ('||nvl(IDDEVISE,'AR')||')' as idCaisselib, cast(credit as number(30,2)) as credit, cast(debit as number(30,2)) as debit,\n" +
                    "       cast(montantDernierReport+credit-debit as number(30,2)) as reste, cast(nvl(taux,1) as number(30,2)) as taux,\n" +
                    "       cast((montantDernierReport+credit-debit)*nvl(taux,1) as number(30,2)) as resteAr, null as daty,\n" +
                    "       datedernierreport, cast(montantdernierreport as number(30,2)) as montantdernierreport, idpoint, idpointlib, idtypecaisse, idtypecaisselib\n" +
                    "FROM somme_precalc";
        }
        if (UtilitaireAsync.isPostgres(c)) {
            req = "WITH max_inventaire AS (\n" +
                    "    -- Etape 1 : date max valide + montant a cette date, par caisse\n" +
                    "    SELECT rep.idcaisse,\n" +
                    "           MAX(rep.daty) AS max_daty,\n" +
                    "           (array_agg(rep.montant ORDER BY rep.daty DESC, rep.montant DESC NULLS LAST))[1] AS montant_max_daty\n" +
                    "    FROM reportcaisseavecheure rep\n" +
                    "    WHERE rep.etat >= 11\n" +
                    "      AND rep.daty <= to_date('"+daty+"', 'DD/MM/YYYY') + 1\n" +
                    "    GROUP BY rep.idcaisse\n" +
                    "),\n" +
                    "tauxchangemax AS (\n" +
                    "    SELECT (array_agg(tau.taux ORDER BY tau.daty DESC, tau.taux DESC NULLS LAST))[1] AS taux,\n" +
                    "           tau.iddevise AS tauxiddevise,\n" +
                    "           MAX(tau.daty) AS tauxdaty\n" +
                    "    FROM tauxdechange tau\n" +
                    "    WHERE tau.daty <= to_date('"+daty+"', 'DD/MM/YYYY') + 1\n" +
                    "    GROUP BY tau.iddevise\n" +
                    "),\n" +
                    "somme_precalc AS (\n" +
                    "    SELECT c.id AS idcaisse,\n" +
                    "           c.val AS idcaisselib,\n" +
                    "           COALESCE(SUM(CASE WHEN mff.daty > COALESCE(inv.max_daty, DATE '2025-01-01') THEN mff.credit ELSE 0 END), 0) AS credit,\n" +
                    "           COALESCE(SUM(CASE WHEN mff.daty > COALESCE(inv.max_daty, DATE '2025-01-01') THEN mff.debit  ELSE 0 END), 0) AS debit,\n" +
                    "           MAX(inv.max_daty) AS datedernierreport,\n" +
                    "           COALESCE(MAX(inv.montant_max_daty), 0) AS montantdernierreport,\n" +
                    "           c.idpoint,\n" +
                    "           p.val AS idpointlib,\n" +
                    "           c.idtypecaisse,\n" +
                    "           typ.val AS idtypecaisselib,\n" +
                    "           c.iddevise,\n" +
                    "           MAX(tau.taux) AS taux\n" +
                    "    FROM caisse c\n" +
                    "             LEFT JOIN mouvementcaisseavecheure mff\n" +
                    "                       ON mff.idcaisse = c.id\n" +
                    "                      AND mff.etat >= 11\n" +
                    "                      AND mff.daty <= to_date('"+daty+"', 'DD/MM/YYYY') + 1\n" +
                    "             JOIN max_inventaire inv ON inv.idcaisse = c.id\n" +
                    "             LEFT JOIN tauxchangemax tau ON tau.tauxiddevise = c.iddevise\n" +
                    "             LEFT JOIN point p ON p.id = c.idpoint\n" +
                    "             LEFT JOIN typecaisse typ ON typ.id = c.idtypecaisse\n" +
                    "    GROUP BY c.id, c.idpoint, p.val, c.idtypecaisse, typ.val, c.val, c.iddevise\n" +
                    ")\n" +
                    "-- Selection finale\n" +
                    "SELECT idcaisse,\n" +
                    "       concat(idcaisselib, ' (', COALESCE(iddevise, 'AR'), ')') AS idcaisselib,\n" +
                    "       CAST(credit AS numeric(30,2)) AS credit,\n" +
                    "       CAST(debit  AS numeric(30,2)) AS debit,\n" +
                    "       CAST(montantdernierreport + credit - debit AS numeric(30,2)) AS reste,\n" +
                    "       CAST(COALESCE(taux, 1) AS numeric(30,2)) AS taux,\n" +
                    "       CAST((montantdernierreport + credit - debit) * COALESCE(taux, 1) AS numeric(30,2)) AS restear,\n" +
                    "       NULL::date AS daty,\n" +
                    "       datedernierreport,\n" +
                    "       CAST(montantdernierreport AS numeric(30,2)) AS montantdernierreport,\n" +
                    "       idpoint,\n" +
                    "       idpointlib,\n" +
                    "       idtypecaisse,\n" +
                    "       idtypecaisselib\n" +
                    "FROM somme_precalc";
        }
        return req;
    }

    public String generateQueryCore(Date dateMin, Date dateMax ) {
        LocalDate localDate = LocalDate.now();
        Date dateDuJours = Date.valueOf(localDate);
        dateDuJours = Utilitaire.ajoutJourDate(dateDuJours, -1) ;
        String query ;
        //System.out.println(dateMin+"  date du jour ="+dateDuJours);
        if(dateDuJours.equals(dateMin)){
            query = "SELECT * FROM V_ETATCAISSE_devise_AR";
        }else{
            query="SELECT  r.ID\n" +
                    "     ,r.IDCAISSE\n" +
                    "     ,c.val                                                                    AS idcaisseLib\n" +
                    "     ,c.idtypecaisse\n" +
                    "     ,tc.desce                                                                 AS idtypecaisselib\n" +
                    "     ,c.idpoint\n" +
                    "     ,p.desce                                                                  AS idpointlib\n" +
                    "     ,r.DATY dateDernierReport\n" +
                    "     ,CAST(NVL(((r.MONTANT+nvl(mvtAv.CREDIT-mvtAv.DEBIT,0))*NVL(taux.taux,1)),0)                                          AS number(30,2)) montantDernierReport\n" +
                    "     ,CAST(NVL(mvt.debit,0)                                                    AS number(30,2)) debit\n" +
                    "     ,CAST(NVL(mvt.credit,0)                                                   AS number(30,2)) credit\n" +
                    "     ,CAST((NVL(mvt.credit,0) + NVL((r.MONTANT+nvl(mvtAv.CREDIT-mvtAv.DEBIT,0))*NVL(taux.taux,1),0) - NVL(mvt.debit,0)) AS number(30,2)) reste\n" +
                    "     ,'AR'                                                                     AS devise\n" +
                    "FROM REPORTCAISSE_devise r,\n" +
                    "     (\n" +
                    "         SELECT  r.IDCAISSE\n" +
                    "              ,MAX(r.DATY) maxDateReport\n" +
                    "         FROM REPORTCAISSE_devise r\n" +
                    "         WHERE r.ETAT = 11\n" +
                    "           AND r.DATY <='"+ Utilitaire.datetostring(dateMin) + "'\n" +
                    "         GROUP BY  r.IDCAISSE\n" +
                    "     ) rm, (\n" +
                    "         SELECT  m.IDCAISSE\n" +
                    "              ,SUM(nvl((m.DEBIT*t.taux),0)) DEBIT\n" +
                    "              ,SUM(nvl((m.CREDIT*t.taux),0)) CREDIT\n" +
                    "         FROM MOUVEMENTCAISSE_VISE m,\n" +
                    "              (\n" +
                    "                  SELECT  r.IDCAISSE\n" +
                    "                       ,MAX(r.DATY) maxDateReport\n" +
                    "                  FROM REPORTCAISSE r\n" +
                    "                  WHERE r.ETAT = 11\n" +
                    "                    AND r.DATY <= '"+ Utilitaire.datetostring(dateMin) + "'\n" +
                    "                  GROUP BY  r.IDCAISSE\n" +
                    "              ) rm, (\n" +
                    "                  SELECT  ta.*\n" +
                    "                  FROM TAUXDECHANGE ta,\n" +
                    "                       (\n" +
                    "                           SELECT  MAX(daty) AS daty\n" +
                    "                                ,iddevise\n" +
                    "                           FROM TAUXDECHANGE t\n" +
                    "                           WHERE daty <= '"+ Utilitaire.datetostring(dateMin) + "'\n" +
                    "                           GROUP BY  iddevise\n" +
                    "                       ) tmax\n" +
                    "                  WHERE ta.daty = tmax.daty\n" +
                    "                    AND ta.iddevise = tmax.iddevise ) t\n" +
                    "         WHERE m.IDDEVISE = t.iddevise(+)\n" +
                    "           AND m.IDCAISSE = rm.idcaisse(+)\n" +
                    "           AND m.DATY >= '"+ Utilitaire.datetostring(dateMin) + "'\n" +
                    "           AND m.DATY <= '"+ Utilitaire.datetostring(dateMax) + "'\n" +
                    "         GROUP BY  m.IDCAISSE ) mvt\n" +
                    "        ,(\n" +
                    "    SELECT  m.IDCAISSE\n" +
                    "         ,SUM(nvl((m.DEBIT*t.taux),0)) DEBIT\n" +
                    "         ,SUM(nvl((m.CREDIT*t.taux),0)) CREDIT\n" +
                    "    FROM MOUVEMENTCAISSE_VISE m,\n" +
                    "         (\n" +
                    "             SELECT  r.IDCAISSE\n" +
                    "                  ,MAX(r.DATY) maxDateReport\n" +
                    "             FROM REPORTCAISSE r\n" +
                    "             WHERE r.ETAT = 11\n" +
                    "               AND r.DATY <= '"+ Utilitaire.datetostring(dateMin) + "'\n" +
                    "             GROUP BY  r.IDCAISSE\n" +
                    "         ) rm, (\n" +
                    "             SELECT  ta.*\n" +
                    "             FROM TAUXDECHANGE ta,\n" +
                    "                  (\n" +
                    "                      SELECT  MAX(daty) AS daty\n" +
                    "                           ,iddevise\n" +
                    "                      FROM TAUXDECHANGE t\n" +
                    "                      WHERE daty <= '"+ Utilitaire.datetostring(dateMin) + "'\n" +
                    "                      GROUP BY  iddevise\n" +
                    "                  ) tmax\n" +
                    "             WHERE ta.daty = tmax.daty\n" +
                    "               AND ta.iddevise = tmax.iddevise ) t\n" +
                    "    WHERE m.IDDEVISE = t.iddevise(+)\n" +
                    "      AND m.IDCAISSE = rm.idcaisse(+)\n" +
                    "      AND m.DATY >= maxDateReport\n" +
                    "      AND m.DATY < '" +Utilitaire.datetostring(dateMin)  +"'\n" +
                    "    GROUP BY  m.IDCAISSE ) mvtAv\n" +
                    "   ,(\n" +
                    "         SELECT  ta.*\n" +
                    "         FROM TAUXDECHANGE ta,\n" +
                    "              (\n" +
                    "                  SELECT  MAX(daty) AS daty\n" +
                    "                       ,iddevise\n" +
                    "                  FROM TAUXDECHANGE t\n" +
                    "                  WHERE daty <= '" +Utilitaire.datetostring(dateMin)  +"'\n" +
                    "                  GROUP BY  iddevise\n" +
                    "              ) tmax\n" +
                    "         WHERE ta.daty = tmax.daty\n" +
                    "           AND ta.iddevise = tmax.iddevise ) taux, (select * from CAISSE where id not in ('CAIS001')) c, typecaisse tc, point p\n" +
                    "WHERE r.DATY = rm.maxDateReport\n" +
                    "  AND r.IDDEVISE = taux.iddevise(+)\n" +
                    "  AND r.ETAT = 11\n" +
                    "  AND r.IDCAISSE = rm.IDCAISSE\n" +
                    "  AND r.IDCAISSE = c.ID(+)\n" +
                    "  AND r.IDCAISSE = mvt.idcaisse(+)\n" +
                    "  AND r.IDCAISSE = mvtAv.idcaisse(+)\n" +
                    "  AND c.IDTYPECAISSE = tc.ID(+)\n" +
                    "  AND c.IDPOINT = p.ID\n";
        }
        System.out.println(query);
        return query;
    }


    public EtatCaisse[] getEtatCaisseByDate(String daty) throws Exception{
        if(daty==null) daty = Utilitaire.dateDuJour();
        String[] colInt = {"daty"};
        String[] valInt = {daty, daty};

        ResultatEtSomme res = rechercherPage(colInt, valInt, 1, "", null,  null, 1);
        return (EtatCaisse[]) res.getResultat();
    }
     public EtatCaisse[] getEtatCaisseByDate(String daty,String idPoint ) throws Exception{
        if(daty==null) daty = Utilitaire.dateDuJour();;
        System.out.println("daty oo "+daty);
        String[] colInt = {"daty"};
        String[] valInt = {daty, daty};

        ResultatEtSomme res = rechercherPage(colInt, valInt, 1, " and idpoint='"+idPoint+"'", null,  null, 1);
        System.out.println(" res length "+ res.getResultat().length);
        return (EtatCaisse[]) res.getResultat();
    }
    public List<EtatCaisse> getEtatCaisse(Date dateMin, Date dateMax) throws SQLException {
        List<EtatCaisse> result = new ArrayList<>();
        String sql = generateQueryCore(dateMin, dateMax);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn =  new UtilDB().GetConn();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                EtatCaisse e = new EtatCaisse();
                e.setId(rs.getString("ID"));
                e.setIdCaisselib(rs.getString("IDCAISSELIB"));
                e.setReste(rs.getDouble("RESTE"));
                result.add(e);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException ignored) {}
            if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
        }
        return result;
    }

}
