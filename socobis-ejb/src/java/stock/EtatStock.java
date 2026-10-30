package stock;

import bean.CGenUtil;
import bean.ClassMAPTable;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import bean.ResultatEtSomme;
import demande.DemandeTransfertFille;
import utilitaire.UtilDB;
import utilitaire.Utilitaire;
import utilitaire.UtilitaireAsync;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Angela
 */
public class EtatStock extends ClassMAPTable {
    protected String id;
    protected String idProduitLib;
    protected String idTypeProduit;
    protected String idTypeProduitLib;
    protected String idMagasin;
    protected String idMagasinLib;
    protected Date dateDernierInventaire,dateDernierMouvement, daty;
    protected double quantite;
    protected double entree;
    protected double sortie;
    protected double reste,montantReste,montantSortie,montantEntree;
    protected double puVente;
    protected String idUnite;
    protected String idUniteLib;
    protected String idPoint;
    protected String idPointLib;
    protected  double pu;
    protected  String mvtsrc;
    String typeStock;
    String idProduit;
    java.sql.Date invdaty;
    double invquantite;
    double seuil, seuilMin, seuilMax, qteDemande;

    private  String idModele;
    private  String idModeleLib;
    private String idCategorieModele;
    private String idCategorieModeleLib;
    private String idSousCategorieModele;
    private String idSousCategorieModeleLib;
    private String idPointure;
    private String idPointureLib;

    private String referenceCouleur;
    private  String codeCouleur;
    private String nomCouleur;

    public String getReferenceCouleur() {
        return referenceCouleur;
    }

    public void setReferenceCouleur(String referenceCouleur) {
        this.referenceCouleur = referenceCouleur;
    }

    public String getCodeCouleur() {
        return codeCouleur;
    }

    public void setCodeCouleur(String codeCouleur) {
        this.codeCouleur = codeCouleur;
    }

    public String getNomCouleur() {
        return nomCouleur;
    }

    public void setNomCouleur(String nomCouleur) {
        this.nomCouleur = nomCouleur;
    }

    public String getIdPointure() {
        return idPointure;
    }

    public void setIdPointure(String idPointure) {
        this.idPointure = idPointure;
    }

    public String getIdPointureLib() {
        return idPointureLib;
    }

    public void setIdPointureLib(String idPointureLib) {
        this.idPointureLib = idPointureLib;
    }

    public String getIdModele() {
        return idModele;
    }

    public void setIdModele(String idModele) {
        this.idModele = idModele;
    }

    public String getIdModeleLib() {
        return idModeleLib;
    }

    public void setIdModeleLib(String idModeleLib) {
        this.idModeleLib = idModeleLib;
    }

    public String getIdCategorieModele() {
        return idCategorieModele;
    }

    public void setIdCategorieModele(String idCategorieModele) {
        this.idCategorieModele = idCategorieModele;
    }

    public String getIdCategorieModeleLib() {
        return idCategorieModeleLib;
    }

    public void setIdCategorieModeleLib(String idCategorieModeleLib) {
        this.idCategorieModeleLib = idCategorieModeleLib;
    }

    public String getIdSousCategorieModele() {
        return idSousCategorieModele;
    }

    public void setIdSousCategorieModele(String idSousCategorieModele) {
        this.idSousCategorieModele = idSousCategorieModele;
    }

    public String getIdSousCategorieModeleLib() {
        return idSousCategorieModeleLib;
    }

    public void setIdSousCategorieModeleLib(String idSousCategorieModeleLib) {
        this.idSousCategorieModeleLib = idSousCategorieModeleLib;
    }

    public double getSeuil() {
        return seuil;
    }

    public void setSeuil(double seuil) {
        this.seuil = seuil;
    }

    public double getSeuilMin() {
        return seuilMin;
    }

    public void setSeuilMin(double seuilMin) {
        this.seuilMin = seuilMin;
    }

    public double getSeuilMax() {
        return seuilMax;
    }

    public void setSeuilMax(double seuilMax) {
        this.seuilMax = seuilMax;
    }

    public double getQteDemande() {
        return qteDemande;
    }

    public void setQteDemande(double qteDemande) {
        this.qteDemande = qteDemande;
    }

    public Date getInvdaty() {
        return invdaty;
    }

    public void setInvdaty(Date invdaty) {
        this.invdaty = invdaty;
    }

    public double getInvquantite() {
        return invquantite;
    }

    public void setInvquantite(double invquantite) {
        this.invquantite = invquantite;
    }

    public String getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(String idProduit) {
        this.idProduit = idProduit;
    }

    public String getTypeStock() {
        return typeStock;
    }

    public void setTypeStock(String typeStock) {
        this.typeStock = typeStock;
    }

    public String getMvtsrc() {
        return mvtsrc;
    }

    public void setMvtsrc(String mvtsrc) {
        this.mvtsrc = mvtsrc;
    }

    public Date getDateDernierMouvement() {
        return dateDernierMouvement;
    }

    public void setDateDernierMouvement(Date dateDernierMouvement) {
        this.dateDernierMouvement = dateDernierMouvement;
    }

    public double getMontantReste() {
        return montantReste;
    }
    // public ResultatEtSomme rechercherPage(String[] colInt, String[]valInt, int numPage, String apresWhere, String[]nomColSomme, Connection c, int npp) throws Exception {
    //     String daty=Utilitaire.dateDuJour();
    //     if(valInt!=null&&valInt.length>1) {daty=valInt[1].toString();}

    //     String req= "SELECT\n" +
    //             "\tp.ID AS ID,\n" +
    //             "\tp.LIBELLE AS idproduitLib,\n" +
    //             "\tp.CATEGORIEINGREDIENT,\n" +
    //             "\ttp.DESCE AS idtypeproduitlib,\n" +
    //             "\tms.IDMAGASIN,\n" +
    //             "\tmag.DESCE AS idmagasinlib,\n" +
    //             "\tTO_DATE('01-01-2001', 'DD-MM-YYYY') AS dateDernierMouvement,\n" +
    //             "\tms.quantite AS QUANTITE,\n" +
    //             "\tms.entree AS ENTREE,\n" +
    //             "\tms.sortie AS SORTIE,\n" +
    //             "\tms.quantite AS reste,\n" +
    //             "\tp.UNITE,\n" +
    //             "\tu.DESCE AS idunitelib,\n" +
    //             "\tCAST(NVL(p.PV, 0) AS NUMBER(30, 2)) AS PUVENTE,\n" +
    //             "\tmag.IDPOINT,\n" +
    //             "\tmag.IDTYPEMAGASIN,\n" +
    //             "\tp.SEUILMIN,\n" +
    //             "\tp.SEUILMAX,\n" +
    //             "\tms.montantEntree,\n" +
    //             "\tms.montantSortie,\n" +
    //             "\tp.pu,\n" +
    //             "\tms.montant as montantReste\n" +
    //             "FROM AS_INGREDIENTS p\n" +
    //             "LEFT JOIN (SELECT\n" +
    //             "    mf.IDPRODUIT,\n" +
    //             "    SUM(NVL(mf.ENTREE,0)) AS ENTREE,\n" +
    //             "    SUM(NVL(mf.SORTIE,0)) AS SORTIE,\n" +
    //             "    SUM(NVL(mf.ENTREE,0)) - SUM(NVL(mf.SORTIE,0)) AS quantite,\n" +
    //             "    cast(sum(mf.montantEntree) as number(30,2))  AS montantEntree,\n" +
    //             "    cast(sum(mf.montantSortie) as number(30,2))  AS montantSortie,\n" +
    //             "    CAST(NVL(ai.PU, 0) * (SUM(NVL(mf.ENTREE,0)) - SUM(NVL(mf.SORTIE,0))) AS NUMBER(30,2)) AS montant,\n" +
    //             "    m.IDMAGASIN\n" +
    //             "FROM\n" +
    //             "    mvtStockFilleMontant mf\n" +
    //             "JOIN MVTSTOCK m ON m.id = mf.IDMVTSTOCK\n" +
    //             "JOIN AS_INGREDIENTS ai ON ai.ID = mf.IDPRODUIT\n" +
    //             "WHERE\n" +
    //             "    m.ETAT >= 11\n" +
    //             "    AND mf.IDPRODUIT IS NOT NULL\n" +
    //             "    AND m.daty<=TO_DATE('"+daty+"', 'DD/MM/YYYY') \n" +
    //             "GROUP BY\n" +
    //             "    mf.IDPRODUIT,\n" +
    //             "    ai.PU,m.IDMAGASIN) ms ON ms.IDPRODUIT = p.ID\n" +
    //             "LEFT JOIN CATEGORIEINGREDIENT tp ON p.CATEGORIEINGREDIENT = tp.ID\n" +
    //             "LEFT JOIN MAGASINPOINT mag ON ms.IDMAGASIN = mag.ID\n" +
    //             "LEFT JOIN AS_UNITE u ON p.UNITE = u.ID\n" +
    //             "where NVL(ms.ENTREE, 0)>0 or NVL(ms.SORTIE, 0)>0";
    //     ResultatEtSomme rs= CGenUtil.rechercherPage(this,req,numPage,nomColSomme,apresWhere,c,npp);
    //     return rs;
    // }

    // TALOHA rechercherPage
//    public ResultatEtSomme rechercherPage(String[] colInt, String[]valInt, int numPage, String apresWhere, String[]nomColSomme, Connection c, int npp) throws Exception {
//        String daty = Utilitaire.dateDuJour();
//        if(valInt != null && valInt.length > 1) {
//            daty = valInt[1].toString();
//        }
//        /*String req = "SELECT " +
//                "ve.idproduit AS id, " +
//                "ve.idproduitlib, " +
//                "ve.categorieingredient, " +
//                "ve.idtypeproduitlib, " +
//                "ve.idmagasin, " +
//                "ve.idmagasinlib, " +
//                "TO_DATE('01-01-2001', 'DD-MM-YYYY') AS dateDernierMouvement, " +
//                "SUM(ve.quantite) AS quantite, " +
//                "SUM(ve.entree) AS entree, " +
//                "SUM(ve.sortie) AS sortie, " +
//                "SUM(ve.reste) AS reste, " +
//                "ve.unite, " +
//                "ve.idunitelib, " +
//                "ve.puvente, " +
//                "ve.IDPOINT, " +
//                "ve.IDTYPEMAGASIN, " +
//                "ve.SEUILMIN, " +
//                "ve.SEUILMAX, " +
//                "SUM(ve.MONTANTENTREE) AS MONTANTENTREE, " +
//                "SUM(ve.MONTANTSORTIE) AS MONTANTSORTIE, " +
//                "ve.PU, " +
//                "SUM(ve.MONTANTRESTE) AS MONTANTRESTE " +
//                "FROM V_ETATSTOCK_ENTREE ve " +
//                "WHERE ve.PU > 0 AND ve.daty <= '" + daty + "' " +
//                "GROUP BY ve.idproduit, ve.idproduitlib, ve.categorieingredient, " +
//                "ve.idtypeproduitlib, ve.idmagasin, ve.idmagasinlib, ve.unite, " +
//                "ve.idunitelib, ve.puvente, ve.IDPOINT, ve.IDTYPEMAGASIN, " +
//                "ve.SEUILMIN, ve.SEUILMAX, ve.PU " +
//                "ORDER BY ve.idproduit, ve.PU"; */
//        String req="SELECT\n" +
//                "   '-' as id,\n" +
//                "   ai.id AS IDPRODUIT,\n" +
//                "   ai.LIBELLE AS idproduitlib,\n" +
//                "   ai.CATEGORIEINGREDIENT,\n" +
//                "   c.VAL AS idtypeproduitlib,\n" +
//                "   m.id as IDMAGASIN,\n" +
//                "   m.VAL AS idmagasinlib,\n" +
//                "   ve.QUANTITE,\n" +
//                "   CAST(ve.ENTREE AS NUMBER(30,2)) AS ENTREE,\n" +
//                "   CAST(ve.SORTIE AS NUMBER(30,2)) AS SORTIE,\n" +
//                "   CAST(ve.RESTE AS NUMBER(30,2)) AS RESTE,\n" +
//                "   CAST(nvl(ve.ENTREE*ve.pu,0) AS NUMBER(30,2)) AS MONTANTENTREE,\n" +
//                "   CAST(nvl(ve.sortie*ve.pu,0) AS NUMBER(30,2)) AS MONTANTSORTIE,\n" +
//                "   CAST(nvl((ve.ENTREE*ve.pu)-(ve.sortie*ve.pu),0) AS NUMBER(30,2)) AS MONTANTRESTE,\n" +
//                "   ai.UNITE,\n" +
//                "   u.VAL AS idunitelib,\n" +
//                "   cast(ve.PU as NUMBER(30,2)) as pu,\n" +
//                "   ve.DATY\n" +
//                "FROM (\n" +
//                "   SELECT\n" +
//                "       \"IDPRODUIT\",\n" +
//                "       sum(mf.ENTREE) as \"ENTREE\",\n" +
//                "       sum (mf.SORTIE) \"SORTIE\",\n" +
//                "       '' as \"IDVENTEDETAIL\",\n" +
//                "       '' as \"IDTRANSFERTDETAIL\",\n" +
//                "       avg (mf.PU) as \"PU\",\n" +
//                "       '' as \"MVTSRC\",\n" +
//                "       '' as \"DATY\",\n" +
//                "       (sum(mf.ENTREE)-sum (mf.SORTIE)) as \"RESTE\",\n" +
//                "       0 as \"QUANTITE\",\n" +
//                "       m.IDMAGASIN \"IDMAGASIN\"\n" +
//                "   FROM MVTSTOCKFILLE mf\n" +
//                "        left join mvtStock m on m.id=mf.IDMVTSTOCK\n" +
//                "   where m.ETAT>=11 and m.daty <= TO_DATE('"+daty+"', 'DD-MM-YYYY')\n" +
//                "   group by mf.IDPRODUIT,m.IDMAGASIN\n" +
//                ") ve\n" +
//                "LEFT JOIN AS_INGREDIENTS ai ON ai.id = ve.IDPRODUIT\n" +
//                "LEFT JOIN CATEGORIEINGREDIENT c ON ai.CATEGORIEINGREDIENT = c.id\n" +
//                "LEFT JOIN MAGASINPOINT m ON ve.IDMAGASIN = m.id\n" +
//                "LEFT JOIN AS_UNITE u ON ai.UNITE = u.id\n" +
//                "WHERE  ve.PU > 0 and ve.reste>=0";
//
//        ResultatEtSomme rs = CGenUtil.rechercherPage(this, req, numPage, nomColSomme, apresWhere, c, npp);
//        return rs;
//    }

    //Standard rechercherPage
    public ResultatEtSomme rechercherPage(String[] colInt, String[]valInt, int numPage, String apresWhere, String[]nomColSomme, Connection c, int npp) throws Exception {
        String daty = Utilitaire.dateDuJour();

        String pointureLib1 = null;

        String pointureLib2 = null;
        if(valInt != null && valInt.length > 1) {
            daty = valInt[3].toString();

            pointureLib1 = valInt[0].toString();
            pointureLib2 = valInt[1].toString();
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
        String req=getReqEtatStock(daty,pointureLib1,pointureLib2, c);
        System.out.println("REQ GROUPE "+req);
        ResultatEtSomme rs = CGenUtil.rechercherPage(this, req, numPage, nomColSomme, apresWhere, c, npp);
        return rs;
    }
    public static String getReqEtatStock(String daty, String idPointureLib1,String idPointureLib2, Connection c) throws SQLException {
        String req="SELECT\n" +
                "   '-' as id,ai.typeStock,\n" +
                "   ai.id AS IDPRODUIT,\n" +
                "   ai.LIBELLE AS idproduitlib,\n" +
                "   ai.CATEGORIEINGREDIENT,\n" +
                "   c.VAL AS idtypeproduitlib,\n" +
                "   m.id as IDMAGASIN,\n" +
                "   m.VAL AS idmagasinlib,\n" +
                "   ve.QUANTITE,\n" +
                "   CAST(ve.ENTREE AS NUMBER(30,2)) AS ENTREE,\n" +
                "   CAST(ve.SORTIE AS NUMBER(30,2)) AS SORTIE,\n" +
                "   CAST(ve.RESTE AS NUMBER(30,2)) AS RESTE,\n" +
                "   CAST(nvl(ve.ENTREE*ve.pu,0) AS NUMBER(30,2)) AS MONTANTENTREE,\n" +
                "   CAST(nvl(ve.sortie*ve.pu,0) AS NUMBER(30,2)) AS MONTANTSORTIE,\n" +
                "   CAST(nvl((ve.ENTREE*ve.pu)-(ve.sortie*ve.pu),0) AS NUMBER(30,2)) AS MONTANTRESTE,\n" +
                "   ai.UNITE,\n" +
                "   u.VAL AS idunitelib,\n" +
                "   case\n" +
                "       when ve.reste!=0 then CAST(nvl((((ve.ENTREE*ve.pu)-(ve.sortie*ve.pu))/nvl(ve.reste,1)),0) AS NUMBER(30,2))\n" +
                "        else cast(ai.pu as number(30,2))\n" +
                "            end as pu,\n" +
                "   ve.DATY\n" +
                "FROM (\n" +
                "   SELECT\n" +
                "       \"IDPRODUIT\",\n" +
                "       sum(mf.ENTREE) as \"ENTREE\",\n" +
                "       sum (mf.SORTIE) \"SORTIE\",\n" +
                "       '' as \"IDVENTEDETAIL\",\n" +
                "       '' as \"IDTRANSFERTDETAIL\",\n" +
                "       avg (mf.PU) as \"PU\",\n" +
                "       '' as \"MVTSRC\",\n" +
                "       '' as \"DATY\",\n" +
                "       (sum(mf.ENTREE)-sum (mf.SORTIE)) as \"RESTE\",\n" +
                "       0 as \"QUANTITE\",\n" +
                "       m.IDMAGASIN \"IDMAGASIN\"\n" +
                "   FROM MVTSTOCKFILLE mf\n" +
                "        left join mvtStock m on m.id=mf.IDMVTSTOCK\n" +
                "   where m.ETAT>=11 and m.daty <= TO_DATE('"+daty+"', 'DD-MM-YYYY')\n" +
                "   group by mf.IDPRODUIT,m.IDMAGASIN\n" +
                ") ve\n" +
                "LEFT JOIN AS_INGREDIENTS ai ON ai.id = ve.IDPRODUIT\n" +
                "LEFT JOIN CATEGORIEINGREDIENT c ON ai.CATEGORIEINGREDIENT = c.id\n" +
                "LEFT JOIN MAGASINPOINT m ON ve.IDMAGASIN = m.id\n" +
                "LEFT JOIN AS_UNITE u ON ai.UNITE = u.id\n" +
                "WHERE  ve.PU > 0 and ve.reste>0";
        String reqAvecInv="SELECT\n" +
                "    'invdaty' as invdaty,\n" +
                "    cast(sum(invquantite) as number(30,2)) as invquantite,\n" +
                "    '' as id,\n" +
                "   ai.id AS IDPRODUIT,\n" +
                "   ai.LIBELLE AS idproduitlib,\n" +
                "   ai.CATEGORIEINGREDIENT,\n" +
                "   c.VAL AS idtypeproduitlib,\n" +
                "   ve.IDMAGASIN, ai.typeStock,\n" +
                "   m.VAL AS idmagasinlib,\n" +
                "   sum(ve.QUANTITE) as quantite,\n" +
                "   CAST(sum(ve.ENTREE) AS NUMBER(30,2)) AS ENTREE,\n" +
                "   CAST(sum(ve.SORTIE) AS NUMBER(30,2)) AS SORTIE,\n" +
                "   CAST(sum(ve.RESTE) AS NUMBER(30,2)) AS RESTE,\n" +
                "   cast(sum(ve.reste)* avg(ve.pu) as number(30,2)) as montantReste,\n" +
                "   ai.UNITE,\n" +
                "   u.VAL AS idunitelib,\n" +
                "   avg(ve.PU),\n" +
                "   max(ve.DATY),\n" +
                "   '' as IDFOURNISSEUR,\n" +
                "   '' as IDFOURNISSEURLIB\n" +
                " FROM (\n" +
                "       SELECT\n" +
                "            s.invdaty,\n" +
                "            s.invquantite,\n" +
                "           m.ID,\n" +
                "           m.IDMVTSTOCK,\n" +
                "           m.IDPRODUIT,\n" +
                "           case when s.invFid like 'invvidef%'\n" +
                "           then m.ENTREE\n" +
                "           else s.entree end\n" +
                "               as entree,\n" +
                "           s.SORTIE,\n" +
                "           m.IDVENTEDETAIL,\n" +
                "           m.IDTRANSFERTDETAIL,\n" +
                "           m.PU,\n" +
                "           m.MVTSRC,\n" +
                "           mp.DATY,\n" +
                "           case when s.invFid like 'invvidef%'\n" +
                "           then NVL(m.ENTREE, 0) - NVL(s.SORTIE, 0) + NVL(s.invquantite, 0)\n" +
                "           else NVL(s.ENTREE, 0) - NVL(s.SORTIE, 0) + NVL(s.invquantite, 0)\n" +
                "           end    AS RESTE,\n" +
                "           case when s.invFid like 'invvidef%'\n" +
                "           then NVL(m.ENTREE, 0) - NVL(s.SORTIE, 0) + NVL(s.invquantite, 0)\n" +
                "           else NVL(s.ENTREE, 0) - NVL(s.SORTIE, 0) + NVL(s.invquantite, 0)\n" +
                "           end AS QUANTITE,\n" +
                "           mp.IDMAGASIN,\n" +
                "           b.IDFOURNISSEUR,\n" +
                "           b.IDFOURNISSEURLIB\n" +
                "       FROM mvtSTockFilleVraiSource m\n" +
                "       LEFT JOIN MVTSTOCK mp ON mp.id = m.IDMVTSTOCK\n" +
                "       LEFT JOIN AS_BONDELIVRAISON_LIB b ON b.ID = mp.IDOBJET\n" +
                "\n" +
                "       LEFT JOIN (\n" +
                "           SELECT\n" +
                "    inv.invidproduit AS IDPRODUIT,\n" +
                "    inv.invmvtsrc AS MVTSRC,\n" +
                "    inv.invIdMagasin AS IDMAGASIN,\n" +
                "    inv.invquantite,\n" +
                "    inv.invdaty,\n" +
                "    NVL(mvt.SORTIE, 0) AS SORTIE,\n" +
                "    NVL(mvt.ENTREE, 0) AS ENTREE,\n" +
                "    inv.invFid,\n" +
                "    (NVL(inv.invquantite, 0) + NVL(mvt.ENTREE, 0) - NVL(mvt.SORTIE, 0)) AS STOCK_FINAL\n" +
                "FROM (\n" +
                "    SELECT\n" +
                "        invf.QUANTITE AS invquantite,\n" +
                "        invf.IDPRODUIT AS invidproduit,\n" +
                "        invf.MVTSRC AS invmvtsrc,\n" +
                "        im.daty AS invdaty,\n" +
                "        im.IDMAGASIN AS invIdMagasin,\n" +
                "        invf.id as invFid\n" +
                "    FROM inventaireFilleAvecVide invF\n" +
                "    LEFT JOIN inventaireAvecVide im ON im.ID = invf.IDINVENTAIRE\n" +
                "    WHERE im.ETAT >= 11\n" +
                "      AND im.daty = (\n" +
                "          SELECT MAX(im2.daty)\n" +
                "          FROM inventaireFilleAvecVide invf2\n" +
                "          LEFT JOIN inventaireAvecVide im2 ON im2.id = invf2.IDINVENTAIRE\n" +
                "          WHERE im2.ETAT >= 11\n" +
                "            AND invf2.IDPRODUIT = invf.IDPRODUIT\n" +
                "            AND invf2.MVTSRC = invf.MVTSRC\n" +
                "            AND im2.IDMAGASIN = im.IDMAGASIN\n" +
                "            AND im2.daty <= TO_DATE('20/07/2026', 'DD-MM-YYYY')\n" +
                "      )\n" +
                ") inv\n" +
                "\n" +
                "LEFT JOIN (\n" +
                "    SELECT\n" +
                "        m.MVTSRC,\n" +
                "        mp.IDMAGASIN,\n" +
                "        m.IDPRODUIT,\n" +
                "        SUM(m.SORTIE) AS SORTIE,\n" +
                "        SUM(m.ENTREE) AS ENTREE\n" +
                "    FROM mvtSTockFilleVraiSource m\n" +
                "    JOIN MVTSTOCK mp ON mp.id = m.IDMVTSTOCK\n" +
                "    LEFT JOIN (\n" +
                "        SELECT\n" +
                "            invf3.IDPRODUIT,\n" +
                "            invf3.MVTSRC,\n" +
                "            im3.IDMAGASIN,\n" +
                "            MAX(im3.DATY) AS max_invdaty\n" +
                "        FROM inventaireFilleAvecVide invf3\n" +
                "        LEFT JOIN inventaireAvecVide im3 ON im3.id = invf3.IDINVENTAIRE\n" +
                "        WHERE im3.ETAT >= 11\n" +
                "          AND im3.DATY <= TO_DATE('20/07/2026', 'DD-MM-YYYY')\n" +
                "        GROUP BY invf3.IDPRODUIT, invf3.MVTSRC, im3.IDMAGASIN\n" +
                "    ) inv_ref ON inv_ref.IDPRODUIT = m.IDPRODUIT\n" +
                "             AND inv_ref.MVTSRC = m.MVTSRC\n" +
                "             AND inv_ref.IDMAGASIN = mp.IDMAGASIN\n" +
                "    WHERE mp.ETAT >= 11\n" +
                "      AND mp.DATY <= TO_DATE('20/07/2026', 'DD-MM-YYYY')\n" +
                "      AND mp.DATY > inv_ref.max_invdaty\n" +
                "    GROUP BY m.MVTSRC, mp.IDMAGASIN, m.IDPRODUIT\n" +
                ") mvt\n" +
                "  ON mvt.IDPRODUIT = inv.invidproduit\n" +
                " AND mvt.MVTSRC = inv.invmvtsrc\n" +
                " AND mvt.IDMAGASIN = inv.invIdMagasin\n" +
                "       ) s ON s.MVTSRC = m.id and s.IDMAGASIN=mp.IDMAGASIN\n" +
                "       WHERE  m.ENTREE > 0 --and m.MVTSRC IS NULL\n" +
                "         AND mp.ETAT >= 11\n" +
                "         AND mp.DATY <= TO_DATE('20/07/2026', 'DD-MM-YYYY')\n" +
                ") ve\n" +
                "LEFT JOIN AS_INGREDIENTS ai ON ai.id = ve.IDPRODUIT\n" +
                "LEFT JOIN CATEGORIEINGREDIENT c ON ai.CATEGORIEINGREDIENT = c.id\n" +
                "LEFT JOIN MAGASINPOINT m ON ve.IDMAGASIN = m.id\n" +
                "LEFT JOIN AS_UNITE u ON ai.UNITE = u.id\n" +
                "WHERE\n" +
                "    ve.PU > 0\n" +

                "  and ve.reste !=0 group by ai.id, ai.LIBELLE, ai.CATEGORIEINGREDIENT, c.VAL, ve.IDMAGASIN, ai.typeStock, m.VAL, ai.UNITE, u.VAL";

        //System.out.println(reqAvecInv);

        String reqAvecInvAncien="SELECT\n" +
                "    'invdaty' as invdaty,\n" +
                "    cast(sum(invquantite) as number(30,2)) as invquantite,\n" +
                "    '' as id,\n" +
                "   ai.id AS IDPRODUIT,\n" +
                "   ai.LIBELLE AS idproduitlib,\n" +
                "   ai.CATEGORIEINGREDIENT,\n" +
                "   c.VAL AS idtypeproduitlib,\n" +
                "   ve.IDMAGASIN, ai.typeStock,\n" +
                "   m.VAL AS idmagasinlib,\n" +
                "   sum(ve.QUANTITE) as quantite,\n" +
                "   CAST(sum(ve.ENTREE) AS NUMBER(30,2)) AS ENTREE,\n" +
                "   CAST(sum(ve.SORTIE) AS NUMBER(30,2)) AS SORTIE,\n" +
                "   CAST(sum(ve.RESTE) AS NUMBER(30,2)) AS RESTE,\n" +
                "   cast(sum(ve.reste)* avg(ve.pu) as number(30,2)) as montantReste,\n" +
                "   ai.UNITE,\n" +
                "   u.VAL AS idunitelib,\n" +
                "   avg(ve.PU),\n" +
                "   max(ve.DATY),\n" +
                "   '' as IDFOURNISSEUR,\n" +
                "   '' as IDFOURNISSEURLIB\n" +
                "FROM (\n" +
                "       SELECT\n" +
                "            s.invdaty,\n" +
                "            s.invquantite,\n" +
                "           m.ID,\n" +
                "           m.IDMVTSTOCK,\n" +
                "           m.IDPRODUIT,\n" +
                "           s.ENTREE as entree,\n" +
                "           s.SORTIE,\n" +
                "           m.IDVENTEDETAIL,\n" +
                "           m.IDTRANSFERTDETAIL,\n" +
                "           m.PU,\n" +
                "           m.MVTSRC,\n" +
                "           mp.DATY,\n" +
                "           NVL(s.ENTREE, 0) - NVL(s.SORTIE, 0) + NVL(s.invquantite, 0)  AS RESTE,\n" +
                "           NVL(s.ENTREE, 0) - NVL(s.SORTIE, 0) + NVL(s.invquantite, 0) AS QUANTITE,\n" +
                "           mp.IDMAGASIN,\n" +
                "           b.IDFOURNISSEUR,\n" +
                "           b.IDFOURNISSEURLIB\n" +
                "       FROM mvtSTockFilleVraiSource m\n" +
                "       LEFT JOIN MVTSTOCK mp ON mp.id = m.IDMVTSTOCK\n" +
                "       LEFT JOIN AS_BONDELIVRAISON_LIB b ON b.ID = mp.IDOBJET\n" +
                "\n" +
                "       LEFT JOIN (\n" +
                "           SELECT\n" +
                "    inv.invidproduit AS IDPRODUIT,\n" +
                "    inv.invmvtsrc AS MVTSRC,\n" +
                "    inv.invIdMagasin AS IDMAGASIN,\n" +
                "    inv.invquantite,\n" +
                "    inv.invdaty,\n" +
                "    NVL(mvt.SORTIE, 0) AS SORTIE,\n" +
                "    NVL(mvt.ENTREE, 0) AS ENTREE,\n" +
                "    (NVL(inv.invquantite, 0) + NVL(mvt.ENTREE, 0) - NVL(mvt.SORTIE, 0)) AS STOCK_FINAL\n" +
                "FROM (\n" +
                "    SELECT\n" +
                "        invf.QUANTITE AS invquantite,\n" +
                "        invf.IDPRODUIT AS invidproduit,\n" +
                "        invf.MVTSRC AS invmvtsrc,\n" +
                "        im.daty AS invdaty,\n" +
                "        im.IDMAGASIN AS invIdMagasin\n" +
                "    FROM INVENTAIREFILLE invF\n" +
                "    LEFT JOIN inventaire im ON im.ID = invf.IDINVENTAIRE\n" +
                "    WHERE im.ETAT >= 11\n" +
                "      AND im.daty = (\n" +
                "          SELECT MAX(im2.daty)\n" +
                "          FROM INVENTAIREFILLE invf2\n" +
                "          LEFT JOIN inventaire im2 ON im2.id = invf2.IDINVENTAIRE\n" +
                "          WHERE im2.ETAT >= 11\n" +
                "            AND invf2.IDPRODUIT = invf.IDPRODUIT\n" +
                "            AND invf2.MVTSRC = invf.MVTSRC\n" +
                "            AND im2.IDMAGASIN = im.IDMAGASIN\n" +
                "            AND im2.daty <= TO_DATE('"+daty+"', 'DD-MM-YYYY')\n" +
                "      )\n" +
                ") inv\n" +
                "\n" +
                "LEFT JOIN (\n" +
                "    SELECT\n" +
                "        m.MVTSRC,\n" +
                "        mp.IDMAGASIN,\n" +
                "        m.IDPRODUIT,\n" +
                "        SUM(m.SORTIE) AS SORTIE,\n" +
                "        SUM(m.ENTREE) AS ENTREE\n" +
                "    FROM mvtSTockFilleVraiSource m\n" +
                "    JOIN MVTSTOCK mp ON mp.id = m.IDMVTSTOCK\n" +
                "    LEFT JOIN (\n" +
                "        SELECT\n" +
                "            invf3.IDPRODUIT,\n" +
                "            invf3.MVTSRC,\n" +
                "            im3.IDMAGASIN,\n" +
                "            MAX(im3.DATY) AS max_invdaty\n" +
                "        FROM INVENTAIREFILLE invf3\n" +
                "        LEFT JOIN INVENTAIRE im3 ON im3.id = invf3.IDINVENTAIRE\n" +
                "        WHERE im3.ETAT >= 11\n" +
                "          AND im3.DATY <= TO_DATE('"+daty+"', 'DD-MM-YYYY')\n" +
                "        GROUP BY invf3.IDPRODUIT, invf3.MVTSRC, im3.IDMAGASIN\n" +
                "    ) inv_ref ON inv_ref.IDPRODUIT = m.IDPRODUIT\n" +
                "             AND inv_ref.MVTSRC = m.MVTSRC\n" +
                "             AND inv_ref.IDMAGASIN = mp.IDMAGASIN\n" +
                "    WHERE mp.ETAT >= 11\n" +
                "      AND mp.DATY <= TO_DATE('"+daty+"', 'DD-MM-YYYY')\n" +
                "      AND mp.DATY > inv_ref.max_invdaty\n" +
                "    GROUP BY m.MVTSRC, mp.IDMAGASIN, m.IDPRODUIT\n" +
                ") mvt\n" +
                "  ON mvt.IDPRODUIT = inv.invidproduit\n" +
                " AND mvt.MVTSRC = inv.invmvtsrc\n" +
                " AND mvt.IDMAGASIN = inv.invIdMagasin\n" +
                "       ) s ON s.MVTSRC = m.id and s.IDMAGASIN=mp.IDMAGASIN\n" +
                "       WHERE  m.ENTREE > 0 --and m.MVTSRC IS NULL\n" +
                "         AND mp.ETAT >= 11\n" +
                "         AND mp.DATY <= TO_DATE('"+daty+"', 'DD-MM-YYYY')\n" +
                ") ve\n" +
                "LEFT JOIN AS_INGREDIENTS ai ON ai.id = ve.IDPRODUIT\n" +
                "LEFT JOIN CATEGORIEINGREDIENT c ON ai.CATEGORIEINGREDIENT = c.id\n" +
                "LEFT JOIN MAGASINPOINT m ON ve.IDMAGASIN = m.id\n" +
                "LEFT JOIN AS_UNITE u ON ai.UNITE = u.id\n" +
                "WHERE\n" +
                "    ve.PU > 0\n" +
                "  and ve.reste !=0 group by ai.id, ai.LIBELLE, ai.CATEGORIEINGREDIENT, c.VAL, ve.IDMAGASIN, ai.typeStock, m.VAL, ai.UNITE, u.VAL";

//        String reqReutilise="WITH max_inventaire AS (\n" +
//                "    -- Étape 1 : On extrait la date maximale valide et la quantité par COUPLE produit/magasin\n" +
//                "    SELECT\n" +
//                "        invff.idproduit,\n" +
//                "        imeref.idmagasin,\n" +
//                "        MAX(imeref.DATY) AS max_daty,\n" +
//                "        MAX(invff.QUANTITE) KEEP (DENSE_RANK LAST ORDER BY imeref.DATY) AS qte_max_daty,\n" +
//                "        MAX(invff.pu) KEEP (DENSE_RANK LAST ORDER BY imeref.DATY) AS pu_max_daty\n" +
//                "    FROM INVENTAIREFILLE invff\n" +
//                "    LEFT JOIN inventaireDateHeure imeref ON imeref.ID = invff.IDINVENTAIRE\n" +
//                "    WHERE imeref.ETAT >= 11 and imeref.DATY< to_date('"+daty+"','DD-MM-YYYY') + 1\n" +
//                "    GROUP BY invff.idproduit, imeref.idmagasin\n" +
//                "),\n" +
//                "somme_precalc AS (\n" +
//                "    -- Étape 3 : Calcul des sommes globales agrégées par COUPLE produit/magasin\n" +
//                "    SELECT\n" +
//                "        mff.idproduit as idproduit,\n" +
//                "        meref.idmagasin as idmagasin,\n" +
//                "        NVL(SUM(CASE WHEN meref.DATY >NVL(inv.max_daty, TO_DATE('01/01/2025','DD-MM-YYYY')) THEN mff.ENTREE ELSE 0 END), 0) AS entree,\n" +
//                "\n" +
//                "        NVL(SUM(CASE WHEN meref.DATY > NVL(inv.max_daty, TO_DATE('01/01/2025','DD-MM-YYYY')) THEN mff.SORTIE ELSE 0 END), 0) AS sortie,\n" +
//                "        ai.LIBELLE AS idproduitlib,\n" +
//                "        ai.CATEGORIEINGREDIENT as CATEGORIEINGREDIENT,\n" +
//
//                "             ai.IDMODELE as idmodele,\n" +
//                "             mo.LIBELLE as idmodelelib,\n" +
//                "             mo.IDCATEGORIE as idCategorieModele,\n" +
//                "             camo.VAL as idCategorieModeleLib,\n" +
//                "             scm.id as idSousCategorieModele,\n" +
//                "             scm.VAL as idSousCategorieModeleLib,\n"+
//
//                "             vrc.REFERENCECOULEUR ,\n" +
//                "             vrc.NOMCOULEUR,\n" +
//                "             vrc.CODECOULEUR,\n"+
//                "        ai.UNITE as unite,\n" +
//                "        ai.TYPESTOCK as typestock,\n" +
//                "        ai.marque as idPointure,\n" +
//                "        pt.VAL as idPointureLib,\n" +
//                "        u.VAL AS idunitelib,\n" +
//                "        c.VAL AS idtypeproduitlib,\n" +
//                "        avg(mff.PU) as pu,\n" +
//                "        null as IDFOURNISSEUR,\n" +
//                "        null as IDFOURNISSEURLIB,\n" +
//                "        m.VAL AS idmagasinlib,\n" +
//                "        max(inv.max_daty) AS invdaty,\n" +
//                "        nvl(max(inv.qte_max_daty),0)as invquantite,\n" +
//                "        NVL(SUM(CASE WHEN meref.DATY >NVL(inv.max_daty, TO_DATE('01/01/2025','DD-MM-YYYY')) THEN mff.ENTREE*mff.pu ELSE 0 END), 0) AS montantEntree,\n" +
//                "        NVL(SUM(CASE WHEN meref.DATY > NVL(inv.max_daty, TO_DATE('01/01/2025','DD-MM-YYYY')) THEN mff.SORTIE*mff.pu ELSE 0 END), 0) AS montantSortie,\n" +
//                "        nvl(avg(inv.pu_max_daty*inv.qte_max_daty),0) as montantInv\n" +
//                "    FROM MVTSTOCKFILLE mff\n" +
//                "    LEFT JOIN mvtStockDateHeure meref ON meref.ID = mff.IDMVTSTOCK\n" +
//                "    LEFT JOIN max_inventaire inv ON inv.idproduit = mff.idproduit AND inv.idmagasin = meref.idmagasin\n" +
//                "    LEFT JOIN AS_INGREDIENTS ai ON ai.id = mff.IDPRODUIT\n" +
//                "    LEFT JOIN CATEGORIEINGREDIENT c ON ai.CATEGORIEINGREDIENT = c.id\n" +
//                "    LEFT JOIN MAGASINPOINT m ON meref.IDMAGASIN = m.id\n" +
//                "    LEFT JOIN AS_UNITE u ON ai.UNITE = u.id\n" +
//
//                "    LEFT join modele mo on mo.id = ai.IDMODELE\n" +
//                "    LEFT join categoriemodele camo on camo.ID = mo.IDCATEGORIE\n" +
//                "    LEFT join  souscategoriemodele scm on  scm.ID =mo.IDSOUSCATEGORIE\n"+
//                "    LEFT join pointure pt on pt.ID = ai.MARQUE\n" +
//                "    LEFT join V_REFERENCE_COULEUR vrc on vrc.REFERENCECOULEUR = ai.COULEUR\n"+
//                "    WHERE --meref.DATY > NVL(inv.max_daty, TO_DATE('01/01/2025','DD-MM-YYYY')) and\n" +
//                "      meref.ETAT >= 11 and meref.DATY<to_date('"+daty+"','DD-MM-YYYY')+1\n" +
//                "      AND meref.IDTYPEMVSTOCK != 'TPMVST000023'\n" +
//                "    GROUP BY mff.idproduit, meref.idmagasin,ai.LIBELLE,ai.CATEGORIEINGREDIENT,ai.UNITE,ai.TYPESTOCK,u.VAL,c.VAL,m.VAL, ai.IDMODELE, mo.IDCATEGORIE , mo.LIBELLE , camo.VAL, scm.ID , scm.VAL, ai.MARQUE,pt.VAL,vrc.REFERENCECOULEUR ,vrc.NOMCOULEUR,vrc.CODECOULEUR\n" +
//                ")\n" +
//                "-- Sélection finale\n" +
//                "SELECT\n" +
//                "    null as ID, null as DATY,  cast(invquantite as number(30,2)) as invquantite,cast(entree as number(30,2)) as entree, cast(sortie as number(30,2)) as sortie,CAST((invquantite)+(ENTREE)-(sortie) AS NUMBER(30,2)) as reste,\n" +
//                "    CAST(invquantite+ENTREE-sortie AS NUMBER(30,2)) AS quantite,\n" +
//                "    CAST((montantInv+montantEntree-montantSortie)AS NUMBER(30,2))AS montantreste,\n" +
//                "    cast(\n" +
//                "        CASE WHEN (invquantite+ENTREE-sortie) = 0 THEN NULL\n" +
//                "             ELSE (montantInv+montantEntree-montantSortie)/(invquantite+ENTREE-sortie)\n" +
//                "        END\n" +
//                "    as number(30,2)) as pu,\n" +
//                "    idproduitlib, CATEGORIEINGREDIENT, UNITE, TYPESTOCK, idunitelib, idtypeproduitlib,\n" +
//                "     IDFOURNISSEUR, IDFOURNISSEURLIB, IDPRODUIT, IDMAGASIN, idmagasinlib,invdaty,\n" +
//                "    IDPRODUIT || ' ' || idproduitlib || ' ' ||\n" +
//                "    IDMAGASIN || ' ' || idmagasinlib || ' ' ||\n" +
//                "    CATEGORIEINGREDIENT || ' ' || idtypeproduitlib || ' ' ||\n" +
//                "    UNITE || ' ' || idunitelib AS motsclesss,\n" +
//
//                "    idmodele,\n" +
//                "    idmodelelib,\n" +
//                "    idCategorieModele,\n" +
//                "    idCategorieModeleLib,\n" +
//                "    idSousCategorieModele,\n" +
//                "    idSousCategorieModeleLib,\n" +
//                "    idPointure,\n" +
//                "    idPointureLib,\n"+
//                "    REFERENCECOULEUR,\n" +
//                "    NOMCOULEUR,\n" +
//                "    CODECOULEUR\n"+
//
//                "FROM somme_precalc\n" +
//                "WHERE invquantite+ENTREE-sortie != 0";

        String reqReutilise = "";
        if (UtilitaireAsync.isOracle(c)) {
            reqReutilise = "WITH max_inventaire AS (\n" +
                    "    -- Etape 1 : On extrait la date maximale valide et la quantite par COUPLE produit/magasin\n" +
                    "    SELECT\n" +
                    "        invff.idproduit,\n" +
                    "        imeref.idmagasin,\n" +
                    "        MAX(imeref.DATY) AS max_daty,\n" +
                    "        MAX(invff.QUANTITE) KEEP (DENSE_RANK LAST ORDER BY imeref.DATY) AS qte_max_daty,\n" +
                    "        MAX(invff.pu) KEEP (DENSE_RANK LAST ORDER BY imeref.DATY) AS pu_max_daty\n" +
                    "    FROM INVENTAIREFILLE invff\n" +
                    "             LEFT JOIN inventaireDateHeure imeref ON imeref.ID = invff.IDINVENTAIRE\n" +
                    "    WHERE imeref.ETAT >= 11 and imeref.DATY< to_date('"+daty+"','DD-MM-YYYY') + 1\n" +
                    "    GROUP BY invff.idproduit, imeref.idmagasin\n" +
                    "),\n" +
                    "     somme_precalc AS (\n" +
                    "         -- Etape 3 : Calcul des sommes globales agregees par COUPLE produit/magasin\n" +
                    "         SELECT\n" +
                    "             mff.idproduit as idproduit,\n" +
                    "             meref.idmagasin as idmagasin,\n" +
                    "             NVL(SUM(CASE WHEN meref.DATY >NVL(inv.max_daty, TO_DATE('01/01/2025','DD-MM-YYYY')) THEN mff.ENTREE ELSE 0 END), 0) AS entree,\n" +
                    "\n" +
                    "             NVL(SUM(CASE WHEN meref.DATY > NVL(inv.max_daty, TO_DATE('01/01/2025','DD-MM-YYYY')) THEN mff.SORTIE ELSE 0 END), 0) AS sortie,\n" +
                    "             ai.LIBELLE AS idproduitlib,\n" +
                    "             ai.CATEGORIEINGREDIENT as CATEGORIEINGREDIENT,\n" +
                    "             ai.IDMODELE as idmodele,\n" +
                    "             mo.LIBELLE as idmodelelib,\n" +
                    "             mo.IDCATEGORIE as idCategorieModele,\n" +
                    "             camo.VAL as idCategorieModeleLib,\n" +
                    "             scm.id as idSousCategorieModele,\n" +
                    "             scm.VAL as idSousCategorieModeleLib,\n" +
                    "             ai.UNITE as unite,\n" +
                    "             ai.TYPESTOCK as typestock,\n" +
                    "             ai.marque as idPointure,\n" +
                    "             pt.VAL as idPointureLib,\n" +
                    "             u.VAL AS idunitelib,\n" +
                    "             c.VAL AS idtypeproduitlib,\n" +
                    "             vrc.REFERENCECOULEUR ,\n" +
                    "             vrc.NOMCOULEUR || '-' || vrc.REFERENCECOULEUR as NOMCOULEUR,\n" +
                    "             vrc.CODECOULEUR,\n" +
                    "             avg(mff.PU) as pu,\n" +
                    "             null as IDFOURNISSEUR,\n" +
                    "             null as IDFOURNISSEURLIB,\n" +
                    "             m.VAL AS idmagasinlib,\n" +
                    "             max(inv.max_daty) AS invdaty,\n" +
                    "             nvl(max(inv.qte_max_daty),0)as invquantite,\n" +
                    "             NVL(SUM(CASE WHEN meref.DATY >NVL(inv.max_daty, TO_DATE('01/01/2025','DD-MM-YYYY')) THEN mff.ENTREE*mff.pu ELSE 0 END), 0) AS montantEntree,\n" +
                    "             NVL(SUM(CASE WHEN meref.DATY > NVL(inv.max_daty, TO_DATE('01/01/2025','DD-MM-YYYY')) THEN mff.SORTIE*mff.pu ELSE 0 END), 0) AS montantSortie,\n" +
                    "             nvl(avg(inv.pu_max_daty*inv.qte_max_daty),0) as montantInv, nvl(max(ai.SEUIL),0) as seuil,nvl(max(ai.SEUILMAX),nvl(max(ai.SEUILMIN),0)+1000) as seuilmax,nvl(max(ai.SEUILMIN),0) as seuilMin\n" +
                    "         FROM MVTSTOCKFILLE mff\n" +
                    "                  LEFT JOIN mvtStockDateHeure meref ON meref.ID = mff.IDMVTSTOCK\n" +
                    "                  LEFT JOIN max_inventaire inv ON inv.idproduit = mff.idproduit AND inv.idmagasin = meref.idmagasin\n" +
                    "                  LEFT JOIN AS_INGREDIENTS ai ON ai.id = mff.IDPRODUIT\n" +
                    "                  LEFT JOIN CATEGORIEINGREDIENT c ON ai.CATEGORIEINGREDIENT = c.id\n" +
                    "                  LEFT JOIN MAGASINPOINT m ON meref.IDMAGASIN = m.id\n" +
                    "                  LEFT JOIN AS_UNITE u ON ai.UNITE = u.id\n" +
                    "                  LEFT join modele mo on mo.id = ai.IDMODELE\n" +
                    "                  LEFT join categoriemodele camo on camo.ID = mo.IDCATEGORIE\n" +
                    "                  LEFT join  souscategoriemodele scm on  scm.ID =mo.IDSOUSCATEGORIE\n" +
                    "                  LEFT join pointure pt on pt.ID = ai.MARQUE\n" +
//                "                  LEFT JOIN COULEUR coul ON coul.id = ai.COULEUR\n" +
                    "                  LEFT join V_REFERENCE_COULEUR vrc on vrc.REFERENCECOULEUR = ai.COULEUR\n" +
                    "         WHERE --meref.DATY > NVL(inv.max_daty, TO_DATE('01/01/2025','DD-MM-YYYY')) and\n" +
                    "      meref.ETAT >= 11 and meref.DATY<to_date('"+daty+"','DD-MM-YYYY')+1\n" +
                    "           AND meref.IDTYPEMVSTOCK != 'TPMVST000023'\n" +
                    "         GROUP BY mff.idproduit, meref.idmagasin,ai.LIBELLE,ai.CATEGORIEINGREDIENT,ai.UNITE,ai.TYPESTOCK,u.VAL,c.VAL,m.VAL, ai.IDMODELE, mo.IDCATEGORIE, camo.VAL, mo.LIBELLE, scm.ID , scm.VAL, ai.MARQUE,pt.VAL,vrc.REFERENCECOULEUR ,vrc.NOMCOULEUR,vrc.CODECOULEUR\n" +
                    "     )\n" +
                    "-- Selection finale\n" +
                    "SELECT\n" +
                    "    NULL AS id,\n" +
                    "    NULL AS daty,\n" +
                    "\n" +
                    "    CAST(NVL(invquantite,0) AS NUMBER(30,2)) AS invquantite,\n" +
                    "    CAST(NVL(entree,0) AS NUMBER(30,2)) AS entree,\n" +
                    "    CAST(NVL(sortie,0) AS NUMBER(30,2)) AS sortie,\n" +
                    "\n" +
                    "    CAST(\n" +
                    "            NVL(invquantite,0)\n" +
                    "                + NVL(entree,0)\n" +
                    "                - NVL(sortie,0)\n" +
                    "        AS NUMBER(30,2)) AS reste,\n" +
                    "\n" +
                    "    CAST(\n" +
                    "            NVL(invquantite,0)\n" +
                    "                + NVL(entree,0)\n" +
                    "                - NVL(sortie,0)\n" +
                    "        AS NUMBER(30,2)) AS quantite,\n" +
                    "\n" +
                    "    CAST(\n" +
                    "            NVL(montantInv,0)\n" +
                    "                + NVL(montantEntree,0)\n" +
                    "                - NVL(montantSortie,0)\n" +
                    "        AS NUMBER(30,2)) AS montantreste,\n" +
                    "\n" +
                    "    CASE\n" +
                    "        WHEN NVL(invquantite,0)\n" +
                    "                 + NVL(entree,0)\n" +
                    "                 - NVL(sortie,0) = 0\n" +
                    "            THEN 0\n" +
                    "        ELSE CAST(\n" +
                    "                (\n" +
                    "                    NVL(montantInv,0)\n" +
                    "                        + NVL(montantEntree,0)\n" +
                    "                        - NVL(montantSortie,0)\n" +
                    "                    )\n" +
                    "                    / (\n" +
                    "                    NVL(invquantite,0)\n" +
                    "                        + NVL(entree,0)\n" +
                    "                        - NVL(sortie,0)\n" +
                    "                    )\n" +
                    "            AS NUMBER(30,2))\n" +
                    "        END AS pu,\n" +
                    "\n" +
                    "    idproduitlib,\n" +
                    "\n" +
                    "    idmodele,\n" +
                    "    idmodelelib,\n" +
                    "    idCategorieModele,\n" +
                    "    idCategorieModeleLib,\n" +
                    "    idSousCategorieModele,\n" +
                    "    idSousCategorieModeleLib,\n" +
                    "    idPointure,\n" +
                    "    idPointureLib,\n" +
                    "    categorieingredient,\n" +
                    "    unite,\n" +
                    "    typestock,\n" +
                    "    idunitelib,\n" +
                    "    idtypeproduitlib,\n" +
                    "    idfournisseur,\n" +
                    "    idfournisseurlib,\n" +
                    "    idproduit,\n" +
                    "    idmagasin,\n" +
                    "    idmagasinlib,\n" +
                    "    invdaty,\n" +
                    "    REFERENCECOULEUR,\n" +
                    "    NOMCOULEUR,\n" +
                    "    CODECOULEUR,\n" +
                    "    CAST(NVL(seuil,0) AS NUMBER(30,2)) AS seuil,\n" +
                    "    CAST(NVL(seuilMin,0) AS NUMBER(30,2)) AS seuilMin,\n" +
                    "    CAST(NVL(seuilMax,0) AS NUMBER(30,2)) AS seuilMax\n" +
                    "\n" +
                    "FROM somme_precalc";
        }
        if (UtilitaireAsync.isPostgres(c)) {
            reqReutilise = "WITH max_inventaire AS (\n" +
                    "    -- Etape 1 : date max valide + quantite/pu a cette date, par couple produit/magasin\n" +
                    "    SELECT\n" +
                    "        invff.idproduit,\n" +
                    "        imeref.idmagasin,\n" +
                    "        MAX(imeref.daty) AS max_daty,\n" +
                    "        (array_agg(invff.quantite ORDER BY imeref.daty DESC, invff.quantite DESC NULLS LAST))[1] AS qte_max_daty,\n" +
                    "        (array_agg(invff.pu       ORDER BY imeref.daty DESC, invff.pu       DESC NULLS LAST))[1] AS pu_max_daty\n" +
                    "    FROM inventairefille invff\n" +
                    "             LEFT JOIN inventairedateheure imeref ON imeref.id = invff.idinventaire\n" +
                    "    WHERE imeref.etat >= 11\n" +
                    "      AND imeref.daty < to_date('"+daty+"', 'DD/MM/YYYY') + 1\n" +
                    "    GROUP BY invff.idproduit, imeref.idmagasin\n" +
                    "),\n" +
                    "somme_precalc AS (\n" +
                    "    -- Etape 2 : sommes agregees par couple produit/magasin\n" +
                    "    SELECT\n" +
                    "        mff.idproduit AS idproduit,\n" +
                    "        meref.idmagasin AS idmagasin,\n" +
                    "        COALESCE(SUM(CASE WHEN meref.daty > COALESCE(inv.max_daty, DATE '2025-01-01') THEN mff.entree ELSE 0 END), 0) AS entree,\n" +
                    "        COALESCE(SUM(CASE WHEN meref.daty > COALESCE(inv.max_daty, DATE '2025-01-01') THEN mff.sortie ELSE 0 END), 0) AS sortie,\n" +
                    "        ai.libelle AS idproduitlib,\n" +
                    "        ai.categorieingredient AS categorieingredient,\n" +
                    "        ai.idmodele AS idmodele,\n" +
                    "        mo.libelle AS idmodelelib,\n" +
                    "        mo.idcategorie AS idcategoriemodele,\n" +
                    "        camo.val AS idcategoriemodelelib,\n" +
                    "        scm.id AS idsouscategoriemodele,\n" +
                    "        scm.val AS idsouscategoriemodelelib,\n" +
                    "        ai.unite AS unite,\n" +
                    "        ai.typestock AS typestock,\n" +
                    "        ai.marque AS idpointure,\n" +
                    "        pt.val AS idpointurelib,\n" +
                    "        u.val AS idunitelib,\n" +
                    "        c.val AS idtypeproduitlib,\n" +
                    "        vrc.referencecouleur,\n" +
                    "        concat(vrc.nomcouleur, '-', vrc.referencecouleur) AS nomcouleur,\n" +
                    "        vrc.codecouleur,\n" +
                    "        AVG(mff.pu) AS pu,\n" +
                    "        NULL::varchar AS idfournisseur,\n" +
                    "        NULL::varchar AS idfournisseurlib,\n" +
                    "        m.val AS idmagasinlib,\n" +
                    "        MAX(inv.max_daty) AS invdaty,\n" +
                    "        COALESCE(MAX(inv.qte_max_daty), 0) AS invquantite,\n" +
                    "        COALESCE(SUM(CASE WHEN meref.daty > COALESCE(inv.max_daty, DATE '2025-01-01') THEN mff.entree * mff.pu ELSE 0 END), 0) AS montantentree,\n" +
                    "        COALESCE(SUM(CASE WHEN meref.daty > COALESCE(inv.max_daty, DATE '2025-01-01') THEN mff.sortie * mff.pu ELSE 0 END), 0) AS montantsortie,\n" +
                    "        COALESCE(AVG(inv.pu_max_daty * inv.qte_max_daty), 0) AS montantinv,\n" +
                    "        COALESCE(MAX(ai.seuil), 0) AS seuil,\n" +
                    "        COALESCE(MAX(ai.seuilmax), COALESCE(MAX(ai.seuilmin), 0) + 1000) AS seuilmax,\n" +
                    "        COALESCE(MAX(ai.seuilmin), 0) AS seuilmin\n" +
                    "    FROM mvtstockfille mff\n" +
                    "             LEFT JOIN mvtstockdateheure meref ON meref.id = mff.idmvtstock\n" +
                    "             LEFT JOIN max_inventaire inv ON inv.idproduit = mff.idproduit AND inv.idmagasin = meref.idmagasin\n" +
                    "             LEFT JOIN as_ingredients ai ON ai.id = mff.idproduit\n" +
                    "             LEFT JOIN categorieingredient c ON ai.categorieingredient = c.id\n" +
                    "             LEFT JOIN magasinpoint m ON meref.idmagasin = m.id\n" +
                    "             LEFT JOIN as_unite u ON ai.unite = u.id\n" +
                    "             LEFT JOIN modele mo ON mo.id = ai.idmodele\n" +
                    "             LEFT JOIN categoriemodele camo ON camo.id = mo.idcategorie\n" +
                    "             LEFT JOIN souscategoriemodele scm ON scm.id = mo.idsouscategorie\n" +
                    "             LEFT JOIN pointure pt ON pt.id = ai.marque\n" +
                    "             LEFT JOIN v_reference_couleur vrc ON vrc.referencecouleur = ai.couleur\n" +
                    "    WHERE meref.etat >= 11\n" +
                    "      AND meref.daty < to_date('"+daty+"', 'DD/MM/YYYY') + 1\n" +
                    "      AND meref.idtypemvstock <> 'TPMVST000023'\n" +
                    "    GROUP BY mff.idproduit, meref.idmagasin, ai.libelle, ai.categorieingredient, ai.unite, ai.typestock,\n" +
                    "             u.val, c.val, m.val, ai.idmodele, mo.idcategorie, camo.val, mo.libelle, scm.id, scm.val,\n" +
                    "             ai.marque, pt.val, vrc.referencecouleur, vrc.nomcouleur, vrc.codecouleur\n" +
                    ")\n" +
                    "-- Selection finale\n" +
                    "SELECT\n" +
                    "    NULL::varchar AS id,\n" +
                    "    NULL::date    AS daty,\n" +
                    "    CAST(COALESCE(invquantite, 0) AS numeric(30,2)) AS invquantite,\n" +
                    "    CAST(COALESCE(entree, 0)      AS numeric(30,2)) AS entree,\n" +
                    "    CAST(COALESCE(sortie, 0)      AS numeric(30,2)) AS sortie,\n" +
                    "    CAST(COALESCE(invquantite, 0) + COALESCE(entree, 0) - COALESCE(sortie, 0) AS numeric(30,2)) AS reste,\n" +
                    "    CAST(COALESCE(invquantite, 0) + COALESCE(entree, 0) - COALESCE(sortie, 0) AS numeric(30,2)) AS quantite,\n" +
                    "    CAST(COALESCE(montantinv, 0) + COALESCE(montantentree, 0) - COALESCE(montantsortie, 0) AS numeric(30,2)) AS montantreste,\n" +
                    "    CASE\n" +
                    "        WHEN COALESCE(invquantite, 0) + COALESCE(entree, 0) - COALESCE(sortie, 0) = 0 THEN 0\n" +
                    "        ELSE CAST(\n" +
                    "                (COALESCE(montantinv, 0) + COALESCE(montantentree, 0) - COALESCE(montantsortie, 0))\n" +
                    "              / (COALESCE(invquantite, 0) + COALESCE(entree, 0) - COALESCE(sortie, 0))\n" +
                    "             AS numeric(30,2))\n" +
                    "    END AS pu,\n" +
                    "    idproduitlib,\n" +
                    "    idmodele,\n" +
                    "    idmodelelib,\n" +
                    "    idcategoriemodele,\n" +
                    "    idcategoriemodelelib,\n" +
                    "    idsouscategoriemodele,\n" +
                    "    idsouscategoriemodelelib,\n" +
                    "    idpointure,\n" +
                    "    idpointurelib,\n" +
                    "    categorieingredient,\n" +
                    "    unite,\n" +
                    "    typestock,\n" +
                    "    idunitelib,\n" +
                    "    idtypeproduitlib,\n" +
                    "    idfournisseur,\n" +
                    "    idfournisseurlib,\n" +
                    "    idproduit,\n" +
                    "    idmagasin,\n" +
                    "    idmagasinlib,\n" +
                    "    invdaty,\n" +
                    "    referencecouleur,\n" +
                    "    nomcouleur,\n" +
                    "    codecouleur,\n" +
                    "    CAST(COALESCE(seuil, 0)    AS numeric(30,2)) AS seuil,\n" +
                    "    CAST(COALESCE(seuilmin, 0) AS numeric(30,2)) AS seuilmin,\n" +
                    "    CAST(COALESCE(seuilmax, 0) AS numeric(30,2)) AS seuilmax\n" +
                    "FROM somme_precalc";
        }

        if (idPointureLib1 != null && !idPointureLib1.trim().isEmpty()
                && idPointureLib2 != null && !idPointureLib2.trim().isEmpty()) {
            reqReutilise  += " WHERE idPointureLib BETWEEN '" + idPointureLib1.trim()
                    + "' AND '" + idPointureLib2.trim() + "' ";
        }

//        System.out.println(reqReutilise);
        return reqReutilise;
    }

    public void setMontantReste(double montantReste) {
        this.montantReste = montantReste;
    }

    public double getMontantSortie() {
        return montantSortie;
    }

    public void setMontantSortie(double montantSortie) {
        this.montantSortie = montantSortie;
    }

    public double getMontantEntree() {
        return montantEntree;
    }

    public void setMontantEntree(double montantEntree) {
        this.montantEntree = montantEntree;
    }

    public EtatStock() {
        this.setNomTable("v_etatstock_ing");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdProduitLib() {
        return idProduitLib;
    }

    public void setIdProduitLib(String idProduitLib) {
        this.idProduitLib = idProduitLib;
    }

    public String getIdTypeProduit() {
        return idTypeProduit;
    }

    public void setIdTypeProduit(String idTypeProduit) {
        this.idTypeProduit = idTypeProduit;
    }

    public String getIdTypeProduitLib() {
        return idTypeProduitLib;
    }

    public void setIdTypeProduitLib(String idTypeProduitLib) {
        this.idTypeProduitLib = idTypeProduitLib;
    }

    public String getIdMagasin() {
        return idMagasin;
    }

    public void setIdMagasin(String idMagasin) {
        this.idMagasin = idMagasin;
    }

    public String getIdMagasinLib() {
        return idMagasinLib;
    }

    public void setIdMagasinLib(String idMagasinLib) {
        this.idMagasinLib = idMagasinLib;
    }

    public Date getDateDernierInventaire() {
        return dateDernierInventaire;
    }

    public void setDateDernierInventaire(Date dateDernierInventaire) {
        this.dateDernierInventaire = dateDernierInventaire;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public double getEntree() {
        return entree;
    }

    public void setEntree(double entree) {
        this.entree = entree;
    }

    public double getSortie() {
        return sortie;
    }

    public void setSortie(double sortie) {
        this.sortie = sortie;
    }

    public double getReste() {
        return reste;
    }

    public void setReste(double reste) {
        this.reste = reste;
    }

    public String getIdUnite() {
        return idUnite;
    }

    public void setIdUnite(String idUnite) {
        this.idUnite = idUnite;
    }

    public String getIdUniteLib() {
        return idUniteLib;
    }

    public void setIdUniteLib(String idUniteLib) {
        this.idUniteLib = idUniteLib;
    }

    public String getIdPointLib() {
        return idPointLib;
    }

    public void setIdPointLib(String idPointLib) {
        this.idPointLib = idPointLib;
    }

    public String getIdPoint() {
        return idPoint;
    }

    public void setIdPoint(String idPoint) {
        this.idPoint = idPoint;
    }

    public String getFieldDateName() {
        return "dateDernierInventaire";
    }

       public double getPuVente() {
              return puVente;
       }

       public void setPuVente(double puVente) {
              this.puVente = puVente;
       }

    public double getPu() {
        return pu;
    }

    public void setPu(double pu) {
        this.pu = pu;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public String generateQueryCore(Date dateMin, Date dateMax) {
        String query =  " SELECT  " +
                    "	inv.IDPRODUIT AS ID, " +
                    "	p.libelle AS idproduitLib, " +
                    "	p.CATEGORIEINGREDIENT, " +
                    "	tp.desce AS idtypeproduitlib, " +
                    "	inv.idmagasin, " +
                    "	mag.desce AS idmagasinlib, " +
                    "	inv.DATY dateDernierinventaire, " +
                    "	NVL(inv.QUANTITE,0) QUANTITE, " +
                    "	NVL(mvt.ENTREE,0) ENTREE,  " +
                    "	NVL(mvt.SORTIE,0) SORTIE,  " +
                    "	NVL(mvt.ENTREE,0)+NVL(inv.QUANTITE,0)-NVL(mvt.SORTIE,0) reste, " +
                    "	p.UNITE, " +
                    "	u.desce AS idunitelib, " +
                    "   CAST(NVL(p.PV ,0) AS NUMBER(30,2)) PUVENTE, " +
                    "	mag.IDPOINT, " +
                    "	mag.IDTYPEMAGASIN "+
                    "FROM  " +
                    "	INVENTAIRE_FILLE_CPL inv, " +
                    "	( " +
                    "       SELECT  " +
                    "			inv.IDPRODUIT , " +
                    "                   inv.IDMAGASIN, "+
                    "			MAX(inv.DATY) maxDateInventaire " +
                    "		FROM  " +
                    "			INVENTAIRE_FILLE_CPL inv  " +
                    "		WHERE  " +
                    "			inv.ETAT = 11  " +
                    "			AND inv.DATY <= '"+Utilitaire.datetostring(dateMin)+"' " +
                    "		GROUP BY inv.IDPRODUIT,inv.IDMAGASIN " +
                    "	) invm, " +
                    "	( " +
                    "		SELECT  " +
                    "			m.IDPRODUIT , " +
                    "                   dinv.IDMAGASIN, "+
                    "			SUM(nvl(m.ENTREE,0)) ENTREE ,  " +
                    "			SUM(nvl(m.SORTIE ,0)) SORTIE  " +
                    "		FROM  " +
                    "			MVTSTOCKFILLELIB m , " +
                    "			( " +
                    "			SELECT  " +
                    "				inv.IDPRODUIT , " +
                    "                           inv.IDMAGASIN,"+
                    "				MAX(inv.DATY) maxDateInventaire " +
                    "			FROM  " +
                    "				INVENTAIRE_FILLE_CPL inv  " +
                    "			WHERE  " +
                    "				inv.ETAT = 11  " +
                    "				AND inv.DATY <= '"+Utilitaire.datetostring(dateMin)+"' " +
                    "			GROUP BY inv.IDPRODUIT,inv.IDMAGASIN " +
                    "			) dinv " +
                    "		WHERE  " +
                    "			m.IDPRODUIT = dinv.IDPRODUIT(+) " +
                    "                   AND m.IDMAGASIN = dinv.IDMAGASIN(+)"+
                    "			AND m.DATY > dinv.maxDateInventaire " +
                    "			AND m.DATY <= '"+Utilitaire.datetostring(dateMax)+"' " +
                    "		GROUP BY m.IDPRODUIT,dinv.IDMAGASIN " +
                    "	) mvt, " +
                    "	as_ingredients p, " +
                    "	CATEGORIEINGREDIENT tp, " +
                    "	magasin mag, " +
                    "	unite u " +
                    "WHERE  " +
                    "	inv.DATY = invm.maxDateInventaire " +
                    "	AND inv.IDMAGASIN = invm.IDMAGASIN " +
                    "	AND inv.IDPRODUIT = invm.IDPRODUIT " +
                    "	AND inv.IDPRODUIT = mvt.IDPRODUIT(+) " +
                    "	AND inv.IDMAGASIN = mvt.IDMAGASIN(+) " +
                    "	AND inv.IDPRODUIT = p.ID(+) " +
                    "	AND p.CATEGORIEINGREDIENT = tp.ID " +
                    "	AND inv.idmagasin = mag.ID " +
                    "	AND p.UNITE = u.ID(+) "+
                    "	AND ( mvt.sortie > 0  OR mvt.ENTREE > 0 ) "+
                    "   AND inv.ETAT >= 11 ";
        return query;
    }

    public double getTotalVente() {
        return getQuantite() * getPuVente();
    }

    @Override
    public String getTuppleID() {
        return this.id;
    }

    @Override
    public String getAttributIDName() {
        return "id";
    }
    
    @Override
    public String getValColLibelle() {
        return this.getIdProduitLib()+";"+this.getPuVente();
    }

    public DemandeTransfertFille[] genererDemandeTransfertMagasin(Connection c) throws Exception {
        DemandeTransfertFille[] demandeTransfertFilles = null;
        try {
            if (c == null){
                c = new UtilDB().GetConn();
                c.setAutoCommit(false);
            }
            EtatStock[] etatStocks = (EtatStock[]) CGenUtil.rechercher(this, null, null, "");
            demandeTransfertFilles = new DemandeTransfertFille[etatStocks.length];
            for (int i = 0; i < etatStocks.length; i++) {
                DemandeTransfertFille demandeTransfertFille = new DemandeTransfertFille();
                demandeTransfertFille.setIdProduit(etatStocks[i].getIdProduit());
                demandeTransfertFille.setRemarque(etatStocks[i].getIdProduitLib());
                demandeTransfertFille.setQuantite(etatStocks[i].getQteDemande());
                demandeTransfertFilles[i] = demandeTransfertFille;
            }
            return demandeTransfertFilles;
        } catch (Exception e) {
            c.rollback();
            throw new Exception(e);
        } finally {
            if (c != null){
                c.commit();
                c.close();
            }
        }
    }
       
}
