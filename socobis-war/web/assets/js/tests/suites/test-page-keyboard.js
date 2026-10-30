(function () {
    'use strict';

    function keyEvent(target, key, options) {
        return Object.assign({ target: target, key: key, preventDefault: function () { this.defaultPrevented = true; }, defaultPrevented: false }, options || {});
    }

    POSTestRunner.test('Page keyboard : délègue Entrée au select global et conserve la navigation fermée', (assert) => {
        const root = document.createElement('div');
        root.innerHTML = '<form id="page-kb-form"><input id="page-kb-first"><select id="page-kb-select"><option>A</option><option>B</option></select><button id="page-kb-submit" type="submit">Valider</button></form>';
        document.body.appendChild(root);
        POSPageKeyboard.init({ mode: 'form', formSelector: '#page-kb-form' });
        const first = document.getElementById('page-kb-first');
        const select = document.getElementById('page-kb-select');
        first.focus();
        POSPageKeyboard.handleKeydown(keyEvent(first, 'Enter'));
        assert.isTrue(document.activeElement === select, 'Entrée doit cibler le select suivant');
        const delegatedEnter = keyEvent(select, 'Enter');
        POSPageKeyboard.handleKeydown(delegatedEnter);
        assert.isFalse(delegatedEnter.defaultPrevented, 'Entrée doit être laissée au sélecteur clavier global');
        const leave = keyEvent(select, 'ArrowDown');
        POSPageKeyboard.handleKeydown(leave);
        assert.isTrue(document.activeElement === document.getElementById('page-kb-submit'), 'Bas doit quitter un select fermé');
        POSPageKeyboard.destroy();
        root.remove();
    });

    POSTestRunner.test('Page keyboard : Haut depuis le bouton du login dépasse les selects', (assert) => {
        const root = document.createElement('div');
        root.innerHTML = '<form id="page-kb-login"><input id="page-login-password"><select id="page-login-point"><option>Point</option></select><select id="page-login-caisse"><option>Caisse</option></select><button id="page-login-submit" type="submit">Se connecter</button></form>';
        document.body.appendChild(root);
        POSPageKeyboard.init({ mode: 'form', formSelector: '#page-kb-login' });
        const password = document.getElementById('page-login-password');
        const submit = document.getElementById('page-login-submit');

        submit.focus();
        const arrow = keyEvent(submit, 'ArrowUp');
        POSPageKeyboard.handleKeydown(arrow);
        assert.isTrue(arrow.defaultPrevented, 'la remontée depuis le bouton doit être interceptée');
        assert.isTrue(document.activeElement === password, 'Haut doit dépasser les deux selects et cibler le champ précédent');
        POSPageKeyboard.destroy();
        root.remove();
    });

    POSTestRunner.test('Page keyboard : Haut quitte un select du login sans y bloquer le focus', (assert) => {
        const root = document.createElement('div');
        root.innerHTML = '<form id="page-kb-login-select"><input id="page-login-input"><select id="page-login-point-up"><option>Point</option></select><select id="page-login-caisse-up"><option>Caisse</option></select><button type="submit">Se connecter</button></form>';
        document.body.appendChild(root);
        POSPageKeyboard.init({ mode: 'form', formSelector: '#page-kb-login-select' });
        const input = document.getElementById('page-login-input');
        const caisse = document.getElementById('page-login-caisse-up');

        caisse.focus();
        const arrow = keyEvent(caisse, 'ArrowUp');
        POSPageKeyboard.handleKeydown(arrow);
        assert.isTrue(arrow.defaultPrevented, 'Haut ne doit pas rester natif lorsque le focus doit remonter');
        assert.isTrue(document.activeElement === input, 'Haut doit quitter les selects et cibler le champ précédent');
        POSPageKeyboard.destroy();
        root.remove();
    });

    POSTestRunner.test('Page keyboard : les flèches parcourent les actions et Entrée active', (assert) => {
        const root = document.createElement('div');
        root.id = 'page-kb-actions';
        root.innerHTML = '<button id="page-action-a">A</button><button id="page-action-b">B</button>';
        document.body.appendChild(root);
        POSPageKeyboard.init({ mode: 'actions', containerSelector: '#page-kb-actions', actionSelector: 'button' });
        const first = document.getElementById('page-action-a');
        const second = document.getElementById('page-action-b');
        let clicks = 0;
        second.addEventListener('click', function () { clicks++; });
        first.focus();
        POSPageKeyboard.handleKeydown(keyEvent(first, 'ArrowRight'));
        assert.isTrue(document.activeElement === second, 'Droite doit cibler l’action suivante');
        POSPageKeyboard.handleKeydown(keyEvent(second, 'Enter'));
        assert.equal(clicks, 1, 'Entrée doit activer l’action ciblée');
        POSPageKeyboard.destroy();
        root.remove();
    });

    POSTestRunner.test('Page keyboard : la fermeture suit l’ordre visuel configuré', (assert) => {
        const root = document.createElement('div');
        root.id = 'page-kb-visual-order';
        root.innerHTML = '<button id="order-disconnect">Déconnexion</button><button id="order-print">Imprimer</button><button id="order-mail">Mail</button>';
        document.body.appendChild(root);
        POSPageKeyboard.init({
            mode: 'actions',
            containerSelector: '#page-kb-visual-order',
            actionSelector: 'button',
            actionOrder: ['order-disconnect', 'order-mail', 'order-print']
        });
        const disconnect = document.getElementById('order-disconnect');
        const mail = document.getElementById('order-mail');
        disconnect.focus();
        POSPageKeyboard.handleKeydown(keyEvent(disconnect, 'ArrowRight'));
        assert.isTrue(document.activeElement === mail, 'Droite depuis Déconnexion doit cibler le bouton Mail placé à sa droite');
        POSPageKeyboard.destroy();
        root.remove();
    });
})();
