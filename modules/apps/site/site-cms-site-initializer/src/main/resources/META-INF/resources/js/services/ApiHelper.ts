/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

const HEADERS = new Headers({
	'Accept': 'application/json',
	'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
	'Content-Type': 'application/json',
});

async function get(url: string) {
	const response = await fetch(url, {
		headers: HEADERS,
		method: 'GET',
	});

	if (response.ok) {
		return await response.json();
	}

	const {title} = await response.json();

	throw new Error(title);
}

async function post<T>(url: string, data?: T) {
	const response = await fetch(url, {
		body: JSON.stringify(data),
		headers: HEADERS,
		method: 'POST',
	});

	if (response.ok) {
		return await response.json();
	}

	const {title} = await response.json();

	throw new Error(title);
}

async function put<T>(url: string, data?: T) {
	const response = await fetch(url, {
		body: JSON.stringify(data),
		headers: HEADERS,
		method: 'PUT',
	});

	if (response.ok) {
		return await response.json();
	}

	const {title} = await response.json();

	throw new Error(title);
}

export default {get, post, put};
