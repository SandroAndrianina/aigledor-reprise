<%--
  Created by IntelliJ IDEA.
  User: Tsinjoniaina
  Date: 27/05/2026
  Time: 15:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="user.UserEJB" %>
<%@ page import="affichage.PageConsulte" %>
<%@ page import="depense.AchatDiversLib" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="utilitaire.ConstanteEtat" %>
<%@ page import="faturefournisseur.FactureFournisseurCpl" %>
<%@ page import="bean.CGenUtil" %>

<% try{
  UserEJB u = (user.UserEJB)session.getValue("u");
  String lien = (String) session.getValue("lien");

  AchatDiversLib o = new AchatDiversLib();
  o.setNomTable("AchatDiversLib");
  PageConsulte pc = new PageConsulte(o, request, u);
  pc.setTitre("Fiche de l'achat divers");
  String id = pc.getBase().getTuppleID();

  o=(AchatDiversLib) pc.getBase();
  pc.getChampByName("id").setLibelle("Id");
  pc.getChampByName("daty").setLibelle("Date");
  pc.getChampByName("fournisseurLib").setLibelle("Fournisseur");
  pc.getChampByName("fournisseurLib").setLien((String) session.getValue("lien") + "?but=fournisseur/fournisseur-fiche.jsp&id=" + o.getFournisseur(), "nom=");
  pc.getChampByName("designation").setLibelle("D&eacute;signation");
  pc.getChampByName("etatLib").setLibelle("&Eacute;tat");
  pc.getChampByName("fournisseur").setVisible(false);
  pc.getChampByName("etat").setVisible(false);
  pc.getChampByName("idCaisse").setLibelle("Tr&eacute;sorerie");
  pc.getChampByName("idCaisse").setVisible(false);
  pc.getChampByName("idcaisselib").setLibelle("Tr&eacute;sorerie");
  pc.getChampByName("idcaisselib").setLien((String) session.getValue("lien") + "?but=caisse/caisse-fiche.jsp&id=" + o.getIdCaisse(), "val=");
  pc.getChampByName("montantTotal").setLibelle("Montant Total");
  pc.getChampByName("surplusTotal").setLibelle("Surplus Total");
  pc.getChampByName("renduTotal").setLibelle("Rendu de Monnaie Total");

  String[] ordre = {"daty", "designation", "fournisseurLib", "idcaisselib", "montantTotal", "surplusTotal", "renduTotal", "etatLib"};
  pc.setOrdre(ordre);

  String pageActuel = "depense/achat-divers-fiche.jsp";
  String pageRetour = ".jsp";
  String pageModif = "depense/achat-divers-saisie.jsp&acte=update";
  String pageApresDelete = "depense/achat-divers-liste.jsp";
  String classe = "depense.AchatDivers";

  Map<String, String> map = new HashMap<>();
  map.put("inc/achat-divers-detail", "");
  map.put("inc/as-bondelivraison", "");
  map.put("inc/facture-fournisseur", "");
  map.put("inc/mvtcaisse-details", "");
  map.put("../compta/ecriture/inc/sous-ecriture-detail", "");
  String tab = request.getParameter("tab");
  if (tab == null) {
    tab = "inc/achat-divers-detail";
  }
  map.put(tab, "active");
  String tabInclude = tab + ".jsp";

  FactureFournisseurCpl facture = new FactureFournisseurCpl();
  facture.setNomTable("DEPENSEDFACTUREFOURNISSEUR");
  FactureFournisseurCpl[] factures = (FactureFournisseurCpl[]) CGenUtil.rechercher(facture, null, null, null, " and idDepenseDivers='" + id + "'");
  String idFactureFournisseur = (factures != null && factures.length > 0) ? factures[0].getId() : "";

%>

<div class="content-wrapper">

  <h1 class="box-title"><a href=<%= lien + "?but=" + pageRetour%>> <i class="fa fa-angle-left"></i></a><%=pc.getTitre()%></h1>

  <div class="row m-0">
    <div class="col-md-3"></div>
    <div class="col-md-6">
      <div class="box-fiche">
        <div class="box">
          <div class="box-body">
            <%
              out.println(pc.getHtml());
            %>
            <br/>
            <div class="box-footer">
              <% if (o.getEtat() == ConstanteEtat.getEtatCreer()) { %>
                <a class="btn btn-primary pull-right" href="<%= (String) session.getValue("lien") + "?but=apresTarif.jsp&acte=valider&id=" + request.getParameter("id") + "&bute=depense/achat-divers-fiche.jsp&classe=" + classe %> " style="margin-right: 10px">Viser</a>
                <a class="btn btn-secondary pull-right"  href="<%= lien + "?but="+ pageModif +"&id=" + id+"&acte=update"%>" style="margin-right: 10px">Modifier</a>
                <a class="pull-left btn btn-danger" href="<%= lien + "?but=apresTarif.jsp&id=" + id+"&acte=annuler&bute="+pageActuel+"&id=" + request.getParameter("id") %>&classe=<%= classe %>">Annuler</a>
              <% } %>
              <% if(o.getEtat()>=11){%>
                <% if(factures == null || factures.length == 0 || factures[0].getMontantpaye() == 0) { %>
                  <a class="btn btn-secondary pull-right"  href="<%= lien + "?but=depense/apresGenererFactureDefinitive.jsp&id=" + id%>" style="margin-right: 10px">G&eacute;n&eacute;rer facture d&eacute;finitive</a>
                <% } %>
                <a class="btn btn-secondary pull-right"  href="<%= lien + "?but=caisse/mvt/mvtCaisse-saisie-sortie-fc.jsp&idOrigine=" + id + "&devise=AR&montant=" + o.getMontantTotal() + "&tiers=" + o.getFournisseur() + "&reference=" %>" style="margin-right: 10px">D&eacute;caisser</a>
                <a class="btn btn-secondary pull-right"  href="<%= lien + "?but=caisse/mvt/mvtCaisse-saisie-sortie.jsp&currentMenu=MENUDYN00173&idOrigine=" + id + "&devise=AR&tiers=" + o.getFournisseur() + "&reference=" %>" style="margin-right: 10px">Surplus</a>
                <a class="btn btn-secondary pull-right" href="<%= lien + "?but=caisse/mvt/mvtCaisse-saisie-entree.jsp&currentMenu=MENUDYN00171&idOrigine=" + request.getParameter("id") + "&devise=AR&tiers=" + o.getFournisseur() + "&taux=1" %>" style="margin-right: 10px">Rendu de monnaie</a>
              <% } %>
            </div>
            <br/>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="row m-0">
    <div class="col-md-12 nopadding">
      <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
          <li class="<%=map.get("inc/achat-divers-detail")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/achat-divers-detail">D&eacute;tails</a></li>
          <li class="<%=map.get("inc/as-bondelivraison")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/as-bondelivraison">D&eacute;tails de r&eacute;ception</a></li>
          <li class="<%=map.get("inc/facture-fournisseur")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/facture-fournisseur">Facture</a></li>
          <li class="<%=map.get("../compta/ecriture/inc/sous-ecriture-detail")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=../compta/ecriture/inc/sous-ecriture-detail">&Eacute;criture</a></li>
          <li class="<%=map.get("inc/mvtcaisse-details")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/mvtcaisse-details">Mouvement de tr&eacute;sorerie</a></li>
        </ul>
        <div class="tab-content">
          <%
            String idParam = id;
            if ("../compta/ecriture/inc/sous-ecriture-detail".equals(tab) && !idFactureFournisseur.isEmpty()) {
              idParam = idFactureFournisseur;
            }
          %>
          <jsp:include page="<%= tabInclude %>" >
            <jsp:param name="id" value="<%= idParam %>" />
            <jsp:param name="idFactureFournisseur" value="<%= idFactureFournisseur %>" />
          </jsp:include>
        </div>
      </div>
    </div>
  </div>
</div>

<% } catch (Exception e) {
  e.printStackTrace();
%>
<script language="JavaScript"> alert('<%=e.getMessage()%>');
history.back();
</script>
<% }%>
