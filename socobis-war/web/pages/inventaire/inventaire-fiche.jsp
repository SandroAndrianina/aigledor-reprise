<%@page import="inventaire.InventaireLib"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="user.*" %>
<%@ page import="affichage.*" %>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@ page import="utils.ConstanteSocobis" %>
<%@ page import="utilitaire.ConstanteEtat" %>

<%
    UserEJB u = (user.UserEJB)session.getValue("u");

%>
<%
    InventaireLib unite = new InventaireLib();
    PageConsulte pc = new PageConsulte(unite, request, u);
    pc.setTitre("Fiche inventaire");
    unite = (InventaireLib) pc.getBase();
    String id=pc.getBase().getTuppleID();
    pc.getChampByName("Id").setLibelle("Id");
    pc.getChampByName("designation").setLibelle("D&eacute;signation");
    pc.getChampByName("idMagasinlib").setLibelle("Magasin");
    pc.getChampByName("daty").setLibelle("Date");
    pc.getChampByName("etatlib").setLibelle("&Eacute;tat");
    pc.getChampByName("idcategorielib").setLibelle("Cat&eacute;gorie");
    pc.getChampByName("idcategorielib").setVisible(false);

    pc.getChampByName("idMagasin").setVisible(false);
    pc.getChampByName("etat").setVisible(false);
    pc.getChampByName("idcategorie").setVisible(false);
    String pageActuel = "inventaire/inventaire-fiche.jsp";

    String lien = (String) session.getValue("lien");
    String pageModif = "inventaire/inventaire-saisie.jsp&acte=update";
    String classe = "inventaire.Inventaire";
    // Contexte serveur du fragment Historique : ne pas lire ces valeurs depuis ses filtres.
    request.setAttribute("historiqueRefObjet", pc.getBase().getTuppleID());
    request.setAttribute("historiqueObjets", new String[] {classe});
    request.setAttribute("historiqueFiche", "inventaire/inventaire-fiche.jsp");
    request.setAttribute("historiqueOnglet", "../historique/inc/historique-liste");


    Map<String, String> map = new HashMap<String, String>();
    map.put("inventairefille-liste", "");
    map.put("../historique/inc/historique-liste", "");

    String tab = request.getParameter("tab");
    if (tab == null) {
        tab = "inventairefille-liste";
    }
    map.put(tab, "active");
    tab = tab + ".jsp";
%>

<div class="content-wrapper">
    <h1 class="box-title"><a href=<%= lien + "?but=inventaire/inventaire-liste.jsp"%> <i class="fa fa-arrow-circle-left"></i></a><%=pc.getTitre()%></h1>
    <div class="row">
        <div class="col-md-12 mb-5">
            <div class="box-fiche">
                <div class="box">
                    <div class="box-body">
                        <%
                            out.println(pc.getHtml());
                        %>
                        <%=pc.getDuplicateButtonJS(pageModif,id)%>
                        <div class="box-footer">
                            <%
                                if(unite.getEtat() < ConstanteEtat.getEtatValider()){
                            %>
                            <a class="btn btn-primary pull-right" href="<%= (String) session.getValue("lien") + "?but=apresTarif.jsp&acte=valider&id=" + request.getParameter("id") + "&bute=inventaire/inventaire-fiche.jsp&classe=" + classe%> " style="margin-right: 10px">Viser</a>
                            <a class="btn btn-secondary pull-right"  href="<%= lien + "?but="+ pageModif +"&id=" + id%>" style="margin-right: 10px">Modifier</a>
                            <a  class="btn btn-danger pull-left" href="<%= lien + "?but=apresTarif.jsp&id=" + id+"&acte=annuler&bute=inventaire/inventaire-liste.jsp&classe="+classe%>">Annuler</a>
                            <% }else{ %>
                            <a class="btn btn-danger pull-left" href="<%= (String) session.getValue("lien") + "?but=apresTarif.jsp&acte=annulerVisa&id=" + request.getParameter("id") + "&bute=inventaire/inventaire-fiche.jsp&classe=" + classe%> " style="margin-right: 10px">Annuler</a>
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
                    <li class="<%=map.get("inventairefille-liste")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inventairefille-liste">Inventaire détails</a></li>
                    <li class="<%=map.get("../historique/inc/historique-liste")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=../historique/inc/historique-liste">Historique</a></li>
                </ul>
                <div class="tab-content">
                    <jsp:include page="<%= tab %>" >
                        <jsp:param name="idInventaire" value="<%= id %>" />
                    </jsp:include>
                </div>
            </div>

        </div>
    </div>
    <%=pc.getHtmlAttacherFichier()%>
</div>

