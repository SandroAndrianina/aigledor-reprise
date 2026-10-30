/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mg.cnaps.compta;
import bean.CGenUtil;
import bean.ClassEtat;
import bean.ClassFille;
import bean.ClassMAPTable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import rapprochement.ReleverDetail;
import utilitaire.ConstanteEtat;
import utilitaire.UtilDB;
import utilitaire.Utilitaire;
import mg.cnaps.compta.*;
import utilitaire.ConstanteEtat;

/**
 *
 * @author Jetta
 */
public class ComptaSousEcriture extends ClassFille {

    private String id;
    private String compte;
    private double debit;
    private double credit;
    private String remarque;
    private String libellePiece;
    private String idMere;
    private String reference_engagement;
    private String compte_aux;
    private String lettrage;
    private String journal;
    private int exercice;
    private String folio;
    private Date daty,dateComptable;
    private String analytique;
    private String source;

    private String idJournal ;
    private String ecriture ;
    private String numero ;
    private int mouvement;

    private int annee;

    public String getContrePartie(Connection c) throws Exception{
        String awhere = " AND ID!='"+this.getId()+"' AND IDMERE='"+this.getIdMere()+"'";
        ComptaSousEcriture [] sous = (ComptaSousEcriture []) CGenUtil.rechercher(new ComptaSousEcriture(), null, null, c, awhere);
        System.out.println("====================="+awhere);
        if(sous.length>0){
            if(sous.length>1){
                for (int i = 0; i < sous.length; i++) {
                    if(this.getCredit()>0 && sous[i].getCredit()==0 && sous[i].getDebit()>0){
                        return sous[i].getCompte();
                    }
                    else if(this.getDebit()>0 && sous[i].getDebit()==0 && sous[i].getCredit()>0){
                        return sous[i].getCompte();
                    }
                    System.err.println(this.getCompte()+"============================"+sous[i].getCompte());
                }
            }
            return sous[0].getCompte();
        }
        return "";
    }

    public static ComptaSousEcriture[] getSousEcriture(String[] ids) throws Exception {
        ComptaSousEcriture[] res = (ComptaSousEcriture[]) CGenUtil.rechercher(new ComptaSousEcriture(), null,null,null," and id in ("+ Utilitaire.tabToString(ids, "'", ",")+" )");
        return res;
    }

    public boolean isActif() throws Exception {
        if(this.getCompte().startsWith("2") || this.getCompte().startsWith("3") || this.getCompte().startsWith("5")){
            return true;
        }else if(this.getCompte().startsWith("41") || this.getCompte().startsWith("409") || this.getCompte().startsWith("43") || this.getCompte().startsWith("44")){
            return true;
        }
        else{
            return false;
        }
    }

    public int getMouvement() {
        return mouvement;
    }

    public void setMouvement(int mouvement) {
        this.mouvement = mouvement;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public double getSolde() throws Exception {
        return Math.abs(this.getCredit()-this.getDebit());
    }

    public double getSoldeActif() throws Exception {
        return Math.abs(this.getDebit() - this.getCredit());
    }

    public Date getDateComptable() {
        return dateComptable;
    }

    public void setDateComptable(Date dateComptable) {
        this.dateComptable = dateComptable;
    }

    public String getIdJournal() {
        return idJournal;
    }

    public void setIdJournal(String idJournal) {
        this.idJournal = idJournal;
    }

    public String getEcriture() {
        return ecriture;
    }

    public void setEcriture(String ecriture) {
        this.ecriture = ecriture;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }


    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public String getAnalytique() {
        return analytique;
    }

    public void setAnalytique(String analytique) {
        this.analytique = analytique;
    }
    
    public String getFolio() {
        return Utilitaire.champNull(folio);
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public String getJournal() {
        return Utilitaire.champNull(journal);
    }

    public void setJournal(String journal) throws Exception{

//        if(getMode().compareToIgnoreCase("modif") == 0){
//            if(journal == null || journal.compareToIgnoreCase("") == 0) throw new Exception("Journal vide");
//        }
        this.journal = journal;
      /*  }
        else
        {
            if(journal==null || (journal!=null && journal.compareTo("")==0))
            {
                 throw new Exception ("Journal invalide");
            }
            else{
                this.journal = journal;
            }
        }*/
    }
    public void updateComptaEcritureWithLettrage(String idLettrage, String u, Connection c) throws Exception{
        boolean estOuvert = false;
        try {
            if (c == null) {
                estOuvert = true;
                c = new UtilDB().GetConn();
            }
            this.setNomTable("COMPTA_SOUS_ECRITURE");
            this.setLettrage(idLettrage);
            this.updateToTableWithHisto(u, c);

        } catch (Exception e) {
            throw e;
        } finally {
            if (c != null && estOuvert == true) {
                c.close();
            }
        }

    }

    public int getExercice() {
        return exercice;
    }

    public void setExercice(int exercice) {
        this.exercice = exercice;
    }

    public String getReference_engagement() {
        return Utilitaire.champNull(reference_engagement);
    }

    public void setReference_engagement(String reference_engagement) {
        this.reference_engagement = reference_engagement;
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("SECR", "GETSEQCOMPTASOUSECRITURE");
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
    @Override
    public void controlerDelete(Connection c)throws Exception{
        controlerUpdate(c);
    }

    public void setNomTableSelonEtatEtTypeCompte(int etat, int typeCompte){
        if( etat == 0 && typeCompte == ConstanteComptabilite.TYPE_COMPTE_ANALYTIQUE ){
            super.setNomTable("COMPTA_MOUVEMENT_ANAL");
        }
        if( etat == ConstanteEtat.getEtatCreer() && typeCompte == ConstanteComptabilite.TYPE_COMPTE_ANALYTIQUE ){
            super.setNomTable("COMPTA_MOUVEMENT_ANAL_C");
        }
        if( etat == 0 && typeCompte == ConstanteComptabilite.TYPE_COMPTE_GENERAL ){
            super.setNomTable("COMPTA_MOUVEMENT_DETAILS_GEN_2");
        }
        if( etat == ConstanteEtat.getEtatCreer() && typeCompte == ConstanteComptabilite.TYPE_COMPTE_GENERAL){
            super.setNomTable("COMPTAMOUVEMENTDETAILS_GEN_2_C");
        }
        if( etat == ConstanteEtat.getEtatValider() && typeCompte == ConstanteComptabilite.TYPE_COMPTE_GENERAL){
            super.setNomTable("COMPTAMOUVEMENTDETAILS_GEN_2_V");
        }
        if( etat == ConstanteEtat.getEtatValider() && typeCompte == ConstanteComptabilite.TYPE_COMPTE_ANALYTIQUE){
            super.setNomTable("COMPTA_MOUVEMENT_ANAL_V");
        }
        
    }
    
    public ComptaSousEcriture(String id, String compte, double debit, double credit, String remarque, String libellePiece, String ecriture, String ref_engag, String compte_a) throws Exception {
        super.setNomTable("compta_sous_ecriture");
        this.setLiaisonMere("idMere");
        this.setNomClasseMere("mg.cnaps.compta.ComptaEcriture");
        this.setCompte(compte);
        this.setDebit(debit);
        this.setCredit(credit);
        this.setRemarque(remarque);
        this.setLibellePiece(libellePiece);
        this.setIdMere(ecriture);
        this.setReference_engagement(ref_engag);
        this.setCompte_aux(compte_a);
    }
 
    public ComptaSousEcriture(String compte, double debit, double credit, String remarque, String libellePiece, String ecriture, String ref_engag, String compte_a) throws Exception {
        super.setNomTable("compta_sous_ecriture");
        this.setLiaisonMere("idMere");
        this.setNomClasseMere("mg.cnaps.compta.ComptaEcriture");
        this.setCompte(compte);
        this.setDebit(debit);
        this.setCredit(credit);
        this.setRemarque(remarque);
        this.setLibellePiece(libellePiece);
        this.setIdMere(ecriture);
        this.setReference_engagement(ref_engag);
        this.setCompte_aux(compte_a);
    }

    public ComptaSousEcriture(String compte, double debit, double credit, String remarque, String libellePiece, String ecriture, String ref_engag, String compte_a,String folio, String analytique) throws Exception {
        super.setNomTable("compta_sous_ecriture");
        this.setLiaisonMere("idMere");
        this.setNomClasseMere("mg.cnaps.compta.ComptaEcriture");
        this.setCompte(compte);
        this.setDebit(debit);
        this.setCredit(credit);
        this.setRemarque(remarque);
        this.setLibellePiece(libellePiece);
        this.setIdMere(ecriture);
        this.setReference_engagement(ref_engag);
        this.setCompte_aux(compte_a);
        this.setFolio(folio);
        this.setAnalytique(analytique);
    }

    public ComptaSousEcriture(String compte, double debit, double credit, String remarque, String libellePiece, String ecriture, String ref_engag) throws Exception {
        super.setNomTable("compta_sous_ecriture");
        this.setLiaisonMere("idMere");
        this.setNomClasseMere("mg.cnaps.compta.ComptaEcriture");
        this.setCompte(compte);
        this.setDebit(debit);
        this.setCredit(credit);
        this.setRemarque(remarque);
        this.setLibellePiece(libellePiece);
        this.setIdMere(ecriture);
        this.setReference_engagement(ref_engag);
    }
    public ComptaSousEcriture(String compte, String tiers, double debit, double credit, String remarque, String libellePiece, String ecriture, String ref_engag) throws Exception {
        super.setNomTable("compta_sous_ecriture");
        this.setLiaisonMere("idMere");
        this.setNomClasseMere("mg.cnaps.compta.ComptaEcriture");
        this.setCompte(compte);
        this.setCompte_aux(tiers);
        this.setDebit(debit);
        this.setCredit(credit);
        this.setRemarque(remarque);
        this.setLibellePiece(libellePiece);
        this.setIdMere(ecriture);
        this.setReference_engagement(ref_engag);
    }

    public ComptaSousEcriture(String compte, double debit, double credit, String remarque, String libellePiece,String reference_engagement, String compte_aux, String lettrage, String journal, String exercice, String folio, Date daty, String analytique, String source) throws Exception {
        super.setNomTable("compta_sous_ecriture");
        this.setCompte(compte);
        this.setDebit(debit);
        this.setCredit(credit);
        this.setRemarque(remarque);
        this.setLibellePiece(libellePiece);
        this.setReference_engagement(reference_engagement);
        this.setCompte_aux(compte_aux);
        this.setLettrage(lettrage);
        this.setJournal(journal);
        this.setExercice(Integer.parseInt(exercice));
        this.setFolio(folio);
        this.setDaty(daty);
        this.setAnalytique(analytique);
        this.setSource(source);
        this.setIdMere("MVT320259");
    }

    
    public ComptaSousEcriture()throws Exception {
        super.setNomTable("compta_sous_ecriture");
        this.setLiaisonMere("idMere");
        this.setNomClasseMere("mg.cnaps.compta.ComptaEcriture");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompte() throws Exception {
        return Utilitaire.champNull(compte);
    }

    public void setCompte(String compte) throws Exception {
       if(compte==null){
            if(this.getMode().compareToIgnoreCase("choix") == 0){
            throw new Exception("Compte Obligatoire");
            }
        }
        
        this.compte = compte;
    }

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) throws Exception {
        if(this.getMode().compareToIgnoreCase("modif") == 0){
            if(debit < 0){
                setCredit(debit*-1);
                return;
            }
        }
        this.debit = debit;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) throws Exception {
        if(this.getMode().compareToIgnoreCase("modif") == 0){
            if(credit < 0){
                setDebit(credit*-1);
                return;
            }
        }
        this.credit = credit;
    }

    public String getRemarque() {
        return Utilitaire.champNull(remarque).replace('"',' ');
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public String getLibellePiece() {
        return Utilitaire.champNull(libellePiece);
    }

    public void setLibellePiece(String libellePiece) {
        this.libellePiece = libellePiece;
    }

    public String getIdMere() {
        return Utilitaire.champNull(idMere);
    }

    public void setIdMere(String mere) throws Exception {
        if (getMode().compareTo("modif") != 0) {
            this.idMere = mere;
            return;
        }
        this.idMere = mere;
    }

    public String getCompte_aux() {
        return Utilitaire.champNull(compte_aux);
    }

    public void setCompte_aux(String compte_aux) throws Exception{
        this.compte_aux = compte_aux;
    }

    /**
     * @return the lettrage
     */
    public String getLettrage() {
        return Utilitaire.champNull(lettrage);
    }

    /**
     * @param lettrage the lettrage to set
     */
    public void setLettrage(String lettrage) {
        this.lettrage = lettrage;
    }

    

    @Override

    public void controler(Connection c) throws Exception{

        this.testEtatMoisExercice("D AJOUTER UNE ECRITURE", c);
        this.testCompteExiste(c);
        this.checkDebitCredit();
        super.controler(c);
    }

    private void checkDebitCredit() throws Exception{
        if(this.debit == 0 && this.credit == 0){
            throw new Exception("Les champs debit et credit ne doivent pas etre null en meme temps!!");
        }
        if(this.credit > 0){
            if(this.debit > 0){
                throw new Exception("Vous devez saisir une valeur dans le champ debit ou une valeur dans le champ credit");
            }
        }
    }

    // =========================================================================
    // CACHES LOCAUX EN MÉMOIRE POUR RÉDUIRE LES REQUÊTES RÉPÉTITIVES DE CONTRÔLE
    // =========================================================================
    private static final java.util.concurrent.ConcurrentHashMap<String, Long> CACHE_CLOTURE_ETAT = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.concurrent.ConcurrentHashMap<String, Long> CACHE_CLOTURE_TIMESTAMP = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.concurrent.ConcurrentHashMap<String, Boolean> CACHE_COMPTE_VALIDE = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.concurrent.ConcurrentHashMap<String, Long> CACHE_COMPTE_TIMESTAMP = new java.util.concurrent.ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 60_000L;

    /**
     * Vide les caches de validation comptable. Cette méthode ne modifie aucune
     * donnée Oracle et permet aux tests ou à une opération comptable explicite
     * de forcer la prochaine lecture en base.
     */
    public static void viderCachesValidation() {
        CACHE_CLOTURE_ETAT.clear();
        CACHE_CLOTURE_TIMESTAMP.clear();
        CACHE_COMPTE_VALIDE.clear();
        CACHE_COMPTE_TIMESTAMP.clear();
    }

    static boolean estCompteCacheValide(String compte, long maintenant) {
        Boolean valide = CACHE_COMPTE_VALIDE.get(compte);
        Long dateControle = CACHE_COMPTE_TIMESTAMP.get(compte);
        long age = dateControle == null ? CACHE_TTL_MS : maintenant - dateControle;
        if (Boolean.TRUE.equals(valide) && dateControle != null
                && age >= 0 && age < CACHE_TTL_MS) {
            return true;
        }
        CACHE_COMPTE_VALIDE.remove(compte);
        CACHE_COMPTE_TIMESTAMP.remove(compte);
        return false;
    }

    static void memoriserCompteValide(String compte, long dateControle) {
        CACHE_COMPTE_VALIDE.put(compte, Boolean.TRUE);
        CACHE_COMPTE_TIMESTAMP.put(compte, dateControle);
    }

    /**
     * Vérifie l'existence et la validité du compte comptable en base Oracle.
     * Utilise un cache thread-safe avec un TTL de 60 secondes pour éviter de
     * ré-interroger la table {@code compta_compte} à chaque sous-écriture,
     * tout en prenant en compte les changements de configuration comptable.
     *
     * @param c Connexion JDBC
     * @throws Exception si le compte est vide, invalide ou inexistant en base
     */
    void testCompteExiste(Connection c) throws Exception{
        if (this.compte == null || this.compte.trim().isEmpty()) {
            throw new Exception("Compte invalide ou vide");
        }
        String cpt = this.compte.trim();
        long maintenant = System.currentTimeMillis();
        if (estCompteCacheValide(cpt, maintenant)) {
            return;
        }
        ComptaCompte[] comptes = rechercherComptes(c, cpt);
        if(comptes == null || comptes.length <= 0){
            throw new Exception("Compte "+cpt+" invalide ou inexistant");
        }
        memoriserCompteValide(cpt, System.currentTimeMillis());
    }

    /**
     * Point de lecture Oracle isole pour tester le comportement du cache sans
     * exposer de nouvelle API distante.
     */
    ComptaCompte[] rechercherComptes(Connection c, String cpt) throws Exception {
        ComptaCompte comptacompte = new ComptaCompte();
        comptacompte.setCompte(cpt);
        return (ComptaCompte[]) CGenUtil.rechercher(comptacompte, null, null, c, "");
    }

    private boolean checkerValidation(Connection c) throws Exception{
        ComptaSousEcriture cse = new ComptaSousEcriture();
        cse.setId(this.id);
        ComptaSousEcriture[] cses = (ComptaSousEcriture[]) CGenUtil.rechercher(cse, null, null, c, "");
        if(cses == null || cses.length == 0){
            throw new Exception("Compta ecriture inexistant");
        }
        if(cses[0].getEtat() == ConstanteEtat.getEtatValider()){
            return true;
        }
        return false;
    }

    /**
     * Vérifie si le mois et l'exercice de la date comptable sont clôturés.
     * <p>
     * Utilise un cache en mémoire avec TTL (Time-To-Live) de 60 secondes pour éliminer
     * ~20 requêtes SELECT synchrones par vente tout en garantissant la prise en compte
     * rapide d'une clôture manuelle effectuée dans l'application.
     * </p>
     *
     * @param texte Libellé contextuel de l'opération (ex: "D'ENREGISTRER", "DE MODIFIER")
     * @param c     Connexion JDBC
     * @throws Exception si le mois correspondant est clôturé (état = 9)
     */
    public void testEtatMoisExercice(String texte, Connection c) throws Exception {
        try {
            int mois = Utilitaire.getMois(this.daty);
            int exercice = Utilitaire.getAnnee(this.daty);
            String key = mois + "_" + exercice;
            long now = System.currentTimeMillis();
            Long cachedTime = CACHE_CLOTURE_TIMESTAMP.get(key);
            int etat = -1;
            long age = cachedTime == null ? CACHE_TTL_MS : now - cachedTime;
            if (cachedTime != null && age >= 0 && age < CACHE_TTL_MS) {
                Long cachedEtat = CACHE_CLOTURE_ETAT.get(key);
                if (cachedEtat != null) {
                    etat = cachedEtat.intValue();
                }
            }
            if (etat == -1) {
                CACHE_CLOTURE_ETAT.remove(key);
                CACHE_CLOTURE_TIMESTAMP.remove(key);
                ClotureMoisAnnee[] cl = (ClotureMoisAnnee[]) CGenUtil.rechercher(new ClotureMoisAnnee(), null, null, c, " AND MOIS = " + mois + " AND ANNEE=" + exercice);
                if (cl != null && cl.length > 0) {
                    etat = cl[0].getEtat();
                } else {
                    etat = 1;
                }
                CACHE_CLOTURE_ETAT.put(key, (long) etat);
                CACHE_CLOTURE_TIMESTAMP.put(key, now);
            }
            if (etat == 9) {
                String m = Utilitaire.nbToMois(mois);
                throw new Exception("IMPOSSIBLE " + texte + ". MOIS " + m.toUpperCase() + " " +  exercice + " CLOTURE");
            }
        } catch (Exception e) {
            throw e;
        }
    }
    @Override
    public void controlerUpdate(Connection c) throws Exception {

        this.testEtatMoisExercice("DE MODIFIER L ECRITURE", c);
        if(this.checkerValidation(c)){
            throw new Exception("Impossible de corriger l ecriture. Ecriture déjà validé");
        }
        this.testCompteExiste(c);
        this.checkDebitCredit();
    }

    private void controlerValider(Connection c)throws Exception {
        this.testEtatMoisExercice("DE VALIDER L ECRITURE", c);
        if(this.checkerValidation(c)){
            throw new Exception("Ecriture déjà validé");
        }
        this.testCompteExiste(c);
        this.checkDebitCredit();
    }

    @Override
    public void controlerAnnulationVisa(Connection c) throws Exception {
        if(!checkerValidation(c)){
            throw new Exception("Impossible d annuler visa écriture , écriture non validé");
        }
        this.testEtatMoisExercice("D ANNULER VISA ECRITURE", c);
    }

    @Override
    public Object validerObject(String u, Connection c) throws Exception
    {
        setMode("modif");
        this.controlerValider(c);
        this.setEtat(ConstanteEtat.getEtatValider());
        this.updateToTableWithHisto(u, c);
        return this;
    }
    /*@Override
    public int insertToTableWithHisto(String refUser, java.sql.Connection c)throws Exception {
        
        this.controler(c);
        
        return super.insertToTableWithHisto(refUser, c);
    }*/

    public void afficher() {
        System.out.println("id : " + id);
        System.out.println("compte : " + compte);
        System.out.println("debit : " + Utilitaire.formaterAr(debit));
        System.out.println("credit : " + Utilitaire.formaterAr(credit));
        System.out.println("remarque : " + remarque);
        System.out.println("libellePiece : " + libellePiece);
        System.out.println("idMere : " + idMere);
        System.out.println("reference_engagement : " + reference_engagement);
        System.out.println("compte_aux : " + compte_aux);
        System.out.println("lettrage : " + lettrage);
        System.out.println("journal : " + journal);
        System.out.println("exercice : " + exercice);
        System.out.println("folio : " + folio);
        System.out.println("daty : " + daty);
        System.out.println("analytique : " + analytique);
        System.out.println("///////////////////////");
    }

    @Override
    public ClassMAPTable createObject(String u, Connection c) throws Exception {
        ClassMAPTable a = null;
        ComptaCompte[] comptes = (ComptaCompte[]) CGenUtil.rechercher(new ComptaCompte(), null, null, c, " AND COMPTE='"+this.getCompte()+"'");
        if(comptes.length == 0){
            throw new Exception("Compte "+this.getCompte()+" invalide ou inexistant");
        }
        try {
          a =  super.createObject(u, c);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return a;
    }
}
