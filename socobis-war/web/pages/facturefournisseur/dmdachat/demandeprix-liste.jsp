<%--
  Created by IntelliJ IDEA.
  User: mihary
  Date: 21/09/2026
  Time: 15:01
--%>

<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="affichage.PageRecherche"%>
<%@ page import="utilitaire.Utilitaire" %>
<%@ page import="faturefournisseur.DemandePrixLib" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>

<% try{ 
    DemandePrixLib o = new DemandePrixLib();
    String[] listeCrt = {"id","daty","designation", "acheteur", };
    String[] listeInt = {"daty"};
    String[] libEntete = {"id", "daty","designation", "acheteur",  "etatLib"};

    String[] etatVal = {"","1", "11", "0"};
    String[] etatAff = {"Tous","Cr&eacute;&eacute;e(s)",  "Vis&eacute;e(s)", "Annul&eacute;e(s)"};

    PageRecherche pr = new PageRecherche(o, request, listeCrt, listeInt, 4, libEntete, libEntete.length);
    pr.setTitre("Liste des Demandes de Prix");
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("facturefournisseur/dmdachat/demandeprix-liste.jsp");

    if(request.getParameter("etat")!=null && request.getParameter("etat").compareToIgnoreCase("")!=0) {
        pr.setAWhere(" and etat=" + request.getParameter("etat"));
    }

    pr.getFormu().getChamp("id").setLibelle("Ref.");
    pr.getFormu().getChamp("designation").setLibelle("D&eacute;signation");
    pr.getFormu().getChamp("daty1").setLibelle("Date Min");
    String date = Utilitaire.formatterDaty(Utilitaire.ajoutJourDate(Utilitaire.dateDuJour(), -1));
    pr.getFormu().getChamp("daty1").setDefaut(date);
    pr.getFormu().getChamp("daty2").setLibelle("Date Max");
    pr.getFormu().getChamp("daty2").setDefaut(Utilitaire.dateDuJour());

    pr.setNpp(25);

    String[] colSomme = null;
    pr.creerObjetPage(libEntete, colSomme);
    Map<String,String> lienTab=new HashMap();
    String[] id = {"daty1"};
    String[] val = {pr.getFormu().getChamp("daty1").getValeur()};
    String classe = "faturefournisseur.DemandePrix";
    lienTab.put("viser",pr.getLien() + "?but=apresTarif.jsp&acte=valider&bute=facturefournisseur/dmdachat/demandeprix-liste.jsp&classe=" + classe+"&rajoutLien="+ Utilitaire.ajouterParam(id, val) );
    lienTab.put("dupliquer",pr.getLien() + "?but=facturefournisseur/dmdachat/demandeprix-saisie.jsp&acte=dupliquer");
    pr.getTableau().setLienClicDroite(lienTab);

    String[] lienTableau = {pr.getLien() + "?but=facturefournisseur/dmdachat/demandeprix-fiche.jsp"};
    String[] colonneLien = {"id"};
    pr.getTableau().setLien(lienTableau);
    pr.getTableau().setLienFille("facturefournisseur/dmdachat/inc/demandeprix-details.jsp&id=");
    pr.getTableau().setColonneLien(colonneLien);

    String[] libEnteteAffiche = {"Ref.", "Date","D&eacute;signation", "Acheteur",  "&Eacute;tat"};
    pr.getTableau().setLibelleAffiche(libEnteteAffiche);
    pr.getFormu().setAnotherButton("<a class=\"btn btn-primary pull-right btn-small\" href=\"module.jsp?but=facturefournisseur/dmdachat/demandeprix-saisie.jsp\">\n" +
            "                    <i class=\"material-symbols-rounded\">add</i>Saisie d'une demande de prix</a>"
    );
%>

<script>
    function changerDesignation() {
        document.getElementById("dmd-prix-form").submit();
    }
</script>

<div class="content-wrapper">
    <section class="content-header">
        <h1><%= pr.getTitre() %></h1>
    </section>
    <section class="content">
        <form action="<%=pr.getLien()%>?but=<%= pr.getApres() %>" id="dmd-prix-form" method="post">
            <%
                out.println(pr.getFormu().getHtmlEnsemble());
            %>
            <div class="row col-md-12 noppadding">
                <div class="col-md-2 noppadding">
                    &Eacute;tat :
                    <select name="etat" class="champ form-control" id="etat" onchange="changerDesignation()">
                        <%
                            for( int i = 0; i < etatAff.length; i++ ){ %>
                        <% if(request.getParameter("etat") !=null && request.getParameter("etat").compareToIgnoreCase(etatVal[i]) == 0) {%>
                        <option value="<%= etatVal[i] %>" selected> <%= etatAff[i] %> </option>
                        <% } else { %>
                        <option value="<%= etatVal[i] %>"> <%= etatAff[i] %> </option>
                        <% } %>
                        <%    }
                        %>
                    </select>
                </div>
            </div>
            <br>
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

