package caisse;

public class EtatCaisseMagasin extends EtatCaisse{
    private String idMagasin;
    private String idMagasinLib;

    public EtatCaisseMagasin() {
        setNomTable("V_ETAT_CAISSE_MAGASIN");
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
}
