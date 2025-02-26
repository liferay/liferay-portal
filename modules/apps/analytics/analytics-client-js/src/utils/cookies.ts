/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	STORAGE_KEY_CHANNEL_ID,
	STORAGE_KEY_CONTEXTS,
	STORAGE_KEY_EVENTS,
	STORAGE_KEY_IDENTITY,
	STORAGE_KEY_MESSAGES,
	STORAGE_KEY_MESSAGE_IDENTITY,
	STORAGE_KEY_PREV_EMAIL_ADDRESS_HASHED,
	STORAGE_KEY_STORAGE_VERSION,
} from './constants';

const AC_COOKIE_LIST = [
	STORAGE_KEY_CHANNEL_ID,
	STORAGE_KEY_CONTEXTS,
	STORAGE_KEY_EVENTS,
	STORAGE_KEY_IDENTITY,
	STORAGE_KEY_MESSAGES,
	STORAGE_KEY_MESSAGE_IDENTITY,
	STORAGE_KEY_PREV_EMAIL_ADDRESS_HASHED,
	STORAGE_KEY_STORAGE_VERSION,
];

export function removeCookiesFromUserBrowser() {
	const cookies = document.cookie.split(';');

	if (cookies.length) {
		for (const cookie of cookies) {
			const [name] = cookie.split('=');

			if (AC_COOKIE_LIST.includes(name?.trim())) {
				document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC`;
			}
		}
	}
}
