
<%@page import="faturefournisseur.FactureFournisseurCpl"%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8;" %>
<%@ page import="user.*" %>
<%@ page import="bean.*" %>
<%@ page import="utilitaire.*" %>
<%@ page import="affichage.*" %>

<%
    try{
    FactureFournisseurCpl t = new FactureFournisseurCpl();
    t.setNomTable("FACTUREFOURNISSEURCPL_TOUS");
    String listeCrt[] = {};
    String listeInt[] = {};
    String libEntete[] = {"id", "idBc", "daty", "designation", "idFournisseurLib", "idDevise", "montantttc", "montantpaye", "montantreste", "dateEcheancePaiement", "idServiceLib", "etatlib"};
    PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3, libEntete, libEntete.length);
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    if(request.getParameter("id") != null){
        pr.setAWhere(" and idbc='"+request.getParameter("id")+"'");
    }
    String[] colSomme = null;
    pr.creerObjetPage(libEntete, colSomme);
%>

<div class="box-body">
    <%
        String libEnteteAffiche[] =  {"R&eacute;f. facture", "R&eacute;f. Bon de commande", "Date", "D&eacute;signation", "Fournisseur", "Devise", "Montant TTC", "Montant pay&eacute;", "Montant restant", "Date d'&eacute;ch&eacute;ance du paiement", "D&eacute;partement", "&Eacute;tat"};
        String lienTableau[] = {pr.getLien() + "?but=facturefournisseur/facturefournisseur-fiche.jsp", pr.getLien() + "?but=bondecommande/bondecommande-fiche.jsp"};
        String colonneLien[] = {"id","idBc"};
        String[] attLien = new String[] {"id", "id"};
        pr.getTableau().setLien(lienTableau);
        pr.getTableau().setColonneLien(colonneLien);
        pr.getTableau().setAttLien(attLien);
        pr.getTableau().setLienFille("facturefournisseur/inc/facture-fournisseur-liste-detail.jsp&id=");
        pr.getTableau().setLibelleAffiche(libEnteteAffiche);
        if(pr.getTableau().getHtml() != null){
            out.println(pr.getTableau().getHtml());
         }else
         {
               %><div style="text-align: center;"><h4>Aucune donnée trouvée</h4></div><%
         }

        
    %>
</div>
<%
} catch (Exception e) {
    e.printStackTrace();
}%>


