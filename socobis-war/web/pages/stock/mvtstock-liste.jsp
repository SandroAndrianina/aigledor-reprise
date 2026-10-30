<%@page import="stock.MvtStockLib"%>
<%@page import="stock.MvtStock"%>
<%@page import="affichage.PageRecherche"%>
<%@page import="magasin.Magasin"%>
<%@page import="affichage.Liste"%>
<%@page import="stock.TypeMvtStock"%>
<%@ page import="java.util.Map" %> 
<%@ page import="java.util.HashMap" %>
<%@page import="user.UserEJB"%>
<%@ page import="bean.TypeObjet" %>
<%@ page import="utilitaire.Utilitaire" %>

<% try{
    UserEJB u = (UserEJB) session.getValue("u");
    Magasin mag = u.getMagasinEncours();
    MvtStockLib stock = new MvtStockLib();
    if (request.getParameter("etat") != null && request.getParameter("etat").compareToIgnoreCase("") != 0) {
        stock.setNomTable(request.getParameter("etat"));
    }else{
        stock.setNomTable("mvtstocklib");
    }
    
    String listeCrt[] = {"id","designation","idMagasin","idTypeMvStock","daty"};
    String listeInt[] = {"daty"};
    String libEntete[] = {"id","daty","heure","designation", "montantEntree", "montantSortie","idMagasinlib","idVentelib","idTransfertlib","idTypeMvStocklib","etatlib"};
    PageRecherche pr = new PageRecherche(stock, request, listeCrt, listeInt, 3, libEntete, libEntete.length);
    pr.setTitre("Liste des mouvements de stocks");
    if(mag != null){
        pr.setAWhere("AND IDMAGASIN='"+mag.getId()+"'");
    }
    Liste[] dropDowns = new Liste[2];
    dropDowns[0] = new Liste( "idMagasin", new Magasin(), "val" , "id" );
    dropDowns[1] = new Liste( "idTypeMvStock", new TypeMvtStock(), "val" , "id" );

    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("stock/mvtstock-liste.jsp");
    pr.getFormu().changerEnChamp(dropDowns);
    pr.getFormu().getChamp("id").setLibelle("Id");
    pr.getFormu().getChamp("designation").setLibelle("D&eacute;signation");
    pr.getFormu().getChamp("idMagasin").setLibelle("Magasin");
    pr.getFormu().getChamp("idTypeMvStock").setLibelle("Type de mouvement");
    pr.getFormu().getChamp("daty1").setLibelle("Date min");
    pr.getFormu().getChamp("daty1").setDefaut(utilitaire.Utilitaire.dateDuJour());
    pr.getFormu().getChamp("daty2").setLibelle("Date max");
    pr.getFormu().getChamp("daty2").setDefaut(utilitaire.Utilitaire.dateDuJour());
    if(mag != null){
        pr.getFormu().getChamp("idMagasin").setDefaut(mag.getId());
        pr.getFormu().getChamp("idMagasin").setAutre("disabled");
    }
    String[] colSomme = null;
    pr.creerObjetPage(libEntete, colSomme);

    String[] idAttributsLienTab = {"daty1"};
    String[] valAttributsLienTab = {pr.getFormu().getChamp("daty1").getValeur()};

    Map<String,String> lienTab=new HashMap();
    lienTab.put("viser", pr.getLien() +"?but=apresTarif.jsp&acte=valider&bute=stock/mvtstock-liste.jsp&classe=stock.MvtStock&rajoutLien="+ Utilitaire.ajouterParam(idAttributsLienTab, valAttributsLienTab));
    lienTab.put("modifier",pr.getLien() + "?but=stock/mvtstock-saisie.jsp&acte=update");
    lienTab.put("dupliquer", pr.getLien() + "?but=stock/mvtstock-saisie.jsp&acte=dupliquer");
    pr.getTableau().setLienClicDroite(lienTab);

    //Definition des lienTableau et des colonnes de lien
    String lienTableau[] = {pr.getLien() + "?but=stock/mvtstock-fiche.jsp"};
    String colonneLien[] = {"id"};
    pr.getTableau().setLien(lienTableau);
    pr.getTableau().setColonneLien(colonneLien);
    String libEnteteAffiche[] = {"Id","Date","Heure","D&eacute;signation","Montant Entr&eacute;e", "Montant Sortie","Magasin","Vente","Transfert","Type de mouvement","&Eacute;tat"};
    pr.getTableau().setLibelleAffiche(libEnteteAffiche);
    pr.getTableau().setLienFille("stock/mvtfille-liste.jsp&id=");
    pr.getFormu().setAnotherButton("" +
    "<a class=\"btn btn-primary pull-right btn-small\" href=\"module.jsp?but=stock/mvtstock-saisie.jsp&currentMenu=MNDN000000024\">\n" +
    "                    <i class=\"material-symbols-rounded\">add</i>Saisie d'un mouvement de stock</a>");
%>
<script>
    function changerDesignation() {
        document.stock.submit();
    }
</script>

<div class="content-wrapper">
    <section class="content-header">
        <h1><%= pr.getTitre() %></h1>
    </section>
    <section class="content">
        <form action="<%=pr.getLien()%>?but=<%= pr.getApres() %>" method="post" name = "stock">
            <%
                out.println(pr.getFormu().getHtmlEnsemble());
            %>
            <div class="row col-md-12 nopadding">
                <div class="col-md-2 nopadding">
                    &Eacute;tat :
                    <select name="etat" class="champ form-control" id="etat" onchange="changerDesignation()" >
                        <% if (request.getParameter("etat") != null && request.getParameter("etat").compareToIgnoreCase("mvtstocklib") == 0) {%>
                        <option value="mvtstocklib" selected>Tous</option>
                        <% } else { %>
                        <option value="mvtstocklib" >Tous</option>
                        <% } %>
                        <% if (request.getParameter("etat") != null && request.getParameter("etat").compareToIgnoreCase("mvtstocklibcreer") == 0) {%>
                        <option value="mvtstocklibcreer" selected>Cr&eacute;&eacute;e(s)</option>
                        <% } else { %>
                        <option value="mvtstocklibcreer">Cr&eacute;&eacute;e(s)</option>
                        <% } %>
                        <% if (request.getParameter("etat") != null && request.getParameter("etat").compareToIgnoreCase("mvtstocklibvalider") == 0) {%>
                        <option value="mvtstocklibvalider" selected>Valid&eacute;e(s)</option>
                        <% } else { %>
                        <option value="mvtstocklibvalider">Valid&eacute;e(s)</option>
                        <% } %>
                    </select>
                </div>
            </div>
        </form>
        <%
            out.println(pr.getTableauRecap().getHtml());%>
        <br>
        <%
            out.println(pr.getTableau().getHtml());
        %>
    </section>
</div>
    <%
    }catch(Exception e){

        e.printStackTrace();
    }
%>



