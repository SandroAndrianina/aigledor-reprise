<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="java.util.Locale" %>
<%@page import="java.util.ResourceBundle" %>
<%@page import="bean.CGenUtil" %>
<%@page import="utilitaire.Utilitaire" %>
<%@ page import="java.io.*" %>
<%@ page import="annexe.Point" %>
<%@ page import="utils.ConstanteAsync" %>
<%@ page import="caisse.Caisse" %>
<%@ page import="affichage.Liste" %>

<% String but = "";
    String queryString = "";
    Point[] points = null;
    Caisse[] caisses = null;
    try {
        queryString = request.getQueryString();
        but = "pages/testLogin.jsp";
        if (queryString != null && !queryString.equals("")) {
            but += "?" +
                    queryString;
        }
        points = Point.getListePoint();
        caisses = Caisse.getListeCaisseEspece();
    } catch (Exception ex) { %>
<script language="JavaScript">
    alert("<%=ex.getMessage()%>");
    document.location.replace("../index.jsp");
</script>


</body>
<% } %>
<script src="${pageContext.request.contextPath}/assets/js/password-toggle.js"></script>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        var pointSelect = document.getElementById("idPoint");
        var caisseSelect = document.getElementById("idCaisse");

        if (!pointSelect || !caisseSelect) {
            console.error("idPoint ou idCaisse introuvable");
            return;
        }

        // Sauvegarder toutes les caisses générées par JSP.
        var toutesLesCaisses = [];

        for (var i = 0; i < caisseSelect.options.length; i++) {
            var option = caisseSelect.options[i];
            var idPointCaisse = option.getAttribute("data-id-point");

            if (idPointCaisse !== null) {
                toutesLesCaisses.push({
                    id: option.value.trim(),
                    libelle: option.textContent.trim(),
                    idPoint: idPointCaisse.trim()
                });
            }
        }

        function filtrerCaisses() {
            var idPoint = pointSelect.value.trim();
            var caissesDuPoint = [];
            var idsCaisses = [];

            // Vider la liste.
            caisseSelect.options.length = 0;
            caisseSelect.add(
                new Option("Sélectionner une caisse", "")
            );

            if (idPoint === "") {
                caisseSelect.disabled = true;
                return;
            }

            caisseSelect.disabled = false;

            // Rechercher les caisses du point.
            for (var i = 0; i < toutesLesCaisses.length; i++) {
                if (toutesLesCaisses[i].idPoint === idPoint) {
                    caissesDuPoint.push(toutesLesCaisses[i]);
                    idsCaisses.push(toutesLesCaisses[i].id);
                }
            }

            if (caissesDuPoint.length === 0) {
                var optionVide = new Option(
                    "Aucune caisse pour ce point",
                    ""
                );

                optionVide.disabled = true;
                caisseSelect.add(optionVide);
                return;
            }

            // Ajouter l’option Tous.
            var valeurTous = "%";

            caisseSelect.add(
                new Option("Tous", valeurTous, true, true)
            );

            // Ajouter les caisses individuelles.
            for (var j = 0; j < caissesDuPoint.length; j++) {
                caisseSelect.add(
                    new Option(
                        caissesDuPoint[j].libelle,
                        caissesDuPoint[j].id
                    )
                );
            }

            caisseSelect.value = valeurTous;
        }

        pointSelect.addEventListener("change", filtrerCaisses);

        // Filtrer au chargement avec le point présélectionné.
        filtrerCaisses();
    });

    document.addEventListener("DOMContentLoaded", function () {
        const passwordInput = document.getElementById("password-input");
        const toggleButton = document.getElementById("toggle-password");

        if (!passwordInput || !toggleButton) return;

        toggleButton.addEventListener("click", function () {
            const passwordVisible = passwordInput.type === "text";

            passwordInput.type = passwordVisible ? "password" : "text";

            const label = passwordVisible
                ? "Afficher le mot de passe"
                : "Masquer le mot de passe";

            toggleButton.setAttribute("aria-label", label);
            toggleButton.setAttribute("title", label);
            toggleButton.setAttribute("aria-pressed", String(!passwordVisible));
        });
    });
</script>

<!DOCTYPE html>
<html lang="fr">

<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Connexion</title>

    <link href="${pageContext.request.contextPath}/assets/css/refontlogin.css"
          rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/apj-global-style.css"
          rel="stylesheet">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
</head>
<style>
    .form .field {
        display: block;
        width: 100%;
    }

    .form .field .input-wrap {
        width: 100%;
        max-width: 100%;
        grid-template-columns: 34px minmax(0, 1fr) 34px;
    }

    .form .field .input-wrap > select {
        grid-column: 2 / -1;
        display: block;
        width: 100%;
        min-width: 0;
        max-width: 100%;
        box-sizing: border-box;
    }
</style>
<body>
<div class="page">
    <div class="frame">
        <!-- Left / Form -->
        <section class="panel-left">
            <!-- miova icon de projet -->
            <header class="brand" style="text-align: center; margin-top: 70px;">
                <img class="logo"
                     src="${pageContext.request.contextPath}/assets/img/logo_socobis.png"
                     alt="Logo">
            </header>
            <!-- <img class="logo" src="${pageContext.request.contextPath}/assets/img/Logo-Async-Footer.png" alt="Logo Async"> -->
            <main class="card">
                <h1>Connexion</h1>
                <p class="subtitle">Accédez à vos outils de gestion</p>

                <form class="form" action="<%=but%>" method="post">
                    <label class="field">
                        <span class="label">Identifiant</span>
                        <span class="input-wrap">
                                <span class="icon" aria-hidden="true">
                                    <!-- user icon -->
                                    <svg viewBox="0 0 24 24" width="18" height="18"
                                         fill="currentColor">
                                        <path
                                                d="M12 12a5 5 0 1 0-5-5 5 5 0 0 0 5 5zm0 2c-5.33 0-8 2.67-8 6a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1c0-3.33-2.67-6-8-6z" />
                                    </svg>
                                </span>
                                <input type="text" name="identifiant"
                                       placeholder="Entrez votre identifiant" required />
                            </span>
                    </label>

                    <label class="field">
                        <span class="label">Mot de passe</span>
                        <span class="input-wrap">
                                <span class="icon" aria-hidden="true">
                                    <!-- lock icon -->
                                    <svg viewBox="0 0 24 24" width="18" height="18"
                                         fill="currentColor">
                                        <path
                                                d="M17 10h-1V7a4 4 0 0 0-8 0v3H7a2 2 0 0 0-2 2v7a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-7a2 2 0 0 0-2-2zm-6 0V7a3 3 0 0 1 6 0v3z" />
                                    </svg>
                                </span>
                                <input id="password-input" type="password" name="passe"
                                       placeholder="Entrez votre mot de passe" required />
                                <button id="toggle-password" class="toggle-eye" type="button"
                                    aria-label="Afficher le mot de passe" aria-pressed="false"
                                    title="Afficher le mot de passe">
                                    <svg viewBox="0 0 24 24" width="18" height="18"
                                         fill="currentColor">
                                        <path
                                                d="M12 5C7 5 2.73 8.11 1 12c1.73 3.89 6 7 11 7s9.27-3.11 11-7c-1.73-3.89-6-7-11-7zm0 12a5 5 0 1 1 5-5 5 5 0 0 1-5 5zm0-8a3 3 0 1 0 3 3 3 3 0 0 0-3-3z" />
                                    </svg>
                                </button>
                            </span>
                    </label>

                    <label class="field">
                        <span class="label">Point</span>
                        <span class="input-wrap">
                            <span class="icon" aria-hidden="true">
                                <svg viewBox="0 0 24 24"
                                     width="18"
                                     height="18"
                                     fill="currentColor">
                                    <path
                                            d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5A2.5 2.5 0 1 1 12 6a2.5 2.5 0 0 1 0 5.5z"/>
                                </svg>
                            </span>
                            <select id="idPoint" name="idPoint" class="form-control borderless" style="background-color: var(--Background-primaire)" required>
                                <option value="">Sélectionner un Point</option>
                                <% if (points != null) {
                                    for (int i = 0; i < points.length; i++) { %>
                                        <option
                                                value="<%= points[i].getId() %>"
                                                <%= points[i].getId().equals(ConstanteAsync.MAGASIN)
                                                        ? "selected" : "" %>>
                                            <%= points[i].getVal() %>
                                        </option>
                                <%  }
                                } %>
                            </select>
                        </span>
                    </label>
                    <label class="field">
                        <span class="label">Caisse</span>
                        <span class="input-wrap">
                        <span class="icon" aria-hidden="true">
                            <svg viewBox="0 0 24 24"
                                 width="18"
                                 height="18"
                                 fill="currentColor">
                                <path
                                        d="M19 2H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2H3v2h18v-2h-2a2 2 0 0 0 2-2V4a2 2 0 0 0-2-2zm0 16H5V4h14v14zM6 6h12v5H6V6zm0 7h3v3H6v-3zm5 0h3v3h-3v-3zm5 0h2v3h-2v-3z"/>
                            </svg>
                        </span>
                        <select id="idCaisse" name="idCaisse" class="form-control borderless" style="background-color: var(--Background-primaire)" >
                            <option value="">Sélectionner une caisse</option>
                            <% if (caisses != null) {
                                for (int i = 0; i < caisses.length; i++) { %>
                                    <option
                                            value="<%= caisses[i].getId() %>"
                                            data-id-point="<%= caisses[i].getIdPoint() %>">
                                        <%= caisses[i].getVal() %>
                                    </option>
                            <%  }
                            } %>
                        </select>
                    </span>
                </label>
                    <!-- <button class="btn-primary" type="submit">Se connecter</button> -->
                    <button class="btn-primary" id="login-btn" type="submit">
                        <span id="login-btn-text">Se connecter</span>
                        <!-- <span id="login-btn-loader"
                            style="display:none;vertical-align:middle;margin-left:8px;">
                            <svg width="22" height="22" viewBox="0 0 50 50">
                                <circle cx="25" cy="25" r="20" fill="none"
                                    stroke="#fff" stroke-width="5"
                                    stroke-linecap="round"
                                    stroke-dasharray="31.4 31.4"
                                    stroke-dashoffset="0">
                                    <animateTransform attributeName="transform"
                                        type="rotate" from="0 25 25" to="360 25 25"
                                        dur="0.8s" repeatCount="indefinite" />
                                </circle>
                            </svg>
                        </span> -->
                    </button>
                </form>
            </main>

            <footer class="footer">
                <img class="logo"
                     src="${pageContext.request.contextPath}/assets/img/Logo-Async-Footer.png"
                     alt="Logo Async">
                <span class="by">by BICI</span>
            </footer>
        </section>

        <!-- Right / Illustration -->
        <aside class="panel-right" aria-hidden="true">
            <div class="image">
            </div>
        </aside>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/js/password-toggle.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/pos/pos-select-keyboard.js?v=85"></script>
<script>
    window.POSPageKeyboardConfig = {
        mode: 'form',
        formSelector: '.form'
    };
</script>
<script src="${pageContext.request.contextPath}/assets/js/pos/pos-page-keyboard.js?v=controller-1"></script>
<script src="${pageContext.request.contextPath}/assets/js/apj-keyboard.js?v=default-off-1"></script>
</body>

</html>
<!-- script loader -->
<!-- <script>
document.addEventListener('DOMContentLoaded', function () {
var form = document.querySelector('.form');
var btn = document.getElementById('login-btn');
var btnText = document.getElementById('login-btn-text');
var btnLoader = document.getElementById('login-btn-loader');
if (form && btn) {
form.addEventListener('submit', function (e) {
btn.disabled = true;
btn.style.background = '#bdbdbd';
btnText.style.opacity = '0.6';
btnLoader.style.display = 'inline-block';
});
}
});
</script> -->
