/**
 * Suite de tests - Hardware & Synchronisation
 */
POSTestRunner.test('Hardware : sendMessage est une fonction', (assert) => {
    assert.isTrue(typeof window.sendMessage === 'function', 'sendMessage doit exister');
});

POSTestRunner.test('Hardware : sync est une fonction', (assert) => {
    assert.isTrue(typeof window.sync === 'function', 'sync doit exister');
});

POSTestRunner.test('Hardware : sendProducts est une fonction', (assert) => {
    assert.isTrue(typeof window.sendProducts === 'function', 'sendProducts doit exister');
});

POSTestRunner.test('Hardware : save est une fonction', (assert) => {
    assert.isTrue(typeof window.save === 'function', 'save doit exister');
});

POSTestRunner.test('Hardware : setupListeners est une fonction', (assert) => {
    assert.isTrue(typeof window.setupListeners === 'function', 'setupListeners doit exister');
});
