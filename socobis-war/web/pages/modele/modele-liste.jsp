<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="affichage.PageRecherche"%>
<%@ page import="modele.ModeleLib" %>
<%@ page import="affichage.Liste"%>
<%@ page import="bean.TypeObjet" %>
<%@ page import="couleur.Couleur" %>

<% try{ 
    ModeleLib o = new ModeleLib();
    o.setNomTable("V_MODELE_LIB");
    String[] listeCrt = {"id","libelle","idcategorie","idsouscategorie","univers","gamme","statut","serie","collection","typologie", "provenance" , "idCouleur"};
    String[] listeInt = {};
    String[] libEntete = {"id","idCategorieLib","idSousCategorieLib","universLib","gammeLib","statutLib","matiereLib","serieLib","collectionLib","typologieLib","provenanceLib","nomCouleur","semelleLib"};
    PageRecherche pr = new PageRecherche(o, request, listeCrt, listeInt, 4, libEntete, libEntete.length);
    pr.setTitre("modele-liste");
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("modele/modele-liste.jsp");


    Liste[] liste = new Liste[12];
    TypeObjet liste0 = new TypeObjet();
    liste0.setNomTable("CATEGORIEMODELE");
    liste[0] = new Liste("idcategorie",liste0,"val","id");
    TypeObjet liste1 = new TypeObjet();
    liste1.setNomTable("SOUSCATEGORIEMODELE");
    liste[1] = new Liste("idsouscategorie",liste1,"val","id");
    TypeObjet liste2 = new TypeObjet();
    liste2.setNomTable("UNIVERS");
    liste[2] = new Liste("univers",liste2,"val","id");
    TypeObjet liste3 = new TypeObjet();
    liste3.setNomTable("GAMME");
    liste[3] = new Liste("gamme",liste3,"val","id");
    TypeObjet liste4 = new TypeObjet();
    liste4.setNomTable("STATUT");
    liste[4] = new Liste("statut",liste4,"val","id");
    TypeObjet liste5 = new TypeObjet();
    liste5.setNomTable("MATIERE");
    liste[5] = new Liste("matiere",liste5,"val","id");
    TypeObjet liste6 = new TypeObjet();
    liste6.setNomTable("SERIE");
    liste[6] = new Liste("serie",liste6,"val","id");
    TypeObjet liste7 = new TypeObjet();
    liste7.setNomTable("COLLECTION");
    liste[7] = new Liste("collection",liste7,"val","id");
    TypeObjet liste8 = new TypeObjet();
    liste8.setNomTable("TYPOLOGIE");
    liste[8] = new Liste("typologie",liste8,"val","id");
    TypeObjet liste9 = new TypeObjet();
    liste9.setNomTable("PROVENANCEMODELE");
    liste[9] = new Liste("provenance",liste9,"val","id");
    Couleur liste10 = new Couleur();
    liste10.setNomTable("V_REFERENCE_COULEUR");
    liste[10] = new Liste("idCouleur",liste10,"nomcouleur","referencecouleur");

    TypeObjet liste11 = new TypeObjet();
    liste11.setNomTable("semelle");
    liste[11] = new Liste("semelle",liste11,"val","id");

    pr.getFormu().changerEnChamp(liste);
    pr.getFormu().getChamp("id").setLibelle("Id");
    pr.getFormu().getChamp("libelle").setLibelle("Libell&eacute;");
    pr.getFormu().getChamp("idcategorie").setLibelle("Cat&eacute;gorie");
    pr.getFormu().getChamp("idsouscategorie").setLibelle("Sous-cat&eacute;gorie");
    pr.getFormu().getChamp("idCouleur").setLibelle("Couleur");

    String[] colSomme = null;
    pr.creerObjetPage(libEntete, colSomme);

    String[] libEnteteAffiche = {
            "Id",
            "Cat&eacute;gorie",
            "Sous-cat&eacute;gorie",
            "Univers",
            "Gamme",
            "Statut",
            "Mati&egrave;re",
            "S&eacute;rie",
            "Collection",
            "Typologie",
            "Provenance",
            "Couleur",
            "Semelle"
    };

    String[] lienTableau = {pr.getLien() + "?but=modele/modele-fiche.jsp"};
    String[] colonneLien = {"id"};
    String[] attributLien = {"id"};
    pr.getTableau().setLien(lienTableau);
    pr.getTableau().setColonneLien(colonneLien);
    pr.getTableau().setAttLien(attributLien);

    pr.getTableau().setLibelleAffiche(libEnteteAffiche);

%>

<div class="content-wrapper">
    <section class="content-header">
        <h1><%= pr.getTitre() %></h1>
    </section>
    <section class="content">
        <form action="<%=pr.getLien()%>?but=<%= pr.getApres() %>" method="post">
            <%
                out.println(pr.getFormu().getHtmlEnsemble());
            %>
        </form>
        <%
            out.println(pr.getTableauRecap().getHtml());
        %>
        <br>
        <%
            out.println(pr.getTableau().getHtml());
            out.println(pr.getBasPage());
        %>
    </section>
</div>

<% } catch (Exception e) {
  e.printStackTrace();
%>
<script language="JavaScript"> alert('<%=e.getMessage()%>');
    history.back();
</script>
<% }%>

