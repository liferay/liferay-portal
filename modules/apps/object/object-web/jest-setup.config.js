/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-env jest */

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
jest.mock('@ckeditor/ckeditor5-special-characters/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-style/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-table/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-ui/dist/index', () => ({}));
jest.mock('@ckeditor/ckeditor5-widget/dist/index', () => ({}));

window.themeDisplay = {
	...window.themeDisplay,
	getDefaultLanguageId: () => 'en_US',
	getLanguageId: () => 'en_US',
	getUserId: () => 0,
};

window.Liferay = {
	...(window.Liferay || {}),
	Language: {
		...(window.Liferay.Language || {}),
		available: {
			ar_SA: 'Arabic (Saudi Arabia)',
			ca_ES: 'Catalan (Spain)',
			de_DE: 'German (Germany)',
			en_US: 'English (United States)',
			es_ES: 'Spanish (Spain)',
			fi_FI: 'Finnish (Finland)',
			fr_FR: 'French (France)',
			hu_HU: 'Hungarian (Hungary)',
			ja_JP: 'Japanese (Japan)',
			nl_NL: 'Dutch (Netherlands)',
			pt_BR: 'Portuguese (Brazil)',
			sv_SE: 'Swedish (Sweden)',
			zh_CN: 'Chinese (China)',
		},

		direction: {en_US: 'rtl'},
	},
	ThemeDisplay: {
		...(window.Liferay.ThemeDisplay || {}),
		getDefaultLanguageId: () => 'en_US',
		getLanguageId: () => 'en_US',
	},
	Util: {
		...window.Liferay.Util,
		escapeHTML: (title) => title,
		getLexiconIconTpl: (icon) => icon,
	},
	component: () => {},
};
