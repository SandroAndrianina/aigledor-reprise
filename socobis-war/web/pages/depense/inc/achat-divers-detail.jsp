<%--
  Created by IntelliJ IDEA.
  User: Tsinjoniaina
  Date: 27/05/2026
  Time: 16:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="affichage.PageRecherche"%>
<%@ page import="depense.AchatDiversFilleLib" %>
<%@ page import="bean.AdminGen" %>

<% try{
    AchatDiversFilleLib o = new AchatDiversFilleLib();
    o.setNomTable("AchatDiversFilleLib");
    String[] listeCrt = {};
    String[] listeInt = {};
    String[] libEntete = {"id","designation","quantite","pu"};
    PageRecherche pr = new PageRecherche(o, request, listeCrt, listeInt, 4, libEntete, libEntete.length);
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));

    pr.setAWhere(" and idMere = '" + request.getParameter("id")+"'");

    String[] colSomme = null;
    pr.creerObjetPage(libEntete, colSomme);

    String[] libEnteteAffiche = {"Ref.","D&eacute;signation","Quantit&eacute;","Prix unitaire"};
    pr.getTableau().setLibelleAffiche(libEnteteAffiche);
%>

<div class="box-body">
    <%  if(pr.getTableau().getHtml() != null) { %>
    <%= pr.getTableau().getHtml() %>
    <div class="w-100" style="display: flex; flex-direction: row-reverse;">
        <table style="width: 20%"class="table">
            <tr>
                <td><b>Total :</b></td>
                <td><b><%=utilitaire.Utilitaire.formaterAr(AdminGen.calculSommeDouble(pr.getListe(),"montant")) %> Ar</b></td>
            </tr>
        </table>
    </div>
    <% } else{ %>
    <center><h4>Aucune donn&eacute;e trouv&eacute;e</h4></center>
    <% } %>
</div>

<% } catch (Exception e) {
    e.printStackTrace();
%>
<script language="JavaScript"> alert('<%=e.getMessage()%>');
history.back();
</script>
<% }%>


