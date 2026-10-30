<%@page import="mg.cnaps.compta.*"%>
<%@ page import="user.*" %>
<%@ page import="bean.*" %>
<%@ page import="utilitaire.*" %>
<%@ page import="affichage.*" %>
<%@ page import="faturefournisseur.ProformaAchatFilleLib" %>

<%
    try{
        ProformaAchatFilleLib t = new ProformaAchatFilleLib();
        t.setNomTable("PROFORMA_ACHAT_FILLELIB");
        String[] listeCrt = {};
        String[] listeInt = {};
        String libEntete[] = {"id","idmere","daty","idFournisseurLib","idProduitLib","designation","qteDisponible","idUniteLib","pu","delaisLivraison","remarque"};
        PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 4, libEntete, libEntete.length);
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        String idDemandePrix = request.getParameter("id");
        pr.setAWhere(" AND idDemandePrix = '"+idDemandePrix+"'");
        pr.setLien((String) session.getValue("lien"));
        String[] colSomme = null;
        pr.setNpp(100);
        pr.creerObjetPage(libEntete, colSomme);

        String[] libEnteteAffiche = {"Id","Ref. Proforma","Date","Fournisseur","Produit","D&eacute;signation","Quantit&eacute; disponible","Unit&eacute;","Prix unitaire","D&eacute;lais (j)","Remarque"};
        pr.getTableau().setLibelleAffiche(libEnteteAffiche);
        pr.getTableau().setNameBoutton("G&eacute;n&eacute;rer demande d'achat");

        
        String [] lienTableau = {pr.getLien() + "?but=facturefournisseur/proformaachat/proformaachat-fiche.jsp"};
        String [] colonneLien = {"idmere"};
        String[] attributLien = {"id"};
        pr.getTableau().setLien(lienTableau);
        pr.getTableau().setColonneLien(colonneLien);
        pr.getTableau().setAttLien(attributLien);

        ProformaAchatFilleLib[] data = (ProformaAchatFilleLib[]) pr.getTableau().getData();
%>

<div class="box-body">
    <form action="<%= pr.getLien() + "?but=facturefournisseur/dmdachat/apresGenererDmdAchatParFournisseur.jsp"%>" method="post" >
        <input type="hidden" name="acte">
        <input type="hidden" name="idDemandePrix" value="<%=idDemandePrix%>">
        <% if(pr.getTableau().getHtmlWithCheckbox() != null){
//            out.println(pr.getTableau().getHtmlWithCheckbox());
        %>
        <div class="col-md-12 nopadding" data-apj-filter-root>
            <div class="col-md-12 nopadding">
                <div class="box table-box">
                    <div class="box-body table-responsive no-padding">
                        <div class="scroll-table-container" id="selectnonee">
                            <table align="center" border="0" cellpadding="3" cellspacing="3" class="table table-hover table-striped" data-apj-filter-table id="table-apj" style="font-size: 14px;" width="100%">
                                <thead>
                                <tr class="head">
                                    <th align="center" class="contenuetable" valign="top">
                                        <input onclick="CocheToutCheckbox(this, 'ids')" type="checkbox"/>
                                    </th>
                                    <th data-apj-filter-column align="center" class="contenuetable" valign="top" width="9%">
                                        <a href="module.jsp?but=facturefournisseur/dmdachat/demandeprix-fiche.jsp&amp;numPag=1&amp;npp=20&amp;colonne=&amp;ordre=&amp;colAffiche1=id&amp;colAffiche2=idmere&amp;colAffiche3=daty&amp;colAffiche4=idFournisseurLib&amp;colAffiche5=idProduitLib&amp;colAffiche6=designation&amp;colAffiche7=qteDisponible&amp;colAffiche8=idUniteLib&amp;colAffiche9=pu&amp;colAffiche10=delaisLivraison&amp;colAffiche11=remarque&amp;triCol=yes&amp;newcol=id">
                                            Ref
                                        </a>
                                    </th>
                                    <th data-apj-filter-column align="center" class="contenuetable" valign="top" width="9%">
                                        <a href="module.jsp?but=facturefournisseur/dmdachat/demandeprix-fiche.jsp&amp;numPag=1&amp;npp=20&amp;colonne=&amp;ordre=&amp;colAffiche1=id&amp;colAffiche2=idmere&amp;colAffiche3=daty&amp;colAffiche4=idFournisseurLib&amp;colAffiche5=idProduitLib&amp;colAffiche6=designation&amp;colAffiche7=qteDisponible&amp;colAffiche8=idUniteLib&amp;colAffiche9=pu&amp;colAffiche10=delaisLivraison&amp;colAffiche11=remarque&amp;triCol=yes&amp;newcol=idmere">
                                            Ref. proforma
                                        </a>
                                    </th>
                                    <th data-apj-filter-column align="center" class="contenuetable" valign="top" width="9%">
                                        <a href="module.jsp?but=facturefournisseur/dmdachat/demandeprix-fiche.jsp&amp;numPag=1&amp;npp=20&amp;colonne=&amp;ordre=&amp;colAffiche1=id&amp;colAffiche2=idmere&amp;colAffiche3=daty&amp;colAffiche4=idFournisseurLib&amp;colAffiche5=idProduitLib&amp;colAffiche6=designation&amp;colAffiche7=qteDisponible&amp;colAffiche8=idUniteLib&amp;colAffiche9=pu&amp;colAffiche10=delaisLivraison&amp;colAffiche11=remarque&amp;triCol=yes&amp;newcol=daty">
                                            Date
                                        </a>
                                    </th>
                                    <th data-apj-filter-column align="center" class="contenuetable" valign="top" width="9%">
                                        <a href="module.jsp?but=facturefournisseur/dmdachat/demandeprix-fiche.jsp&amp;numPag=1&amp;npp=20&amp;colonne=&amp;ordre=&amp;colAffiche1=id&amp;colAffiche2=idmere&amp;colAffiche3=daty&amp;colAffiche4=idFournisseurLib&amp;colAffiche5=idProduitLib&amp;colAffiche6=designation&amp;colAffiche7=qteDisponible&amp;colAffiche8=idUniteLib&amp;colAffiche9=pu&amp;colAffiche10=delaisLivraison&amp;colAffiche11=remarque&amp;triCol=yes&amp;newcol=idFournisseurLib">
                                            Fournisseur
                                        </a>
                                    </th>
                                    <th data-apj-filter-column align="center" class="contenuetable" valign="top" width="9%">
                                        <a href="module.jsp?but=facturefournisseur/dmdachat/demandeprix-fiche.jsp&amp;numPag=1&amp;npp=20&amp;colonne=&amp;ordre=&amp;colAffiche1=id&amp;colAffiche2=idmere&amp;colAffiche3=daty&amp;colAffiche4=idFournisseurLib&amp;colAffiche5=idProduitLib&amp;colAffiche6=designation&amp;colAffiche7=qteDisponible&amp;colAffiche8=idUniteLib&amp;colAffiche9=pu&amp;colAffiche10=delaisLivraison&amp;colAffiche11=remarque&amp;triCol=yes&amp;newcol=idProduitLib">
                                            Produit
                                        </a>
                                    </th>
                                    <th data-apj-filter-column align="center" class="contenuetable" valign="top" width="9%">
                                        <a href="module.jsp?but=facturefournisseur/dmdachat/demandeprix-fiche.jsp&amp;numPag=1&amp;npp=20&amp;colonne=&amp;ordre=&amp;colAffiche1=id&amp;colAffiche2=idmere&amp;colAffiche3=daty&amp;colAffiche4=idFournisseurLib&amp;colAffiche5=idProduitLib&amp;colAffiche6=designation&amp;colAffiche7=qteDisponible&amp;colAffiche8=idUniteLib&amp;colAffiche9=pu&amp;colAffiche10=delaisLivraison&amp;colAffiche11=remarque&amp;triCol=yes&amp;newcol=designation">
                                            D&eacute;signation
                                        </a>
                                    </th>
                                    <th data-apj-filter-column align="center" class="contenuetable" valign="top" width="9%">
                                        <a href="module.jsp?but=facturefournisseur/dmdachat/demandeprix-fiche.jsp&amp;numPag=1&amp;npp=20&amp;colonne=&amp;ordre=&amp;colAffiche1=id&amp;colAffiche2=idmere&amp;colAffiche3=daty&amp;colAffiche4=idFournisseurLib&amp;colAffiche5=idProduitLib&amp;colAffiche6=designation&amp;colAffiche7=qteDisponible&amp;colAffiche8=idUniteLib&amp;colAffiche9=pu&amp;colAffiche10=delaisLivraison&amp;colAffiche11=remarque&amp;triCol=yes&amp;newcol=qteDisponible">
                                            Quantit&eacute; disponible
                                        </a>
                                    </th>
                                    <th data-apj-filter-column align="center" class="contenuetable" valign="top" width="9%">
                                        <a href="module.jsp?but=facturefournisseur/dmdachat/demandeprix-fiche.jsp&amp;numPag=1&amp;npp=20&amp;colonne=&amp;ordre=&amp;colAffiche1=id&amp;colAffiche2=idmere&amp;colAffiche3=daty&amp;colAffiche4=idFournisseurLib&amp;colAffiche5=idProduitLib&amp;colAffiche6=designation&amp;colAffiche7=qteDisponible&amp;colAffiche8=idUniteLib&amp;colAffiche9=pu&amp;colAffiche10=delaisLivraison&amp;colAffiche11=remarque&amp;triCol=yes&amp;newcol=qteDisponible">
                                            Quantit&eacute; demand&eacute;
                                        </a>
                                    </th>
                                    <th data-apj-filter-column align="center" class="contenuetable" valign="top" width="9%">
                                        <a href="module.jsp?but=facturefournisseur/dmdachat/demandeprix-fiche.jsp&amp;numPag=1&amp;npp=20&amp;colonne=&amp;ordre=&amp;colAffiche1=id&amp;colAffiche2=idmere&amp;colAffiche3=daty&amp;colAffiche4=idFournisseurLib&amp;colAffiche5=idProduitLib&amp;colAffiche6=designation&amp;colAffiche7=qteDisponible&amp;colAffiche8=idUniteLib&amp;colAffiche9=pu&amp;colAffiche10=delaisLivraison&amp;colAffiche11=remarque&amp;triCol=yes&amp;newcol=idUniteLib">
                                            Unit&eacute;
                                        </a>
                                    </th>
                                    <th data-apj-filter-column align="center" class="contenuetable" valign="top" width="9%">
                                        <a href="module.jsp?but=facturefournisseur/dmdachat/demandeprix-fiche.jsp&amp;numPag=1&amp;npp=20&amp;colonne=&amp;ordre=&amp;colAffiche1=id&amp;colAffiche2=idmere&amp;colAffiche3=daty&amp;colAffiche4=idFournisseurLib&amp;colAffiche5=idProduitLib&amp;colAffiche6=designation&amp;colAffiche7=qteDisponible&amp;colAffiche8=idUniteLib&amp;colAffiche9=pu&amp;colAffiche10=delaisLivraison&amp;colAffiche11=remarque&amp;triCol=yes&amp;newcol=pu">
                                            Prix unitaire
                                        </a>
                                    </th>
                                    <th data-apj-filter-column align="center" class="contenuetable" valign="top" width="9%">
                                        <a href="module.jsp?but=facturefournisseur/dmdachat/demandeprix-fiche.jsp&amp;numPag=1&amp;npp=20&amp;colonne=&amp;ordre=&amp;colAffiche1=id&amp;colAffiche2=idmere&amp;colAffiche3=daty&amp;colAffiche4=idFournisseurLib&amp;colAffiche5=idProduitLib&amp;colAffiche6=designation&amp;colAffiche7=qteDisponible&amp;colAffiche8=idUniteLib&amp;colAffiche9=pu&amp;colAffiche10=delaisLivraison&amp;colAffiche11=remarque&amp;triCol=yes&amp;newcol=delaisLivraison">
                                            D&eacute;lais (j)
                                        </a>
                                    </th>
                                    <th data-apj-filter-column align="center" class="contenuetable" valign="top" width="9%">
                                        <a href="module.jsp?but=facturefournisseur/dmdachat/demandeprix-fiche.jsp&amp;numPag=1&amp;npp=20&amp;colonne=&amp;ordre=&amp;colAffiche1=id&amp;colAffiche2=idmere&amp;colAffiche3=daty&amp;colAffiche4=idFournisseurLib&amp;colAffiche5=idProduitLib&amp;colAffiche6=designation&amp;colAffiche7=qteDisponible&amp;colAffiche8=idUniteLib&amp;colAffiche9=pu&amp;colAffiche10=delaisLivraison&amp;colAffiche11=remarque&amp;triCol=yes&amp;newcol=remarque">
                                            Remarque
                                        </a>
                                    </th>
                                </tr>
                                </thead>
                                <tbody>
                                <% for (int i = 0; i < data.length; i++) { %>
                                <tr onmouseout="this.style.backgroundColor=''" onmouseover="this.style.backgroundColor='#EAEAEA'" style="">
                                    <td align="center"><input id="checkbox<%= i %>" name="ids" type="checkbox" value="<%= data[i].getId() %>"/></td>
                                    <td align="left" width="9%"><%= data[i].getId() %></td>
                                    <td align="left" width="9%"><a href="module.jsp?but=facturefournisseur/proformaachat/proformaachat-fiche.jsp&id=<%= data[i].getIdMere() %>"><%= data[i].getIdMere() %> </a></td>
                                    <td align="left" width="9%"><%= data[i].getDaty() %></td>
                                    <td align="left" width="9%"><%= data[i].getIdFournisseurLib() %></td>
                                    <td align="left" width="9%"><%= data[i].getIdProduitLib() %></td>
                                    <td align="left" width="9%"><%= data[i].getDesignation() %></td>
                                    <td align="left" width="9%"><%= data[i].getQteDisponible() %></td>
                                    <td align="left" width="9%"><input class="form-control" id="qteDemande<%= i %>" name="qteDemande_<%= data[i].getId() %>" type="number" value="<%= data[i].getQteDisponible() %>"/></td>
                                    <td align="left" width="9%"><%= data[i].getIdUniteLib() %></td>
                                    <td align="left" width="9%"><%= data[i].getPu() %></td>
                                    <td align="left" width="9%"><%= data[i].getDelaisLivraison() %></td>
                                    <td align="left" width="9%"><%= Utilitaire.champNull(data[i].getRemarque()) %></td>
                                </tr>
                                <% } %>
                                </tbody>
                            </table>
                            <%= pr.getTableau().getHtmlDataTableApj() %>
                            <%= pr.getTableau().getJsDataTableApj() %>
                        </div>
                    </div>
                    <div class="box-footer">
                        <button class="btn btn-secondary pull-right" name="Submit2" onclick="acte.value='validerMultiple'" style="margin-right: 8px;" type="submit">
                            G&eacute;n&eacute;rer demande d'achat
                        </button>
                    </div>
                </div>
            </div>
        </div>
        <%

        }else { %>
        <div style="text-align: center;"><h4>Aucune donn&eacute;e trouv&eacute;e</h4></div>
        <% } %>
    </form>
    <%
        out.println(pr.getBasPage());
    %>
</div>
<%=pr.getModalHtml("modalContent")%>
<%
    } catch (Exception e) {
        e.printStackTrace();
    }%>
