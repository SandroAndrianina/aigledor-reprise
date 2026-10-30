<%@ page import="user.UserEJB" %>
<%@ page import="affichage.*" %>
<%@ page import="utilitaire.Utilitaire" %>
<%@ page import="vente.*" %>
<%@ page import="utils.ConstanteAigleDor" %>
<%@ page import="constante.ConstanteDashboard" %>
<%@ page import="dashboard.KpiCa" %>
<%@ page import="dashboard.KpiCaVals" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!-- Inclusion de Chart.js pour les graphiques -->
<script src="${pageContext.request.contextPath}/chartPlugins/Chart.min.js"></script>

<% try {
    String lien = (String) session.getAttribute("lien");
    UserEJB u = (user.UserEJB) session.getValue("u");
    DashboardGraph[] graphs = new DashboardGraph[6];
    DashboardGraph[] graphsToday = new DashboardGraph[6];
    String dateDuJour = Utilitaire.dateDuJour();
    String anneeCourante = Utilitaire.getAnnee(dateDuJour);
    String backgroundColor = ConstanteDashboard.DEFAULT_GRAPH_BACKGROUND_COLOR;
    String[] palettes = ConstanteAigleDor.DEFAULT_COLORS;
    String[] paramVide = {};
    String[] paramDaty = {"daty"};

    VenteCa venteCa = new VenteCa();
    String[] colValCa = {"montantttc"};
    String[] colValNbVente = {"nb"};

    String[] legendeCa = {"Chiffre d'affaire"};
    String[] legendeNbVente = {"Nombre de ventes"};
    PageRecherche prVenteCa = new PageRecherche(venteCa, request, paramDaty, paramDaty, 4, legendeCa, legendeCa.length);
    graphs[0] = new DashboardGraph(prVenteCa, colValCa, null,legendeCa, DashboardGraphType.CARD);
    graphs[0].setTitre("Chiffre d'affaire total");
    graphs[0].setUnite(" Ar");
    graphs[0].setColSpan(12);
    graphs[0].setIcon("shopping_cart");
    graphs[0].setLienPage("vente/vente-liste.jsp");
    graphs[0].setBackgroundColor(backgroundColor);
    graphs[0].setCouleurs(palettes);
    graphs[0].setCanvasId("caVenteCanvas");

    PageRecherche prNbVente = new PageRecherche(venteCa, request, paramDaty, paramDaty, 4, legendeNbVente, legendeNbVente.length);
    graphs[1] = new DashboardGraph(prNbVente, colValNbVente, null,legendeNbVente, DashboardGraphType.CARD);
    graphs[1].setColSpan(12);
    graphs[1].setTitre("Nombre total de vente");
    graphs[1].setIcon("shopping_cart");
    graphs[1].setLienPage("vente/vente-liste.jsp");
    graphs[1].setBackgroundColor(backgroundColor);
    graphs[1].setCouleurs(palettes);
    graphs[1].setCanvasId("nbVenteCanvas");

    String[] colAfficheCa = {"daty"};
    graphs[2] = new DashboardGraph(venteCa, request, colValCa, colAfficheCa, legendeCa, 2000, paramVide,"daty", DashboardGraphType.LINE);
    graphs[2].setTitre("&Eacute;volution CA");
    graphs[2].setIcon("trending_up");
    graphs[2].setColSpan(12);
    graphs[2].setOrientation("horizontal");
    graphs[2].setLienPage("vente/vente-liste.js");
    graphs[2].setBackgroundColor(backgroundColor);
    graphs[2].setCouleurs(palettes);
    graphs[2].setCanvasId("graphCaPeriode");

    VenteDetailsCplCategorie venteDetailsCpl = new VenteDetailsCplCategorie();
    String[] colAfficheCat = {"categorieIngredient"};
    graphs[4] = new DashboardGraph(venteDetailsCpl, request, colValNbVente, colAfficheCat, paramVide, 2000, paramVide,"daty", DashboardGraphType.DOUGHNUT);
    graphs[4].setTitre("R&eacute;partition des ventes par catégorie");
    graphs[4].setColSpan(4);
    graphs[4].setIcon("pie_chart");
    graphs[4].setLienPage("vente/vente-liste.jsp");
    graphs[4].setBackgroundColor(backgroundColor);
    graphs[4].setCouleurs(palettes);
    graphs[4].setCanvasId("graphVenteParCategorie");

    String[] colAffichePrd = {"idProduitLib"};
    String[] legendesPrd = {"Nombre de ventes"};
    graphs[5] = new DashboardGraph(venteDetailsCpl, request, colValNbVente, colAffichePrd, legendesPrd, 2000, paramVide,"daty", DashboardGraphType.BAR);
    graphs[5].setTitre("Top 10 des produits les plus vendus");
    graphs[5].setIcon("bar_chart");
    graphs[5].setColSpan(8);
    graphs[5].setLimite(10);
    graphs[5].setOrdre("desc");
    graphs[5].setOrientation("horizontal");
    graphs[5].setLienPage("vente/vente-liste.js");
    graphs[5].setBackgroundColor(backgroundColor);
    graphs[5].setCouleurs(palettes);
    graphs[5].setCanvasId("graphTopProduitsBar");

    String[] colValParMagasin = {"montantttc","nb"};
    String[] colAfficheParMagasin = {"idMagasinLib"};
    String[] legendesParMagasin = {"CA","Total de vente"};
    graphs[3] = new DashboardGraph(venteCa,request, colValParMagasin, colAfficheParMagasin, legendesParMagasin, 2000, paramVide,"daty", DashboardGraphType.CARD);
    graphs[3].setTitre("Performance par magasin");
    graphs[3].setColSpan(6);
    graphs[3].setUnite(new String[]{" Ar",""});
    graphs[3].setIcon("bar_chart");
    graphs[3].setLienPage("vente/vente-liste.jsp");
    graphs[3].setBackgroundColor(backgroundColor);
    graphs[3].setCouleurs(palettes);
    graphs[3].setCanvasId("graphCaParMagasin");

    DashboardApj da = new DashboardApj(graphs, u);
    da.setApres("dashboard.jsp");
    da.setLien(lien);
    da.getFormu().getChamp("daty1").setDefaut(Utilitaire.getDebutAnnee(anneeCourante));
    da.getFormu().getChamp("daty2").setDefaut(dateDuJour);
    da.getFormu().getChamp("daty1").setLibelle("Date Min");
    da.getFormu().getChamp("daty2").setLibelle("Date Max");

    da.creerObjetPage(true);
//
    VenteCaEvolution venteCaEvolution = new VenteCaEvolution();
    String[] colValCaToday = {"ca_jour"};
    String[] legendeCaToday = {"Chiffre d'affaire total du jour"};
    graphsToday[0] = new KpiCa(venteCa, request, colValCa, null, legendeCaToday,"daty", DashboardGraphType.CARD);
    graphsToday[0].setTitre("Chiffre d'affaire total du jour");
    graphsToday[0].setUnite(" Ar");
    graphsToday[0].setColSpan(12);
    graphsToday[0].setIcon("today");
    graphsToday[0].setLienPage("vente/vente-liste.jsp");
    graphsToday[0].setBackgroundColor(backgroundColor);
    graphsToday[0].setCouleurs(palettes);
    graphsToday[0].setCanvasId("caTodayCanvas");

//    String[] colValNbVenteToday = {"ventes_jour", "evolution_ventes_pct", "ca_jour"};
//    String[] legendeNbVenteToday = {"Nombre de ventes", "Evolution des ventes (%)", "Chiffre d'affaire du jour"};
//    graphsToday[1] = new KpiCaVals(venteCaEvolution, request, colValNbVenteToday, null, legendeNbVenteToday,"daty", DashboardGraphType.CARD);
//    graphsToday[1].setColSpan(6);
//    graphsToday[1].setTitre("Total vente du jour");
//    graphsToday[1].setIcon("today");
//    graphsToday[1].setUnite(new String[]{""," %"," Ar"});
//    graphsToday[1].setLienPage("vente/vente-liste.jsp");
//    graphsToday[1].setBackgroundColor(backgroundColor);
//    graphsToday[1].setCouleurs(palettes);
//    graphsToday[1].setCanvasId("nbVenteTodayCanvas");

    DashboardApj daToday = new DashboardApj(graphsToday, u);
    daToday.setApres("dashboard.jsp");
    daToday.setLien(lien);
    daToday.getFormu().getChamp("daty1").setDefaut(dateDuJour);
    daToday.getFormu().getChamp("daty2").setDefaut(dateDuJour);
    daToday.getFormu().getChamp("daty1").setLibelle("Date Min");
    daToday.getFormu().getChamp("daty2").setLibelle("Date Max");
    daToday.creerObjetPage(true);

    DashboardGraph[] graphsMois = new DashboardGraph[1];
    String[] colValNbVenteMois = {"ventes_jour", "evolution_ventes_pct", "ca_jour"};
    String[] legendeNbVenteMois = {"Nombre de ventes", "Evolution des ventes (%)", "Chiffre d'affaire du jour"};

    PageRecherche prmois = new PageRecherche(venteCaEvolution, request, new String[]{"moisanneelib"}, paramVide, 4, colValNbVenteMois, colValNbVenteMois.length);
    graphsMois[0] = new KpiCaVals(prmois, colValNbVenteMois, null, legendeNbVenteMois, DashboardGraphType.CARD);
    graphsMois[0].setAvecDate(true);
    graphsMois[0].setColSpan(6);
    graphsMois[0].setTitre("Total vente du mois");
    graphsMois[0].setIcon("today");
    graphsMois[0].setUnite(new String[]{""," %"," Ar"});
    graphsMois[0].setLienPage("vente/vente-liste.jsp");
    graphsMois[0].setBackgroundColor(backgroundColor);
    graphsMois[0].setCouleurs(palettes);
    graphsMois[0].setCanvasId("nbVenteMoisCanvas");

    DashboardApj daMois = new DashboardApj(graphsMois, u);
    daMois.setApres("dashboard.jsp");
    daMois.setLien(lien);
    daMois.getFormu().getChamp("moisanneelib").setDefaut(Utilitaire.getMois(dateDuJour)+"/"+anneeCourante);
//    daMois.creerObjetPage(true);
%>

<div class="content-wrapper">
    <section class="content-header">
        <h1>Tableau de bord globale</h1>
    </section>
    <section class="content">
        <form action="<%=lien%>?but=<%=da.getApres()%>" method="post">
            <%=da.getHtmlFormu()%>
        </form>
        <div class="row gap-2">
            <div class="col-md-6">
                <%=graphsToday[0].getHtml()%>
                <%--                <%=graphsToday[1].getHtml()%>--%>
                <%--                <%=graphsMois[0].getHtml()%>--%>
                <%=graphs[0].getHtml()%>
                <%=graphs[1].getHtml()%>
            </div>
            <div class="col-md-6">
                <%=graphs[2].getHtml()%>
            </div>
        </div>
        <div class="row gap-2">
            <%--            <div class="col-md-12 nopadding">--%>
            <%--                <h2 class="col-md-12 h520pxSemibold">Performance globale</h2>--%>
            <%--                <%=graphs[1].getHtml()%>--%>
            <%--                <%=graphs[0].getHtml()%>--%>
            <%--            </div>--%>
            <div class="col-md-12 nopadding">
                <h2 class="col-md-12 h520pxSemibold">Performance par boutique</h2>
                <%=graphs[3].getHtml()%>
            </div>
            <div class="col-md-12 nopadding">
                <h2 class="col-md-12 h520pxSemibold">Performance des produits</h2>
                <%=graphs[4].getHtml()%>
                <%=graphs[5].getHtml()%>
            </div>
        </div>
    </section>
</div>

<% } catch (Exception e) {
    e.printStackTrace(); %>
<script language="JavaScript"> alert('<%=e.getMessage()%>');
history.back();
</script>
<% }%>


