/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caisse;

import bean.CGenUtil;
import bean.ClassEtat;
import bean.ClassMAPTable;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import utilitaire.UtilDB;
import utilitaire.Utilitaire;
import utils.ConstanteAsync;

/**
 *
 * @author nouta
 */
public class ReportCaisse extends ClassEtat{
    private String id, remarque ,idCaisse ;
    private double montant ,montantTheorique;
    private Date daty;
    String heure;
    String typeReport;
    String idUser;
    String caisseLib;
    String idSession;
    String billetage;

    public String getBilletage() {
        return billetage;
    }

    public void setBilletage(String billetage) {
        this.billetage = billetage;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        if((this.getMode().compareToIgnoreCase("modif")==0||this.getMode().compareToIgnoreCase("insert")==0)&&(heure==null||heure.equals(""))){
            heure=Utilitaire.heureCouranteHMS();
        }
        this.heure = heure;
    }
    public ReportCaisse() {
        super.setNomTable("REPORTCAISSE");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

   
    public String getIdCaisse() {
        return idCaisse;
    }

    public void setIdCaisse(String idCaisse) {
        this.idCaisse = idCaisse;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public double getMontantTheorique() {
        return montantTheorique;
    }

    public void setMontantTheorique(double montantTheorique) {
        this.montantTheorique = montantTheorique;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
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
    public void construirePK(Connection c) throws Exception {
        this.preparePk("REC", "GETSEQREPORTCAISSE");
        this.setId(makePK(c));
    }
    
    protected void calculateMontantTheorique(Connection c) throws Exception{
        EtatCaisse et=new EtatCaisse();
        et.setId(this.getIdCaisse());
        EtatCaisse[] listetat=(EtatCaisse[]) CGenUtil.rechercher(et, null,null, c,"");
        double mt=0;
        if (listetat.length==1) {
            mt=listetat[0].getReste();
        }
        this.setMontantTheorique(mt);
    }
    
   @Override
    public ClassMAPTable createObject (String u, Connection c) throws Exception {
      this.calculateMontantTheorique(c);
      return super.createObject(u, c);
    }

    public  void genererMvtCaisse (String u, Connection c) throws Exception{
        MvtCaisse mvtcaisse = new MvtCaisse();
        boolean canClose=false;
        try{
            if(c==null){
                c=new UtilDB().GetConn();
                canClose=true;
            }
            double montantTheorique = this.getMontantTheorique();
            mvtcaisse.setIdReport(this.getId());
            mvtcaisse.setDesignation("Mouvement pour ecart du report "+this.getId());
            mvtcaisse.setIdDevise("AR");
            mvtcaisse.setIdOrigine(this.getId());
            mvtcaisse.setTaux(1);
            LocalDate localDate = this.getDaty().toLocalDate();
            LocalDate hier = localDate.minusDays(1);
            mvtcaisse.setDaty(Date.valueOf(hier));
            mvtcaisse.setIdCaisse(this.getIdCaisse());
            if(this.getMontant() > montantTheorique){
                mvtcaisse.setDebit(0);
                mvtcaisse.setCredit(this.getMontant()  - montantTheorique);
            }
            if(this.getMontant() < montantTheorique){
                mvtcaisse.setDebit(montantTheorique- this.getMontant());
                mvtcaisse.setCredit(0);
            }

            System.out.println(" create object ++++++++");
            mvtcaisse.createObject(u, c);
            mvtcaisse.validerObject(u, c);

        } catch(Exception e){
            throw e;
        } finally {
            if(canClose){
                c.close();
            }
        }

    }

    public EtatCaisse getEtatCaisse(Connection c, String nomTable) throws Exception{
            EtatCaisse caissse = new EtatCaisse();
            caissse.setIdCaisse(this.getIdCaisse());
            if(nomTable!= null && nomTable.equalsIgnoreCase("")) {
                caissse.setNomTable(nomTable);
            }
            EtatCaisse[] caisse = (EtatCaisse[]) CGenUtil.rechercher(caissse, null,null, c,"");
            return caisse[0];
    }

    @Override
    public Object validerObject(String u, Connection c) throws Exception {
        boolean estOuvert = false;
        try {
            if (c == null) {
            estOuvert = true;
                c = new UtilDB().GetConn();
                c.setAutoCommit(false);
            }
            EtatCaisse etatCaisse = this.getEtatCaisse(c,null);
            this.setMontantTheorique(etatCaisse.getReste());
            if(this.getMontant() != this.getMontantTheorique())
                this.genererMvtCaisse(u, c);
            this.setHeure(Utilitaire.heureCouranteHMS());
            if (ConstanteAsync.type_report_ouverture.equalsIgnoreCase(this.getTypeReport())) {
                Caisse caisse = new Caisse();
                caisse.setId(this.getIdCaisse());
                Caisse[] resultat = (Caisse[]) CGenUtil.rechercher(caisse, null, null, c, "");
                String idPoint = resultat[0].getIdPoint();
                CaisseSession recherche = new CaisseSession();
                recherche.setIdPoint(idPoint);
                recherche.setIdCaisse(this.getIdCaisse());
                Date dateOuverture = Utilitaire.dateDuJourSql();
                String condition =
                        " AND TRUNC(DATEOUVERTURE) = TO_DATE('" +
                                new java.text.SimpleDateFormat("yyyy-MM-dd").format(dateOuverture) +
                                "', 'YYYY-MM-DD')" +
                                " AND DATEFERMETURE IS NULL";
//                recherche.setDateFermeture(null);
                CaisseSession[] sessions =(CaisseSession[]) CGenUtil.rechercher(recherche, null, null, c, condition);
                if (sessions != null && sessions.length > 0) {
                    this.setIdSession(sessions[0].getId());
                } else {
                    CaisseSession session = new CaisseSession();
                    session.setIdPoint(idPoint);
                    session.setDateOuverture(Utilitaire.dateDuJourSql());
                    session.setHeureOuverture(Utilitaire.heureCouranteHMS());
                    session.setIdUserOuverture(u);
                    session.setIdCaisse(this.getIdCaisse());
                    session.createObject(u, c);
                    this.setIdSession(session.getId());
                }
            } else if(ConstanteAsync.type_report_fermeture.equalsIgnoreCase(this.getTypeReport())) {
                Caisse caisse = new Caisse();
                caisse.setId(this.getIdCaisse());
                Caisse[] resultat = (Caisse[]) CGenUtil.rechercher(caisse, null, null, c, "");
                String idPoint = resultat[0].getIdPoint();
                CaisseSession recherche = new CaisseSession();
                recherche.setIdPoint(idPoint);
                recherche.setIdCaisse(this.getIdCaisse());
//                recherche.setDateOuverture(Utilitaire.dateDuJourSql());
                CaisseSession[] sessions =(CaisseSession[]) CGenUtil.rechercher(recherche, null, null, c, "AND DATEFERMETURE IS NULL");
                if(sessions != null && sessions.length>0) {
                    sessions[0].setDateFermeture(Utilitaire.dateDuJourSql());
                    sessions[0].setHeureFermeture(Utilitaire.heureCouranteHMS());
                    sessions[0].setIdUserFermeture(u);
                    sessions[0].updateObject(u, c);
                }
            }
            super.validerObject(u, c);
            if(estOuvert==true)c.commit();
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

    public ReportCaisse[] genererCaisseAOuvrir(Caisse[] caisseCloture,Connection c) throws Exception {
        ReportCaisse[] fille = new ReportCaisse[caisseCloture.length];
        for(int i=0;i<caisseCloture.length;i++) {
            String idCaisseCloture=caisseCloture[i].getId();
            fille[i]=new ReportCaisse();
            fille[i].setIdCaisse(idCaisseCloture);
            fille[i].setMontantTheorique(this.getMontantTheoriqueFromEtatCaisse(idCaisseCloture,c));
            System.out.println(" this.getMontantTheorique +++++++ " + " caisse +++ " + idCaisseCloture+ " montant === " + this.getMontantTheoriqueFromEtatCaisse(idCaisseCloture, c));
            fille[i].setCaisseLib(caisseCloture[i].getVal());
            fille[i].setMontant(0);
            fille[i].setTypeReport(ConstanteAsync.type_report_ouverture);
        }
        return fille;
    }

    public ReportCaisse[] genererCaisseAFermer(Caisse[] caisseCloture,Connection c) throws Exception {
        ReportCaisse[] fille = new ReportCaisse[caisseCloture.length];
        for(int i=0;i<caisseCloture.length;i++) {
            String idCaisseCloture=caisseCloture[i].getId();
            fille[i]=new ReportCaisse();
            fille[i].setIdCaisse(idCaisseCloture);
            fille[i].setMontantTheorique(this.getMontantTheoriqueFromEtatCaisse(idCaisseCloture,c));
            System.out.println(" this.getMontantTheorique +++++++ " + " caisse +++ " + idCaisseCloture+ " montant === " + this.getMontantTheoriqueFromEtatCaisse(idCaisseCloture, c));
            fille[i].setCaisseLib(caisseCloture[i].getVal());
            fille[i].setMontant(0);
            fille[i].setTypeReport(ConstanteAsync.type_report_fermeture);
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



    public String getTypeReport() {
        return typeReport;
    }

    public void setTypeReport(String typeReport) {
        this.typeReport = typeReport;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getCaisseLib() {
        return caisseLib;
    }

    public void setCaisseLib(String caisseLib) {
        this.caisseLib = caisseLib;
    }

    public String getIdSession() {
        return idSession;
    }

    public void setIdSession(String idSession) {
        this.idSession = idSession;
    }

    public double getEcartCalc () throws Exception {
        return this.getMontant()-this.getMontantTheorique();
    }
}
