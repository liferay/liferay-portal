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

const UNEXPECTED_ERROR_MESSAGE = Liferay.Language.get(
	'an-unexpected-error-occurred'
);

export type RequestResult<T> =
	| {
			data: null;
			error: string;
			status?: number;
	  }
	| {
			data: T;
			error: undefined;
			status?: number;
	  };

async function deleteRequest(url: string) {
	return handleRequest<null>(() =>
		fetch(url, {
			headers: HEADERS,
			method: 'DELETE',
		})
	);
}

async function get<T>(url: string) {
	return handleRequest<T>(() =>
		fetch(url, {
			headers: HEADERS,
			method: 'GET',
		})
	);
}

async function handleRequest<T>(
	fetcher: () => Promise<Response>
): Promise<RequestResult<T>> {
	try {
		const response = await fetcher();

		if (!response.ok) {
			const {message, title} = await response.json();

			const error = title ?? message ?? UNEXPECTED_ERROR_MESSAGE;

			return {
				data: null,
				error,
				status: response.status,
			};
		}

		if (response.status === 204) {
			return {
				data: {} as T,
				error: undefined,
				status: response.status,
			};
		}

		return {
			data: await response.json(),
			error: undefined,
			status: response.status,
		};
	}
	catch (error) {
		return {
			data: null,
			error: (error as Error).message || UNEXPECTED_ERROR_MESSAGE,
			status: 500,
		};
	}
}

async function patch<T>(url: string, data?: Record<string, any>) {
	return handleRequest<T>(() =>
		fetch(url, {
			body: JSON.stringify(data),
			headers: HEADERS,
			method: 'PATCH',
		})
	);
}

async function post<T>(url: string, data?: Record<string, any>) {
	return handleRequest<T>(() =>
		fetch(url, {
			body: JSON.stringify(data),
			headers: HEADERS,
			method: 'POST',
		})
	);
}

export default {
	delete: deleteRequest,
	get,
	patch,
	post,
};
