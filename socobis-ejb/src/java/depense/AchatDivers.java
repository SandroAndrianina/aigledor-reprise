package depense;

import bean.AdminGen;
import bean.CGenUtil;
import bean.ClassMere;
import caisse.MvtCaisse;
import faturefournisseur.As_BonDeLivraison;
import faturefournisseur.As_BonDeLivraison_Fille;
import faturefournisseur.FactureFournisseur;
import faturefournisseur.FactureFournisseurDetails;
import paiement.LiaisonPaiement;
import utilitaire.ConstanteEtat;
import utilitaire.UtilDB;
import utilitaire.Utilitaire;
import utils.ConstanteSocobis;

import java.sql.Connection;
import java.sql.Date;

public class AchatDivers extends ClassMere {
    private String id;
    private Date daty;
    private String fournisseur;
    private String designation;
    private String idCaisse;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public String getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getIdCaisse() {
        return idCaisse;
    }

    public void setIdCaisse(String idCaisse) {
        this.idCaisse = idCaisse;
    }

    public AchatDivers() throws Exception {
        this.setNomTable("ACHATDIVERS");
        this.setNomClasseFille("depense.AchatDiversFille");
        this.setLiaisonFille("idMere");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("AD","getseqachatDivers");
        this.setId(makePK(c));
    }

    @Override
    public String getTuppleID() {
        return id;
    }

    @Override
    public String getAttributIDName() {
        return "id";
    }

    public As_BonDeLivraison_Fille[] genererBonDeLivraisonFille(Connection c)throws Exception{
        AchatDiversFille depenseDiversFille = new AchatDiversFille();
        depenseDiversFille.setIdMere(this.getId());
        AchatDiversFille[] depenseDiversFilles = (AchatDiversFille[]) CGenUtil.rechercher(depenseDiversFille, null, null, c, "");
        As_BonDeLivraison_Fille[] as_BonDeLivraison_Filles = new As_BonDeLivraison_Fille[depenseDiversFilles.length];
        for (int i = 0; i < depenseDiversFilles.length; i++) {
            as_BonDeLivraison_Filles[i] = new As_BonDeLivraison_Fille();
//            ING000T0134D Produit Divers
            as_BonDeLivraison_Filles[i].setProduit("ING000T0134D");
            as_BonDeLivraison_Filles[i].setDesignation(depenseDiversFilles[i].getDesignation());
            as_BonDeLivraison_Filles[i].setQuantite(depenseDiversFilles[i].getQuantite());
            as_BonDeLivraison_Filles[i].setPu(depenseDiversFilles[i].getPu());
        }
        return as_BonDeLivraison_Filles;
    }

    public As_BonDeLivraison genererBonDeLivaison()throws Exception{
        As_BonDeLivraison as = new As_BonDeLivraison();
        as.setRemarque("Bon de livraison Achat Divers "+this.getId());
        as.setDaty(this.getDaty());
        as.setIdFournisseur(this.getFournisseur());
        as.setEtat(ConstanteEtat.getEtatCreer());
        return as;
    }

    public FactureFournisseur genererFactureFournisseur(As_BonDeLivraison as)throws Exception{
        FactureFournisseur factureFournisseur = new FactureFournisseur();
        factureFournisseur.setIdFournisseur(as.getIdFournisseur());
//        Mode de paiement espece
        factureFournisseur.setIdModePaiement("ESP");
        factureFournisseur.setTypeFactureFournisseur(ConstanteSocobis.typeFacturePrincipale);
        factureFournisseur.setDaty(as.getDaty());
        factureFournisseur.setDesignation("Facture Fournisseur Achat Divers "+this.getId());
        factureFournisseur.setIdObjet(this.getId());
        factureFournisseur.setDateEcheancePaiement(as.getDaty());
        factureFournisseur.setIdMagasin(as.getMagasin());
        factureFournisseur.setIdDevise("AR");
        factureFournisseur.setDevise("AR");
        factureFournisseur.setEstPrevu(0);
        factureFournisseur.setDatyPrevu(as.getDaty());
//        Type Achat fourniture de bureau
        factureFournisseur.setTypeachat("TA0002");
        return factureFournisseur;
    }

    public FactureFournisseurDetails[] genererFactureFournisseurDetails(As_BonDeLivraison_Fille[] as, Connection c)throws Exception{
        FactureFournisseurDetails[] factureFournisseurDetails = new FactureFournisseurDetails[as.length];
        for (int i = 0; i < as.length; i++) {
            factureFournisseurDetails[i] = new FactureFournisseurDetails();
            factureFournisseurDetails[i].setIdProduit(as[i].getProduit());
            factureFournisseurDetails[i].setPu(as[i].getPu());
            factureFournisseurDetails[i].setTauxDeChange(1);
            factureFournisseurDetails[i].setIdDevise("AR");
//            Compte achat produit divers
            factureFournisseurDetails[i].setCompte("606500");
            factureFournisseurDetails[i].setMois(Utilitaire.getMois(this.getDaty()));
            factureFournisseurDetails[i].setAnnee(Utilitaire.getAnnee(this.getDaty()));
            factureFournisseurDetails[i].setQte(as[i].getQuantite());
            factureFournisseurDetails[i].setDesignation(as[i].getDesignation());
        }
        return factureFournisseurDetails;
    }

    public LiaisonPaiement genererLiaisonPaiement(String idMvtCaisse, String idFactureFournisseur, double montant)throws Exception{
        LiaisonPaiement liaisonPaiement = new LiaisonPaiement();
        liaisonPaiement.setId1(idMvtCaisse);
        liaisonPaiement.setId2(idFactureFournisseur);
        liaisonPaiement.setMontant(montant);
        return liaisonPaiement;
    }

    public MvtCaisse genererMvtCaisse(FactureFournisseur ff, double montant)throws Exception{
        MvtCaisse mvtCaisse = new MvtCaisse();
        mvtCaisse.setDaty(ff.getDaty());
        mvtCaisse.setIdModePaiement("ESP");
        mvtCaisse.setIdDevise("AR");
        mvtCaisse.setDesignation("Mouvement de caisse pour Achat Divers "+this.getId());
        mvtCaisse.setIdTiers(ff.getIdFournisseur());
        mvtCaisse.setIdCaisse(this.getIdCaisse());
        mvtCaisse.setDatycomptabilisation(null);
        mvtCaisse.setDebit(montant);
        mvtCaisse.setIdOrigine(ff.getId());
        mvtCaisse.setTaux(1);
        return mvtCaisse;
    }

    public void genererFactureDefinitive(String u, Connection c) throws Exception {
        boolean canClose = false;
        try {
            if (c == null) {
                c = new UtilDB().GetConn();
                c.setAutoCommit(false);
                canClose = true;
            }
            AchatDivers depense = this;
            if (depense.getDaty() == null) {
                AchatDivers search = new AchatDivers();
                search.setId(this.getId());
                AchatDivers[] res = (AchatDivers[]) CGenUtil.rechercher(search, null, null, c, "");
                if (res != null && res.length > 0) {
                    depense = res[0];
                }
            }

            FactureFournisseur existFact = new FactureFournisseur();
            FactureFournisseur[] facts = (FactureFournisseur[]) CGenUtil.rechercher(existFact, null, null, c, " and (idObjet = '" + this.getId() + "' or designation like '%" + this.getId() + "%')");
            if (facts != null && facts.length > 0) {
                throw new Exception("Facture d\u00E9finitive d\u00E9j\u00E0 g\u00E9n\u00E9r\u00E9e pour cet achat divers (" + facts[0].getId() + ").");
            }

            AchatDiversFilleLib achatDiversFilleLib = new AchatDiversFilleLib();
            achatDiversFilleLib.setIdMere(this.getId());
            AchatDiversFilleLib[] achatDiversFilleLibs = (AchatDiversFilleLib[]) CGenUtil.rechercher(achatDiversFilleLib, null, null, c, "");
            double montant = AdminGen.calculSommeDouble(achatDiversFilleLibs, "montant");

            As_BonDeLivraison as = depense.genererBonDeLivaison();
            As_BonDeLivraison_Fille[] asf = depense.genererBonDeLivraisonFille(c);
            as.setFille(asf);
            as.createObject(u, c);
            as.validerObject(u, c);

            FactureFournisseur fact = depense.genererFactureFournisseur(as);
            FactureFournisseurDetails[] factFille = depense.genererFactureFournisseurDetails(asf, c);
            fact.setFille(factFille);
            fact.createObject(u, c);
            fact.validerObject(u, c);

            as.setIdFactureFournisseur(fact.getId());
            as.updateToTableWithHisto(u, c);

            MvtCaisse mvt = new MvtCaisse();
            mvt.setIdOrigine(this.getId());
            MvtCaisse[] caisses = (MvtCaisse[]) CGenUtil.rechercher(mvt, null, null, c, "");
            for (MvtCaisse m : caisses) {
                m.setIdOrigine(fact.getId());
                LiaisonPaiement liaisonPaiement = depense.genererLiaisonPaiement(m.getId(), fact.getId(), montant);
                liaisonPaiement.createObject(u, c);
                liaisonPaiement.validerObject(u, c);
                m.updateToTableWithHisto(u, c);
            }
        } catch (Exception e) {
            if (canClose && c != null) c.rollback();
            throw e;
        } finally {
            if (canClose && c != null) c.close();
        }
    }

    @Override
    public Object validerObject(String u, Connection c) throws Exception {
//        AchatDiversFilleLib achatDiversFilleLib = new AchatDiversFilleLib();
//        achatDiversFilleLib.setIdMere(this.getId());
//        AchatDiversFilleLib[] achatDiversFilleLibs = (AchatDiversFilleLib[]) CGenUtil.rechercher(achatDiversFilleLib, null, null, c, "");
//        double montant = AdminGen.calculSommeDouble(achatDiversFilleLibs, "montant");
//
////        Bon de Livraison
//        As_BonDeLivraison as = this.genererBonDeLivaison();
//        As_BonDeLivraison_Fille[] asf = this.genererBonDeLivraisonFille(c);
//        as.setFille(asf);
//        as.createObject(u, c);
//        as.validerObject(u ,c);
//
////        Facture Fournisseur
//        FactureFournisseur fact = this.genererFactureFournisseur(as);
//        FactureFournisseurDetails[] factFille = this.genererFactureFournisseurDetails(asf, c);
//        fact.setFille(factFille);
//        fact.createObject(u, c);
//        fact.validerObject(u, c);
//
//        as.setIdFactureFournisseur(fact.getId());
//        as.updateToTableWithHisto(u, c);
//
////        Paiement
//        MvtCaisse mvtCaisse = this.genererMvtCaisse(fact, montant);
//        mvtCaisse.createObject(u, c);
//        mvtCaisse.validerObject(u, c);
//
//        LiaisonPaiement liaisonPaiement = this.genererLiaisonPaiement(mvtCaisse.getId(), fact.getId(), montant);
//        liaisonPaiement.createObject(u, c);
//        liaisonPaiement.validerObject(u, c);
        return super.validerObject(u, c);
    }
}

