<%@page import="affichage.PageRecherche"%>
<%@ page import="faturefournisseur.DmdAchatLib" %>
<% try{
    DmdAchatLib o = new DmdAchatLib();
    o.setNomTable("DMDACHATLIB_TRAITE");
    String[] listeCrt = {};
    String[] listeInt = {};
    String[] libEntete = {"id", "daty","fournisseurlib","idMagasinLib","idCategorieLib","remarque","etatlib", "traite"};
    PageRecherche pr = new PageRecherche(o, request, listeCrt, listeInt,4, libEntete, libEntete.length);
    pr.setUtilisateur((user.UserEJB) session.getAttribute("u"));
    pr.setLien((String) session.getAttribute("lien"));
    String id = request.getParameter("id");
    if (id != null && !id.isEmpty()) {
        pr.setAWhere(" AND IDOBJET = '"+id+"'");
    }
    pr.creerObjetPage(libEntete, null);
    String[] lienTableau = {pr.getLien() + "?but=facturefournisseur/dmdachat/dmdachat-fiche.jsp"};
    String[] colonneLien = {"id"};
    pr.getTableau().setLien(lienTableau);
    pr.getTableau().setLienFille("facturefournisseur/dmdachat/inc/dmdachat-details.jsp&id=");
    pr.getTableau().setColonneLien(colonneLien);

    String[] libEnteteAffiche = {"Ref", "Date","Fournisseur","Magasin","Cat&eacute;gorie","Remarque","&Eacute;tat", "Commande"};
    pr.getTableau().setLibelleAffiche(libEnteteAffiche);
%>
<div class="box-body">
    <%  if(pr.getTableau().getHtml() != null){
            out.println(pr.getTableau().getHtml());
        } else{ %>
            <h4>Aucune donn&eacute;e trouv&eacute;e</h4>
    <%  } %>
</div>

<% } catch (Exception e) {
  e.printStackTrace();
%>
<script language="JavaScript"> alert('<%=e.getMessage()%>');
    history.back();
</script>
<% }%>

