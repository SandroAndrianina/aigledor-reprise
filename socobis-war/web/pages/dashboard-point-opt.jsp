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
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script src="${pageContext.request.contextPath}/chartPlugins/Chart.min.js"></script>

<% try {
    String lien = (String) session.getAttribute("lien");
    UserEJB u = (user.UserEJB) session.getAttribute("u");
    Point pointDeVente = u.getPointEncours();
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
    // ================================================================
    // MAPPING
    VenteCa venteCa = new VenteCa();
    venteCa.setNomTable("vente_ca_ameliore");
    VenteDetailsCplCategorie venteDetailsCpl = new VenteDetailsCplCategorie();
    venteDetailsCpl.setNomTable("VENTE_DETAILS_CAT");
    EtatCaisseMagasin etatCaisse = new EtatCaisseMagasin();
    etatCaisse.setNomTable("V_ETAT_CAISSE_MAGASIN");

    String[] colValNbVente = {"nb"};
    String[] colAfficheCat = {"categorieIngredient","idProduitLib"};
    String[] criterePoint = {"daty", "idMagasinLib"};
    String[] critereIntPoint = {"daty"};

    // ================================================================
    // INSTANCIATION PAGE RECHERCHE
    PageRecherche[] pr = new PageRecherche[3];
    pr[0] = new PageRecherche(venteDetailsCpl, request, criterePoint, critereIntPoint, 4, paramVide, paramVide.length);
    pr[0].setUtilisateur((user.UserEJB) session.getAttribute("u"));
    pr[0].setNpp(9999);
    pr[0].getFormu().getChamp("daty1").setDefaut(Utilitaire.getDebutAnnee(anneeCourante));
    pr[0].getFormu().getChamp("daty2").setDefaut(dateDuJour);
    pr[0].getFormu().getChamp("idMagasinLib").setDefaut(pointDeVente.getVal());
    pr[0].getFormu().getChamp("daty1").setLibelle("Date Min");
    pr[0].getFormu().getChamp("daty2").setLibelle("Date Max");
    pr[0].getFormu().getChamp("idMagasinLib").setLibelle("Point de vente");
    String[] colAfficheCaisse = {"idcaisseLib"};
    String[] colValCaisse = {"credit", "debit", "reste"};
    String[] legendeCaisse = {"Somme Crédit", "Somme Débit", "Reste"};
    pr[1] = new PageRecherche(etatCaisse, request,  new String[]{"idPointlib"}, paramVide, 4, paramVide, paramVide.length);
    pr[1].setUtilisateur((user.UserEJB) session.getAttribute("u"));
    pr[1].setNpp(9999);
    pr[1].getFormu().getChamp("idPointlib").setDefaut(pointDeVente.getVal());

    pr[2] = new PageRecherche(venteDetailsCpl, request, criterePoint, critereIntPoint, 4, paramVide, paramVide.length);
    pr[2].setUtilisateur((user.UserEJB) session.getAttribute("u"));
    pr[2].setNpp(9999);
    pr[2].getFormu().getChamp("daty1").setDefaut(dateDuJour);
    pr[2].getFormu().getChamp("daty2").setDefaut(dateDuJour);
    pr[2].getFormu().getChamp("idMagasinLib").setDefaut(pointDeVente.getVal());
    pr[2].getFormu().getChamp("daty1").setLibelle("Date Min");
    pr[2].getFormu().getChamp("daty2").setLibelle("Date Max");
    pr[2].getFormu().getChamp("idMagasinLib").setLibelle("Point de vente");
    // PREPARER DATA
    PageRecherche.creerObjetPage(pr);
    ResultatEtSomme rsVenteDetailsCpl = pr[0].getRs();
    ResultatEtSomme rsCaisse = pr[1].getRs();
    ResultatEtSomme rsVenteDetailsCplJour = pr[2].getRs();
    // ================================================================
    // --------- GRAPHIQUES ---------
    String[] legendeCa = {"Chiffre d`affaire total"};
    String[] colGrEvoCa = {"daty"};
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

    DashboardGraphOpt pgCaisse  = new DashboardGraphOpt(rsCaisse,colAfficheCaisse, legendeCaisse, DashboardGraphType.CARD);
    pgCaisse.setColValeur(colValCaisse);
    pgCaisse.setIcon("account_balance_wallet");
    pgCaisse.setUnite(new String[]{" Ar"," Ar"," Ar"});
    pgCaisse.setColSpan(3);
    pgCaisse.setAvecDate(false);
    pgCaisse.setLienPage("caisse/caisse-liste.jsp");
    pgCaisse.setBackgroundColor(backgroundColor);
    pgCaisse.setCouleurs(palettes);
    pgCaisse.setCanvasId("graphEtatCaissePoint");
    pgCaisse.creerGraph();

    DashboardGraphOpt pgCa = new DashboardGraphOpt(rsVenteDetailsCpl,null,legendeCa, DashboardGraphType.CARD);
    pgCa.setTitre("Chiffre d'affaire total " + pointDeVente.getVal());
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
    dgNbVente.setTitre("Ventes pour " + pointDeVente.getVal());
    dgNbVente.setColSpan(12);
    dgNbVente.setIcon("shopping_cart");
    dgNbVente.setLienPage("vente/vente-liste.jsp");
    dgNbVente.setBackgroundColor(backgroundColor);
    dgNbVente.setCouleurs(palettes);
    dgNbVente.setCanvasId("nbVentePointCanvas");
    dgNbVente.setColValeur(colValNbVente);
    dgNbVente.creerGraph();

    String[] colValTest = {"montantttc"};
    String[] legendeEvolution = {"Chiffre d'affaire total"};
    DashboardGraphOpt dgEvoCa = new DashboardGraphOpt(rsVenteDetailsCpl,colGrEvoCa,legendeEvolution,DashboardGraphType.LINE);
    dgEvoCa.setTitre("Evolution du CA - " + pointDeVente.getVal());
    dgEvoCa.setColValeur(colValTest);
    dgEvoCa.setColAffiche(colGrEvoCa);
    dgEvoCa.setIcon("trending_up");
    dgEvoCa.setUnite(" Ar");
    dgEvoCa.setColSpan(12);
    dgEvoCa.setLienPage("vente/vente-liste.jsp");
    dgEvoCa.setBackgroundColor(backgroundColor);
    dgEvoCa.setCouleurs(palettes);
    dgEvoCa.setCanvasId("graphCaPeriodePoint");
    dgEvoCa.creerGraph();

    String[] legendeCat = {"Nombre de ventes"};
    String[] colGr = {"categorieIngredient"};
    DashboardGraphOpt dgVenteCat = new DashboardGraphOpt(rsVenteDetailsCpl,colGr,legendeCat,DashboardGraphType.DOUGHNUT);
    dgVenteCat.setTitre("Ventes par catégorie");
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
    dgVenteProduit.setDateColonne("daty");
    dgVenteProduit.setColAffiche(colAfficheCat);
    dgVenteProduit.setTitre("Top 10 produits vendus");
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
            <h2 class="col-md-12 h520pxSemibold">Performance du jour</h2>
            <div class="col-md-6">
                <%=pgCaTotal.getHtml()%>
                <%=pgCa.getHtml()%>
                <%=dgNbVente.getHtml()%>
            </div>

            <div class="col-md-6">
                <%=dgEvoCa.getHtml()%>
            </div>
            <div class="col-md-12 nopadding">
                <h2 class="col-md-12 h520pxSemibold">Situation de caisse</h2>
                <%=pgCaisse.getHtml()%>
            </div>
            <div class="col-md-12 nopadding">
                <h2 class="col-md-12 h520pxSemibold">Performance boutique</h2>
                <div class="col-md-12 mb-5"></div>
                <%=dgVenteProduit.getHtml()%>
                <%=dgVenteCat.getHtml()%>
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