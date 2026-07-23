/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * @deprecated As of Athanasius (7.3.x), with no direct replacement
 */
(function () {
	const ALLOY = YUI();

	if (ALLOY.html5shiv) {
		ALLOY.html5shiv();
	}

	const applyCSPNonce = function (Y) {
		const nonce =
			(window.Liferay && Liferay.CSP && Liferay.CSP.nonce) || '';

		// Remote scripts recreated by Y.Get. Adding the nonce to the default
		// node attributes stamps it on every script and link Y.Get inserts.

		if (nonce && Y.Get && Y.Get.options && Y.Get.options.attributes) {
			Y.Get.options.attributes.nonce = nonce;
		}

		// Inline scripts recreated by ParseContent.globalEval.

		const ParseContent = Y.Plugin && Y.Plugin.ParseContent;

		if (!ParseContent || ParseContent.prototype._cspNonceApplied) {
			return;
		}

		ParseContent.prototype.globalEval = function (data) {
			if (typeof data === 'string') {
				data = {
					text: data,
					type: 'text/javascript',
				};
			}

			const doc = Y.getDoc();
			const head = doc.one('head') || doc.get('documentElement');

			const newScript = Y.config.doc.createElement('script');

			newScript.type = data.type;

			if (window.Liferay && Liferay.CSP && Liferay.CSP.nonce) {
				newScript.setAttribute('nonce', Liferay.CSP.nonce);
			}

			if (data.text) {
				newScript.text = Y.Lang.trim(data.text);
			}

			// Removes the script node immediately after executing it

			head.appendChild(newScript).remove();
		};

		ParseContent.prototype._cspNonceApplied = true;
	};

	const originalUse = ALLOY.use;

	ALLOY.use = function () {
		const args = Array.prototype.slice.call(arguments, 0);

		const currentURL = Liferay.currentURL;

		const originalCallback = args[args.length - 1];

		if (typeof originalCallback === 'function') {
			args[args.length - 1] = function (Y) {
				applyCSPNonce(Y);

				if (Liferay.currentURL === currentURL) {
					originalCallback.apply(this, arguments);
				}
			};
		}

		return originalUse.apply(this, args);
	};

	window.AUI = function () {
		return ALLOY;
	};

	ALLOY.mix(AUI, YUI);

	AUI.$ = window.jQuery;
	AUI._ = window._;
})();
