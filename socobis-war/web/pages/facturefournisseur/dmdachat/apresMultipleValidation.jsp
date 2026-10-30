<%@ page import="user.UserEJB" %>
<%@ page import="faturefournisseur.As_BonDeCommande" %>
<%@ page import="avoir.AvoirFCLib" %>
<%@ page import="user.ValiderObject" %>
<%@ page import="faturefournisseur.DmdAchat" %>
<%--
  Fichier : bondecommande/bondecommande-validation-multiple.jsp
  Action : Validation multiple des bons de commande
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String[] ids = request.getParameterValues("ids");

    UserEJB u;
    String lien = (String) session.getValue("lien");
    String bute = request.getParameter("bute");
    System.out.println(bute);
    try {
        if (ids == null || ids.length == 0) {
            throw new Exception("Veuillez sélectionner au moins un élément à valider.");
        }

        u = (UserEJB) session.getAttribute("u");

        DmdAchat da = new DmdAchat();
        da.validerObjectMultiple(ids, null, u.getUser().getTuppleID());


%>
<script language="JavaScript"> document.location.replace("<%=lien%>?but=<%=bute%>");</script>
<%
} catch (Exception e) {
    e.printStackTrace();
    String msg = e.getMessage().replace("'", "\\'");
%>
<script language="JavaScript">
    alert('Erreur lors de la validation : <%=msg%>');
    history.back();
</script>
<%
    }
%>