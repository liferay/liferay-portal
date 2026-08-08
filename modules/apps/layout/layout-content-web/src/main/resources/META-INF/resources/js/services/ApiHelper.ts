/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

const UNEXPECTED_ERROR_MESSAGE = Liferay.Language.get(
	'an-unexpected-error-occurred'
);

export type RequestResult<T> =
	| {
			data: null;
			error: string;
	  }
	| {
			data: T;
			error: null;
	  };

async function handleRequest<T>(
	fetcher: () => Promise<Response>
): Promise<RequestResult<T>> {
	try {
		const response = await fetcher();

		if (!response.ok) {
			const {message, title} = await response.json();

			return {
				data: null,
				error: title ?? message ?? UNEXPECTED_ERROR_MESSAGE,
			};
		}

		if (response.status === 204) {
			return {
				data: null as unknown as T,
				error: null,
			};
		}

		return {
			data: await response.json(),
			error: null,
		};
	}
	catch (error) {
		return {
			data: null,
			error: (error as Error).message || UNEXPECTED_ERROR_MESSAGE,
		};
	}
}

async function del(url: string, signal?: AbortSignal) {
	return handleRequest<void>(() =>
		fetch(url, {
			headers: new Headers({
				'Accept': 'application/json',
				'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
				'Content-Type': 'application/json',
			}),
			method: 'DELETE',
			signal,
		})
	);
}

async function get<T>(url: string, signal?: AbortSignal) {
	return handleRequest<T>(() =>
		fetch(url, {
			headers: new Headers({
				'Accept': 'application/json',
				'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
				'Content-Type': 'application/json',
			}),
			method: 'GET',
			signal,
		})
	);
}

async function post<T>(url: string, signal?: AbortSignal) {
	return handleRequest<T>(() =>
		fetch(url, {
			headers: new Headers({
				'Accept': 'application/json',
				'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
				'Content-Type': 'application/json',
			}),
			method: 'POST',
			signal,
		})
	);
}

export default {del, get, post};
