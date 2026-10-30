<%-- 
    Document   : acceuil.jsp
    Created on : 30 mars 2023, 17:23:45
    Author     : Ny Anjara Mamisoa
--%>
<link href="${pageContext.request.contextPath}/assets/css/accueil.css" rel="stylesheet" type="text/css" />
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<div class="content-wrapper">
    
    <div class="row m-0">
        <div class="col-md-12 nopadding">
            <h1 class="title-service h142pxBold m-0">Bienvenue sur votre espace de gestion</h1>
            <p class="desc-service h617pxRegular">Pilotez vos ventes, gérez vos stocks et optimisez vos opérations depuis une seule plateforme, simplement et efficacement.</p>
        </div>
        <div class="col-md-12 nopadding card-wrapper">

            <a href="module.jsp?but=dashboard-opt.jsp" class="card col-md-4 vert">
                <i class="fa-solid fa-arrow-right arrow"></i>
                <img src="${pageContext.request.contextPath}/assets/img/chart.png" class="icon"/>
                <div class="card-content">
                    <h3 class="h617pxSemibold">Dashboard</h3>
                    <p class="Body14pxRegular">
                        Visualisez les indicateurs cl&eacute;s de performance de vos points.
                    </p>
                </div>
            </a>
<%--            <!-- ORDRE DE FABRICATION -->--%>
<%--            <a href="module.jsp?but=fabrication/ordre-fabrication-liste.jsp" class="card col-md-4 violet">--%>
<%--                <i class="fa-solid fa-arrow-right arrow"></i>--%>
<%--                <img src="${pageContext.request.contextPath}/assets/img/ordre-de-fabrication.png" class="icon"/>--%>
<%--                <div class="card-content">--%>
<%--                    <h3 class="h617pxSemibold">Ordre de fabrication</h3>--%>
<%--                    <p class="Body14pxRegular">--%>
<%--                        Cr&eacute;ez et suivez vos ordres de fabrication en toute simplicit&eacute;.--%>
<%--                    </p>--%>
<%--                </div>--%>
<%--            </a>--%>

            <!-- Client -->
            <a href="module.jsp?but=client/client-liste.jsp&currentMenu=MNDN0000508001" class="card col-md-4 bleu">
                <i class="fa-solid fa-arrow-right arrow"></i>
                <img src="${pageContext.request.contextPath}/assets/img/avatar-des-utilisateurs.png" class="icon"/>
                <div class="card-content">
                    <h3 class="h617pxSemibold">Client</h3>
                    <p class="Body14pxRegular">
                        Créez, modifiez et suivez vos fiches clients en toute simplicité.
                    </p>
                </div>
            </a>

            <!-- ÉTAT DE STOCK -->
            <a href="module.jsp?but=stock/etatstock/etatstock-liste.jsp" class="card col-md-4 vert">
                <i class="fa-solid fa-arrow-right arrow"></i>
                <img src="${pageContext.request.contextPath}/assets/img/stock.png" class="icon"/>
                <div class="card-content">
                    <h3 class="h617pxSemibold">&Eacute;tat de Stock</h3>
                    <p class="Body14pxRegular">
                        Consultez l&rsquo;&eacute;tat r&eacute;el de votre stock en temps r&eacute;el.
                    </p>
                </div>
            </a>

            <!-- TRANSFERT DE STOCK -->
            <a href="module.jsp?but=stock/transfertstock/transfertstock-liste.jsp" class="card col-md-4 jaune">
                <i class="fa-solid fa-arrow-right arrow"></i>
                <img src="${pageContext.request.contextPath}/assets/img/transfert.png" class="icon"/>
                <div class="card-content">
                    <h3 class="h617pxSemibold">Demande de transfert de stock</h3>
                    <p class="Body14pxRegular">
                        G&eacute;rez les demandes de transfert entre vos d&eacute;p&ocirc;ts sans effort.
                    </p>
                </div>
            </a>

            <!-- MATIÈRES PREMIÈRES -->
            <a href="module.jsp?but=modele/modele-liste.jsp&currentMenu=MENDYN1785359977312967" class="card col-md-4 marron">
                <i class="fa-solid fa-arrow-right arrow"></i>
                <img src="${pageContext.request.contextPath}/assets/img/chaussure-intelligente.png" class="icon"/>
                <div class="card-content">
                    <h3 class="h617pxSemibold">Modèles</h3>
                    <p class="Body14pxRegular">
                        Visualisez et administrez vos modèles efficacement.
                    </p>
                </div>
            </a>

            <a href="module.jsp?but=dashboard-point-opt.jsp" class="card col-md-4">
                <i class="fa fa-solid fa-arrow-right arrow"></i>
                <img src="${pageContext.request.contextPath}/assets/img/ajouter-au-panier.png" class="icon" />
                <div class="card-content">
                    <h3 class="h617pxSemibold">Dashboard par point de vente</h3>
                    <p class="Body14pxRegular">
                        Visualisez vos performances par point de vente.
                    </p>
                </div>
            </a>

        </div>
    </div>
</div>
