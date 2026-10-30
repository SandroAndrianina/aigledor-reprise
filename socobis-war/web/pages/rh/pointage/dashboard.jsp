<%@ page import="user.UserEJB" %>
<%@ page import="affichage.*" %>
<%@ page import="paie.formation.dashboard.*" %>
<%@ page import="constante.ConstanteDashboard" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="bean.ResultatEtSomme" %>
<%@ page import="rh.pointage.PointageKPIPresence" %>
<%@ page import="java.sql.Date" %>
<%@ page import="utilitaire.Utilitaire" %>
<%@ page import="pointage.PointageKPIAbsence" %>
<%@ page import="pointage.PointageRetard" %>
<%@ page import="fabrication.Of" %>
<%@ page import="rh.pointage.PointageRetardPoint" %>
<%@ page import="rh.pointage.PointageDuJour" %>

<script src="${pageContext.request.contextPath}/chartPlugins/Chart.min.js"></script>

<%
    try {
        String lien = (String) session.getAttribute("lien");
        UserEJB u = (user.UserEJB) session.getAttribute("u");
        String[] paramVide = {};
        String backgroundColor = ConstanteDashboard.DEFAULT_GRAPH_BACKGROUND_COLOR;

        // Nouvelle palette de couleurs personnalisée
        String[] palettes = {"#4e73df", "#1cc88a", "#36b9cc", "#f6c23e", "#e74a3b"};

        String datyjour = Utilitaire.dateDuJour();

        // 1. Initialisation de toutes les recherches (16 au total)
        PageRecherche[] pr = new PageRecherche[3];

        pr[0] = new PageRecherche(new PointageKPIPresence(), request, paramVide, paramVide, 4, null, 0);
        pr[0].setAWhere(" and daty = '"+ datyjour +"'");
        pr[1] = new PageRecherche(new PointageKPIAbsence(), request, paramVide, paramVide, 4, null, 0);
        pr[1].setAWhere(" and daty = '"+ datyjour +"'");
        pr[2] = new PageRecherche(new PointageRetardPoint(), request, paramVide, paramVide, 4, null, 0);
        pr[2].setAWhere(" and daty = '"+ datyjour +"'");

        String listeCrt[] = {"daty"};
        String listeInt[] = {};
        String libEntete[] = {"daty", "point_de_vente", "personnel", "heure_arrivee", "retard_minutes"};
        PageRecherche pr2 = new PageRecherche(new PointageRetard(), request, listeCrt, listeInt, 3, libEntete, libEntete.length);
        pr2.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr2.setLien((String) session.getValue("lien"));
        pr2.setTitre("Tableau des retards du jour");
        String libEnteteAffiche[] = {"Date", "Point de vente", "Personnel", "Heure d'arriv&eacute;", "Retard en minutes"};
        String[] colSomme = null;
        pr2.setAWhere(" and daty = '"+ datyjour +"'");
        pr2.creerObjetPage(libEntete, colSomme);
        pr2.getTableau().setLibelleAffiche(libEnteteAffiche);


        for (PageRecherche p : pr) {
            p.setUtilisateur(u);
            p.setNpp(9999);
        }
        // Exécution de toutes les requêtes SQL en une seule opération
        PageRecherche.creerObjetPage(pr);


        // 2. Configuration des Widgets (DashboardGraphOpt)
        DashboardGraphOpt[] g = new DashboardGraphOpt[3];

        // 15. Executif (Card) - Ancien 'dgFormationExecutif'
        g[0] = new DashboardGraphOpt(pr[0].getRs(), null, new String[]{"Personnels attendus", "Nombre de Personnels Presents", "Nombre de Personnels Absents", "Taux de pr&eacute;sence"}, DashboardGraphType.CARD);
        g[0].setColValeur(new String[]{"magasiniers_attendus", "presents", "absents", "taux_presence"});
        g[0].setIcon("trending_up");
        g[0].setColSpan(3);
        g[0].setTitre("Personnels");
        g[0].setAvecDate(false);
        g[0].setCanvasId("graphpkpipresence");

        g[1] = new DashboardGraphOpt(pr[1].getRs(), null, new String[]{"Nombre de retards", "Durée moyenne des retards en minutes"}, DashboardGraphType.CARD);
        g[1].setColValeur(new String[]{"nb_retards", "retard_moyen_minutes"});
        g[1].setIcon("trending_up");
        g[1].setColSpan(3);
        g[1].setTitre("Retards");
        g[1].setAvecDate(true);
        g[1].setCanvasId("graphpkpiabsence");

        g[2] = new DashboardGraphOpt(pr[2].getRs(), null, new String[]{"Nombre de retard"}, DashboardGraphType.BAR);
        g[2].setTitre("Retard par magasin");
        g[2].setOrientation("horizontal");
        g[2].setColValeur(new String[]{"nb_retards"});
        g[2].setColAffiche(new String[]{"point_de_vente"});
        g[2].setOrientation("horizontal");
        g[2].setOrdre("desc");
        g[2].setColSpan(12);
        g[2].setIcon("list");
        g[2].setCanvasId("chartRetardParMagasin");

        // Finalisation de tous les graphiques
        for (DashboardGraphOpt graph : g) {
            graph.setBackgroundColor(backgroundColor);
            graph.setCouleurs(palettes); // La nouvelle palette est appliquée ici
            graph.creerGraph();
        }
%>
<style>
    #chartCategorie {
        height: 344px !important;
    }

    #chartContainer_chartSemestre {
        height: 360px !important;
    }

    #chartSemestre {
        height: 344px !important;
    }

    #chartContainer_chartCategorie {
        height: 360px !important;
    }

    #chartContainer_chartTopFormateur {
        height: 724px !important;
    }

    #chartTopFormateur {
        height: 708px !important;
    }

    #chartContainer_chartCoutAction {
        height: 724px !important;
    }

    #chartCoutAction {
        height: 708px !important;
    }

</style>
<div class="content-wrapper">
    <section class="content-header">
        <h1>Dashboard Gestion des Formations</h1>
    </section>

    <section class="content">
        <form action="<%=lien%>?but=formation/dashboard-formation.jsp" method="post" name="formulaire">
            <!--  <%= pr[0].getFormu().getHtmlEnsemble() %> -->
        </form>


        <div class="row" style="margin-top: 20px;">
            <%=g[0].getHtml()%>
            <%=g[1].getHtml()%>
        </div>

        <div class="row">
            <%=g[2].getHtml()%>
        </div>
    </section>

    <section class="content">
        <div class="row" style="margin-top: 20px;">
            <section class="content-header">
                <h3> <%=pr2.getTitre()%></h3>
            </section>
            <%=pr2.getTableau().getHtml()%>
        </div>
    </section>
</div>


<script>
    document.addEventListener("DOMContentLoaded", function () {
        const exportBtn = document.querySelector('[data-target="#exporter"]'); // Cacher le bouton Exporter
        if (exportBtn) {
            const wrapper = exportBtn.closest(".d-flex");
            (wrapper || exportBtn).style.display = "none";
        }
        const motsCles = document.querySelector(".mots-cless"); // Cacher le bloc mots-clés
        if (motsCles) {
            motsCles.style.display = "none";
        }
    });
</script>

<%
    } catch (Exception e) {
        e.printStackTrace();
        out.println("<div class='alert alert-danger'>Erreur : " + e.getMessage() + "</div>");
    }
%>