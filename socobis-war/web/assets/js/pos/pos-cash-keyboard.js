/**
 * POS APJ - Navigation clavier commune aux opérations de caisse.
 *
 * Le module reste inactif tant que window.POSCashPageConfig n'est pas défini.
 */
(function (window) {
    'use strict';

    const STORAGE_KEY = 'posPendingProductSearch';
    const SEARCH_TTL_MS = 30000;
    const PAGE_TITLES = {
        opening: 'Ouverture de caisse',
        closing: 'Clôture de caisse',
        entry: 'Entrée de caisse',
        exit: 'Sortie de caisse',
        transfer: 'Transfert de caisse'
    };

    const POSCashKeyboard = {
        initialized: false,
        config: null,
        helpPreviousFocus: null,
        pendingAmountInput: null,
        finishingBillModal: false,
        boundKeydown: null,
        boundHelpClose: null,
        boundHelpBackdrop: null,

        init(config) {
            if (this.initialized) return;
            this.config = Object.assign({
                formSelector: '#venteForm',
                pageType: 'entry',
                posUrl: ''
            }, config || {});
            this.initialized = true;
            document.body.classList.add('pos-cash-kb-mode');
            this.ensureHelpModal();
            this.decorateCashAmountInputs();
            this.boundKeydown = this.handleKeydown.bind(this);
            document.addEventListener('keydown', this.boundKeydown, true);
            this.bindHelpModal();

            window.setTimeout(() => {
                if (this.initialized && !this.isHelpOpen() && !this.isBillModalOpen()) this.focusFirstControl();
            }, 0);
        },

        destroy() {
            if (this.boundKeydown) {
                document.removeEventListener('keydown', this.boundKeydown, true);
            }
            document.body.classList.remove('pos-cash-kb-mode', 'pos-help-open');
            this.closeHelpModal(false);
            const modal = document.getElementById('pos-cash-help-modal');
            const closeButton = document.getElementById('pos-cash-help-close');
            if (closeButton && this.boundHelpClose) closeButton.removeEventListener('click', this.boundHelpClose);
            if (modal && this.boundHelpBackdrop) modal.removeEventListener('click', this.boundHelpBackdrop);
            this.boundHelpClose = null;
            this.boundHelpBackdrop = null;
            this.initialized = false;
            this.config = null;
            this.boundKeydown = null;
            this.pendingAmountInput = null;
            this.finishingBillModal = false;
        },

        getForm() {
            return this.config ? document.querySelector(this.config.formSelector) : null;
        },

        getPrimaryButton() {
            const form = this.getForm();
            if (!form) return null;
            const candidates = Array.from(form.querySelectorAll('button[type="submit"], input[type="submit"]'));
            return candidates.find(button => !button.disabled && this.isVisible(button)) || null;
        },

        isVisible(element) {
            if (!element || element.hidden || element.closest('[hidden], .d-none')) return false;
            if (!window.getComputedStyle) return true;
            let current = element;
            while (current && current !== document.documentElement) {
                const style = window.getComputedStyle(current);
                if (style.display === 'none' || style.visibility === 'hidden') return false;
                current = current.parentElement;
            }
            return true;
        },

        isInteractiveElement(element) {
            if (!element || element === document.body) return false;
            return /^(INPUT|SELECT|TEXTAREA|BUTTON|A)$/.test(element.tagName || '');
        },

        getFormControls() {
            const form = this.getForm();
            if (!form) return [];
            return Array.from(form.querySelectorAll('input, select, textarea')).filter(element => {
                if (element.type === 'hidden' || element.disabled || element.readOnly) return false;
                if (element.type === 'checkbox' || element.type === 'radio') return false;
                if (element.closest('.clavier-container, .custom-modal, .vd-header')) return false;
                return this.isVisible(element);
            });
        },

        resolveFormControl(element) {
            if (!element) return null;
            if (/^(INPUT|SELECT|TEXTAREA)$/.test(element.tagName || '')) return element;

            const select2Container = element.closest ? element.closest('.select2-container') : null;
            const select = select2Container ? select2Container.previousElementSibling : null;
            return select && select.tagName === 'SELECT' ? select : null;
        },

        getSelect2FocusTarget(select) {
            if (!select || select.tagName !== 'SELECT' || !select.classList.contains('select2-hidden-accessible')) return null;
            const container = select.nextElementSibling;
            if (!container || !container.classList.contains('select2-container')) return null;
            return container.querySelector('.select2-selection[tabindex], .select2-selection');
        },

        isDatepickerInput(input) {
            return !!(input && input.tagName === 'INPUT' && input.classList.contains('datepicker'));
        },

        isDatepickerOpen(input) {
            if (!this.isDatepickerInput(input)) return false;
            if (window.jQuery) {
                const instance = window.jQuery(input).data('datepicker');
                if (instance && instance.picker) return instance.picker.is(':visible');
            }
            return Array.from(document.querySelectorAll('.datepicker-dropdown')).some(this.isVisible.bind(this));
        },

        closeDatepicker(input) {
            if (!this.isDatepickerInput(input)) return;
            if (window.jQuery && typeof window.jQuery(input).datepicker === 'function') {
                window.jQuery(input).datepicker('hide');
            }
        },

        confirmDatepickerAndContinue(input) {
            if (!this.isDatepickerInput(input)) return false;
            window.setTimeout(() => {
                this.closeDatepicker(input);
                input.dispatchEvent(new Event('change', { bubbles: true }));
                this.focusRelativeControl(input, 1);
            }, 0);
            return true;
        },

        focusElement(element) {
            if (!element) return;
            const focusTarget = this.getSelect2FocusTarget(element) || element;
            focusTarget.focus();
            if (typeof element.select === 'function' && element.tagName === 'INPUT') {
                element.select();
            }
            if (typeof focusTarget.scrollIntoView === 'function') {
                focusTarget.scrollIntoView({ block: 'nearest', behavior: 'smooth' });
            }
        },

        focusRelativeControl(current, delta) {
            const controls = this.getFormControls();
            const control = this.resolveFormControl(current) || current;
            const index = controls.indexOf(control);
            if (index < 0 || controls.length === 0) return false;
            let targetIndex = index + delta;
            // En remontant depuis un champ situé sous un select, ne pas piéger
            // le focus dans les flèches natives du select : viser le champ précédent.
            if (delta < 0) {
                while (targetIndex >= 0 && controls[targetIndex].tagName === 'SELECT') {
                    targetIndex--;
                }
            }
            if (targetIndex >= 0 && targetIndex < controls.length) {
                this.focusElement(controls[targetIndex]);
            } else if (targetIndex >= controls.length) {
                this.focusElement(this.getPrimaryButton());
            }
            return true;
        },

        focusFirstControl() {
            const controls = this.getFormControls();
            if (controls.length > 0) this.focusElement(controls[0]);
        },

        submitPrimaryAction() {
            const button = this.getPrimaryButton();
            if (!button || button.disabled || !this.isVisible(button)) return false;
            button.click();
            return true;
        },

        getSearchInput() {
            return document.querySelector('.vd-header #idProduit_libelle');
        },

        focusSearch() {
            const search = this.getSearchInput();
            if (!search) return this.handoffSearch('');
            this.focusElement(search);
            return true;
        },

        focusHeader() {
            const elements = this.getHeaderElements();
            if (elements.length === 0) return false;
            this.focusElement(elements[0]);
            return true;
        },

        handoffSearch(value) {
            if (!this.config || !this.config.posUrl) return false;
            const payload = {
                value: String(value || '').trim(),
                timestamp: Date.now()
            };
            try {
                window.sessionStorage.setItem(STORAGE_KEY, JSON.stringify(payload));
            } catch (error) {
                // La navigation reste possible si le stockage navigateur est indisponible.
            }

            if (typeof this.config.navigate === 'function') {
                this.config.navigate(this.config.posUrl);
            } else {
                window.location.assign(this.config.posUrl);
            }
            return true;
        },

        getHeaderElements() {
            const header = document.querySelector('.vd-header');
            if (!header) return [];
            return Array.from(header.querySelectorAll('a[href], button:not([disabled])')).filter(element => {
                if (element.closest('.dropdown-menu')) return false;
                return this.isVisible(element);
            });
        },

        getDropdown(button) {
            return button && button.closest ? button.closest('.dropdown') : null;
        },

        getDropdownItems(dropdown) {
            if (!dropdown) return [];
            return Array.from(dropdown.querySelectorAll('.dropdown-menu a[href]')).filter(this.isVisible.bind(this));
        },

        openDropdown(button) {
            const dropdown = this.getDropdown(button);
            if (!dropdown) return false;
            if (!dropdown.classList.contains('open')) button.click();
            dropdown.classList.add('open');
            button.setAttribute('aria-expanded', 'true');
            const items = this.getDropdownItems(dropdown);
            if (items.length > 0) this.focusElement(items[0]);
            return true;
        },

        closeDropdown(dropdown, restoreFocus) {
            if (!dropdown) return;
            const button = dropdown.querySelector('.dropdown-toggle');
            dropdown.classList.remove('open');
            if (button) {
                button.setAttribute('aria-expanded', 'false');
                if (restoreFocus) this.focusElement(button);
            }
        },

        handleHeaderKeydown(event) {
            const target = event.target;
            const header = target && target.closest ? target.closest('.vd-header') : null;
            if (!header) return false;

            const search = this.getSearchInput();
            if (target === search) {
                if (event.key === 'Enter' && !event.isComposing) {
                    event.preventDefault();
                    this.handoffSearch(search.value);
                    return true;
                }
                return false;
            }

            const dropdownItem = target.closest('.dropdown-menu a[href]');
            if (dropdownItem) {
                const dropdown = dropdownItem.closest('.dropdown');
                const items = this.getDropdownItems(dropdown);
                const index = items.indexOf(dropdownItem);
                if (event.key === 'ArrowDown' || event.key === 'ArrowUp') {
                    event.preventDefault();
                    const delta = event.key === 'ArrowDown' ? 1 : -1;
                    const next = (index + delta + items.length) % items.length;
                    this.focusElement(items[next]);
                    return true;
                }
                if (event.key === 'Escape' || event.key === 'ArrowLeft') {
                    event.preventDefault();
                    this.closeDropdown(dropdown, true);
                    return true;
                }
                if (event.key === 'Enter') {
                    event.preventDefault();
                    dropdownItem.click();
                    return true;
                }
                return false;
            }

            const elements = this.getHeaderElements();
            const current = target.closest('a[href], button');
            const index = elements.indexOf(current);
            if (index < 0) return false;

            if (event.key === 'ArrowRight' || event.key === 'ArrowLeft') {
                event.preventDefault();
                const delta = event.key === 'ArrowRight' ? 1 : -1;
                const next = (index + delta + elements.length) % elements.length;
                this.focusElement(elements[next]);
                return true;
            }
            if ((event.key === 'ArrowDown' || event.key === 'Enter') && current.classList.contains('dropdown-toggle')) {
                event.preventDefault();
                this.openDropdown(current);
                return true;
            }
            if (event.key === 'Enter') {
                event.preventDefault();
                current.click();
                return true;
            }
            if (event.key === 'ArrowDown') {
                event.preventDefault();
                this.focusFirstControl();
                return true;
            }
            return false;
        },

        isCashAmountInput(input) {
            if (!this.config || !this.config.billetage || !input || !/^montant_\d+$/.test(input.id || '')) {
                return false;
            }
            const index = input.id.replace('montant_', '');
            const label = document.getElementById('caisseLib_' + index);
            const labelText = label ? (label.value || label.textContent || '') : '';
            return /esp.{1,2}ce/i.test(labelText);
        },

        decorateCashAmountInputs() {
            const form = this.getForm();
            if (!form || !this.config || !this.config.billetage) return 0;

            let decorated = 0;
            Array.from(form.querySelectorAll('input[id^="montant_"]')).forEach(input => {
                if (!this.isCashAmountInput(input)) return;
                input.setAttribute('placeholder', 'Tapez ou appuyez sur Entrée pour le billetage');
                input.setAttribute('title', 'Tapez ou appuyez sur Entrée pour afficher le sélecteur de monnaies');
                if (!input.getAttribute('aria-label')) {
                    input.setAttribute('aria-label', 'Montant espèces — tapez ou appuyez sur Entrée pour ouvrir le sélecteur de monnaies');
                }
                decorated++;
            });
            return decorated;
        },

        openBillModal(input) {
            if (!this.isCashAmountInput(input) || this.isBillModalOpen()) return false;
            this.pendingAmountInput = input;
            input.dispatchEvent(new Event('pos-cash-open-billetage', { bubbles: true }));
            return true;
        },

        getBillModal() {
            return document.getElementById('montantModal');
        },

        isBillModalOpen() {
            const modal = this.getBillModal();
            return !!(modal && modal.classList.contains('is-open'));
        },

        getBillModalInputs() {
            const modal = this.getBillModal();
            if (!modal) return [];
            return Array.from(modal.querySelectorAll('.denomination-input')).filter(element => {
                return !element.disabled && !element.readOnly && this.isVisible(element);
            });
        },

        finishBillModal(validate) {
            const modal = this.getBillModal();
            if (!modal) return false;
            const button = modal.querySelector(validate ? '#montantModalValidate' : '#montantModalCancel, #montantModalClose');
            if (!button || button.disabled) return false;
            const amountInput = this.pendingAmountInput;
            this.finishingBillModal = true;
            try {
                button.click();
            } finally {
                this.finishingBillModal = false;
            }
            this.pendingAmountInput = null;
            if (amountInput) this.focusRelativeControl(amountInput, 1);
            return true;
        },

        handleBillModalKeydown(event) {
            if (!this.isBillModalOpen()) return false;
            const modal = this.getBillModal();
            const target = event.target;

            if (event.key === 'Escape') {
                event.preventDefault();
                this.finishBillModal(false);
                return true;
            }
            if ((event.ctrlKey || event.shiftKey) && event.key === 'Enter') {
                event.preventDefault();
                this.finishBillModal(true);
                return true;
            }
            if (event.key !== 'ArrowDown' && event.key !== 'ArrowUp' && event.key !== 'Enter') return true;

            const inputs = this.getBillModalInputs();
            const index = inputs.indexOf(target);
            const validateButton = modal.querySelector('#montantModalValidate');
            if (event.key === 'Enter' && target === validateButton) {
                event.preventDefault();
                this.finishBillModal(true);
                return true;
            }
            if (index < 0) return true;

            event.preventDefault();
            const delta = event.key === 'ArrowUp' ? -1 : 1;
            const nextIndex = index + delta;
            if (nextIndex >= 0 && nextIndex < inputs.length) {
                this.focusElement(inputs[nextIndex]);
            } else if (nextIndex >= inputs.length && validateButton) {
                this.focusElement(validateButton);
            }
            return true;
        },

        ensureHelpModal() {
            if (document.getElementById('pos-cash-help-modal')) return;
            const title = PAGE_TITLES[this.config.pageType] || 'Opération de caisse';
            const modal = document.createElement('div');
            modal.id = 'pos-cash-help-modal';
            modal.className = 'pos-help-modal';
            modal.setAttribute('role', 'dialog');
            modal.setAttribute('aria-modal', 'true');
            modal.setAttribute('aria-labelledby', 'pos-cash-help-title');
            modal.setAttribute('aria-hidden', 'true');
            modal.innerHTML = `
                <div class="pos-help-modal-content">
                    <div class="pos-help-modal-header">
                        <h2 id="pos-cash-help-title" class="h617pxSemibold">Raccourcis — ${title}</h2>
                        <button type="button" class="pos-help-close" id="pos-cash-help-close" aria-label="Fermer l'aide">&times;</button>
                    </div>
                    <div class="pos-help-modal-body">
                        <section class="pos-help-section">
                            <h3 class="h617pxSemibold">Global</h3>
                            <div class="pos-help-list">
                                <div class="pos-help-row"><span class="Body14pxRegular">Ctrl / Shift + Entrée</span><p class="Body14pxRegular">Valider l'opération courante</p></div>
                                <div class="pos-help-row"><span class="Body14pxRegular">F1 / Ctrl + H</span><p class="Body14pxRegular">Afficher ou masquer cette aide</p></div>
                                <div class="pos-help-row"><span class="Body14pxRegular">Shift + H</span><p class="Body14pxRegular">Accéder aux actions du header</p></div>
                                <div class="pos-help-row"><span class="Body14pxRegular">Ctrl + F</span><p class="Body14pxRegular">Ouvrir la recherche de la vente directe</p></div>
                                <div class="pos-help-row"><span class="Body14pxRegular">Tab / Shift + Tab</span><p class="Body14pxRegular">Navigation standard entre les contrôles</p></div>
                            </div>
                        </section>
                        <section class="pos-help-section">
                            <h3 class="h617pxSemibold">Navigation</h3>
                            <div class="pos-help-list">
                                <div class="pos-help-row"><span class="Body14pxRegular">Haut / Bas</span><p class="Body14pxRegular">Parcourir les champs et les éléments de liste</p></div>
                                <div class="pos-help-row"><span class="Body14pxRegular">Gauche / Droite</span><p class="Body14pxRegular">Parcourir les actions du header</p></div>
                                <div class="pos-help-row"><span class="Body14pxRegular">Entrée</span><p class="Body14pxRegular">Continuer ou activer l'élément ciblé</p></div>
                                <div class="pos-help-row"><span class="Body14pxRegular">Saisie / Entrée sur espèces</span><p class="Body14pxRegular">Ouvrir le sélecteur de monnaies</p></div>
                                <div class="pos-help-row"><span class="Body14pxRegular">Calendrier ouvert</span><p class="Body14pxRegular">Flèches pour choisir une date, Entrée pour confirmer et continuer</p></div>
                                <div class="pos-help-row"><span class="Body14pxRegular">Échap</span><p class="Body14pxRegular">Fermer un menu, le billetage ou cette aide</p></div>
                            </div>
                        </section>
                    </div>
                </div>`;
            document.body.appendChild(modal);
        },

        bindHelpModal() {
            const modal = document.getElementById('pos-cash-help-modal');
            const closeButton = document.getElementById('pos-cash-help-close');
            this.boundHelpClose = () => this.closeHelpModal(true);
            this.boundHelpBackdrop = event => {
                if (event.target === modal) this.closeHelpModal(true);
            };
            if (closeButton) closeButton.addEventListener('click', this.boundHelpClose);
            if (modal) modal.addEventListener('click', this.boundHelpBackdrop);
        },

        isHelpOpen() {
            const modal = document.getElementById('pos-cash-help-modal');
            return !!(modal && modal.classList.contains('is-open'));
        },

        openHelpModal() {
            const modal = document.getElementById('pos-cash-help-modal');
            if (!modal) return;
            this.helpPreviousFocus = document.activeElement;
            modal.classList.add('is-open');
            modal.setAttribute('aria-hidden', 'false');
            document.body.classList.add('pos-help-open');
            this.focusElement(document.getElementById('pos-cash-help-close'));
        },

        closeHelpModal(restoreFocus) {
            const modal = document.getElementById('pos-cash-help-modal');
            if (!modal) return;
            modal.classList.remove('is-open');
            modal.setAttribute('aria-hidden', 'true');
            document.body.classList.remove('pos-help-open');
            if (restoreFocus && this.helpPreviousFocus && document.contains(this.helpPreviousFocus)) {
                this.focusElement(this.helpPreviousFocus);
            }
            this.helpPreviousFocus = null;
        },

        toggleHelpModal() {
            if (this.isHelpOpen()) this.closeHelpModal(true);
            else this.openHelpModal();
        },

        handleFormKeydown(event) {
            const form = this.getForm();
            const target = event.target;
            if (!form || !target || !form.contains(target)) return false;
            if (target.closest('.clavier-container, .custom-modal')) return false;
            const control = this.resolveFormControl(target);
            if (!control) return false;
            if (event.key === 'Tab') return false;
            if (control.tagName === 'TEXTAREA' && event.key === 'Enter') return false;
            if (this.isDatepickerOpen(control)) {
                if (event.key === 'ArrowLeft' || event.key === 'ArrowRight' || event.key === 'ArrowUp' || event.key === 'ArrowDown') {
                    return false;
                }
                if (event.key === 'Enter' && !event.ctrlKey && !event.shiftKey) {
                    return this.confirmDatepickerAndContinue(control);
                }
                if (event.key === 'Escape') return false;
            }

            if (control.tagName === 'SELECT') {
                if (event.key === 'Enter' || event.key === 'Escape') return false;
                if (event.key === 'ArrowUp' || event.key === 'ArrowDown') {
                    event.preventDefault();
                    return this.focusRelativeControl(control, event.key === 'ArrowDown' ? 1 : -1);
                }
            }

            if (event.key === 'ArrowDown' || event.key === 'ArrowUp') {
                event.preventDefault();
                return this.focusRelativeControl(control, event.key === 'ArrowDown' ? 1 : -1);
            }
            if (event.key === 'Enter' && !event.ctrlKey && !event.shiftKey) {
                if (control === this.getPrimaryButton()) return false;
                event.preventDefault();
                if (this.openBillModal(control)) return true;
                return this.focusRelativeControl(control, 1);
            }
            return false;
        },

        handleKeydown(event) {
            if (!event || event.isComposing || event.key === 'Process') return false;

            const isHelpShortcut = event.key === 'F1' || (event.ctrlKey && !event.altKey && !event.metaKey && /^h$/i.test(event.key));
            if (isHelpShortcut) {
                event.preventDefault();
                this.toggleHelpModal();
                return true;
            }
            if (this.isHelpOpen()) {
                if (event.key === 'Escape') {
                    event.preventDefault();
                    this.closeHelpModal(true);
                }
                return true;
            }

            if (this.isBillModalOpen()) return this.handleBillModalKeydown(event);

            if (!event.ctrlKey && event.shiftKey && !event.altKey && !event.metaKey && /^h$/i.test(event.key)) {
                event.preventDefault();
                return this.focusHeader();
            }
            if (!event.shiftKey && event.ctrlKey && !event.altKey && !event.metaKey && /^f$/i.test(event.key)) {
                event.preventDefault();
                return this.focusSearch();
            }
            if (!event.altKey && !event.metaKey && (event.ctrlKey || event.shiftKey) && event.key === 'Enter') {
                event.preventDefault();
                return this.submitPrimaryAction();
            }
            if (event.altKey || event.metaKey) return false;

            if (this.handleHeaderKeydown(event)) return true;
            return this.handleFormKeydown(event);
        }
    };

    window.POSCashKeyboard = POSCashKeyboard;
    window.POS_CASH_SEARCH_STORAGE_KEY = STORAGE_KEY;
    window.POS_CASH_SEARCH_TTL_MS = SEARCH_TTL_MS;

    function boot() {
        if (window.APJKeyboard && window.APJKeyboardAutoInit !== false) return;
        if (window.POSSelectKeyboard) window.POSSelectKeyboard.init();
        if (window.POSCashPageConfig) POSCashKeyboard.init(window.POSCashPageConfig);
    }

    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', boot);
    else boot();
})(window);
