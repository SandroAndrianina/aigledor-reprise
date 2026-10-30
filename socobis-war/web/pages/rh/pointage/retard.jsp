<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<%@ page import="affichage.PageRecherche"%>
<%@ page import="affichage.Liste"%>
<%@ page import="pointage.PointageRetard"%>
<%@ page import="utilitaire.Utilitaire"%>
<%@ page import="rh.pointage.TypePointage" %>
<%@ page import="annexe.Point" %>

<%
    try {
        PointageRetard o = new PointageRetard();
        String[] listeCrt = {
                "personnel",
                "point_de_vente",
                "daty"
        };
        String[] listeInt = {
                "daty"
        };
        String[] libEntete = {
                "daty",
                "personnel",
                "point_de_vente",
                "heure_arrivee",
                "retard_minutes"
        };
        PageRecherche pr = new PageRecherche(
                o,
                request,
                listeCrt,
                listeInt,
                3,
                libEntete,
                libEntete.length
        );
        pr.setTitre("Liste des retards des personnels");
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setLien((String) session.getValue("lien"));
        pr.setApres("rh/pointage/retard.jsp");

        // Personnalisation des champs de recherche
        pr.getFormu().getChamp("personnel").setLibelle("Personnel");
        pr.getFormu().getChamp("daty1").setLibelle("Date Min");
        pr.getFormu().getChamp("daty1").setDefaut(Utilitaire.dateDuJour());

        pr.getFormu().getChamp("daty2").setLibelle("Date Max");
        pr.getFormu().getChamp("daty2").setDefaut(Utilitaire.dateDuJour());

        String[] colSomme = null;

        Liste[] liste = new Liste[1];
        Point liste1 = new Point();
        liste1.setNomTable("POINT");
        liste[0] = new Liste("point_de_vente", liste1, "val", "val");
        pr.getFormu().changerEnChamp(liste);
        pr.getFormu().getChamp("point_de_vente").setLibelle("Point de vente");

        pr.creerObjetPage(libEntete, colSomme);
        String[] libEnteteAffiche = {
            "Date",
            "Personnel",
            "Point de vente",
            "Heure d'arrivée",
            "Retard (minutes)"
        };

        pr.getTableau().setLibelleAffiche(libEnteteAffiche);

%>

<div class="content-wrapper">

    <section class="content-header">
        <h1><%= pr.getTitre() %></h1>
    </section>

    <section class="content">

        <form action="<%=pr.getLien()%>?but=<%=pr.getApres()%>" method="post">
            <%
                out.println(pr.getFormu().getHtmlEnsemble());
            %>
        </form>

        <%
            out.println(pr.getTableauRecap().getHtml());
        %>

        <br>

        <%
            out.println(pr.getTableau().getHtml());
            out.println(pr.getBasPage());
        %>

    </section>

</div>

<%
} catch (Exception e) {
    e.printStackTrace();
%>

<script language="JavaScript">
    alert('<%= e.getMessage() %>');
    history.back();
</script>

<%
    }
%>