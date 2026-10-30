<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="affichage.PageRecherche"%>
<%@ page import="faturefournisseur.ProformaAchatLib" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="utilitaire.Utilitaire" %>

<% try{ 
    ProformaAchatLib o = new ProformaAchatLib();
    o.setNomTable("PROFORMA_ACHAT_LIB");
    String[] listeCrt = {"id","designation","daty","idFournisseurLib"};
    String[] listeInt = {"daty"};
    String[] libEntete = {"id","daty","idFournisseurLib","designation","etatlib"};

    String[] etatVal = {"","1", "11", "0"};
    String[] etatAff = {"Tous","Cr&eacute;&eacute;e(s)",  "Vis&eacute;e(s)", "Annul&eacute;e(s)"};

    PageRecherche pr = new PageRecherche(o, request, listeCrt, listeInt, 4, libEntete, libEntete.length);
    pr.setTitre("Liste des Proformas");
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("facturefournisseur/proformaachat/proformaachat-liste.jsp");

    if(request.getParameter("etat")!=null && request.getParameter("etat").compareToIgnoreCase("")!=0) {
        pr.setAWhere(" and etat=" + request.getParameter("etat"));
    }

    pr.getFormu().getChamp("id").setLibelle("Ref");
    pr.getFormu().getChamp("designation").setLibelle("D&eacute;signation");
    pr.getFormu().getChamp("daty1").setLibelle("Date min");
//    pr.getFormu().getChamp("daty1").setDefaut(Utilitaire.dateDuJour());
    String date = Utilitaire.formatterDaty(Utilitaire.ajoutJourDate(Utilitaire.dateDuJour(), -1));
    pr.getFormu().getChamp("daty1").setDefaut(date);
    pr.getFormu().getChamp("daty2").setLibelle("Date max");
    pr.getFormu().getChamp("daty2").setDefaut(Utilitaire.dateDuJour());
    pr.getFormu().getChamp("idFournisseurLib").setLibelle("Fournisseur");
    
    String[] colSomme = null;
    pr.creerObjetPage(libEntete, colSomme);
    Map<String,String> lienTab=new HashMap();
    String[] id = {"daty1"};
    String[] val = {pr.getFormu().getChamp("daty1").getValeur()};
    String classe = "faturefournisseur.ProformaAchat";
    lienTab.put("viser",pr.getLien() + "?but=apresTarif.jsp&acte=valider&bute=facturefournisseur/proformaachat/proformaachat-liste.jsp&classe=" + classe+"&rajoutLien="+ Utilitaire.ajouterParam(id, val) );
    lienTab.put("dupliquer",pr.getLien() + "?but=facturefournisseur/proformaachat/proformaachat-saisie.jsp&acte=dupliquer");
    pr.getTableau().setLienClicDroite(lienTab);
    String[] libEnteteAffiche = {"Ref","Date","Fournisseur","D&eacute;signation","&Eacute;tat"};
    pr.getTableau().setLibelleAffiche(libEnteteAffiche);
    String lienTableau[] = {pr.getLien() + "?but=facturefournisseur/proformaachat/proformaachat-fiche.jsp"};
    String colonneLien[] = {"id"};
    pr.getTableau().setLien(lienTableau);
    pr.getTableau().setColonneLien(colonneLien);
    pr.getTableau().setLibelleAffiche(libEnteteAffiche);
    pr.getTableau().setLienFille("facturefournisseur/proformaachat/inc/proformaachat-details.jsp&id=");
//    pr.getFormu().setAnotherButton(
//            "<a class=\"btn btn-primary pull-right btn-small\" href=\"module.jsp?but=facturefournisseur/proformaachat/proformaachat-saisie.jsp\">\n" +
//                    "                    <i class=\"material-symbols-rounded\">add</i>Saisie d'une proforma</a>"
//    );

%>

<script>
    function changerDesignation() {
        document.getElementById("proforma-form").submit();
    }
</script>

<div class="content-wrapper">
    <section class="content-header">
        <h1><%= pr.getTitre() %></h1>
    </section>
    <section class="content">
        <form id="proforma-form" action="<%=pr.getLien()%>?but=<%= pr.getApres() %>" method="post">
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

