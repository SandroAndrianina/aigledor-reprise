<%@ page import="user.*" %>
<%@ page import="utilitaire.*" %>
<%@ page import="bean.*" %>
<%@ page import="depense.AchatDivers" %>
<html>
<% try {
    UserEJB u = (UserEJB) session.getAttribute("u");
    String id = request.getParameter("id");
    AchatDivers depenseDivers = new AchatDivers();
    depenseDivers.setId(id);
    depenseDivers.genererFactureDefinitive(u.getUser().getTuppleID(), null);

    String lien = (String) session.getAttribute("lien");
    String bute = "depense/achat-divers-fiche.jsp";
    String redirection = lien + "?but=" + bute + "&id=" + id;
%>
<script language="JavaScript"> document.location.replace("<%=redirection%>");</script>
<% } catch (Exception e) {
    e.printStackTrace(); %>
    <script>alert("<%=e.getMessage()%>"); history.back();</script>
<% return; } %>
</html>
