<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="affichage.PageInsert"%>
<%@ page import="user.UserEJB" %>
<%@ page import="modele.Modele" %>
<%@ page import="affichage.Liste"%>
<%@ page import="bean.TypeObjet" %>
<%@ page import="couleur.Couleur" %>

<% try{ 
    UserEJB u = (user.UserEJB) session.getValue("u");
    String mapping = "modele.Modele";
    String nomTable = "MODELE";
    String apres = "modele/modele-fiche.jsp";

    Modele o = new Modele();
    o.setNomTable("MODELE");
    PageInsert pi = new PageInsert(o, request, u);
    pi.setLien((String) session.getValue("lien"));  
    pi.setTitre("modele-saisie");

    Liste[] liste = new Liste[11];
    TypeObjet liste0 = new TypeObjet();
    liste0.setNomTable("CATEGORIEMODELE");
    liste[0] = new Liste("idcategorie",liste0,"val","id");
    liste[0].setEstRadio(true);
    TypeObjet liste1 = new TypeObjet();
    liste1.setNomTable("SOUSCATEGORIEMODELE");
    liste[1] = new Liste("idsouscategorie",liste1,"val","id");
    TypeObjet liste2 = new TypeObjet();
    liste2.setNomTable("UNIVERS");
    liste[2] = new Liste("univers",liste2,"val","id");
    liste[2].setEstRadio(true);
    TypeObjet liste3 = new TypeObjet();
    liste3.setNomTable("GAMME");
    liste[3] = new Liste("gamme",liste3,"val","id");
    liste[3].setEstRadio(true);
    TypeObjet liste4 = new TypeObjet();
    liste4.setNomTable("STATUT");
    liste[4] = new Liste("statut",liste4,"val","id");
    liste[4].setEstRadio(true);
    TypeObjet liste5 = new TypeObjet();
    liste5.setNomTable("MATIERE");
    liste[5] = new Liste("matiere",liste5,"val","id");
    liste[5].setEstRadio(true);
    TypeObjet liste6 = new TypeObjet();
    liste6.setNomTable("SERIE");
    liste[6] = new Liste("serie",liste6,"val","id");
    liste[6].setEstRadio(true);
    TypeObjet liste7 = new TypeObjet();
    liste7.setNomTable("COLLECTION");
    liste[7] = new Liste("collection",liste7,"val","id");
    TypeObjet liste8 = new TypeObjet();
    liste8.setNomTable("TYPOLOGIE");
    liste[8] = new Liste("typologie",liste8,"val","id");
    TypeObjet liste9 = new TypeObjet();
    liste9.setNomTable("PROVENANCEMODELE");
    liste[9] = new Liste("provenance",liste9,"val","id");

    TypeObjet liste11 = new TypeObjet();
    liste11.setNomTable("semelle");
    liste[10] = new Liste("semelle",liste11,"val","id");
    liste[10].setEstRadio(true);

    pi.getFormu().changerEnChamp(liste);

    pi.getFormu().getChamp("libelle").setLibelle("Nom du modèle");
    pi.getFormu().getChamp("idcategorie").setLibelle("Catégorie");
    pi.getFormu().getChamp("idsouscategorie").setLibelle("Sous catégorie");
    pi.getFormu().getChamp("univers").setLibelle("Univers");
    pi.getFormu().getChamp("gamme").setLibelle("Gamme");
    pi.getFormu().getChamp("statut").setLibelle("Statut");
    pi.getFormu().getChamp("matiere").setLibelle("Matière");
    pi.getFormu().getChamp("serie").setLibelle("Série");
    pi.getFormu().getChamp("collection").setLibelle("Collection");
    pi.getFormu().getChamp("typologie").setLibelle("Typologie");
    pi.getFormu().getChamp("provenance").setLibelle("Provenance");
    pi.getFormu().getChamp("codecouleur").setLibelle("Codecouleur");
    pi.getFormu().getChamp("codecouleur").setVisible(false);
    pi.getFormu().getChamp("semelle").setLibelle("Semelle");
 
    String[] ordre = {
            "libelle","gamme",
            "collection","serie",
            "typologie","univers",
            "idcategorie","statut",
            "idsouscategorie","matiere",
            "provenance","semelle"
    };
    pi.getFormu().setOrdre(ordre);
 
    String acte = request.getParameter("acte");
    if(acte != null && acte.equalsIgnoreCase("update")){
        pi.setTitre("modele-modif");
    }

    pi.preparerDataFormu();
    pi.getFormu().makeHtmlInsertTabIndex();
%>

<div class="content-wrapper">
    <h1><%=pi.getTitre()%></h1>
    <form action="<%=pi.getLien()%>?but=apresTarif.jsp" method="post" name="<%=nomTable%>" id="<%=nomTable%>">
        <%
            out.println(pi.getFormu().getHtmlInsert());
            out.println(pi.getHtmlAddOnPopup());
        %>
        <input name="acte" type="hidden" id="nature" value="insert">
        <input name="bute" type="hidden" id="bute" value="<%=apres%>">
        <input name="classe" type="hidden" id="classe" value="<%=mapping%>">
        <input name="nomtable" type="hidden" id="nomtable" value="<%=nomTable%>">
    </form>
</div>

<% } catch (Exception e) {
  e.printStackTrace();
%>
<script language="JavaScript"> alert('<%=e.getMessage()%>');
    history.back();
</script>
<% }%>

