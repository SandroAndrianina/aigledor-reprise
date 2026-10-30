package faturefournisseur;

import bean.CGenUtil;
import bean.ClassMere;
import utilitaire.UtilDB;
import utilitaire.Utilitaire;
import utils.ConstanteAsync;

import java.sql.Connection;
import java.sql.Date;
import java.util.*;

public class ProformaAchat extends ClassMere {

    private String id;
    private String designation;
    private Date daty;
    private String idFournisseur;
    private String idDemandePrix;

    public ProformaAchat() throws Exception  {
        setNomTable("PROFORMA_ACHAT");
        setLiaisonFille("idMere");
        this.setNomClasseFille("faturefournisseur.ProformaAchatFille");
    }

    public void construirePK(Connection c) throws Exception {
        this.preparePk("PROFA", "GET_SEQ_PROFORMA_ACHAT");
        this.setId(makePK(c));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public String getIdFournisseur() {
        return idFournisseur;
    }

    public void setIdFournisseur(String idFournisseur) throws Exception {
        if (this.getMode().equalsIgnoreCase("modif")) {
            if (idFournisseur != null && idFournisseur.equals("")){
                throw new Exception("Fournisseur Obligatoire");
            }
        }
        this.idFournisseur = idFournisseur;
    }

    public String getIdDemandePrix() {
        return idDemandePrix;
    }

    public void setIdDemandePrix(String idDemandePrix) {
        this.idDemandePrix = idDemandePrix;
    }

    @Override
    public String getTuppleID() {
        return id;
    }

    @Override
    public String getAttributIDName() {
        return "id";
    }


    public static void genererDmdAchatParFournisseur(String u, HashMap<String, String> map, Connection c) throws Exception {
        boolean canClose = false;
        try {
            if (c == null) {
                c = new UtilDB().GetConn();
                canClose = true;
            }
            String aWhere = " and id in (" + Utilitaire.tabToString(map.keySet().toArray(new String[0]), "'", ",") + ")";
            ProformaAchatFilleLib pf = new ProformaAchatFilleLib();
            ProformaAchatFilleLib[] filles = (ProformaAchatFilleLib[]) CGenUtil.rechercher(pf, null, null, c, aWhere);

            Map<String, List<ProformaAchatFilleLib>> parFournisseur = new LinkedHashMap<String, List<ProformaAchatFilleLib>>();
            for (ProformaAchatFilleLib fille : filles) {
                List<ProformaAchatFilleLib> lignes = parFournisseur.get(fille.getIdFournisseur());
                if (lignes == null) {
                    lignes = new ArrayList<ProformaAchatFilleLib>();
                    parFournisseur.put(fille.getIdFournisseur(), lignes);
                }
                lignes.add(fille);
            }

            String maxJour = Utilitaire.getMaxColonneMultiCritere(pf.getNomTable(), "delaisLivraison", " where 1<2 "+aWhere);
            Date dateJour = Utilitaire.dateDuJourSql();
            for (Map.Entry<String, List<ProformaAchatFilleLib>> entry : parFournisseur.entrySet()) {
                List<ProformaAchatFilleLib> lignes = entry.getValue();

                DmdAchat dmdAchat = new DmdAchat();
                dmdAchat.setFournisseur(entry.getKey());
                dmdAchat.setDaty(dateJour);
                if (maxJour == null || !Utilitaire.estNumerique(maxJour)){
                    maxJour = "0";
                }
                dmdAchat.setDateLimite(Utilitaire.ajoutJourDate(dateJour, Utilitaire.stringToInt(maxJour)));
                dmdAchat.setIdCategorie(ConstanteAsync.CATEGORIE_AUTRES);
                dmdAchat.setIdMagasin(ConstanteAsync.MAGASIN_ATELIER);
                dmdAchat.setIdObjet(lignes.get(0).getIdDemandePrix());

                DmdAchatFille[] dmdAchatFilles = new DmdAchatFille[lignes.size()];
                double qte = 0;
                for (int i = 0; i < lignes.size(); i++) {
                    ProformaAchatFilleLib ligne = lignes.get(i);

                    if (map.get(ligne.getId()) == null || map.get(ligne.getId()).equals("")){
                        throw new Exception("Quantit\u00E9 demand\u00E9 obligatoire");
                    }
                    qte = Utilitaire.stringToDouble(map.get(ligne.getId()));

                    DmdAchatFille dmdAchatFille = new DmdAchatFille();
                    dmdAchatFille.setIdproduit(ligne.getIdProduit());
                    dmdAchatFille.setDesignation(ligne.getDesignation());
                    dmdAchatFille.setQuantite(qte);
                    dmdAchatFille.setPu(ligne.getPu());
                    dmdAchatFilles[i] = dmdAchatFille;
                }
                dmdAchat.setFille(dmdAchatFilles);
                dmdAchat.createObject(u, c);
            }
        } catch (Exception e) {
            if (canClose) c.rollback();
            throw e;
        } finally {
            if (canClose) c.close();
        }
    }
}
