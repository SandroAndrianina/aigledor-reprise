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
            propagationStopped: false,
            preventDefault: function () { this.defaultPrevented = true; },
            stopImmediatePropagation: function () { this.propagationStopped = true; }
        }, options || {});
    }

    function createSelect(id) {
        const select = document.createElement('select');
        select.id = id;
        select.innerHTML = '<option value="a">Alpha</option><option value="b">Bravo</option><option value="c">Charlie</option>';
        document.body.appendChild(select);
        document.body.classList.add('pos-page-kb-mode');
        return select;
    }

    function cleanup(element) {
        POSSelectKeyboard.close(false);
        if (element && element.parentNode) element.parentNode.removeChild(element);
        document.body.classList.remove('pos-page-kb-mode', 'pos-cash-kb-mode', 'pos-kb-mode');
    }

    POSTestRunner.test('Select global : détecte automatiquement un select existant', (assert) => {
        const select = createSelect('global-existing-select');
        const event = keyEvent(select, 'Enter');
        assert.isTrue(POSSelectKeyboard.handleKeydown(event), 'Entrée doit être prise en charge sans configuration du champ');
        assert.isTrue(POSSelectKeyboard.isOpenFor(select), 'la liste globale doit être ouverte');
        cleanup(select);
    });

    POSTestRunner.test('Select global : Entrée garde la priorité sur les moteurs de page', (assert) => {
        const select = createSelect('global-priority-select');
        const pageListener = function (event) {
            if (event.key === 'Enter') event.preventDefault();
        };
        document.addEventListener('keydown', pageListener, true);
        select.focus();
        select.dispatchEvent(new KeyboardEvent('keydown', {
            key: 'Enter',
            bubbles: true,
            cancelable: true
        }));
        document.removeEventListener('keydown', pageListener, true);
        assert.isTrue(POSSelectKeyboard.isOpenFor(select), 'le menu doit s’ouvrir avant le gestionnaire clavier de la page');
        cleanup(select);
    });

    POSTestRunner.test('Select global : détecte un select ajouté dynamiquement', (assert) => {
        document.body.classList.add('pos-page-kb-mode');
        const wrapper = document.createElement('div');
        document.body.appendChild(wrapper);
        const select = document.createElement('select');
        select.innerHTML = '<option>Ajout dynamique</option>';
        wrapper.appendChild(select);
        assert.isTrue(POSSelectKeyboard.handleKeydown(keyEvent(select, 'Enter')), 'la délégation doit prendre en charge le nouveau select');
        cleanup(wrapper);
    });

    POSTestRunner.test('Select global : Haut/Bas parcourt puis Entrée valide une option', (assert) => {
        const select = createSelect('global-commit-select');
        let inputs = 0;
        let changes = 0;
        select.addEventListener('input', function () { inputs++; });
        select.addEventListener('change', function () { changes++; });
        POSSelectKeyboard.handleKeydown(keyEvent(select, 'Enter'));
        const down = keyEvent(select, 'ArrowDown');
        POSSelectKeyboard.handleKeydown(down);
        assert.isTrue(down.defaultPrevented && down.propagationStopped, 'Bas doit rester dans la liste ouverte');
        POSSelectKeyboard.handleKeydown(keyEvent(select, 'Enter'));
        assert.equal(select.value, 'b', 'Bravo doit être sélectionné');
        assert.equal(inputs, 1, 'input doit être déclenché une seule fois');
        assert.equal(changes, 1, 'change doit être déclenché une seule fois');
        assert.isFalse(POSSelectKeyboard.isOpenFor(select), 'la liste doit être fermée après validation');
        cleanup(select);
    });

    POSTestRunner.test('Select global : une valeur inchangée ne déclenche pas change', (assert) => {
        const select = createSelect('global-unchanged-select');
        let changes = 0;
        select.addEventListener('change', function () { changes++; });
        POSSelectKeyboard.handleKeydown(keyEvent(select, 'Enter'));
        POSSelectKeyboard.handleKeydown(keyEvent(select, 'Enter'));
        assert.equal(changes, 0, 'la confirmation de la valeur courante ne doit pas simuler un changement');
        cleanup(select);
    });

    POSTestRunner.test('Select global : Échap annule la sélection', (assert) => {
        const select = createSelect('global-escape-select');
        POSSelectKeyboard.handleKeydown(keyEvent(select, 'Enter'));
        POSSelectKeyboard.handleKeydown(keyEvent(select, 'ArrowDown'));
        POSSelectKeyboard.handleKeydown(keyEvent(select, 'Escape'));
        assert.equal(select.value, 'a', 'Échap doit conserver la valeur initiale');
        assert.isFalse(POSSelectKeyboard.isOpenFor(select), 'Échap doit fermer la liste');
        cleanup(select);
    });

    POSTestRunner.test('Select global : Tab ferme la liste sans bloquer la navigation native', (assert) => {
        const select = createSelect('global-tab-select');
        POSSelectKeyboard.handleKeydown(keyEvent(select, 'Enter'));
        const tab = keyEvent(select, 'Tab');
        POSSelectKeyboard.handleKeydown(tab);
        assert.isFalse(tab.defaultPrevented, 'Tab doit rester natif');
        assert.isFalse(POSSelectKeyboard.isOpenFor(select), 'Tab doit fermer la liste');
        cleanup(select);
    });

    POSTestRunner.test('Select global : ignore les options cachées et désactivées', (assert) => {
        const select = createSelect('global-filter-select');
        select.options[1].disabled = true;
        select.options[2].hidden = true;
        POSSelectKeyboard.open(select);
        assert.equal(POSSelectKeyboard.state.options.length, 1, 'seule l’option disponible doit être navigable');
        cleanup(select);
    });

    POSTestRunner.test('Select global : prend en charge une cible Select2', (assert) => {
        const root = document.createElement('div');
        root.innerHTML = '<select class="select2-hidden-accessible"><option value="a">Alpha</option><option value="b">Bravo</option></select><span class="select2-container"><span class="select2-selection" tabindex="0"></span></span>';
        document.body.appendChild(root);
        document.body.classList.add('pos-page-kb-mode');
        const select = root.querySelector('select');
        const selection = root.querySelector('.select2-selection');
        assert.isTrue(POSSelectKeyboard.handleKeydown(keyEvent(selection, 'Enter')), 'la cible visuelle Select2 doit résoudre son select');
        assert.isTrue(POSSelectKeyboard.isOpenFor(select), 'la liste globale Select2 doit être ouverte');
        cleanup(root);
    });

    POSTestRunner.test('Select global : respecte les exclusions et le mode classique', (assert) => {
        const select = createSelect('global-native-select');
        select.setAttribute('data-pos-keyboard-native', '');
        assert.isFalse(POSSelectKeyboard.handleKeydown(keyEvent(select, 'Enter')), 'un select exclu doit rester natif');
        select.removeAttribute('data-pos-keyboard-native');
        document.body.classList.remove('pos-page-kb-mode');
        const previousMode = window.isKeyboardMode;
        window.isKeyboardMode = false;
        assert.isFalse(POSSelectKeyboard.handleKeydown(keyEvent(select, 'Enter')), 'le mode classique ne doit pas être intercepté');
        window.isKeyboardMode = previousMode;
        cleanup(select);
    });

    POSTestRunner.test('Select global : un clic sur une option applique la valeur', (assert) => {
        const select = createSelect('global-click-select');
        POSSelectKeyboard.open(select);
        const option = POSSelectKeyboard.state.menu.querySelectorAll('.pos-keyboard-select-option')[2];
        const event = { target: option, preventDefault: function () { this.defaultPrevented = true; } };
        POSSelectKeyboard.handlePointerDown(event);
        assert.equal(select.value, 'c', 'le clic doit sélectionner Charlie');
        assert.isTrue(event.defaultPrevented, 'le clic ne doit pas voler le focus au select');
        cleanup(select);
    });

    POSTestRunner.test('Select global : le défilement interne d’une longue liste ne ferme pas le menu', (assert) => {
        const select = document.createElement('select');
        select.id = 'global-long-select';
        for (let index = 0; index < 70; index++) {
            const option = document.createElement('option');
            option.value = String(index);
            option.textContent = 'Point ' + index;
            option.selected = index === 50;
            select.appendChild(option);
        }
        document.body.appendChild(select);
        document.body.classList.add('pos-page-kb-mode');
        POSSelectKeyboard.open(select);
        POSSelectKeyboard.state.menu.dispatchEvent(new Event('scroll', { bubbles: false }));
        assert.isTrue(POSSelectKeyboard.isOpenFor(select), 'le scroll du menu ne doit pas être traité comme un scroll de page');
        cleanup(select);
    });
})();
