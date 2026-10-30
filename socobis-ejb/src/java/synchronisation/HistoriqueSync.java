package synchronisation;

import bean.ClassEtat;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class HistoriqueSync extends ClassEtat {
    private String tablename ="";
    private String sequence="";

    private String id;
    private String action;
    private String heure;
    private Date daty;
    private String tableconcerne;
    private String requete;
    private String serveur ;

    public HistoriqueSync(String id, String action, String heure, Date daty, String tableconcerne, String requete, String serveur) {
        this.id = id;
        this.action = action;
        this.heure = heure;
        this.daty = daty;
        this.tableconcerne = tableconcerne;
        this.requete = requete;
        this.serveur = serveur;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public String getServeur() {
        return serveur;
    }

    public void setServeur(String serveur) {
        this.serveur = serveur;
    }


    @Override
    public String getTuppleID() {
        return id;
    }

    @Override
    public String getAttributIDName() {
        return "id";
    }

    public void construirePK(Connection connection) throws Exception {
        preparePk("HS", "getSeqHystoricSync");
        setId(makePK(connection));
    }

    public HistoriqueSync(){
        this.setNomTable("HISTORIQUE_SYNC");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTableconcerne() {
        return tableconcerne;
    }

    public void setTableconcerne(String tableconcerne) {
        this.tableconcerne = tableconcerne;
    }

    public String getRequete() {
        return requete;
    }

    public void setRequete(String requete) {
        this.requete = requete;
    }

    public boolean insert(Connection connection)throws Exception{
        String query = "INSERT INTO "+this.getTablename()+" (ID,ACTION,TABLECONCERNE,REQUETE,ETAT) VALUES ('"
                +this.getId()+"','"
                +this.getAction()+"','"
                +this.getTableconcerne()
                +"','"
                +this.getRequeteEscaped()
                +"',"
                +this.getEtat()+")";

        Statement statement = null;
        try{
            statement = connection.createStatement();
            statement.executeUpdate(query);
            //LogSynchro.println("INFO_QUERY: "+query);
            return true;
        }
        catch(Exception e){
            throw e;
        }
        finally{
            if(statement!=null)
                statement.close();
        }
    }

    public void updateEtat(Connection connection,String id)throws Exception{
        String query = "UPDATE "+this.getTablename()+" SET ETAT=11 WHERE id = '"+id+"' ";
        Statement statement = null;
        try{
            //LogSynchro.println("INFO_REQUEST: " + query);
            statement = connection.createStatement();
            statement.executeUpdate(query);
        }
        catch(Exception e){
            throw e;
        }
        finally{
            if(statement!=null)
                statement.close();
        }
    }

    public void updateEtat(Connection connection)throws Exception{
        updateEtat(connection,this.getId());
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public HistoriqueSync(String action, String heure, Date daty, String tableconcerne, String requete, String serveur) {
        this.action = action;
        this.heure = heure;
        this.daty = daty;
        this.tableconcerne = tableconcerne;
        this.requete = requete;
        this.serveur = serveur;
    }



    public HistoriqueSync(String id, String action, String tableconcerne, String requete,String etat) {
        this.setId(id);
        this.setAction( action);
        this.setTableconcerne(tableconcerne);
        this.setRequete(requete);
    }

    static ArrayList<HistoriqueSync> getAll(ResultSet rs) throws Exception {
        ArrayList<HistoriqueSync> result = new ArrayList<HistoriqueSync>();
        int count = 0;
        while(rs.next()){
            HistoriqueSync temp = new HistoriqueSync(rs.getString("id"),rs.getString("action"),rs.getString("tableconcerne"),rs.getString("requete"),rs.getString("etat"));
            //LogSynchro.print(sb, count==c);
            //LogSynchro.printProgress(System.currentTimeMillis(), c, count);
            //*/
            count++;
            result.add(temp);
        }
        LogSynchro.println("\n");
        //stmt.close();
        return result;
    }

    public String getRequeteEscaped(){
        return this.getRequete().replace("'", "\"");
    }

    public String getIdFromTous() throws Exception
    {
        if(this.getAction().compareToIgnoreCase("INSERT")==0)return getIdFromReqInsert();
        return "";
    }
    public boolean estValide() throws Exception
    {
        if(this.getRequete().startsWith("INSERT")||this.getRequete().startsWith("UPDATE")||this.getRequete().startsWith("DELETE"))
            return true;
        return false;
    }
    public void setActionTableMarina() throws Exception
    {
        this.setAction(this.getActionFromRek());
        this.setTableconcerne(this.getNomTableFromRek());
    }
    public String getNomTableFromRek() throws Exception
    {
        String actionV=this.getActionFromRek();
        String rek=this.getRequete();
        String split[]=rek.split(" ");
        if(rek.length()==0)return "";

        if(actionV.compareToIgnoreCase("INSERT")==0||actionV.compareToIgnoreCase("DELETE")==0)
        {
            return split[2];
        }
        if(actionV.compareToIgnoreCase("UPDATE")==0)
        {
            return split[1];
        }

        return "";
    }
    public String getActionFromRek() throws Exception
    {
        String rek=this.getRequete();
        String split[]=rek.split(" ");
        if(rek.length()==0)return "";
        return split[0];
    }
    public String getIdFromReqInsert()
    {
        String rek=this.getRequete();
        String[] split=rek.split("values");
        if(split.length==0)return "";
        char[] akavanana=split[1].toCharArray();
        int i=0;
        int debut=-1;
        int fin=0;
        int nbCote=1;
        while(i<akavanana.length)
        {
            if(akavanana[i]=='\''&&debut==-1)
            {
                debut=i;
            }
            if(debut>0&&i!=debut&&i!=debut+1&&akavanana[i]=='\'')
            {
                fin=i;
                break;
            }
            i++;
        }
        if(akavanana[debut+1]=='\'')nbCote++;
        return split[1].substring(debut+nbCote, fin);
    }
    public String getIdFromUpdate() throws Exception
    {
        if(this.getMode().compareToIgnoreCase("INSERT")==0)return "";
        String rek=this.getRequete();
        char[] finRek=rek.toCharArray();

        int debut=-1;
        int fin=finRek.length;
        int i=fin-1;
        int nbCote=1;
        if(finRek[i-1]=='\'')
        {
            nbCote=2;
        }
        fin=fin-nbCote-1;
        i=fin;
        while(i>0)
        {
            if(finRek[i]=='\'')
            {
                debut=i;
                break;
            }

            i--;
        }
        return rek.substring(debut+1, fin);
    }
}
