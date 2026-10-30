<%--
    Document   : vente-saisie
    Created on : 22 mars 2024, 14:37:44
    Author     : Angela
--%>

<%@page import="produits.CategorieIngredient"%>
<%@page import="user.*"%>
<%@ page import="bean.*" %>
<%@page import="affichage.*"%>
<%@page import="utilitaire.*"%>
<%@page import="faturefournisseur.*"%>
<%@ page import="magasin.Magasin" %>
<%@ page import="mg.cnaps.compta.ConstanteCompta" %>
<%@ page import="affichage.Champ" %>
<%
    try {
        UserEJB u = null;
        u = (UserEJB) session.getValue("u");
        As_BonDeCommande mere = new As_BonDeCommande();
        As_BonDeCommande_Fille_CPL fille = new As_BonDeCommande_Fille_CPL();
        fille.setNomTable("AS_BONDECOMMANDE_FILLE_CPL_U");
        int nombreLigne = 10;
        PageInsertMultiple pi = new PageInsertMultiple(mere, fille, request, nombreLigne, u);
        pi.setLien((String) session.getValue("lien"));
        Liste[] liste = new Liste[4];
        caisse.Devise dev = new caisse.Devise();
        //dev.setId("AR");
        liste[0] = new Liste("idDevise",dev,"val","id");
        liste[0].setDefaut("AR");
        ModePaiement mp = new ModePaiement();
        liste[1] = new Liste("modepaiement",mp,"val","id");
        Magasin magasin = new Magasin();
        magasin.setNomTable("MAGASIN2");
        liste[2] = new Liste("idMagasin",magasin,"val","id");
        CategorieIngredient categorieIngredient = new CategorieIngredient();
        categorieIngredient.setNomTable("CATEGORIEINGREDIENTLIB_ACHT");
        liste[3] = new Liste("idCategorie", categorieIngredient, "val", "id");
        pi.getFormu().changerEnChamp(liste);
        pi.getFormu().getChamp("idCategorie").setLibelle("Cat&eacute;gorie");
        pi.getFormu().getChamp("idCategorie").setAutre("onchange=\"updateFille(event, 'formId')\"");
        pi.getFormu().getChamp("modepaiement").setLibelle("Mode de paiement");
        pi.getFormu().getChamp("conformite").setDefaut("1");
        String magasinEnCours = u.getMagasinEncoursVente().getId();
        pi.getFormu().getChamp("idMagasin").setDefaut(magasinEnCours);
        pi.getFormu().getChamp("idMagasin").setLibelle("Magasin");
        pi.getFormu().getChamp("conformite").setVisible(false);
        pi.getFormu().getChamp("etat").setVisible(false);
        pi.getFormu().getChamp("refProforma").setVisible(false);
        pi.getFormu().getChamp("service").setVisible(false);
        pi.getFormu().getChamp("reference").setLibelle("R&eacute;f&eacute;rence");
        pi.getFormu().getChamp("designation").setLibelle("D&eacute;signation");
        pi.getFormu().getChamp("remarque").setLibelle("Remarque");
        pi.getFormu().getChamp("daty").setLibelle("Date");
        pi.getFormu().getChamp("dateLimite").setLibelle("Date limite de livraison");
        pi.getFormu().getChamp("fournisseur").setLibelle("Fournisseur");
        pi.getFormu().getChamp("fournisseur").setPageAppelComplete("faturefournisseur.Fournisseur","id","Fournisseur","taxe;idmodepaiement","taxe;modepaiement");
        pi.getFormu().getChamp("fournisseur").setPageAppelInsert("fournisseur/fournisseur-saisie.jsp","fournisseur;fournisseurlibelle","id;nom");
        pi.getFormu().getChamp("idDevise").setLibelle("Devise");
        pi.getFormu().getChamp("idDevise").setAutre("onChange='deviseModification()'");
        affichage.Champ.setPageAppelComplete(pi.getFormufle().getChampFille("produit"),"produits.IngredientsLib","id","ST_INGREDIENTSAUTOACHAT_CPL","pu;taux;compte_achat;compte_achat;libelle;unite","pu;tauxDeChange;compte;comptelibelle;designation;unitelib");
        String[] ordre={"daty","reference","designation","idCategorie","fournisseur","idMagasin","modepaiement","idDevise","dateLimite","remarque"};
        pi.getFormu().setOrdre(ordre);
        if (request.getParameter("onchanged") != null && request.getParameter("onchanged").equals("true")){
            String idCategorieIngredient = request.getParameter("idCategorie");
            if (idCategorieIngredient != null) {
                    affichage.Champ.setPageAppelCompleteAWhere(pi.getFormufle().getChampFille("produit"),"produits.IngredientsLib","id","ST_INGREDIENTSAUTOACHAT_CPL","pu;taux;compte_achat;compte_achat;libelle;unite","pu;tauxDeChange;compte;comptelibelle;produitLib;unitelib" , " AND CATEGORIEINGREDIENT = '"+idCategorieIngredient+"'");
            }
       }
         affichage.Champ.setPageAppelInsert(pi.getFormufle().getChampFille("produit"),"produits/as-ingredients-saisie.jsp","id;libelle");

        pi.getFormufle().getChamp("produit_0").setLibelle("Produit");
        pi.getFormufle().getChamp("produitLib_0").setLibelle("Libell&eacute; Produit");
        pi.getFormufle().getChamp("produitLib_0").setVisible(false);
        pi.getFormufle().getChamp("designation_0").setLibelle("D&eacute;signation");
        pi.getFormufle().getChamp("tva_0").setLibelle("TVA (%)");
        pi.getFormufle().getChamp("remise_0").setLibelle("Remise (En %)");
        pi.getFormufle().getChamp("quantite_0").setLibelle("Quantit&eacute;");
        pi.getFormufle().getChamp("unitelib_0").setLibelle("Unit&eacute;");
        pi.getFormufle().getChamp("pu_0").setLibelle("Prix Unitaire HT");
        pi.getFormufle().getChamp("idDevise_0").setLibelle("Devise");
        pi.getFormufle().getChampMulitple("id").setVisible(false);
        pi.getFormufle().getChampMulitple("idbc").setVisible(false);
        pi.getFormufle().getChampMulitple("montant").setVisible(false);
        pi.getFormufle().getChampMulitple("unite").setVisible(false);
        //pi.getFormufle().getChampMulitple("tauxDeChange").setVisible(false);
        //pi.getFormufle().getChampMulitple("remise").setVisible(false);
        pi.getFormufle().getChampMulitple("idDevise").setVisible(false);
        pi.getFormufle().getChamp("tauxDeChange_0").setLibelle("Taux de change");
//        pi.getFormufle().getChampMulitple("tauxDeChange").setAutre("readonly");

        Champ.setAutre(pi.getFormufle().getChampFille("unitelib"), "readonly");

        Champ.setVisible(pi.getFormufle().getChampFille("produitLib"), false);
        affichage.Champ.setAutre(pi.getFormufle().getChampFille("pu"),"onchange='calculerMontantV2()'");
        affichage.Champ.setAutre(pi.getFormufle().getChampFille("quantite"),"onchange='calculerMontantV2()'");
        affichage.Champ.setAutre(pi.getFormufle().getChampFille("tva"),"onchange='calculerMontantV2()'");
        affichage.Champ.setAutre(pi.getFormufle().getChampFille("produit"),"onchange='calculerMontantV2()'");
        affichage.Champ.setAutre(pi.getFormufle().getChampFille("remise"),"onchange='calculerMontantV2()'");

        pi.preparerDataFormu();
        for(int i=0;i<nombreLigne;i++){
            pi.getFormufle().getChamp("idDevise_"+i).setAutre("readonly");
//            pi.getFormufle().getChamp("quantite_"+i).setAutre("onChange='calculerMontant("+i+")'");
            pi.getFormufle().getChamp("quantite_"+i).setDefaut("0");
            pi.getFormufle().getChamp("tva_"+i).setDefaut("0");
            pi.getFormufle().getChamp("idDevise_"+i).setDefaut("AR");
            pi.getFormufle().getChamp("tauxDeChange_"+i).setDefaut("1");
//            pi.getFormufle().getChamp("tva_"+i).setDefaut("20");
        }

        //pi.getFormufle().getChampMulitple("quantite").setVisible(false);
        String iddmdachat = request.getParameter("iddmdachat");
        if (iddmdachat!=null && !iddmdachat.isEmpty()) {
            DmdAchat dmdAchat = (DmdAchat) new DmdAchat().getById(iddmdachat,"DMDACHAT",null);
            if (dmdAchat!=null){
                pi.getFormu().getChamp("daty").setDefaut(Utilitaire.datetostring(dmdAchat.getDaty()));
                pi.getFormu().getChamp("fournisseur").setDefaut(dmdAchat.getFournisseur());
                pi.getFormu().getChamp("designation").setDefaut("Bon de commande de la demande d'achat "+iddmdachat);
                pi.getFormu().getChamp("idMagasin").setDefaut(dmdAchat.getIdMagasin());
                pi.getFormu().getChamp("idcategorie").setDefaut(dmdAchat.getIdCategorie());

                if(dmdAchat.getDateLimite()!=null){
                    pi.getFormu().getChamp("dateLimite").setDefaut(Utilitaire.datetostring(dmdAchat.getDateLimite()));
                }
                As_BonDeCommande_Fille_CPL[] bcFille = dmdAchat.getBonDeCommandeDetails(null);
                pi.setDefautFille(bcFille);
            }
            pi.getFormu().getChamp("idDmdAchat").setDefaut(iddmdachat);
        }
        pi.getFormu().getChamp("idDmdAchat").setLibelle("Demande d'achat");
        pi.getFormu().getChamp("idDmdAchat").setAutre("readonly");
        //Variables de navigation
        String classeMere = "faturefournisseur.As_BonDeCommande";
        String classeFille = "faturefournisseur.As_BonDeCommande_Fille";
        String butApresPost = "bondecommande/bondecommande-fiche.jsp";
        String colonneMere = "idbc";

        pi.getFormufle().setColOrdre(new String[]{"produit", "produitLib","designation", "quantite", "unitelib","pu","remise", "tva", "tauxDeChange"});
        //Preparer les affichages
        pi.getFormu().makeHtmlInsertTabIndex();
        pi.getFormufle().makeHtmlInsertTableauIndex();

%>
<div class="content-wrapper">
    <%if (request.getParameter("acte") != null && request.getParameter("acte").equalsIgnoreCase("update")) {%>
    <h1>Modification d'un bon de commande fournisseur</h1>
    <% }else{ %>
    <h1>Saisie d'un bon de commande fournisseur</h1>
    <% } %>
    <div class="box-body">
        <form id="formId" class='container' action="<%=pi.getLien()%>?but=apresMultiple.jsp" method="post" >
            <%

                out.println(pi.getFormu().getHtmlInsert());
            %>
            <div class="col-md-12 nopadding" >
                <div class="col-md-12 cardradius">
                    <h3 class="fontinter" style="background: white;padding: 16px;margin-top: 10px;border-radius: 16px;" >Total  : <span id="montanttotal">0</span> <span id="deviseLibelle">AR</span></h3>
                </div>
            </div>
            <div id="butfillejsp">
                <%
                    out.println(pi.getFormufle().getHtmlTableauInsert());
                %>
            </div>
            <input name="taxe" type="hidden" id="taxe" value="">
            <input name="acte" type="hidden" id="nature" value="insert">
            <input name="bute" type="hidden" id="bute" value="<%= butApresPost %>">
            <input name="classe" type="hidden" id="classe" value="<%= classeMere %>">
            <input name="classefille" type="hidden" id="classefille" value="<%= classeFille %>">
            <input name="nombreLigne" type="hidden" id="nombreLigne" value="<%= nombreLigne %>">
            <input name="colonneMere" type="hidden" id="colonneMere" value="<%= colonneMere %>">
            <input name="nomtable" type="hidden" id="nomtable" value="AS_BONDECOMMANDE_FILLE">
        </form>
    </div>
</div>
<script>
    function calculerDateEcheance() {
        const champEcheance = document.getElementById("echeanceFacture");
        const champDateEcheance = document.getElementById("dateEcheancePaiement");

        if (!champEcheance || !champDateEcheance) {
            return;
        }
        let date = new Date();
        const nbJours = parseInt(champEcheance.value) || 0;
        date.setDate(date.getDate() + nbJours);
        const formattedDate =
            String(date.getDate()).padStart(2, '0') + "/" +
            String(date.getMonth() + 1).padStart(2, '0') + "/" +
            date.getFullYear();
        champDateEcheance.value = formattedDate;
    }
    function changerEcheance() {
        const champEcheance = document.getElementById("echeanceFacture");
        const champDateEcheance = document.getElementById("dateEcheancePaiement");

        if (!champEcheance || !champDateEcheance || !champDateEcheance.value) {
            return;
        }

        const today = new Date();
        today.setHours(0,0,0,0);

        const tab = champDateEcheance.value.split("/");
        if (tab.length !== 3) {
            return;
        }

        const dateFin = new Date(
            parseInt(tab[2]),
            parseInt(tab[1]) - 1,
            parseInt(tab[0])
        );
        dateFin.setHours(0,0,0,0);

        if (isNaN(dateFin.getTime())) {
            return;
        }

        const diff = Math.round(
            (dateFin - today) / (1000 * 60 * 60 * 24)
        );

        champEcheance.value = diff;
    }

    function handlerNbLines() {
        const btnAddLigne = document.getElementById("addLines");
        const inputNbLigne = document.getElementById("nombreLigne");
        const allBtnRemove = document.querySelector(".table-container").querySelectorAll("span.glyphicon-remove");
        const allBtnDuplicate = document.querySelector(".table-container").querySelectorAll("span.glyphicon-duplicate");

        allBtnRemove.forEach(btn => {
            btn.parentElement.addEventListener('click', () => {
                inputNbLigne.value = Math.max(0, Number(inputNbLigne.value) - 1);
                console.log("Ao fa miova ah")
            })
        })
    }

    document.addEventListener("DOMContentLoaded", function () {
        calculerDateEcheance();
        document.getElementById("echeanceFacture")
            .addEventListener("input", calculerDateEcheance);
        document.getElementById("echeanceFacture")
            .addEventListener("change", calculerDateEcheance);
        document.getElementById("dateEcheancePaiement")
            .addEventListener("input", changerEcheance);
        document.getElementById("dateEcheancePaiement")
            .addEventListener("change", changerEcheance);

    });
</script>
<script>
    function initialiserDeviseTotal() {
        var devise = $("#idDevise").val();
        if (devise == null || devise === "") {
            devise = "AR";
        }
        $("#deviseLibelle").html(devise);
    }
    $(document).ready(function() {
        initialiserDeviseTotal();
        var nombreLigne = <%=nombreLigne%>;
        for(var i=0;i<nombreLigne;i++){
            calculerMontant(i);
        }

        const TAXE_VALUE = <%= ConstanteCompta.constante_tva %>;

        const taxeInput = document.getElementById('taxe');
        const tvaInputs = document.querySelectorAll('[id^="tva_"]');

        const observer = new MutationObserver(() => {
            const newValue = taxeInput.value;

            if (newValue === '1.0') {
                tvaInputs.forEach(input => {
                    input.value = TAXE_VALUE;
                });
            } else {
                tvaInputs.forEach(input => {
                    input.value = '0';
                });
            }
        });

        observer.observe(taxeInput, {
            attributes: true,
            attributeFilter: ['value']
        });
    });
</script>
<script>
    $(document).ready(function () {
        var lignes = <%=pi.getNombreLigne()%>
        for (let i = 0; i < lignes; i++) {
            calculerMontant(i);
        }
    });
    function parseNombre(valeur) {
        if (valeur === null || valeur === undefined || valeur === '') {
            return 0;
        }
        return parseFloat(
            valeur.toString()
                .replace(/[\s\u00A0\u202F]/g, '') // enlève espace, espace insécable, espace fine
                .replace(',', '.')               // virgule décimale -> point
        );
    }
    function calculerMontant() {
        var val = 0;
        $('input[id^="quantite_"]').each(function() {
            var idPu = $(this).attr('id').replace("quantite", "pu");
            var quantite = parseNombre($(this).val());
            var pu = parseNombre($("#" + idPu).val());
            if (!isNaN(quantite) && !isNaN(pu)) {
                val += quantite * pu;
            }
        });
        $("#montanttotal").html(
            val.toLocaleString('fr-FR', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            })
        );
    }

    function calculerMontantV2(){
        var listChamp = document.querySelectorAll('input[id^="pu_"]');
        let montantTtc = 0;
        for (let i = 0; i < listChamp.length; i++) {
            let pu = parseNombre(document.getElementById("pu_"+i)?.value || 0);
            let qte = parseNombre(document.getElementById("quantite_"+i)?.value || 0);
            let tva = parseNombre(document.getElementById("tva_"+i)?.value || 0);
            let remise = parseNombre(document.getElementById("remise_"+i)?.value || 0);
            let montantremise = (pu*qte)*(remise/100);
            montantTtc += ((pu*qte)-montantremise)+(((pu*qte)-montantremise)*(tva/100));
        }
        document.getElementById("montanttotal").innerHTML = montantTtc.toLocaleString('fr-FR');
    }

    document.addEventListener("DOMContentLoaded", function() {
        calculerMontantV2();
    });

    function deviseModification() {

        var nombreLigne = parseInt($("#nombreLigne").val());
        for(let iL=0;iL<nombreLigne;iL++){
            $(function(){
                var mapping = {
                    "AR": {
                        "table": "ST_INGREDIENTSAUTOACHAT_CPL",
                    },
                    "USD": {
                        "table": "ST_INGREDIENTSAUTOACHAT_CPL"
                    },
                    "EUR": {
                        "table": "ST_INGREDIENTSAUTOACHAT_CPL"
                    }
                };
                $("#deviseLibelle").html($('#idDevise').val());
                var idDevise = $('#idDevise').val();
                $("#idDevise_"+iL).val(idDevise);
                let autocompleteTriggered = false;
                $("#produit_"+iL+"libelle").autocomplete('destroy');
                $("#tauxDeChange_"+iL).val('');
                $("#pu_"+iL).val('');
                $("#produit_"+iL+"libelle").autocomplete({
                    source: function(request, response) {
                        $("#produit_"+iL).val('');
                        if (autocompleteTriggered) {
                            fetchAutocomplete(request, response, "null", "id", "null", mapping[idDevise].table, "produits.IngredientsLib", "true","pu;taux;compte_achat;compte_achat;unite");
                        }
                    },
                    select: function(event, ui) {
                        $("#produit_"+iL+"libelle").val(ui.item.label);
                        $("#produit_"+iL).val(ui.item.value);
                        $("#produit_"+iL).trigger('change');
                        $(this).autocomplete('disable');
                        var champsDependant = ['pu_'+iL,'tauxDeChange_'+iL,'compte_'+iL, 'comptelibelle_'+iL, 'unitelib_'+iL];
                        for(let i=0;i<champsDependant.length;i++){
                            $('#'+champsDependant[i]).val(ui.item.retour.split(';')[i]);
                        }
                        autocompleteTriggered = false;
                        return false;
                    }
                }).autocomplete('disable');
                $("#produit_"+iL+"libelle").off('keydown');
                $("#produit_"+iL+"libelle").keydown(function(event) {
                    if (event.key === 'Tab') {
                        event.preventDefault();
                        autocompleteTriggered = true;
                        $(this).autocomplete('enable').autocomplete('search', $(this).val());
                    }
                });
                $("#produit_"+iL+"libelle").off('input');
                $("#produit_"+iL+"libelle").on('input', function() {
                    $("#produit_"+iL).val('');
                    autocompleteTriggered = false;
                    $(this).autocomplete('disable');
                });
                $("#produit_"+iL+"searchBtn").off('click');
                $("#produit_"+iL+"searchBtn").click(function() {
                    autocompleteTriggered = true;
                    $("#produit_"+iL+"libelle").autocomplete('enable').autocomplete('search', $("#produit_"+iL+"libelle").val());
                });
            });
        }
    }
</script>
<%
} catch (Exception e) {
    e.printStackTrace();
%>
<script language="JavaScript">
    alert('<%=e.getMessage()%>');
    history.back();
</script>
<% }%>

