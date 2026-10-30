<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="user.UserEJB" %>
<%@ page import="affichage.PageConsulte" %>
<%@ page import="annexe.src.java.rh.pointage.PointageMagasinierLib" %>
<%@ page import="constante.ConstanteEtat" %>
<%@ page import="annexe.src.java.rh.pointage.PointageMagasinier" %>

<% try{
    UserEJB u = (user.UserEJB)session.getValue("u");
    String lien = (String) session.getValue("lien");

    PointageMagasinierLib o = new PointageMagasinierLib();
    o.setNomTable("POINTAGE_MAGASINIER_LIB");
    PageConsulte pc = new PageConsulte(o, request, u);
    pc.setTitre("Fiche de pointage ");
    String id = pc.getBase().getTuppleID();
    PointageMagasinier pm = new PointageMagasinier();
    pm = (PointageMagasinier) pm.getById(id, "POINTAGE_MAGASINIER",null);
    int etatActuel = pm.getEtat();

    pc.getChampByName("id").setLibelle("ID");
    pc.getChampByName("idPointLib").setLibelle("Magasin");
    pc.getChampByName("idPersonnel").setLibelle("ID Personnel");
    pc.getChampByName("idPersonnelLib").setLibelle("Personnel");
    pc.getChampByName("idTypePointageLib").setLibelle("Type de Pointage");
    pc.getChampByName("daty").setLibelle("Daty");
    pc.getChampByName("heure").setLibelle("Heure");
    pc.getChampByName("remarque").setLibelle("Remarque");
    pc.getChampByName("etatLib").setLibelle("Etat");
    pc.getChampByName("matriculePersonnel").setVisible(false);
    pc.getChampByName("idPoint").setVisible(false);
    pc.getChampByName("idTypePointage").setVisible(false);
    pc.getChampByName("etat").setVisible(false);
    pc.getChampByName("idPersonnel").setLien(lien+"paie/employe/personnel-fiche-portrait.jsp","id=");

    String[] ordre = {"id", "idPersonnel", "idPersonnelLib", "idTypePointageLib", "idPointLib", "daty", "heure", "remarque", "etatLib"};
    pc.setOrdre(ordre);

    String pageRetour = "paie/rh/pointage/pointagemagasinier-liste.jsp";
    String pageModif = "paie/rh/pointage/pointagemagasinier-saisie.jsp&acte=update";
    String pageApresDelete = "paie/rh/pointage/pointagemagasinier-liste.jsp";
    String classe = "annexe.src.java.rh.pointage.PointageMagasinierLib";
    String classeOg = "annexe.src.java.rh.pointage.PointageMagasinier";
%>

<div class="content-wrapper">

    <h1 class="box-title"><a href=<%= lien + "?but=" + pageRetour%>> <i class="fa fa-angle-left"></i></a><%=pc.getTitre()%></h1>

    <div class="row m-0">
        <div class="col-md-3"></div>
        <div class="col-md-6">
            <div class="box-fiche">
                <div class="box">
                    <div class="box-body">
                        <%
                            out.println(pc.getHtml());
                        %>
                        <br/>
                        <div class="box-footer">
                            <% if(etatActuel < ConstanteEtat.getEtatValider()){%>
                                <a class="btn btn-primary pull-right" href="<%= (String) session.getValue("lien") + "?but=apresTarif.jsp&acte=valider&id=" + request.getParameter("id") + "&bute=rh/pointage/pointagemagasinier-fiche.jsp&classe=" + classeOg %> " style="margin-right: 10px">Valider</a>
                            <% } %>
                            <a class="btn btn-warning pull-right"  href="<%= lien + "?but="+ pageModif +"&id=" + id %>" style="margin-right: 10px">Modifier</a>
                            <a  class="pull-right" href="<%= lien + "?but=apresTarif.jsp&id=" + id+"&acte=delete&bute="+pageApresDelete+"&classe="+classe %>"><button class="btn btn-danger">Supprimer</button></a>
                        </div>
                        <br/>
                    </div>
                </div>
            </div>
        </div>
    </div>

        <% } catch (Exception e) {
  e.printStackTrace();
%>
    <script language="JavaScript"> alert('<%=e.getMessage()%>');
    history.back();
    </script>
        <% }%>

