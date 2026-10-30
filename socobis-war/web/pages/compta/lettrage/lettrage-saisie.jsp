<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="mg.cnaps.compta.*"%>
<%@page import="affichage.Liste"%>
<%@page import="bean.TypeObjet"%>
<%@page import="user.UserEJB"%>
<%@page import="affichage.*"%>
<%@page import="mg.cnaps.configuration.Configuration"%>
<%@page import="utilitaire.Utilitaire"%>
<%@page import="bean.CGenUtil"%>
<%@ page import="com.google.gson.Gson" %>

<%
    try{
        UserEJB u = (UserEJB) session.getAttribute("u");

        ComptaSousEcritureLib  a = new ComptaSousEcritureLib ();
        a.setNomTable("COMPTA_SOUSECRITURE_LETTRE2");

        String listeCrt[] = {"id","daty","exercice", "journal","ecriture","compte"};
        String listeInt[] = {"daty"};

        String libEntete[] = { "id","daty","exercice","compte","journal","ecriture","debit","credit"};

        PageRechercheChoix pr = new PageRechercheChoix(a, request, listeCrt, listeInt, 3, libEntete, libEntete.length,true);
        pr.setPreciserChoix(true);
        System.out.println(pr.getPremier());

        // pr.getFormu().getChamp("compte").setAutre("required");
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setLien((String) session.getValue("lien"));
        pr.setApres("compta/lettrage/lettrage-saisie.jsp");
        pr.setAWhere(" AND LETTRAGE is NULL and (COMPTE LIKE '4%') AND (COMPTE NOT LIKE '445%')");

        pr.getFormu().getChamp("daty1").setLibelle("Date Min");
        pr.getFormu().getChamp("daty2").setLibelle("Date Max");
        //pr.getFormu().getChamp("numero").setLibelle("Libelle");
        pr.getFormu().getChamp("exercice").setDefaut(Utilitaire.getAnneeEnCours());

        String[] colSomme = null;

        pr.setNpp(9000);

        pr.creerObjetPage(libEntete, colSomme);

        int nombreLigne = pr.getTableau().getData().length;
        String nomtable = "compta_lettrage";

        Configuration[] lettrageParram = null;
        lettrageParram = (Configuration[]) CGenUtil.rechercher(new Configuration(), null, null, " AND TYPECONFIG = 'lettrage'");
        System.out.println("json "+lettrageParram[0].getRemarque());
        char[] l = lettrageParram[0].getRemarque().toLowerCase().toCharArray();
        String lettre = Utilitaire.incrementLettre(l);
        System.out.println("lettre" +lettre);
%>
<div class="content-wrapper">
    <section class="content-header">
        <h1>Lettrage</h1>
    </section>
    <section class="content">
        <form action="<%=pr.getLien()%>?but=compta/lettrage/lettrage-saisie.jsp" method="post" name="incident" id="incident">
            <%
                out.println(pr.getFormu().getHtmlEnsemble());
            %>
            <input name="premier" type="hidden"  value="false">
        </form>
        <% if (!pr.getPremier()) {%>
        <form action="<%=pr.getLien()%>?but=compta/lettrage/apresLettrage.jsp" id='modif-form' method="post" data-parsley-validate>
            <div class="result mt-5 table-result">
                <div class="row" >
                    <div class="col-md-3 mb-5">
                        <label class="input-label" for="lettreInitial">Lettre</label>
                        <input class="form-control" id="lettreInitial" name="lettreInitial" type="text" readonly value="<%=lettre.toUpperCase()%>" >
                    </div>
                    <div class="col-md-3"  >
                        <label>Total D&eacute;bit</label>
                        <input class="form-control montant" id="totalDebit" name="totalDebit" readonly value="0" type="text">
                    </div>
                    <div class="col-md-3">
                        <label for="totalCredit" class="input-label">Total Cr&eacute;dit</label>
                        <input class="form-control montant" id="totalCredit" name="totalCredit" readonly value="0" type="text">
                    </div>
                    <div class="col-md-3">
                        <label class="input-label" for="ecart" >&Eacute;cart</label>
                        <input class="form-control" id="ecart" name="ecart" type="text" readonly value="0" readonly>
                    </div>
                </div>
                <%
                    String libEnteteAffiche[] = {"ID","Date","Exercice","Compte","Journal","D&eacute;signation","D&eacute;bit","Cr&eacute;dit"};
                    pr.getTableau().setLibelleAffiche(libEnteteAffiche);
                    pr.getTableau().setNameBoutton("Lettrer");
                    pr.getTableau().setNameActe("insertMereLierFille");
                    pr.getTableau().setNameActe2("insertMereLierFille");
                    out.println(pr.getTableau().getHtmlWithCheckbox());
                %>
            </div>
            <input name="acte" type="hidden" id="acte" value="insertMereLierFille">
            <input name="bute" type="hidden" id="bute" value="compta/lettrage/lettrage-saisie.jsp">
            <input name="classe" type="hidden" id="classe" value="mg.cnaps.compta.ComptaLettrage">
            <input name="nombreLigne" type="hidden" id="nombreLigne" value="<%=nombreLigne%>">
            <input name="colonneMere" type="hidden" id="colonneMere" value="id"/>
            <input name="colonneFille" type="hidden" id="colonneFille" value="lettrage"/>
            <input name="classeFille" type="hidden" id="classe" value="mg.cnaps.compta.ComptaEcriture"/>
            <input name="nomtable" type="hidden" id="nomtable" value="<%=nomtable%>">
            <input name="lettre" type="hidden" id="lettre" value="<%=lettre%>">

        </form>
        <% } %>
    </section>
</div>


<%}catch(Exception e){
    e.printStackTrace();
}%>

<script>
    document.addEventListener("DOMContentLoaded", function () {

        function parseMontant(text) {
            if (!text) return 0;
            return parseFloat(
                text.replace(/\u00a0/g, '')
                    .replace(/\s/g, '')
            ) || 0;
        }

        window.recalculerTotaux = function () {
            let totalDebit = 0;
            let totalCredit = 0;

            document.querySelectorAll('input[type="checkbox"][name="ids"]:checked')
                .forEach(cb => {
                    const tr = cb.closest("tr");
                    const tds = tr.querySelectorAll("td");

                    const debitText  = tds[7]?.innerText || "0";
                    const creditText = tds[8]?.innerText || "0";

                    totalDebit  += parseMontant(debitText);
                    totalCredit += parseMontant(creditText);
                });

            const ecart = totalDebit - totalCredit;

            document.getElementById("totalDebit").value  = totalDebit.toLocaleString('fr-FR');
            document.getElementById("totalCredit").value = totalCredit.toLocaleString('fr-FR');
            document.getElementById("ecart").value       = ecart.toLocaleString('fr-FR');
        };

        document.addEventListener("change", function (e) {
            if (e.target.matches('input[type="checkbox"][name="ids"]')) {
                recalculerTotaux();
            }
        });

        if (typeof window.CocheToutCheckbox === "function") {
            const originalCocheTout = window.CocheToutCheckbox;

            window.CocheToutCheckbox = function () {
                originalCocheTout.apply(this, arguments);
                recalculerTotaux();
            };
        }

    });
</script>
