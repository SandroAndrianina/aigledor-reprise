
<%@page import="caisse.ReportCaisseCpl"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="user.*" %>
<%@ page import="bean.*" %>
<%@ page import="utilitaire.*" %>
<%@ page import="affichage.*" %>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>

<%
    UserEJB u = (user.UserEJB)session.getValue("u");

%>
<%
    ReportCaisseCpl caisse = new ReportCaisseCpl();
    PageConsulte pc = new PageConsulte(caisse, request, u);
    pc.setTitre("Fiche cloture de caisse");
    caisse=(ReportCaisseCpl) pc.getBase();
    String id=pc.getBase().getTuppleID();
    pc.getChampByName("idCaisseLib").setLibelle("Caisse");
    pc.getChampByName("daty").setLibelle("Date");
    pc.getChampByName("IdCaisse").setVisible(false);
    pc.getChampByName("typeReport").setVisible(false);
    pc.getChampByName("EtatLib").setVisible(false);
    pc.getChampByName("MontantTheorique").setLibelle("Montant th&eacute;orique");
    pc.getChampByName("etat").setLibelle("&Eacute;tat");
    String lien = (String) session.getValue("lien");
    String pageModif = "caisse/cloture/cloturecaisse-saisie.jsp";
    String classe = "caisse.ReportCaisse";
    // Contexte serveur du fragment Historique : ne pas lire ces valeurs depuis ses filtres.
    request.setAttribute("historiqueRefObjet", pc.getBase().getTuppleID());
    request.setAttribute("historiqueObjets", new String[] {classe});
    request.setAttribute("historiqueFiche", "caisse/cloture/cloturecaisse-fiche.jsp");
    request.setAttribute("historiqueOnglet", "../../historique/inc/historique-liste");

    Map<String, String> map = new HashMap<String, String>();
    map.put("inc/mvtcaisse-liste", "");

    String tab = request.getParameter("tab");
    if (tab == null) {
        tab = "../../historique/inc/historique-liste";
    }
    map.put("../../historique/inc/historique-liste", "");
    map.put(tab, "active");
    tab = tab + ".jsp";

    String pageActuel ="caisse/cloture/cloturecaisse-fiche.jsp";
%>

<div class="content-wrapper">
    <h1 class="box-title"><a href=<%= lien + "?but=caisse/cloture/cloturecaisse-liste.jsp"%>> <i class="fa fa-angle-left"></i></a><%=pc.getTitre()%></h1>
    <div class="row m-0">
        <div class="col-md-3"></div>
        <div class="col-md-6">
            <div class="box-fiche">
                <div class="box">
                    <div class="box-title with-border">
                    </div>
                    <div class="box-body">
                        <%
                            out.println(pc.getHtml());
                        %>
                        <div class="box-footer">
                            <% if(caisse.getEtat() < ConstanteEtat.getEtatValider()) { %>
                            <a class="btn btn-primary pull-right" href="<%= (String) session.getValue("lien") + "?but=apresTarif.jsp&acte=valider&id=" + request.getParameter("id") + "&bute=caisse/cloture/cloturecaisse-fiche.jsp&classe=" + classe %> " style="margin-right: 10px">Viser</a>
                            <% } %>
                             <% if(caisse.getEtat()== 11) { %>
                                <a class="pull-left btn btn-danger"  href="<%= (String) session.getValue("lien") + "?but=apresTarif.jsp&acte=annulerVisa&id=" + request.getParameter("id") + "&bute=caisse/cloture/cloturecaisse-fiche.jsp&classe=" + classe %>">Annuler Visa</a>
                            <% } %>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="row m-0">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <li class="<%=map.get("../../historique/inc/historique-liste")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=../../historique/inc/historique-liste">Historique</a></li>
                </ul>
                <div class="tab-content">
                    <jsp:include page="<%= tab %>" />
                </div>
            </div>
        </div>
    </div>
</div>

