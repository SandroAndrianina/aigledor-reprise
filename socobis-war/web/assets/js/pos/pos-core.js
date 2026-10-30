/**
 * POS APJ - Core Utilities (pos-core.js)
 * Exhaustivité : 9 fonctions fondamentales
 */
(function (window) {
    'use strict';

    const amountFormatter = new Intl.NumberFormat('fr-FR', {
        minimumFractionDigits: 0,
        maximumFractionDigits: 2
    });

    function parseAmount(value) {
        if (value === null || value === undefined) return 0;
        const normalized = value.toString()
            .replace(/Ar/gi, '')
            .replace(/[\s\u00A0]/g, '')
            .replace(/,/g, '.');
        const parsed = Number(normalized);
        return Number.isFinite(parsed) ? parsed : 0;
    }

    function formatAmount(value, withCurrency) {
        const formatted = amountFormatter.format(Number(value) || 0);
        return withCurrency ? formatted + ' Ar' : formatted;
    }

    function roundToNearest100(number) {
        return Math.round((Number(number) || 0) / 100) * 100;
    }

    function formatNumberWithSpaces(number) {
        return (number || 0).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
    }

    function parseWeightBarcode(barcode) {
        if (!barcode) return { isWeightBased: false, productCode: null, weight: null };
        const cleaned = barcode.toString().trim();
        const validWeightPrefixes = ["20", "21", "22"];
        const prefix = cleaned.substring(0, 2);

        if (cleaned.length !== 13 || !/^\d{13}$/.test(cleaned) || !validWeightPrefixes.includes(prefix)) {
            return { isWeightBased: false, productCode: null, weight: null };
        }

        const productCode = cleaned.substring(4, 7);
        const weightStr = cleaned.substring(7, 12);
        const weight = parseInt(weightStr, 10);

        return {
            isWeightBased: true,
            productCode: productCode,
            weight: isNaN(weight) ? 0 : weight
        };
    }

    async function getProductByBarcode(barcode) {
        const barcodeInfo = parseWeightBarcode(barcode);
        const contextPath = window.contextPath || '';
        try {
            const response = await fetch(`${contextPath}/ApresTarif?acte=pos&reference=${barcodeInfo.productCode}`);
            const data = await response.json();
            if (data.productPos) {
                return { product: data.productPos, barcodeInfo: barcodeInfo, isWeightUnit: data.productPos.unite === "UNT00004" };
            }
            return { product: null, error: "Produit non trouvé" };
        } catch (error) {
            return { product: null, error: error.message };
        }
    }

    function resolveProductImage(photo) {
        const contextPath = window.contextPath || '';
        const fallback = contextPath + '/assets/img/products/no_image.png';
        if (!photo) return fallback;
        if (/^https?:\/\//i.test(photo)) return photo;
        return contextPath + '/assets/img/produit/' + photo;
    }

    function setCellDisplay(cell, text) {
        if (!cell) return;
        Array.from(cell.childNodes).forEach(node => {
            if (node.nodeType === 3 && node.textContent.trim() !== '') cell.removeChild(node);
        });
        const hiddenInputs = cell.querySelectorAll("input[type='hidden']");
        let span = cell.querySelector('.cell-value');
        if (!span) {
            span = document.createElement('span');
            span.className = 'cell-value';
            if (hiddenInputs.length > 0) cell.insertBefore(span, hiddenInputs[0]);
            else cell.appendChild(span);
        }
        span.textContent = text;
    }

    function getCellDisplay(cell) {
        if (!cell) return '';
        const span = cell.querySelector('.cell-value');
        return span ? span.textContent.trim() : cell.textContent.trim();
    }

    // Export global strict (Zéro régression)
    window.parseAmount = parseAmount;
    window.formatAmount = formatAmount;
    window.roundToNearest100 = roundToNearest100;
    window.formatNumberWithSpaces = formatNumberWithSpaces;
    window.parseWeightBarcode = parseWeightBarcode;
    window.getProductByBarcode = getProductByBarcode;
    window.resolveProductImage = resolveProductImage;
    window.setCellDisplay = setCellDisplay;
    window.getCellDisplay = getCellDisplay;
})(window);