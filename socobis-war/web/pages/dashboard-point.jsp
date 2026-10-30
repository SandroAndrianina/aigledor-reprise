<%@ page import="user.UserEJB" %>
<%@ page import="affichage.*" %>
<%@ page import="utilitaire.Utilitaire" %>
<%@ page import="vente.*" %>
<%@ page import="annexe.Point" %>
<%@ page import="utils.ConstanteAigleDor" %>
<%@ page import="constante.ConstanteDashboard" %>
<%@ page import="bean.CGenUtil" %>
<%@ page import="caisse.EtatCaisseMagasin" %>
<%@ page import="dashboard.KpiCa" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!-- Inclusion de Chart.js pour les graphiques -->
<script src="${pageContext.request.contextPath}/chartPlugins/Chart.min.js"></script>

<% try {
    String lien = (String) session.getAttribute("lien");
    UserEJB u = (user.UserEJB) session.getValue("u");
    Point myPoint = u.getPointEncours();
    Point pointDeVente = myPoint;
    String idMagasinLib = request.getParameter("idMagasinLib");
    String dateDuJour = Utilitaire.dateDuJour();
    String anneeCourante = Utilitaire.getAnnee(dateDuJour);
    String backgroundColor = ConstanteDashboard.DEFAULT_GRAPH_BACKGROUND_COLOR;
    String[] palettes = ConstanteAigleDor.DEFAULT_COLORS;
    String[] paramVide = {};

    if (idMagasinLib != null && !idMagasinLib.isEmpty()) {
        Point[] points = (Point[]) CGenUtil.rechercher(new Point(), null, null, null, " AND val LIKE '%"+idMagasinLib+"%'");
        if (points.length > 0) {
            pointDeVente = points[0];
        }
    }

    // ========== Données de base ==========
    VenteCa venteCa = new VenteCa();
    VenteDetailsCplCategorie venteDetailsCpl = new VenteDetailsCplCategorie();
    EtatCaisseMagasin etatCaisse = new EtatCaisseMagasin();

    String[] colValCa = {"montantttc"};
    String[] colValNbVente = {"nb"};

    // ========== Critères avec point de vente ==========
    String[] criterePoint = {"daty", "idMagasinLib"};
    String[] critereIntPoint = {"daty"};

    // ========== Graphiques ==========
    DashboardGraph[] graphs = new DashboardGraph[8];

    // --- CARD : CA total du point ---
    String[] legendeCa = {"Chiffre d`affaire total"};
    PageRecherche prCa = new PageRecherche(venteCa, request, criterePoint, critereIntPoint, 4, legendeCa, legendeCa.length);
    graphs[0] = new DashboardGraph(prCa, colValCa, null, legendeCa, DashboardGraphType.CARD);
    graphs[0].setTitre("Chiffre d'affaire total " + pointDeVente.getVal());
    graphs[0].setUnite(" AR");
    graphs[0].setColSpan(12);
    graphs[0].setIcon("shopping_cart");
    graphs[0].setLienPage("vente/vente-liste.jsp");
    graphs[0].setBackgroundColor(backgroundColor);
    graphs[0].setCouleurs(palettes);
    graphs[0].setCanvasId("caVentePointCanvas");

    // --- CARD : Nombre de ventes du point ---
    String[] legendeNbVente = {"Nombre de ventes"};
    PageRecherche prNbVente = new PageRecherche(venteCa, request, criterePoint, critereIntPoint, 4, legendeNbVente, legendeNbVente.length);
    graphs[1] = new DashboardGraph(prNbVente, colValNbVente, null, legendeNbVente, DashboardGraphType.CARD);
    graphs[1].setTitre("Ventes pour " + pointDeVente.getVal());
    graphs[1].setColSpan(12);
    graphs[1].setIcon("shopping_cart");
    graphs[1].setLienPage("vente/vente-liste.jsp");
    graphs[1].setBackgroundColor(backgroundColor);
    graphs[1].setCouleurs(palettes);
    graphs[1].setCanvasId("nbVentePointCanvas");

    // --- LINE : Evolution du CA par date ---
    String[] colAfficheCa = {"daty"};
    PageRechercheGroupe evoCa = new PageRechercheGroupe(venteCa, request, criterePoint, critereIntPoint, 4, colAfficheCa, colValCa, paramVide, colAfficheCa.length, colValCa.length);
    evoCa.setNpp(2000);
    graphs[2] = new DashboardGraph(evoCa, legendeCa, DashboardGraphType.LINE);
    graphs[2].setTitre("Evolution du CA - " + pointDeVente.getVal());
    graphs[2].setIcon("trending_up");
    graphs[2].setUnite(" AR");
    graphs[2].setColSpan(12);
    graphs[2].setLienPage("vente/vente-liste.jsp");
    graphs[2].setBackgroundColor(backgroundColor);
    graphs[2].setCouleurs(palettes);
    graphs[2].setCanvasId("graphCaPeriodePoint");

    // --- BAR : Ventes par heure du jour ---
//    String[] colAfficheHeure = {"heurevente"};
//    String[] legendeHeure = {"Nombre de ventes"};
//    graphs[3] = new DashboardGraph(venteCa, request, colValNbVente, colAfficheHeure, legendeHeure, 2000, paramVide, "daty", DashboardGraphType.BAR);
//    graphs[3].setTitre("Ventes par heure");
//    graphs[3].setIcon("schedule");
//    graphs[3].setColSpan(6);
//    graphs[3].setLienPage("vente/vente-liste.jsp");
//    graphs[3].setBackgroundColor(backgroundColor);
//    graphs[3].setCouleurs(palettes);
//    graphs[3].setCanvasId("graphVenteHeurePoint");

//     --- DOUGHNUT : Répartition modes de paiement ---
    String[] colAfficheModePaie = {"idModePaiementLib"};
    String[] legendeModePaie = {"Nombre de ventes"};
    PageRechercheGroupe modePaie = new PageRechercheGroupe(venteCa, request, criterePoint, critereIntPoint, 4, colAfficheModePaie, colValNbVente, paramVide, colAfficheModePaie.length, colValNbVente.length);
    modePaie.setNpp(2000);
    graphs[4] = new DashboardGraph(modePaie, legendeModePaie, DashboardGraphType.DOUGHNUT);
    graphs[4].setTitre("Moyens de paiement");
    graphs[4].setIcon("payments");
    graphs[4].setColSpan(6);
    graphs[4].setLienPage("vente/vente-liste.jsp");
    graphs[4].setBackgroundColor(backgroundColor);
    graphs[4].setCouleurs(palettes);
    graphs[4].setCanvasId("graphModePaiePoint");

    // --- DOUGHNUT : Répartition ventes par catégorie ---
    String[] colAfficheCat = {"categorieIngredient"};
    String[] legendeCat = {"Nombre de ventes"};
    PageRechercheGroupe venteCategorie = new PageRechercheGroupe(venteDetailsCpl, request, criterePoint, critereIntPoint, 4, colAfficheCat, colValNbVente, paramVide, colAfficheCat.length, colValNbVente.length);
    venteCategorie.setNpp(2000);
    graphs[5] = new DashboardGraph(venteCategorie, legendeCat, DashboardGraphType.DOUGHNUT);
    graphs[5].setTitre("Ventes par catégorie");
    graphs[5].setIcon("pie_chart");
    graphs[5].setColSpan(4);
    graphs[5].setLienPage("vente/vente-liste.jsp");
    graphs[5].setBackgroundColor(backgroundColor);
    graphs[5].setCouleurs(palettes);
    graphs[5].setCanvasId("graphVenteParCategoriePoint");

    // --- BAR horizontal : Top 10 produits ---
    String[] colAffichePrd = {"idProduitLib"};
    String[] legendePrd = {"Nombre de ventes"};
    PageRechercheGroupe topProd = new PageRechercheGroupe(venteDetailsCpl, request, criterePoint, critereIntPoint, 4, colAffichePrd, colValNbVente, paramVide, colAffichePrd.length, colValNbVente.length);
    topProd.setNpp(2000);
    graphs[6] = new DashboardGraph(topProd, legendePrd, DashboardGraphType.BAR);
    graphs[6].setTitre("Top 10 produits vendus");
    graphs[6].setIcon("bar_chart");
    graphs[6].setColSpan(8);
    graphs[6].setLimite(10);
    graphs[6].setOrdre("desc");
    graphs[6].setOrientation("horizontal");
    graphs[6].setLienPage("vente/vente-liste.jsp");
    graphs[6].setBackgroundColor(backgroundColor);
    graphs[6].setCouleurs(palettes);
    graphs[6].setCanvasId("graphTopProduitsBarPoint");

    // --- BAR : Ventes par vendeur ---
//    String[] colAfficheVendeur = {"idResponsablelib"};
//    String[] legendeVendeur = {"Nombre de ventes"};
//    PageRechercheGroupe venteVendeur = new PageRechercheGroupe(venteCa, request, criterePoint, critereIntPoint, 4, colAfficheVendeur, colValNbVente, paramVide, colAfficheVendeur.length, colValNbVente.length);
//    venteVendeur.setNpp(2000);
//    graphs[-1] = new DashboardGraph(venteVendeur, legendeVendeur, DashboardGraphType.BAR);
//    graphs[-1].setTitre("Ventes par vendeur");
//    graphs[-1].setIcon("person");
//    graphs[-1].setColSpan(12);
//    graphs[-1].setOrdre("desc");
//    graphs[-1].setLienPage("vente/vente-liste.jsp");
//    graphs[-1].setBackgroundColor(backgroundColor);
//    graphs[-1].setCouleurs(palettes);
//    graphs[-1].setCanvasId("graphVenteVendeurPoint");

    String[] colAfficheCaisse = {"idcaisseLib"};
    String[] colValCaisse = {"credit", "debit", "reste"};
    String[] legendeCaisse = {"Somme Crédit", "Somme Débit", "Reste"};
    PageRechercheGroupe prCaisse = new PageRechercheGroupe(etatCaisse, request, new String[]{"idMagasinLib"}, paramVide, 4, colAfficheCaisse, colValCaisse, paramVide, colAfficheCaisse.length, colValCaisse.length);
    prCaisse.setNpp(2000);
    prCaisse.getFormu().getChamp("idMagasinLib").setDefaut(pointDeVente.getVal());
    graphs[7] = new DashboardGraph(prCaisse, legendeCaisse, DashboardGraphType.CARD);
    graphs[7].setTitre("Situation de caisse");
    graphs[7].setIcon("account_balance_wallet");
    graphs[7].setUnite(new String[]{" Ar"," Ar"," Ar"});
    graphs[7].setColSpan(3);
    graphs[7].setAvecDate(false);
    graphs[7].setLienPage("caisse/caisse-liste.jsp");
    graphs[7].setBackgroundColor(backgroundColor);
    graphs[7].setCouleurs(palettes);
    graphs[7].setCanvasId("graphEtatCaissePoint");

    // ========== Dashboard ==========
    DashboardApj da = new DashboardApj(graphs, u);
    da.setApres("dashboard-point.jsp");
    da.setLien(lien);
    da.getFormu().getChamp("daty1").setDefaut(Utilitaire.getDebutAnnee(anneeCourante));
    da.getFormu().getChamp("daty2").setDefaut(dateDuJour);
    da.getFormu().getChamp("idMagasinLib").setDefaut(pointDeVente.getVal());
    da.getFormu().getChamp("daty1").setLibelle("Date Min");
    da.getFormu().getChamp("daty2").setLibelle("Date Max");
    da.getFormu().getChamp("idMagasinLib").setLibelle("Point de vente");
    da.creerObjetPage(true);

    // ========== Graphiques du jour (VenteCaEvolutionMagasin) ==========
    VenteCaEvolutionMagasin venteCaEvolutionMag = new VenteCaEvolutionMagasin();
    DashboardGraph[] graphsToday = new DashboardGraph[2];

    String[] colValCaToday = {"ca_jour"};
    String[] legendeCaToday = {"Chiffre d'affaire total du jour"};
    PageRecherche prCaToday = new PageRecherche(venteCa, request, new String[]{"daty", "idmagasinlib"}, new String[]{"daty"}, 4, colValCa, colValCa.length);
    graphsToday[0] = new KpiCa(prCaToday, colValCa, null, legendeCaToday, DashboardGraphType.CARD);
    graphsToday[0].setTitre("Chiffre d'affaire total du jour");
    graphsToday[0].setUnite(" Ar");
    graphsToday[0].setColSpan(12);
    graphsToday[0].setIcon("today");
    graphsToday[0].setLienPage("vente/vente-liste.jsp");
    graphsToday[0].setBackgroundColor(backgroundColor);
    graphsToday[0].setCouleurs(palettes);
    graphsToday[0].setCanvasId("caTodayPointCanvas");

    String[] colValNbVenteToday = {"ventes_jour", "evolution_ventes_pct", "ca_jour"};
    String[] legendeNbVenteToday = {"Nombre de ventes", "Evolution des ventes (%)", "Chiffre d'affaire du jour"};
    PageRecherche prNbVenteToday = new PageRecherche(venteCaEvolutionMag, request, new String[]{"daty", "idmagasinlib"}, new String[]{"daty"}, 4, colValNbVenteToday, colValNbVenteToday.length);
    graphsToday[1] = new DashboardGraph(prNbVenteToday, colValNbVenteToday, null, legendeNbVenteToday, DashboardGraphType.CARD);
    graphsToday[1].setColSpan(6);
    graphsToday[1].setTitre("Total vente du jour - " + pointDeVente.getVal());
    graphsToday[1].setIcon("today");
    graphsToday[1].setLienPage("vente/vente-liste.jsp");
    graphsToday[1].setBackgroundColor(backgroundColor);
    graphsToday[1].setCouleurs(palettes);
    graphsToday[1].setCanvasId("nbVenteTodayPointCanvas");

    DashboardApj daToday = new DashboardApj(graphsToday, u);
    daToday.setApres("dashboard-point.jsp");
    daToday.setLien(lien);
    daToday.getFormu().getChamp("daty1").setDefaut(dateDuJour);
    daToday.getFormu().getChamp("daty2").setDefaut(dateDuJour);
    daToday.getFormu().getChamp("idmagasinlib").setDefaut(pointDeVente.getVal());
    daToday.getFormu().getChamp("daty1").setLibelle("Date Min");
    daToday.getFormu().getChamp("daty2").setLibelle("Date Max");
    daToday.getFormu().getChamp("idmagasinlib").setLibelle("Point de vente");
    daToday.creerObjetPage(true);

    // ========== Graphique du mois (VenteCaEvolutionMagasin) ==========
    DashboardGraph[] graphsMois = new DashboardGraph[1];
    String[] colValNbVenteMois = {"ventes_jour", "evolution_ventes_pct", "ca_jour"};
    String[] legendeNbVenteMois = {"Nombre de ventes", "Evolution des ventes (%)", "Chiffre d'affaire du jour"};

    PageRecherche prmois = new PageRecherche(venteCaEvolutionMag, request, new String[]{"moisanneelib", "idmagasinlib"}, paramVide, 4, colValNbVenteMois, colValNbVenteMois.length);
    graphsMois[0] = new DashboardGraph(prmois, colValNbVenteMois, null, legendeNbVenteMois, DashboardGraphType.CARD);
    graphsMois[0].setColSpan(6);
    graphsMois[0].setTitre("Total vente du mois - " + pointDeVente.getVal());
    graphsMois[0].setIcon("today");
    graphsMois[0].setLienPage("vente/vente-liste.jsp");
    graphsMois[0].setBackgroundColor(backgroundColor);
    graphsMois[0].setCouleurs(palettes);
    graphsMois[0].setCanvasId("nbVenteMoisPointCanvas");

    DashboardApj daMois = new DashboardApj(graphsMois, u);
    daMois.setApres("dashboard-point.jsp");
    daMois.setLien(lien);
    daMois.getFormu().getChamp("moisanneelib").setDefaut(Utilitaire.getMois(dateDuJour)+"/"+anneeCourante);
    daMois.getFormu().getChamp("idmagasinlib").setDefaut(pointDeVente.getVal());
    daMois.creerObjetPage(true);
%>

<div class="content-wrapper">
    <section class="content-header">
        <h1>Tableau de bord par point de vente</h1>
    </section>
    <section class="content">
        <form action="<%=lien%>?but=<%=da.getApres()%>" method="post">
            <%=da.getHtmlFormu()%>
        </form>
        <div class="row p-3">
            <h2 class="col-md-12 h520pxSemibold">Performance du jour</h2>
            <div class="col-md-6">
                <%=graphsToday[0].getHtml()%>
                <%--                <%=graphsToday[1].getHtml()%>--%>
                <%--                <%=graphsMois[0].getHtml()%>--%>
                <%--                <div class="col-md-6 mb-5 nopadding">--%>
                <%=graphs[0].getHtml()%>
                <%=graphs[1].getHtml()%>
                <%--                </div>--%>
            </div>

            <div class="col-md-6">
                <%=graphs[2].getHtml()%>
            </div>
            <div class="col-md-12 nopadding">
                <h2 class="col-md-12 h520pxSemibold">Situation de caisse</h2>
                <%=graphs[7].getHtml()%>
            </div>
            <div class="col-md-12 nopadding">
                <h2 class="col-md-12 h520pxSemibold">Performance boutique</h2>
                <%--                <div class="col-md-12">--%>
<%--                <%=graphs[7].getHtml()%>--%>
                <%--                </div>--%>
                <%=graphs[6].getHtml()%>
                <%=graphs[5].getHtml()%>
                <%--                <%=graphs[3].getHtml()%>--%>
            </div>
            <div class="col-md-12">
                <%--                <%=graphs[4].getHtml()%>--%>
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

