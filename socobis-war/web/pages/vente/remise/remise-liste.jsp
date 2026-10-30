<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="affichage.PageRecherche"%>
<%@ page import="remise.Remise" %>

<% try{ 
    Remise o = new Remise();
    o.setNomTable("REMISE");
    String[] listeCrt = {"id","daty","datefin","datedebut","nom"};
    String[] listeInt = {"daty"};
    String[] libEntete = {"id","nom","daty","datedebut","datefin"};
    PageRecherche pr = new PageRecherche(o, request, listeCrt, listeInt, 4, libEntete, libEntete.length);
    pr.setTitre("");
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("vente/remise/remise-liste.jsp");
    pr.getFormu().getChamp("id").setLibelle("Ref.");
    pr.getFormu().getChamp("daty1").setLibelle("Date de saisie min");
    String date = utilitaire.Utilitaire.formatterDaty(utilitaire.Utilitaire.ajoutJourDate(utilitaire.Utilitaire.dateDuJour(), -1));
    pr.getFormu().getChamp("daty1").setDefaut(date);
    pr.getFormu().getChamp("daty2").setLibelle("Date de saisie max");
    pr.getFormu().getChamp("datefin").setLibelle("Date fin");
    pr.getFormu().getChamp("datedebut").setLibelle("Date d&eacute;but");
    pr.getFormu().getChamp("nom").setLibelle("Nom");
    
    String[] colSomme = null;
    String[] ordre = {"id","nom","daty1","daty2","datedebut","datefin"};
    pr.getFormu().setOrdre(ordre);
    pr.creerObjetPage(libEntete, colSomme);

    String[] libEnteteAffiche = {"Ref.","Nom","Date de saisie","Date d&eacute;but","Date fin"};
    pr.getTableau().setLibelleAffiche(libEnteteAffiche);

    String lienTableau[] = {pr.getLien() + "?but=vente/remise/remise-fiche.jsp"};
    String colonneLien[] = {"id"};
    pr.getTableau().setLien(lienTableau);
    pr.getTableau().setColonneLien(colonneLien);

    pr.getFormu().setAnotherButton("" +
            "<a class=\"btn btn-primary pull-right btn-small\" href=\"module.jsp?but=vente/remise/remise-saisie.jsp&currentMenu=MENDYNA177883819268969\">\n" +
            "                    <i class=\"material-symbols-rounded\">add</i>Saisie d'une remise</a>"
    );

    pr.getTableau().setLienFille("vente/remise/inc/remise-detail.jsp&id=");
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

