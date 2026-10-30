<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="user.UserEJB" %>
<%@ page import="affichage.PageConsulte" %>
<%@ page import="modele.ModeleLib" %>

<% try{ 
    UserEJB u = (user.UserEJB)session.getValue("u");
    String lien = (String) session.getValue("lien");

    ModeleLib o = new ModeleLib();
    o.setNomTable("V_MODELE_LIB");
    PageConsulte pc = new PageConsulte(o, request, u);
    pc.setTitre("modele-fiche");
    String id = pc.getBase().getTuppleID();

    pc.getChampByName("idCategorieLib").setLibelle("Categorie");
    pc.getChampByName("idSousCategorieLib").setLibelle("Sous categorie");
    pc.getChampByName("universLib").setLibelle("Univers");
    pc.getChampByName("gammeLib").setLibelle("Gamme");
    pc.getChampByName("statutLib").setLibelle("Statut");
    pc.getChampByName("matiereLib").setLibelle("Matiere");
    pc.getChampByName("serieLib").setLibelle("Serie");
    pc.getChampByName("collectionLib").setLibelle("Collection");
    pc.getChampByName("typologieLib").setLibelle("Typologie");
    pc.getChampByName("provenanceLib").setLibelle("Provenance");
    pc.getChampByName("nomCouleur").setLibelle("Couleur");
    pc.getChampByName("semelleLib").setLibelle("Semelle");
    pc.getChampByName("libelle").setLibelle("Libelle");
    pc.getChampByName("idCouleur").setVisible(false);
    pc.getChampByName("codeCouleur").setVisible(false);
    pc.getChampByName("id").setVisible(false);
    pc.getChampByName("idcategorie").setVisible(false);
    pc.getChampByName("idsouscategorie").setVisible(false);
    pc.getChampByName("univers").setVisible(false);
    pc.getChampByName("gamme").setVisible(false);
    pc.getChampByName("statut").setVisible(false);
    pc.getChampByName("matiere").setVisible(false);
    pc.getChampByName("serie").setVisible(false);
    pc.getChampByName("collection").setVisible(false);
    pc.getChampByName("typologie").setVisible(false);
    pc.getChampByName("provenance").setVisible(false);
    pc.getChampByName("codecouleur").setVisible(false);
    pc.getChampByName("semelle").setVisible(false);

    String[] ordre = {"idCategorieLib","idSousCategorieLib","universLib","gammeLib","statutLib","matiereLib","serieLib","collectionLib","typologieLib","provenanceLib","nomCouleur","semelleLib","libelle"};
    pc.setOrdre(ordre);

    String pageRetour = ".jsp";
    String pageModif = "modele/modele-saisie.jsp&acte=update";
    String pageApresDelete = "modele/modele-liste.jsp";
    String classe = "modele.ModeleLib";

%>

<div class="content-wrapper">

<h1 class="box-title"><a href=<%= lien + "?but=" + pageRetour%>> <i class="fa fa-angle-left"></i></a><%=pc.getTitre()%></h1>

<div class="row m-0">
    <div class="col-md-3"></div>
    <div class="col-md-6">
        <div class="box-fiche">
            <div class="box">
                <div class="box-body">
                    <%
                        out.println(pc.getHtml());
                    %>
                    <br/>
                    <div class="box-footer">
                        <a class="btn btn-warning pull-right"  href="<%= lien + "?but="+ pageModif +"&id=" + id %>" style="margin-right: 10px">Modifier</a>
                        <a  class="pull-right" href="<%= lien + "?but=apresTarif.jsp&id=" + id+"&acte=delete&bute="+pageApresDelete+"&classe="+classe %>"><button class="btn btn-danger">Supprimer</button></a>
                    </div>
                    <br/>
                </div>
            </div>
        </div>
    </div>
</div>

<% } catch (Exception e) {
  e.printStackTrace();
%>
<script language="JavaScript"> alert('<%=e.getMessage()%>');
    history.back();
</script>
<% }%>

