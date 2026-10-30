package avoir;

public class AvoirAchatLib extends AvoirAchat {

    String idMagasinLib;
    String idFournisseurLib;

    public AvoirAchatLib() throws Exception {
        setNomTable("AVOIRACHATLIB");
    }

    public AvoirAchatLib(String nomtable) {
        super(nomtable);
    }

    public String getIdMagasinLib() {
        return idMagasinLib;
    }

    public void setIdMagasinLib(String idMagasinLib) {
        this.idMagasinLib = idMagasinLib;
    }

    public String getIdFournisseurLib() {
        return idFournisseurLib;
    }

    public void setIdFournisseurLib(String idFournisseurLib) {
        this.idFournisseurLib = idFournisseurLib;
    }
}
