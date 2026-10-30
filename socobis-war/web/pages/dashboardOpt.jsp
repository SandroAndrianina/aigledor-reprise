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

    String dateDuJour = Utilitaire.dateDuJour();
    String anneeCourante = Utilitaire.getAnnee(dateDuJour);
    String debutMois = Utilitaire.getDebutMoisString();
    String finMois = Utilitaire.getFinMoisString();
    String backgroundColor = ConstanteDashboard.DEFAULT_GRAPH_BACKGROUND_COLOR;
    String[] palettes = ConstanteAigleDor.DEFAULT_COLORS;
    String[] paramVide = {};
    String[] paramDaty = {"daty"};

    VenteCa venteCa = new VenteCa();
    String[] colValCa = {"montantttc"};
    String[] colValNbVente = {"nb"};
    String[] legendeCa = {"Chiffre d'affaire"};
    String[] legendeNbVente = {"Nombre de ventes"};
    VenteDetailsCplCategorie venteDetailsCpl = new VenteDetailsCplCategorie();
    String[] colAfficheCat = {"categorieIngredient"};
    String[] colAffichePrd = {"idProduitLib"};
    String[] legendesPrd = {"Nombre de ventes"};
    System.out.println("eto1");
    PageRecherche[] pr = new PageRecherche[3];
    pr[0] =  new PageRecherche(venteCa, request, paramVide, paramVide, 4, null, 0);
    pr[0].setAWhere(" and daty >= '"+ debutMois +"' and daty <= '"+finMois+"'");
    pr[1] =  new PageRecherche(venteCa, request, paramVide, paramVide, 4, null, 0);
    pr[1].setAWhere(" and daty >= '"+ debutMois +"' and daty <= '"+finMois+"'");
    pr[2] =  new PageRecherche(venteDetailsCpl, request, colAffichePrd, paramVide, 4, colAffichePrd, 0);
    pr[2].setAWhere(" and daty >= '"+ debutMois +"' and daty <= '"+finMois+"'");
    for (PageRecherche p : pr) {
        p.setUtilisateur(u);
        p.setNpp(9999);
    }
    System.out.println("eto2"+ Utilitaire.heureCouranteHMS());

    PageRecherche.creerObjetPage(pr);

    System.out.println("eto3  "+ Utilitaire.heureCouranteHMS());
    DashboardGraphOpt[] g = new DashboardGraphOpt[7];
    g[0] = new DashboardGraphOpt(pr[0].getRs(), null, legendeCa, DashboardGraphType.CARD);
    g[0].setColValeur(colValCa);
    g[0].setIcon("shopping_cart");
    g[0].setUnite(" Ar");
    g[0].setColSpan(12);
    g[0].setTitre("Chiffre d'affaire total");
    g[0].setAvecDate(false);
    g[0].setLienPage("vente/vente-liste.jsp");
    g[0].setCanvasId("caVenteCanvas");
    System.out.println("eto4");

    g[1] = new DashboardGraphOpt(pr[1].getRs(), null, legendeNbVente, DashboardGraphType.CARD);
    g[1].setColValeur(colValNbVente);
    g[1].setColSpan(12);
    g[1].setTitre("Nombre total de vente");
    g[1].setIcon("shopping_cart");
    g[1].setLienPage("vente/vente-liste.jsp");
    g[1].setBackgroundColor(backgroundColor);
    g[1].setCouleurs(palettes);
    g[1].setCanvasId("nbVenteCanvas");

    String[] colAfficheCa = {"daty"};
    g[2] = new DashboardGraphOpt(pr[0].getRs(), null, legendeCa, DashboardGraphType.LINE);
    g[2].setColValeur(colValCa);
    g[2].setLegendes(legendeCa);
    g[2].setColAffiche(colAfficheCa);
    g[2].setDateColonne("daty");
    g[2].setTitre("&Eacute;volution CA");
    g[2].setIcon("trending_up");
    g[2].setColSpan(12);
    g[2].setOrientation("horizontal");
    g[2].setLienPage("vente/vente-liste.js");
    g[2].setBackgroundColor(backgroundColor);
    g[2].setCouleurs(palettes);
    g[2].setCanvasId("graphCaPeriode");


    g[4] = new DashboardGraphOpt(pr[2].getRs(), null, paramVide, DashboardGraphType.DOUGHNUT);
    g[4].setTitre("R&eacute;partition des ventes par catégorie");
    g[4].setColValeur(colValNbVente);
    g[4].setColAffiche(colAfficheCat);
    g[4].setColSpan(4);
    g[4].setIcon("pie_chart");
    g[4].setLienPage("vente/vente-liste.jsp");
    g[4].setBackgroundColor(backgroundColor);
    g[4].setCouleurs(palettes);
    g[4].setCanvasId("graphVenteParCategorie");


    g[5] = new DashboardGraphOpt(pr[2].getRs(), null, paramVide, DashboardGraphType.BAR);
    g[5].setTitre("Top 10 des produits les plus vendus");
    g[5].setIcon("bar_chart");
    g[5].setLegendes(legendesPrd);
    g[5].setColValeur(colValNbVente);
    g[5].setColAffiche(colAffichePrd);
    g[5].setColSpan(8);
    g[5].setLimite(10);
    g[5].setOrdre("desc");
    g[5].setOrientation("horizontal");
    g[5].setLienPage("vente/vente-liste.js");
    g[5].setBackgroundColor(backgroundColor);
    g[5].setCouleurs(palettes);
    g[5].setCanvasId("graphTopProduitsBar");

    String[] colValParMagasin = {"montantttc","nb"};
    String[] colAfficheParMagasin = {"idMagasinLib"};
    String[] legendesParMagasin = {"CA","Total de vente"};
    g[3] = new DashboardGraphOpt(pr[0].getRs(),null, colValParMagasin, DashboardGraphType.CARD);
    g[3].setTitre("Performance par magasin");
    g[3].setColValeur(colValParMagasin);
    g[3].setColAffiche(colAfficheParMagasin);
    g[3].setLegendes(legendesParMagasin);
    g[3].setColSpan(6);
    g[3].setUnite(new String[]{" Ar",""});
    g[3].setIcon("bar_chart");
    g[3].setLienPage("vente/vente-liste.jsp");
    g[3].setBackgroundColor(backgroundColor);
    g[3].setCouleurs(palettes);
    g[3].setCanvasId("graphCaParMagasin");

    VenteCaEvolution venteCaEvolution = new VenteCaEvolution();
    String[] colValCaToday = {"ca_jour"};
    String[] legendeCaToday = {"Chiffre d'affaire total du jour"};
    g[6] = new DashboardGraphOpt(pr[0].getRs(), null, paramVide, DashboardGraphType.CARD);
    g[6].setTitre("Chiffre d'affaire total du jour");
    g[6].setColValeur(colValCa);
    g[6].setLegendes(legendeCaToday);
    g[6].setUnite(" Ar");
    g[6].setColSpan(12);
    g[6].setIcon("today");
    g[6].setLienPage("vente/vente-liste.jsp");
    g[6].setBackgroundColor(backgroundColor);
    g[6].setCouleurs(palettes);
    g[6].setCanvasId("caTodayCanvas");

//
//    String[] colValNbVenteMois = {"ventes_jour", "evolution_ventes_pct", "ca_jour"};
//    String[] legendeNbVenteMois = {"Nombre de ventes", "Evolution des ventes (%)", "Chiffre d'affaire du jour"};
//
//    PageRecherche prmois = new PageRecherche(venteCaEvolution, request, new String[]{"moisanneelib"}, paramVide, 4, colValNbVenteMois, colValNbVenteMois.length);
//    g[7] = new DashboardGraphOpt(prmois.getRs(), null, paramVide, DashboardGraphType.CARD);
//    g[7].setAvecDate(true);
//    g[7].setColValeur(colValNbVenteMois);
//    g[7].setLegendes(legendeNbVenteMois);
//    g[7].setColSpan(6);
//    g[7].setTitre("Total vente du mois");
//    g[7].setIcon("today");
//    g[7].setUnite(new String[]{""," %"," Ar"});
//    g[7].setLienPage("vente/vente-liste.jsp");
//    g[7].setBackgroundColor(backgroundColor);
//    g[7].setCouleurs(palettes);
//    g[7].setCanvasId("nbVenteMoisCanvas");


    System.out.println("eto5");

    for (DashboardGraphOpt graph : g) {
        graph.setBackgroundColor(backgroundColor);
        graph.setCouleurs(palettes); // La nouvelle palette est appliquée ici
        graph.creerGraph();
    }

%>

<div class="content-wrapper">
    <section class="content-header">
        <h1>Tableau de bord globale</h1>
    </section>
    <section class="content">
<%--        <form action="<%=lien%>?but=<%=da.getApres()%>" method="post">--%>
<%--            <%=da.getHtmlFormu()%>--%>
<%--        </form>--%>
        <div class="row gap-2">
            <div class="row">
                <div class="col-md-6"><%=g[6].getHtml()%></div>
                <div class="col-md-6"><%=g[0].getHtml()%></div>
                <div class="col-md-6"><%=g[1].getHtml()%></div>
            </div>
            <div class="row">
                <%=g[2].getHtml()%>
            </div>
        </div>
        <div class="row gap-2">
        <div class="col-md-12 nopadding">
            <h2 class="col-md-12 h520pxSemibold">Performance par boutique</h2>
            <%=g[3].getHtml()%>
        </div>
        <div class="col-md-12 nopadding">
            <h2 class="col-md-12 h520pxSemibold">Performance des produits</h2>
            <%=g[4].getHtml()%>
            <%=g[5].getHtml()%>
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


