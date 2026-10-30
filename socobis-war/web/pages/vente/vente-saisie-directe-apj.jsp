<%--
    Document   : vente-saisie-directe-apj
    Created on : 22 mars 2024, 14:37:44
    Author     : Angela / APJ Migration
--%>
<%@ page import="client.Client" %>
<%@ page import="vente.Vente" %>
<%@ page import="vente.InsertionVente" %>
<%@ page import="user.UserEJB" %>
<%@ page import="vente.VenteDetails" %>
<%@ page import="affichage.PageInsertMultiple" %>
<%@ page import="utilitaire.Utilitaire" %>
<%@ page import="java.util.List" %>
<%@ page import="affichage.Liste" %>
<%@ page import="annexe.Unite" %>
<%@ page import="bean.TypeObjet" %>
<%@ page import="personnel.Personnel" %>
<%@ page import="paie.employe.EmployeComplet" %>
<%@ page import="vente.VenteDetailsLib" %>
<%@ page import="caisse.CaisseSession" %>
<%@ page import="java.io.IOException" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<%
    try {
        String classeMere = "vente.VentePaiement";
        String classeFille = "vente.VenteDetails";
        String butApresPost = "vente/vente-saisie-directe-apj.jsp";
        String colonneMere = "idVente";
        String nombreLigne = "1";
        int nbl = 1;
        String lien =(String) session.getValue("lien");

        String idPoint = (String)session.getAttribute("idPoint");
        Client client = new Client();
        client.setIdPoint(idPoint);
        client.clientPoint();

        String idClient = "CLI000576";
        String nomClient = "Client Divers";

        if(client.getId()!=null){
            idClient = client.getId();
            nomClient = client.getNom();
        }

        UserEJB u = (UserEJB) session.getValue("u");
        Vente mere = new InsertionVente();
        mere.setNomTable("INSERTION_VENTE_PERSONNEL_VIDE");
        VenteDetailsLib fille = new VenteDetailsLib();
        fille.setNomTable("VENTE_DETAILS_SAISIE_DIRECTE");

        PageInsertMultiple pi = new PageInsertMultiple(mere, fille, request, nbl, u);
        pi.setLien((String) session.getValue("lien"));
        affichage.Champ.setPageAppelComplete(pi.getFormufle().getChampFille("idProduit"),"produits.IngredientsLib","id","ST_INGREDIENTSAUTOVENTE_MGA2","pv;compte_vente;libelleComposant;codebarre;taux;photo;reference;unite","pu;compte;designation;codebarre;tauxDeChange;photo;reference;unite");

        pi.getFormu().getChamp("idMagasin").setDefaut(idPoint);
        pi.getFormu().getChamp("daty").setDefaut(Utilitaire.dateDuJour());
        pi.getFormu().getChamp("designation").setVisible(false);
        pi.getFormu().getChamp("idMagasin").setVisible(false);
        pi.getFormu().getChamp("daty").setVisible(false);

        // Client & Responsable
        pi.getFormu().getChamp("idClient").setLibelle("Client");
        pi.getFormu().getChamp("idResponsable").setLibelle("Responsable");
        pi.getFormu().getChamp("nomClientDivers").setLibelle("Nom du Client");
        pi.getFormu().getChamp("idResponsable").setDefaut(u.getUser().getTuppleID());
        pi.getFormu().getChamp("idResponsable").setVisible(false);
        pi.getFormu().getChamp("idClient").setAutocompleteDynamique("client.Client","nom","id","Client");

        // Masquage des champs framework superflus
        pi.getFormu().getChamp("remarque").setVisible(false);
        pi.getFormu().getChamp("etat").setVisible(false);
        pi.getFormu().getChamp("idOrigine").setVisible(false);
        pi.getFormu().getChamp("idDevise").setVisible(false);
        pi.getFormu().getChamp("estPrevu").setVisible(false);
        pi.getFormu().getChamp("datyPrevu").setVisible(false);
        pi.getFormu().getChamp("idCommercial").setVisible(false);
        pi.getFormu().getChamp("idmodepaiement").setVisible(false);
        pi.getFormu().getChamp("remise").setVisible(false);
        pi.getFormu().getChamp("remiseMontant").setVisible(false);
        pi.getFormu().getChamp("heureSaisie").setVisible(false);

        // Formulaire Fille
        pi.getFormufle().getChamp("couleurLib_0").setLibelle("Couleur");
        pi.getFormufle().getChamp("marqueLib_0").setLibelle("Taille");
        pi.getFormufle().getChamp("idProduit_0").setLibelle("Produit");
        pi.getFormufle().getChamp("tva_0").setLibelle("TVA");
        pi.getFormufle().getChamp("designation_0").setLibelle("Article");
        pi.getFormufle().getChamp("compte_0").setLibelle("Compte");
        pi.getFormufle().getChamp("remise_0").setLibelle("Remise en %");
        pi.getFormufle().getChamp("ristourne_0").setLibelle("Ristourne en %");
        pi.getFormufle().getChamp("remiseMontant_0").setLibelle("Remise en Montant");
        pi.getFormufle().getChamp("idOrigine_0").setLibelle("Origine");
        pi.getFormufle().getChamp("qte_0").setLibelle("Quantit&eacute;");
        pi.getFormufle().getChamp("photo_0").setLibelle("Image");
        pi.getFormufle().getChamp("pu_0").setLibelle("Montant Sans TVA");
        pi.getFormufle().getChamp("pu_0").setVisible(false);
        pi.getFormufle().getChamp("reference_0").setLibelle("R&eacute;f&eacute;rence");
        pi.getFormufle().getChamp("unite_0").setLibelle("Unit&eacute;");
        pi.getFormufle().getChamp("tauxDeChange_0").setLibelle("Taux de change");
        pi.getFormufle().getChamp("idDevise_0").setLibelle("Devise");

        affichage.Champ.setAutre(pi.getFormufle().getChampFille("pu"),"onChange='updateTotals()' readonly");
        affichage.Champ.setVisible(pi.getFormufle().getChampFille("pu"),false);
        affichage.Champ.setAutre(pi.getFormufle().getChampFille("qte"),"onChange='updateTotals()'");
        affichage.Champ.setAutre(pi.getFormufle().getChampFille("remiseMontant"),"onChange='updateTotals()'");
        affichage.Champ.setAutre(pi.getFormufle().getChampFille("compte"),"readonly");
        affichage.Champ.setAutre(pi.getFormufle().getChampFille("tauxDeChange"),"readonly");
        affichage.Champ.setAutre(pi.getFormufle().getChampFille("designation"),"readonly");
        affichage.Champ.setDefaut(pi.getFormufle().getChampFille("tauxDeChange"),"1");
        affichage.Champ.setDefaut(pi.getFormufle().getChampFille("idDevise"),"Ar");

        pi.getFormufle().getChampMulitple("compte").setVisible(false);
        pi.getFormufle().getChampMulitple("idDevise").setVisible(false);
        pi.getFormufle().getChampMulitple("tauxDeChange").setVisible(false);
        pi.getFormufle().getChampMulitple("remise").setVisible(false);
        pi.getFormufle().getChampMulitple("remiseMontant").setVisible(false);
        pi.getFormufle().getChampMulitple("reference").setVisible(false);
        pi.getFormufle().getChampMulitple("tva").setVisible(false);
        pi.getFormufle().getChampMulitple("idVente").setVisible(false);
        pi.getFormufle().getChampMulitple("montant").setVisible(false);
        pi.getFormufle().getChampMulitple("idProduit").setVisible(false);
        pi.getFormufle().getChampMulitple("id").setVisible(false);
        pi.getFormufle().getChampMulitple("ristourne").setVisible(false);
        pi.getFormufle().getChampMulitple("IdOrigine").setVisible(false);
        pi.getFormufle().getChampMulitple("puAchat").setVisible(false);
        pi.getFormufle().getChampMulitple("puVente").setVisible(false);
        pi.getFormufle().getChampMulitple("unite").setVisible(false);

        pi.preparerDataFormu();
        String[] ordreFille = new String[]{"photo", "idProduit", "designation", "couleurLib", "marqueLib", "pu", "qte"};
        pi.getFormufle().setColOrdre(ordreFille);

        pi.getFormu().makeHtmlInsertTabIndex();
        pi.getFormufle().makeHtmlInsertTableauIndex();
%>

<link href="${pageContext.request.contextPath}/assets/css/vente-directe.css?v=86" rel="stylesheet" type="text/css" />

<!-- HEADER CAISSE -->
<div class="vd-header">
    <img src="${pageContext.request.contextPath}/assets/img/logo_A.png" alt="logo" id="site-logo">
    <div class="form-input w-100">
        <input value="" class="form-control ui-autocomplete-input ui-autocomplete-loading" id="idProduit_libelle" type="text"
               oninput="autoCompleteVenteDirect('idProduit_libelle','ST_INGREDIENTSVENTEPOS','produits.IngredientsLib',
      'photo;libelle;reference;quantite=1;pvTva;pv;codegros;pvgros;remise=0;prix=0;unite;compte_vente;couleurLib;marqueLib;codeBarre;tva;montantTTC',
      'photo;designation;reference;qte;pvTva;pu;codegros;pu_pvgros;remiseMontant;prix;unite;compte;couleurLib;marqueLib;codeBarre;tva;montantTTC','id')"
        />
        <input value='' class='form-control' id='idProduit' name='idProduit' type='hidden'/>
    </div>
    <div class="d-flex gap-2">
        <a class="btn btn-tertiary" href="${pageContext.request.contextPath}/pages/module.jsp?but=vente/vente-saisie-directe-apj.jsp" target="_blank">
            <i class="material-symbols-rounded">add</i>
        </a>
        <div class="dropdown">
            <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                Mouvement <span class="material-symbols-rounded">keyboard_arrow_down</span>
            </button>
            <ul class="dropdown-menu" aria-labelledby="dropdownMenu1">
                <li><a href="${pageContext.request.contextPath}/pages/module.jsp?but=caisse/mvt/mvtcaisse-entree-pos.jsp">Entrée de caisse</a></li>
                <li><a href="${pageContext.request.contextPath}/pages/module.jsp?but=caisse/mvt/mvtcaisse-sortie-pos.jsp">Sortie de caisse</a></li>
                <li><a href="${pageContext.request.contextPath}/pages/module.jsp?but=caisse/virementIntraCaisse/virementIntraCaisse-saisie-pos.jsp">Transfert de caisse</a></li>
            </ul>
        </div>
        <a class="btn btn-tertiary" href="${pageContext.request.contextPath}/pages/module.jsp?but=caisse/cloturecaisse/cloturecaisse-saisie.jsp">Clôture de caisse</a>
    </div>
    <div class="btn btn-tertiary">
        <i class="material-symbols-rounded">account_circle</i>
        <%=u.getCnapsUser().getUsername()%>
    </div>
</div>

<!-- CONTENU PRINCIPAL CAISSE -->
<div class="content-wrapper">
    <div class="box-body">
        <form class='container' id="venteForm" data-pos-screen data-keyboard-default="on" action="vente/apresVenteDirecteOptim.jsp" method="post">
            <div class="row">
                <!-- CONTENEUR GAUCHE : TABLEAU PANIER & CLAVIER -->
                <div class="col-md-8 d-flex vente-directe-left-container">
                    <div class="w-100" id="formTableContainer">
                        <% out.println(pi.getFormu().getHtmlInsert()); %>
                        <br>
                        <div id="butfillejsp">
                            <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>
                            <input name="nombreLigne" type="hidden" id="nombreLigne" value="<%= nombreLigne %>">
                        </div>
                    </div>
                    <!-- CONTENEUR CLAVIER VIRTUEL -->
                    <div class="d-none fade w-100" id="clavierContainer">
                        <button type="button" class="btn btn-secondary" id="btnCloseClavier">
                            <i class="material-symbols-rounded">close</i>
                        </button>
                        <%=pi.getHtmlClavierVisuel()%>
                    </div>
                </div>

                <!-- CONTENEUR DROIT : RÉCAPITULATIF & ENCAISSEMENT -->
                <div class="col-md-4">
                    <div class="d-flex vente-directe-right-container">
                        <h2 class="h520pxSemibold m-0">R&eacute;sum&eacute; des commandes</h2>
                        <div class="box">
                            <div class="box-body recap-achat upside">
                                <div class="d-flex"><p class="Body14pxRegular">Sous-total</p> <p id="recapSousTotal" class="Body14pxRegular">0 Ar</p> </div>
                                <div class="d-flex"><p class="Body14pxRegular">Remises totales</p> <p id="recapRemises" class="Body14pxRegular">0 Ar</p> </div>
                            </div>
                            <div class="box-footer total-container">
                                <p class="Body12pxBold">Total à payer</p>
                                <p id="totalGeneral" class="h142pxBold" style="color: var(--VD-main-color);">0 Ar</p>
                                <input type="hidden" name="totalGeneralInput" id="totalGeneralInput">
                                <div class="d-flex gap-2" style="justify-content: end; color: var(--Text-primary);">
                                    <p class="Body14pxRegular">Montant arrondi espèces :</p>
                                    <p id="montantArrondiEspeces" class="Body14pxRegular" style="font-weight: 600;">0 Ar</p>
                                </div>
                            </div>
                            <div class="box-body recap-achat downside" style="border-top: 1px solid #eee; padding-top: 10px;">
                                <div class="d-flex">
                                    <p class="Body14pxRegular">Montant de la facture</p>
                                    <p id="montantFacture" class="Body14pxRegular" style="color: var(--VD-main-color);">0 Ar</p>
                                </div>
                                <div class="d-flex"><p class="h520pxRegular">Montant payé</p> <p id="montantPayerTexte" class="h235pxBold" style="color: var(--VD-main-color);">0 Ar</p> </div>
                                <div class="d-flex"><p class="h520pxRegular">&Agrave; retourner</p> <p id="montantRetourner" name="montantRetourner" class="h235pxBold" style="color: var(--VD-main-color);">0 Ar</p> </div>
                                <input type="hidden" name="aretourner" id="aretourner">
                            </div>
                        </div>

                        <!-- OPTION PAIEMENT MULTIPLE -->
                        <label class="toggle-button link">
                            <input type="checkbox" id="multiple-payment">
                            <div class="d-flex">
                                <span class="label" style="white-space: nowrap">Paiement multiple</span>
                            </div>
                            <input type="hidden" name="multiplePayment" id="multiplePaymentInput" value="false" />
                        </label>

                        <!-- SÉLECTION DES 8 MODES DE PAIEMENT -->
                        <div class="paiement-container">
                            <div class="toggle-container">
                                <label class="toggle-button active">
                                    <input type="radio" name="payment-method" value="especes" checked>
                                    <div class="checkbox"><span class="checkmark"><i class="material-symbols-rounded">check</i></span></div>
                                    <div class="d-flex"><img src="${pageContext.request.contextPath}/assets/img/payments.png"><span class="label">Espèces</span></div>
                                </label>
                                <label class="toggle-button">
                                    <input type="radio" name="payment-method" value="cheque">
                                    <div class="checkbox"><span class="checkmark"><i class="material-symbols-rounded">check</i></span></div>
                                    <div class="d-flex"><img src="${pageContext.request.contextPath}/assets/img/checkbook.png"><span class="label">Chèque</span></div>
                                </label>
                                <label class="toggle-button">
                                    <input type="radio" name="payment-method" value="mvola">
                                    <div class="checkbox"><span class="checkmark"><i class="material-symbols-rounded">check</i></span></div>
                                    <div class="d-flex"><img src="${pageContext.request.contextPath}/assets/img/mvola.png"></div>
                                </label>
                                <label class="toggle-button">
                                    <input type="radio" name="payment-method" value="airtel">
                                    <div class="checkbox"><span class="checkmark"><i class="material-symbols-rounded">check</i></span></div>
                                    <div class="d-flex"><img src="${pageContext.request.contextPath}/assets/img/airtel.png"></div>
                                </label>
                                <label class="toggle-button">
                                    <input type="radio" name="payment-method" value="orange">
                                    <div class="checkbox"><span class="checkmark"><i class="material-symbols-rounded">check</i></span></div>
                                    <div class="d-flex"><img src="${pageContext.request.contextPath}/assets/img/orangemoney.png"></div>
                                </label>
                                <label class="toggle-button">
                                    <input type="radio" name="payment-method" value="visa">
                                    <div class="checkbox"><span class="checkmark"><i class="material-symbols-rounded">check</i></span></div>
                                    <div class="d-flex"><img src="${pageContext.request.contextPath}/assets/img/visa.png"></div>
                                </label>
                                <label class="toggle-button">
                                    <input type="radio" name="payment-method" value="bonAchat">
                                    <div class="checkbox"><span class="checkmark"><i class="material-symbols-rounded">check</i></span></div>
                                    <div class="d-flex"><i class="material-symbols-rounded">hand_package</i><label class="label">BON D'ACHAT</label></div>
                                </label>
                                <label class="toggle-button">
                                    <input type="radio" name="payment-method" value="cadeaux">
                                    <div class="checkbox"><span class="checkmark"><i class="material-symbols-rounded">check</i></span></div>
                                    <div class="d-flex"><i class="material-symbols-rounded">redeem</i><span class="label">CARTE CADEAUX</span></div>
                                </label>
                            </div>
                        </div>

                        <!-- INPUTS DES 8 MODES DE PAIEMENT -->
                        <div class="input-container">
                            <!-- Espèces -->
                            <div class="form-input w-100 paiement-section" data-mode="especes">
                                <label class="input-label">Montant payé</label>
                                <div class="d-flex input-with-addons pos-payment-input-with-addon">
                                    <span class="pos-payment-addon">ESPÈCES</span>
                                    <input class="form-control" name="valeurEspece" id="valeurEspece" value="" autocomplete="off" />
                                </div>
                            </div>
                            <!-- Mvola -->
                            <div class="form-input paiement-section d-none" data-mode="mvola">
                                <label class="input-label">Référence</label>
                                <div class="d-flex input-with-addons pos-payment-input-with-addon"><span class="pos-payment-addon">MVOLA</span><input class="form-control" id="refMvola" name="refMvola"/></div>
                            </div>
                            <div class="form-input paiement-section d-none" data-mode="mvola">
                                <label class="input-label">Montant payé</label>
                                <div class="d-flex input-with-addons pos-payment-input-with-addon"><span class="pos-payment-addon">MVOLA</span><input class="form-control" id="montantMvola" name="montantMvola"/></div>
                            </div>
                            <!-- Orange Money -->
                            <div class="form-input paiement-section d-none" data-mode="orange">
                                <label class="input-label">Référence</label>
                                <div class="d-flex input-with-addons pos-payment-input-with-addon"><span class="pos-payment-addon">ORANGE MONEY</span><input class="form-control" id="refOrange" name="refOrange"/></div>
                            </div>
                            <div class="form-input paiement-section d-none" data-mode="orange">
                                <label class="input-label">Montant payé</label>
                                <div class="d-flex input-with-addons pos-payment-input-with-addon"><span class="pos-payment-addon">ORANGE MONEY</span><input class="form-control" id="montantOrange" name="montantOrange"/></div>
                            </div>
                            <!-- Airtel Money -->
                            <div class="form-input paiement-section d-none" data-mode="airtel">
                                <label class="input-label">Référence</label>
                                <div class="d-flex input-with-addons pos-payment-input-with-addon"><span class="pos-payment-addon">AIRTEL MONEY</span><input class="form-control" id="refAirtel" name="refAirtel"/></div>
                            </div>
                            <div class="form-input paiement-section d-none" data-mode="airtel">
                                <label class="input-label">Montant payé</label>
                                <div class="d-flex input-with-addons pos-payment-input-with-addon"><span class="pos-payment-addon">AIRTEL MONEY</span><input class="form-control" id="montantAirtel" name="montantAirtel"/></div>
                            </div>
                            <!-- Chèque -->
                            <div class="form-input paiement-section d-none" data-mode="cheque">
                                <label class="input-label">Numéro chèque</label>
                                <div class="d-flex input-with-addons pos-payment-input-with-addon"><span class="pos-payment-addon">CHÈQUE</span><input class="form-control" id="refCheque" name="refCheque"/></div>
                            </div>
                            <div class="form-input paiement-section d-none" data-mode="cheque">
                                <label class="input-label">Montant payé</label>
                                <div class="d-flex input-with-addons pos-payment-input-with-addon"><span class="pos-payment-addon">CHÈQUE</span><input class="form-control" id="montantCheque" name="montantCheque"/></div>
                            </div>
                            <!-- Visa -->
                            <div class="form-input paiement-section d-none" data-mode="visa">
                                <label class="input-label">Référence Visa</label>
                                <div class="d-flex input-with-addons pos-payment-input-with-addon"><span class="pos-payment-addon">VISA</span><input class="form-control" id="refVisa" name="refVisa"/></div>
                            </div>
                            <div class="form-input paiement-section d-none" data-mode="visa">
                                <label class="input-label">Montant payé</label>
                                <div class="d-flex input-with-addons pos-payment-input-with-addon"><span class="pos-payment-addon">VISA</span><input class="form-control" id="montantVisa" name="montantVisa"/></div>
                            </div>
                            <!-- Bon d'achat -->
                            <div class="form-input paiement-section d-none" data-mode="bonAchat">
                                <label class="input-label">Référence du bon d'achat</label>
                                <div class="d-flex input-with-addons pos-payment-input-with-addon"><span class="pos-payment-addon">BON D'ACHAT</span><input class="form-control" id="refBOA" name="refBOA"/></div>
                            </div>
                            <div class="form-input paiement-section d-none" data-mode="bonAchat">
                                <label class="input-label">Montant payé</label>
                                <div class="d-flex input-with-addons pos-payment-input-with-addon"><span class="pos-payment-addon">BON D'ACHAT</span><input class="form-control" id="montantBOA" name="montantBOA"/></div>
                            </div>
                            <!-- Carte Cadeaux -->
                            <div class="form-input paiement-section d-none" data-mode="cadeaux">
                                <label class="input-label">Référence de la carte cadeaux</label>
                                <div class="d-flex input-with-addons pos-payment-input-with-addon"><span class="pos-payment-addon">CARTE CADEAU</span><input class="form-control" id="refCarte" name="refCarte"/></div>
                            </div>
                            <div class="form-input paiement-section d-none" data-mode="cadeaux">
                                <label class="input-label">Montant payé</label>
                                <div class="d-flex input-with-addons pos-payment-input-with-addon"><span class="pos-payment-addon">CARTE CADEAU</span><input class="form-control" id="montantCarte" name="montantCarte"/></div>
                            </div>
                        </div>

                        <!-- BOUTON PAYER -->
                        <button type="submit" class="btn btn-primary btn-large w-100" style="justify-content: center;align-items: center">Payer</button>
                    </div>
                </div>
            </div>

            <!-- CHAMPS CACHÉS SOUMISSION BDD -->
            <input name="acte" type="hidden" id="nature" value="insert">
            <input name="action" type="hidden" id="action" value="insert">
            <input name="bute" type="hidden" id="bute" value="<%= butApresPost %>">
            <input name="classe" type="hidden" id="classe" value="<%= classeMere %>">
            <input name="classefille" type="hidden" id="classefille" value="<%= classeFille %>">
            <input name="nombreLigne" type="hidden" id="nombreLigne" value="<%= nombreLigne %>">
            <input name="colonneMere" type="hidden" id="colonneMere" value="<%= colonneMere %>">
            <input name="nomtable" type="hidden" id="nomtable" value="Vente_Details">
            <input type="hidden" id="idPoint" value="<%=session.getAttribute("idPoint")%>">
        </form>
    </div>
</div>

<!-- GLOBAL LOADER -->
<div id="globalLoader" style="display:none;position:fixed;top:0;left:0;width:100vw;height:100vh;z-index:99999;background:rgba(255,255,255,0.7);justify-content:center;align-items:center;">
    <div style="text-align:center;">
        <div class="spinner-border" role="status"></div>
        <div style="margin-top:1rem;font-size:1.2rem;">Chargement...</div>
    </div>
</div>

<!-- MODAL D'AIDE DES RACCOURCIS CLAVIER -->
<div id="pos-help-modal" class="pos-help-modal" role="dialog" aria-modal="true" aria-labelledby="pos-help-title" aria-hidden="true">
    <div class="pos-help-modal-content">
        <div class="pos-help-modal-header">
            <h2 id="pos-help-title" class="h617pxSemibold">Tous les raccourcis clavier</h2>
            <button type="button" class="pos-help-close" id="pos-help-close" aria-label="Fermer l'aide">&times;</button>
        </div>
        <div class="pos-help-modal-body">
            <section class="pos-help-section">
                <h3 class="h617pxSemibold">Global</h3>
                <div class="pos-help-list">
                    <div class="pos-help-row"><span class="Body14pxRegular">Ctrl + Entrer</span><p class="Body14pxRegular">Valider : Commande, Clôture caisse, Déclaration espèces, Ouverture de caisse.</p></div>
                    <div class="pos-help-row"><span class="Body14pxRegular">F1 / Ctrl + H</span><p class="Body14pxRegular">Afficher ou masquer l'aide des raccourcis clavier</p></div>
                    <div class="pos-help-row"><span class="Body14pxRegular">Entrer</span><p class="Body14pxRegular">Validation simple (Ex : Valider un produit recherché)</p></div>
                    <div class="pos-help-row"><span class="Body14pxRegular">Shift + Tab</span><p class="Body14pxRegular">Basculer entre le panier et le mode de paiement</p></div>
                    <div class="pos-help-row"><span class="Body14pxRegular">Shift + M</span><p class="Body14pxRegular">Saisie montant</p></div>
                    <div class="pos-help-row"><span class="Body14pxRegular">Shift + F / Ctrl + F</span><p class="Body14pxRegular">Saisie code barre / Recherche produit</p></div>
                    <div class="pos-help-row"><span class="Body14pxRegular">Directions ←→↑↓</span><p class="Body14pxRegular">Navigation dans la recherche, le header, les listes déroulantes, le panier et les modes de paiement</p></div>
                    <div class="pos-help-row"><span class="Body14pxRegular">Touches<br>lettres &amp; chiffres</span><p class="Body14pxRegular">Entrer code barre / nom produit / référence produit</p></div>
                </div>
            </section>
            <section class="pos-help-section">
                <h3 class="h617pxSemibold">Navigation Panier</h3>
                <div class="pos-help-list">
                    <div class="pos-help-row"><span class="Body14pxRegular">Shift + Tab</span><p class="Body14pxRegular">Basculer entre le panier et le mode de paiement</p></div>
                    <div class="pos-help-row"><span class="Body14pxRegular">(+ / −)</span><p class="Body14pxRegular">Ajout et diminuer nombre</p></div>
                    <div class="pos-help-row"><span class="Body14pxRegular">Suppr</span><p class="Body14pxRegular">Supprimer un article</p></div>
                </div>
            </section>
        </div>
    </div>
</div>

<!-- ================================================================ -->
<!-- INCLUSION DES MODULES JAVASCRIPT POS (REFACTORISÉS)              -->
<!-- ================================================================ -->

<!-- 1. Variables de Contexte Global -->
<script>
    window.contextPath = '${pageContext.request.contextPath}';
    window.lienAccueil = '<%=lien%>';
    window.idPoint = '<%=session.getAttribute("idPoint")%>';
    // Valeur provisoire pour les modules chargés avant le contrôleur APJ.
    window.isKeyboardMode = document.getElementById('venteForm').getAttribute('data-keyboard-default') !== 'off';
</script>

<!-- 2. Socket.io -->
<script src="${pageContext.request.contextPath}/assets/js/socket.io.min.js"></script>

<!-- 3. Vos 6 Modules Métier POS -->
<script src="${pageContext.request.contextPath}/assets/js/pos/pos-core.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/pos/pos-basket-handlers.js?v=keyboard-mode-1"></script>
<script src="${pageContext.request.contextPath}/assets/js/pos/pos-payment.js?v=codal-parity-1"></script>
<script src="${pageContext.request.contextPath}/assets/js/pos/pos-ui-handlers.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/pos/pos-hardware-handlers.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/pos/pos-keyboard.js?v=controller-1"></script>
<script src="${pageContext.request.contextPath}/assets/js/pos/pos-events.js?v=keyboard-mode-1"></script>

<!-- 4. Initialisation globale au chargement du DOM -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Initialisation des données Client
        const elNom = document.getElementById("idClientlibelle");
        const elId = document.getElementById("idClient");
        if (elNom) elNom.value = "<%=nomClient%>";
        if (elId) elId.value = "<%=idClient%>";

        // Initialisation des handlers et protections
        if (typeof initVirtualKeyboard === 'function') initVirtualKeyboard();
        if (typeof verrouillerChampsButFille === 'function') verrouillerChampsButFille();
        if (typeof masquerEnTeteImageEnDouble === 'function') masquerEnTeteImageEnDouble();
        if (typeof nettoyerBoutonsTableau === 'function') nettoyerBoutonsTableau();
        if (typeof attachSubmitGuard === 'function') attachSubmitGuard();
        if (typeof checkPaymentValidation === 'function') checkPaymentValidation();
        if (typeof updateTotals === 'function') updateTotals();
        if (typeof addLinkAccueilLogo === 'function') addLinkAccueilLogo();

        // Masquer la ligne 0 modèle
        const row0 = document.getElementById("ligne-multiple-0");
        if (row0) row0.classList.add("d-none");

        // Focus Scanner & Message Afficheur
        if (typeof focusOnProduit === 'function') focusOnProduit();
        if (typeof restoreCartState === 'function') restoreCartState();
        if (typeof sendMessage === 'function') sendMessage("BIENVENUE !!!");
    });
</script>

<%
} catch (Exception e) {
    e.printStackTrace();
%>
<div id="errorMessage" class="alert alert-danger" style="position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); z-index: 99999; min-width: 300px; padding: 20px; text-align: center;">
    <%= e.getMessage() %>
</div>
<%--<script>--%>
<%--    setTimeout(function(){--%>
<%--        window.location.href = "${pageContext.request.contextPath}/pages/module.jsp?but=caisse/ouveturecaisse/ouverturecaisse-saisie.jsp";--%>
<%--    }, 900);--%>
<%--</script>--%>
<% } %>
