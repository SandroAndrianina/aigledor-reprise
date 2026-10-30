package affichage;

public class ChampMagasin extends Champ{
    public ChampMagasin()
    {
        this.setPageAppelComplete(this.getAc_classeMapping(),this.getAc_valeur(),this.getAc_nomTable());
    }
    public ChampMagasin(String nomChamp,Formulaire f) throws Exception
    {
        super(nomChamp);
        this.setFormulaire(f);
        if(f.isEstFille()==true)
        {
            affichage.Champ.setPageAppelComplete(f.getChampFille(nomChamp),"magasin.Magasin",nomChamp,"MAGASIN2");
        }
        else f.getChamp(nomChamp).setPageAppelComplete("magasin.Magasin",nomChamp,"MAGASIN2");
    }
}
