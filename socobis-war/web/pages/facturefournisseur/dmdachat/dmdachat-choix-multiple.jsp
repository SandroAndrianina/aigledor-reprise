<%@page import="affichage.PageRecherche"%>
<%@ page import="faturefournisseur.DmdAchatLib" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="static java.time.DayOfWeek.MONDAY" %>
<%@ page import="static java.time.temporal.TemporalAdjusters.previousOrSame" %>
<%@ page import="static java.time.DayOfWeek.SUNDAY" %>
<%@ page import="static java.time.temporal.TemporalAdjusters.nextOrSame" %>
<%@ page import="java.sql.Date" %>
<%@ page import="affichage.Liste" %>
<%@ page import="magasin.Magasin" %>
<%@ page import="produits.CategorieIngredient" %>
<%@ page import="user.UserEJB" %>
<%@ page import="historique.MapUtilisateur" %>
<%@ page import="utils.ConstanteAsync" %>

<%
    UserEJB u;
    try{
    u = (UserEJB) session.getAttribute("u");
    String role  = u.getUser().getIdrole();
    LocalDate today = LocalDate.now();
    LocalDate monday = today.with(previousOrSame(MONDAY));
    LocalDate sunday = today.with(nextOrSame(SUNDAY));
        String roleMagasinier =  ConstanteAsync.ROLE_MAGASINIER;
        String roleChefAtelier = ConstanteAsync.ROLE_CHEF_ATELIER;
        String roleDaf = ConstanteAsync.ROLE_DAF;
        String roleDg = ConstanteAsync.ROLE_DG;
    String[] etatVal = {"", "1", "11","3","4","5"};
    String[] etatAff = {"Tous","Cr&eacute;&eacute;e(s)", "Vis&eacute;e(s)","Vis&eacute;e(s) par magasinier","Vis&eacute;e(s) par Chef d&rsquo;atelier","Vis&eacute;e(s) par DAF"};
    DmdAchatLib o = new DmdAchatLib();
    String[] listeCrt = {"id","daty","fournisseurlib","idMagasinLib","idCategorieLib","remarque",};
    String[] listeInt = {"daty"};
    String[] libEntete = {"id","daty","fournisseurlib","idMagasinLib","idCategorieLib","remarque","etatlib"};
    PageRecherche pr = new PageRecherche(o, request, listeCrt, listeInt,4, libEntete, libEntete.length);

    pr.setTitre("Liste des demandes d'achat");
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("facturefournisseur/dmdachat/dmdachat-choix-multiple.jsp");
    String etatDefaut = "";
//    if (roleMagasinier.equals(role)) {
//        etatDefaut = "1";
//    } else if (roleChefAtelier.equals(role)) {
//        etatDefaut = "3";
//    } else if (roleDaf.equals(role) || roleDg.equals(role)) {
//        etatDefaut = "4";
//    } else {
//        etatDefaut = "1";
//    }

    String etatCourant = (request.getParameter("etat") != null) ? request.getParameter("etat") : etatDefaut;
    if (etatCourant != null && !etatCourant.isEmpty()) {
        pr.setAWhere(" and etat=" + etatCourant);
    }
    pr.getFormu().getChamp("id").setLibelle("Ref.");
    pr.getFormu().getChamp("daty1").setLibelle("Date min");
//    pr.getFormu().getChamp("daty1").setDefaut(utilitaire.Utilitaire.datetostring(Date.valueOf(monday)));
    pr.getFormu().getChamp("daty2").setLibelle("Date max");
//    pr.getFormu().getChamp("daty2").setDefaut(utilitaire.Utilitaire.datetostring(Date.valueOf(sunday)));
    String date = utilitaire.Utilitaire.formatterDaty(utilitaire.Utilitaire.ajoutJourDate(utilitaire.Utilitaire.dateDuJour(), -1));
    pr.getFormu().getChamp("daty1").setDefaut(date);
    pr.getFormu().getChamp("daty2").setDefaut(utilitaire.Utilitaire.dateDuJour());
    pr.getFormu().getChamp("fournisseurlib").setLibelle("Fournisseur");

    Liste[] listes = new Liste[2];
    Magasin magasin = new Magasin();
    magasin.setNomTable("MAGASIN2");
    listes[0] = new Liste("idMagasinLib", magasin, "val", "val");
    CategorieIngredient categorieIngredient = new CategorieIngredient();
    categorieIngredient.setNomTable("CATEGORIEINGREDIENT");
    listes[1] = new Liste("idCategorieLib", categorieIngredient, "val", "val");
    pr.getFormu().changerEnChamp(listes);

    pr.getFormu().getChamp("idMagasinLib").setLibelle("Magasin");
    pr.getFormu().getChamp("idCategorieLib").setLibelle("Cat&eacute;gorie");
//    pr.getFormu().getChamp("remarque").setLibelle("Fournisseur s&eacute;condaire");
    pr.getFormu().getChamp("remarque").setLibelle("Remarque");

    String[] colSomme = null;
    pr.creerObjetPage(libEntete, colSomme);

    String[] lienTableau = {pr.getLien() + "?but=facturefournisseur/dmdachat/dmdachat-fiche.jsp", pr.getLien() + "?but=facturefournisseur/dmdachat/demandeprix-fiche.jsp"};
    String[] colonneLien = {"id", "idObjet"};
    String[] attLien = {"id", "id"};
    pr.getTableau().setLien(lienTableau);
    pr.getTableau().setAttLien(attLien);
    pr.getTableau().setColonneLien(colonneLien);

    String[] libEnteteAffiche = {"Ref.", "Date","Fournisseur","Magasin","Cat&eacute;gorie","Remarque","&Eacute;tat"};
    pr.getTableau().setLibelleAffiche(libEnteteAffiche);
    pr.getFormu().setAnotherButton("" +
            "<a class=\"btn btn-primary pull-right btn-small\" href=\"module.jsp?but=facturefournisseur/dmdachat/dmdachat-saisie.jsp\">\n" +
            "                    <i class=\"material-symbols-rounded\">add</i>Saisie d'une demande d'achat</a>"
    );

%>
<script>
    function changerDesignation() {
        document.getElementById("dmd-liste--form").submit();
    }
</script>
<div class="content-wrapper">
    <section class="content-header">
        <h1><%= pr.getTitre() %></h1>
    </section>
    <section class="content">
        <form action="<%=pr.getLien()%>?but=<%= pr.getApres() %>" id="dmd-liste--form" method="post">
            <%
                out.println(pr.getFormu().getHtmlEnsemble());
            %>
            <div class="row col-md-12 noppadding">
                <div class="col-md-2 noppadding">
                    &Eacute;tat :
                    <select name="etat" class="champ form-control" id="etat" onchange="changerDesignation()">
                        <%
                            for( int i = 0; i < etatAff.length; i++ ){ %>
                        <% if(etatCourant.compareToIgnoreCase(etatVal[i]) == 0) {%>
                        <option value="<%= etatVal[i] %>" selected> <%= etatAff[i] %> </option>
                        <% } else { %>
                        <option value="<%= etatVal[i] %>"> <%= etatAff[i] %> </option>
                        <% } %>
                        <%    }
                        %>
                    </select>
                </div>
            </div>
            <br>
        </form>
        <%
            out.println(pr.getTableauRecap().getHtml());
        %>
        <br>
        <form action="module.jsp?but=facturefournisseur/dmdachat/apresMultipleValidation.jsp" method="post" name="ffd" id="ffd">
            <input name="bute" type="hidden" id="bute" value="facturefournisseur/dmdachat/dmdachat-liste.jsp">
            <%
                //out.println(pr.getTableau().getHtmlWithCheckbox());
                    if(pr.getTableau().getHtmlWithCheckbox() != null){
                        out.println(pr.getTableau().getHtmlWithCheckbox());
                    }else { %>
                    <div style="text-align: center;"><h4>Aucune donn&eacute;e disponible</h4></div>
                     <% } %> 
                    <% out.println(pr.getBasPage());
                    %>
           
        </form>
    </section>
</div>
<%
    }catch(Exception e){

        e.printStackTrace();
    }
%>