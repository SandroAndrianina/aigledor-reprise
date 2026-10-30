<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="affichage.PageInsert"%>
<%@ page import="user.UserEJB" %>
<%@ page import="faturefournisseur.ObservationBc" %>

<% try{ 
    UserEJB u = (user.UserEJB) session.getValue("u");
    String mapping = "faturefournisseur.ObservationBc";
    String nomTable = "OBSERVATIONBC";
    String apres = "bondecommande/bondecommande-fiche.jsp&id=" + request.getParameter("idBc") + "&tab=inc/observation";

    ObservationBc o = new ObservationBc();
    o.setNomTable("OBSERVATIONBC");
    PageInsert pi = new PageInsert(o, request, u);
    pi.setLien((String) session.getValue("lien"));  
    pi.setTitre("Saisie une Observation");

    if (pi.getFormu().getChamp("id") != null) {
        pi.getFormu().getChamp("id").setVisible(false);
    }

    pi.getFormu().getChamp("idBc").setLibelle("Bon De Commande");
    pi.getFormu().getChamp("idBc").setDefaut(request.getParameter("idBc"));
    pi.getFormu().getChamp("idBc").setAutre("readonly");


    String acte = request.getParameter("acte");
    if(acte != null && acte.equalsIgnoreCase("update")){
        pi.setTitre("Modification d'une Observation");
    }

    pi.preparerDataFormu();
    pi.getFormu().makeHtmlInsertTabIndex();
%>

<div class="content-wrapper">
    <h1><%=pi.getTitre()%></h1>
    <form action="<%=pi.getLien()%>?but=apresTarif.jsp" method="post" name="<%=nomTable%>" id="<%=nomTable%>">
        <%
            out.println(pi.getFormu().getHtmlInsert());
            out.println(pi.getHtmlAddOnPopup());
        %>
        <input name="acte" type="hidden" id="nature" value="insert">
        <input name="bute" type="hidden" id="bute" value="<%=apres%>">
        <input name="classe" type="hidden" id="classe" value="<%=mapping%>">
        <input name="nomtable" type="hidden" id="nomtable" value="<%=nomTable%>">
    </form>
</div>

<% } catch (Exception e) {
  e.printStackTrace();
%>
<script language="JavaScript"> alert('<%=e.getMessage()%>');
    history.back();
</script>
<% }%>

