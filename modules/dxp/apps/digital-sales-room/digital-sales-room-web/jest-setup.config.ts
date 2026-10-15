/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {configure} from '@testing-library/dom';

// @ts-nocheck

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

configure({
	testIdAttribute: 'data-qa-id',
});

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
		getPathFriendlyURLPublic: () => '/web',
		getUserId: () => '1',
	},
	Util: {
		...(globalThis.Liferay.Util || {}),
		escapeHTML: (str: string) => str,
		formatStorage: (size: number) => `${size / 1024} KB`,
	},
	authToken: 'mocked-auth-token',
};

(globalThis as any).ResizeObserver = jest.fn().mockImplementation(() => ({
	disconnect: jest.fn(),
	observe: jest.fn(),
	unobserve: jest.fn(),
}));
