/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-env jest */

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

window.themeDisplay = {
	...window.themeDisplay,
	getDefaultLanguageId: () => 'en_US',
	getLanguageId: () => 'en_US',
	getUserId: () => 0,
};

const eventHandlers = {};

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
	detach: (name, handler) => {
		if (eventHandlers[name]) {
			eventHandlers[name] = eventHandlers[name].filter(
				(eventHandler) => eventHandler !== handler
			);
		}
	},
	fire: (name, data) => {
		(eventHandlers[name] || []).forEach((eventHandler) =>
			eventHandler(data)
		);
	},
	on: (name, handler) => {
		if (!eventHandlers[name]) {
			eventHandlers[name] = [];
		}

		eventHandlers[name].push(handler);

		return {
			detach: () => window.Liferay.detach(name, handler),
		};
	},
};
