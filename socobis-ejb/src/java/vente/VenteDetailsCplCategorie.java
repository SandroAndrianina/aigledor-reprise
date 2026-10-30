/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vente;


/**
 *
 * @author Angela
 */
public class VenteDetailsCplCategorie extends VenteDetailsCpl{
    private String categorieIngredient;
    private int nb;
    private String idModele,idModeleLib,idCategorieModele;
    public VenteDetailsCplCategorie(){
        setNomTable("VENTE_DETAILS_CAT");
    }


    public String getCategorieIngredient() {
        return categorieIngredient;
    }

    public void setCategorieIngredient(String categorieIngredient) {
        this.categorieIngredient = categorieIngredient;
    }

    public int getNb() {
        return nb;
    }

    public void setNb(int nb) {
        this.nb = nb;
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
}
