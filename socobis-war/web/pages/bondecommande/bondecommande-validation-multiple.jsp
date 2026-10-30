<%@ page import="user.UserEJB" %>
<%@ page import="faturefournisseur.As_BonDeCommande" %>
<%@ page import="avoir.AvoirFCLib" %>
<%@ page import="user.ValiderObject" %>
<%--
  Fichier : bondecommande/bondecommande-validation-multiple.jsp
  Action : Validation multiple des bons de commande
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String[] ids = request.getParameterValues("ids");

    UserEJB u;
    String lien = (String) session.getValue("lien");
    String bute;

    try {
        if (ids == null || ids.length == 0) {
            throw new Exception("Veuillez sélectionner au moins un élément à valider.");
        }

        u = (UserEJB) session.getAttribute("u");
        bute = request.getParameter("bute");

        As_BonDeCommande bc = new As_BonDeCommande();
        bc.validerObjectMultiple(u.getUser().getTuppleID(), ids);

//        As_BonDeCommande[] bc = new As_BonDeCommande[ids.length];
//        for (int i = 0; i < ids.length ; i++) {
//            bc[i] = (As_BonDeCommande)new As_BonDeCommande().getById(ids[i], "AS_BONDECOMMANDE", null);
//            bc[i].validerObject(u,null);
//        }

%>
<script language="JavaScript">
    document.location.replace("../module.jsp?but=bondecommande/bondecommande-liste.jsp");
</script>
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