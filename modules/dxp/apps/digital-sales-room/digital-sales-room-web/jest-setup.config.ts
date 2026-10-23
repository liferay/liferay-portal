/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {configure} from '@testing-library/dom';

// @ts-nocheck

jest.mock('@ckeditor/ckeditor5-bookmark/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-clipboard/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-editor-decoupled/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-editor-balloon/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-editor-classic/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-editor-inline/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-editor-multi-root/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-emoji/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-find-and-replace/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-horizontal-line/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-html-embed/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-html-support/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-image/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-link/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-list/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-markdown-gfm/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-media-embed/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-mention/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-minimap/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-page-break/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-source-editing-enhanced/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-special-characters/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-style/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-table/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-ui/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-widget/dist/index', () => ({}));

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
