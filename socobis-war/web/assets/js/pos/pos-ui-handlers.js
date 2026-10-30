/**
 * POS APJ - UI Ergonomics (pos-ui-handlers.js)
 * Correctif complet : Clavier virtuel, focus et neutralisation en Mode Clavier
 */
(function (window) {
    'use strict';

    window.bloquerOuvertureClavier = false;
    window.activeInput = null;
    window.isShiftActive = false;

    let clavierBlockTimer = null;
    let repeatInterval = null;
    let repeatTimeout = null;

    function getFormTableContainer() {
        let el = document.querySelector('#formTableContainer #butfillejsp');
        if (!el) {
            const parent = document.querySelector('.vente-directe-left-container');
            if (parent && parent.children.length > 0) el = parent.children[0];
        }
        return el || document.getElementById('formTableContainer');
    }

    function getClavierContainer() {
        let el = document.getElementById('clavierContainer');
        if (!el) {
            const parent = document.querySelector('.vente-directe-left-container');
            if (parent && parent.children.length > 1) el = parent.children[1];
        }
        return el;
    }

    function focusOnProduit() {
        const input = document.getElementById("idProduit_libelle");
        if (input) {
            input.value = "";
            window.bloquerOuvertureClavier = true;
            input.focus();
            setTimeout(() => { window.bloquerOuvertureClavier = false; }, 300);
        }
    }

    function ouvrirClavierVisuel(inputTarget) {
        // ➕ MODE CLAVIER : Ne jamais ouvrir le clavier visuel
        if (window.isKeyboardMode) return;

        if (window.bloquerOuvertureClavier) return;
        const clavier = getClavierContainer();
        const formTable = getFormTableContainer();

        if (!clavier || !formTable) return;

        formTable.style.setProperty('display', 'none', 'important');
        clavier.style.setProperty('display', 'block', 'important');
        clavier.classList.remove('d-none', 'hidden', 'hide');
        clavier.classList.add('in', 'show');

        if (inputTarget) setActiveInput(inputTarget);
    }

    function fermerClavierVisuel() {
        window.bloquerOuvertureClavier = true;

        document.querySelectorAll('.vd-header, #formTableContainer, #butfillejsp, .paiement-section').forEach(el => {
            el.style.removeProperty('z-index');
        });

        if (clavierBlockTimer) {
            clearTimeout(clavierBlockTimer);
            clavierBlockTimer = null;
        }

        window.activeInput = null;

        if (document.activeElement && document.activeElement.id !== 'idProduit_libelle') {
            if (typeof document.activeElement.blur === 'function') document.activeElement.blur();
        }

        if (typeof $ !== 'undefined' && $('#clavier-abc').length) {
            $('#clavier-abc').modal('hide');
            $('.modal-backdrop').remove();
            $('body').removeClass('modal-open');
        }

        const clavier = getClavierContainer();
        const formTable = getFormTableContainer();

        if (clavier) {
            clavier.style.setProperty('display', 'none', 'important');
            clavier.classList.add('d-none', 'hidden', 'hide');
            clavier.classList.remove('in', 'show');
        }
        if (formTable) {
            formTable.style.setProperty('display', 'block', 'important');
            formTable.classList.remove('d-none', 'hidden', 'hide');
        }

        clavierBlockTimer = setTimeout(() => { window.bloquerOuvertureClavier = false; }, 300);
    }

    function verifierEtOuvrirClavier(e) {
        // ➕ MODE CLAVIER : Neutralisation totale du clavier virtuel
        if (window.isKeyboardMode) return;

        if (window.bloquerOuvertureClavier) return;
        const input = e.target;
        if (!input || !document.body.contains(input)) return;

        // Bloquer strictement le clavier virtuel dans la table du panier
        if (input.closest('#butfillejsp')) return;
        if (input.closest('.ui-autocomplete') || input.closest('.ui-menu-item')) return;

        const isInputSaisie = input.tagName === 'INPUT' && !['hidden', 'checkbox', 'radio', 'submit', 'button', 'file'].includes(input.type.toLowerCase());

        if (isInputSaisie) {
            const header = input.closest('.vd-header');
            const formTable = input.closest('#formTableContainer');
            const butFille = input.closest('#butfillejsp');
            const paiementSection = input.closest('.paiement-section');

            document.querySelectorAll('.vd-header, #formTableContainer, #butfillejsp, .paiement-section').forEach(el => {
                el.style.removeProperty('z-index');
            });

            if (header) {
                header.style.setProperty('z-index', '2000', 'important');
                ouvrirClavierVisuel(input);
            } else if (formTable || butFille) {
                if (formTable) formTable.style.setProperty('z-index', '2000', 'important');
                if (butFille) butFille.style.setProperty('z-index', '2000', 'important');
                ouvrirClavierVisuel(input);
            } else if (paiementSection) {
                paiementSection.style.setProperty('z-index', '2000', 'important');
                ouvrirClavierVisuel(input);
            }
        }
    }

    function setActiveInput(input) {
        document.querySelectorAll('.form-control').forEach(el => el.classList.remove('active'));
        if (input) {
            input.classList.add('active');
            window.activeInput = input;
        }
    }

    function addText(char) {
        if (!window.activeInput) return;
        const cursorPos = window.activeInput.selectionStart || window.activeInput.value.length;
        const textBefore = window.activeInput.value.substring(0, cursorPos);
        const textAfter = window.activeInput.value.substring(cursorPos);

        if (window.isShiftActive) {
            window.activeInput.value = textBefore + char.toUpperCase() + textAfter;
            window.isShiftActive = false;
            document.querySelectorAll('.btn-clavier.maj').forEach(btn => btn.classList.remove('active'));
        } else {
            window.activeInput.value = textBefore + char.toLowerCase() + textAfter;
        }

        const newPos = cursorPos + 1;
        window.activeInput.setSelectionRange(newPos, newPos);
        window.activeInput.focus();
        window.activeInput.dispatchEvent(new Event('input', { bubbles: true }));
    }

    function deleteChar() {
        if (!window.activeInput) return;
        const cursorPos = window.activeInput.selectionStart || window.activeInput.value.length;
        if (cursorPos > 0) {
            const textBefore = window.activeInput.value.substring(0, cursorPos - 1);
            const textAfter = window.activeInput.value.substring(cursorPos);
            window.activeInput.value = textBefore + textAfter;
            const newPos = cursorPos - 1;
            window.activeInput.setSelectionRange(newPos, newPos);
            window.activeInput.focus();
            window.activeInput.dispatchEvent(new Event('input', { bubbles: true }));
        }
    }

    function executeButtonAction(btn) {
        if (btn.classList.contains('maj')) {
            window.isShiftActive = !window.isShiftActive;
            btn.classList.toggle('active');
            return false;
        }
        if (btn.classList.contains('delete')) {
            deleteChar();
            return true;
        }
        if (btn.classList.contains('space-touch')) {
            addText(' ');
            return true;
        }
        const key = btn.getAttribute('data-key');
        if (key) {
            addText(key);
            return true;
        }
        const btnText = btn.textContent.trim();
        if (btnText && btnText !== 'ABC' && btnText !== '123' && btnText !== 'Espace') {
            addText(btnText);
            return true;
        }
        return false;
    }

    function stopRepeat() {
        if (repeatTimeout) { clearTimeout(repeatTimeout); repeatTimeout = null; }
        if (repeatInterval) { clearInterval(repeatInterval); repeatInterval = null; }
    }

    function initVirtualKeyboard() {
        // En mode clavier, ne pas attacher les événements d'activation du clavier visuel
        if (window.isKeyboardMode) return;

        document.querySelectorAll('.form-control:not(select)').forEach(input => {
            input.addEventListener('focus', () => setActiveInput(input));
            input.addEventListener('click', () => setActiveInput(input));
        });

        document.querySelectorAll('.btn-clavier').forEach(btn => {
            if (btn.dataset.kbInitialized) return;
            btn.dataset.kbInitialized = 'true';

            btn.addEventListener('mousedown', (e) => {
                e.preventDefault();
                const shouldRepeat = executeButtonAction(btn);
                if (shouldRepeat) {
                    repeatTimeout = setTimeout(() => {
                        repeatInterval = setInterval(() => executeButtonAction(btn), 100);
                    }, 500);
                }
            });

            btn.addEventListener('mouseup', stopRepeat);
            btn.addEventListener('mouseleave', stopRepeat);

            btn.addEventListener('touchstart', (e) => {
                e.preventDefault();
                const shouldRepeat = executeButtonAction(btn);
                if (shouldRepeat) {
                    repeatTimeout = setTimeout(() => {
                        repeatInterval = setInterval(() => executeButtonAction(btn), 100);
                    }, 500);
                }
            });

            btn.addEventListener('touchend', stopRepeat);
            btn.addEventListener('touchcancel', stopRepeat);
        });

        document.addEventListener('mouseup', stopRepeat);
        document.addEventListener('touchend', stopRepeat);
    }

    function swapclavier(e) {
        if (e) e.preventDefault();
        if (typeof $ !== 'undefined') {
            $('.modal-body .clavier-container').toggleClass('d-none');
            $('.clavier-abc-container').toggleClass('d-none');
        }
    }

    function openModal() {
        if (window.isKeyboardMode) return;
        const modal = document.getElementById('clavier-abc');
        if (!modal || modal.classList.contains('in')) return;
        modal.style.display = 'block';
        modal.setAttribute('aria-hidden', 'false');
        document.body.classList.add('modal-open');
        setTimeout(() => modal.classList.add('in'), 10);
    }

    function closeModal() {
        const modal = document.getElementById('clavier-abc');
        if (!modal) return;
        modal.classList.remove('in');
        modal.setAttribute('aria-hidden', 'true');
        document.body.classList.remove('modal-open');
        setTimeout(() => modal.style.display = 'none', 300);
    }

    function addLinkAccueilLogo() {
        const img = document.getElementById("site-logo");
        if (!img || img.parentNode.tagName === 'A') return;
        const link = document.createElement("a");
        const lien = window.lienAccueil || '#';
        link.href = lien + "?but=accueil.jsp";
        img.parentNode.insertBefore(link, img);
        link.appendChild(img);
    }

    // Exports
    window.focusOnProduit = focusOnProduit;
    window.ouvrirClavierVisuel = ouvrirClavierVisuel;
    window.fermerClavierVisuel = fermerClavierVisuel;
    window.verifierEtOuvrirClavier = verifierEtOuvrirClavier;
    window.setActiveInput = setActiveInput;
    window.initVirtualKeyboard = initVirtualKeyboard;
    window.addText = addText;
    window.deleteChar = deleteChar;
    window.executeButtonAction = executeButtonAction;
    window.stopRepeat = stopRepeat;
    window.swapclavier = swapclavier;
    window.openModal = openModal;
    window.closeModal = closeModal;
    window.addLinkAccueilLogo = addLinkAccueilLogo;
})(window);