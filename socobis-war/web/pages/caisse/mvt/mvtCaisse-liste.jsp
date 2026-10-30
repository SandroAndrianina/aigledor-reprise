

<%@page import="caisse.MvtCaisseCpl"%>
<%@page import="affichage.PageRecherche"%>
<%@ page import="java.util.Map" %> 
<%@ page import="java.util.HashMap" %>
<%@ page import="affichage.Liste" %>
<%@ page import="caisse.Caisse" %>
<%@ page import="annexe.Point" %>

<% try{ 
    MvtCaisseCpl t = new MvtCaisseCpl();
    String etat = request.getParameter("etat");
    if(etat == null) etat = "";
    t.setNomTable( t.getNomTable().concat(etat) );
    // Les anciennes versions du modele ne comportent pas les champs Point et Heure.
    boolean avecPoint = bean.ListeColonneTable.getChamp(t, "idPoint") != null;
    boolean avecHeure = bean.ListeColonneTable.getChamp(t, "heure") != null;
    String listeCrt[] = avecPoint
        ? new String[] {"id", "designation", "tiers", "idCaisse", "daty", "idPoint"}
        : new String[] {"id", "designation", "tiers", "idCaisse", "daty"};
    String listeInt[] = {"daty"};
    String libEntete[] = avecHeure
        ? new String[] {"id", "daty", "designation", "tiers", "idCaisseLib", "credit", "debit", "heure", "etatLib"}
        : new String[] {"id", "daty", "designation", "tiers", "idCaisseLib", "credit", "debit", "etatLib"};
    PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3, libEntete, libEntete.length);
    pr.setTitre("Liste des mouvements de caisse");
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("caisse/mvt/mvtCaisse-liste.jsp");
    String idPointSession = (String) session.getAttribute("idPoint");

    Liste[] listes = new Liste[avecPoint ? 2 : 1];
    Caisse caisse = new Caisse();
    caisse.setNomTable("CAISSE");
    listes[0] = new Liste("idCaisse", caisse, "val", "id");
    if (avecPoint) listes[1] = new Liste("idPoint", new Point(), "val", "id");

    pr.getFormu().changerEnChamp(listes);

    if (avecPoint && idPointSession != null){
        pr.getFormu().getChamp("idPoint").setDefaut(idPointSession);
    }

    pr.getFormu().getChamp("daty1").setLibelle("Date min");
    pr.getFormu().getChamp("designation").setLibelle("d&eacute;signation");
    String date = utilitaire.Utilitaire.formatterDaty(utilitaire.Utilitaire.ajoutJourDate(utilitaire.Utilitaire.dateDuJour(), -1));
    pr.getFormu().getChamp("daty1").setDefaut(date);
    pr.getFormu().getChamp("daty2").setDefaut(utilitaire.Utilitaire.dateDuJour());
    pr.getFormu().getChamp("daty2").setLibelle("Date max");
    pr.getFormu().getChamp("idCaisse").setLibelle("Caisse");
    if (avecPoint) pr.getFormu().getChamp("idPoint").setLibelle("Point");

    String[] colSomme = {"credit", "debit"};
    pr.creerObjetPage(libEntete, colSomme);
    
    Map<String,String> lienTab=new HashMap();
    lienTab.put("modifier",pr.getLien() + "?but=caisse/mvt/mvtCaisse-modif.jsp");  
    pr.getTableau().setLienClicDroite(lienTab);

    //Definition des lienTableau et des colonnes de lien
    String lienTableau[] = {pr.getLien() + "?but=caisse/mvt/mvtCaisse-fiche.jsp"};
    String colonneLien[] = {"id"};
    pr.getTableau().setLien(lienTableau);
    pr.getTableau().setColonneLien(colonneLien);
    String libEnteteAffiche[] = avecHeure
        ? new String[] {"id", "date", "d&eacute;signation", "Tiers", "Caisse", "Entr&eacute;e de caisse", "Sortie de caisse", "Heure", "&Eacute;tat"}
        : new String[] {"id", "date", "d&eacute;signation", "Tiers", "Caisse", "Entr&eacute;e de caisse", "Sortie de caisse", "&Eacute;tat"};
    pr.getTableau().setLibelleAffiche(libEnteteAffiche);
    String[] etatAffiche = {"Tous","Cr&eacute;&eacute;(s)","&Agrave; Comptabliser","Valid&eacute;(s)","Annul&eacute;(s)"};
    String[] etatPasse = {"","_cree","_a","_valider","_annule"};
    String enteteRecap[] = {"","Nombre","Somme des entr&eacute;es","Somme des sorties"};
    pr.getTableauRecap().setLibeEntete(enteteRecap);
    pr.getFormu().setAnotherButton("" +
    "<a class=\"btn btn-primary pull-right btn-small\" href=\"module.jsp?but=caisse/mvt/mvtCaisse-saisie-entree.jsp&currentMenu=MENUDYN00171\">\n" +
    "                    <i class=\"material-symbols-rounded\">add</i>Saisie mouvement d'entr&eacute;e</a>"+
    "<a class=\"btn btn-primary pull-right btn-small mx-2\" href=\"module.jsp?but=caisse/mvt/mvtCaisse-saisie-sortie.jsp&currentMenu=MENUDYN00173\">\n" +
    "                    <i class=\"material-symbols-rounded\">add</i>Saisie mouvement de sortie</a>"
    );
%>
<script>
    function changerEtat(){
        document.formRecherche.submit();
    }
</script>
<div class="content-wrapper">
    <section class="content-header">
        <h1><%= pr.getTitre() %></h1>
    </section>
    <section class="content">
        <form action="<%=pr.getLien()%>?but=<%= pr.getApres() %>" method="post" name="formRecherche" id="formRecherche">
            <%
                out.println(pr.getFormu().getHtmlEnsemble());
            %>
        <div class="row">
            <div class="col-lg-12 ">
                    <div class="col-md-2 nopadding" style="margin-top: 12px">
                        <div class="d-flex" style="align-items: end;gap: 8px;">
                            <div class="form-input w-100">
                                <label for="etat" class="input-label">&Eacute;tat</label>
                                <select name="etat" id="etat" class="form-control" onchange="changerEtat()">
                                    <%
                                        for(int i=0; i<etatAffiche.length; i++){
                                            String selected = "";
                                            if(request.getParameter("etat")!=null && request.getParameter("etat").compareToIgnoreCase(etatPasse[i])==0){
                                                selected = "selected";
                                            }
                                    %>
                                    <option value="<%=etatPasse[i]%>" <%=selected%>><%=etatAffiche[i]%></option>
                                    <%
                                        }
                                    %>
                                </select>
                            </div>
                        </div>
                    </div>
            </div>
        </div>
        </form>
        <div class="row">
            <div class="col-md-12">
        <%
            out.println(pr.getTableauRecap().getHtml());%>
            </div>
        </div>
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



