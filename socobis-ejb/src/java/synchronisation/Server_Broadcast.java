package synchronisation;

import java.sql.*;
import java.util.ArrayList;

public class Server_Broadcast {
    private static String DEFAULT_DRIVER        ="oracle.jdbc.driver.OracleDriver";
    private static String DEFAULT_UTILISATEUR   ="apj";
    private static String DEFAULT_MDP           ="apj";

    private String id, nom, adresse_ip, utilisateur, mdp, driver, service_name, port;

    public static ArrayList<Server_Broadcast> getAll(ResultSet rs) throws Exception{
        ArrayList<Server_Broadcast> result = new ArrayList<Server_Broadcast>();
        while(rs.next()){
            Server_Broadcast temp = new Server_Broadcast(rs.getString("ID"), rs.getString("NOM"), rs.getString("ADRESSE_IP"), rs.getString("UTILISATEUR"), rs.getString("MDP"), rs.getString("DRIVER"), rs.getString("SERVICE_NAME"), rs.getString("PORT"));
            result.add(temp);
        }
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse_ip() {
        return adresse_ip;
    }

    public void setAdresse_ip(String adresse_ip) {
        this.adresse_ip = adresse_ip;
    }

    public String getUtilisateur() {
        if(this.utilisateur.compareTo("")!=0)
            return utilisateur;
        return Server_Broadcast.DEFAULT_UTILISATEUR;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Server_Broadcast(String id, String nom, String adresse_ip, String utilisateur, String mdp, String driver, String service_name, String port) {
        this.id = id;
        this.nom = nom;
        this.adresse_ip = adresse_ip;
        this.utilisateur = utilisateur;
        this.mdp = mdp;
        this.driver = driver;
        this.service_name = service_name;
        this.port = port;
    }

    private static Driver createDriver(String urlDriver) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
        Class<?> driverClass = Class.forName(urlDriver);
        Driver driverResult = (Driver) driverClass.newInstance();
        return driverResult;
    }

    private static Connection createConnectionUserMode(String urlDriver, String urlDatabase, String login, String password) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        DriverManager.registerDriver(createDriver(urlDriver));
        Connection result = DriverManager.getConnection(urlDatabase, login, password);
        return result;
    }

    public String getMdp() {
        if(this.mdp.compareTo("")!=0)
            return mdp;
        return Server_Broadcast.DEFAULT_MDP;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }


    public String getUrldriver(){
        if(this.getDriver().compareTo("")!=0)
            return this.getDriver();
        return Server_Broadcast.DEFAULT_DRIVER;
    }

    public String getUrlDatabase(){
        return "jdbc:oracle:thin:@"+this.getAdresse_ip()+":"+this.getService_name();
    }

    public Connection connect()throws Exception{
        return Server_Broadcast.createConnectionUserMode(this.getUrldriver(), this.getUrlDatabase(),this.getUtilisateur() , this.getMdp());
    }

    public Connection GetConn()throws Exception{
        return SynchroBase.GetConn(this.getAdresse_ip()+":"+this.getPort()+":"+this.getService_name(), this.getUtilisateur(), this.getMdp());
    }

    @Override
    public String toString() {
        return getNom();
    }
}
