<%@page import="faturefournisseur.As_BonDeCommande"%>
<%@page import="affichage.PageRecherche"%>
<%@page import="bean.TypeObjet"%>
<%@page import="affichage.Liste"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="faturefournisseur.As_BonDeCommandeCpl" %>
<%@ page import="utilitaire.Utilitaire" %>

<% try{
    String[] etatVal = {"","1","11","0"};
    String[] etatAff = {"Tous","Cr&eacute;&eacute;e(s)", "Vis&eacute;e(s)","Annul&eacute;e(s)"};
    
    String[] etatValLiv = {"","0","1","2","3"};
    String[] etatAffLiv = {"Tous","Non Trait&eacute;","Trait&eacute; partiellement", "Trait&eacute;","Trait&eacute; avec surplus"};
    As_BonDeCommandeCpl f = new As_BonDeCommandeCpl();
    f.setNomTable("As_BonDeCommande_MERETRAITE ");
    String listeCrt[] = {"id","daty","designation","fournisseurlib","modepaiementlib","idDeviselib","montantTTC","montantTTCAriary","reference","refproforma"};
    String listeInt[] = {"daty","montantTTC","montantTTCAriary"};
    String libEntete[] = {"id","daty","reference","designation","fournisseurlib","modepaiementlib","idDeviselib","montantTTC","montantTTCAriary","idServiceLib","refproforma","etatlib","traite"};
    PageRecherche pr = new PageRecherche(f, request, listeCrt, listeInt, 3, libEntete, libEntete.length);
    pr.setNpp(50);
    pr.setTitre("Liste des bons de commande fournisseur");
    String awhere = "";
    if(request.getParameter("etat")!=null && request.getParameter("etat").compareToIgnoreCase("")!=0) {
        awhere += " and etat=" + request.getParameter("etat");
    }
    if(request.getParameter("livraison")!=null && request.getParameter("livraison").compareToIgnoreCase("")!=0) {
        awhere += " and idtraite= '"+request.getParameter("livraison")+"'";
    }
    pr.setAWhere(awhere + " order by daty desc");
    pr.getFormu().getChamp("id").setLibelle("Ref.");
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("bondecommande/bondecommande-liste.jsp");
    pr.getFormu().getChamp("reference").setLibelle("R&eacute;f&eacute;rence"); 
    pr.getFormu().getChamp("designation").setLibelle("D&eacute;signation"); 
    pr.getFormu().getChamp("fournisseurlib").setLibelle("Fournisseur");
    pr.getFormu().getChamp("refproforma").setLibelle("R&eacute;f&eacute;rence proforma");

    TypeObjet modePaiement= new TypeObjet();
    modePaiement.setNomTable("MODEPAIEMENT");
    Liste[] liste = new Liste[1];
    liste[0] = new Liste("modepaiementlib", modePaiement, "val", "val");
    pr.getFormu().changerEnChamp(liste);
    pr.getFormu().getChamp("modepaiementlib").setLibelle("Mode de paiement");
//    pr.getFormu().getChamp("reference").setLibelle("R&eacute;f&eacute;rence");
    pr.getFormu().getChamp("daty1").setLibelle("Date min");
    String date = utilitaire.Utilitaire.formatterDaty(utilitaire.Utilitaire.ajoutJourDate(utilitaire.Utilitaire.dateDuJour(), -1));
    pr.getFormu().getChamp("daty1").setDefaut(date);
    pr.getFormu().getChamp("daty2").setLibelle("Date max");
    pr.getFormu().getChamp("daty2").setDefaut(utilitaire.Utilitaire.dateDuJour());
    pr.getFormu().getChamp("idDeviseLib").setLibelle("Devise");
    pr.getFormu().getChamp("montantTTC1").setLibelle("Montant TTC min");
    pr.getFormu().getChamp("montantTTC2").setLibelle("Montant TTC max");
    pr.getFormu().getChamp("montantTTCAriary1").setLibelle("Montant TTC min Ar");
    pr.getFormu().getChamp("montantTTCAriary2").setLibelle("Montant TTC max Ar");

    String[] colSomme = {"montantTTCAriary"};
    pr.creerObjetPage(libEntete, colSomme);

    Map<String,String> lienTab=new HashMap(); 
        lienTab.put("modifier",pr.getLien() + "?but=bondecommande/bondecommande-modif.jsp"); 
    String[] id = {"daty1"};
    String[] val = {pr.getFormu().getChamp("daty1").getValeur()};
    String classe = "faturefournisseur.As_BonDeCommande";
    lienTab.put("viser",pr.getLien() + "?but=apresTarif.jsp&acte=valider&bute=bondecommande/bondecommande-liste.jsp&classe=" + classe+"&rajoutLien="+ Utilitaire.ajouterParam(id, val) );
    pr.getTableau().setLienClicDroite(lienTab);
    String enteteRecap[] = { "", "Nombre", "Somme des montants TTC en ariary"};
    pr.getTableauRecap().setLibeEntete(enteteRecap);

    //Definition des lienTableau et des colonnes de lien
    String lienTableau[] = {pr.getLien() + "?but=bondecommande/bondecommande-fiche.jsp"};
    String colonneLien[] = {"id"};
    pr.getTableau().setLien(lienTableau);
    pr.getTableau().setColonneLien(colonneLien);
    String libEnteteAffiche[] = {"Ref.","Date","R&eacute;f&eacute;rence","D&eacute;signation","Fournisseur","Mode de paiement","Devise","Montant TTC","Montant TTC AR","D&eacute;partement","R&eacute;f&eacute;rence proforma","&Eacute;tat","Livraison"};
    pr.getTableau().setLibelleAffiche(libEnteteAffiche);
    pr.getFormu().setAnotherButton("" +
            "<a class=\"btn btn-primary pull-right btn-small\" href=\"module.jsp?but=bondecommande/bondecommande-saisie.jsp\">\n" +
            "                    <i class=\"material-symbols-rounded\">add</i> Saisir un bon de commande</a>"
    );
    pr.getTableau().setLienFille("bondecommande/inc/bondecommande-liste-detail.jsp&id=");
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
        <form action="<%=pr.getLien()%>?but=<%= pr.getApres() %>" method="post"  name="filtre">
            <%
                out.println(pr.getFormu().getHtmlEnsemble());
            %>
            <div class="row col-md-12" style="margin-top: 12px;">
                    <div class="col-md-2 nopadding" style="width: 271px" >
                        <label class="input-label" for="etat">&Eacute;tat :</label>
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
