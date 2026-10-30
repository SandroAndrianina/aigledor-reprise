/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vente;

import affichage.PageInsertMultiple;
import annexe.Produit;
import avoir.AvoirFC;
import avoir.AvoirFCFille;
import avoir.AvoirFCLib;
import bean.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import caisse.Caisse;
import caisse.MouvementCaisseFille;
import caisse.MvtCaisse;
import client.Client;
import constante.ConstanteEtat;
import encaissement.Encaissement;
import encaissement.EncaissementDetails;
import equivalence.EquivalenceIngredient;
import faturefournisseur.FactureFournisseur;
import faturefournisseur.FactureFournisseurCpl;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import faturefournisseur.FactureFournisseurDetails;
import magasin.Magasin;
import mg.cnaps.compta.ComptaEcriture;
import mg.cnaps.compta.ComptaSousEcriture;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import paiement.LiaisonPaiement;
import prevision.Prevision;
import prevision.PrevisionComplet;
import produits.Ingredients;
import remise.RemiseFille;
import ristourne.Ristourne;
import ristourne.RistourneDetails;
import rapprochement.RapprochementDBMere;
import stock.*;
import user.UserEJB;
import utilitaire.UtilDB;
import utilitaire.Utilitaire;
import utils.*;
import utilitaire.ConstanteComptable;

import javax.imageio.ImageIO;
import javax.swing.plaf.nimbus.State;

import produits.RecetteLib;
/**
 *
 * @author Angela
 */
public class Vente extends FactureCF {
    @LibelleAffichage("D&eacute;signation")
    protected String designation;

    @LibelleAffichage("Magasin")
    protected String idMagasin;

    @LibelleAffichage("Remarque")
    protected String remarque;

    @LibelleAffichage("Origine")
    protected String idOrigine;

    @LibelleAffichage("Client")
    protected String idClient;

    @LibelleAffichage("Libell&eacute; du client")
    protected String clientlib;

    @LibelleAffichage("Compte")
    protected String compte;

    @LibelleAffichage("Taux de change")
    protected double tauxdechange;

    @LibelleAffichage("D&eacute;tails de la vente")
    protected VenteDetails[] venteDetails;

    @LibelleAffichage("Est pr&eacute;vu")
    int estPrevu;

    @LibelleAffichage("Montant de revient")
    double montantRevient;

    @LibelleAffichage("Marge brute")
    double margeBrute;

    @LibelleAffichage("&Eacute;ch&eacute;ance de la facture")
    double echeancefacture;

    @LibelleAffichage("Mode de paiement")
    String modepaiement;

    @LibelleAffichage("Frais de livraison")
    double fraislivraison;

    @LibelleAffichage("Mode de livraison")
    int modelivraison;

    @LibelleAffichage("R&eacute;f&eacute;rence de la facture")
    String referencefact;

    @LibelleAffichage("Num&eacute;ro de facture")
    String numerofacture;

    @LibelleAffichage("Lieu de livraison")
    private String lieuLivraison;

    @LibelleAffichage("Date de livraison")
    private Date dateLivraison;

    @LibelleAffichage("Pr&eacute;vision")
    private String idPrevision;

    @LibelleAffichage("Montant donn&eacute;")
    private double montantDonne;

    @LibelleAffichage("Montant retourn&eacute;")
    private double montantRetourner;

    @LibelleAffichage("Mode de paiement")
    private String idModePaiementLib;

    @LibelleAffichage("Num&eacute;ro du mode de paiement")
    private String idModePaiement;

    @LibelleAffichage("Responsable")
    private String idResponsable;

    @LibelleAffichage("Remise")
    private double remise;

    @LibelleAffichage("Montant de la remise")
    private double remiseMontant;

    @LibelleAffichage("Commercial")
    private String idCommercial;

    @LibelleAffichage("Heure de saisie")
    private String heureSaisie;

    @LibelleAffichage("Date d'&eacute;ch&eacute;ance")
    private Date dateEcheance;

    @LibelleAffichage("Facture d'&eacute;change")
    private String factureEchange;

    @LibelleAffichage("Nom du client divers")
    private String nomClientDivers;

    @LibelleAffichage("Liste des ingr&eacute;dients")
    Ingredients[] listeIng = null;

    @LibelleAffichage("Client")
    Client client;

    @LibelleAffichage("Avec appoint")
    boolean isAppoint = false;
    String idMagasinVente;
    String userFacture;

    public String getUserFacture() {
        return userFacture;
    }

    public void setUserFacture(String userFacture) {
        this.userFacture = userFacture;
    }

    public String getIdMagasinVente() {
        return idMagasinVente;
    }

    public void setIdMagasinVente(String idMagasinVente) {
        this.idMagasinVente = idMagasinVente;
    }

    public boolean getIsAppoint() {
        return isAppoint;
    }

    public void setIsAppoint(boolean appoint) {
        isAppoint = appoint;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Ingredients[] getListeIng() {
        return listeIng;
    }

    public void setListeIng(Ingredients[] listeIng) {
        this.listeIng = listeIng;
    }

    public String getNomClientDivers() {
        return nomClientDivers;
    }

    public void setNomClientDivers(String nomClientDivers) {
        this.nomClientDivers = nomClientDivers;
    }

    public String getFactureEchange() {
        return factureEchange;
    }

    public void setFactureEchange(String factureEchange) {
        this.factureEchange = factureEchange;
    }

    public String getIdCommercial() {
        return idCommercial;
    }

    public void setIdCommercial(String idCommercial) {
        this.idCommercial = idCommercial;
    }

    public String getIdPrevision() {
        return idPrevision;
    }

    public void setIdPrevision(String idPrevision) {
        this.idPrevision = idPrevision;
    }

    public String getLieuLivraison() {
        return lieuLivraison;
    }

    public void setLieuLivraison(String lieuLivraison) {
        this.lieuLivraison = lieuLivraison;
    }

    public Date getDateLivraison() {
        return dateLivraison;
    }

    public void setDateLivraison(Date dateLivraison) {
        this.dateLivraison = dateLivraison;
    }

    public String getNumerofacture() {
        return numerofacture;
    }

    public void setNumerofacture(String numerofacture) {
        this.numerofacture = numerofacture;
    }

    public int getModelivraison() {
        return modelivraison;
    }

    public void setModelivraison(int modelivraison) {
        this.modelivraison = modelivraison;
    }

    public double getFraislivraison() {
        return fraislivraison;
    }

    public void setFraislivraison(double fraislivraison) {
        this.fraislivraison = fraislivraison;
    }

    public String getModepaiement() {
        return modepaiement;
    }

    public void setModepaiement(String modepaiement) {
        this.modepaiement = modepaiement;
    }

    public void setMargeBrute(double margeBrute) {
        this.margeBrute = margeBrute;
    }

    public double getMontantRevient() {
        return montantRevient;
    }

    public void setMontantRevient(double montantRevient) {
        this.montantRevient = montantRevient;
    }
    public String getReferencefact() {
        return referencefact;
    }

    public void setReferencefact(String referencefact) {
        this.referencefact = referencefact;
    }

    public double getMargeBrute()
    {
        return this.getMontantttc()-this.getMontantRevient();
    }

    @Override
    public boolean isSynchro(){
        return true;
    }
    
    public void Vente(){
    }

    @Override
    public boolean getEstIndexable() {
        return true;
    }

    @Override
    public String getTiers(){
        return this.getIdClient();
    }
    @Override
    public String getSensPrev(){
        return "credit";
    }

    public Prevision[] genererPrevision(String u, Connection c) throws Exception{
//        Prevision mere = new Prevision();
//        Vente venteComplet = this.getVenteWithMontant(c);
//        mere.setDaty(datyPrevu);
//        mere.setCredit(venteComplet.getMontantttcAr());
//        mere.setIdFacture(this.id);
//        mere.setIdCaisse(ConstanteStation.idCaisse);
//        mere.setIdDevise("AR");
//        mere.setDesignation("Prevision rattachée au vente N : "+this.getId());
//        mere.setIdTiers(this.getIdClient());
//        return ( Prevision ) mere.createObject(u, c);

        VenteDetails[] fille = (VenteDetails[]) this.getFille();
        List<Prevision> previsions = new ArrayList<>();


        for(VenteDetails f : fille)
        {

            Prevision prev = f.genererPrevision();
            prev.setDaty(this.getDatyPrevu());
            prev.setIdTiers(this.getIdClient());
            Prevision newPrev = (Prevision) prev.createObject(u, c);
            previsions.add(newPrev);
            System.out.println("Prevision created: " + newPrev.getId());

        }

        return previsions.toArray(new Prevision[previsions.size()]);

    }

    public Prevision[] genererPrevisionBatch(String u, Statement st) throws Exception{
//        Prevision mere = new Prevision();
//        Vente venteComplet = this.getVenteWithMontant(c);
//        mere.setDaty(datyPrevu);
//        mere.setCredit(venteComplet.getMontantttcAr());
//        mere.setIdFacture(this.id);
//        mere.setIdCaisse(ConstanteStation.idCaisse);
//        mere.setIdDevise("AR");
//        mere.setDesignation("Prevision rattachée au vente N : "+this.getId());
//        mere.setIdTiers(this.getIdClient());
//        return ( Prevision ) mere.createObject(u, c);
        //Connection c=st.getConnection();
        VenteDetails[] fille = (VenteDetails[])this.getFille();
        List<Prevision> previsions = new ArrayList<>();


        for(VenteDetails f : fille)
        {
            Prevision prev = f.genererPrevision();
            prev.setDaty(this.getDatyPrevu());
            prev.setIdTiers(this.getIdClient());
            Prevision newPrev = (Prevision) prev.createObjectBatch(u, st);
            previsions.add(newPrev);
        }

        return previsions.toArray(new Prevision[previsions.size()]);

    }
    
    public Vente getVenteWithMontant(Connection c) throws Exception{
        return (Vente)new Vente().getById(this.getId(), "VENTE_CPL", c);
    }
    
    public String genererBonLivraison(String u) throws Exception{
        Connection c = null;
        try{
            c = new UtilDB().GetConn();
            c.setAutoCommit(false);
            Vente enBase = (Vente)this.getById(this.getId(), this.getNomTable(), c);
            VenteDetailsLib vLib = new VenteDetailsLib();
            vLib.setNomTable("VENTE_DETAILS_RESTE");
            VenteDetailsLib[] details = (VenteDetailsLib[]) CGenUtil.rechercher(vLib,null,null,c," AND idVente='"+this.getId()+"' AND reste > 0");
            if(details.length > 0){
                As_BondeLivraisonClient client = new As_BondeLivraisonClient();
                client.setMode("modif");
                client.setIdvente(this.getId());
                client.setEtat(1);
                client.setIdclient(enBase.getIdClient());
                client.setRemarque("Livraison de la facture numero "+this.getId());
                client.setDaty(Utilitaire.dateDuJourSql());
                client.createObject(u, c);
                for(VenteDetailsLib detail:details){
                    As_BondeLivraisonClientFille clientFille = new As_BondeLivraisonClientFille();
                    clientFille.setMode("modif");
                    clientFille.setProduit(detail.getIdProduit());
                    clientFille.setUnite(detail.getIdUnite());
                    clientFille.setQuantite(detail.getReste());
                    clientFille.setIdventedetail(detail.getId());
                    clientFille.setNumbl(client.getId());
                    clientFille.createObject(u, c);
                }
                c.commit();
                return client.getId();
            }
            throw new Exception("Plus aucun article à livrer");
        }
        catch(Exception e){
            if(c!=null) c.rollback();
            throw e;
        }
        finally{
            if(c!=null) c.close();
        }
    }

    public As_BondeLivraisonClient genererBonLivraison() throws Exception{
        Connection c = null;
        try{
            c = new UtilDB().GetConn();
            Vente enBase = (Vente)this.getById(this.getId(), this.getNomTable(), c);
            VenteDetailsLib vLib = new VenteDetailsLib();
            vLib.setNomTable("VENTE_DETAILS_RESTE");
            VenteDetailsLib[] details = (VenteDetailsLib[]) CGenUtil.rechercher(vLib,null,null,c," AND idVente='"+this.getId()+"' AND reste > 0");
            if(details.length > 0){
                As_BondeLivraisonClient client = new As_BondeLivraisonClient();
                client.setMode("modif");
                client.setIdbc(enBase.getIdOrigine());
                client.setIdvente(this.getId());
                client.setMagasin(enBase.getIdMagasin());
                client.setEtat(1);
                client.setIdclient(enBase.getIdClient());
                client.setRemarque("Livraison de la facture numero "+this.getId());
                client.setDaty(Utilitaire.dateDuJourSql());
                client.setIdorigine(this.getId());
                As_BondeLivraisonClientFille [] filles = new As_BondeLivraisonClientFille[details.length];
                int i = 0;
                List<As_BondeLivraisonClientFille_Cpl> listeFilles = new ArrayList<>();
                for(VenteDetailsLib detail:details){
                    As_BondeLivraisonClientFille_Cpl clientFille = new As_BondeLivraisonClientFille_Cpl();
                    clientFille.setMode("modif");
                    clientFille.setProduit(detail.getIdProduit());
                    Ingredients ing = (Ingredients) new Ingredients().getById(detail.getIdProduit(),"AS_INGREDIENTS",c);
                    if (ing.getCategorieIngredient() != null &&
                            ing.getCategorieIngredient().compareToIgnoreCase(ConstanteStation.idProduitService) == 0) {
                            continue; 
                    }
                    clientFille.setProduitlib(ing.getLibelleVente());
                    clientFille.setUnite(detail.getIdUnite());
                    clientFille.setUnitelib(detail.getUnitelib());
                    clientFille.setQuantite(detail.getReste());
                    clientFille.setIdventedetail(detail.getId());
                    clientFille.setIdbc_fille(detail.getIdbcfille());
                    clientFille.setNumbl(client.getId());
                    filles[i] = clientFille;
                    i++;
                    listeFilles.add(clientFille);
                }
                As_BondeLivraisonClientFille[] lsfilles = listeFilles.toArray(new As_BondeLivraisonClientFille[listeFilles.size()]);
                client.setFille(lsfilles);
                return client;
            }
            throw new Exception("Plus aucun article à livrer");
        }
        catch(Exception e){
            throw e;
        }
        finally{
            if(c!=null) c.close();
        }
    }

    public String getCompte() {
        return compte;
    }

    
    public void setCompte(String compte) {
        this.compte = compte;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) throws Exception {
        if(this.getMode().compareTo("modif")==0)
        {
            if(idClient==null||idClient.compareToIgnoreCase("")==0)
                throw new Exception("Client obligatoire");
        }
        this.idClient = idClient;
    }

    public Vente() {
        this.setNomTable("VENTE");
    }

    public Vente(String nomtable) {
        setNomTable(nomtable);
    }

    public int getEstPrevu() {
	 return estPrevu;
    }

    public void setEstPrevu(int estPrevu) {
	 this.estPrevu = estPrevu;
    }

    
    
    
    


    public String getIdMagasin() {
        return idMagasin;
    }

    public void setIdMagasin(String idMagasin) {
        this.idMagasin = idMagasin;
    }

    

    public boolean isPaye(){
        if(this.getEtat()== ConstanteEtatCustom.PAYE_LIVRE  || this.getEtat()==ConstanteEtatCustom.PAYE_NON_LIVRE){
            return true;
        }
        return false;
    }

    public boolean isLivre(){
        if(this.getEtat()== ConstanteEtatCustom.LIVRE_NON_PAYE || this.getEtat()==ConstanteEtatCustom.PAYE_LIVRE){
            return true;
        }
        return false;
    }

    

    public String getClientlib() {
        return clientlib;
    }

    public void setClientlib(String clientlib) {
        this.clientlib = clientlib;
    }

    public double getTauxdechange() {
        return tauxdechange;
    }

    public void setTauxdechange(double tauxdechange) {
        this.tauxdechange = tauxdechange;
    }
    
    public void payer(String u,Connection c) throws Exception{
        if(this.getEtat() < ConstanteEtat.getEtatValider()){
            throw new Exception("Impossible d encaisser une vente non validée");
        }
        if(isLivre()){
            this.updateEtat(ConstanteEtatCustom.PAYE_LIVRE, this.getId(), c);
        }
        else{
            this.updateEtat(ConstanteEtatCustom.PAYE_NON_LIVRE, this.getId(), c);
        }
        genererEcritureEncaissement(u, c);
    }

    public void livrer(String u,Connection c) throws Exception{
        if(this.getEtat() < ConstanteEtat.getEtatValider()){
            throw new Exception("Impossible de livrer une vente non validée");
        }
        if(isPaye()){
            this.updateEtat(ConstanteEtatCustom.PAYE_LIVRE, this.getId(), c);
        }
        else{
            this.updateEtat(ConstanteEtatCustom.LIVRE_NON_PAYE, this.getId(), c);
        }
    }

    public void lierLivraisons(String u, String [] idLivraison) throws Exception{
        Connection c = null;
        try {
            c = new UtilDB().GetConn();
            VenteDetails[] venteDetails = getVenteDetails(c);
            As_BondeLivraisonClient [] blcs = As_BondeLivraisonClient.getAll(idLivraison,c);
            As_BondeLivraisonClient.controlerClient(blcs);
            for (As_BondeLivraisonClient blcTemp : blcs) {
                blcTemp.setIdvente(this.getId());
                blcTemp.updateToTableWithHisto(u, c);
                As_BondeLivraisonClientFille [] blcfs = (As_BondeLivraisonClientFille[]) CGenUtil.rechercher(new As_BondeLivraisonClientFille(), null, null, c, " and NUMBL = '"+ blcTemp.getId() +"'");
                    for (int i = 0; i < venteDetails.length; i++) {
                        for (As_BondeLivraisonClientFille as_BondeLivraisonClientFilleTemp: blcfs) {
                            if(venteDetails[i].getIdProduit().equals(as_BondeLivraisonClientFilleTemp.getProduit())){
                                as_BondeLivraisonClientFilleTemp.setIdventedetail(venteDetails[i].getId());
                                as_BondeLivraisonClientFilleTemp.updateToTableWithHisto(u, c);
                            }
                        }
                    }
            }
            
        } catch (Exception e) {
            throw e;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
    
    @Override
    public void changeState(String acte, String u,Connection con)throws Exception{
        if(acte.equals("livrer")){
            this.livrer(u, con);
        }
        else if(acte.equals("payer")){
            this.payer(u, con);
        }
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    @Override
    public String getTuppleID() {
        return this.getId();
    }

    @Override
    public String getAttributIDName() {
        return "id";
    }

    public String getIdOrigine() {
        return idOrigine;
    }

    public void setIdOrigine(String idOrigine) {
        this.idOrigine = idOrigine;
    }


    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("VNT", "getSeqVente");
        this.setId(makePK(c));
    }

    public Caisse getCaisse(Connection c) throws Exception {
        Caisse caisse = new Caisse();
        Caisse[] caisses = (Caisse[]) CGenUtil.rechercher(caisse, null, null, c, " and idMagasin = '"+this.getIdMagasin()+"'");
        if (caisses.length > 0) {
            return caisses[0];
        }
        return null;
    }

    public Magasin getMagasin(Connection c) throws Exception {
        Magasin magasin = new Magasin();
        magasin.setId(this.getIdMagasin());
        Magasin[] magasins = (Magasin[]) CGenUtil.rechercher(magasin, null, null, c, " ");
        if (magasins.length > 0) {
            return magasins[0];
        }
        return null;
    }

    public VenteDetails[] getVenteDetails() {
        return venteDetails;
    }

    public void setVenteDetails(VenteDetails[] venteDetails) {
        this.venteDetails = venteDetails;
    }

    @Override
    public void controler(Connection c) throws Exception {
        super.controler(c);
    }

    @Override
    public void controlerUpdate(Connection c) throws Exception {
        super.controlerUpdate(c);

    }

    public void createMvtCaisses(String u, Connection c) throws Exception {
        VenteDetailsCpl vdc = new VenteDetailsCpl();
        vdc.setIdVente(this.getId());
        VenteDetailsCpl[] vdcs = (VenteDetailsCpl[]) CGenUtil.rechercher(vdc, null, null, c, " ");
        for (int i = 0; i < vdcs.length; i++) {
            MvtCaisse mc = vdcs[i].createMvtCaisse();
            mc.createObject(u, c);
        }
    }

    public VenteDetailsLib[] getVenteDetails(Connection c) throws Exception {
            VenteDetailsLib obj = new VenteDetailsLib();
            obj.setNomTable("VENTE_DETAILS_CPL");
            obj.setIdVente(this.getId());
            VenteDetailsLib[] objs = (VenteDetailsLib[]) CGenUtil.rechercher(obj, null, null, c, " ");
            if (objs.length > 0) {
                return objs;
            }
            return null;
    }
protected EncaissementDetails [] generateDetailsEncaissements (Connection c ) throws Exception{
       VenteDetailsLib[] vd= this.getVenteDetails(c);
        EncaissementDetails[] ed=new EncaissementDetails [vd.length];
        for(int i=0;i<ed.length;i++){
            ed[i]=vd[i].generateEncaissementDetails();
        }
        return ed;
 }

    @Override
    public Object payerObject(String u,Connection con) throws Exception{
        super.payerObject(u, con);
        Encaissement enc=this.genererEncaissement();
        enc=(Encaissement) enc.createObject(u, con);
        EncaissementDetails [] ed=generateDetailsEncaissements ( con );
        for(int i=0;i<ed.length;i++){
             ed[i].setIdEncaissement(enc.getId());
             ed[i].createObject(u,con);
        }
        return enc;
    } 


 
    protected MvtStockFille[] createMvtStockFilles(Connection c) throws Exception {
        VenteDetails[] tsd = this.getVenteDetails(c);
        MvtStockFille[] mvtf = new MvtStockFille[tsd.length];
        for (int i = 0; i < tsd.length; i++) {
            mvtf[i] = tsd[i].createMvtStockFille();
        }
        return mvtf;
    }

    protected MvtStock createMvtStock() throws Exception {
        MvtStock md = new MvtStock();
        md.setDaty(this.getDaty());
        md.setDesignation("Vente lubrifiant : " + this.getDesignation());
        md.setIdTransfert(this.getId());
        md.setIdTypeMvStock(ConstanteStation.TYPEMVTSTOCKSORTIE);
        md.setIdMagasin(this.getIdMagasin());
        return md;
    }

    protected MvtStock createMvtStockSortie(String u, Connection c) throws Exception {
        MvtStock ms = this.createMvtStock();
        ms.setFille(this.createMvtStockFilles(c));
        ms.createObject(u, c);
        ms.saveMvtStockFille(u, c);
        ms.validerObject(u, c);
        return ms;
    }

    @Override
    public ClassMAPTable createObjectBatch(String u, Statement st) throws Exception {
        boolean estOuvert=false;
        Connection c=null;
        try {
            if (st == null) {
                c = new UtilDB().GetConn();
                estOuvert = true;
                st=c.createStatement();
            }
            else c = st.getConnection();
            if (this.getFactureEchange() != null && !this.getFactureEchange().trim().isEmpty()) {
                Vente ancienneVente = this.getAncien(c);
                if (ancienneVente.getMontantTTCCalcul() > this.getMontantTTCCalcul()) {
                    throw new Exception("La facture d'échange ne peut pas être supérieure à la facture initiale.");
                }
            }
            String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            this.setHeureSaisie(currentTime);
            //NumeroFacture[] numFact = (NumeroFacture[]) CGenUtil.rechercher(new NumeroFacture(), null, null, c, "");
            //this.setNumerofacture(numFact[0].getProchain_num_format());
            VenteDetails[] listeFille = (VenteDetails[]) this.getFille();
            RemiseFille[] listeRemise = this.getRemise(null, c);
            Client cl = null;
            Ingredients[] listeIng = null;
            if (listeRemise != null && listeRemise.length > 0) {
                cl = this.getClient(c);
                String apresW = " and id in (" + Utilitaire.tabToString(listeFille, "idProduit", "'", ",") + ")";
                //listeIng=(Ingredients[])CGenUtil.rechercher(new Ingredients(),null,null,c,apresW);
                listeIng = this.getListeIngredients(null, c);
            }
            Set<String> seen = new HashSet<>();
            System.out.println("AVANT CHECK DETAIL NULL");

            if (this.getFille() == null) {
                throw new Exception("Vente Detail is null");
            }
            System.out.println("APRES CHECK DETAIL NULL");

            for (VenteDetails vd : listeFille) {
                System.out.println("check 1");
                String key = vd.getIdProduit();
                System.out.println("check 2");
                String designation = vd.getDesignation();
                System.out.println("check 3");
                System.out.println("Clinet: " + cl.getId());
                vd.setCatClient(cl.getIdTypeClient());
                RemiseFille rf = vd.estDansRemise(listeRemise, listeIng);
                String idDevise = null;
                if (vd.getIdDevise() != null && !vd.getIdDevise().trim().isEmpty()) {
                    idDevise = vd.getIdDevise().trim().toUpperCase();
                    vd.setIdDevise(idDevise);
                }
                //vd.setTva(20);
                if (rf != null) {
                    vd.setRemise(rf.getRemise());
                    vd.setIdRemise(rf.getIdremise());
                }
                if (key == null || key.trim().isEmpty()) continue;

                if (!seen.add(key)) {
                    throw new Exception("Doublon pour l'article : " + designation);
                }
            }
            System.out.println("APRES CHECK LISTE FILLE");
//        CheckEtatStockVenteDetailsALaCreation(c);
            this.checkEtatStockGroupe(null,c);


            Vente createVente = (Vente) super.createObjectBatch(u, st);
            String codebarre = this.getId();

            this.genererCodeBarreWithName(createVente.getId(), ConstanteAsync.buildChemin(codebarre), codebarre, u);
            //if (estOuvert==true)st.executeBatch();
            return createVente;
        }
        catch (Exception e) {
            c.rollback();
            throw e;
        }
        finally{
            if(c!=null&&estOuvert==true)
            {
                st.close();
                c.close();
            }
        }
    }

    
    @Override
    public ClassMAPTable createObject(String u, Connection c) throws Exception {
//        if (this.getFactureEchange() != null && !this.getFactureEchange().trim().isEmpty()) {
//            Vente ancienneVente = this.getAncien(c);
//            if (ancienneVente.getMontantTTCCalcul() > this.getMontantTTCCalcul()) {
//                throw new Exception("La facture d'échange ne peut pas être supérieure à la facture initiale.");
//            }
//        }
//        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
//        this.setHeureSaisie(currentTime);
//        NumeroFacture[] numFact = (NumeroFacture[]) CGenUtil.rechercher(new NumeroFacture(), null, null, c, "");
//        this.setNumerofacture(numFact[0].getProchain_num_format());
//        VenteDetails[] listeFille = (VenteDetails[]) this.getFille();
//        RemiseFille[] listeRemise=this.getRemise(null,c);
//        Client cl=null;
//        Ingredients[] listeIng=null;
//        if(listeRemise!=null&&listeRemise.length>0)
//        {
//            cl=this.getClient(c);
//            String apresW=" and id in ("+Utilitaire.tabToString(listeFille,"idProduit","'",",")+")";
//            //listeIng=(Ingredients[])CGenUtil.rechercher(new Ingredients(),null,null,c,apresW);
//            listeIng=this.getListeIngredients(null,c);
//        }
//        Set<String> seen = new HashSet<>();
//
//        if (this.getFille() == null)
//        {
//            throw new Exception("Vente Detail is null");
//        }
//
//        for (VenteDetails vd : listeFille) {
//            String key = vd.getIdProduit();
//            String designation = vd.getDesignation();
//            vd.setCatClient(cl.getIdTypeClient());
//            RemiseFille rf=vd.estDansRemise(listeRemise,listeIng);
//            String idDevise = null;
//            if (vd.getIdDevise() != null || !vd.getIdDevise().trim().isEmpty())
//            {
//                idDevise = vd.getIdDevise().trim().toUpperCase();
//                vd.setIdDevise(idDevise);
//            }
//
//            if(rf!=null)
//            {
//                vd.setRemise(rf.getRemise());
//                vd.setIdRemise(rf.getIdremise());
//            }
//            if (key == null || key.trim().isEmpty()) continue;
//
//            if (!seen.add(key)) {
//                throw new Exception("Doublon pour l'article : " + designation);
//            }
//        }
////        CheckEtatStockVenteDetailsALaCreation(c);
//
//
//        Vente createVente = (Vente) super.createObject(u, c);
//        String codebarre = this.getId();
//
//        this.genererCodeBarreWithName(createVente.getId(), ConstanteAsync.buildChemin(codebarre), codebarre, u);
//
//        return createVente;
        boolean estOuvert=false;
        Statement st=null;
        try {
            if(c==null)
            {
                c=new UtilDB().GetConn();
                c.setAutoCommit(false);
                estOuvert = true;
            }
            st=c.createStatement();
            ClassMAPTable ret= this.createObjectBatch(u,st);
            st.executeBatch();
            c.commit();
            st.close();
            return ret;
        } catch (Exception e) {
            c.rollback();
            throw (e);
        } finally {
            if(estOuvert==true&&st!=null)st.close();
            if(estOuvert==true)c.close();
        }
    }

    public void checkEtatStockGroupe(String ntStock, Connection c) throws Exception {
        Ingredients[] ids = this.getListeIngredients(null, c);
        VenteDetails[] listeFille = (VenteDetails[]) this.getFille();
        if (ids == null || ids.length == 0 || listeFille == null || listeFille.length == 0) return;

        // Indexer les ingredients par id pour acces rapide
        Map<String, Ingredients> mapIng = new HashMap<String, Ingredients>();
        for (Ingredients ing : ids) {
            if (ing != null && ing.getId() != null) mapIng.put(ing.getId(), ing);
        }

        // Collecter les ids des produits stockables demandes
        Set<String> idsProdStockables = new LinkedHashSet<String>();
        for (VenteDetails vd : listeFille) {
            if (vd == null || vd.getIdProduit() == null || vd.getIdProduit().trim().isEmpty()) continue;
            Ingredients ing = mapIng.get(vd.getIdProduit());
            if (ing != null && ing.isService()) continue;
            idsProdStockables.add(vd.getIdProduit());
        }
        if (idsProdStockables.isEmpty()) return;

        // Charger et indexer le stock disponible
        Map<String, EtatStock> mapStock = chargerEtatStock(ntStock, idsProdStockables, c);

        // Controler chaque ligne de vente
        StringBuilder erreurs = new StringBuilder();
        for (VenteDetails vd : listeFille) {
            if (vd == null || vd.getIdProduit() == null || vd.getIdProduit().trim().isEmpty()) continue;
            Ingredients ing = mapIng.get(vd.getIdProduit());
            if (ing != null && ing.isService()) continue;

            EtatStock es = mapStock.get(vd.getIdProduit());
            double stockDispo = (es != null) ? es.getReste() : 0.0;
            if (stockDispo < vd.getQte()) {
                String libelle = (es != null && es.getIdProduitLib() != null && !es.getIdProduitLib().trim().isEmpty())
                        ? es.getIdProduitLib()
                        : (vd.getDesignation() != null ? vd.getDesignation() : vd.getIdProduit());
                erreurs.append("- ").append(libelle)
                        .append(" : Stock ").append(stockDispo)
                        .append(" < Demande ").append(vd.getQte()).append("\n");
            }
        }

        if (erreurs.length() > 0) {
            throw new Exception("Stock insuffisant :\n" + erreurs.toString());
        }
    }

    private Map<String, EtatStock> chargerEtatStock(String ntStock, Set<String> idsProduits, Connection c) throws Exception {
        EtatStock etat = new EtatStock();
        etat.setNomTable("V_ETATSTOCK_ING_eng");
        if (ntStock != null && ntStock.compareToIgnoreCase("") != 0) etat.setNomTable(ntStock);

        StringBuilder valIn = new StringBuilder();
        int idx = 0;
        for (String id : idsProduits) {
            if (idx > 0) valIn.append(",");
            valIn.append("'").append(id).append("'");
            idx++;
        }

        String awhere = " and id in(" + valIn.toString() + ") and idpoint='" + this.getIdMagasin() + "' ";
        EtatStock[] et = (EtatStock[]) CGenUtil.rechercher(etat, null, null, c, awhere);
        if (et == null) et = new EtatStock[0];

        Map<String, EtatStock> map = new HashMap<String, EtatStock>();
        for (EtatStock es : et) {
            if (es != null && es.getId() != null) map.put(es.getId(), es);
        }
        return map;
    }

    public RemiseFille[] getRemise(String nt, Connection c) throws Exception {
        RemiseFille crt = new RemiseFille();
        crt.setNomTable("remiseFilleMereVise");
        if(nt!=null&&nt.compareToIgnoreCase("")!=0)crt.setNomTable(nt);
        String apresWherePoint=" and (idpoint='"+this.getIdMagasin()+"' or idpoint is null) ";
        String apresWhereDaty=" and '"+Utilitaire.formatterDaty(this.getDaty())+"'>=dateDebut and '"+Utilitaire.formatterDaty(this.getDaty())+ "'<=dateFin order by daty desc";
        RemiseFille[] ret= (RemiseFille[]) CGenUtil.rechercher(crt,null,null, c,apresWherePoint+ apresWhereDaty);
        return ret;
    }
    public Ingredients[] getListeIngredients(String nt,Connection c) throws Exception {
        if(this.getListeIng()!=null&&(nt==null||nt.compareToIgnoreCase("")==0))return this.getListeIng();
        Ingredients crt=new Ingredients();
        VenteDetails[]fille=(VenteDetails[]) this.getFille();
        if(nt!=null&&nt.compareToIgnoreCase("")!=0)crt.setNomTable(nt);
        String apresW=" and id in ("+Utilitaire.tabToString(fille,"idProduit","'",",") +")";
        setListeIng ((Ingredients[]) CGenUtil.rechercher(crt,null,null, c,apresW));
        return listeIng;
    }

    @Override
    public Object validerObject(String u, Connection c) throws Exception {
//        boolean estOuvert = false;
//        try {
//            if (c == null) {
//                estOuvert = true;
//                c = new UtilDB().GetConn();
//            }
////            CheckEtatStockVenteDetails(c);
//            VenteDetails[] listeFille=(VenteDetails[]) this.getFille();
//            Ingredients[] listeIng=this.getListeIngredients(null,c);
//            for(int i=0;i<listeFille.length;i++){
//                Ingredients ingredients = null;
//                if(listeFille[i].getIdProduit()!=null&&!listeFille[i].getIdProduit().trim().isEmpty())
//                {
//                    String []col={"id"};
//                    String []val={listeFille[i].getIdProduit()};
//                    ingredients=(Ingredients)AdminGen.find(listeIng,col,val)[0];
//                    listeFille[i].setPoids(ingredients.getCalorie());
//                    listeFille[i].calculerRevient(c);
//                    listeFille[i].updateToTableWithHisto(u,c);
//                }
//            }
//            super.validerObject(u, c);
//            echanger(u,c);
//            setFille(listeFille);
//            genererEcriture(u, c);
//            MvtStock stockEngage = genererStockEngage(listeFille, c);
//            if (stockEngage != null) {
//                stockEngage.createObject(u, c);
//            }
//
//            Ristourne ristourne = genererRistourne();
//            if (ristourne != null) {
//                ristourne.createObject(u, c);
//                ristourne.validerObject(u, c);
//            }
//
////            createMvtStockSortie(u, c);
//            if(this.getEstPrevu() == 0){
//                genererPrevision(u, c);
//            }
//            else if (this.getEstPrevu() == 1){
//                genererPrevisionPrevu(u, c);
//            }
//            boolean estAppro = false;
//            String[] clientsAppro = ConstanteSocobis.CLIENT_APPRO_VENTE;
//            for (String idClientListe : clientsAppro) {
//                if (this.getIdClient().equals(idClientListe)) {
//                    estAppro = true;
//                    break;
//                }
//            }
//            if(estAppro == true){
//                //genererApprovisionnement(listeFille, u, c);
//            }
//
//            return this;
//
//        } catch (Exception e) {
//            if (c != null) {
//                c.rollback();
//            }
//            e.printStackTrace();
//            throw e;
//        } finally {
//            if (c != null && estOuvert == true) {
//                c.close();
//            }
//        }
        c.setAutoCommit(false);
        Statement st=c.createStatement();
        try{
            System.out.println("AVANT VALIDER OBCJECT BATCH "+Utilitaire.heureCouranteHMS());
            Object o= this.validerObjectBatch(u, st);
            st.executeBatch();
            st.close();
            c.commit();
            System.out.println("APRES COMMIT BATCH "+Utilitaire.heureCouranteHMS());
            return o;
        }
        catch(Exception e){
            c.rollback();
            throw e;
        }
        finally {
            if(st!=null) st.close();
        }
    }

    public TransfertStock genererApprovisionnement(VenteDetails[] listeFille, String u, Connection c) throws Exception {
        TransfertStock st = new TransfertStock();
        st.setDaty(this.getDaty());
        st.setDesignation("Transfert de stock pour vente "+this.getId());
        st.setIdMagasinArrive(ConstanteSocobis.MAGASIN_APPRO);
        st.setIdMagasinDepart(this.getIdMagasin());
        st.setIsvente(1);
        st.setFille(new TransfertStockDetails[0]);
        st.createObject(u,c);
        TransfertStockDetails[] transfertFille =  genererApprovisionnementFilleParPiece(listeFille, st, u, c);
        st.validerObject(u, c);
        return st;
    }

    public TransfertStock genererTransfertStock(Connection c) throws Exception {
        VenteDetails[] listeFille=(VenteDetails[]) this.getFille(null,c,"");
        TransfertStock st = new TransfertStock();
        st.setDaty(this.getDaty());
        st.setDesignation("Transfert de stock pour vente "+this.getId());
        st.setIdMagasinArrive(ConstanteSocobis.MAGASIN_APPRO);
        st.setIdMagasinDepart(this.getIdMagasin());
        st.setIsvente(1);
        st.setFille(new TransfertStockDetails[0]);
        //st.createObject(u,c);
        TransfertStockDetails[] transfertFille =  genererTransfertFilleParPiece(listeFille, st, c);
        st.setFille(transfertFille);
        //st.validerObject(u, c);
        return st;
    }

    public TransfertStockDetails[] genererApprovisionnementFilleParPiece(VenteDetails[] listeFille, TransfertStock mere, String u, Connection c) throws Exception {
        if (listeFille == null || listeFille.length == 0) {
            listeFille = (VenteDetails[]) this.getFille(null,c,"");
        }
        TransfertStockDetailsAppro[] fille = new TransfertStockDetailsAppro[listeFille.length];
        for (int i = 0; i < listeFille.length; i++) {

            double qtePiece = listeFille[i].getQte();
            double puPiece =  listeFille[i].getPu();
            String idproduit1 = "";
            /*EquivalenceVente equivalence = Ingredients.getEquivalenceVente(listeFille[i].getIdProduit(), ConstanteSocobis.UNITE_PIECE, c);
            if(equivalence != null){
                qtePiece = listeFille[i].getQte() * equivalence.getQuantite();
                puPiece = listeFille[i].getPu() / equivalence.getQuantite();
                idproduit1 = equivalence.getIdproduit1();
            }*/
            fille[i] = new TransfertStockDetailsAppro();
            fille[i].setQuantitesource(listeFille[i].getQte());
            fille[i].setPusource(listeFille[i].getPu());
            fille[i].setIdProduit(listeFille[i].getIdProduit());
            fille[i].setDaty(listeFille[i].getDatesaisie());
            fille[i].setPu(listeFille[i].getPu());
            fille[i].setQuantite(listeFille[i].getQte());
            fille[i].setIdTransfertStock(mere.getId());
            fille[i].setEstapprodv(1);
            fille[i].createObject(u,c);
        }
        mere.setFille(fille);
        return  fille;
    }

    public TransfertStockDetails[] genererTransfertFilleParPiece(VenteDetails[] listeFille, TransfertStock mere, Connection c) throws Exception {
        if (listeFille == null || listeFille.length == 0) {
            listeFille = (VenteDetails[]) this.getFille(null,c,"");
        }
        TransfertStockDetailsAppro[] fille = new TransfertStockDetailsAppro[listeFille.length];
        for (int i = 0; i < listeFille.length; i++) {

            double qtePiece = listeFille[i].getQte();
            double puPiece =  listeFille[i].getPu();
            String idproduit1 = "";
            /*EquivalenceVente equivalence = Ingredients.getEquivalenceVente(listeFille[i].getIdProduit(), ConstanteSocobis.UNITE_PIECE, c);
            if(equivalence != null){
                qtePiece = listeFille[i].getQte() * equivalence.getQuantite();
                puPiece = listeFille[i].getPu() / equivalence.getQuantite();
                idproduit1 = equivalence.getIdproduit1();
            }*/
            fille[i] = new TransfertStockDetailsAppro();
            fille[i].setQuantitesource(listeFille[i].getQte());
            fille[i].setPusource(listeFille[i].getPu());
            fille[i].setIdProduit(listeFille[i].getIdProduit());
            fille[i].setDaty(listeFille[i].getDatesaisie());
            fille[i].setPu(listeFille[i].getPu());
            fille[i].setQuantite(listeFille[i].getQte());
            fille[i].setIdTransfertStock(mere.getId());
            fille[i].setEstapprodv(1);
            //fille[i].createObject(u,c);
        }
        mere.setFille(fille);
        return  fille;
    }

    public Ristourne genererRistourne() throws Exception {
        Ristourne ristourne = new Ristourne();
        ristourne.setEstLiaisonPaiement(1);
        ristourne.setDesignation("Ristourne de la facture "+this.getId());
        ristourne.setIdClient(getIdClient());
        ristourne.setIdOrigine(this.getId());
        ristourne.setDaty(Utilitaire.dateDuJourSql());
        ristourne.setMois(Utilitaire.getMois(getDaty()));
        ristourne.setAnnee(Utilitaire.getAnnee(getDaty()));
        ristourne.setIdVente(getId());
        ristourne.setDateDebutRistourne(null);
        ristourne.setDateFinRistourne(null);

        List<RistourneDetails> ristourneDetails = getRistourneDetails();

        if (ristourneDetails.isEmpty()) {
            return null;
        }

        ristourne.setFille(ristourneDetails.toArray(new RistourneDetails[]{}));
        return ristourne;
    }

    private List<RistourneDetails> getRistourneDetails() throws Exception {
        VenteDetails[] listeFille= (VenteDetails[]) this.getFille();
        List<RistourneDetails> ristourneDetails = new ArrayList<>();
        for (VenteDetails venteFille : listeFille) {
            if (venteFille.getRistourne() != 0) {
                RistourneDetails ristourneFille = new RistourneDetails();
                ristourneFille.setIdProduit(venteFille.getIdProduit());
                ristourneFille.setIdOrigine(venteFille.getIdOrigine());
                ristourneFille.setTaux1(venteFille.getRistourne());
                ristourneFille.setPu(venteFille.getPu()*(1+(venteFille.getTva()/100))*venteFille.getQte());
                ristourneFille.setQte((int)venteFille.getQte());
                ristourneDetails.add(ristourneFille);
            }
        }
        return ristourneDetails;
    }

    public MvtStock genererStockEngage(VenteDetails[] listeFille, Connection c) throws Exception {
        MvtStock mvtf = new MvtStock();
        mvtf.setNomTable("stock_engage");
        mvtf.setDesignation("Engagement du vente " + this.getId());
        mvtf.setIdMagasin(this.getIdMagasin());
        mvtf.setIdVente(this.getId());
        mvtf.setIdTypeMvStock(ConstanteStation.TYPEMVTSTOCKSORTIE);
        mvtf.setIdobjet(this.getId());
        mvtf.setDaty(this.getDaty());
        mvtf.setEtat(ConstanteEtat.getEtatValider());

        List<MvtStockFille> mvtStockFilles = new ArrayList<>();
        for (VenteDetails vd : listeFille) {
            if(vd.getIdProduit()!=null&&vd.getIdProduit().compareToIgnoreCase("")!=0){
                Ingredients ing = vd.getIngFille();
                String catIng = null;
                String idProd = vd.getIdProduit();
                if (ing != null) {
                    catIng = ing.getCategorieIngredient();
                } else {
                    Produit produit = vd.getProduitAs(c);
                    if (produit != null) {
                        idProd = produit.getId();
                        catIng = produit.getIdCategorie();
                    }
                }
                if(catIng != null && catIng.compareToIgnoreCase(ConstanteStation.idProduitAutre)!=0 && catIng.compareToIgnoreCase(ConstanteStation.idProduitService)!=0){
                    MvtStockFille mvtFille = new MvtStockFille();
                    mvtFille.setNomTable("stock_engage_fille");

                    mvtFille.setIdProduit(idProd);
                    mvtFille.setIdVenteDetail(vd.getId());
                    mvtFille.setDesignation("Engagement detail : " + vd.getId());
                    mvtFille.setCategorieIngredient(catIng);
                    mvtFille.setEntree(0);
                    mvtFille.setSortie(vd.getQte());
                    mvtFille.setPu(vd.getPu());
                    mvtFille.setMontant(vd.getMontant());
                    mvtFille.setEtat(ConstanteEtat.getEtatValider());

                    mvtStockFilles.add(mvtFille);
                }
            }
        }
        if(mvtStockFilles.size()>0){
            mvtf.setFille(mvtStockFilles.toArray(new MvtStockFille[]{}));
            return mvtf;
        }
        return null;
    }

    public void CheckEtatStockVenteDetails(Connection c) throws Exception {
        VenteDetailsLib[] vds = getVenteDetails(c);
        if (vds != null) {
            for (VenteDetailsLib v : vds) {
                Ingredients [] ings = (Ingredients[]) CGenUtil.rechercher(new Ingredients(), null, null, c, " and id = '"+v.getIdProduit()+"'");
                if(ings[0].getCategorieIngredient().compareToIgnoreCase(ConstanteStation.idProduitAutre)!=0 &&  ings[0].getCategorieIngredient().compareToIgnoreCase(ConstanteStation.idProduitService)!=0){
                    v.CheckEtatStock(c);
                }
            }
        }
    }

    public void CheckEtatStockVenteDetailsALaCreation(Connection c) throws Exception {
        VenteDetails[] vds = (VenteDetails[])this.getFille();
        if (vds != null) {
            for (VenteDetails v : vds) {
                v.CheckEtatStockALaCreation(c, this.getIdMagasin());
            }
        }
    }
    
    public void genererEcritureEncaissement(String u, Connection c) throws Exception{
        ComptaEcriture mere = new ComptaEcriture();
        Date dateDuJour = utilitaire.Utilitaire.dateDuJourSql();
        int exercice = utilitaire.Utilitaire.getAnnee(daty);
        mere.setDaty(dateDuJour);
        mere.setDesignation(this.getDesignation());
        mere.setExercice(""+exercice);
        mere.setDateComptable(this.getDaty());
        mere.setJournal(ConstanteStation.JOURNALVENTE);
        mere.setOrigine(this.getId());
        mere.setIdobjet(this.getId());
        mere.createObject(u, c);
        ComptaSousEcriture[] filles = this.genererSousEcritureEncaissement(u,c);
        for(int i=0; i<filles.length; i++){
            filles[i].setIdMere(mere.getId());
            filles[i].setExercice(exercice);
            filles[i].setDaty(this.getDaty());
            filles[i].setJournal(ConstanteStation.JOURNALVENTE);
            
            if(filles[i].getDebit()>0 || filles[i].getCredit()>0) filles[i].createObject(u, c);
        }
    }
    
    public ComptaSousEcriture[] genererSousEcritureEncaissement(String refUser,Connection c) throws Exception{
        ComptaSousEcriture[] compta={};
        boolean canClose=false;
        try{
            if(c==null){
                c=new UtilDB().GetConn();
                canClose=true;
            }
            Vente[] ventes = (Vente[]) CGenUtil.rechercher(new Vente("VENTE_MERE_MONTANT"), null, null, c, " and id = '"+this.getId()+"'");
            if(ventes.length<1) throw new Exception("Facture mere Introuvable");
            this.setCompte(getClient(c).getCompte());

            compta = new ComptaSousEcriture[2];
            
            compta[0]=new ComptaSousEcriture();
            compta[0].setLibellePiece(this.getDesignation());
            compta[0].setRemarque(this.getDesignation());
            compta[0].setCompte(getCaisse(c).getCompte());
            compta[0].setDebit(ventes[0].getMontantttc());
	     MvtCaisse mvt= new MvtCaisse();
	     mvt.setCredit(ventes[0].getMontantttc());
         mvt.setIdCaisse(getCaisse(c).getId());
	     mvt.setDaty(utilitaire.Utilitaire.dateDuJourSql());
	     mvt.setDesignation( "mvt pour"+ this.getDesignation());
	     mvt.setIdOrigine(this.getId());
	     mvt.createObject(refUser, c);
	     mvt.validerObject(refUser, c);
            
            compta[1]=new ComptaSousEcriture();
            compta[1].setLibellePiece("Encaissement Client "+ventes[0].getClientlib());
            compta[1].setRemarque("Encaissement Client "+ventes[0].getClientlib());
            compta[1].setCompte(this.getCompte());
//            compta[i].setDebit((montantHT-retenue) * ((this.getTva()/100)));
            compta[1].setCredit(ventes[0].getMontantttc());
            
        } catch(Exception e){
            throw e;
        } finally {
            if(canClose){
                c.close();
            }
        }
        return compta;
    }
    
    public void genererEcriture(String u, Connection c) throws Exception{
        ComptaEcriture mere = new ComptaEcriture();
        Date dateDuJour = utilitaire.Utilitaire.dateDuJourSql();
        int exercice = utilitaire.Utilitaire.getAnnee(daty);
        mere.setDaty(dateDuJour);
        mere.setDesignation(this.getDesignation());
        mere.setExercice(""+exercice);
        mere.setDateComptable(this.getDaty());
        mere.setJournal(ConstanteStation.JOURNALVENTE);
        mere.setOrigine(this.getId());
        mere.setIdobjet(this.getId());
        mere.createObject(u, c);

        ComptaSousEcriture[] filles = this.genererSousEcriture(c);
        for(int i=0; i<filles.length; i++){
            filles[i].setIdMere(mere.getId());
            filles[i].setExercice(exercice);
            filles[i].setDaty(this.getDaty());
            filles[i].setJournal(ConstanteStation.JOURNALVENTE);

            if(filles[i].getDebit()>0 || filles[i].getCredit()>0) filles[i].createObject(u, c);
        }
    }

    public void genererEcritureBatch(String u, Statement st) throws Exception{
        ComptaEcriture mere = new ComptaEcriture();
        Date dateDuJour = utilitaire.Utilitaire.dateDuJourSql();
        int exercice = utilitaire.Utilitaire.getAnnee(daty);
        mere.setDaty(dateDuJour);
        mere.setDesignation(this.getDesignation());
        mere.setExercice(""+exercice);
        mere.setDateComptable(this.getDaty());
        mere.setJournal(ConstanteStation.JOURNALVENTE);
        mere.setOrigine(this.getId());
        mere.setIdobjet(this.getId());
        mere.construirePK(st.getConnection());

        ComptaSousEcriture[] filles = this.genererSousEcriture(st.getConnection());
        List<ComptaSousEcriture> sousEcrituresValides = new ArrayList<>();
        if (filles != null) {
            for(int i=0; i<filles.length; i++){
                filles[i].setIdMere(mere.getId());
                filles[i].setExercice(exercice);
                filles[i].setDaty(this.getDaty());
                filles[i].setJournal(ConstanteStation.JOURNALVENTE);

                if(filles[i].getDebit()>0 || filles[i].getCredit()>0) {
                    sousEcrituresValides.add(filles[i]);
                }
            }
        }
        mere.setFille(sousEcrituresValides.toArray(new ComptaSousEcriture[0]));
        mere.createObjectBatch(u, st);

    }

    public Client getClient(Connection c) throws Exception{
        Client client = new Client();
        Client[] clients = (Client[]) CGenUtil.rechercher(client,null,null,c, " and id = '"+this.getIdClient()+"'");
        if(clients.length > 0){
            this.setClient(clients[0]);
            return clients[0];
        }
        throw new Exception("Le client n'existe pas");
    }

    public double getPoidsTotal(Connection c) throws Exception{
        double poidsTotal =0;
        VenteDetails[] details = (VenteDetails[]) this.getFille();
        for (VenteDetails detail : details) {
            poidsTotal += detail.getPoids() * detail.getQte();
        }
        return poidsTotal;
    }

    public ComptaSousEcriture[] genererSousEcriture(Connection c) throws Exception{
        ComptaSousEcriture[] compta={};
        boolean canClose=false;
        try{
            if(c==null){
                c=new UtilDB().GetConn();
                canClose=true;
            }
            String magasin = this.getIdMagasin();
            //Vente[] ventes = (Vente[]) CGenUtil.rechercher(new Vente("VENTE_MERE_MONTANT"), null, null, c, " and id = '"+this.getId()+"'");
            //if(ventes.length<1) throw new Exception("Facture mere Introuvable");
            Client client = getClient(c);
            String cptClient = (client != null && client.getCompte() != null && !client.getCompte().trim().isEmpty()) ? client.getCompte() : this.getCompte();
            if (cptClient == null || cptClient.trim().isEmpty()) {
                cptClient = "411000";
            }
            this.setCompte(cptClient);
            VenteDetails [] details =(VenteDetails[]) this.getFille();
            double montantTva = AdminGen.calculSommeDouble(details,"montantTVACalc")*details[0].getTauxDeChange();
            double montantTTC = AdminGen.calculSommeDouble(details,"montantTTCCalc")*details[0].getTauxDeChange();
            int taille = details.length;
            compta = new ComptaSousEcriture[taille+2];
            if(montantTva==0)compta=new ComptaSousEcriture[taille+1];
            Ingredients ing = null;
            int i=0;
            double poidsTotal = getPoidsTotal(c);
            if(poidsTotal==0)poidsTotal=1;
            for(i=i;i<taille;i++){
                //ing = details[i].getIngFille();
                compta[i]=new ComptaSousEcriture();
                compta[i].setLibellePiece(this.getDesignation());
                compta[i].setRemarque(details[i].getLibelle());
                if(magasin != null && magasin.compareToIgnoreCase(ConstanteComptable.magasinVD) == 0){
                    compta[i].setCompte(ConstanteComptable.CompteMagasinVD);
                }else {
                    String cptVente = details[i].getCompte();
                    if (cptVente == null || cptVente.trim().isEmpty()) {
                        if (details[i].getIngFille() != null && details[i].getIngFille().getCompte_vente() != null && !details[i].getIngFille().getCompte_vente().trim().isEmpty()) {
                            cptVente = details[i].getIngFille().getCompte_vente();
                        }
                    }
                    if (cptVente == null || cptVente.trim().isEmpty()) {
                        cptVente = "701000";
                    }
                    compta[i].setCompte(cptVente);
                }
                double fraisdetails = poidsTotal * this.getFraislivraison();
                compta[i].setCredit((details[i].getMontantHTCalcule() * details[i].getTauxDeChange()) - fraisdetails);
            }
            if(montantTva>0){
                compta[i]=new ComptaSousEcriture();
                compta[i].setLibellePiece("TVA Collectee");
                compta[i].setRemarque("TVA Collectee");
                compta[i].setCompte(ConstanteStation.compteTVACollecte != null ? ConstanteStation.compteTVACollecte : "445700");
                compta[i].setCredit(montantTva);
                i++;
            }

            compta[i]=new ComptaSousEcriture();
            compta[i].setLibellePiece("Vente Client numereo "+this.getTuppleID());
            compta[i].setRemarque("Vente Client numero "+this.getId());
            compta[i].setCompte(cptClient);
            compta[i].setCompte_aux(this.getIdClient());
            compta[i].setDebit(montantTTC- (fraislivraison* poidsTotal)) ;
        } catch(Exception e){
            throw e;
        } finally {
            if(canClose){
                c.close();
            }
        }
        return compta;
    }



    public VenteDetails[] getDetails(Connection c) throws Exception{           
        VenteDetails[] venteDetails = null;        
        try{
            String awhere = " and IDVENTE = '"+this.getId()+"'";
            venteDetails = (VenteDetails[]) CGenUtil.rechercher(new VenteDetails("VENTE_COMPTA_GEN"), null, null, c, awhere);
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
        return venteDetails;
    }

    public VenteDetails[] getDetailsLib(Connection c) throws Exception{
        VenteDetails[] venteDetails = null;
        try{
            String awhere = " and IDVENTE = '"+this.getId()+"'";
            venteDetails = (VenteDetails[]) CGenUtil.rechercher(new VenteDetails("Vente_Details"), null, null, c, awhere);
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
        return venteDetails;
    }
    
    
    public Encaissement genererEncaissement(){
        Encaissement enc = new Encaissement();
        enc.setIdOrigine(this.getId());
        enc.setDaty(utilitaire.Utilitaire.dateDuJourSql());
        enc.setDesignation("Encaissement vente du "+ this.getDaty()+ " de la facture numéro "+this.getId());
        enc.setIdTypeEncaissement(ConstanteStation.TYPE_ENCAISSEMENT_ENTREE);
        return enc;
    }
    
    public void genererAPartirLivraison(String[]ids, String u, Connection c)throws Exception{
        boolean canClose=false;
        try{
            if(c==null){
                c=new UtilDB().GetConn();
                canClose=true;
            }
        As_BondeLivraisonClient[] bls = As_BondeLivraisonClient.getAll(ids, c);
        As_BondeLivraisonClient.controlerClient(bls);
      
        this.setDesignation("Facturation de Bon de Livraison");
        this.setIdClient(bls[0].getIdclient());
        this.setIdMagasin(bls[0].getMagasin());
        this.setDaty(Utilitaire.dateDuJourSql());
        this.createObject(u, c);
        for(As_BondeLivraisonClient bl : bls){
            bl.setIdvente(this.getId());
            bl.updateToTableWithHisto(u, c);
        }
        As_BondeLivraisonClientFille blf = new As_BondeLivraisonClientFille();
        String[] somGr = {"quantite"};
        String[] gr = {"produit"};
        String[] tabvide = {};
        ResultatEtSomme rs =  CGenUtil.rechercherGroupe(blf, gr, somGr, null, null, " and numbl in "+Utilitaire.tabToString(ids, "'", ","), tabvide, "", c);
        As_BondeLivraisonClientFille[] blfs = (As_BondeLivraisonClientFille[]) rs.getResultat();
        for(As_BondeLivraisonClientFille item : blfs){
            VenteDetails vd = item.toVenteDetails();
            vd.setIdVente(this.getId());
            vd.setIdDevise("AR");
            vd.createObject(u, c);
        }
        } catch(Exception e){
            throw e;
        } finally {
            if(canClose){
                c.close();
            }
        }        
    }

    public static void annulerFacture(String u, Connection c, String idVente) throws SQLException, Exception{
        AvoirFC avoirFC = genererAvoir2(u, c, idVente);

        avoirFC.validerObject(u, c);
        System.out.println("avoir généré");
    }
    
    public static AvoirFC genererAvoir(String u, Connection c, String idVente) throws SQLException, Exception{
        AvoirFC avoirFC = null;
        boolean estOuvert = false;
        if(c==null){
            c = new UtilDB().GetConn();
            estOuvert = true;
            c.setAutoCommit(false);
        }
        try {
            
            Vente vente = Vente.getById(c, idVente);
            vente.getVenteDetailsNonGrp(c);
            
            int tailleFilles = vente.venteDetails.length;
            avoirFC = Vente.transformerFactureToAvoir(vente);
            avoirFC.createObject(u, c);
            AvoirFCFille[] avoirFCFilles = new AvoirFCFille[tailleFilles];
            for(int i = 0; i < tailleFilles; i++){
                avoirFCFilles[i] = Vente.transformerFactureToAvoirFille(vente.venteDetails[i]);
                avoirFCFilles[i].setIdAvoirFC(avoirFC.getId());
                avoirFCFilles[i].createObject(u, c);
            }
            avoirFC.setAvoirDetails(avoirFCFilles);
            c.commit();
        } catch (Exception e) {
            if(estOuvert) c.rollback();
            throw e ;
        } finally {
            if(estOuvert) c.close();
        }
        
        return avoirFC;
    }
    
    public static AvoirFC genererAvoir2(String u, Connection c, String idVente) throws SQLException, Exception{
        AvoirFC avoirFC = null;
        boolean estOuvert = false;
        if(c==null){
            c = new UtilDB().GetConn();
            estOuvert = true;
            c.setAutoCommit(false);
        }
        try {
            Vente vente = Vente.getById(c, idVente);
            vente.getVenteDetailsNonGrp(c);
            AvoirFC[] avoirs = (AvoirFC[]) CGenUtil.rechercher(new AvoirFC(), null, null, c, " and idVente = '"+idVente+"'");
            int tailleFilles = vente.venteDetails.length;
            avoirFC = Vente.transformerFactureToAvoir(vente);
            avoirFC.setIdtypeavoir(ConstanteSocobis.ID_TYPE_AVOIR);
            avoirFC.createObject(u, c);
            AvoirFCFille[] avoirFCFilles = new AvoirFCFille[tailleFilles];
            for(int i = 0; i < tailleFilles; i++){
                avoirFCFilles[i] = vente.transformerFactureToAvoirFille2(vente.venteDetails[i], c, avoirs);
                avoirFCFilles[i].setIdAvoirFC(avoirFC.getId());
                avoirFCFilles[i].createObject(u, c);
            }
            avoirFC.setAvoirDetails(avoirFCFilles);
            c.commit();
        } catch (Exception e) {
            if(estOuvert) c.rollback();
            throw e ;
        } finally {
            if(estOuvert) c.close();
        }
        
        return avoirFC;
    }

    public static AvoirFC transformerFactureToAvoir(Vente vente){
        AvoirFC valeur = new AvoirFC();
        valeur.setDesignation(vente.getDesignation());
        valeur.setIdMagasin(vente.getIdMagasin());
        valeur.setRemarque(vente.getRemarque());
        valeur.setIdOrigine(vente.getIdOrigine());
        valeur.setIdClient(vente.getIdClient());
        valeur.setIdVente(vente.getId());
        valeur.setCompte(vente.getCompte());
	 valeur.setDaty(vente.getDaty());
        valeur.setEtat(1);
        return valeur;
    }
    
    public static AvoirFCFille transformerFactureToAvoirFille(VenteDetails venteDetails) throws Exception{
        AvoirFCFille valeur = new AvoirFCFille();
//        valeur.setIdAvoirFC(venteDetails.get());
        valeur.setIdProduit(venteDetails.getIdProduit());
        //valeur.setIdOrigine(venteDetails.getIdOrigine());
        valeur.setQte(1);
        valeur.setPu(0);
        valeur.setTva(venteDetails.getTva());
        valeur.setPuAchat(0);
        valeur.setPuVente(0);
        valeur.setIdDevise(venteDetails.getIdDevise());
        valeur.setTauxDeChange(1);
        valeur.setDesignation(venteDetails.getIdProduit());
        //valeur.setIdVenteDetails(venteDetails.getId());
        valeur.setEtat(11);
        return valeur;
    }
        public  AvoirFCFille transformerFactureToAvoirFille2(VenteDetails venteDetails, Connection c, AvoirFC[] avoirs) throws Exception{
        AvoirFCFille valeur = new AvoirFCFille();   
        valeur.setIdProduit(venteDetails.getIdProduit());
        double  montantFraisLivraison = this.getFraislivraison() * venteDetails.getPoids() * venteDetails.getQte();
        double montantHT = venteDetails.getPu() * venteDetails.getQte();
        double  montantRemise = montantHT * venteDetails.getRemise() / 100;
        double  htApresRemise = montantHT - montantRemise;
        double  montantRistourne = htApresRemise * venteDetails.getRistourne() / 100;
        double  htFinal = htApresRemise - montantRistourne - montantFraisLivraison ;
        double  puNet = htFinal / venteDetails.getQte();
        valeur.setQte(venteDetails.getQte() );
        valeur.setPu(puNet);
        valeur.setRemise(venteDetails.getRemise());
        valeur.setTva(venteDetails.getTva());
        valeur.setPuAchat(0);
        valeur.setPuVente(0);
        valeur.setIdDevise(venteDetails.getIdDevise());
        valeur.setTauxDeChange(1);
        valeur.setDesignation(venteDetails.getIdProduit());
        //valeur.setIdVenteDetails(venteDetails.getId());
        valeur.setEtat(11);
        return valeur;
    }
    
    public static Vente getById(Connection c, String id) throws Exception{
        Vente vtn = new Vente();        
           try{
            vtn.setId(id);
            vtn = ((Vente[]) CGenUtil.rechercher(vtn, null, null, c, "")).length > 0 ? (Vente)((Vente[]) CGenUtil.rechercher(vtn, null, null, c, ""))[0] : null;
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
        return vtn;
    }
    
    public VenteDetails [] getVenteDetailsNonGrp(Connection c) throws Exception{
        VenteDetails[] venteDetails = null;        
           try{
            String awhere = " and IDVENTE = '"+this.getId()+"'";
            venteDetails = (VenteDetails[]) CGenUtil.rechercher(new VenteDetails(), null, null, c, awhere);
            this.setVenteDetails(venteDetails);
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
        return venteDetails;
    }
    
     public Prevision[] getPrevisions(Connection c) throws Exception{
        Boolean estOuvert = false;
        try{
            if(c==null){
                c = new UtilDB().GetConn();
                estOuvert=true;
            }
            Prevision prevision = new Prevision();
            prevision.setIdFacture(this.getId());
            Prevision[] prev = (Prevision[]) CGenUtil.rechercher(prevision, null, null, c, " ");
            return prev;
        } catch (Exception e) {
            throw e;
        } finally {
            if(estOuvert)c.close();
        }     
    }
    public Vente[] controllerVentePaiementMultiple(String[] ids)throws Exception{
        String aWhere=Utilitaire.getAWhereIn(ids,"id");
        Vente[] ventes = (Vente[]) CGenUtil.rechercher(new Vente(), null, null, null, aWhere);
        String firstClientId = ventes[0].getIdClient();
        for (int i = 0; i < ventes.length; i++) {
            if (!ventes[i].getIdClient().equalsIgnoreCase(firstClientId))throw new Exception("Clients differents!");
            if (ventes[i].getEtat() < 11)throw new Exception("Presence de facture non visee!");
        }
        return ventes;
    }

     public String getDesignation() {
         return designation;
     }

     public void setDesignation(String designation) {
         this.designation = designation;
     }


    public static VenteLib genererVenteClient(String [] ids, Connection c) throws Exception{
        boolean estOuvert = false;
        try{
            if(c==null){
                c = new UtilDB().GetConn();
                estOuvert=true;
            }
            VenteLib[] liste =(VenteLib[])CGenUtil.rechercher(new VenteLib(), null, null,c," AND ID IN ("+ Utilitaire.tabToString(ids, "'", ",")+")");
            if(liste.length<=0){
                return null;
            }
            String idClient = liste[0].getIdClient();
            double montant = liste[0].getMontantreste();
            for (int i = 1; i < liste.length; i++) {
                if(liste[i].getIdClient().compareToIgnoreCase(idClient)!=0){
                    throw new Exception("Client different pour ces ventes");
                }
                montant += liste[i].getMontantreste();
            }
            liste[0].setMontantreste(montant);
            return liste[0];
        } catch (Exception e) {
            throw e;
        } finally {
            if(estOuvert)c.close();
        }
    }
    public static LiaisonPaiement genererPaiementFactureParAvoir(String u, Connection c, String idVente) throws SQLException, Exception{
        LiaisonPaiement pFacture = null;
        boolean estOuvert = false;
        if(c==null){
            c = new UtilDB().GetConn();
            estOuvert = true;
            c.setAutoCommit(false);
        }
        try {
            
            Vente vente = Vente.getById(c, idVente);
            AvoirFCLib t = new AvoirFCLib();
            t.setNomTable("AVOIRFCLIB_CPL");
            AvoirFCLib[] details = (AvoirFCLib[]) CGenUtil.rechercher(t,null,null,c," AND idClient='"+vente.getIdClient()+"' order by id asc");
            StringBuilder sb = new StringBuilder();
            if(details.length>0){
                for(int i=0;i<details.length;i++){
                    if (i > 0) sb.append(";"); 
                    sb.append(details[i].getId());
                }
            }
            double montantTotalReste = AdminGen.calculSommeDouble(details,"resteapayerar");
            pFacture = new LiaisonPaiement();
            pFacture.setId2(idVente);
            //pFacture.setId1(sb.toString());
            //pFacture.setDaty(vente.getDaty());
            pFacture.setMontant(montantTotalReste);
            c.commit();
        } catch (Exception e) {
            if(estOuvert) c.rollback();
            throw e ;
        } finally {
            if(estOuvert) c.close();
        }
        
        return pFacture;
    }

    public double getEcheancefacture() {
        return echeancefacture;
    }

    public void setEcheancefacture(double echeancefacture) {
        this.echeancefacture = echeancefacture;
    }
    public static MvtCaisse genererPaiementFacture(String[] ids,Connection c) throws Exception{
        System.out.println("ids = "+ids.length);
        String requete = null;
        boolean ifconnnull = false;
        MvtCaisse mvt = new MvtCaisse();
        try {
            if (c == null) {
                c = new UtilDB().GetConn();
                ifconnnull = true;
                VenteLib ff = new VenteLib();
                ff.setNomTable("VENTE_CPL");
                requete = "SELECT * FROM " + ff.getNomTable() + " WHERE id IN (";
                requete += Utilitaire.tabToString(ids, "'", ",") + ")";
                System.out.println(requete);
                VenteLib[] liste = (VenteLib[]) CGenUtil.rechercher(ff, requete, c);
                Set<String> clients = new HashSet<>();
                for (VenteLib v : liste) {
                    clients.add(v.getIdClient());
                }
                if (clients.size() > 1) {
                    throw new Exception("Client different pour ces ventes !");
                }

                double montantTotal = AdminGen.calculSommeDouble(liste,"montantreste");
                mvt.setIdOrigine(String.join(";;", ids));
                mvt.setCredit(montantTotal);
                mvt.setIdDevise("Ar");
                mvt.setIdTiers(liste[0].getIdClient());
                mvt.setDesignation("Paiement facture  par "+liste[0].getIdClientLib());
            }
        } catch (Exception x) {
            x.printStackTrace();
            throw x;
        } finally {
            if (ifconnnull && c != null) {
                c.close();
            }
        }
        return mvt;
    }

    public Vente reglerReleverCredit(String u, Connection c, String[] ids) throws Exception{
        boolean canClose=false;
        try {
            if (c == null) {
                c = new UtilDB().GetConn();
                canClose = true;
                c.setAutoCommit(false);
            }
            Vente facture = (Vente) super.createObject(u,c);
            VenteDetails[] filles = (VenteDetails[]) facture.getFille();
            double montant = 0;
            for (VenteDetails fille : filles ) {
                //fille.createObject(u,c);
                fille.validerObject(u,c);
                montant += fille.getPu() * fille.getQte();
            }
            facture.validerObject(u,c);

            MvtCaisse mvt = new MvtCaisse();
            mvt.setIdCaisse(facture.getReferencefact());
            mvt.setDesignation("Paiement de la facture de règlement : "+facture.getId());
            mvt.setIdTiers(facture.getIdClient());
            mvt.setDaty(facture.getDaty());
            mvt.setIdOrigine(facture.getId());
            mvt.setCredit(montant);
            mvt.setDebit(0);
            MvtCaisse val = (MvtCaisse) mvt.createObject(u,c);
            mvt.validerObject(u,c);

            ComptaEcriture ce = new ComptaEcriture();
            ce.setIdobjet(val.getId());
            ComptaEcriture[] ecriture = (ComptaEcriture[]) CGenUtil.rechercher(ce,null,null,c,"");
            System.err.println("==============================================IDS"+ids.length);
            System.err.println("==============================================ecriture"+ecriture.length);
            if (ecriture.length > 0) {
                ecriture[0].validerObject(u,c);
                ComptaSousEcriture sousEcriture = new ComptaSousEcriture();
                ComptaSousEcriture[] sousEcritures = (ComptaSousEcriture[]) CGenUtil.rechercher(sousEcriture,null,null,c," AND IDMERE='"+ecriture[0].getId()+"' AND COMPTE like '5%'");
                String[] idEcritures = new String[1];
                //for (int i = 0; i < sousEcritures.length; i++) {
                idEcritures[0] = sousEcritures[0].getId();
                sousEcritures[0].validerObject(u,c);
                //}
                RapprochementDBMere rp = new RapprochementDBMere().rapprochement(u,idEcritures,ids,c);
                rp.validerObject(u,c);
            }
            c.commit();
            return facture;
        }catch (Exception e){
            if(canClose) c.rollback();
            throw e;
        } finally {
            if(canClose) c.close();
        }
    }

    public Prevision[] genererPrevisionPrevu(String u, Connection c) throws Exception {
        VenteDetails[] details = (VenteDetails[]) getFille("VENTE_DETAILS",c,"");
        Prevision[] previsions = new Prevision[details.length];
        for (int i = 0; i < details.length; i++) {
            previsions[i] = details[i].genererPrevisionPrevu(u, c);
        }
        return  previsions;
    }
    public Prevision[] genererPrevisionPrevuBatch(String u, Statement st) throws Exception {
        VenteDetails[] details = (VenteDetails[]) getFille("VENTE_DETAILS",st.getConnection(),"");
        if (details == null || details.length == 0) {
            details = (VenteDetails[]) this.getFille();
        }
        if (details == null) return new Prevision[0];
        Prevision[] previsions = new Prevision[details.length];
        for (int i = 0; i < details.length; i++) {
            previsions[i] = details[i].genererPrevisionPrevuBatch(u, st);
        }
        return  previsions;
    }
    public MouvementCaisseFille creerAPpoint(double montant,String idCaisse) throws Exception {
        if(this.getIsAppoint()==false)return null;
        MouvementCaisseFille mvtAppoint = null;
        if(Math.abs(montant) >= 0.01) {
            mvtAppoint=new MouvementCaisseFille();
            mvtAppoint.setDaty(Utilitaire.dateDuJourSql());
            mvtAppoint.setDesignation("Appoint sur la vente " + this.getId());
            mvtAppoint.setIdDevise("AR");
            mvtAppoint.setTaux(1);
            mvtAppoint.setIdCassier(this.getUserFacture());
            mvtAppoint.setReference(this.getId());
            mvtAppoint.setIdCaisse(idCaisse);
            mvtAppoint.setIdModePaiement(ConstanteAsync.PAIEMENT_ESPECE);
            if (montant > 0) mvtAppoint.setCredit(+montant);
            else mvtAppoint.setDebit(-montant);
        }
        return mvtAppoint;
    }

    protected MvtStock createMvtStockVenteDirecte() throws Exception {
        MvtStock md = new MvtStock();
        md.setDaty(this.getDaty());
        md.setDesignation("Vente directe Num : " + this.getId() );
        md.setIdTypeMvStock(ConstanteStation.TYPEMVTSTOCKSORTIE);
        md.setIdMagasin(this.getIdMagasin());
        md.setIdVente(this.getId());
        md.setIdobjet(this.getId());
        return md;
    }

    public static String enregistrerViserLivrer(UserEJB u, PageInsertMultiple p, String nomtable, String colonneMere, String modePaiement, String reference)throws Exception{
        Connection c = null;
        try {

            c = new UtilDB().GetConn();
            c.setAutoCommit(false);
            ClassMAPTable cmere = p.getObjectAvecValeur();
            VenteDetails[] cfille = (VenteDetails[]) p.getObjectFilleAvecValeur();
//            System.out.println("cfilleee " + cfille.length);
            for (int i = 0; i < cfille.length; i++) {
                cfille[i].setNomTable(nomtable);
//                cfille[i].setCompte("710011");
//                System.out.println("====> Compte POS" + cfille[i].getCompte());
            }
            Vente mere = ((Vente)cmere);
            if(mere.getIdMagasin()==null || mere.getIdMagasin().compareToIgnoreCase("")==0) {
                mere.setIdMagasin(ConstanteAsync.MAGASIN);
            }
            mere.setDatyPrevu(Utilitaire.dateDuJourSql());
            mere.setDaty(Utilitaire.dateDuJourSql());

            if (modePaiement==null || modePaiement.compareToIgnoreCase("")==0 || modePaiement.compareToIgnoreCase("especes") == 0) {
                mere.setIdModePaiement(ConstanteAsync.PAIEMENT_ESPECE);
            } else if(modePaiement.compareToIgnoreCase("virement")==0) {
                mere.setIdModePaiement(ConstanteAsync.PAIEMENT_CARTE);
            } else if(modePaiement.compareToIgnoreCase("mvola")==0) {
                mere.setIdModePaiement(ConstanteAsync.PAIEMENT_MOBILEMONEY);
            } else if(modePaiement.compareToIgnoreCase("orange")==0) {
                mere.setIdModePaiement(ConstanteAsync.PAIEMENT_MOBILEMONEY);
            } else if(modePaiement.compareToIgnoreCase("airtel")==0) {
                mere.setIdModePaiement(ConstanteAsync.PAIEMENT_MOBILEMONEY);
            } else if(modePaiement.compareToIgnoreCase("cheque")==0) {
                mere.setIdModePaiement(ConstanteAsync.PAIEMENT_CHEQUE);
            }
//            mere.createObject(""+u.getUser().getRefuser(),c);
//            u.createObjectMultiple(cfille,colonneMere,mere.getTuppleID(),c);
            mere.setFille(cfille);
            mere.createObject(""+u.getUser().getRefuser(),c);
            mere.validerObject(""+u.getUser().getRefuser(), c);
            String idBL = mere.genererBonLivraisonAvecMvtStock(""+u.getUser().getRefuser(),c);

            VenteDetails[] listeFille=(VenteDetails[]) mere.getFille(null,c,"");
            MvtStock mvtStockMere = mere.createMvtStockVenteDirecte();
            List<MvtStockFille> mvtStockFilles = new ArrayList<>();

            for(int i=0;i<listeFille.length;i++){
                Ingredients ing = listeFille[i].getIngredientById();
                if (ing.getCompose() == 1) {
                    RecetteLib[] listeComposition = ing.getRecette(null, c);
                    for (RecetteLib recetteLib : listeComposition) {
                        MvtStockFille mvtStockFille = new MvtStockFille();
                        mvtStockFille.setIdProduit(recetteLib.getIdingredients());
                        double qteIngredient = recetteLib.getQuantite() * listeFille[i].getQte();
                        mvtStockFille.setSortie(qteIngredient);
                        mvtStockFille.setIdVenteDetail(listeFille[i].getId());
                        mvtStockFille.setPu(recetteLib.getPu());
                        mvtStockFille.setMontant(mvtStockFille.getSortie() * recetteLib.getPu());
                        mvtStockFille.setDesignation("Sortie par vente directe de " + ing.getLibelle() + " - Composition de " + recetteLib.getLibelleingredient());
                        mvtStockFilles.add(mvtStockFille);
                    }

                }

                listeFille[i].calculerRevient(c);
                listeFille[i].updateToTableWithHisto(""+u.getUser().getRefuser(),c);
            }
            mvtStockMere.setFille(mvtStockFilles.toArray(new MvtStockFille[]{}));

            mvtStockMere.setEtat(ConstanteEtat.getEtatValider());
            mvtStockMere.createObject(String.valueOf(u.getUser().getRefuser()), c);

            // mere.genererEncaissementVente(modePaiement, reference, u.getUser().getTuppleID(), c);
            c.commit();
            try {
                ExportPDFDirect.ficheVente(mere.getId());
            } catch (Exception printEx) {
                printEx.printStackTrace();
            }
            return mere.getId();
        } catch (Exception e) {
            e.printStackTrace();
            c.rollback();
            throw e;
        }finally {
            if (c != null) {
                c.close();
            }
        }
    }
    public double getMontantTTCCalc()throws Exception
    {
        return AdminGen.calculSommeDouble(this.getFille(), "montantTTCCalc");
    }
    public double getAppoint() throws Exception
    {
        double montantTTC = getMontantTTCCalc();
        double montantTTCPAr1000 = montantTTC / 1000;
        montantTTCPAr1000 = Utilitaire.arrondir(montantTTCPAr1000, 1);
//            System.out.println(" montantTTC " + montantTTC + " montantTTCPAr1000 1 " + montantTTCPAr1000);
        montantTTCPAr1000 = montantTTCPAr1000 * 1000;
        double appoint = montantTTCPAr1000 - montantTTC;
        return appoint;
    }
    public double getAppoint(double montantPaye) throws Exception
    {
        double montantTTC = getMontantTTCCalc();
        double diff = Utilitaire.arrondir(montantPaye - montantTTC, 2);
        if (Math.abs(diff) < 0.01) return 0.0;
        return diff;
    }
    public static synchronized String enregistrerViserLivrerEncaisser(UserEJB u, PageInsertMultiple p, String nomtable, String colonneMere, String modePaiement, String reference, String[] estEnGrosListe, String aretourner, double montantPaye)throws Exception{
        Connection c = null;
        try {
            System.out.println("NIDITRA FONCTION "+Utilitaire.heureCouranteHMS());
            c = new UtilDB().GetConn();
            c.setAutoCommit(false);
            ClassMAPTable cmere = p.getObjectAvecValeur();
            VenteDetails[] cfille = (VenteDetails[]) p.getObjectFilleAvecValeur();
            for (int i = 0; i < cfille.length; i++) {
//                System.out.println("cfilleee " + i);
                cfille[i].setNomTable(nomtable);
//                cfille[i].setCompte("710011");
//                System.out.println("====> Compte POS" + cfille[i].getCompte());
            }
            Vente mere = ((Vente)cmere);

            if(mere.getIdMagasin()==null || mere.getIdMagasin().compareToIgnoreCase("")==0) {
                mere.setIdMagasin(u.getMagasinEncours().getId());
            }
            mere.setDatyPrevu(Utilitaire.dateDuJourSql());
            mere.setDaty(Utilitaire.dateDuJourSql());
            mere.setMontantDonne(montantPaye);
            if (modePaiement==null || modePaiement.compareToIgnoreCase("")==0 || modePaiement.compareToIgnoreCase("especes") == 0) {
                mere.setIdModePaiement(ConstanteAsync.PAIEMENT_ESPECE);
            } else if(modePaiement.compareToIgnoreCase("virement")==0) {
                mere.setIdModePaiement(ConstanteAsync.PAIEMENT_CARTE);
            } else if(modePaiement.compareToIgnoreCase("mvola")==0) {
                mere.setIdModePaiement(ConstanteAsync.PAIEMENT_MOBILEMONEY);
            } else if(modePaiement.compareToIgnoreCase("orange")==0) {
                mere.setIdModePaiement(ConstanteAsync.PAIEMENT_MOBILEMONEY);
            } else if(modePaiement.compareToIgnoreCase("airtel")==0) {
                mere.setIdModePaiement(ConstanteAsync.PAIEMENT_MOBILEMONEY);
            } else if(modePaiement.compareToIgnoreCase("cheque")==0) {
                mere.setIdModePaiement(ConstanteAsync.PAIEMENT_CHEQUE);
            } else if(modePaiement.compareToIgnoreCase("bonAchat")==0) {
                mere.setIdModePaiement(ConstanteAsync.PAIEMENT_BONACHAT);
            } else if(modePaiement.compareToIgnoreCase("cadeaux")==0) {
                mere.setIdModePaiement(ConstanteAsync.PAIEMENT_CARTECADEUAX);
            }

            System.out.println(" mode paiement mere ++++++++ " + mere.getIdModePaiement());

//            mere.createObject(""+u.getUser().getRefuser(),c);
            mere.setFille(cfille);
            Ingredients[] listeIng=mere.getListeIngredients(null,c);
            System.out.println("AVANT BOUCLE "+Utilitaire.heureCouranteHMS());
            int j = 0;
            for (j=0; j < cfille.length; j++) {
                Ingredients ing = (Ingredients) AdminGen.findById(listeIng,cfille[j].getIdProduit());
                cfille[j].setPu(ing.getPv());
                if ((estEnGrosListe[j]!=null || estEnGrosListe[j]==null) && ing.getUnite().compareToIgnoreCase("UNT00004") == 0 && !estEnGrosListe[j].equals("1")) {
                    double qteModifie = cfille[j].getQte() / 1000;
                    cfille[j].setQte(qteModifie);
                    cfille[j].setPu(ing.getPv());
                } else if (estEnGrosListe[j]!=null && ing.getUnite().compareToIgnoreCase("UNT00004") == 0 && estEnGrosListe[j].equals("1")) {
                    double qteModifie = cfille[j].getQte() / 1000;
                    cfille[j].setQte(qteModifie);
                    cfille[j].setPu(ing.getPvgros());

                }
            }
            double appoint=mere.getAppoint();
            if(appoint!=0)
            {
                //VenteDetails app=mere.creerAPpoint();
                //mere.ajouterFille(app);
            }
            mere.createObject(u.getUser().getTuppleID(),c);


            /*double montantTTC = AdminGen.calculSommeDouble(cfille, "montantTTCCalc");
//            System.out.println(" montantTTC " + montantTTC);
            double montantTTCPAr1000 = montantTTC / 1000;
            montantTTCPAr1000 = Utilitaire.arrondir(montantTTCPAr1000, 1);
//            System.out.println(" montantTTC " + montantTTC + " montantTTCPAr1000 1 " + montantTTCPAr1000);
            montantTTCPAr1000 = montantTTCPAr1000 * 1000;
            double appoint = montantTTCPAr1000 - montantTTC;
//            u.createObjectMultiple(cfille,colonneMere,mere.getTuppleID(),c);

            mere.createObject(""+u.getUser().getRefuser(),c);

            if (appoint != 0) mere.creerAPpoint(appoint).createObject(""+u.getUser().getRefuser(),c);*/
            /*double appoint=mere.getAppoint();
            if(appoint!=0)  mere.creerAPpoint().createObject(u.getUser(),c);*/



            mere.setEtat(11);
            mere.updateToTableWithHisto(""+u.getUser().getRefuser(),c);


            VenteDetails[] listeFille=(VenteDetails[]) mere.getFille();
            MvtStock mvtStockMere = mere.createMvtStockVenteDirecte();
            List<MvtStockFille> mvtStockFilles = new ArrayList<>();
            System.out.println("AVANT BOUCLE 2 "+Utilitaire.heureCouranteHMS());
            for(int i=0;i<listeFille.length;i++){
                //Ingredients ing = listeFille[i].getIngredientById();
                Ingredients ing=(Ingredients) AdminGen.findById(listeIng,listeFille[i].getIdProduit());
                if (ing.getCompose() == 1) {
                    RecetteLib[] listeComposition = ing.getRecette(null, c);
                    for (RecetteLib recetteLib : listeComposition) {
                        MvtStockFille mvtStockFille = new MvtStockFille();
                        mvtStockFille.setIdProduit(recetteLib.getIdingredients());
                        double qteIngredient = recetteLib.getQuantite() * listeFille[i].getQte();
                        mvtStockFille.setSortie(qteIngredient);
                        mvtStockFille.setIdVenteDetail(listeFille[i].getId());
                        mvtStockFille.setPu(recetteLib.getPu());
                        mvtStockFille.setMontant(mvtStockFille.getSortie() * recetteLib.getPu());
                        mvtStockFille.setDesignation("Sortie par vente directe de " + ing.getLibelle() + " - Composition de " + recetteLib.getLibelleingredient());
                        mvtStockFilles.add(mvtStockFille);
                    }

                } else {
                    MvtStockFille mvtStockFille = new MvtStockFille();
                    mvtStockFille.setIdProduit(listeFille[i].getIdProduit());
                    double qteIngredient = listeFille[i].getQte();
                    mvtStockFille.setSortie(qteIngredient);
                    mvtStockFille.setIdVenteDetail(listeFille[i].getId());
                    mvtStockFille.setPu(ing.getPv());
                    mvtStockFille.setMontant(mvtStockFille.getSortie() * ing.getPv());
                    mvtStockFille.setDesignation("Sortie par vente directe de " + ing.getLibelle() + " - Composition de " + ing.getLibelle());
                    mvtStockFilles.add(mvtStockFille);
                }

                listeFille[i].calculerRevient(c);
                listeFille[i].updateToTableWithHisto(""+u.getUser().getRefuser(),c);
                System.out.println("APRES CALUCLER REVIENT "+Utilitaire.heureCouranteHMS());
            }
            mvtStockMere.setFille(mvtStockFilles.toArray(new MvtStockFille[]{}));

            mvtStockMere.setEtat(ConstanteEtat.getEtatValider());
            System.out.println("AVANT creation Mvt stock "+Utilitaire.heureCouranteHMS());
            mvtStockMere.createObject(String.valueOf(u.getUser().getRefuser()), c);
            System.out.println("APRES creation Mvt stock "+Utilitaire.heureCouranteHMS());
            mere.genererEcriture(""+u.getUser().getRefuser(), c);
            System.out.println("APRES GENERATION ECRITURE "+Utilitaire.heureCouranteHMS());
//            mere.validerObject(""+u.getUser().getRefuser(),c);
            String idBL = mere.genererBonLivraisonAvecMvtStockTotal(""+u.getUser().getRefuser(),c);
            System.out.println("AVANT A RETOURNER "+Utilitaire.heureCouranteHMS());
            // mere.genererEncaissementVente(modePaiement, reference, u, c, estEnGrosListe, aretourner);
            double aRetournerCalcule = 0;
            if (mere.getIdModePaiement().equals(ConstanteAsync.PAIEMENT_ESPECE) && montantPaye > mere.getMontantTTCCalc()) {
                aRetournerCalcule = montantPaye - mere.getMontantTTCCalcul();
            }

            mere.genererEncaissementVente(modePaiement, reference, u, c, estEnGrosListe, String.valueOf(aRetournerCalcule), montantPaye);
            System.out.println("APRES ENCAISSEMENT "+Utilitaire.heureCouranteHMS());
            c.commit();
//            try {
//                ExportPDFDirect.ficheVente(mere.getId());
//            } catch (Exception printEx) {
//                printEx.printStackTrace();
//            }
            return mere.getId();
        } catch (Exception e) {
            e.printStackTrace();
            c.rollback();
            throw e;
        }finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public void genererEncaissementVente(String modePaiement,String reference, UserEJB u, Connection c, String[] estEnGrosListe, String aretourner, double montantPaye) throws Exception {
        try {
            if(c == null) {
                c = new UtilDB().GetConn();
            }
            MvtCaisse mvt = new MvtCaisse();
            VenteDetails venteDetail = new VenteDetails();
            venteDetail.setIdVente(this.getId());
            VenteDetails[] res = (VenteDetails[]) this.getFille();
            double montantTotal = 0;
//            if (res != null && res.length > 0) {
//                for (int i = 0; i < res.length; i++) {
//                    Ingredients ing = new Ingredients();
//                    ing.setId(res[i].getIdProduit());
//                    Ingredients[] ings = (Ingredients[]) CGenUtil.rechercher(ing, null, null, c, "");
//                    System.out.println(" ing " + ings.length + ings[0].getId() + " i = " + i );
//                    double prixUnitaire = 0;
//                    prixUnitaire = ings[0].getPv();;
//                    System.out.println("inggg " + ings[0] + estEnGrosListe[i]);
//                    if ( ings[0].getId().compareToIgnoreCase("APP")!=0&& ings[0].getUnite()!= null && ings[0].getUnite().compareToIgnoreCase("UNT00004") == 0 && (estEnGrosListe == null || i >= estEnGrosListe.length )) {
//                        prixUnitaire = ings[0].getPv();
//                    } else if( ings[0].getId().compareToIgnoreCase("APP")!=0&&  ings[0].getUnite()!= null && ings[0].getUnite().compareToIgnoreCase("UNT00004")==0 && (estEnGrosListe != null || estEnGrosListe.length>0 || estEnGrosListe[i].equals("1"))){
//                        prixUnitaire = ings[0].getPvgros();
//                    }
//                    prixUnitaire = Math.round(prixUnitaire / 10.0) * 10.0;
//
//                    System.out.println(" pv " + ings[0].getPv() + " pvgros " + ings[0].getPvgros());
//                    double montant = (prixUnitaire * res[i].getQte()) - res[i].getRemiseMontant();
//                    montantTotal += montant;
//                }
//            }
//            montantTotal = AdminGen.calculSommeDouble(res, "montantTTCCalc");
            montantTotal = montantPaye;

            String idCaisse = "";
            /*Caisse caisse =  new Caisse();
            Caisse crt=new Caisse();
            crt.setIdPoint(u.getPointEncours().getId());
            crt.setIdTypeCaisse(ConstanteAsync.caisseTypeEspece);
            Caisse[] listeCaisse=(Caisse[]) CGenUtil.rechercher(crt, null, null, c, " ");*/
            Caisse[] listeCaisse=u.getListeCaissePoint();


            String idPoint = u.getPointEncours().getId();
            if (modePaiement==null || modePaiement.compareToIgnoreCase("")==0 || modePaiement.compareToIgnoreCase("especes") == 0) {
                idCaisse = u.getCaisseEspeceDefaut().getId();
            } else if(modePaiement.compareToIgnoreCase("virement")==0 || modePaiement.compareToIgnoreCase("visa")==0) {
                //idCaisse = Caisse.getCaisseParCat(u.getListeCaissePoint(),"vir");
            } else if(modePaiement.compareToIgnoreCase("mvola")==0) {
                idCaisse = Caisse.getCaisseParCat(u.getListeCaissePoint(),ConstanteAsync.caisseCategMvola).getId();
            } else if(modePaiement.compareToIgnoreCase("orange")==0) {
                idCaisse = Caisse.getCaisseParCat(u.getListeCaissePoint(),ConstanteAsync.caisseCategOrange).getId();
            } else if(modePaiement.compareToIgnoreCase("airtel")==0) {
                idCaisse = Caisse.getCaisseParCat(u.getListeCaissePoint(),ConstanteAsync.caisseCategAirtel).getId();
            } else if(modePaiement.compareToIgnoreCase("cheque")==0) {
                idCaisse = Caisse.getCaisseParCat(u.getListeCaissePoint(),ConstanteAsync.caisseCategCheque).getId();
            } else if(modePaiement.compareToIgnoreCase("bonAchat")==0) {
                idCaisse = Caisse.getCaisseParCat(u.getListeCaissePoint(),ConstanteAsync.caisseBonAchat).getId();
            } else if(modePaiement.compareToIgnoreCase("cadeaux")==0) {
                idCaisse = Caisse.getCaisseParCat(u.getListeCaissePoint(),ConstanteAsync.caisseCadeaux).getId();
            }

            if(reference!=null && reference.compareToIgnoreCase("")!=0) {
                mvt.setReference(reference);
            }
            mvt.setIdCaisse(idCaisse);
            mvt.setDaty(this.getDaty());
            mvt.setCredit(montantTotal);
            mvt.setIdOrigine(this.getId());
            mvt.setDesignation("Encaissement de la facture Num : " + this.getId());
            mvt.setTaux(1);
            mvt.setIdDevise("AR");
            mvt.setIdTiers(this.getIdClient());
            mvt.setMontantRetourner(Double.parseDouble(aretourner));
            mvt.setCompte(ConstanteAsync.COMPTE_REGROUPEMENT);
            mvt.setIdCaissier(u.getUser().getTuppleID());

            mvt.createObject(u.getUser().getTuppleID(), c);
            mvt.validerObjectSimple(u.getUser().getTuppleID(), c);

            LiaisonPaiement lp = new LiaisonPaiement();
            lp.setId1(mvt.getId());
            lp.setId2(this.getId());
            lp.setMontant(montantTotal);
            lp.createObject(u.getUser().getTuppleID(), c);
            lp.validerObject(u.getUser().getTuppleID(), c);


        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }



    public static String getCaisseMvola(Connection c, String idPoint)throws Exception {
        Caisse crt=new Caisse();
        crt.setIdPoint(idPoint);
        crt.setIdCategorieCaisse(ConstanteAsync.caisseCategMvola);
        Caisse[] listeCaisse=(Caisse[]) CGenUtil.rechercher(crt, null, null, c, " ");

        if(listeCaisse!=null&&listeCaisse.length>0){
            return listeCaisse[0].getId();
        }
        else{
            return ConstanteAsync.CAISSE_MVOLA;
        }
    }

    public static String getCaisseOrange(Connection c, String idPoint)throws Exception {
        Caisse crt=new Caisse();
        crt.setIdPoint(idPoint);
        crt.setIdCategorieCaisse(ConstanteAsync.caisseCategOrange);
        Caisse[] listeCaisse=(Caisse[]) CGenUtil.rechercher(crt, null, null, c, " ");

        if(listeCaisse!=null&&listeCaisse.length>0){
            return listeCaisse[0].getId();
        }
        else{
            return ConstanteAsync.CAISSE_ORANGE;
        }
    }

    public static String getCaisseAirtel(Connection c, String idPoint)throws Exception {
        Caisse crt=new Caisse();
        crt.setIdPoint(idPoint);
        crt.setIdCategorieCaisse(ConstanteAsync.caisseCategAirtel);
        Caisse[] listeCaisse=(Caisse[]) CGenUtil.rechercher(crt, null, null, c, " ");

        if(listeCaisse!=null&&listeCaisse.length>0){
            return listeCaisse[0].getId();
        }
        else{
            return ConstanteAsync.CAISSE_AIRTEL;
        }
    }

    public static String getCaisseTpe(Connection c, String idPoint)throws Exception {
        Caisse crt=new Caisse();
        crt.setIdPoint(idPoint);
        crt.setIdCategorieCaisse(ConstanteAsync.caisseCategTpe);
        Caisse[] listeCaisse=(Caisse[]) CGenUtil.rechercher(crt, null, null, c, " ");

        if(listeCaisse!=null&&listeCaisse.length>0){
            return listeCaisse[0].getId();
        }
        else{
            return ConstanteAsync.CAISSE_TPE;
        }
    }

    public static String getCaisseCheque(Connection c, String idPoint)throws Exception {
        Caisse crt=new Caisse();
        crt.setIdPoint(idPoint);
        crt.setIdCategorieCaisse(ConstanteAsync.caisseCategCheque);
        Caisse[] listeCaisse=(Caisse[]) CGenUtil.rechercher(crt, null, null, c, " ");

        if(listeCaisse!=null&&listeCaisse.length>0){
            return listeCaisse[0].getId();
        }
        else{
            return ConstanteAsync.CAISSE_CHEQUE;
        }
    }

    public static String getCaisseBonAchat(Connection c, String idPoint)throws Exception {
        Caisse crt=new Caisse();
        crt.setIdPoint(idPoint);
        crt.setIdCategorieCaisse(ConstanteAsync.caisseBonAchat);
        Caisse[] listeCaisse=(Caisse[]) CGenUtil.rechercher(crt, null, null, c, " ");

        if(listeCaisse!=null&&listeCaisse.length>0){
            return listeCaisse[0].getId();
        }
        else{
            return ConstanteAsync.CAISSE_BONACHAT;
        }
    }


    public static String getCaisseCadeau(Connection c, String idPoint)throws Exception {
        Caisse crt=new Caisse();
        crt.setIdPoint(idPoint);
        crt.setIdCategorieCaisse(ConstanteAsync.caisseCadeaux);
        Caisse[] listeCaisse=(Caisse[]) CGenUtil.rechercher(crt, null, null, c, " ");

        if(listeCaisse!=null&&listeCaisse.length>0){
            return listeCaisse[0].getId();
        }
        else{
            return ConstanteAsync.CAISSE_CADEAUX;
        }
    }

    public static double arrondir(double nombre) {
        return Math.round(nombre / 100.0) * 100.0;
    }

    public String genererBonLivraisonAvecMvtStockTotal(String u,Connection c) throws Exception{
        try{
            VenteDetailsLib vLib = new VenteDetailsLib();
            vLib.setNomTable("VENTE_DETAILS_RESTE");
            //VenteDetailsLib[] details = (VenteDetailsLib[]) CGenUtil.rechercher(vLib,null,null,c," AND idVente='"+this.getId()+"' AND reste > 0");
            VenteDetails[] details=(VenteDetails[]) this.getFille();
            if(details.length > 0){
                As_BondeLivraisonClient client = new As_BondeLivraisonClient();
                client.setMode("modif");
                client.setIdvente(this.getId());
                client.setMagasin(this.getIdMagasin());
                client.setEtat(1);
                client.setIdclient(this.getIdClient());
                client.setRemarque("POS Livraison de la facture numero "+this.getId());
                client.setDaty(Utilitaire.dateDuJourSql());
//                client.createObjectSimple(u, c);

                As_BondeLivraisonClient createdClient = (As_BondeLivraisonClient) client.createObjectSimple(u, c);
                createdClient.setEtat(11);
                createdClient.updateToTableWithHisto(u,c);
                As_BondeLivraisonClientFille[] clientFille=new As_BondeLivraisonClientFille[details.length];
                for(int ii=0;ii<details.length;ii++){
                    clientFille[ii] = new As_BondeLivraisonClientFille();
                    clientFille[ii].setMode("modif");
                    clientFille[ii].setProduit(details[ii].getIdProduit());
                    clientFille[ii].setUnite(details[ii].getUnite());
                    clientFille[ii].setQuantite(details[ii].getQte());
                    clientFille[ii].setIdventedetail(details[ii].getId());
                    clientFille[ii].setNumbl(createdClient.getId());
                    clientFille[ii].createObject(u, c);
                }
                createdClient.setFille(clientFille);
                MvtStock mvt = createdClient.genererMvtStock();
                mvt.createObject(u,c);
                mvt.validerObject(u,c);
                return createdClient.getId();
            }
            throw new Exception("Plus aucun article à livrer");
        }
        catch(Exception e){
            throw e;
        }
    }

    public String genererBonLivraisonAvecMvtStock(String u,Connection c) throws Exception{
        try{
            VenteDetailsLib vLib = new VenteDetailsLib();
            vLib.setNomTable("VENTE_DETAILS_RESTE");
            VenteDetailsLib[] details = (VenteDetailsLib[]) CGenUtil.rechercher(vLib,null,null,c," AND idVente='"+this.getId()+"' AND reste > 0");

            if(details.length > 0){
                As_BondeLivraisonClient client = new As_BondeLivraisonClient();
                client.setMode("modif");
                client.setIdvente(this.getId());
                client.setMagasin(this.getIdMagasin());
                client.setEtat(1);
                client.setIdclient(this.getIdClient());
                client.setRemarque("POS Livraison de la facture numero "+this.getId());
                client.setDaty(Utilitaire.dateDuJourSql());
//                client.createObjectSimple(u, c);

                As_BondeLivraisonClient createdClient = (As_BondeLivraisonClient) client.createObjectSimple(u, c);
                createdClient.setEtat(11);
                createdClient.updateToTableWithHisto(u,c);
                for(VenteDetailsLib detail:details){
                    As_BondeLivraisonClientFille clientFille = new As_BondeLivraisonClientFille();
                    clientFille.setMode("modif");
                    clientFille.setProduit(detail.getIdProduit());
                    clientFille.setUnite(detail.getIdUnite());
                    clientFille.setQuantite(detail.getReste());
                    clientFille.setIdventedetail(detail.getId());
                    clientFille.setNumbl(createdClient.getId());
                    clientFille.createObject(u, c);
                }
                MvtStock mvt = createdClient.genererMvtStock(c);
                mvt = (MvtStock) mvt.createObject(u,c);
                mvt.validerObject(u,c);
                return createdClient.getId();
            }
            throw new Exception("Plus aucun article à livrer");
        }
        catch(Exception e){
            throw e;
        }
    }

    public void genererEncaissementMultiple(String idCaisse, String reference, String u, Connection c, double montant, String aretourner) throws Exception {
        try {
            if(c == null) {
                c = new UtilDB().GetConn();
            }

            MvtCaisse mvt = new MvtCaisse();
            mvt.setReference(reference);
            mvt.setIdCaisse(idCaisse);
            mvt.setDaty(this.getDaty());
            mvt.setCredit(montant);
            mvt.setIdOrigine(this.getId());
            mvt.setDesignation("Encaissement multiple de la facture Num : " + this.getId());
            mvt.setTaux(1);
            mvt.setIdDevise("AR");
            mvt.setIdTiers(this.getIdClient());
            mvt.setCompte(ConstanteAsync.COMPTE_REGROUPEMENT);
            mvt.setIdCaissier(u);
            mvt.setMontantRetourner(Double.parseDouble(aretourner));
            mvt.createObject(u, c);
            mvt.validerObjectSimple(u, c);

            LiaisonPaiement lp = new LiaisonPaiement();
            lp.setId1(mvt.getId());
            lp.setId2(this.getId());
            lp.setMontant(montant);
            lp.createObject(u, c);
            lp.validerObject(u, c);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public static String enregistrerViserLivrerEncaisserMultiple(UserEJB u, PageInsertMultiple p, String nomtable, String colonneMere, Map<String, PaiementInfo> paiements, String[] estEnGrosListe, String aretourner, double montantPaye)throws Exception{
        Connection c = null;
        try {
            System.out.println("NIDITRA FONCTION "+Utilitaire.heureCouranteHMS());
            c = new UtilDB().GetConn();
            c.setAutoCommit(false);
            ClassMAPTable cmere = p.getObjectAvecValeur();
            VenteDetails[] cfille = (VenteDetails[]) p.getObjectFilleAvecValeur();
            for (int i = 0; i < cfille.length; i++) {
                cfille[i].setNomTable(nomtable);
//                cfille[i].setCompte("710011");
//                System.out.println("====> Compte POS" + cfille[i].getCompte());
            }
            Vente mere = ((Vente)cmere);
            if(mere.getIdMagasin()==null || mere.getIdMagasin().compareToIgnoreCase("")==0) {
                mere.setIdMagasin(u.getMagasinEncours().getId());
            }

            mere.setDatyPrevu(Utilitaire.dateDuJourSql());
            mere.setDaty(Utilitaire.dateDuJourSql());
            String user = u.getUser().getTuppleID();
            mere.setIdModePaiement(ConstanteAsync.PAIEMENT_MULTIPLE);
            mere.setMontantDonne(montantPaye);
            mere.setFille(cfille);
            Ingredients[] listeIng=mere.getListeIngredients(null,c);
            System.out.println("AVANT BOUCLE "+Utilitaire.heureCouranteHMS());
            for (int j = 0; j < cfille.length; j++) {
                Ingredients ing = (Ingredients) AdminGen.findById(listeIng,cfille[j].getIdProduit());  cfille[j].getIngredientById();;
                cfille[j].setPu(ing.getPv());
                if (ing.getUnite().compareToIgnoreCase("UNT00004") == 0 && !estEnGrosListe[j].equals("1")) {
                    double qteModifie = cfille[j].getQte() / 1000;
                    cfille[j].setQte(qteModifie);
                    cfille[j].setPu(ing.getPv());
                } else if (ing.getUnite().compareToIgnoreCase("UNT00004") == 0 && estEnGrosListe[j].equals("1")) {
                    double qteModifie = cfille[j].getQte() / 1000;
                    cfille[j].setQte(qteModifie);
                    cfille[j].setPu(ing.getPvgros());

                }
            }
            System.out.println("APRES  BOUCLE "+Utilitaire.heureCouranteHMS());
            PaiementInfo[] listePayement = paiements.values().toArray(new PaiementInfo[0]);
            double somme = 0;
            double sommeApayer = 0;

            for (PaiementInfo info : listePayement) {
                somme += info.getMontant();
            }

            for (VenteDetails detail : cfille) {
                double aPayer = arrondir(detail.getPu() * detail.getQte());
                sommeApayer += (aPayer);
            }

            System.out.println("SOMME ====>" + somme+ " heure "+Utilitaire.heureCouranteHMS());
            System.out.println("SOMME A PAYER ====>" + sommeApayer);
            // I removed this because it doesn't allow the remise to be paid, we must fix the top
//            if (somme < sommeApayer) throw new Exception("Montant payer insuffisant par rapport au montant total !");
            String idPoint = u.getPointEncours().getId();

//            mere.createObject(""+u.getUser().getRefuser(),c);

            double montantTTC = AdminGen.calculSommeDouble(cfille, "montantTTCCalc");
//            System.out.println(" montantTTC " + montantTTC);
            double montantTTCPAr1000 = montantTTC / 1000;
            montantTTCPAr1000 = Utilitaire.arrondir(montantTTCPAr1000, 1);
//            System.out.println(" montantTTC " + montantTTC + " montantTTCPAr1000 1 " + montantTTCPAr1000);
            montantTTCPAr1000 = montantTTCPAr1000 * 1000;
            double appoint = montantTTCPAr1000 - montantTTC;
//            u.createObjectMultiple(cfille,colonneMere,mere.getTuppleID(),c);


            mere.createObject(""+u.getUser().getRefuser(),c);

            //if (appoint != 0) mere.creerAPpoint(appoint).createObject(""+u.getUser().getRefuser(),c);

            mere.setEtat(11);
            mere.updateToTableWithHisto(""+u.getUser().getRefuser(),c);
            VenteDetails[] listeFille=(VenteDetails[]) mere.getFille();
            MvtStock mvtStockMere = mere.createMvtStockVenteDirecte();
            List<MvtStockFille> mvtStockFilles = new ArrayList<>();
            for(int i=0;i<listeFille.length;i++){
                Ingredients ing = (Ingredients) AdminGen.findById(listeIng,listeFille[i].getIdProduit());  //cfille[j].getIngredientById();;
                if (ing.getCompose() == 1) {
                    RecetteLib[] listeComposition = ing.getRecette(null, c);
                    for (RecetteLib recetteLib : listeComposition) {
                        MvtStockFille mvtStockFille = new MvtStockFille();
                        mvtStockFille.setIdProduit(recetteLib.getIdingredients());
                        double qteIngredient = recetteLib.getQuantite() * listeFille[i].getQte();
                        mvtStockFille.setSortie(qteIngredient);
                        mvtStockFille.setIdVenteDetail(listeFille[i].getId());
                        mvtStockFille.setPu(recetteLib.getPu());
                        mvtStockFille.setMontant(mvtStockFille.getSortie() * recetteLib.getPu());
                        mvtStockFille.setDesignation("Sortie par vente directe de " + ing.getLibelle() + " - Composition de " + recetteLib.getLibelleingredient());
                        mvtStockFilles.add(mvtStockFille);
                    }

                }

                listeFille[i].calculerRevient(c);
                listeFille[i].updateToTableWithHisto(""+u.getUser().getRefuser(),c);
            }
            mvtStockMere.setFille(mvtStockFilles.toArray(new MvtStockFille[]{}));

            mvtStockMere.setEtat(ConstanteEtat.getEtatValider());
            mvtStockMere.createObject(String.valueOf(u.getUser().getRefuser()), c);

            mere.genererEcriture(""+u.getUser().getRefuser(), c);
//            mere.validerObject(""+u.getUser().getRefuser(),c);
            String idBL = mere.genererBonLivraisonAvecMvtStockTotal(""+u.getUser().getRefuser(),c);
//            mere.genererEncaissementVente(modePaiement, reference, u.getUser().getTuppleID(), c, estEnGrosListe);
            double aRetournerCalcule = 0;
            if (somme > montantTTCPAr1000) {
                aRetournerCalcule = somme - montantTTCPAr1000;
            }
            Caisse[]listeCaisse=u.getListeCaissePoint();
            String []colCaisse={"idCategorieCaisse"};
            String[] valCaisse=new String[1];
            for(Map.Entry<String, PaiementInfo> entry : paiements.entrySet()) {
                String modePaiement = entry.getKey();
                PaiementInfo paiement = entry.getValue();
                String monnaie = (modePaiement.equalsIgnoreCase("especes")) ? String.valueOf(aRetournerCalcule) : "0";
                if (modePaiement==null || modePaiement.compareToIgnoreCase("")==0 || modePaiement.compareToIgnoreCase("especes") == 0) {
                    valCaisse[0]=ConstanteAsync.caisseCategEspeece;
                    Caisse[] retourCaisse=(Caisse[]) AdminGen.find(listeCaisse,colCaisse,valCaisse);
                    if(retourCaisse!=null) mere.genererEncaissementMultiple(retourCaisse[0].getId(), paiement.getReference(), user, c, paiement.getMontant(), monnaie);
                } else if(modePaiement.compareToIgnoreCase("virement")==0) {
                    mere.genererEncaissementMultiple(getCaisseTpe(c, idPoint), paiement.getReference(), user, c, paiement.getMontant(), monnaie);
                } else if(modePaiement.compareToIgnoreCase("mvola")==0) {
                    valCaisse[0]=ConstanteAsync.caisseCategMvola;
                    valCaisse[0]=ConstanteAsync.caisseCategEspeece;
                    Caisse[] retourCaisse=(Caisse[]) AdminGen.find(listeCaisse,colCaisse,valCaisse);
                    if(retourCaisse!=null)
                        mere.genererEncaissementMultiple(retourCaisse[0].getId(), paiement.getReference(), user, c, paiement.getMontant(), monnaie);
                } else if(modePaiement.compareToIgnoreCase("orange")==0) {
                    valCaisse[0]=ConstanteAsync.caisseCategOrange;
                    valCaisse[0]=ConstanteAsync.caisseCategEspeece;
                    Caisse[] retourCaisse=(Caisse[]) AdminGen.find(listeCaisse,colCaisse,valCaisse);
                    if(retourCaisse!=null)
                    mere.genererEncaissementMultiple(retourCaisse[0].getId(), paiement.getReference(), user, c, paiement.getMontant(), monnaie);
                } else if(modePaiement.compareToIgnoreCase("airtel")==0) {
                    valCaisse[0]=ConstanteAsync.caisseCategAirtel;
                    valCaisse[0]=ConstanteAsync.caisseCategEspeece;
                    Caisse[] retourCaisse=(Caisse[]) AdminGen.find(listeCaisse,colCaisse,valCaisse);
                    if(retourCaisse!=null)
                        mere.genererEncaissementMultiple(retourCaisse[0].getId(), paiement.getReference(), user, c, paiement.getMontant(), monnaie);
                } else if(modePaiement.compareToIgnoreCase("cheque")==0) {
                    mere.genererEncaissementMultiple(getCaisseCheque(c, idPoint), paiement.getReference(), user, c, paiement.getMontant(), monnaie);
                }  else if(modePaiement.compareToIgnoreCase("bonAchat")==0) {
                    mere.genererEncaissementMultiple(getCaisseBonAchat(c, idPoint), paiement.getReference(), user, c, paiement.getMontant(), monnaie);
                } else if(modePaiement.compareToIgnoreCase("cadeaux")==0) {
                    mere.genererEncaissementMultiple(getCaisseCadeau(c, idPoint), paiement.getReference(), user, c, paiement.getMontant(), monnaie);
                }
            }
            c.commit();
//            try {
//                ExportPDFDirect.ficheVente(mere.getId());
//            } catch (Exception printEx) {
//                printEx.printStackTrace();
//            }
            return mere.getId();
        } catch (Exception e) {
            e.printStackTrace();
            c.rollback();
            throw e;
        }finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private File genererCodeBarreWithName(String id, String chemin, String nomPersonne, String u) throws Exception {
        File fichier = new File(chemin);

        File parentDir = fichier.getParentFile();
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            parentDir = new File("/tmp/vente");
            parentDir.mkdirs();
            fichier = new File(parentDir, fichier.getName());
            System.out.println("➡ Fichier redirigé vers : " + fichier.getAbsolutePath());
        }

        if (fichier.exists()) {
            System.out.println("ℹ Code-barre déjà généré : " + fichier.getAbsolutePath());
            return fichier;
        }

        ConstanteAsync.CODE128_BEAN.setFontSize(0);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                baos, "image/png", ConstanteAsync.DPI, BufferedImage.TYPE_BYTE_BINARY, false, 0);
        ConstanteAsync.CODE128_BEAN.generateBarcode(canvas, id);
        canvas.finish();

        BufferedImage barcodeImage = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));

        int extraHeight = 30;
        BufferedImage combined = new BufferedImage(
                barcodeImage.getWidth(), barcodeImage.getHeight() + extraHeight,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g = combined.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, combined.getWidth(), combined.getHeight());

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(nomPersonne);
        int x = (combined.getWidth() - textWidth) / 2;
        int y = fm.getAscent() + 5;
        g.drawString(nomPersonne, x, y);

        g.drawImage(barcodeImage, 0, extraHeight, null);

        g.dispose();
//        System.out.println("avant updateeeee ");
//
        ImageIO.write(combined, "png", fichier);
//        System.out.println("avant updateeeee 2222 ");
//        Ingredients ing = (Ingredients) new Ingredients().getById(this.getId(), "AS_INGREDIENTS", null);
//        System.out.println(" andeha hanao updateeeee ");
//        ing.setCodeBarre(nomPersonne);
//
//        ing.updateToTableWithHisto(u);

        return fichier;
    }


    public void echanger(String u, Connection c) throws Exception {

        if (this.getFactureEchange() == null || this.getFactureEchange().isEmpty()) {
            return;
        }

        LiaisonPaiement[] paiements = (LiaisonPaiement[]) CGenUtil.rechercher(new LiaisonPaiement(), null, null, c, " AND id2='" + this.getFactureEchange() + "'");
        for (int j = 0; j < paiements.length; j++) {
            LiaisonPaiement paiement = paiements[j];
            paiement.setId2(this.getId());
            paiement.updateToTableWithHisto(u,c);
        }


        AvoirFC[] avoirs = (AvoirFC[]) CGenUtil.rechercher(new AvoirFC(), null, null, c, " AND idVente ='" + this.getFactureEchange() + "'");
        for (int j = 0; j < avoirs.length; j++) {
            AvoirFC avoir = avoirs[j];
            avoir.setIdVente(this.getId());
            avoir.setIdOrigine(this.getId());
            avoir.updateToTableWithHisto(u,c);
        }


        Vente ancienVente = getAncien(c);
        AvoirFC avoirReste = ancienVente.genereAvoirReste();
        avoirReste.createObject(u, c);
        avoirReste.validerObject(u, c);
//        avoirReste.sortieStock(u, c);

        MvtStock mvtStock = avoirReste.genereEntre(ancienVente);
        mvtStock.createObject(u, c);
        mvtStock.validerObject(u, c);

    }

    public Vente getAncien(Connection c) throws Exception {
        Vente ancien = new Vente();
        ancien.setId(this.getFactureEchange());
        ancien = (Vente) CGenUtil.getMereAvecFille(ancien ,  c, null);
        return ancien;
    }

    public AvoirFC genereAvoirReste() throws Exception {
        AvoirFC avoirAVnt = new AvoirFC();

        avoirAVnt.setDesignation("Retour vente");
        avoirAVnt.setIdMagasin(this.getIdMagasin());
        avoirAVnt.setDaty(this.getDaty());
        avoirAVnt.setIdOrigine(this.getIdOrigine());
        avoirAVnt.setIdVente(this.getId());
        avoirAVnt.setIdClient(this.getIdClient());
        avoirAVnt.setIdMotif(ConstanteSocobis.ID_MTF_AVOIR_RET);
        avoirAVnt.setIdtypeavoir(ConstanteSocobis.ID_TYPE_AVOIR);

        VenteDetails[] details = (VenteDetails[]) this.getFille();

        AvoirFCFille[] avoirFCFilles = new AvoirFCFille[details.length];
        for (int j = 0; j < details.length; j++) {
            VenteDetails ancienDetail = details[j];
            avoirFCFilles[j] = ancienDetail.genereAvoirReste();
        }

        avoirAVnt.setFille(avoirFCFilles);

        return avoirAVnt;
    }

    @Override
    public String getLiaisonFille() {
        return "idVente";
    }

    @Override
    public String getNomClasseFille() {
        return "vente.VenteDetails";
    }

    public double getMontantDonne() {
        return montantDonne;
    }

    public void setMontantDonne(double montantDonne) {
        this.montantDonne = montantDonne;
    }

    public double getMontantRetourner() {
        return montantRetourner;
    }

    public void setMontantRetourner(double montantRetourner) {
        this.montantRetourner = montantRetourner;
    }

    public String getIdModePaiementLib() {
        return idModePaiementLib;
    }

    public void setIdModePaiementLib(String idModePaiementLib) {
        this.idModePaiementLib = idModePaiementLib;
    }

    public String getIdModePaiement() {
        return idModePaiement;
    }

    public void setIdModePaiement(String idModePaiement) {
        this.idModePaiement = idModePaiement;
    }

    public String getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(String idResponsable) {
        this.idResponsable = idResponsable;
    }

    public double getRemise() {
        return remise;
    }

    public void setRemise(double remise) {
        this.remise = remise;
    }

    public double getRemiseMontant() {
        return remiseMontant;
    }

    public void setRemiseMontant(double remiseMontant) {
        this.remiseMontant = remiseMontant;
    }

    public Date getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(Date dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public String getHeureSaisie() {
        return heureSaisie;
    }

    public void setHeureSaisie(String heureSaisie) {
        this.heureSaisie = heureSaisie;
    }

    public double getMontantTTCCalcul() {
        return AdminGen.calculSommeDouble((VenteDetails[])this.getFille(), "montantTTCCalc");
    }

    public MvtStock genererMvtStock(Connection c) throws Exception {
        boolean estOuvert = false;

        MvtStock var12;
        try {
            if (c == null) {
                c = (new UtilDB()).GetConn();
                estOuvert = true;
            }

            MvtStock mv = new MvtStock();
            MvtStock[] listeMv = (MvtStock[])CGenUtil.rechercher(mv, (String[])null, (String[])null, c, " AND idVente ='" + this.getId() + "'");
            if (listeMv.length > 0) {
                throw new Exception("Un mouvement de stock a d\u00E9j\u00E0 \u00E9t\u00E9 g\u00E9n\u00E9r\u00E9 pour cette vente.");
            }

            mv.setDaty(this.getDaty());
            mv.setDesignation("Mouvement de stock relatif " + this.getId());
            String mag = (this.getIdMagasinVente() != null && !this.getIdMagasinVente().isEmpty()) ? this.getIdMagasinVente() : this.getIdMagasin();
            mv.setIdMagasin(mag);
            mv.setIdTypeMvStock("TPMVST000022");
            //mv.setIdTransfert(this.getId());
            mv.setIdVente(this.getId());
            mv.setIdobjet(this.getId());
            VenteDetails[] listeF = (VenteDetails[])this.getFille();
            if (listeF != null) {
                List<MvtStockFille> lfList = new ArrayList<>();
                for(int i = 0; i < listeF.length; ++i) {
                    MvtStockFille msf = listeF[i].genererMvtStockFille(c);
                    if (msf != null) {
                        msf.setIdMvtStock(mv.getId());
                        lfList.add(msf);
                    }
                }
                mv.setFille(lfList.toArray(new MvtStockFille[0]));
            }
            var12 = mv;
        } catch (Exception e) {
            throw e;
        } finally {
            if (c != null && estOuvert) {
                c.close();
            }

        }

        return var12;
    }


    /*public MvtStock genererMvtStock(Connection c, String idMagasin) throws Exception {
        boolean estOuvert = false;

        MvtStock var12;
        try {
            if (c == null) {
                c = (new UtilDB()).GetConn();
                estOuvert = true;
            }

            MvtStock mv = new MvtStock();
            MvtStock[] listeMv = (MvtStock[])CGenUtil.rechercher(mv, (String[])null, (String[])null, c, " AND idVente ='" + this.getId() + "'");
            if (listeMv.length > 0) {
                throw new Exception("Un mouvement de stock a d\u00E9j\u00E0 \u00E9t\u00E9 g\u00E9n\u00E9r\u00E9 pour cette vente.");
            }

            mv.setDaty(this.getDaty());
            mv.setDesignation("Mouvement de stock relatif " + this.getId());
            mv.setIdMagasin(idMagasin);
            mv.setIdTypeMvStock("TPMVST000022");
            //mv.setIdTransfert(this.getId());
            mv.setIdVente(this.getId());
            mv.setIdobjet(this.getId());
            VenteDetails[] listeF = (VenteDetails[])this.getFille();
            MvtStockFille[] lf = new MvtStockFille[listeF.length];

            for(int i = 0; i < listeF.length; ++i) {
                lf[i] = listeF[i].genererMvtStockFille(c);
                if(lf[i]!=null) lf[i].setIdMvtStock(mv.getId());
            }

            mv.setFille(lf);
            var12 = mv;
        } catch (Exception e) {
            throw e;
        } finally {
            if (c != null && estOuvert) {
                c.close();
            }

        }

        return var12;
    }*/



    @Override
    public Object validerObjectBatch(String u, Statement st) throws Exception {
        Connection c = st.getConnection();
        boolean estOuvert = false;
        try {
            if (c == null) {
                estOuvert = true;
                c = new UtilDB().GetConn();
            }
//            CheckEtatStockVenteDetails(c);
            VenteDetails[] listeFille=(VenteDetails[]) this.getFille();
            Ingredients[] listeIng=this.getListeIngredients(null,c);
            this.setListeIng(listeIng);
            for(int i=0;i<listeFille.length;i++){
                Ingredients ingredients = null;
                if(listeFille[i].getIdProduit()!=null&&!listeFille[i].getIdProduit().trim().isEmpty())
                {
                    String []col={"id"};
                    String []val={listeFille[i].getIdProduit()};
                    ClassMAPTable[] found = AdminGen.find(listeIng,col,val);
                    if (found != null && found.length > 0) {
                        ingredients=(Ingredients) found[0];
                        listeFille[i].setIngFille(ingredients);
                        listeFille[i].setPoids(ingredients.getCalorie());
                    }
                    listeFille[i].calculerRevient(c);
                    listeFille[i].updateToTableWithHistoBatch(u,st);
                }
            }
            st.executeBatch();
            super.validerObjectBatch(u, st);
            echanger(u,c);
            setFille(listeFille);
            genererEcritureBatch(u, st);
            MvtStock stockEngage = genererStockEngage(listeFille, c);
            if (stockEngage != null) {
                stockEngage.createObjectBatch(u, st);
            }
            Ristourne ristourne = genererRistourne();
            if (ristourne != null) {
                ristourne.createObjectBatch(u, st);
                ristourne.validerObjectBatch(u, st);
            }
              //createMvtStockSortie(u, c);
            if(this.getEstPrevu() == 0){
                genererPrevisionBatch(u, st);
            }
            else if (this.getEstPrevu() == 1){
                genererPrevisionPrevuBatch(u, st);
            }
            boolean estAppro = false;
            String[] clientsAppro = ConstanteSocobis.CLIENT_APPRO_VENTE;
            for (String idClientListe : clientsAppro) {
                if (this.getIdClient().equals(idClientListe)) {
                    estAppro = true;
                    break;
                }
            }
            if(estAppro == true){
                //genererApprovisionnement(listeFille, u, c);
            }
            st.executeBatch();

            return this;

        } catch (Exception e) {
            if (c != null) {
                c.rollback();
            }
            e.printStackTrace();
            throw e;
        } finally {
            if (c != null && estOuvert == true) {
                c.close();
            }
        }
    }

}
