package compteur;

import bean.CGenUtil;
import bean.ClassMAPTable;
import bean.ClassMere;
import historique.MapUtilisateur;

import java.sql.Connection;
import java.sql.Date;

public class CompteurElectriciteMere extends ClassMere {
    private String id;
    private Date daty;
    private String ligne;
    private String remarque;

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

    public String getLigne() {
        return ligne;
    }

    public void setLigne(String ligne) {
        this.ligne = ligne;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }


    public CompteurElectriciteMere() throws Exception {
        this.setNomTable("COMPTEURELECTRICITEMERE");
        this.setNomClasseFille("compteur.CompteurElectricite");
        this.setLiaisonFille("idmere");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("CME", "GETSEQ_CompteurElectriciteMere");
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
    public ClassMAPTable createObject(String u, Connection c) throws Exception {
        CompteurElectriciteMere filtre = new CompteurElectriciteMere();
        setLigne(this.ligne);
        setDaty(this.daty);
        CompteurElectriciteMere[] existeMere = (CompteurElectriciteMere[]) CGenUtil.rechercher(filtre, null, null, c, "");
        CompteurElectricite[] filles = (CompteurElectricite[]) getFille();
        if(filles.length>0){
            filles[0].setEcart(Math.abs(filles[0].getValeur() - filles[0].getAncien()));
            filles[0].setIdligne(this.ligne);
            CompteurElectricite filtreFille = new CompteurElectricite();
            filtreFille.setIdcategorie(filles[0].getIdcategorie());
            filtreFille.setIdmachine(filles[0].getIdmachine());
            CompteurElectricite[] existeFille = (CompteurElectricite[]) CGenUtil.rechercher(filtreFille, null, null, c, "");
            if(existeFille.length>0 && existeMere.length>0){
                throw new Exception("Ce compteur existe d\\u00E9j\\u00E0 pour cette ligne, ce categorie, cette machine et  cette date");
            }
        }
        return super.createObject(u, c);
    }

    @Override
    public String[] getRoleValidation() {
        return new String[]{"maintenancier", "cheffab"};
    }

    @Override
    public Object validerObject(MapUtilisateur u, Connection c) throws Exception {
        String role = u.getIdrole();
        switch (role) {
            case "maintenancier":
                checkEtat(3);
                this.setEtat(3);
                break;
            case "cheffab":
                checkEtatNonValide(1);
                checkEtat(11);
                this.setEtat(11);
                break;
        }
        return super.validerObject(u.getTuppleID(), c);
    }

    public void checkEtat(int etat) throws Exception {
        if(this.getEtat()==etat){
            throw new Exception("Ce relev\\u00E9 est d\\u00E9j\\u00E0 valid\\u00E9.");
        }
    }

    public void checkEtatNonValide(int etat) throws Exception {
        if(this.getEtat()==etat){
            throw new Exception("Ce relev\\u00E9 est doit valider par un maintenancier d'abord");
        }
    }
}

