/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-nocheck

import {escapeHTML} from 'frontend-js-web';

jest.mock('@ckeditor/ckeditor5-bookmark', () => ({}));
jest.mock('@ckeditor/ckeditor5-clipboard', () => ({}));
jest.mock('@ckeditor/ckeditor5-editor-decoupled', () => ({}));
jest.mock('@ckeditor/ckeditor5-editor-balloon', () => ({}));
jest.mock('@ckeditor/ckeditor5-editor-classic', () => ({}));
jest.mock('@ckeditor/ckeditor5-editor-inline', () => ({}));
jest.mock('@ckeditor/ckeditor5-editor-multi-root', () => ({}));
jest.mock('@ckeditor/ckeditor5-emoji', () => ({}));
jest.mock('@ckeditor/ckeditor5-find-and-replace', () => ({}));
jest.mock('@ckeditor/ckeditor5-horizontal-line', () => ({}));
jest.mock('@ckeditor/ckeditor5-html-embed', () => ({}));
jest.mock('@ckeditor/ckeditor5-html-support', () => ({}));
jest.mock('@ckeditor/ckeditor5-image', () => ({}));
jest.mock('@ckeditor/ckeditor5-link', () => ({}));
jest.mock('@ckeditor/ckeditor5-list', () => ({}));
jest.mock('@ckeditor/ckeditor5-markdown-gfm', () => ({}));
jest.mock('@ckeditor/ckeditor5-media-embed', () => ({}));
jest.mock('@ckeditor/ckeditor5-mention', () => ({}));
jest.mock('@ckeditor/ckeditor5-minimap', () => ({}));
jest.mock('@ckeditor/ckeditor5-page-break', () => ({}));
jest.mock('@ckeditor/ckeditor5-source-editing-enhanced', () => ({}));
jest.mock('@ckeditor/ckeditor5-special-characters', () => ({}));
jest.mock('@ckeditor/ckeditor5-style', () => ({}));
jest.mock('@ckeditor/ckeditor5-table', () => ({}));
jest.mock('@ckeditor/ckeditor5-ui', () => ({}));
jest.mock('@ckeditor/ckeditor5-widget', () => ({}));
jest.mock('@jsonurl/jsonurl', () => ({}));

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
	Browser: {
		...(globalThis.Liferay.Browser || {}),
		isMac: () => false,
	},
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
		getPathFriendlyURLPublic: () => '/web',
		getTimeZone: () => 'UTC',
		getUserId: () => '1',
		isImpersonated: () => false,
	},
	Util: {
		...(globalThis.Liferay.Util || {}),
		escapeHTML,
		formatStorage: (size: number) => `${size / 1024} KB`,
	},
	authToken: 'mocked-auth-token',
	detach: () => {},
	fire: () => {},
	on: () => {},
};
