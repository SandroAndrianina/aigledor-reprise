<%@page import="mg.cnaps.compta.*"%>
<%@ page import="user.*" %>
<%@ page import="bean.*" %>
<%@ page import="utilitaire.*" %>
<%@ page import="affichage.*" %>
<%@ page import="faturefournisseur.DmdAchatFille" %>
<%@ page import="faturefournisseur.As_BonDeCommandeCpl" %>

<%
    try{
        As_BonDeCommandeCpl t = new As_BonDeCommandeCpl();
        t.setNomTable("AS_BONDECOMMANDE_MERECPL");
        String[] listeCrt = {};
        String[] listeInt = {};
        String libEntete[] = {"id","daty","designation","fournisseurlib","modepaiementlib","remarque","etatlib"};
        PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3, libEntete, libEntete.length);
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setAWhere(" AND idDmdAchat = '"+request.getParameter("id")+"'");
        pr.setLien((String) session.getValue("lien"));
        String[] colSomme = null;
        pr.setNpp(10);
        pr.creerObjetPage(libEntete, colSomme);

        String lienTableau[] = {pr.getLien() + "?but=bondecommande/bondecommande-fiche.jsp"};
        String colonneModal[] = {"id"};
        String[] colonneLien = {"id"};
        pr.getTableau().setLien(lienTableau);
        pr.getTableau().setModalOnClick(true,colonneModal);
        pr.getTableau().setColonneLien(colonneLien);
        pr.getTableau().setLienFille("bondecommande/inc/bondecommande-liste-detail.jsp&id=");
%>

<div class="box-body">
    <%
        String libEnteteAffiche[] = {"Ref. Bon de commande", "Date", "D&eacute;signation", "Fournisseur", "Mode de paiement", "Remarque", "&Eacute;tat"};
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