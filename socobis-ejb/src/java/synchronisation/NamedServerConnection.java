package synchronisation;

import java.sql.Connection;

public class NamedServerConnection extends Server_Broadcast {
    private Connection connection;

    public NamedServerConnection(String id, String nom, String adresse_ip, String utilisateur, String mdp, String driver, String service_name, String port){
        super(id,nom,adresse_ip,utilisateur,mdp, driver, service_name, port);
    }

    public NamedServerConnection(Server_Broadcast server){
        super(server.getId(), server.getNom(), server.getAdresse_ip(), server.getUtilisateur(), server.getMdp(), server.getDriver(), server.getService_name(), server.getPort());
    }

    public void setConnection(Connection connection){
        this.connection = connection;
    }

    public Connection getConnection(){
        return this.connection;
    }
}
