/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Rewrites the nonce of every inline `<script>`/`<style>` in the given markup
 * to the nonce bound to the current document (`Liferay.CSP.nonce`).
 *
 * Markup fetched over AJAX carries the nonce of its own response. When
 * per-request nonces are in effect (for example when SPA is disabled) that
 * nonce does not match the one bound to the current document, so injecting the
 * markup (for example through `Range.createContextualFragment`, which evaluates
 * scripts and applies styles) is blocked by the Content Security Policy.
 * Rewriting the nonces to the document nonce keeps the injected content valid.
 *
 * @param {string} html The markup to rewrite.
 * @return {string} The markup with its inline script/style nonces rewritten.
 */
export default function setCSPNonce(html) {
	if (
		typeof html !== 'string' ||
		!window.Liferay ||
		!Liferay.CSP ||
		!Liferay.CSP.nonce
	) {
		return html;
	}

	return html.replace(
		/(<(?:script|style)\b[^>]*\bnonce=")[^"]*(")/gi,
		`$1${Liferay.CSP.nonce}$2`
	);
}
