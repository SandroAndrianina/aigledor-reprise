<%@page import="magasin.Magasin"%>
<%@page import="faturefournisseur.As_BonDeCommande"%>
<%@page import="affichage.PageRecherche"%>
<%@page import="bean.TypeObjet"%>
<%@page import="affichage.Liste"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="faturefournisseur.As_BonDeCommandeCpl" %>
<%@ page import="utils.ConstanteAsync" %>
<%@ page import="user.UserEJB" %>

<%     UserEJB u;
    try{
        u = (UserEJB) session.getAttribute("u");
        String role  = u.getUser().getIdrole();
    String roleMagasinier =  ConstanteAsync.ROLE_MAGASINIER;
    String roleChefAtelier = ConstanteAsync.ROLE_CHEF_ATELIER;
    String roleDaf = ConstanteAsync.ROLE_DAF;
    String roleDg = ConstanteAsync.ROLE_DG;

    String[] etatVal = {"%","1","3","4","5"};
    String[] etatAff = {"Tous","Cr&eacute;&eacute;(s)", "Vis&eacute;(s) par le Magasinier", "Vis&eacute;(s) par le Chef d&rsquo;atelier", "Vis&eacute;(s) par le DAF"};

    As_BonDeCommandeCpl f = new As_BonDeCommandeCpl();
    f.setNomTable("AS_BONDECOMMANDE_CPL_MAGASIN");
    String listeCrt[] = {"id", "daty", "designation", "fournisseurlib", "idMagasin", "modepaiementlib", "remarque"};
    String listeInt[] = {"daty"};
    String libEntete[] = {"id","daty","designation","fournisseurlib","idMagasinLib","modepaiementlib","remarque","etatlib"};
    PageRecherche pr = new PageRecherche(f, request, listeCrt, listeInt, 3, libEntete, libEntete.length);

    String etatDefaut;
    if (roleMagasinier.equals(role)) {
        etatDefaut = "1";
    } else if (roleChefAtelier.equals(role)) {
        etatDefaut = "3";
    } else if (roleDaf.equals(role) || roleDg.equals(role)) {
        etatDefaut = "4";
    } else if (roleDg.equals(role)) {
        etatDefaut = "5";
    }else {
        etatDefaut = "1";
    }

    String etatCourant = (request.getParameter("etat") != null) ? request.getParameter("etat") : etatDefaut;
    if ("%".equals(etatCourant)) {
        pr.setAWhere(" and etat in (1,3,4,5)");
    } else {
        pr.setAWhere(" and etat=" + etatCourant);
    }

    pr.setTitre("Validation des bons de commande");
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("bondecommande/bondecommande-choix-multiple.jsp");
    pr.getFormu().getChamp("id").setLibelle("Ref.");
    pr.getFormu().getChamp("designation").setLibelle("D&eacute;signation");
    pr.getFormu().getChamp("fournisseurlib").setLibelle("Fournisseur");

    TypeObjet modePaiement= new TypeObjet();
    modePaiement.setNomTable("MODEPAIEMENT");
    Liste[] liste = new Liste[2];
    liste[0] = new Liste("modepaiementlib", modePaiement, "val", "val");
    Magasin magasin = new Magasin();
    magasin.setNomTable("MAGASIN2");
    liste[1] = new Liste("idMagasin",magasin,"val","id");
    pr.getFormu().changerEnChamp(liste);
    pr.getFormu().getChamp("modepaiementlib").setLibelle("Mode de paiement");
    pr.getFormu().getChamp("idMagasin").setLibelle("Magasin");
//    pr.getFormu().getChamp("reference").setLibelle("R&eacute;f&eacute;rence");
    pr.getFormu().getChamp("daty1").setLibelle("Date min");
    String date = utilitaire.Utilitaire.formatterDaty(utilitaire.Utilitaire.ajoutJourDate(utilitaire.Utilitaire.dateDuJour(), -1));
    pr.getFormu().getChamp("daty1").setDefaut(date);
    pr.getFormu().getChamp("daty2").setLibelle("Date max");
    pr.getFormu().getChamp("daty2").setDefaut(utilitaire.Utilitaire.dateDuJour());
    String[] colSomme = null;
    pr.creerObjetPage(libEntete, colSomme);

    Map<String,String> lienTab=new HashMap();
    lienTab.put("modifier",pr.getLien() + "?but=bondecommande/bondecommande-modif.jsp");
    pr.getTableau().setLienClicDroite(lienTab);

    //Definition des lienTableau et des colonnes de lien
    String lienTableau[] = {pr.getLien() + "?but=bondecommande/bondecommande-fiche.jsp"};
    String colonneLien[] = {"id"};
    pr.getTableau().setLien(lienTableau);
    pr.getTableau().setColonneLien(colonneLien);
    String libEnteteAffiche[] = {"Ref.","Date","D&eacute;signation","Fournisseur","Magasin","Mode de paiement","Remarque","&Eacute;tat"};
    pr.getTableau().setLibelleAffiche(libEnteteAffiche);
    pr.getTableau().setLienFille("bondecommande/inc/bdc-liste-details.jsp&id=");
    pr.getFormu().setAnotherButton("" +
            "<a class=\"btn btn-primary pull-right btn-small\" href=\"module.jsp?but=bondecommande/bondecommande-saisie.jsp\">\n" +
            "                    <i class=\"material-symbols-rounded\">add</i> Saisir un bon de commande</a>"
    );
%>
<script>
    function changerDesignation() {
        document.bdc.submit();
    }
</script>
<div class="content-wrapper">
    <section class="content-header">
        <h1><%= pr.getTitre() %></h1>
    </section>
    <section class="content">
        <form action="<%=pr.getLien()%>?but=<%= pr.getApres() %>" method="post" name="bdc">
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
        </form>
        <%
            out.println(pr.getTableauRecap().getHtml());%>
        <br>
        <form action="bondecommande/bondecommande-validation-multiple.jsp" method="get" name="incident" id="incident">
        <%
           // out.println(pr.getTableau().getHtmlWithCheckbox());
             if(pr.getTableau().getHtmlWithCheckbox() != null){
                out.println(pr.getTableau().getHtmlWithCheckbox());
            }else { %>
            <div style="text-align: center;"><h4>Aucune donn&eacute;e disponible</h4></div>
             <% } %> 
            <% out.println(pr.getBasPage());
        %>
            <input name="bute" type="hidden" id="bute" value="bondecommande/bondecommande-liste.jsp">
        </form>

    </section>
</div>
<%
    }catch(Exception e){

        e.printStackTrace();
    }
%>
