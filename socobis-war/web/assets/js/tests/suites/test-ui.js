/**
 * Suite de tests - Ergonomie & Focus UI
 */
POSTestRunner.test('UI : Fonctions d\'ergonomie et clavier virtuel', (assert) => {
    assert.isTrue(typeof focusOnProduit === 'function', "focusOnProduit existe");
    assert.isTrue(typeof ouvrirClavierVisuel === 'function', "ouvrirClavierVisuel existe");
    assert.isTrue(typeof fermerClavierVisuel === 'function', "fermerClavierVisuel existe");
    assert.isTrue(typeof setActiveInput === 'function', "setActiveInput existe");
});