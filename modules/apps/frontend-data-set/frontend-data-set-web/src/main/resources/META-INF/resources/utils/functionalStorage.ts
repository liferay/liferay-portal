/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {checkConsent, localStorage} from 'frontend-js-web';

/**
 * Reads a JSON value from browser storage, or returns null when there is none
 * and when it cannot be read.
 *
 * @param key Key the value is stored under
 */
function get(key: string): unknown {
	try {
		_checkConsentFunctionalCookies();

		return JSON.parse(
			localStorage.getItem(key, localStorage.TYPES.FUNCTIONAL) as string
		);
	}
	catch (error) {
		logWarning(key, error);

		return null;
	}
}

/**
 * Warns about a value that could not be read, only while developing, because a
 * history nobody can store is worth neither an error nor a user facing message.
 *
 * @param key Key the value is stored under
 * @param error What kept the value from being read
 */
function logWarning(key: string, error: unknown): void {
	if (process.env.NODE_ENV === 'development') {
		console.warn(`${key} could not be accessed in browser storage`, error);
	}
}

/**
 * Removes a value from browser storage.
 *
 * @param key Key the value is stored under
 */
function remove(key: string): void {
	try {
		_checkConsentFunctionalCookies();

		localStorage.removeItem(key);
	}
	catch (error) {
		logWarning(key, error);
	}
}

/**
 * Writes a JSON value to browser storage.
 *
 * @param key Key to store the value under
 * @param value The value to store
 */
function set(key: string, value: unknown): void {
	try {
		_checkConsentFunctionalCookies();

		localStorage.setItem(
			key,
			JSON.stringify(value),
			localStorage.TYPES.FUNCTIONAL
		);
	}
	catch (error) {
		logWarning(key, error);
	}
}

function _checkConsentFunctionalCookies() {
	if (!checkConsent(localStorage.TYPES.FUNCTIONAL)) {
		throw new Error('There is no consent for functional cookies');
	}
}

export default {
	get,
	logWarning,
	remove,
	set,
};
