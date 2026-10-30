<%--
  Created by IntelliJ IDEA.
  User: Tsinjoniaina
  Date: 29/07/2026
  Time: 18:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="user.*" %>

<%
    try{
        String lien = (String) session.getValue("lien");

        String point = session.getAttribute("idPoint").toString();

        UserEJB user = (UserEJB) session.getValue("u");
%>

<style>

    .content-wrapper .box {
        width: 100%;
        max-width: 522px;
        margin: 0 auto;
    }

    .content-wrapper .box-body {
        display: flex;
        flex-direction: column;
        align-items: center;
        text-align: center;
        gap: 20px;
        padding: 24px;
    }

    .content-wrapper .scc-header {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 12px;
    }

    .content-wrapper .scc-title {
        margin: 0;
    }

    .content-wrapper .scc-description {
        margin: 0;
        max-width: 420px;
    }

    .content-wrapper .scc-actions {
        display: flex;
        justify-content: center;
        gap: 12px;
        width: 100%;
    }

    .content-wrapper .material-symbols-rounded.spin {
        animation: scc-spin 1s linear infinite;
    }

    @keyframes scc-spin {
        from { transform: rotate(0deg); }
        to { transform: rotate(360deg); }
    }
</style>

<div class="content-wrapper">
    <div class="box">
        <div class="box-body">
            <div class="scc-header">
                <h2 class="scc-title h329pxBold">Synchronisation des donn&eacute;es</h2>
                <p class="scc-description h617pxRegular">
                    Voulez-vous <strong>synchroniser vos donn&eacute;es</strong> maintenant ?
                    Cette op&eacute;ration met &agrave; jour les donn&eacute;es entre les serveurs.
                </p>
            </div>

            <div id="synchroStatusArea" style="width: 100%; margin-top: 10px; display: none;">
            </div>

            <div class="scc-actions box-footer borderless">
                <button type="button" class="btn btn-primary pull-right" id="btnSynchroniser" onclick="lancerSynchronisation()">
                    <span class="material-symbols-rounded" id="btnSynchroniserIcon">sync</span>
                    <span id="btnSynchroniserLabel">Synchroniser</span>
                </button>
            </div>
        </div>

    </div>
</div>

<script>
    function lancerSynchronisation() {
        var btn = document.getElementById('btnSynchroniser');
        var icon = document.getElementById('btnSynchroniserIcon');
        var label = document.getElementById('btnSynchroniserLabel');
        var statusArea = document.getElementById('synchroStatusArea');

        // Clear any previous status message when starting
        statusArea.style.display = 'none';
        statusArea.innerHTML = '';

        // Reset states and styles
        btn.disabled = true;
        icon.classList.add('spin');
        icon.textContent = 'sync';
        label.textContent = 'Synchronisation en cours...';

        // Perform AJAX call to SynchronisationServlet
        fetch('${pageContext.request.contextPath}/SynchronisationServlet', {
            method: 'POST'
        })
        .then(function(response) {
            return response.json().then(function(data) {
                if (response.ok && data.success) {
                    marquerSynchronisationTerminee(true, data.message);
                } else {
                    marquerSynchronisationTerminee(false, data.message || "Une erreur est survenue lors de la synchronisation.");
                }
            });
        })
        .catch(function(error) {
            console.error("Erreur de synchronisation:", error);
            marquerSynchronisationTerminee(false, "Impossible de contacter le serveur ou de terminer la synchronisation.");
        });
    }

    function marquerSynchronisationTerminee(succes, message) {
        var btn = document.getElementById('btnSynchroniser');
        var icon = document.getElementById('btnSynchroniserIcon');
        var label = document.getElementById('btnSynchroniserLabel');
        var statusArea = document.getElementById('synchroStatusArea');

        icon.classList.remove('spin');

        // Show status area for final feedback
        statusArea.style.display = 'block';

        if (succes) {
            icon.textContent = 'check_circle';
            label.innerHTML = 'Donn&eacute;es synchronis&eacute;es';
            btn.disabled = true;

            // Show gorgeous green success alert box
            statusArea.innerHTML = 
                '<div class="alert alert-success" style="display: flex; align-items: center; gap: 15px; text-align: left; margin-bottom: 0; padding: 15px; border-radius: 4px;">' +
                    '<span class="material-symbols-rounded" style="font-size: 28px;">check_circle</span>' +
                    '<div>' +
                        '<strong style="font-size: 15px; display: block; margin-bottom: 4px;">Succ&egrave;s !</strong>' +
                        '<span style="font-size: 13px; opacity: 0.9;">' + message + '</span>' +
                    '</div>' +
                '</div>';
        } else {
            icon.textContent = 'error';
            label.innerHTML = 'R&eacute;essayer la synchronisation';
            btn.disabled = false;

            // Show beautiful error alert box
            statusArea.innerHTML =
                '<div class="alert alert-danger" style="display: flex; align-items: center; gap: 15px; text-align: left; margin-bottom: 0; padding: 15px; border-radius: 4px;">' +
                    '<span class="material-symbols-rounded" style="font-size: 28px;">error</span>' +
                    '<div>' +
                        '<strong style="font-size: 15px; display: block; margin-bottom: 4px;">&Eacute;chec de la synchronisation</strong>' +
                        '<span style="font-size: 13px; opacity: 0.9;">' + message + '</span>' +
                    '</div>' +
                '</div>';
        }
    }
</script>
<%
    }catch(Exception e){
        e.printStackTrace();
    }
%>