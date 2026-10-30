/**
 * POS APJ - Keyboard Navigation Engine (pos-keyboard.js)
 * Navigation 100% Clavier avec attribution directe de la frappe et suppression propre
 */

(function (window) {
    'use strict';

    const POSKeyboard = {
        enabled: true,
        initialized: false,
        // États possibles : 'SEARCH', 'BASKET', 'PAYMENT'
        currentState: 'SEARCH',
        activeRowIndex: 0,
        // Index dans la liste complète retournée par getBasketElements().
        // Il peut différer de l'index de ligne lorsque des champs client
        // sont présents avant le tableau.
        activePaymentIndex: 0,
        // Index de l'élément actif dans la liste verticale de la zone droite
        paymentRowIndex: 0,
        helpPreviousFocus: null,
        listeners: [],
        pendingTimers: [],

        schedule(callback, delay) {
            const timer = window.setTimeout(() => {
                this.pendingTimers = this.pendingTimers.filter(id => id !== timer);
                if (this.initialized) callback();
            }, delay);
            this.pendingTimers.push(timer);
        },

        listen(target, type, handler, options) {
            target.addEventListener(type, handler, options);
            this.listeners.push({ target, type, handler, options });
        },

        destroy() {
            this.listeners.forEach(({ target, type, handler, options }) =>
                target.removeEventListener(type, handler, options));
            this.listeners = [];
            this.pendingTimers.forEach(id => window.clearTimeout(id));
            this.pendingTimers = [];
            this.initialized = false;
            this.enabled = false;
            this.currentState = 'SEARCH';
            this.activeRowIndex = 0;
            this.activePaymentIndex = 0;
            this.paymentRowIndex = 0;
            if (this.isHelpOpen()) this.closeHelpModal();
            document.body.classList.remove('pos-kb-mode', 'pos-help-open');
            document.querySelectorAll('.pos-kb-focused-row, .pos-kb-focused-payment').forEach(el =>
                el.classList.remove('pos-kb-focused-row', 'pos-kb-focused-payment'));
            document.querySelectorAll('.pos-kb-search-hint').forEach(el => el.remove());
            document.querySelectorAll('.pos-kb-search-wrapper, .pos-kb-client-card').forEach(el =>
                el.classList.remove('pos-kb-search-wrapper', 'pos-kb-client-card'));
            document.getElementById('pos-keyboard-css')?.remove();
        },

        init() {
            if (this.initialized || window.isKeyboardMode !== true) return;
            if (window.POSSelectKeyboard) window.POSSelectKeyboard.init();
            this.initialized = true;
            this.enabled = true;
            this.injectCSS();
            this.injectKeyboardHints();
            this.bindDirectSearchTyping();
            this.bindGlobalShortcuts();
            this.bindBasketShortcuts();
            this.bindPaymentShortcuts();
            this.bindHeaderShortcuts();
            const closeButton = document.getElementById('pos-help-close');
            if (closeButton) this.listen(closeButton, 'click', () => this.closeHelpModal());
            const helpModal = document.getElementById('pos-help-modal');
            if (helpModal) this.listen(helpModal, 'click', e => {
                if (e.target === helpModal) this.closeHelpModal();
            });
            console.log('[POS Keyboard] ⌨️ Moteur 100% Clavier complet initialisé.');
        },

        injectKeyboardHints() {
            if (window.isKeyboardMode !== true) {
                document.body.classList.remove('pos-kb-mode');
                return;
            }
            document.body.classList.add('pos-kb-mode');

            const createHint = (text, className) => {
                const hint = document.createElement('span');
                hint.className = className;
                hint.textContent = text;
                hint.setAttribute('aria-hidden', 'true');
                return hint;
            };

            const search = document.getElementById('idProduit_libelle');
            const searchContainer = search && (search.closest('.form-input') || search.parentElement);
            if (searchContainer && !searchContainer.querySelector('.pos-kb-search-hint')) {
                searchContainer.classList.add('pos-kb-search-wrapper');
                searchContainer.appendChild(createHint('Shift + F', 'pos-kb-search-hint'));
            }

            const basket = document.getElementById('formTableContainer');
            const clientCard = basket && Array.from(basket.children).find(child => child.classList.contains('col-md-12') && child.classList.contains('cardradius'));
            if (clientCard) clientCard.classList.add('pos-kb-client-card');
        },

        setState(newState) {
            this.currentState = newState;
            this.updateVisualState();
            console.log(`[POS Keyboard] État actif : ${this.currentState}`);
        },

        /**
         * Bordure bleue distinctive sur la ligne panier active
         */
        injectCSS() {
            if (document.getElementById('pos-keyboard-css')) return;
            const style = document.createElement('style');
            style.id = 'pos-keyboard-css';
            style.innerHTML = `
                .pos-kb-focused-row {
                    border: 3px solid #007bff !important;
                    box-shadow: 0 0 8px rgba(0, 123, 255, 0.4) !important;
                    background-color: rgba(0, 123, 255, 0.05) !important;
                    transition: all 0.15s ease-in-out;
                }
                .pos-kb-focused-row:focus {
                    outline: none !important;
                }
                .pos-kb-focused-payment {
                    outline: 3px solid #007bff !important;
                    box-shadow: 0 0 6px rgba(0, 123, 255, 0.5) !important;
                }
                .pos-kb-focused-payment.form-input,
                .pos-kb-focused-payment.form-control,
                .pos-kb-focused-payment input,
                .pos-kb-focused-payment select {
                    outline: 3px solid #007bff !important;
                    border: 3px solid #007bff !important;
                    box-shadow: 0 0 6px rgba(0, 123, 255, 0.5) !important;
                }
                #idProduit_libelle:focus,
                #formTableContainer input:focus,
                #formTableContainer select:focus,
                .vente-directe-right-container input:focus,
                .vente-directe-right-container select:focus {
                    border: 3px solid #007bff !important;
                    outline: 3px solid rgba(0, 123, 255, 0.25) !important;
                    box-shadow: 0 0 6px rgba(0, 123, 255, 0.5) !important;
                }
                .vd-header a.btn:focus,
                .vd-header button.btn:focus,
                .vd-header [role="button"]:focus,
                .vd-header .dropdown-menu a:focus,
                .vd-header .dropdown-menu button:focus {
                    outline: 3px solid #007bff !important;
                    outline-offset: 2px;
                    box-shadow: 0 0 6px rgba(0, 123, 255, 0.45) !important;
                }
            `;
            document.head.appendChild(style);
        },

        updateVisualState() {
            // Nettoyer les surbrillances
            document.querySelectorAll('.pos-kb-focused-row').forEach(el => el.classList.remove('pos-kb-focused-row'));
            document.querySelectorAll('.pos-kb-focused-payment').forEach(el => el.classList.remove('pos-kb-focused-payment'));

            if (this.currentState === 'BASKET') {
                const elems = this.getBasketElements();
                const el = elems[this.activeRowIndex];
                if (el) {
                    const row = el.closest('#ajout_multiple_ligne tr');
                    if (row) {
                        row.classList.add('pos-kb-focused-row');
                    }
                }
            } else if (this.currentState === 'PAYMENT') {
                const elems = this.getPaymentElements();
                const el = elems[this.paymentRowIndex];
                if (el) {
                    if (el.classList && el.classList.contains('toggle-button')) {
                        el.classList.add('pos-kb-focused-payment');
                    } else if (el.closest && el.closest('.paiement-section')) {
                        el.closest('.paiement-section').classList.add('pos-kb-focused-payment');
                    } else {
                        el.classList.add('pos-kb-focused-payment');
                    }
                }
            }
        },

        isHelpOpen() {
            const modal = document.getElementById('pos-help-modal');
            return !!(modal && modal.classList.contains('is-open'));
        },

        toggleHelpModal() {
            if (this.isHelpOpen()) this.closeHelpModal();
            else this.openHelpModal();
        },

        openHelpModal() {
            const modal = document.getElementById('pos-help-modal');
            if (!modal) return;
            this.helpPreviousFocus = document.activeElement;
            modal.classList.add('is-open');
            modal.setAttribute('aria-hidden', 'false');
            document.body.classList.add('pos-help-open');
            const closeButton = document.getElementById('pos-help-close');
            if (closeButton) closeButton.focus();
        },

        closeHelpModal() {
            const modal = document.getElementById('pos-help-modal');
            if (!modal) return;
            modal.classList.remove('is-open');
            modal.setAttribute('aria-hidden', 'true');
            document.body.classList.remove('pos-help-open');
            if (this.helpPreviousFocus && document.contains(this.helpPreviousFocus)) {
                this.helpPreviousFocus.focus();
            }
            this.helpPreviousFocus = null;
        },

        getVisibleCartRows() {
            return Array.from(document.querySelectorAll('#ajout_multiple_ligne tr:not(.d-none)'));
        },

        getPaymentButtons() {
            return Array.from(document.querySelectorAll('.toggle-container .toggle-button'));
        },

        /**
         * Retourne la liste ordonnée des éléments focalisables de la zone RÉCAP.
         * Navigation PAR LIGNES : les champs hors-tableau (client) puis UN SEUL
         * représentant par ligne de panier (la quantité), pour que ↑/↓ sautent
         * de ligne en ligne (et pas de cellule en cellule).
         */
        getBasketElements() {
            const els = [];
            const container = document.getElementById('formTableContainer') || document.getElementById('butfillejsp');
            if (!container) return els;

            // 1. Champs hors-tableau (inputs client en haut de la zone récap)
            container.querySelectorAll('input, select').forEach(el => {
                if (el.closest('#ajout_multiple_ligne tr')) return; // cellule de table => gérée par ligne
                const type = (el.type || '').toLowerCase();
                if (type === 'hidden' || type === 'checkbox' || type === 'radio' || type === 'submit' || type === 'button' || type === 'file') return;
                els.push(el);
            });

            // 2. Une ligne panier = un élément représentatif (la quantité)
            this.getVisibleCartRows().forEach(row => {
                const qte = row.querySelector('input[name^="qte_"], input.qte-editable-input');
                if (qte) els.push(qte);
            });

            return els;
        },

        /**
         * Retourne la liste ordonnée (haut -> bas) des éléments focalisables
         * de la zone PAIEMENT (dans .vente-directe-right-container) :
         * le label "Paiement multiple", les 8 modes (toggles), puis les champs
         * de saisie des .paiement-section visibles (montant / référence).
         */
        getPaymentElements() {
            const els = [];
            const container = document.querySelector('.vente-directe-right-container');
            if (!container) return els;

            // Paiement multiple (label checkbox) en premier s'il est visible
            const mpLabel = document.getElementById('multiple-payment');
            const mp = mpLabel ? mpLabel.closest('label') || mpLabel : null;
            if (mp && container.contains(mp) && mp.offsetParent !== null) els.push(mp);

            // Les 8 modes de paiement (toggles visibles)
            container.querySelectorAll('.toggle-container .toggle-button').forEach(btn => {
                if (btn.offsetParent !== null) els.push(btn);
            });

            // Les champs de saisie visibles de la zone paiement (dans l'ordre du DOM)
            container.querySelectorAll('.paiement-section').forEach(section => {
                if (section.offsetParent === null) return;
                section.querySelectorAll('input, select').forEach(field => {
                    const type = (field.type || '').toLowerCase();
                    if (type === 'hidden' || type === 'checkbox' || type === 'radio' || type === 'submit' || type === 'button' || type === 'file') return;
                    els.push(field);
                });
            });

            return els;
        },

        getHeaderElements() {
            const header = document.querySelector('.vd-header');
            if (!header) return [];
            const searchInput = header.querySelector('#idProduit_libelle');
            const controls = Array.from(header.querySelectorAll('a.btn, button.btn, [role="button"]'))
                .filter(el => !el.disabled && el.offsetParent !== null);
            if (searchInput && searchInput.offsetParent !== null) controls.unshift(searchInput);
            return controls;
        },

        focusHeaderElement(elements, index) {
            if (!elements.length) return;
            const safe = (index + elements.length) % elements.length;
            elements.forEach(element => element.setAttribute('tabindex', '0'));
            elements[safe].focus();
        },

        getHeaderDropdownItems(header) {
            if (!header) return [];
            return Array.from(header.querySelectorAll('.dropdown-menu a, .dropdown-menu button'))
                .filter(item => !item.disabled);
        },

        openHeaderDropdown(header, dropdown) {
            if (!header || !dropdown) return;
            const parent = dropdown.closest('.dropdown');
            const isOpen = !!(parent && parent.classList.contains('open'));
            if (!isOpen) dropdown.click();
            this.schedule(() => {
                const items = this.getHeaderDropdownItems(header);
                if (items.length) {
                    items.forEach(item => item.setAttribute('tabindex', '0'));
                    items[0].focus();
                }
            }, 0);
        },

        bindHeaderShortcuts() {
            this.listen(document, 'keydown', (e) => {
                if (!this.enabled || document.querySelector('.modal.in, .modal.show') || this.isHelpOpen()) return;
                if (document.querySelector('.ui-autocomplete:not([style*="display: none"])')) return;

                const activeEl = document.activeElement;
                if (activeEl && activeEl.id === 'idProduit_libelle' && e.key === 'Enter') {
                    const elements = this.getHeaderElements();
                    if (elements.length > 1) {
                        e.preventDefault();
                        this.focusHeaderElement(elements, 1);
                    }
                    return;
                }
                if (activeEl && activeEl.id === 'idProduit_libelle' && e.key === 'ArrowUp') {
                    const elements = this.getHeaderElements();
                    if (elements.length) {
                        e.preventDefault();
                        this.focusHeaderElement(elements, elements.length - 1);
                    }
                    return;
                }
                if (activeEl && activeEl.id === 'idProduit_libelle' && e.key === 'ArrowDown') {
                    const elements = this.getHeaderElements();
                    if (elements.length > 1) {
                        e.preventDefault();
                        this.focusHeaderElement(elements, 1);
                    }
                    return;
                }
                const header = activeEl && activeEl.closest('.vd-header');
                if (!header) return;

                const menu = activeEl.closest('.dropdown-menu');
                if (menu) {
                    const items = this.getHeaderDropdownItems(header);
                    const current = items.indexOf(activeEl);
                    if (e.key === 'ArrowDown' || e.key === 'ArrowUp') {
                        e.preventDefault();
                        const delta = e.key === 'ArrowDown' ? 1 : -1;
                        items[(current + delta + items.length) % items.length]?.focus();
                    } else if (e.key === 'Enter' && current >= 0) {
                        e.preventDefault();
                        items[current].click();
                    } else if (e.key === 'Escape') {
                        e.preventDefault();
                        const dropdown = header.querySelector('#dropdownMenu1');
                        const parent = dropdown && dropdown.closest('.dropdown');
                        if (parent && parent.classList.contains('open')) dropdown.click();
                        if (dropdown) dropdown.focus();
                    }
                    return;
                }

                const elements = this.getHeaderElements();
                const current = elements.indexOf(activeEl);
                if (current < 0) return;

                if (e.key === 'ArrowLeft' || e.key === 'ArrowRight') {
                    e.preventDefault();
                    this.focusHeaderElement(elements, current + (e.key === 'ArrowRight' ? 1 : -1));
                } else if (e.key === 'ArrowDown' && activeEl.id === 'dropdownMenu1') {
                    e.preventDefault();
                    this.openHeaderDropdown(header, activeEl);
                } else if (e.key === 'Enter' && activeEl.id === 'dropdownMenu1') {
                    e.preventDefault();
                    this.openHeaderDropdown(header, activeEl);
                } else if (e.key === 'Enter' && (activeEl.matches('a.btn, button.btn, [role="button"]'))) {
                    e.preventDefault();
                    activeEl.click();
                }
            }, true);
        },

        // ====================================================================
        // 1. ATTRIBUTION DIRECTE DE LA FRAPPE À LA RECHERCHE HEADER
        // ====================================================================
        bindDirectSearchTyping() {
            this.listen(document, 'keydown', (e) => {
                if (!this.enabled) return;

                // Ne pas intercepter si une modale est ouverte
                if (document.querySelector('.modal.in, .modal.show')) return;

                const isBasketQuantityKey = this.currentState === 'BASKET' && (
                    e.key === '+' || e.key === '-' || e.key === 'Add' || e.key === 'Subtract' ||
                    e.key === '\u2212' || e.code === 'Minus' || e.code === 'NumpadSubtract' || e.code === 'NumpadAdd'
                );
                if (isBasketQuantityKey) return;

                // Ne rediriger que les frappes imprimables sans modificateur.
                // Sur certains claviers QWERTY japonais, « + » utilise Shift :
                // il ne doit donc pas être injecté pendant Cmd + + (zoom navigateur).
                // Les raccourcis Shift sont traités séparément dans bindGlobalShortcuts().
                if (e.key.length !== 1 || e.shiftKey || e.ctrlKey || e.altKey || e.metaKey || e.isComposing) return;

                const activeEl = document.activeElement;
                const searchInput = document.getElementById('idProduit_libelle');
                if (!searchInput) return;

                // Un champ est réellement « éditable » s'il est saisissable (non readonly/disabled)
                const isEditable = activeEl && activeEl.tagName &&
                    (/INPUT|TEXTAREA|SELECT/.test(activeEl.tagName) && !activeEl.readOnly && !activeEl.disabled);

                // Si l'utilisateur saisit déjà dans une zone éditable du formulaire (panier, client, paiement),
                // NE PAS rediriger la frappe vers la recherche.
                // Un champ readonly (ex. aucune cellule de la table panier en mode clavier) ne bloque PAS :
                // taper depuis un champ readonly => recherche.
                const isInFormArea = activeEl && (
                    (isEditable && activeEl.closest('#butfillejsp, #ajout_multiple_ligne')) ||
                    (isEditable && activeEl.closest('.vente-directe-right-container, .input-container, .paiement-section')) ||
                    activeEl.id === 'idClientlibelle' ||
                    (activeEl.tagName === 'INPUT' && !activeEl.readOnly && !activeEl.closest('.vd-header') && activeEl.id !== 'idProduit_libelle')
                );

                if (isInFormArea) return;

                // Si le focus n'est pas déjà sur la recherche header
                if (activeEl !== searchInput) {
                    e.preventDefault();
                    this.setState('SEARCH');
                    if (typeof window.fermerClavierVisuel === 'function') window.fermerClavierVisuel();
                    searchInput.focus();

                    // Injecter le caractère tapé et déclencher l'événement de recherche
                    searchInput.value = searchInput.value + e.key;
                    searchInput.dispatchEvent(new Event('input', { bubbles: true }));
                }
            }, true); // Capture prioritaire
        },

        // ====================================================================
        // 2. RACCOURCIS GLOBAUX (Shift/Ctrl+Entrée, Tab, Shift+Tab, Shift+F, Shift+M)
        // ====================================================================
        bindGlobalShortcuts() {
            this.listen(document, 'keydown', (e) => {
                if (!this.enabled) return;

                if (e.key === 'F1' || (e.ctrlKey && (e.key === 'h' || e.key === 'H'))) {
                    e.preventDefault();
                    e.stopPropagation();
                    this.toggleHelpModal();
                    return;
                }

                if (this.isHelpOpen()) {
                    if (e.key === 'Escape') {
                        e.preventDefault();
                        this.closeHelpModal();
                    }
                    return;
                }

                if (document.querySelector('.modal.in, .modal.show')) return;

                // Shift/Ctrl + Entrée : action de validation primaire (Valider/Payer).
                // Les deux variantes sont acceptées pour les claviers de caisse
                // qui exposent l'une ou l'autre combinaison.
                if ((e.shiftKey || e.ctrlKey) && e.key === 'Enter') {
                    e.preventDefault();
                    this.submitPrimaryAction();
                    return;
                }

                // Shift/Ctrl + F : saisie code-barres / recherche produit
                if ((e.shiftKey || e.ctrlKey) && (e.key === 'F' || e.key === 'f')) {
                    e.preventDefault();
                    this.focusSearch();
                    return;
                }

                // Shift + M : Saisie Montant / Paiement
                if (e.shiftKey && (e.key === 'M' || e.key === 'm')) {
                    e.preventDefault();
                    this.focusPayment();
                    return;
                }

                // Shift + Tab : Changement de zone (Panier / Mode de paiement / Recherche)
                // Tab seul = comportement navigateur par défaut (non intercepté).
                if (e.key === 'Tab') {
                    const isAutocompleteOpen = document.querySelector('.ui-autocomplete:not([style*="display: none"])');
                    if (isAutocompleteOpen) return;
                    if (!e.shiftKey) return; // Tab seul : ne pas changer de zone

                    e.preventDefault();

                    const activeEl = document.activeElement;
                    if (activeEl && activeEl.closest && activeEl.closest('.vd-header')) {
                        if (this.getVisibleCartRows().length > 0) this.focusBasket(0);
                        else this.focusRecap();
                        return;
                    }

                    if (this.currentState === 'PAYMENT') this.focusRecap();
                    else this.focusPayment();
                }
            });
        },

        // ====================================================================
        // 3. RECHERCHE PRODUIT (Shift + F)
        // ====================================================================
        focusSearch() {
            this.setState('SEARCH');
            const searchInput = document.getElementById('idProduit_libelle');
            if (searchInput) {
                if (typeof window.fermerClavierVisuel === 'function') window.fermerClavierVisuel();
                searchInput.focus();
                if (searchInput.select) searchInput.select();
            }
        },

        // ====================================================================
        // 4. PANIER : NAVIGATION & SUPPRESSION PROPRE
        // ====================================================================
        focusBasket(rowIndex = 0) {
            const rows = this.getVisibleCartRows();
            if (rows.length === 0) {
                this.focusSearch();
                return;
            }
            const safeRowIndex = Math.min(Math.max(rowIndex, 0), rows.length - 1);
            const basketEls = this.getBasketElements();
            const basketIndex = basketEls.indexOf(rows[safeRowIndex].querySelector('input[name^="qte_"], input.qte-editable-input'));
            this.activeRowIndex = basketIndex >= 0 ? basketIndex : safeRowIndex;
            this.setState('BASKET');

            const targetRow = rows[safeRowIndex];
            this.focusCartRow(targetRow);
        },

        /**
         * Rendre une ligne panier focalisable et la mettre au focus (surbrillance
         * sur le <tr>, PAS sur l'input quantité).
         */
        focusCartRow(row) {
            if (!row) return;
            if (!row.hasAttribute('tabindex')) row.setAttribute('tabindex', '-1');
            this.updateVisualState();
            row.scrollIntoView({ block: 'nearest', behavior: 'smooth' });
            row.focus({ preventScroll: true });
        },

        /**
         * Focus un élément de la zone RÉCAP (input/select de #formTableContainer).
         */
        focusBasketEl(elems, index) {
            if (!elems || elems.length === 0) return;
            const safe = Math.min(Math.max(index, 0), elems.length - 1);
            this.activeRowIndex = safe;
            this.setState('BASKET');

            const el = elems[safe];
            if (!el) return;

            // Mise à jour de l'index de ligne active si on est sur une quantité du panier
            const row = el.closest('#ajout_multiple_ligne tr');
            if (row) {
                this.focusCartRow(row);
                return;
            }

            // Champ hors-tableau (input client en haut) : focus normal sur l'input
            el.focus();
            if (el.select && (el.type === 'text' || el.type === 'search')) el.select();
            this.updateVisualState();
        },

        /**
         * Focus le premier élément de la zone RÉCAP (l'input client en haut).
         */
        focusRecap() {
            const elems = this.getBasketElements();
            if (elems.length === 0) {
                this.focusSearch();
                return;
            }
            this.focusBasketEl(elems, 0);
        },

        getActiveBasketRow() {
            if (this.currentState !== 'BASKET') return null;

            const activeEl = document.activeElement;
            const basketContainer = document.getElementById('formTableContainer') || document.getElementById('butfillejsp');
            if (!basketContainer || !activeEl || !basketContainer.contains(activeEl)) return null;

            const focusedRow = activeEl && activeEl.closest ? activeEl.closest('#ajout_multiple_ligne tr') : null;
            if (focusedRow) return focusedRow;

            const basketEl = this.getBasketElements()[this.activeRowIndex];
            return basketEl && basketEl.closest ? basketEl.closest('#ajout_multiple_ligne tr') : null;
        },

        bindBasketShortcuts() {
            this.listen(document, 'keydown', (e) => {
                if (document.querySelector('#pos-quantity-removal-modal.is-open')) return;

                // Touche SUPPR (Delete) : supprimer directement la ligne
                // uniquement lorsque le focus est réellement dans le panier.
                if (e.key === 'Delete' || e.key === 'Del') {
                    if (this.currentState !== 'BASKET') return;
                    const activeEl = document.activeElement;
                    const targetRow = activeEl && activeEl.closest ? activeEl.closest('#ajout_multiple_ligne tr') : null;
                    if (targetRow) {
                        e.preventDefault(); // 🛑 EMPÊCHE L'EFFACEMENT DU TEXTE DANS L'INPUT QUANTITÉ !
                        e.stopPropagation();

                        const rowIndex = this.getVisibleCartRows().indexOf(targetRow);
                        const deleteBtn = targetRow.querySelector('.btn-danger, button[onclick*="removeLine"]');
                        if (deleteBtn) {
                            deleteBtn.click();
                        } else if (typeof window.removeLine === 'function') {
                            window.removeLine(targetRow);
                        }

                        this.schedule(() => {
                            const newRows = this.getVisibleCartRows();
                            if (newRows.length > 0) this.focusBasket(Math.min(Math.max(rowIndex, 0), newRows.length - 1));
                            else this.focusSearch();
                        }, 50);
                        return;
                    }
                }

                if (this.currentState !== 'BASKET') return;

                // Ne RIEN intercepter si la barre de recherche est focusée (édition libre)
                const activeEl = document.activeElement;
                if (activeEl && (activeEl.id === 'idProduit_libelle' || activeEl.closest('.vd-header'))) return;

                // Ne RIEN intercepter si une liste d'autocomplete est ouverte (naviguer dans les suggestions)
                if (document.querySelector('.ui-autocomplete:not([style*="display: none"])')) return;

                const basketEls = this.getBasketElements();
                if (basketEls.length === 0) return;

                // Trouver l'élément actuellement focusé dans la liste
                let currentIndex = basketEls.indexOf(activeEl);
                if (currentIndex < 0) currentIndex = this.activeRowIndex;

                // Backspace édite une quantité saisissable ; ailleurs, il retire la ligne focalisée.
                if (e.key === 'Backspace') {
                    const targetRow = activeEl && activeEl.closest ? activeEl.closest('#ajout_multiple_ligne tr') : null;
                    if (targetRow && activeEl.matches?.("input[name^='qte_']") && !activeEl.readOnly) {
                        return; // Édition de la quantité : Backspace conserve son comportement natif.
                    }
                    if (targetRow) {
                        e.preventDefault();
                        e.stopPropagation();
                        const deleteBtn = targetRow.querySelector('.btn-danger, button[onclick*="removeLine"]');
                        if (deleteBtn) {
                            deleteBtn.click();
                        } else if (typeof window.removeLine === 'function') {
                            window.removeLine(targetRow);
                        }
                        this.schedule(() => {
                            const newRows = this.getVisibleCartRows();
                            if (newRows.length > 0) {
                                const newEls = this.getBasketElements();
                                this.focusBasketEl(newEls, Math.max(currentIndex - 1, 0));
                            } else this.focusSearch();
                        }, 50);
                        return;
                    }
                }
                if (e.key === 'Backspace') return;

                // FLÈCHE BAS : descendre dans l'ordre du DOM (vers le panier)
                if (e.key === 'ArrowDown') {
                    e.preventDefault();
                    this.focusBasketEl(basketEls, currentIndex + 1);
                    return;
                }

                // FLÈCHE HAUT : remonter dans l'ordre du DOM (vers les inputs client)
                if (e.key === 'ArrowUp') {
                    e.preventDefault();
                    if (currentIndex > 0) {
                        this.focusBasketEl(basketEls, currentIndex - 1);
                    } else {
                        this.focusSearch();
                    }
                    return;
                }

                // FLÈCHES GAUCHE/DROITE : naviguer horizontalement entre les inputs du récap.
                // HORS tableau uniquement : dans une ligne de la table panier, G/D sont inertes.
                if (e.key === 'ArrowLeft' || e.key === 'ArrowRight') {
                    const rowEl = activeEl ? activeEl.closest('#ajout_multiple_ligne tr') : null;
                    if (rowEl) return; // dans la table : seuls ↑/↓ naviguent
                    e.preventDefault();
                    const delta = (e.key === 'ArrowRight') ? 1 : -1;
                    this.focusBasketEl(basketEls, currentIndex + delta);
                    return;
                }

                // Touche + : Incrémenter quantité (sur une ligne panier)
                if (e.key === '+' || e.key === 'Add' || e.code === 'NumpadAdd') {
                    const currentRow = this.getActiveBasketRow();
                    e.preventDefault();
                    if (currentRow) {
                        const qteInput = currentRow.querySelector('input[name^="qte_"]');
                        if (qteInput) {
                            const cur = window.parseAmount(qteInput.value || 0);
                            qteInput.value = cur + 1;
                            if (typeof window.updateTotals === 'function') window.updateTotals();
                            if (typeof window.sync === 'function') window.sync();
                        }
                    }
                    return;
                }

                // Touche - : Décrémenter quantité
                if (e.key === '-' || e.key === 'Subtract' || e.key === '\u2212' || e.code === 'Minus' || e.code === 'NumpadSubtract') {
                    const currentRow = this.getActiveBasketRow();
                    e.preventDefault();
                    if (currentRow) {
                        const qteInput = currentRow.querySelector('input[name^="qte_"]');
                        if (qteInput) {
                            const cur = window.parseAmount(qteInput.value || 0);
                            const next = Math.max(cur - 1, 0);
                            if (next === 0 && typeof window.requestQuantityRemoval === 'function') {
                                window.requestQuantityRemoval(currentRow);
                            } else {
                                qteInput.value = next;
                                if (typeof window.updateTotals === 'function') window.updateTotals();
                                if (typeof window.sync === 'function') window.sync();
                            }
                        }
                    }
                    return;
                }
            }, true); // Capture prioritaire
        },

        // ====================================================================
        // 5. SECTION PAIEMENT (Shift + M, Flèches ↑ ↓ / ← →, Entrée toggle)
        // ====================================================================
        focusPayment() {
            this.setState('PAYMENT');
            const elems = this.getPaymentElements();
            if (elems.length === 0) return;
            const activeEl = document.activeElement;
            if (!activeEl || !activeEl.closest('.vente-directe-right-container')) {
                // Cibler le mode actif (radio :checked) si présent, sinon le premier élément
                const checked = document.querySelector('.toggle-container .toggle-button:has(input[name="payment-method"]:checked)');
                const alreadyInList = checked && elems.indexOf(checked) !== -1;
                const startIdx = alreadyInList ? elems.indexOf(checked) : 1;
                this.focusPaymentElement(elems, startIdx);
                return;
            }
            const idx = elems.indexOf(activeEl);
            if (idx !== -1) {
                this.paymentRowIndex = idx;
            }
            this.updateVisualState();
        },

        /**
         * Focus un élément de la zone PAIEMENT. G/D navigue ET active le mode visé.
         */
        focusPaymentElement(elems, index) {
            this.paymentRowIndex = Math.min(Math.max(index, 0), elems.length - 1);
            this.setState('PAYMENT');
            const el = elems[this.paymentRowIndex];
            if (!el) return;

            this.updateVisualState();
            el.scrollIntoView({ block: 'nearest', behavior: 'smooth' });

            if (el.classList && el.classList.contains('toggle-button')) {
                // Pointer sur le mode (ne pas le surligner comme un champ)
                el.setAttribute('tabindex', '-1');
                el.focus();
            } else if (el.closest && el.closest('.toggle-container')) {
                el.setAttribute('tabindex', '-1');
                el.focus();
            } else {
                // Champ de saisie (input/select)
                el.focus();
                if (el.select && (el.type === 'text' || el.type === 'search' || el.type === 'number')) el.select();
            }
        },

        getPaymentDirectionalIndex(elems, currentIndex, key) {
            if (currentIndex < 0) currentIndex = this.paymentRowIndex;
            const current = elems[currentIndex];
            const toggles = elems.filter(el => el.classList && el.classList.contains('toggle-button') && el.querySelector('input[name="payment-method"]'));
            const toggleIndex = toggles.indexOf(current);
            if (toggleIndex !== -1) {
                if (key === 'ArrowLeft' || key === 'ArrowRight') {
                    const nextToggle = toggleIndex + (key === 'ArrowRight' ? 1 : -1);
                    if (nextToggle >= 0 && nextToggle < toggles.length) return elems.indexOf(toggles[nextToggle]);
                    return currentIndex;
                }
                const nextToggle = toggleIndex + (key === 'ArrowDown' ? 2 : -2);
                if (nextToggle >= 0 && nextToggle < toggles.length) return elems.indexOf(toggles[nextToggle]);
                if (key === 'ArrowDown') {
                    const firstField = elems.findIndex(el => /^(INPUT|SELECT)$/.test(el.tagName || '') && el.type !== 'radio' && el.type !== 'checkbox');
                    return firstField >= 0 ? firstField : currentIndex;
                }
                const multipleLabel = elems.findIndex(el => el.querySelector && el.querySelector('#multiple-payment'));
                return multipleLabel >= 0 ? multipleLabel : currentIndex;
            }
            return Math.min(Math.max(currentIndex + (key === 'ArrowDown' || key === 'ArrowRight' ? 1 : -1), 0), elems.length - 1);
        },

        bindPaymentShortcuts() {
            this.listen(document, 'keydown', (e) => {
                if (this.currentState !== 'PAYMENT') return;

                const elems = this.getPaymentElements();
                if (elems.length === 0) return;

                const isInList = (el) => el && elems.indexOf(el.closest && (el.closest('.toggle-button') || el)) !== -1;

                // FLÈCHES GAUCHE/DROITE : pure navigation entre les modes (SANS activer)
                if (e.key === 'ArrowRight' || e.key === 'ArrowLeft') {
                    const activeEl = document.activeElement;
                    const activeInput = activeEl && activeEl.tagName === 'INPUT' && activeEl.value.length > 0;
                    if (activeInput) return; // laisser naviguer le texte dans un champ de saisie
                    e.preventDefault();
                    const curIdx = activeEl ? elems.indexOf(activeEl.closest('.toggle-button') || activeEl) : -1;
                    const base = curIdx >= 0 ? curIdx : this.paymentRowIndex;
                    this.focusPaymentElement(elems, this.getPaymentDirectionalIndex(elems, base, e.key));
                    return;
                }

                // FLÈCHES HAUT/BAS : navigation verticale (modes + champs de saisie)
                if (e.key === 'ArrowDown' || e.key === 'ArrowUp') {
                    const activeEl = document.activeElement;
                    if (activeEl && activeEl.tagName === 'INPUT') {
                        // Ne pas rediriger les flèches si l'input n'est pas un élément de la liste paiement
                        if (isInList(activeEl)) {
                            e.preventDefault();
                            const current = elems.indexOf(activeEl);
                            this.focusPaymentElement(elems, this.getPaymentDirectionalIndex(elems, current, e.key));
                        }
                        return;
                    }
                    e.preventDefault();
                    const verticalActiveEl = document.activeElement;
                    const current = verticalActiveEl ? elems.indexOf(verticalActiveEl.closest('.toggle-button') || verticalActiveEl) : this.paymentRowIndex;
                    this.focusPaymentElement(elems, this.getPaymentDirectionalIndex(elems, current, e.key));
                    return;
                }

                // ENTRÉE : actionner l'élément focalisé (toggle d'un mode, du multiple, ou paiement)
                if (e.key === 'Enter') {
                    const activeEl = document.activeElement;
                    const mp = document.getElementById('multiple-payment');
                    if (activeEl && mp && (activeEl === mp || activeEl.closest && activeEl.closest('label') && activeEl.closest('label').contains(mp))) {
                        e.preventDefault();
                        mp.click();
                        return;
                    }
                    const modeEl = activeEl && activeEl.closest('.toggle-button');
                    if (modeEl) {
                        e.preventDefault();
                        modeEl.click();
                        return;
                    }
                    const isPaymentInput = activeEl && activeEl.closest('.paiement-section, .input-container');
                    if (isPaymentInput) {
                        e.preventDefault();
                        // Shift/Ctrl+Entrée = validation (prise en charge globalement)
                        if (e.shiftKey || e.ctrlKey) return;

                        // Entrée = passer à l'input suivant (ex. référence Mvola -> montant Mvola)
                        let idx = elems.indexOf(activeEl);
                        if (idx < 0) idx = this.paymentRowIndex;
                        const next = (idx + 1) % elems.length;
                        this.focusPaymentElement(elems, next);
                        return;
                    }
                }
            });
        },

        // ====================================================================
        // 6. VALIDATION PRIMAIRE (Ctrl + Entrer)
        // ====================================================================
        submitPrimaryAction() {
            const form = document.getElementById('venteForm');
            if (!form) return;
            const validateButton = form.querySelector('.btn-primary.btn-large');

            if (validateButton && !validateButton.disabled) {
                validateButton.click();
            }
        }
    };

    window.POSKeyboard = POSKeyboard;

    const initPOSKeyboard = () => {
        // Sur le layout APJ, le contrôleur central choisit et active le moteur.
        if (window.APJKeyboard && window.APJKeyboardAutoInit !== false) return;
        if (window.isKeyboardMode === true) POSKeyboard.init();
    };

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initPOSKeyboard);
    } else {
        initPOSKeyboard();
    }

})(window);
