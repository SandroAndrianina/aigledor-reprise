<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="affichage.PageRecherche"%>
<%@ page import="caisse.CaisseSession" %>
<%@ page import="affichage.Liste"%>
<%@ page import="bean.TypeObjet" %>
<%@ page import="caisse.CaisseSessionLib" %>
<%@ page import="utilisateur.Utilisateur" %>

<% try{ 
    CaisseSessionLib o = new CaisseSessionLib();
    o.setNomTable("CAISSESESSIONLIB");
    String[] listeCrt = {"dateOuverture","dateFermeture","idPoint", "idUserOuverture", "idUserFermeture"};
    String[] listeInt = {"dateOuverture","dateFermeture"};
    String[] libEntete = {"id","idPointLib","idcaisselib","dateOuverture","heureOuverture","dateFermeture","heureFermeture","idUserOuvertureLib","idUserFermetureLib"};
    PageRecherche pr = new PageRecherche(o, request, listeCrt, listeInt, 4, libEntete, libEntete.length);
    pr.setTitre("Liste des sessions de caisse");
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("caisse/caisse-session-liste.jsp");

    Liste[] liste = new Liste[3];
    TypeObjet liste0 = new TypeObjet();
    liste0.setNomTable("POINT");
    liste[0] = new Liste("idPoint",liste0,"val","id");
    Utilisateur idUserOuverture = new Utilisateur();
    idUserOuverture.setNomTable("utilisateur");
    liste[1] = new Liste("idUserOuverture", idUserOuverture, "nomuser", "refuser");
    Utilisateur idUserFermeture = new Utilisateur();
    idUserFermeture.setNomTable("utilisateur");
    liste[2] = new Liste("idUserFermeture", idUserFermeture, "nomuser", "refuser");

    pr.getFormu().changerEnChamp(liste);

    pr.getFormu().getChamp("dateOuverture1").setLibelle("Date d'ouverture min");
    pr.getFormu().getChamp("dateOuverture2").setLibelle("Date d'ouverture max");
    pr.getFormu().getChamp("dateFermeture1").setLibelle("Date de fermeture min");
    pr.getFormu().getChamp("dateFermeture2").setLibelle("Date de fermeture max");
    pr.getFormu().getChamp("idPoint").setLibelle("Point");
    pr.getFormu().getChamp("idUserOuverture").setLibelle("Utilisateur d'ouverture");
    pr.getFormu().getChamp("idUserFermeture").setLibelle("Utilisateur de fermeture");
    pr.setNpp(20);
    
    String[] colSomme = null;
    pr.creerObjetPage(libEntete, colSomme);

    String[] libEnteteAffiche = {"Id","Point","Caisse","Date d'ouverture","Heure d'ouverture","Date de fermeture","Heure de fermeture","Utilisateur d'ouverture","Utilisateur de fermeture"};
    pr.getTableau().setLibelleAffiche(libEnteteAffiche);
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
            out.println(pr.getTableauRecap().getHtml());
        %>
        <br>
        <%
            out.println(pr.getTableau().getHtml());
            out.println(pr.getBasPage());
        %>
    </section>
</div>

<% } catch (Exception e) {
  e.printStackTrace();
%>
<script language="JavaScript"> alert('<%=e.getMessage()%>');
    history.back();
</script>
<% }%>

