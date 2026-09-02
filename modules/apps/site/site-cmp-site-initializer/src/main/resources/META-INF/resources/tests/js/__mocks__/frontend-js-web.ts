/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export * from 'frontend-js-web/src/main/resources/META-INF/resources/main';

export function buildFragment(html: string) {
	const template = document.createElement('template');
	template.innerHTML = html.trim();

	return template.content;
}

function debounceFunction(fn: Function, delay: number) {
	const debouncedFunctions = new Map();
	let timerId: any;

	const debounced = function (...args: any[]) {
		if (timerId) {
			clearTimeout(timerId);
		}

		timerId = setTimeout(() => {
			fn(...args);
			timerId = null;
		}, delay);
	};

	Object.defineProperty(debounced, 'id', {
		get: () => timerId,
		set: (newId) => {
			timerId = newId;
		},
	});

	debouncedFunctions.set(fn, debounced);

	return debounced;
}

export function getObjectValueFromPath({
	object,
	path = 'id',
}: {
	object: object;
	path?: string | null;
}): any {
	if (!path) {
		path = 'id';
	}

	return path.split('.').reduce((acc, currentPath) => {
		return acc?.[currentPath as keyof typeof acc] ?? null;
	}, object);
}

export function loadClientExtensions() {
	return Promise.resolve();
}

export function objectToFormData(object: object) {
	return object;
}

export function sub(str: string, ...params: unknown[]) {
	const paramList = params.flat();

	const keyArray: unknown[] = str
		.split(/({\d+})/g)
		.filter((value) => value.length !== 0);

	paramList.forEach((param, index) => {
		let paramIndex = keyArray.indexOf(`{${index}}`);

		while (paramIndex >= 0) {
			keyArray.splice(paramIndex, 1, param);

			paramIndex = keyArray.indexOf(`{${index}}`);
		}
	});

	return keyArray.some((value) => value && typeof value === 'object')
		? keyArray
		: keyArray.join('');
}

export const mockFetch = jest.fn(() => {
	return Promise.resolve({
		json: async () => ({}),
		ok: true,
		status: 200,
		text: async () => '',
	} as Response);
});

export const addParams = jest.fn();
export const debounce = jest.fn(debounceFunction);
export const fetch = mockFetch;
export const mockNavigate = jest.fn();
export const navigate = mockNavigate;
export const throttle = jest.fn();

export default {};
