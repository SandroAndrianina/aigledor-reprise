<%@page import="faturefournisseur.As_BonDeCommande_Fille_CPL"%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8;" %>
<%@ page import="user.*" %>
<%@ page import="bean.*" %>
<%@ page import="utilitaire.*" %>
<%@ page import="affichage.*" %>

<%
    try{
    As_BonDeCommande_Fille_CPL t = new As_BonDeCommande_Fille_CPL();
    t.setNomTable("AS_BONDECOMMANDE_CPL_RECEPT");
    String listeCrt[] = {};
    String listeInt[] = {};
    String libEntete[] = {"produit","produitlib","unitelib","quantite","qtelivrer","qtefacturer","pu","remise","montant","montantremise","montantTva","montantTTC","tauxDeChange","montantTTCArCalc"};
    PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3, libEntete, libEntete.length);
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    if(request.getParameter("id") != null){
        pr.setAWhere(" and idbc='"+request.getParameter("id")+"'");
    }
    String[] colSomme = null;
    pr.creerObjetPage(libEntete, colSomme);

    String idDevise = (String) request.getAttribute("idDevise");
    if (idDevise == null || idDevise.isEmpty()) {
        if (pr.getListe() != null && pr.getListe().length > 0) {
            idDevise = ((As_BonDeCommande_Fille_CPL)pr.getListe()[0]).getIdDevise();
        }
    }
    if (idDevise == null || idDevise.isEmpty()) idDevise = "AR";
%>

<div class="box-body">
    <%
        String [] lienTableau = {pr.getLien() + "?but=produits/as-ingredients-fiche.jsp"};
        String [] colonneLien = {"produit"};
        String[] attributLien = {"id"};
        pr.getTableau().setLien(lienTableau);
        pr.getTableau().setColonneLien(colonneLien);
        pr.getTableau().setAttLien(attributLien);
        String libEnteteAffiche[] =   {"Ref. produit","produit","unit&eacute;","Quantit&eacute;","Quantit&eacute; re&ccedil;ue", "Quantit&eacute; factur&eacute;e","Prix unitaire H.T","Remise (%)","Montant H.T ("+idDevise+")","Net H.T  ("+idDevise+")","Montant TVA  ("+idDevise+")","Montant TTC  ("+idDevise+")","Taux de change","Montant TTC (AR)"};
        pr.getTableau().setLibelleAffiche(libEnteteAffiche);
        if(pr.getTableau().getHtml() != null){
            out.println(pr.getTableau().getHtml());
    %>
    <div class="w-100" style="display: flex; flex-direction: row-reverse;">
        <table style="width: 25%" class="table">
            <tr>
                <td><b>Total H.T:</b></td>
                <td><b><%= Utilitaire.formaterAr(AdminGen.calculSommeDouble(pr.getListe(),"montant")) %> <%= idDevise %></b></td>
            </tr>
            <tr>
                <td><b>Montant Remise:</b></td>
                <td><b><%= Utilitaire.formaterAr(AdminGen.calculSommeDouble(pr.getListe(),"montantdelaremise")) %> <%= idDevise %></b></td>
            </tr>
            <tr>
                <td><b>Total Net H.T:</b></td>
                <td><b><%= Utilitaire.formaterAr(AdminGen.calculSommeDouble(pr.getListe(),"montantremise")) %> <%= idDevise %></b></td>
            </tr>
            <tr>
                <td><b>Total TVA:</b></td>
                <td><b><%= Utilitaire.formaterAr(AdminGen.calculSommeDouble(pr.getListe(),"montantTVA")) %> <%= idDevise %></b></td>
            </tr>
            <tr>
                <td><b>Net &agrave; payer:</b></td>
                <td><b><%= Utilitaire.formaterAr(AdminGen.calculSommeDouble(pr.getListe(),"montantTTC")) %> <%= idDevise %></b></td>
            </tr>
        </table>
    </div>
    <% }if(pr.getTableau().getHtml() == null){
                %><div style="text-align: center;"><h4>Aucune donnée trouvée</h4></div><%
    }
    %>  
</div>
<%
} catch (Exception e) {
    e.printStackTrace();
}%>