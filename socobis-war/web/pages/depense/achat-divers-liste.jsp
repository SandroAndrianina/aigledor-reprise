<%--
  Created by IntelliJ IDEA.
  User: Tsinjoniaina
  Date: 27/05/2026
  Time: 15:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="affichage.PageRecherche"%>
<%@ page import="depense.AchatDiversLib" %>
<%@ page import="utilitaire.Utilitaire" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<% try{
    AchatDiversLib o = new AchatDiversLib();
    o.setNomTable("achatDiversLIB");
    String[] listeCrt = {"id","daty","designation","fournisseurLib"};
    String[] listeInt = {"daty"};
    String[] libEntete = {"id","daty","designation","fournisseurLib","etatLib"};
    PageRecherche pr = new PageRecherche(o, request, listeCrt, listeInt, 4, libEntete, libEntete.length);
    if(request.getParameter("etat")!=null && request.getParameter("etat").compareToIgnoreCase("")!=0) {
        pr.setAWhere(" and etat=" + request.getParameter("etat"));
    }
    pr.setTitre("Liste des achats divers");
    pr.setUtilisateur((user.UserEJB) session.getAttribute("u"));
    pr.setLien((String) session.getAttribute("lien"));
    pr.setApres("depense/achat-divers-liste.jsp");

    pr.getFormu().getChamp("id").setLibelle("Ref.");
    pr.getFormu().getChamp("fournisseurLib").setLibelle("Fournisseur");
    pr.getFormu().getChamp("designation").setLibelle("D&eacute;signation");
    pr.getFormu().getChamp("daty1").setLibelle("Date min");
    pr.getFormu().getChamp("daty2").setLibelle("Date max");
    String date = Utilitaire.formatterDaty(Utilitaire.ajoutJourDate(Utilitaire.dateDuJour(), -1));
    pr.getFormu().getChamp("daty1").setDefaut(date);
    pr.getFormu().getChamp("daty2").setDefaut(Utilitaire.dateDuJour());

    pr.creerObjetPage(libEntete, null);

    Map<String,String> lienTab=new HashMap();
    String[] id = {"daty1"};
    String[] val = {pr.getFormu().getChamp("daty1").getValeur()};
    String classe = "depense.AchatDivers";
    lienTab.put("viser",pr.getLien() + "?but=apresTarif.jsp&acte=valider&bute=depense/achat-divers-liste.jsp&classe=" + classe  + "&rajoutLien="+ Utilitaire.ajouterParam(id, val) );
    lienTab.put("dupliquer",pr.getLien() + "?but=depense/achat-divers-saisie.jsp&acte=dupliquer");
    pr.getTableau().setLienClicDroite(lienTab);

    String[] lienTableau = {pr.getLien() + "?but=depense/achat-divers-fiche.jsp"};
    String[] colonneLien = {"id"};
    String[] attributLien = {"id"};
    pr.getTableau().setLien(lienTableau);
    pr.getTableau().setColonneLien(colonneLien);
    pr.getTableau().setAttLien(attributLien);

    String[] libEnteteAffiche = {"Ref.","Date","D&eacute;signation","Fournisseur","&Eacute;tat"};
    pr.getTableau().setLibelleAffiche(libEnteteAffiche);

    pr.getFormu().setAnotherButton(
            "                <a class=\"btn btn-primary pull-right  btn-small\" href=\"module.jsp?but=depense/achat-divers-saisie.jsp\">\n" +
                    "                    <i class=\"material-symbols-rounded\">add</i> Saisir une dépense diverse\n" +
                    "                </a>"
    );

    String[] etatVal = {"","1","11","0"};
    String[] etatAff = {"Tous", "Cr&eacute;&eacute;e(s)","Vis&eacute;e(s)","Annul&eacute;e(s)"};
    pr.getTableau().setLienFille("depense/inc/achat-divers-detail.jsp&id=");
%>
<script>
    function submitEtat() {
        document.depense.submit();
    }
</script>
<div class="content-wrapper">
    <section class="content-header">
        <h1><%= pr.getTitre() %></h1>
    </section>
    <section class="content">
        <form action="<%=pr.getLien()%>?but=<%= pr.getApres() %>" method="post"  name="depense" id="depense">
            <%
                out.println(pr.getFormu().getHtmlEnsemble());
            %>
            <div class="row col-md-12 nopadding" style="margin-top: 12px">
                <div class="col-md-2 nopadding">
                    <label class="input-label" for="etat">&Eacute;tat :</label>
                    <select name="etat" class="champ form-control" id="etat" onchange="submitEtat()">
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


