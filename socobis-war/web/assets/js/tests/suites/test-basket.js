/**
 * Suite de tests - Panier & DOM
 */
POSTestRunner.test('Basket : Fonctions du panier bien enregistrées', (assert) => {
    assert.isTrue(typeof updateTotals === 'function', "updateTotals existe");
    assert.isTrue(typeof handleProductSelection === 'function', "handleProductSelection existe");
    assert.isTrue(typeof removeLine === 'function', "removeLine existe");
    assert.isTrue(typeof verrouillerChampsButFille === 'function', "verrouillerChampsButFille existe");
    assert.isTrue(typeof saveCartState === 'function', "saveCartState existe");
    assert.isTrue(typeof restoreCartState === 'function', "restoreCartState existe");
});

POSTestRunner.test('Basket : updateTotals met à jour le montant dans le DOM', (assert) => {
    updateTotals();
    const totalText = document.getElementById('totalGeneral').textContent.replace(/[\u00A0\u202F]/g, ' ');
    assert.equal(totalText, "15 000 Ar", "Le total calculé dans le DOM doit être 15 000 Ar");
});

POSTestRunner.test('Basket : une ligne produit masquée est rendue visible en secours', (assert) => {
    const row = document.getElementById('ligne-multiple-0');
    if (!row) { assert.isTrue(true, 'ligne de test indisponible'); return; }
    let id = row.querySelector('input[name^="idProduit_"]');
    const createdId = !id;
    if (!id) {
        id = document.createElement('input');
        id.type = 'hidden';
        id.name = 'idProduit_0';
        row.appendChild(id);
    }
    const oldId = id.value;
    id.value = 'PROD-FALLBACK';
    row.classList.add('d-none');
    row.hidden = true;
    row.style.display = 'none';

    assert.isTrue(window.ensureProductRowVisible(row), 'le fallback doit reconnaître la ligne renseignée');
    assert.isFalse(row.classList.contains('d-none'), 'la classe de masquage doit être retirée');
    assert.isFalse(row.hidden, 'l’attribut hidden doit être retiré');
    assert.isFalse(row.style.display === 'none', 'le style display none doit être retiré');

    if (createdId) id.remove();
    else id.value = oldId;
    row.hidden = false;
    row.style.removeProperty('display');
    row.classList.add('d-none');
});

POSTestRunner.test('Basket : la quantité zéro demande confirmation avant suppression', async (assert) => {
    const tbody = document.getElementById('ajout_multiple_ligne');
    if (!tbody) { assert.isTrue(true, 'panier de test indisponible'); return; }
    const row = document.createElement('tr');
    row.id = 'ligne-multiple-999';
    row.dataset.productName = 'Produit de test';
    row.innerHTML = '<td><input name="idProduit_999" value="PROD-999"></td><td><input name="qte_999" value="1"></td>';
    tbody.appendChild(row);

    assert.isTrue(window.requestQuantityRemoval(row), 'la demande doit ouvrir le dialogue');
    const modal = document.getElementById('pos-quantity-removal-modal');
    assert.isTrue(modal.classList.contains('is-open'), 'le dialogue doit être visible');
    assert.equal(row.querySelector('[name="qte_999"]').value, '1', 'la quantité doit rester à 1 pendant la décision');
    assert.isTrue(document.activeElement === modal.querySelector('#pos-quantity-confirm-cancel'), 'Conserver doit recevoir le focus par défaut');

    window.cancelQuantityRemoval();
    assert.isTrue(document.contains(row), 'annuler doit conserver la ligne');
    assert.equal(row.querySelector('[name="qte_999"]').value, '1', 'annuler doit conserver une quantité de 1');

    window.requestQuantityRemoval(row);
    window.confirmQuantityRemoval();
    await new Promise(resolve => window.setTimeout(resolve, 0));
    assert.isFalse(document.contains(row), 'confirmer doit retirer la ligne du panier');
});
