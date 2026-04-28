/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {COOKIE_TYPES, getCookie} from 'frontend-js-web';

import {declineAllCookies} from './CookiesUtil';

export function flipThirdPartyCookiesOff(element) {
	const elements = element.querySelectorAll(
		'[data-third-party-cookie-flipped]'
	);

	elements.forEach((item) => {
		item.removeAttribute('data-third-party-cookie-flipped');

		switch (item.tagName) {
			case 'SCRIPT': {
				item.type = 'text/plain';
				break;
			}
			case 'LINK': {
				item.dataset['href'] = item.href;
				item.removeAttribute('href');
				break;
			}
			case 'EMBED':
			case 'IFRAME':
			case 'IMG': {
				item.dataset['src'] = item.src;
				item.removeAttribute('src');
				break;
			}
			default:
		}
	});

	return element;
}

// We want to ignore these containers because our html-editor and ckeditor are
// changing their content based on the rendered output after we flip those
// elements on and enable the cookies.

const IGNORED_CONTAINER_SELECTORS = [
	'.page-editor__html-editor-modal__preview-columns',
	'.cke_editable',
].join(',');

/**
 * Enables the html elements that contain the third party cookie.
 *
 * @param {string} type - The type of cookie to check against.
 */
function flipThirdPartyCookie(type) {
	const selector = type
		? `[data-third-party-cookie="${type}"]`
		: '[data-third-party-cookie]';

	function flipItems(documentElement) {
		documentElement.querySelectorAll(selector).forEach((element) => {
			if (element.closest(IGNORED_CONTAINER_SELECTORS)) {
				return;
			}

			const flipped = element.getAttribute(
				'data-third-party-cookie-flipped'
			);

			if (flipped) {
				return;
			}

			element.setAttribute('data-third-party-cookie-flipped', true);

			switch (element.tagName) {
				case 'SCRIPT': {
					const newScript = element.cloneNode(true);

					newScript.type = 'text/javascript';

					element.replaceWith(newScript);
					break;
				}
				case 'LINK': {
					const dataHref = element.dataset['href'];

					if (dataHref) {
						const newLink = element.cloneNode();

						newLink.href = dataHref;
						newLink.removeAttribute('data-href');

						element.replaceWith(newLink);
					}

					break;
				}
				case 'EMBED':
				case 'IFRAME':
				case 'IMG': {
					const dataSrc = element.dataset['src'];

					if (dataSrc) {
						element.src = dataSrc;
						element.removeAttribute('data-src');
					}

					break;
				}
				default:

					// eslint-disable-next-line no-console
					console.warn(
						'3rd Party Cookies: ',
						`Unable to enable ${element.tagName}. See element: `,
						element
					);
			}
		});
	}

	flipItems(document);

	document.querySelectorAll('iframe').forEach((item) => {
		if (item && item.contentDocument) {
			flipItems(item.contentDocument);
		}
	});
}

/**
 * Checks the consent for each cookie type and if consent is given, we flip the
 * scripts that contain the cookies.
 */
export default function toggleThirdPartyCookies() {
	Object.values(COOKIE_TYPES).forEach((type) => {
		const typeConsent = getCookie(type) === 'true';

		if (typeConsent) {
			flipThirdPartyCookie(type);
		}
	});
}

export function suppressThirdPartyCookies(
	consentRenewalPeriod,
	consentRenewalPeriodTimeUnit,
	dissentRenewalPeriod,
	dissentRenewalPeriodTimeUnit,
	optionalConsentCookieTypeNames,
	requiredConsentCookieTypeNames,
	storeConsent
) {
	flipThirdPartyCookiesOff(document);

	declineAllCookies(
		consentRenewalPeriod,
		consentRenewalPeriodTimeUnit,
		dissentRenewalPeriod,
		dissentRenewalPeriodTimeUnit,
		optionalConsentCookieTypeNames,
		requiredConsentCookieTypeNames,
		storeConsent
	);
}

/**
 * Runs `toggleThirdPartyCookies` on a gradual increasing interval of 500ms.
 *
 * @param {number} startingInterval - The initial interval to start with
 */
export function runThirdPartyCookiesInterval(startingInterval = 2000) {
	function refresh(interval) {

		// Don't increase the interval past 10s

		if (interval < 10000) {
			interval += 500;
		}

		toggleThirdPartyCookies();

		return setTimeout(() => refresh(interval), interval);
	}

	return refresh(startingInterval);
}
