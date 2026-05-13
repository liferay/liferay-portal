/**
 * Google Tag Manager bootstrap for liferay.com.
 *
 * Replaces the OSGi-based GTMTopHeadJSPDynamicInclude from
 * lfris-www/liferay/modules/extensions/gtm. The Java implementation:
 *   1) read GTMConfiguration (gtm.id + site.gtm.ids map),
 *   2) skip control-panel and system layouts (except asset display),
 *   3) inject the standard GTM <script> snippet into <head>.
 *
 * This client-extension equivalent runs in the browser at page load:
 * it fetches config.json (a {default, sites:{siteId:gtmIdSuffix}} map),
 * applies the same skip rules using Liferay.ThemeDisplay, and injects
 * the same GTM container script.
 */
(function () {
	'use strict';

	if (window.__lfrDotcomGtmLoaded) {
		return;
	}
	window.__lfrDotcomGtmLoaded = true;

	var CONFIG_URL = '/o/liferay-dotcom-gtm-global-js/config.json';

	fetch(CONFIG_URL, {credentials: 'same-origin'})
		.then(function (response) {
			return response.ok ? response.json() : null;
		})
		.then(function (config) {
			if (!config) {
				return;
			}

			var themeDisplay = window.Liferay && window.Liferay.ThemeDisplay;

			if (!themeDisplay) {
				injectGtm(config.default);

				return;
			}

			if (shouldSkipLayout(themeDisplay)) {
				return;
			}

			var siteGroupId = String(themeDisplay.getSiteGroupId && themeDisplay.getSiteGroupId());

			var idSuffix = (config.sites && config.sites[siteGroupId]) || config.default;

			injectGtm(idSuffix);
		})
		.catch(function () {
			// Silent: if the config can't be loaded, do nothing rather than
			// inject the wrong GTM container.
		});

	/**
	 * Mirrors the server-side filter:
	 *   layout.isTypeControlPanel() || (layout.isSystem() && layout.type != TYPE_ASSET_DISPLAY)
	 */
	function shouldSkipLayout(themeDisplay) {
		var layout = typeof themeDisplay.getLayout === 'function' ? themeDisplay.getLayout() : null;

		if (!layout) {
			return false;
		}

		var isControlPanel =
			typeof layout.isTypeControlPanel === 'function' && layout.isTypeControlPanel();

		if (isControlPanel) {
			return true;
		}

		var isSystem = typeof layout.isSystem === 'function' && layout.isSystem();

		if (!isSystem) {
			return false;
		}

		var type = typeof layout.getType === 'function' ? layout.getType() : null;

		return type !== 'asset_display';
	}

	function injectGtm(idSuffix) {
		if (!idSuffix || !/^[A-Z0-9]+$/.test(idSuffix)) {
			return;
		}

		var containerId = 'GTM-' + idSuffix;

		window.dataLayer = window.dataLayer || [];
		window.dataLayer.push({'gtm.start': new Date().getTime(), event: 'gtm.js'});

		var script = document.createElement('script');

		script.async = true;
		script.src = 'https://www.googletagmanager.com/gtm.js?id=' + encodeURIComponent(containerId);

		var firstScript = document.getElementsByTagName('script')[0];

		if (firstScript && firstScript.parentNode) {
			firstScript.parentNode.insertBefore(script, firstScript);
		}
		else {
			(document.head || document.documentElement).appendChild(script);
		}
	}
})();
