/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';

const HEADERS = {
	'Accept': 'application/json',
	'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
	'Content-Type': 'application/json',
};

function fetchItem(url, options) {
	return new Promise((resolve, reject) => {
		let isOk;

		fetch(url, options)
			.then((response) => {
				isOk = response.ok;

				return response.json();
			})
			.then((data) => {
				if (isOk) {
					resolve(data);
				}
				else {
					reject(data);
				}
			})
			.catch((error) => reject(error));
	});
}

export function getURL(path, params) {
	params = {
		['p_auth']: Liferay.authToken,
		t: Date.now(),
		...params,
	};

	let pathContext = themeDisplay.getPathContext();

	if (!pathContext || pathContext === '/') {
		pathContext = '';
	}

	const uri = new URL(`${window.location.origin}${pathContext}${path}`);

	const keys = Object.keys(params);

	keys.forEach((key) => uri.searchParams.set(key, params[key]));

	return uri.toString();
}

export function addItem(endpoint, item) {
	return fetchItem(getURL(endpoint), {
		body: JSON.stringify(item),
		headers: HEADERS,
		method: 'POST',
	});
}

export function deleteItem(endpoint) {
	return fetch(getURL(endpoint), {
		method: 'DELETE',
	});
}

export function confirmDelete(endpoint) {
	return (item) =>
		new Promise((resolve, reject) => {
			openConfirmModal({
				message: Liferay.Language.get(
					'are-you-sure-you-want-to-delete-this'
				),
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						deleteItem(endpoint + item.id)
							.then(() => resolve(true))
							.catch((error) => reject(error));
					}
					else {
						resolve(false);
					}
				},
			});
		});
}

export function getItem(endpoint) {
	return fetch(getURL(endpoint), {
		headers: HEADERS,
		method: 'GET',
	}).then((response) => response.json());
}

export function getItems(baseURL, keywords = '', {signal} = {}) {
	return fetchItem(getURL(baseURL, {keywords, page: 1, pageSize: 250}), {
		headers: HEADERS,
		method: 'GET',
		signal,
	}).then(({items = [], totalCount = 0}) => ({items, totalCount}));
}

export function request(endpoint, method = 'GET') {
	return fetch(getURL(endpoint), {
		headers: HEADERS,
		method,
	});
}

export function updateItem(endpoint, item, params) {
	return fetchItem(getURL(endpoint, params), {
		body: JSON.stringify(item),
		headers: HEADERS,
		method: 'PUT',
	});
}
