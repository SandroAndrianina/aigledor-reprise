<%@page import="user.UserEJB"%>
<%@ page import="affichage.*" %>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@ page import="faturefournisseur.DemandePrixLib" %>
<%@ page import="utilitaire.ConstanteEtat" %>
<%
    try{
        UserEJB u = (user.UserEJB)session.getValue("u");
        String lien = (String) session.getValue("lien");
        DemandePrixLib t = new DemandePrixLib();
        t.setNomTable("DEMANDE_PRIX_LIB");
        PageConsulte pc = new PageConsulte(t, request, (user.UserEJB) session.getValue("u"));
        t = (DemandePrixLib) pc.getBase();
        String id = pc.getBase().getTuppleID();
        pc.getChampByName("id").setLibelle("ID");
        pc.getChampByName("designation").setLibelle("D&eacute;signation");
        pc.getChampByName("daty").setLibelle("Date");
        pc.getChampByName("acheteur").setLibelle("Acheteur");
        pc.getChampByName("etat").setVisible(false);
        pc.getChampByName("etatlib").setLibelle("&Eacute;tat");
        String[] ordre = new String[]{"daty","designation","acheteur","etatlib"};
        pc.setOrdre(ordre);

        pc.setTitre("Fiche de demande de prix");
        String pageModif = "facturefournisseur/dmdachat/demandeprix-saisie.jsp";
        String pageActuel = "facturefournisseur/dmdachat/demandeprix-fiche.jsp";

        Map<String, String> map = new HashMap<String, String>();
        map.put("inc/demandeprix-details", "");
        map.put("inc/proformaachat-liste", "");
        map.put("inc/dmdachat-liste", "");

        String tab = request.getParameter("tab");
        if (tab == null) {
            tab = "inc/demandeprix-details";
        }
        map.put(tab, "active");
        tab = tab + ".jsp";

        DemandePrixLib dp = (DemandePrixLib) pc.getBase();
        int etat = dp.getEtat();
        boolean afficherViser = etat == ConstanteEtat.getEtatCreer();
        boolean afficherGenererProforma = etat == ConstanteEtat.getEtatValider();

        String dossier = "DemandePrixLib";
%>
<div class="content-wrapper">
    <h1 class="box-title"><a href=<%= lien + "?but=facturefournisseur/dmdachat/demandeprix-liste.jsp"%> ><i class="fa fa-angle-left"></i></a><% out.println(pc.getTitre()); %></h1>
    <div class="row m-0">
        <div class="col-md-3"></div>
        <div class="col-md-6">
            <div class="box-fiche">
                <div class="box">
                    <div class="box-body">
                        <%
                            out.println(pc.getHtml());
                        %>
                        <div class="box-footer">
                            <% if (afficherViser) { %>
                            <a class="btn btn-primary pull-right" href="<%= lien + "?but=apresTarif.jsp&acte=valider&id=" + id + "&bute=facturefournisseur/dmdachat/demandeprix-fiche.jsp&classe=faturefournisseur.DemandePrix&nomtable=DEMANDE_PRIX"%> " style="margin-right: 10px">Viser</a>
                            <a class="btn btn-secondary pull-right" href="<%= lien + "?but="+ pageModif +"&id=" + id +"&acte=update"%>" style="margin-right: 10px">Modifier</a>
                            <% } %>
                            <% if (afficherGenererProforma) { %>
                            <a class="btn btn-secondary pull-right" href="<%= lien + "?but=facturefournisseur/proformaachat/proformaachat-saisie.jsp&iddemandeprix=" + id %>" style="margin-right: 10px">G&eacute;n&eacute;rer Proforma</a>
                            <a class="btn btn-tertiary pull-right"
                               href="<%= (String) session.getValue("lien") + "?but=pageupload.jsp&id=" + id + "&dossier=" + dossier + "&nomtable=ATTACHER_FICHIER&procedure=GETSEQ_ATTACHER_FICHIER&bute=" + pageActuel + "&id=" + id %>"
                               style="margin-right:10px;">Attacher Fichier</a>
                            <% } %>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row m-0">
        <div class="col-md-12 nopadding">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <li class="<%=map.get("inc/demandeprix-details")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/demandeprix-details">D&eacute;tails</a></li>
                    <li class="<%=map.get("inc/proformaachat-liste")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/proformaachat-liste">Comparaison de prix</a></li>
                    <li class="<%=map.get("inc/dmdachat-liste")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/dmdachat-liste">Demandes d'achat</a></li>
                </ul>
                <div class="tab-content">
                    <jsp:include page="<%= tab %>" >
                        <jsp:param name="id" value="<%= id %>" />
                    </jsp:include>
                </div>
            </div>

        </div>
    </div>
    <% out.println(pc.getHtmlAttacherFichier());%>

</div>
<%
    } catch (Exception e) {
        e.printStackTrace();
    } %>
