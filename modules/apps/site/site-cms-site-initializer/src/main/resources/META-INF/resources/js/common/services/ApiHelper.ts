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

const HEADERS_ALL_LANGUAGES = new Headers({
	'Accept': 'application/json',
	'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
	'Content-Type': 'application/json',
	'X-Accept-All-Languages': 'true',
});

const UNEXPECTED_ERROR_MESSAGE = Liferay.Language.get(
	'an-unexpected-error-occurred'
);

export type RequestResult<T> =
	| {
			data: null;
			error: string;
			status?: string | null;
	  }
	| {
			data: T;
			error: null;
			status?: string | null;
	  };

async function deleteRequest(url: string) {
	return handleRequest<null>(() =>
		fetch(url, {
			headers: HEADERS,
			method: 'DELETE',
		})
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
			const {message, status, title} = await response.json();

			let error = title ?? message ?? UNEXPECTED_ERROR_MESSAGE;

			if (Array.isArray(error)) {
				error = JSON.stringify(error);
			}

			return {
				data: null,
				error,
				status,
			};
		}

		if (response.status === 204) {
			return {
				data: {} as T,
				error: null,
				status: null,
			};
		}

		const data: T | {error: string} = await response.json();

		if (data && typeof data === 'object' && 'error' in data) {
			return {
				data: null,
				error: data.error || UNEXPECTED_ERROR_MESSAGE,
				status: null,
			};
		}

		return {
			data,
			error: null,
			status: null,
		};
	}
	catch (error) {
		return {
			data: null,
			error: (error as Error).message || UNEXPECTED_ERROR_MESSAGE,
			status: null,
		};
	}
}

async function get<T>(url: string) {
	return handleRequest<T>(() =>
		fetch(url, {
			headers: HEADERS_ALL_LANGUAGES,
			method: 'GET',
		})
	);
}

/**
 * Fetches all items from a paginated API endpoint. It will keep requesting pages
 * until it reaches the last one, accumulating all items into a single array
 */

async function getAll<T>({
	filter,
	url,
}: {
	filter?: string;
	url: string;
}): Promise<T[]> {
	const items: T[] = [];

	let page = 1;
	let lastPage = 1;

	while (page <= lastPage) {
		const {data, error} = await get<{
			items: T[];
			lastPage: number;
		}>(`${url}?pageSize=-1&page=${page}&filter=${filter || ''}`);

		if (error !== null) {
			return Promise.reject(error);
		}

		lastPage = data.lastPage;

		items.push(...data.items);

		page = page + 1;
	}

	return items;
}

async function batch({
	data,
	method,
	url,
}: {
	data: Record<string, any>[];
	method: 'DELETE' | 'POST' | 'PUT';
	url: string;
}): Promise<{error: string} | void> {
	const response = await handleRequest<{id: number}>(() =>
		fetch(url, {
			body: JSON.stringify(data),
			headers: HEADERS,
			method,
		})
	);

	if (response.error || !response.data) {
		return {error: UNEXPECTED_ERROR_MESSAGE};
	}

	const taskId = response.data.id;

	while (true) {
		const result = await get<{
			executeStatus: string;
		}>(`/o/headless-batch-engine/v1.0/import-task/${taskId}`);

		if (result.error) {
			return {error: UNEXPECTED_ERROR_MESSAGE};
		}

		const status = result.data?.executeStatus;

		if (status === 'FAILED') {
			return {error: UNEXPECTED_ERROR_MESSAGE};
		}

		if (status === 'COMPLETED') {
			return;
		}

		// Wait 200 ms before polling again

		await new Promise((resolve) => setTimeout(resolve, 200));
	}
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

async function put<T>(url: string, data?: Record<string, any>) {
	return handleRequest<T>(() =>
		fetch(url, {
			body: JSON.stringify(data),
			headers: HEADERS,
			method: 'PUT',
		})
	);
}

async function postFormData<T>(formData: FormData, url: string) {
	return handleRequest<T>(() =>
		fetch(url, {
			body: formData,
			headers: new Headers({
				'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
			}),
			method: 'POST',
		})
	);
}

async function patch<T>(data: any, url: string) {
	return handleRequest<T>(() =>
		fetch(url, {
			body: JSON.stringify(data),
			headers: HEADERS,
			method: 'PATCH',
		})
	);
}

export default {
	batch,
	delete: deleteRequest,
	get,
	getAll,
	patch,
	post,
	postFormData,
	put,
};
