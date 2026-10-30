<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="affichage.PageInsert"%>
<%@ page import="user.UserEJB" %>
<%@ page import="annexe.src.java.rh.pointage.PointageMagasinier" %>
<%@ page import="affichage.Liste"%>
<%@ page import="annexe.Point" %>
<%@ page import="rh.pointage.TypePointage" %>
<%@ page import="utilitaire.Utilitaire" %>

<% try{
    UserEJB u = (user.UserEJB) session.getValue("u");
    String mapping = "annexe.src.java.rh.pointage.PointageMagasinier";
    String nomTable = "POINTAGE_MAGASINIER";
    String apres = "rh/pointage/pointagemagasinier-fiche.jsp";
    String idPoint = (String)session.getValue("idPoint");

    PointageMagasinier o = new PointageMagasinier();
    o.setNomTable("POINTAGE_MAGASINIER");
    PageInsert pi = new PageInsert(o, request, u);
    pi.setLien((String) session.getValue("lien"));
    pi.setTitre("Saisie d'un pointage");

    Liste[] liste = new Liste[1];
    TypePointage liste1 = new TypePointage();
    liste1.setNomTable("TYPEPOINTAGE");
    liste[0] = new Liste("idTypePointage",liste1,"val","id");
    pi.getFormu().changerEnChamp(liste);

    pi.getFormu().getChamp("idPersonnel").setPageAppelComplete("annexe.Point","id","POINT","id","idPoint");
    pi.getFormu().getChamp("idPoint").setLibelle("Magasin");
    pi.getFormu().getChamp("idPoint").setDefaut(idPoint);
    pi.getFormu().getChamp("idPersonnel").setLibelle("Personnel");
    pi.getFormu().getChamp("idTypePointage").setLibelle("Type de Pointage");
    pi.getFormu().getChamp("daty").setLibelle("Daty");
    pi.getFormu().getChamp("daty").setDefaut(Utilitaire.dateDuJour());
    pi.getFormu().getChamp("heure").setLibelle("Heure");
    pi.getFormu().getChamp("heure").setDefaut(Utilitaire.heureCourante());
    pi.getFormu().getChamp("remarque").setLibelle("Remarque");
    pi.getFormu().getChamp("idPoint").setAutre("readonly");
    pi.getFormu().getChamp("daty").setAutre("readonly");
    pi.getFormu().getChamp("heure").setAutre("readonly");
    pi.getFormu().getChamp("etat").setVisible(false);
    pi.getFormu().getChamp("idPoint").setVisible(false);
    pi.getFormu().getChamp("idPersonnel").setPageAppelComplete("personnel.Personnel","id","LOG_PERSONNEL_V2","id","idPersonnel");

    String[] ordre = {
            "idPersonnel",
            "idTypePointage",
            "remarque",
            "idPoint",
            "daty",
            "heure"
    };    pi.getFormu().setOrdre(ordre);

    String acte = request.getParameter("acte");
    if(acte != null && acte.equalsIgnoreCase("update")){
        pi.setTitre("Modification d'un pointage");
    }

    pi.preparerDataFormu();
    pi.getFormu().makeHtmlInsertTabIndex();
%>

<div class="content-wrapper">
    <h1><%=pi.getTitre()%></h1>
    <form action="<%=pi.getLien()%>?but=apresTarif.jsp" method="post" name="<%=nomTable%>" id="<%=nomTable%>">
        <%
            out.println(pi.getFormu().getHtmlInsert());
            out.println(pi.getHtmlAddOnPopup());
        %>
        <input name="acte" type="hidden" id="nature" value="insert">
        <input name="bute" type="hidden" id="bute" value="<%=apres%>">
        <input name="classe" type="hidden" id="classe" value="<%=mapping%>">
        <input name="nomtable" type="hidden" id="nomtable" value="<%=nomTable%>">
    </form>
</div>

<% } catch (Exception e) {
  e.printStackTrace();
%>
<script language="JavaScript"> alert('<%=e.getMessage()%>');
    history.back();
</script>
<% }%>

