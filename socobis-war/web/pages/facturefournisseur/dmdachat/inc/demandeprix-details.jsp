<%@page import="mg.cnaps.compta.*"%>
<%@ page import="user.*" %>
<%@ page import="bean.*" %>
<%@ page import="utilitaire.*" %>
<%@ page import="affichage.*" %>
<%@ page import="faturefournisseur.DemandePrixFilleLib" %>

<%
    try{
        DemandePrixFilleLib t = new DemandePrixFilleLib();
        t.setNomTable("DEMANDE_PRIX_FILLELIB");
        String[] listeCrt = {};
        String[] listeInt = {};
        String[] libEntete = {"idproduit","idProduitLib","designation","quantite","idUniteLib"};
        PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3, libEntete, libEntete.length);
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setAWhere(" AND idmere='"+request.getParameter("id")+"'");
        pr.setLien((String) session.getValue("lien"));
        String[] colSomme = null;
        pr.setNpp(100);
        pr.creerObjetPage(libEntete, colSomme);

        String[] lienTableau = {pr.getLien() + "?but=produits/as-ingredients-fiche.jsp"};
        String colonneModal[] = {"idproduit"};
        String[] colonneLien = {"idproduit"};
        String [] attributLien = {"id"};
        pr.getTableau().setAttLien(attributLien);
        pr.getTableau().setLien(lienTableau);
        pr.getTableau().setModalOnClick(true);
        pr.getTableau().setColonneLien(colonneLien);
%>

<div class="box-body">
    <%
        String[] libEnteteAffiche =  {"Ref. produit","Produit","D&eacute;signation","Quantit&eacute;","Unit&eacute;"};
        pr.getTableau().setLibelleAffiche(libEnteteAffiche);
        if(pr.getTableau().getHtml() != null){
            out.println(pr.getTableau().getHtml());
        }else
        {
    %><center><h4>Aucune donne trouv&eacute;e</h4></center><%
    }

%>
</div>
<%=pr.getModalHtml("modalContent")%>
<%
    } catch (Exception e) {
        e.printStackTrace();
    }%>
