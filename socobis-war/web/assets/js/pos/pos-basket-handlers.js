/**
 * POS APJ - Basket Handlers (pos-basket-handlers.js)
 * Correctif complet : Gestion du panier, remises Servlet, totaux et adaptation Mode Clavier
 */

(function (window) {
    'use strict';

    const productImageFallback = (window.location.origin || '') + '/aigledor/assets/img/products/no_image.png';
    let pendingQuantityRemovalRow = null;
    let quantityRemovalPreviousFocus = null;
    let pendingQuantityCorrection = null;
    const quantityInputCooldowns = new WeakMap();
    const quantityLastValidValues = new WeakMap();

    function normalizeQuantityInput(rawValue) {
        const raw = String(rawValue == null ? '' : rawValue).trim();
        let integerPart = '', decimalPart = '', decimalFound = false;
        for (const character of raw) {
            if (/\d/.test(character)) {
                if (decimalFound) decimalPart += character;
                else integerPart += character;
            } else if (!decimalFound && character === ',' && integerPart !== '') {
                decimalFound = true;
            }
        }
        const value = integerPart === '' ? '' : integerPart + (decimalFound && decimalPart ? ',' + decimalPart : '');
        return { raw, value, hasDigits: integerPart !== '', needsFormatting: raw !== value };
    }

    function rememberValidQuantity(input) {
        const normalized = normalizeQuantityInput(input.value);
        const quantity = Number(normalized.value.replace(',', '.'));
        if (normalized.hasDigits && !normalized.needsFormatting && Number.isFinite(quantity) && quantity > 0) {
            quantityLastValidValues.set(input, normalized.value);
        }
    }

    function resolveProductImage(photo) {
        if (!photo) return productImageFallback;
        if (/^https?:\/\//i.test(photo)) return photo;
        const contextPath = window.contextPath || '';
        return contextPath + '/assets/img/produit/' + photo;
    }

    /**
     * 🏷️ VÉRIFICATION DES REMISES SPÉCIFIQUES CLIENT (Servlet)
     */
    function checkRemiseForRow(rowCount, productId, idClient, rowEl) {
        const contextPath = window.contextPath || '';
        const idPoint = window.idPoint || document.getElementById('idPoint')?.value || '';
        const client = idClient || document.getElementById('idClient')?.value || document.querySelector("[name='idClient']")?.value || '';

        if (!productId) return Promise.resolve();

        return fetch(`${contextPath}/IngredientsRemiseServlet`, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                idProduit: productId,
                idClient: client,
                idPoint: idPoint
            })
        })
            .then(response => response.json())
            .then(data => {
                const row = rowEl || document.getElementById("remiseMontant_" + rowCount)?.closest('tr');
                if (!row || !document.contains(row)) return;
                if (row.querySelector("input[name^='idProduit_']")?.value !== String(productId)) return;
                const currentClient = document.getElementById('idClient')?.value
                    || document.querySelector("[name='idClient']")?.value || '';
                if (String(currentClient) !== String(client)) return;

                if (data && data.hasRemise) {
                    if (row) row.dataset.remiseUnitaire = window.parseAmount(data.remise);

                    const remInput = document.getElementById("remiseMontant_" + rowCount);
                    const puInput = document.getElementById("pvTva_" + rowCount);

                    if (remInput) remInput.value = data.remise;
                    if (puInput && data.pvRemise) puInput.value = data.pvRemise;

                    updateTotals();
                    if (typeof window.sync === 'function') window.sync();
                } else {
                    if (row) row.dataset.remiseUnitaire = 0;
                    const remInput = document.getElementById("remiseMontant_" + rowCount);
                    if (remInput) remInput.value = 0;

                    updateTotals();
                    if (typeof window.sync === 'function') window.sync();
                }
            })
            .catch(error => {
                console.error("Erreur lors de la vérification de la remise :", error);
            });
    }

    /**
     * Recalcule et réapplique les remises pour TOUS les produits lors du changement de client
     */
    function recheckRemiseAllRows(idClient) {
        const promises = [];
        document.querySelectorAll("#ajout_multiple_ligne tr").forEach(tr => {
            const idProduitInput = tr.querySelector("input[name^='idProduit_']");
            if (!idProduitInput || !idProduitInput.value) return;
            const match = idProduitInput.name.match(/\d+$/);
            if (match) {
                const rowCount = match[0];
                promises.push(checkRemiseForRow(rowCount, idProduitInput.value, idClient, tr));
            }
        });
        return Promise.all(promises).then(() => {
            updateTotals();
            if (typeof window.sync === 'function') window.sync();
        });
    }

    /**
     * ➕ / ➖ ENCAPSULATION DES BOUTONS + ET -
     * Adapte l'affichage selon window.isKeyboardMode
     */
    function formaterBoutonsQuantiteLigne(tr) {
        if (!tr) return;
        const qteInput = tr.querySelector("input[name^='qte_']");
        if (!qteInput) return;

        // ➕ MODE CLAVIER : Pas de boutons visuels +/-, input brut avec sa classe framework.
        if (window.isKeyboardMode) {
            qteInput.style.textAlign = '';
            return;
        }

        if (qteInput.closest('.qte-action-container')) return;

        const tdqte = qteInput.closest("td");
        if (!tdqte) return;

        const minusBtn = document.createElement("button");
        minusBtn.className = "btn btn-secondary btn-small minus-btn-qte";
        minusBtn.type = "button";
        minusBtn.innerHTML = '<i class="material-symbols-rounded">remove</i>';
        minusBtn.onclick = function() {
            const puInput = tr.querySelector("input[name^='pvTva_']");
            const basePriceKgInput = tr.querySelector("input[name^='basePriceKg_']");
            const uniteInput = tr.querySelector("input[name^='unite_']");
            const currentQte = window.parseAmount(qteInput.value || 0);
            const isWeightBased = (uniteInput?.value === "UNT00004");
            const decrement = isWeightBased ? 10 : 1;
            const newQte = Math.max(currentQte - decrement, 0);

            if (newQte === 0) {
                requestQuantityRemoval(tr);
                return;
            }

            qteInput.value = newQte;

            if (isWeightBased && basePriceKgInput && puInput) {
                const basePricePerKg = window.parseAmount(basePriceKgInput.value);
                puInput.value = (newQte * basePricePerKg) / 1000;
            }
            updateTotals();
            if (typeof window.sync === 'function') window.sync();
        };

        const plusBtn = document.createElement("button");
        plusBtn.className = "btn btn-secondary btn-small plus-btn-qte";
        plusBtn.type = "button";
        plusBtn.innerHTML = '<i class="material-symbols-rounded">add</i>';
        plusBtn.onclick = function() {
            const puInput = tr.querySelector("input[name^='pvTva_']");
            const basePriceKgInput = tr.querySelector("input[name^='basePriceKg_']");
            const uniteInput = tr.querySelector("input[name^='unite_']");
            const currentQte = window.parseAmount(qteInput.value || 0);
            const isWeightBased = (uniteInput?.value === "UNT00004");
            const increment = isWeightBased ? 10 : 1;
            const newQte = currentQte + increment;

            qteInput.value = newQte;

            if (isWeightBased && basePriceKgInput && puInput) {
                const basePricePerKg = window.parseAmount(basePriceKgInput.value);
                puInput.value = (newQte * basePricePerKg) / 1000;
            }
            updateTotals();
            if (typeof window.sync === 'function') window.sync();
        };

        const qteWrapper = document.createElement("div");
        qteWrapper.className = "d-flex gap-1 qte-action-container";
        qteWrapper.appendChild(minusBtn);
        qteWrapper.appendChild(qteInput);
        qteWrapper.appendChild(plusBtn);

        tdqte.innerHTML = "";
        tdqte.appendChild(qteWrapper);
    }

    function ensureQuantityRemovalModal() {
        let modal = document.getElementById('pos-quantity-removal-modal');
        if (modal) return modal;
        modal = document.createElement('div');
        modal.id = 'pos-quantity-removal-modal';
        modal.className = 'modal pos-quantity-confirm-modal';
        modal.setAttribute('role', 'alertdialog');
        modal.setAttribute('aria-modal', 'true');
        modal.setAttribute('aria-labelledby', 'pos-quantity-confirm-title');
        modal.setAttribute('aria-describedby', 'pos-quantity-confirm-description');
        modal.setAttribute('aria-hidden', 'true');
        modal.innerHTML = `
            <div class="pos-quantity-confirm-content">
                <div class="pos-quantity-confirm-header">
                    <h2 id="pos-quantity-confirm-title" class="h329pxBold">Quantité égale à zéro</h2>
                </div>
                <p id="pos-quantity-confirm-description" class="Body14pxRegular">
                    <span id="pos-quantity-confirm-message">La quantité renseignée est égale à zéro.</span>
                    <span id="pos-quantity-confirm-detail">Voulez-vous retirer <strong id="pos-quantity-confirm-product">cet article</strong> du panier&nbsp;?</span>
                </p>
                <div class="pos-quantity-confirm-actions">
                    <button type="button" class="btn btn-danger" id="pos-quantity-confirm-delete">Retirer du panier</button>
                    <button type="button" class="btn btn-secondary" id="pos-quantity-confirm-cancel">Conserver une unité</button>
                </div>
            </div>`;
        document.body.appendChild(modal);
        const cancelButton = modal.querySelector('#pos-quantity-confirm-cancel');
        const actionButton = modal.querySelector('#pos-quantity-confirm-delete');
        cancelButton.addEventListener('click', cancelQuantityRemoval);
        actionButton.addEventListener('click', confirmQuantityRemoval);
        modal.addEventListener('click', event => {
            if (event.target === modal) cancelQuantityRemoval();
        });
        modal.addEventListener('keydown', event => {
            if (event.key === 'Escape') {
                event.preventDefault();
                cancelQuantityRemoval();
            } else if (event.key === 'ArrowLeft' || event.key === 'ArrowRight') {
                event.preventDefault();
                (document.activeElement === cancelButton ? actionButton : cancelButton).focus();
            } else if (event.key === 'Enter' && (event.target === cancelButton || event.target === actionButton)) {
                event.preventDefault();
                event.target.click();
            }
        });
        return modal;
    }

    function openQuantityModal(row, input) {
        pendingQuantityRemovalRow = row;
        quantityRemovalPreviousFocus = input || document.activeElement;
        const modal = ensureQuantityRemovalModal();
        modal.classList.add('is-open', 'show');
        modal.setAttribute('aria-hidden', 'false');
        document.body.classList.add('pos-quantity-confirm-open');
        return modal;
    }

    function closeQuantityRemovalModal(restoreFocus) {
        const modal = document.getElementById('pos-quantity-removal-modal');
        if (!modal) return;
        modal.classList.remove('is-open', 'show');
        modal.setAttribute('aria-hidden', 'true');
        document.body.classList.remove('pos-quantity-confirm-open');
        if (restoreFocus && quantityRemovalPreviousFocus && document.contains(quantityRemovalPreviousFocus)) {
            quantityRemovalPreviousFocus.focus();
        }
        pendingQuantityRemovalRow = null;
        pendingQuantityCorrection = null;
        quantityRemovalPreviousFocus = null;
    }

    function requestQuantityRemoval(row, reason) {
        if (!row) return false;
        const qteInput = row.querySelector("input[name^='qte_']");
        if (!qteInput) return false;
        const pendingTimer = quantityInputCooldowns.get(qteInput);
        if (pendingTimer) window.clearTimeout(pendingTimer);
        quantityInputCooldowns.delete(qteInput);
        qteInput.value = '1';
        pendingQuantityCorrection = null;
        const modal = openQuantityModal(row, document.activeElement);
        const contents = {
            empty: ['Quantité manquante', 'Aucune quantité n’a été renseignée.', 'Mettre la quantité à 1'],
            negative: ['Quantité négative', 'Une quantité négative n’est pas autorisée.', 'Mettre la quantité à 1'],
            invalid: ['Quantité invalide', 'Aucun chiffre exploitable n’a été trouvé.', 'Remettre la quantité à 1'],
            zero: ['Quantité égale à zéro', 'La quantité renseignée est égale à zéro.', 'Conserver une unité']
        };
        const content = contents[reason] || contents.zero;
        modal.querySelector('#pos-quantity-confirm-title').textContent = content[0];
        modal.querySelector('#pos-quantity-confirm-message').textContent = content[1];
        const detail = modal.querySelector('#pos-quantity-confirm-detail');
        detail.textContent = 'Voulez-vous retirer ';
        const product = document.createElement('strong');
        product.id = 'pos-quantity-confirm-product';
        product.textContent = row.dataset.productName || row.querySelector("input[name^='designation_']")?.value || 'cet article';
        detail.appendChild(product);
        detail.appendChild(document.createTextNode(' du panier ?'));
        const cancel = modal.querySelector('#pos-quantity-confirm-cancel');
        const action = modal.querySelector('#pos-quantity-confirm-delete');
        cancel.textContent = content[2];
        action.textContent = 'Retirer du panier';
        action.classList.remove('btn-primary');
        action.classList.add('btn-danger');
        modal.querySelector('.pos-quantity-confirm-actions').append(action, cancel);
        cancel.focus();
        return true;
    }

    function requestQuantityFormatting(row, input, value) {
        if (!row || !input || !value) return false;
        const pendingTimer = quantityInputCooldowns.get(input);
        if (pendingTimer) window.clearTimeout(pendingTimer);
        quantityInputCooldowns.delete(input);
        pendingQuantityCorrection = {
            input, value,
            previousValue: quantityLastValidValues.get(input) || '1'
        };
        const modal = openQuantityModal(row, input);
        modal.querySelector('#pos-quantity-confirm-title').textContent = 'Format de quantité invalide';
        modal.querySelector('#pos-quantity-confirm-message').textContent =
            'La quantité « ' + input.value + ' » contient des caractères non autorisés.';
        modal.querySelector('#pos-quantity-confirm-detail').textContent =
            'La quantité sera corrigée en ' + value + '.';
        const cancel = modal.querySelector('#pos-quantity-confirm-cancel');
        const action = modal.querySelector('#pos-quantity-confirm-delete');
        cancel.textContent = 'Annuler';
        action.textContent = 'Confirmer';
        action.classList.remove('btn-danger');
        action.classList.add('btn-primary');
        modal.querySelector('.pos-quantity-confirm-actions').append(cancel, action);
        action.focus();
        return true;
    }

    function scheduleQuantityCheck(row, input) {
        const previous = quantityInputCooldowns.get(input);
        if (previous) window.clearTimeout(previous);
        const timer = window.setTimeout(() => {
            quantityInputCooldowns.delete(input);
            if (!document.contains(row)) return;
            const normalized = normalizeQuantityInput(input.value);
            const quantity = Number(normalized.value.replace(',', '.'));
            let reason = null;
            if (normalized.raw === '') reason = 'empty';
            else if (/^\s*-/.test(input.value)) reason = 'negative';
            else if (!normalized.hasDigits) reason = 'invalid';
            else if (quantity === 0) reason = 'zero';
            if (reason) {
                if (requestQuantityRemoval(row, reason)) {
                    updateTotals();
                    if (typeof window.sync === 'function') window.sync();
                }
            } else if (normalized.needsFormatting) {
                requestQuantityFormatting(row, input, normalized.value);
            } else {
                quantityLastValidValues.set(input, normalized.value);
            }
        }, 1000);
        quantityInputCooldowns.set(input, timer);
    }

    function cancelQuantityRemoval() {
        if (pendingQuantityCorrection) {
            const correction = pendingQuantityCorrection;
            correction.input.value = correction.previousValue;
            quantityLastValidValues.set(correction.input, correction.previousValue);
            closeQuantityRemovalModal(true);
            updateTotals();
            if (typeof window.sync === 'function') window.sync();
            return;
        }
        const input = pendingQuantityRemovalRow?.querySelector("input[name^='qte_']");
        if (input) {
            input.value = '1';
            quantityLastValidValues.set(input, '1');
        }
        closeQuantityRemovalModal(true);
        updateTotals();
        if (typeof window.sync === 'function') window.sync();
    }

    function confirmQuantityRemoval() {
        if (pendingQuantityCorrection) {
            const correction = pendingQuantityCorrection;
            correction.input.value = correction.value;
            quantityLastValidValues.set(correction.input, correction.value);
            closeQuantityRemovalModal(true);
            updateTotals();
            if (typeof window.sync === 'function') window.sync();
            return;
        }
        const row = pendingQuantityRemovalRow;
        if (!row) return closeQuantityRemovalModal(false);
        const visibleRows = Array.from(document.querySelectorAll('#ajout_multiple_ligne tr:not(.d-none)'));
        const rowIndex = visibleRows.indexOf(row);
        closeQuantityRemovalModal(false);
        removeLine(row);
        if (typeof window.refreshProductPreview === 'function') window.refreshProductPreview();
        window.setTimeout(() => {
            if (window.isKeyboardMode !== true || !window.POSKeyboard?.initialized) return;
            const remainingRows = window.POSKeyboard.getVisibleCartRows();
            if (remainingRows.length) window.POSKeyboard.focusBasket(Math.min(Math.max(rowIndex, 0), remainingRows.length - 1));
            else window.POSKeyboard.focusSearch();
        }, 0);
    }

    function validateBasketQuantities() {
        if (document.querySelector('#pos-quantity-removal-modal.is-open')) return false;
        const rows = document.querySelectorAll('#ajout_multiple_ligne tr:not(.d-none)');
        for (const row of rows) {
            if (!row.querySelector("input[name^='idProduit_']")?.value) continue;
            const input = row.querySelector("input[name^='qte_']");
            if (!input) continue;
            const normalized = normalizeQuantityInput(input.value);
            const quantity = Number(normalized.value.replace(',', '.'));
            let reason = null;
            if (normalized.raw === '') reason = 'empty';
            else if (/^\s*-/.test(input.value)) reason = 'negative';
            else if (!normalized.hasDigits) reason = 'invalid';
            else if (quantity === 0) reason = 'zero';
            if (reason) {
                requestQuantityRemoval(row, reason);
                return false;
            }
            if (normalized.needsFormatting) {
                requestQuantityFormatting(row, input, normalized.value);
                return false;
            }
        }
        return true;
    }

    document.addEventListener('focusin', event => {
        if (event.target?.matches("input[name^='qte_']")) rememberValidQuantity(event.target);
    });
    document.addEventListener('input', event => {
        const input = event.target;
        if (!input?.matches("input[name^='qte_']") || !input.closest('#ajout_multiple_ligne')) return;
        scheduleQuantityCheck(input.closest('tr'), input);
        updateTotals();
        if (typeof window.sync === 'function') window.sync();
    });

    function formaterTousBoutonsQuantite() {
        document.querySelectorAll('#ajout_multiple_ligne tr').forEach(tr => {
            formaterBoutonsQuantiteLigne(tr);
        });
    }

    function getIdNumber() {
        const ids = [];
        document.querySelectorAll("input[name^='designation_']").forEach(input => {
            const match = input.name.match(/^designation_(\d+)$/);
            if (match) ids.push(parseInt(match[1], 10));
        });
        return ids.length > 0 ? ids : [0];
    }

    function extractProductValue(id) {
        const designationInput = document.querySelector("[name='designation_" + id + "']");
        const idProduitInput = document.querySelector("[name='idProduit_" + id + "']");
        const quantiteInput = document.querySelector("[name='qte_" + id + "']");
        const prixInput = document.querySelector("[name='pvTva_" + id + "']");
        const remiseMontantInput = document.querySelector("[name='remiseMontant_" + id + "']");
        const photoInput = document.querySelector("[name='photo_" + id + "']");
        const referenceInput = document.querySelector("[name='reference_" + id + "']");
        const basePriceKgInput = document.querySelector("[name='basePriceKg_" + id + "']");
        const compteVenteInput = document.querySelector("[name='compte_" + id + "']");
        const unitInput = document.querySelector("[name='unite_" + id + "']");

        if (!designationInput || !quantiteInput || !prixInput) return null;
        const designation = designationInput.value.trim();
        if (!designation) return null;

        const quantite = window.parseAmount(quantiteInput.value);
        const prixunitaire = window.parseAmount(prixInput.value);
        const remiseMontant = window.parseAmount(remiseMontantInput?.value || 0);
        const isWeightBased = unitInput && unitInput.value === "UNT00004";

        return {
            id: idProduitInput ? idProduitInput.value : '',
            designation,
            reference: referenceInput?.value || '',
            quantite: isNaN(quantite) ? 0 : quantite,
            prixunitaire: isNaN(prixunitaire) ? 0 : prixunitaire,
            originalPrice: basePriceKgInput?.value || prixunitaire,
            remiseMontant: isNaN(remiseMontant) ? 0 : remiseMontant,
            urlPhoto: photoInput ? photoInput.value : '',
            isWeightBased: isWeightBased,
            compte: compteVenteInput ? compteVenteInput.value : ''
        };
    }

    function gatherProductData() {
        const ids = getIdNumber();
        const products = [];
        ids.forEach(id => {
            const product = extractProductValue(id);
            if (product) products.push(product);
        });
        return products;
    }

    function getExistingProductRow(productId) {
        if (!productId) return null;
        const normalized = productId.toString().trim();
        let found = null;
        document.querySelectorAll("input[name^='idProduit_']").forEach(input => {
            if (found) return;
            const val = (input.value || "").toString().trim();
            if (val && val === normalized) {
                const row = input.closest('tr');
                const designation = row?.querySelector("input[name^='designation_']")?.value?.trim();
                if (row && designation && !row.classList.contains('d-none')) found = row;
            }
        });
        return found;
    }

    function incrementProductRow(row, overrideQuantity) {
        const qteInput = row.querySelector("input[name^='qte_']");
        const puInput = row.querySelector("input[name^='pvTva_']");
        const basePriceKgInput = row.querySelector("input[name^='basePriceKg_']");
        const uniteInput = row.querySelector("input[name^='unite_']");
        if (!qteInput) return;

        const isWeightBased = uniteInput?.value === "UNT00004";
        const increment = isWeightBased ? (overrideQuantity !== null && overrideQuantity !== undefined ? overrideQuantity : 10) : 1;
        const currentQte = window.parseAmount(qteInput.value || 0);
        const newQte = currentQte + increment;
        qteInput.value = newQte;

        if (isWeightBased && basePriceKgInput && puInput) {
            const basePricePerKg = window.parseAmount(basePriceKgInput.value);
            puInput.value = (newQte * basePricePerKg) / 1000;
        }
        updateTotals();
        if (typeof window.sync === 'function') window.sync();
        row.classList.add('row-qty-updated');
        ensureProductRowVisible(row);
        setTimeout(() => row.classList.remove('row-qty-updated'), 500);
    }

    function ensureProductRowVisible(row) {
        if (!row) return false;
        const productId = row.querySelector("input[name^='idProduit_']")?.value;
        const productCode = row.dataset ? row.dataset.productCode : '';
        if (!productId && !productCode) return false;
        row.classList.remove('d-none');
        row.hidden = false;
        if (row.style && row.style.display === 'none') row.style.removeProperty('display');
        return true;
    }

    function ensureProductRowsVisible() {
        let visibleCount = 0;
        document.querySelectorAll('#ajout_multiple_ligne tr').forEach(row => {
            if (ensureProductRowVisible(row)) visibleCount++;
        });
        return visibleCount;
    }

    function removeLine(button) {
        const row = button.closest('tr');
        if (!row) return;

        if (row.id === 'ligne-multiple-0') {
            row.querySelectorAll('input').forEach(input => {
                if (input.name === 'ids' && input.type === 'checkbox') input.checked = false;
                else if (input.type !== 'checkbox' && input.type !== 'radio') input.value = '';
            });
            row.classList.add('d-none');
        } else {
            row.remove();
        }
        updateTotals();
        if (typeof window.sync === 'function') window.sync();
    }

    function removeProductRow(button) {
        removeLine(button);
        if (typeof window.refreshProductPreview === 'function') window.refreshProductPreview();
    }

    function formaterChampsPU() {
        document.querySelectorAll("#butfillejsp input[name^='pu_'], #butfillejsp input[name^='pvTva_']").forEach(input => {
            if (!input.value) return;
            const montantNumerique = window.parseAmount(input.value);
            input.value = window.formatAmount(montantNumerique, true);
        });
    }

    /**
     * 🧮 CALCUL DES TOTAUX
     */
    function updateTotals() {
        let sousTotal = 0;
        let remiseTotale = 0;

        document.querySelectorAll("#ajout_multiple_ligne tr").forEach(tr => {
            const qteInput = tr.querySelector("input[name^='qte_']");
            const puInput = tr.querySelector("input[name^='pvTva_']");
            const remiseInput = tr.querySelector("input[name^='remiseMontant_']");
            const prixInput = tr.querySelector("input[name^='montant_']");
            const uniteInput = tr.querySelector("input[name^='unite_']");
            const montantCell = tr.querySelector(".montant-row");

            const quantite = window.parseAmount(qteInput?.value || 0);
            const prixUnitaire = window.parseAmount(puInput?.value || 0);
            const isWeightBased = (uniteInput?.value === "UNT00004");

            const remiseUnitaire = tr.dataset.remiseUnitaire !== undefined
                ? window.parseAmount(tr.dataset.remiseUnitaire)
                : window.parseAmount(remiseInput?.value || 0);

            const remise = isWeightBased ? window.parseAmount(remiseInput?.value || 0) : remiseUnitaire * quantite;
            if (remiseInput) remiseInput.value = remise;

            const montantBrut = isWeightBased ? prixUnitaire : quantite * prixUnitaire;
            sousTotal += montantBrut;
            remiseTotale += remise;

            const montantNet = Math.max(montantBrut - remise, 0);

            if (prixInput) prixInput.value = montantNet.toFixed(2);
            if (montantCell) {
                if (montantCell.tagName === 'TD') window.setCellDisplay(montantCell, window.formatAmount(montantNet, true));
                else montantCell.textContent = window.formatAmount(montantNet, true);
            }
        });

        const total = Math.max(sousTotal - remiseTotale, 0);

        const montantFactureEl = document.getElementById("montantFacture");
        if (montantFactureEl) montantFactureEl.textContent = window.formatAmount(total, true);

        const sousTotalEl = document.getElementById("recapSousTotal");
        if (sousTotalEl) sousTotalEl.textContent = window.formatAmount(sousTotal, true);

        const remiseEl = document.getElementById("recapRemises");
        if (remiseEl) remiseEl.textContent = window.formatAmount(remiseTotale, true);

        const totalEl = document.getElementById("totalGeneral");
        if (totalEl) {
            const roundedTotal = window.roundToNearest100(total);
            totalEl.textContent = window.formatAmount(total, true);
            const roundedElement = document.getElementById('montantArrondiEspeces');
            if (roundedElement) roundedElement.textContent = window.formatAmount(roundedTotal, true);

            const totalInput = document.getElementById("totalGeneralInput");
            if (totalInput) totalInput.value = window.parseAmount(totalEl.textContent);

            if (typeof window.sendMessage === 'function') {
                window.sendMessage("A PAYER: " + window.formatNumberWithSpaces(roundedTotal) + " Ar");
            }
        }
        if (window.POSPayment?.refreshTotals) window.POSPayment.refreshTotals();
        else if (typeof window.calculerRetour === 'function') window.calculerRetour();
        formaterChampsPU();
    }

    function formaterActionLigne(tr) {
        if (!tr) return;
        const duplicateLink = tr.querySelector('a[onclick*="duplicateSingleRow"]');
        if (duplicateLink) {
            const tdDuplicate = duplicateLink.closest('td');
            if (tdDuplicate) tdDuplicate.remove();
        }
        const deleteLink = tr.querySelector('a[onclick*="delete_line"]');
        if (deleteLink) {
            const tdDelete = deleteLink.closest('td');
            if (tdDelete) {
                if (window.isKeyboardMode) {
                    // ➕ MODE CLAVIER : masquer le bouton visuel, suppression via Suppr/Backspace
                    tdDelete.style.display = 'none';
                } else {
                    tdDelete.style.textAlign = 'center';
                    tdDelete.style.verticalAlign = 'middle';
                    tdDelete.innerHTML = `
                        <button type="button" class="btn btn-tertiary btn-danger" onclick="removeLine(this)">
                            <i class="material-symbols-rounded">delete</i>
                        </button>
                    `;
                }
            }
        }
    }

    function nettoyerBoutonsTableau() {
        document.querySelectorAll('#ajout_multiple_ligne tr').forEach(tr => {
            formaterActionLigne(tr);
            formaterBoutonsQuantiteLigne(tr);
        });
    }

    function masquerEnTeteImageEnDouble() {
        const table = document.querySelector('#butfillejsp table');
        if (!table) return;
        const imageHeaders = [];
        table.querySelectorAll('thead th').forEach(th => {
            const text = th.textContent.trim().toLowerCase();
            if (text === 'image' || text === 'photo') imageHeaders.push(th);
        });
        if (imageHeaders.length > 1) {
            for (let i = 1; i < imageHeaders.length; i++) {
                const thDoublon = imageHeaders[i];
                const colIndex = Array.from(thDoublon.parentNode.children).indexOf(thDoublon);
                thDoublon.style.display = 'none';
                table.querySelectorAll('tbody tr').forEach(tr => {
                    if (tr.children[colIndex]) tr.children[colIndex].style.display = 'none';
                });
            }
        }
    }

    function verrouillerChampsButFille() {
        const container = document.getElementById('butfillejsp');
        if (!container) return;
        const inputs = container.querySelectorAll('input:not([type="hidden"]):not([type="checkbox"]):not([type="radio"])');
        inputs.forEach(input => {
            const isQte = input.name?.startsWith('qte_') || input.classList.contains('qte-editable-input');
            if (isQte) {
                // Codal : la quantité reste saisissable même lorsque la navigation clavier est active.
                input.readOnly = false;
                input.removeAttribute('readonly');
                input.removeAttribute('tabindex');
                input.style.pointerEvents = 'auto';
                input.style.userSelect = 'auto';
            } else {
                input.readOnly = true;
                input.setAttribute('readonly', 'readonly');
                input.tabIndex = -1;
                input.style.pointerEvents = 'none';
                input.style.userSelect = 'none';
            }
        });
    }

    /**
     * 🛒 SÉLECTION DE PRODUIT
     */
    function handleProductSelection(selectedObj, inputID, champRetour, inputNom, valeur, defaults, overrideQuantity = null) {
        if (!selectedObj || !selectedObj.value) return;

        // Après une sélection depuis la barre de recherche, la nettoyer pour la prochaine saisie
        function clearSearchBarIfNeeded() {
            if (inputID === 'idProduit_libelle') {
                const sb = document.getElementById('idProduit_libelle');
                const sh = document.getElementById('idProduit');
                if (sb) sb.value = '';
                if (sh) sh.value = '';
            }
        }

        const champsBase = champRetour.split(";").filter(c => !c.includes("="));
        const inputNames = inputNom.split(";");
        const productIdCandidate = selectedObj.value.split("::")[0].trim();
        const existingRow = getExistingProductRow(productIdCandidate);

        if (existingRow) {
            incrementProductRow(existingRow, overrideQuantity);
            if (typeof window.fermerClavierVisuel === 'function') window.fermerClavierVisuel();
            clearSearchBarIfNeeded();
            setTimeout(() => {
                if (window.isKeyboardMode && typeof window.POSKeyboard === 'object') {
                    window.POSKeyboard.focusBasket(window.POSKeyboard.getVisibleCartRows().indexOf(existingRow));
                } else if (typeof window.focusOnProduit === 'function') {
                    window.focusOnProduit();
                }
            }, 50);
            return;
        }

        let tbody = document.getElementById("ajout_multiple_ligne");
        if (!tbody) return;

        let row = null;
        let rowCount = 0;
        const line0 = document.getElementById("ligne-multiple-0");
        const desig0 = document.getElementById("designation_0");

        if (line0 && desig0 && (!desig0.value || desig0.value.trim() === "")) {
            row = line0;
            rowCount = 0;
        } else {
            let addLineButton = document.getElementById("addLines");
            if (addLineButton) {
                addLineButton.dispatchEvent(new Event("click"));
                if (typeof window.attachEvents === 'function') window.attachEvents();
                const allRows = document.querySelectorAll("#ajout_multiple_ligne tr");
                if (allRows.length > 0) {
                    row = allRows[allRows.length - 1];
                    const match = row.id ? row.id.match(/\d+$/) : null;
                    if (match) rowCount = parseInt(match[0], 10);
                }
            }
            if (!row) {
                const idsInput = getIdNumber();
                rowCount = idsInput.length > 0 ? idsInput[idsInput.length - 1] + 1 : 1;
                row = document.getElementById("ligne-multiple-" + rowCount);
            }
        }
        if (!row) return;

        row.classList.remove("d-none");
        const check = document.getElementById("checkbox" + rowCount);
        if (check) check.checked = true;

        let hiddenID = document.getElementById("idProduit_" + rowCount);
        if (!hiddenID) {
            hiddenID = document.createElement("input");
            hiddenID.type = "hidden";
            hiddenID.name = "idProduit_" + rowCount;
            hiddenID.id = "idProduit_" + rowCount;
            row.appendChild(hiddenID);
        }
        hiddenID.value = productIdCandidate;

        let hiddenEstEnGros = document.getElementById("estEnGros_" + rowCount);
        if (!hiddenEstEnGros) {
            hiddenEstEnGros = document.createElement("input");
            hiddenEstEnGros.type = "hidden";
            hiddenEstEnGros.name = "estEnGros_" + rowCount;
            hiddenEstEnGros.id = "estEnGros_" + rowCount;
            row.appendChild(hiddenEstEnGros);
        }
        hiddenEstEnGros.value = "0";

        let hiddenDevise = document.getElementById("idDevise_" + rowCount);
        if (!hiddenDevise) {
            hiddenDevise = document.createElement("input");
            hiddenDevise.type = "hidden";
            hiddenDevise.name = "idDevise_" + rowCount;
            hiddenDevise.id = "idDevise_" + rowCount;
            row.appendChild(hiddenDevise);
        }
        hiddenDevise.value = "Ar";

        let hiddenTaux = document.getElementById("tauxDeChange_" + rowCount);
        if (!hiddenTaux) {
            hiddenTaux = document.createElement("input");
            hiddenTaux.type = "hidden";
            hiddenTaux.name = "tauxDeChange_" + rowCount;
            hiddenTaux.id = "tauxDeChange_" + rowCount;
            row.appendChild(hiddenTaux);
        }
        hiddenTaux.value = "1";

        const valeurs = (selectedObj.retour || "").split(";");
        const finalColumns = champRetour.split(";").map(c => c.split("=")[0]);
        let photoVal = '', designationVal = '', qteVal = '1', puVal = '0', pvgrosVal = '', remiseVal = '0', prixVal = '0', compteVenteVal = '';
        let referenceVal = '', codegrosVal = '';

        champsBase.forEach((col, idx) => {
            if (valeurs[idx] !== undefined) {
                if (col === 'reference') referenceVal = valeurs[idx].toString();
                if (col === 'codegros') codegrosVal = valeurs[idx].toString();
                if (col === 'pvgros') pvgrosVal = valeurs[idx].toString();
                if (col === 'compte' || col === 'compte_vente') compteVenteVal = valeurs[idx].toString();
                if (col === 'remise') remiseVal = valeurs[idx].toString();
            }
        });

        const labelParts = (selectedObj.label || "").split(' - ');
        if (labelParts.length >= 4) referenceVal = labelParts[3].trim();
        if (labelParts.length >= 5) codegrosVal = labelParts[4].trim();

        const originalInput = document.getElementById(inputID) ? document.getElementById(inputID).value : "";
        const barcodeInfo = window.parseWeightBarcode ? window.parseWeightBarcode(originalInput) : { isWeightBased: false };
        let isWholesaleCode = barcodeInfo.isWeightBased ? (codegrosVal && barcodeInfo.productCode.toString() === codegrosVal) : (codegrosVal && originalInput.toString() === codegrosVal);

        if (isWholesaleCode) hiddenEstEnGros.value = "1";

        finalColumns.forEach((col, idx) => {
            const val = defaults[col] !== undefined ? defaults[col] : (champsBase.indexOf(col) >= 0 ? valeurs[champsBase.indexOf(col)] : "");
            if (inputNames[idx] === 'qte' && overrideQuantity !== null) {
                qteVal = overrideQuantity.toString();
            } else {
                if (inputNames[idx] === 'photo') photoVal = val;
                if (inputNames[idx] === 'designation') designationVal = val;
                if (inputNames[idx] === 'qte') qteVal = val || '1';
                if (inputNames[idx] === 'reference') referenceVal = val;
                if (inputNames[idx] === 'pu') {
                    const priceToUse = isWholesaleCode && pvgrosVal ? pvgrosVal : val;
                    if (overrideQuantity !== null) {
                        const uniteParts = selectedObj.retour ? selectedObj.retour.split(';') : [];
                        const uniteCode = uniteParts.length >= 7 ? uniteParts[6] : null;
                        const basePrice = window.parseAmount(priceToUse);
                        puVal = uniteCode === "UNT00004" ? ((overrideQuantity * basePrice) / 1000).toString() : basePrice.toString();
                    } else {
                        puVal = priceToUse;
                    }
                }
                if (inputNames[idx] === 'pu_pvgros') pvgrosVal = val;
                if (inputNames[idx] === 'remiseMontant') remiseVal = val || '0';
                if (inputNames[idx] === 'prix') prixVal = val || '0';
                if (inputNames[idx] === 'compte') compteVenteVal = val;
            }

            if (inputNames[idx]) {
                let hiddenInput = document.getElementById(inputNames[idx] + "_" + rowCount);
                if (!hiddenInput) {
                    hiddenInput = document.createElement("input");
                    hiddenInput.type = "hidden";
                    hiddenInput.name = inputNames[idx] + "_" + rowCount;
                    hiddenInput.id = inputNames[idx] + "_" + rowCount;
                    row.appendChild(hiddenInput);
                }
                let inputValue = val;
                if (inputNames[idx] === 'qte' && overrideQuantity !== null) inputValue = overrideQuantity.toString();
                else if (inputNames[idx] === 'pu') inputValue = puVal;
                else if (inputNames[idx] === 'reference') inputValue = referenceVal;
                else if (inputNames[idx] === 'compte') inputValue = compteVenteVal;
                hiddenInput.value = inputValue;
            }
        });

        let basePriceKgInput = document.getElementById("basePriceKg_" + rowCount);
        if (!basePriceKgInput) {
            basePriceKgInput = document.createElement("input");
            basePriceKgInput.type = "hidden";
            basePriceKgInput.name = "basePriceKg_" + rowCount;
            basePriceKgInput.id = "basePriceKg_" + rowCount;
            row.appendChild(basePriceKgInput);
        }
        basePriceKgInput.value = window.parseAmount(puVal);
        row.dataset.remiseUnitaire = (window.parseAmount(remiseVal) || 0) / (window.parseAmount(qteVal) || 1);

        const photoInput = document.getElementById("photo_" + rowCount);
        if (photoInput) {
            photoInput.type = "hidden";
            const tdimg = photoInput.closest('td');
            if (tdimg) {
                tdimg.querySelectorAll("img").forEach(i => i.remove());
                const img = document.createElement("img");
                img.className = "product-img";
                img.alt = "Produit sélectionné";
                img.src = window.resolveProductImage ? window.resolveProductImage(photoVal) : photoVal;
                tdimg.insertBefore(img, photoInput);
            }
        }

        const designationInput = row.querySelector("input[name^='designation_']");
        const designationCell = designationInput?.closest('td');
        if (designationInput) designationInput.type = 'hidden';
        if (designationCell) {
            Array.from(designationCell.childNodes).forEach(node => {
                if (node !== designationInput && node.nodeType !== 1) designationCell.removeChild(node);
                else if (node.nodeType === 1 && node !== designationInput && node.tagName !== 'INPUT') designationCell.removeChild(node);
            });
            window.setCellDisplay(designationCell, designationVal);
        }
        row.dataset.productPhoto = photoVal;
        row.dataset.productName = designationVal;
        row.dataset.productCode = productIdCandidate;
        row.dataset.productReference = referenceVal;
        row.dataset.productPrice = puVal;
        ensureProductRowVisible(row);

        // Formater les boutons + / - et boutons d'action
        formaterBoutonsQuantiteLigne(row);
        try { if (typeof formaterActionLigne === 'function') formaterActionLigne(row); } catch(err) {}

        // 🎯 APPEL À LA SERVLET DE REMISE CLIENT
        const idClient = document.getElementById("idClient")?.value || document.querySelector("[name='idClient']")?.value || "";
        checkRemiseForRow(rowCount, productIdCandidate, idClient, row);

        if (typeof window.sync === 'function') window.sync();
        updateTotals();
        if (typeof window.refreshProductPreview === 'function') window.refreshProductPreview({ photo: photoVal, name: designationVal, code: productIdCandidate, price: puVal });
        if (typeof window.fermerClavierVisuel === 'function') window.fermerClavierVisuel();

        const searchInput = document.getElementById(inputID);
        if (searchInput && typeof $ !== 'undefined' && $(searchInput).data('ui-autocomplete')) {
            try { $(searchInput).autocomplete("close"); } catch(e){}
        }

        clearSearchBarIfNeeded();

        // ➕ MODE CLAVIER : Focus automatique sur la ligne du panier ajoutée
        setTimeout(() => {
            if (window.isKeyboardMode && typeof window.POSKeyboard === 'object') {
                const visibleRows = window.POSKeyboard.getVisibleCartRows();
                if (visibleRows.length > 0) {
                    window.POSKeyboard.focusBasket(visibleRows.length - 1);
                } else if (typeof window.focusOnProduit === 'function') {
                    window.focusOnProduit();
                }
            } else if (typeof window.focusOnProduit === 'function') {
                window.focusOnProduit();
            }
        }, 50);
    }

    function autoCompleteDyn(inputID, table, classe, champRetour, valeur) {
        let input = document.getElementById(inputID);
        if (!input || typeof $ === 'undefined') return;

        // Bind unique du widget autocomplete (sans ré-initialisation à chaque frappe)
        // afin que les suggestions s'affichent dès la première saisie.
        $('#' + inputID).autocomplete({
            source: function (request, response) {
                if (typeof window.fetchAutocomplete === 'function') {
                    window.fetchAutocomplete(request, response, "null", valeur, "null", table, classe, "true", champRetour, "null");
                }
            },
            minLength: 0,
            select: function (event, ui) {
                let selectedObj = ui.item;
                let prest = selectedObj.value;
                let temp = prest.split("::");
                let compte = temp[0].trim();
                let prest2 = selectedObj.label;
                let temp2 = prest2.split("::");
                let compte2 = temp2[0].trim();

                document.getElementById(inputID).value = compte2;
                let tempinput = inputID.split("_");
                $('#' + tempinput[0]).val(compte);
                document.getElementById(tempinput[0]).dispatchEvent(new Event("change"));

                setTimeout(() => {
                    const input = document.getElementById(inputID);
                    input.value = compte2;
                    if (inputID === 'idClientlibelle') {
                        setTimeout(() => { if (typeof window.focusOnProduit === 'function') window.focusOnProduit(); }, 100);
                    }
                }, 10);
                return false;
            }
        });

        // Gestion dédiée de Entrée / Tab (validation de la sélection sans réinitialiser le widget)
        input.addEventListener('keydown', function (event) {
            if (event.key === 'Enter' || event.key === 'Tab') {
                event.preventDefault();
                let prest = input.value;
                if (prest != null && prest.trim() !== "") {
                    let temp = prest.split("::");
                    input.value = temp[0].trim();
                }
            }
        });
    }

    function autoCompleteVenteDirect(inputID, table, classe, champRetour, inputNom, valeur) {
        const champsBase = champRetour.split(";").filter(c => !c.includes("="));
        const defaults = {};
        champRetour.split(";").forEach(c => {
            if (c.includes("=")) {
                const [key, val] = c.split("=");
                defaults[key] = val;
            }
        });

        let input = document.getElementById(inputID);
        if (!input || typeof $ === 'undefined') return;

        $('#' + inputID).autocomplete({
            source: function(request, response) {
                const searchTerm = request.term;
                const barcodeInfo = window.parseWeightBarcode ? window.parseWeightBarcode(searchTerm) : { isWeightBased: false };

                if (barcodeInfo.isWeightBased) {
                    const productCode = barcodeInfo.productCode;
                    const codegrosSearchRequest = { term: productCode };
                    window.fetchAutocomplete(codegrosSearchRequest, function(codegrosResults) {
                        const codegrosMatch = codegrosResults.find(result => {
                            let codegros = '';
                            const values = (result.retour || "").split(";");
                            champsBase.forEach((col, idx) => { if (col === 'codegros' && values[idx]) codegros = values[idx].toString(); });
                            return codegros === productCode;
                        });
                        if (codegrosMatch) {
                            handleProductSelection(codegrosMatch, inputID, champRetour, inputNom, valeur, defaults, barcodeInfo.weight);
                            $('#' + inputID).autocomplete("close");
                        }
                    }, "null", valeur, "null", table, classe, "true", champsBase.join(";"), "null");
                } else {
                    window.fetchAutocomplete({ term: searchTerm }, function(results) {
                        if (results && results.length === 1) {
                            handleProductSelection(results[0], inputID, champRetour, inputNom, valeur, defaults);
                            $('#' + inputID).autocomplete("close");
                        } else {
                            response(results);
                        }
                    }, "null", valeur, "null", table, classe, "true", champsBase.join(";"), "null");
                }
            },
            minLength: 0,
            select: function(event, ui) {
                if (event) event.preventDefault();
                handleProductSelection(ui.item, inputID, champRetour, inputNom, valeur, defaults);
                return false;
            }
        });
    }

    function saveCartState() {
        const cartState = {
            products: gatherProductData(),
            client: {
                id: document.getElementById('idClient')?.value || '',
                nom: document.getElementById('idClientlibelle')?.value || ''
            },
            timestamp: Date.now()
        };
        sessionStorage.setItem('cartStateBeforeSubmit', JSON.stringify(cartState));
    }

    function restoreCartState() {
        const savedState = sessionStorage.getItem('cartStateBeforeSubmit');
        if (!savedState) return;
        try {
            const cartState = JSON.parse(savedState);
            if (Date.now() - cartState.timestamp > 300000) {
                sessionStorage.removeItem('cartStateBeforeSubmit');
                return;
            }
            if (cartState.client.id) {
                const idClientInput = document.getElementById('idClient');
                const idClientLibelleInput = document.getElementById('idClientlibelle');
                if (idClientInput) idClientInput.value = cartState.client.id;
                if (idClientLibelleInput) idClientLibelleInput.value = cartState.client.nom;
            }
            if (cartState.products && cartState.products.length > 0) {
                const tbody = document.getElementById("ajout_multiple_ligne");
                if (tbody) {
                    const template = document.getElementById('ligne-multiple-0');
                    if (template) {
                        template.querySelectorAll('input').forEach(input => {
                            if (input.name === 'ids' && input.type === 'checkbox') input.checked = false;
                            else if (input.type !== 'checkbox' && input.type !== 'radio') input.value = '';
                        });
                        template.dataset.remiseUnitaire = '';
                        template.classList.add('d-none');
                    }
                    cartState.products.forEach((product, index) => restoreProductRow(product, index));
                    updateTotals();
                    if (typeof window.sync === 'function') window.sync();
                }
            }
            sessionStorage.removeItem('cartStateBeforeSubmit');
        } catch (error) {
            sessionStorage.removeItem('cartStateBeforeSubmit');
        }
    }

    function restoreProductRow(product, index) {
        const tbody = document.getElementById('ajout_multiple_ligne');
        if (!tbody || !product) return;
        let row = null;
        let rowCount = 0;
        const line0 = document.getElementById('ligne-multiple-0');
        if (index === 0 && line0 && !document.getElementById('designation_0')?.value?.trim()) {
            row = line0;
        } else {
            const before = new Set(tbody.querySelectorAll('tr'));
            document.getElementById('addLines')?.dispatchEvent(new Event('click'));
            if (typeof window.attachEvents === 'function') window.attachEvents();
            row = Array.from(tbody.querySelectorAll('tr')).find(candidate => !before.has(candidate));
        }
        if (!row) return;
        const match = row.id?.match(/\d+$/);
        if (match) rowCount = Number(match[0]);
        row.classList.remove('d-none');
        let checkbox = row.querySelector("input[name='ids']");
        if (!checkbox) {
            checkbox = document.createElement('input');
            checkbox.type = 'hidden';
            checkbox.name = 'ids';
            row.appendChild(checkbox);
        }
        checkbox.checked = true;
        checkbox.value = String(rowCount);
        function setValue(name, value) {
            const fieldName = name + '_' + rowCount;
            let input = row.querySelector("[name='" + fieldName + "']");
            if (!input) {
                input = document.createElement('input');
                input.type = 'hidden';
                input.name = fieldName;
                input.id = fieldName;
                row.appendChild(input);
            }
            input.value = value == null ? '' : value;
            return input;
        }
        const quantity = window.parseAmount(product.quantite);
        const unitPrice = window.parseAmount(product.prixunitaire);
        const discount = window.parseAmount(product.remiseMontant);
        const amount = Math.max((product.isWeightBased ? unitPrice : quantity * unitPrice) - discount, 0);
        setValue('idProduit', product.id || '');
        const designation = setValue('designation', product.designation || '');
        setValue('reference', product.reference || '');
        const qte = setValue('qte', quantity);
        setValue('pvTva', unitPrice);
        setValue('pu', unitPrice);
        setValue('remiseMontant', discount);
        setValue('montant', amount);
        setValue('montantttc', amount);
        setValue('photo', product.urlPhoto || '');
        setValue('compte', product.compte || '');
        setValue('unite', product.isWeightBased ? 'UNT00004' : 'UNT00001');
        setValue('basePriceKg', window.parseAmount(product.originalPrice || unitPrice));
        setValue('estEnGros', 0);
        setValue('idDevise', 'Ar');
        setValue('tauxDeChange', 1);
        row.dataset.remiseUnitaire = product.isWeightBased ? discount : (quantity > 0 ? discount / quantity : discount);
        row.dataset.productPhoto = product.urlPhoto || '';
        row.dataset.productName = product.designation || '';
        row.dataset.productCode = product.id || '';
        row.dataset.productReference = product.reference || '';
        row.dataset.productPrice = String(unitPrice);
        qte.type = 'text';
        const designationCell = designation.closest('td');
        designation.type = 'hidden';
        if (designationCell) {
            Array.from(designationCell.childNodes).forEach(node => {
                if (node !== designation && node.nodeType !== 1) designationCell.removeChild(node);
                else if (node.nodeType === 1 && node !== designation && node.tagName !== 'INPUT') designationCell.removeChild(node);
            });
            window.setCellDisplay(designationCell, product.designation || '');
        }
        const photoInput = row.querySelector("[name='photo_" + rowCount + "']");
        const photoCell = photoInput?.closest('td');
        if (photoCell) {
            photoCell.querySelectorAll('img').forEach(image => image.remove());
            const image = document.createElement('img');
            image.className = 'product-img';
            image.alt = 'Produit';
            image.src = window.resolveProductImage(product.urlPhoto);
            photoCell.insertBefore(image, photoInput);
        }
        const amountInput = row.querySelector("[name='montant_" + rowCount + "']");
        const amountCell = amountInput?.closest('td');
        if (amountInput) amountInput.type = 'hidden';
        if (amountCell) {
            amountCell.classList.add('montant-row');
            window.setCellDisplay(amountCell, window.formatAmount(amount, true));
        }
        formaterBoutonsQuantiteLigne(row);
        formaterActionLigne(row);
        rememberValidQuantity(qte);
        ensureProductRowVisible(row);
    }

    function updateProductPreview(data) {
        const box = document.getElementById('selectedProductBox');
        if (!box) return;
        box.classList.remove('d-none');
        const img = document.getElementById('selectedProductImage');
        if (img) img.src = window.resolveProductImage ? window.resolveProductImage(data.photo) : data.photo;
        const nameEl = document.getElementById('selectedProductName');
        if (nameEl) nameEl.textContent = data.name || 'Aucun produit';
    }

    function hideProductPreview() {
        const box = document.getElementById('selectedProductBox');
        if (box) box.classList.add('d-none');
    }

    function refreshProductPreview(previewData) {
        if (previewData && previewData.name) {
            updateProductPreview(previewData);
            return;
        }
        const rows = document.querySelectorAll('#ajout_multiple_ligne tr');
        if (!rows.length) hideProductPreview();
    }

    function adjustModalQuantity(delta) {
        const modalQuantite = document.getElementById("modalQuantite");
        if (!modalQuantite) return;
        const currentValue = window.parseAmount(modalQuantite.value || 0);
        const updated = Math.max(currentValue + delta, 0);
        modalQuantite.value = Number.isInteger(updated) ? updated : updated.toFixed(2);
    }

    // Export global strict (Zéro régression)
    window.resolveProductImage = resolveProductImage;
    window.checkRemiseForRow = checkRemiseForRow;
    window.recheckRemiseAllRows = recheckRemiseAllRows;
    window.formaterBoutonsQuantiteLigne = formaterBoutonsQuantiteLigne;
    window.formaterTousBoutonsQuantite = formaterTousBoutonsQuantite;
    window.ensureQuantityRemovalModal = ensureQuantityRemovalModal;
    window.requestQuantityRemoval = requestQuantityRemoval;
    window.validateBasketQuantities = validateBasketQuantities;
    window.cancelQuantityRemoval = cancelQuantityRemoval;
    window.confirmQuantityRemoval = confirmQuantityRemoval;
    window.getIdNumber = getIdNumber;
    window.extractProductValue = extractProductValue;
    window.gatherProductData = gatherProductData;
    window.getExistingProductRow = getExistingProductRow;
    window.incrementProductRow = incrementProductRow;
    window.ensureProductRowVisible = ensureProductRowVisible;
    window.ensureProductRowsVisible = ensureProductRowsVisible;
    window.removeLine = removeLine;
    window.removeProductRow = removeProductRow;
    window.formaterChampsPU = formaterChampsPU;
    window.updateTotals = updateTotals;
    window.formaterActionLigne = formaterActionLigne;
    window.nettoyerBoutonsTableau = nettoyerBoutonsTableau;
    window.masquerEnTeteImageEnDouble = masquerEnTeteImageEnDouble;
    window.verrouillerChampsButFille = verrouillerChampsButFille;
    window.handleProductSelection = handleProductSelection;
    window.autoCompleteDyn = autoCompleteDyn;
    window.autoCompleteVenteDirect = autoCompleteVenteDirect;
    window.saveCartState = saveCartState;
    window.restoreCartState = restoreCartState;
    window.restoreProductRow = restoreProductRow;
    window.updateProductPreview = updateProductPreview;
    window.hideProductPreview = hideProductPreview;
    window.refreshProductPreview = refreshProductPreview;
    window.adjustModalQuantity = adjustModalQuantity;
})(window);
