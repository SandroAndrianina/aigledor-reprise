/**
 * Navigation clavier légère pour les pages périphériques du POS.
 * Le module reste inactif sans window.POSPageKeyboardConfig.
 */
(function (window) {
    'use strict';

    const POSPageKeyboard = {
        initialized: false,
        config: null,
        boundKeydown: null,
        focusTimer: null,

        init(config) {
            if (this.initialized) this.destroy();
            this.config = Object.assign({
                mode: 'form',
                formSelector: 'form',
                actionSelector: 'a[href], button:not([disabled])',
                actionOrder: []
            }, config || {});
            this.initialized = true;
            document.body.classList.add('pos-page-kb-mode');
            this.injectCSS();
            this.boundKeydown = this.handleKeydown.bind(this);
            document.addEventListener('keydown', this.boundKeydown, true);
            this.focusTimer = window.setTimeout(() => {
                this.focusTimer = null;
                if (this.initialized && this.config) this.focusFirst();
            }, 0);
        },

        injectCSS() {
            if (document.getElementById('pos-page-keyboard-css')) return;
            const style = document.createElement('style');
            style.id = 'pos-page-keyboard-css';
            style.textContent = '.pos-page-kb-mode input:focus, .pos-page-kb-mode select:focus, .pos-page-kb-mode textarea:focus, .pos-page-kb-mode button:focus, .pos-page-kb-mode a:focus { outline: 3px solid #2f6fde !important; outline-offset: 2px; box-shadow: 0 0 0 3px rgba(47,111,222,.2) !important; }';
            document.head.appendChild(style);
        },

        destroy() {
            if (this.boundKeydown) document.removeEventListener('keydown', this.boundKeydown, true);
            if (this.focusTimer) window.clearTimeout(this.focusTimer);
            document.body.classList.remove('pos-page-kb-mode');
            this.initialized = false;
            this.config = null;
            this.boundKeydown = null;
            this.focusTimer = null;
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

        getFormElements() {
            const form = document.querySelector(this.config.formSelector);
            if (!form) return [];
            return Array.from(form.querySelectorAll('input, select, textarea, button[type="submit"]')).filter(element => {
                if (element.disabled || element.readOnly || !this.isVisible(element)) return false;
                return !['hidden', 'checkbox', 'radio', 'file'].includes((element.type || '').toLowerCase());
            });
        },

        getActions() {
            const root = this.config.containerSelector ? document.querySelector(this.config.containerSelector) : document;
            if (!root) return [];
            const actions = Array.from(root.querySelectorAll(this.config.actionSelector)).filter(element => !element.disabled && this.isVisible(element));
            if (!Array.isArray(this.config.actionOrder) || this.config.actionOrder.length === 0) return actions;
            const positions = new Map(this.config.actionOrder.map((id, index) => [id, index]));
            return actions.sort((left, right) => {
                const leftIndex = positions.has(left.id) ? positions.get(left.id) : Number.MAX_SAFE_INTEGER;
                const rightIndex = positions.has(right.id) ? positions.get(right.id) : Number.MAX_SAFE_INTEGER;
                return leftIndex - rightIndex;
            });
        },

        getElements() {
            return this.config.mode === 'actions' ? this.getActions() : this.getFormElements();
        },

        focusElement(element) {
            if (!element) return false;
            element.focus();
            if (element.tagName === 'INPUT' && typeof element.select === 'function') element.select();
            if (typeof element.scrollIntoView === 'function') element.scrollIntoView({ block: 'nearest' });
            return true;
        },

        focusFirst() {
            return this.focusElement(this.getElements()[0]);
        },

        focusRelative(current, delta, wrap) {
            const elements = this.getElements();
            const index = elements.indexOf(current);
            if (index < 0 || elements.length === 0) return false;
            let next = index + delta;
            if (wrap) {
                next = (next + elements.length) % elements.length;
            } else {
                next = Math.min(Math.max(next, 0), elements.length - 1);
                // Depuis un champ ou le bouton placé sous les listes du login,
                // remonter directement au précédent champ de saisie. Sinon le
                // focus s'arrête sur un select dont les flèches sont natives.
                if (delta < 0) {
                    while (next > 0 && elements[next].tagName === 'SELECT') next--;
                }
            }
            return this.focusElement(elements[next]);
        },

        activate(element) {
            if (!element || element.disabled) return false;
            element.click();
            return true;
        },

        handleKeydown(event) {
            if (!event || event.isComposing || event.key === 'Process' || event.altKey || event.metaKey) return false;
            const target = event.target;

            if (this.config.mode === 'actions') {
                const actions = this.getActions();
                if (actions.indexOf(target) < 0) return false;
                if (['ArrowLeft', 'ArrowUp', 'ArrowRight', 'ArrowDown'].includes(event.key)) {
                    event.preventDefault();
                    return this.focusRelative(target, event.key === 'ArrowLeft' || event.key === 'ArrowUp' ? -1 : 1, true);
                }
                if (event.key === 'Enter') {
                    event.preventDefault();
                    return this.activate(target);
                }
                return false;
            }

            const elements = this.getFormElements();
            if (elements.indexOf(target) < 0) return false;
            if (event.key === 'Tab') return false;
            if ((event.ctrlKey || event.shiftKey) && event.key === 'Enter') {
                const submit = elements.find(element => element.type === 'submit');
                if (submit && !submit.disabled) {
                    event.preventDefault();
                    return this.activate(submit);
                }
                return false;
            }
            if (target.tagName === 'SELECT') {
                if (event.key === 'Enter' || event.key === 'Escape') return false;
                if (event.key === 'ArrowUp' || event.key === 'ArrowDown') {
                    event.preventDefault();
                    return this.focusRelative(target, event.key === 'ArrowDown' ? 1 : -1, false);
                }
            }
            if (target.tagName === 'TEXTAREA' && event.key === 'Enter') return false;
            if (event.key === 'ArrowUp' || event.key === 'ArrowDown') {
                event.preventDefault();
                return this.focusRelative(target, event.key === 'ArrowDown' ? 1 : -1, false);
            }
            if (event.key === 'Enter' && target.type !== 'submit') {
                event.preventDefault();
                return this.focusRelative(target, 1, false);
            }
            return false;
        }
    };

    window.POSPageKeyboard = POSPageKeyboard;

    function boot() {
        if (window.APJKeyboard && window.APJKeyboardAutoInit !== false) return;
        if (window.POSSelectKeyboard) window.POSSelectKeyboard.init();
        if (window.POSPageKeyboardConfig) POSPageKeyboard.init(window.POSPageKeyboardConfig);
    }
    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', boot);
    else boot();
})(window);
