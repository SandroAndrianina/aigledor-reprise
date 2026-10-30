<%@page import="stock.TransfertStockCpl"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="user.*" %>
<%@ page import="bean.*" %>
<%@ page import="utilitaire.*" %>
<%@ page import="affichage.*" %>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>

<%
    UserEJB u = (user.UserEJB)session.getValue("u");
    try {
        String lien = (String) session.getValue("lien");
        TransfertStockCpl unite = new TransfertStockCpl();
        PageConsulte pc = new PageConsulte(unite, request, u);
        pc.setTitre("Fiche transfert de stock");
        pc.getBase();
        unite=(TransfertStockCpl)pc.getBase();
        String id=pc.getBase().getTuppleID();
        pc.getChampByName("Id").setLibelle("Id");
        pc.getChampByName("designation").setLibelle("D&eacute;signation");
        pc.getChampByName("idMagasinDepartlib").setLibelle("Magasin de d&eacute;part");
        pc.getChampByName("idMagasinDepart").setVisible(false);
        pc.getChampByName("idMagasinArrivelib").setLibelle("Magasin d&apos;arriv&eacute;e");
        pc.getChampByName("idMagasinArrive").setVisible(false);
        pc.getChampByName("etatlib").setVisible(false);
        pc.getChampByName("daty").setLibelle("Date");
        pc.getChampByName("etat").setLibelle("&Eacute;tat");
        pc.getChampByName("idOf").setLibelle("Ordre de fabrication associé");
        String idOf = pc.getChampByName("idOf").getValeur();
        if(!Utilitaire.champNull(idOf).isEmpty()){
            pc.getChampByName("idOf").setLien(lien+"?but=fabrication/ordre-fabrication-fiche.jsp", "id=");
        }

        String pageActuel = "stock/transfertstock/transfertstock-fiche.jsp";

        String pageModif = "stock/transfertstock/transfertstock-saisie.jsp&acte=update";
        String classe = "stock.TransfertStock";
        // Contexte serveur du fragment Historique : ne pas lire ces valeurs depuis ses filtres.
        request.setAttribute("historiqueRefObjet", pc.getBase().getTuppleID());
        request.setAttribute("historiqueObjets", new String[] {classe});
        request.setAttribute("historiqueFiche", "stock/transfertstock/transfertstock-fiche.jsp");
        request.setAttribute("historiqueOnglet", "../../historique/inc/historique-liste");


        Map<String, String> map = new HashMap<String, String>();
        map.put("transfertstockdetails-liste", "");
        map.put("mvtstock-details", "");

        String tab = request.getParameter("tab");
        if (tab == null) {
            tab = "transfertstockdetails-liste";
        }
        map.put("../../historique/inc/historique-liste", "");
        map.put(tab, "active");
        tab = ("../../historique/inc/historique-liste".equals(tab) ? tab : "inc/" + tab) + ".jsp";
%>

<div class="content-wrapper">
    <h1 class="box-title"><a href=<%= lien + "?but=stock/mvtstock-liste.jsp"%> <i class="fa fa-arrow-circle-left"></i></a><%=pc.getTitre()%></h1>
    <div class="row">
        <div class="col-md-12 mb-5" >
            <div class="box-fiche">
                <div class="box">
                    <div class="box-body ">
                        <%
                            out.println(pc.getHtml());
                        %>
                        <br/>
                        <div class="box-footer">
                            <% if (unite.getEtat() < 11) { %>
                            <a class="btn btn-primary pull-right" href="<%= (String) session.getValue("lien") + "?but=apresTarif.jsp&acte=valider&id=" + request.getParameter("id") + "&bute=stock/transfertstock/transfertstock-fiche.jsp&classe=" + classe %> " style="margin-right: 10px">Viser</a>
                            <a class="btn btn-secondary pull-right"  href="<%= lien + "?but="+ pageModif +"&id=" + id%>" style="margin-right: 10px">Modifier</a>
                            <a  class="btn btn-danger pull-left" href="<%= lien + "?but=apresTarif.jsp&id=" + id+"&acte=delete&bute=annexe/unite/unite-liste.jsp&classe="+classe %>">Supprimer</a>
                            <% } %>
                            <a class="btn btn-tertiary pull-right" href="<%= (String) session.getValue("lien") + "?but=pageupload.jsp&id=" + id  + "&dossier=" + pc.getBase().getClass().getSimpleName() + "&nomtable=ATTACHER_FICHIER&procedure=GETSEQ_ATTACHER_FICHIER&bute=" + pageActuel + "&id=" + id %>" style="margin-right: 10px;">Attacher Fichier</a>
                        </div>
                        <br/>

                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <!-- a modifier -->
                    <li class="<%=map.get("transfertstockdetails")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=transfertstockdetails-liste">Transfert détails</a></li>
                    <li class="<%=map.get("mvtstock-details")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=mvtstock-details">Mouvement de stock</a></li>
                    <li class="<%=map.get("../../historique/inc/historique-liste")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=../../historique/inc/historique-liste">Historique</a></li>
                </ul>
                <div class="tab-content">
                    <jsp:include page="<%= tab %>" >
                        <jsp:param name="idTransfertStock" value="<%= id %>" />
                    </jsp:include>
                </div>
            </div>

        </div>
    </div>
    <%=pc.getHtmlAttacherFichier()%>
</div>
</div>

<%
    }catch(Exception e){

        e.printStackTrace();
    }
%>