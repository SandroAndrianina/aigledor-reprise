package faturefournisseur;

import bean.CGenUtil;
import bean.ClassMere;
import historique.MapUtilisateur;
import maintenance.travaux.TravauxCpl;
import produits.Ingredients;
import utilitaire.UtilDB;
import utils.ConstanteAsync;
import vente.Vente;

import java.sql.Connection;
import java.sql.Date;

public class DmdAchat extends ClassMere {
    private String id;
    private Date daty;
    private String fournisseur;
    private String remarque;

    private Date dateLimite;

    private String idMagasin;

    private String idCategorie;

    private String idObjet;
    private String service;
    private String idProvenance;

    public DmdAchat() throws Exception {
        this.setNomTable("DMDACHAT");
        this.setLiaisonFille("idmere");
        this.setNomClasseFille("faturefournisseur.DmdAchatFille");
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Date getDateLimite() {
        return dateLimite;
    }

    public void setDateLimite(Date dateLimite) {
        this.dateLimite = dateLimite;
    }

    public String getIdMagasin() {
        return idMagasin;
    }

    public void setIdMagasin(String idMagasin) {
        this.idMagasin = idMagasin;
    }

    public String getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(String idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date date) {
        this.daty = date;
    }

    public String getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(String fournisseur) throws Exception {
        if(this.getMode().compareToIgnoreCase("modif")==0&&(fournisseur==null||fournisseur.compareToIgnoreCase("")==0)) throw new Exception("Fournisseur obligatoire");
        this.fournisseur = fournisseur;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public String getIdObjet() {
        return idObjet;
    }

    public void setIdObjet(String idObjet) {
        this.idObjet = idObjet;
    }

    public FactureFournisseurDetails[] getFactureFournisseurDetails(Connection c) throws Exception {
        int indice = 0;
        try {
            if (c == null) {
                c = new UtilDB().GetConn();
                indice = 1;
            }
            DmdAchatFille[] dmdAchatFille = (DmdAchatFille[])this.getFille("DMDACHATFILLE",c,"");
            FactureFournisseurDetails[] factureFoDetails = new  FactureFournisseurDetails[dmdAchatFille.length];

            for (int i = 0; i < dmdAchatFille.length; i++) {
                Ingredients ing = (Ingredients)new Ingredients().getById(dmdAchatFille[i].getIdproduit(),"AS_INGREDIENTS",c);
                factureFoDetails[i] = new FactureFournisseurDetails();
                factureFoDetails[i].setIdProduit(dmdAchatFille[i].getIdproduit());
                factureFoDetails[i].setPu(dmdAchatFille[i].getPu());
                factureFoDetails[i].setQte(dmdAchatFille[i].getQuantite());
                factureFoDetails[i].setTva(dmdAchatFille[i].getTva());
                factureFoDetails[i].setIdDevise("AR");
                factureFoDetails[i].setCompte(ing.getCompte_achat());
            }
            return factureFoDetails;
        }catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }finally{
            if(indice == 1 && c!=null)c.close();
        }
    }

    public As_BonDeCommande_Fille_CPL[] getBonDeCommandeDetails(Connection c) throws Exception {
        int indice = 0;
        try {
            if (c == null) {
                c = new UtilDB().GetConn();
                indice = 1;
            }
            DmdAchatFilleLib[] dmdAchatFille = (DmdAchatFilleLib[]) CGenUtil.rechercher  (new DmdAchatFilleLib(), null, null,c," and idmere = '" + getId() + "'");
            As_BonDeCommande_Fille_CPL[] factureFoDetails = new  As_BonDeCommande_Fille_CPL[dmdAchatFille.length];

            for (int i = 0; i < dmdAchatFille.length; i++) {
//                Ingredients ing = (Ingredients)new Ingredients().getById(dmdAchatFille[i].getIdproduit(),"AS_INGREDIENTS",c);
                factureFoDetails[i] = new As_BonDeCommande_Fille_CPL();
                factureFoDetails[i].setProduit(dmdAchatFille[i].getIdproduit());
                factureFoDetails[i].setPu(dmdAchatFille[i].getPu());
                factureFoDetails[i].setQuantite(dmdAchatFille[i].getQuantite());
                factureFoDetails[i].setTva(dmdAchatFille[i].getTva());
                factureFoDetails[i].setIdDevise("AR");
                factureFoDetails[i].setTauxDeChange(1);
                factureFoDetails[i].setProduitLib(dmdAchatFille[i].getDesignation());
                factureFoDetails[i].setDesignation(dmdAchatFille[i].getDesignation());
                factureFoDetails[i].setUnitelib(dmdAchatFille[i].getUnite());
            }
            return factureFoDetails;
        }catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }finally{
            if(indice == 1 && c!=null)c.close();
        }
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("DMDA", "GETSEQDMDACHAT");
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
    public String[] getRoleValidation() {
        return new String[]{"cheffab", "dg", "respachat"};
    }

    @Override
    public Object validerObject(MapUtilisateur u, Connection c) throws Exception {
        this.setRoleValidation(new String[0]);
//        String role = u.getIdrole();
//        switch (ConstanteAsync.identifierRole(role)) {
//            case MAGASINIER:
//                this.setEtat(3);
//                break;
//
//            case CHEF_ATELIER:
//                this.setEtat(4);
//                break;
//
//            case DAF:
////                this.setEtat(5);
//                this.setEtat(11);
//                break;
//        }
//        if(u.getIdrole().compareToIgnoreCase("dg")==0)
            this.setEtat(11);
        return super.validerObject(u, c);
    }

    public TravauxCpl findTravaux(Connection c ) throws Exception {
        boolean estOuvert = false;
        try {
            if (c == null) {
                c = new UtilDB().GetConn();
                estOuvert = true;
            }

            TravauxCpl[] tvcpl = (TravauxCpl[]) CGenUtil.rechercher(new TravauxCpl(), null, null, c,"and id = '" + this.getIdObjet() + "'");
            if(tvcpl.length != 0 ) {
                return tvcpl[0];
            } else {
                return null;
            }

        } catch (Exception e) {
            throw e;
        } finally {
            if (estOuvert && c != null) {
                c.close();
            }
        }
    }

    public String getIdProvenance() {
        return idProvenance;
    }

    public void setIdProvenance(String idProvenance) {
        this.idProvenance = idProvenance;
    }

    public void validerObjectMultiple(String[] ids, Connection c, String u) throws Exception {
        boolean estOuvert = false;
        try{
            if (c == null)
            {
                c = new UtilDB().GetConn();
                estOuvert = true;
            }
            c.setAutoCommit(false);
            if (ids == null || ids.length == 0)
            {
                throw new Exception("Aucune demande d'achat s\u00E9lectionn\u00E9e.");
            }
            DmdAchat dmdAchat = (DmdAchat) new DmdAchat().getById(ids[0], "DMDACHAT", c);
            int firstEtat = dmdAchat.getEtat();
            for (int i = 0; i < ids.length; i++)
            {
                DmdAchat dmdAchatBoucle = (DmdAchat) new DmdAchat().getById(ids[i], "DMDACHAT", c);

                if (dmdAchatBoucle.getEtat() != firstEtat) {
                    throw new Exception(
                            "L'\u00E9tat de la demande d'achat " + dmdAchatBoucle.getId()
                                    + " n'est pas identique aux autres."
                    );
                }
                else{
                    MapUtilisateur[] utilisateurs = (MapUtilisateur[]) CGenUtil.rechercher(new MapUtilisateur(), null, null, c, " and refuser = "+u);
                    if (utilisateurs == null || utilisateurs.length == 0) {
                        throw new Exception("Utilisateur non trouvé.");
                    }
                    dmdAchatBoucle.validerObject(utilisateurs[0], c);
                }
            }
            c.commit();
        } catch (Exception e) {
            c.rollback();
            throw e;
        }
        finally {
            if (estOuvert) {
                c.close();
            }
        }
    }
}
