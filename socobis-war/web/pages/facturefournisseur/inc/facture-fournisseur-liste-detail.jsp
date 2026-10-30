
<%@page import="faturefournisseur.FactureFournisseurDetailsCpl"%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8;" %>
<%@ page import="user.*" %>
<%@ page import="bean.*" %>
<%@ page import="utilitaire.*" %>
<%@ page import="affichage.*" %>

<%
    try{
    FactureFournisseurDetailsCpl t = new FactureFournisseurDetailsCpl();

    String listeCrt[] = {};
    String listeInt[] = {};
    String libEntete[] = {"idProduit","libelleexacte","qte","pu","idPrevision","remise","tva","montant","montantht", "montantttc", "puavecfraisaccessoire", "montantavecfraisaccessoire"};
    PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3, libEntete, libEntete.length);
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    String id = request.getParameter("idOrigine");
    if (id == null || id.isEmpty()) {
        id = request.getParameter("id");
    }
    if(id != null){
        pr.setAWhere(" and idFactureFournisseur='"+id+"'");
    }
    String[] colSomme = null;
    pr.setNpp(100);
//    String idDevise = (String) request.getAttribute("idDevise");
//    if(idDevise==null) idDevise="Ar";
    pr.creerObjetPage(libEntete, colSomme);
        String idDevise = (String) request.getAttribute("idDevise");
        if (idDevise == null || idDevise.isEmpty()) {
            if (pr.getListe() != null && pr.getListe().length > 0) {
                idDevise = ((FactureFournisseurDetailsCpl)pr.getListe()[0]).getIdDevise();
            }
        }
        if (idDevise == null || idDevise.isEmpty()) idDevise = "AR";
%>

<div class="box-body">
    <%

        String [] lienTableau = {pr.getLien() + "?but=produits/as-ingredients-fiche.jsp",pr.getLien() + "?but=prevision/prevision-fiche.jsp"};
        String [] colonneLien = {"idProduit","idPrevision"};
        String [] colonneModal = {"idProduit","idPrevision"};
        String[] attributLien = {"id","id"};
        pr.getTableau().setLien(lienTableau);
        pr.getTableau().setColonneLien(colonneLien);
        pr.getTableau().setAttLien(attributLien);
        pr.getTableau().setModalOnClick(true);
        String libEnteteAffiche[] =  {"Ref. produit","Produit","Quantit&eacute;","Prix unitaire H.T","Pr&eacute;vision","Remise (%)","TVA (%)","Montant H.T ("+idDevise+")","Net H.T ("+idDevise+")", "Montant TTC ("+idDevise+")", "Montant avec Frais accessoire (AR)", "Montant TTC avec frais accessoire (AR)"};

        pr.getTableau().setColonneLien(colonneLien);

        pr.getTableau().setLibelleAffiche(libEnteteAffiche);
        if(pr.getTableau().getHtml() != null){
            out.println(pr.getTableau().getHtml());
    %>
    <div class="w-100" style="display: flex; flex-direction: row-reverse;">
        <table style="width: 25%" class="table">
            <tr>
                <td><b>Total H.T:</b></td>
                <td><b><%= utilitaire.Utilitaire.formaterAr(AdminGen.calculSommeDouble(pr.getListe(), "montant")) %> <%= idDevise %></b></td>
            </tr>
            <tr>
                <td><b>Montant Remise:</b></td>
                <td><b><%= utilitaire.Utilitaire.formaterAr(AdminGen.calculSommeDouble(pr.getListe(), "montantRemise")) %> <%= idDevise %></b></td>
            </tr>
            <tr>
                <td><b>Total Net H.T:</b></td>
                <td><b><%= utilitaire.Utilitaire.formaterAr(AdminGen.calculSommeDouble(pr.getListe(),"montantHT")) %> <%= idDevise %></b></td>
            </tr>
            <tr>
                <td><b>Total TVA:</b></td>
                <td><b><%= utilitaire.Utilitaire.formaterAr(AdminGen.calculSommeDouble(pr.getListe(),"montantTva")) %> <%= idDevise %></b></td>
            </tr>
            <tr>
                <td><b>Net &agrave; payer:</b></td>
                <td><b><%= utilitaire.Utilitaire.formaterAr(AdminGen.calculSommeDouble(pr.getListe(),"montantttc")) %> <%= idDevise %></b></td>
            </tr>
        </table>
    </div>
    <% }if(pr.getTableau().getHtml() == null){
               %><div style="text-align: center;"><h4>Aucune donnée trouvée</h4></div><%
         }
    %>
</div>
<%=pr.getModalHtml("modalContent")%>
<%
} catch (Exception e) {
    e.printStackTrace();
}%>


