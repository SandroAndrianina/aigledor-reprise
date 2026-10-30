<%--
    Document   : etatcaisse-liste
    Created on : 2 avr. 2024, 10:11:22
    Author     : 26134
--%>


<%@page import="produits.CategorieIngredient"%>
<%@page import="utils.ConstanteStation"%>
<%@page import="stock.PageRechercheEtatStock"%>
<%@page import="stock.EtatStock"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="affichage.PageRecherche"%>
<%@page import="magasin.Magasin"%>
<%@page import="annexe.TypeProduit"%>
<%@page import="annexe.Unite"%>
<%@page import="affichage.Liste"%>
<%@page import="user.UserEJB"%>
<%@ page import="utilitaire.Utilitaire" %>
<%@ page import="bean.ClassMAPTable" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="bean.TypeObjet" %>
<%@ page import="couleur.Couleur" %>

<% try{
    UserEJB u = (UserEJB) session.getValue("u");
    Magasin mag = u.getMagasinEncours();
    EtatStock t = new EtatStock();
    t.setNomTable("V_ETATSTOCK_ING");
    String listeCrt[] = {"idProduitLib","idModele","idModeleLib","idCategorieModele","idCategorieModeleLib","idSousCategorieModele","idSousCategorieModeleLib","idPointureLib","nomCouleur","idMagasinLib","idTypeProduitLib", "daty"};
    String listeInt[] = {"daty","idPointureLib"};
    String libEntete[] = {"idProduit","idProduitLib","idModeleLib","idCategorieModeleLib","idSousCategorieModeleLib","idPointureLib","nomCouleur","idMagasinLib","idUniteLib","pu","invdaty","invquantite","entree","sortie","reste"};
    PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3, libEntete, libEntete.length);
    pr.setTitre("&Eacute;tat des Stocks");
    pr.setUtilisateur(u);
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("stock/etatstock/etatstock-liste.jsp");

    // Initialisation Liste
    Liste[] dropDowns = new Liste[4];
    Magasin m = new Magasin();
    m.setNomTable("magasinpoint");
    dropDowns[0] = new Liste("idMagasinLib", m, "val", "val");

    TypeObjet cat = new TypeObjet();
    cat.setNomTable("CATEGORIEMODELE");
    dropDowns[1] = new Liste("idCategorieModele",cat, "val","id");

    TypeObjet sousCat = new TypeObjet();
    sousCat.setNomTable("SOUSCATEGORIEMODELE");
    dropDowns[2] = new Liste("idSousCategorieModele",sousCat, "val","id");


    Couleur c = new Couleur();
    c.setNomTable("V_REFCOULEUR_AVECCODE");
    dropDowns[3] = new Liste("nomCouleur",c, "nomCouleur","nomCouleur");
    dropDowns[3].setEstMultiple(true);

    pr.getFormu().changerEnChamp(dropDowns);

    pr.getFormu().getChamp("nomCouleur").setLibelle("Couleur");

    pr.getFormu().getChamp("idCategorieModeleLib").setVisible(false);
    pr.getFormu().getChamp("idCategorieModele").setLibelle("Cat&eacute;gorie de mod&eacute;le");

    pr.getFormu().getChamp("idSousCategorieModeleLib").setVisible(false);
    pr.getFormu().getChamp("idSousCategorieModele").setLibelle(" Sous-cat&eacute;gorie de mod&eacute;le");

    pr.getFormu().getChamp("idModeleLib").setLibelle("mod&egrave;le");

//    pr.getFormu().getChamp("idModele").setPageAppelComplete("bean.TypeObjet","id","V_MODELE_TYPE_OBJET");

    pr.getFormu().getChamp("idModele").setVisible(false);

    pr.getFormu().getChamp("idPointureLib1").setLibelle("Pointure min");
    pr.getFormu().getChamp("idPointureLib2").setLibelle("Pointure max");

    pr.getFormu().getChamp("daty1").setDefaut("01/01/2001");
    pr.getFormu().getChamp("daty1").setVisible(false);
    pr.getFormu().getChamp("daty1").setLibelle("-");
    pr.getFormu().getChamp("daty2").setLibelle("Date");
    pr.getFormu().getChamp("daty2").setEstMoitier(true);
    pr.getFormu().getChamp("daty2").setDefaut(Utilitaire.dateDuJour());
    pr.getFormu().getChamp("idProduitLib").setLibelle("Produit");
    pr.getFormu().getChamp("idProduitLib").setEstMoitier(true);
    pr.getFormu().getChamp("idTypeProduitLib").setVisible(false);
    pr.getFormu().getChamp("idTypeProduitLib").setLibelle("Cat&eacute;gorie");
    pr.getFormu().getChamp("idMagasinLib").setLibelle("Magasin");
    if(mag != null){
        pr.getFormu().getChamp("idMagasinLib").setDefaut(mag.getVal());
    }
    //pr.getFormu().getChamp("idUnite").setLibelle("Unite");
    //pr.getFormu().getChamp("puVente1").setLibelle("Prix de vente minimum");
    //pr.getFormu().getChamp("puVente2").setLibelle("Prix de vente maximum");
    String[] colSomme = {"invquantite","entree","sortie","reste","montantReste" };
    pr.creerObjetPage(libEntete, colSomme);
    String[] ordreForm = {"daty1","daty2", "idProduitLib", "idModeleLib", "idCategorieModele", "idSousCategorieModele"};
    pr.getFormu().setOrdre(ordreForm);
    //Definition des lienTableau et des colonnes de lien
    String lienFiltre = pr.getLien() + "?but=stock/mvtstockfille-liste.jsp";

    String lienTableau[] = {pr.getLien() +"?but=produits/as-ingredients-fiche.jsp",lienFiltre, lienFiltre};
    String colonneLien[] = {"idProduit","entree", "sortie"};
    String attLien[] = {"id","entree", "sortie"};
//    String[] attributLien = {"id", "", ""};

    String[] urllien = new String[] {"idProduit","idMagasin-idProduit-invdaty-idProduit","idMagasin-idProduit-invdaty-idProduit"};
    String[] urllienAffiche = new String[] {"idProduit","idMagasin-idProduit-invdaty-clicStock","idMagasin-idProduit-invdaty-clicStock"};

    pr.getTableau().setLien(lienTableau);
    pr.getTableau().setColonneLien(colonneLien);
    pr.getTableau().setAttLien(attLien);
    pr.getTableau().setUrlLien(urllien);
    pr.getTableau().setUrlLienAffiche(urllienAffiche);

    String libEnteteAffiche[] = {"ID Produit","Produit","Mod&egrave;le", "Cat&eacute;gorie Mod&egrave;le", "Sous-cat&eacute;gorie Mod&egrave;le","Pointure","Couleur","Magasin","Unit&eacute;","Prix unitaire","date inventaire","Quantit&eacute; inventaire","entr&eacute;e","sortie","reste","montant restant"};
    pr.getTableau().setLibelleAffiche(libEnteteAffiche);
    String enteteRecap[] = {"","Nombre","Inventaire","Somme des entr&eacute;es","Somme des sorties","Somme des restes","Somme des montants restants"};
    pr.getTableauRecap().setLibeEntete(enteteRecap);
%>


<div class="content-wrapper">
    <section class="content-header">
        <h1><%= pr.getTitre() %></h1>
    </section>
    <section class="content">
        <form action="<%=pr.getLien()%>?but=<%= pr.getApres() %>" method="post">
            <%
                out.println(pr.getFormu().getHtmlEnsemble());
            %>
        </form>
        <%
            out.println(pr.getTableauRecap().getHtml());%>
        <br>
        <%
            out.println(pr.getTableau().getHtml());
            out.println(pr.getBasPage());
        %>
    </section>
</div>
    <%
    }catch(Exception e){

        e.printStackTrace();
    }
%>



