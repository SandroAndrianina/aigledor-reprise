<%@page import="user.*"%>
<%@ page import="bean.*" %>
<%@page import="affichage.*"%>
<%@page import="utilitaire.*"%>
<%@page import="faturefournisseur.*"%>
<%@ page import="affichage.Champ" %>
<%
    try {
        UserEJB u = null;
        u = (UserEJB) session.getValue("u");
        ProformaAchat mere = new ProformaAchat();
        ProformaAchatFilleLib fille = new ProformaAchatFilleLib();
        fille.setNomTable("PROFORMA_ACHAT_FILLELIB");

        String idDemandePrix = request.getParameter("iddemandeprix");
        DemandePrixLib demandePrix = null;
        ProformaAchatFilleLib[] paFille = null;
        int nb = 0;
        if (idDemandePrix != null && !idDemandePrix.isEmpty()) {
            demandePrix = (DemandePrixLib) new DemandePrixLib().getById(idDemandePrix, "DEMANDE_PRIX_LIB", null);
            paFille = demandePrix.getProformaAchatDetails(null);
            nb = paFille.length;
        }

        int nombreLigne = Math.max(nb, 10);
        PageInsertMultiple pi = new PageInsertMultiple(mere, fille, request, nombreLigne, u);
        pi.setLien((String) session.getValue("lien"));

        if (demandePrix != null) {
            pi.getFormu().getChamp("designation").setDefaut("Proforma de la demande de prix " + idDemandePrix);
            pi.getFormu().getChamp("daty").setDefaut(Utilitaire.dateDuJour());
            pi.getFormu().getChamp("idDemandePrix").setDefaut(idDemandePrix);

            pi.setDefautFille(paFille);
        }

        pi.getFormu().getChamp("designation").setLibelle("D&eacute;signation");
        pi.getFormu().getChamp("daty").setLibelle("Date");
        pi.getFormu().getChamp("etat").setVisible(false);
        pi.getFormu().getChamp("idDemandePrix").setVisible(false);
        pi.getFormu().getChamp("idFournisseur").setLibelle("Fournisseur");
        pi.getFormu().getChamp("idFournisseur").setPageAppelComplete("faturefournisseur.Fournisseur","id","FOURNISSEUR_ACTIF","echeance;taxe","echeance;taxe");
        pi.getFormu().getChamp("idFournisseur").setPageAppelInsert("fournisseur/fournisseur-saisie.jsp","idFournisseur;idFournisseurlibelle","id;nom");


        String[] ordre = new String[] {"daty", "designation", "fournisseur"};
        pi.getFormu().setOrdre(ordre);

        pi.getFormufle().getChamp("idProduit_0").setLibelle("Produit");
        pi.getFormufle().getChamp("designation_0").setLibelle("D&eacute;signation");
        pi.getFormufle().getChamp("qteDisponible_0").setLibelle("Quantit&eacute; disponible");
        pi.getFormufle().getChamp("pu_0").setLibelle("Prix unitaire");
        pi.getFormufle().getChamp("delaisLivraison_0").setLibelle("D&eacute;lais de livraison (j)");
        pi.getFormufle().getChamp("remarque_0").setLibelle("Remarque");
        pi.getFormufle().getChamp("idUniteLib_0").setLibelle("Unit&eacute;");

        Champ.setVisible(pi.getFormufle().getChampMulitple("id").getListeChamp(), false);

        affichage.Champ.setVisible(pi.getFormufle().getChampMulitple("idMere").getListeChamp(),false);
        affichage.Champ.setPageAppelComplete(pi.getFormufle().getChampMulitple("idProduit").getListeChamp(), "produits.IngredientsLib", "id", "AS_INGREDIENTS_LIB_ACHAT", "libelle;pu;unite", "designation;pu;idUniteLib");
        String[] colOrdre = {"id", "idProduit","designation","qteDisponible", "idUniteLib","pu","delaisLivraison","remarque"};
        pi.getFormufle().setColOrdre(colOrdre);

        Champ.setAutre(pi.getFormufle().getChampFille("pu"), "onchange='calculerMontantV2()'");
        Champ.setAutre(pi.getFormufle().getChampFille("qteDisponible"), "onchange='calculerMontantV2()'");

        String acte = request.getParameter("acte");
        if(acte != null && acte.equalsIgnoreCase("update")){
            pi.setTitre("Modification de Proforma d'achat");
        } else {
            pi.setTitre("Saisie de Proforma d'achat");
        }

        pi.preparerDataFormu();
        pi.getFormu().makeHtmlInsertTabIndex();
        pi.getFormufle().makeHtmlInsertTableauIndex();

        String classeMere = "faturefournisseur.ProformaAchat";
        String classeFille = "faturefournisseur.ProformaAchatFille";
        String colonneMere = "idMere";
        String apres = "facturefournisseur/proformaachat/proformaachat-fiche.jsp";
%>

<div class="content-wrapper">
    <h1><%=pi.getTitre()%></h1>
    <form id="formId" class='container' action="<%=pi.getLien()%>?but=apresMultiple.jsp" method="post" >
        <%
            out.println(pi.getFormu().getHtmlInsert());
        %>
        <div class="col-md-12 nopadding" >
            <div class="col-md-12 cardradius">
                <h3 class="fontinter m-0" >Total  : <span id="montanttotal">0</span> <span id="deviseLibelle">AR</span></h3>
            </div>
        </div>
        <%
            out.println(pi.getFormufle().getHtmlTableauInsert());
        %>
        <input name="acte" type="hidden" id="nature" value="insert">
        <input name="bute" type="hidden" id="bute" value="<%=apres%>">
        <input name="classe" type="hidden" id="classe" value="<%=classeMere%>">
        <input name="classefille" type="hidden" id="classefille" value="<%=classeFille%>">
        <input name="nombreLigne" type="hidden" id="nombreLigne" value="<%=nombreLigne%>">
        <input name="colonneMere" type="hidden" id="colonneMere" value="<%=colonneMere%>">
        <input name="nomtable" type="hidden" id="nomtable" value="PROFORMA_ACHAT_FILLE">
        <div class="box-footer">
            <input type="submit" class="btn btn-primary pull-right" value="Enregistrer">
        </div>
    </form>
</div>
<script>
    function cleanNumber(val) {
        if (!val) return 0;

        return parseFloat(
            val
                .toString()
                .replace(/&nbsp;/g, "")   // retire le code HTML
                .replace(/\u00A0/g, "")   // retire espace insécable réel
                .replace(/\s/g, "")       // retire tous les autres espaces
                .replace(",", ".")        // virgule → point
        ) || 0;
    }

    function calculerMontantV2() {
        var puList = document.querySelectorAll('input[id^="pu_"]');
        let montantTtc = 0;

        puList.forEach(input => {
            let id = input.id.split("_")[1];

            let pu = cleanNumber(document.getElementById("pu_" + id)?.value);
            let qte = cleanNumber(document.getElementById("qteDisponible_" + id)?.value);

            let montantHt = pu * qte;

            montantTtc += montantHt;
        });

        document.getElementById("montanttotal").innerHTML =
            montantTtc.toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }
</script>
<%
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
