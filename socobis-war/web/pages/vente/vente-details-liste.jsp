<%--
    Document   : vente-liste
    Created on : 25 mars 2024, 09:57:03
    Author     : Angela
--%>

<%@page import="vente.VenteLib"%>
<%@page import="vente.Vente"%>
<%@page import="utilitaire.Utilitaire"%>
<%@page import="affichage.PageRecherche"%>
<%@ page import="java.time.LocalDate" %>
<%@ page import="static java.time.DayOfWeek.MONDAY" %>
<%@ page import="static java.time.temporal.TemporalAdjusters.previousOrSame" %>
<%@ page import="static java.time.DayOfWeek.SUNDAY" %>
<%@ page import="static java.time.temporal.TemporalAdjusters.nextOrSame" %>
<%@ page import="java.sql.Date" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="vente.Vente" %>
<%@ page import="affichage.Liste" %>
<%@ page import="bean.TypeObjet" %>
<%@ page import="vente.EtatVenteDetailsLib" %>
<%@ page import="utils.ConstanteAsync" %>
<%@ page import="java.time.ZoneId" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalTime" %>

<% try{
  String idMagasinLib  = request.getParameter("idMagasinLib");
  String idCategorieLib  = request.getParameter("idCategorieLib");
  String idPointLib  = request.getParameter("idPointLib");
  String typeProduitLib  = request.getParameter("typeProduitLib");
  String idprovincelib  = request.getParameter("idprovincelib");

  String currentPoint = session.getAttribute("idPoint").toString();
  System.out.println(" current oint ++++++ " + currentPoint);
  EtatVenteDetailsLib bc = new EtatVenteDetailsLib();
  bc.setNomTable("ETAT_VENTE_DETAILS_POINT");

  LocalDate today = LocalDate.now();
  LocalDate monday = today.with(previousOrSame(MONDAY));
  LocalDate sunday = today.with(nextOrSame(SUNDAY));

  String mondayStr = Utilitaire.datetostring(Date.valueOf(monday));


  String[] listeCrt = {
          "id","idVente", "idMagasin",
          "idProduit", "idProduitLib","idClient","daty", "idCategorieLib", "typeProduitLib", "heureSaisie"};
  String[] listeInt = {"daty", "heureSaisie"};
  String[] libEntete = {
          "id","idVente","idMagasinLib",
          "idProduit", "idProduitLib", "qte", "montantTTC",  "idCategorieLib", "typeProduitLib", "heureSaisie"};
  String[] libEnteteAffiche = {
          "Ref.", "ID Vente", "Point",
          "ID Produit", "Produit", "Quantit&eacute;","Montant", "Cat&eacute;gorie", "Type", "Heure de saisie"};

  PageRecherche pr = new PageRecherche(bc, request, listeCrt, listeInt, 3, libEntete, libEntete.length);
  pr.setAWhere(pr.getAWhere()+" and etat>=11");


  pr.setUtilisateur((user.UserEJB) session.getValue("u"));
  pr.setLien((String) session.getValue("lien"));
  pr.setApres("vente/vente-details-liste.jsp");
  pr.setTitre("Liste des ventes d&eacute;tails");
  //pr.setOrdre(" order by daty desc");
  String[] colSomme = { "qte", "montantTTC"};

  pr.getFormu().getChamp("idMagasin").setLibelle("Magasin");
  if (idMagasinLib != null)
    pr.getFormu().getChamp("idMagasin").setDefaut(idMagasinLib);

  pr.getFormu().getChamp("idCategorieLib").setLibelle("Cat&eacute;gorie");
  if (idCategorieLib != null)
    pr.getFormu().getChamp("idCategorieLib").setDefaut(idCategorieLib);


  pr.getFormu().getChamp("typeProduitLib").setLibelle("Produit");
  if (typeProduitLib != null)
    pr.getFormu().getChamp("typeProduitLib").setDefaut(typeProduitLib);

  Liste[] listes = new Liste[1];
  TypeObjet idMagasin = new TypeObjet();
  idMagasin.setNomTable("POINT");
  listes[0] = new Liste("idMagasin", idMagasin, "val", "id");
  if(currentPoint.compareToIgnoreCase(ConstanteAsync.SIEGE)!=0) {
    listes[0].setDefaut(currentPoint);
  }

  pr.getFormu().changerEnChamp(listes);


  pr.getFormu().getChamp("id").setLibelle("Ref.");
  pr.getFormu().getChamp("idVente").setLibelle("ID Vente");
  pr.getFormu().getChamp("idMagasin").setLibelle("Point");
  pr.getFormu().getChamp("idProduit").setLibelle("ID Produit");
  pr.getFormu().getChamp("idClient").setLibelle("Client");
  pr.getFormu().getChamp("idClient").setPageAppelComplete("client.Client", "id", "CLIENT");
  pr.getFormu().getChamp("idProduitLib").setLibelle("Produits");
  pr.getFormu().getChamp("daty1").setLibelle("Date Min");
  String date = utilitaire.Utilitaire.formatterDaty(utilitaire.Utilitaire.ajoutJourDate(utilitaire.Utilitaire.dateDuJour(), -1));
  pr.getFormu().getChamp("daty1").setDefaut(date);
  pr.getFormu().getChamp("daty2").setLibelle("Date Max");
  pr.getFormu().getChamp("daty2").setDefaut(utilitaire.Utilitaire.dateDuJour());
  pr.getFormu().getChamp("heureSaisie1").setLibelle("Heure de saisie Min");
  pr.getFormu().getChamp("heureSaisie2").setLibelle("Heure de saisie Max");
  pr.getFormu().getChamp("heureSaisie1").setType("time");
  pr.getFormu().getChamp("heureSaisie2").setType("time");

//  TypeObjet prov = new TypeObjet();
//  prov.setNomTable("province");

//  Liste[] liste = new Liste[1];
//  liste[0] = new Liste("idProvince",prov,"val","id");
//  liste[0].setLibelle("Province");
  //pr.getFormu().getChamp("provincelib").setLibelle("Province");
//  pr.getFormu().changerEnChamp(liste);
  pr.creerObjetPage(libEntete, colSomme);

  //Definition des lienTableau et des colonnes de lien
  String[] lienTableau = {pr.getLien() + "?but=vente/vente-fiche.jsp"};
  String[] colonneLien = {"idVente"};
  pr.getTableau().setLien(lienTableau);
  pr.getTableau().setColonneLien(colonneLien);
  pr.getTableau().setLibelleAffiche(libEnteteAffiche);


  String[] enteteRecap = {"","Nombres", "Somme des Quantit&eacute;" , "Somme des Montant"};
  pr.getTableauRecap().setLibeEntete(enteteRecap);
%>
<div class="content-wrapper">
  <section class="content-header">
    <h1><%= pr.getTitre() %></h1>
  </section>
  <section class="content">
    <form action="<%=pr.getLien()%>?but=<%= pr.getApres() %>" method="post" name="vente" id="vente">
      <%
        out.println(pr.getFormu().getHtmlEnsemble());
      %>
    </form>
    <%
      out.println(pr.getTableauRecap().getHtml());%>
    <br>

    <%
      out.println(pr.getTableau().getHtml());
    %>
    <%
      out.println(pr.getBasPage());
    %>
  </section>
</div>
<%
  }catch(Exception e){

    e.printStackTrace();
  }
%>




