/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Analytics} from '../types';
import {getTimezoneOffsetHour} from '../utils/date';

/**
 * Generates a local helper function to fetch information from DOM elements
 */
function getAttribute(selector: string, attribute: string) {
	const tag = document.querySelector(selector) || {};

	// @ts-ignore

	return tag[attribute] || '';
}

/**
 * Updates context with general page information
 */
function meta(request: {context: Analytics.Context}) {
	Object.assign(request.context, {
		canonicalUrl: getAttribute('link[rel=canonical]', 'href'),
		contentLanguageId: getAttribute('html', 'lang'),
		description: getAttribute('meta[name="description"]', 'content'),
		keywords: getAttribute('meta[name="keywords"]', 'content'),
		languageId: navigator.language,
		referrer: document.referrer,
		timezoneOffset: getTimezoneOffsetHour(),
		title: getAttribute('title', 'textContent'),
		url: location.href,
		userAgent: navigator.userAgent,
	});

	return request;
}

export {meta};
export default meta;
