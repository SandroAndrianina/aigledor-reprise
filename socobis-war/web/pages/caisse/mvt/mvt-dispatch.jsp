
<%
    String lien = (String) session.getValue("lien");
    String id = request.getParameter("idOrigine");
    if(id == null) id = "";

    String but = "";
    if(id.startsWith("VNT")){
        but = "vente/vente-fiche.jsp&id=" + id;
    }else if (id.startsWith("FCF")){
        but = "facturefournisseur/facturefournisseur-fiche.jsp&id=" + id;
    }

    String url = lien + "?but=" + but;
%>
<script>
    window.location.href = "<%=url%>";
</script>
