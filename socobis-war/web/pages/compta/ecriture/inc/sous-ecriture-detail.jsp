<%@page import="mg.cnaps.compta.*"%>
<%@ page import="user.*" %>
<%@ page import="bean.*" %>
<%@ page import="utilitaire.*" %>
<%@ page import="affichage.*" %>


<%
    try{
    ComptaSousEcritureLib t = new ComptaSousEcritureLib();
    t.setNomTable("COMPTA_SOUSECRITURE_LIB");
    String listeCrt[] = {};
    String listeInt[] = {};
    String libEntete[] =  {"id", "numero","journal", "compte","daty","remarque","libellePiece", "debit", "credit","compte_aux"};
    PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3, libEntete, libEntete.length);
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    String awhere="";
    if (request.getParameter("id") != null) {
        String id = request.getParameter("id");
        if (id.startsWith("ECR")) {
            awhere = " and idMere='" + id + "'";
        } else if (id.startsWith("LET")) {
            awhere = " and lettrage='" + id + "'";
        } else {
            awhere = " and idobjet='" + id + "'";
        }
    }
    pr.setAWhere(awhere);
    String[] colSomme = null;
    pr.setNpp(10);
    pr.creerObjetPage(libEntete, colSomme);
    int nombreLigne = pr.getTableau().getData().length;
%>

<div class="box-body">
    <%
         String libEnteteAffiche[] =  {"ID", "Intitul&eacute;","Journal",  "Compte","Date","Remarque","Libell&eacute;",  "D&eacute;bit", "Cr&eacute;dit","Compte Auxiliaire"};
        pr.getTableau().setLibelleAffiche(libEnteteAffiche);
        ComptaSousEcritureLib[] liste=(ComptaSousEcritureLib[]) pr.getTableau().getData();
        if(pr.getTableau().getHtml() != null){
            out.println(pr.getTableau().getHtml());
         }else
         {
               %><center><h4>Aucune donne trouvee</h4></center><%
         }

        
    %>
</div>
<%
} catch (Exception e) {
    e.printStackTrace();
}%>