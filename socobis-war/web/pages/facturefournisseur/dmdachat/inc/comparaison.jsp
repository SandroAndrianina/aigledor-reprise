<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="affichage.PageRecherche"%>
<%@ page import="faturefournisseur.ComparaisonPrixAchatFilleLib" %>
<% try{ 
    ComparaisonPrixAchatFilleLib o = new ComparaisonPrixAchatFilleLib();
    String[] listeCrt = {};
    String[] listeInt = {};
    String[] libEntete = {"id","idFournisseurLib","delaiLivraison","pu","qteDisponible","echeanceFacture","modepaiementLib","remarque"};
    PageRecherche pr = new PageRecherche(o, request, listeCrt, listeInt, 4, libEntete, libEntete.length);
    pr.setUtilisateur((user.UserEJB) session.getAttribute("u"));
    pr.setLien((String) session.getAttribute("lien"));

    String id = request.getParameter("id");
    if (id != null && !id.isEmpty()) {
        pr.setAWhere(" AND IDMERE = '"+id+"'");
    }
    pr.creerObjetPage(libEntete, null);
    String[] libEnteteAffiche = {"Id","Fournisseur","D&eacute;lai de livraison (Jour)","Prix unitaire","Quantit&eacute; disponible","&Eacute;ch&eacute;ance de facture (Jour)","Mode de paiement","Remarque"};
    pr.getTableau().setLibelleAffiche(libEnteteAffiche);
    pr.getTableau().setNameBoutton("G&eacute;n&eacute;rer demande d'achat");
%>
<div class="box-body">
    <form action="<%= pr.getLien() + "?but=facturefournisseur/dmdachat/apresGenerer.jsp"%>" method="post" >
        <input type="hidden" name="acte">
        <input type="hidden" name="idMere" value="<%=id%>">
        <% if(pr.getTableau().getHtmlWithCheckbox() != null){
            out.println(pr.getTableau().getHtmlWithCheckbox());
        }else { %>
        <div style="text-align: center;"><h4>Aucune donn&eacute;e trouv&eacute;e</h4></div>
        <% } %>
    </form>
    <%
        out.println(pr.getBasPage());
    %>
</div>

<% } catch (Exception e) {
  e.printStackTrace();
%>
<script language="JavaScript"> alert('<%=e.getMessage()%>');
    history.back();
</script>
<% }%>

