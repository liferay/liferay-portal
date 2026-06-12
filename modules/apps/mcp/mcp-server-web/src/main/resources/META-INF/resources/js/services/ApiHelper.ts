/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

export type RequestResult<T> =
	| {data: T; error: null; status?: string}
	| {data: null; error: string; status?: string};

interface ApiErrorResponse {
	message?: string;
	status?: string;
	title?: string;
}

const UNEXPECTED_ERROR_MESSAGE = Liferay.Language.get(
	'an-unexpected-error-occurred'
);

const HEADERS = new Headers({
	'Accept': 'application/json',
	'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
	'Content-Type': 'application/json',
});

const HEADERS_ALL_LANGUAGES = new Headers({
	'Accept': 'application/json',
	'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
	'Content-Type': 'application/json',
	'X-Accept-All-Languages': 'true',
});

function getErrorMessage(errorResponse: ApiErrorResponse): string {
	return (
		errorResponse.title ?? errorResponse.message ?? UNEXPECTED_ERROR_MESSAGE
	);
}

async function handleRequest<T>(
	fetcher: () => Promise<Response>
): Promise<RequestResult<T>> {
	try {
		const response = await fetcher();

		if (response.status === 401) {
			window.location.reload();
		}

		if (!response.ok) {
			const errorResponse: ApiErrorResponse =
				(await response.json().catch(() => ({}))) ?? {};

			return {
				data: null,
				error: getErrorMessage(errorResponse),
				status: errorResponse.status,
			};
		}

		if (response.status === 204) {
			return {data: {} as T, error: null};
		}

		const data: T = await response.json();

		return {data, error: null};
	}
	catch (error) {
		return {data: null, error: UNEXPECTED_ERROR_MESSAGE};
	}
}

function del<T>(url: string): Promise<RequestResult<T>> {
	return handleRequest<T>(() =>
		fetch(url, {headers: HEADERS, method: 'DELETE'})
	);
}

function get<T>(url: string): Promise<RequestResult<T>> {
	return handleRequest<T>(() =>
		fetch(url, {headers: HEADERS_ALL_LANGUAGES, method: 'GET'})
	);
}

function patch<T>(url: string, body: unknown): Promise<RequestResult<T>> {
	return handleRequest<T>(() =>
		fetch(url, {
			body: JSON.stringify(body),
			headers: HEADERS,
			method: 'PATCH',
		})
	);
}

function post<T>(url: string, body: unknown): Promise<RequestResult<T>> {
	return handleRequest<T>(() =>
		fetch(url, {
			body: JSON.stringify(body),
			headers: HEADERS,
			method: 'POST',
		})
	);
}

export default {del, get, patch, post};
