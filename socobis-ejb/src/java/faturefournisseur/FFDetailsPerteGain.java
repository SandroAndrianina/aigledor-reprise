package faturefournisseur;
import java.sql.Connection;

public class FFDetailsPerteGain extends FactureFournisseurDetails{
    private String libelleProduit;
    public FFDetailsPerteGain() throws Exception{
        super();
    }

    @Override
    public void controlerUpdate(Connection c){
    }

    public String getLibelleProduit() {
        return libelleProduit;
    }

    public void setLibelleProduit(String libelleProduit) {
        this.libelleProduit = libelleProduit;
    }
}
