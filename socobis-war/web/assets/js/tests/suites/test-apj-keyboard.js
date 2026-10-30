(function () {
    'use strict';

    function keyEvent(target, key, options) {
        return Object.assign({
            target: target,
            key: key,
            ctrlKey: false,
            shiftKey: false,
            altKey: false,
            metaKey: false,
            isComposing: false,
            defaultPrevented: false,
            preventDefault: function () { this.defaultPrevented = true; },
            stopImmediatePropagation: function () {}
        }, options || {});
    }

    function mount(html) {
        const root = document.createElement('div');
        root.className = 'apj-keyboard-test-fixture';
        root.innerHTML = html;
        document.body.appendChild(root);
        return root;
    }

    function start() {
        window.isKeyboardMode = true;
        document.body.classList.remove('pos-kb-mode', 'pos-cash-kb-mode', 'pos-page-kb-mode');
        APJKeyboard.init();
        APJKeyboard.refresh();
    }

    function cleanup(root) {
        APJKeyboard.destroy();
        POSSelectKeyboard.close(false);
        document.body.classList.remove('apj-kb-mode', 'pos-kb-mode', 'pos-cash-kb-mode', 'pos-page-kb-mode', 'pos-help-open');
        document.querySelectorAll('.apj-keyboard-test-fixture, #apj-keyboard-help-modal').forEach(element => element.remove());
        if (root && root.parentNode) root.remove();
    }

    POSTestRunner.test('APJ global : active le mode standard et cède la priorité au POS', (assert) => {
        const root = mount('<div id="butjsp"><input></div>');
        start();
        assert.isTrue(document.body.classList.contains('apj-kb-mode'), 'le mode APJ doit être actif par défaut');
        document.body.classList.add('pos-kb-mode');
        APJKeyboard.syncMode();
        assert.isFalse(document.body.classList.contains('apj-kb-mode'), 'un moteur POS doit suspendre le moteur global');
        cleanup(root);
    });

    POSTestRunner.test('APJ global : setEnabled(false) désactive le mode sans configuration', (assert) => {
        const previousFlag = window.isKeyboardMode;
        const root = mount('<div id="butjsp"><input id="default-off-input"></div>');
        try {
            delete window.isKeyboardMode;
            APJKeyboard.init();
            APJKeyboard.setEnabled(false);
            assert.equal(APJKeyboard.getMode(), 'off', 'la désactivation explicite arrête le mode');
            assert.isFalse(document.body.classList.contains('apj-kb-mode'), 'aucun focus clavier APJ après désactivation');
        } finally {
            cleanup(root);
            window.isKeyboardMode = previousFlag;
        }
    });

    POSTestRunner.test('APJ global : setEnabled pilote une page standard', (assert) => {
        const previousFlag = window.isKeyboardMode;
        const root = mount('<div id="butjsp"><input id="disabled-keyboard-input"></div>');
        try {
            window.isKeyboardMode = false;
            APJKeyboard.init();
            APJKeyboard.refresh(true);
            APJKeyboard.setEnabled(false);
            assert.equal(APJKeyboard.getMode(), 'off', 'setEnabled(false) désactive le mode APJ');
            assert.isFalse(document.body.classList.contains('apj-kb-mode'), 'la bordure clavier doit être retirée');
            APJKeyboard.setEnabled(true);
            assert.isTrue(document.body.classList.contains('apj-kb-mode'), 'le mode peut être réactivé');
            APJKeyboard.setEnabled(false);
            assert.isFalse(document.body.classList.contains('apj-kb-mode'), 'la désactivation retire la classe de focus');
        } finally {
            cleanup(root);
            window.isKeyboardMode = previousFlag;
        }
    });

    POSTestRunner.test('APJ global : le POS tactile reste hors du moteur clavier commun', (assert) => {
        const root = mount('<form data-pos-screen><input id="pos-touch-input"></form>');
        start();
        assert.isTrue(APJKeyboard.isSpecializedMode(), 'le POS doit être reconnu sans classe pos-kb-mode');
        assert.isFalse(document.body.classList.contains('apj-kb-mode'), 'le clavier APJ ne doit pas remplacer le mode tactile');
        cleanup(root);
    });

    POSTestRunner.test('APJ global : le mode POS ne relance pas l’observateur du body en boucle', async (assert) => {
        const root = mount('<div id="butjsp"><input></div>');
        start();
        let mutations = 0;
        const observer = new MutationObserver(records => { mutations += records.length; });
        observer.observe(document.body, { attributes: true, attributeFilter: ['class'] });
        document.body.classList.add('pos-kb-mode');
        await new Promise(resolve => window.setTimeout(resolve, 40));
        observer.disconnect();
        assert.isTrue(mutations < 5, 'le mode spécialisé ne doit pas provoquer de mutations répétées du body');
        assert.isFalse(APJKeyboard.isActive(), 'le moteur global doit rester suspendu sur le POS');
        cleanup(root);
    });

    POSTestRunner.test('APJ global : pilote le POS tactile et clavier sans rechargement', (assert) => {
        const originalPOS = window.POSKeyboard;
        const previousFlag = window.isKeyboardMode;
        const calls = { init: 0, destroy: 0 };
        const root = mount('<form data-pos-screen data-keyboard-default="off"><input></form>');
        window.POSKeyboard = {
            initialized: false,
            init() { calls.init++; this.initialized = true; document.body.classList.add('pos-kb-mode'); },
            destroy() { calls.destroy++; this.initialized = false; document.body.classList.remove('pos-kb-mode'); }
        };
        try {
            start();
            assert.equal(APJKeyboard.getMode(), 'off', 'le POS démarre en mode tactile');
            assert.equal(calls.init, 0, 'aucun listener POS ne doit être ajouté au démarrage tactile');
            APJKeyboard.setEnabled(true);
            assert.equal(APJKeyboard.getMode(), 'pos', 'le contrôleur active le POS');
            assert.equal(calls.init, 1, 'le moteur POS est initialisé une seule fois');
            APJKeyboard.syncMode();
            assert.equal(calls.init, 1, 'une synchronisation ne réinstalle pas les écouteurs');
            APJKeyboard.setEnabled(false);
            assert.equal(calls.destroy, 1, 'le moteur POS est détruit lors de la désactivation');
            assert.isFalse(window.isKeyboardMode, 'le drapeau de compatibilité est remis à false');
            APJKeyboard.setEnabled(true);
            assert.equal(APJKeyboard.getMode(), 'pos', 'le POS peut être réactivé');
            root.querySelector('[data-pos-screen]').remove();
            APJKeyboard.syncMode();
            assert.equal(APJKeyboard.getPageMode(), 'apj', 'la nouvelle page retrouve son moteur APJ');
            APJKeyboard.setEnabled(true);
            assert.isTrue(APJKeyboard.isActive(), 'la navigation APJ peut être activée');
        } finally {
            cleanup(root);
            window.POSKeyboard = originalPOS;
            window.isKeyboardMode = previousFlag;
        }
    });

    POSTestRunner.test('APJ global : pilote les moteurs caisse et page selon le contexte', (assert) => {
        const oldCash = window.POSCashKeyboard;
        const oldPage = window.POSPageKeyboard;
        const oldCashConfig = window.POSCashPageConfig;
        const oldPageConfig = window.POSPageKeyboardConfig;
        const calls = { cashStart: 0, cashStop: 0, pageStart: 0, pageStop: 0 };
        const root = mount('<form id="controller-cash-form"></form>');
        window.POSCashPageConfig = { formSelector: '#controller-cash-form' };
        window.POSPageKeyboardConfig = undefined;
        window.POSCashKeyboard = {
            initialized: false,
            init() { this.initialized = true; calls.cashStart++; document.body.classList.add('pos-cash-kb-mode'); },
            destroy() { this.initialized = false; calls.cashStop++; document.body.classList.remove('pos-cash-kb-mode'); }
        };
        window.POSPageKeyboard = {
            initialized: false,
            init() { this.initialized = true; calls.pageStart++; document.body.classList.add('pos-page-kb-mode'); },
            destroy() { this.initialized = false; calls.pageStop++; document.body.classList.remove('pos-page-kb-mode'); }
        };
        try {
            start();
            assert.equal(APJKeyboard.getMode(), 'cash', 'la page caisse prend la priorité');
            assert.equal(calls.cashStart, 1, 'le moteur caisse démarre une seule fois');
            APJKeyboard.setEnabled(false);
            assert.equal(calls.cashStop, 1, 'le moteur caisse est nettoyé');
            APJKeyboard.setEnabled(true);
            assert.equal(calls.cashStart, 2, 'le moteur caisse redémarre');
            root.querySelector('form').remove();
            root.innerHTML = '<div id="controller-page-actions"><button>Action</button></div>';
            window.POSPageKeyboardConfig = { containerSelector: '#controller-page-actions' };
            APJKeyboard.syncMode();
            assert.equal(APJKeyboard.getPageMode(), 'page', 'la nouvelle page est reconnue');
            assert.equal(calls.cashStop, 2, 'la caisse est arrêtée avant le changement de page');
            APJKeyboard.setEnabled(true);
            assert.equal(APJKeyboard.getMode(), 'page', 'la page périphérique peut être activée');
            assert.equal(calls.pageStart, 1, 'le moteur de la nouvelle page démarre');
        } finally {
            cleanup(root);
            window.POSCashKeyboard = oldCash;
            window.POSPageKeyboard = oldPage;
            window.POSCashPageConfig = oldCashConfig;
            window.POSPageKeyboardConfig = oldPageConfig;
        }
        assert.equal(calls.pageStop, 1, 'le moteur périphérique est nettoyé à la fin');
    });

    POSTestRunner.test('APJ global : une page liste démarre sur le bouton Filtre', (assert) => {
        const root = mount('<div id="butjsp"><input id="list-input"><button id="list-filter" class="btn-filtre" data-target="#filtre-modal">Filtre</button></div>');
        start();
        APJKeyboard.focusInitial();
        assert.equal(document.activeElement.id, 'list-filter', 'Filtre doit être prioritaire sur les champs de la liste');
        cleanup(root);
    });

    POSTestRunner.test('APJ global : un formulaire démarre sur son premier champ éditable', (assert) => {
        const root = mount('<div id="butjsp"><input value="fixe" readonly><input id="first-editable"><input id="second-editable"></div>');
        start();
        APJKeyboard.focusInitial();
        assert.equal(document.activeElement.id, 'first-editable', 'le readonly doit être ignoré');
        cleanup(root);
    });

    POSTestRunner.test('APJ global : Shift + Tab alterne entre contenu et menu courant', (assert) => {
        const root = mount('<aside class="main-sidebar"><div class="sidebar-icon-item"></div><div id="sidebarSubmenuPanel"><a href="#x" class="submenu-item active">Courant</a></div></aside><div id="butjsp"><input id="content-control"></div>');
        start();
        const content = root.querySelector('#content-control');
        const menu = root.querySelector('.submenu-item');
        content.focus();
        APJKeyboard.handleKeydown(keyEvent(content, 'Tab', { shiftKey: true }));
        assert.isTrue(document.activeElement === menu, 'le menu courant doit recevoir le focus');
        APJKeyboard.handleKeydown(keyEvent(menu, 'Tab', { shiftKey: true }));
        assert.isTrue(document.activeElement === content, 'le dernier contrôle du contenu doit être restauré');
        cleanup(root);
    });

    POSTestRunner.test('APJ global : les raccourcis ciblent le header et la recherche', (assert) => {
        const root = mount('<header class="main-header"><button class="sidebar-toggle">Menu</button><input class="global-search-input"></header><div id="butjsp"><input id="header-origin"></div>');
        start();
        const origin = root.querySelector('#header-origin');
        APJKeyboard.handleKeydown(keyEvent(origin, 'h', { shiftKey: true }));
        assert.isTrue(document.activeElement === root.querySelector('.sidebar-toggle'), 'Shift + H doit cibler la première action');
        APJKeyboard.handleKeydown(keyEvent(document.activeElement, 'ArrowRight'));
        assert.isTrue(document.activeElement === root.querySelector('.global-search-input'), 'Droite doit parcourir les actions du header');
        APJKeyboard.handleKeydown(keyEvent(document.activeElement, 'f', { ctrlKey: true }));
        assert.isTrue(document.activeElement === root.querySelector('.global-search-input'), 'Ctrl + F doit cibler la recherche');
        cleanup(root);
    });

    POSTestRunner.test('APJ global : la validation utilise le bouton principal actif', (assert) => {
        const root = mount('<div id="butjsp"><form><input id="primary-origin"><button id="primary-disabled" data-apj-keyboard-primary disabled>Non</button><button id="primary-active" type="submit">Oui</button></form></div>');
        start();
        let clicks = 0;
        root.querySelector('form').addEventListener('submit', event => event.preventDefault());
        root.querySelector('#primary-active').addEventListener('click', () => clicks++);
        const origin = root.querySelector('#primary-origin');
        APJKeyboard.handleKeydown(keyEvent(origin, 'Enter', { ctrlKey: true }));
        assert.equal(clicks, 1, 'le bouton désactivé doit être ignoré et le bouton actif cliqué');
        cleanup(root);
    });

    POSTestRunner.test('APJ global : Entrée avance dans les champs et reste native dans un textarea', (assert) => {
        const root = mount('<div id="butjsp"><input id="enter-first"><input id="enter-second"><textarea id="enter-textarea"></textarea></div>');
        start();
        const first = root.querySelector('#enter-first');
        const textarea = root.querySelector('#enter-textarea');
        first.focus();
        const enter = keyEvent(first, 'Enter');
        APJKeyboard.handleKeydown(enter);
        assert.equal(document.activeElement.id, 'enter-second', 'Entrée doit avancer au champ suivant');
        const textEnter = keyEvent(textarea, 'Enter');
        APJKeyboard.handleKeydown(textEnter);
        assert.isFalse(textEnter.defaultPrevented, 'le retour à la ligne doit rester natif');
        cleanup(root);
    });

    POSTestRunner.test('APJ global : les flèches parcourent les icônes du menu', (assert) => {
        const root = mount('<aside class="main-sidebar"><div id="menu-one" class="sidebar-icon-item"></div><div id="menu-two" class="sidebar-icon-item"></div><div id="sidebarSubmenuPanel"><a id="submenu-one" class="submenu-item" href="#one">Un</a><a id="submenu-two" class="submenu-item" href="#two">Deux</a></div></aside><div id="butjsp"></div>');
        start();
        const first = root.querySelector('#menu-one');
        first.focus();
        APJKeyboard.handleKeydown(keyEvent(first, 'ArrowDown'));
        assert.equal(document.activeElement.id, 'menu-two', 'Bas doit cibler l’icône suivante');
        const submenu = root.querySelector('#submenu-one');
        submenu.focus();
        APJKeyboard.handleKeydown(keyEvent(submenu, 'ArrowDown'));
        assert.equal(document.activeElement.id, 'submenu-two', 'Bas doit aussi parcourir les éléments du sous-menu');
        cleanup(root);
    });

    POSTestRunner.test('APJ global : les lignes et actions d’un tableau sont navigables', (assert) => {
        const root = mount('<div id="butjsp"><div class="table-container"><table><tbody><tr id="row-one"><td><button id="row-action-one">Voir</button></td></tr><tr id="row-two"><td><button>Voir</button></td></tr></tbody></table></div></div>');
        start();
        APJKeyboard.decorateTables();
        const row = root.querySelector('#row-one');
        row.focus();
        APJKeyboard.handleKeydown(keyEvent(row, 'Enter'));
        assert.equal(document.activeElement.id, 'row-action-one', 'Entrée doit cibler la première action de la ligne');
        APJKeyboard.handleKeydown(keyEvent(document.activeElement, 'ArrowDown'));
        assert.equal(document.activeElement.id, 'row-two', 'Bas doit cibler la ligne suivante');
        cleanup(root);
    });

    POSTestRunner.test('APJ global : la modale Filtre prend puis restitue le focus', async (assert) => {
        const root = mount('<div id="butjsp"><button class="btn-filtre" data-target="#filtre-modal" id="filter-return">Filtre</button><div id="filtre-modal" class="modal in" aria-hidden="false" style="display:block"><input id="filter-first"><button id="btnListe">Afficher</button></div></div>');
        start();
        let validations = 0;
        root.querySelector('#btnListe').addEventListener('click', () => validations++);
        window.jQuery(root.querySelector('#filtre-modal')).trigger('shown.bs.modal');
        await new Promise(resolve => window.setTimeout(resolve, 30));
        assert.equal(document.activeElement.id, 'filter-first', 'le premier champ du filtre doit recevoir le focus');
        APJKeyboard.handleKeydown(keyEvent(document.activeElement, 'ArrowDown'));
        assert.equal(document.activeElement.id, 'btnListe', 'Bas doit parcourir les contrôles du filtre');
        APJKeyboard.handleKeydown(keyEvent(document.activeElement, 'Enter', { ctrlKey: true }));
        assert.equal(validations, 1, 'Ctrl + Entrée doit activer #btnListe');
        root.querySelector('#filtre-modal').classList.remove('in');
        root.querySelector('#filtre-modal').style.display = 'none';
        window.jQuery(root.querySelector('#filtre-modal')).trigger('hidden.bs.modal');
        assert.equal(document.activeElement.id, 'filter-return', 'la fermeture doit revenir sur Filtre');
        cleanup(root);
    });

    POSTestRunner.test('APJ global : une modale ouverte bloque les raccourcis qui en sortent', (assert) => {
        const root = mount('<header class="main-header"><button id="modal-header">Header</button><input class="global-search-input"></header><div id="butjsp"><div class="modal in" aria-hidden="false" style="display:block"><input id="modal-control"></div></div>');
        start();
        const control = root.querySelector('#modal-control');
        control.focus();
        APJKeyboard.handleKeydown(keyEvent(control, 'h', { shiftKey: true }));
        assert.isTrue(document.activeElement === control, 'le focus doit rester dans la modale');
        APJKeyboard.handleKeydown(keyEvent(control, 'F1'));
        assert.isTrue(APJKeyboard.isHelpOpen(), 'F1 doit ouvrir l’aide globale');
        APJKeyboard.handleKeydown(keyEvent(document.activeElement, 'Escape'));
        assert.isFalse(APJKeyboard.isHelpOpen(), 'Échap doit fermer l’aide globale');
        cleanup(root);
    });

    POSTestRunner.test('APJ global : le contenu injecté replace le focus sur Filtre', async (assert) => {
        const root = mount('<div id="butjsp"><input id="dynamic-old"></div>');
        start();
        root.querySelector('#butjsp').innerHTML = '<button id="dynamic-filter" class="btn-filtre" data-target="#filtre-modal">Filtre</button>';
        document.dispatchEvent(new Event('dynamicContentLoaded'));
        await new Promise(resolve => window.setTimeout(resolve, 80));
        assert.equal(document.activeElement.id, 'dynamic-filter', 'le nouveau bouton Filtre doit recevoir le focus');
        cleanup(root);
    });

    POSTestRunner.test('APJ global : une composition IME n’est jamais interceptée', (assert) => {
        const root = mount('<div id="butjsp"><input id="ime-first"><input id="ime-second"></div>');
        start();
        const first = root.querySelector('#ime-first');
        first.focus();
        const event = keyEvent(first, 'Enter', { isComposing: true });
        assert.isFalse(APJKeyboard.handleKeydown(event), 'la composition doit être ignorée');
        assert.isFalse(event.defaultPrevented, 'l’événement IME doit rester natif');
        cleanup(root);
    });

    POSTestRunner.test('Select global : le mode APJ ouvre automatiquement les listes', (assert) => {
        const root = mount('<select id="apj-global-select"><option>A</option><option>B</option></select><select id="apj-select2" class="select2-hidden-accessible"><option>A</option></select><span class="select2-container"><span class="select2-selection" tabindex="0"></span></span>');
        window.isKeyboardMode = false;
        document.body.classList.add('apj-kb-mode');
        const select = root.querySelector('select');
        assert.isTrue(POSSelectKeyboard.handleKeydown(keyEvent(select, 'Enter')), 'apj-kb-mode doit activer le sélecteur global');
        assert.isTrue(POSSelectKeyboard.isOpenFor(select), 'la liste doit être ouverte');
        assert.equal(APJKeyboard.getControls(root).length, 2, 'un Select2 ne doit apparaître qu’une fois dans le parcours');
        cleanup(root);
    });
})();
