<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="affichage.PageRecherche"%>
<%@ page import="annexe.src.java.rh.pointage.PointageMagasinierLib" %>
<%@ page import="affichage.Liste"%>
<%@ page import="rh.pointage.TypePointage" %>
<%@ page import="annexe.Point" %>
<%@ page import="bean.CGenUtil" %>
<%@ page import="utilitaire.Utilitaire" %>

<% try{ 
    PointageMagasinierLib o = new PointageMagasinierLib();
    o.setNomTable("POINTAGE_MAGASINIER_LIB");
    String[] listeCrt = {"idPointLib","idPersonnelLib","idTypePointageLib","daty"};
    String[] listeInt = {"daty"};
    String[] libEntete = {"id", "idPersonnelLib", "idTypePointageLib", "idPointLib", "daty", "heure", "etatLib"};
    PageRecherche pr = new PageRecherche(o, request, listeCrt, listeInt, 4, libEntete, libEntete.length);
    pr.setTitre("Liste des pointages des personnels");
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("rh/pointage/pointagemagasinier-liste.jsp");
    String idPoint = (String)session.getValue("idPoint");
    Point m = new Point();
    m = (Point) m.getById(idPoint,"POINT",null);
    String nompoint = m.getVal();

    Liste[] liste = new Liste[2];
    TypePointage liste0 = new TypePointage();
    liste0.setNomTable("TYPEPOINTAGE");
    liste[0] = new Liste("idTypePointageLib",liste0,"val","val");
    Point liste1 = new Point();
    liste1.setNomTable("POINT");
    liste[1] = new Liste("idPointLib", liste1, "val", "val");
    pr.getFormu().changerEnChamp(liste);
    pr.getFormu().getChamp("idPersonnelLib").setLibelle("Personnel");
    pr.getFormu().getChamp("idTypePointageLib").setLibelle("Type de pointage");
    pr.getFormu().getChamp("daty1").setLibelle("Date Min");
    pr.getFormu().getChamp("daty1").setDefaut(Utilitaire.dateDuJour());
    pr.getFormu().getChamp("daty2").setLibelle("Date Max");
    pr.getFormu().getChamp("daty2").setDefaut(Utilitaire.dateDuJour());
    pr.getFormu().getChamp("idPointLib").setLibelle("Magasin");
    pr.getFormu().getChamp("idPointLib").setDefaut(nompoint);
    
    String[] colSomme = null;
    pr.creerObjetPage(libEntete, colSomme);

    String[] lienTableau = {pr.getLien() + "?but=rh/pointage/pointagemagasinier-fiche.jsp"};
    String[] colonneLien = {"id"};
    String[] attributLien = {"id"};
    pr.getTableau().setLien(lienTableau);
    pr.getTableau().setColonneLien(colonneLien);
    pr.getTableau().setAttLien(attributLien);

    String[] libEnteteAffiche = {"ID", "Personnel", "Type de Pointage", "Magasin", "Date", "Heure", "État"};
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

