/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faturefournisseur;


import bean.CGenUtil;
import produits.Ingredients;
import produits.RecetteLib;
import stock.MvtStockFille;
import utils.ConstanteSocobis;

import java.sql.Connection;

public class As_BonDeLivraison_Fille_Cpl extends As_BonDeLivraison_Fille{
    String produitlib,unitelib;
    private double qteEntree;
    private double resteAEntrer,montant,puar;

    public As_BonDeLivraison_Fille_Cpl() {
        this.setNomTable("AS_BONDELIVRAISON_LIBCPL");
    }

    public String getProduitlib() {
        return produitlib;
    }

    public void setProduitlib(String produitlib) {
        this.produitlib = produitlib;
    }

    public String getUnitelib() {
        return unitelib;
    }

    public void setUnitelib(String unitelib) {
        this.unitelib = unitelib;
    }

    public MvtStockFille createMvtStockFille() throws Exception{
        MvtStockFille msf=new MvtStockFille();
        msf.setIdProduit(this.produit);
        msf.setEntree(this.getQuantite());
        msf.setPu(this.getPu());
        msf.setDesignation(this.produitlib);
        return msf;
    }

    public FactureFournisseurDetailsCpl toFactureFournisseurDetails(As_BonDeCommande bc) throws Exception{
        FactureFournisseur f = (FactureFournisseur) new FactureFournisseur().getById(this.getIdFactureFournisseur(),"FACTUREFOURNISSEUR",null);
        FactureFournisseurDetailsCpl v = new FactureFournisseurDetailsCpl();
        v.setIdProduit(this.getProduit());
        if(this.getProduit() != null){
            Ingredients p = new Ingredients();
            p.setId(this.getProduit());
            Ingredients[] produit = (Ingredients[]) CGenUtil.rechercher(p,null, null, null, "");
            v.setCompte(produit[0].getCompte_achat());
        }
        v.setDesignation(this.getDesignation());
        v.setQte(this.getQuantite());
        v.setIdUniteLib(this.getUnitelib());
        if (this.getIdFactureFournisseur() == null) {
            v.setIdDevise("AR");
        } else {
            v.setIdDevise(f.getIdDevise());
        }
        if(bc != null){
            v.setIdDevise(bc.getIdDevise());
        }else{
            v.setIdDevise("AR");
        }

        v.setPu(this.getPu());
        v.setIdbcDetail(this.getId());
        return v;
    }

    public FactureFournisseurDetailsCpl[] toFactureFournisseurPrestataire(Connection c) throws Exception {
        FactureFournisseurDetailsCpl[] details = null;
        boolean connectionWasOpenedInside = false;

        try {
            if (c == null) {
                c = new utilitaire.UtilDB().GetConn();
                connectionWasOpenedInside = true;
            }

            Ingredients ingredients = new Ingredients();
            ingredients.setId(this.produit);

            RecetteLib[] recetteLibs = ingredients.getRecetteIngredient("AS_RECETTE_LIB_CAT", ConstanteSocobis.CATEGORIE_MAINDOEUVRE, c);

            details = new FactureFournisseurDetailsCpl[recetteLibs.length];
            for (int i = 0; i < recetteLibs.length; i++) {
                details[i] = new FactureFournisseurDetailsCpl();
                details[i].setIdProduit(recetteLibs[i].getIdingredients());
                details[i].setPu(recetteLibs[i].getPu());
                details[i].setQte(recetteLibs[i].getQuantite() * this.getQuantite());
                details[i].setCompte(recetteLibs[i].getCompte_achat());
            }

            return details;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (connectionWasOpenedInside && c != null) {
                try {
                    c.close();
                } catch (Exception ce) {
                    ce.printStackTrace();
                }
            }
        }
    }

    public double getQteEntree() {
        return qteEntree;
    }

    public void setQteEntree(double qteEntree) {
        this.qteEntree = qteEntree;
    }

    public double getResteAEntrer() {
        return resteAEntrer;
    }

    public void setResteAEntrer(double resteAEntrer) {
        this.resteAEntrer = resteAEntrer;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public double getPuar() {
        return puar;
    }

    public void setPuar(double puar) {
        this.puar = puar;
    }

    
}
