<%@ page import="user.*" %>
<%@ page import="utilitaire.*" %>
<%@ page import="bean.*" %>
<%@ page import="faturefournisseur.ProformaAchat" %>
<%@ page import="java.util.HashMap" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<% try {
    String lien = (String) session.getAttribute("lien");
    UserEJB u = (UserEJB) session.getAttribute("u");
    String[] ids = request.getParameterValues("ids");
    String idDemandePrix = request.getParameter("idDemandePrix");

    HashMap<String, String> map = new HashMap<>();
    if (ids != null) {
        for (String id : ids) {

            String qte = request.getParameter("qteDemande_" + id);

            map.put(id, qte);
        }
    }

    ProformaAchat.genererDmdAchatParFournisseur(u.getUser().getTuppleID(), map, null);
%>
<script> document.location.replace("<%=lien%>?but=facturefournisseur/dmdachat/demandeprix-fiche.jsp&id=<%=idDemandePrix%>&tab=inc/dmdachat-liste");</script>
<% } catch (Exception ex) {
    ex.printStackTrace(); %>
    <script>alert("<%=ex.getMessage()%>"); history.back();</script>
<% return; } %>
</html>
