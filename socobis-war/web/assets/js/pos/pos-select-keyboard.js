/**
 * APJ - Sélecteur clavier global.
 *
 * Tous les select simples sont pris en charge automatiquement lorsque l'un
 * des modes clavier APJ est actif. Aucun branchement par champ n'est requis.
 */
(function (window) {
    'use strict';

    const POSSelectKeyboard = {
        initialized: false,
        state: null,
        menuSequence: 0,
        boundKeydown: null,
        boundPointerDown: null,
        boundViewportChange: null,
        boundPageHide: null,
        boundReady: null,
        modeObserver: null,

        init() {
            if (this.initialized) return;
            this.initialized = true;
            this.boundKeydown = this.handleKeydown.bind(this);
            this.boundPointerDown = this.handlePointerDown.bind(this);
            this.boundViewportChange = () => this.close(false);
            this.boundPageHide = () => this.close(false);
            this.boundReady = () => {
                this.boundReady = null;
                this.observeKeyboardMode();
            };
            // Le listener window en capture passe avant les moteurs clavier de page.
            // Le select garde ainsi la priorité sur Entrée, même sur le login.
            window.addEventListener('keydown', this.boundKeydown, true);
            document.addEventListener('mousedown', this.boundPointerDown, true);
            // Sans capture : le défilement interne d'une longue liste ne doit
            // pas être confondu avec un défilement de la page.
            window.addEventListener('scroll', this.boundViewportChange);
            window.addEventListener('resize', this.boundViewportChange);
            window.addEventListener('pagehide', this.boundPageHide);
            if (document.body) this.observeKeyboardMode();
            else document.addEventListener('DOMContentLoaded', this.boundReady, { once: true });
        },

        destroy() {
            if (!this.initialized) return;
            this.close(false);
            window.removeEventListener('keydown', this.boundKeydown, true);
            document.removeEventListener('mousedown', this.boundPointerDown, true);
            window.removeEventListener('scroll', this.boundViewportChange);
            window.removeEventListener('resize', this.boundViewportChange);
            window.removeEventListener('pagehide', this.boundPageHide);
            if (this.boundReady) document.removeEventListener('DOMContentLoaded', this.boundReady);
            if (this.modeObserver) this.modeObserver.disconnect();
            this.initialized = false;
            this.boundKeydown = null;
            this.boundPointerDown = null;
            this.boundViewportChange = null;
            this.boundPageHide = null;
            this.boundReady = null;
            this.modeObserver = null;
        },

        observeKeyboardMode() {
            if (!window.MutationObserver || !document.body) return;
            this.modeObserver = new MutationObserver(() => {
                if (!this.isKeyboardMode()) this.close(false);
            });
            this.modeObserver.observe(document.body, { attributes: true, attributeFilter: ['class'] });
        },

        isKeyboardMode() {
            if (window.isKeyboardMode === true) return true;
            if (!document.body) return false;
            return document.body.classList.contains('pos-kb-mode') ||
                document.body.classList.contains('pos-cash-kb-mode') ||
                document.body.classList.contains('pos-page-kb-mode') ||
                document.body.classList.contains('apj-kb-mode');
        },

        resolveSelect(target) {
            if (!target) return null;
            if (target.tagName === 'SELECT') return target;
            const container = target.closest ? target.closest('.select2-container') : null;
            if (!container) return null;
            const select = container.previousElementSibling;
            return select && select.tagName === 'SELECT' ? select : null;
        },

        getAnchor(select) {
            if (select.classList.contains('select2-hidden-accessible')) {
                const container = select.nextElementSibling;
                if (container && container.classList.contains('select2-container')) {
                    return container.querySelector('.select2-selection') || container;
                }
            }
            return select;
        },

        isVisible(element) {
            if (!element || element.hidden || element.closest('[hidden], .d-none')) return false;
            if (element.classList.contains('select2-hidden-accessible')) return !!this.getAnchor(element);
            if (!window.getComputedStyle) return true;
            const style = window.getComputedStyle(element);
            return style.display !== 'none' && style.visibility !== 'hidden';
        },

        isEligible(select) {
            return !!(select && select.tagName === 'SELECT' && !select.disabled && !select.multiple &&
                !select.hasAttribute('data-pos-keyboard-native') && this.isVisible(select));
        },

        getOptions(select) {
            return Array.from(select.options || []).filter(option => {
                const group = option.parentElement && option.parentElement.tagName === 'OPTGROUP' ? option.parentElement : null;
                return !option.disabled && !option.hidden && !(group && group.disabled);
            });
        },

        isOpenFor(select) {
            return !!(this.state && this.state.select === select);
        },

        open(select) {
            if (!this.isKeyboardMode() || !this.isEligible(select)) return false;
            this.close(false);
            const options = this.getOptions(select);
            if (options.length === 0) return false;

            const anchor = this.getAnchor(select);
            const menu = document.createElement('div');
            const selectedOption = options.indexOf(select.options[select.selectedIndex]);
            const activeIndex = selectedOption >= 0 ? selectedOption : 0;
            const menuId = 'pos-keyboard-select-menu-' + (++this.menuSequence);
            menu.id = menuId;
            menu.className = 'pos-keyboard-select-menu';
            menu.setAttribute('role', 'listbox');

            options.forEach((option, index) => {
                const item = document.createElement('div');
                item.className = 'pos-keyboard-select-option';
                item.dataset.optionIndex = String(index);
                item.setAttribute('role', 'option');
                item.textContent = option.textContent;
                menu.appendChild(item);
            });

            document.body.appendChild(menu);
            this.state = {
                select: select,
                anchor: anchor,
                options: options,
                menu: menu,
                activeIndex: activeIndex,
                previousExpanded: select.getAttribute('aria-expanded'),
                previousControls: select.getAttribute('aria-controls')
            };
            select.setAttribute('aria-expanded', 'true');
            select.setAttribute('aria-controls', menuId);
            this.positionMenu();
            this.render();
            return true;
        },

        positionMenu() {
            if (!this.state) return;
            const margin = 8;
            const rect = this.state.anchor.getBoundingClientRect();
            const menu = this.state.menu;
            const width = Math.max(rect.width, 180);
            menu.style.width = width + 'px';
            const menuHeight = menu.offsetHeight;
            const below = window.innerHeight - rect.bottom - margin;
            const above = rect.top - margin;
            const top = below >= menuHeight || below >= above ? rect.bottom : Math.max(margin, rect.top - menuHeight);
            const left = Math.min(Math.max(margin, rect.left), Math.max(margin, window.innerWidth - width - margin));
            menu.style.top = top + 'px';
            menu.style.left = left + 'px';
        },

        render() {
            if (!this.state) return;
            const items = this.state.menu.querySelectorAll('.pos-keyboard-select-option');
            items.forEach((item, index) => {
                const active = index === this.state.activeIndex;
                item.classList.toggle('is-active', active);
                item.setAttribute('aria-selected', active ? 'true' : 'false');
                if (active && typeof item.scrollIntoView === 'function') item.scrollIntoView({ block: 'nearest' });
            });
        },

        move(delta) {
            if (!this.state) return false;
            this.state.activeIndex = Math.min(
                Math.max(this.state.activeIndex + delta, 0),
                this.state.options.length - 1
            );
            this.render();
            return true;
        },

        commit() {
            if (!this.state) return false;
            const state = this.state;
            const option = state.options[state.activeIndex];
            const optionIndex = Array.prototype.indexOf.call(state.select.options, option);
            const changed = state.select.selectedIndex !== optionIndex;
            state.select.selectedIndex = optionIndex;
            this.close(true);
            if (changed) {
                state.select.dispatchEvent(new Event('input', { bubbles: true }));
                state.select.dispatchEvent(new Event('change', { bubbles: true }));
            }
            return true;
        },

        close(restoreFocus) {
            if (!this.state) return false;
            const state = this.state;
            this.state = null;
            if (state.previousExpanded === null) state.select.removeAttribute('aria-expanded');
            else state.select.setAttribute('aria-expanded', state.previousExpanded);
            if (state.previousControls === null) state.select.removeAttribute('aria-controls');
            else state.select.setAttribute('aria-controls', state.previousControls);
            if (state.menu.parentNode) state.menu.parentNode.removeChild(state.menu);
            if (restoreFocus && document.contains(state.anchor)) state.anchor.focus();
            return true;
        },

        handlePointerDown(event) {
            if (!this.state) return false;
            const option = event.target.closest ? event.target.closest('.pos-keyboard-select-option') : null;
            if (option && this.state.menu.contains(option)) {
                event.preventDefault();
                this.state.activeIndex = Number(option.dataset.optionIndex);
                return this.commit();
            }
            if (event.target === this.state.anchor || this.state.anchor.contains(event.target)) return false;
            this.close(false);
            return false;
        },

        handleKeydown(event) {
            if (!event || event.isComposing || event.key === 'Process') return false;
            if (event.ctrlKey || event.shiftKey || event.altKey || event.metaKey) return false;
            if (!this.isKeyboardMode()) {
                this.close(false);
                return false;
            }
            const select = this.resolveSelect(event.target);
            if (!this.isEligible(select)) return false;

            if (this.isOpenFor(select)) {
                if (event.key === 'ArrowUp' || event.key === 'ArrowDown') {
                    event.preventDefault();
                    event.stopImmediatePropagation();
                    return this.move(event.key === 'ArrowDown' ? 1 : -1);
                }
                if (event.key === 'Enter') {
                    event.preventDefault();
                    event.stopImmediatePropagation();
                    return this.commit();
                }
                if (event.key === 'Escape') {
                    event.preventDefault();
                    event.stopImmediatePropagation();
                    return this.close(true);
                }
                if (event.key === 'Tab') this.close(false);
                return false;
            }

            if (event.key === 'Enter') {
                event.preventDefault();
                event.stopImmediatePropagation();
                return this.open(select);
            }
            return false;
        }
    };

    window.POSSelectKeyboard = POSSelectKeyboard;
    POSSelectKeyboard.init();
})(window);
