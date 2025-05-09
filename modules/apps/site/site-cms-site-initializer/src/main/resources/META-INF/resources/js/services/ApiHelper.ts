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

export const HEADERS_ALL_LANGUAGES = new Headers({
	'Accept': 'application/json',
	'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
	'Content-Type': 'application/json',
	'X-Accept-All-Languages': 'true',
});

const UNEXPECTED_ERROR_MESSAGE = Liferay.Language.get(
	'an-unexpected-error-occurred'
);

type RequestHandlerResult<T> = {
	data?: T;
	errorMessage?: string;
	success: boolean;
};

export async function handleRequest<T>(
	fetcher: () => Promise<Response>,
	{returnValue = false}: {returnValue?: boolean} = {}
): Promise<RequestHandlerResult<T>> {
	try {
		const response = await fetcher();

		if (response.status === 401) {
			window.location.reload();
		}

		if (!response.ok) {
			let errorMessage = UNEXPECTED_ERROR_MESSAGE;

			try {
				const {message, title} = await response.json();

				errorMessage = title ?? message ?? UNEXPECTED_ERROR_MESSAGE;

				if (Array.isArray(errorMessage)) {
					errorMessage = JSON.stringify(errorMessage);
				}
			}
			catch (error) {
				throw new Error(UNEXPECTED_ERROR_MESSAGE);
			}

			throw new Error(errorMessage);
		}

		let data: T | undefined;

		if (returnValue) {
			data = await response.json();
		}

		return {
			data,
			success: true,
		};
	}
	catch (error) {
		return {
			errorMessage: (error as Error).message || UNEXPECTED_ERROR_MESSAGE,
			success: false,
		};
	}
}

export async function fetchJSON<T>(input: RequestInfo, init?: RequestInit) {
	const result = await fetch(input, {headers: HEADERS, method: 'GET', ...init});

	return (await result.json()) as T;
}

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

async function postFormData(formData: FormData, url: string) {
	return handleRequest(() =>
		fetch(url, {
			body: formData,
			method: 'POST',
		})
	);
}

async function patch(data: any, url: string) {
	return handleRequest(() =>
		fetch(url, {
			body: JSON.stringify(data),
			headers: HEADERS,
			method: 'PATCH',
		})
	);
}

export async function postScopeScopeKeyObjectEntryFolder(
	scopeKey: string,
	title: string,
	parentObjectEntryFolderExternalReferenceCode: string
) {
	return await handleRequest(() =>
		fetch(
			`/o/headless-object/v1.0/scopes/${scopeKey}/object-entry-folders`,
			{
				body: JSON.stringify({
					parentObjectEntryFolderExternalReferenceCode,
					title,
				}),
				headers: HEADERS,
				method: 'POST',
			}
		)
	);
}

export default {get, patch, post, postFormData, put};
