/**
 * POS APJ - Test Runner Framework (pos-test-runner.js)
 */

(function (window) {
    'use strict';

    const POSTestRunner = {
        tests: [],
        results: { passed: 0, failed: 0, total: 0 },

        test(description, testFn) {
            this.tests.push({ description, testFn });
        },

        assert: {
            equal(actual, expected, message) {
                if (actual !== expected) {
                    throw new Error(message || `Attendu: [${expected}] (${typeof expected}), Reçu: [${actual}] (${typeof actual})`);
                }
            },
            deepEqual(actual, expected, message) {
                const aStr = JSON.stringify(actual);
                const eStr = JSON.stringify(expected);
                if (aStr !== eStr) {
                    throw new Error(message || `Attendu: ${eStr}, Reçu: ${aStr}`);
                }
            },
            isTrue(value, message) {
                if (value !== true) {
                    throw new Error(message || `Attendu: true, Reçu: ${value}`);
                }
            },
            isFalse(value, message) {
                if (value !== false) {
                    throw new Error(message || `Attendu: false, Reçu: ${value}`);
                }
            }
        },

        async runAll() {
            this.results = { passed: 0, failed: 0, total: this.tests.length };
            const container = document.getElementById('test-results') || document.body;
            container.innerHTML = '<h2>🚀 Exécution de la suite de tests POS APJ...</h2>';

            for (const testItem of this.tests) {
                const testCard = document.createElement('div');
                testCard.style.cssText = 'margin: 6px 0; padding: 10px; border-radius: 4px; font-family: monospace; font-size: 13px;';

                try {
                    await testItem.testFn(this.assert);
                    this.results.passed++;
                    testCard.style.background = '#e6fffa';
                    testCard.style.border = '1px solid #38b2ac';
                    testCard.style.color = '#234e52';
                    testCard.innerHTML = `✅ <strong>PASS</strong> : ${testItem.description}`;
                } catch (error) {
                    this.results.failed++;
                    testCard.style.background = '#fff5f5';
                    testCard.style.border = '1px solid #e53e3e';
                    testCard.style.color = '#742a2a';
                    testCard.innerHTML = `❌ <strong>FAIL</strong> : ${testItem.description}<br><span style="margin-left:22px;display:block;font-weight:bold;">⚠️ ${error.message}</span>`;
                }
                container.appendChild(testCard);
            }

            const summary = document.createElement('div');
            summary.style.cssText = 'margin-bottom:15px; padding:12px; font-weight:bold; font-size:15px; border-radius:6px; background:#edf2f7; border:1px solid #cbd5e0;';
            summary.innerHTML = `Bilan : Total = ${this.results.total} | <span style="color:#276749">Réussis = ${this.results.passed}</span> | <span style="color:#9b2c2c">Échoués = ${this.results.failed}</span>`;
            container.insertBefore(summary, container.firstChild);
        }
    };

    window.POSTestRunner = POSTestRunner;

})(window);