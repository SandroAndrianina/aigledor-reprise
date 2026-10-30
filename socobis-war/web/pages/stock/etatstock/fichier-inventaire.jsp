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
    String listeCrt[] = {"idProduitLib","idModele","idCategorieModeleLib","idSousCategorieModeleLib","idPointureLib","referencecouleur","idMagasin", "daty"};
    String listeInt[] = {"daty","idPointureLib"};
    String libEntete[] = {"idProduit","reste","pu"};
    PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3, libEntete, libEntete.length);
    if(mag != null){
        pr.setAWhere("AND IDMAGASIN='"+mag.getId()+"'");
    }
    pr.setTitre("Fichier d'inventaire par entr&eacute;e");
    pr.setUtilisateur(u);
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("stock/etatstock/fichier-inventaire.jsp");

    Liste[] dropDowns = new Liste[4];
    Magasin m = new Magasin();
    m.setNomTable("magasinpoint");
    dropDowns[0] = new Liste("idMagasin", m, "val", "id");

    TypeObjet cat = new TypeObjet();
    cat.setNomTable("CATEGORIEMODELE");
    dropDowns[1] = new Liste("idCategorieModelelib",cat, "val","val");

    TypeObjet sousCat = new TypeObjet();
    sousCat.setNomTable("SOUSCATEGORIEMODELE");
    dropDowns[2] = new Liste("idSousCategorieModeleLib",sousCat, "val","val");


    Couleur c = new Couleur();
    c.setNomTable("V_REFERENCE_COULEUR");
    dropDowns[3] = new Liste("referenceCouleur",c, "nomCouleur","referenceCouleur");
    dropDowns[3].setEstMultiple(true);

    pr.getFormu().changerEnChamp(dropDowns);

    pr.getFormu().changerEnChamp(dropDowns);
    pr.getFormu().getChamp("daty1").setDefaut("01/01/2001");
    pr.getFormu().getChamp("daty1").setVisible(false);
    pr.getFormu().getChamp("daty1").setLibelle("-");
    pr.getFormu().getChamp("daty2").setLibelle("Date");
    pr.getFormu().getChamp("daty2").setDefaut(Utilitaire.dateDuJour());
    pr.getFormu().getChamp("idProduitLib").setLibelle("Produit");
    //pr.getFormu().getChamp("idTypeProduitLib").setLibelle("Cat&eacute;gorie");
    pr.getFormu().getChamp("idMagasin").setLibelle("Magasin");
    if(mag != null){
        pr.getFormu().getChamp("idMagasin").setAutre("disabled");
        pr.getFormu().getChamp("idMagasin").setDefaut(mag.getId());
    }
    pr.getFormu().getChamp("idPointureLib1").setLibelle("Pointure min");
    pr.getFormu().getChamp("idPointureLib2").setLibelle("Pointure max");
    pr.getFormu().getChamp("idSousCategorieModeleLib").setLibelle("Sous Cat&eacute;gorie Mod&egrave;le");
    pr.getFormu().getChamp("idCategorieModeleLib").setLibelle("Cat&eacute;gorie Mod&egrave;le");
    pr.getFormu().getChamp("referenceCouleur").setLibelle("Couleur");
    pr.getFormu().getChamp("idModele").setPageAppelComplete("bean.TypeObjet","id","V_MODELE_TYPE_OBJET");
    pr.getFormu().getChamp("idModele").setLibelle("Mod&eacute;le");
    String[] colSomme = {"reste"};
    pr.creerObjetPage(libEntete, colSomme);

    String lienTableau[] = {pr.getLien() +"?but=produits/as-ingredients-fiche.jsp"};
    String colonneLien[] = {"idProduit"};
    String varColonneLien[] = {"id"};

    String[] urllien = new String[] {"idProduit","idMagasin-idProduit-invdaty-idProduit","idMagasin-idProduit-invdaty-idProduit"};
    String[] urllienAffiche = new String[] {"idProduit","idMagasin-idProduit-invdaty-clicStock","idMagasin-idProduit-invdaty-clicStock"};

    pr.getTableau().setLien(lienTableau);
    pr.getTableau().setColonneLien(colonneLien);
    pr.getTableau().setUrlLien(urllien);
    pr.getTableau().setUrlLienAffiche(urllienAffiche);
    pr.getTableau().setAttLien(varColonneLien);

    String libEnteteAffiche[] = {"idProduit","quantite","pu"};
    pr.getTableau().setLibelleAffiche(libEnteteAffiche);
    String enteteRecap[] = {"","Nombre","Somme des quantit&eacute;s"};
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



