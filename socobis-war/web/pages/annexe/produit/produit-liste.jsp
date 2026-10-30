
<%@page import="affichage.Liste"%>
<%@page import="bean.TypeObjet"%>
<%@page import="produits.Ingredients"%>
<%@page import="affichage.PageRecherche"%>
<%@ page import="produits.IngredientsLib" %>

<%
    try
    {
        IngredientsLib lv = new IngredientsLib();
        lv.setNomTable("as_ingredients_lib");

        String listeCrt[] = {"id","libelle", "typeProduitLib","idModeleLib", "libelle","marquelib", "couleur","codeBarre","reference"};
        String listeInt[] = null;
        String libEntete[] = {"id", "libelle", "reference", "codeBarre","typeProduitLib", "idModeleLib", "marquelib","couleur" };


        PageRecherche pr = new PageRecherche(lv, request, listeCrt, listeInt, 3, libEntete, libEntete.length);
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setLien((String) session.getValue("lien"));
//        pr.getFormu().getChamp("unite").setLibelle("D&eacute;signation");
        pr.getFormu().getChamp("libelle").setLibelle( "unit&eacute;");
//        pr.getFormu().getChamp("typeProduit").setLibelle("Cat&eacute;gorie");
        pr.setNpp(20);


        affichage.Champ[] liste = new affichage.Champ[1];

//        TypeObjet ou = new TypeObjet();
//        ou.setNomTable("as_unite");
//        liste[0] = new Liste("unite", ou, "VAL", "VAL");

        TypeObjet ob = new TypeObjet();
        ob.setNomTable("TYPE_PRODUIT");
        liste[0] = new Liste("typeProduitLib", ob, "val", "val");

        pr.getFormu().changerEnChamp(liste);
        pr.getFormu().getChamp("typeProduitLib").setLibelle("Type produit");
        pr.getFormu().getChamp("idModeleLib").setLibelle("Mod&egrave;le");
        pr.getFormu().getChamp("marquelib").setLibelle("Pointure / Taille");
        pr.getFormu().getChamp("couleur").setLibelle("Couleur");
        pr.getFormu().getChamp("reference").setLibelle("R&eacute;f&eacute;rence");
        pr.getFormu().getChamp("codeBarre").setLibelle("Code barre");
        pr.getFormu().getChamp("libelle").setLibelle("D&eacute;signation");



        pr.setApres("annexe/produit/produit-liste.jsp");
        String[] colSomme = null;
        pr.creerObjetPage(libEntete, colSomme);

        pr.getFormu().setAnotherButton("" +
                "<a class=\"btn btn-primary pull-right btn-small\" href=\"module.jsp?but=produits/as-ingredients-saisie.jsp&currentMenu=MENUDYN0304008\">\n" +
                "                    <i class=\"material-symbols-rounded\">add</i>Saisie d'un produit</a>"
        );
        String libAff[] = {"ID", "D&eacute;signation", "R&eacute;f&eacute;rence", "Code barre","Type produit","Mod&egrave;le", "Pointure / Taille", "couleur" };
        pr.getTableau().setLibelleAffiche(libAff);
%>
<script>
    function changerDesignation() {
        document.incident.submit();
    }
</script>
<div class="content-wrapper">
    <section class="content-header">
        <h1>Liste ingredients</h1>
    </section>
    <section class="content">
        <form action="<%=pr.getLien()%>?but=annexe/produit/produit-liste.jsp" method="post" name="incident" id="incident">
            <%
                out.println(pr.getFormu().getHtmlEnsemble());
            %>

        </form>
        <%  String lienTableau[] = {pr.getLien() + "?but=produits/as-ingredients-fiche.jsp"};
            String colonneLien[] = {"id"};
            pr.getTableau().setLien(lienTableau);
            pr.getTableau().setColonneLien(colonneLien);
            out.println(pr.getTableauRecap().getHtml());%>
        <br>
        <%

            out.println(pr.getTableau().getHtml());
            out.println(pr.getBasPage());

        %>
    </section>
</div>
<%
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
%>