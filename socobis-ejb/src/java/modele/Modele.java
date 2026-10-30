package modele;

import bean.ClassMAPTable;
import java.sql.Connection;

public class Modele extends ClassMAPTable {
    private String id;
    private String libelle;
    private String idcategorie;
    private String idsouscategorie;
    private String univers;
    private String gamme;
    private String statut;
    private String matiere;
    private String serie;
    private String collection;
    private String typologie;
    private String provenance;
    private String codecouleur;
    private String semelle;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getIdcategorie() {
        return idcategorie;
    }

    public void setIdcategorie(String idcategorie) {
        this.idcategorie = idcategorie;
    }

    public String getIdsouscategorie() {
        return idsouscategorie;
    }

    public void setIdsouscategorie(String idsouscategorie) {
        this.idsouscategorie = idsouscategorie;
    }

    public String getUnivers() {
        return univers;
    }

    public void setUnivers(String univers) {
        this.univers = univers;
    }

    public String getGamme() {
        return gamme;
    }

    public void setGamme(String gamme) {
        this.gamme = gamme;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getMatiere() {
        return matiere;
    }

    public void setMatiere(String matiere) {
        this.matiere = matiere;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getTypologie() {
        return typologie;
    }

    public void setTypologie(String typologie) {
        this.typologie = typologie;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getCodecouleur() {
        return codecouleur;
    }

    public void setCodecouleur(String codecouleur) {
        this.codecouleur = codecouleur;
    }

    public String getSemelle() {
        return semelle;
    }

    public void setSemelle(String semelle) {
        this.semelle = semelle;
    }



    public Modele() throws Exception {
        this.setNomTable("MODELE");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("MOD","GETSEQ_MODELE");
        this.setId(makePK(c));
    }

    @Override
    public String getTuppleID() {
        return id;
    }

    @Override
    public String getAttributIDName() {
        return "id";
    }

    @Override
    public String[] getMotCles() {
        String[] motCles={"id","libelle"};
        return motCles;
    }
}

