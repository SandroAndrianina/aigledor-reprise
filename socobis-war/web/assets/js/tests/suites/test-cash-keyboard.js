/**
 * Suite de tests - Navigation clavier commune aux opérations de caisse.
 */
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
            preventDefault: function () { this.defaultPrevented = true; }
        }, options || {});
    }

    function createFixture(options) {
        const settings = Object.assign({ billetage: false }, options || {});
        const root = document.createElement('div');
        root.innerHTML = `
            <div class="vd-header">
                <div class="form-input w-100"></div>
                <a href="#pos" id="cash-header-pos">POS</a>
                <div class="dropdown">
                    <button type="button" class="dropdown-toggle" aria-expanded="false">Mouvement</button>
                    <ul class="dropdown-menu">
                        <li><a href="#entry">Entrée</a></li>
                        <li><a href="#exit">Sortie</a></li>
                    </ul>
                </div>
                <a href="#close" id="cash-header-close">Clôture</a>
            </div>
            <form id="cash-test-form">
                <input type="hidden" value="secret">
                <input type="checkbox" id="cash-row-checkbox" checked>
                <input id="cash-first" value="1">
                <input id="cash-readonly" readonly value="2">
                <input id="cash-disabled" disabled value="3">
                <select id="cash-select"><option>A</option><option>B</option></select>
                <textarea id="cash-description"></textarea>
                <div class="d-none"><button type="submit" id="cash-hidden-submit">Bouton APJ caché</button></div>
                <div class="clavier-container"><input id="cash-keypad-input"></div>
                <button type="submit" id="cash-submit">Valider</button>
            </form>`;
        document.body.appendChild(root);

        POSCashKeyboard.init({
            formSelector: '#cash-test-form',
            pageType: 'entry',
            billetage: settings.billetage,
            posUrl: '/pos',
            navigate: settings.navigate || function () {}
        });

        return {
            root: root,
            form: root.querySelector('#cash-test-form'),
            cleanup: function () {
                POSCashKeyboard.destroy();
                root.remove();
                const help = document.getElementById('pos-cash-help-modal');
                if (help) help.remove();
                sessionStorage.removeItem('posPendingProductSearch');
            }
        };
    }

    POSTestRunner.test('Cash keyboard : filtre les contrôles APJ non modifiables', (assert) => {
        const fixture = createFixture();
        const ids = POSCashKeyboard.getFormControls().map(element => element.id);
        assert.deepEqual(ids, ['cash-first', 'cash-select', 'cash-description'], 'seuls les champs visibles et modifiables sont navigables');
        fixture.cleanup();
    });

    POSTestRunner.test('Cash keyboard : le premier contrôle éditable reçoit le focus initial', (assert) => {
        const fixture = createFixture();
        POSCashKeyboard.focusFirstControl();
        assert.isTrue(document.activeElement === document.getElementById('cash-first'), 'le focus doit ignorer les champs cachés, readonly et désactivés');
        fixture.cleanup();
    });

    POSTestRunner.test('Cash keyboard : Haut/Bas parcourt les champs et Tab reste natif', async (assert) => {
        const fixture = createFixture();
        const first = document.getElementById('cash-first');
        const select = document.getElementById('cash-select');
        first.focus();

        const down = keyEvent(first, 'ArrowDown');
        POSCashKeyboard.handleKeydown(down);
        assert.isTrue(down.defaultPrevented, 'Bas doit être intercepté dans un input');
        assert.isTrue(document.activeElement === select, 'Bas doit cibler le champ suivant');

        const closedSelect = keyEvent(select, 'ArrowDown');
        POSCashKeyboard.handleKeydown(closedSelect);
        assert.isTrue(closedSelect.defaultPrevented, 'un select fermé doit rendre les flèches à la navigation du formulaire');

        const tab = keyEvent(select, 'Tab');
        POSCashKeyboard.handleKeydown(tab);
        assert.isFalse(tab.defaultPrevented, 'Tab ne doit pas être intercepté');

        const picker = document.createElement('div');
        picker.className = 'datepicker-dropdown';
        picker.style.display = 'block';
        fixture.root.appendChild(picker);
        first.classList.add('datepicker');
        first.focus();
        let dateChanges = 0;
        first.addEventListener('change', function () { dateChanges++; }, { once: true });
        const dateArrow = keyEvent(first, 'ArrowDown');
        POSCashKeyboard.handleKeydown(dateArrow);
        assert.isFalse(dateArrow.defaultPrevented, 'Bas doit rester disponible pour parcourir le calendrier ouvert');
        assert.isTrue(document.activeElement === first, 'le calendrier ouvert doit conserver le focus sur son champ date');

        const dateEnter = keyEvent(first, 'Enter');
        POSCashKeyboard.handleKeydown(dateEnter);
        await new Promise(resolve => window.setTimeout(resolve, 0));
        assert.equal(dateChanges, 1, 'Entrée doit notifier la validation de la date');
        assert.isTrue(document.activeElement === select, 'Entrée doit confirmer la date puis passer au champ suivant');
        picker.remove();
        first.classList.remove('datepicker');
        fixture.cleanup();
    });

    POSTestRunner.test('Cash keyboard : délègue Entrée au select global puis navigue avec les flèches', (assert) => {
        const fixture = createFixture();
        const first = document.getElementById('cash-first');
        const select = document.getElementById('cash-select');
        const textarea = document.getElementById('cash-description');
        const submit = document.getElementById('cash-submit');

        POSCashKeyboard.handleKeydown(keyEvent(first, 'Enter'));
        assert.isTrue(document.activeElement === select, 'Entrée doit avancer vers le select');
        const delegatedEnter = keyEvent(select, 'Enter');
        POSCashKeyboard.handleKeydown(delegatedEnter);
        assert.isFalse(delegatedEnter.defaultPrevented, 'Entrée doit être laissée au sélecteur clavier global');
        POSCashKeyboard.handleKeydown(keyEvent(select, 'ArrowDown'));
        assert.isTrue(document.activeElement === textarea, 'Bas doit avancer depuis un select fermé');

        select.classList.add('select2-hidden-accessible');
        const select2Container = document.createElement('span');
        select2Container.className = 'select2-container';
        select2Container.innerHTML = '<span class="select2-selection" tabindex="0"></span>';
        select.insertAdjacentElement('afterend', select2Container);
        const select2Selection = select2Container.querySelector('.select2-selection');
        select2Selection.focus();
        const delegatedSelect2 = keyEvent(select2Selection, 'Enter');
        POSCashKeyboard.handleKeydown(delegatedSelect2);
        assert.isFalse(delegatedSelect2.defaultPrevented, 'Select2 doit également être laissé au module global');
        select2Container.remove();
        select.classList.remove('select2-hidden-accessible');

        const nativeTextarea = keyEvent(textarea, 'Enter');
        POSCashKeyboard.handleKeydown(nativeTextarea);
        assert.isFalse(nativeTextarea.defaultPrevented, 'Entrée doit conserver le retour à la ligne du textarea');

        POSCashKeyboard.focusRelativeControl(textarea, 1);
        assert.isTrue(document.activeElement === submit, 'après le dernier champ, le bouton principal est ciblé');
        fixture.cleanup();
    });

    POSTestRunner.test('Cash keyboard : Haut depuis un champ inférieur dépasse le select', (assert) => {
        const fixture = createFixture();
        const first = document.getElementById('cash-first');
        const textarea = document.getElementById('cash-description');

        textarea.focus();
        POSCashKeyboard.handleKeydown(keyEvent(textarea, 'ArrowUp'));
        assert.isTrue(document.activeElement === first, 'Haut doit rejoindre le champ éditable placé avant le select');
        fixture.cleanup();
    });

    POSTestRunner.test('Cash keyboard : validation passe uniquement par le bouton actif', (assert) => {
        const fixture = createFixture();
        const submit = document.getElementById('cash-submit');
        let clicks = 0;
        submit.addEventListener('click', function (event) {
            event.preventDefault();
            clicks++;
        });

        submit.disabled = true;
        POSCashKeyboard.handleKeydown(keyEvent(document.body, 'Enter', { ctrlKey: true }));
        assert.equal(clicks, 0, 'un bouton désactivé ne doit pas être contourné');

        submit.disabled = false;
        POSCashKeyboard.handleKeydown(keyEvent(document.body, 'Enter', { shiftKey: true }));
        assert.equal(clicks, 1, 'le premier bouton submit visible doit recevoir un clic normal malgré un submit APJ caché avant lui');
        fixture.cleanup();
    });

    POSTestRunner.test('Cash keyboard : navigation du header et du dropdown', (assert) => {
        const fixture = createFixture();
        const pos = document.getElementById('cash-header-pos');
        const dropdownButton = fixture.root.querySelector('.dropdown-toggle');
        const dropdown = dropdownButton.closest('.dropdown');
        const items = dropdown.querySelectorAll('.dropdown-menu a');

        POSCashKeyboard.handleKeydown(keyEvent(pos, 'ArrowRight'));
        assert.isTrue(document.activeElement === dropdownButton, 'Droite doit cibler Mouvement');

        POSCashKeyboard.handleKeydown(keyEvent(dropdownButton, 'ArrowDown'));
        assert.isTrue(dropdown.classList.contains('open'), 'Bas doit ouvrir le dropdown');
        assert.isTrue(document.activeElement === items[0], 'le premier lien du dropdown doit être ciblé');

        POSCashKeyboard.handleKeydown(keyEvent(items[0], 'ArrowDown'));
        assert.isTrue(document.activeElement === items[1], 'Bas doit parcourir les liens du dropdown');

        POSCashKeyboard.handleKeydown(keyEvent(items[1], 'Escape'));
        assert.isFalse(dropdown.classList.contains('open'), 'Échap doit fermer le dropdown');
        assert.isTrue(document.activeElement === dropdownButton, 'le focus doit revenir au bouton Mouvement');
        fixture.cleanup();
    });

    POSTestRunner.test('Cash keyboard : Shift + H donne accès au header', (assert) => {
        const fixture = createFixture();
        const shortcut = keyEvent(document.getElementById('cash-first'), 'H', { shiftKey: true });
        POSCashKeyboard.handleKeydown(shortcut);
        assert.isTrue(shortcut.defaultPrevented, 'Shift + H doit être intercepté');
        assert.isTrue(document.activeElement === document.getElementById('cash-header-pos'), 'le premier bouton du header doit recevoir le focus');
        fixture.cleanup();
    });

    POSTestRunner.test('Cash keyboard : Ctrl + F ouvre le POS et cible sa recherche', (assert) => {
        let destination = '';
        const fixture = createFixture({ navigate: function (url) { destination = url; } });
        const first = document.getElementById('cash-first');
        const shortcut = keyEvent(first, 'f', { ctrlKey: true });
        POSCashKeyboard.handleKeydown(shortcut);

        const stored = JSON.parse(sessionStorage.getItem('posPendingProductSearch'));
        assert.isTrue(shortcut.defaultPrevented, 'Ctrl + F doit être intercepté');
        assert.equal(stored.value, '', 'une demande de focus vide doit être mémorisée');
        assert.equal(destination, '/pos', 'la destination doit être le POS');

        const globalSearch = document.querySelector('#sandbox-dom #idProduit_libelle');
        const sandbox = document.getElementById('sandbox-dom');
        const previousDisplay = sandbox.style.display;
        const previousValue = globalSearch.value;
        sandbox.style.display = 'block';
        assert.isTrue(window.consumePendingProductSearch(stored.timestamp), 'la recherche récente doit être consommée');
        assert.isTrue(document.activeElement === globalSearch, 'la barre de recherche du POS doit recevoir le focus');
        assert.equal(sessionStorage.getItem('posPendingProductSearch'), null, 'la recherche doit être supprimée après lecture');

        globalSearch.value = previousValue;
        sandbox.style.display = previousDisplay;
        fixture.cleanup();
    });

    POSTestRunner.test('Cash keyboard : Shift + F reste libre hors du POS', (assert) => {
        let destination = '';
        const fixture = createFixture({ navigate: function (url) { destination = url; } });
        const event = keyEvent(document.getElementById('cash-first'), 'F', { shiftKey: true });
        POSCashKeyboard.handleKeydown(event);
        assert.isFalse(event.defaultPrevented, 'Shift + F ne doit pas être intercepté sur une page de caisse');
        assert.equal(destination, '', 'Shift + F ne doit pas ouvrir une nouvelle page POS');
        fixture.cleanup();
    });

    POSTestRunner.test('Cash keyboard : une recherche expirée est ignorée', (assert) => {
        const fixture = createFixture();
        sessionStorage.setItem('posPendingProductSearch', JSON.stringify({ value: 'Ancienne', timestamp: 1000 }));
        assert.isFalse(window.consumePendingProductSearch(32001), 'une recherche de plus de 30 secondes doit être ignorée');
        assert.equal(sessionStorage.getItem('posPendingProductSearch'), null, 'une recherche expirée doit être supprimée');
        fixture.cleanup();
    });

    POSTestRunner.test('Cash keyboard : les raccourcis respectent la composition IME', (assert) => {
        let destination = '';
        const fixture = createFixture({ navigate: function (url) { destination = url; } });
        const event = keyEvent(document.getElementById('cash-first'), 'f', { ctrlKey: true, isComposing: true });
        POSCashKeyboard.handleKeydown(event);
        assert.isFalse(event.defaultPrevented, 'un événement IME ne doit pas être intercepté');
        assert.equal(destination, '', 'une composition IME ne doit pas ouvrir le POS');
        fixture.cleanup();
    });

    POSTestRunner.test('Cash keyboard : F1 ouvre et Échap ferme l’aide', (assert) => {
        const fixture = createFixture();
        const f1 = keyEvent(document.body, 'F1');
        POSCashKeyboard.handleKeydown(f1);
        assert.isTrue(POSCashKeyboard.isHelpOpen(), 'F1 doit ouvrir le modal d’aide');

        const escape = keyEvent(document.getElementById('pos-cash-help-close'), 'Escape');
        POSCashKeyboard.handleKeydown(escape);
        assert.isFalse(POSCashKeyboard.isHelpOpen(), 'Échap doit fermer le modal d’aide');
        fixture.cleanup();
    });

    POSTestRunner.test('Cash keyboard : billetage accessible et navigable au clavier', (assert) => {
        const fixture = createFixture({ billetage: true });
        const form = fixture.form;
        const label = document.createElement('input');
        label.id = 'caisseLib_99';
        label.value = 'Caisse Espèce';
        label.readOnly = true;
        const previous = document.createElement('input');
        previous.id = 'cash-before-amount';
        const amount = document.createElement('input');
        amount.id = 'montant_99';
        const next = document.createElement('input');
        next.id = 'montant_100';
        form.insertBefore(label, form.firstChild);
        form.insertBefore(previous, document.getElementById('cash-first'));
        form.insertBefore(amount, document.getElementById('cash-first'));
        form.insertBefore(next, document.getElementById('cash-first'));

        const modal = document.createElement('div');
        modal.id = 'montantModal';
        modal.className = 'custom-modal';
        modal.innerHTML = `
            <input class="denomination-input" id="denom-one">
            <input class="denomination-input" id="denom-two">
            <button type="button" id="montantModalCancel">Annuler</button>
            <button type="button" id="montantModalValidate">Valider</button>`;
        document.body.appendChild(modal);

        amount.addEventListener('pos-cash-open-billetage', function () {
            modal.classList.add('is-open');
            document.getElementById('denom-one').focus();
        });
        let validations = 0;
        document.getElementById('montantModalValidate').addEventListener('click', function () {
            validations++;
            modal.classList.remove('is-open');
        });

        next.focus();
        POSCashKeyboard.handleKeydown(keyEvent(next, 'ArrowUp'));
        assert.equal(document.activeElement, amount, 'Haut doit atteindre les espèces sans ouvrir le billetage');
        assert.isFalse(modal.classList.contains('is-open'), 'la navigation ne doit pas ouvrir le billetage');
        POSCashKeyboard.handleKeydown(keyEvent(amount, 'ArrowUp'));
        assert.equal(document.activeElement, previous, 'Haut doit traverser les espèces vers le champ précédent');
        POSCashKeyboard.handleKeydown(keyEvent(previous, 'ArrowDown'));
        amount.click();
        assert.isFalse(modal.classList.contains('is-open'), 'un clic de focus ne doit pas ouvrir le billetage');
        POSCashKeyboard.handleKeydown(keyEvent(amount, 'Enter'));
        assert.isTrue(modal.classList.contains('is-open'), 'Entrée sur les espèces doit ouvrir le billetage');
        const firstDenomination = document.getElementById('denom-one');
        const secondDenomination = document.getElementById('denom-two');
        POSCashKeyboard.handleKeydown(keyEvent(firstDenomination, 'ArrowDown'));
        assert.isTrue(document.activeElement === secondDenomination, 'Bas doit parcourir les coupures');
        POSCashKeyboard.handleKeydown(keyEvent(secondDenomination, 'Enter', { ctrlKey: true }));
        assert.equal(validations, 1, 'Ctrl + Entrée doit valider le billetage');
        assert.isTrue(document.activeElement === next, 'le focus doit passer au champ modifiable suivant après validation');

        modal.remove();
        fixture.cleanup();
    });

    POSTestRunner.test('Cash keyboard : une caisse espèces affiche l’indication dans son placeholder', (assert) => {
        const fixture = createFixture({ billetage: true });
        const cell = document.createElement('div');
        const label = document.createElement('input');
        label.id = 'caisseLib_7';
        label.value = 'Caisse Espèce Principale';
        label.readOnly = true;
        const amount = document.createElement('input');
        amount.id = 'montant_7';
        cell.appendChild(label);
        cell.appendChild(amount);
        fixture.form.insertBefore(cell, fixture.form.firstChild);

        assert.equal(POSCashKeyboard.decorateCashAmountInputs(), 1, 'une seule caisse espèces doit être décorée');
        assert.isTrue((amount.placeholder || '').indexOf('Entrée') >= 0, 'le placeholder doit annoncer le raccourci Entrée');
        assert.isTrue((amount.placeholder || '').toLowerCase().indexOf('sélecteur de monnaies') >= 0, 'le placeholder doit expliquer l’action');
        fixture.cleanup();
    });
})();
