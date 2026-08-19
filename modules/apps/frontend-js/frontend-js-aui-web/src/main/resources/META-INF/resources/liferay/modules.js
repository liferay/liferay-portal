/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * @deprecated As of Athanasius (7.3.x), with no direct replacement
 */
(function () {
	const LiferayAUI = Liferay.AUI;

	const COMBINE = LiferayAUI.getCombine();

	const PATH_JAVASCRIPT = '/o/frontend-js-aui-web';

	window.YUI_config = {
		base:
			Liferay.ThemeDisplay.getCDNBaseURL() +
			Liferay.ThemeDisplay.getPathContext() +
			PATH_JAVASCRIPT +
			'/aui/',
		combine: COMBINE,
		comboBase: LiferayAUI.getComboPath(),
		filter: 'min', // If you need to debug this file, replace this with 'raw'
		groups: {
			liferay: {
				base:
					Liferay.ThemeDisplay.getCDNBaseURL() +
					Liferay.ThemeDisplay.getPathContext() +
					PATH_JAVASCRIPT +
					'/liferay/',
				combine: COMBINE,
				filter: Liferay.AUI.getFilterConfig(),
				modules: {
					'liferay-form': {
						path: 'form.js',
						requires: ['aui-base', 'aui-form-validator'],
					},
					'liferay-input-localized': {
						path: 'input_localized.js',
						requires: [
							'aui-base',
							'aui-component',
							'aui-event-input',
							'aui-palette',
							'aui-set',
						],
					},
					'liferay-menu': {
						path: 'menu.js',
						requires: ['aui-debounce', 'aui-node'],
					},
					'liferay-menu-filter': {
						path: 'menu_filter.js',
						requires: [
							'autocomplete-base',
							'autocomplete-filters',
							'autocomplete-highlighters',
						],
					},
					'liferay-portlet-base': {
						path: 'portlet_base.js',
						requires: ['aui-base'],
					},
					'liferay-search-container': {
						path: 'search_container.js',
						requires: ['aui-base', 'aui-datatable-core'],
					},
					'liferay-search-container-move': {
						path: 'search_container_move.js',
						requires: [
							'aui-component',
							'dd-constrain',
							'dd-delegate',
							'dd-drag',
							'dd-drop',
							'dd-proxy',
							'plugin',
						],
					},
					'liferay-search-container-select': {
						path: 'search_container_select.js',
						requires: ['aui-component', 'aui-url', 'plugin'],
					},
					'liferay-upload': {
						path: 'upload.js',
						requires: [
							'aui-template-deprecated',
							'collection',
							'liferay-portlet-base',
							'uploader',
						],
					},
					'liferay-util-window': {
						path: 'util_window.js',
						requires: [
							'aui-dialog-iframe-deprecated',
							'aui-modal',
							'aui-url',
							'event-resize',
						],
					},
				},
				root: PATH_JAVASCRIPT + '/liferay/',
			},
		},
		insertBefore: 'liferayAUICSS',
		lang: themeDisplay.getBCP47LanguageId(),
		root: PATH_JAVASCRIPT + '/aui/',
		useBrowserConsole: false,
	};
})();
