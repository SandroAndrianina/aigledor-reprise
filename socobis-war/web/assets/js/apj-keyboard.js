/**
 * APJ - Navigation clavier globale.
 *
 * Ce moteur couvre les pages APJ standards. Les écrans POS, caisse et les
 * pages qui possèdent leur propre moteur clavier gardent toujours la priorité.
 */
(function (window) {
    'use strict';

    const SPECIALIZED_CLASSES = ['pos-kb-mode', 'pos-cash-kb-mode', 'pos-page-kb-mode'];
    const FOCUSABLE_SELECTOR = [
        'input:not([type="hidden"])',
        'select',
        'textarea',
        'button',
        'a[href]',
        '[tabindex]:not([tabindex="-1"])',
        '[role="button"]'
    ].join(',');
    const EDITABLE_SELECTOR = 'input:not([type="hidden"]), select, textarea';
    const ROW_SELECTOR = '#table-apj tbody tr, .table-container table tbody tr, .table-responsive table tbody tr';

    const APJKeyboard = {
        initialized: false,
        enabled: false,
        enabledOverride: null,
        currentPageMode: null,
        pageDefaultEnabled: false,
        managedEngine: null,
        syncingMode: false,
        lastContentFocus: null,
        modalReturnFocus: null,
        helpPreviousFocus: null,
        bodyObserver: null,
        contentObserver: null,
        refreshTimer: null,
        focusTimer: null,
        boundKeydown: null,
        boundFocusIn: null,
        boundDynamicContent: null,
        boundReady: null,

        init() {
            if (this.initialized) {
                this.syncMode();
                this.refresh();
                return this;
            }
            this.initialized = true;
            this.enabled = false;
            this.enabledOverride = null;
            this.currentPageMode = null;
            this.pageDefaultEnabled = false;
            this.boundKeydown = this.handleKeydown.bind(this);
            this.boundFocusIn = this.handleFocusIn.bind(this);
            this.boundDynamicContent = () => this.refresh(true);
            this.boundReady = () => {
                this.boundReady = null;
                this.start();
            };
            document.addEventListener('keydown', this.boundKeydown, true);
            document.addEventListener('focusin', this.boundFocusIn, true);
            document.addEventListener('dynamicContentLoaded', this.boundDynamicContent);
            if (document.readyState === 'loading') {
                document.addEventListener('DOMContentLoaded', this.boundReady, { once: true });
            } else {
                this.start();
            }
            return this;
        },

        start() {
            this.ensureHelpModal();
            this.bindBootstrapModals();
            this.observePage();
            this.syncMode();
            this.refresh(true);
        },

        destroy() {
            if (!this.initialized) return;
            document.removeEventListener('keydown', this.boundKeydown, true);
            document.removeEventListener('focusin', this.boundFocusIn, true);
            document.removeEventListener('dynamicContentLoaded', this.boundDynamicContent);
            if (this.boundReady) document.removeEventListener('DOMContentLoaded', this.boundReady);
            if (this.bodyObserver) this.bodyObserver.disconnect();
            if (this.contentObserver) this.contentObserver.disconnect();
            if (window.jQuery) {
                window.jQuery(document).off('.apjKeyboard');
            }
            window.clearTimeout(this.refreshTimer);
            window.clearTimeout(this.focusTimer);
            this.closeHelp(false);
            this.stopManagedEngine();
            if (document.body) document.body.classList.remove('apj-kb-mode');
            this.initialized = false;
            this.bodyObserver = null;
            this.contentObserver = null;
            this.lastContentFocus = null;
            this.modalReturnFocus = null;
            this.enabledOverride = null;
            this.currentPageMode = null;
            this.pageDefaultEnabled = false;
        },

        isSpecializedMode() {
            if (!document.body) return false;
            return SPECIALIZED_CLASSES.some(className => document.body.classList.contains(className)) ||
                !!document.querySelector('[data-pos-screen]');
        },

        isActive() {
            return this.initialized && this.enabled && !!document.body &&
                document.body.classList.contains('apj-kb-mode') && !this.isSpecializedMode();
        },

        getPageMode() {
            if (document.querySelector('[data-pos-screen]')) return 'pos';
            const cash = window.POSCashPageConfig;
            if (cash && document.querySelector(cash.formSelector || '#venteForm')) return 'cash';
            const page = window.POSPageKeyboardConfig;
            if (page && document.querySelector(page.containerSelector || page.formSelector || 'form')) return 'page';
            return 'apj';
        },

        getMode() {
            return this.enabled ? this.getPageMode() : 'off';
        },

        setEnabled(enabled) {
            this.enabledOverride = enabled === true;
            this.enabled = this.enabledOverride;
            this.syncMode();
            if (this.enabled) this.refresh(true);
            return this;
        },

        stopManagedEngine() {
            const engine = this.managedEngine;
            this.managedEngine = null;
            if (engine === 'pos') {
                if (window.POSKeyboard) window.POSKeyboard.destroy();
                window.isKeyboardMode = false;
            }
            if (engine === 'cash' && window.POSCashKeyboard) window.POSCashKeyboard.destroy();
            if (engine === 'page' && window.POSPageKeyboard) window.POSPageKeyboard.destroy();
        },

        syncMode() {
            if (!document.body || this.syncingMode) return false;
            this.syncingMode = true;
            try {
                const pageMode = this.getPageMode();
                if (this.currentPageMode !== pageMode) {
                    const firstPage = this.currentPageMode === null;
                    if (!firstPage) this.enabledOverride = null;
                    // Désactivé par défaut sur chaque page ; une activation explicite
                    // avant le chargement ou via setEnabled(true) reste possible.
                    // this.pageDefaultEnabled = firstPage && window.isKeyboardMode === true;
                    this.pageDefaultEnabled = true; // setting deafault keyboard mode on
                    this.currentPageMode = pageMode;
                }
                const posScreen = pageMode === 'pos' && document.querySelector('[data-pos-screen]');
                const defaultEnabled = posScreen
                    ? posScreen.getAttribute('data-keyboard-default') !== 'off'
                    : this.pageDefaultEnabled;
                this.enabled = this.enabledOverride !== null ? this.enabledOverride : defaultEnabled;
                const mode = this.getMode();
                if (this.managedEngine && this.managedEngine !== mode) this.stopManagedEngine();
                window.isKeyboardMode = this.enabled; // Drapeau commun et compatibilité POS.
                if (mode === 'pos') {
                    if (window.POSKeyboard) {
                        if (!window.POSKeyboard.initialized) window.POSKeyboard.init();
                        this.managedEngine = 'pos';
                    }
                } else if (mode === 'cash' && window.POSCashKeyboard) {
                    if (!window.POSCashKeyboard.initialized) window.POSCashKeyboard.init(window.POSCashPageConfig);
                    this.managedEngine = 'cash';
                } else if (mode === 'page' && window.POSPageKeyboard) {
                    if (!window.POSPageKeyboard.initialized) window.POSPageKeyboard.init(window.POSPageKeyboardConfig);
                    this.managedEngine = 'page';
                } else if (this.getPageMode() === 'pos') {
                    window.isKeyboardMode = false;
                    document.body.classList.remove('pos-kb-mode');
                }
                const active = mode === 'apj' && !this.isSpecializedMode();
                document.body.classList.toggle('apj-kb-mode', active);
                if (!active && this.isHelpOpen()) this.closeHelp(false);
                if (!this.enabled && window.POSSelectKeyboard) window.POSSelectKeyboard.close(false);
                return active;
            } finally {
                this.syncingMode = false;
            }
        },

        observePage() {
            if (!window.MutationObserver || !document.body) return;
            if (this.bodyObserver) this.bodyObserver.disconnect();
            this.bodyObserver = new MutationObserver(() => this.syncMode());
            this.bodyObserver.observe(document.body, { attributes: true, attributeFilter: ['class'] });

            const content = document.getElementById('butjsp') || document.querySelector('.container-apj');
            if (this.contentObserver) this.contentObserver.disconnect();
            if (content) {
                this.contentObserver = new MutationObserver(() => this.scheduleRefresh(false));
                this.contentObserver.observe(content, { childList: true, subtree: true });
            }
        },

        scheduleRefresh(focusInitial) {
            window.clearTimeout(this.refreshTimer);
            this.refreshTimer = window.setTimeout(() => this.refresh(focusInitial), 20);
        },

        refresh(focusInitial) {
            if (!document.body) return this;
            this.syncMode();
            this.decorateHeader();
            this.decorateMenu();
            this.decorateTables();
            if (focusInitial && this.isActive()) {
                window.clearTimeout(this.focusTimer);
                this.focusTimer = window.setTimeout(() => this.focusInitial(), 60);
            }
            return this;
        },

        isIgnored(element) {
            return !!(element && element.closest && element.closest('[data-apj-keyboard-ignore], [data-apj-keyboard-native]'));
        },

        isVisible(element) {
            if (!element || !document.documentElement.contains(element) || element.hidden) return false;
            if (element.tagName === 'SELECT' && element.classList.contains('select2-hidden-accessible')) {
                const select2 = element.nextElementSibling;
                return !!(select2 && this.isVisible(select2));
            }
            let current = element;
            while (current && current.nodeType === 1) {
                if (current.hidden || current.classList.contains('d-none') || current.classList.contains('hidden')) return false;
                if (window.getComputedStyle) {
                    const style = window.getComputedStyle(current);
                    if (style.display === 'none' || style.visibility === 'hidden') return false;
                }
                current = current.parentElement;
            }
            return true;
        },

        isEnabled(element) {
            return !!(element && !element.disabled && element.getAttribute('aria-disabled') !== 'true');
        },

        isEditable(element) {
            if (!element || !element.matches || !element.matches(EDITABLE_SELECTOR)) return false;
            return this.isVisible(element) && this.isEnabled(element) && !element.readOnly && !this.isIgnored(element);
        },

        uniqueVisible(elements) {
            const seen = [];
            return Array.from(elements || []).map(element => this.normalizeControl(element)).filter(element => {
                if (seen.indexOf(element) >= 0 || !this.isVisible(element) || !this.isEnabled(element) || this.isIgnored(element)) return false;
                seen.push(element);
                return true;
            });
        },

        normalizeControl(element) {
            if (!element || !element.closest) return element;
            const select2 = element.closest('.select2-container');
            if (!select2) return element;
            const select = select2.previousElementSibling;
            return select && select.tagName === 'SELECT' ? select : element;
        },

        getContentRoot() {
            return document.getElementById('butjsp') || document.querySelector('.container-apj') || document.querySelector('.content-wrapper') || document.body;
        },

        getActiveModal() {
            const candidates = document.querySelectorAll('.modal.in, .modal.show, [role="dialog"][aria-hidden="false"]');
            for (let index = candidates.length - 1; index >= 0; index--) {
                if (this.isVisible(candidates[index])) return candidates[index];
            }
            return null;
        },

        getControls(scope) {
            const root = scope || this.getActiveModal() || this.getContentRoot();
            if (!root) return [];
            return this.uniqueVisible(root.querySelectorAll(FOCUSABLE_SELECTOR));
        },

        getEditableControls(scope) {
            const root = scope || this.getActiveModal() || this.getContentRoot();
            if (!root) return [];
            return Array.from(root.querySelectorAll(EDITABLE_SELECTOR)).filter(element => this.isEditable(element));
        },

        focusElement(element) {
            if (!element || !this.isVisible(element)) return false;
            let target = element;
            if (element.tagName === 'SELECT' && element.classList.contains('select2-hidden-accessible')) {
                const container = element.nextElementSibling;
                target = container && container.querySelector('.select2-selection') || container || element;
            }
            if (!target.hasAttribute('tabindex') && !/^(INPUT|SELECT|TEXTAREA|BUTTON|A)$/.test(target.tagName)) {
                target.setAttribute('tabindex', '0');
            }
            if (typeof target.focus === 'function') {
                target.focus();
                return true;
            }
            return false;
        },

        focusInitial() {
            if (!this.isActive() || this.getActiveModal()) return false;
            const filterButton = Array.from(document.querySelectorAll('.btn-filtre[data-target="#filtre-modal"]'))
                .find(button => this.isVisible(button) && this.isEnabled(button));
            if (filterButton) return this.focusElement(filterButton);

            const root = this.getContentRoot();
            const legacyFilter = root && Array.from(root.querySelectorAll('#btnListe'))
                .find(button => this.isVisible(button) && this.isEnabled(button));
            if (legacyFilter) return this.focusElement(legacyFilter);
            const firstEditable = this.getEditableControls(root)[0];
            return this.focusElement(firstEditable);
        },

        handleFocusIn(event) {
            if (!this.isActive() || !event.target) return;
            const content = this.getContentRoot();
            if (content && content.contains(event.target) && !this.getActiveModal()) {
                this.lastContentFocus = event.target;
            }
        },

        decorateHeader() {
            const header = document.querySelector('.main-header');
            if (!header) return;
            const controls = header.querySelectorAll('.sidebar-toggle, #user-login-span, #notifrefresh > *, [onclick], [role="button"]');
            controls.forEach(control => {
                if (!control.matches('button, a[href], input, [tabindex]')) control.setAttribute('tabindex', '0');
                if (!control.hasAttribute('role') && !control.matches('button, a[href], input')) control.setAttribute('role', 'button');
            });
        },

        decorateMenu() {
            document.querySelectorAll('.sidebar-icon-item').forEach(item => {
                item.setAttribute('tabindex', '0');
                item.setAttribute('role', 'button');
            });
            document.querySelectorAll('.submenu-item').forEach(item => item.setAttribute('tabindex', '0'));
        },

        decorateTables() {
            document.querySelectorAll(ROW_SELECTOR).forEach(row => {
                if (!this.isVisible(row) || this.isIgnored(row) || row.closest('thead') || row.classList.contains('d-none')) return;
                if (!row.hasAttribute('tabindex')) row.setAttribute('tabindex', '0');
                row.classList.add('apj-kb-table-row');
            });
        },

        getHeaderControls() {
            const header = document.querySelector('.main-header');
            if (!header) return [];
            return this.uniqueVisible(header.querySelectorAll('.sidebar-toggle, .global-search-input, .navbar-custom-menu a[href], .navbar-custom-menu button, .navbar-custom-menu [role="button"], .navbar-custom-menu [tabindex]'));
        },

        focusHeader() {
            this.decorateHeader();
            return this.focusElement(this.getHeaderControls()[0]);
        },

        ensureSubmenuVisible(item) {
            const panel = document.getElementById('sidebarSubmenuPanel');
            if (panel) {
                panel.classList.remove('hidden');
                panel.setAttribute('data-was-visible', 'true');
            }
            document.body.classList.remove('sidebar-collapse', 'submenu-closed');
            let parent = item && item.parentElement;
            while (parent && panel && panel.contains(parent)) {
                if (parent.classList.contains('submenu-children')) {
                    parent.classList.add('open');
                    if (parent.previousElementSibling) parent.previousElementSibling.classList.add('open');
                }
                parent = parent.parentElement;
            }
        },

        focusMenu() {
            this.decorateMenu();
            let active = document.querySelector('.submenu-item.active');
            if (active) {
                this.ensureSubmenuVisible(active);
                return this.focusElement(active);
            }
            const activeIcon = document.querySelector('.sidebar-icon-item.active');
            if (activeIcon) {
                return this.focusElement(activeIcon);
            }
            return this.focusElement(document.querySelector('.sidebar-icon-item'));
        },

        returnToContent() {
            if (this.lastContentFocus && this.isVisible(this.lastContentFocus) && this.isEnabled(this.lastContentFocus)) {
                return this.focusElement(this.lastContentFocus);
            }
            return this.focusInitial();
        },

        isInMenu(element) {
            return !!(element && element.closest && element.closest('.main-sidebar'));
        },

        isInHeader(element) {
            return !!(element && element.closest && element.closest('.main-header'));
        },

        handleHeader(event, target) {
            if (!this.isInHeader(target)) return false;
            if (event.key === 'Escape') {
                const popup = document.getElementById('custom-popup');
                if (popup && this.isVisible(popup)) popup.style.display = 'none';
                document.querySelectorAll('.main-header .open').forEach(element => element.classList.remove('open'));
                return true;
            }
            if (event.key === 'ArrowLeft' || event.key === 'ArrowRight') {
                const controls = this.getHeaderControls();
                const index = controls.indexOf(target);
                if (index < 0 || controls.length === 0) return false;
                event.preventDefault();
                return this.focusElement(controls[(index + (event.key === 'ArrowRight' ? 1 : -1) + controls.length) % controls.length]);
            }
            if (event.key === 'Enter' && !target.matches('input, textarea, select')) {
                event.preventDefault();
                target.click();
                return true;
            }
            return false;
        },

        getVisibleIcons() {
            return this.uniqueVisible(document.querySelectorAll('.sidebar-icon-item'));
        },

        getVisibleSubmenuItems() {
            return this.uniqueVisible(document.querySelectorAll('#sidebarSubmenuPanel .submenu-item'));
        },

        handleMenu(event, target) {
            if (!this.isInMenu(target)) return false;
            const icon = target.closest('.sidebar-icon-item');
            const submenu = target.closest('.submenu-item');
            if (icon) {
                const icons = this.getVisibleIcons();
                const index = icons.indexOf(icon);
                if (event.key === 'ArrowUp' || event.key === 'ArrowDown') {
                    event.preventDefault();
                    return this.focusElement(icons[(index + (event.key === 'ArrowDown' ? 1 : -1) + icons.length) % icons.length]);
                }
                if (event.key === 'ArrowRight' || event.key === 'Enter') {
                    event.preventDefault();
                    icon.click();
                    window.setTimeout(() => {
                        this.decorateMenu();
                        this.focusElement(document.querySelector('#sidebarSubmenuPanel .submenu-item.active') || document.querySelector('#sidebarSubmenuPanel .submenu-item'));
                    }, 20);
                    return true;
                }
                return false;
            }
            if (!submenu) return false;
            const items = this.getVisibleSubmenuItems();
            const index = items.indexOf(submenu);
            if (event.key === 'ArrowUp' || event.key === 'ArrowDown') {
                event.preventDefault();
                return this.focusElement(items[(index + (event.key === 'ArrowDown' ? 1 : -1) + items.length) % items.length]);
            }
            if (event.key === 'ArrowRight') {
                const children = submenu.nextElementSibling;
                if (children && children.classList.contains('submenu-children')) {
                    event.preventDefault();
                    if (!children.classList.contains('open')) submenu.click();
                    return this.focusElement(children.querySelector('.submenu-item'));
                }
            }
            if (event.key === 'ArrowLeft') {
                event.preventDefault();
                const children = submenu.closest('.submenu-children');
                if (children) {
                    const parent = children.previousElementSibling;
                    children.classList.remove('open');
                    if (parent) {
                        parent.classList.remove('open');
                        return this.focusElement(parent);
                    }
                }
                return this.focusElement(document.querySelector('.sidebar-icon-item.active') || this.getVisibleIcons()[0]);
            }
            if (event.key === 'Enter') {
                event.preventDefault();
                submenu.click();
                return true;
            }
            if (event.key === 'Escape') {
                event.preventDefault();
                document.body.classList.add('sidebar-collapse', 'submenu-closed');
                const panel = document.getElementById('sidebarSubmenuPanel');
                if (panel) panel.classList.add('hidden');
                return this.returnToContent();
            }
            return false;
        },

        getTableRows(row) {
            const table = row && row.closest('table');
            return table ? this.uniqueVisible(table.querySelectorAll('tbody tr.apj-kb-table-row')) : [];
        },

        getRowActions(row) {
            return row ? this.uniqueVisible(row.querySelectorAll('a[href], button, input[type="button"], input[type="submit"], [role="button"], [tabindex]:not([tabindex="-1"])')) : [];
        },

        handleTable(event, target) {
            const row = target.closest && target.closest('tr.apj-kb-table-row');
            if (!row) return false;
            const action = target === row ? null : target.closest('a[href], button, input[type="button"], input[type="submit"], [role="button"], [tabindex]');
            if (event.key === 'ArrowUp' || event.key === 'ArrowDown') {
                const rows = this.getTableRows(row);
                const index = rows.indexOf(row);
                if (index < 0) return false;
                event.preventDefault();
                return this.focusElement(rows[Math.min(Math.max(index + (event.key === 'ArrowDown' ? 1 : -1), 0), rows.length - 1)]);
            }
            if (event.key === 'ArrowLeft' || event.key === 'ArrowRight') {
                const actions = this.getRowActions(row);
                if (!actions.length) return false;
                let index = action ? actions.indexOf(action) : (event.key === 'ArrowRight' ? -1 : 0);
                event.preventDefault();
                index = (index + (event.key === 'ArrowRight' ? 1 : -1) + actions.length) % actions.length;
                return this.focusElement(actions[index]);
            }
            if (event.key === 'Enter') {
                if (!action) {
                    const firstAction = this.getRowActions(row)[0];
                    if (!firstAction) return false;
                    event.preventDefault();
                    return this.focusElement(firstAction);
                }
                event.preventDefault();
                action.click();
                return true;
            }
            return false;
        },

        isAutocompleteOpen() {
            return Array.from(document.querySelectorAll('.ui-autocomplete')).some(menu => this.isVisible(menu));
        },

        isDatepickerOpen() {
            return Array.from(document.querySelectorAll('.datepicker-dropdown, #ui-datepicker-div')).some(picker => this.isVisible(picker));
        },

        isSelectOpen(target) {
            return !!(window.POSSelectKeyboard && window.POSSelectKeyboard.isOpenFor &&
                window.POSSelectKeyboard.isOpenFor(window.POSSelectKeyboard.resolveSelect(target)));
        },

        moveAmongControls(target, delta, scope) {
            const controls = this.getControls(scope).filter(control => {
                if (control.matches(EDITABLE_SELECTOR)) return this.isEditable(control);
                return true;
            });
            const index = controls.indexOf(this.normalizeControl(target));
            if (index < 0) return false;
            const next = controls[index + delta];
            return next ? this.focusElement(next) : false;
        },

        getPrimaryAction(scope, target) {
            const root = scope || this.getActiveModal() || target && target.closest('form') || this.getContentRoot();
            const selectors = '[data-apj-keyboard-primary], #btnListe, button[type="submit"], input[type="submit"], .btn-primary';
            return root && Array.from(root.querySelectorAll(selectors)).find(element => this.isVisible(element) && this.isEnabled(element) && !this.isIgnored(element));
        },

        activatePrimary(target) {
            const modal = this.getActiveModal();
            const primary = this.getPrimaryAction(modal, target);
            if (!primary) return false;
            primary.click();
            return true;
        },

        handleForm(event, target) {
            const modal = this.getActiveModal();
            const scope = modal || this.getContentRoot();
            if (!scope || !scope.contains(target)) return false;
            if (this.isAutocompleteOpen() || this.isSelectOpen(target)) return false;
            if (this.isDatepickerOpen()) return false;
            const control = target.closest && target.closest(FOCUSABLE_SELECTOR);
            if (!control) return false;
            if (event.key === 'ArrowUp' || event.key === 'ArrowDown') {
                if (control.closest('tr.apj-kb-table-row')) return false;
                event.preventDefault();
                return this.moveAmongControls(control, event.key === 'ArrowDown' ? 1 : -1, scope);
            }
            if (event.key !== 'Enter') return false;
            if (control.tagName === 'TEXTAREA') return false;
            if (control.tagName === 'SELECT' || control.classList.contains('select2-selection')) return false;
            if (control.matches('button, a[href], input[type="button"], input[type="submit"], input[type="reset"], [role="button"]')) {
                event.preventDefault();
                control.click();
                return true;
            }
            event.preventDefault();
            return this.moveAmongControls(control, 1, scope);
        },

        openFilterModal(button) {
            if (!button) return false;
            this.modalReturnFocus = button;
            button.click();
            return true;
        },

        bindBootstrapModals() {
            if (!window.jQuery) return;
            const $document = window.jQuery(document);
            $document.off('.apjKeyboard');
            $document.on('shown.bs.modal.apjKeyboard', '.modal', event => {
                if (!this.isActive()) return;
                const modal = event.currentTarget;
                if (modal.id === 'filtre-modal') {
                    this.modalReturnFocus = document.querySelector('.btn-filtre[data-target="#filtre-modal"]');
                }
                this.refresh();
                window.setTimeout(() => {
                    const first = this.getEditableControls(modal)[0] || this.getControls(modal)[0];
                    this.focusElement(first);
                }, 20);
            });
            $document.on('hidden.bs.modal.apjKeyboard', '.modal', event => {
                if (!this.isActive()) return;
                if (event.currentTarget.id === 'filtre-modal') {
                    const button = this.modalReturnFocus || document.querySelector('.btn-filtre[data-target="#filtre-modal"]');
                    this.modalReturnFocus = null;
                    this.focusElement(button);
                }
            });
        },

        closeActiveModal() {
            const modal = this.getActiveModal();
            if (!modal) return false;
            if (window.jQuery && typeof window.jQuery(modal).modal === 'function') {
                window.jQuery(modal).modal('hide');
            } else {
                const close = modal.querySelector('[data-dismiss="modal"], .close, .btn-exit');
                if (close) close.click();
            }
            return true;
        },

        ensureHelpModal() {
            if (!document.body || document.getElementById('apj-keyboard-help-modal')) return;
            const modal = document.createElement('div');
            modal.id = 'apj-keyboard-help-modal';
            modal.className = 'pos-help-modal apj-keyboard-help-modal';
            modal.setAttribute('role', 'dialog');
            modal.setAttribute('aria-modal', 'true');
            modal.setAttribute('aria-hidden', 'true');
            modal.setAttribute('aria-labelledby', 'apj-keyboard-help-title');
            modal.innerHTML = `
                <div class="pos-help-modal-content">
                    <div class="pos-help-modal-header">
                        <h2 id="apj-keyboard-help-title" class="h617pxSemibold">Raccourcis clavier</h2>
                        <button type="button" class="pos-help-close" aria-label="Fermer l'aide">&times;</button>
                    </div>
                    <div class="pos-help-modal-body">
                        <section class="pos-help-section">
                            <h3 class="h617pxSemibold">Global</h3>
                            <div class="pos-help-list">
                                <div class="pos-help-row"><span>Shift + Tab</span><p>Accéder au menu ou revenir au contenu</p></div>
                                <div class="pos-help-row"><span>Shift + H</span><p>Accéder aux actions du header</p></div>
                                <div class="pos-help-row"><span>Shift / Ctrl + F</span><p>Accéder à la recherche globale</p></div>
                                <div class="pos-help-row"><span>Ctrl / Shift + Entrée</span><p>Valider l'action principale</p></div>
                                <div class="pos-help-row"><span>F1 / Ctrl + H</span><p>Afficher ou masquer cette aide</p></div>
                            </div>
                        </section>
                        <section class="pos-help-section">
                            <h3 class="h617pxSemibold">Navigation</h3>
                            <div class="pos-help-list">
                                <div class="pos-help-row"><span>Haut / Bas</span><p>Parcourir les champs, lignes et menus</p></div>
                                <div class="pos-help-row"><span>Gauche / Droite</span><p>Parcourir les actions ou développer le menu</p></div>
                                <div class="pos-help-row"><span>Entrée</span><p>Continuer, ouvrir ou activer l'élément ciblé</p></div>
                                <div class="pos-help-row"><span>Échap</span><p>Fermer la fenêtre ou le menu ouvert</p></div>
                            </div>
                        </section>
                    </div>
                </div>`;
            document.body.appendChild(modal);
            modal.querySelector('.pos-help-close').addEventListener('click', () => this.closeHelp(true));
            modal.addEventListener('click', event => {
                if (event.target === modal) this.closeHelp(true);
            });
        },

        isHelpOpen() {
            const modal = document.getElementById('apj-keyboard-help-modal');
            return !!(modal && modal.classList.contains('is-open'));
        },

        openHelp() {
            this.ensureHelpModal();
            const modal = document.getElementById('apj-keyboard-help-modal');
            if (!modal) return false;
            this.helpPreviousFocus = document.activeElement;
            modal.classList.add('is-open');
            modal.setAttribute('aria-hidden', 'false');
            document.body.classList.add('pos-help-open');
            return this.focusElement(modal.querySelector('.pos-help-close'));
        },

        closeHelp(restoreFocus) {
            const modal = document.getElementById('apj-keyboard-help-modal');
            if (!modal) return false;
            modal.classList.remove('is-open');
            modal.setAttribute('aria-hidden', 'true');
            if (document.body && document.body.classList.contains('pos-help-open')) {
                document.body.classList.remove('pos-help-open');
            }
            const previous = this.helpPreviousFocus;
            this.helpPreviousFocus = null;
            if (restoreFocus && previous && this.isVisible(previous)) this.focusElement(previous);
            return true;
        },

        toggleHelp() {
            return this.isHelpOpen() ? this.closeHelp(true) : this.openHelp();
        },

        handleKeydown(event) {
            if (!this.isActive() || !event || event.isComposing || event.key === 'Process') return false;
            const target = event.target || document.activeElement;
            if (!target || this.isIgnored(target)) return false;

            const helpShortcut = event.key === 'F1' || (event.ctrlKey && !event.shiftKey && !event.altKey && !event.metaKey && event.key.toLowerCase() === 'h');
            if (helpShortcut) {
                event.preventDefault();
                this.toggleHelp();
                return true;
            }
            if (this.isHelpOpen()) {
                if (event.key === 'Escape') {
                    event.preventDefault();
                    return this.closeHelp(true);
                }
                return false;
            }
            const activeModal = this.getActiveModal();
            if ((event.ctrlKey || event.shiftKey) && !event.altKey && !event.metaKey && event.key.toLowerCase() === 'f') {
                if (activeModal) {
                    event.preventDefault();
                    return true;
                }
                const search = Array.from(document.querySelectorAll('.global-search-input')).find(input => this.isVisible(input) && this.isEnabled(input));
                if (!search) return false;
                event.preventDefault();
                return this.focusElement(search);
            }
            if (event.shiftKey && !event.ctrlKey && !event.altKey && !event.metaKey && event.key.toLowerCase() === 'h') {
                event.preventDefault();
                if (activeModal) return true;
                return this.focusHeader();
            }
            if (event.shiftKey && !event.ctrlKey && !event.altKey && !event.metaKey && event.key === 'Tab') {
                if (activeModal) return false;
                if (this.isInMenu(target)) {
                    event.preventDefault();
                    return this.returnToContent();
                }
                if (this.isInHeader(target)) return false;
                const content = this.getContentRoot();
                if (!content || !content.contains(target)) return false;
                event.preventDefault();
                this.lastContentFocus = target;
                return this.focusMenu();
            }
            if ((event.ctrlKey || event.shiftKey) && !event.altKey && !event.metaKey && event.key === 'Enter') {
                event.preventDefault();
                return this.activatePrimary(target);
            }
            if (event.ctrlKey || event.shiftKey || event.altKey || event.metaKey) return false;
            if (event.key === 'Escape' && this.getActiveModal()) {
                event.preventDefault();
                return this.closeActiveModal();
            }
            if (this.handleHeader(event, target)) return true;
            if (this.handleMenu(event, target)) return true;
            if (this.handleTable(event, target)) return true;
            if (target.matches && target.matches('.btn-filtre[data-target="#filtre-modal"]') && event.key === 'Enter') {
                event.preventDefault();
                return this.openFilterModal(target);
            }
            return this.handleForm(event, target);
        }
    };

    window.APJKeyboard = APJKeyboard;
    if (window.APJKeyboardAutoInit !== false) APJKeyboard.init();
})(window);
