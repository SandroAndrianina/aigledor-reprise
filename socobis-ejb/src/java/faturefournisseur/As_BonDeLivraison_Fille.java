/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faturefournisseur;

import bean.CGenUtil;
import bean.ClassFille;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import produits.Ingredients;
import produits.RecetteLib;
import stock.MvtStockFille;
import utilitaire.Constante;
import utils.ConstanteSocobis;
import vente.As_BondeLivraisonClient;
import vente.VenteDetails;

/**
 *
 * @author Mirado
 */
public class As_BonDeLivraison_Fille extends ClassFille{
    
    String id,produit,numbl;
    double quantite,pu;
    String iddetailsfacturefournisseur;
    String unite, idbc_fille;
    String idFactureFournisseur;
    String designation;
    double tauxdechange;

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public As_BonDeLivraison_Fille() {
        try{
            this.setNomClasseMere("faturefournisseur.As_BonDeLivraison");
            this.setNomTable("AS_BONDELIVRAISON_FILLE");
            this.setLiaisonMere("numbl");
        }catch(Exception e){
            Logger.getLogger(VenteDetails.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void construirePK(Connection c) throws Exception {
        this.preparePk("BLF", "getSeqBondeLivraisonFille");
        this.setId(makePK(c));
    }

//    public String getNomClasseMere(){
//      return "faturefournisseur.As_BonDeLivraison";
//    }
//
//    public String getLiaisonMere(){
//      return "numbl";
//    }

    
    @Override
    public String getTuppleID() {
        return id;
    }

    @Override
    public String getAttributIDName() {
        return "id";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIdFactureFournisseur(String idFactureFournisseur) {
        this.idFactureFournisseur = idFactureFournisseur;
    }

    public String getIdFactureFournisseur() {
        return idFactureFournisseur;
    }

    public String getProduit() {
        return produit;
    }

    public void setProduit(String produit) {
        this.produit = produit;
    }

    public String getNumbl() {
        return numbl;
    }

    public void setNumbl(String numbl) {
        this.numbl = numbl;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) throws Exception {
        if (this.getMode().equals("modif") && quantite <= 0 ) {
            throw new Exception("Quantite ne peut pas etre inferieur à 0");
        }
        this.quantite = quantite;
    }

    public String getIddetailsfacturefournisseur() {
        return iddetailsfacturefournisseur;
    }

    public void setIddetailsfacturefournisseur(String iddetailsfacturefournisseur) {
        this.iddetailsfacturefournisseur = iddetailsfacturefournisseur;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public String getIdbc_fille() {
        return idbc_fille;
    }

    public void setIdbc_fille(String idbc_fille) {
        this.idbc_fille = idbc_fille;
    }
    public double getPu() {
        return pu;
    }

    public void setPu(double pu) throws Exception {
        if (this.getMode().equals("modif") && pu <= 0) {
            throw new Exception("Pu non valide");
        }
        this.pu = pu;
    }
    @Override
    public String getNomClasseMere() {
        return "faturefournisseur.As_BonDeLivraison";
    }
    @Override
    public String getLiaisonMere() {
        return "numbl";
    }

    
    public MvtStockFille createMvtStockFille() throws Exception{
        MvtStockFille msf=new MvtStockFille();
        msf.setIdProduit(this.produit);
        msf.setEntree(this.quantite);
        return msf;
    }

    public MvtStockFille genererMvtStockFille(Connection c)throws Exception
    {
        //TODO : Anontaniana an'i Alicia ny logique an'ity
//        As_BonDeLivraison m=(As_BonDeLivraison) this.findMere(null,c);
//        System.out.println("BL = "+m.getNomTable());
//        System.out.println("BL_ID = "+m.getId());
        MvtStockFille mf=new MvtStockFille();
        mf.setIdProduit(this.getProduit());
        mf.setEntree(this.getQuantite());
        return mf;
    }


    @Override
    public void controler(Connection c) throws Exception {
        Ingredients p = new Ingredients();
        p.setId(this.produit);
        Ingredients[] produit = (Ingredients[]) CGenUtil.rechercher(p,null, null, c, "");
        this.setUnite(produit[0].getUnite());
        super.controler(c);
    }

    public double getTauxdechange() {
        return tauxdechange;
    }

    public void setTauxdechange(double tauxdechange) {
        this.tauxdechange = tauxdechange;
    }
}
