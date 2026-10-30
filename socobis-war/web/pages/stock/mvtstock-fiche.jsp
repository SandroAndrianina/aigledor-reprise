<%@page import="utils.ConstanteSocobis"%>
<%@page import="stock.MvtStockLib"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="user.*" %>
<%@ page import="affichage.*" %>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@ page import="utilitaire.Utilitaire" %>

<%
    try {
        UserEJB u = (user.UserEJB)session.getValue("u");

%>
<%
    String lien = (String) session.getValue("lien");
    MvtStockLib unite = new MvtStockLib();
    PageConsulte pc = new PageConsulte(unite, request, u);
    pc.setTitre("Fiche du mouvement de stock");
    unite = (MvtStockLib) pc.getBase();
    String id=pc.getBase().getTuppleID();
    pc.getChampByName("Id").setLibelle("Id");
    pc.getChampByName("designation").setLibelle("D&eacute;signation");
    pc.getChampByName("idMagasinlib").setLibelle("Magasin");
    pc.getChampByName("idVentelib").setVisible(false);
    pc.getChampByName("idVente").setLibelle("Vente");
    pc.getChampByName("idTransfertlib").setLibelle("Transfert");
    pc.getChampByName("idTypeMvStocklib").setLibelle("Type de mouvement de stock");
    pc.getChampByName("daty").setLibelle("Date");
    pc.getChampByName("etat").setLibelle("&Eacute;tat");
    pc.getChampByName("montantEntree").setLibelle("Montant d'entr&eacute;e");
    pc.getChampByName("montantSortie").setLibelle("Montant de sortie");
    pc.getChampByName("idCategorieStockLib").setLibelle("Cat&eacute;gorie de Stock");
//    pc.getChampByName("idCategorieStockLib").setVisible(false);
    pc.getChampByName("idCategorieStock").setVisible(false);
    pc.getChampByName("idMagasin").setVisible(false);
//    pc.getChampByName("idVente").setVisible(false);
    pc.getChampByName("idTransfert").setVisible(false);
    pc.getChampByName("idTypeMvStock").setVisible(false);
    pc.getChampByName("etatLib").setVisible(false);
    //pc.getChampByName("motsclesss").setVisible(false);
//    String[] ordre={"id","designation","daty","idventelib","idTransfertlib","idTypeMvStocklib","idMagasinlib","etat","montantEntree","montantSortie"};
//    pc.getFormu().setOrdre(ordre);
    String idobjet = pc.getChampByName("idobjet").getValeur();
    if(idobjet!=null && idobjet.startsWith("FAB")) {
        pc.getChampByName("idobjet").setLibelle("Fabrication associé");
        pc.getChampByName("idobjet").setLien(lien+"?but=fabrication/fabrication-fiche.jsp", "id=");
    } else if(idobjet!=null && idobjet.startsWith("BLC")) {
        pc.getChampByName("idobjet").setLibelle("Origine");
        pc.getChampByName("idobjet").setLien(lien+"?but=bondelivraison-client/bondelivraison-client-fiche.jsp", "id=");
    } else if(idobjet!=null && idobjet.startsWith("BL")){
        pc.getChampByName("idobjet").setLibelle("Bon de r&eacute;ception associ&eacute;");
        pc.getChampByName("idobjet").setLien(lien+"?but=bondelivraison/bondelivraison-fiche.jsp", "id=");
    } else if (idobjet!=null && idobjet.startsWith("IVT")) {
        pc.getChampByName("idobjet").setLibelle("Inventaire associ&eacute;");
        pc.getChampByName("idobjet").setLien(lien+"?but=inventaire/inventaire-fiche.jsp", "id=");
    } else if (idobjet!=null && idobjet.startsWith("VNT")) {
        pc.getChampByName("idVente").setVisible(false);
        pc.getChampByName("idobjet").setLibelle("Origine");
        pc.getChampByName("idobjet").setLien(lien+"?but=vente/vente-fiche.jsp", "id=");
    } else{
        pc.getChampByName("idobjet").setVisible(false);
    }

    String[] colOrdre = new String[] {"daty", "heure", "designation", "idTypeMvStocklib", "idCategorieStockLib", "idobjet", "idVentelib", "idTransfertlib", "idMagasinlib", "montantEntree", "montantSortie", "etat"};
    pc.setOrdre(colOrdre);

    unite=(MvtStockLib)pc.getBase();
    String pageActuel = "stock/mvtstock-fiche.jsp";

    String pageModif = "stock/mvtstock-saisie.jsp&acte=update";
    String classe = "stock.MvtStock";
    // Contexte serveur du fragment Historique : ne pas lire ces valeurs depuis ses filtres.
    request.setAttribute("historiqueRefObjet", pc.getBase().getTuppleID());
    request.setAttribute("historiqueObjets", new String[] {classe});
    request.setAttribute("historiqueFiche", "stock/mvtstock-fiche.jsp");
    request.setAttribute("historiqueOnglet", "../historique/inc/historique-liste");


    Map<String, String> map = new HashMap<String, String>();
    map.put("mvtfille-liste", "");
    map.put("../compta/ecriture/inc/sous-ecriture-detail","");
    map.put("../historique/inc/historique-liste", "");

    String tab = request.getParameter("tab");
    if (tab == null) {
        tab = "mvtfille-liste";
    }
    map.put(tab, "active");
    tab = tab + ".jsp";

    int etat = unite.getEtat();

    System.out.println("HEURE ===> " + Utilitaire.heureCouranteHM());
%>

<div class="content-wrapper">
    <h1 class="box-title"><a href=<%= lien + "?but=stock/mvtstock-liste.jsp"%>> <i class="fa fa-angle-left"></i></a><%=pc.getTitre()%></h1>
    <div class="row m-0">
        <div class="col-md-6" >
            <div class="box-fiche">
                <div class="box">

                    <div class="box-body " >
                        <%
                            out.println(pc.getHtml());
                        %>
                        <div class="box-footer">
                            <% if(etat < 11 && etat!=0) {
                            %>
                            <a class="btn btn-primary pull-right" href="<%= (String) session.getValue("lien") + "?but=apresTarif.jsp&acte=valider&id=" + request.getParameter("id") + "&bute=stock/mvtstock-fiche.jsp&classe=" + classe%> " style="margin-right: 10px">Viser</a>
                            <a class="btn btn-secondary pull-right"  href="<%= lien + "?but="+ pageModif +"&id=" + id%>" style="margin-right: 10px">Modifier</a>
                            <a  class="pull-left btn btn-danger" href="<%= lien + "?but=apresTarif.jsp&id=" + id+"&acte=delete&bute=annexe/unite/unite-liste.jsp&classe="+classe %>">Supprimer</a>
                            <% } if(etat == 11){%>
                            <% if (unite.getIdCategorieStock()!=null && unite.getIdCategorieStock().compareToIgnoreCase(ConstanteSocobis.ID_CATEGORIESTOCK_DECHETS) == 0 && unite.getIdTypeMvStock().compareToIgnoreCase(ConstanteSocobis.TYPE_MVT_ENTREE) == 0) { %>
                            <a  style="margin-right: 10px" class="btn btn-secondary pull-right" href="<%= lien %>?but=apresJeter.jsp&id=<%= id %>">Jeter </a>
                            <% } %>
                            <% } %>
                            <a class="btn btn-tertiary pull-right" href="<%= (String) session.getValue("lien") + "?but=pageupload.jsp&id=" + id  + "&dossier=" + pc.getBase().getClass().getSimpleName() + "&nomtable=ATTACHER_FICHIER&procedure=GETSEQ_ATTACHER_FICHIER&bute=" + pageActuel + "&id=" + id %>" style="margin-right: 10px;">Attacher Fichier</a>
                        </div>
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
                    <li class="<%=map.get("mvtfille-liste")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=mvtfille-liste">Mouvement détails</a></li>
                    <li class="<%=map.get("../compta/ecriture/inc/sous-ecriture-detail")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=../compta/ecriture/inc/sous-ecriture-detail">&Eacute;criture</a></li>
                    <li class="<%=map.get("../historique/inc/historique-liste")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=../historique/inc/historique-liste">Historique</a></li>
                </ul>
                <div class="tab-content">
                    <jsp:include page="<%= tab %>" >
                        <jsp:param name="idmvtstock" value="<%= id %>" />
                    </jsp:include>
                </div>
            </div>

        </div>
    </div>
    <%=pc.getHtmlAttacherFichier()%>
</div>

<%
    } catch (Exception e) {
        e.printStackTrace();
    }%>

