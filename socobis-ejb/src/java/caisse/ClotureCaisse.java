package caisse;

import bean.CGenUtil;
import bean.ClassEtat;
import bean.ClassMere;

import bean.ClassMAPTable;
import java.sql.Date;
import java.sql.Connection;
import java.sql.SQLException;

public class ClotureCaisse extends ClassMere {

    private String id,remarque,idPoint, pointLib, explication, idCaisse;
    private Date daty;
    private int etat;
    private String idUser;
    //    double montantTheorique, montant;
    public ClotureCaisse()throws Exception {
        setLiaisonFille("idcloture");
        setNomClasseFille("caisser.ClotureCaisseFille");
        super.setNomTable("CLOTURECAISSE");
    }
    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("CCA", "getseqcloturecaisse");
        this.setId(makePK(c));
    }
    @Override
    public String getAttributIDName() {
        return "id";
    }
    @Override
    public String getTuppleID() {
        return id;
    }

    public double getMontantTheoriqueCaisse(String idCaisse) throws Exception {
        Connection c = null;
        double montantTheorique = 0;
        try {
            ClotureCaisse clotureCaisse = new ClotureCaisse();
            clotureCaisse.setNomTable("MONTANTCAISSE");
            clotureCaisse.setIdCaisse(idCaisse);
            ClotureCaisseFille[] fille = (ClotureCaisseFille[]) CGenUtil.rechercher(clotureCaisse, null, null, c, "");
            if (fille != null && fille.length>0) {
                return fille[0].getMontantTheorique();
            }
        } catch (Exception e) {
            e.printStackTrace();
            c.rollback();
            throw e;
        } finally {
            c.close();
        }
        return montantTheorique;
    }

    public ClotureCaisseFille[] getCaisseAvecMontantTheorique(Date date) throws Exception {
        Connection c = null;
        ClotureCaisseFille[] fille = null;
        try {
            ClotureCaisseFille clotureCaisseFille = new ClotureCaisseFille();
            clotureCaisseFille.setNomTable("MONTANTCAISSE");
            String apresWhere = "";
            if(date!=null) {
                apresWhere = "WHERE datedernierreport <= TO_DATE('" + date + "','YYYY-MM-DD')";
            }
            fille = (ClotureCaisseFille[]) CGenUtil.rechercher(clotureCaisseFille, null, null, c, apresWhere);
            System.out.println(" filles clotures " + fille.length);
            if (fille != null && fille.length>0) {
                return fille;
            }
        } catch (Exception e) {
            e.printStackTrace();
            c.rollback();
            throw e;
        } finally {
            if(c != null) c.close();
        }
        return fille;
    }
    public ClotureCaisseFille[] genererCaisseACloturer(Caisse[] caisseCloture,Connection c) throws Exception {
        ClotureCaisseFille[] fille = new ClotureCaisseFille[caisseCloture.length];
        for(int i=0;i<caisseCloture.length;i++) {
            String idCaisseCloture=caisseCloture[i].getId();
            fille[i]=new ClotureCaisseFille();
            fille[i].setIdCaisse(idCaisseCloture);
            fille[i].setMontantTheorique(this.getMontantTheoriqueFromEtatCaisse(idCaisseCloture,c));
            fille[i].setCaisseLib(caisseCloture[i].getVal());
            fille[i].setMontant(0);
        }
        return fille;
    }
    public double getMontantTheoriqueFromEtatCaisse(String idCaisse,Connection c)throws Exception{
        double montanttheorique = 0;
        EtatCaisse etatCaisse = new EtatCaisse();
        etatCaisse.setIdCaisse(idCaisse);
        EtatCaisse[] le = etatCaisse.getEtatCaisseByDate(null);
        System.out.println("le " + le.length);
        if(le.length>0){
            montanttheorique = le[0].getReste();
        }
        return montanttheorique;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    @Override
    public Object validerObject(String u, Connection c) throws Exception {
        ClotureCaisseFille tmp = new ClotureCaisseFille();
        tmp.setNomTable("CAISSECLOTURECPL");
        String apresWhere = " AND IDCLOTURE='"+this.getId()+"' AND DATY=TO_DATE('"+this.getDaty()+"','YYYY-MM-DD')";
        System.out.println("apresWhere: "+apresWhere);
        ClotureCaisseFille[] cloture = (ClotureCaisseFille[]) CGenUtil.rechercher(tmp, null, null, c, apresWhere);
        System.out.println("cloture fille " + cloture.length);
        if(cloture!=null && cloture.length>0) {
            for (int i = 0; i < cloture.length; i++) {
                this.checkClotureCaisseExistant(cloture[i].getIdCaisse(), cloture[i].getDaty(), c);
                ReportCaisse rep = new ReportCaisse();
                rep.setDaty(this.getDaty());
                rep.setIdCaisse(cloture[i].getIdCaisse());
                rep.setMontant(cloture[i].getMontant());
                rep.setRemarque("Report de caisse apr&eacute; cloturation le " + this.getDaty());
                rep.createObject(u, c);
                rep.validerObject(u, c);
            }
            return super.validerObject(u, c);
        }
        return null;
    }

    public void checkClotureCaisseExistant(String idCaisse, Date daty, Connection c) throws Exception {
        ClotureCaisseFille cloture = new ClotureCaisseFille();
        cloture.setNomTable("CAISSECLOTURECPL_VISEE");
        String apresWhere = " AND IDCAISSE='"+idCaisse+"' AND DATY=TO_DATE('"+daty+"','YYYY-MM-DD')";
        ClotureCaisseFille[] res = (ClotureCaisseFille[]) CGenUtil.rechercher(cloture, null, null, c, apresWhere);
        if(res != null && res.length>0) {
            throw new Exception("Vous ne pouvez pas effectuer une cl\\u00F4ture de la m\\u00EAme caisse pour la m\\u00EAme date.");
        }
    }

    public String getIdPoint() {
        return idPoint;
    }

    public void setIdPoint(String idPoint) {
        this.idPoint = idPoint;
    }

    public String getPointLib() {
        return pointLib;
    }

    public void setPointLib(String pointLib) {
        this.pointLib = pointLib;
    }

    public String getIdCaisse() {
        return idCaisse;
    }

    public void setIdCaisse(String idCaisse) {
        this.idCaisse = idCaisse;
    }

    public String getExplication() {
        return explication;
    }

    public void setExplication(String explication) {
        this.explication = explication;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

//    public double getMontantTheorique() {
//        return montantTheorique;
//    }
//
//    public void setMontantTheorique(double montantTheorique) {
//        this.montantTheorique = montantTheorique;
//    }
//
//    public double getMontant() {
//        return montant;
//    }
//
//    public void setMontant(double montant) {
//        this.montant = montant;
//    }
}
