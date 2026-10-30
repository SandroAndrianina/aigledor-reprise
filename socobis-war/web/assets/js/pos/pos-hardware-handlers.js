/**
 * POS APJ - Hardware & Synchronization (pos-hardware-handlers.js)
 * Exhaustivité : 7 fonctions matériel et réseau
 */
(function (window) {
    'use strict';

    function sendMessage(texte) {
        if (!texte) return;
        const endpoint = "http://192.168.88.90:3100/lcd";
        fetch(endpoint, {
            method: "POST",
            headers: { "content-type": "application/json" },
            body: JSON.stringify({ message: texte })
        })
            .then(response => response.json())
            .catch(error => console.error("LCD Error:", error));
    }

    function sendProducts(produits) {
        try {
            const products = produits || (typeof window.gatherProductData === 'function' ? window.gatherProductData() : []);
            if (typeof window.socket !== 'undefined' && window.socket && typeof window.socket.emit === 'function') {
                window.socket.emit("send_products", products);
            }
        } catch (e) {}
    }

    function save(products) {
        try {
            localStorage.setItem('products', JSON.stringify(products));
            sendProducts(products);
        } catch(e) {}
    }

    function sync() {
        setupListeners();
        const products = typeof window.gatherProductData === 'function' ? window.gatherProductData() : [];
        save(products);
    }

    function checkAndUpdateListener(id) {
        const input = document.getElementById(id);
        if (input && !input.dataset.listenerAdded) {
            const update = () => sync();
            input.addEventListener('change', update);
            input.addEventListener('input', update);
            input.dataset.listenerAdded = 'true';
        }
    }

    function setupLinkListeners() {
        document.querySelectorAll("td a").forEach(link => {
            if (!link.dataset.listenerAdded) {
                link.addEventListener("click", () => sync());
                link.dataset.listenerAdded = "true";
            }
        });
    }

    function setupListeners() {
        if (typeof window.getIdNumber === 'function') {
            const ids = window.getIdNumber();
            ids.forEach(id => {
                checkAndUpdateListener("idProduit_" + id + "libelle");
                checkAndUpdateListener("designation_" + id);
                checkAndUpdateListener("qte_" + id);
                checkAndUpdateListener("pvTva_" + id);
                checkAndUpdateListener("remise_" + id);
                checkAndUpdateListener("remiseMontant_" + id);
            });
        }
        setupLinkListeners();
    }

    // Export global strict (Zéro régression)
    window.sendMessage = sendMessage;
    window.sendProducts = sendProducts;
    window.save = save;
    window.sync = sync;
    window.checkAndUpdateListener = checkAndUpdateListener;
    window.setupLinkListeners = setupLinkListeners;
    window.setupListeners = setupListeners;
})(window);