/**
 * Paiement POS : total exact, espèces arrondies et solde automatique.
 * Porté de Codal sans traitement de consignation.
 */
(function(window) {
    'use strict';

    const MODES = {
        especes: 'valeurEspece',
        mvola: 'montantMvola',
        orange: 'montantOrange',
        airtel: 'montantAirtel',
        cheque: 'montantCheque',
        visa: 'montantVisa',
        bonAchat: 'montantBOA',
        cadeaux: 'montantCarte'
    };
    const REFERENCES = {
        mvola: 'refMvola',
        orange: 'refOrange',
        airtel: 'refAirtel',
        cheque: 'refCheque',
        visa: 'refVisa',
        bonAchat: 'refBOA',
        cadeaux: 'refCarte'
    };
    const selectionOrder = [];
    let initialized = false;

    const rules = {
        requiredAmount: function(exactTotal, roundedCashTotal, activeModes) {
            return activeModes.length === 1 && activeModes[0] === 'especes'
                ? roundedCashTotal
                : exactTotal;
        },
        remainingAmount: function(exactTotal, otherPayments) {
            return Math.max(exactTotal - otherPayments, 0);
        },
        balanceMode: function(activeModes, orderedModes) {
            for (let index = orderedModes.length - 1; index >= 0; index--) {
                const mode = orderedModes[index];
                if (mode !== 'especes' && activeModes.includes(mode)) return mode;
            }
            for (let index = activeModes.length - 1; index >= 0; index--) {
                if (activeModes[index] !== 'especes') return activeModes[index];
            }
            return '';
        },
        cashChange: function(exactTotal, roundedCashTotal, activeModes, amounts) {
            if (!activeModes.includes('especes')) return 0;
            const cashPaid = amounts.especes || 0;
            if (activeModes.length === 1) {
                return Math.max(cashPaid - roundedCashTotal, 0);
            }
            const nonCashPaid = activeModes.reduce(function(sum, mode) {
                return mode === 'especes' ? sum : sum + (amounts[mode] || 0);
            }, 0);
            const cashRequired = Math.max(exactTotal - nonCashPaid, 0);
            return Math.max(cashPaid - cashRequired, 0);
        }
    };

    function getExactTotal() {
        const element = document.getElementById('totalGeneral');
        return element ? parseAmount(element.textContent) : 0;
    }

    function getRoundedCashTotal() {
        const element = document.getElementById('montantArrondiEspeces');
        if (element) return parseAmount(element.textContent);
        return Math.round(getExactTotal() / 100) * 100;
    }

    function getActiveModes() {
        return Array.from(
            document.querySelectorAll('.toggle-container .toggle-button.active input[name="payment-method"]')
        ).map(function(input) { return input.value; });
    }

    function isEffectiveMultiple() {
        const checkbox = document.getElementById('multiple-payment');
        return !!(checkbox && checkbox.checked && getActiveModes().length > 1);
    }

    function getSingleMode() {
        const modes = getActiveModes();
        return modes.length === 1 ? modes[0] : '';
    }

    function readAmountById(inputId) {
        const input = document.getElementById(inputId);
        if (!input || !input.value) return 0;
        const raw = input.value.replace(/\D/g, '');
        return raw ? parseInt(raw, 10) : 0;
    }

    function readModeAmount(mode) {
        return MODES[mode] ? readAmountById(MODES[mode]) : 0;
    }

    function usesImplicitSingleCash(activeModes) {
        return activeModes.length === 1
            && activeModes[0] === 'especes'
            && readModeAmount('especes') <= 0;
    }

    function getEffectiveModeAmount(mode, activeModes) {
        if (mode === 'especes' && usesImplicitSingleCash(activeModes)) {
            return getRoundedCashTotal();
        }
        return readModeAmount(mode);
    }

    function formatInputAmount(amount) {
        return amount > 0 ? Math.round(amount).toLocaleString('fr-FR') + ' Ar' : '';
    }

    function writeAmount(mode, amount, automatic) {
        const input = document.getElementById(MODES[mode]);
        if (!input) return;
        input.value = formatInputAmount(amount);
        if (automatic) input.dataset.paymentAuto = 'true';
        else delete input.dataset.paymentAuto;
    }

    function getAmounts(activeModes) {
        const amounts = {};
        activeModes.forEach(function(mode) {
            amounts[mode] = getEffectiveModeAmount(mode, activeModes);
        });
        return amounts;
    }

    function getPaidAmount(activeModes) {
        return activeModes.reduce(function(sum, mode) {
            return sum + getEffectiveModeAmount(mode, activeModes);
        }, 0);
    }

    function hideAmountError() {
        const error = document.getElementById('amount-error');
        if (error) error.style.display = 'none';
    }

    function showAmountError(input, message) {
        if (!input) return;
        let error = document.getElementById('amount-error');
        const parent = input.closest('.form-input') || input.parentNode;
        if (!error) {
            error = document.createElement('div');
            error.id = 'amount-error';
            error.className = 'error-message';
            error.style.cssText = 'color:red;margin-top:5px;font-size:14px;font-weight:bold;' +
                'background:#ffe6e6;padding:5px;border:1px solid #ff0000;border-radius:4px;';
        }
        if (error.parentNode !== parent) parent.appendChild(error);
        error.textContent = message;
        error.style.display = 'block';
    }

    function setPaymentButtonState(valid) {
        const button = document.querySelector('#venteForm button[type="submit"].btn-primary.btn-large');
        if (!button) return;
        button.disabled = !valid;
        button.style.opacity = valid ? '1' : '0.6';
        button.style.cursor = valid ? 'pointer' : 'not-allowed';
    }

    function syncSubmissionState(activeModes) {
        const effectiveMultiple = isEffectiveMultiple();
        const hidden = document.getElementById('multiplePaymentInput');
        if (hidden) hidden.value = effectiveMultiple ? 'true' : 'false';

        if (activeModes.length === 1) {
            document.querySelectorAll('input[name="payment-method"]').forEach(function(radio) {
                radio.checked = radio.value === activeModes[0];
            });

            const referenceId = REFERENCES[activeModes[0]];
            let referenceHidden = document.getElementById('paymentRefInput');
            if (!referenceHidden) {
                referenceHidden = document.createElement('input');
                referenceHidden.type = 'hidden';
                referenceHidden.name = 'referencePaiement';
                referenceHidden.id = 'paymentRefInput';
                const form = document.getElementById('venteForm');
                if (form) form.appendChild(referenceHidden);
            }
            referenceHidden.value = referenceId
                ? (document.getElementById(referenceId)?.value || '')
                : '';
        }
    }

    function clearInactiveAmounts(activeModes) {
        Object.keys(MODES).forEach(function(mode) {
            if (activeModes.includes(mode)) return;
            writeAmount(mode, 0, false);
        });
    }

    function applySingleDefault(activeModes, force) {
        if (activeModes.length !== 1 || !force) return;
        const mode = activeModes[0];
        if (mode === 'especes') return;
        const input = document.getElementById(MODES[mode]);
        if (!input) return;
        const target = rules.requiredAmount(getExactTotal(), getRoundedCashTotal(), activeModes);
        const current = readModeAmount(mode);
        if (current <= 0) {
            writeAmount(mode, target, true);
        }
    }

    function updateBalanceInputState(activeModes) {
        const balanceMode = activeModes.length > 1
            ? rules.balanceMode(activeModes, selectionOrder)
            : '';
        Object.keys(MODES).forEach(function(mode) {
            const input = document.getElementById(MODES[mode]);
            if (input) input.readOnly = mode === balanceMode;
        });
        return balanceMode;
    }

    function rebalance(activeModes) {
        if (activeModes.length < 2) return;
        const balanceMode = updateBalanceInputState(activeModes);
        if (!balanceMode) return;

        const otherPayments = activeModes.reduce(function(sum, mode) {
            return mode === balanceMode ? sum : sum + readModeAmount(mode);
        }, 0);
        writeAmount(
            balanceMode,
            rules.remainingAmount(getExactTotal(), otherPayments),
            true
        );
    }

    function updateRecapDisplay(activeModes) {
        const exactTotal = getExactTotal();
        const roundedCashTotal = getRoundedCashTotal();
        const paid = getPaidAmount(activeModes);
        const required = rules.requiredAmount(exactTotal, roundedCashTotal, activeModes);
        const change = rules.cashChange(exactTotal, roundedCashTotal, activeModes, getAmounts(activeModes));
        const paidElement = document.getElementById('montantPayerTexte');
        const changeElement = document.getElementById('montantRetourner');
        const changeInput = document.getElementById('aretourner');

        if (paidElement) paidElement.textContent = formatAmount(paid, true);
        if (changeInput) changeInput.value = change;

        if (changeElement) {
            if (paid < required) {
                changeElement.textContent = 'Montant payé inférieur';
                changeElement.className = 'Body14pxRegular';
            } else {
                changeElement.textContent = formatAmount(change, true);
                changeElement.className = 'h520pxSemibold';
            }
        }

        if (typeof sendMessage === 'function') {
            sendMessage(
                'PAYE: ' + formatNumberWithSpaces(paid) + ' Ar||' +
                'MONNAIE: ' + formatNumberWithSpaces(change) + ' Ar'
            );
        }
        return change;
    }

    function getFirstInvalid(activeModes) {
        if (!activeModes.length) return null;
        for (let index = 0; index < activeModes.length; index++) {
            const mode = activeModes[index];
            const amountInput = document.getElementById(MODES[mode]);
            if (readModeAmount(mode) <= 0 && !(mode === 'especes' && usesImplicitSingleCash(activeModes))) {
                return amountInput;
            }
            if (mode !== 'especes') {
                const referenceInput = document.getElementById(REFERENCES[mode]);
                if (!referenceInput || !referenceInput.value.trim()) return referenceInput || amountInput;
            }
        }

        const required = rules.requiredAmount(getExactTotal(), getRoundedCashTotal(), activeModes);
        if (getPaidAmount(activeModes) < required) {
            return document.getElementById(MODES[activeModes[activeModes.length - 1]]);
        }
        return null;
    }

    function validate(showError) {
        const activeModes = getActiveModes();
        const invalidInput = getFirstInvalid(activeModes);
        const valid = !!activeModes.length && !invalidInput;
        setPaymentButtonState(valid);

        if (valid) {
            hideAmountError();
        } else if (showError && invalidInput) {
            const required = rules.requiredAmount(getExactTotal(), getRoundedCashTotal(), activeModes);
            showAmountError(
                invalidInput,
                'Montant minimum requis : ' + formatAmount(required, true)
            );
        }
        return valid;
    }

    function applyState(options) {
        const settings = options || {};
        const activeModes = getActiveModes();
        clearInactiveAmounts(activeModes);
        syncSubmissionState(activeModes);
        updateBalanceInputState(activeModes);

        if (activeModes.length === 1) {
            applySingleDefault(activeModes, !!settings.forceSingle);
        } else if (settings.rebalance !== false) {
            rebalance(activeModes);
        }

        updateRecapDisplay(activeModes);
        validate(false);
    }

    function handleAmountInput(event) {
        const input = event.currentTarget;
        delete input.dataset.paymentAuto;
        const raw = input.value.replace(/\D/g, '');
        input.value = raw ? formatInputAmount(parseInt(raw, 10)) : '';

        applyState({ sourceInputId: input.id, rebalance: true });
        try {
            const position = input.value.length - (input.value ? 3 : 0);
            input.setSelectionRange(position, position);
        } catch (ignored) {}
    }

    function handleModeChange(mode, active) {
        const previousIndex = selectionOrder.indexOf(mode);
        if (previousIndex >= 0) selectionOrder.splice(previousIndex, 1);
        if (active) selectionOrder.push(mode);
        applyState({ forceSingle: getActiveModes().length === 1, rebalance: true });
    }

    function handleMultipleChange() {
        applyState({ forceSingle: getActiveModes().length === 1, rebalance: true });
    }

    function refreshTotals() {
        applyState({ forceSingle: false, rebalance: true });
    }

    function prepareForSubmit() {
        const activeModes = getActiveModes();
        if (activeModes.length === 1) applySingleDefault(activeModes, false);
        else rebalance(activeModes);
        syncSubmissionState(activeModes);
        updateRecapDisplay(activeModes);
        return validate(true);
    }

    function applyImplicitCashForSubmission(activeModes) {
        if (!usesImplicitSingleCash(activeModes)) return function() {};
        const input = document.getElementById(MODES.especes);
        if (!input) return function() {};

        const previousValue = input.value;
        input.value = String(getRoundedCashTotal());
        return function() {
            if (document.contains(input)) input.value = previousValue;
        };
    }

    function resetRejectedSubmission() {
        if (typeof hideLoader === 'function') hideLoader();
        const button = document.querySelector('#venteForm button[type="submit"].btn-primary.btn-large');
        if (!button) return;
        validate(false);
        button.style.pointerEvents = 'auto';
        button.style.backgroundColor = '';
        button.textContent = 'Payer';
    }

    function init() {
        if (initialized) return;
        initialized = true;
        getActiveModes().forEach(function(mode) { selectionOrder.push(mode); });

        Object.keys(MODES).forEach(function(mode) {
            const input = document.getElementById(MODES[mode]);
            if (!input || input.dataset.paymentEngine) return;
            input.dataset.paymentEngine = 'true';
            input.addEventListener('input', handleAmountInput);
            input.addEventListener('blur', function() { validate(false); });
        });

        Object.keys(REFERENCES).forEach(function(mode) {
            const input = document.getElementById(REFERENCES[mode]);
            if (!input || input.dataset.paymentEngine) return;
            input.dataset.paymentEngine = 'true';
            input.addEventListener('input', function() {
                syncSubmissionState(getActiveModes());
                validate(false);
            });
        });

        const form = document.getElementById('venteForm');
        if (form && !form.dataset.paymentEngine) {
            form.dataset.paymentEngine = 'true';
            form.addEventListener('submit', function(event) {
                if (typeof window.validateBasketQuantities === 'function' && !window.validateBasketQuantities()) {
                    event.preventDefault();
                    event.stopImmediatePropagation();
                    return;
                }
                if (prepareForSubmit()) {
                    const restoreImplicitCash = applyImplicitCashForSubmission(getActiveModes());
                    window.setTimeout(restoreImplicitCash, 0);
                    return;
                }
                event.preventDefault();
                event.stopImmediatePropagation();
                resetRejectedSubmission();
                const activeModes = getActiveModes();
                const required = rules.requiredAmount(getExactTotal(), getRoundedCashTotal(), activeModes);
                alert('Paiement incomplet. Montant minimum requis : ' + formatAmount(required, true));
                const invalidInput = getFirstInvalid(activeModes);
                if (invalidInput) invalidInput.focus();
            }, true);
        }

        applyState({ forceSingle: true, rebalance: true });
    }

    function setPaymentMode(mode) {
        const form = document.getElementById('venteForm');
        if (!form) return;
        let hidden = document.getElementById('paymentModeInput');
        if (!hidden) {
            hidden = document.createElement('input');
            hidden.type = 'hidden';
            hidden.name = 'modePaiement';
            hidden.id = 'paymentModeInput';
            form.appendChild(hidden);
        }
        hidden.value = mode;
    }

    function setPaymentWithRef(mode, refInputId) {
        const form = document.getElementById('venteForm');
        if (!form) return;
        setPaymentMode(mode);
        let hidden = document.getElementById('paymentRefInput');
        if (!hidden) {
            hidden = document.createElement('input');
            hidden.type = 'hidden';
            hidden.name = 'referencePaiement';
            hidden.id = 'paymentRefInput';
            form.appendChild(hidden);
        }
        hidden.value = document.getElementById(refInputId)?.value || '';
        if (form.requestSubmit) form.requestSubmit();
        else form.querySelector('button[type="submit"]')?.click();
    }

    window.POSPayment = {
        rules: rules,
        init: init,
        refreshTotals: refreshTotals,
        handleModeChange: handleModeChange,
        handleMultipleChange: handleMultipleChange,
        prepareForSubmit: prepareForSubmit,
        getExactTotal: getExactTotal,
        getRoundedCashTotal: getRoundedCashTotal,
        getActiveModes: getActiveModes,
        getPaidAmount: getPaidAmount
    };

    window.MODES = MODES;
    window.getPaymentInputId = function(mode) { return MODES[mode] || ''; };
    window.getMontantInputId = window.getPaymentInputId;
    window.getRefInputId = function(mode) { return REFERENCES[mode] || ''; };
    window.getModesActifs = getActiveModes;
    window.isMultiple = isEffectiveMultiple;
    window.getSingleMode = getSingleMode;
    window.getRawMontant = readAmountById;
    window.setMontant = function(inputId, amount) {
        const mode = Object.keys(MODES).find(function(key) { return MODES[key] === inputId; });
        if (mode) writeAmount(mode, amount, false);
    };
    window.getSomme = function(excludeId) {
        return getActiveModes().reduce(function(sum, mode) {
            return MODES[mode] === excludeId ? sum : sum + readModeAmount(mode);
        }, 0);
    };
    window.hideAmountError = hideAmountError;
    window.showAmountError = showAmountError;
    window.checkSingleModeAmount = function() {
        return getActiveModes().length === 1 && validate(false);
    };
    window.checkPaymentValidation = function() { return validate(false); };
    window.controler = function() { return validate(false); };
    window.calculerRetour = function() { return updateRecapDisplay(getActiveModes()); };
    window.updateRecap = function() { return updateRecapDisplay(getActiveModes()); };
    window.pasteMontantPayer = function() { return updateRecapDisplay(getActiveModes()); };
    window.pasteMontantPayerMultiple = window.pasteMontantPayer;
    window.attachSubmitGuard = function() {};
    window.setPaymentMode = setPaymentMode;
    window.setPaymentWithRef = setPaymentWithRef;
    window.showError = showAmountError;
    window.hideError = hideAmountError;
    window.hideAllErrors = hideAmountError;

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})(window);
