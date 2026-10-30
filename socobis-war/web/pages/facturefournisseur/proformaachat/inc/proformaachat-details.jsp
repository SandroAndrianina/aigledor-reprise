<%@page import="mg.cnaps.compta.*"%>
<%@ page import="user.*" %>
<%@ page import="bean.*" %>
<%@ page import="utilitaire.*" %>
<%@ page import="affichage.*" %>
<%@ page import="faturefournisseur.ProformaAchatFilleLib" %>

<%
    try{
        ProformaAchatFilleLib t = new ProformaAchatFilleLib();
        t.setNomTable("PROFORMA_ACHAT_FILLELIB");
        String[] listeCrt = {};
        String[] listeInt = {};
        String[] libEntete = {"idProduit", "idProduitLib","designation","qteDisponible","idUniteLib","pu","delaisLivraison","remarque", "montant"};
        PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3, libEntete, libEntete.length);
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setAWhere(" AND idmere='"+request.getParameter("id")+"'");
        pr.setLien((String) session.getValue("lien"));
        String[] colSomme = null;
        pr.setNpp(100);
        pr.creerObjetPage(libEntete, colSomme);

        String[] lienTableau = {pr.getLien() + "?but=produits/as-ingredients-fiche.jsp"};
        String[] colonneLien = {"idproduit"};
        String[] attLien = {"id"};
        pr.getTableau().setLien(lienTableau);
        pr.getTableau().setColonneLien(colonneLien);
        pr.getTableau().setAttLien(attLien);
%>

<div class="box-body">
    <%
        String[] libEnteteAffiche =  {"Ref Produit","Produit","D&eacute;signation","Quantit&eacute; disponible","Unit&eacute;","Prix unitaire","D&eacute;lai (j)","Remarque", "Montant"};

        pr.getTableau().setLibelleAffiche(libEnteteAffiche);
        if(pr.getTableau().getHtml() != null){
            out.println(pr.getTableau().getHtml());
        }else
        {
    %><center><h4>Aucune donne trouv&eacute;e</h4></center><%
    }

%>
    <div class="w-100" style="display: flex; flex-direction: row-reverse;">
        <table style="width: 20%"class="table">
            <tr>
                <td><b>Montant Total:</b></td>
                <td><b><%= utilitaire.Utilitaire.formaterAr(AdminGen.calculSommeDouble(pr.getListe(),"montant")) %> AR</b></td>
            </tr>
        </table>
    </div>
</div>
<%=pr.getModalHtml("modalContent")%>
<%
    } catch (Exception e) {
        e.printStackTrace();
    }%>
