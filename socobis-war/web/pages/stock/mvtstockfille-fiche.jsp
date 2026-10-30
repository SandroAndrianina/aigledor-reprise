<%@page import="stock.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="user.*" %>
<%@ page import="bean.*" %>
<%@ page import="utilitaire.*" %>
<%@ page import="affichage.*" %>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>

<%
    try{
    UserEJB u = (user.UserEJB)session.getValue("u");
    String lien = (String) session.getValue("lien");

%>
<%
    MvtStockFille mvt = new MvtStockFilleLib();
    mvt.setNomTable("mvtstockfillelib");
    PageConsulte pc = new PageConsulte(mvt, request, u);
    pc.setTitre("Fiche mouvement de stock fille");
    pc.getBase();
    String id=pc.getBase().getTuppleID();
    pc.getChampByName("id").setLibelle("Id");
    pc.getChampByName("idProduit").setLibelle("Id Produit");
    pc.getChampByName("idProduit").setLien(lien+"?but=produits/as-ingredients-fiche.jsp", "id=");
    pc.getChampByName("idProduitLib").setLibelle("D&eacute;signation");
    pc.getChampByName("idMagasinLib").setLibelle("Magasin");
    pc.getChampByName("idMvtStock").setLibelle("Id mouvement de stock");
    pc.getChampByName("idMvtStock").setLien(lien+"?but=stock/mvtstock-fiche.jsp", "id=");
    pc.getChampByName("entree").setLibelle("Entr&eacute;e");
    pc.getChampByName("daty").setLibelle("Date");
    pc.getChampByName("pu").setLibelle("Prix unitaire");
    pc.getChampByName("mvtsrc").setLibelle("Mouvement source");
    pc.getChampByName("IdTransfertDetaillib").setVisible(false);
    pc.getChampByName("IdVenteDetail").setVisible(false);
    pc.getChampByName("IdVenteDetaillib").setVisible(false);
    pc.getChampByName("IdTransfertDetail").setVisible(false);
    pc.getChampByName("IdObjet").setVisible(false);
    pc.getChampByName("DateSql").setVisible(false);
    pc.getChampByName("Libelleexacte").setVisible(false);
    pc.getChampByName("IdMagasin").setVisible(false);
    pc.getChampByName("libelle").setVisible(false);
     String pageActuel = "stock/mvtstockfille-fiche.jsp";
    Map<String, String> map = new HashMap<String, String>();
    map.put("mvt-stock-fille", "");

    String tab = request.getParameter("tab");
    if (tab == null) {
        tab = "mvt-stock-fille";
        //tab = "liste-inventaire-fille";
    }
    map.put(tab, "active");
    tab = tab + ".jsp";
%>

<div class="content-wrapper">
    <div class="row">
        <div class="col-md-3"></div>
        <div class="col-md-6">
            <div class="box-fiche">
                <div class="box">
                    <div class="box-title with-border">
                        <h1 class="box-title"><a href=<%= lien + "?but=stock/mvtstockfille-liste.jsp"%> <i class="fa fa-arrow-circle-left"></i></a><%=pc.getTitre()%></h1>
                    </div>
                    <div class="box-body">
                        <%
                            out.println(pc.getHtml());
                        %>
                        <br/>
                        <div class="box-footer">
                           <a  style="margin-right: 10px" class="btn btn-primary pull-right" href="<%= lien %>?but=inventaire/inventaire-saisie.jsp&idmvtstockfille=<%= id %>">Faire inventaire</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row m-0" style="margin-top: 25px">
        <div class="col-md-12 nopadding">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <!-- a modifier -->
                    <li class="<%=map.get("mvt-stock-fille")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=mvt-stock-fille">Liste des mouvements de stock fille </a></li>
                    <li class="<%=map.get("liste-inventaire-fille")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=liste-inventaire-fille">Liste des inventaires fille</a></li>
                </ul>
                <div class="tab-content">
                    <jsp:include page="<%= tab %>" >
                        <jsp:param name="idmvtstock" value="<%= id %>" />
                    </jsp:include>
                </div>
            </div>

        </div>
    </div>      
    </div>
</div>
<%
} catch (Exception e) {
    e.printStackTrace();
}%>
