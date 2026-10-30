/**
 * POS APJ - Event Listeners & DOM Binding (pos-events.js)
 * Contient 100% des écouteurs d'événements, gestionnaires de soumission,
 * masquage des footers framework et modales.
 */

(function (window) {
    'use strict';

    // ================================================================
    // 1. MASQUAGE DES FOOTERS DU FRAMEWORK JSP & LIGNE MODÈLE
    // ================================================================
    function masquerFootersFramework() {
        document.querySelectorAll('.box-footer.borderless.nopadding').forEach(footer => {
            footer.classList.add("d-none");
            footer.style.setProperty('display', 'none', 'important');
        });

        const row0 = document.getElementById("ligne-multiple-0");
        if (row0) {
            row0.classList.add("d-none");
        }
    }

    // ================================================================
    // 2. GESTION DU GLOBAL LOADER & OVERRIDE ALERT
    // ================================================================
    function initLoaderEvents() {
        const loader = document.getElementById('globalLoader');

        const originalAlert = window.alert;
        window.alert = function(msg) {
            if (loader) {
                loader.style.display = 'none';
                loader.classList.add('d-none');
                loader.style.setProperty('display', 'none', 'important');
            }
            originalAlert.call(window, msg);
            setTimeout(function() {
                if (loader) {
                    loader.style.display = 'none';
                    loader.classList.add('d-none');
                }
            }, 100);
        };

        window.addEventListener('load', function() {
            if (loader) {
                loader.style.display = 'none';
                loader.classList.add('d-none');
            }
        });

        window.addEventListener('error', function() {
            if (loader) {
                loader.style.display = 'none';
                loader.classList.add('d-none');
            }
        });

        window.addEventListener('unhandledrejection', function() {
            if (loader) {
                loader.style.display = 'none';
                loader.classList.add('d-none');
            }
        });
    }

    // ================================================================
    // 3. ÉCOUTEURS DES MODES DE PAIEMENT & SECTIONS
    // ================================================================
    function initPaymentEvents() {
        const multipleInput = document.getElementById('multiple-payment');
        const paymentButtons = Array.from(document.querySelectorAll('.toggle-container .toggle-button'))
            .filter(button => button.querySelector('input[name="payment-method"]'));
        const sections = Array.from(document.querySelectorAll('.paiement-section'));
        let lastClicked = paymentButtons.find(button =>
            button.querySelector('input[name="payment-method"]')?.value === 'especes') || null;

        function getMode(button) {
            return button?.querySelector('input[name="payment-method"]')?.value || '';
        }
        function updateSections() {
            const activeModes = paymentButtons.filter(button => button.classList.contains('active')).map(getMode);
            sections.forEach(section => section.classList.toggle('d-none', !activeModes.includes(section.dataset.mode)));
        }
        function selectOnly(button) {
            paymentButtons.forEach(candidate => {
                const active = candidate === button;
                candidate.classList.toggle('active', active);
                const radio = candidate.querySelector('input[name="payment-method"]');
                if (radio) radio.checked = active;
            });
        }
        if (lastClicked) selectOnly(lastClicked);

        paymentButtons.forEach(button => {
            button.addEventListener('click', function(event) {
                const mode = getMode(button);
                if (multipleInput?.checked) {
                    event.preventDefault();
                    button.classList.toggle('active');
                    if (button.classList.contains('active')) lastClicked = button;
                } else {
                    selectOnly(button);
                    lastClicked = button;
                }
                updateSections();
                window.POSPayment?.handleModeChange(mode, button.classList.contains('active'));
            });
        });
        multipleInput?.addEventListener('change', function() {
            if (!this.checked) {
                const selected = lastClicked?.classList.contains('active')
                    ? lastClicked : paymentButtons.find(button => button.classList.contains('active'));
                selectOnly(selected || paymentButtons[0]);
                lastClicked = selected || paymentButtons[0];
            }
            updateSections();
            window.POSPayment?.handleMultipleChange();
        });
        updateSections();
    }

    // ================================================================
    // 4. SOUMISSION DU FORMULAIRE VENTE
    // ================================================================
    function initFormSubmitEvents() {
        const form = document.getElementById('venteForm');
        const loader = document.getElementById('globalLoader');

        if (!form) return;

        form.addEventListener('submit', function(e) {
            const monetaryInputs = ['valeurEspece','montantMvola','montantOrange','montantAirtel','montantCheque','montantVisa','montantBOA','montantCarte'];
            monetaryInputs.forEach(inputId => {
                const input = document.getElementById(inputId);
                if (input && input.value) {
                    input.value = String(window.parseAmount(input.value));
                }
            });

            document.querySelectorAll("input[name^='pvTva_'], input[name^='prix_'], input[name^='qte_'], input[name^='remiseMontant_']").forEach(input => {
                if (input.value) input.value = String(window.parseAmount(input.value));
            });

            if (typeof window.saveCartState === 'function') window.saveCartState();

            const submitBtn = form.querySelector('button[type="submit"].btn-primary.btn-large');
            if (submitBtn) {
                submitBtn.style.backgroundColor = '#6c757d';
                submitBtn.style.cursor = 'not-allowed';
                submitBtn.style.opacity = '0.7';
                submitBtn.style.pointerEvents = 'none';
                submitBtn.innerHTML = 'Paiement en cours...';
                submitBtn.disabled = true;
            }

            const selectedMode = typeof window.getSingleMode === 'function'
                ? window.getSingleMode()
                : document.querySelector('input[name="payment-method"]:checked')?.value;
            if (selectedMode === 'especes') {
                const roundedTotal = window.POSPayment?.getRoundedCashTotal() || 0;
                const inputEspece = document.getElementById('valeurEspece');
                if (inputEspece && !inputEspece.value && roundedTotal > 0) {
                    inputEspece.value = String(roundedTotal);
                    const inputRetour = document.getElementById('aretourner');
                    if (inputRetour) inputRetour.value = '0';
                }
            }

            if (loader) {
                loader.style.display = 'flex';
                loader.classList.remove('d-none');
            }
        });
    }

    // ================================================================
    // 5. ÉCOUTEURS DES MODALES, CLIENT & CLAVIER VIRTUEL
    // ================================================================
    function initModalEvents() {
        // Changement Client => Recalcul des remises
        if (typeof $ !== 'undefined') {
            $("#idClient").on("change", function() {
                const idClient = $(this).val();
                if (typeof window.recheckRemiseAllRows === 'function') {
                    window.recheckRemiseAllRows(idClient);
                }
            });
        }

        // Modale ABC Clavier
        const btnABC = document.getElementById('btnClavierABC');
        if (btnABC) {
            btnABC.addEventListener('click', (e) => { e.preventDefault(); if (typeof window.openModal === 'function') window.openModal(); });
            btnABC.addEventListener('touchstart', (e) => { e.preventDefault(); if (typeof window.openModal === 'function') window.openModal(); });
        }

        // MutationObserver sur Butfillejsp pour verrouiller les champs
        const targetNode = document.getElementById('butfillejsp');
        if (targetNode) {
            const observer = new MutationObserver(function() {
                if (typeof window.verrouillerChampsButFille === 'function') window.verrouillerChampsButFille();
                if (typeof window.ensureProductRowsVisible === 'function') window.ensureProductRowsVisible();
            });
            observer.observe(targetNode, { childList: true, subtree: true });
        }

        // Bouton de fermeture du clavier virtuel
        const btnClose = document.getElementById('btnCloseClavier');
        if (btnClose) {
            btnClose.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                if (typeof window.fermerClavierVisuel === 'function') window.fermerClavierVisuel();
            });
        }

        // Écouteurs globaux d'ouverture de clavier
        if (typeof window.verifierEtOuvrirClavier === 'function') {
            document.addEventListener('focusin', window.verifierEtOuvrirClavier);
            document.addEventListener('click', window.verifierEtOuvrirClavier);
        }
    }

    // ================================================================
    // 6. REPRISE D'UNE RECHERCHE DEPUIS UNE PAGE D'OPÉRATION DE CAISSE
    // ================================================================
    function consumePendingProductSearch(now) {
        const storageKey = window.POS_CASH_SEARCH_STORAGE_KEY || 'posPendingProductSearch';
        const ttl = window.POS_CASH_SEARCH_TTL_MS || 30000;
        let rawState = null;

        try {
            rawState = window.sessionStorage.getItem(storageKey);
            if (rawState !== null) window.sessionStorage.removeItem(storageKey);
        } catch (error) {
            return false;
        }
        if (!rawState) return false;

        try {
            const state = JSON.parse(rawState);
            const currentTime = typeof now === 'number' ? now : Date.now();
            if (!state || typeof state.timestamp !== 'number' || currentTime - state.timestamp > ttl || currentTime < state.timestamp) {
                return false;
            }

            const search = document.getElementById('idProduit_libelle');
            if (!search) return false;
            search.value = typeof state.value === 'string' ? state.value : '';
            search.focus();
            if (search.value) {
                search.dispatchEvent(new Event('input', { bubbles: true }));
            }
            return true;
        } catch (error) {
            return false;
        }
    }

    window.consumePendingProductSearch = consumePendingProductSearch;

    // ================================================================
    // 7. INITIALISATION AUTOMATIQUE AU CHARGEMENT DOM
    // ================================================================
    document.addEventListener('DOMContentLoaded', function() {
        masquerFootersFramework();
        initLoaderEvents();
        initPaymentEvents();
        initFormSubmitEvents();
        initModalEvents();

        if (typeof window.initVirtualKeyboard === 'function') window.initVirtualKeyboard();
        if (typeof window.verrouillerChampsButFille === 'function') window.verrouillerChampsButFille();
        if (typeof window.masquerEnTeteImageEnDouble === 'function') window.masquerEnTeteImageEnDouble();
        if (typeof window.nettoyerBoutonsTableau === 'function') window.nettoyerBoutonsTableau();
        if (typeof window.attachSubmitGuard === 'function') window.attachSubmitGuard();
        if (typeof window.checkPaymentValidation === 'function') window.checkPaymentValidation();
        if (typeof window.updateTotals === 'function') window.updateTotals();
        if (typeof window.addLinkAccueilLogo === 'function') window.addLinkAccueilLogo();

        const pendingSearchConsumed = consumePendingProductSearch();
        if (!pendingSearchConsumed && typeof window.focusOnProduit === 'function') window.focusOnProduit();
        if (typeof window.restoreCartState === 'function') window.restoreCartState();
        if (typeof window.ensureProductRowsVisible === 'function') window.ensureProductRowsVisible();
        if (typeof window.sendMessage === 'function') window.sendMessage("BIENVENUE !!!");

        // Socket.io sync toutes les 30 secondes
        setInterval(function() {
            if (typeof window.sendProducts === 'function') window.sendProducts();
        }, 30000);
    });

})(window);
