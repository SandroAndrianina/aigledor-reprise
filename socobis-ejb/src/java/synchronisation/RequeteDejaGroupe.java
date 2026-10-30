package synchronisation;

public class RequeteDejaGroupe {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdReq() {
        return idReq;
    }

    public void setIdReq(String idReq) {
        this.idReq = idReq;
    }

    public String getIdGroupe() {
        return idGroupe;
    }

    public void setIdGroupe(String idGroupe) {
        this.idGroupe = idGroupe;
    }

    String id,idReq,idGroupe;
    public RequeteDejaGroupe(String r,String g)
    {
        this.setIdReq(r);
        this.setIdGroupe(g);
    }

}
