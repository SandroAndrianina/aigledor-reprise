package caisse;
import java.sql.Connection;
import java.sql.Statement;

import bean.*;
import mg.cnaps.compta.ecriture.ComptaEcritureFille;
import paiement.LiaisonPaiement;
import utils.ConstanteAsync;
import vente.FactureCF;
import vente.Vente;

public class MouvementCaisseMere extends ClassMere{
    String id,designation,idmodepaiement,idtiers,idorigine,idtierslib,etatlib;
    double credit,debit;
    java.sql.Date daty;
    Vente vente;

    public Vente getVente() {
        return vente;
    }

    public void setVente(Vente vente) {
        this.vente = vente;
    }

    public MouvementCaisseMere() throws Exception {
        this.setNomTable("mouvementcaissemere");
        setLiaisonFille("idmvtcaissemere");
        setNomClasseFille("caisse.MouvementCaisseFille");
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("MVTM", "GETSEQMOUVEMENTCAISSEMERE");
        this.setId(makePK(c));
    }
    @Override
    public String getTuppleID() {
        return id;
    }

    public  String getNomClasseFille()
    {
        return "caisse.MouvementCaisseFille";
    }
    public String getLiaisonFille() {
        return "idmvtcaissemere";
    }
    @Override
    public String getAttributIDName() {
        return "id";
    }
    public String getDesignation() {
        return designation;
    }
    public void setDesignation(String designation) {
        this.designation = designation;
    }
    public String getIdmodepaiement() {
        return idmodepaiement;
    }
    public void setIdmodepaiement(String idmodepaiement) {
        this.idmodepaiement = idmodepaiement;
    }
    public String getIdtiers() {
        return idtiers;
    }
    public void setIdtiers(String idtiers) {
        this.idtiers = idtiers;
    }
    public String getIdorigine() {
        return idorigine;
    }
    public void setIdorigine(String idorigine) {
        this.idorigine = idorigine;
    }
    public String getIdtierslib() {
        return idtierslib;
    }
    public void setIdtierslib(String idtierslib) {
        this.idtierslib = idtierslib;
    }
    public String getEtatlib() {
        return etatlib;
    }
    public void setEtatlib(String etatlib) {
        this.etatlib = etatlib;
    }
    public double getCredit() {
        return credit;
    }
    public void setCredit(double credit) {
        this.credit = credit;
    }
    public double getDebit() {
        return debit;
    }
    public void setDebit(double debit) {
        this.debit = debit;
    }
    public java.sql.Date getDaty() {
        return daty;
    }
    public void setDaty(java.sql.Date daty) {
        this.daty = daty;
    }

    @Override
    public ClassMAPTable createObject(String u, Connection c) throws Exception {
        MouvementCaisseFille [] filles = (MouvementCaisseFille[])this.getFille();
        for(MouvementCaisseFille f : filles){
            f.setIdTiers(this.getIdtiers());
            f.setDaty(this.getDaty());
            f.setIdOrigine(this.getIdorigine());
            if(f.getIdCaisse()==null || f.getIdCaisse().compareToIgnoreCase("")==0) {
                f.setIdCaisse(ConstanteAsync.CAISSE_DEFAUT);
            }
            TypeObjet crd = new TypeObjet();
            crd.setNomTable("modepaiementmaj");
            TypeObjet[] ret = (TypeObjet[]) CGenUtil.rechercher(crd, null, null, c, " and id = '"+f.getIdModePaiement()+"'");
            if(ret.length > 0) f.setDesignation(ret[0].getVal());
        }
        MouvementCaisseMere m = (MouvementCaisseMere) super.createObject(u, c);
        this.createliaisonPayement(u, c);
        return m;
    }

    @Override
    public ClassMAPTable createObjectBatch(String u, Statement st) throws Exception {
        MouvementCaisseFille[] filles = (MouvementCaisseFille[]) this.getFille();
        for (MouvementCaisseFille f : filles)
        {
            f.setIdTiers(this.getIdtiers());
            f.setDaty(this.getDaty());
            f.setIdOrigine(this.getIdorigine());
            if(f.getIdCaisse()==null || f.getIdCaisse().compareToIgnoreCase("")==0) {
                f.setIdCaisse(ConstanteAsync.CAISSE_DEFAUT);
            }
            f.setTaux(1);
            TypeObjet crd = new TypeObjet();
            crd.setNomTable("modepaiementmaj");
            TypeObjet[] ret = (TypeObjet[]) CGenUtil.rechercher(crd, null, null, st.getConnection(), " and id = '"+f.getIdModePaiement()+"'");
            if(ret != null && ret.length > 0) {
                String currentDesination = f.getDesignation();

                if (currentDesination != null && !currentDesination.trim().isEmpty())
                {
                    f.setDesignation(currentDesination + " " + ret[0].getVal());
                }
                else {
                    f.setDesignation(ret[0].getVal());
                }

            }
        }

        this.setFille(filles);
        MouvementCaisseMere createdMvtCaisse = (MouvementCaisseMere) super.createObjectBatch(u, st);
        this.createliaisonPayementBatch(u, st);

        return createdMvtCaisse;
    }

    @Override
    public Object validerObject(String u, Connection c) throws Exception {
        MouvementCaisseFille [] filles = (MouvementCaisseFille[])this.getFille("mouvementcaisse", c, "");
        for(MouvementCaisseFille f : filles){
            f.validerSimple(u, c);
        }
        return super.validerObject(u, c);
    }

    /**
     * Valide le mouvement de caisse parent et l'ensemble de ses lignes de caisse filles en batch JDBC.
     *
     * @param u  Identifiant de l'utilisateur
     * @param st Statement JDBC pour l'accumulation en batch
     * @return L'objet MouvementCaisseMere validé
     * @throws Exception en cas d'erreur de validation SQL
     */
    @Override
    public Object validerObjectBatch(String u, Statement st) throws Exception {
        ClassFille[] filles = this.getFille();
        if (filles == null || filles.length == 0) {
            Connection c = st.getConnection();
            filles = this.getFille("mouvementcaisse", c, "");
        }
        if (filles != null) {
            for (ClassFille f : filles) {
                if (f instanceof MouvementCaisseFille) {
                    ((MouvementCaisseFille) f).validerSimpleBatch(u, st);
                }
            }
        }

        return super.validerObjectBatch(u, st);
    }

    public void createliaisonPayement(String u, Connection c) throws Exception {
        ClassFille[] filles = this.getFille();
        double total_mvt_caisse = 0.0;

        for (int i = 0; i < filles.length; i++) {
            MouvementCaisseFille fille = (MouvementCaisseFille) filles[i];
            total_mvt_caisse += fille.getCredit();
        }
        double ratio = 1.0;

        if (total_mvt_caisse > this.getCredit() && total_mvt_caisse > 0) {
            ratio = this.getCredit() / total_mvt_caisse;
        }
        
        //this.setFille((MouvementCaisseFille[]) filles);
        
        for (int i = 0; i < filles.length; i++) {
            MouvementCaisseFille fille = (MouvementCaisseFille) filles[i];
            LiaisonPaiement p = new LiaisonPaiement();
            p.setId1(fille.getId());
            p.setId2(this.getIdorigine());
            p.setMontant(fille.getCredit() * ratio);
            p.createObject(u, c);
            p.validerObject(u, c);
        }
    }

    /**
     * Crée et valide en batch JDBC les liaisons de paiement ({@code LiaisonPaiement})
     * reliant chaque ligne de caisse au document d'origine (la vente).
     * <p>
     * Calcule le ratio de prorata en cas de multi-paiement pour ajuster exactement les montants liés.
     * </p>
     *
     * @param u    Identifiant de l'utilisateur
     * @param stmt Statement JDBC pour l'accumulation en batch
     * @throws Exception en cas d'erreur de création ou de validation
     */
    public void createliaisonPayementBatch(String u, Statement stmt) throws Exception {
        ClassFille[] filles = this.getFille();
        if (filles == null || filles.length == 0) return;
        double total_mvt_caisse = 0.0;

        for (int i = 0; i < filles.length; i++) {
            MouvementCaisseFille fille = (MouvementCaisseFille) filles[i];
            total_mvt_caisse += fille.getCredit();
        }
        double ratio = 1.0;

        if (total_mvt_caisse > this.getCredit() && total_mvt_caisse > 0) {
            ratio = this.getCredit() / total_mvt_caisse;
        }

        //this.setFille((MouvementCaisseFille[]) filles);

        for (int i = 0; i < filles.length; i++) {
            MouvementCaisseFille fille = (MouvementCaisseFille) filles[i];
            LiaisonPaiement p = new LiaisonPaiement();
            p.setId1(fille.getId());
            p.setId2(this.getIdorigine());
            double maxMontant = this.getVente() != null ? this.getVente().getMontantTTCCalc() : this.getCredit();
            p.setMontant(Math.min(maxMontant, fille.getCredit()) * ratio);
            p.createObjectBatch(u, stmt);
            p.validerObjectBatch(u, stmt);
        }

        System.out.println("CREATED BATCH");
        System.out.println("VALIDATED BATCH");
    }
    
}
