/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faturefournisseur;

import bean.AdminGen;
import bean.TypeObjet;
import caisse.Caisse;

/**
 *
 * @author nouta
 */
public class ModePaiement extends TypeObjet{

    public ModePaiement() {
        super.setNomTable("MODEPAIEMENT");
    }

    public static ModePaiement getModePaiementByNom(ModePaiement[] liste, String nom) throws Exception
    {
        if(liste==null || liste.length==0) return null;
        String[] col={"val"};
        String[] val={nom};
        ModePaiement [] lc=(ModePaiement[]) AdminGen.find(liste,col,val);
        if(lc==null || lc.length==0) return null;
        return lc[0];
    }
    
}
