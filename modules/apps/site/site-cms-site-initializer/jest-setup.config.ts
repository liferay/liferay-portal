/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore

jest.mock('ckeditor5', () => ({
	Plugin: () => {},
}));

class MockBroadcastChannel {
	name: string;
	onmessage: ((event: MessageEvent) => void) | null = null;
	private listeners: ((event: MessageEvent) => void)[] = [];

	constructor(name: string) {
		this.name = name;
	}

	postMessage(message: any) {
		const event = {data: message} as MessageEvent;
		this.listeners.forEach((listener) => listener(event));
	}

	addEventListener(_: string, listener: (event: MessageEvent) => void) {
		this.listeners.push(listener);
	}

	removeEventListener(_: string, listener: (event: MessageEvent) => void) {
		this.listeners = this.listeners.filter((l) => l !== listener);
	}

	close() {
		this.listeners = [];
	}
}

(globalThis as any).BroadcastChannel = MockBroadcastChannel;

(globalThis as any).Liferay = {
	...(globalThis.Liferay || {}),
	Language: {
		...(globalThis.Liferay.Language || {}),
		direction: {en_US: 'rtl'},
		get: (key: string) => key,
	},
	ThemeDisplay: {
		...(globalThis.Liferay.ThemeDisplay || {}),
		getBCP47LanguageId: () => 'en-US',
		getDefaultLanguageId: () => 'en_US',
		getLanguageId: () => 'en_US',
		getUserId: () => '1',
	},
	Util: {
		...(globalThis.Liferay.Util || {}),
		escapeHTML: (str: string) => str,
		formatStorage: (size: number) => `${size / 1024} KB`,
	},
	authToken: 'mocked-auth-token',
};
