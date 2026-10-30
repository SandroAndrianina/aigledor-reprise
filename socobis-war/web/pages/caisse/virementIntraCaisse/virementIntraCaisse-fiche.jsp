<%--
    Document   : page-fiche-simple
    Created on : 9 mars 2023, 10:08:42
    Author     : BICI
--%>

<%@page import="constante.ConstanteEtat"%>
<%@page import="caisse.VirementIntraCaisseCpl"%>
<%@ page import="affichage.*" %>

<%
    try{
    VirementIntraCaisseCpl t = new VirementIntraCaisseCpl();
    t.setNomTable("VirementIntraCaisseCpl");
    PageConsulte pc = new PageConsulte(t, request, (user.UserEJB) session.getValue("u"));
    String id=pc.getBase().getTuppleID( );
    pc.getChampByName("id").setLibelle("Id");
    pc.getChampByName("designation").setLibelle("D&eacute;signation");
    pc.getChampByName("idCaisseDepartLib").setLibelle("Caisse de d&eacute;part");
        pc.getChampByName("idCaisseArriveLib").setLibelle("Caisse d'arriv&eacute;e");
        pc.getChampByName("etat").setLibelle("&Eacute;tat");
        pc.getChampByName("daty").setLibelle("Date");
    pc.getChampByName("idCaisseDepart").setVisible(false);
        pc.getChampByName("idCaisseArrive").setVisible(false);
        pc.getChampByName("etatLib").setVisible(false);
    pc.setTitre("Fiche virement intra caisse ");
    String lien = (String) session.getValue("lien");
    String pageModif = "caisse/virementIntraCaisse/virementIntraCaisse-saisie.jsp";
    String classe = "caisse.VirementIntraCaisse";
    // Contexte serveur du fragment Historique : ne pas lire ces valeurs depuis ses filtres.
    request.setAttribute("historiqueRefObjet", pc.getBase().getTuppleID());
    request.setAttribute("historiqueObjets", new String[] {classe});
    request.setAttribute("historiqueFiche", "caisse/virementIntraCaisse/virementIntraCaisse-fiche.jsp");
    request.setAttribute("historiqueOnglet", "../../historique/inc/historique-liste");

    String pageActuel = "caisse/virementIntraCaisse/virementIntraCaisse-fiche.jsp";
    java.util.Map<String, String> map = new java.util.HashMap<String, String>();
    String tab = "../../historique/inc/historique-liste";
    map.put("../../historique/inc/historique-liste", "");
    map.put(tab, "active");
    tab = tab + ".jsp";

    t=(VirementIntraCaisseCpl)pc.getBase();
%>
<div class="content-wrapper">
    <h1 class="box-title"><a href="<%= lien + "?but=caisse/virementIntraCaisse/virementIntraCaisse-liste.jsp"%>"><i class="fa fa-angle-left"></i></a><%=pc.getTitre()%></h1>
    <div class="row">
        <div class="col-md-12">
            <div class="box-fiche">
                <div class="box">
                    <div class="box-body">
                        <%
                            out.println(pc.getHtml());
                        %>
                        <br/>
                        <div class="box-footer">
                            <% if (t.getEtat()!=ConstanteEtat.getEtatValider()) { %>
                                    <a class="btn btn-primary pull-right" href="<%= (String) session.getValue("lien") + "?but=apresTarif.jsp&acte=valider&id=" + request.getParameter("id") + "&bute=caisse/virementIntraCaisse/virementIntraCaisse-fiche.jsp&classe=" + classe %> " style="margin-right: 10px">Viser</a>
                                    <a class="btn btn-secondary pull-right"  href="<%= lien + "?but="+ pageModif +"&acte=update&id=" + id%>" style="margin-right: 10px">Modifier</a>
                                    <a class="btn btn-danger pull-left" href="<%= lien + "?but=apresTarif.jsp&id=" + id+"&acte=delete&bute=caisse/virementIntraCaisse/virementIntraCaisse-liste.jsp&classe="+classe+"&nomtable=VirementIntraCaisse" %>">Supprimer</a>
                         <%    }  %>

                        </div>
                        <br/>

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


<%
} catch (Exception e) {
    e.printStackTrace();
} %>


