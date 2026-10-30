/**
 * Suite de tests - Core & Calculs
 */
POSTestRunner.test('Core : Conversions et formatages de montants', (assert) => {
    const norm = (s) => String(s).replace(/[\u00A0\u202F]/g, ' ');
    assert.equal(parseAmount("15 000 Ar"), 15000, "15 000 Ar => 15000");
    assert.equal(norm(formatAmount(15000, true)), "15 000 Ar", "15000 => 15 000 Ar");
    assert.equal(roundToNearest100(15049), 15000, "Arrondi à 15000");
    assert.equal(roundToNearest100(15050), 15100, "Arrondi à 15100");
    assert.equal(norm(formatNumberWithSpaces(15000)), "15 000", "Séparateur de milliers");
});

POSTestRunner.test('Core : Décodage des codes-barres balance au poids', (assert) => {
    const validWeight = parseWeightBarcode("2100123004501");
    assert.isTrue(validWeight.isWeightBased, "Doit être identifié au poids");
    assert.equal(validWeight.productCode, "123", "Code produit = 123");
    assert.equal(validWeight.weight, 450, "Poids = 450g");

    const standardCode = parseWeightBarcode("6001234567890");
    assert.isFalse(standardCode.isWeightBased, "Doit ignorer le code-barres standard");
});

POSTestRunner.test('Core : parseAmount gère null et undefined', (assert) => {
    assert.equal(window.parseAmount(null), 0, 'null => 0');
    assert.equal(window.parseAmount(undefined), 0, 'undefined => 0');
});

POSTestRunner.test('Core : parseAmount gère les formats variés', (assert) => {
    assert.equal(window.parseAmount('1 500 Ar'), 1500, '"1 500 Ar" => 1500');
    assert.equal(window.parseAmount('1,5'), 1.5, '"1,5" => 1.5');
});

POSTestRunner.test('Core : formatAmount inclut la devise', (assert) => {
    const res = window.formatAmount(1500, true);
    assert.isTrue(res.indexOf('Ar') !== -1, 'doit contenir Ar');
});

POSTestRunner.test('Core : roundToNearest100 cas limites', (assert) => {
    assert.equal(window.roundToNearest100(0), 0, '0 => 0');
    assert.equal(window.roundToNearest100(149), 100, '149 => 100');
    assert.equal(window.roundToNearest100(150), 200, '150 => 200');
});

POSTestRunner.test('Core : resolveProductImage retourne une URL', (assert) => {
    const url = window.resolveProductImage(null);
    assert.isTrue(typeof url === 'string' && url.length > 0, 'doit retourner une URL');
});