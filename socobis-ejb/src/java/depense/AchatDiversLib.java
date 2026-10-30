package depense;

import bean.AdminGen;
import bean.CGenUtil;

public class AchatDiversLib extends AchatDivers{
    private String fournisseurLib;
    private String etatLib;
    private String idcaisselib;
    private double montantTotal;
    private double surplusTotal;
    private double renduTotal;

    public String getIdcaisselib() {
        return idcaisselib;
    }

    public void setIdcaisselib(String idcaisselib) {
        this.idcaisselib = idcaisselib;
    }

    public AchatDiversLib() throws Exception {
        this.setNomTable("achatDiversLIB");
    }

    public String getFournisseurLib() {
        return fournisseurLib;
    }

    public void setFournisseurLib(String fournisseurLib) {
        this.fournisseurLib = fournisseurLib;
    }

    public String getEtatLib() {
        return etatLib;
    }

    public void setEtatLib(String etatLib) {
        this.etatLib = etatLib;
    }

    public double getMontantTotal() {
        if (montantTotal <= 0 && this.getId() != null) {
            try {
                AchatDiversFilleLib f = new AchatDiversFilleLib();
                f.setIdMere(this.getId());
                AchatDiversFilleLib[] filles = (AchatDiversFilleLib[]) CGenUtil.rechercher(f, null, null, null, "");
                montantTotal = AdminGen.calculSommeDouble(filles, "montant");
            } catch (Exception e) {}
        }
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public double getSurplusTotal() {
        return surplusTotal;
    }

    public void setSurplusTotal(double surplusTotal) {
        this.surplusTotal = surplusTotal;
    }

    public double getRenduTotal() {
        return renduTotal;
    }

    public void setRenduTotal(double renduTotal) {
        this.renduTotal = renduTotal;
    }
}
