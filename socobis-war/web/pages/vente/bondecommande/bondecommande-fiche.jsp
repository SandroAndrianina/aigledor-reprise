<%@page import="faturefournisseur.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="user.*" %>
<%@ page import="bean.*" %>
<%@ page import="utilitaire.*" %>
<%@ page import="affichage.*" %>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="vente.BonDeCommandeCpl"%>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="fichier.AttacherFichier" %>
<%@ page import="uploadbean.UploadService" %>
<%@ page import="java.util.LinkedHashMap" %>

<%
    UserEJB u = (user.UserEJB)session.getValue("u");
%>
<%
    try{
    session.removeAttribute("idMagasin");
    session.removeAttribute("idclient");
    session.removeAttribute("idDevise");
    session.removeAttribute("modelivraison");
    session.removeAttribute("modepaiement");
    String lien = (String) session.getValue("lien");
    BonDeCommandeCpl f = new BonDeCommandeCpl();
    f.setNomTable("BONDECOMMANDE_CLIENT_CPL_M");
    PageConsulte pc = new PageConsulte(f, request, u);
    pc.setTitre("Fiche du bon de commande client");
    BonDeCommandeCpl bc=(BonDeCommandeCpl)pc.getBase();
    String id=pc.getBase().getTuppleID();
    pc.getChampByName("id").setLibelle("Id");
    pc.getChampByName("reference").setLibelle("R&eacute;f&eacute;rence");
    pc.getChampByName("daty").setLibelle("date");
    pc.getChampByName("remarque").setLibelle("Remarque");
    pc.getChampByName("modepaiementlib").setLibelle("Mode de paiement");
    pc.getChampByName("designation").setLibelle("d&eacute;signation");
    pc.getChampByName("idclientlib").setLibelle("Client");
    pc.getChampByName("idClientLib").setLien((String) session.getValue("lien") + "?but=client/client-fiche.jsp&id=" + bc.getIdClient(), "nom=");
    pc.getChampByName("etatlib").setLibelle("Etat");
    pc.getChampByName("etat").setVisible(false);
    pc.getChampByName("idclient").setVisible(false);
    pc.getChampByName("IdMagasin").setVisible(false);
    pc.getChampByName("idMagasinLib").setLibelle("Magasin");
    pc.getChampByName("id").setVisible(false);
    pc.getChampByName("idproforma").setLien(lien+"?but=vente/proforma/proforma-fiche.jsp", "id=");
    pc.getChampByName("idproforma").setLibelle("Ref. Proforma");
    pc.getChampByName("modelivraisonlib").setLibelle("Mode de livraison");
        pc.getChampByName("nbfacture").setLibelle("Nombre de facture");
        pc.getChampByName("facturelib").setLibelle("Statut");
    pc.getChampByName("numeroBc").setLibelle("Num&eacute;ro du bon de commande");
    pc.getChampByName("nbfacture").setVisible(false);
    pc.getChampByName("etatlib").setLibelle("&Eacute;tat");
//    pc.getChampByName("montantTTC").setLibelle("Montant TTC");
//    pc.getChampByName("montantTTCAr").setLibelle("Montant TTC en Ar");
    pc.getChampByName("modepaiement").setVisible(false);
    pc.getChampByName("lieuLivraison").setLibelle("Lieu de livraison");
    pc.getChampByName("dateLivraison").setLibelle("Date de livraison");

    pc.getChampByName("idDevise").setVisible(false);
    pc.getChampByName("montantTtcAr").setLibelle("Montant TTC (AR)");
    pc.getChampByName("montantHT").setLibelle("Montant HT ("+bc.getIdDevise()+")");
    pc.getChampByName("montantRemise").setLibelle("Montant de la Remise ("+bc.getIdDevise()+")");
    pc.getChampByName("montantTva").setLibelle("Montant TVA ("+bc.getIdDevise()+")");
    pc.getChampByName("montantTTC").setLibelle("Montant TTC ("+bc.getIdDevise()+")");

        if(bc.getIdDevise().compareToIgnoreCase("AR")==0) {
            pc.getChampByName("montantTtcAr").setVisible(false);
        }

        String[] ordre = {
                "daty",
                "designation",
                "idClientLib",
                "numeroBC",
                "reference",
                "idMagasinLib",
                "idproforma",
                "idDevise",
                "modepaiementlib",
                "modelivraisonlib",
                "dateLivraison",
                "lieuLivraison",
                "montantHT",
                "montantRemise",
                "montantTva",
                "montantTTC",
                "montantTTCAr",
                "etatlib",
                "remarque"
        };
        pc.setOrdre(ordre);

    String pageActuel = "vente/bondecommande/bondecommande-fiche.jsp";

    String pageModif = "vente/bondecommande/bondecommande-saisie.jsp";
    String classe = "vente.BonDeCommande";
    // Contexte serveur du fragment Historique : ne pas lire ces valeurs depuis ses filtres.
    request.setAttribute("historiqueRefObjet", pc.getBase().getTuppleID());
    request.setAttribute("historiqueObjets", new String[] {classe});
    request.setAttribute("historiqueFiche", "vente/bondecommande/bondecommande-fiche.jsp");
    request.setAttribute("historiqueOnglet", "../../historique/inc/historique-liste");

    
    Map<String, String> map = new HashMap<String, String>();
    map.put("inc/bondecommande-detail", "");
    map.put("inc/bondecommande-liste-detail", "");
    map.put("inc/bondecommande-besoin", "");
    map.put("inc/livraisondetail-bc", "");
    map.put("inc/fabricationdetail-bc", "");
    map.put("inc/vente-cpl-visee", "");
    map.put("../../historique/inc/historique-liste", "");

    String tab = request.getParameter("tab");
    if (tab == null) {
        tab = "inc/bondecommande-detail";
    }

    map.put(tab, "active");
    tab = tab + ".jsp";
    f = (BonDeCommandeCpl)pc.getBase();
    boolean estFacturee = f.estFacture(null);

        AttacherFichier[] fichiers = UploadService.getUploadFile(request.getParameter("id"));
        configuration.CynthiaConf.load();
        String cdn = configuration.CynthiaConf.properties.getProperty("cdnReadUri");
        String projectName = pc.getChampByName("designation").getValeur()
                .replace("'","_")
                .replace("/","_")
                .replace("-","_")
                .replace(":", "_")
                .replace("*", "_")
                .replace(" ", "_");
        String dossier = "bondecommande/files/" + projectName;
        LinkedHashMap<String, String[]> rubrique = new LinkedHashMap<>();
        rubrique.put("Information g&eacute;n&eacute;rale", new String[]{"daty",
                "designation",
                "idClientLib",
                "numeroBC",
                "reference",
                "idMagasinLib",
                "idproforma",
                "idDevise",
                "modepaiementlib",
                "modelivraisonlib",
                "dateLivraison",
                "lieuLivraison",});
        rubrique.put("Financi&egrave;re", new String[]{
                "montantHT",
                "montantRemise",
                "montantTva",
                "montantTTC",
                "montantTTCAr",});
        pc.setRubrique(rubrique);
%>

<div class="content-wrapper">
    <h1 class="box-title"><a href=<%= lien + "?but=bondecommande/bondecommande-fiche.jsp"%>> <i class="fa fa-angle-left"></i></a><%=pc.getTitre()%></h1>
    <div class="row m-0">
        <div class="col-md-12 mb-5">
            <div class="box-fiche">
                <div class="box">
                    <div class="box-body">
                        <%
                            out.println(pc.getHtml());
                        %>
                        <br/>
                        <div class="box-footer">
<%--                            <a class="btn btn-success pull-right" href="<%= (String) session.getValue("lien") + "?but=fabrication/fabrication-saisie.jsp&idBC=" + id + "&classe=" + classe%> " style="margin-right: 10px">Fabriquer</a>--%>
<%--                            <a class="btn btn-success pull-right" href="<%= (String) session.getValue("lien") + "?but=fabrication/ordre-fabrication-saisie.jsp&idBC=" + id + "&classe=" + classe%> " style="margin-right: 10px">Generer OF</a>--%>
                            <% if (f.getEtat() < ConstanteEtat.getEtatValider()) {%>
                                <a class="btn btn-primary pull-right" href="<%= (String) session.getValue("lien") + "?but=apresTarif.jsp&acte=valider&id=" + request.getParameter("id") + "&bute=vente/bondecommande/bondecommande-fiche.jsp&classe=" + classe %> " style="margin-right: 10px">Viser</a>
                                <a class="btn btn-secondary pull-right"  href="<%= lien + "?but="+ pageModif +"&acte=update&id=" + id%>" style="margin-right: 10px">Modifier</a>
                            <% } %>
                            <% if (f.getEtat() == ConstanteEtat.getEtatValider()) {%>
                                <a class="btn btn-primary pull-right" href="<%= (String) session.getValue("lien") + "?but=bondelivraison-client/bondelivraison-client-saisie.jsp&idbc_client=" + request.getParameter("id") + "&classe=" + classe+"&idMagasin="+f.getIdMagasin()%> " style="margin-right: 10px">Livrer</a>
                                <!--a class="btn btn-primary pull-right" href="<%= (String) session.getValue("lien") + "?but=apresTarif.jsp&acte=valider&id=" + request.getParameter("id") + "&bute=vente/bondecommande/bondecommande-fiche.jsp&classe=" + classe %> " style="margin-right: 10px">Cl&ocirc;turer</a-->
                            <% }if (f.getEtat() == ConstanteEtat.getEtatValider()&&!estFacturee) {%>
                                <a class="btn btn-secondary pull-right" href="<%= (String) session.getValue("lien") + "?but=vente/vente-saisie.jsp&idBC=" + request.getParameter("id")%> " style="margin-right: 10px">Facturer</a>
                            <% } %>
<%--                            <a class="btn btn-tertiary pull-right"  href="${pageContext.request.contextPath}/ExportPDF?action=bc_client&id=<%=request.getParameter("id")%>" style="margin-right: 10px">Imprimer en PDF</a>--%>
                            <a class="btn btn-tertiary pull-right" href="<%= (String) session.getValue("lien") + "?but=pageupload.jsp&id=" + request.getParameter("id") + "&dossier=" + dossier + "&nomtable=ATTACHER_FICHIER&procedure=GETSEQ_ATTACHER_FICHIER&bute=" + pageActuel + "&id=" + request.getParameter("id") + "&nomprj=" + pc.getChampByName("designation").getValeur() %>" style="margin-right: 10px;">Attacher Fichier</a>

                            <%
                                // inline=1 -> ExportPDF sert le PDF en Content-Disposition: inline (+ application/pdf), ce qui permet le rendu dans l'iframe du modal.
                                // Sans le parametre : disposition attachment, comportement d'origine.
                                String pdfBase = request.getContextPath() + "/ExportPDF?action=bc_client&id="
                                        + URLEncoder.encode(id, "UTF-8");
                                String pdfApercu = pdfBase + "&inline=1";
                            %>
                            <button type="button" class="btn btn-tertiary pull-right"
                                    data-url="<%= pdfApercu %>"
                                    data-dl="<%= pdfBase %>"
                                    data-kind="pdf"
                                    data-name="Bon de commande <%= id %>"
                                    onclick="openPreview(this.dataset.url, this.dataset.kind, this.dataset.name, this.dataset.dl)"
                                    style="margin-right: 10px">Imprimer en PDF
                            </button>
                       </div>
                        <br/>

                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="row m-0">
        <div class="col-md-12 nopadding">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <!-- a modifier -->
                    <li class="<%=map.get("inc/bondecommande-detail")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/bondecommande-detail">Détails </a></li>
<%--                    <li class="<%=map.get("inc/bondecommande-liste-detail")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/bondecommande-liste-detail">Détails de Fabrication</a></li>--%>
<%--                    <li class="<%=map.get("inc/bondecommande-besoins")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/bondecommande-besoins">Besoins</a></li>--%>
                    <li class="<%=map.get("inc/livraisondetail-bc")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/livraisondetail-bc">Livraisons</a></li>
<%--                    <li class="<%=map.get("inc/fabricationdetail-bc")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/fabricationdetail-bc">Fabrications</a></li>--%>
<%--                    <li class="<%=map.get("inc/ordre-fabrication-details")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/ordre-fabrication-details">Ordre de Fabrication</a></li>--%>
                    <li class="<%=map.get("inc/vente-cpl-visee")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/vente-cpl-visee">Factures</a></li>
                    <li class="<%=map.get("../../historique/inc/historique-liste")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=../../historique/inc/historique-liste">Historique</a></li>

                </ul>
                <div class="tab-content">
                    <jsp:include page="<%= tab %>" >
                        <jsp:param name="idbc" value="<%= id %>" />
                    </jsp:include>
                </div>
            </div>

        </div>

        <div class="row m-0">
            <div class="col-md-12 nopadding">
                <div class="box">
                    <h2 class="box-title" style="margin-left: 10px;">Les fichiers attach&eacute;s</h2>
                    <div class="box-body" style="padding: 0 20px 20px 20px;">
                        <table class="table table-striped table-bordered table-condensed tree" style="color: #4e4e4e;">
                            <thead>
                            <tr>
                                <th class='contenuetable'></th>
                                <th class='contenuetable'>Libell&eacute;</th>
                                <th class='contenuetable'>Fichier</th>
                                <th class='contenuetable'>Date d'upload</th>
                                <th class='contenuetable'>T&eacute;l&eacute;charger</th>
                            </tr>
                            </thead>
                            <tbody>
                            <%if (fichiers == null || fichiers.length == 0) { %>
                            <tr>
                                <td colspan="5" style="text-align: center;"><strong>Aucun fichier</strong></td>
                            </tr>
                            <%} else {
                                for (AttacherFichier fichier : fichiers) {%>
                            <tr class="treegrid-1 treegrid-expanded">
                                <td><span class="treegrid-expander glyphicon glyphicon-minus"></span></td>
                                <td><%=fichier.getChemin()%></td>
                                <td><%=Utilitaire.champNull(fichier.getLibelle())%></td>
                                <td><%=fichier.getDaty()%></td>
                                <td>
                                    <a href="../FileManager2?parent=<%= "/" + dossier + "/" + fichier.getChemin() %>" class="btn btn-success">T&eacute;l&eacute;charger</a>
                                </td>
                            </tr>
                            <%}
                            }%>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>                    
</div>
<%
	} catch (Exception e) {
		e.printStackTrace();
%>
    <script language="JavaScript">
        alert('<%=e.getMessage()%>');
        history.back();
    </script>
<% }%>
