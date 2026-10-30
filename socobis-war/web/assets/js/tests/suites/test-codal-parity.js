/**
 * Contrats de portage Codal -> Aigle d'Or.
 * Ces tests décrivent le comportement attendu avant de modifier les modules POS.
 * Les échecs signalent les fonctions qui restent à porter ; aucun test n'est sauté.
 */
(function () {
    'use strict';

    const wait = ms => new Promise(resolve => window.setTimeout(resolve, ms));
    const amount = id => window.parseAmount(document.getElementById(id).value);
    const total = () => window.parseAmount(document.getElementById('totalGeneral').textContent);
    const columnNames = 'photo;libelle;reference;quantite=1;pvTva;pv;codegros;pvgros;remise=0;prix=0;unite;compte_vente;codeBarre;tva;montantttc';
    const inputNames = 'photo;designation;reference;qte;pvTva;pu;codegros;pu_pvgros;remiseMontant;prix;unite;compte;codeBarre;tva;montantttc';

    function product(id, name, price, containerId) {
        return {
            value: id,
            label: name,
            retour: ['image.png', name, id, price, price, '', '', 'UNT00001', '', '', '0', price, containerId || ''].join(';')
        };
    }

    function rowMarkup(index, oldLabel) {
        return '<tr id="ligne-multiple-' + index + '" class="d-none">' +
            '<td><input type="checkbox" name="ids" id="checkbox' + index + '" value="' + index + '"></td>' +
            '<td><input name="photo_' + index + '" id="photo_' + index + '"></td>' +
            '<td data-test-article><span>' + (oldLabel || '') + '</span><input name="designation_' + index + '" id="designation_' + index + '" value=""></td>' +
            '<td><input name="qte_' + index + '" id="qte_' + index + '" value="1"></td>' +
            '<td><input name="pvTva_' + index + '" id="pvTva_' + index + '" value="0"></td>' +
            '<td><input name="remiseMontant_' + index + '" id="remiseMontant_' + index + '" value="0"></td>' +
            '<td><input name="montant_' + index + '" id="montant_' + index + '" value="0"></td>' +
            '<td><input name="unite_' + index + '" id="unite_' + index + '" value="UNT00001"></td>' +
            '</tr>';
    }

    async function withBasketFixture(run) {
        const tbody = document.getElementById('ajout_multiple_ligne');
        if (!tbody) throw new Error('Le tableau de panier manque au runner');
        const original = tbody.innerHTML;
        const addLines = document.createElement('button');
        addLines.id = 'addLines';
        addLines.type = 'button';
        addLines.addEventListener('click', function () {
            if (!document.getElementById('ligne-multiple-1')) {
                tbody.insertAdjacentHTML('beforeend', rowMarkup(1, 'Ancien produit'));
            }
        });
        tbody.after(addLines);
        tbody.innerHTML = rowMarkup(0, 'Corned beef');

        const previous = {
            fetch: window.fetch,
            sync: window.sync,
            fermerClavierVisuel: window.fermerClavierVisuel,
            focusOnProduit: window.focusOnProduit,
            fetchAutocomplete: window.fetchAutocomplete,
            isKeyboardMode: window.isKeyboardMode
        };
        window.fetch = () => new Promise(() => {}); // La remise réseau n'entre pas dans ces scénarios.
        window.sync = function () {};
        window.fermerClavierVisuel = function () {};
        window.focusOnProduit = function () {};
        window.isKeyboardMode = true;
        try {
            await run(tbody);
        } finally {
            Object.keys(previous).forEach(key => { window[key] = previous[key]; });
            addLines.remove();
            tbody.innerHTML = original;
            window.cancelQuantityRemoval?.();
        }
    }

    async function withPaymentFixture(run) {
        const zone = document.getElementById('test-payment-zone');
        const mp = document.getElementById('multiple-payment');
        const exact = document.getElementById('totalGeneral');
        const rounded = document.getElementById('montantArrondiEspeces');
        if (!zone || !mp || !exact || !rounded) throw new Error('Fixture de paiement incomplète');
        const buttons = Array.from(zone.querySelectorAll('.toggle-container .toggle-button'));
        const values = ['valeurEspece', 'montantMvola', 'montantCheque', 'refMvola', 'refCheque'];
        const before = {
            exact: exact.textContent,
            rounded: rounded.textContent,
            multiple: mp.checked,
            active: buttons.map(button => button.classList.contains('active')),
            checked: buttons.map(button => button.querySelector('input').checked),
            values: values.map(id => document.getElementById(id).value)
        };
        try {
            exact.textContent = '12 528 Ar';
            rounded.textContent = '12 500 Ar';
            mp.checked = false;
            mp.dispatchEvent(new Event('change', { bubbles: true }));
            buttons.forEach(button => button.classList.remove('active'));
            const cash = zone.querySelector('input[value="especes"]').closest('.toggle-button');
            cash.classList.add('active');
            cash.querySelector('input').checked = true;
            values.forEach(id => { document.getElementById(id).value = ''; });
            await run(zone, mp);
        } finally {
            mp.checked = before.multiple;
            exact.textContent = before.exact;
            rounded.textContent = before.rounded;
            buttons.forEach((button, index) => {
                button.classList.toggle('active', before.active[index]);
                button.querySelector('input').checked = before.checked[index];
            });
            values.forEach((id, index) => { document.getElementById(id).value = before.values[index]; });
        }
    }

    POSTestRunner.test('Portage Codal : retirer la ligne 0 ne soumet plus un produit vide', async assert => {
        await withBasketFixture(tbody => {
            const first = tbody.querySelector('#ligne-multiple-0');
            const second = document.createElement('tr');
            second.id = 'ligne-multiple-1';
            second.innerHTML = '<td><input type="checkbox" name="ids" value="1" checked></td><td><input name="idProduit_1" value="JUS"></td>';
            tbody.appendChild(second);
            first.querySelector('[name="ids"]').checked = true;
            first.querySelector('[name="designation_0"]').value = 'Corned beef';
            removeLine(first);
            assert.isFalse(first.querySelector('[name="ids"]').checked, 'la ligne modèle supprimée reste cochée pour APJ');
            assert.isTrue(second.querySelector('[name="ids"]').checked, 'la seconde ligne doit rester soumise');
        });
    });

    POSTestRunner.test('Portage Codal : une nouvelle sélection remplace le libellé cloné', async assert => {
        await withBasketFixture(tbody => {
            handleProductSelection(product('JUS', 'Jus', 12528), 'idProduit_libelle', columnNames, inputNames, 'id', { quantite: '1', remise: '0', prix: '0' });
            const row = tbody.querySelector('#ligne-multiple-0');
            assert.equal(row.querySelector('[name="idProduit_0"]').value, 'JUS');
            assert.equal(row.querySelector('[name="designation_0"]').value, 'Jus');
            const visibleName = row.querySelector('[data-test-article]').textContent.trim();
            assert.equal(visibleName, 'Jus', 'le nom visible ne doit pas rester « Corned beef »');
        });
    });

    POSTestRunner.test('Portage Codal : aucun contenant n’est ajouté automatiquement', async assert => {
        await withBasketFixture(tbody => {
            handleProductSelection(product('JUS', 'Jus', 12528, 'CONT'), 'idProduit_libelle',
                columnNames, inputNames, 'id', { quantite: '1', remise: '0', prix: '0' });
            assert.equal(tbody.querySelectorAll("input[name^='idProduit_']").length, 1,
                'la sélection d’un produit ne crée qu’une seule ligne');
        });
    });

    POSTestRunner.test('Portage Codal : la virgule décimale est conservée dans le panier', async assert => {
        await withBasketFixture(async tbody => {
            const input = tbody.querySelector("[name='qte_0']");
            tbody.querySelector("[name='designation_0']").value = 'Jus';
            input.value = '7,5';
            input.dispatchEvent(new Event('input', { bubbles: true }));
            await wait(1100);
            assert.equal(input.value, '7,5', 'une quantité valide ne doit pas être remplacée');
            assert.equal(gatherProductData()[0].quantite, 7.5, 'la sauvegarde comprend la virgule');
        });
    });

    POSTestRunner.test('Portage Codal : « 7aha{} » propose 7 après une seconde sans corriger avant accord', async assert => {
        await withBasketFixture(async tbody => {
            const row = tbody.querySelector('tr');
            const input = row.querySelector('[name="qte_0"]');
            row.dataset.productName = 'Jus';
            input.value = '7aha{}';
            input.dispatchEvent(new Event('input', { bubbles: true }));
            assert.equal(input.value, '7aha{}', 'la correction ne doit pas être imposée pendant la saisie');
            await wait(1100);
            const modal = document.getElementById('pos-quantity-removal-modal');
            assert.isTrue(!!modal && modal.classList.contains('is-open'), 'le dialogue de correction doit apparaître');
            assert.isTrue(modal.textContent.includes('7'), 'la quantité proposée doit être 7');
            modal.querySelector('#pos-quantity-confirm-delete').click();
            assert.equal(input.value, '7', 'Confirmer doit appliquer la valeur corrigée');
        });
    });

    POSTestRunner.test('Portage Codal : quantité vide et négative proposent 1 ou le retrait', async assert => {
        await withBasketFixture(async tbody => {
            const row = tbody.querySelector('tr');
            const input = row.querySelector('[name="qte_0"]');
            for (const [value, word] of [['', 'manquante'], ['-3', 'négative']]) {
                input.value = value;
                input.dispatchEvent(new Event('input', { bubbles: true }));
                await wait(1100);
                const modal = document.getElementById('pos-quantity-removal-modal');
                assert.isTrue(!!modal && modal.classList.contains('is-open'), 'le dialogue doit apparaître pour ' + word);
                assert.isTrue(modal.textContent.toLowerCase().includes(word), 'le message doit expliquer la quantité ' + word);
                modal.querySelector('#pos-quantity-confirm-cancel').click();
                assert.equal(input.value, '1', 'Conserver doit rétablir une unité');
            }
        });
    });

    POSTestRunner.test('Portage Codal : une remise tardive ne modifie pas une ligne réutilisée', async assert => {
        await withBasketFixture(async tbody => {
            const row = tbody.querySelector('tr');
            const id = document.createElement('input');
            id.name = 'idProduit_0';
            id.id = 'idProduit_0';
            id.value = 'ANCIEN';
            row.appendChild(id);
            row.querySelector('[name="pvTva_0"]').value = '1000';
            let deliver;
            window.fetch = () => new Promise(resolve => { deliver = resolve; });
            const pending = checkRemiseForRow(0, 'ANCIEN', 'CLIENT', row);
            id.value = 'NOUVEAU';
            deliver({ json: () => Promise.resolve({ hasRemise: true, remise: 100, pvRemise: 900 }) });
            await pending;
            assert.equal(row.querySelector('[name="pvTva_0"]').value, '1000', 'une ancienne réponse ne doit pas changer le prix du nouveau produit');
            assert.equal(row.querySelector('[name="remiseMontant_0"]').value, '0', 'la remise ancienne ne doit pas être réutilisée');
        });
    });

    POSTestRunner.test('Portage Codal : une ligne restaurée reste visible et soumise à APJ', async assert => {
        await withBasketFixture(tbody => {
            const key = 'cartStateBeforeSubmit';
            const previous = sessionStorage.getItem(key);
            try {
                sessionStorage.setItem(key, JSON.stringify({
                    products: [{
                        id: 'JUS', designation: 'Jus', reference: 'REF-JUS',
                        quantite: 2, prixunitaire: 12528, remiseMontant: 0,
                        urlPhoto: '', isWeightBased: false, compte: ''
                    }],
                    client: { id: '', nom: '' }, timestamp: Date.now()
                }));
                restoreCartState();
                const row = tbody.querySelector('#ligne-multiple-0');
                assert.isFalse(row.classList.contains('d-none'), 'la ligne restaurée doit être affichée');
                assert.isTrue(row.querySelector('[name="ids"]').checked, 'APJ doit soumettre la ligne');
                assert.equal(row.querySelector('[name="idProduit_0"]').value, 'JUS');
                assert.equal(row.querySelector('[name="qte_0"]').value, '2');
                assert.equal(row.querySelector('[data-test-article]').textContent.trim(), 'Jus');
            } finally {
                if (previous === null) sessionStorage.removeItem(key);
                else sessionStorage.setItem(key, previous);
            }
        });
    });

    POSTestRunner.test('Portage Codal : le panier sauvegardé est consommé une seule fois', assert => {
        const key = 'cartStateBeforeSubmit';
        const previous = sessionStorage.getItem(key);
        try {
            sessionStorage.setItem(key, JSON.stringify({
                products: [],
                client: { id: '', nom: '' },
                timestamp: Date.now()
            }));
            restoreCartState();
            assert.equal(sessionStorage.getItem(key), null, 'la sauvegarde doit disparaître après restauration');
            restoreCartState();
            assert.equal(sessionStorage.getItem(key), null, 'un second chargement ne doit pas rejouer la sauvegarde');
        } finally {
            if (previous === null) sessionStorage.removeItem(key);
            else sessionStorage.setItem(key, previous);
        }
    });

    POSTestRunner.test('Portage Codal : les espèces simples restent vides et payables à 12 500 Ar', async assert => {
        await withPaymentFixture(() => {
            assert.equal(document.getElementById('valeurEspece').value, '', 'pas de montant espèces forcé');
            assert.isTrue(checkPaymentValidation(), 'les espèces vides doivent permettre le paiement simple');
        });
    });

    POSTestRunner.test('Portage Codal : un paiement simple non espèces prend le total exact et reste éditable', async assert => {
        await withPaymentFixture(async zone => {
            const cheque = zone.querySelector('input[value="cheque"]').closest('.toggle-button');
            cheque.click();
            await wait(100);
            assert.equal(amount('montantCheque'), total(), 'le montant par défaut doit être 12 528 Ar');
            document.getElementById('refCheque').value = 'REF-TEST';
            const input = document.getElementById('montantCheque');
            input.value = '13 000 Ar';
            input.dispatchEvent(new Event('input', { bubbles: true }));
            await wait(100);
            assert.equal(amount('montantCheque'), 13000, 'une valeur supérieure doit rester saisissable');
        });
    });

    POSTestRunner.test('Portage Codal : en multiple, Mvola prend le reste exact des espèces', async assert => {
        await withPaymentFixture(async (zone, mp) => {
            mp.checked = true;
            mp.dispatchEvent(new Event('change', { bubbles: true }));
            const mvola = zone.querySelector('input[value="mvola"]').closest('.toggle-button');
            mvola.click();
            const cash = document.getElementById('valeurEspece');
            cash.value = '10 000 Ar';
            cash.dispatchEvent(new Event('input', { bubbles: true }));
            await wait(100);
            assert.equal(amount('montantMvola'), 2528, '12 528 - 10 000 = 2 528 Ar');
            assert.equal(window.parseAmount(document.getElementById('montantPayerTexte').textContent), 12528,
                'le montant payé affiché suit les champs effectifs');
        });
    });
})();
