<%--
  Created by IntelliJ IDEA.
  User: safidy
  Date: 05/08/2025
  Time: 14:49
  To change this template use File | Settings | File Templates.
--%>

<%@page import="java.util.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="proforma.*"%>
<%@page import="user.*"%>
<%@page import="vente.*"%>
<%@page import="bean.*" %>
<%@page import="affichage.*"%>
<%@page import="utilitaire.*"%>
<%@ page import="fichier.AttacherFichier" %>
<%@ page import="uploadbean.UploadService" %>

<%
  UserEJB u = (user.UserEJB)session.getValue("u");
  String lien = (String) session.getValue("lien");
  String pageActuel = "vente/proforma/proforma-fiche.jsp";
%>
<%
  try{
    String pageModif = "vente/proforma/proforma-saisie.jsp";
    ProformaLib f = new ProformaLib();
    f.setNomTable("PROFORMA_CPL");
    PageConsulte pc = new PageConsulte(f, request, u);
    pc.setTitre("Fiche de proforma");
    ProformaLib prof=(ProformaLib)pc.getBase();
    String id=prof.getTuppleID();
    pc.getChampByName("daty").setLibelle("date");
    pc.getChampByName("idclient").setLibelle("ID client");
    pc.getChampByName("idclient").setVisible(false);
    pc.getChampByName("idclientLib").setLibelle("Client");
    pc.getChampByName("remarque").setLibelle("Remarque");
    pc.getChampByName("etatlib").setLibelle("&Eacute;tat");
    pc.getChampByName("montantremise").setLibelle("Montant remise");
    pc.getChampByName("montantristourne").setLibelle("Montant Ristourne");
    pc.getChampByName("iddevise").setLibelle("Devise");
    pc.getChampByName("iddevise").setVisible(false);
    pc.getChampByName("montant").setLibelle("Montant HT sans remise ("+prof.getIdDevise()+")");
    pc.getChampByName("montanttotal").setLibelle("Montant HT avec remise ("+prof.getIdDevise()+")");
    pc.getChampByName("montanttva").setLibelle("Montant TVA ("+prof.getIdDevise()+")");
    pc.getChampByName("montantttc").setLibelle("Montant TTC ("+prof.getIdDevise()+")");
    pc.getChampByName("montantreste").setVisible(false);
    pc.getChampByName("montantpaye").setVisible(false);
    pc.getChampByName("remise").setVisible(false);
    pc.getChampByName("idMagasinLib").setLibelle("Magasin");
    pc.getChampByName("designation").setLibelle("D&eacute;signation");
    pc.getChampByName("fraislivraison").setLibelle("Frais de Livraison(Par Kg)");
    pc.getChampByName("fraislivraison").setVisible(false);
    pc.getChampByName("fraistransport").setLibelle("Transport total");
    pc.getChampByName("fraistransport").setVisible(false);
    pc.getChampByName("codeclient").setVisible(false);
    pc.getChampByName("numeroProforma").setLibelle("Num&eacute;ro proforma");
    pc.getChampByName("idCommande").setLibelle("Ref. commande");
    pc.getChampByName("modeLivraisonLib").setLibelle("Mode de livraison");
    pc.getChampByName("lieuLivraison").setLibelle("Lieu de livraison");
    pc.getChampByName("datelivraison").setLibelle("Date de livraison/R&eacute;cup&eacute;ration");
    pc.getChampByName("idorigine").setVisible(false);
    pc.getChampByName("etat").setVisible(false);
    pc.getChampByName("montantTtcAr").setLibelle("Montant TTC (AR)");
    if(prof.getIdDevise().compareToIgnoreCase("AR")==0) {
      pc.getChampByName("montantTtcAr").setVisible(false);
    }
    pc.getChampByName("montantrevient").setVisible(false);
    pc.getChampByName("margebrute").setVisible(false);
    pc.getChampByName("idReservation").setVisible(false);
    pc.getChampByName("tauxdechange").setVisible(false);
    //pc.getChampByName("idorigine").setVisible(false);
    pc.getChampByName("idMagasin").setVisible(false);
    pc.getChampByName("adresse").setVisible(false);
    pc.getChampByName("contact").setVisible(false);
    //pc.getChampByName("montant").setVisible(false);
    pc.getChampByName("avoir").setVisible(false);
    pc.getChampByName("reference").setLibelle("R&eacute;f&eacute;rence");

//    pc.getChampByName("idclient").setLien(lien+"?but=client/client-fiche.jsp", "id=");
    pc.getChampByName("idCommande").setLien(lien+"?but=commande/commande-fiche.jsp", "id=");
    pc.getChampByName("idClientLib").setLien((String) session.getValue("lien") + "?but=client/client-fiche.jsp&id=" + prof.getIdClient(), "nom=");

    if(pc.getChampByName("idorigine").getValeur().startsWith("PROF")){
      pc.getChampByName("idorigine").setLien(lien+"?but=vente/proforma/proforma-fiche.jsp", "id=");
    }
    String[] ordre = {
            "daty",
            "numeroProforma",
            "designation",
            "idclientLib",
            "idCommande",
            "reference",
            "idMagasinLib",
            "modeLivraisonLib",
            "datelivraison",
            "lieuLivraison",
            "montant",
            "montanttotal",
            "montantremise",
            "montantristourne",
            "montanttva",
            "montantttc",
            "montantTtcAr",
            "etatlib",
            "remarque"
    };
    pc.setOrdre(ordre);

    String classe = "proforma.Proforma";

    Map<String, String> map = new HashMap<String, String>();
    map.put("proforma-detail-liste", "");
    map.put("proforma-lie", "");
    map.put("bondecommande", "");
    map.put("facture", "");
    map.put("../../../historique/inc/historique-liste", "");


    String tab = request.getParameter("tab");
    if (tab == null) {
      tab = "proforma-detail-liste";
    }
    map.put(tab, "active");
    tab = "inc/" + tab + ".jsp";
    int etat = prof.getEtat();

    AttacherFichier[] fichiers = UploadService.getUploadFile(request.getParameter("id"));
    String projectName = pc.getChampByName("designation").getValeur()
            .replace("'","_")
            .replace("/","_")
            .replace("-","_")
            .replace(":", "_")
            .replace("*", "_")
            .replace(" ", "_");
    String dossier = "proforma/files/" + projectName;

    LinkedHashMap<String, String[]> rubrique = new LinkedHashMap<>();
    rubrique.put("Information g&eacute;n&eacute;rale", new String[]{"daty",
            "numeroProforma",
            "designation",
            "idclientLib",
            "idCommande",
            "reference",
            "idMagasinLib",
            "modeLivraisonLib",
            "datelivraison",
            "lieuLivraison",});
    rubrique.put("Financi&egrave;re", new String[]{
            "montant",
            "montanttotal",
            "montantremise",
            "montantristourne",
            "montanttva",
            "montantttc",
            "montantTtcAr",});
    pc.setRubrique(rubrique);

%>

<div class="content-wrapper">
<h1 class="box-title"><a href=<%= lien + "?but=vente/demandeprix/demandeprix-fiche.jsp"%>> <i class="fa fa-angle-left"></i></a><%=pc.getTitre()%></h1>
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
       
              <%if( etat < 11 && etat>0){ %>
              <a class="btn btn-primary pull-right" href="<%= (String) session.getValue("lien") + "?but=apresTarif.jsp&acte=valider&id=" + request.getParameter("id") + "&bute=vente/proforma/proforma-fiche.jsp&classe="+classe %> " style="margin-right: 10px">Valider</a>
              <a class="btn btn-secondary pull-right"  href="<%= lien + "?but="+ pageModif +"&id=" + id +"&acte=update"%>" style="margin-right: 10px">Modifier</a>
              <a  class="btn btn-danger pull-left"  href="<%= lien + "?but=apresTarif.jsp&id=" + id+"&acte=annuler&bute=vente/proforma/proforma-fiche.jsp&classe="+classe %>">Annuler</a>
              <%}%>
              <%if( etat == 11){ %>
              <!--a class="btn btn-primary pull-right" href="<%= (String) session.getValue("lien") + "?but=apresTarif.jsp&acte=valider&id=" + request.getParameter("id") + "&bute=vente/proforma/proforma-fiche.jsp&classe=" + classe %> " style="margin-right: 10px">Cl&ocirc;turer</a-->
              <%}%>
              <%if( etat == 11){ %>
                <a class="btn btn-secondary pull-right" href="<%= (String) session.getValue("lien") + "?but=vente/bondecommande/bondecommande-saisie.jsp&idProforma="+id%> " style="margin-right: 10px">Cr&eacute;er un bon de commande</a>
              <%}%>
<%--                    <a class="btn btn-tertiary pull-right"  href="${pageContext.request.contextPath}/ExportPDFARBIOCHEM?action=proforma&id=<%=request.getParameter("id")%>" style="margin-right: 10px">Imprimer en PDF</a>--%>
              <a class="btn btn-tertiary pull-right" href="<%= (String) session.getValue("lien") + "?but=pageupload.jsp&id=" + request.getParameter("id") + "&dossier=" + dossier + "&nomtable=ATTACHER_FICHIER&procedure=GETSEQ_ATTACHER_FICHIER&bute=" + pageActuel + "&id=" + request.getParameter("id") + "&nomprj=" + pc.getChampByName("designation").getValeur() %>" style="margin-right: 10px;">Attacher Fichier</a>
              <%
                        // inline=1 -> ExportPDF sert le PDF en Content-Disposition: inline
                        // (+ application/pdf), ce qui permet le rendu dans l'iframe du modal.
                        // Sans le parametre : disposition attachment, comportement d'origine.
                        String pdfBase   = request.getContextPath() + "/ExportPDFARBIOCHEM?action=proforma&id="
                                         + URLEncoder.encode(id, "UTF-8");
                        String pdfApercu = pdfBase + "&inline=1";
                    %>
                    <button type="button" class="btn btn-tertiary pull-right"
                            data-url="<%= pdfApercu %>"
                            data-dl="<%= pdfBase %>"
                            data-kind="pdf"
                            data-name="Proforma <%= id %>"
                            onclick="openPreview(this.dataset.url, this.dataset.kind, this.dataset.name, this.dataset.dl)"
                            style="margin-right: 10px">Imprimer en PDF</button>
              <!--a href="<%=(String) session.getValue("lien")%>/../../ExportProforma?id=<%=request.getParameter("id")%>" class="btn btn-primary">Export Excel</a-->
            </div>
            <br/>

          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="row m-0">
    <div class="col-md-12 ">
      <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
          <!-- a modifier -->
          <li class="<%=map.get("proforma-detail-liste")%>"><a href="<%= lien%>?but=<%= pageActuel%>&id=<%= id%>&tab=proforma-detail-liste">D&eacute;tails</a></li>
          <!--li class="<%=map.get("proforma-lie")%>"><a href="<%= lien%>?but=<%= pageActuel%>&id=<%= id%>&tab=proforma-lie">Proforma Li&eacute;</a></li>-->
          <li class="<%=map.get("bondecommande")%>"><a href="<%= lien%>?but=<%= pageActuel%>&id=<%= id%>&tab=bondecommande">Liste des Bons de commande</a></li>
          <li class="<%=map.get("../../../historique/inc/historique-liste")%>"><a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=../../../historique/inc/historique-liste">Historique</a></li>
        </ul>
        <div class="tab-content">
          <jsp:include page="<%= tab %>" >
            <jsp:param name="id" value="<%= id %>" />
          </jsp:include>
        </div>
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

  }%>

