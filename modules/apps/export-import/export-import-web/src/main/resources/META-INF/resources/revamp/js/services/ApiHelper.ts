/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type RequestResult<T> =
	| {data: null; error: string; status?: string | null}
	| {data: T; error: null; status?: string | null};

interface ApiErrorResponse {
	error?: string;
	message?: string;
}

const UNEXPECTED_ERROR_MESSAGE = Liferay.Language.get(
	'an-unexpected-error-occurred'
);

const HEADERS = {
	'ACCEPT': 'application/json',
	'ACCEPT-LANGUAGE': Liferay.ThemeDisplay.getBCP47LanguageId(),
	'X-CSRF-Token': Liferay.authToken,
};

async function postFormDataWithProgress<T>(
	url: string,
	formData: FormData,
	onProgress?: (percent: number) => void,
	signal?: AbortSignal
): Promise<RequestResult<T>> {
	return new Promise((resolve) => {
		const xhr = new XMLHttpRequest();

		const handleAbort = () => {
			xhr.abort();
			resolve({data: null, error: 'Aborted'});
		};

		xhr.open('POST', url);

		Object.entries(HEADERS).forEach(([key, value]) => {
			xhr.setRequestHeader(key, value);
		});

		if (signal) {
			signal.addEventListener('abort', handleAbort);
		}

		if (onProgress && xhr.upload) {
			xhr.upload.onprogress = (event) => {
				if (event.lengthComputable) {
					onProgress(Math.round((event.loaded / event.total) * 100));
				}
			};
		}

		xhr.onload = () => {
			let responseData: T | ApiErrorResponse;

			try {
				responseData = JSON.parse(xhr.responseText);
			}
			catch (error) {
				return resolve({
					data: null,
					error: UNEXPECTED_ERROR_MESSAGE,
				});
			}

			if (xhr.status >= 200 && xhr.status < 300) {
				resolve({data: responseData as T, error: null});
			}
			else {
				const errorResponse = responseData as ApiErrorResponse;

				const errorMessage =
					errorResponse?.message ||
					errorResponse?.error ||
					UNEXPECTED_ERROR_MESSAGE;

				resolve({
					data: null,
					error: errorMessage,
					status: xhr.status.toString(),
				});
			}
		};

		xhr.onerror = () =>
			resolve({
				data: null,
				error: UNEXPECTED_ERROR_MESSAGE,
			});

		xhr.onloadend = () => {
			if (signal) {
				signal.removeEventListener('abort', handleAbort);
			}
		};

		xhr.send(formData);
	});
}

export default {
	postFormDataWithProgress,
};
