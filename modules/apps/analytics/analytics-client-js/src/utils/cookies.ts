/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {COOKIE_EXPIRATION_DAYS, COOKIE_EXPIRED_DATE} from './constants';

/**
 * The path this client writes its cookies at. Reading and expiring a cookie
 * have to agree with the write on this, or they address a different cookie and
 * silently do nothing.
 */
const getCookiePath = () =>
	window.Liferay?.ThemeDisplay?.getPathContext?.() || '/';

/**
 * Reads a cookie through the same API that wrote it. The client is Liferay
 * Portal agnostic and may run on a page that has no Liferay global, or an older
 * one without the cookie API, so both paths have to work.
 */
const getCookie = (key: string) => {
	const Liferay = window.Liferay;

	if (Liferay?.Util?.Cookie) {
		return (
			Liferay.Util.Cookie.get?.(
				key,
				Liferay.Util.Cookie.TYPES?.PERSONALIZATION
			) || ''
		);
	}

	const cookie = document.cookie
		.split('; ')
		.find((item) => item.startsWith(`${key}=`));

	return cookie ? cookie.slice(key.length + 1) : '';
};

/**
 * Writes a cookie, optionally scoped to a domain so every host under it shares
 * the one cookie rather than each holding its own.
 */
const setCookie = (key: string, value: string, domain: string = '') => {
	const Liferay = window.Liferay;

	const expires = new Date();

	expires.setDate(expires.getDate() + COOKIE_EXPIRATION_DAYS);

	if (Liferay?.Util?.Cookie) {
		const options: {domain?: string; expires: Date; secure: boolean} = {
			expires,
			secure: true,
		};

		if (domain) {
			options.domain = domain;
		}

		Liferay.Util.Cookie.set?.(
			key,
			value,
			Liferay.Util.Cookie.TYPES?.PERSONALIZATION,
			options
		);

		return;
	}

	const cookie = [`${key}=${value}`];

	if (domain) {
		cookie.push(`domain=${domain}`);
	}

	cookie.push(
		`expires=${expires.toUTCString()}`,
		`path=${getCookiePath()}`,
		'Secure'
	);

	document.cookie = cookie.join('; ');
};

/**
 * Expires the cookie scoped to this exact host. Omitting the domain is what
 * makes the browser expire that cookie rather than one shared with sibling
 * hosts, and both paths are covered because the Liferay API writes at the root
 * while the fallback above writes at the path context.
 */
const removeHostOnlyCookie = (key: string) => {
	const paths = new Set([getCookiePath(), '/']);

	paths.forEach((path) => {
		document.cookie = `${key}=; expires=${COOKIE_EXPIRED_DATE}; path=${path}`;
	});
};

export {getCookie, getCookiePath, removeHostOnlyCookie, setCookie};
