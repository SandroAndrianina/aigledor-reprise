<%@page import="faturefournisseur.As_BonDeLivraison_Lib"%>
<%@page import="affichage.PageRecherche"%>
<%@page import="affichage.Liste"%>
<%@ page import="magasin.Magasin" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="utilitaire.Utilitaire" %>

<% 
    try{ 
    String[] etatVal = {"","1","11","0"};
    String[] etatAff = {"Tous","Cr&eacute;&eacute;(s)", "Vis&eacute;e(s)","Annul&eacute;(s)"};
    String[] etatValLiv = {"","0","1","2","3"};
    String[] etatAffLiv = {"Tous","Non Trait&eacute;","Trait&eacute; partiellement", "Trait&eacute;","Trait&eacute; avec surplus"};
    
    As_BonDeLivraison_Lib t = new As_BonDeLivraison_Lib();
    t.setNomTable("AS_BONDELIVRAISON_TRAITE");
    String listeCrt[] = {"id","daty","idFournisseurLib","magasinlib","remarque"};
    String listeInt[] = {"daty"};
    String libEntete[] = {"id","daty","idFournisseurLib","magasinlib","remarque","traite","etatlib"};
    PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3, libEntete, libEntete.length);
    pr.setNpp(50);
    pr.setTitre("Liste des bons de r&eacute;ception");
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    String awhere = "";
    if(request.getParameter("etat")!=null && request.getParameter("etat").compareToIgnoreCase("")!=0) {
        awhere += " and etat=" + request.getParameter("etat");
    }
    if(request.getParameter("livraison")!=null && request.getParameter("livraison").compareToIgnoreCase("")!=0) {
        awhere += " and idtraite= '"+request.getParameter("livraison")+"'";
    }
    pr.setAWhere(awhere + " order by daty desc");

    Liste[] liste = new Liste[1];
    Magasin magasinlib = new Magasin();
    magasinlib.setNomTable("MAGASIN2");
    liste[0] = new Liste("magasinlib", magasinlib, "val", "val");
    pr.getFormu().changerEnChamp(liste);
    pr.getFormu().getChamp("id").setLibelle("Ref.");
    pr.setApres("bondelivraison/bondelivraison-liste.jsp");
    
    pr.getFormu().getChamp("magasinlib").setLibelle("Magasin");
    pr.getFormu().getChamp("remarque").setLibelle("Remarque");
    pr.getFormu().getChamp("daty1").setLibelle("Date min");
    String date = utilitaire.Utilitaire.formatterDaty(utilitaire.Utilitaire.ajoutJourDate(utilitaire.Utilitaire.dateDuJour(), -1));
    pr.getFormu().getChamp("daty1").setDefaut(date);
    pr.getFormu().getChamp("daty2").setDefaut(utilitaire.Utilitaire.dateDuJour());
    pr.getFormu().getChamp("daty2").setLibelle("Date max");
    pr.getFormu().getChamp("idFournisseurLib").setLibelle("Fournisseur");
    
    String[] colSomme = null;
    pr.setNpp(25);
    pr.creerObjetPage(libEntete, colSomme);
    //Definition des lienTableau et des colonnes de lien
    String lienTableau[] = {pr.getLien() + "?but=bondelivraison/bondelivraison-fiche.jsp"};
    String colonneLien[] = {"id"};
    pr.getTableau().setLien(lienTableau);
    pr.getTableau().setLienFille("bondelivraison/inc/bondelivraison-details.jsp&id=");
    pr.getTableau().setColonneLien(colonneLien);

    Map<String,String> lienTab=new HashMap();
    String[] id = {"daty1"};
    String[] val = {pr.getFormu().getChamp("daty1").getValeur()};
    String classe = "faturefournisseur.As_BonDeLivraison";
    lienTab.put("viser",pr.getLien() + "?but=apresTarif.jsp&acte=valider&bute=bondelivraison/bondelivraison-liste.jsp&classe=" + classe + "&rajoutLien="+ Utilitaire.ajouterParam(id, val) );
    pr.getTableau().setLienClicDroite(lienTab);
    
    String libEnteteAffiche[] = {"Ref.","date", "fournisseur","magasin","remarque","Livraison","&Eacute;tat" };
    pr.getTableau().setLibelleAffiche(libEnteteAffiche);
    pr.getFormu().setAnotherButton("" +
    "<a class=\"btn btn-primary pull-right btn-small\" href=\"module.jsp?but=bondelivraison/bondelivraison-saisie.jsp&currentMenu=MNDN001804005\">\n" +
    "                    <i class=\"material-symbols-rounded\">add</i>Saisie d'un bon de livraison</a>"
    );
%>
<script>
    function changerDesignation() {
        document.filtre.submit();
    }
</script>

<div class="content-wrapper">
    <section class="content-header">
        <h1><%= pr.getTitre() %></h1>
    </section>
    <section class="content">
        <form action="<%=pr.getLien()%>?but=<%= pr.getApres() %>" method="post" name = "filtre">
            <%
                out.println(pr.getFormu().getHtmlEnsemble());
            %>
            <div class="row col-md-12 nopadding" style="margin-top: 12px">
                <div class="col-md-2 nopadding">
                            &Eacute;tat
                            <select name="etat" class="champ form-control" id="etat" onchange="changerDesignation()">
                                <%
                                    for( int i = 0; i < etatAff.length; i++ ){ %>
                                <% if(request.getParameter("etat") !=null && request.getParameter("etat").compareToIgnoreCase(etatVal[i]) == 0) {%>
                                <option value="<%= etatVal[i] %>" selected> <%= etatAff[i] %> </option>
                                <% } else { %>
                                <option value="<%= etatVal[i] %>"> <%= etatAff[i] %> </option>
                                <% } %>
                                <%    }
                                %>
                            </select>
                       </div>
                    <div class="col-md-2 nopadding">
                        <label class="input-label" for="livraison">Livraison  :</label>
                        <select name="livraison" class="champ form-control" id="livraison" onchange="changerDesignation()">
                            <%
                                for( int i = 0; i < etatAffLiv.length; i++ ){ %>
                            <% if(request.getParameter("livraison") !=null && request.getParameter("livraison").compareToIgnoreCase(etatValLiv[i]) == 0) {%>
                            <option value="<%= etatValLiv[i] %>" selected> <%= etatAffLiv[i] %> </option>
                            <% } else { %>
                            <option value="<%= etatValLiv[i] %>"> <%= etatAffLiv[i] %> </option>
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
