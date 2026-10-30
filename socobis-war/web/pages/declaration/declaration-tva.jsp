<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="affichage.PageInsert"%>
<%@ page import="user.UserEJB" %>
<%@ page import="declaration.DeclarationTva" %>
<%@ page import="java.sql.Date" %>
<%@ page import="utilitaire.Utilitaire" %>
<%@ page import="java.time.LocalDate" %>

<% try{ 
    UserEJB u = (user.UserEJB) session.getValue("u");
    String mapping = "declaration.DeclarationTva";
    String nomTable = "DECLARATIONTVA";
    String apres = "declaration/tva-deductible-adeclarer.jsp";

    DeclarationTva o = new DeclarationTva();
    o.setNomTable("DECLARATIONTVA");
    PageInsert pi = new PageInsert(o, request, u);
    pi.setLien((String) session.getValue("lien"));  
    pi.setTitre("Saisie d'une d&eacute;claration Tva");

    pi.getFormu().getChamp("designation").setLibelle("D&eacute;signation");
    pi.getFormu().getChamp("datydebut").setLibelle("Date de d&eacute;but");
    pi.getFormu().getChamp("datyfin").setLibelle("Date de fin");
    pi.getFormu().getChamp("etat").setVisible(false);

    LocalDate today = LocalDate.now();
    LocalDate firstDayPrevMonth = today.minusMonths(1).withDayOfMonth(1);
    LocalDate lastDayPrevMonth  = today.minusMonths(1).withDayOfMonth(
        today.minusMonths(1).lengthOfMonth()
    );
    Date debut = Date.valueOf(firstDayPrevMonth);
    Date fin   = Date.valueOf(lastDayPrevMonth);
    pi.getFormu().getChamp("datydebut").setDefaut(Utilitaire.datetostring(debut));
    pi.getFormu().getChamp("datyfin").setDefaut(Utilitaire.datetostring(fin));


    String[] ordre = {"designation","datydebut","datyfin"};
    pi.getFormu().setOrdre(ordre);
 
    String acte = request.getParameter("acte");
    if(acte != null && acte.equalsIgnoreCase("update")){
        pi.setTitre("Modification d'une d&eacute;claration Tva");
    }

    pi.preparerDataFormu();
    pi.getFormu().makeHtmlInsertTabIndex();
%>

<div class="content-wrapper">
    <h1><%=pi.getTitre()%></h1>
    <form action="<%=pi.getLien()%>?but=declaration/tva-deductible-adeclarer.jsp" method="post" name="<%=nomTable%>" id="<%=nomTable%>">
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

