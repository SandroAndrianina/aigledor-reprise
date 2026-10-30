<%@page import="user.UserEJB"%>
<%@ page import="affichage.*" %>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@ page import="faturefournisseur.ProformaAchatLib" %>
<%@ page import="utilitaire.ConstanteEtat" %>
<%
    try{
        UserEJB u = (user.UserEJB)session.getValue("u");
        String lien = (String) session.getValue("lien");
        ProformaAchatLib t = new ProformaAchatLib();
        t.setNomTable("PROFORMA_ACHAT_LIB");
        PageConsulte pc = new PageConsulte(t, request, (user.UserEJB) session.getValue("u"));
        t = (ProformaAchatLib) pc.getBase();
        String id = pc.getBase().getTuppleID();
        pc.getChampByName("id").setLibelle("ID");
        pc.getChampByName("designation").setLibelle("D&eacute;signation");
        pc.getChampByName("daty").setLibelle("Date");
        pc.getChampByName("idFournisseur").setVisible(false);
        pc.getChampByName("idFournisseurLib").setLibelle("Fournisseur");
        pc.getChampByName("idFournisseurLib").setLien(lien+"?but=fournisseur/fournisseur-fiche.jsp&id=" + t.getIdFournisseur(), "nom=");
        pc.getChampByName("idDemandePrix").setLibelle("Ref. Demande de prix");
        pc.getChampByName("idDemandePrix").setLien(lien+"?but=facturefournisseur/dmdachat/demandeprix-fiche.jsp", "id=");
        pc.getChampByName("etat").setVisible(false);
        pc.getChampByName("etatlib").setLibelle("&Eacute;tat");

        String [] ordre = new String[] {"daty","designation","idFournisseurLib","idDemandePrix","etatlib"};
        pc.setOrdre(ordre);

        pc.setTitre("Fiche de proforma d'achat");
        String pageModif = "facturefournisseur/proformaachat/proformaachat-saisie.jsp";
        String pageActuel = "facturefournisseur/proformaachat/proformaachat-fiche.jsp";

        Map<String, String> map = new HashMap<String, String>();
        map.put("inc/proformaachat-details", "");

        String tab = request.getParameter("tab");
        if (tab == null) {
            tab = "inc/proformaachat-details";
        }
        map.put(tab, "active");
        tab = tab + ".jsp";

        ProformaAchatLib pa = (ProformaAchatLib) pc.getBase();
        int etat = pa.getEtat();
        boolean afficherViserModif = etat == ConstanteEtat.getEtatCreer();
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
                            <% if (afficherViserModif) { %>
                            <a class="btn btn-primary pull-right" href="<%= lien + "?but=apresTarif.jsp&acte=valider&id=" + id + "&bute=facturefournisseur/proformaachat/proformaachat-fiche.jsp&classe=faturefournisseur.ProformaAchat&nomtable=PROFORMA_ACHAT"%> " style="margin-right: 10px">Viser</a>
                            <a class="btn btn-secondary pull-right" href="<%= lien + "?but="+ pageModif +"&id=" + id +"&acte=update"%>" style="margin-right: 10px">Modifier</a>
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
                    <li class="<%=map.get("inc/proformaachat-details")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/proformaachat-details">D&eacute;tails</a></li>
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
    } catch (Exception e) {
        e.printStackTrace();
    } %>
