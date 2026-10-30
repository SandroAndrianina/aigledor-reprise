/**
 * Suite de tests - Moteur 100% Clavier
 */
POSTestRunner.test('Keyboard : État initial et transitions', (assert) => {
    assert.equal(POSKeyboard.currentState, 'SEARCH', "Démarre en état SEARCH");

    POSKeyboard.setState('BASKET');
    assert.equal(POSKeyboard.currentState, 'BASKET', "Passage à BASKET");

    POSKeyboard.setState('PAYMENT');
    assert.equal(POSKeyboard.currentState, 'PAYMENT', "Passage à PAYMENT");

    POSKeyboard.setState('SEARCH');
});

POSTestRunner.test('Keyboard : enabled est true par défaut', (assert) => {
    assert.isTrue(POSKeyboard.enabled === true, 'enabled doit être true');
});

POSTestRunner.test('Keyboard : destroy retire les écouteurs puis init les réinstalle une fois', (assert) => {
    const previousFlag = window.isKeyboardMode;
    const wasInitialized = POSKeyboard.initialized;
    window.isKeyboardMode = true;
    if (!wasInitialized) POSKeyboard.init();
    const listenerCount = POSKeyboard.listeners.length;
    assert.isTrue(listenerCount >= 5, 'les raccourcis clavier doivent être branchés');
    POSKeyboard.destroy();
    assert.equal(POSKeyboard.listeners.length, 0, 'destroy retire tous les écouteurs');
    assert.isFalse(POSKeyboard.initialized, 'le moteur est désactivé');
    POSKeyboard.init();
    assert.equal(POSKeyboard.listeners.length, listenerCount, 'la réactivation ne duplique pas les écouteurs');
    if (!wasInitialized) POSKeyboard.destroy();
    window.isKeyboardMode = previousFlag;
});

POSTestRunner.test('Keyboard : getVisibleCartRows retourne un tableau', (assert) => {
    assert.isTrue(Array.isArray(POSKeyboard.getVisibleCartRows()), 'doit être un tableau');
});

POSTestRunner.test('Keyboard : getPaymentElements retourne un tableau', (assert) => {
    assert.isTrue(Array.isArray(POSKeyboard.getPaymentElements()), 'doit être un tableau');
});

POSTestRunner.test('Keyboard : getPaymentElements inclut modes et champs de saisie', (assert) => {
    const elems = POSKeyboard.getPaymentElements();
    const toggleButtons = elems.filter(el => el.classList && el.classList.contains('toggle-button')).length;
    const hasSaisie = elems.some(el => el.id === 'valeurEspece');
    assert.isTrue(elems.length >= 3, 'au moins un label multiple + 2 modes + 1 champ présent');
    assert.isTrue(toggleButtons >= 2, 'les 2 modes toggles doivent être inclus');
    assert.isTrue(hasSaisie, 'le champ de saisie valeurEspece doit être inclus');
});

POSTestRunner.test('Keyboard : focusPayment cible le mode actif (checked)', (assert) => {
    const before = POSKeyboard.currentState;
    const mp = document.getElementById('multiple-payment');
    const savedContainer = document.getElementById('test-payment-zone');
    if (!savedContainer) { assert.isTrue(true, 'pas de zone de test, test sauté'); return; }

    // Simuler un focus hors zone droite puis focusPayment
    POSKeyboard.setState('SEARCH');
    POSKeyboard.focusPayment();
    const checkedEl = savedContainer.querySelector('.toggle-button.active');
    assert.equal(POSKeyboard.currentState, 'PAYMENT', 'doit passer en PAYMENT');
    assert.isTrue(checkedEl !== null, 'un mode actif doit exister');
    POSKeyboard.setState(before === 'PAYMENT' ? 'SEARCH' : before);
});

POSTestRunner.test('Keyboard : injectCSS ajoute le style', (assert) => {
    POSKeyboard.injectCSS();
    const style = document.getElementById('pos-keyboard-css');
    assert.isTrue(!!style, 'le style pos-keyboard-css doit exister');
});

POSTestRunner.test('Keyboard : focusSearch bascule en SEARCH et cible la recherche', (assert) => {
    POSKeyboard.setState('BASKET');
    POSKeyboard.focusSearch();
    assert.equal(POSKeyboard.currentState, 'SEARCH', 'doit passer à SEARCH');
    const input = document.getElementById('idProduit_libelle');
    assert.isTrue(!!input, 'la zone de recherche doit exister');
    POSKeyboard.setState('SEARCH');
});

POSTestRunner.test('Keyboard : getBasketElements retourne un tableau', (assert) => {
    assert.isTrue(Array.isArray(POSKeyboard.getBasketElements()), 'doit être un tableau');
});

POSTestRunner.test('Keyboard : getBasketElements navigue par lignes (1 qte / ligne)', (assert) => {
    const row0 = document.getElementById('ligne-multiple-0');
    if (!row0) { assert.isTrue(true, 'pas de ligne sandbox, test sauté'); return; }

    // Exposer une ligne visible pour tester la navigation par lignes
    const prevHidden = row0.classList.contains('d-none');
    row0.classList.remove('d-none');

    const elems = POSKeyboard.getBasketElements();
    // Chaque qte d'une ligne visible doit apparaître au plus une fois
    const qteCount = elems.filter(el => /^qte_/.test((el.name || ''))).length;
    const visibleRows = POSKeyboard.getVisibleCartRows().length;

    assert.equal(qteCount, visibleRows, 'une quantité par ligne visible (pas cellule par cellule)');

    row0.classList.toggle('d-none', prevHidden);
});

POSTestRunner.test('Keyboard : la quantité reste éditable en mode clavier', (assert) => {
    const qte = document.getElementById('qte_0');
    if (!qte) return;
    window.isKeyboardMode = true;
    window.verrouillerChampsButFille();
    assert.isFalse(qte.readOnly, 'la saisie directe doit rester possible');
    assert.isFalse(qte.hasAttribute('readonly'), 'aucun attribut readonly sur la quantité');
    assert.equal(qte.style.pointerEvents, 'auto', 'la quantité reste accessible à la souris');
});

POSTestRunner.test('Keyboard : Backspace édite la quantité sans retirer la ligne', (assert) => {
    const row = document.getElementById('ligne-multiple-0');
    const qte = document.getElementById('qte_0');
    if (!row || !qte) return;
    const sandbox = document.getElementById('sandbox-dom');
    const oldDisplay = sandbox.style.display;
    const hidden = row.classList.contains('d-none');
    sandbox.style.display = 'block';
    row.classList.remove('d-none');
    try {
        window.verrouillerChampsButFille();
        POSKeyboard.setState('BASKET');
        qte.focus();
        assert.isTrue(document.activeElement === qte, 'la quantité doit recevoir le focus');
        const event = new KeyboardEvent('keydown', { key: 'Backspace', bubbles: true, cancelable: true });
        qte.dispatchEvent(event);
        assert.isFalse(event.defaultPrevented, 'Backspace ne doit pas être intercepté');
        assert.isTrue(document.contains(row), 'la ligne doit rester dans le panier');
    } finally {
        row.classList.toggle('d-none', hidden);
        sandbox.style.display = oldDisplay;
        POSKeyboard.setState('SEARCH');
    }
});

POSTestRunner.test('Keyboard : focusBasketEl focalise un élément RÉCAP sans casser', (assert) => {
    const elems = POSKeyboard.getBasketElements();
    if (elems.length === 0) {
        assert.isTrue(true, 'pas d\'élément récap, test sauté');
        return;
    }
    const before = POSKeyboard.currentState;
    POSKeyboard.focusBasketEl(elems, 0);
    assert.equal(POSKeyboard.currentState, 'BASKET', 'doit passer en BASKET');
    POSKeyboard.focusBasketEl(elems, elems.length - 1);
    assert.equal(POSKeyboard.currentState, 'BASKET', 'reste en BASKET');
    POSKeyboard.setState(before === 'BASKET' ? 'SEARCH' : before);
});

POSTestRunner.test('Keyboard : Paiement - ref et montant sont contigus (Enter passe au suivant)', (assert) => {
    const zone = document.getElementById('test-payment-zone');
    const inputContainer = zone && zone.querySelector('.input-container');
    if (!inputContainer) { assert.isTrue(true, 'pas de zone paiement, test sauté'); return; }

    // Injecter les sections chèque (référence + montant) visibles dans la zone paiement
    const refSec = document.createElement('div');
    refSec.className = 'form-input paiement-section';
    refSec.dataset.mode = 'cheque';
    const refEl = document.createElement('input');
    refEl.className = 'form-control';
    refEl.name = 'refCheque';
    refSec.appendChild(refEl);
    const montSec = document.createElement('div');
    montSec.className = 'form-input paiement-section';
    montSec.dataset.mode = 'cheque';
    const montEl = document.createElement('input');
    montEl.className = 'form-control';
    montEl.name = 'montantCheque';
    montSec.appendChild(montEl);
    inputContainer.appendChild(refSec);
    inputContainer.appendChild(montSec);

    const elems = POSKeyboard.getPaymentElements();
    const idxRef = elems.indexOf(refEl);
    const idxMont = elems.indexOf(montEl);
    assert.isTrue(idxRef !== -1, 'refCheque doit être dans la liste paiement');
    assert.isTrue(idxMont !== -1, 'montantCheque doit être dans la liste paiement');
    assert.equal(idxMont, idxRef + 1, 'montant doit suivre immédiatement la référence (Entrée = input suivant)');

    refSec.remove();
    montSec.remove();
});

POSTestRunner.test('Keyboard : Panier - le focus/surlignage est sur le tr, pas sur l input qte', (assert) => {
    const row0 = document.getElementById('ligne-multiple-0');
    if (!row0) { assert.isTrue(true, 'pas de ligne sandbox, test sauté'); return; }
    const sandbox = document.getElementById('sandbox-dom');
    const prevHidden = row0.classList.contains('d-none');
    const previousSandboxDisplay = sandbox ? sandbox.style.display : '';
    const previousTabindex = row0.getAttribute('tabindex');

    try {
        // Un élément dans display:none ne peut pas recevoir le focus navigateur.
        if (sandbox) sandbox.style.display = 'block';
        row0.classList.remove('d-none');
        row0.removeAttribute('tabindex');
        row0.classList.remove('pos-kb-focused-row');

        POSKeyboard.focusBasket(0);

        assert.isTrue(row0.classList.contains('pos-kb-focused-row'), 'le tr doit porter la surbrillance de focus');
        assert.isTrue(row0.hasAttribute('tabindex'), 'le tr doit être focalisable (tabindex)');
        assert.equal(document.activeElement, row0, 'le focus DOM doit rester sur le tr');
    } finally {
        row0.classList.toggle('d-none', prevHidden);
        if (previousTabindex === null) row0.removeAttribute('tabindex');
        else row0.setAttribute('tabindex', previousTabindex);
        if (sandbox) sandbox.style.display = previousSandboxDisplay;
    }
});

POSTestRunner.test('Keyboard : + et - modifient uniquement la ligne panier focalisée', (assert) => {
    const container = document.getElementById('formTableContainer');
    const tbody = document.getElementById('ajout_multiple_ligne');
    const row0 = document.getElementById('ligne-multiple-0');
    const sandbox = document.getElementById('sandbox-dom');
    if (!container || !tbody || !row0) { assert.isTrue(true, 'sandbox panier indisponible, test sauté'); return; }

    const previousHidden = row0.classList.contains('d-none');
    const previousSandboxDisplay = sandbox ? sandbox.style.display : '';
    if (sandbox) sandbox.style.display = 'block';
    const clientA = document.createElement('input');
    const clientB = document.createElement('input');
    clientA.id = 'test-client-a';
    clientB.id = 'test-client-b';
    container.insertBefore(clientA, container.firstChild);
    container.insertBefore(clientB, container.firstChild);

    const row1 = document.createElement('tr');
    row1.innerHTML = '<td><input name="qte_901" value="4"></td>';
    const row2 = document.createElement('tr');
    row2.innerHTML = '<td><input name="qte_902" value="7"></td>';
    tbody.appendChild(row1);
    tbody.appendChild(row2);
    row0.classList.remove('d-none');
    row0.querySelector('input[name^="qte_"]').value = '1';

    const originalUpdateTotals = window.updateTotals;
    const originalSync = window.sync;
    window.updateTotals = function() {};
    window.sync = function() {};

    POSKeyboard.focusBasket(0);
    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Add', bubbles: true }));
    assert.equal(row0.querySelector('input[name^="qte_"]').value, '2', '+ doit incrémenter la première ligne focalisée');
    assert.equal(row1.querySelector('input').value, '4', '+ ne doit pas modifier la deuxième ligne');
    assert.equal(row2.querySelector('input').value, '7', '+ ne doit pas modifier la troisième ligne');

    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Unidentified', code: 'Minus', bubbles: true }));
    assert.equal(row0.querySelector('input[name^="qte_"]').value, '1', 'la touche physique Minus doit décrémenter la même ligne');

    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Unidentified', code: 'Minus', bubbles: true }));
    const removalModal = document.getElementById('pos-quantity-removal-modal');
    assert.isTrue(!!removalModal && removalModal.classList.contains('is-open'), '- depuis 1 doit ouvrir la confirmation');
    assert.equal(row0.querySelector('input[name^="qte_"]').value, '1', 'la quantité doit rester à 1 avant confirmation');
    window.cancelQuantityRemoval();

    POSKeyboard.focusBasketEl(POSKeyboard.getBasketElements(), 0);
    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Add', bubbles: true }));
    assert.equal(row0.querySelector('input[name^="qte_"]').value, '1', '+ ne doit rien modifier depuis un champ client');

    window.updateTotals = originalUpdateTotals;
    window.sync = originalSync;
    clientA.remove();
    clientB.remove();
    row1.remove();
    row2.remove();
    row0.classList.toggle('d-none', previousHidden);
    if (sandbox) sandbox.style.display = previousSandboxDisplay;
    POSKeyboard.setState('SEARCH');
});

POSTestRunner.test('Keyboard : Suppr depuis le paiement ne retire aucun article', (assert) => {
    const row0 = document.getElementById('ligne-multiple-0');
    const paymentInput = document.querySelector('#test-payment-zone input.form-control');
    if (!row0 || !paymentInput) { assert.isTrue(true, 'sandbox indisponible, test sauté'); return; }

    const previousHidden = row0.classList.contains('d-none');
    const deleteButton = document.createElement('button');
    let deleteCount = 0;
    deleteButton.type = 'button';
    deleteButton.className = 'btn-danger';
    deleteButton.addEventListener('click', function() { deleteCount++; });
    row0.appendChild(deleteButton);
    row0.classList.remove('d-none');

    POSKeyboard.focusBasket(0);
    POSKeyboard.setState('PAYMENT');
    paymentInput.focus();
    paymentInput.dispatchEvent(new KeyboardEvent('keydown', { key: 'Delete', bubbles: true }));

    assert.equal(deleteCount, 0, 'Suppr dans le paiement ne doit pas déclencher la suppression panier');
    assert.isTrue(document.contains(row0), 'la ligne panier doit rester dans le DOM');

    deleteButton.remove();
    row0.classList.toggle('d-none', previousHidden);
    POSKeyboard.setState('SEARCH');
});

POSTestRunner.test('Keyboard : validation désactivée ne contourne pas le submit', (assert) => {
    const form = document.createElement('form');
    form.id = 'venteForm';
    const button = document.createElement('button');
    button.type = 'submit';
    button.className = 'btn-primary btn-large';
    button.disabled = true;
    form.appendChild(button);
    document.body.appendChild(form);

    let clickCount = 0;
    let submitCount = 0;
    button.addEventListener('click', function() { clickCount++; });
    form.addEventListener('submit', function(event) {
        event.preventDefault();
        submitCount++;
    });

    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter', ctrlKey: true, bubbles: true }));
    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter', shiftKey: true, bubbles: true }));

    assert.equal(clickCount, 0, 'le bouton désactivé ne doit pas être cliqué');
    assert.equal(submitCount, 0, 'aucune soumission ne doit être déclenchée');

    form.remove();
});

POSTestRunner.test('Keyboard : Entrée depuis la recherche cible le premier bouton du header', (assert) => {
    const header = document.createElement('div');
    header.className = 'vd-header';
    const search = document.createElement('input');
    search.id = 'idProduit_libelle';
    const firstButton = document.createElement('button');
    firstButton.type = 'button';
    firstButton.className = 'btn';
    firstButton.textContent = 'POS';
    const secondButton = document.createElement('button');
    secondButton.type = 'button';
    secondButton.className = 'btn';
    secondButton.textContent = 'Mouvement';
    header.appendChild(search);
    header.appendChild(firstButton);
    header.appendChild(secondButton);
    document.body.appendChild(header);

    POSKeyboard.setState('SEARCH');
    search.focus();
    search.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter', bubbles: true }));

    assert.isTrue(document.activeElement === firstButton, 'Entrée doit sortir de la recherche vers le premier bouton');

    const row0 = document.getElementById('ligne-multiple-0');
    const sandbox = document.getElementById('sandbox-dom');
    const previousHidden = row0 ? row0.classList.contains('d-none') : true;
    const previousDisplay = sandbox ? sandbox.style.display : '';
    if (sandbox) sandbox.style.display = 'block';
    if (row0) row0.classList.remove('d-none');

    firstButton.dispatchEvent(new KeyboardEvent('keydown', { key: 'Tab', shiftKey: true, bubbles: true }));
    assert.equal(POSKeyboard.currentState, 'BASKET', 'Shift + Tab depuis le header doit activer la zone panier');
    if (row0) assert.isTrue(document.activeElement === row0, 'la première ligne du panier doit recevoir le focus');

    if (row0) row0.classList.toggle('d-none', previousHidden);
    if (sandbox) sandbox.style.display = previousDisplay;

    header.remove();
    POSKeyboard.setState('SEARCH');
});

POSTestRunner.test('Keyboard : paiement Haut/Bas suit les colonnes de la grille', (assert) => {
    const elements = [];
    for (let index = 0; index < 4; index++) {
        const button = document.createElement('label');
        button.className = 'toggle-button';
        button.innerHTML = '<input type="radio" name="payment-method" value="test-' + index + '">';
        elements.push(button);
    }
    const input = document.createElement('input');
    elements.push(input);
    assert.equal(POSKeyboard.getPaymentDirectionalIndex(elements, 0, 'ArrowDown'), 2, 'Bas doit rester dans la même colonne');
    assert.equal(POSKeyboard.getPaymentDirectionalIndex(elements, 2, 'ArrowUp'), 0, 'Haut doit revenir dans la même colonne');
    assert.equal(POSKeyboard.getPaymentDirectionalIndex(elements, 2, 'ArrowDown'), 4, 'Bas depuis la dernière ligne doit atteindre la saisie');
});

POSTestRunner.test('Keyboard : le dropdown Mouvement expose et parcourt ses liens', async (assert) => {
    const header = document.createElement('div');
    header.className = 'vd-header';
    header.innerHTML = '<div class="dropdown"><button class="btn" id="dropdownMenu1" aria-expanded="false">Mouvement</button><ul class="dropdown-menu"><li><a href="#one">Entrée</a></li><li><a href="#two">Sortie</a></li></ul></div>';
    document.body.appendChild(header);
    const dropdown = header.querySelector('.dropdown');
    const button = header.querySelector('#dropdownMenu1');
    button.addEventListener('click', function () {
        dropdown.classList.add('open');
        button.setAttribute('aria-expanded', 'true');
    });
    POSKeyboard.openHeaderDropdown(header, button);
    await new Promise(resolve => window.setTimeout(resolve, 0));
    const items = POSKeyboard.getHeaderDropdownItems(header);
    assert.equal(items.length, 2, 'les deux liens doivent être accessibles');
    assert.isTrue(document.activeElement === items[0], 'le premier lien doit recevoir le focus à l’ouverture');
    items[1].focus();
    assert.isTrue(document.activeElement === items[1], 'un autre lien du menu doit pouvoir recevoir le focus');
    header.remove();
});
