<%@ page import="user.UserEJB" %>
<%@ page import="affichage.*" %>
<%@ page import="utilitaire.Utilitaire" %>
<%@ page import="vente.*" %>
<%@ page import="annexe.Point" %>
<%@ page import="utils.ConstanteAigleDor" %>
<%@ page import="constante.ConstanteDashboard" %>
<%@ page import="bean.CGenUtil" %>
<%@ page import="caisse.EtatCaisseMagasin" %>
<%@ page import="bean.ResultatEtSomme" %>
<%@ page import="bean.AdminGen" %>
<%@ page import="bean.ClassMAPTable" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script src="${pageContext.request.contextPath}/chartPlugins/Chart.min.js"></script>

<% try {
    String lien = (String) session.getAttribute("lien");
    UserEJB u = (user.UserEJB) session.getAttribute("u");
    String dateDuJour = Utilitaire.dateDuJour();
    String anneeCourante = Utilitaire.getAnnee(dateDuJour);
    String backgroundColor = ConstanteDashboard.DEFAULT_GRAPH_BACKGROUND_COLOR;
    String[] palettes = ConstanteAigleDor.DEFAULT_COLORS;
    String[] paramVide = {};

    // ================================================================
    // MAPPING
    VenteDetailsCplCategorie venteDetailsCpl = new VenteDetailsCplCategorie();
    venteDetailsCpl.setNomTable("VENTE_DETAILS_CAT");
    EtatCaisseMagasin etatCaisse = new EtatCaisseMagasin();
    etatCaisse.setNomTable("V_ETAT_CAISSE_MAGASIN");

    String[] colValNbVente = {"nb"};
    String[] colAfficheCat = {"categorieIngredient","idProduitLib"};
    String[] criterePoint = {"daty"};
    String[] critereIntPoint = {"daty"};

    // ================================================================
    // INSTANCIATION PAGE RECHERCHE
    PageRecherche[] pr = new PageRecherche[2];
    pr[0] = new PageRecherche(venteDetailsCpl, request, criterePoint, critereIntPoint, 4, paramVide, paramVide.length);
    pr[0].setUtilisateur((user.UserEJB) session.getAttribute("u"));
    pr[0].setNpp(9999);
    pr[0].getFormu().getChamp("daty1").setDefaut(Utilitaire.getDebutAnnee(anneeCourante));
    pr[0].getFormu().getChamp("daty2").setDefaut(dateDuJour);
    pr[0].getFormu().getChamp("daty1").setLibelle("Date Min");
    pr[0].getFormu().getChamp("daty2").setLibelle("Date Max");
    pr[1] = new PageRecherche(venteDetailsCpl, request, criterePoint, critereIntPoint, 4, paramVide, paramVide.length);
    pr[1].setUtilisateur((user.UserEJB) session.getAttribute("u"));
    pr[1].setNpp(9999);
    pr[1].getFormu().getChamp("daty1").setDefaut(dateDuJour);
    pr[1].getFormu().getChamp("daty2").setDefaut(dateDuJour);
    pr[1].getFormu().getChamp("daty1").setLibelle("Date Min");
    pr[1].getFormu().getChamp("daty2").setLibelle("Date Max");

    // PREPARER DATA
    PageRecherche.creerObjetPage(pr);
    ResultatEtSomme rsVenteDetailsCpl = pr[0].getRs();
    ResultatEtSomme rsVenteDetailsCplJour = pr[1].getRs();
    // ================================================================
    // --------- GRAPHIQUES ---------
    String[] colValEvoCa = {"montantttc"};
    String[] legendeCaToday = {"Chiffre d'affaire total du jour"};
    DashboardGraphOpt pgCaTotal = new DashboardGraphOpt(rsVenteDetailsCplJour, null, legendeCaToday, DashboardGraphType.KPI);
    pgCaTotal.setTitre("Chiffre d'affaire total du jour");
    pgCaTotal.setUnite(" Ar");
    pgCaTotal.setColSpan(12);
    pgCaTotal.setIcon("today");
    pgCaTotal.setLienPage("vente/vente-liste.jsp");
    pgCaTotal.setCanvasId("caTodayPo-dicointCanvas");
    pgCaTotal.setColValeur(colValEvoCa);
    pgCaTotal.creerGraph();

    String[] colValParMagasin = {"montantttc","nb"};
    String[] colAfficheParMagasin = {"idMagasinLib"};
    String[] legendesParMagasin = {"CA","Total de vente"};
    DashboardGraphOpt pgPerfBoutique  = new DashboardGraphOpt(rsVenteDetailsCpl,colAfficheParMagasin, legendesParMagasin, DashboardGraphType.CARD);
    pgPerfBoutique.setColValeur(colValParMagasin);
    pgPerfBoutique.setIcon("bar_chart");
    pgPerfBoutique.setUnite(new String[]{" Ar"," "});
    pgPerfBoutique.setColSpan(3);
    pgPerfBoutique.setAvecDate(false);
    pgPerfBoutique.setLienPage("vente/vente-liste.jsp");
    pgPerfBoutique.setBackgroundColor(backgroundColor);
    pgPerfBoutique.setCouleurs(palettes);
    pgPerfBoutique.setCanvasId("graphPerfBoutique");
    pgPerfBoutique.creerGraph();

    String[] legendeCa = {"Chiffre d`affaire total"};
    String[] colGrEvoCa = {"daty"};
    DashboardGraphOpt pgCa = new DashboardGraphOpt(rsVenteDetailsCpl,null,legendeCa, DashboardGraphType.CARD);
    pgCa.setTitre("Chiffre d'affaire total");
    pgCa.setUnite(" AR");
    pgCa.setColSpan(12);
    pgCa.setIcon("shopping_cart");
    pgCa.setLienPage("vente/vente-liste.jsp");
    pgCa.setBackgroundColor(backgroundColor);
    pgCa.setCouleurs(palettes);
    pgCa.setCanvasId("caVentePointCanvas");
    pgCa.setColValeur(colValEvoCa);
    pgCa.creerGraph();

    String[] legendeNbVente = {"Nombre de ventes"};
    DashboardGraphOpt dgNbVente = new DashboardGraphOpt(rsVenteDetailsCpl,null, legendeNbVente, DashboardGraphType.CARD);
    dgNbVente.setTitre("Nombre total de vente");
    dgNbVente.setColSpan(12);
    dgNbVente.setIcon("shopping_cart");
    dgNbVente.setLienPage("vente/vente-liste.jsp");
    dgNbVente.setBackgroundColor(backgroundColor);
    dgNbVente.setCouleurs(palettes);
    dgNbVente.setCanvasId("nbVentePointCanvas");
    dgNbVente.setColValeur(colValNbVente);
    dgNbVente.creerGraph();

    String[] colValTest = {"montantttc"};
    String[] legendeEvolution = {"Chiffre d'affaire"};
    DashboardGraphOpt dgEvoCa = new DashboardGraphOpt(rsVenteDetailsCpl,colGrEvoCa,legendeEvolution,DashboardGraphType.LINE);
    dgEvoCa.setTitre("&Eacute;volution CA");
    dgEvoCa.setColValeur(colValTest);
    dgEvoCa.setColAffiche(colGrEvoCa);
    dgEvoCa.setIcon("trending_up");
    dgEvoCa.setUnite(" AR");
    dgEvoCa.setColSpan(12);
    dgEvoCa.setLienPage("vente/vente-liste.jsp");
    dgEvoCa.setBackgroundColor(backgroundColor);
    dgEvoCa.setCouleurs(palettes);
    dgEvoCa.setCanvasId("graphCaPeriodePoint");
    dgEvoCa.creerGraph();

    String[] legendeCat = {"Nombre de ventes"};
    String[] colGr = {"idCategorieModele"};
    DashboardGraphOpt dgVenteCat = new DashboardGraphOpt(rsVenteDetailsCpl,colGr,legendeCat,DashboardGraphType.DOUGHNUT);
    dgVenteCat.setTitre("R&eacute;partition des ventes par catégorie");
    dgVenteCat.setIcon("pie_chart");
    dgVenteCat.setColValeur(colValNbVente);
    dgVenteCat.setColSpan(4);
    dgVenteCat.setLienPage("vente/vente-liste.jsp");
    dgVenteCat.setBackgroundColor(backgroundColor);
    dgVenteCat.setCouleurs(palettes);
    dgVenteCat.setCanvasId("graphVenteParCategoriePoint");
    dgVenteCat.creerGraph();

    String[] colAffichePrd = {"idProduitLib"};
    String[] legendePrd = {"Nombre de ventes"};
    DashboardGraphOpt dgVenteProduit = new DashboardGraphOpt(rsVenteDetailsCpl,colAffichePrd,legendePrd,DashboardGraphType.BAR);
//    dgVenteProduit.setDateColonne("daty");
    dgVenteProduit.setColAffiche(colAfficheCat);
    dgVenteProduit.setTitre("Top 10 des produits les plus vendus");
    dgVenteProduit.setIcon("bar_chart");
    dgVenteProduit.setColSpan(8);
    dgVenteProduit.setLimite(10);
    dgVenteProduit.setOrdre("desc");
    dgVenteProduit.setOrientation("horizontal");
    dgVenteProduit.setLienPage("vente/vente-liste.jsp");
    dgVenteProduit.setBackgroundColor(backgroundColor);
    dgVenteProduit.setCouleurs(palettes);
    dgVenteProduit.setCanvasId("graphTopProduitsBarPoint");
    dgVenteProduit.setColValeur(colValNbVente);
    dgVenteProduit.creerGraph();

%>

<div class="content-wrapper">
    <section class="content-header">
        <h1>Tableau de bord par point de vente</h1>
    </section>
    <section class="content">
        <form action="<%=lien%>?but=<%=pr[0].getApres()%>" method="post">
            <%=pr[0].getFormu().getHtmlEnsemble()%>
        </form>
        <div class="row p-3">
            <div class="col-md-6">
                <%=pgCaTotal.getHtml()%>
                <%=pgCa.getHtml()%>
                <%=dgNbVente.getHtml()%>
            </div>

            <div class="col-md-6">
                <%=dgEvoCa.getHtml()%>
            </div>
            <div class="col-md-12 nopadding">
                <h2 class="col-md-12 h520pxSemibold">Performance par boutique</h2>
                <%=pgPerfBoutique.getHtml()%>
            </div>
            <div class="col-md-12 nopadding">
                <h2 class="col-md-12 h520pxSemibold">Performance des produits</h2>
                <div class="col-md-12 mb-5"></div>
                <%=dgVenteCat.getHtml()%>
                <%=dgVenteProduit.getHtml()%>
            </div>
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
<% } catch (Exception e) {
    e.printStackTrace(); %>
<script language="JavaScript"> alert('<%=e.getMessage()%>');
history.back();
</script>
<% }%>