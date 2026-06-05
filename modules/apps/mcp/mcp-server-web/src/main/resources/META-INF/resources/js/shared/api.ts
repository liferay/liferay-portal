/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

export type RequestResult<T> =
	| {data: T; error: null; status: number}
	| {data: null; error: string; status: number};

interface ApiErrorResponse {
	detail?: string;
	message?: string;
	title?: string;
}

const UNEXPECTED_ERROR_MESSAGE = Liferay.Language.get(
	'an-unexpected-error-occurred'
);

const HEADERS = {
	'Accept': 'application/json',
	'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
};

function getErrorMessage(errorResponse: ApiErrorResponse): string {
	return (
		errorResponse.title ||
		errorResponse.detail ||
		errorResponse.message ||
		UNEXPECTED_ERROR_MESSAGE
	);
}

async function request<T>(
	url: string,
	options: RequestInit
): Promise<RequestResult<T>> {
	try {
		const response = await fetch(url, {
			...options,
			headers: {...HEADERS, ...options.headers},
		});

		if (response.status === 401) {
			window.location.reload();

			return {data: null, error: '', status: 401};
		}

		if (response.status === 204) {
			return {data: {} as T, error: null, status: 204};
		}

		let body: T | ApiErrorResponse | null;

		try {
			body = await response.json();
		}
		catch (error) {
			body = null;
		}

		if (response.ok) {
			return {data: body as T, error: null, status: response.status};
		}

		return {
			data: null,
			error: getErrorMessage((body as ApiErrorResponse) ?? {}),
			status: response.status,
		};
	}
	catch (error) {
		return {data: null, error: UNEXPECTED_ERROR_MESSAGE, status: 0};
	}
}

function del<T>(url: string): Promise<RequestResult<T>> {
	return request<T>(url, {method: 'DELETE'});
}

function get<T>(url: string): Promise<RequestResult<T>> {
	return request<T>(url, {method: 'GET'});
}

function patch<T>(url: string, body: unknown): Promise<RequestResult<T>> {
	return request<T>(url, {
		body: JSON.stringify(body),
		headers: {'Content-Type': 'application/json'},
		method: 'PATCH',
	});
}

function post<T>(url: string, body: unknown): Promise<RequestResult<T>> {
	return request<T>(url, {
		body: JSON.stringify(body),
		headers: {'Content-Type': 'application/json'},
		method: 'POST',
	});
}

export default {del, get, patch, post};
