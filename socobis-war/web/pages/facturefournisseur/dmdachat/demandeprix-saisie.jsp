<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="affichage.PageInsertMultiple"%>
<%@ page import="user.UserEJB" %>
<%@ page import="faturefournisseur.DemandePrix" %>
<%@ page import="affichage.Champ" %>
<%@ page import="faturefournisseur.DemandePrixFilleLib" %>

<% try{ 
    UserEJB u = (user.UserEJB) session.getValue("u");
    String classeMere = "faturefournisseur.DemandePrix";
    String classeFille = "faturefournisseur.DemandePrixFille";
    String nomTableFille = "DEMANDE_PRIX_FILLE";
    String colonneMere = "idMere";
    String apres = "facturefournisseur/dmdachat/demandeprix-fiche.jsp";

    DemandePrix mere = new DemandePrix();
    mere.setNomTable("DEMANDE_PRIX");
    DemandePrixFilleLib fille = new DemandePrixFilleLib();
    fille.setNomTable("DEMANDE_PRIX_FILLELIB");
    int taille = 10;
    PageInsertMultiple pi = new PageInsertMultiple(mere, fille, request, taille, u);
    pi.setLien((String) session.getValue("lien"));  
    pi.setTitre("Saisie de Demande de Prix");


    pi.getFormu().getChamp("designation").setLibelle("D&eacute;signation");
    pi.getFormu().getChamp("daty").setLibelle("Date");
    pi.getFormu().getChamp("acheteur").setLibelle("Acheteur");
    pi.getFormu().getChamp("etat").setVisible(false);

    pi.getFormufle().getChamp("idProduit_0").setLibelle("Produit");
    pi.getFormufle().getChamp("designation_0").setLibelle("D&eacute;signation");
    pi.getFormufle().getChamp("quantite_0").setLibelle("Quantit&eacute;");
    pi.getFormufle().getChamp("idunitelib_0").setLibelle("Unit&eacute;");
    Champ.setVisible(pi.getFormufle().getChampMulitple("id").getListeChamp(), false);

    String[] ordre = new String[] {"daty", "designation", "acheteur"};
    pi.getFormu().setOrdre(ordre);
    Champ.setVisible(pi.getFormufle().getChampMulitple("idMere").getListeChamp(),false);
    Champ.setAutre(pi.getFormufle().getChampMulitple("idunitelib").getListeChamp(), "readonly");
    Champ.setPageAppelComplete(pi.getFormufle().getChampMulitple("idProduit").getListeChamp(), "produits.IngredientsLib", "id", "AS_INGREDIENTS_LIB", "libelle;unite", "designation;idUniteLib");

    String[] colOrdre = {"id", "idProduit","designation","quantite", "idunitelib"};
    pi.getFormufle().setColOrdre(colOrdre);

    String acte = request.getParameter("acte");
    if(acte != null && acte.equalsIgnoreCase("update")){
        pi.setTitre("Modification de Demande de Prix");
    }

    pi.preparerDataFormu();
    pi.getFormu().makeHtmlInsertTabIndex();
    pi.getFormufle().makeHtmlInsertTableauIndex();
%>

<div class="content-wrapper">
    <h1><%=pi.getTitre()%></h1>
    <form id="formId" class='container' action="<%=pi.getLien()%>?but=apresMultiple.jsp" method="post" >
        <%
            out.println(pi.getFormu().getHtmlInsert());
            out.println(pi.getFormufle().getHtmlTableauInsert());
        %>
        <input name="acte" type="hidden" id="nature" value="insert">
        <input name="bute" type="hidden" id="bute" value="<%=apres%>">
        <input name="classe" type="hidden" id="classe" value="<%=classeMere%>">
        <input name="classefille" type="hidden" id="classefille" value="<%=classeFille%>">
        <input name="nomtable" type="hidden" id="nomtable" value=<%=nomTableFille%>>
        <input name="nombreLigne" type="hidden" id="nombreLigne" value="<%=taille%>">
        <input name="colonneMere" type="hidden" id="colonneMere" value="<%=colonneMere%>">
    </form>
</div>

<% } catch (Exception e) {
  e.printStackTrace();
%>
<script language="JavaScript"> alert('<%=e.getMessage()%>');
    history.back();
</script>
<% }%>

