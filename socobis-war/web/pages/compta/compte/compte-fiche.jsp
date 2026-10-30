<%@ page import="user.*" %>
<%@ page import="bean.*" %>
<%@ page import="utilitaire.*" %>
<%@ page import="affichage.*" %>
<%@ page import="mg.cnaps.compta.*" %>

<%
    try{
        ComptaCompte compte = new ComptaCompte();
        compte.setNomTable("COMPTA_COMPTE_LIBELLE");
        String[] libelleCompteFiche = {"Id", "Compte", "Libell&eacute;", "Type Compte","Classe","Journal"};
        PageConsulte pc = new PageConsulte(compte, request, (user.UserEJB) session.getValue("u"));
        pc.setLibAffichage(libelleCompteFiche);
        pc.setTitre("Fiche Compte");
        String lien = (String) session.getValue("lien");
%>

<div class="content-wrapper">
    <h1 class="box-title">
        <a href=<%= lien + "?but=compta/compte/compte-liste.jsp"%>> <i class="fa fa-arrow-circle-left"></i></a>
        <%=pc.getTitre()%>
    </h1>
    <div class="row m-0">
        <div class="col-md-6">
            <div class="box-fiche">
                <div class="box">
                    <div class="box-body">
                        <%= pc.getHtml() %>
                    </div>
                     <div class="box-footer">
                        <a  href="<%=(String) session.getValue("lien") + "?but=compta/compte/compte-saisie.jsp&acte=update&id=" +  request.getParameter("id")%>" class="btn btn-secondary pull-right">Modifier</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%
    } catch (Exception e) {
        e.printStackTrace();
    } %>