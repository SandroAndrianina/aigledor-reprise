/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faturefournisseur;

import bean.LibelleAffichage;
import chatbot.AiTabDesc;
import chatbot.ClassIA;
import utils.ConstanteAsync;

import java.sql.Date;

/**
 *
 * @author nouta
 */
@AiTabDesc("La structure de ma table d'achat et depesnes est comme ceci: ")
public class FactureFournisseurDetailsCpl extends FactureFournisseurDetails implements ClassIA {
    @LibelleAffichage("Produit")
    protected String idProduitLib;
    @LibelleAffichage("Num&eacute;ro du magasin")
    protected String idMagasin;
    @LibelleAffichage("Magasin")
    protected String idMagasinLib;
    @LibelleAffichage("Num&eacute;ro du point de vente")
    protected String idPoint;
    @LibelleAffichage("Point de vente")
    protected String idPointLib;
    @LibelleAffichage("Montant total")
    protected double montanttotal;
    @LibelleAffichage("Montant avec frais accessoire")
    protected double montantavecfraisaccessoire;
    @LibelleAffichage("Prix unitaire avec frais accessoire")
    protected double puavecfraisaccessoire;
    @LibelleAffichage("Montant total TTC")
    protected double montantttc;
    @LibelleAffichage("Num&eacute;ro de la cat&eacute;gorie")
    protected String idCategorie;
    @LibelleAffichage("Libell&eacute; de la cat&eacute;gorie")
    protected String idCategorieLib;
    @LibelleAffichage("Date")
    protected Date daty;
    @LibelleAffichage("Libell&eacute; exact du produit")
    String libelleexacte;

    private String idUnite, idUniteLib;

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

    @Override
    public String getNomTableIA() {
        return "FFFILLECPL_VISEE";
    }
    @Override
    public String getUrlListe() {
        return "/aigledor/pages/module.jsp?but=facturefournisseur/facturefournisseur-liste.jsp&currentMenu=MNDN000000007";
    }
    @Override
    public String getUrlAnalyse() {
        return "/aigledor/pages/module.jsp?but=facturefournisseur/achat-analyse.jsp";
    }
    @Override
    public String getUrlSaisie() {
        return "/aigledor/pages/module.jsp?but=facturefournisseur/facturefournisseur-saisie.jsp&currentMenu=MNDN000000014";
    }
    @Override
    public ClassIA getClassListe() {
        return this;
    }
    @Override
    public ClassIA getClassAnalyse() {
        return this;
    }

    @Override
    public ClassIA getClassSaisie() {
        return this;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public String getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(String idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getIdCategorieLib() {
        return idCategorieLib;
    }

    public void setIdCategorieLib(String idCategorieLib) {
        this.idCategorieLib = idCategorieLib;
    }

    public double getMontantavecfraisaccessoire() {
        return montantavecfraisaccessoire;
    }

    public void setMontantavecfraisaccessoire(double montantavecfraisaccessoire) {
        this.montantavecfraisaccessoire = montantavecfraisaccessoire;
    }

    public double getPuavecfraisaccessoire() {
        return puavecfraisaccessoire;
    }

    public void setPuavecfraisaccessoire(double puavecfraisaccessoire) {
        this.puavecfraisaccessoire = puavecfraisaccessoire;
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

    public String getIdPoint() {
        return idPoint;
    }

    public void setIdPoint(String idPoint) {
        this.idPoint = idPoint;
    }

    public String getIdPointLib() {
        return idPointLib;
    }

    public void setIdPointLib(String idPointLib) {
        this.idPointLib = idPointLib;
    }

    public double getMontanttotal() {
        return montanttotal;
    }

    public void setMontanttotal(double montanttotal) {
        this.montanttotal = montanttotal;
    }

    public double getMontantttc() {
        return montantttc;
    }

    public void setMontantttc(double montantttc) {
        this.montantttc = montantttc;
    }

    public FactureFournisseurDetailsCpl()throws Exception{
        super.setNomTable("FACTUREFOURNISSEURFILLECPL");
    }

    public String getIdProduitLib() {
        return idProduitLib;
    }

    public void setIdProduitLib(String idProduitLib) {
        this.idProduitLib = idProduitLib;
    }
    public String getLibelleexacte() {
        return libelleexacte;
    }
    public void setLibelleexacte(String libelleexacte) {
        this.libelleexacte = libelleexacte;
    }
    
    
}
