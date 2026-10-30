<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="affichage.PageRecherche"%>
<%@ page import="produits.TarifIngredientsLib" %>
<%@ page import="affichage.Liste" %>
<%@ page import="bean.TypeObjet" %>

<% try{ 
    TarifIngredientsLib o = new TarifIngredientsLib();
    o.setNomTable("");
    String[] listeCrt = {"daty","idtypeclientlib","idingredientlib","unitelib","prixUnitaire"};
    String[] listeInt = {"daty","prixUnitaire"};
    String[] libEntete = {"id","idtypeclientlib","idingredientlib","prixUnitaire","unitelib","daty"};
    PageRecherche pr = new PageRecherche(o, request, listeCrt, listeInt, 4, libEntete, libEntete.length);
    pr.setTitre("");
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));

    Liste[] liste = new Liste[2];
    TypeObjet tc = new TypeObjet();
    tc.setNomTable("TYPECLIENT");
    liste[0] = new Liste("idtypeclientlib",tc,"val","val");

    TypeObjet unite = new TypeObjet();
    unite.setNomTable("UNITE_VENTE");
    liste[1] = new Liste("unitelib",unite,"val","val");

    pr.getFormu().changerEnChamp(liste);

    pr.setApres("vente/tarifIngredients/tarifIngredients-liste.jsp");
    pr.getFormu().getChamp("idtypeclientlib").setLibelle("Type client");
    pr.getFormu().getChamp("idingredientlib").setLibelle("Produit");
    pr.getFormu().getChamp("unitelib").setLibelle("Unit&eacute;");
    pr.getFormu().getChamp("daty1").setLibelle("Date min");
    String date = utilitaire.Utilitaire.formatterDaty(utilitaire.Utilitaire.ajoutJourDate(utilitaire.Utilitaire.dateDuJour(), -1));
    pr.getFormu().getChamp("daty1").setDefaut(date);
    pr.getFormu().getChamp("daty2").setLibelle("Date max");
    pr.getFormu().getChamp("prixUnitaire1").setLibelle("Prix unitaire min");
    pr.getFormu().getChamp("prixUnitaire2").setLibelle("Prix unitaire max");

    pr.getFormu().setAnotherButton(
            "<a class='btn btn-primary pull-right btn-small' href='module.jsp?but=vente/tarifIngredients/tarifIngredients-saisie.jsp'>" +
                    "<i class='material-symbols-rounded'>add</i>Saisir un tarif produit</a>"
    );

    String[] colSomme = null;
    pr.creerObjetPage(libEntete, colSomme);
    String lienTableau[] = {pr.getLien() + "?but=vente/tarifIngredients/tarifIngredients-fiche.jsp"};
    String colonneLien[] = {"id"};
    pr.getTableau().setLien(lienTableau);
    pr.getTableau().setColonneLien(colonneLien);
    String[] libEnteteAffiche = {"Ref.","Type client","Produit","Prix unitaire","Unit&eacute;","Date"};
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

