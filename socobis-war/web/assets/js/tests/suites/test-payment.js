/**
 * Suite de tests - Paiement & Règles des 8 modes
 */
POSTestRunner.test('Payment : Mappings et contrôles des 8 modes', (assert) => {
    assert.equal(Object.keys(MODES).length, 8, "8 modes configurés");
    assert.equal(getRefInputId('mvola'), 'refMvola', "Ref Mvola = refMvola");
    assert.equal(getMontantInputId('bonAchat'), 'montantBOA', "Montant Bon d'achat = montantBOA");
    assert.equal(getPaymentInputId('cadeaux'), 'montantCarte', "Montant Carte Cadeaux = montantCarte");
});

POSTestRunner.test('Payment : Validation dynamique du paiement', (assert) => {
    assert.isTrue(typeof checkPaymentValidation === 'function', "checkPaymentValidation existe");
    assert.isTrue(typeof checkSingleModeAmount === 'function', "checkSingleModeAmount existe");
    assert.isTrue(typeof controler === 'function', "controler existe");
});

POSTestRunner.test('Payment : MODES contient les 8 modes', (assert) => {
    const modes = ['especes','mvola','orange','airtel','cheque','visa','bonAchat','cadeaux'];
    modes.forEach(m => {
        assert.isTrue(!!MODES[m], 'mode manquant: ' + m);
    });
});

POSTestRunner.test('Payment : getRefInputId mapping', (assert) => {
    assert.equal(getRefInputId('visa'), 'refVisa', 'visa => refVisa');
    assert.equal(getRefInputId('cheque'), 'refCheque', 'cheque => refCheque');
});

POSTestRunner.test('Payment : getMontantInputId mapping', (assert) => {
    assert.equal(getMontantInputId('especes'), 'valeurEspece', 'especes => valeurEspece');
    assert.equal(getMontantInputId('orange'), 'montantOrange', 'orange => montantOrange');
});

POSTestRunner.test('Payment : checkPaymentValidation retourne un booléen', (assert) => {
    const res = checkPaymentValidation();
    assert.isTrue(typeof res === 'boolean', 'doit être un booléen');
});

POSTestRunner.test('Payment : retour en simple ne garde qu\'un seul mode actif', (assert) => {
    const mp = document.getElementById('multiple-payment');
    const zone = document.getElementById('test-payment-zone');
    if (!mp || !zone) { assert.isTrue(true, 'pas de zone paiement de test, test sauté'); return; }

    const buttons = Array.from(zone.querySelectorAll('.toggle-button'));
    // Récupérer les toggles de MODE (ceux qui contiennent un radio payment-method)
    const modeButtons = buttons.filter(b => b.querySelector('input[name="payment-method"]'));
    if (modeButtons.length < 2) { assert.isTrue(true, 'pas assez de modes, test sauté'); return; }

    // Passer en paiement multiple
    mp.checked = true;
    mp.dispatchEvent(new Event('change', { bubbles: true }));
    const chequeToggle = document.querySelector('#test-payment-zone .toggle-button input[value="cheque"]');
    if (chequeToggle) chequeToggle.closest('.toggle-button').classList.add('active');

    // Activer plusieurs modes
    modeButtons.forEach(b => { b.classList.add('active'); });
    zone.querySelectorAll('.paiement-section').forEach(s => {});

    // Revenir en paiement simple
    mp.checked = false;
    mp.dispatchEvent(new Event('change', { bubbles: true }));

    const actives = zone.querySelectorAll('.toggle-button.active');
    assert.equal(actives.length, 1, 'un seul mode doit rester actif après retour en simple');

    // Remise à zéro du checkbox pour ne pas perturber les autres tests
    mp.checked = false;
});

POSTestRunner.test('Payment : un montant non espèces supérieur reste modifiable', (assert) => {
    const mp = document.getElementById('multiple-payment');
    const cheque = document.getElementById('montantCheque');
    const toggle = document.querySelector('#test-payment-zone .toggle-button input[value="cheque"]')?.closest('.toggle-button');
    if (!mp || !cheque || !toggle) return;
    mp.checked = false;
    mp.dispatchEvent(new Event('change', { bubbles: true }));
    toggle.click();
    const total = window.parseAmount(document.getElementById('totalGeneral').textContent);
    cheque.value = String(total + 5000);
    cheque.dispatchEvent(new Event('input', { bubbles: true }));
    assert.equal(window.getRawMontant('montantCheque'), total + 5000,
        'un montant supérieur au total ne doit pas être réécrit');
    cheque.value = '';
});

POSTestRunner.test('Payment : espèces autorise le dépassement en mode multiple', (assert) => {
    const mp = document.getElementById('multiple-payment');
    const espece = document.getElementById('valeurEspece');
    if (!mp || !espece) { assert.isTrue(true, 'pas de champ de test, test sauté'); return; }

    const total = window.parseAmount(document.getElementById('totalGeneral').textContent);

    mp.checked = true;
    mp.dispatchEvent(new Event('change', { bubbles: true }));
    const cashToggle = document.querySelector('#test-payment-zone .toggle-button input[value="especes"]')?.closest('.toggle-button');
    const chequeToggle = document.querySelector('#test-payment-zone .toggle-button input[value="cheque"]')?.closest('.toggle-button');
    // Le test précédent peut laisser Chèque seul actif. Activer réellement les deux
    // modes avant de saisir les espèces ; un mode inactif est remis à zéro par le moteur.
    if (cashToggle && !cashToggle.classList.contains('active')) cashToggle.click();
    if (chequeToggle && !chequeToggle.classList.contains('active')) chequeToggle.click();
    try {
        assert.isTrue(window.getModesActifs().includes('especes') && window.getModesActifs().includes('cheque'),
            'Espèces et Chèque doivent être actifs pour ce scénario multiple');
        // Espèces : dépassement autorisé (pas d'écrêtage, pas d'erreur)
        espece.value = String(total + 5000);
        espece.dispatchEvent(new Event('input', { bubbles: true }));

        const finalValue = window.getRawMontant('valeurEspece');
        const errEl = document.getElementById('mpay-err-valeurEspece');
        assert.equal(finalValue, total + 5000, 'les espèces ne doivent pas être écrêtées');
        assert.isTrue(!errEl || errEl.style.display === 'none', 'aucune erreur inline sur les espèces');
    } finally {
        if (typeof window.hideAllErrors === 'function') window.hideAllErrors();
        espece.value = '';
        mp.checked = false;
        mp.dispatchEvent(new Event('change', { bubbles: true }));
    }
});

POSTestRunner.test('Payment : un seul mode actif reste un paiement simple', (assert) => {
    const mp = document.getElementById('multiple-payment');
    const zone = document.getElementById('test-payment-zone');
    if (!mp || !zone) { assert.isTrue(true, 'zone paiement indisponible'); return; }
    zone.querySelectorAll('.toggle-button').forEach(button => button.classList.remove('active'));
    const especesButton = zone.querySelector('.toggle-button input[value="especes"]').closest('.toggle-button');
    especesButton.classList.add('active');
    mp.checked = true;
    mp.dispatchEvent(new Event('change', { bubbles: true }));

    assert.isFalse(window.isMultiple(), 'une seule méthode active ne doit pas appliquer les règles multiples');
    assert.equal(document.getElementById('multiplePaymentInput').value, 'false', 'le contrat POST doit rester en paiement simple');

    mp.checked = false;
    mp.dispatchEvent(new Event('change', { bubbles: true }));
});

POSTestRunner.test('Payment : le montant affiché suit le solde automatique', (assert) => {
    const zone = document.getElementById('test-payment-zone');
    const mp = document.getElementById('multiple-payment');
    const cash = document.getElementById('valeurEspece');
    const mvola = document.getElementById('montantMvola');
    if (!zone || !mp || !cash || !mvola) return;
    const totalEl = document.getElementById('totalGeneral');
    const oldTotal = totalEl.textContent;
    totalEl.textContent = '218 000 Ar';
    mp.checked = true;
    mp.dispatchEvent(new Event('change', { bubbles: true }));
    const toggle = zone.querySelector('.toggle-button input[value="mvola"]').closest('.toggle-button');
    if (!toggle.classList.contains('active')) toggle.click();
    cash.value = '200000';
    cash.dispatchEvent(new Event('input', { bubbles: true }));
    assert.equal(window.getRawMontant('montantMvola'), 18000, 'Mvola doit couvrir le solde');
    assert.equal(window.parseAmount(document.getElementById('montantPayerTexte').textContent), 218000,
        'le montant payé affiché doit utiliser les valeurs effectives');
    cash.value = '';
    mvola.value = '';
    totalEl.textContent = oldTotal;
    mp.checked = false;
    mp.dispatchEvent(new Event('change', { bubbles: true }));
});
