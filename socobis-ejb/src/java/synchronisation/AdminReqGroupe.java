package synchronisation;

import java.util.ArrayList;

public class AdminReqGroupe {
    ArrayList<RequeteAenvoyerGroupe> listeGroupe = new ArrayList<RequeteAenvoyerGroupe>();
    ArrayList<RequeteDejaGroupe> listeMarqGroupe=new ArrayList<RequeteDejaGroupe>();
    int nbReqParGroupe=300;

    public int getNbReqParGroupe() {
        return nbReqParGroupe;
    }

    public void setNbReqParGroupe(int nbReqParGroupe) {
        this.nbReqParGroupe = nbReqParGroupe;
    }

    public ArrayList<RequeteAenvoyerGroupe> getListeGroupe() {
        return listeGroupe;
    }

    public void setListeGroupe(ArrayList<RequeteAenvoyerGroupe> listeGroupe) {
        this.listeGroupe = listeGroupe;
    }
    public AdminReqGroupe(ArrayList<RequeteAEnvoyer> tRequeteAEnvoyer) throws Exception
    {
        ArrayList<RequeteAEnvoyer> groupe=new ArrayList<RequeteAEnvoyer>();
        int nbReqDansGroupe=0;
        int compteur=0;
        for(RequeteAEnvoyer req:tRequeteAEnvoyer)
        {
            groupe.add(req);
            nbReqDansGroupe++;
            if(nbReqDansGroupe==this.getNbReqParGroupe()||compteur==tRequeteAEnvoyer.size()-1)
            {
                listeGroupe.add(new RequeteAenvoyerGroupe(groupe));
                nbReqDansGroupe=0;
                groupe=new ArrayList<RequeteAEnvoyer>();
            }
            compteur++;
        }

    }


}
