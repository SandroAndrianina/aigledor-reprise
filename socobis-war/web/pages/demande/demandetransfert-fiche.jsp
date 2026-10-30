<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="user.*" %>
<%@ page import="utilitaire.*" %>
<%@ page import="affichage.*" %>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@ page import="demande.DemandeTransfertCpl" %>
<%@ page import="utils.ConstanteSocobis" %>
<%
    UserEJB u = (user.UserEJB)session.getValue("u");
    try {
        String lien = (String) session.getValue("lien");
        DemandeTransfertCpl unite = new DemandeTransfertCpl();
        PageConsulte pc = new PageConsulte(unite, request, u);
        pc.setTitre("Fiche de demande de transfert de stock");
        unite=(DemandeTransfertCpl)pc.getBase();
        String id=pc.getBase().getTuppleID();
        pc.getChampByName("Id").setLibelle("Id");
        pc.getChampByName("designation").setLibelle("D&eacute;signation");
        pc.getChampByName("etat").setLibelle("&Eacute;tat");
        pc.getChampByName("idMagasinDepartlib").setLibelle("Magasin de d&eacute;part");
        pc.getChampByName("idMagasinDepart").setVisible(false);
        pc.getChampByName("idMagasinArrivelib").setLibelle("Magasin d'arriv&eacute;e");
        pc.getChampByName("idMagasinArrive").setVisible(false);
        pc.getChampByName("etatlib").setVisible(false);
        pc.getChampByName("daty").setLibelle("Date");
        pc.getChampByName("categorieingredient").setVisible(false);
        pc.getChampByName("categorieIngredientLib").setLibelle("Cat&eacute;gorie");
        
        pc.getChampByName("idOf").setLibelle("Ordre de fabrication associé");
        pc.getChampByName("idOf").setVisible(false);
        String idOf = pc.getChampByName("idOf").getValeur();
        if(!Utilitaire.champNull(idOf).isEmpty()){
            pc.getChampByName("idOf").setLien(lien+"?but=fabrication/ordre-fabrication-fiche.jsp", "id=");
        }

        String pageActuel = "demande/demandetransfert-saisie.jsp";

        String pageModif = "demande/demandetransfert-saisie.jsp";
        String classe = "demande.DemandeTransfert";

        Map<String, String> map = new HashMap<String, String>();
        map.put("demandetransfertstockdetails-liste", "");

        String tab = request.getParameter("tab");
        if (tab == null) {
            tab = "demandetransfertstockdetails-liste";
        }
        map.put(tab, "active");
        tab ="inc/"+ tab + ".jsp";
%>

<div class="content-wrapper">
        <h1 class="box-title"><a href=<%= lien + "?but=demande/demandetransfert-liste.jsp"%> <i class="fa fa-arrow-circle-left"></i></a><%=pc.getTitre()%></h1>
    <div class="row">
        <div class="col-md-12 mb-5" >
            <div class="box-fiche">
                <div class="box">
                    <div class="box-body " style="margin:20px">
                        <%
                            out.println(pc.getHtml());
                        %>
                        <br/>
                        <div class="box-footer">
                            <% if (unite.getEtat() < 11) { %>
                            <a class="btn btn-primary pull-right" href="<%= (String) session.getValue("lien") + "?but=apresTarif.jsp&acte=valider&id=" + request.getParameter("id") + "&bute=demande/demandetransfert-fiche.jsp&classe=" + classe %> " style="margin-right: 10px">Viser</a>
                            <a class="btn btn-secondary pull-right"  href="<%= lien + "?but="+ pageModif +"&acte=update&id=" + id%>" style="margin-right: 10px">Modifier</a>
                            <a class="pull-left btn btn-danger" href="<%= lien + "?but=apresTarif.jsp&id=" + id + "&acte=annuler&bute=demande/demandetransfert-fiche.jsp&classe=" + classe%>">Annuler</a>
                            <% } %>
                            <% if (unite.getEtat() >= 11) { %>
                                <% if(u.getUser().getIdrole().compareTo(ConstanteSocobis.MAGCENTRAL_RANG)==0){ %>
                                    <% if (unite.getIdOf() != null && !unite.getIdOf().isEmpty()) { %>
                                    <a class="btn btn-warning pull-right"  href="<%= lien + "?but=stock/transfertstock/transfertstock-saisie.jsp&idDemande=" + unite.getId() + "&idOrderFabrication=" + unite.getIdOf()%>" style="margin-right: 10px">Transf&eacute;rer avec OF</a>
                                    <% } %>
                                <% } %>
                                  <a class="btn btn-warning pull-right"  href="<%= lien + "?but=stock/transfertstock/transfertstock-saisie.jsp&idDemande=" + unite.getId()%>" style="margin-right: 10px">Transf&eacute;rer </a>

                            <% } %>
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
                    <li class="<%=map.get("demandetransfertstockdetails-liste")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=demandetransfertstockdetails-liste">Détails</a></li>
                </ul>
                <div class="tab-content">
                    <jsp:include page="<%= tab %>" >
                        <jsp:param name="id" value="<%= id %>" />
                    </jsp:include>
                </div>
            </div>

        </div>
    </div>
</div>

<%
    }catch(Exception e){

        e.printStackTrace();
    }
%>