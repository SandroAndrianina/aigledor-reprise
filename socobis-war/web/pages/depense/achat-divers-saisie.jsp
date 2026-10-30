<%--
  Created by IntelliJ IDEA.
  User: Tsinjoniaina
  Date: 27/05/2026
  Time: 15:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="affichage.PageInsertMultiple"%>
<%@ page import="user.UserEJB" %>
<%@ page import="depense.AchatDivers" %>
<%@ page import="depense.AchatDiversFille" %>
<%@ page import="affichage.Liste" %>
<%@ page import="caisse.Caisse" %>
<%@ page import="utilitaire.Utilitaire" %>

<% try{
    UserEJB u = (user.UserEJB) session.getValue("u");
    String classeMere = "depense.AchatDivers";
    String classeFille = "depense.AchatDiversFille";
    String nomTableFille = "ACHATDIVERSFILLE";
    String colonneMere = "idMere";
    String apres = "depense/achat-divers-fiche.jsp";

    AchatDivers mere = new AchatDivers();
    mere.setNomTable("ACHATDIVERS");
    AchatDiversFille fille = new AchatDiversFille();
    fille.setNomTable("ACHATDIVERSFILLE");
    int taille = 10;
    PageInsertMultiple pi = new PageInsertMultiple(mere, fille, request, taille, u);
    pi.setLien((String) session.getValue("lien"));
    pi.setTitre("Saisie d'un achat Divers");

    Liste[] listes = new Liste[1];
    listes[0] = new Liste("idCaisse", new Caisse(), "val", "id");
    pi.getFormu().changerEnChamp(listes);

    pi.getFormu().getChamp("daty").setLibelle("Date");
    pi.getFormu().getChamp("daty").setDefaut(Utilitaire.dateDuJour());
    pi.getFormu().getChamp("idCaisse").setLibelle("Tr&eacute;sorerie");
    pi.getFormu().getChamp("fournisseur").setLibelle("Fournisseur");
    pi.getFormu().getChamp("fournisseur").setDefaut("FRNDIV01");
    pi.getFormu().getChamp("designation").setLibelle("D&eacute;signation");
    pi.getFormu().getChamp("etat").setLibelle("&Eacute;tat");
    pi.getFormu().getChamp("etat").setVisible(false);
    pi.getFormu().getChamp("fournisseur").setPageAppelComplete("faturefournisseur.Fournisseur","id","FOURNISSEUR","","");
    String[] ordre = {"daty","designation", "fournisseur","idCaisse"};
    pi.getFormu().setOrdre(ordre);

    pi.getFormufle().getChamp("idProduit_0").setLibelle("Produit");
    pi.getFormufle().getChamp("designation_0").setLibelle("D&eacute;signation");
    pi.getFormufle().getChamp("quantite_0").setLibelle("Quantit&eacute;");
    pi.getFormufle().getChamp("pu_0").setLibelle("Prix Unitaire");

    for(int i = 0; i < taille; i++){
        pi.getFormufle().getChamp("quantite_"+i).setAutre("onChange='calculerMontant("+i+")'");
        pi.getFormufle().getChamp("pu_"+i).setAutre("onChange='calculerMontant("+i+")'");
    }

    affichage.Champ.setPageAppelComplete(pi.getFormufle().getChampFille("idProduit"),"produits.IngredientsLib","id","AS_INGREDIENTS_LIB","id;libelle;pu","idproduit;designation;pu");
    affichage.Champ.setVisible(pi.getFormufle().getChampFille("id"), false);
    affichage.Champ.setVisible(pi.getFormufle().getChampFille("idMere"), false);
    affichage.Champ.setVisible(pi.getFormufle().getChampFille("idProduit"), false);

    String[] colOrdre = {"id","idMere","idProduit","designation","quantite","pu"};
    pi.getFormufle().setColOrdre(colOrdre);

    String acte = request.getParameter("acte");
    if(acte != null && acte.equalsIgnoreCase("update")){
        pi.setTitre("Modification de l'achat Divers");
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
        %>
        <div class="col-md-12 cardradius">
            <h3 class="fontinter" style="background: white; padding: 16px; margin-top: 10px; border-radius: 16px;">
                Total : <span id="montanttotal">0</span> Ar
            </h3>
        </div>
        <%
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
<script>
    function formatNumber(number) {
        if (isNaN(number)) return '';
        return number.toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }

    function calculerMontant(indice) {
        var pu  = parseFloat(document.getElementById('pu_' + indice).value.replace(/\s/g, '')) || 0;
        var qte = parseFloat(document.getElementById('quantite_' + indice).value.replace(/\s/g, '')) || 0;
        var montant = pu * qte;

        mettreAJourTotal();
    }

    function mettreAJourTotal() {
        var val = 0;
        $('input[id^="pu_"]').each(function(index) {
            var puEl  = document.getElementById('pu_' + index);
            var qteEl = document.getElementById('quantite_' + index);
            if (puEl && qteEl) {
                var pu  = parseFloat(puEl.value.replace(/\s/g, '')) || 0;
                var qte = parseFloat(qteEl.value.replace(/\s/g, '')) || 0;
                val += pu * qte;
            }
        });
        $("#montanttotal").html(Intl.NumberFormat('fr-FR', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(val));
    }

    $(document).ready(function () {
        mettreAJourTotal();
    });
</script>
<% } catch (Exception e) {
    e.printStackTrace();
%>
<script language="JavaScript"> alert('<%=e.getMessage()%>');
history.back();
</script>
<% }%>


