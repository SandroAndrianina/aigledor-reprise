package vente;

import bean.AdminGen;
import bean.ClassMAPTable;
import caisse.Caisse;
import caisse.MouvementCaisseFille;
import caisse.MouvementCaisseMere;
import caisse.MvtCaisse;
import faturefournisseur.ModePaiement;
import mg.cnaps.compta.ConstanteCompta;
import stock.MvtStock;
import stock.MvtStockFille;
import utilitaire.UtilDB;
import utilitaire.Utilitaire;
import utils.ConstanteAsync;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VentePaiement extends Vente{
    PaiementInfo[] paiementInfos;
    Caisse[] listeCaissePoint;
    Caisse caisseEspeceDefaut;

    public VentePaiement() throws Exception
    {
        this.setNomTable("VENTE");
    }
    public double getMontantPayementRecu() throws  Exception
    {
        return AdminGen.calculSommeDouble(paiementInfos,"montant");
    }
    public PaiementInfo getPaiementEspece() throws Exception
    {
        PaiementInfo paiementEspece[]=(PaiementInfo[]) AdminGen.find(getPaiementInfos(),new String[]{"modePaiement"},new String[]{ConstanteAsync.PAIEMENT_ESPECE});
        if(paiementEspece==null||paiementEspece.length==0) return null;
        return  paiementEspece[0];
    }
    public PaiementInfo getPaiementMobile() throws Exception
    {
        PaiementInfo paiementEspece[]=(PaiementInfo[]) AdminGen.find(getPaiementInfos(),new String[]{"modePaiement"},new String[]{ConstanteAsync.PAIEMENT_MOBILEMONEY});
        if(paiementEspece==null||paiementEspece.length==0) return null;
        return  paiementEspece[0];
    }



    /**
     * Orchestre l'insertion et la validation batch synchrone d'une vente directe
     * (Vente, Caisse, Stock, Comptabilité et Prévisions).
     * <p>
     * Toutes les étapes métier sont exécutées sur la même connexion avant le commit.
     * Lorsque la connexion est fournie par l'appelant, celui-ci reste seul propriétaire
     * du commit ou du rollback.
     * </p>
     *
     * @param u  Identifiant de l'utilisateur effectuant la vente
     * @param st Statement JDBC ouvert pour l'accumulation des requêtes en batch
     * @return L'objet Vente inséré et validé
     * @throws Exception en cas d'erreur de validation, de stock insuffisant ou d'échec SQL
     */
    @Override
    public ClassMAPTable createObjectBatch(String u, Statement st) throws Exception {
        boolean estOuvert = false;
        Connection c = null;
        try {
            if (st == null) {
                c = new UtilDB().GetConn();
                estOuvert = true;
                c.setAutoCommit(false);
                st = c.createStatement();
            } else {
                c = st.getConnection();
            }

            if (this.getUserFacture() == null || this.getUserFacture().isEmpty()) {
                this.setUserFacture(u);
            }
            if (this.getIdResponsable() == null || this.getIdResponsable().isEmpty()) {
                this.setIdResponsable(u);
            }
            if (paiementInfos != null && paiementInfos.length > 0) {
                if (paiementInfos.length > 1) {
                    this.setModepaiement(ConstanteAsync.PAIEMENT_MULTIPLE);
                    this.setIdModePaiement(ConstanteAsync.PAIEMENT_MULTIPLE);
                } else {
                    this.setModepaiement(paiementInfos[0].getModePaiement());
                    this.setIdModePaiement(paiementInfos[0].getModePaiement());
                }
            }
            if (this.getDaty() == null) {
                this.setDaty(Utilitaire.dateDuJourSql());
            }
            if (this.getDatyPrevu() == null) {
                this.setDatyPrevu(Utilitaire.dateDuJourSql());
            }
            if (this.getIdMagasinVente() == null || this.getIdMagasinVente().isEmpty()) {
                this.setIdMagasinVente(this.getIdMagasin());
            }
            super.setIsAppoint(true);

            // 1. Insertion batch de la Vente et des lignes VenteDetails
            super.createObjectBatch(u, st);

            // 2. Generation et insertion batch des mouvements de caisse
            MouvementCaisseMere mvtCaisseMere = this.genererEncaissementBatch(u);
            if (mvtCaisseMere != null) {
                mvtCaisseMere.setVente(this);
                mvtCaisseMere.createObjectBatch(u, st);
            }

            // 3. Generation et insertion batch des mouvements de stock
            MvtStock mvt = super.genererMvtStock(c);
            if (mvt != null) {
                mvt.createObjectBatch(u, st);
            }

            // Flush initial batch inserts to make entities visible in transaction for validation
            st.executeBatch();

            // 4. Validation batch complete de la vente (revient, comptabilite,
            // previsions et traitements metier associes)
            ClassMAPTable retour = (ClassMAPTable) super.validerObjectBatch(u, st);

            // 5. Validation batch des mouvements de caisse
            if (mvtCaisseMere != null) {
                mvtCaisseMere.validerObjectBatch(u, st);
            }

            // 6. Validation batch des mouvements de stock (controle disponibilite stock et validation)
            if (mvt != null) {
                mvt.validerObjectBatch(u, st);
            }

            // 7. Execution complete du lot JDBC avant l'unique commit
            st.executeBatch();

            if (estOuvert && c != null) {
                c.commit();
            }

            return retour;
        } catch (Exception e) {
            if (estOuvert && c != null) {
                c.rollback();
            }
            throw e;
        } finally {
            if (estOuvert) {
                if (st != null) try { st.close(); } catch (Exception e) { e.printStackTrace(); }
                if (c != null) try { c.close(); } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Point d'entrée de création synchrone standard (rétrocompatibilité).
     * Délègue l'exécution à {@link #createObjectBatch(String, Statement)} sur une transaction isolée.
     *
     * @param u Identifiant de l'utilisateur
     * @param c Connexion existante (si null, une nouvelle connexion gérée est ouverte)
     * @return L'objet Vente inséré et validé
     * @throws Exception en cas d'échec
     */
    @Override
    public ClassMAPTable createObject(String u, Connection c) throws Exception {
        Statement st = null;
        boolean canClose = false;
        try {
            if (c == null) {
                c = new UtilDB().GetConn();
                c.setAutoCommit(false);
                canClose = true;
            }
            st = c.createStatement();
            ClassMAPTable res = this.createObjectBatch(u, st);
            if (canClose) {
                c.commit();
            }
            return res;
        } catch (Exception e) {
            if (canClose && c != null) {
                c.rollback();
            }
            throw e;
        } finally {
            if (st != null) try { st.close(); } catch (Exception e) { e.printStackTrace(); }
            if (canClose && c != null) try { c.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    /**
     * Génère l'ensemble des mouvements de caisse associés aux modes de paiement de la vente
     * (Espèces avec rendu de monnaie et appoint, TPE, Mobile Money, Bon d'achat, Chèques, etc.).
     *
     * @param u Identifiant de l'utilisateur caissier
     * @return L'objet MouvementCaisseMere avec l'ensemble de ses lignes filles configurées
     * @throws Exception si le montant payé est incohérent ou si une caisse obligatoire est introuvable
     */
    public MouvementCaisseMere genererEncaissementBatch(String u) throws Exception
    {
        try{
            if (this.getPaiementInfos() == null || this.getPaiementInfos().length == 0) {
                return null;
            }
            double montantPaye = this.getMontantPayementRecu() - this.getMontantRetourner();
            System.out.println("MONTANT RETORUNER ===> " + this.getMontantRetourner());
            System.out.println("NEW MONTANT PAYE =====>  " + montantPaye);

            MouvementCaisseMere mvtCaisseMere = new MouvementCaisseMere();
            mvtCaisseMere.setCredit(this.getMontantTTCCalcul());
            mvtCaisseMere.setDaty(this.getDaty());
            mvtCaisseMere.setIdtiers(this.getIdClient());
            mvtCaisseMere.setIdorigine(this.getId());
            mvtCaisseMere.setVente(this);

            List<MouvementCaisseFille> mvtCaisseFilleListe = new ArrayList<>();
            double app = this.getAppoint(montantPaye);
            if (Math.abs(app) < 0.01) {
                app = 0.0;
            }

            for (PaiementInfo pi : this.getPaiementInfos())
            {
                if (pi == null || pi.getModePaiement() == null || pi.getModePaiement().trim().isEmpty()) continue;
                String idCaisse = "";
                MouvementCaisseFille mvtCaisseFille = new MouvementCaisseFille();
                mvtCaisseFille.setCredit(pi.getMontant());
                mvtCaisseFille.setReference(pi.getReference());
                mvtCaisseFille.setIdModePaiement(pi.getModePaiement());
                mvtCaisseFille.setDesignation("POS Encaissement de la facture Num :" + this.getId() + " " + this.getDaty());
                mvtCaisseFille.setIdDevise("AR");
                mvtCaisseFille.setTaux(1);
                mvtCaisseFille.setHeure(Utilitaire.getCurrentHeure());
                mvtCaisseFille.setIdCassier(u);

                if (pi.getIdCaisse() == null || pi.getIdCaisse().compareToIgnoreCase("") == 0 || pi.getIdCaisse().compareToIgnoreCase(ConstanteAsync.caisseCategEspeece) == 0) {
                    if (this.getCaisseEspeceDefaut() != null) {
                        idCaisse = this.getCaisseEspeceDefaut().getId();
                    } else if (this.getListeCaissePoint() != null) {
                        Caisse cEsp = Caisse.getCaisseParCat(this.getListeCaissePoint(), ConstanteAsync.caisseCategEspeece);
                        if (cEsp != null) idCaisse = cEsp.getId();
                    }
                    if (montantPaye > this.getMontantTTCCalc() + ConstanteCompta.montantMinimumAppoint) {
                        throw new Exception("Le montant est supérieur");
                    }
                    mvtCaisseFille.setCredit(pi.getMontant() - this.getMontantRetourner() - app);
                } else if (pi.getIdCaisse().compareToIgnoreCase(ConstanteAsync.caisseCategTpe) == 0) {
                    if (montantPaye > this.getMontantTTCCalc() + ConstanteCompta.montantMinimumAppoint) {
                        throw new Exception("Le montant est supérieur");
                    }
                    Caisse cTpe = Caisse.getCaisseParCat(this.getListeCaissePoint(), ConstanteAsync.caisseCategTpe);
                    if (cTpe != null) idCaisse = cTpe.getId();
                } else if (pi.getIdCaisse().compareToIgnoreCase(ConstanteAsync.caisseCategMvola) == 0) {
                    if (montantPaye > this.getMontantTTCCalc() + ConstanteCompta.montantMinimumAppoint) {
                        throw new Exception("Le montant est supérieur");
                    }
                    Caisse cMv = Caisse.getCaisseParCat(this.getListeCaissePoint(), ConstanteAsync.caisseCategMvola);
                    if (cMv != null) idCaisse = cMv.getId();
                } else if (pi.getIdCaisse().compareToIgnoreCase(ConstanteAsync.caisseCategOrange) == 0) {
                    if (montantPaye > this.getMontantTTCCalc() + ConstanteCompta.montantMinimumAppoint) {
                        throw new Exception("Le montant est supérieur");
                    }
                    Caisse cOr = Caisse.getCaisseParCat(this.getListeCaissePoint(), ConstanteAsync.caisseCategOrange);
                    if (cOr != null) idCaisse = cOr.getId();
                } else if (pi.getIdCaisse().compareToIgnoreCase(ConstanteAsync.caisseCategAirtel) == 0) {
                    if (montantPaye > this.getMontantTTCCalc() + ConstanteCompta.montantMinimumAppoint) {
                        throw new Exception("Le montant est supérieur");
                    }
                    Caisse cAir = Caisse.getCaisseParCat(this.getListeCaissePoint(), ConstanteAsync.caisseCategAirtel);
                    if (cAir != null) idCaisse = cAir.getId();
                } else if (pi.getIdCaisse().compareToIgnoreCase(ConstanteAsync.caisseCategCheque) == 0) {
                    if (montantPaye > this.getMontantTTCCalc() + ConstanteCompta.montantMinimumAppoint) {
                        throw new Exception("Le montant est supérieur");
                    }
                    Caisse cChq = Caisse.getCaisseParCat(this.getListeCaissePoint(), ConstanteAsync.caisseCategCheque);
                    if (cChq != null) idCaisse = cChq.getId();
                } else if (pi.getIdCaisse().compareToIgnoreCase(ConstanteAsync.caisseBonAchat) == 0) {
                    Caisse cBon = Caisse.getCaisseParCat(this.getListeCaissePoint(), ConstanteAsync.caisseBonAchat);
                    if (cBon != null) idCaisse = cBon.getId();
                    mvtCaisseFille.setCredit(pi.getMontant());
                } else if (pi.getIdCaisse().compareToIgnoreCase(ConstanteAsync.caisseCadeaux) == 0) {
                    Caisse cCad = Caisse.getCaisseParCat(this.getListeCaissePoint(), ConstanteAsync.caisseCadeaux);
                    if (cCad != null) idCaisse = cCad.getId();
                } else {
                    idCaisse = pi.getIdCaisse();
                }

                mvtCaisseFille.setIdCaisse(idCaisse);
                mvtCaisseFilleListe.add(mvtCaisseFille);
            }

            MouvementCaisseFille mvtApp = null;
            if (this.getIsAppoint())
            {
                if (app != 0 && Math.abs(app) <= ConstanteCompta.montantMinimumAppoint)
                {
                    PaiementInfo payementMobile = this.getPaiementMobile();
                    String idCaisse = null;
                    if (this.getPaiementEspece() != null)
                    {
                        if (this.getCaisseEspeceDefaut() != null) {
                            idCaisse = this.getCaisseEspeceDefaut().getId();
                        } else if (this.getListeCaissePoint() != null) {
                            Caisse cEsp = Caisse.getCaisseParCat(this.getListeCaissePoint(), ConstanteAsync.caisseCategEspeece);
                            if (cEsp != null) idCaisse = cEsp.getId();
                        }
                    }
                    else if (payementMobile != null && payementMobile.getIdCaisse() != null)
                    {
                        if (this.getListeCaissePoint() != null) {
                            Caisse cMob = Caisse.getCaisseParCat(this.getListeCaissePoint(), payementMobile.getIdCaisse());
                            if (cMob != null) idCaisse = cMob.getId();
                        }
                    }
                    mvtApp = this.creerAPpoint(app, idCaisse);
                }
            }
            if (mvtApp != null) mvtCaisseFilleListe.add(mvtApp);
            mvtCaisseMere.setFille(mvtCaisseFilleListe.toArray(new MouvementCaisseFille[0]));

            return mvtCaisseMere;

        } catch (Exception e) {
            throw e;
        }
    }


    public PaiementInfo[] getPaiementInfos() {
        return paiementInfos;
    }

    public void setPaiementInfos(PaiementInfo[] paiementInfos) {
        this.paiementInfos = paiementInfos;
    }


    public Caisse[] getListeCaissePoint() {
        return listeCaissePoint;
    }

    public void setListeCaissePoint(Caisse[] listeCaissePoint) {
        this.listeCaissePoint = listeCaissePoint;
    }

    public Caisse getCaisseEspeceDefaut() {
        return caisseEspeceDefaut;
    }

    public void setCaisseEspeceDefaut(Caisse caisseEspeceDefaut) {
        this.caisseEspeceDefaut = caisseEspeceDefaut;
    }

    // =========================================================================
    // MÉTHODES DE DÉLÉGATION (BRIDGES) RÉSERVÉES AUX TESTS ET BENCHMARKS
    // Ces méthodes permettent aux classes de tests (ex: SmokeTestVenteDirecteBatch)
    // d'exécuter et de profiler chaque sous-étape de la classe parente Vente.
    // =========================================================================

    /**
     * Délégation de {@link Vente#createObjectBatch(String, Statement)} pour les tests unitaires.
     */
    public ClassMAPTable superCreateObjectBatch(String u, Statement st) throws Exception {
        return super.createObjectBatch(u, st);
    }

    /**
     * Délégation de {@link Vente#genererMvtStock(Connection)} pour les tests unitaires.
     */
    public MvtStock superGenererMvtStock(Connection c) throws Exception {
        return super.genererMvtStock(c);
    }

    /**
     * Délégation de {@link Vente#validerObjectBatch(String, Statement)} pour les tests unitaires.
     */
    public Object superValiderObjectBatch(String u, Statement st) throws Exception {
        return super.validerObjectBatch(u, st);
    }
}
